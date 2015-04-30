package com.embraceplus.fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.embraceplus.app.R;
import com.embraceplus.app.SearchActivity;
import com.embraceplus.app.fragment.utils.SettingAdapter;

public class SettingFragment extends BaseFragment {

	private SettingAdapter adapter;
	private ListView listView;
	private int[] type = { 0, 1, 2, 0, 0, 0 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container, false);
		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initBackButton();
		listView = (ListView) this.getView().findViewById(R.id.setting_listView);
		adapter = new SettingAdapter(this.getActivity(), type);
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {

					ThemeSelectionFragment fragment = new ThemeSelectionFragment();
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.replace(R.id.container, fragment);
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					transaction.addToBackStack(null);
					transaction.commit();
				} else if (position == 4) {
					replaceFragment(new AboutUsViewFragment());
//					FragmentTransaction transaction = getFragmentManager().beginTransaction();
//					transaction.replace(R.id.container, about);
//					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//					transaction.addToBackStack(null);
//					transaction.commit();
				} else if (position == 5) {
					replaceFragment(new HelpViewFragment());
					
//					FragmentTransaction transaction = getFragmentManager().beginTransaction();
//					transaction.replace(R.id.container, about);
//					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//					transaction.addToBackStack(null);
//					transaction.commit();
				} else if (position == 3) {
					Intent intent = new Intent(SettingFragment.this.getActivity(), SearchActivity.class);
					intent.putExtra("scannNew", 1);
					SettingFragment.this.startActivity(intent);
				}
			}
		});
	}
}
