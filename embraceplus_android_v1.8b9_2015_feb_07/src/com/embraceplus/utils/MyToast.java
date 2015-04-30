package com.embraceplus.utils;

import android.widget.Toast;

public class MyToast {
	static public void show(String text){
		Toast.makeText(ContextUtil.getInstance(), text, Toast.LENGTH_SHORT).show();
	}
	
	static public void debugShow(String text){
		Toast.makeText(ContextUtil.getInstance(), text, Toast.LENGTH_SHORT).show();
	}
}
