package com.embraceplus.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.embraceplus.app.R;

public class SkinSettingManager {

	private Activity mActivity;
	
	
	public SkinSettingManager(Activity activity) {
		this.mActivity = activity;	
		
	}
	
	/**
	 * ��ȡ��ǰ�����Ƥ�����
	 * 
	 * @return
	 */
	
	public void changeSkin(String  themeBackgroundName) {
		mActivity.getWindow().setBackgroundDrawable(null);
		if(themeBackgroundName.indexOf("bg_")!= -1)
		{
		try {
			mActivity.getWindow().setBackgroundDrawableResource(ResourceUtils.getInstance().getResourceId(themeBackgroundName));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		}else
		{
			String filePath = "/embrace/themes/"+themeBackgroundName+"/backgroundPic.png";
			filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ filePath;
			Drawable bgPic = Drawable.createFromPath(filePath);
			mActivity.getWindow().setBackgroundDrawable(bgPic);
		}
	
	}

	public void changeSkin(Bitmap bitMap) {
		Drawable bgPic = new BitmapDrawable(bitMap);
		mActivity.getWindow().setBackgroundDrawable(bgPic);
	}
	 
	public void changeSkin(int id) {
	
		switch (id) {
		case 0:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_business);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
			break;
		case 1:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_fashionista);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
		case 2:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_vampire);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
		case 3:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_clubber);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
		case 4:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_student);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
		case 5:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_magician);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
		case 6:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_adventurer);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
		case 7:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_athlete);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
		case 8:
			mActivity.getWindow().setBackgroundDrawable(null);
			try {
				mActivity.getWindow().setBackgroundDrawableResource(R.drawable.bg_entertainer);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			break;
			
			
		default:
			break;
		}
	}
		
}	
		
		
		

