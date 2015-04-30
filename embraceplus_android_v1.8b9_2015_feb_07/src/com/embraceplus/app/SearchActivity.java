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

package com.embraceplus.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import com.embraceplus.ble.BluetoothLeService;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.GlobalHandlerUtil;
import com.embraceplus.ble.utils.ServiceManager;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class SearchActivity extends Activity {
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private Handler searchHandler;
	private BluetoothLeService mBluetoothLeService;
	private List<String> strongDeviceSearchList = new ArrayList<String>();
	boolean findDeviceAndForward = false;
	boolean hasPreDevice = false;
	boolean requestBleOpen = true;
	private Intent gattServiceIntent;

	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;
	public static int i = 0;
	ProgressDialog m_pDialog;
	String previousAddress = "";
	public boolean getNew = false;
	Map<Integer, BluetoothDevice> deviceMap = new HashMap<Integer, BluetoothDevice>();
	boolean isfinish = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PackageManager pm = this.getPackageManager();
		boolean hasBLE = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

		SharedPreferences deviceadd = getSharedPreferences("deviceAddress", 0);
		previousAddress = deviceadd.getString("address", "");
		if (this.getIntent().getIntExtra("scannNew", 0) == 1) {
			getNew = true;
			if (ServiceManager.getInstant().getBluetoothService() != null) {
				ServiceManager.getInstant().getBluetoothService().disconnect();
			}
		}
		
		if (ServiceManager.getInstant().isEmbraceAvailable()) {
			SearchActivity.this.launchActivity(MainActivity.class, false, "yes");
			finish();
		}

		m_pDialog = new ProgressDialog(SearchActivity.this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if ("".equals(previousAddress) || getNew) {
			m_pDialog.setTitle("Scanning Embrace+");
		} else {
			m_pDialog.setTitle("Connecting to the Embrace+");
		}
		m_pDialog.setMessage("");
		m_pDialog.setIndeterminate(false);

		m_pDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				SearchActivity.this.launchActivity(MainActivity.class, false, "yes");
				finish();
			}
		});

		mHandler = new Handler();
		searchHandler = new Handler() {

			public void handleMessage(Message msg) {
				if (msg.what == Constants.handler_msg_connected_server && !isfinish) {
					launchActivity(MainActivity.class, true, "yes");
					GlobalHandlerUtil.getInstant().setDemoHandler(null);
				}
			}
		};

		GlobalHandlerUtil.getInstant().setDemoHandler(searchHandler);

		// Use this check to determine whether BLE is supported on the device.
		// Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			finish();
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			finish();
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		gattServiceIntent = new Intent(this, BluetoothLeService.class);

		ServiceManager.getInstant().setConnectToBleDeviceAndJump(true);

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled() && requestBleOpen) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		} else {
			m_pDialog.show();
			this.startService(gattServiceIntent);
			if ("".equals(previousAddress) || getNew) {
				searchNewDevice();
			} else {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						isfinish = true;
						if (ServiceManager.getInstant().getBluetoothService() != null)
							ServiceManager.getInstant().getBluetoothService().connect(previousAddress);

					}
				}, 2000);

				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						m_pDialog.dismiss();

						if (!getNew){
							isfinish = true;
							SearchActivity.this.launchActivity(MainActivity.class, false, "no");
							finish();
						}else{
							isfinish = true;
							finish();
						}
					}
				}, 4000);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			SearchActivity.this.launchActivity(MainActivity.class, false, "no");
			requestBleOpen = false;
			return;
		} else if (requestCode == REQUEST_ENABLE_BT) {
			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
			m_pDialog.show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
	}

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				finish();
			}
			ServiceManager.getInstant().setBluetoothService(mBluetoothLeService);

			// First things first, let's ensure that we are not connected at the
			// moment
			mBluetoothLeService.disconnect();

			String preAddress = getSharedPreferences("deviceAddress", 0).getString("address", "");
			// if does not has the preAddress, then list the device list
			// directly
			if (preAddress == null || preAddress.equals("")) {
				hasPreDevice = false;
			} else {
				hasPreDevice = true;
				// if has the preAddress, and preaddress is as same as searched
				// device,
				// then connect to the device directly
				strongDeviceSearchList.add(preAddress);

				ServiceManager.getInstant().setDevice(preAddress);
				scanLeDevice(false);
				findDeviceAndForward = true;
				mBluetoothLeService.connect(preAddress);
			}

			scanLeDevice(true);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	private void searchNewDevice() {

		if (previousAddress == null || previousAddress.equals("") || getNew) {
			hasPreDevice = false;
			scanLeDevice(true);
		} else {
			hasPreDevice = true;
			strongDeviceSearchList.add(previousAddress);

			ServiceManager.getInstant().setDevice(previousAddress);
			scanLeDevice(false);
			findDeviceAndForward = true;
			if(mBluetoothLeService == null){
				mBluetoothLeService = ServiceManager.getInstant().getBluetoothService();
			}
			if(mBluetoothLeService != null){
			    mBluetoothLeService.connect(previousAddress);
			}
		}

		
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					

					 Integer rssi = -160;
	                 Set<Integer> keySet = deviceMap.keySet();
				     Iterator<Integer> it = keySet.iterator();
					 while(it.hasNext()){
						Integer tmp = it.next();
						if(rssi < tmp.intValue()){
							rssi = tmp;
						}
					}
					
					if(deviceMap.get(rssi) != null){
				       connectDevice(deviceMap.get(rssi));
					}else{
						isfinish = true;
						SearchActivity.this.launchActivity(MainActivity.class, false, "yes");
					}
				}
			}, SCAN_PERIOD);

			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (device == null) {
				return;
			}
			
			if (device.getName().equals(Constants.DeviceName)) {
				deviceMap.put(rssi, device);
			}
			
		}
		
		
	};
	
	private void connectDevice(BluetoothDevice device){
		SharedPreferences deviceadd = getSharedPreferences("deviceAddress", 0);
		//String preAddress = deviceadd.getString("address", "");
		
		String deviceAddress = device.getAddress();
		ServiceManager.getInstant().setDevice(device.getAddress());
		scanLeDevice(false);
		findDeviceAndForward = true;
		if(mBluetoothLeService == null){
			mBluetoothLeService = ServiceManager.getInstant().getBluetoothService();
		}
		if(mBluetoothLeService != null){
		    mBluetoothLeService.connect(deviceAddress);
		}
		
		Editor editor = deviceadd.edit();
		editor.putString("address", deviceAddress);
		editor.commit();
	}

	/**
	 * Launch a new Activity.
	 * 
	 * @param activityClass
	 *            the activity's class
	 */
	protected void launchActivity(Class<? extends Activity> activityClass, boolean isConnected, String needSearch) {
		// /*
		Intent intent = new Intent(this, activityClass);
		intent.putExtra("connected", isConnected);
		intent.putExtra("needsearch", needSearch);
		startActivity(intent);
		// */
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}