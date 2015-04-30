/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.embraceplus.ble;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.embraceplus.app.R;
import com.embraceplus.app.SearchActivity;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.GlobalHandlerUtil;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.store.ExCommandManager;
import com.embraceplus.utils.EmbraceBatteryUtil;
import com.embraceplus.utils.Optional;
import com.embraceplus.utils.PhoneBatteryListenerUtil;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
//	private String mBluetoothDeviceAddress;
	private Handler myHandler = new Handler();
	private Handler embraceHandler;

	private boolean embraceConnected = false;
	public static int currentRssi = 0;

	// public final static String ACTION_GATT_SERVICES_RSSIR =
	// "com.example.bluetooth.le.RSSI_R";

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

			currentRssi = rssi;
			// BluetoothLeService.this.sendBroadcast(new
			// Intent(ACTION_GATT_SERVICES_RSSIR));
			if (rssi < Constants.BLEMinValue) {
//				final Optional<EmbraceMsg> msg = DbBuilder.getInstant()
//						.getExCommandByNotification(
//								Constants.notification_type_OUTOFSERVICE);

				final Optional<EmbraceMsg> msg = ExCommandManager.getInstance()
						.getExCommandByNotification(Constants.notification_type_OUTOFSERVICE);
				if (msg.notEmpty()) {
					if (!ServiceManager.getInstant().isRingOutofRangeMsgSended()) {
						if (ServiceManager.getInstant().getBluetoothService() != null) {
							ServiceManager
									.getInstant()
									.getBluetoothService()
									.writeEffectCommand(
											msg.get().getFXCommand());
						}
						ServiceManager.getInstant().setRingOutofRangeMsgSended(
								true);
					}
				}
			} else if (rssi >= Constants.BLEMinValue) {
				ServiceManager.getInstant().setRingOutofRangeMsgSended(false);
			}
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {

			if (newState == 2 && 0 == status) {
				// We can't rely on this event, so we'll move most of our status
				// switching code to the setIsConnected function

				if (gatt != null)
					gatt.discoverServices();

				ServiceManager.getInstant().setConnectStateChangeByUser(false);
				ServiceManager.getInstant().setThisWakeupEmbraceLowPowerMsgSended(
						false);
				BluetoothLeService.this.setIsConnected(true);
				ServiceManager.getInstant().stopAutoConnectThread();

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED || (newState == 2 && status == 133)) {
				//disconnect();
				if(newState == BluetoothProfile.STATE_DISCONNECTED){
				   BluetoothLeService.this.setIsConnected(false);
				}
				if (!ServiceManager.getInstant().isConnectStateChangeByUser()) {
//					AutoConnectUtil.getInstant().startAutoConnectThread();
					ServiceManager.getInstant().startAutoConnectThread();
				}

			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			System.out.println("::onServicesDiscovered()");

			PhoneBatteryListenerUtil.getInstant().registPhoneBatteryListener();
			PhoneBatteryListenerUtil.getInstant().startListenRssi();
			PhoneBatteryListenerUtil.getInstant().startListenEmbraceBattery();

		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {

			byte[] notifValue = characteristic.getValue();

			BluetoothLeService.this.setIsConnected(true);

			// int embraceBattery = (int)notifValue[0];
			int embraceBattery = 0;
			try {
				embraceBattery = readUnsignedShort(notifValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.out.println("onCharacteristicRead() embraceBattery:"
//					+ embraceBattery);
			if (embraceHandler != null) {
				Message msg = Message.obtain(embraceHandler,
						Constants.getEmbraceBattery);
				msg.obj = embraceBattery;
				msg.sendToTarget();
			}

			String embraceBatteryValue = EmbraceBatteryUtil
					.getEmbraceBatteryValue(embraceBattery);
			if (embraceBatteryValue == null || embraceBatteryValue.equals("")) {
				return;
			}
			String embraceBatteryStringValue = embraceBatteryValue.substring(0,
					embraceBatteryValue.indexOf("%"));
			int embraceBatteryIntValue = Integer
					.parseInt(embraceBatteryStringValue);

			if (embraceBatteryIntValue <= Constants.minBattery
					&& !ServiceManager.getInstant()
							.isThisWakeupEmbraceLowPowerMsgSended()) {
//				final Optional<EmbraceMsg> EmbraceBatteryMsg = DbBuilder
//						.getInstant().getExCommandByNotification(
//								Constants.notification_type_BATTERYEMBRACCE);
				final Optional<EmbraceMsg> EmbraceBatteryMsg = ExCommandManager.getInstance().
						getExCommandByNotification(Constants.notification_type_BATTERYEMBRACCE);
				if (EmbraceBatteryMsg.notEmpty()) {
					ServiceManager
							.getInstant()
							.getBluetoothService()
							.writeEffectCommand(
									EmbraceBatteryMsg.get().getFXCommand());
					PhoneBatteryListenerUtil.getInstant()
							.setEmbraceBatteryThreadActived(true);
					ServiceManager.getInstant()
							.setThisWakeupEmbraceLowPowerMsgSended(true);
				}
			}
		}

		private int readUnsignedShort(byte[] readBuffer) throws IOException {
			if (readBuffer == null || readBuffer.length < 2)
				return 0;
			byte[] uint64 = new byte[3];
			uint64[2] = 0;
			System.arraycopy(readBuffer, 0, uint64, 0, 2);
			BigInteger intg = new BigInteger(reverse(uint64));
			return intg.intValue();
		}

		private byte[] reverse(byte[] b) {

			byte[] temp = new byte[b.length];
			for (int i = 0; i < b.length; i++) {
				temp[i] = b[b.length - 1 - i];
			}
			return temp;
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
//			System.out.println("::onCharacteristicWrite()");
//			System.out.println(status);

			// Do nothing ???
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {

			// Do nothing ???
		}
	};

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	private void setIsConnected(boolean connected) {
		if (connected) {
			embraceConnected = true;

			notifyStateChange(true);
		} else {
			embraceConnected = false;

			notifyStateChange(false);
		}
	}

	/**
	 * Tells if the device is reliabily connected. True mean connected and ready
	 * to use False means connecting, disconnecting or disconnected.
	 * 
	 * @return
	 */
	public boolean isEmbraceAvailable() {
		return embraceConnected;
	}

	public void notifyStateChange(boolean connected) {
		int connectedMessage = Constants.handler_msg_disconnect_server;
		if (connected)
			connectedMessage = Constants.handler_msg_connected_server;

		if (GlobalHandlerUtil.getInstant().getDemoHandler().notEmpty()) {
			Message msg = Message.obtain(GlobalHandlerUtil.getInstant()
					.getDemoHandler().get(), connectedMessage);
			msg.sendToTarget();
		}

	}

	public boolean connect(final String address) {
		if (!initialize()) {
			return false;
		}
		
		if (mBluetoothAdapter == null || address == null) {
			return false;
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);

		// Previously connected device. Try to reconnect.
		/*if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {

				return true;
			} else {
				return false;
			}
		}*/
		if(mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
		}
		
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		// mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
//		mBluetoothDeviceAddress = address;

		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		BluetoothLeService.this.setIsConnected(false);
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
		mBluetoothGatt.close();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		if (characteristic == null) {
			Log.w(TAG, "Characteristic is null");
			return;
		}

		mBluetoothGatt.readCharacteristic(characteristic);
	}

	public void readEmbraceBattery() {
		// use below code to read battery...
		// ServiceManager.getInstant().getBluetoothService().readEmbraceBattery();

		UUID batteryserviceUUID = UUID
				.fromString(Constants.Battery_service_uuid);
		UUID batteryNotifyUUID = UUID
				.fromString(Constants.Battery_service_characteristics_uuid);
		if (mBluetoothGatt != null
				&& mBluetoothGatt.getService(batteryserviceUUID) != null) {
			BluetoothGattCharacteristic notifyCharacteristic3 = mBluetoothGatt
					.getService(batteryserviceUUID).getCharacteristic(
							batteryNotifyUUID);
			readCharacteristic(notifyCharacteristic3);
		}

	}

	public void writeEffectCommand(byte[] msg) {

		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		if (msg == null) {
			Log.w(TAG, "Message is null");
			return;
		}

		List<BluetoothGattService> services = mBluetoothGatt.getServices();

		UUID serviceUUID = UUID.fromString(Constants.service_uuid);
		UUID writeUUID = UUID.fromString(Constants.write_uuid_effect);
		/*
		 * UUID serviceUUID =
		 * UUID.fromString("00001802-0000-1000-8000-00805f9b34fb"); UUID
		 * writeUUID = UUID.fromString("00002A06-0000-1000-8000-00805f9b34fb");
		 */

		if (mBluetoothGatt == null
				|| mBluetoothGatt.getService(serviceUUID) == null) {
			/*
			 * System.out.println(mBluetoothGatt);
			 * System.out.println(mBluetoothGatt.getService(serviceUUID));
			 * System.out.println("aaaaaa");
			 */
			return;
		}

		BluetoothGattCharacteristic writeCharacteristic = mBluetoothGatt
				.getService(serviceUUID).getCharacteristic(writeUUID);

		writeCharacteristic.setValue(msg);
		mBluetoothGatt.writeCharacteristic(writeCharacteristic);

	}

	public void readRssi() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.readRemoteRssi();
		}
	}

	public Handler getMyHandler() {
		return myHandler;
	}

	public void setMyHandler(Handler myHandler) {
		this.myHandler = myHandler;
	}

	public Handler getEmbraceHandler() {
		return embraceHandler;
	}

	public void setEmbraceHandler(Handler embraceHandler) {
		this.embraceHandler = embraceHandler;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		ServiceManager.getInstant().setBluetoothService(this);
		SearchActivity.i = 200;
	}
	
	Handler handler = new Handler();

	@Override
	public void onDestroy() {
		super.onDestroy();
		 Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		 gattServiceIntent.putExtra("needConnect", embraceConnected);
		 this.startService(gattServiceIntent);
		 if(embraceConnected){
			 handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					SharedPreferences deviceadd = BluetoothLeService.this.getSharedPreferences("deviceAddress", 0);
					 String previousAddress = deviceadd.getString("address", "");
					 if (ServiceManager.getInstant().getBluetoothService() != null && previousAddress != null){
						ServiceManager.getInstant().getBluetoothService().connect(previousAddress);
					 }
					
				}
			}, 3000);
		 }
	}

	/* @Override
	 public int onStartCommand(Intent intent, int flags, int startId)
	 {
	  flags = START_STICKY;
	  return super.onStartCommand(intent, flags, startId);
	  // return START_REDELIVER_INTENT;
	 }*/
	//@Override
    //public int onStartCommand(Intent intent, int flags, int startId) {
      //  return START_STICKY;
    //}
	
}
