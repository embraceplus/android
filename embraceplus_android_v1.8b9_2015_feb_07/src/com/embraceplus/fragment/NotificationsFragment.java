package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import com.embraceplus.app.MainActivity;
import com.embraceplus.app.R;
import com.embraceplus.app.SearchActivity;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.GlobalHandlerUtil;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.CallsAndClockSilentStatusDB;
import com.embraceplus.database.DefinedThemeDB;
import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.database.NotificationDefinitionDB;
import com.embraceplus.database.UtilitiesDB;
import com.embraceplus.model.MessageItem;
import com.embraceplus.model.NotificationWeirdObj;
import com.embraceplus.model.ThemeObj;
import com.embraceplus.utils.AlarmReceiver;
import com.embraceplus.utils.BaseBackPressedListener;
import com.embraceplus.utils.Item;
import com.embraceplus.utils.ListViewCompat;
import com.embraceplus.utils.ResourceUtils;
import com.embraceplus.utils.SkinSettingManager;
import com.embraceplus.utils.SlideView;
import com.embraceplus.utils.SlideView.OnSlideListener;
import com.embraceplus.widget.MyCountdownChronometer;

public class NotificationsFragment extends BaseFragment implements OnItemClickListener, OnClickListener, OnSlideListener {

	private final static String TAG = NotificationsFragment.class.getSimpleName();
	private ListViewCompat mListView;

	// public static List<MessageItem> mMessageItems = new
	// ArrayList<NotificationsFragment.MessageItem>();
	public static List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
	private static String silent_msg_name = "Silent";
	private static String un_silent_msg_name = "On";

	private SlideAdapter mSlideAdapter;
	String themeName;
	private NotificationWeirdObj notificationWeirdObj;

	// �ϴδ��ڴ�״̬��SlideView
	private SlideView mLastSlideViewWithStatusOn;
	MainActivity mainActivity;
	private View mTitleBar;
	List<String> notificationList;
	private Handler connectionHandler;
	ProgressDialog m_pDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initProcessDialog();
		getMainActivity().setOnBackPressedListener(new BaseBackPressedListener(getMainActivity()));
		return inflater.inflate(R.layout.fragment_notifications, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mainActivity = (MainActivity) this.getActivity();
		themeName = UtilitiesDB.getInstance().getCurrentTheme();
		initTitleBar();
		initConnection();
		initView();
	}

