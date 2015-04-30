package com.embraceplus.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.embraceplus.app.MainActivity;
import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.MySwitch;
import com.embraceplus.app.fragment.utils.SwitchOnCheckedChangeListener;
import com.embraceplus.app.fragment.utils.TimeSwitch;
import com.embraceplus.app.fragment.utils.TimeSwitchOnCheckedChangeListener;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.GrandpaMsgDB;
import com.embraceplus.model.EffectVO;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.GrandFatherStatus;
import com.embraceplus.model.MotionPresetVO;
import com.embraceplus.model.VoTransfer;
import com.embraceplus.utils.EColor2;
import com.embraceplus.utils.GrandFatherClockStartor;
import com.embraceplus.utils.Optional;
import com.embraceplus.widget.EmulatorView;
import com.embraceplus.widget.PageSelectorPagerView;
import com.embraceplus.widget.PageSelectorView;

public class GrandpaClockGFragment extends BaseFragment {
	private PageSelectorView mColorSelector;
	private ArrayList<View> mColorButtons = new ArrayList<View>();
	final Optional<GrandFatherStatus> hourlStatus = new Optional<GrandFatherStatus>();
	final Optional<GrandFatherStatus> halfHourStatus = new Optional<GrandFatherStatus>();
	final Optional<EmbraceMsg> grandFatherEmbraceMsg = new Optional<EmbraceMsg>();

	MySwitch hourSwitch;
	TimeSwitch countSwitch;
	MySwitch VibrationSwitch;
	MySwitch Half_Hour_Switch;
	private View view;

	private EffectVO mCurrentEffect = new EffectVO();

	private EmulatorView mEmulator;

	public Integer[] arrayOfInt = new Integer[] { R.drawable.chat, R.drawable.chat, R.drawable.chat, R.drawable.chat, R.drawable.chat, R.drawable.chat, R.drawable.chat, R.drawable.chat,
			R.drawable.chat, R.drawable.chat, R.drawable.chat, R.drawable.chat };

	private TextView preview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.
		view = inflater.inflate(R.layout.fragment_grandpa_clock, container, false);
		mEmulator = (EmulatorView) view.findViewById(R.id.emulator);
		mColorSelector = (PageSelectorView) view.findViewById(R.id.colors);
		setupColorButtons(mColorSelector);
		attachColorData();
		mColorSelector.setPager((PageSelectorPagerView) view.findViewById(R.id.colors_pager));
		preview = (TextView) view.findViewById(R.id.preview);
		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCurrentEffect.editable = true;
				EmbraceMsg msg = VoTransfer.transferFromEffectVOToEmbraceMsg(mCurrentEffect);
				msg.setLoop((byte) 1);
				msg.setPause((byte) 0);
				// msg.setFlag((byte)6);
				msg.setFadeIn((byte) -56);
				msg.setHold((byte) 0);
				msg.setFadeOut((byte) 0);
				msg.setMotoswitch(VibrationSwitch.isChecked());
				msg.setPause((byte) 0);

				ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.getFXCommand());
			}

		});

		return view;
	}

