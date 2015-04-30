package com.embraceplus.model;

public class GrandFatherStatus {
//on/off
boolean status;
String theme;

//hourly/half hour
String type;



//hour/once
String count;

//  0:off/1:on
String vibration;
public String getTheme() {
	return theme;
}
public void setTheme(String theme) {
	this.theme = theme;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}

public String getCount() {
	return count;
}

public boolean getCountBoolean()
{
	boolean flag = false;
	if (count.equals("Once"))
	{
		flag = true;
	}else
	{
		flag = false;
	}
	return flag;
}

public void setCount(String count) {
	this.count = count;
}
public String getVibration() {
	return vibration;
}
public void setVibration(String vibration) {
	this.vibration = vibration;
}

public boolean getboolVibration()
{
	boolean flag = false;
	if (vibration.equals("0"))
	{
		flag = false;
	}else
	{
		flag = true;
	}
	return flag;
}
public boolean isStatus() {
	return status;
}
public void setStatus(boolean status) {
	this.status = status;
}

public int getStatusInt() {
	int flag = 0;;
	if (status)
	{
		flag = 1;
	}else
	{
		flag = 0;
	}
	return flag;
}
}
