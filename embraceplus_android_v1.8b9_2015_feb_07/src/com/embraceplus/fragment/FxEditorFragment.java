package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.embraceplus.app.MainActivity;
import com.embraceplus.app.R;
import com.embraceplus.ble.utils.Constants;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.PreDefineCommandDB;
import com.embraceplus.database.ThemeMsgMappingDB;
import com.embraceplus.model.EffectVO;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.MotionPresetVO;
import com.embraceplus.model.VoTransfer;
import com.embraceplus.utils.EColor;
import com.embraceplus.utils.ResourceUtils;
import com.embraceplus.widget.EmulatorView;
import com.embraceplus.widget.PageSelectorPagerView;
import com.embraceplus.widget.PageSelectorView;

public class FxEditorFragment extends BaseFragment {

	private static final int COLOR_MODE_LEFT = 1;
	private static final int COLOR_MODE_RIGHT = 2;
	private static final int COLOR_MODE_BOTH = 3;
	private String msgName;
	private String oldMsgName = null;
	private String iconName = null;
	ImageButton imbtn;
	private String notificationName;

	private int mColorMode = COLOR_MODE_BOTH;

	private EffectVO mCurrentEffect = new EffectVO();
	private int[] shuffleColorIndex = { 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

	private EmulatorView mEmulator;

	private PageSelectorView mPresetSelector;
	private PageSelectorView mColorSelector;

	private ArrayList<Button> mPresetButtons = new ArrayList<Button>();
	private ArrayList<View> mColorButtons = new ArrayList<View>();

	private View mCurrentMotionPreset = null;

	private ImageButton mShuffleColor;
	private ImageButton mVibrate;
	private EditText newMsgName;

	private Button mSelectLeft;
	private Button mSelectBoth;
	private Button mSelectRight;
	private ImageView mBackbtn;
	private boolean updateMsg;
	private EmbraceMsg msg;
	private String selectedFlashName;

	private View mCurrentColorBoth;
	private View mCurrentColorLeft;
	private View mCurrentColorRight;

	private View mColorCover;

	private MainActivity mActivity;


	private String fromFragment;

	private View view;
	private TextView preview;
	public int currentPage = -1;

	public MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_custom_fx, container, false);

			newMsgName = (EditText) view.findViewById(R.id.newMsgName);
			/*
			  InputMethodManager imm = (InputMethodManager)ContextUtil.getInstance().getSystemService(ContextUtil.getInstance().INPUT_METHOD_SERVICE);     

			    imm.hideSoftInputFromWindow(newMsgName.getWindowToken(), 0);  */

			notificationName = getArguments().getString("notification");
			msgName = getArguments().getString("msgName");
			currentPage = getArguments().getInt("currentPage", -1);

			if (msgName != null) {
				updateMsg = true;

//				msg = DbBuilder.getInstant().getMsgObjByMsgName(msgName);
				msg = PreDefineCommandDB.getInstance().getMsgObjByMsgName(msgName);
				mCurrentEffect = VoTransfer.transferFromEmbraceMsgToEffectVO(msg);
				mCurrentEffect.name = "Long beat";
				newMsgName.setText(msgName);
				// newMsgName.setEnabled(false);
				oldMsgName = msgName;
			}

			mEmulator = (EmulatorView) view.findViewById(R.id.emulator);

			mPresetSelector = (PageSelectorView) view.findViewById(R.id.motion_presets);
			mColorSelector = (PageSelectorView) view.findViewById(R.id.colors);

			mPresetSelector.setPager((PageSelectorPagerView) view.findViewById(R.id.motion_presets_pager));
			mColorSelector.setPager((PageSelectorPagerView) view.findViewById(R.id.colors_pager));

			setupPresetButtons(mPresetSelector);
			attachPresetData();

			setupColorButtons(mColorSelector);
			attachColorData();

			mShuffleColor = (ImageButton) view.findViewById(R.id.shuffle_color);
			mShuffleColor.setOnClickListener(onToggleClick);
			mVibrate = (ImageButton) view.findViewById(R.id.vibrate);
			mVibrate.setOnClickListener(onToggleClick);