	public void initProcessDialog() {
		// 创建ProgressDialog对象
		m_pDialog = new ProgressDialog(this.getActivity());
		// 设置进度条风格，风格为圆形，旋转的
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	
		// 设置ProgressDialog 标题
		m_pDialog.setTitle("Connecting to the Embrace+");
	
		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage("");
	
		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);
	}

	public void initTitleBar() {

		mTitleBar = getView().findViewById(R.id.titlebar);
		setTitle("Notifications");
		initRefreshButton();
		initSettingButton();
	}

	private void initConnection() {
		connectionHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == Constants.handler_msg_connected_server || msg.what == Constants.handler_msg_disconnect_server) {
					try {
						m_pDialog.cancel();
					} catch (Throwable t) {

					}
				}
				// Regardless of what kind of messages we recived, we'll refresh
				// the connectButton
				refreshConnectButton();
			}
		};

		GlobalHandlerUtil.getInstant().setDemoHandler(connectionHandler);
		refreshConnectButton();
	}

	private void refreshConnectButton() {

		// For some reason, all screens are in one activity.
		// So we have to do a very dirty check to see if we're in the right
		// fragment...
		// At some point, the app should be broken into activitites, not dirty
		// fragments

		// if (!GlobalUtils
		// .getInstant()
		// .getCurrentFragment()
		// .equals("com.embraceplus.fragment.NotificationsFragment") ) return ;
		// if
		// (!BaseFragment.getCurrentFragment().equals("com.embraceplus.fragment.NotificationsFragment"))

		boolean userVisibleHint = getUserVisibleHint();
		Log.v("", "refreshConnectButton userVisibleHint:" + userVisibleHint);
		// MyToast.show("refreshConnectButton userVisibleHint:" +
		// userVisibleHint);

		// if
		// (!BaseFragment.getCurrentFragment().equals(this.getClass().getName()))
		// return;

		int connectButtonId = -1;
		if (ServiceManager.getInstant().isEmbraceAvailable()) {
			connectButtonId = R.drawable.btconnect;
		} else {
			connectButtonId = R.drawable.btdisconnect;
		}
		// mTitleBar.SetLeftButton(connectButtonId, "");
//		((ImageView)getView().findViewById(R.id.back)).setImageResource(connectButtonId);
		((ImageView) mTitleBar.findViewById(R.id.button_left)).setImageResource(connectButtonId);

		// For some reason, setting the left button hides the right button...
		// So we have to recreate it here

	}

	private void initRefreshButton() {
		// In case it wasn't set before, we'll set the connect button's handler
		(mTitleBar.findViewById(R.id.button_left)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshConnectButton();

				if (!ServiceManager.getInstant().isEmbraceAvailable()) {
					// Case one, the device is disconnected, so first of all we
					// show a loading dialog
					// try {
					// m_pDialog.show();
					// } catch (Throwable t) {
					//
					// }
					showConnectingDialogStat();
					// Case one-one If we already have connection saved
					// somewhere, we jsut call it
					if (ServiceManager.getInstant().getDevice() != null) {
						ServiceManager.getInstant().setConnectToBleDeviceAndJump(false);

						ServiceManager.getInstant().getBluetoothService().connect(ServiceManager.getInstant().getDevice());
					} else {
						// Case one-two We don't have a connection already,
						// we'll see if there's something saved on the device
						SharedPreferences deviceadd = getActivity().getSharedPreferences("deviceAddress", 0);
						String preAddress = deviceadd.getString("address", "");
						if (preAddress != null && !preAddress.equals("")) {
							// Case one-two-one, we have an adress saved, let's
							// try to connect
							ServiceManager.getInstant().setConnectToBleDeviceAndJump(false);
							ServiceManager.getInstant().setDevice(preAddress);
							ServiceManager.getInstant().getBluetoothService().connect(preAddress);
						} else {
							// Case one-two-two no address is saved, we have no
							// choice, we have to go back to the search activity
							// (if possible)
							m_pDialog.hide();
							if (NotificationsFragment.this.getActivity() != null)
								startActivity(new Intent(NotificationsFragment.this.getActivity(), SearchActivity.class));
						}
					}

				} else {
					// Case two, the device is connected, so unless the service
					// is null, we'll disconnect it
					if (ServiceManager.getInstant().getBluetoothService() == null) {
						return;
					}
					ServiceManager.getInstant().getBluetoothService().disconnect();
					ServiceManager.getInstant().setConnectStateChangeByUser(true);
				}
			}
		});
	}

	private void showConnectingDialogStat() {
		try {
			m_pDialog.show();
			connectionHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					m_pDialog.cancel();
				}
			}, 10000);
		} catch (Throwable t) {
		}
	}

	private void initView() {
		mListView = (ListViewCompat) getView().findViewById(R.id.list);
		// notificationWeirdObj =
		// DbBuilder.getInstant().getNotificationsOnCurrentTheme();
		notificationWeirdObj = NotificationCommandMappingDB.getInstance().getNotificationsOnCurrentTheme();
		List<String> notifications = notificationWeirdObj.getNotificatinoNameList();
		// Add mandatory notifications first
		notificationList = new ArrayList<String>();

		setSkin();
		mMessageItems.clear();

		if (notifications.contains(Constants.notification_type_IncomingCall) || notifications.contains(Constants.notification_type_UnknownCall)) {
			notificationList.add("Calls");
			notifications.remove(Constants.notification_type_IncomingCall);
			notifications.remove(Constants.notification_type_UnknownCall);
		}

		if (notifications.contains(Constants.notification_type_ALARM) || notifications.contains(Constants.notification_type_Timer) || notifications.contains(Constants.notification_type_GRANPACLOCK)) {
			notificationList.add("Clock");
			notifications.remove(Constants.notification_type_ALARM);
			notifications.remove(Constants.notification_type_Timer);
			notifications.remove(Constants.notification_type_GRANPACLOCK);
		}

		notificationList.addAll(notifications);
		// notificationList.add("Clock");
		for (int i = 0; i < notificationList.size(); i++) {
			MessageItem item = new MessageItem();
			String notificationName = notificationList.get(i);
			if (notificationName.equalsIgnoreCase("Calls")) {
				item.iconRes = R.drawable.call_icon;
				item.title = "Calls";
				item.arrow = R.drawable.notification_cell_arrow;
			} else if (notificationName.equalsIgnoreCase("Clock")) {
				item.iconRes = R.drawable.clock_icon;
				item.title = "Clock";
				item.arrow = R.drawable.notification_cell_arrow;
			} else {
				// String resourceName =
				// DbBuilder.getInstant().getResourceNameByNotificatioinName(notificationName);
				String resourceName = NotificationDefinitionDB.getInstance().getResourceNameByNotificatioinName(notificationName);
				item.iconRes = ResourceUtils.getInstance().getResourceId(resourceName);
				item.title = notificationName;
				item.arrow = R.drawable.notification_cell_arrow;
			}

			mMessageItems.add(item);
		}

		View footerView = LayoutInflater.from(this.getActivity()).inflate(R.layout.notification_list_item, null);
		ImageView image = (ImageView) footerView.findViewById(R.id.icon);
		image.setBackgroundResource(R.drawable.add_icon);

		TextView text = (TextView) footerView.findViewById(R.id.title);
		text.setText("Add event");
		text.setTextColor(android.graphics.Color.WHITE);
		mListView.addFooterView(footerView);
		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// for testing....read battery.....
				// ServiceManager.getInstant().getBluetoothService().readEmbraceBattery();

				System.out.println("onClick() dianji");
				AddEventViewFragment fragment = new AddEventViewFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.container, fragment);
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				transaction.commit();

			}
		});
		mSlideAdapter = new SlideAdapter();
		mListView.setAdapter(mSlideAdapter);
		mListView.setOnItemClickListener(this);

	}

	private void setSkin() {
		SkinSettingManager mSettingManager = new SkinSettingManager(this.getActivity());
		// List<ThemeObj> themeList = DbBuilder.getInstant().getThemes();
		List<ThemeObj> themeList = DefinedThemeDB.getInstance().getThemes();
		for (int j = 0; j < themeList.size(); j++) {
			String themeName = themeList.get(j).getThemeName();
			Item item = new Item();
			item.setTitle(themeName);

			int resoureceId = ResourceUtils.getInstance().getResourceId(themeList.get(j).getThemeIcon());
			item.setDrawable(resoureceId);
			item.setSelected(themeList.get(j).isSelected());

			if (themeList.get(j).isSelected()) {
				String backgroundName = themeList.get(j).getThemeBackground();
				if (!backgroundName.startsWith("bg_")) {
					backgroundName = themeList.get(j).getThemeName();
				}
				mSettingManager.changeSkin(backgroundName);

			}

		}
	}

	public class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		SlideAdapter() {
			super();
			mInflater = NotificationsFragment.this.getActivity().getLayoutInflater();
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
			String notificationName = notificationList.get(position);
			String enabled = "1";
			if (notificationName.equalsIgnoreCase("Calls")) {
				// enabled =
				// notificationWeirdObj.getNotificationNames().get("Incoming Call").getEnabled();
				// enabled =
				// notificationWeirdObj.getNotificationNames().get(Constants.notification_type_IncomingCall).getEnabled();
				// enabled =
				// DbBuilder.getInstant().getCurrentThemeCallsAndClockEnableStatus(notificationName);
				enabled = CallsAndClockSilentStatusDB.getInstance().getCurrentThemeCallsAndClockEnableStatus(notificationName);
			} else if (notificationName.equalsIgnoreCase("Clock")) {
				// enabled =
				// notificationWeirdObj.getNotificationNames().get("Timer").getEnabled();
				// enabled =
				// notificationWeirdObj.getNotificationNames().get(Constants.notification_type_Timer).getEnabled();
				// enabled =
				// DbBuilder.getInstant().getCurrentThemeCallsAndClockEnableStatus(notificationName);
				enabled = CallsAndClockSilentStatusDB.getInstance().getCurrentThemeCallsAndClockEnableStatus(notificationName);
			} else {
				// enabled =
				// notificationWeirdObj.getNotificationNames().get(notificationName).getEnabled();
				enabled = notificationWeirdObj.getNotificationNames().get(notificationName).getEnabled();
			}
			// String enabled =
			// notificationWeirdObj.getNotificationNames().get(notificationName).getEnabled();
			// fsdf
			if (slideView == null) {
				// ���������ǵ�item
				View itemView = mInflater.inflate(R.layout.notification_list_item, null);

				slideView = new SlideView(NotificationsFragment.this.getActivity());
				// �����item���뵽slideView
				slideView.setContentView(itemView);
				// ��������һЩ��ݻ���
				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(NotificationsFragment.this);
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
			// holder.deleteHolder.setOnClickListener(CallsNotificationsFragment.this);
			holder.delete.setOnClickListener(NotificationsFragment.this);
			holder.silent.setOnClickListener(NotificationsFragment.this);
			String text = "silent";
			slideView.setAlpha(1);
			if (enabled.equals("1")) {
				text = silent_msg_name;

			} else if (enabled.equals("0")) {
				text = un_silent_msg_name;
				slideView.setAlpha(0.5f);
			}
			holder.silent.setText(text);
			return slideView;
		}

	}

	/*
	 * public class MessageItem { public int iconRes; public String title;
	 * public int arrow; public SlideView slideView; }
	 */
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
			delete.setTextColor(Color.WHITE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String notificationName = "";
		// if (position < 0) position = 0;
		// if (position >= notificationList.size()) position =
		// notificationList.size()-1;
		try {
			notificationName = notificationList.get(position);
		} catch (Exception e) {
			Log.e(TAG, "Invalid Position!! onItemClick() position:" + position);
			return;
		}
		/*
		 * if (position == 11) { //fsd System.out.println("yuan");
		 * FxMenuViewFragment fragment = new FxMenuViewFragment();
		 * FragmentTransaction transaction =
		 * getFragmentManager().beginTransaction();
		 * transaction.replace(R.id.container, fragment);
		 * transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		 * transaction.addToBackStack(null); transaction.commit(); } else if
		 * (notificatioinName.equals("CALENDAR")){ //onSlide(view, 0);
		 * 
		 * 
		 * 
		 * FxMenuViewFragment fragment = new FxMenuViewFragment();
		 * FragmentTransaction transaction =
		 * getFragmentManager().beginTransaction();
		 * transaction.replace(R.id.container, fragment);
		 * transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		 * transaction.addToBackStack(null); transaction.commit();
		 * 
		 * 
		 * }else
		 */
		if (notificationName.equalsIgnoreCase("Calls")) {
			CallsNotificationsFragment fragment = new CallsNotificationsFragment();
			replaceFragment(fragment ,"CallsNotificationsFragment");

		} else if (notificationName.equalsIgnoreCase("Clock")) {
			ClockNotificationsFragment fragment = new ClockNotificationsFragment();
			replaceFragment(fragment);

		} else {
			FxMenuViewFragment fxMenuViewFragment = new FxMenuViewFragment();
			Bundle bundle = new Bundle();
			bundle.putString("notification", notificationName);
			fxMenuViewFragment.setArguments(bundle);
			replaceFragment(fxMenuViewFragment);
		}

		/*
		 * else if (position == 6){ AddStyleFragment fragment = new
		 * AddStyleFragment(); FragmentTransaction transaction =
		 * getFragmentManager().beginTransaction();
		 * transaction.replace(R.id.container, fragment);
		 * transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		 * transaction.addToBackStack(null); transaction.commit();
		 * 
		 * }
		 */

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
	}

	@Override
	public void onClick(View v) {
		// ���ﴦ��ɾ��ť�ĵ���¼�������ɾ��Ի�
		if (v.getId() == R.id.delete) {
			int position = mListView.getPositionForView(v);
			if (position != ListView.INVALID_POSITION) {

				String notificationName = notificationList.get(position);
				// DbBuilder.getInstant().removeNotificationOnCurrentTheme(notificationName);
				NotificationCommandMappingDB.getInstance().removeNotificationOnCurrentTheme(notificationName);
				mMessageItems.remove(position);
				notificationList.remove(position);
				mSlideAdapter.notifyDataSetChanged();
				if(notificationName.equalsIgnoreCase("Clock")) {
					GrandpaClockGFragment.initStatus();
					MyCountdownChronometer.getInstance().stop();
					MyCountdownChronometer.getInstance().setCounting(false);
					MyCountdownChronometer.getInstance().setStopClicked(true);
					AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(getActivity(), AlarmReceiver.class);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
					alarmManager.cancel(pendingIntent);
					AlarmReceiver.isOpen = false;
				}
			}
		} else if (v.getId() == R.id.silent) {
			int position = mListView.getPositionForView(v);

			TextView tv1 = (TextView) v.findViewById(R.id.silent);
			System.out.println();

			if (position != ListView.INVALID_POSITION) {

				TextView tv = (TextView) v;
				String text = tv.getText().toString();
				if (text.equals(silent_msg_name)) {
					String notificationName = notificationList.get(position);
					// DbBuilder.getInstant().silentNotificationOnCurrentTheme(notificationName,
					// true);
					CallsAndClockSilentStatusDB.getInstance().silentNotificationOnCurrentTheme(notificationName, true);
					tv1.setText(un_silent_msg_name);
					// mMessageItems.remove(position);
					// mSlideAdapter.notifyDataSetChanged();
					((View) (v.getParent().getParent())).setAlpha(0.5f);

				} else if (text.equals(un_silent_msg_name)) {
					String notificationName = notificationList.get(position);
					// DbBuilder.getInstant().silentNotificationOnCurrentTheme(notificationName,
					// false);
					CallsAndClockSilentStatusDB.getInstance().silentNotificationOnCurrentTheme(notificationName, false);
					tv1.setText(silent_msg_name);
					// mMessageItems.remove(position);
					// mSlideAdapter.notifyDataSetChanged();
					((View) (v.getParent().getParent())).setAlpha(1);
				}

			}
		}
	}
}
