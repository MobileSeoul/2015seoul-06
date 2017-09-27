package com.gwong.thereisaplace.activity;

import com.gwong.thereisaplace.data.GlobalVar;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseActivity extends Activity {
	private static final String TYPEFACE_NAME = "godoM.ttf";

	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		if (GlobalVar.TYPEFACE == null) {
			GlobalVar.TYPEFACE = Typeface.createFromAsset(this.getAssets(), TYPEFACE_NAME);
		}
		setGlobalFont(getWindow().getDecorView());
	}

	public static void setGlobalFont(View view) {
		if (view != null) {
			if (view instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) view;
				int vgCnt = vg.getChildCount();
				for (int i = 0; i < vgCnt; i++) {
					View v = vg.getChildAt(i);
					if (v instanceof TextView) {
						((TextView) v).setTypeface(GlobalVar.TYPEFACE);
					}
					setGlobalFont(v);
				}
			}
		}
	}
}
