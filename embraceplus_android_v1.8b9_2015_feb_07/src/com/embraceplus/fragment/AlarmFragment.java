package com.embraceplus.fragment;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.MySwitch;
import com.embraceplus.app.fragment.utils.SwitchOnCheckedChangeListener;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.utils.AlarmReceiver;

public class AlarmFragment extends BaseFragmentWithFxGridView {
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	
	PendingIntent pendingIntent;
	AlarmManager alarmManager;
	DatePicker datePicker;
	TimePicker timePicker;
	private MySwitch toggle_Button;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setNotificationName(Constants.notification_type_ALARM);
		setLineNum(1);
		return inflater.inflate(R.layout.fragment_alarm, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initTitleBar("Alarm");

		datePicker = (DatePicker) this.getActivity().findViewById(R.id.datePicker);
		timePicker = (TimePicker) this.getActivity().findViewById(R.id.timePicker);

		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR);
		minute = calendar.get(Calendar.MINUTE);

		datePicker.init(year, month, day, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month, int day) {
				AlarmFragment.this.year = year;
				AlarmFragment.this.month = month;
				AlarmFragment.this.day = day;
				setAlarm();
				// 显示时间
				// showDate(year, month, day, hour, minute);
			}
		});

		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hour, int minute) {
				AlarmFragment.this.hour = hour;
				AlarmFragment.this.minute = minute;
				setAlarm();
				// 显示时间
				// showDate(year, month, day, hour, minute);
			}
		});
		
		toggle_Button = (MySwitch) view.findViewById(R.id.toggle_Button);
		alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		AlarmReceiver.fragment = this;
		if (AlarmReceiver.isOpen)
			toggle_Button.setChecked(true);
		toggle_Button.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MySwitch buttonView, boolean isChecked) {
				if (!isChecked) {
					cancelAlarm();
				} else {
					AlarmReceiver.isOpen = true;
					setAlarm();
				}
			}
		});
	}

	public void setAlarm() {
		Calendar current = Calendar.getInstance();

		Calendar cal = Calendar.getInstance();
		int year = datePicker.getYear();
		int month = datePicker.getMonth();
		int day = datePicker.getDayOfMonth();
		int hour = timePicker.getCurrentHour();
		int min = timePicker.getCurrentMinute();
		cal.set(year, month, day, hour, min, 00);

		if (cal.compareTo(current) <= 0) {
		} else {
			setAlarm(cal);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR);
		minute = calendar.get(Calendar.MINUTE);

		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
		datePicker.init(year, month, day, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month, int day) {
				AlarmFragment.this.year = year;
				AlarmFragment.this.month = month;
				AlarmFragment.this.day = day;
				setAlarm();
				// 显示时间
				// showDate(year, month, day, hour, minute);
			}
		});
	}

	protected void cancelAlarm() {
		// TODO Auto-generated method stub
		alarmManager.cancel(pendingIntent);
		AlarmReceiver.isOpen = false;
	}

	private void setAlarm(Calendar targetCal) {

		Intent intent = new Intent(getActivity(), AlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

		alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

	}

	public void alarmReceived() {
		toggle_Button.setChecked(false);
	}
}
