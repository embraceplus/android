package com.embraceplus.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.embraceplus.app.fragment.utils.FxGridViewAdapter;
import com.embraceplus.fragment.BaseFragmentWithFxGridView;
import com.embraceplus.utils.Item;
import com.embraceplus.utils.SelectMsgWrap;

public class FxGridView extends GridView {
	private BaseFragmentWithFxGridView parentFragment;
	private int index;
	private List<FxGridViewAdapter> adapterList;
	private SelectMsgWrap selectMsgWrap;
	private List<Item> itemList;
	private int lineNum = 2;
	
	public FxGridView(Context context) {
		super(context);
		setNumColumns(3);
		listenlLayoutChange();
		
//		setBackgroundColor(Color.BLUE);
	}

	private void listenlLayoutChange() {
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
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
	
	private void initGridView() {
		int gridViewWidth = getWidth();
		int padding = 30;

		int defaultImageWidth = 182;
		int defaultImageHeight = 264;
		
		int iconWidth = gridViewWidth - padding * 8;
		iconWidth /= 3;
		int iconHeight = iconWidth * defaultImageHeight / defaultImageWidth;
		
		int scaleX = iconWidth + 2*padding;
		int scaleY = iconHeight + 2*padding;

//		ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
//		layoutParams.width = gridViewWidth;
//		layoutParams.height = scaleY * lineNum;
//		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(gridViewWidth, scaleY * lineNum);
//		setLayoutParams(layoutParams);
		
		FxGridViewAdapter adapter = new FxGridViewAdapter(parentFragment.getActivity(), getItemList(), getIndex(), lineNum);
		
		adapter.setItemPadding(padding);
		adapter.setItemScale(scaleX,scaleY);
		adapter.setParentFragment(parentFragment);
		adapter.setSelectMsgWrap(getSelectMsgWrap());
		getAdapterList().add(adapter);
		adapter.setAdapterList(getAdapterList());
		setAdapter(adapter);
		setGravity(Gravity.CENTER);
		setPadding(padding, padding, padding, padding);
		setClickable(true);
		setFocusable(true);
		setSelector(new ColorDrawable(Color.TRANSPARENT));
		final int pageIndex = getIndex();
		setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int index = pageIndex * getItemCountPerPage()  + arg2;
				parentFragment.selectItem(arg1, index);
			}
		});
	}

	private int getItemCountPerPage() {
		return 3 * lineNum;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}


	public List<FxGridViewAdapter> getAdapterList() {
		return adapterList;
	}


	public void setAdapterList(List<FxGridViewAdapter> adapterList) {
		this.adapterList = adapterList;
	}


	public SelectMsgWrap getSelectMsgWrap() {
		return selectMsgWrap;
	}


	public void setSelectMsgWrap(SelectMsgWrap selectMsgWrap) {
		this.selectMsgWrap = selectMsgWrap;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}

	public BaseFragmentWithFxGridView getParentFragment() {
		return parentFragment;
	}

	public void setParentFragment(BaseFragmentWithFxGridView parentFragment) {
		this.parentFragment = parentFragment;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
}
