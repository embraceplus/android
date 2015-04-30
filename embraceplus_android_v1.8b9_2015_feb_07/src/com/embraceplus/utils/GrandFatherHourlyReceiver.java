package com.embraceplus.utils;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.GrandpaMsgDB;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.GrandFatherStatus;

public class GrandFatherHourlyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (ServiceManager.getInstant().getBluetoothService() == null) {
			return;
		}

//		final Optional<GrandFatherStatus> status = DbBuilder.getInstant().getGrandFatherStatus(
//				"hourly");
		final Optional<GrandFatherStatus> status = GrandpaMsgDB.getInstance().getGrandFatherStatus("hourly");
		if (status.notEmpty()) {
			
//			final Optional<EmbraceMsg> msg = DbBuilder.getInstant().getGrandFatherMsg();
			final Optional<EmbraceMsg> msg = GrandpaMsgDB.getInstance().getGrandFatherMsg(); 
			if (msg.notEmpty()&&status.notEmpty()) {
				String count = status.get().getCount();
				boolean vibration = status.get().getboolVibration();
				if (vibration)
				{
					msg.get().setMotoswitch(true);
				}
				/*System.out.println("send the hourly msg....");
				//msg.setEffect(true);
				msg.get().setLoop((byte)1);
				ServiceManager.getInstant().getBluetoothService()
				.writeEffectCommand(msg.get().getFXCommand());
				msg.get().setMotoswitch(false);*/
				
				if (count.equalsIgnoreCase("hour") && msg.notEmpty())
				{
					Calendar now = Calendar.getInstance();
					int nowHour = now.get(Calendar.HOUR);
					int hour = nowHour;
					
					if(hour == 0) hour = 12;
					if(hour > 12) 
						hour = hour - 12;
					
					int loopTime = 0;
					if(hour>0)
					{
						loopTime = hour;
					} 
					
					if(nowHour == 1) {
						loopTime = 1;
					}
					System.out.println("=====  000     ===========" +loopTime);
					
					//if(loopTime <= 0) {
					//	loopTime = 0;
					//}
				
					msg.get().setLoop((byte)loopTime);
					
						ServiceManager.getInstant().getBluetoothService()
						.writeEffectCommand(msg.get().getFXCommand());
				}
				
			}
			
			
			
		}

	}

}