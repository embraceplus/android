package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.embraceplus.app.R;
import com.embraceplus.app.fragment.utils.AddEventViewFragmentAdapter;
import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.model.NotificationSelectedObj;

public class AddEventViewFragment extends BaseFragment {

	private AddEventViewFragmentAdapter adapter;
	private ListView mListView;
	private List<NotificationSelectedObj> list;
	private ImageView mBacButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_add_event, container, false);
		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		SetData();
		initBackButton();
	}

	public void SetData() {
		mListView = (ListView) this.getActivity().findViewById(R.id.add_event_list);
		mListView.setDivider(null);
		list = new ArrayList<NotificationSelectedObj>();
		// list = DbBuilder.getInstant().getAllNotifications();
		list = NotificationCommandMappingDB.getInstance().getAllNotifications();

		adapter = new AddEventViewFragmentAdapter(this.getActivity(), list);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (list.get(position).isSelected()) {

				} else {
					String notificationName = list.get(position).getNotificationName();
					// DbBuilder.getInstant().addNewNotificationOnCurrentTheme(notificationName);
					NotificationCommandMappingDB.getInstance().addNewNotificationOnCurrentTheme(notificationName);

					popBackStack();
				}
			}
		});

	}
}
