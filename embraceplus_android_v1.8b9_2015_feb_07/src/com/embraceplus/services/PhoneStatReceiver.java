package com.embraceplus.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.EmbraceMsgWithEnabled;
import com.embraceplus.store.ExCommandManager;
import com.embraceplus.utils.Optional;

public class PhoneStatReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneStatReceiver";

	// private static MyPhoneStateListener phoneListener = new
	// MyPhoneStateListener();

	private static boolean incomingFlag = false;

	private static String incoming_number = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		// 如果是拨打电话
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			incomingFlag = false;
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Log.i(TAG, "call OUT:" + phoneNumber);
		} else {
			// 如果是来电
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

			switch (tm.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:
				incomingFlag = true;// 标识当前是来电
				incoming_number = intent.getStringExtra("incoming_number");
				// 获取来电用户名。。。
				Log.i(TAG, "RINGING :" + incoming_number);
				if (ServiceManager.getInstant().getBluetoothService() == null) {
					return;
				}
				boolean isUnknow = PhoneNumChecker.getInstant().isUnknownPhone(String.valueOf(incoming_number), context);

				if (isUnknow) {
//					final Optional<EmbraceMsg> msg = DbBuilder.getInstant().getExCommandByNotification(Constants.notification_type_UnknownCall);
					final Optional<EmbraceMsg> msg = ExCommandManager.getInstance().
							getExCommandByNotification(Constants.notification_type_UnknownCall);
					if (msg.notEmpty()) {
						ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
					}
				} else {
					// 根据来电用户名查msg。。。
					String phoneNumber = String.valueOf(incoming_number);
					String userName = PhoneNumChecker.getInstant().getUserName(phoneNumber, context);
					String userNameIndex = "Call_" + userName;
					final Optional<EmbraceMsg> msg = new Optional<EmbraceMsg>();

					// Check if the use's call have already defined in the
					// notification mapping table.
//					final Optional<EmbraceMsgWithEnabled> embraceMsgWithEnabled = DbBuilder.getInstant().getExCommandByIncomingUserCall(userNameIndex);
					final Optional<EmbraceMsgWithEnabled> embraceMsgWithEnabled = ExCommandManager.getInstance().
							getExCommandByIncomingUserCall(userNameIndex);
					if (embraceMsgWithEnabled.notEmpty()) {
						msg.set(embraceMsgWithEnabled.get().getMsg());
						String enable = embraceMsgWithEnabled.get().getEnabled();
						if (enable.equals("0")) {
							return;
						}
					}
					// 如果为某个人已设置了特殊的来电效果，并且，该来电效果已经被silent了，则
					// DbBuilder.getInstant().getExCommandByNotification(userNameIndex)是无法查询到该用户的
					// msg的，在这种情况下，不需要位改用户显示imcoming call的效果。
					/*  if (msg == null && userName!=null)
					  {
					   msg =  DbBuilder.getInstant().getExCommandByNotification(Constants.notification_type_IncomingCall);
					  }*/

					// if(msg == null && userName==null)
					if (msg.isEmpty()) {
//						msg.set(DbBuilder.getInstant().getExCommandByNotification(Constants.notification_type_IncomingCall));
						msg.set(ExCommandManager.getInstance().
								getExCommandByNotification(Constants.notification_type_IncomingCall));
					}
					if (msg.notEmpty()) {
						ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
					}
				}
				break;
				
			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (incomingFlag) {
					Log.i(TAG, "incoming ACCEPT :" + incoming_number);
				}
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				if (incomingFlag) {
					Log.i(TAG, "incoming IDLE");
				}
				break;
			}
		}
	}

}