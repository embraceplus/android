package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.embraceplus.app.R;

public class HelpViewFragment extends BaseFragment {
	private ListView helpItemsListView;
	List<ScrollView> helpView = new ArrayList<ScrollView>();
	int[] helpImageIDs = new int[] { R.drawable.help_welcome_to_embraceplus, R.drawable.help_howtogetstarted, R.drawable.help_mute_notifications, R.drawable.help_light_effects,
			R.drawable.help_calls_features, R.drawable.help_clock_features, R.drawable.help_calendar, R.drawable.help_notifications, R.drawable.help_other_info_notifications,
			R.drawable.help_important_settings, R.drawable.help_important_thirdparty_settings, R.drawable.help_themes, R.drawable.help_multiple_bands, R.drawable.help_troubleshooter };
	String[] items = new String[] { "Welcome to Embrace+", "How to get started", "Mute notifications", "Configure light effects", "Configure calls features", "Configure clock features",
			"Configure calendar", "Add or remove notifications", "Other info notifications", "Important Android settings", "Important third-party app settings", "Themes", "Connecting multiple bands",
			"Troubleshooter" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_help, container, false);
		createHelpView();
		return view;
	}

	private void createHelpView() {
		for (int i = 0; i < helpImageIDs.length; i++) {
			ScrollView sv = new ScrollView(this.getActivity());
			ImageView iv = new ImageView(this.getActivity());
			iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			iv.setTag(helpImageIDs[i]);
			sv.addView(iv);
			helpView.add(sv);
		}
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initBackButton();
		helpItemsListView = (ListView) this.getView().findViewById(R.id.helpItemsListView);
		helpItemsListView.setAdapter(new ItemAdapter());
		helpItemsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				HelpDetailViewFragment help = new HelpDetailViewFragment(index);
				replaceFragment(help);
			}
		});

		initBackButton();
	}

	class ItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int i) {
			return items[i];
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				holder = new ViewHolder();
				LayoutInflater layoutInflater = HelpViewFragment.this.getActivity().getLayoutInflater();
				View view = layoutInflater.inflate(R.layout.help_item, null);
				holder.tv = (TextView) view.findViewById(R.id.titleTextView);
				convertView = view;
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			holder.tv.setText(items[position]);
			return convertView;
		}
	}

	class ViewHolder {
		TextView tv;
	}

}
