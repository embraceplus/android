package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.MyViewPagerAdapter;
import com.embraceplus.app.fragment.utils.FxGridViewAdapter;
import com.embraceplus.ble.utils.ServiceManager;
import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.database.PreDefineCommandDB;
import com.embraceplus.model.EffectVO;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.ExCommandObj;
import com.embraceplus.model.VoTransfer;
import com.embraceplus.utils.Item;
import com.embraceplus.utils.PreDefineUtil;
import com.embraceplus.utils.ResourceUtils;
import com.embraceplus.utils.SelectMsgWrap;
import com.embraceplus.widget.EmulatorView;
import com.embraceplus.widget.FxGridView;
import com.embraceplus.widget.SelectPicPopupWindow;

public class BaseFragmentWithFxGridView extends BaseFragment {
	private List<GridView> mLists;
	private List<Item> itemList;
	private List<FxGridViewAdapter> adapterList = new ArrayList<FxGridViewAdapter>();
	private MyViewPagerAdapter adapter;
	private boolean editModel = false;
	private ViewPager mViewPager;
	private String notificationName;
	private String selectedMsgName;
	private SelectPicPopupWindow menuWindow;
	private SelectMsgWrap selectMsgWrap = new SelectMsgWrap();
	private EmbraceMsg msg;
	private EmulatorView mEmulator;

	private boolean isFinishAdded = false;

	private ImageView[] imageViews;
	private ImageView imageView;

	private List<ExCommandObj> msgNames;
	private int indicatorWidth = 10;
	private int lineNum = 2;

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// initTitleBar();

		indicatorWidth = (int) this.getResources().getDimension(R.dimen.choose_style_pager_indicator_width);
		mViewPager = (ViewPager) this.getActivity().findViewById(R.id.viewpager);
		mViewPager.setOnPageChangeListener(new MyOnPageChanger());

		initGridView();
		adapter = new MyViewPagerAdapter(this.getActivity(), mLists);
		mViewPager.setAdapter(adapter);
		mEmulator = (EmulatorView) view.findViewById(R.id.emulator);
		msg = PreDefineCommandDB.getInstance().getMsgObjByMsgName(selectedMsgName);
		EffectVO mCurrentEffect = VoTransfer.transferFromEmbraceMsgToEffectVO(msg);

		mEmulator.play(mCurrentEffect);

		int currentMessage = 0;
		for (ExCommandObj e : msgNames) {
			if (e.getExCommandName().equals(selectedMsgName))
				currentMessage = msgNames.indexOf(e);
		}

		mViewPager.setCurrentItem((int) Math.floor(currentMessage / (3.0f * lineNum)), true);
		if (getArguments() != null) {
			isFinishAdded = getArguments().getBoolean("isAdd");
			int currentPage = getArguments().getInt("currentPage", -1);
			if (isFinishAdded) {
				mViewPager.setCurrentItem(mLists.size() - 1);
			} else if (currentPage != -1 && currentPage < mLists.size()) {
				mViewPager.setCurrentItem(currentPage);
			}
		}

