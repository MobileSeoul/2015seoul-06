package com.gwong.thereisplace.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.activity.BaseActivity;
import com.gwong.thereisaplace.data.Lines;

public class SelectRow extends ArrayAdapter<String> {
	private Context context;
	private ViewHolder holder;
	private List<String> lines;

	// 자료를 저장할 클래스를 만든다.
	class ViewHolder {
		int position;
		ImageView icon;
		TextView title;
	};

	public SelectRow(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.lines = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflator.inflate(R.layout.select_row, null);
		}

		holder = new ViewHolder();
		holder.icon = (ImageView) row.findViewById(R.id.select_iv_line);
		holder.title = (TextView) row.findViewById(R.id.select_tv_namet);
		holder.position = position;

		// 표시값 세팅
		holder.icon.setImageResource(Lines.LINES_ICON[Integer.parseInt(lines.get(position)) - 1]);
		holder.title.setText(Lines.LINES[Integer.parseInt(lines.get(position)) - 1]);

		row.setTag(holder);
		BaseActivity.setGlobalFont(row);
		return (row);
	}
}
