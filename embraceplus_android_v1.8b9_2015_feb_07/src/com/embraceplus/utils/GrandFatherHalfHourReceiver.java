package com.embraceplus.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.GrandpaMsgDB;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.GrandFatherStatus;

public class GrandFatherHalfHourReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (ServiceManager.getInstant().getBluetoothService() == null) {
			return;
		}
		System.out.println("send the half hour clock .......");
//		final Optional<GrandFatherStatus> status = DbBuilder.getInstant().getGrandFatherStatus("half hour");
		final Optional<GrandFatherStatus> status = GrandpaMsgDB.getInstance().getGrandFatherStatus("half hour");
		if (status.notEmpty()) {
//			final Optional<EmbraceMsg> msg = DbBuilder.getInstant().getGrandFatherMsg();
			final Optional<EmbraceMsg> msg = GrandpaMsgDB.getInstance().getGrandFatherMsg();

			if (msg.notEmpty()) {
				msg.get().setMotoswitch(false);
				msg.get().setEffect(true);
				msg.get().setLoop((byte) 1);
				ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
			}
		}

	}

}