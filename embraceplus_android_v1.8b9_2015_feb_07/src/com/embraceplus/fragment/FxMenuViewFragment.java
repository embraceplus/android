package com.embraceplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.embraceplus.app.R;

public class FxMenuViewFragment extends BaseFragmentWithFxGridView {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setNotificationName(getArguments().getString("notification"));
		return inflater.inflate(R.layout.fragment_fx_menu, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		String title = null;// notificationName.substring(0,5);
		if (getNotificationName().startsWith("Call_")) {
			title = getNotificationName().substring(5, getNotificationName().length());
		} else {
			title = getNotificationName();
		}
		initTitleBar(title);
	}
}
