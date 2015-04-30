package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.ThemeSelectionGridViewAdapter;
import com.embraceplus.app.fragment.utils.ChooseStyleViewPagerAdapter;
import com.embraceplus.app.fragment.utils.Holder;
import com.embraceplus.database.DefinedThemeDB;
import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.database.UtilitiesDB;
import com.embraceplus.model.EffectVO;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.ThemeObj;
import com.embraceplus.model.VoTransfer;
import com.embraceplus.utils.Item;
import com.embraceplus.utils.PreDefineUtil;
import com.embraceplus.utils.ResourceUtils;
import com.embraceplus.utils.SkinSettingManager;
import com.embraceplus.widget.EmulatorView;
import com.embraceplus.widget.SelectPicPopupWindow;

public class ThemeSelectionFragment extends BaseFragment {
	private static int themesPerPageCount = 3;

	private ImageView imageView;
	private ImageView[] imageViews;
	private ViewGroup group;

	private TextView select_text;

	private SkinSettingManager mSettingManager;

	private ChooseStyleViewPagerAdapter adapter;
	private List<GridView> mLists;
	private ViewPager mViewPager;
	View maskView;
	private int index = 0;
	private boolean editModel = false;
	SelectPicPopupWindow menuWindow;
	List<ThemeObj> themeList;
	String selectedThemeName;
	List<Holder> holderList = new ArrayList<Holder>();
	List<ThemeSelectionGridViewAdapter> adapterList;

	private EmulatorView mEmulator;

	public List<HashMap<String, Object>> mList;
	private List<Item> itemList;
	int indicatorWidth = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		indicatorWidth = (int) this.getResources().getDimension(R.dimen.choose_style_pager_indicator_width);
		View view = inflater.inflate(R.layout.choose_style_view_fragment, container, false);
		mEmulator = (EmulatorView) view.findViewById(R.id.emulator);
		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		editModel = false;
		initSettingButton();
		init();
		// ActivityFragmentViewPager();

		mViewPager = (ViewPager) this.getActivity().findViewById(R.id.guidePages);
		maskView = (View) this.getActivity().findViewById(R.id.maskView);
		mViewPager.setOnPageChangeListener(new MyOnPageChanger());
		adapter = new ChooseStyleViewPagerAdapter(this.getActivity(), mLists);
		mViewPager.setAdapter(adapter);

