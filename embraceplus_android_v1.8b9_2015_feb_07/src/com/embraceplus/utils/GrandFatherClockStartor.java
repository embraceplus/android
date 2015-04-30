package com.embraceplus.utils;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.embraceplus.ble.utils.Constants;
import com.embraceplus.database.GrandpaMsgDB;
import com.embraceplus.model.GrandFatherStatus;

public class GrandFatherClockStartor {
private static GrandFatherClockStartor startor = new GrandFatherClockStartor();
AlarmManager alarmManager;
private boolean hourlyClockStarted;
private boolean halfHourClockStarted;

private PendingIntent hourlyPendingIntent;
private PendingIntent halfHourPendingIntent;
private GrandFatherClockStartor()
{
	alarmManager = (AlarmManager)ContextUtil.getInstance().getSystemService(Context.ALARM_SERVICE);
	  Intent hourlyIntent = new Intent(ContextUtil.getInstance(), GrandFatherHourlyReceiver.class);
	  hourlyPendingIntent = PendingIntent.getBroadcast(ContextUtil.getInstance(), 0, hourlyIntent, 0);
	  
	  Intent halfHourIntent = new Intent(ContextUtil.getInstance(), GrandFatherHalfHourReceiver.class);
	  halfHourPendingIntent = PendingIntent.getBroadcast(ContextUtil.getInstance(), 0, halfHourIntent, 0);
}

public static GrandFatherClockStartor getInstant()
{
	return startor;
}
	
public void startHourlyGrandFatherClock()
{
	System.out.println("startHourlyGrandFatherClock status is true, ready to check if the  hourly grandfather clock was started");
	if (!hourlyClockStarted)
	{
		
		System.out.println("hourly grandfather clock was not started, ready to start..");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 1);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		
		
		long firstTime = c.getTime().getTime() - System.currentTimeMillis();
		System.out.println("next  hourly time is " + c.getTime() + ", will started in " + firstTime/1000/60 +"minutes");
	    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,firstTime , 60000*60, hourlyPendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTime().getTime() , 60000*60, hourlyPendingIntent);
	    hourlyClockStarted = true;
	    
	    
	    //below code for testing
	/*	System.out.println("hourly grandfather clock was not started, ready to start..");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 0);
		c.add(Calendar.MINUTE, 1);
		c.set(Calendar.SECOND, 0);
		
		
		long firstTime = c.getTime().getTime() - System.currentTimeMillis();
		System.out.println("next  hourly time is " + c.getTime() + ", will started in " + firstTime/1000 +" seconds");
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,firstTime , 60000, hourlyPendingIntent);
	    hourlyClockStarted = true;*/
	}
}


public void startHalfHOurGrandFatherClock()
{
	System.out.println("startHalfHOurGrandFatherClock status is true, ready to check if the half hour grandfather clock was started");
	if (!halfHourClockStarted)
	{
		System.out.println("half hour grandfather clock was not started, ready to start..");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.SECOND, 0);
		long ss = c.getTime().getTime() - System.currentTimeMillis();
		System.out.println("next half hour time is " + c.getTime()  + ", will started in " + ss/1000/60 +"minutes");
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTime().getTime() , 60000*60, halfHourPendingIntent);
	halfHourClockStarted = true;
	}else
	{
		System.out.println("half hour grandfather clock was  started, no need to start anymore");
	}
/*
	System.out.println("startHalfHOurGrandFatherClock status is true, ready to check if the half hour grandfather clock was started");
	if (!halfHourClockStarted)
	{
		System.out.println("half hour grandfather clock was not started, ready to start..");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		//c.add(Calendar.MINUTE, 1);
		
		c.set(Calendar.SECOND, 0);
		long ss = c.getTime().getTime() - System.currentTimeMillis();
		System.out.println("next half hour time is " + c.getTime()  + ", will started in " + ss/1000/60 +"minutes");
	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,ss , 60000, halfHourPendingIntent);
	halfHourClockStarted = true;
	}else
	{
		System.out.println("half hour grandfather clock was  started, no need to start anymore");
	}
	*/
}

public void stopHourlyGrandFatherClock()
{
	if (hourlyClockStarted)
	{
	alarmManager.cancel(hourlyPendingIntent);
	hourlyClockStarted = false;
	System.out.println("hourly clock canceled");
	}
}

public void stopHalfHOurGrandFatherClock()
{
	if (halfHourClockStarted)
	{
	alarmManager.cancel(halfHourPendingIntent);
	halfHourClockStarted = false;
	System.out.println("half hour clock canceled");
	}
}

public void startGrandFatherClockWhenAppStarted()
{
	System.out.println("Ready to start the GrandFatherClock ");
//	final Optional<GrandFatherStatus> halfHourStatus = DbBuilder.getInstant().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HALF_HOUR);
//	final Optional<GrandFatherStatus> hourlyStatus = DbBuilder.getInstant().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HALF_HOUR);

	final Optional<GrandFatherStatus> halfHourStatus = GrandpaMsgDB.getInstance().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HALF_HOUR);
	final Optional<GrandFatherStatus> hourlyStatus = GrandpaMsgDB.getInstance().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HALF_HOUR);
	
	if (halfHourStatus.notEmpty() && hourlyStatus.notEmpty())
	{
		System.out.println("halfHourStatus is " + halfHourStatus.get().isStatus());
		if (halfHourStatus.get().isStatus()) startHalfHOurGrandFatherClock();

	}
	
	if (halfHourStatus.notEmpty() && hourlyStatus.notEmpty())
	{
	System.out.println("hourlyStatus is " + hourlyStatus.get().isStatus());
	if (hourlyStatus.get().isStatus()) startHourlyGrandFatherClock();

	}
}

}
