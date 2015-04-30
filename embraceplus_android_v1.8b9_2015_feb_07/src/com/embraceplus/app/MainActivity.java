package com.embraceplus.app;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.embraceplus.ble.utils.ConnectionJumpUtil;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.DataBaseHelper;
import com.embraceplus.database.GrandpaMsgDB;
import com.embraceplus.database.UtilitiesDB;
import com.embraceplus.fragment.FxIconFragment;
import com.embraceplus.fragment.ThemeSelectionFragment;
import com.embraceplus.fragment.NotificationsFragment;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.GrandFatherStatus;
import com.embraceplus.model.MotionPresetVO;
import com.embraceplus.store.ExCommandManager;
import com.embraceplus.utils.OnBackPressedListener;
import com.embraceplus.utils.Optional;
import com.embraceplus.utils.PreDefineUtil;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	// EmulatorView
	protected OnBackPressedListener onBackPressedListener;

	Handler handler = new Handler();

	PhoneStateListener psl = new PhoneStateListener() {
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			// " PhoneStateListener " + incomingNumber);
		}

		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			int strength = signalStrength.getGsmSignalStrength();
			if (ServiceState.STATE_OUT_OF_SERVICE == strength) {
				// if (true) {

				// final Optional<EmbraceMsg> msg = DbBuilder.getInstant()
				// .getExCommandByNotification(
				// Constants.notification_type_OUTOFSERVICE);
				final Optional<EmbraceMsg> msg = ExCommandManager.getInstance().getExCommandByNotification(Constants.notification_type_OUTOFSERVICE);

				if (msg.notEmpty() && ServiceManager.getInstant().getBluetoothService() != null) {
					ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.get().getFXCommand());
				}
			}
			// System.out.println(asu);
			// signalStrengthTxt.setText("SignalStrength : "+asu);
		}

		public void onCallForwardingIndicatorChanged(boolean cfi) {
			super.onCallForwardingIndicatorChanged(cfi);
		}

		public void onCellLocationChanged(CellLocation location) {
			super.onCellLocationChanged(location);
		}

		public void onDataActivity(int direction) {
			super.onDataActivity(direction);
		}

		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);
		}

		public void onMessageWaitingIndicatorChanged(boolean mwi) {
			super.onMessageWaitingIndicatorChanged(mwi);
		}

		public void onServiceStateChanged(ServiceState serviceState) {
			super.onServiceStateChanged(serviceState);
		}
	};

	private DataBaseHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// GrandFatherClockStartor.getInstant().startGrandFatherClockWhenAppStarted();
		PreDefineUtil.getInstant();

		// TelephonyManager tm =
		// (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		// tm.listen(psl,
		// PhoneStateListener.LISTEN_SIGNAL_STRENGTHS|PhoneStateListener.LISTEN_CALL_STATE);

		boolean isConnected = getIntent().getBooleanExtra("connected", false);
		String needSearch = getIntent().getStringExtra("needsearch");

		/*if(needSearch.equals("no")||isConnected)
		{
			
		}else*/
		if (needSearch != null && needSearch.equals("yes") && !ServiceManager.getInstant().isEmbraceAvailable()) {
			new AlertDialog.Builder(this).setTitle("Warning").setMessage("Embrace+ not found").setPositiveButton("OK", null).show();
			// ConnectionStatusUtil.getInstant().setIsconnected(false);
		}
		/*
		Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		startActivity(intent);*/

		getNotificationAccessPermission();

		// PreDefineUtil.getInstant().getPreDefineMsgs();
		mDbHelper = new DataBaseHelper(this);

		initGrandFatherClock();
		setupFragment(isConnected);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// MyToast.show("onStop");
	}

	public List<MotionPresetVO> getMotionPresets() {
		return mDbHelper.getDefaultMotionPresets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void getNotificationAccessPermission() {
		ContentResolver contentResolver = this.getContentResolver();
		String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
		String packageName = this.getPackageName();

		if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
			// in this situation we know that the user has not granted the app
			// the Notification access permission
			// throw new Exception();

			// check to see if the enabledNotificationListeners String contains
			// our package name
			new AlertDialog.Builder(this).setTitle("Notification Access").setMessage("The embrace+ need notification access, click 'YES' to get the notification access").setNegativeButton("NO", null)
					.setPositiveButton("YES", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
							startActivity(intent);

						}

					}).show();

		} else {
			// doSomethingThatRequiresNotificationAccessPermission();

		}
	}

	private void setupFragment(boolean isConnected) {

		if (ConnectionJumpUtil.getInstant().isJumpFromNotificationFragment()) {
			NotificationsFragment detailFragment = new NotificationsFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.container, detailFragment);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			transaction.addToBackStack("NotificationsFragment");
			getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
				public void onBackStackChanged() {
					Log.v("MainActivity", "onBackStackChanged");
				}
			});
			transaction.commit();
//			MyToast.debugShow("setupFragment NotificationsFragment");
		} else if (ConnectionJumpUtil.getInstant().isJumpFromChooseStyleFragment()) {
			ThemeSelectionFragment detailFragment = new ThemeSelectionFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.container, detailFragment);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
				public void onBackStackChanged() {
					Log.v("MainActivity", "onBackStackChanged");

				}
			});
			transaction.commit();
//			MyToast.debugShow("setupFragment ChooseStyleViewFragment");
			// }else if (DbBuilder.getInstant().isSystemInited())
		} else if (UtilitiesDB.getInstance().isSystemInited()) {
			NotificationsFragment detailFragment = new NotificationsFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.container, detailFragment);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			transaction.addToBackStack("NotificationsFragment");
			getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
				public void onBackStackChanged() {
					Log.v("MainActivity", "onBackStackChanged");
				}
			});
			transaction.commit();
//			MyToast.debugShow("setupFragment NotificationsFragment");

		} else {
			// DbBuilder.getInstant().initSystem();
			UtilitiesDB.getInstance().initSystem();
			ThemeSelectionFragment detailFragment = new ThemeSelectionFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.container, detailFragment);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
				public void onBackStackChanged() {
					Log.v("MainActivity", "onBackStackChanged");
				}
			});
			transaction.commit();
//			MyToast.debugShow("setupFragment ChooseStyleViewFragment");
		}

	}

	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
		this.onBackPressedListener = onBackPressedListener;
	}

	@Override
	public void onBackPressed() {
		if (onBackPressedListener != null)
			onBackPressedListener.doBack();
		else
			super.onBackPressed();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View view = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (view instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
			}
		}
		return ret;
	}

	private void initGrandFatherClock() {
		GrandFatherStatus hourlStatus = new GrandFatherStatus();
		hourlStatus.setStatus(false);
		hourlStatus.setCount("Hour");
		hourlStatus.setVibration("0");
		hourlStatus.setType(Constants.GRANDFATHER_TYPE_HOURLY);

		GrandFatherStatus halfHourStatus = new GrandFatherStatus();
		halfHourStatus.setType(Constants.GRANDFATHER_TYPE_HALF_HOUR);
		halfHourStatus.setStatus(false);
		// DbBuilder.getInstant().updateGrandFatherStatus(hourlStatus);
		// DbBuilder.getInstant().updateGrandFatherStatus(halfHourStatus);
		GrandpaMsgDB.getInstance().updateGrandFatherStatus(hourlStatus);
		GrandpaMsgDB.getInstance().updateGrandFatherStatus(halfHourStatus);
	}
	
}
