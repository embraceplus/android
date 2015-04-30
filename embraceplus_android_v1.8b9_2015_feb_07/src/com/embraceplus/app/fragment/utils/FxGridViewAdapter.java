package com.embraceplus.app.fragment.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.embraceplus.app.R;
import com.embraceplus.database.PreDefineCommandDB;
import com.embraceplus.database.ThemeMsgMappingDB;
import com.embraceplus.fragment.BaseFragmentWithFxGridView;
import com.embraceplus.fragment.FxMenuViewFragment;
import com.embraceplus.utils.Item;
import com.embraceplus.utils.SelectMsgWrap;

public class FxGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<Item> itemLists;
	private List<Item> wholeItemList;
	private List<FxGridViewAdapter> adapterList;
	private SelectMsgWrap selectMsgWrap;
	private boolean visiableDel = false;

	private List<ImageView> delViewList = new ArrayList<ImageView>();
	int page;
//	private FxMenuViewFragment fxMenuViewFragment;
	private BaseFragmentWithFxGridView parentFragment;
	
	private int padding;
	private int scaleX;
	private int scaleY;

	private int lineNum;
//	public static final int PAGE_SIZE = 6;

	public FxGridViewAdapter(Context pContext, List<Item> itemList, int page, int lineNum) {
		this.mContext = pContext;
		this.itemLists = itemList;
		this.page = page;
		wholeItemList = itemList;
		this.lineNum = lineNum;
		// mLists = new ArrayList<Integer>();
		itemLists = new ArrayList<Item>();
		int i = page * getItemCountPerPage();
		int end = i + getItemCountPerPage();
		while ((i < itemList.size()) && (i < end)) {
			itemLists.add(itemList.get(i));
			i++;
		}
	}

	@Override
	public int getCount() {

		return itemLists.size();
	}

	@Override
	public Object getItem(int position) {

		return itemLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Item item = itemLists.get(position);
		Holder mHolder = null;
		if (null == convertView) {
			mHolder = new Holder();

			LayoutInflater mInflater = LayoutInflater.from(mContext);
			convertView = mInflater.inflate(R.layout.gridview_item, null);
			convertView.setLayoutParams(new GridView.LayoutParams(scaleX, scaleY));
			convertView.setPadding(padding, padding, padding, padding);
			
			ImageView imageView = (ImageView) convertView.findViewById(R.id.icon_image);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(scaleX, scaleY);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			imageView.setLayoutParams(params);
			
//			LayoutInflater mInflater = LayoutInflater.from(mContext);
//			convertView = mInflater.inflate(R.layout.gridview_item, null);
			mHolder.icon_image = (ImageView) convertView.findViewById(R.id.icon_image);
			mHolder.icon_text = (TextView) convertView.findViewById(R.id.icon_text);
			mHolder.icon_image.setFocusable(false);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}


		mHolder.icon_image.setImageResource(item.getContent());
		mHolder.icon_text.setText(item.getTitle());

//		View tmpView = convertView.findViewById(R.id.view);
//		if (selectMsgWrap.getSelectedMsgName().equals(mHolder.icon_text.getText().toString())) {
//			tmpView.setVisibility(View.VISIBLE);
//		} else {
//			tmpView.setVisibility(View.INVISIBLE);
//		}

		String msgName = item.getTitle();
		ImageView delButton = (ImageView) convertView.findViewById(R.id.delbutton);

		if (visiableDel && !isPreDefinedMsg(msgName)) {
			delButton.setVisibility(View.VISIBLE);
		} else {
			delButton.setVisibility(View.INVISIBLE);
		}

		final String delMsgName = msgName;

		delButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				for (int i = 0; i < wholeItemList.size(); i++) {
					if (wholeItemList.get(i).getTitle().equals(delMsgName)) {
//						if (wholeItemList.get(i).isSelected()) {
//							Toast.makeText(mContext, "Could not delete the selected FX command", Toast.LENGTH_LONG).show();
//						} else {
//							ThemeMsgMappingDB.getInstance().delCreatedMsg(delMsgName);
//							PreDefineCommandDB.getInstance().delCreatedMsg(delMsgName);
//							
//							wholeItemList.remove(i);
//						}

						if (wholeItemList.get(i).isSelected()) {
							parentFragment.selectFirstItem();
						}
						ThemeMsgMappingDB.getInstance().delCreatedMsg(delMsgName);
						PreDefineCommandDB.getInstance().delCreatedMsg(delMsgName);						
						wholeItemList.remove(i);
						break;
					}
				}

				refreshMsgView();

			}

		});

		delViewList.add(delButton);

		if (item.isSelected()) {
			RelativeLayout rela = (RelativeLayout) mHolder.icon_text.getParent();
			rela.findViewById(R.id.view).setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private int getItemCountPerPage(){
		return 3 * lineNum;
	}
	private boolean isPreDefinedMsg(String msgName) {
		boolean isPredefinedMsg = false;
		String preDefinedMsgNames = "Chat,Meeting,Night fever,Discreet,Fabulous,Strobe,After work," + "Biohazard,Atomic,Bloodrush,Electrifying,Fugitive,Heartbeat,Holy,Hardcore,Nirvana,"
				+ "Outdoor,Prancing,Psychedelic,Punky,Rasta,Workout,Sweet life,Toxic";
		if (preDefinedMsgNames.indexOf(msgName) != -1) {
			isPredefinedMsg = true;
		}
		return isPredefinedMsg;
	}

	private static class Holder {
		// Button btn_gv_item;
		ImageView icon_image;
		TextView icon_text;
	}

	public void refreshMsgView() {
		itemLists.clear();

		int i = page * getItemCountPerPage();
		int end = i + getItemCountPerPage();
		while ((i < wholeItemList.size()) && (i < end)) {
			itemLists.add(wholeItemList.get(i));
			i++;
		}

		int pageCount = (int) Math.ceil(wholeItemList.size() / getItemCountPerPage());
		int nextPage = page + 1;
		if (nextPage == pageCount) {

		} else {
			if (nextPage < pageCount) {
				adapterList.get(nextPage).refreshMsgView();
			} else if (adapterList.size() > nextPage) {
				adapterList.get(nextPage).refreshMsgView();
			}
		}

		visiableDel = true;
		FxGridViewAdapter.this.notifyDataSetChanged();
		// visiableDel();
	}

	public List<FxGridViewAdapter> getAdapterList() {
		return adapterList;
	}

	public void setAdapterList(List<FxGridViewAdapter> adapterList) {
		this.adapterList = adapterList;
	}

	public void visiableDel() {
		visiableDel = true;
		for (int i = 0; i < delViewList.size(); i++) {
			RelativeLayout rlayout = (RelativeLayout) delViewList.get(i).getParent();
			TextView txView = (TextView) rlayout.findViewById(R.id.icon_text);
			/*if (txView.getText().toString().equals("Add theme"))
			{
				delViewList.get(i).setVisibility(View.GONE);
			}else
			{
				delViewList.get(i).setVisibility(View.VISIBLE);
			}*/
			if (!isPreDefinedMsg(txView.getText().toString())) {
				delViewList.get(i).setVisibility(View.VISIBLE);
			}
//			delViewList.get(i).setVisibility(View.VISIBLE);

		}

	}

	public void invisiableDel() {
		visiableDel = false;
		for (int i = 0; i < delViewList.size(); i++) {

			delViewList.get(i).setVisibility(View.GONE);

		}

	}

	public SelectMsgWrap getSelectMsgWrap() {
		return selectMsgWrap;
	}

	public void setSelectMsgWrap(SelectMsgWrap selectMsgWrap) {
		this.selectMsgWrap = selectMsgWrap;
	}

//	public void setFxMenuViewFragment(FxMenuViewFragment fxMenuViewFragment) {
//		// TODO Auto-generated method stub
//		this.fxMenuViewFragment = fxMenuViewFragment;
//	}

	public void setItemPadding(int padding) {
		this.padding = padding;
	}
	
	public void setItemScale(int scaleX,int scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public BaseFragmentWithFxGridView getParentFragment() {
		return parentFragment;
	}

	public void setParentFragment(BaseFragmentWithFxGridView parentFragment) {
		this.parentFragment = parentFragment;
	}
}
