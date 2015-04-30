package com.embraceplus.utils;

import android.content.res.Resources;

public class ResourceUtils {
	private static ResourceUtils util = new ResourceUtils();
	private ResourceUtils()
	{
		
	}
	public static ResourceUtils getInstance()
	{
		
		return util;
	}
	
	public int getResourceId(String resourceName)
	{
		String pkg = "com.embraceplus.app";
		
		Resources res=ContextUtil.getInstance().getResources();
		int id = res.getIdentifier(resourceName,"drawable",pkg);
		return id;
	}
	
	

}
