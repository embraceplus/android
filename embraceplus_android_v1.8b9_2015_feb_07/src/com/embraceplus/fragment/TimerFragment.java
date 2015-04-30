package com.embraceplus.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.MySwitch;
import com.embraceplus.app.fragment.utils.SwitchOnCheckedChangeListener;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.widget.MyCountdownChronometer;

public class TimerFragment extends BaseFragmentWithFxGridView {

	NumberPicker hourPicker;
//	View view;
	NumberPicker minPicker;
	NumberPicker secPicker;
	TextView minute_seperator;
	TextView hour_seperator;
	TextView second_seperator;
	MyCountdownChronometer countdownChronometer;
	MySwitch startButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setNotificationName(Constants.notification_type_Timer);
		setLineNum(1);
		return inflater.inflate(R.layout.fragment_timer, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		hourPicker = (NumberPicker) this.getActivity().findViewById(R.id.show_num_picker_hour);
		minPicker = (NumberPicker) this.getActivity().findViewById(R.id.show_num_picker_minute);
		secPicker = (NumberPicker) this.getActivity().findViewById(R.id.show_num_picker_second);
		hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		minPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		secPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		hourPicker.setMinValue(0);
		hourPicker.setMaxValue(23);
		minPicker.setMinValue(0);
		minPicker.setMaxValue(59);
		secPicker.setMinValue(0);
		secPicker.setMaxValue(59);
		startButton = (MySwitch) this.getActivity().findViewById(R.id.toggle_Button);
		// startButton.setChecked(countdownChronometer)
		startButton.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener() {
			public void onCheckedChanged(MySwitch buttonView, boolean isChecked) {
				countdownChronometer.stop();
				if (!isChecked) {
					showTimePicker();

					// isCounting = false;
					if (countdownChronometer.isCounting()) {
						// stopClicked = true;
						countdownChronometer.setStopClicked(true);
					}/*else
						}*/
					hourPicker.setValue(0);
					minPicker.setValue(0);
					secPicker.setValue(0);
				} else {
					countdownChronometer.setCounting(true);
					countdownChronometer.setStopClicked(false);
					showCountDownChronometer();

				}
			}
		});

		hour_seperator = (TextView) this.getActivity().findViewById(R.id.hour_seperator);
		minute_seperator = (TextView) this.getActivity().findViewById(R.id.minute_seperator);
		second_seperator = (TextView) this.getActivity().findViewById(R.id.second_seperator);

		countdownChronometer = MyCountdownChronometer.getInstance();
		countdownChronometer.setSelectedMsgName(getSelectedMsgName());
		countdownChronometer.setTimerFragment(this);
		FrameLayout frameLayout = (FrameLayout) getView().findViewById(R.id.chronometerContainer);
		frameLayout.addView(countdownChronometer);
		
		if (!countdownChronometer.isCounting()) {
			countdownChronometer.setVisibility(View.INVISIBLE);

		} else {
			getView().findViewById(R.id.timerPickerLayout).setVisibility(View.INVISIBLE);
			
			countdownChronometer.setVisibility(View.VISIBLE);
			startButton.setChecked(true);
		}

		initTitleBar("Timer");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.v("", "onStop");
		FrameLayout frameLayout = (FrameLayout) getView().findViewById(R.id.chronometerContainer);
		frameLayout.removeView(countdownChronometer);
		countdownChronometer.setTimerFragment(null);
	}
	
	public void selectItem(View arg1, int index){
		super.selectItem(arg1, index);
        countdownChronometer.selectedMsgName = this.getSelectedMsgName();
	}

	public void showTimePicker() {
		getView().findViewById(R.id.timerPickerLayout).setVisibility(View.VISIBLE);
		
		countdownChronometer.setBase(0);
		countdownChronometer.setVisibility(View.INVISIBLE);

	}

	Handler handler = new Handler();

	public void showCountDownChronometer() {
		int hour = hourPicker.getValue();
		int min = minPicker.getValue();
		int sec = secPicker.getValue();

		getView().findViewById(R.id.timerPickerLayout).setVisibility(View.INVISIBLE);

		long totalValue = System.currentTimeMillis() + (hour * 60 * 60 + min * 60 + sec) * 1000;

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				countdownChronometer.start();

			}
		}, 500);

		countdownChronometer.setBase(totalValue);
		countdownChronometer.setVisibility(View.VISIBLE);
	}
	
	public void onCompleteCountDown() {
		startButton.performClick();
	}
	
}
