package com.embraceplus.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.store.ExCommandManager;
import com.embraceplus.utils.Optional;

public class NLService extends NotificationListenerService {

	private String TAG = this.getClass().getSimpleName();
	private NLServiceReceiver nlservicereciver;

	@Override
	public void onCreate() {
		super.onCreate();
		nlservicereciver = new NLServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.embraceplus.app.NOTIFICATION_LISTENER_SERVICE");
		registerReceiver(nlservicereciver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(nlservicereciver);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.i(TAG, "**********  onNotificationPosted");
		try {
			Log.i(TAG, "ID : " + sbn.getId() + " Notif : " + sbn.getNotification().tickerText + " Package : " + sbn.getPackageName());
			String pkg = sbn.getPackageName();
			final Optional<EmbraceMsg> msg = new Optional<EmbraceMsg>();
			if (pkg.indexOf("calendar") != -1) {
				pkg = "Calendar";
//				msg.set(DbBuilder.getInstant().getExCommandByNotification(pkg));
				msg.set(ExCommandManager.getInstance().getExCommandByNotification(pkg));
			} else if(pkg.indexOf("mail") != -1 || pkg.indexOf("com.google.android.gm") != -1) {
				pkg = "Email";
//				msg.set(DbBuilder.getInstant().getExCommandByNotification(pkg));
				msg.set(ExCommandManager.getInstance().getExCommandByNotification(pkg));
			}else if (pkg.indexOf("facebook") != -1) {
				// pkg= "Facebook";
				pkg = "Facebook";
//				msg.set(DbBuilder.getInstant().getExCommandByNotification(pkg));
				msg.set(ExCommandManager.getInstance().getExCommandByNotification(pkg));
			} else {
//				msg.set(DbBuilder.getInstant().getExCommandByNotificationPkg(pkg));
				msg.set(ExCommandManager.getInstance().getExCommandByNotificationPkg(pkg));
			}

			if (ServiceManager.getInstant().getBluetoothService() == null) {
				return;
			}
			if (msg.notEmpty()) {
				ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.i(TAG, "********** onNOtificationRemoved");
		try {
			Log.i(TAG, "ID :" + sbn.getId() + " Notif : " + sbn.getNotification().tickerText + " Package : " + sbn.getPackageName());
			Intent i = new Intent("com.embraceplus.app.NOTIFICATION_LISTENER_SERVICE");
			i.putExtra("notification_event", "onNotificationRemoved :" + sbn.getPackageName() + "n");

			sendBroadcast(i);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	class NLServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if ("clearall".equals(intent.getStringExtra("command"))) {
					NLService.this.cancelAllNotifications();
				} else if (intent.getStringExtra("command").equals("list")) {
					Intent i1 = new Intent("com.embraceplus.app.NOTIFICATION_LISTENER_SERVICE");
					i1.putExtra("notification_event", "=====================");
					sendBroadcast(i1);
					int i = 1;
					for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
						Intent i2 = new Intent("com.embraceplus.app.NOTIFICATION_LISTENER_SERVICE");
						i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "n");
						sendBroadcast(i2);
						i++;
					}
					Intent i3 = new Intent("com.embraceplus.app.NOTIFICATION_LISTENER_SERVICE");
					i3.putExtra("notification_event", "===== Notification List ====");
					sendBroadcast(i3);

				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}
