package com.gwong.thereisplace.view;

import java.util.List;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.activity.BaseActivity;
import com.gwong.thereisaplace.activity.Board;
import com.gwong.thereisaplace.data.GlobalVar;
import com.gwong.thereisaplace.data.Lines;
import com.gwong.thereisplace.view.RankRow.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SurroundingStationRow extends ArrayAdapter<String> {
	private Context context;
	private ViewHolder holder;
	private List<String> stations;
	private List<String> lines;

	class ViewHolder {
		int position;
		ImageView icon;
		TextView title;
	};

	public SurroundingStationRow(Context context, int textViewResourceId, List<String> stations, List<String> lines) {
		super(context, textViewResourceId, stations);
		this.context = context;
		this.stations = stations;
		this.lines = lines;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflator.inflate(R.layout.rank_row, null);
		}

		holder = new ViewHolder();
		holder.icon = (ImageView) row.findViewById(R.id.rank_iv_line);
		holder.title = (TextView) row.findViewById(R.id.rank_tv_name);
		holder.position = position;

		holder.icon.setImageResource(Lines.LINES_ICON[Integer.parseInt(lines.get(position))-1]);
		holder.title.setText(stations.get(position));


		row.setTag(holder);

		BaseActivity.setGlobalFont(row);
		return (row);
	}
}
