package com.embraceplus.widget;

import android.content.Context;
import android.widget.Chronometer;

import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.PreDefineCommandDB;
import com.embraceplus.fragment.TimerFragment;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.store.ExCommandManager;
import com.embraceplus.utils.ContextUtil;
import com.embraceplus.utils.Optional;

public class MyCountdownChronometer extends CountdownChronometer {
	private static MyCountdownChronometer chronometer;

	final Optional<EmbraceMsg> realTimerMsg = new Optional<EmbraceMsg>();
	private TimerFragment timerFragment;	//used for notification of completion
	private boolean stopClicked = false;
	private boolean isCounting = false;
	public String selectedMsgName;
	
	public MyCountdownChronometer(Context context) {
		super(context);
		initOnChronometerTickListener();
		setTextSize(70);
		setFormat("%s");
	}
	
	public static MyCountdownChronometer getInstance(){
		if (null == chronometer) {
			chronometer = new MyCountdownChronometer(ContextUtil.getInstance());
		}
		return chronometer;
	}
	
	private void initOnChronometerTickListener() {
		setOnCompleteListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if (!stopClicked && ServiceManager.getInstant().getBluetoothService() != null) {
					// realTimerMsg.set(DbBuilder.getInstant()
					// .getExCommandByNotification(Constants.notification_type_Timer));
					realTimerMsg.set(ExCommandManager.getInstance().getExCommandByNotification(Constants.notification_type_Timer));
					if (realTimerMsg.notEmpty()) {
						Thread msgSendThread = new Thread() {
							public void run() {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
//								if (realTimerMsg.notEmpty())
//									ServiceManager.getInstant().getBluetoothService().writeEffectCommand(realTimerMsg.get().getFXCommand());
								System.out.println("=================== " + selectedMsgName);
								EmbraceMsg msg = PreDefineCommandDB.getInstance().getMsgObjByMsgName(selectedMsgName);
								if (msg != null) {
									ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.getFXCommand());
								}
							}
						};
						msgSendThread.start();
					}
					// isCounting = false;
					stopClicked = false;
				}

				isCounting = false;
				if (null != timerFragment) {
					timerFragment.onCompleteCountDown();
				}
				
			}
		});
	}

	public boolean isCounting() {
		return isCounting;
	}

	public void setCounting(boolean isCounting) {
		this.isCounting = isCounting;
	}

	public boolean isStopClicked() {
		return stopClicked;
	}

	public void setStopClicked(boolean stopClicked) {
		this.stopClicked = stopClicked;
	}

	public TimerFragment getTimerFragment() {
		return timerFragment;
	}

	public void setTimerFragment(TimerFragment timerFragment) {
		this.timerFragment = timerFragment;
	}

	public String getSelectedMsgName() {
		return selectedMsgName;
	}

	public void setSelectedMsgName(String selectedMsgName) {
		this.selectedMsgName = selectedMsgName;
	}


}
