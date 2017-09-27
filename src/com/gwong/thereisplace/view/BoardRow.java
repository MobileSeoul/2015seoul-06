package com.gwong.thereisplace.view;

import java.util.ArrayList;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.activity.BaseActivity;
import com.gwong.thereisaplace.data.GlobalVar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BoardRow extends ArrayAdapter<String> {
	public Activity context;
	private ArrayList<String> name;
	private ArrayList<String> msg;
	private ArrayList<String> date;
	private ArrayList<String> count;
	private ArrayList<String> isImage;
	private ArrayList<String> sort;
	private ViewHolder viewHolder = null;

	public BoardRow(Activity context, ArrayList<String> name, ArrayList<Bitmap> imageId, ArrayList<String> msg, ArrayList<String> date, ArrayList<String> count, ArrayList<String> isImage,
			ArrayList<String> sort) {
		super(context, R.layout.board_row, name);
		this.context = context;
		this.name = name;
		this.msg = msg;
		this.date = date;
		this.count = count;
		this.isImage = isImage;
		this.sort = sort;
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		View rowView = converView;
		// 이렇게 listview 하면 스크롤 내려도 메모리 부족하다고 안함
		if (rowView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.board_row, parent, false);
			viewHolder.profile = (ImageView) rowView.findViewById(R.id.board_iv_profile);
			viewHolder.listview_name = (TextView) rowView.findViewById(R.id.board_tv_name);
			viewHolder.listview_msg = (TextView) rowView.findViewById(R.id.board_tv_msg);
			viewHolder.listview_date = (TextView) rowView.findViewById(R.id.board_tv_date);
			viewHolder.listview_reply = (TextView) rowView.findViewById(R.id.board_tv_reply);
			viewHolder.listview_isImage = (TextView) rowView.findViewById(R.id.board_tv_image);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		viewHolder.listview_name.setText(name.get(position));
		viewHolder.listview_msg.setText(msg.get(position));
		viewHolder.listview_date.setText(date.get(position).substring(5, 16));
		viewHolder.listview_reply.setText("댓글 " + count.get(position) + "개");
		viewHolder.listview_isImage.setText(isImage.get(position));
		viewHolder.stringSort = sort.get(position);
		if (viewHolder.listview_isImage.getText().equals("1")) {
			viewHolder.listview_isImage.setText("이미지");
			viewHolder.listview_isImage.setVisibility(View.VISIBLE);
		} else
			viewHolder.listview_isImage.setVisibility(View.INVISIBLE);

		if (viewHolder.stringSort.equals(GlobalVar.SORT_COMMENT)) {
			viewHolder.profile.setImageResource(R.drawable.comment);
		} else if (viewHolder.stringSort.equals(GlobalVar.SORT_QUESTION)) {
			viewHolder.profile.setImageResource(R.drawable.question);
		} else if (viewHolder.stringSort.equals(GlobalVar.SORT_ANNOUNCEMENT)) {
			viewHolder.profile.setImageResource(R.drawable.announcement);
		} else if (viewHolder.stringSort.equals(GlobalVar.SORT_FOOD)) {
			viewHolder.profile.setImageResource(R.drawable.food);
		} else {
			viewHolder.profile.setImageResource(R.drawable.food);
		}
		BaseActivity.setGlobalFont(rowView);
		return rowView;
	}

	class ViewHolder {
		public ImageView profile = null;
		public TextView listview_name = null;
		public TextView listview_msg = null;
		public TextView listview_date = null;
		public TextView listview_reply = null;
		public TextView listview_isImage = null;
		String stringSort;
	}
}
