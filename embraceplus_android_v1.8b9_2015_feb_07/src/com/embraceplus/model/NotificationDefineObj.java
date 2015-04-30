package com.embraceplus.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class NotificationDefineObj {
	private String notificationName;
	private String notificationPkg;
	private String icon;
	public String getNotificationName() {
		return notificationName;
	}
	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}
	public String getNotificationPkg() {
		return notificationPkg;
	}
	public void setNotificationPkg(String notificationPkg) {
		this.notificationPkg = notificationPkg;
	}
	
	public static void main(String argc[])
	{
		List<NotificationDefineObj> notificaList = new ArrayList<NotificationDefineObj>();
		NotificationDefineObj obj = new NotificationDefineObj();
		obj.setNotificationName("facebook");
		obj.setNotificationPkg("facebookxxxxx.xxxx.xxx");
		notificaList.add(obj);
		obj.setNotificationName("facebook2");
		obj.setNotificationPkg("facebookxxxxx.xxxx.xxx");
		notificaList.add(obj);
		obj.setNotificationName("facebook3");
		obj.setNotificationPkg("facebookxxxxx.xxxx.xxx");
		notificaList.add(obj);
		
		 java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<NotificationDefineObj>>() {  
	     }.getType(); 
	     Gson gson = new  Gson(); 
	     String beanListToJson = gson.toJson(obj,type); 
	     System.out.println(beanListToJson);
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

}
