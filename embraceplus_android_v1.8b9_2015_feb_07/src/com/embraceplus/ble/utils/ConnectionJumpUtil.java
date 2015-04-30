package com.embraceplus.ble.utils;

public class ConnectionJumpUtil {
	private static ConnectionJumpUtil util = new ConnectionJumpUtil();
	private boolean jumpFromChooseStyleFragment = false;
	private boolean jumpFromNotificationFragment = false;
	
	private ConnectionJumpUtil()
	{
		
	}
	public static ConnectionJumpUtil getInstant()
	{
		return util;
	}
	public boolean isJumpFromChooseStyleFragment() {
		return jumpFromChooseStyleFragment;
	}
	public void setJumpFromChooseStyleFragment(boolean jumpFromChooseStyleFragment) {
		this.jumpFromChooseStyleFragment = jumpFromChooseStyleFragment;
	}
	public boolean isJumpFromNotificationFragment() {
		return jumpFromNotificationFragment;
	}
	public void setJumpFromNotificationFragment(boolean jumpFromNotificationFragment) {
		this.jumpFromNotificationFragment = jumpFromNotificationFragment;
	}

}
