package com.embraceplus.app.fragment.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.embraceplus.app.R;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.UtilitiesDB;
import com.embraceplus.utils.EmbraceBatteryUtil;

public class SettingAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private Activity activity;
	private int[] type;
	private ArrayList<HashMap<String, Object>> list;
	private TextView textView_embrace_battary;
	MySwitch switchTest;

	public SettingAdapter(Activity activity, int[] type) {
		this.activity = activity;
		mInflater = activity.getLayoutInflater();

		this.type = type;
	}

	@Override
	public int getCount() {
		return type.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		switch (type[position]) {
		case 0: {
			convertView = getlistItemView();
			TextView textView = (TextView) convertView.findViewById(R.id.title);
			ImageView image = (ImageView) convertView.findViewById(R.id.icon);
			textView.setTextColor(android.graphics.Color.WHITE);
			convertView.setVisibility(View.VISIBLE);
			switch (position) {
			case 0:
				textView.setText("Themes");
				image.setImageResource(R.drawable.style_icon);
				break;
			case 3:
				textView.setText("New device");
				image.setImageResource(R.drawable.newdevice_icon);
				break;
			case 4:
				textView.setText("About");
				image.setImageResource(R.drawable.about_icon);
				break;
			case 5:
				textView.setText("Help");
				image.setImageResource(R.drawable.help_icon);
				break;
			case 2:

				break;
			default:
				break;
			}
			break;
		}
		case 1: {
			convertView = getSeetingItemView();
			TextView textView1 = (TextView) convertView.findViewById(R.id.title);
			ImageView image1 = (ImageView) convertView.findViewById(R.id.icon);
			// ToggleButton togg =
			// (ToggleButton)convertView.findViewById(R.id.toggleButton1);
			switchTest = (MySwitch) convertView.findViewById(R.id.toggleButton1);

			// switchTest.setSelected(DbBuilder.getInstant().getNotificatioinValidate());
			// switchTest.setActivated(DbBuilder.getInstant().getNotificatioinValidate());
			switchTest.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(MySwitch buttonView, boolean isChecked) {
					if (isChecked) {
						// DbBuilder.getInstant().changeNotificationValidate(true);
						UtilitiesDB.getInstance().changeNotificationValidate(true);
						// switchTest.setActivated(true);
						switchTest.setSelected(true);
					} else {
						// DbBuilder.getInstant().changeNotificationValidate(false);
						UtilitiesDB.getInstance().changeNotificationValidate(false);
						// switchTest.setActivated(false);
						switchTest.setSelected(false);
					}

				}

			});
			// boolean notificationValidate =
			// DbBuilder.getInstant().getNotificatioinValidate();
			boolean notificationValidate = UtilitiesDB.getInstance().getNotificatioinValidate();
			switchTest.setChecked(notificationValidate);
			/*if(notificationValidate)
			{
				switchTest.setActivated(true);
			}else
			{
				switchTest.setActivated(false);
			}*/

			switch (position) {
			case 1:
				textView1.setText("Notifications");
				textView1.setTextColor(android.graphics.Color.WHITE);
				image1.setImageResource(R.drawable.notification_icon);
				/*togg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if(isChecked){
							//tvSound.setText("�ѿ���");
						}else{
							//tvSound.setText("�ѹر�");
						}
					}
				});*/
				break;
			}
			break;
		}

		case 2: {
			convertView = mInflater.inflate(R.layout.setting_embrace_battery_item, null);
			TextView textView2 = (TextView) convertView.findViewById(R.id.title);
			textView2.setText("Battery Embrace+");
			textView2.setTextColor(Color.WHITE);
			ImageView image2 = (ImageView) convertView.findViewById(R.id.icon);
			image2.setImageResource(R.drawable.battery_low_embrace);
			textView_embrace_battary = (TextView) convertView.findViewById(R.id.embraceBattery);

			Handler embraceHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:
						int value = (Integer) msg.obj;

						String batteryValue = "";
						batteryValue = EmbraceBatteryUtil.getEmbraceBatteryValue(value);
						textView_embrace_battary.setText(batteryValue);
						break;
					}
				}
			};

			if (ServiceManager.getInstant().getBluetoothService() != null) {
				ServiceManager.getInstant().getBluetoothService().setEmbraceHandler(embraceHandler);
			}
			textView_embrace_battary.setTextColor(Color.WHITE);
			// textView_embrace_battary.setText("reading the embrace...");
			textView_embrace_battary.setVisibility(View.VISIBLE);
			// GlobalUtils.getInstant().setEmbraceBattery(textView_embrace_battary);
			if (ServiceManager.getInstant().getBluetoothService() != null) {
				ServiceManager.getInstant().getBluetoothService().readEmbraceBattery();
			}
			// textView2.setText("BATTERY EMBRACE+");
			// ToggleButton togg =
			// (ToggleButton)convertView.findViewById(R.id.toggleButton1);
			// Switch switchTest =
			// (Switch)convertView.findViewById(R.id.toggleButton1);

		}
		}

		return convertView;
	}

	private View getlistItemView() {
		return mInflater.inflate(R.layout.notification_list_item, null);
	}

	private View getSeetingItemView() {
		return mInflater.inflate(R.layout.setting_item, null);
	}
}
