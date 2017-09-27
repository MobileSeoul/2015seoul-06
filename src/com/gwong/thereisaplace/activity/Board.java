package com.gwong.thereisaplace.activity;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.data.GlobalVar;
import com.gwong.thereisaplace.data.IsNetworkStat;
import com.gwong.thereisaplace.data.Lines;
import com.gwong.thereisaplace.data.StationInfo;
import com.gwong.thereisaplace.data.SubwayInfo;
import com.gwong.thereisaplace.data.XMLParser;
import com.gwong.thereisplace.view.BoardRow;
import com.gwong.thereisplace.view.SurroundingStationRow;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

@SuppressLint("ClickableViewAccessibility")
public class Board extends BaseActivity implements OnTouchListener {
	private static final int ACT_CLICK_CONTENT = 0;
	private static final int ACT_CLICK_WRITE = 1;
	private static final int ACT_CLICK_MOVE = 2;
	private static final int ITEM_NUMBER = 20;
	private static final String KEY_PAGE = "page";
	private static final String KEY_ITEM = "item";
	private static final String TAG_RENEW = "searchAll";
	private static final String TAG_RANK = "rank";
	private static final String STRING_LAST_STATION = "마지막 역 입니다.";
	private static final String STRING_SELECT_STATION = "역 선택";
	private static final String STRING_ADD = "더 보기";

	private String currentName;
	private String currentLine;
	private int pageNum = 0;
	private String xml;
	private org.w3c.dom.Document doc;
	private XMLParser parser;
	private String server;
	private float x1, x2;

	private ProgressDialog progDialog;
	private Context context;

	private BoardRow adapter;
	private ArrayList<String> arId;
	private ArrayList<String> arWriter;
	private ArrayList<Bitmap> arImage;
	private ArrayList<String> arMsg;
	private ArrayList<String> arDate;
	private ArrayList<String> arCount;
	private ArrayList<String> arIsImage;
	private ArrayList<String> arSort;
	private ListView list;
	private Button addBtn;
	ImageView iv;

