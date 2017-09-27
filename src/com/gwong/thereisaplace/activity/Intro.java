package com.gwong.thereisaplace.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.gwong.thereisaplace.R;

/*
 *	초기화면
 *	대기시간 : 1000ms
 */

public class Intro extends BaseActivity {
	private final int WAITING_TIME = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_activity);

		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		int screenWidth = display.widthPixels;
		int screenHeight = display.heightPixels;
		
		ImageView iv = (ImageView) findViewById(R.id.intro_iv_intro);
		LayoutParams params = (LayoutParams) iv.getLayoutParams();
		params.width = screenWidth;
		params.height = screenHeight;
		iv.setLayoutParams(params);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(Intro.this, Main.class);
				startActivity(intent);
				finish();
			}
		}, WAITING_TIME);
	}
}
