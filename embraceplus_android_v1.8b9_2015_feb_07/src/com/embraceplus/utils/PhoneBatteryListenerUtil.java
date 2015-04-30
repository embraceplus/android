package com.embraceplus.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.store.ExCommandManager;

public class PhoneBatteryListenerUtil {
	private static PhoneBatteryListenerUtil util = new PhoneBatteryListenerUtil();
	private static boolean phoneLowPowerMsgSended = false;
	private boolean registed = false;
	private boolean rssiListened = false;
	private boolean embraceBatteryThreadActived = false;
	private boolean hasError = false;
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent intent) {
			// unregisterReceiver(this);
			int level = intent.getIntExtra("level", 0);// 电量百分比
			System.out.println(level);

			if (level < Constants.minPhoneBattery && ServiceManager.getInstant().getBluetoothService() != null) {
//				if (!GlobalUtils.getInstant().isPhoneLowPowerMsgSended()) {
				if (!phoneLowPowerMsgSended) {
//					final Optional<EmbraceMsg> msg = DbBuilder.getInstant().getExCommandByNotification(Constants.notification_type_BATTERYPHONE);
					final Optional<EmbraceMsg> msg = ExCommandManager.getInstance().
							getExCommandByNotification(Constants.notification_type_BATTERYPHONE);
					
					if (msg.notEmpty()) {
						ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
//						GlobalUtils.getInstant().setPhoneLowPowerMsgSended(true);
						phoneLowPowerMsgSended = true;
					}

				}
			} else if (level >= Constants.minPhoneBattery) {
//				GlobalUtils.getInstant().setPhoneLowPowerMsgSended(false);
				phoneLowPowerMsgSended = false;
			}

			// contentTxt.setText("BAttery : "+String.valueOf(level) + "%");
		}
	};

	private Thread listenRssiThread = new Thread() {
		public void run() {
			while (true) {
				// check if device is connected !
				if (ServiceManager.getInstant().getBluetoothService() == null) {
					continue;
				}

				try {
					ServiceManager.getInstant().getBluetoothService().readRssi();
					// Thread.sleep(1000*60*10); // 10 sec ?
					Thread.sleep(1000 * 5); // 5 sec
//					Log.i("<ME> listenRssiThread:", "" + ServiceManager.getInstant().isConnectStateChangeByUser());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					if (!hasError)
						e.printStackTrace();
					hasError = true;
				}
				// System.out.println("Check the rssi......");
			}

		}
	};

	private Thread listenEmbraceBatteryThread = new Thread() {
		public void run() {
			while (true) {
				if (ServiceManager.getInstant().getBluetoothService() == null) {
					continue;
				}
//				final Optional<EmbraceMsg> msg = DbBuilder.getInstant().getExCommandByNotification(Constants.notification_type_BATTERYEMBRACCE);
				final Optional<EmbraceMsg> msg = ExCommandManager.getInstance().
						getExCommandByNotification(Constants.notification_type_BATTERYEMBRACCE);
				if (msg != null && !embraceBatteryThreadActived) {
					ServiceManager.getInstant().getBluetoothService().readEmbraceBattery();

				}

				try {
					Thread.sleep(1000 * 30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				System.out.println("Check the embrace battery......");
			}

		}
	};

	private PhoneBatteryListenerUtil() {

	}

	public static PhoneBatteryListenerUtil getInstant() {
		return util;
	}

	public void registPhoneBatteryListener() {
		if (!registed) {
			ContextUtil.getInstance().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			registed = true;
		}
	}

	public void startListenRssi() {
		if (!rssiListened) {
			if (!listenRssiThread.isAlive()) {
				listenRssiThread.start();
			}
		}
	}

	public void startListenEmbraceBattery() {
		try {
			listenEmbraceBatteryThread.start();
		} catch (Exception e) {
			// e.
		}
	}

	public boolean isEmbraceBatteryThreadActived() {
		return embraceBatteryThreadActived;
	}

	public void setEmbraceBatteryThreadActived(boolean embraceBatteryThreadActived) {
		this.embraceBatteryThreadActived = embraceBatteryThreadActived;
	}
}
