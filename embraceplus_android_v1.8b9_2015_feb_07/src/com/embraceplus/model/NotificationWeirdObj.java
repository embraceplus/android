package com.embraceplus.model;

import java.util.List;
import java.util.Map;

public class NotificationWeirdObj {
	Map<String,NotificationObj> notificationNames;
	List<String> notificatinoNameList;
	public Map<String, NotificationObj> getNotificationNames() {
		return notificationNames;
	}
	public void setNotificationNames(Map<String, NotificationObj> notificationNames) {
		this.notificationNames = notificationNames;
	}
	public List<String> getNotificatinoNameList() {
		return notificatinoNameList;
	}
	public void setNotificatinoNameList(List<String> notificatinoNameList) {
		this.notificatinoNameList = notificatinoNameList;
	}
	
}
