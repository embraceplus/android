package com.embraceplus.ble.utils;

public class Constants {
	
public static String DeviceName = "Embrace+";
//	public static String DeviceName = "iGard-W";
public static String service_uuid="0000ffc0-0000-1000-8000-00805f9b34fb";                                
//public static String service_uuid="ffc0";

public static String Battery_service_uuid= "0000180f-0000-1000-8000-00805f9b34fb"; 
public static String Battery_service_characteristics_uuid="00002a19-0000-1000-8000-00805f9b34fb"; 

public static String notify_characteristics_uuid1="0000ffc2-0000-1000-8000-00805f9b34fb"; 
public static String notify_characteristics_uuid2="0000ffc3-0000-1000-8000-00805f9b34fb"; 

public static String notify_uuid="00001524-1212-EFDE-1523-785FEABCD123";


public static String write_uuid_OS="0000ffc1-0000-1000-8000-00805f9b34fb";
//public static String write_uuid_OS="ffc1";


public static String write_uuid_effect="0000ffc4-0000-1000-8000-00805f9b34fb";
//public static String write_uuid_effect="ffc4";

public static String read_uuid_battery="0000ffc5-0000-1000-8000-00805f9b34fb";
//public static String write_uuid_effect="ffc4";

public static String descripter_uuid ="00002902-0000-1000-8000-00805f9b34fb";
public static int minBattery = 10;
public static int minPhoneBattery=15;
//public static int minPhoneBattery=64;
public static int getEmbraceBattery=1;

public static final int handler_msg_disconnect_server=2;
public static final int handler_msg_connected_server=3;

public static final int message_type_length=1;
/*public static final String notification_type_IncomingCall="Incoming Call";
public static final String notification_type_UnknownCall="Unknown  Call";*/
public static final String notification_type_IncomingCall="Incoming call";
public static final String notification_type_UnknownCall="Unknown call";
public static final String notification_type_TEXT="Text";
public static final String notification_type_CALENDAR="Calendar";
public static final String notification_type_BATTERYPHONE="Battery phone";
public static final String notification_type_OUTOFSERVICE="Phone out of range";
public static final String notification_type_Timer="Timer";
public static String notification_type_ALARM="Alarm";
public static String notification_type_TIMER="Timer";
public static String notification_type_GRANPACLOCK="Grandpa clock";
public static String notification_type_BATTERYEMBRACCE="Battery Embrace+";
public static final int BLEMinValue = -95; // @ME: RSSI Min Value

public static final String GRANDFATHER_TYPE_HOURLY="hourly";
public static final String GRANDFATHER_TYPE_HALF_HOUR="half hour";

 
}
