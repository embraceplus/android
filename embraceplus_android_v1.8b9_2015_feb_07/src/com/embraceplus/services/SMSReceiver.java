package com.embraceplus.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.store.ExCommandManager;
import com.embraceplus.utils.Optional;

public class SMSReceiver extends BroadcastReceiver {

	public static final String TAG = "ImiChatSMSReceiver";

	// android.provider.Telephony.Sms.Intents

	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent)

	{

		if (intent.getAction().equals(SMS_RECEIVED_ACTION))

		{

			/*SmsMessage[] messages = getMessagesFromIntent(intent);

			for (SmsMessage message : messages)

			{

				Log.i(TAG, message.getOriginatingAddress() + " : " +

				message.getDisplayOriginatingAddress() + " : " +

				message.getDisplayMessageBody() + " : " +

				message.getTimestampMillis());

			}*/
			if (ServiceManager.getInstant().getBluetoothService() == null) {
				return;
			}

			// final Optional<EmbraceMsg> msg =
			// DbBuilder.getInstant().getExCommandByNotification(Constants.notification_type_TEXT);
			final Optional<EmbraceMsg> msg = ExCommandManager.getInstance().getExCommandByNotification(Constants.notification_type_TEXT);
			if (msg.notEmpty())
				ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
		}
	}

	public final SmsMessage[] getMessagesFromIntent(Intent intent)

	{

		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

		if (messages != null) {
			byte[][] pduObjs = new byte[messages.length][];

			for (int i = 0; i < messages.length; i++)

			{

				pduObjs[i] = (byte[]) messages[i];

			}

			byte[][] pdus = new byte[pduObjs.length][];

			int pduCount = pdus.length;

			SmsMessage[] msgs = new SmsMessage[pduCount];

			for (int i = 0; i < pduCount; i++)

			{

				pdus[i] = pduObjs[i];

				msgs[i] = SmsMessage.createFromPdu(pdus[i]);

			}
			return msgs;
		}

		return null;
	}

}