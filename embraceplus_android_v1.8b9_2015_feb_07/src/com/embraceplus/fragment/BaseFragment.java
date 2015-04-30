package com.embraceplus.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.embraceplus.app.MainActivity;
import com.embraceplus.app.R;
import com.embraceplus.utils.MyToast;

public class BaseFragment extends Fragment implements OnClickListener {

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.v("BaseFragment", "onViewCreated:" + getClass().getName());
	}

	protected void replaceFragment(BaseFragment fragment) {
		replaceFragment(fragment,fragment.getClass().getName());
	}

	protected void replaceFragmentForResult(BaseFragment fragment) {
		fragment.setTargetFragment(this, 0);
		replaceFragment(fragment,fragment.getClass().getName());
	}
	
	protected void replaceFragment(BaseFragment fragment,String name) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.container, fragment, name);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(name);
		transaction.commit();
	}

	public void popBackStack() {
		getFragmentManager().popBackStack();
	}

	public void popBackStackWithResult(Intent intent) {
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
		getFragmentManager().popBackStack();
	}
	
	protected void setTitle(String titleText) {
		TextView textView = (TextView) getView().findViewById(R.id.text_title);
		if (null != textView) {
			textView.setText(titleText);
		}
	}
	
	protected void initBackButton() {
		setBackButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popBackStack();
			}
		});
	}

	protected void setBackButtonOnClickListener(OnClickListener l) {
		View backButton = getView().findViewById(R.id.back);
		if (null != backButton) {
			backButton.setOnClickListener(l);
		} else {
			MyToast.debugShow("Back button not exist!");
		}
	}

	protected void initSettingButton() {
		setRightImageButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingFragment fragment = new SettingFragment();
				replaceFragment(fragment);
			}
		});
	}
	
	protected void setRightImageButtonOnClickListener(OnClickListener l) {
		View button = getView().findViewById(R.id.button_right);
		if (null != button) {
			button.setOnClickListener(l);
		} else {
			MyToast.debugShow("Image button not exist!");
		}
	}
	
	protected void showRightImageButton() {
		View view = getView().findViewById(R.id.button_right);
		if (null != view) {
			view.setVisibility(View.VISIBLE);
		}
	}

	protected void hideRightImageButton() {
		View view = getView().findViewById(R.id.button_right);
		if (null != view) {
			view.setVisibility(View.GONE);
		}
	}

	protected void setDoneButtonOnClickListener(OnClickListener l) {
		View button = getView().findViewById(R.id.done_text);
		if (null != button) {
			button.setOnClickListener(l);
		} else {
			MyToast.debugShow("Done button not exist!");
		}
	}
	
	protected void showDoneButton() {
		View view = getView().findViewById(R.id.done_text);
		if (null != view) {
			view.setVisibility(View.VISIBLE);
		}
	}

	protected void hideDoneButton() {
		View view = getView().findViewById(R.id.done_text);
		if (null != view) {
			view.setVisibility(View.GONE);
		}
	}

	protected ImageView getRightImageButton() {
		return (ImageView) getView().findViewById(R.id.button_right);
	}

	protected TextView getRightTextButton() {
		return (TextView) getView().findViewById(R.id.done_text);
	}

	public MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}

	protected int getScreenWidth() {
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}
	
	public void alarmReceived() {
		
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back) {
			popBackStack();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.v("BaseFragment", "onResume:" + getClass().getName());
	}
}
