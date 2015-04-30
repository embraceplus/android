package com.embraceplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.embraceplus.app.R;

public class HelpDetailViewFragment extends BaseFragment implements OnClickListener {
	private ImageView helpImageView, helpImageView2, helpImageView3;
	ScrollView contentScroller;

	List<ScrollView> helpView = new ArrayList<ScrollView>();
	int[] helpImageIDs = new int[] { R.drawable.help_welcome_to_embraceplus, R.drawable.help_howtogetstarted, R.drawable.help_mute_notifications, R.drawable.help_light_effects,
			R.drawable.help_calls_features, R.drawable.help_clock_features_1, R.drawable.help_calendar, R.drawable.help_notifications, R.drawable.help_other_info_notifications,
			R.drawable.help_important_settings, R.drawable.help_important_thirdparty_settings, R.drawable.help_themes, R.drawable.help_multiple_bands, R.drawable.help_troubleshooter };
	String[] items = new String[] { "Welcome to Embrace+", "How to get started", "Mute notifications", "Configure light effects", "Configure calls features", "Configure clock features",
			"Configure calendar", "Add or remove notifications", "Other info notifications", "Important Android settings", "Important third-party app settings", "Themes", "Connecting multiple bands",
			"Troubleshooter" };
	int index;
	View nextLayout, backLayout;

	public HelpDetailViewFragment(int index) {
		this.index = index;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.help_detail, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		helpImageView = (ImageView) this.getActivity().findViewById(R.id.helpImageView);
		helpImageView2 = (ImageView) this.getActivity().findViewById(R.id.helpImageView2);
		helpImageView3 = (ImageView) this.getActivity().findViewById(R.id.helpImageView3);
		contentScroller = (ScrollView) this.getActivity().findViewById(R.id.contentScroller);
		setContent();

		getView().findViewById(R.id.backToIndex).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popBackStack();
			}
		});

		nextLayout = this.getActivity().findViewById(R.id.nextLayout);
		nextLayout.setOnClickListener(this);
		nextLayout.setVisibility(View.VISIBLE);

		backLayout = this.getActivity().findViewById(R.id.backLayout);
		backLayout.setOnClickListener(this);
		backLayout.setVisibility(View.VISIBLE);
	}

	public void setContent() {
		helpImageView.setImageResource(helpImageIDs[index]);
		((TextView) getView().findViewById(R.id.helpTitleTextView)).setText(items[index]);
		// if(index != 10 && index != 11) {
		helpImageView2.setVisibility(View.GONE);
		helpImageView3.setVisibility(View.GONE);
		// }

		switch (index) {
		// case 4:
		// helpImageView2.setImageResource(R.drawable.help_calls_features_2);
		// helpImageView2.setVisibility(View.VISIBLE);
		// break;
		case 10:
			helpImageView2.setImageResource(R.drawable.help_important_thirdparty_settings_2);
			helpImageView3.setImageResource(R.drawable.help_important_thirdparty_settings_3);
			helpImageView2.setVisibility(View.VISIBLE);
			helpImageView3.setVisibility(View.VISIBLE);
			break;
		case 11:
			helpImageView2.setImageResource(R.drawable.help_themes_2);
			helpImageView2.setVisibility(View.VISIBLE);
			break;
		case 5:
			helpImageView2.setImageResource(R.drawable.help_clock_features_2);
			helpImageView2.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
		contentScroller.fullScroll(ScrollView.FOCUS_UP);
	}

	@Override
	public void onClick(View v) {
		if (v == nextLayout) {
			if (index < 13) {
				index++;
				setContent();
			}
		} else if (v == backLayout) {
			if (index > 0) {
				index--;
				setContent();
			}
		}
	}
}
