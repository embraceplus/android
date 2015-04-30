package com.embraceplus.utils;

import com.embraceplus.fragment.AlarmFragment;
import com.embraceplus.fragment.GrandpaClockGFragment;
import com.embraceplus.fragment.TimerFragment;

public class ClockFragmentUtil {
	private static ClockFragmentUtil util = new ClockFragmentUtil();
	private AlarmFragment alarmFragmentl;
	private TimerFragment timerFragment;
	private GrandpaClockGFragment grandpaClockGFragment;
	private ClockFragmentUtil()
	{
		
	}
	
	public static ClockFragmentUtil getInstant()
	{
		return util;
	}

	public AlarmFragment getAlarmFragmentl() {
		return alarmFragmentl;
	}

	public void setAlarmFragmentl(AlarmFragment alarmFragmentl) {
		this.alarmFragmentl = alarmFragmentl;
	}

	public TimerFragment getTimerFragment() {
		return timerFragment;
	}

	public void setTimerFragment(TimerFragment timerFragment) {
		this.timerFragment = timerFragment;
	}

	public GrandpaClockGFragment getGrandpaClockGFragment() {
		return grandpaClockGFragment;
	}

	public void setGrandpaClockGFragment(GrandpaClockGFragment grandpaClockGFragment) {
		this.grandpaClockGFragment = grandpaClockGFragment;
	}
	
	
}
