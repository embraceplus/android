package com.embraceplus.store;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.database.UtilitiesDB;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.EmbraceMsgWithEnabled;
import com.embraceplus.model.NotificationDefineObj;
import com.embraceplus.utils.Optional;
import com.embraceplus.utils.PreDefineUtil;

public class ExCommandManager {
	static private ExCommandManager exCommandManager = null;

	static public ExCommandManager getInstance() {
		if (null == exCommandManager) {
			exCommandManager = new ExCommandManager();
		}
		return exCommandManager;
	}

	/*
	public @NonNull
	Optional<EmbraceMsg> getExCommandByNotification(String notificationName) {
		if (!getNotificatioinValidate()) {
			return new Optional<EmbraceMsg>();
		}*/
	public @NonNull
	Optional<EmbraceMsg> getExCommandByNotification(String notificationName) {
		if (!UtilitiesDB.getInstance().getNotificatioinValidate()) {
			return new Optional<EmbraceMsg>();
		} else {
			// return
			// DbBuilder.getInstant().getExCommandByNotification(notificationName);
//			return null;//TODO:test
			return NotificationCommandMappingDB.getInstance().getExCommandByNotification(notificationName);
		}
	}

	public @NonNull
	Optional<EmbraceMsgWithEnabled> getExCommandByIncomingUserCall(String notificationName) {
		if (!UtilitiesDB.getInstance().getNotificatioinValidate()) {
			return new Optional<EmbraceMsgWithEnabled>();
		} else {
			// return
			// DbBuilder.getInstant().getExCommandByIncomingUserCall(notificationName);
//			return null;//TODO:test
			return NotificationCommandMappingDB.getInstance().getExCommandByIncomingUserCall(notificationName);
		}
	}

