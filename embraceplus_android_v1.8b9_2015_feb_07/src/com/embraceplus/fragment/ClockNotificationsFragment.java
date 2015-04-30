package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.ClockAdapter;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.database.CallsAndClockSilentStatusDB;
import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.model.MessageItem;
import com.embraceplus.model.NotificationWeirdObj;
import com.embraceplus.utils.ClockFragmentUtil;
import com.embraceplus.utils.ListViewCompat;
import com.embraceplus.utils.SlideView;
import com.embraceplus.utils.SlideView.OnSlideListener;

public class ClockNotificationsFragment extends BaseFragment implements OnClickListener, OnSlideListener {
	private ClockAdapter adapter;
	private ListViewCompat mListView;
	private ArrayList<HashMap<String, Object>> list;
	private NotificationWeirdObj notificationWeirdObj;
	public static List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
	private SlideView mLastSlideViewWithStatusOn;

	private static String silent_msg_name = "Silent";
	private static String un_silent_msg_name = "On";
	SlideAdapter mSlideAdapter;
	List<String> notificationList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.calls_notification_fragment, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initTitleBar();
		initView();
	}

	private void initView() {
		mListView = (ListViewCompat) this.getActivity().findViewById(R.id.list);
//		notificationWeirdObj = DbBuilder.getInstant().getClockNotifications();
		notificationWeirdObj = NotificationCommandMappingDB.getInstance().getClockNotifications();
		List<String> notifications = notificationWeirdObj.getNotificatinoNameList();
		// Add mandatory notifications first
		notificationList = new ArrayList<String>();

		mMessageItems.clear();

		notificationList.addAll(notifications);
		for (int i = 0; i < notificationList.size(); i++) {
			MessageItem item = new MessageItem();
			String notificationName = notificationList.get(i);
			// if (notificationName.equals("Alarm"))
			if (notificationName.equals(Constants.notification_type_ALARM)) {
				item.iconRes = R.drawable.clock_alarm;
				item.title = notificationName;
				item.arrow = R.drawable.notification_cell_arrow;
				// }else if (notificationName.equals("Timer"))
			} else if (notificationName.equals(Constants.notification_type_Timer)) {
				item.iconRes = R.drawable.clock_timer;
				item.title = notificationName;
				item.arrow = R.drawable.notification_cell_arrow;
				// }else if (notificationName.equals("Grandpa Clock"))
			} else if (notificationName.equals(Constants.notification_type_GRANPACLOCK)) {
				item.iconRes = R.drawable.clock_grandpa;
				item.title = notificationName;
				item.arrow = R.drawable.notification_cell_arrow;
			}

			mMessageItems.add(item);
		}

		mSlideAdapter = new SlideAdapter();
		mListView.setAdapter(mSlideAdapter);
		// mListView.setOnItemClickListener(this);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {

					AlarmFragment fragment = null;
					if (ClockFragmentUtil.getInstant().getAlarmFragmentl() == null) {
						fragment = new AlarmFragment();
						ClockFragmentUtil.getInstant().setAlarmFragmentl(fragment);
					} else {
						fragment = ClockFragmentUtil.getInstant().getAlarmFragmentl();
					}
					replaceFragment(fragment);
				} else if (position == 1) {

					TimerFragment fragment = null;
//					if (ClockFragmentUtil.getInstant().getTimerFragment() == null) {
//						fragment = new TimerFragment();
//						ClockFragmentUtil.getInstant().setTimerFragment(fragment);
//					} else {
//						fragment = ClockFragmentUtil.getInstant().getTimerFragment();
//
//						// int countdownvalue=
//						// fragment.getCountdownChronometer().get
//					}
					fragment = new TimerFragment();

					replaceFragment(fragment);
				} else if (position == 2) {

					GrandpaClockGFragment fragment = null;
					fragment = new GrandpaClockGFragment();
					/*if (ClockFragmentUtil.getInstant().getGrandpaClockGFragment() ==null)
					{
						fragment = new GrandpaClockGFragment();
						ClockFragmentUtil.getInstant().setGrandpaClockGFragment(fragment);
					}else
					{
						fragment = ClockFragmentUtil.getInstant().getGrandpaClockGFragment();
					}*/

					replaceFragment(fragment);
				}

			}
		});
	}

	public void initTitleBar() {
		setTitle("Clock");
		initBackButton();
		initSettingButton();
		hideRightImageButton();
	}
	
	@Override
	public void onSlide(View view, int status) {

		// 锟斤拷锟角帮拷锟斤拷锟斤拷丫锟斤拷蚩锟絊lideView锟斤拷锟斤拷么锟斤拷锟斤拷乇锟�
		if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
			mLastSlideViewWithStatusOn.shrink();
		}
		// 锟斤拷录锟斤拷锟轿达拷锟节达拷状态锟斤拷view
		if (status == SLIDE_STATUS_ON) {
			mLastSlideViewWithStatusOn = (SlideView) view;

		}

	}

	@Override
	public void onClick(View v) {

		int position = mListView.getPositionForView(v);
		String notificationName = mMessageItems.get(position).getTitle();
		/* if (notificationName.equals("Incoming Call") || notificationName.equals("Unknown  Call"))
		  {
			  
		  }else
		  {
			  notificationName = "Call_"+notificationName;
		  }*/

		if (v.getId() == R.id.delete) {
			// int position = mListView.getPositionForView(v);
			if (position != ListView.INVALID_POSITION) {

				// String notificationName = notificationList.get(position);
//				DbBuilder.getInstant().removeNotificationOnCurrentTheme(notificationName);
				NotificationCommandMappingDB.getInstance().removeNotificationOnCurrentTheme(notificationName);
				mMessageItems.remove(position);
				mSlideAdapter.notifyDataSetChanged();
			}
		} else if (v.getId() == R.id.silent) {
			// int position = mListView.getPositionForView(v);

			TextView tv1 = (TextView) v.findViewById(R.id.silent);
			// System.out.println();

			if (position != ListView.INVALID_POSITION) {

				TextView tv = (TextView) v;
				String text = tv.getText().toString();
				if (text.equals(silent_msg_name)) {
					// String notificationName = notificationList.get(position);
//					DbBuilder.getInstant().silentNotificationOnCurrentTheme(notificationName, true);
					CallsAndClockSilentStatusDB.getInstance().silentNotificationOnCurrentTheme(notificationName, true);
					tv1.setText(un_silent_msg_name);
					// mMessageItems.remove(position);
					// mSlideAdapter.notifyDataSetChanged();

				} else if (text.equals(un_silent_msg_name)) {
					// String notificationName = notificationList.get(position);
//					DbBuilder.getInstant().silentNotificationOnCurrentTheme(notificationName, false);
					CallsAndClockSilentStatusDB.getInstance().silentNotificationOnCurrentTheme(notificationName, false);
					tv1.setText(silent_msg_name);
					// mMessageItems.remove(position);
					// mSlideAdapter.notifyDataSetChanged();

				}

			}
		}
	}

	public class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		SlideAdapter() {
			super();
			mInflater = ClockNotificationsFragment.this.getActivity().getLayoutInflater();
		}

		@Override
		public int getCount() {
			return mMessageItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessageItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			SlideView slideView = (SlideView) convertView;
			// String notificationName = notificationList.get(position);
			String notificationName = mMessageItems.get(position).getTitle();
			String enabled = "1";

			enabled = notificationWeirdObj.getNotificationNames().get(notificationName).getEnabled();
			if (slideView == null) {
				// 锟斤拷锟斤拷锟斤拷锟斤拷锟角碉拷item
				View itemView = mInflater.inflate(R.layout.notification_list_item, null);

				slideView = new SlideView(ClockNotificationsFragment.this.getActivity());
				// 锟斤拷锟斤拷锟絠tem锟斤拷锟诫到slideView
				slideView.setContentView(itemView);
				// 锟斤拷锟斤拷锟斤拷锟斤拷一些锟斤拷莼锟斤拷锟�
				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(ClockNotificationsFragment.this);
				slideView.setTag(holder);
			} else {
				holder = (ViewHolder) slideView.getTag();
			}
			MessageItem item = mMessageItems.get(position);
			item.slideView = slideView;
			item.slideView.shrink();
			holder.icon.setImageResource(item.iconRes);
			holder.title.setText(item.title);
			holder.title.setTextColor(android.graphics.Color.WHITE);
			holder.time.setImageResource(item.arrow);
			// holder.deleteHolder.setOnClickListener(CallsCallsNotificationsFragment.this);
			holder.delete.setOnClickListener(ClockNotificationsFragment.this);
			holder.silent.setOnClickListener(ClockNotificationsFragment.this);
			String text = "silent";
			if (enabled.equals("1")) {
				text = silent_msg_name;
			} else if (enabled.equals("0")) {
				text = un_silent_msg_name;
			}
			holder.silent.setText(text);

			slideView.setWidth(90);
			holder.delete.setVisibility(View.GONE);

			// holder.silent.setLayoutParams(new
			// RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

			return slideView;
		}

	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView title;

		public ImageView time;
		public TextView delete;
		public TextView silent;
		public ViewGroup deleteHolder;

		ViewHolder(View view) {
			icon = (ImageView) view.findViewById(R.id.icon);
			title = (TextView) view.findViewById(R.id.title);
			time = (ImageView) view.findViewById(R.id.time);
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
			delete = (TextView) view.findViewById(R.id.delete);
			silent = (TextView) view.findViewById(R.id.silent);
		}
	}
}