	private StrictMode.ThreadPolicy policy;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board_activity);
		context = this.getApplicationContext();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(GlobalVar.BACKGROUND_COLOR));
		getActionBar().setDisplayShowHomeEnabled(false);

		policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		Intent intent = getIntent();
		currentName = (String) intent.getStringExtra(GlobalVar.EXTRA_NAME);
		currentLine = (String) intent.getStringExtra(GlobalVar.EXTRA_LINE);
		currentLine = currentLine.toLowerCase();
		this.setTitle(currentName);
		// setting custom title bar
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		TextView tv = (TextView) findViewById(titleId);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(GlobalVar.TYPEFACE);
		// setting listview
		list = (ListView) findViewById(R.id.board_lv_list);
		list.setOnTouchListener((OnTouchListener) this);
		// list.setDivider(new ColorDrawable(GlobalVar.BACKGROUND_COLOR));
		list.setDividerHeight(2);
		list.setOnItemClickListener(mItemClickListener);

		// increase rank count
		try {
			URL url = new URL(GlobalVar.SERVER_ADDRESS + "/rank.php?" + "tag=" + URLEncoder.encode(TAG_RANK, "UTF-8") + "&" + GlobalVar.TAG_NAME + "=" + URLEncoder.encode(currentName, "UTF-8") + "&"
					+ GlobalVar.TAG_LINE + "=" + URLEncoder.encode(currentLine, "UTF-8"));
			url.openStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		iv = (ImageView) findViewById(android.R.id.empty);
		iv.setOnTouchListener((OnTouchListener) this);
		new loadListView().execute();
	}


	// create a content when item of listview is clicked
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@SuppressWarnings("unchecked")
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			if (parent == list) {
				Intent intent = new Intent(view.getContext(), Content.class);
				intent.putExtra(GlobalVar.EXTRA_NAME, currentName);
				intent.putExtra(GlobalVar.EXTRA_LINE, currentLine);
				intent.putExtra(GlobalVar.EXTRA_ID, arId.get(position));
				startActivityForResult(intent, ACT_CLICK_CONTENT);
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACT_CLICK_CONTENT:
//			new loadListView().execute();
			break;
		case ACT_CLICK_WRITE:
			pageNum = 0;
			new loadListView().execute();
			break;
		case ACT_CLICK_MOVE:
			break;
		}
	}

	private class loadListView extends AsyncTask<Void, Void, Void> {
		private boolean running = true;

		protected void onCancelled() {
			running = false;
		}

		@Override
		protected void onPreExecute() {
			if (IsNetworkStat.isNetworkStat(Board.this) == false) {
				onCancelled();
			}
			progDialog = new ProgressDialog(Board.this, R.style.dialog);
			progDialog.setCancelable(false);
			progDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
			progDialog.setMessage("Loading");
			progDialog.show();
		}

		protected Void doInBackground(Void... unused) {
			if (running) {
				runOnUiThread(new Runnable() {
					public void run() {
						URL url;
						try {
							url = new URL(GlobalVar.SERVER_ADDRESS + "/search.php?" + "tag=" + URLEncoder.encode(TAG_RENEW, "UTF-8") + "&" + GlobalVar.TAG_NAME + "="
									+ URLEncoder.encode(currentName, "UTF-8") + "&" + GlobalVar.TAG_LINE + "=" + URLEncoder.encode(currentLine, "UTF-8"));
							url.openStream();
							server = new String(GlobalVar.SERVER_ADDRESS + "/" + URLEncoder.encode(currentName + "_" + currentLine + ".xml", "UTF-8"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						// To renew
						if (pageNum == 0) {
							arId = new ArrayList<String>();
							arWriter = new ArrayList<String>();
							arImage = new ArrayList<Bitmap>();
							arMsg = new ArrayList<String>();
							arDate = new ArrayList<String>();
							arCount = new ArrayList<String>();
							arIsImage = new ArrayList<String>();
							arSort = new ArrayList<String>();
							list.removeFooterView(addBtn);
						}
						parser = new XMLParser();
						xml = parser.getXmlFromUrl(server);
						doc = parser.getDomElement(xml);
						// getting data
						NodeList pageList = doc.getElementsByTagName(KEY_PAGE);
						Element pageE = (Element) pageList.item(pageNum);
						if (pageE == null) {
							SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.subway);
							ImageView iv = (ImageView) findViewById(android.R.id.empty);
							iv.setImageDrawable(svg.createPictureDrawable());
							list.setEmptyView(iv);
							return;
						}

						NodeList itemList = pageE.getElementsByTagName(KEY_ITEM);
						for (int i = 0; i < itemList.getLength(); i++) {
							Element itemE = (Element) itemList.item(i);
							arId.add(parser.getValue(itemE, GlobalVar.TAG_ID));
							arWriter.add(parser.getValue(itemE, GlobalVar.TAG_WRITER));
							arMsg.add(parser.getValue(itemE, GlobalVar.TAG_MSG));
							arDate.add(parser.getValue(itemE, GlobalVar.TAG_REGDATE));
							arCount.add(parser.getValue(itemE, GlobalVar.TAG_REPLYCNT));
							arIsImage.add(parser.getValue(itemE, GlobalVar.TAG_ISIMAGE));
							arSort.add(parser.getValue(itemE, GlobalVar.TAG_SORT));
						}
						// create more load btn
						if (itemList.getLength() == ITEM_NUMBER) {
							addBtn = new Button(Board.this);
							addBtn.setText(STRING_ADD);
							addBtn.setBackgroundColor(GlobalVar.BACKGROUND_COLOR);
							addBtn.setTypeface(GlobalVar.TYPEFACE);
							list.addFooterView(addBtn);
							// renew btn
							addBtn.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									new loadListView().execute();
								}
							});
							// delete more load btn
						} else {
							list.removeFooterView(addBtn);
						}
						int currentPosition = list.getFirstVisiblePosition();
						// Appending new data to menuItems ArrayList
						adapter = new BoardRow(Board.this, arWriter, arImage, arMsg, arDate, arCount, arIsImage, arSort);
						adapter.notifyDataSetChanged();
						list.setAdapter(adapter);
						// Setting new scroll position
						if (pageNum != 0)
							list.setSelectionFromTop(currentPosition + 1, 0);
						pageNum += 1;
					}
				});
			}
			return (null);
		}

		protected void onPostExecute(Void unused) {
			// closing progress dialog
			progDialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.board_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.board_mnu_write) {
			Intent intent = new Intent(context, Write.class);
			intent.putExtra(GlobalVar.EXTRA_NAME, currentName);
			intent.putExtra(GlobalVar.EXTRA_LINE, currentLine);
			startActivityForResult(intent, ACT_CLICK_WRITE);
			return true;
		} else if (id == R.id.board_mnu_refresh) {
			pageNum = 0;
			new loadListView().execute();
		} else if (id == R.id.board_mnu_before) {
			slideBoard(0);
			this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
		} else if (id == R.id.board_mnu_next) {
			slideBoard(1);
			this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		// when user first touches the screen we get x and y coordinate
		case MotionEvent.ACTION_DOWN: {
			x1 = event.getX();

			break;
		}
		case MotionEvent.ACTION_UP: {
			x2 = event.getX();

			// if left to right sweep event on screen
			if (x2 - x1 > 150) {
				slideBoard(0);
				this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
			}
			// if right to left sweep event on screen
			if (x1 - x2 > 150) {
				slideBoard(1);
				this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
			break;
		}
		}

		return false;
	}

	public void slideBoard(int flag) {
		StationInfo currentStation, nextStation;
		String[] surroundStation;
		String[] surroundLine;
		final ArrayList<String> nextNames = new ArrayList<String>();
		final ArrayList<String> nextLines = new ArrayList<String>();
		int size = 0;

		currentStation = SubwayInfo.findStation(currentName);
		surroundStation = currentStation.getSurroundingStation();
		surroundLine = currentStation.getLine();

		for (int i = 0; i < surroundLine.length; i++) {
			if (Lines.LINES_ENG[Integer.parseInt(surroundLine[i]) - 1].toLowerCase().equals(currentLine)) {
				size = i;
				break;
			}
		}
		if (flag == 0) {
			nextStation = SubwayInfo.findStation(surroundStation[size * 2]);
			// if last station,
			if (nextStation == null) {
				Toast.makeText(context, STRING_LAST_STATION, Toast.LENGTH_SHORT).show();
				return;
			} else {
				for (int i = 0; i < nextStation.getLine().length; i++) {
					nextNames.add(nextStation.getName());
					nextLines.add(nextStation.getLine()[i]);
				}

				if (currentStation.getOffset() % 2 == 1) {
					nextStation = SubwayInfo.findStation(surroundStation[size * 2 + 1]);
					for (int i = 0; i < nextStation.getLine().length; i++) {
						nextNames.add(nextStation.getName());
						nextLines.add(nextStation.getLine()[i]);
					}
				}
			}
		} else if (flag == 1) {
			nextStation = SubwayInfo.findStation(surroundStation[size * 2 + 1]);
			if (nextStation == null) {
				Toast.makeText(context, STRING_LAST_STATION, Toast.LENGTH_SHORT).show();
				return;
			} else {
				for (int i = 0; i < nextStation.getLine().length; i++) {
					nextNames.add(nextStation.getName());
					nextLines.add(nextStation.getLine()[i]);
				}

				if (currentStation.getOffset() % 2 == 0 && currentStation.getOffset() == (size * 2 + 2) && currentStation.getOffset() != 0) {
					nextStation = SubwayInfo.findStation(surroundStation[size * 2 + 1 + 1]);
					for (int i = 0; i < nextStation.getLine().length; i++) {
						nextNames.add(nextStation.getName());
						nextLines.add(nextStation.getLine()[i]);
					}
				}
			}
		}
		// 선택 가능한 역이 1개밖에 없을 경우 바로 게시판 호출
		if (nextNames.size() == 1) {
			Intent intent = new Intent(context, Board.class);
			intent.putExtra(GlobalVar.EXTRA_NAME, nextNames.get(0));
			intent.putExtra(GlobalVar.EXTRA_LINE, Lines.LINES_ENG[Integer.parseInt(nextLines.get(0)) - 1].toLowerCase());
			startActivityForResult(intent, ACT_CLICK_MOVE);
			finish();
		} else {
			SurroundingStationRow adapter = new SurroundingStationRow(Board.this, R.layout.rank_row, nextNames, nextLines);
			AlertDialog.Builder builder = new AlertDialog.Builder(Board.this, R.style.dialog);
			builder.setAdapter(adapter, null);

			TextView content = new TextView(Board.this);
			content.setText(STRING_SELECT_STATION);
			content.setTextColor(GlobalVar.BACKGROUND_COLOR);
			content.setTextSize(20);
			content.setTypeface(GlobalVar.TYPEFACE);
			content.setPadding(0, 30, 0, 30);

			builder.setCustomTitle(content);
			builder.setItems((CharSequence[]) nextNames.toArray(new String[nextNames.size()]), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int index) {
					Intent intent = new Intent(context, Board.class);
					intent.putExtra(GlobalVar.EXTRA_NAME, nextNames.get(index));
					intent.putExtra(GlobalVar.EXTRA_LINE, Lines.LINES_ENG[Integer.parseInt(nextLines.get(index)) - 1].toLowerCase());
					startActivityForResult(intent, ACT_CLICK_MOVE);
					finish();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();

			int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
			// View divider = dialog.findViewById(dividerId);
			// divider.setBackgroundColor(GlobalVar.BACKGROUND_COLOR);
		}
	}

}
