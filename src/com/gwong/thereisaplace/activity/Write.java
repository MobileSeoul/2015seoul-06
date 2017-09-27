package com.gwong.thereisaplace.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.data.GlobalVar;

public class Write extends BaseActivity {
	private static final int ACT_TAKE_CAMERA = 0;

	private static String register_tag = "register";
	private static String search_tag = "searchAll";

	private String stationName;
	private String stationLine;
	private String isImage = "0";

	private EditText msgET;
	private Button regBtn;
	private Button cancelBtn;
	private RadioGroup sortRadioGroup;

	private StrictMode.ThreadPolicy policy;

	private int serverResponseCode = 0;
	private ProgressDialog progDialog = null;

	/********** File Path *************/
	private String uploadServerAddr = null;
	private String picturePath = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(GlobalVar.BACKGROUND_COLOR));
		getActionBar().setDisplayShowHomeEnabled(false);

		policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		Intent intent = getIntent();
		stationName = (String) intent.getStringExtra(GlobalVar.EXTRA_NAME);
		stationLine = (String) intent.getStringExtra(GlobalVar.EXTRA_LINE);
		this.setTitle(stationName);

		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		TextView tv = (TextView) findViewById(titleId);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(GlobalVar.TYPEFACE);

		msgET = (EditText) findViewById(R.id.write_et_msg);
		regBtn = (Button) findViewById(R.id.write_btn_reg);
		cancelBtn = (Button) findViewById(R.id.write_btn_cancel);
		sortRadioGroup = (RadioGroup) findViewById(R.id.write_rg_group);

		uploadServerAddr = GlobalVar.SERVER_ADDRESS + "/UploadToServer.php";

		regBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (msgET.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "글을 입력해주세요.", Toast.LENGTH_SHORT).show();
					return;
				}

				final Handler h = new Handler() {
					public void handleMessage(Message msg) {
						progDialog.dismiss();
						setResult(Activity.RESULT_OK);
						finish();
					}
				};

				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						Write.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progDialog = new ProgressDialog(Write.this, R.style.dialog);
								progDialog.setCancelable(false);
								progDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
								progDialog.setMessage("Uploading");
								progDialog.show();
							}
						});

						String id = null;
						String msg = msgET.getText().toString();
						String sort = null;
						switch (sortRadioGroup.getCheckedRadioButtonId()) {
						case R.id.write_rb_comment:
							sort = GlobalVar.SORT_COMMENT;
							break;
						case R.id.write_rb_question:
							sort = GlobalVar.SORT_QUESTION;
							break;
						case R.id.write_rb_announcement:
							sort = GlobalVar.SORT_ANNOUNCEMENT;
							break;
						case R.id.write_rb_food:
							sort = GlobalVar.SORT_FOOD;
							break;
						}
						try {
							URL url = new URL(GlobalVar.SERVER_ADDRESS + "/subway.php?" + "tag=" + URLEncoder.encode(register_tag, "UTF-8") + "&name=" + URLEncoder.encode(stationName, "UTF-8")
									+ "&line=" + URLEncoder.encode(stationLine, "UTF-8") + "&writer=" + URLEncoder.encode("익명", "UTF-8") + "&msg=" + URLEncoder.encode(msg, "UTF-8") + "&isImage="
									+ URLEncoder.encode(isImage, "UTF-8") + "&sort=" + URLEncoder.encode(sort, "UTF-8"));
							url.openStream();

							if (picturePath != null) {
								url = new URL(GlobalVar.SERVER_ADDRESS + "/search.php?" + "tag=" + URLEncoder.encode(search_tag, "UTF-8") + "&stationName=" + URLEncoder.encode(stationName, "UTF-8")
										+ "&stationLine=" + URLEncoder.encode(stationLine, "UTF-8"));
								url.openStream();

								XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
								factory.setNamespaceAware(true);
								XmlPullParser xpp = factory.newPullParser();
								URL server = new URL(GlobalVar.SERVER_ADDRESS + "/" + URLEncoder.encode(stationName + "_" + stationLine + ".xml", "UTF-8"));
								InputStream is = server.openStream();
								xpp.setInput(is, "UTF-8");

								int eventType = xpp.getEventType();
								while (eventType != XmlPullParser.END_DOCUMENT) {
									if (eventType == XmlPullParser.START_TAG) {
										if (xpp.getName().equals("id")) {
											id = xpp.nextText();
											break;
										}
									}
									eventType = xpp.next();
								}

								final String idid = id;
								uploadFile(picturePath, stationLine, idid);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						h.sendEmptyMessage(0);
					}
				});
				t.start();
			}

		});

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
	}

	public void setContentView(int viewId) {
		View view = LayoutInflater.from(this).inflate(viewId, null);
		BaseActivity.setGlobalFont(view);
		super.setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.write_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.write_mnu_camara) {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, ACT_TAKE_CAMERA);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onContextItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case 1:
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, ACT_TAKE_CAMERA);
			return true;

		}
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACT_TAKE_CAMERA:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				cursor.close();

				Toast.makeText(getApplicationContext(), "글을 등록하시면 사진이 업로드 됩니다.", Toast.LENGTH_SHORT).show();
				// BitmapFactory.decodeFile(picturePath);
				isImage = "1";
			}
			break;
		}
	}

	public int uploadFile(final String sourceFileUri, final String stationLine, final String id) {
		final File sourceFile = new File(sourceFileUri);
		if (sourceFile.isFile()) {

			try {
				String fileName = sourceFileUri;
				HttpURLConnection conn = null;
				DataOutputStream dos = null;
				String lineEnd = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";
				int bytesRead, bytesAvailable, bufferSize;
				byte[] buffer;
				int maxBufferSize = 1 * 1024 * 1024;

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				URL url = new URL(uploadServerAddr);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + stationLine + "_" + id + ".png" + "\"" + lineEnd);
				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				// send multipart form data necesssary after file
				// data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();

				if (serverResponseCode == 200) {
					runOnUiThread(new Runnable() {
						public void run() {
						}
					});
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return serverResponseCode;
	}
}
