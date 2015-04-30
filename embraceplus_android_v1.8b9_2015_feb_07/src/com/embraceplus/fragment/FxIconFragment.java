package com.embraceplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.GridView;

import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.IconGridViewAdapter;
import com.embraceplus.widget.SelectPicPopupWindow;

public class FxIconFragment extends BaseFragment {
	SelectPicPopupWindow menuWindow;
	private int[] ResIds;
	private String notificationName;
	private FxEditorFragment lastCustomFxFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_fx_icon, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initTitleBar();
		listenlLayoutChange();
	}

	public void initTitleBar() {
		initBackButton();
	}

	public void initGridView() {
		GridView gridView = (GridView) getView().findViewById(R.id.fx_icon_gridView);
		int gridViewWidth = gridView.getWidth();
		int padding = 30;

		int defaultImageWidth = 182;
		int defaultImageHeight = 264;
		
		int iconWidth = gridViewWidth - padding * 8;
		iconWidth /= 3;
		int iconHeight = iconWidth * defaultImageHeight / defaultImageWidth;
		
		int scaleX = iconWidth + 2*padding;
		int scaleY = iconHeight + 2*padding;
		
		gridView.setPadding(padding, padding, padding, padding);
		IconGridViewAdapter adapter = new IconGridViewAdapter(this, ResIds);
		adapter.setItemPadding(padding);
		adapter.setItemScale(scaleX, scaleY);
		gridView.setPadding(padding, padding, padding, padding);
		adapter.setLastCustomFxFragment(lastCustomFxFragment);
		adapter.setNotificationName(notificationName);
		gridView.setAdapter(adapter);
	}

	public FxEditorFragment getLastCustomFxFragment() {
		return lastCustomFxFragment;
	}

	public void setLastCustomFxFragment(FxEditorFragment lastCustomFxFragment) {
		this.lastCustomFxFragment = lastCustomFxFragment;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	private void listenlLayoutChange() {
		GridView gridView = (GridView) getView().findViewById(R.id.fx_icon_gridView);
		gridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			boolean subViewIsSet = false;
			@Override
			public void onGlobalLayout() {
				if (!subViewIsSet) {
					initGridView();
					subViewIsSet = true;
				}
			}
		});
	}
}
