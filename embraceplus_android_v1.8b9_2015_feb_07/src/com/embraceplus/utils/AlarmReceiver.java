package com.embraceplus.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.PreDefineCommandDB;
import com.embraceplus.fragment.AlarmFragment;
import com.embraceplus.fragment.BaseFragment;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.store.ExCommandManager;

public class AlarmReceiver extends BroadcastReceiver {
	public static boolean isOpen = false;
	public static BaseFragment fragment;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		isOpen = false;
		if (null != fragment) {
			fragment.alarmReceived();
		}
		if (ServiceManager.getInstant().getBluetoothService() != null) {
			// final Optional<EmbraceMsg> msg = DbBuilder.getInstant()
			// .getExCommandByNotification(Constants.notification_type_ALARM);
//			final Optional<EmbraceMsg> msg = ExCommandManager.getInstance().getExCommandByNotification(Constants.notification_type_ALARM);
//			if (msg.notEmpty()) {
//				ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
//			}
			
			AlarmFragment alarmFragment = (AlarmFragment) fragment;
			EmbraceMsg msg = PreDefineCommandDB.getInstance().getMsgObjByMsgName(alarmFragment.getSelectedMsgName());
			if (msg != null) {
				ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.getFXCommand());
			}
		}

	}

}