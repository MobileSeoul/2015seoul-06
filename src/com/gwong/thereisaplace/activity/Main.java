package com.gwong.thereisaplace.activity;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.data.GlobalVar;
import com.gwong.thereisaplace.data.IsNetworkStat;
import com.gwong.thereisplace.view.RankRow;
import com.gwong.thereisplace.view.SearchDialog;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public class Main extends BaseActivity {
	private static final String RANK_XML_NAME = "rank.xml";
	private static final String RANK_DIALOG_TITLE = "   실시간 역 순위";
	private static final int RANK_CNT = 7;
	private static final int LODING_TIME = 2000;

	private ImageView mapIv;

	private PhotoViewAttacher attacher;
	private ProgressDialog progDialog;
	private SearchDialog searchDialog;
	private AlertDialog rankDialog;
	private StrictMode.ThreadPolicy policy;
	

	private String[] stationName;
	private String[] stationLine;
	private int cnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		// setting color
		GlobalVar.BACKGROUND_COLOR = getResources().getColor(R.color.background1);
		GlobalVar.BACKGROUND_COLOR2 = getResources().getColor(R.color.background2);
		getActionBar().setBackgroundDrawable(new ColorDrawable(GlobalVar.BACKGROUND_COLOR));
		getActionBar().setDisplayShowHomeEnabled(false);
		// setting policy
		policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// setting dpi and route map
		mapIv = (ImageView) findViewById(R.id.main_iv_map);
		SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.maps);
		mapIv.setImageDrawable(svg.createPictureDrawable());
		attacher = new PhotoViewAttacher(mapIv);
		attacher.setScaleType(ScaleType.CENTER_CROP);
		attacher.dpi = getResources().getDisplayMetrics().densityDpi;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.main_mnu_search) {
			searchDialog = new SearchDialog(Main.this);
			searchDialog.show();
			searchDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			searchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		} else if (id == R.id.main_mnu_rank) {
			if (IsNetworkStat.isNetworkStat(Main.this) == false) {
				return false;
			}

			stationName = new String[RANK_CNT];
			stationLine = new String[RANK_CNT];
			cnt = 0;
			// getting ranking data with progressDialog
			final Handler h = new Handler() {
				public void handleMessage(Message msg) {
					progDialog.dismiss();
					if (msg.arg1 == 1) {
						if (IsNetworkStat.isNetworkStat(Main.this) == false) {
						}
					}
				}
			};

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					Main.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progDialog = new ProgressDialog(Main.this, R.style.dialog);
							progDialog.setCancelable(false);
							progDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
							progDialog.setMessage("Loading");
							progDialog.show();
							h.sendEmptyMessageDelayed(1, LODING_TIME);
						}
					});

					Looper.prepare();
					try {
						XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
						factory.setNamespaceAware(true);
						XmlPullParser xpp = factory.newPullParser();
						URL server = new URL(GlobalVar.SERVER_ADDRESS + "/" + URLEncoder.encode(RANK_XML_NAME, "UTF-8"));
						InputStream is = server.openStream();
						xpp.setInput(is, "UTF-8");
						int eventType = xpp.getEventType();

						while (eventType != XmlPullParser.END_DOCUMENT) {
							if (eventType == XmlPullParser.START_TAG) {
								if (xpp.getName().equals(GlobalVar.TAG_NAME)) {
									stationName[cnt] = xpp.nextText();
								} else if (xpp.getName().equals(GlobalVar.TAG_LINE)) {
									stationLine[cnt] = xpp.nextText();
									cnt++;
									if (cnt == RANK_CNT)
										break;
								}
							}
							eventType = xpp.next();
						}

						RankRow adapter = new RankRow(Main.this, R.layout.rank_row, Arrays.asList(stationName), Arrays.asList(stationLine));
						AlertDialog.Builder builder = new AlertDialog.Builder(Main.this, R.style.dialog);
						builder.setAdapter(adapter, null);

						TextView content = new TextView(Main.this);
						content.setText(RANK_DIALOG_TITLE);
						content.setTextColor(GlobalVar.BACKGROUND_COLOR2);
						content.setTextSize(20);
						content.setTypeface(GlobalVar.TYPEFACE);
						content.setPadding(0, 30, 0, 30);

						builder.setCustomTitle(content);
						builder.setItems(stationName, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int index) {
							}
						});
						rankDialog = builder.create();
						rankDialog.show();

						int dividerId = rankDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
						View divider = rankDialog.findViewById(dividerId);
						divider.setBackgroundColor(GlobalVar.BACKGROUND_COLOR2);

						h.sendEmptyMessage(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Looper.loop();
				}
			});
			t.start();
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
}
