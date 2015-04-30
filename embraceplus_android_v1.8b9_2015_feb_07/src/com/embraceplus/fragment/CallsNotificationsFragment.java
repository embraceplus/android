package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
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
import android.widget.TextView;

import com.embraceplus.app.R;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.database.CallsAndClockSilentStatusDB;
import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.model.MessageItem;
import com.embraceplus.model.NotificationWeirdObj;
import com.embraceplus.utils.ListViewCompat;
import com.embraceplus.utils.SlideView;
import com.embraceplus.utils.SlideView.OnSlideListener;
import com.embraceplus.widget.SelectPicPopupWindow;

public class CallsNotificationsFragment extends BaseFragment implements OnItemClickListener, OnClickListener, OnSlideListener {
	private ListViewCompat mListView;
	public static List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
	List<String> callNoti;
	private NotificationWeirdObj notificationWeirdObj;
	private SlideView mLastSlideViewWithStatusOn;
	private static String silent_msg_name = "Silent";
	private static String un_silent_msg_name = "On";
	SlideAdapter mSlideAdapter;
	List<String> notificationList;
	SelectPicPopupWindow menuWindow;

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

	public void initTitleBar() {
		setTitle("Calls");
		initBackButton();
		initSettingButton();
		setRightImageButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menuWindow = new SelectPicPopupWindow(CallsNotificationsFragment.this.getActivity(), itemsOnClick);
				menuWindow.showAtLocation(CallsNotificationsFragment.this.getActivity().findViewById(R.id.add_style_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
			}
		});		
	}

	private void initView() {
		mListView = (ListViewCompat) this.getActivity().findViewById(R.id.list);
		// notificationWeirdObj = DbBuilder.getInstant().getCallNotifications();
		notificationWeirdObj = NotificationCommandMappingDB.getInstance().getCallNotifications();
		List<String> notifications = notificationWeirdObj.getNotificatinoNameList();
		// Add mandatory notifications first
		notificationList = new ArrayList<String>();

		mMessageItems.clear();

		notificationList.addAll(notifications);
		// notificationList.add("Clock");
		for (int i = 0; i < notificationList.size(); i++) {
			MessageItem item = new MessageItem();
			String notificationName = notificationList.get(i);
			// if (notificationName.equals("Unknown  Call"))
			if (notificationName.equals(Constants.notification_type_UnknownCall)) {
				item.iconRes = R.drawable.call_unknown;
				item.title = notificationName;
				item.arrow = R.drawable.notification_cell_arrow;
			} else {
				item.iconRes = R.drawable.call_icon;
				item.title = notificationName;
				item.arrow = R.drawable.notification_cell_arrow;
			}

			mMessageItems.add(item);
		}
		mSlideAdapter = new SlideAdapter();
		mListView.setAdapter(mSlideAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (data == null) {
			return;
		}
		switch (reqCode) {
		case (1):
			// if (resultCode == Activity.RESULT_OK) {
			Uri contactData = data.getData();
			Cursor c = getActivity().managedQuery(contactData, null, null, null, null);
			if (c.moveToFirst()) {
				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				MessageItem item = new MessageItem();
				item.setTitle(name);
				item.setIconRes(R.drawable.call_incoming);
				item.setArrow(R.drawable.notification_cell_arrow);
				mMessageItems.add(item);
				/*
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				//if (i == 0){
					map.put("title_text", name);
					map.put("icon", R.drawable.call_incoming);
					list.add(map);*/

				// DbBuilder.getInstant().addNewCallNotificatioin(name);
				// notificationWeirdObj =
				// DbBuilder.getInstant().getCallNotifications();
				NotificationCommandMappingDB.getInstance().addNewCallNotificatioin(name);
				notificationWeirdObj = NotificationCommandMappingDB.getInstance().getCallNotifications();

				mSlideAdapter.notifyDataSetChanged();
				// String phoneNumber =
				// c.getString(c.getColumnIndex(ContactsContract.Contacts.));

			}
			// }
			break;
		}
	}

	@Override
	public void onSlide(View view, int status) {

		// ���ǰ�����Ѿ��򿪵�SlideView����ô����ر�
		if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
			mLastSlideViewWithStatusOn.shrink();
		}
		// ��¼���δ��ڴ�״̬��view
		if (status == SLIDE_STATUS_ON) {
			mLastSlideViewWithStatusOn = (SlideView) view;

		}
//		onSlideEvent = true;

	}

	@Override
	public void onClick(View v) {

		int position = mListView.getPositionForView(v);
		String notificationName = mMessageItems.get(position).getTitle();
		// if (notificationName.equals("Incoming Call") ||
		// notificationName.equals("Unknown  Call"))
		if (notificationName.equals(Constants.notification_type_IncomingCall) || notificationName.equals(Constants.notification_type_UnknownCall)) {

		} else {
			notificationName = "Call_" + notificationName;
		}

		if (v.getId() == R.id.delete) {
			// int position = mListView.getPositionForView(v);
			if (position != ListView.INVALID_POSITION) {

				// String notificationName = notificationList.get(position);
				// DbBuilder.getInstant().removeNotificationOnCurrentTheme(notificationName);
				NotificationCommandMappingDB.getInstance().removeNotificationOnCurrentTheme(notificationName);
				CallsAndClockSilentStatusDB.getInstance().silentNotificationOnCurrentTheme(notificationName, false);

				mMessageItems.remove(position);
				mSlideAdapter.notifyDataSetChanged();
			}
		} else if (v.getId() == R.id.silent) {
			// int position = mListView.getPositionForView(v);

			TextView tv1 = (TextView) v.findViewById(R.id.silent);

			if (position != ListView.INVALID_POSITION) {

				TextView tv = (TextView) v;
				String text = tv.getText().toString();
				if (text.equals(silent_msg_name)) {
					// String notificationName = notificationList.get(position);
					// DbBuilder.getInstant().silentNotificationOnCurrentTheme(notificationName,true);
					CallsAndClockSilentStatusDB.getInstance().silentNotificationOnCurrentTheme(notificationName, true);
					tv1.setText(un_silent_msg_name);
					// mMessageItems.remove(position);
					// mSlideAdapter.notifyDataSetChanged();

				} else if (text.equals(un_silent_msg_name)) {
					// String notificationName = notificationList.get(position);
					// DbBuilder.getInstant().silentNotificationOnCurrentTheme(notificationName,false);
					CallsAndClockSilentStatusDB.getInstance().silentNotificationOnCurrentTheme(notificationName, false);

					tv1.setText(silent_msg_name);
					// mMessageItems.remove(position);
					// mSlideAdapter.notifyDataSetChanged();

				}

			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String notificationName = mMessageItems.get(position).getTitle();

		// if (notificationName.equals("Incoming Call") ||
		// notificationName.equals("Unknown  Call"))
		if (notificationName.equals(Constants.notification_type_IncomingCall) || notificationName.equals(Constants.notification_type_UnknownCall)) {

		} else {
			notificationName = "Call_" + (String) mMessageItems.get(position).getTitle();
		}
		Bundle bundle = new Bundle();
		bundle.putString("notification", notificationName);
		FxMenuViewFragment fragment = new FxMenuViewFragment();
		fragment.setArguments(bundle);
		replaceFragment(fragment);
	}

	public class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		SlideAdapter() {
			super();
			mInflater = CallsNotificationsFragment.this.getActivity().getLayoutInflater();
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
			if (notificationName.equalsIgnoreCase("Calls")) {
				// enabled =
				// DbBuilder.getInstant().getCurrentThemeCallsAndClockEnableStatus("Calls");
				enabled = CallsAndClockSilentStatusDB.getInstance().getCurrentThemeCallsAndClockEnableStatus("Calls");
			} else if (notificationName.equalsIgnoreCase("Clock")) {
				// enabled =
				// DbBuilder.getInstant().getCurrentThemeCallsAndClockEnableStatus("Clock");;
				enabled = CallsAndClockSilentStatusDB.getInstance().getCurrentThemeCallsAndClockEnableStatus("Clock");
			}

			if (slideView == null) {
				// ���������ǵ�item
				View itemView = mInflater.inflate(R.layout.notification_list_item, null);

				slideView = new SlideView(CallsNotificationsFragment.this.getActivity());
				// �����item���뵽slideView
				slideView.setContentView(itemView);
				// ��������һЩ��ݻ���
				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(CallsNotificationsFragment.this);
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
			holder.delete.setOnClickListener(CallsNotificationsFragment.this);
			holder.delete.setTextColor(android.graphics.Color.WHITE);
			holder.silent.setOnClickListener(CallsNotificationsFragment.this);
			String text = "silent";
			if (enabled.equals("1")) {
				text = silent_msg_name;
			} else if (enabled.equals("0")) {
				text = un_silent_msg_name;
			}
			holder.silent.setText(text);

			LayoutParams lp = holder.deleteHolder.getLayoutParams();
			// if (notificationName.equals("Incoming Call") ||
			// notificationName.equals("Unknown  Call"))
			if (notificationName.equals(Constants.notification_type_IncomingCall) || notificationName.equals(Constants.notification_type_UnknownCall)) {
				slideView.setWidth(90);
				holder.delete.setVisibility(View.GONE);
				// holder.silent.getLayoutParams();
				// holder.silent.setLayoutParams(new
				// RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			} else {
				slideView.setWidth(90 * 2);
				holder.delete.setVisibility(View.VISIBLE);
			}

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
			// delete.setTextColor(0xffffff);
		}
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			int id = v.getId();
			if (id == R.id.button_edit) {

			} else if (id == R.id.btn_add) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 1);

			} else {
			}
		}

	};
}
