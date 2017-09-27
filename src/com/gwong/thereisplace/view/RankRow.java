package com.gwong.thereisplace.view;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.activity.BaseActivity;
import com.gwong.thereisaplace.activity.Board;
import com.gwong.thereisaplace.data.GlobalVar;
import com.gwong.thereisaplace.data.Lines;

public class RankRow extends ArrayAdapter<String> {
	private Context context;
	private ViewHolder holder;
	private List<String> stations;
	private List<String> lines;

	class ViewHolder {
		int position;
		ImageView icon;
		TextView title;
	};

	public RankRow(Context context, int textViewResourceId, List<String> stations, List<String> lines) {
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

		for (int i = 0; i < Lines.count; i++) {
			if (Lines.LINES_ENG[i].equals(lines.get(position).toUpperCase())) {
				holder.icon.setImageResource(Lines.LINES_ICON[i]);
				break;
			}
		}
		holder.title.setText(stations.get(position));

		row.setTag(holder);
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, Board.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(GlobalVar.EXTRA_NAME, stations.get(position));
				intent.putExtra(GlobalVar.EXTRA_LINE, lines.get(position));
				v.getContext().startActivity(intent);
			}
		});
		
		BaseActivity.setGlobalFont(row);
		return (row);
	}
}
