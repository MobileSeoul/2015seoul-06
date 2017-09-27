package com.gwong.thereisplace.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.activity.BaseActivity;

public class ReplyRow extends ArrayAdapter<String> {
	private final Activity context;
	private final ArrayList<String> replyMsg;
	private final ArrayList<String> replyDate;

	public ReplyRow(Activity context, ArrayList<String> replyMsg, ArrayList<String> replyDate) {
		super(context, R.layout.reply_row, replyMsg);
		this.context = context;
		this.replyMsg = replyMsg;
		this.replyDate = replyDate;

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View rowView;
		// 이렇게 listview 하면 스크롤 내려도 메모리 부족하다고 안함
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.reply_row, parent, false);
			
			TextView listview_msg = (TextView) rowView.findViewById(R.id.listview_replyMsg);
			TextView listview_date = (TextView) rowView.findViewById(R.id.listview_replyDate);
			listview_msg.setText(replyMsg.get(position));
			listview_date.setText(replyDate.get(position).substring(5,16));
		} else {
			rowView = view;
		}

		BaseActivity.setGlobalFont(rowView);
		return rowView;
	}
}