		select_text = (TextView) this.getActivity().findViewById(R.id.select_text);
		select_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NotificationsFragment detailFragment = new NotificationsFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.container, detailFragment);
				Bundle bundle = new Bundle();
				// bundle.putString("themeName", notificatioinName);
				detailFragment.setArguments(bundle);
				// DbBuilder.getInstant().selectCurrentTheme(selectedThemeName);
				UtilitiesDB.getInstance().selectCurrentTheme(selectedThemeName);

				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});

		// EmbraceMsg msg = DbBuilder.getInstant()
		// .getThemeFirstCommandOnCurrentTheme();
		EmbraceMsg msg = NotificationCommandMappingDB.getInstance().getThemeFirstCommandOnCurrentTheme();
		EffectVO mCurrentEffect = VoTransfer.transferFromEmbraceMsgToEffectVO(msg);

		mEmulator.play(mCurrentEffect);
	}

	public void initSettingButton() {
		getView().findViewById(R.id.button_right).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popBackStack();

			}
		});
	}

	public void init() {

		mSettingManager = new SkinSettingManager(this.getActivity());
		group = (ViewGroup) this.getActivity().findViewById(R.id.viewGroup);
		itemList = new ArrayList<Item>();
		// themeList = DbBuilder.getInstant().getThemes();
		themeList = DefinedThemeDB.getInstance().getThemes();
		for (int j = 0; j < themeList.size(); j++) {
			String themeName = themeList.get(j).getThemeName();
			Item item = new Item();
			item.setTitle(themeName);

			int resoureceId = ResourceUtils.getInstance().getResourceId(themeList.get(j).getThemeIcon());
			item.setDrawable(resoureceId);
			item.setSelected(themeList.get(j).isSelected());

			if (themeList.get(j).isSelected()) {
				String backgroundName = themeList.get(j).getThemeBackground();
				if (!backgroundName.startsWith("bg_")) {
					backgroundName = themeList.get(j).getThemeName();
				}

				mSettingManager.changeSkin(backgroundName);
				selectedThemeName = themeList.get(j).getThemeName();
			}

			itemList.add(item);

		}

		Item addThemeItem = new Item();
		addThemeItem.setTitle("Add theme");
		addThemeItem.setDrawable(R.drawable.add_theme);
		itemList.add(addThemeItem);

		final int PageCount = (int) Math.ceil(itemList.size() / 3.0f);
		mLists = new ArrayList<GridView>();
		adapterList = new ArrayList<ThemeSelectionGridViewAdapter>();
		for (int i = 0; i < PageCount; i++) {

			GridView gv = new GridView(this.getActivity());
			gv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
			// gv.setAdapter(new ChooseStyleGridViewAdapter(
			// ChooseStyleViewFragment.this.getActivity(), arrayOfInt, i));
			ThemeSelectionGridViewAdapter chooseStyleGridViewAdapter = new ThemeSelectionGridViewAdapter(ThemeSelectionFragment.this.getActivity(), itemList, i);
			chooseStyleGridViewAdapter.setHolderList(holderList);
			adapterList.add(chooseStyleGridViewAdapter);
			chooseStyleGridViewAdapter.setAdapterList(adapterList);
			gv.setAdapter(chooseStyleGridViewAdapter);
			gv.setGravity(Gravity.CENTER);
			// gv.setHorizontalSpacing(10);
			gv.setHorizontalSpacing(0);

			// gv.setHorizontalSpacing(0);
			// gv.setVerticalSpacing(20);
			gv.setVerticalSpacing(0);

			// gv.setStretchMode(GridView.NO_STRETCH);
			gv.setClickable(true);
			gv.setFocusable(true);
			gv.setNumColumns(themesPerPageCount);

			gv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

					View imgView = (View) arg1.findViewById(R.id.view);
					int themeIndex = index * themesPerPageCount + arg2;
					if (!editModel || themeIndex < 9) {
						ViewPager viewPager = (ViewPager) imgView.getParent().getParent().getParent().getParent();
						for (int i = 0; i < viewPager.getChildCount(); i++) {
							GridView gridView = (GridView) viewPager.getChildAt(i);
							for (int j = 0; j < gridView.getChildCount(); j++) {
								LinearLayout linearLayout = (LinearLayout) gridView.getChildAt(j);
								// ImageView tmpImgView =(ImageView)
								// linearLayout.findViewById(R.id.theme_selected_image);
								View tmpImgView = (View) linearLayout.findViewById(R.id.view);

								if (i == index && j == arg2) {
									// tmpImgView.setVisibility(View.VISIBLE);

								} else {
									tmpImgView.setVisibility(View.INVISIBLE);
								}
							}
						}
						imgView.setVisibility(View.VISIBLE);

						if (themeIndex < themeList.size()) {
							selectedThemeName = itemList.get(themeIndex).getTitle();
							if (selectedThemeName.equals("Add theme")) {
								ThemeEditorFragment fragment = new ThemeEditorFragment();
								replaceFragment(fragment);
							} else {
								EmbraceMsg msg = NotificationCommandMappingDB.getInstance().getThemeFirstCommand(selectedThemeName);

								EffectVO mCurrentEffect = VoTransfer.transferFromEmbraceMsgToEffectVO(msg);

								mEmulator.play(mCurrentEffect);

								String themeBackground = themeList.get(themeIndex).getThemeBackground();
								if (themeBackground.equals("")) {
									themeBackground = selectedThemeName;
								}

								mSettingManager.changeSkin(themeBackground);
							}
						} else {
							ThemeEditorFragment fragment = new ThemeEditorFragment();
							replaceFragment(fragment);
						}
					} else {
						if (themeIndex < themeList.size()) {
							Bundle bundle = new Bundle();
							// bundle.putString("notification",
							// notificationName);
							bundle.putString("themeName", themeList.get(themeIndex).getThemeName());

							ThemeEditorFragment fragment = new ThemeEditorFragment();
							fragment.setArguments(bundle);
							replaceFragment(fragment);
						} else {
							if (!editModel) {
								ThemeEditorFragment fragment = new ThemeEditorFragment();
								replaceFragment(fragment);
							}
						}
					}
				}

			});
			mLists.add(gv);
		}

		imageViews = new ImageView[mLists.size()];
		if (mLists.size() > 1) {
			for (int i = 0; i < mLists.size(); i++) {
				imageView = new ImageView(ThemeSelectionFragment.this.getActivity());
				// ��������������������
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indicatorWidth, indicatorWidth);
				params.setMargins(0, 0, 13, 0);
				imageView.setLayoutParams(params);

				imageViews[i] = imageView;
				if (i == 0) {
					// ������������������
					imageViews[i].setBackgroundResource(R.drawable.dot_focused);
				} else {
					// ������������������������
					imageViews[i].setBackgroundResource(R.drawable.dot_normal);
				}
				group.addView(imageViews[i]);
			}
		}
	}

	class MyOnPageChanger implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {

			index = arg0;
			if (arg0 >= 3) {
				showRightImageButton();
				getRightImageButton().setImageResource(R.drawable.btn_options);
				setRightImageButtonOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow = new SelectPicPopupWindow(ThemeSelectionFragment.this.getActivity(), itemsOnClick);
						// menuWindow.getBtn_add().setVisibility(View.INVISIBLE);
						menuWindow.getBtn_add().setVisibility(View.GONE);
						maskView.setVisibility(View.VISIBLE);
						boolean hasCustomizedThee = false;
						for (Item ite : itemList) {
							String themeName = ite.getTitle();
							if (!PreDefineUtil.getInstant().isPreDefinedTheme(themeName)) {
								hasCustomizedThee = true;
								break;
							}
						}

						if (hasCustomizedThee) {
							menuWindow.getBtn_edit().setVisibility(View.VISIBLE);

						} else {
							menuWindow.getBtn_edit().setVisibility(View.INVISIBLE);
						}

						menuWindow.showAtLocation(ThemeSelectionFragment.this.getActivity().findViewById(R.id.add_style_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��

						menuWindow.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss() {
								maskView.setVisibility(View.GONE);

							}
						});
					}
				});

			}

			else {
				hideRightImageButton();
			}

			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.dot_focused);

				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.dot_normal);
				}
			}

		}

	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			int id = v.getId();
			if (id == R.id.button_edit) {
				editModel = true;
				for (int i = 3; i < adapterList.size(); i++) {
					adapterList.get(i).visiableDel();
				}

				hideRightImageButton();
				showDoneButton();
				setDoneButtonOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						editModel = false;
						for (int i = 0; i < adapterList.size(); i++) {
							adapterList.get(i).invisiableAllDel();
						}
						showRightImageButton();
						hideDoneButton();
					}

				});

			}
		}
	};
}
