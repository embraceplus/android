package com.embraceplus.app.fragment.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.embraceplus.app.R;
import com.embraceplus.database.DefinedThemeDB;
import com.embraceplus.database.UtilitiesDB;
import com.embraceplus.utils.Item;

public class ThemeSelectionGridViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private final Context context;
	private List<Item> itemLists;
	private List<Item> wholeItemLists;
	private List<ThemeSelectionGridViewAdapter> adapterList;
	private List<Holder> holderList = new ArrayList<Holder>();
	private List<ImageView> delViewList = new ArrayList<ImageView>();
	int position;
	int page;
	public static final int PAGE_SIZE = 3;
	Item item;

	public ThemeSelectionGridViewAdapter(Context context, List<Item> itemList, int page) {
		this.itemLists = itemList;
		wholeItemLists = itemList;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		itemLists = new ArrayList<Item>();
		this.page = page;
		int i = page * PAGE_SIZE;
		int end = i + PAGE_SIZE;
		while ((i < itemList.size()) && (i < end)) {
			itemLists.add(itemList.get(i));
			i++;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemLists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return itemLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.position = position;
		item = itemLists.get(position);

		Holder mHolder = null;
		if (null == convertView) {
			mHolder = new Holder();
			// LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.choose_style_gridview_item, null);
			mHolder.image_gv_item = (ImageView) convertView.findViewById(R.id.theme_icon_image);
			mHolder.theme_text = (TextView) convertView.findViewById(R.id.theme_icon_text);
			mHolder.image_gv_item.setFocusable(false);
			mHolder.image_del_item = (ImageView) convertView.findViewById(R.id.delbutton);
			mHolder.image_del_item.setTag(mHolder);
			mHolder.image_del_item.setVisibility(View.INVISIBLE);
			holderList.add(mHolder);
			delViewList.add(mHolder.image_del_item);

			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}

		if (mHolder.theme_text.getText().toString().equals("Add theme")) {
			mHolder.image_del_item.setVisibility(View.INVISIBLE);
		} else {
			mHolder.image_del_item.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Holder mHolder = (Holder) v.getTag();

					delSelectedTheme(mHolder.theme_text.getText().toString());
					invisiableDel();
				}

			});
		}

		if (item.getContent() != 0) {
			mHolder.image_gv_item.setImageResource(item.getContent());
		} else {

			Bitmap bmpDefaultPic = null;
			// ImageView iv = (ImageView) contentView.findViewById(R.id.x);
			String filePath = "/embrace/themes/" + item.getTitle() + "/iconPic.png";
			filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + filePath;
			File f = new File(filePath);

			// bmpDefaultPic = BitmapFactory.decodeFile(filePath,null);
			try {
				bmpDefaultPic = BitmapFactory.decodeStream(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHolder.image_gv_item.setImageBitmap(bmpDefaultPic);
		}
		mHolder.theme_text.setText(item.getTitle());
		if (item.isSelected()) {
			RelativeLayout parentLayout = (RelativeLayout) mHolder.theme_text.getParent();
			View view = parentLayout.findViewById(R.id.view);
			view.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	protected void delSelectedTheme(String title) {
//		String currentTheme = DbBuilder.getInstant().getCurrentTheme();
		String currentTheme = UtilitiesDB.getInstance().getCurrentTheme();
		if (!currentTheme.equals(title)) {
//			DbBuilder.getInstant().delTheme(title);
			DefinedThemeDB.getInstance().delTheme(title);
			for (int i = 0; i < wholeItemLists.size(); i++) {
				Item item = wholeItemLists.get(i);
				if (item.getTitle().equals(title)) {
					wholeItemLists.remove(i);

				}
			}
			refreshGridView();
		} else {
			Toast.makeText(context, "The current selected theme does not allowed to be deleted", Toast.LENGTH_LONG).show();
		}

	}

	public void refreshGridView() {
		itemLists.clear();
		int i = page * PAGE_SIZE;
		int end = i + PAGE_SIZE;
		while ((i < wholeItemLists.size()) && (i < end)) {
			itemLists.add(wholeItemLists.get(i));
			i++;
		}

		int pageCount = (int) Math.ceil(wholeItemLists.size() / 3.0f);
		int nextPage = page + 1;
		if (nextPage < pageCount) {
			adapterList.get(nextPage).refreshGridView();
		}

		ThemeSelectionGridViewAdapter.this.notifyDataSetChanged();
	}

	/*private static class Holder {
		ImageView image_gv_item;
		TextView theme_text;
		ImageView image_del_item;
	}*/
	public List<ThemeSelectionGridViewAdapter> getAdapterList() {
		return adapterList;
	}

	public void setAdapterList(List<ThemeSelectionGridViewAdapter> adapterList) {
		this.adapterList = adapterList;
	}

	public List<Holder> getHolderList() {
		return holderList;
	}

	public void setHolderList(List<Holder> holderList) {
		this.holderList = holderList;
	}

	public void visiableDel() {
		// int pageCount = (int) Math.ceil(wholeItemLists.size() / 3.0f);
		if (page >= 3) {

			for (int i = 0; i < delViewList.size(); i++) {
				RelativeLayout rlayout = (RelativeLayout) delViewList.get(i).getParent();
				TextView txView = (TextView) rlayout.findViewById(R.id.theme_icon_text);
				if (txView.getText().toString().equals("Add theme")) {
					delViewList.get(i).setVisibility(View.GONE);
				} else {
					delViewList.get(i).setVisibility(View.VISIBLE);
				}

			}
		}

	}

	public void invisiableDel() {

		if (page >= 3) {

			for (int i = 0; i < delViewList.size(); i++) {
				RelativeLayout rlayout = (RelativeLayout) delViewList.get(i).getParent();
				TextView txView = (TextView) rlayout.findViewById(R.id.theme_icon_text);
				if (txView.getText().toString().equalsIgnoreCase("Add theme")) {
					delViewList.get(i - 1).setVisibility(View.GONE);

				}
			}
		}

	}

	public void invisiableAllDel() {

		if (page >= 3) {

			for (int i = 0; i < delViewList.size(); i++) {
				delViewList.get(i).setVisibility(View.GONE);
			}
		}

	}

}