			mColorCover = view.findViewById(R.id.colors_cover);
			mColorCover.setVisibility(View.GONE);

			mSelectLeft = (Button) view.findViewById(R.id.select_left);
			mSelectLeft.setOnClickListener(mOnSideSelectClick);

			mSelectBoth = (Button) view.findViewById(R.id.select_both);
			mSelectBoth.setOnClickListener(mOnSideSelectClick);

			mSelectRight = (Button) view.findViewById(R.id.select_right);
			mSelectRight.setOnClickListener(mOnSideSelectClick);

			mCurrentColorBoth = mColorButtons.get(0);
			mCurrentColorLeft = mColorButtons.get(0);
			mCurrentColorRight = mColorButtons.get(0);

			mCurrentColorBoth.setSelected(true);
			mCurrentColorLeft.setSelected(true);
			mCurrentColorRight.setSelected(true);

			mSelectBoth.performClick();

			if (msgName != null) {
				for (int i = 0; i < mPresetButtons.size(); i++) {
					MotionPresetVO m = (MotionPresetVO) mPresetButtons.get(i).getTag();
					if (msg.getFlashtype().equals(m.name)) {
						mPresetButtons.get(i).performClick();

						iconName = msg.getMsgIcon();

						mPresetSelector.setCurrentScreen((int) Math.floor(i / 6.0d));

						break;
					}
				}
			} else {
				mPresetButtons.get(0).performClick();
			}

			preview = (TextView) view.findViewById(R.id.preview);
			preview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mCurrentEffect.editable = true;
					msg = VoTransfer.transferFromEffectVOToEmbraceMsg(mCurrentEffect);

