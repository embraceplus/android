package com.embraceplus.app.fragment.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.embraceplus.app.R;
import com.embraceplus.fragment.FxEditorFragment;
import com.embraceplus.fragment.FxIconFragment;

public class IconGridViewAdapter extends BaseAdapter {

	// ����Context
	private Context mContext;
	private String notificationName;
	// ������������ ��ͼƬԴ
	final int[] ResIds;
	final String[] msgIconNames;

	private FxEditorFragment lastCustomFxFragment;
	private int padding;
	private final FxIconFragment fxIconFragment;
//	private int scale;
	
	private int scaleX;
	private int scaleY;

	// public IconGridViewAdapter(Context context, int[] ResIds) {
	// init(context);
	//
	// }

	public IconGridViewAdapter(FxIconFragment fxIconFragment, int[] resIds2) {
		// public IconGridViewAdapter(FxIconFragment fxIconFragment) {
		this.fxIconFragment = fxIconFragment;
		// IconGridViewAdapter(FxIconFragment.getMainActivity(),resIds2);
		// // TODO Auto-generated constructor stub
		// init(fxIconFragment.getMainActivity());
		// }
		//
		// public void init(Context context) {
		mContext = fxIconFragment.getMainActivity();

		int[] arrayOfInt = new int[24];
		arrayOfInt[0] = R.drawable.meeting;
		arrayOfInt[1] = R.drawable.workout;
		arrayOfInt[2] = R.drawable.after_work;
		arrayOfInt[3] = R.drawable.chat;
		arrayOfInt[4] = R.drawable.heartbeat;
		arrayOfInt[5] = R.drawable.sweet_life;
		arrayOfInt[6] = R.drawable.psychedelic;
		arrayOfInt[7] = R.drawable.bloodrush;
		arrayOfInt[8] = R.drawable.rasta;
		arrayOfInt[9] = R.drawable.electrifying;
		arrayOfInt[10] = R.drawable.nirvana;
		arrayOfInt[11] = R.drawable.holy;
		arrayOfInt[12] = R.drawable.atomic;
		arrayOfInt[13] = R.drawable.outdoor;
		arrayOfInt[14] = R.drawable.hardcore;
		arrayOfInt[15] = R.drawable.discreet;
		arrayOfInt[16] = R.drawable.toxic;
		arrayOfInt[17] = R.drawable.prancing;
		arrayOfInt[18] = R.drawable.biohazard;
		arrayOfInt[19] = R.drawable.punky;
		arrayOfInt[20] = R.drawable.singularity;
		arrayOfInt[21] = R.drawable.fabulous;
		arrayOfInt[22] = R.drawable.fugitive;
		arrayOfInt[23] = R.drawable.night_fever;

		String[] arrayIconName = new String[24];
		arrayIconName[0] = "meeting";
		arrayIconName[1] = "workout";
		arrayIconName[2] = "after_work";
		arrayIconName[3] = "chat";
		arrayIconName[4] = "heartbeat";
		arrayIconName[5] = "sweet_life";
		arrayIconName[6] = "psychedelic";
		arrayIconName[7] = "bloodrush";
		arrayIconName[8] = "rasta";
		arrayIconName[9] = "electrifying";
		arrayIconName[10] = "nirvana";
		arrayIconName[11] = "holy";
		arrayIconName[12] = "atomic";
		arrayIconName[13] = "outdoor";
		arrayIconName[14] = "hardcore";
		arrayIconName[15] = "discreet";
		arrayIconName[16] = "toxic";
		arrayIconName[17] = "prancing";
		arrayIconName[18] = "biohazard";
		arrayIconName[19] = "punky";
		arrayIconName[20] = "singularity";
		arrayIconName[21] = "fabulous";
		arrayIconName[22] = "fugitive";
		arrayIconName[23] = "night_fever";
		this.msgIconNames = arrayIconName;

		this.ResIds = arrayOfInt;
	}

	// ��ȡͼƬ�ĸ���
	public int getCount() {
		return ResIds.length;
	}

	// ��ȡͼƬ�ڿ��е�λ��
	public Object getItem(int position) {
		return position;
	}

	// ��ȡͼƬID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(scaleX, scaleY));
//			imageView.setPadding(0, padding, 0, padding);
			imageView.setPadding(padding, padding, padding, padding);
		} else {
			imageView = (ImageView) convertView;

		}
		final int resPosition = position;
		imageView.setImageResource(this.ResIds[position]);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lastCustomFxFragment.setIconName(msgIconNames[resPosition]);
				fxIconFragment.popBackStack();

				/*
				// CustomFxFragment fragment = new CustomFxFragment();
				FragmentTransaction transaction = ((Activity) mContext).getFragmentManager().beginTransaction();
				
				
				transaction.replace(R.id.container, lastCustomFxFragment);
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				transaction.commit();
				*/
			}

		});
		// imageView.setLayoutParams(new
		// GridView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

		/*	LayoutParams params = new LayoutParams(500,400);//imageView.getLayoutParams();
			params.height=50;
			params.width=50;
			imageView.setLayoutParams(params);*/

		// GridView.LayoutParams lp = new GridView.LayoutParams((int)
		// mContext.getResources().getDimension(R.dimen.icon_grid_width), (int)
		// mContext.getResources().getDimension(R.dimen.icon_grid_height));

		// imageView.setLayoutParams(lp);

		// imageView.setPadding(0, 0, 0, 0);
		// imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//		imageView.setPadding(0, padding, 0, padding);
		// imageView.setScaleType(ImageView.ScaleType.CENTER);
		// imageView.setBackgroundColor(0x000000);
		// imageView.setBackgroundColor(0x999999);
		// imageView.setBackgroundColor(0xffffff);
		return imageView;
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

	public void setItemPadding(int padding) {
		this.padding = padding;
	}

	public void setItemScale(int scale) {
//		this.scale = scale;
	}
	
	public void setItemScale(int scaleX,int scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
}