		initSelectButton(view);
	}

	public void initSelectButton(final View view) {
		TextView selectText = (TextView) view.findViewById(R.id.select_text);

		selectText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ServiceManager.getInstant().getBluetoothService() == null) {
					return;
				}
				if (ServiceManager.getInstant().isEmbraceAvailable()) {
					msg = PreDefineCommandDB.getInstance().getMsgObjByMsgName(selectedMsgName);
					if (msg != null) {
//						boolean effect = msg.getEffect();
						ServiceManager.getInstant().getBluetoothService().writeEffectCommand(msg.getFXCommand());
					}
				}
			}
		});
	}

	public void initTitleBar(String title) {

		TextView textView = (TextView) getView().findViewById(R.id.text_title);
		textView.setText(title);
		initBackButton();

		setRightImageButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menuWindow = new SelectPicPopupWindow(getActivity(), itemsOnClick);

				boolean hasCustomizedMsg = false;
				for (Item ite : itemList) {
					String msgName = ite.getTitle();
					if (!PreDefineUtil.getInstant().isPreDefinedMsg(msgName)) {
						hasCustomizedMsg = true;
						break;
					}
				}
				if (hasCustomizedMsg) {
					menuWindow.getBtn_edit().setVisibility(View.VISIBLE);

				} else {
					menuWindow.getBtn_edit().setVisibility(View.INVISIBLE);
				}

				menuWindow.showAtLocation(getActivity().findViewById(R.id.add_style_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});

		setDoneButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// /* mRightButton = (Button)
				// this.getActivity().findViewById(R.id.button_right);*/
				for (int i = 0; i < adapterList.size(); i++) {
					adapterList.get(i).invisiableDel();
				}

				editModel = false;
				showRightImageButton();
				hideDoneButton();
			}
		});

	}

	public void initGridView() {
		itemList = new ArrayList<Item>();
		// msgNames =
		// DbBuilder.getInstant().getExCommandListByByCurTheme(notificationName);
		msgNames = NotificationCommandMappingDB.getInstance().getExCommandListByByCurTheme(getNotificationName());

		for (int j = 0; j < msgNames.size(); j++) {

			String msgName = msgNames.get(j).getExCommandName();
			Item item = new Item();
			if (msgNames.get(j).isSelected()) {
				selectedMsgName = msgName;
				selectMsgWrap.setSelectedMsgName(selectedMsgName);
				item.setSelected(true);
			}

			item.setTitle(msgNames.get(j).getExCommandName());

			item.setDrawable(ResourceUtils.getInstance().getResourceId(msgNames.get(j).getMsgIcon()));

			itemList.add(item);
		}

		if (selectedMsgName == null || selectedMsgName.equals("")) {
			itemList.get(0).setSelected(true);
			selectedMsgName = msgNames.get(0).getExCommandName();
		}

		if (selectMsgWrap.getSelectedMsgName() == null) {
			selectMsgWrap.setSelectedMsgName(selectedMsgName);
		}

		final int PageCount = (int) Math.ceil(itemList.size() / (3.0f * lineNum));
		mLists = new ArrayList<GridView>();

		for (int i = 0; i < PageCount; i++) {
			FxGridView gridView = new FxGridView(this.getActivity());
			gridView.setLineNum(lineNum);
			gridView.setParentFragment(this);
			gridView.setIndex(i);
			gridView.setAdapterList(adapterList);
			gridView.setSelectMsgWrap(selectMsgWrap);
			gridView.setItemList(itemList);
			mLists.add(gridView);

		}
		initDotsView();
	}

	public void initDotsView() {
		ViewGroup group = (ViewGroup) this.getActivity().findViewById(R.id.viewGroup);
		imageViews = new ImageView[mLists.size()];
		if (mLists.size() > 1) {
			for (int i = 0; i < mLists.size(); i++) {
				imageView = new ImageView(getActivity());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indicatorWidth, indicatorWidth);
				params.setMargins(0, 0, 14, 0);
				imageView.setLayoutParams(params);

				imageViews[i] = imageView;
				if (i == 0) {
					imageViews[i].setBackgroundResource(R.drawable.dot_focused);
				} else {
					imageViews[i].setBackgroundResource(R.drawable.dot_normal);
				}
				group.addView(imageViews[i]);
			}
		}
	}

	public void selectItem(View arg1, int index) {
		if (!editModel || index < 6) {

			ViewPager viewPager = (ViewPager) arg1.getParent().getParent();
			for (int i = 0; i < viewPager.getChildCount(); i++) {
				GridView gv = (GridView) viewPager.getChildAt(i);
				for (int j = 0; j < gv.getChildCount(); j++) {
					// LinearLayout lineLayout =
					View view = gv.getChildAt(j);
					View tmpView = view.findViewById(R.id.view);
					tmpView.setVisibility(View.INVISIBLE);
					//
				}
			}

			for (int i = 0; i < itemList.size(); i++) {
				if (i != index) {
					itemList.get(i).setSelected(false);
				} else {
					itemList.get(i).setSelected(true);
				}
			}

			View view = (View) arg1.findViewById(R.id.view);
			view.setVisibility(View.VISIBLE);

			selectedMsgName = msgNames.get(index).getExCommandName();
			selectMsgWrap.setSelectedMsgName(selectedMsgName);
			NotificationCommandMappingDB.getInstance().selectCommandForSelectedNotificationOnCurrentTheme(selectedMsgName, notificationName);
			msg = PreDefineCommandDB.getInstance().getMsgObjByMsgName(selectedMsgName);
			EffectVO mCurrentEffect = VoTransfer.transferFromEmbraceMsgToEffectVO(msg);
            System.out.println("----=-=-=-1222222======" + selectedMsgName);
			mEmulator.play(mCurrentEffect);
		} else {
			Bundle bundle = new Bundle();
			bundle.putString("notification", notificationName);
			bundle.putString("msgName", itemList.get(index).getTitle());
			bundle.putInt("currentPage", mViewPager.getCurrentItem());

			FxEditorFragment customFxFragment1 = new FxEditorFragment();
			customFxFragment1.setArguments(bundle);
			FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
			transaction1.replace(R.id.container, customFxFragment1);
			transaction1.addToBackStack(null);
			transaction1.commit();
			editModel = false;
		}
	}

	public void selectFirstItem() {
		GridView gridView = (GridView) mViewPager.getChildAt(0);
		editModel = false;
		// gridView.performItemClick(gridView.getChildAt(0), 0,
		// gridView.getChildAt(0).getId());
		selectItem(gridView.getChildAt(0), 0);
		editModel = true;
		Log.v("", "testSelectFirstItem");

	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public String getSelectedMsgName() {
		return selectedMsgName;
	}

	public void setSelectedMsgName(String selectedMsgName) {
		this.selectedMsgName = selectedMsgName;
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			int id = v.getId();
			if (id == R.id.button_edit) {

				for (int i = 0; i < adapterList.size(); i++) {
					adapterList.get(i).visiableDel();
				}
				editModel = true;
				hideRightImageButton();
				showDoneButton();

			} else if (id == R.id.btn_add) {

				Bundle bundle = new Bundle();
				bundle.putString("notification", getNotificationName());

				FxEditorFragment customFxFragment1 = new FxEditorFragment();
				customFxFragment1.setArguments(bundle);
				customFxFragment1.setNotificationName(getNotificationName());
				FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
				transaction1.replace(R.id.container, customFxFragment1);
				transaction1.addToBackStack(null);
				transaction1.commit();
			} else {
			}
		}

	};

	class MyOnPageChanger implements OnPageChangeListener {

		private int index = 0;

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {

			index = arg0;

			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.dot_focused);

				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.dot_normal);
				}
			}
			Log.i("jiangqq", "��ǰ�ڵ�" + index + "ҳ");

		}

	}
	
	
}
