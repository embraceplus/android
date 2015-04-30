package com.embraceplus.ble.utils;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.embraceplus.ble.BluetoothLeService;

public class ServiceManager {
	public static ServiceManager serviceManager;

	private BluetoothLeService bluetoothService;
	private boolean ringOutofRangeMsgSended = false;
	private boolean thisWakeupEmbraceLowPowerMsgSended = false;
	private boolean connectStateChangeByUser = false;
	private boolean connectToBleDeviceAndJump = true;
	private String device;
	private Thread autoConnectionThread;// = new AutoConnectionThread();

	public static ServiceManager getInstant() {
		if (serviceManager == null)
			serviceManager = new ServiceManager();
		return serviceManager;
	}

	public void setBluetoothService(BluetoothLeService bluetoothService) {
		this.bluetoothService = bluetoothService;
	}

	public BluetoothLeService getBluetoothService() {
		return bluetoothService;
	}

	public boolean isRingOutofRangeMsgSended() {
		return ringOutofRangeMsgSended;
	}

	public void setRingOutofRangeMsgSended(boolean ringOutofRangeMsgSended) {
		this.ringOutofRangeMsgSended = ringOutofRangeMsgSended;
	}

	public boolean isThisWakeupEmbraceLowPowerMsgSended() {
		return thisWakeupEmbraceLowPowerMsgSended;
	}

	public void setThisWakeupEmbraceLowPowerMsgSended(boolean thisWakeupEmbraceLowPowerMsgSended) {
		this.thisWakeupEmbraceLowPowerMsgSended = thisWakeupEmbraceLowPowerMsgSended;
	}

	public boolean isConnectStateChangeByUser() {
		return connectStateChangeByUser;
	}

	public void setConnectStateChangeByUser(boolean connectStateChangeByUser) {
		this.connectStateChangeByUser = connectStateChangeByUser;
	}

	public boolean isConnectToBleDeviceAndJump() {
		return connectToBleDeviceAndJump;
	}

	public void setConnectToBleDeviceAndJump(boolean connectToBleDeviceAndJump) {
		this.connectToBleDeviceAndJump = connectToBleDeviceAndJump;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public boolean isEmbraceAvailable() {
		// return ServiceManager.getInstant().getBluetoothService() != null &&
		// ServiceManager.getInstant().getBluetoothService().isEmbraceAvailable();
		return null != bluetoothService && bluetoothService.isEmbraceAvailable();
	}

	/*
		private class AutoConnectionThread extends Thread {
			public void run() {
				while (!ServiceManager.getInstant().isEmbraceAvailable()) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ServiceManager.getInstant().getBluetoothService().getMyHandler().post(new Runnable() {
						@Override
						public void run() {
							if (ServiceManager.getInstant().getBluetoothService() != null && !ServiceManager.getInstant().getBluetoothService().isEmbraceAvailable())
								ServiceManager.getInstant().getBluetoothService().connect(ServiceManager.getInstant().getDevice());

						}
					});

				}
			}
		}
		*/

	private class AutoConnectionThread extends Thread {
		final BluetoothLeService bluetoothService = serviceManager.getBluetoothService();
		public void run() {
			while (!serviceManager.isEmbraceAvailable()) {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				bluetoothService.getMyHandler().post(new Runnable() {
					@Override
					public void run() {
						if (!serviceManager.isEmbraceAvailable()) {
							if(serviceManager.getDevice() == null){
							    SharedPreferences deviceadd = bluetoothService.getApplicationContext().getSharedPreferences("deviceAddress", 0);
							    String preAddress = deviceadd.getString("address", "");
							    serviceManager.setDevice(preAddress);
							}
							bluetoothService.connect(serviceManager.getDevice());
						}
					}
				});
			}
		}
	}

	public void startAutoConnectThread() {
		if (null == autoConnectionThread || !autoConnectionThread.isAlive()) {
			autoConnectionThread = new AutoConnectionThread();
		}
		autoConnectionThread.start();

	}
	
	public void stopAutoConnectThread() {
		if (null != autoConnectionThread) {
			autoConnectionThread.interrupt();
		}
	}
}