	// 根据收到的系统通知，如facebook，qq等返回对应的command对象
	public @NonNull
	Optional<EmbraceMsg> getExCommandByNotificationPkg(String pkgName) {
		// Let's Try to fetch the notification name associated with this package
		List<NotificationDefineObj> notifDefs = PreDefineUtil.getInstant().getNotificationDefineObjs();

		for (NotificationDefineObj n : notifDefs) {
			// Once we have the right package, we'll call the
			// getExCommandByNotification function
			// And find the right notification by name, rather than package
			if (n.getNotificationPkg().equals(pkgName))
				// return getExCommandByNotification(n.getNotificationName());
				return getExCommandByNotification(n.getNotificationName());
		}

		return new Optional<EmbraceMsg>();
	}
	
/*
	// 用户为来电和短信找到对应的command
	public @NonNull
	Optional<EmbraceMsg> getExCommandByNotification(String notificationName) {
		if (!getNotificatioinValidate()) {
			return new Optional<EmbraceMsg>();
		}

		// if the notification is incoming calls and unknown calls, then
		// "CALL_XXX", then check the table CallsAndClockSilentStatusTable to
		// see if the
		// calls notification was enabled.
		if (notificationName.equals(Constants.notification_type_IncomingCall) || notificationName.equals(Constants.notification_type_UnknownCall) || notificationName.startsWith("Call_")) {
			String enable = getCurrentThemeCallsAndClockEnableStatus("Calls");
			if (enable.equals("0")) {
				return new Optional<EmbraceMsg>();
			}
		}

		if (notificationName.equals(Constants.notification_type_ALARM) || notificationName.equals(Constants.notification_type_Timer)
				|| notificationName.equals(Constants.notification_type_GRANPACLOCK)) {
			String enable = getCurrentThemeCallsAndClockEnableStatus("Clock");
			if (enable.equals("0")) {
				return new Optional<EmbraceMsg>();
			}
		}

		String sql = "select NotificationCommandMapping.msgName,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause from NotificationCommandMapping,PreDefineCommand,selectedTheme where "
				+ "NotificationCommandMapping.msgName=PreDefineCommand.msgName and NotificationCommandMapping.notificationName='" + notificationName
				+ "' and NotificationCommandMapping.theme=selectedTheme.themeName and enabled=1";

		Cursor cursor = db.rawQuery(sql, null);

		EmbraceMsg msg = null;
		if (cursor != null) {
			// int count = cursor.getCount();
			while (cursor.moveToNext()) {
				String msgName = cursor.getString(1);
				String strC1 = cursor.getString(1);
				String strC2 = cursor.getString(2);
				String strC3 = cursor.getString(3);
				String strC4 = cursor.getString(4);
				String fadeIn = cursor.getString(5);
				String fadeOut = cursor.getString(6);
				String flag = cursor.getString(7);
				String hold = cursor.getString(8);
				String loop = cursor.getString(9);
				String pause = cursor.getString(10);
				msg = new EmbraceMsg();
				msg.setMsgName(msgName);

				msg.setC1((byte) Integer.parseInt(strC1));
				msg.setC2((byte) Integer.parseInt(strC2));
				msg.setC3((byte) Integer.parseInt(strC3));
				msg.setC4((byte) Integer.parseInt(strC4));
				msg.setFadeIn((byte) Integer.parseInt(fadeIn));
				msg.setFadeOut((byte) Integer.parseInt(fadeOut));
				msg.setFlag((byte) Integer.parseInt(flag));
				msg.setHold((byte) Integer.parseInt(hold));
				msg.setLoop((byte) Integer.parseInt(loop));
				msg.setPause((byte) Integer.parseInt(pause));
			}
			cursor.close();
		}
		// When something can be null, we should package it in a Optional to
		// make it very clear that it can be null
		return new Optional<EmbraceMsg>(msg);
	}
	*/
	
/*
	public @NonNull
	Optional<EmbraceMsgWithEnabled> getExCommandByIncomingUserCall(String notificationName) {
		if (!getNotificatioinValidate()) {
			return new Optional<EmbraceMsgWithEnabled>();
		}

		// if the notification is incoming calls and unknown calls, then
		// "CALL_XXX", then check the table CallsAndClockSilentStatusTable to
		// see if the
		// calls notification was enabled.
		if (notificationName.startsWith("Call_")) {
			String enable = getCurrentThemeCallsAndClockEnableStatus("Calls");
			if (enable.equals("0")) {
				return new Optional<EmbraceMsgWithEnabled>();
			}
		}

		String sql = "select NotificationCommandMapping.msgName,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause,enabled from NotificationCommandMapping,PreDefineCommand,selectedTheme where "
				+ "NotificationCommandMapping.msgName=PreDefineCommand.msgName and NotificationCommandMapping.notificationName='" + notificationName
				+ "' and NotificationCommandMapping.theme=selectedTheme.themeName ";

		Cursor cursor = db.rawQuery(sql, null);
		EmbraceMsgWithEnabled embraceMsgWithEnabled = null;
		EmbraceMsg msg = null;
		String enabledStr = "";
		while (cursor.moveToNext()) {
			String msgName = cursor.getString(1);
			String strC1 = cursor.getString(1);
			String strC2 = cursor.getString(2);
			String strC3 = cursor.getString(3);
			String strC4 = cursor.getString(4);
			String fadeIn = cursor.getString(5);
			String fadeOut = cursor.getString(6);
			String flag = cursor.getString(7);
			String hold = cursor.getString(8);
			String loop = cursor.getString(9);
			String pause = cursor.getString(10);
			int enabled = cursor.getInt(11);
			msg = new EmbraceMsg();
			msg.setMsgName(msgName);

			msg.setC1((byte) Integer.parseInt(strC1));
			msg.setC2((byte) Integer.parseInt(strC2));
			msg.setC3((byte) Integer.parseInt(strC3));
			msg.setC4((byte) Integer.parseInt(strC4));
			msg.setFadeIn((byte) Integer.parseInt(fadeIn));
			msg.setFadeOut((byte) Integer.parseInt(fadeOut));
			msg.setFlag((byte) Integer.parseInt(flag));
			msg.setHold((byte) Integer.parseInt(hold));
			msg.setLoop((byte) Integer.parseInt(loop));
			msg.setPause((byte) Integer.parseInt(pause));
			enabledStr = String.valueOf(enabled);
			embraceMsgWithEnabled = new EmbraceMsgWithEnabled();
			embraceMsgWithEnabled.setMsg(msg);
			embraceMsgWithEnabled.setEnabled(enabledStr);
		}
		cursor.close();

		// When something can be null, we should package it in a Optional to
		// make it very clear that it can be null
		return new Optional<EmbraceMsgWithEnabled>(embraceMsgWithEnabled);
	}*/
}
