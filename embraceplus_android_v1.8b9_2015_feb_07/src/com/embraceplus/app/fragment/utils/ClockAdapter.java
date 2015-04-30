package com.embraceplus.app.fragment.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.embraceplus.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClockAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	private LayoutInflater mInflater;

	public ClockAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.notification_list_item, null);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView.findViewById(R.id.title);
			holder.image = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText((CharSequence) list.get(position).get("title_text"));
		holder.image.setBackgroundResource((Integer) list.get(position).get("icon"));
		holder.textView.setTextColor(android.graphics.Color.WHITE);
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		private TextView textView;
		private ImageView image;
	}
}