//	public MainActivity getMainActivity() {
//		return (MainActivity) getActivity();
//	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
//		MotionPresetVO m = getMainActivity().getMotionPresets().get(0);

		// grandFatherEmbraceMsg.set(DbBuilder.getInstant().getGrandFatherMsg());
		grandFatherEmbraceMsg.set(GrandpaMsgDB.getInstance().getGrandFatherMsg());
		if (grandFatherEmbraceMsg.isEmpty()) {
			grandFatherEmbraceMsg.set(new EmbraceMsg());

			mCurrentEffect.colorL1 = mCurrentEffect.colorR1 = EColor2.WHITE;

		} else {
			mCurrentEffect = VoTransfer.transferFromEmbraceMsgToEffectVO(grandFatherEmbraceMsg.get());
		}

		/*mCurrentEffect.fadeInTime = m.duration;
		mCurrentEffect.fadeOutTime = m.duration;
		mCurrentEffect.pauseTime = m.pause;*/
		/*mCurrentEffect.fadeInTime = -56;
		mCurrentEffect.fadeOutTime = 0;
		mCurrentEffect.pauseTime = 0;
		mCurrentEffect.blackoutOnPause=false;
		mCurrentEffect.holdTime=0;*/

		mCurrentEffect.fadeInTime = 1;
		mCurrentEffect.fadeOutTime = 6;
		mCurrentEffect.pauseTime = 1000;
		mCurrentEffect.blackoutOnPause = false;
		mCurrentEffect.holdTime = 1000;

		mCurrentEffect.editable = true;

		mCurrentEffect.colorL2 = mCurrentEffect.colorR2 = Color.TRANSPARENT;

		mEmulator.play(mCurrentEffect);

		// halfHourStatus.set(DbBuilder.getInstant().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HALF_HOUR));
		// hourlStatus.set(DbBuilder.getInstant().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HOURLY));

		halfHourStatus.set(GrandpaMsgDB.getInstance().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HALF_HOUR));
		hourlStatus.set(GrandpaMsgDB.getInstance().getGrandFatherStatus(Constants.GRANDFATHER_TYPE_HOURLY));

		if (halfHourStatus.isEmpty()) {
			halfHourStatus.set(new GrandFatherStatus());
			if (halfHourStatus.notEmpty())
				halfHourStatus.get().setStatus(false);

		}
		if (halfHourStatus.notEmpty())
			halfHourStatus.get().setType(Constants.GRANDFATHER_TYPE_HALF_HOUR);

		if (hourlStatus.isEmpty()) {
			hourlStatus.set(new GrandFatherStatus());
			hourlStatus.get().setStatus(false);
			hourlStatus.get().setCount("Hour");
			hourlStatus.get().setVibration("0");

		}
		if (hourlStatus.notEmpty())
			hourlStatus.get().setType(Constants.GRANDFATHER_TYPE_HOURLY);

		hourSwitch = (MySwitch) this.getActivity().findViewById(R.id.hourSwitch);
		countSwitch = (TimeSwitch) this.getActivity().findViewById(R.id.countSwitch);
		VibrationSwitch = (MySwitch) this.getActivity().findViewById(R.id.VibrationSwitch);
		Half_Hour_Switch = (MySwitch) this.getActivity().findViewById(R.id.Half_Hour_Switch);

		if (hourlStatus.notEmpty() && halfHourStatus.notEmpty()) {
			hourSwitch.setChecked(hourlStatus.get().isStatus());
			countSwitch.setChecked(hourlStatus.get().getCountBoolean());
			VibrationSwitch.setChecked(hourlStatus.get().getboolVibration());
			Half_Hour_Switch.setChecked(halfHourStatus.get().isStatus());
		}

		/*
				hourSwitch.setChecked(false);
				countSwitch.setChecked(false);
				VibrationSwitch.setChecked(false);
				Half_Hour_Switch.setChecked(false);*/

		hourSwitch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener() {
			public void onCheckedChanged(MySwitch buttonView, boolean isChecked) {
				// hourSwitch.setChecked(isChecked);
				saveGrandFatherEffectMsg();
				if (isChecked) {
					if (hourlStatus.notEmpty())
						hourlStatus.get().setStatus(true);
					saveHourlyGrandFatherStatus();
					GrandFatherClockStartor.getInstant().startHourlyGrandFatherClock();

					// when the hourswitch was checked, the halfhourswitch was
					// also checked, then start the halfhour clock tasks also.
					if (hourSwitch.isChecked() && Half_Hour_Switch.isChecked()) {
						GrandFatherClockStartor.getInstant().startHalfHOurGrandFatherClock();
						System.out.println("the hour switch was checked,,");
					}

					System.out.println("the hour switch was checked,,");
				} else {
					if (hourlStatus.notEmpty())
						hourlStatus.get().setStatus(false);
					saveHourlyGrandFatherStatus();
					GrandFatherClockStartor.getInstant().stopHourlyGrandFatherClock();
					System.out.println("the hour switch was un checked,,");

					// when the hourswitch was checked, the halfhourswitch was
					// also checked, then start the halfhour clock tasks also.
					// if ( Half_Hour_Switch.isChecked() == false)
					// {
					GrandFatherClockStartor.getInstant().stopHalfHOurGrandFatherClock();
					System.out.println("the hour switch was checked,,");
					// }
				}

			}
		});

		countSwitch.setOnCheckedChangeListener(new TimeSwitchOnCheckedChangeListener() {
			public void onCheckedChanged(TimeSwitch buttonView, boolean isChecked) {
				// hourSwitch.setChecked(isChecked);
				String count = "";
				if (isChecked) {
					count = "Once";
				} else {
					count = "Hour";
				}
				if (hourlStatus.notEmpty())
					hourlStatus.get().setCount(count);
				saveHourlyGrandFatherStatus();
			}

		});

		VibrationSwitch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener() {
			public void onCheckedChanged(MySwitch buttonView, boolean isChecked) {
				// hourSwitch.setChecked(isChecked);
				if (isChecked) {
					if (hourlStatus.notEmpty())
						hourlStatus.get().setVibration("1");
					System.out.println("the hour switch was checked,,");
				} else {
					if (hourlStatus.notEmpty())
						hourlStatus.get().setVibration("0");
					System.out.println("the hour switch was un checked,,");

				}
				saveHourlyGrandFatherStatus();
			}
		});

		Half_Hour_Switch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener() {
			public void onCheckedChanged(MySwitch buttonView, boolean isChecked) {
				// hourSwitch.setChecked(isChecked);
				saveGrandFatherEffectMsg();
				if (isChecked) {
					if (halfHourStatus.notEmpty())
						halfHourStatus.get().setStatus(true);
					saveHalfHourGrandFatherStatus();

					if (hourSwitch.isChecked() && Half_Hour_Switch.isChecked()) {
						GrandFatherClockStartor.getInstant().startHalfHOurGrandFatherClock();
						System.out.println("the hour switch was checked,,");
					}
				} else {
					if (halfHourStatus.notEmpty())
						halfHourStatus.get().setStatus(false);
					saveHalfHourGrandFatherStatus();
					System.out.println("the hour switch was un checked,,");
					GrandFatherClockStartor.getInstant().stopHalfHOurGrandFatherClock();
				}

			}
		});

		initTitileBar();

	}

	public void initTitileBar() {
		initBackButton();
	}

	private void setupColorButtons(ViewGroup container) {
		View child = null;
		for (int i = 0; i < container.getChildCount(); i++) {
			child = container.getChildAt(i);

			if (child instanceof RelativeLayout) {
				setupColorButton((RelativeLayout) child);
			} else if (child instanceof Button) {

			} else if (child instanceof LinearLayout) {
				setupColorButtons((ViewGroup) child);
			}
		}
	}

	private void setupColorButton(RelativeLayout view) {
		view.setOnClickListener(mOnColorClick);
		// view.setOnTouchListener(mOnColorClick);

		mColorButtons.add(view);
	}

	private void attachColorData() {
		View v;
		for (int i = 0; i < EColor2.COLORS.length; i++) {
			int color = EColor2.COLORS[i];

			v = mColorButtons.get(i).findViewById(R.id.color_container);
			/*if(color==-99)
			{
				v.setVisibility(View.GONE);
				continue;
			}*/
			if (color == -99) {
				v.setBackgroundResource(R.drawable.btn_customfx_col_random);
				// continue;
			} else {
				v.setBackgroundColor(color);
			}
			mColorButtons.get(i).setTag(new ColorData(color));
		}
	}

	private void saveGrandFatherEffectMsg() {
		mCurrentEffect.editable = true;

		grandFatherEmbraceMsg.set(VoTransfer.transferFromEffectVOToEmbraceMsg(mCurrentEffect));

		if (grandFatherEmbraceMsg.notEmpty()) {
			grandFatherEmbraceMsg.get().setFadeIn((byte) -56);
			grandFatherEmbraceMsg.get().setFadeOut((byte) 0);
			grandFatherEmbraceMsg.get().setPause((byte) 0);
			grandFatherEmbraceMsg.get().setBlackout(false);
			grandFatherEmbraceMsg.get().setHold((byte) 0);
			// DbBuilder.getInstant().updateGrandFatherMsg(grandFatherEmbraceMsg.get());
			GrandpaMsgDB.getInstance().updateGrandFatherMsg(grandFatherEmbraceMsg.get());
		}
	}

	private void saveHalfHourGrandFatherStatus() {
		if (halfHourStatus.notEmpty()) {
			// DbBuilder.getInstant().updateGrandFatherStatus(halfHourStatus.get());
			GrandpaMsgDB.getInstance().updateGrandFatherStatus(halfHourStatus.get());
		}
	}

	private void saveHourlyGrandFatherStatus() {
		if (hourlStatus.notEmpty()) {
			// DbBuilder.getInstant().updateGrandFatherStatus(hourlStatus.get());
			GrandpaMsgDB.getInstance().updateGrandFatherStatus(hourlStatus.get());
		}
	}

	private OnClickListener mOnColorClick = new OnClickListener() {
		@Override
		public void onClick(View view) {

			int color = ((ColorData) view.getTag()).color;

			if (color == -99) {
				mCurrentEffect.random = true;

			} else {
				mCurrentEffect.random = false;
				mCurrentEffect.colorL1 = mCurrentEffect.colorR1 = color;
			}

			mCurrentEffect.editable = true;
			mCurrentEffect.colorL2 = mCurrentEffect.colorR2 = Color.TRANSPARENT;

			mEmulator.play(mCurrentEffect);

			saveGrandFatherEffectMsg();

		}
	};

	private class ColorData {
		public int color;

		public ColorData(int color) {
			this.color = color;
		}
	}
	
	public static void initStatus() {
		GrandpaMsgDB.getInstance().deleteGrandFatherStatus();
	}

}
