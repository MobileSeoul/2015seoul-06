package com.gwong.thereisaplace.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.data.GlobalVar;
import com.gwong.thereisaplace.data.IsNetworkStat;
import com.gwong.thereisaplace.data.XMLParser;
import com.gwong.thereisplace.view.ReplyRow;

public class Content extends BaseActivity {
	private static final String imgUrl = GlobalVar.SERVER_ADDRESS + "/uploads/";
	private static final String reply_tag = "reply";
	private static final String KEY_ROOT = "rootroot";
	private static final String KEY_REPLY = "reply";
	private static final String STRING_NOINPUT = "댓글을 입력해주세요.";
	private static final String STRING_LODING = "Loding...";

	private String stationName;
	private String stationLine;
	private String id;
	private String replyMsg;
	private String xml;
	private org.w3c.dom.Document doc;
	private XMLParser parser;
	private String server;

	private String writerS, msgS, dateS;
	private Bitmap bmImg;

	private TextView writer;
	private TextView msg;
	private TextView date;
	private ImageView img;
	private StrictMode.ThreadPolicy policy;
	private EditText replyText;
	private Button replyBtn;

	private ReplyRow adapter;
	private ArrayList<String> arReplyMsg;
	private ArrayList<String> arReplyDate;
	private ListView list;

	private ProgressDialog progDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(GlobalVar.BACKGROUND_COLOR));
		getActionBar().setDisplayShowHomeEnabled(false);

		policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// getting data from board
		Intent intent = getIntent();
		stationName = (String) intent.getStringExtra(GlobalVar.EXTRA_NAME);
		stationLine = (String) intent.getStringExtra(GlobalVar.EXTRA_LINE);
		id = (String) intent.getStringExtra(GlobalVar.EXTRA_ID);
		this.setTitle(stationName);
		// setting custom title bar
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		TextView tv = (TextView) findViewById(titleId);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(GlobalVar.TYPEFACE);

		writer = (TextView) findViewById(R.id.content_tv_name);
		msg = (TextView) findViewById(R.id.content_tv_msg);
		date = (TextView) findViewById(R.id.content_tv_date);
		img = (ImageView) findViewById(R.id.content_iv_img);
		replyText = (EditText) findViewById(R.id.content_et_replyText);
		replyBtn = (Button) findViewById(R.id.content_btn_reply);

		final Handler h = new Handler() {
			public void handleMessage(Message msg) {
				progDialog.dismiss();
				img.setImageBitmap(bmImg);
				writer.setText("익명");
				date.setText(dateS.substring(5,16));
				Content.this.msg.setText(msgS);
				// setting listview
				adapter = new ReplyRow(Content.this, arReplyMsg, arReplyDate);
				adapter.notifyDataSetChanged();
				list = (ListView) findViewById(R.id.content_lv_reply);
				list.setAdapter(adapter);
				list.setDivider(new ColorDrawable(GlobalVar.BACKGROUND_COLOR));
				list.setDividerHeight(2);
			}
		};

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Content.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progDialog = new ProgressDialog(Content.this, R.style.dialog);
						progDialog.setCancelable(false);
						progDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
						progDialog.setMessage(STRING_LODING);
						progDialog.show();
					}
				});

				try {
					updateContent();
					updateReply();
					bmImg = null;
					// getting a image if exist
					if (IsNetworkStat.isNetworkStat(Content.this)) {
						URL url = new URL(imgUrl + stationLine + "_" + id + ".png");
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setDoInput(true);
						conn.connect();
						InputStream is = conn.getInputStream();
						bmImg = BitmapFactory.decodeStream(is);
						// resizing a image
						DisplayMetrics display = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(display);
						int screenWidth = display.widthPixels;
						int screenHeight = display.heightPixels;
						bmImg = Bitmap.createScaledBitmap(bmImg, screenWidth, screenHeight / 2, false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				h.sendEmptyMessage(0);
			}
		});
		if (IsNetworkStat.isNetworkStat(Content.this)) {
			t.start();
		}

		replyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (replyText.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), STRING_NOINPUT, Toast.LENGTH_SHORT).show();
					return;
				}

				final Handler h = new Handler() {
					public void handleMessage(Message msg) {
						progDialog.dismiss();
						// create listview
						adapter = new ReplyRow(Content.this, arReplyMsg, arReplyDate);
						adapter.notifyDataSetChanged();
						list = (ListView) findViewById(R.id.content_lv_reply);
						list.setAdapter(adapter);
						list.setDivider(new ColorDrawable(GlobalVar.BACKGROUND_COLOR));
						list.setDividerHeight(2);
						replyText.setText("");
					}
				};

				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						Content.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progDialog = new ProgressDialog(Content.this, R.style.dialog);
								progDialog.setCancelable(false);
								progDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
								progDialog.setMessage(STRING_LODING);
								progDialog.show();
							}
						});

						try {
							replyMsg = replyText.getText().toString();
							URL url = new URL(GlobalVar.SERVER_ADDRESS + "/reply.php?" + "tag=" + URLEncoder.encode(reply_tag, "UTF-8") + "&stationLine=" + URLEncoder.encode(stationLine, "UTF-8")
									+ "&id=" + URLEncoder.encode(id, "UTF-8") + "&replyMsg=" + URLEncoder.encode(replyMsg, "UTF-8"));
							url.openStream();
							updateReply();

						} catch (Exception e) {
							e.printStackTrace();
						}
						h.sendEmptyMessage(0);
					}
				});
				if (IsNetworkStat.isNetworkStat(Content.this)) {
					t.start();
				}
			}
		});
	}

	public void updateContent() {
		try {
			server = new String(GlobalVar.SERVER_ADDRESS + "/" + URLEncoder.encode(stationLine + "_" + id + ".xml", "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new XMLParser();
		xml = parser.getXmlFromUrl(server);
		doc = parser.getDomElement(xml);
		// getting data
		NodeList content = doc.getElementsByTagName(KEY_ROOT);
		for (int i = 0; i < content.getLength(); i++) {
			Element itemE = (Element) content.item(i);
			id = (parser.getValue(itemE, GlobalVar.TAG_ID));
			writerS = (parser.getValue(itemE, GlobalVar.TAG_WRITER));
			msgS = (parser.getValue(itemE, GlobalVar.TAG_MSG));
			dateS = (parser.getValue(itemE, GlobalVar.TAG_REGDATE));
		}
	}

	public void updateReply() {
		try {
			server = new String(GlobalVar.SERVER_ADDRESS + "/" + URLEncoder.encode(stationLine + "_" + id + ".xml", "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new XMLParser();
		xml = parser.getXmlFromUrl(server);
		doc = parser.getDomElement(xml);
		arReplyMsg = new ArrayList<String>();
		arReplyDate = new ArrayList<String>();
		// getting data
		NodeList content = doc.getElementsByTagName(KEY_ROOT);
		NodeList reply;
		for (int i = 0; i < content.getLength(); i++) {
			Element itemE = (Element) content.item(i);
			reply = itemE.getElementsByTagName(KEY_REPLY);
			for (int j = 0; j < reply.getLength(); j++) {
				Element replyE = (Element) reply.item(j);
				arReplyMsg.add(parser.getValue(replyE, GlobalVar.TAG_REPLYMSG));
				arReplyDate.add(parser.getValue(replyE, GlobalVar.TAG_REPLYDATE));
			}
		}
	}

	public void setContentView(int viewId) {
		View view = LayoutInflater.from(this).inflate(viewId, null);
		BaseActivity.setGlobalFont(view);
		super.setContentView(view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
}
