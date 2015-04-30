package com.embraceplus.utils;

import com.embraceplus.ble.BluetoothLeService;

public class EmbraceBatteryUtil {

	public static String getEmbraceBatteryValue(int value) {
		String batteryValue = "";
		if (value>=400)
		 {
			 batteryValue = "100%";
		 }else if (value>=395&&value<=400)
		 {
			 batteryValue = "90%";
		 }else if (value>=390&&value<=400)
		 {
			 batteryValue = "80%";
		 }else if (value>=385&&value<=390)
		 {
			 batteryValue = "70%";
		 }else if (value>=380&&value<=390)
		 {
			 batteryValue = "60%";
		 }else if (value>=375&&value<=380)
		 {
			 batteryValue = "50%";
		 }else if (value>=370&&value<=375)
		 {
			 batteryValue = "40%";
		 }else if (value>=365&&value<=370)
		 {
			 batteryValue = "30%";
		 }else if (value>=360&&value<=365)
		 {
			 batteryValue = "20%";
		 }else if (value>=355&&value<=360)
		 {
			 batteryValue = "10%";
		 }else if (value>=350&&value<=355)
		 {
			 batteryValue = "3%";
		 }
		return batteryValue;
	}
 }