					// msg.setLoop((byte)3);
					int loop = getLoopValue(selectedFlashName);
					msg.setLoop((byte) loop);
					ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.getFXCommand());
				}
			});
		}


		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initTitleBar();
	}
	
	private OnClickListener mOnSideSelectClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mSelectBoth.setSelected(false);
			mSelectLeft.setSelected(false);
			mSelectRight.setSelected(false);

			v.setSelected(true);

			int id = v.getId();
			if (id == R.id.select_both) {
				mCurrentColorLeft.setSelected(false);
				mCurrentColorRight.setSelected(false);
				mCurrentColorBoth.setSelected(true);
				mColorMode = COLOR_MODE_BOTH;
			} else if (id == R.id.select_left) {
				mCurrentColorBoth.setSelected(false);
				mCurrentColorRight.setSelected(false);
				mCurrentColorLeft.setSelected(true);
				mColorMode = COLOR_MODE_LEFT;
			} else if (id == R.id.select_right) {
				mCurrentColorBoth.setSelected(false);
				mCurrentColorLeft.setSelected(false);
				mCurrentColorRight.setSelected(true);
				mColorMode = COLOR_MODE_RIGHT;
			}
		}
	};

	private OnClickListener onToggleClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {

			arg0.setSelected(!arg0.isSelected());

			int id = arg0.getId();
			if (id == R.id.shuffle_color) {
				// mCurrentEffect.random = arg0.isSelected();
				mCurrentEffect.random = false;
				mSelectBoth.setSelected(false);
				mSelectLeft.setSelected(false);
				mSelectRight.setSelected(false);
				Random rd = new Random();

				int randomIndexL1 = rd.nextInt(15);
				int randomIndexR1 = rd.nextInt(15);
				mCurrentEffect.colorL1 = EColor.COLORS[shuffleColorIndex[randomIndexL1]];
				mCurrentEffect.colorR1 = EColor.COLORS[shuffleColorIndex[randomIndexR1]];
				mSelectBoth.setSelected(true);
				mColorMode = COLOR_MODE_BOTH;
			} else if (id == R.id.vibrate) {
				mCurrentEffect.vibrate = arg0.isSelected();
			}

			mColorCover.setVisibility(mCurrentEffect.random ? View.VISIBLE : View.GONE);

//			mEmulator.play(mCurrentEffect);
			playEmulator();
		}
	};

	public int getLoopValue(String presetName) {
		int loopValue = 0;
		if (presetName.equals("Long beat")) {
			loopValue = 1;
		} else if (presetName.equals("Short beat")) {
			loopValue = 1;
		} else if (presetName.equals("Lightning")) {
			loopValue = 30;
		} else if (presetName.equals("Gallop")) {
			loopValue = 3;
		} else if (presetName.equals("Flicker")) {
			loopValue = 2;
		} else if (presetName.equals("Irredescent")) {
			loopValue = 3;
		} else if (presetName.equals("Oscillate")) {
			loopValue = 5;
		} else if (presetName.equals("Twinkle")) {
			loopValue = 3;
		} else if (presetName.equals("Sirens")) {
			loopValue = 3;
		} else if (presetName.equals("Bomb")) {
			loopValue = 1;
		} else if (presetName.equals("Pulse")) {
			loopValue = 3;
		} else if (presetName.equals("Shimmering")) {
			loopValue = 3;
		} else if (presetName.equals("3 Beat")) {
			loopValue = 3;
		} else if (presetName.equals("Shine")) {
			loopValue = 1;
		} else if (presetName.equals("Trickle")) {
			loopValue = 3;
		}
		return loopValue;
	}

	private void submitNewFxCommand() {
		String msgName = newMsgName.getText().toString();

		if (msgName == null || msgName.equals("") || msgName.length() < 3) {
			Toast.makeText(FxEditorFragment.this.getActivity(), "Please input the FX name, 3 letters minimum", Toast.LENGTH_LONG).show();
			return;
		}
		if (iconName == null || iconName.equals("")) {

			Toast.makeText(FxEditorFragment.this.getActivity(), "Please select the FX icon", Toast.LENGTH_LONG).show();
			return;

		}

		mCurrentEffect.editable = true;
		EmbraceMsg msg = VoTransfer.transferFromEffectVOToEmbraceMsg(mCurrentEffect);

		msg.setFlashtype(selectedFlashName);
		msg.setMsgName(msgName);
		msg.setMsgIcon(iconName);
		msg.setFlashtype(selectedFlashName);
		int loop = getLoopValue(selectedFlashName);
		msg.setLoop((byte) loop);
		if (updateMsg) {
			if (oldMsgName != null) {
				msg.setOldMsgName(oldMsgName);
			}
//			DbBuilder.getInstant().updateFxMsg(msg);
			PreDefineCommandDB.getInstance().updateFxMsg(msg);
			if (oldMsgName != null) {
//				DbBuilder.getInstant().updateMapping(msgName, oldMsgName);
				ThemeMsgMappingDB.getInstance().updateMapping(msgName, oldMsgName);
			}
		} else {
			if (PreDefineCommandDB.getInstance().isMsgExisted((msgName))) {
				alertMsg("The Fx message " + msgName + " is already existed, please change to a new name");
			}
			else{
				PreDefineCommandDB.getInstance().addNewFxMsg(msg);
			}
		}

		jumpPage();
	}

	public void jumpPage() {
		Bundle bundle = new Bundle();
		bundle.putString("notification", notificationName);
		bundle.putBoolean("isAdd", !updateMsg);
		bundle.putInt("currentPage", currentPage);
		if (notificationName.equals(Constants.notification_type_ALARM)) {
			AlarmFragment fragment = new AlarmFragment();
			fragment.setArguments(bundle);
		} else if (Constants.notification_type_Timer.equals(notificationName)) {

			TimerFragment fragment = new TimerFragment();
			fragment.setArguments(bundle);

		} else {
			FxMenuViewFragment fragment = new FxMenuViewFragment();
			fragment.setArguments(bundle);
		}
		popBackStack();
	}

	public void alertMsg(String msg) {
		new AlertDialog.Builder(FxEditorFragment.this.getActivity()).setTitle("Warning").setMessage(msg).setPositiveButton("OK", null).show();
	}

	private void attachColorData() {
		View v;
		for (int i = 1; i < EColor.COLORS.length; i++) {
			int color = EColor.COLORS[i];

			v = mColorButtons.get(i - 1).findViewById(R.id.color_container);
			if (color == -99) {
				v.setBackgroundResource(R.drawable.btn_customfx_col_random);

			} else {
				v.setBackgroundColor(color);
			}
			mColorButtons.get(i - 1).setTag(new ColorData(color));

			if (mCurrentEffect.colorL1 == color || mCurrentEffect.colorL2 == color || mCurrentEffect.colorR1 == color || mCurrentEffect.colorR2 == color) {

				mColorSelector.setCurrentScreen((int) Math.floor((i - 1) / 8.0d));
			}

			if (mCurrentEffect.random)
				mColorSelector.setCurrentScreen(0);

		}
	}

	private void attachPresetData() {
		List<MotionPresetVO> list = getMainActivity().getMotionPresets();

		Button b;
		MotionPresetVO m;
		for (int i = 0; i < mPresetButtons.size(); i++) {
			b = mPresetButtons.get(i);
			m = i < list.size() ? list.get(i) : null;

			if (m != null) {
				b.setVisibility(View.VISIBLE);
				b.setText(m.name);
				if (msg != null) {
					if (m.name.equals(msg.getFlashtype())) {
						b.setSelected(true);
					} else {
						b.setSelected(false);
					}
				}

				b.setTag(m);
			} else {
				b.setVisibility(View.GONE);
			}
		}
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

	private void setupPresetButtons(ViewGroup container) {
		View child = null;
		for (int i = 0; i < container.getChildCount(); i++) {
			child = container.getChildAt(i);

			if (child instanceof ViewGroup) {
				setupPresetButtons((ViewGroup) child);
			} else if (child instanceof Button) {
				setupPresetButton((Button) child);
			}
		}
	}

	private void setupPresetButton(Button button) {
		button.setOnClickListener(mOnPresetClick);
		button.setText("");

		mPresetButtons.add(button);
	}

	private OnClickListener mOnColorClick = new OnClickListener() {
		@Override
		public void onClick(View view) {

			int color = ((ColorData) view.getTag()).color;

			if (color == -99) {
				mSelectBoth.setSelected(false);
				mSelectLeft.setSelected(false);
				mSelectRight.setSelected(false);
				// mColorMode = COLOR_MODE_BOTH;
				mCurrentEffect.random = true;
				// mCurrentColorLeft.setSelected(false);
				// mCurrentColorRight.setSelected(false);
				// mCurrentColorBoth.setSelected(true);
				mSelectBoth.setSelected(true);
				mColorMode = COLOR_MODE_BOTH;
			} else {
				mCurrentEffect.random = false;
			}

			switch (mColorMode) {
			case COLOR_MODE_BOTH:
				mCurrentColorBoth.setSelected(false);
				mCurrentColorBoth = view;
				mCurrentColorBoth.setSelected(true);

				mCurrentEffect.colorL1 = mCurrentEffect.colorR1 = color;
				break;
			case COLOR_MODE_LEFT:
				mCurrentColorLeft.setSelected(false);
				mCurrentColorLeft = view;
				mCurrentColorLeft.setSelected(true);

				mCurrentEffect.colorL1 = color;
				break;
			case COLOR_MODE_RIGHT:
				mCurrentColorRight.setSelected(false);
				mCurrentColorRight = view;
				mCurrentColorRight.setSelected(true);

				mCurrentEffect.colorR1 = color;
				break;
			}

			mCurrentEffect.colorL2 = mCurrentEffect.colorR2 = Color.TRANSPARENT;

//			mEmulator.play(mCurrentEffect);
			playEmulator();

		}
	};

	/*
	private OnTouchListener mOnColorClick = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getAction();
			
			System.out.println("in the action ..CustomFxFragment");
			
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("in the action ..CustomFxFragment..MotionEvent.ACTION_DOWN");
				

				break;

			case MotionEvent.ACTION_MOVE:
				System.out.println("in the action .CustomFxFragment...MotionEvent.ACTION_MOVE");
				
				break;

			case MotionEvent.ACTION_UP:
				System.out.println("in the action ..CustomFxFragment..MotionEvent.ACTION_UP");

				break;
			case MotionEvent.ACTION_CANCEL:
				System.out.println("in the action ..CustomFxFragment..MotionEvent.ACTION_CANCEL");
			}
			
			
			return true;
		}
		@Override
		public void onClick(View view) {
			
			int color = ((ColorData) view.getTag()).color;
			
			if (color == -99)
			{
				mColorMode = COLOR_MODE_BOTH;
				mCurrentEffect.random=true;
			}else
			{
				mCurrentEffect.random=false;
			}
			
			switch (mColorMode) {
			case COLOR_MODE_BOTH:
				mCurrentColorBoth.setSelected(false);
				mCurrentColorBoth = view;
				mCurrentColorBoth.setSelected(true);
				
				mCurrentEffect.colorL1 = mCurrentEffect.colorR1 = color;
				break;
			case COLOR_MODE_LEFT:
				mCurrentColorLeft.setSelected(false);
				mCurrentColorLeft = view;
				mCurrentColorLeft.setSelected(true);
				
				mCurrentEffect.colorL1 = color;
				break;
			case COLOR_MODE_RIGHT:
				mCurrentColorRight.setSelected(false);
				mCurrentColorRight = view;
				mCurrentColorRight.setSelected(true);
				
				mCurrentEffect.colorR1 = color;
				break;
			}
			
			mCurrentEffect.colorL2 = mCurrentEffect.colorR2 = Color.TRANSPARENT;
			
			mEmulator.play(mCurrentEffect);
			
			
			
		}
	};*/

	private OnClickListener mOnPresetClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (mCurrentMotionPreset != null) {
				mCurrentMotionPreset.setSelected(false);
			}

			mCurrentMotionPreset = v;
			mCurrentMotionPreset.setSelected(true);

			MotionPresetVO m = (MotionPresetVO) mCurrentMotionPreset.getTag();
			selectedFlashName = m.name;

			if (m != null) {

				mCurrentEffect = getEffectVoFromCurrentMotionName(selectedFlashName);

				/*mCurrentEffect.fadeInTime = m.duration;
				mCurrentEffect.fadeOutTime = m.duration;
				mCurrentEffect.pauseTime = m.pause;*/

				// if (m.reverse) {
				// mCurrentEffect.colorR1 = mCurrentEffect.colorL2;
				// mCurrentEffect.colorR2 = mCurrentEffect.colorL1;
				// }

//				mEmulator.play(mCurrentEffect);
				playEmulator();

			}
		}
	};

	private class ColorData {
		public int color;

		public ColorData(int color) {
			this.color = color;
		}
	}

	public void initTitleBar() {

		//mFragmentManager = this.getActivity().getFragmentManager();
		imbtn = (ImageButton) view.findViewById(R.id.select_icon);
		if (iconName != null) {
			imbtn.setImageResource(ResourceUtils.getInstance().getResourceId(iconName));
		}
		imbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FxIconFragment fragment = new FxIconFragment();				
				fragment.setLastCustomFxFragment(FxEditorFragment.this);
				fragment.setNotificationName(notificationName);
				replaceFragmentForResult(fragment);
//				replaceFragment(fragment);
//				FragmentTransaction transaction = getFragmentManager().beginTransaction();
//				transaction.replace(R.id.container, fragment);
//				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//				transaction.addToBackStack(null);
//				transaction.commit();
			}
		});

		setDoneButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitNewFxCommand();
			}
		});
		setBackButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// popBackStack();
				jumpPage();
			}
		});
	}

	protected EffectVO getEffectVoFromCurrentMotionName(String selectedFlashName) {
		if (selectedFlashName.equals("Long beat")) {
			mCurrentEffect.fadeInTime = 0;
			mCurrentEffect.fadeOutTime = 0;
			mCurrentEffect.holdTime = 1000;
			mCurrentEffect.pauseTime = 1000;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Short beat")) {
			mCurrentEffect.fadeInTime = 0;
			mCurrentEffect.fadeOutTime = 0;
			mCurrentEffect.holdTime = 500;
			mCurrentEffect.pauseTime = 100;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Lightning")) {
			mCurrentEffect.fadeInTime = 100;
			mCurrentEffect.fadeOutTime = 0;
			mCurrentEffect.holdTime = 0;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = true;
		} else if (selectedFlashName.equals("Flicker")) {
			mCurrentEffect.fadeInTime = 0;
			mCurrentEffect.fadeOutTime = 300;
			mCurrentEffect.holdTime = 400;
			mCurrentEffect.pauseTime = 1000;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Oscillate")) {
			mCurrentEffect.fadeInTime = 0;
			mCurrentEffect.fadeOutTime = 100;
			mCurrentEffect.holdTime = 200;
			mCurrentEffect.pauseTime = 200;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Twinkle")) {
			mCurrentEffect.fadeInTime = 300;
			mCurrentEffect.fadeOutTime = 100;
			mCurrentEffect.holdTime = 300;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Sirens")) {
			mCurrentEffect.fadeInTime = 300;
			mCurrentEffect.fadeOutTime = 300;
			mCurrentEffect.holdTime = 400;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = true;
		} else if (selectedFlashName.equals("Bomb")) {
			mCurrentEffect.fadeInTime = 1500;
			mCurrentEffect.fadeOutTime = 0;
			mCurrentEffect.holdTime = 500;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Pulse")) {
			mCurrentEffect.fadeInTime = 400;
			mCurrentEffect.fadeOutTime = 400;
			mCurrentEffect.holdTime = 0;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Shimmering")) {
			mCurrentEffect.fadeInTime = 1000;
			mCurrentEffect.fadeOutTime = 1000;
			mCurrentEffect.holdTime = 0;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = true;
		} else if (selectedFlashName.equals("3 Beat")) {
			mCurrentEffect.fadeInTime = 0;
			mCurrentEffect.fadeOutTime = 0;
			mCurrentEffect.holdTime = 600;
			mCurrentEffect.pauseTime = 400;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Shine")) {
			mCurrentEffect.fadeInTime = 1000;
			mCurrentEffect.fadeOutTime = 1000;
			mCurrentEffect.holdTime = 2000;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Gallop")) {
			mCurrentEffect.fadeInTime = 400;
			mCurrentEffect.fadeOutTime = 400;
			mCurrentEffect.holdTime = 0;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Irredescent")) {
			mCurrentEffect.fadeInTime = 200;
			mCurrentEffect.fadeOutTime = 200;
			mCurrentEffect.holdTime = 0;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		} else if (selectedFlashName.equals("Trickle")) {
			mCurrentEffect.fadeInTime = 200;
			mCurrentEffect.fadeOutTime = 200;
			mCurrentEffect.holdTime = 0;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		} else {
			mCurrentEffect.fadeInTime = 1000;
			mCurrentEffect.fadeOutTime = 1000;
			mCurrentEffect.holdTime = 2000;
			mCurrentEffect.pauseTime = 0;
			mCurrentEffect.revert = false;
		}

		return mCurrentEffect;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public String getFromFragment() {
		return fromFragment;
	}

	public void setFromFragment(String fromFragment) {
		this.fromFragment = fromFragment;
	}

	/*
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
	        if(data != null) {
	           String value = data.getStringExtra(FRAGMENT_KEY);
	           if(value != null) {
	              Log.v(TAG, "Data passed from Child fragment = " + value);
	           }
	        }
	    }
	}
	*/
	
	private void playEmulator() {
		if (null != mEmulator) {
			mEmulator.play(mCurrentEffect);
//			mEmulator.playOnce(mCurrentEffect);
		}
	}
}
