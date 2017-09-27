package com.gwong.thereisplace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.gwong.thereisaplace.R;
import com.gwong.thereisaplace.activity.Board;
import com.gwong.thereisaplace.data.GlobalVar;
import com.gwong.thereisaplace.data.Lines;
import com.gwong.thereisaplace.data.StationInfo;
import com.gwong.thereisaplace.data.SubwayInfo;

public class SearchDialog extends AlertDialog implements OnTouchListener {
	private static final String HASH_ITEM1 = "flag";
	private static final String HASH_ITEM2 = "text";
	
	private Context context;
	private AutoCompleteTextView autoTv;
	private TextView tv;

	public SearchDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_dialog);

		autoTv = (AutoCompleteTextView) findViewById(R.id.search_actv_input);
		autoTv.setTypeface(GlobalVar.TYPEFACE);
		List<HashMap<String, String>> stationList = new ArrayList<HashMap<String, String>>();
		// setting auto complete list
		for (int i = 0; i < SubwayInfo.stationInfo.size(); i++) {
			StationInfo info = SubwayInfo.stationInfo.get(i);
			for (int j = 0; j < info.getLine().length; j++) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put(HASH_ITEM1, Integer.toString(Lines.LINES_ICON[Integer.parseInt(info.getLine()[j]) - 1]));
				hm.put(HASH_ITEM2, info.getName());
				stationList.add(hm);
			}
		}

		String[] from = { HASH_ITEM1, HASH_ITEM2 };
		int[] to = { R.id.flag, R.id.txt };

		SimpleAdapter adapter = new SimpleAdapter(context, stationList, R.layout.search_row, from, to) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row = convertView;
				if (row == null) {
					LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					row = inflator.inflate(R.layout.search_row, null);
				}

				tv = (TextView) row.findViewById(R.id.txt);
				tv.setTypeface(GlobalVar.TYPEFACE);
				return super.getView(position, row, parent);
			}
		};

		CustomAutoCompleteTextView autoComplete = (CustomAutoCompleteTextView) findViewById(R.id.search_actv_input);
		autoComplete.setDropDownHeight(context.getResources().getDisplayMetrics().densityDpi);
		OnItemClickListener itemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				HashMap<String, String> hm = (HashMap<String, String>) arg0.getAdapter().getItem(position);
				int icon = Integer.parseInt(hm.get( HASH_ITEM1));
				String stationName = hm.get(HASH_ITEM2);

				Intent intent = new Intent(context, Board.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(GlobalVar.EXTRA_NAME, stationName);
				for (int i = 0; i < Lines.LINES_ICON.length; i++) {
					if (Lines.LINES_ICON[i] == icon) {
						intent.putExtra(GlobalVar.EXTRA_LINE, Lines.LINES_ENG[i]);
						break;
					}
				}
				context.startActivity(intent);
			}
		};
		autoComplete.setOnItemClickListener(itemClickListener);
		autoComplete.setAdapter(adapter);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public void dismiss() {
		autoTv.setText("");
		super.dismiss();
	}
}
