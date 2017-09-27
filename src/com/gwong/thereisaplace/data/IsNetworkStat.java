package com.gwong.thereisaplace.data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;

import com.gwong.thereisaplace.R;

public class IsNetworkStat {
	private static final String NETWORK_DIALOG_TITLE = "   네트워크 오류";
	private static final String NETWORK_DIALOG_CONTENT = "네트워크 상태를 확인해 주십시요.";
	private static final String NETWORK_DIALOG_OK = "확인";
	
	public static boolean isNetworkStat(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
		boolean blte_4g = false;
		if (lte_4g != null)
			blte_4g = lte_4g.isConnected();
		if (mobile != null) {
			if (mobile.isConnected() || wifi.isConnected() || blte_4g)
				return true;
		} else {
			if (wifi.isConnected() || blte_4g)
				return true;
		}
		

		AlertDialog.Builder dlg = new AlertDialog.Builder(context, R.style.dialog);
		TextView content = new TextView(context);
		content.setText(NETWORK_DIALOG_TITLE);
		content.setTextColor(GlobalVar.BACKGROUND_COLOR);
		content.setTextSize(20);
		content.setTypeface(GlobalVar.TYPEFACE);
		content.setPadding(0, 30, 0, 30);

		dlg.setCustomTitle(content);
		dlg.setMessage(NETWORK_DIALOG_CONTENT);

		Dialog d = dlg.show();
		TextView messageTv = (TextView) d.findViewById(android.R.id.message);
		messageTv.setTypeface(GlobalVar.TYPEFACE);
		int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = d.findViewById(dividerId);
		divider.setBackgroundColor(GlobalVar.BACKGROUND_COLOR);
		
		return false;
	}
}
