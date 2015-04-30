package com.embraceplus.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.embraceplus.ble.utils.Constants;
import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.EmbraceMsgWithEnabled;
import com.embraceplus.model.ExCommandObj;
import com.embraceplus.model.NotificationObj;
import com.embraceplus.model.NotificationSelectedObj;
import com.embraceplus.model.NotificationWeirdObj;
import com.embraceplus.utils.Optional;
import com.embraceplus.utils.PreDefineUtil;

public class NotificationCommandMappingDB extends Database {
	private static NotificationCommandMappingDB instance;// = new NotificationCommandMappingDB();
	private SQLiteDatabase db;

	public NotificationCommandMappingDB() {
		super();
		db = getWritableDatabase();
	}

	public static NotificationCommandMappingDB getInstance() {
		if (instance == null)
			instance = new NotificationCommandMappingDB();
		return instance;
	}

//	static public void createNotificationCommandMappingDb(SQLiteDatabase db) {
	static public void testCreate(SQLiteDatabase db) {
	}
	
	static public void create(SQLiteDatabase db) {
		String sql = "create table NotificationCommandMapping (id integer PRIMARY KEY autoincrement,notificationName varchar(20) "
				+ ", msgName varchar(20) ,enabled smallint,pkg varchar(100),theme varchar(30));";
		db.execSQL(sql);

		HashMap<String, NotificationObj> preDefinedNotification = PreDefineUtil.getInstant().getPreDefineNotifications();
		Iterator<String> ite = (Iterator<String>) preDefinedNotification.keySet().iterator();
		db.beginTransaction();
		while (ite.hasNext()) {
			String keyName = ite.next();
			String notificationName = preDefinedNotification.get(keyName).getNotificationName();
			String msgName = preDefinedNotification.get(keyName).getMsgName();
			String enabled = preDefinedNotification.get(keyName).getEnabled();
			String pkg = preDefinedNotification.get(keyName).getPkg();
			String theme = preDefinedNotification.get(keyName).getTheme();
			StringBuffer insertSql = new StringBuffer("insert into NotificationCommandMapping (notificationName,msgName,enabled,theme,pkg) values (");
			insertSql.append("'" + notificationName + "',");
			insertSql.append("'" + msgName + "',");
			insertSql.append("" + enabled + ",");
			insertSql.append("'" + theme + "',");
			insertSql.append("'" + pkg + "')");
			db.execSQL(insertSql.toString());
		}
		db.setTransactionSuccessful();
		db.endTransaction();

		// String selectSql = "select * from NotificationCommandMapping";
		// Cursor cur = db.rawQuery(selectSql, null);
		// int count = cur.getCount();
		// cur.close();

	}

	// 用于增加一个新的notification，如facebook，
	// qq等，被增加的notification必须已存在于NotificationDefinition文件中。
	public void addNewNotificationOnCurrentTheme(String notificationName) {
		String insertSql = "insert into NotificationCommandMapping (notificationName,msgName,enabled,pkg,theme) values (?,?,1,?,?)";
		String getCurrentThemeSql = "select * from selectedTheme";
		Cursor themeCur = db.rawQuery(getCurrentThemeSql, null);
		themeCur.moveToNext();
		String currentTheme = themeCur.getString(0);
		themeCur.close();

		if (notificationName.equalsIgnoreCase("Calls")) {
			/*
			db.execSQL(insertSql, new Object[]{"Incoming Call","Chat","",currentTheme});
			db.execSQL(insertSql, new Object[]{"Unknown  Call","Chat","",currentTheme});*/

			db.execSQL(insertSql, new Object[] { Constants.notification_type_IncomingCall, "Chat", "", currentTheme });
			db.execSQL(insertSql, new Object[] { Constants.notification_type_UnknownCall, "Chat", "", currentTheme });
		} else if (notificationName.equalsIgnoreCase("Clock")) {
			// String insertSql =
			// "insert into NotificationCommandMapping (notificationName,msgName,enabled,pkg,theme) values (?,?,1,?,?)";

			/* db.execSQL(insertSql, new Object[]{"Alarm","Chat","",currentTheme});
			 db.execSQL(insertSql, new Object[]{"Timer","Chat","",currentTheme});
			 db.execSQL(insertSql, new Object[]{"Grandpa Clock","Chat","",currentTheme});*/
			db.execSQL(insertSql, new Object[] { Constants.notification_type_ALARM, "Chat", "", currentTheme });
			db.execSQL(insertSql, new Object[] { Constants.notification_type_TIMER, "Chat", "", currentTheme });
			db.execSQL(insertSql, new Object[] { Constants.notification_type_GRANPACLOCK, "Chat", "", currentTheme });
		} else {
			String getNotificationDetailSql = "select notificationName,notificationPkg from NotificationDefinition where notificationName='" + notificationName + "'";

			Cursor notifiCur = db.rawQuery(getNotificationDetailSql, null);
			notifiCur.moveToNext();

			String pkg = notifiCur.getString(1);
			notifiCur.close();

			// String insertSql =
			// "insert into NotificationCommandMapping (notificationName,msgName,enabled,pkg,theme) values (?,?,1,?,?)";

			db.execSQL(insertSql, new Object[] { notificationName, "Chat", pkg, currentTheme });
		}
	}

	// 用于增加一个用户自定义的theme
	public void addNewTheme(String themeName) {
		String selectedCurrentThemeNotificationCommandMappingInfo = "select notificationName,msgName,pkg from NotificationCommandMapping,"
				+ "selectedTheme where NotificationCommandMapping.theme=selectedTheme.themeName";
		String insertNewThemeNotificationCommandMappingInfo = "insert into NotificationCommandMapping (notificationName,msgName,enabled,pkg,theme) values (?,?,1,?,'" + themeName + "')";

		// String newThemeMsg =
		// "select msgName from PreDefineCommand where msgName not in (select DISTINCT msgName from NotificationCommandMapping where theme = '"+themeName+"')";
		String selectedCurrentThemeMsgMappingInfo = "select msgName from ThemeMsgMapping,selectedTheme  where theme =selectedTheme.themeName";
		// String insertNewThemeMsgMappingInfo="insert into ";fd

		Cursor cursor = db.rawQuery(selectedCurrentThemeNotificationCommandMappingInfo, null);
		db.beginTransaction();
		// cursor.moveToFirst();
		while (cursor.moveToNext()) {
			String notificationName = cursor.getString(0);
			String msgName = cursor.getString(1);
			String pkg = cursor.getString(2);
			db.execSQL(insertNewThemeNotificationCommandMappingInfo, new Object[] { notificationName, msgName, pkg });
		}

		Cursor cursor2 = db.rawQuery(selectedCurrentThemeMsgMappingInfo, null);
		while (cursor2.moveToNext()) {
			String msgName = cursor2.getString(0);
			StringBuffer insertSql = new StringBuffer("insert into ThemeMsgMapping (theme,msgName) values (");
			insertSql.append("'" + themeName + "',");
			insertSql.append("'" + msgName + "')");
			db.execSQL(insertSql.toString());
			// db.execSQL(insertNewThemeNotificationCommandMappingInfo, new
			// Object[]{"",msgName,""});
		}

		// addNewDefinedTheme(db,themeName);

		DefinedThemeDB.getInstance().addNewDefinedTheme(db, themeName);

		// addNewCallsAndClockSilentStatusRecord(db, themeName);
		CallsAndClockSilentStatusDB.getInstance().addNewCallsAndClockSilentStatusRecord(db, themeName);

		db.setTransactionSuccessful();
		db.endTransaction();

		// String selectSql = "select * from CallsAndClockSilentStatusTable";
		// Cursor cur = db.rawQuery(selectSql, null);
		// int count1 = cur.getCount();
		// System.out.println("this is the testing....");
		cursor.close();
		cursor2.close();
		// cur.close();
	}

	// 用户在当前theme中为某一个notification选择一个command
	public void selectCommandForSelectedNotificationOnCurrentTheme(String commandName, String notificationName) {
		String checkIfNotificationExisted = "select * from NotificationCommandMapping where NotificationCommandMapping.theme=? and notificationName=?";
		// String updateCurrentThmeNotificationSql =
		// "update NotificationCommandMapping set notificationName=''   where NotificationCommandMapping.theme=? and notificationName=?";
		String pkgSql = "select notificationPkg from NotificationDefinition where notificationName='" + notificationName + "'";
		String updateNewNotificationSql = "update NotificationCommandMapping set pkg=?,msgName=?  where NotificationCommandMapping.theme=? and notificationName=?";
		// String insertNewNotificationSql = "";
		String currentTheme = UtilitiesDB.getInstance().getCurrentTheme();
		Cursor checkCursor = db.rawQuery(checkIfNotificationExisted, new String[] { currentTheme, notificationName });
		int notificationCount = checkCursor.getCount();
		checkCursor.close();
		Cursor pkg = db.rawQuery(pkgSql, null);
		String packageStr = "";
		while (pkg.moveToNext()) {
			packageStr = pkg.getString(0);
		}
		pkg.close();
		if (notificationCount > 0) {

			// db.execSQL(updateCurrentThmeNotificationSql, new
			// Object[]{currentTheme,notificationName});
			db.execSQL(updateNewNotificationSql, new Object[] { packageStr, commandName, currentTheme, notificationName });
		} else if (notificationCount == 0) {
			StringBuffer insertSql = new StringBuffer("insert into NotificationCommandMapping (notificationName,msgName,enabled,theme,pkg) values (");
			insertSql.append("'" + notificationName + "',");
			insertSql.append("'" + commandName + "',");
			insertSql.append("" + 1 + ",");
			insertSql.append("'" + currentTheme + "',");
			insertSql.append("'" + packageStr + "')");
			db.execSQL(insertSql.toString());
		}
		// test if a notification not existed in the NotificationCommandMapping,
		// then add a new record...DbBuilder....
	}

	public EmbraceMsg getThemeFirstCommand(String themeName) {
		String sql = "select NotificationCommandMapping.msgName,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause from NotificationCommandMapping,PreDefineCommand"
				+ " where NotificationCommandMapping.msgName=PreDefineCommand.msgName and NotificationCommandMapping.theme='" + themeName + "'";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
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
		EmbraceMsg msg = new EmbraceMsg();
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
		cursor.close();
		return msg;
	}

	public List<ExCommandObj> getExCommandListByByCurTheme(String notificationName) {
		String sql = "select distinct ThemeMsgMapping.msgName,msgIcon from ThemeMsgMapping,selectedTheme,PreDefineCommand where selectedTheme.themeName=ThemeMsgMapping.theme"
				+ " and PreDefineCommand.msgName=ThemeMsgMapping.msgName";
		String selectedNotificationNameSql = "select msgName from NotificationCommandMapping,selectedTheme " + " where selectedTheme.themeName=NotificationCommandMapping.theme and notificationName='"
				+ notificationName + "'";
		Cursor cur = db.rawQuery(sql, null);
		List<ExCommandObj> msgNames = new ArrayList<ExCommandObj>();
		while (cur.moveToNext()) {
			ExCommandObj commandObj = new ExCommandObj();
			commandObj.setExCommandName(cur.getString(0));
			commandObj.setSelected(false);
			commandObj.setMsgIcon(cur.getString(1));
			msgNames.add(commandObj);
		}
		cur.close();

		Cursor selectCur = db.rawQuery(selectedNotificationNameSql, null);
		String selectedMsgName = "";
		while (selectCur.moveToNext()) {
			selectedMsgName = selectCur.getString(0);
		}
		for (ExCommandObj commandObj : msgNames) {
			if (commandObj.getExCommandName().equals(selectedMsgName)) {
				commandObj.setSelected(true);
			}
		}

		selectCur.close();
		return msgNames;
	}

	public EmbraceMsg getThemeFirstCommandOnCurrentTheme() {
		String sql = "select NotificationCommandMapping.msgName,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause from NotificationCommandMapping,PreDefineCommand,selectedTheme"
				+ " where NotificationCommandMapping.msgName=PreDefineCommand.msgName and NotificationCommandMapping.theme=selectedTheme.themeName";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		String msgName = cursor.getString(0);
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
		EmbraceMsg msg = new EmbraceMsg();
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
		cursor.close();
		return msg;
	}

	public void removeNotificationOnCurrentTheme(String notificationName) {
		StringBuffer notificationNames = new StringBuffer();
		String currentTheme = UtilitiesDB.getInstance().getCurrentTheme();
		if (notificationName.equals("Calls")) {
			// notificationNames =
			// notificationNames.append("'Incoming Call','Unknown  Call'");
			notificationNames = notificationNames.append("'" + Constants.notification_type_IncomingCall + "','" + Constants.notification_type_UnknownCall + "'");
			String call_sql = "select notificationName from NotificationCommandMapping where notificationName like 'Call_%' and theme='" + currentTheme + "'";
			Cursor cur = db.rawQuery(call_sql, null);
			while (cur.moveToNext()) {
				String tmpNotiName = cur.getString(0);
				notificationNames.append(",'" + tmpNotiName + "'");

			}
			cur.close();
		} else if (notificationName.equalsIgnoreCase("Clock")) {
			// notificationNames =
			// notificationNames.append("'Alarm','Timer','Grandpa Clock'");
			notificationNames = notificationNames.append("'" + Constants.notification_type_ALARM + "','" + Constants.notification_type_Timer + "','" + Constants.notification_type_GRANPACLOCK + "'");
		} else {
			notificationNames = notificationNames.append("'" + notificationName + "'");
		}
		// String sql =
		// "update NotificationCommandMapping set notificationName='' where notificationName in ("+notificationNames+") and theme='"+currentTheme+"'";
		/*String querysql = "select notificationName from NotificationCommandMapping where notificationName in ("+notificationNames+") and theme='"+currentTheme+"'";
		Cursor cur = db.rawQuery(querysql,null);
		while( cur.moveToNext() )
		{
			String queryNotificationName = cur.getString(0);
			System.out.println(queryNotificationName);
			System.out.println("xxxxx");
		}*/

		String sql = "delete from NotificationCommandMapping where notificationName in (" + notificationNames + ") and theme='" + currentTheme + "'";
		db.execSQL(sql);
	}

	public List<NotificationSelectedObj> getAllNotifications() {
		List<NotificationSelectedObj> notificationList = new ArrayList<NotificationSelectedObj>();
		String sql = "select notificationName from NotificationDefinition ";
		HashMap<String, NotificationSelectedObj> notificationMap = new HashMap<String, NotificationSelectedObj>();
		Cursor cur = db.rawQuery(sql, null);
		while (cur.moveToNext()) {
			NotificationSelectedObj obj = new NotificationSelectedObj();
			String notificationName = cur.getString(0);
			if (notificationName.equalsIgnoreCase("Incoming Call") || notificationName.equalsIgnoreCase("Unknown Call")) {
				notificationName = "Calls";
				if (notificationMap.containsKey(notificationName)) {
					continue;
				}
				obj.setNotificationName(notificationName);
			} else if (notificationName.equalsIgnoreCase("Alarm") || notificationName.equalsIgnoreCase("Timer") || notificationName.equalsIgnoreCase("Grandpa Clock")) {
				notificationName = "Clock";
				if (notificationMap.containsKey(notificationName)) {
					continue;
				}
				obj.setNotificationName(notificationName);
			} else {
				obj.setNotificationName(notificationName);
			}

			notificationList.add(obj);
			notificationMap.put(notificationName, obj);
		}
		cur.close();
		String selectedSql = "select notificationName from NotificationCommandMapping,selectedTheme where NotificationCommandMapping.theme=selectedTheme.themeName	";
		Cursor noCur = db.rawQuery(selectedSql, null);
		while (noCur.moveToNext()) {
			String notificationName = noCur.getString(0);

			if (notificationName.equalsIgnoreCase("Incoming Call") || notificationName.equalsIgnoreCase("Unknown Call")) {
				notificationName = "Calls";

			} else if (notificationName.equalsIgnoreCase("Alarm") || notificationName.equalsIgnoreCase("Timer") || notificationName.equalsIgnoreCase("Grandpa Clock")) {
				notificationName = "Clock";

			}

			if (notificationMap.get(notificationName) != null) {
				// notificationMap.get(notificationName).setSelected(true);
				notificationList.remove(notificationMap.get(notificationName));
			}
		}
		noCur.close();
		return notificationList;
	}

	public void addNewCallNotificatioin(String name) {
		String currentTheme = UtilitiesDB.getInstance().getCurrentTheme();
		String insertNewThemeNotificationCommandMappingInfo = "insert into NotificationCommandMapping (notificationName,msgName,enabled,pkg,theme) values (?,?,1,'','" + currentTheme + "')";
		db.execSQL(insertNewThemeNotificationCommandMappingInfo, new Object[] { "Call_" + name, "Chat" });

	}

	public NotificationWeirdObj getNotificationsOnCurrentTheme() {
		String sql = "select distinct notificationName,enabled,NotificationCommandMapping.theme from NotificationCommandMapping,selectedTheme where NotificationCommandMapping.theme=selectedTheme.themeName and notificationName not in ('') and notificationName not like 'Call_%'";
		Cursor cur = db.rawQuery(sql, null);
		List<String> notificatinoNameList = new ArrayList<String>();
		Map<String, NotificationObj> notificationNames = new HashMap<String, NotificationObj>();
		while (cur.moveToNext()) {
			NotificationObj notiObj = new NotificationObj();
			notiObj.setMsgName(cur.getString(0));
			notiObj.setEnabled(String.valueOf(cur.getInt(1)));

			// notificationNames.add(notiObj);
			notificationNames.put(cur.getString(0), notiObj);
			notificatinoNameList.add(cur.getString(0));
			System.out.println(cur.getString(2));
		}
		cur.close();
		NotificationWeirdObj obj = new NotificationWeirdObj();
		obj.setNotificatinoNameList(notificatinoNameList);
		obj.setNotificationNames(notificationNames);
		return obj;
	}

	public NotificationWeirdObj getCallNotifications() {
		List<String> notificatinoNameList = new ArrayList<String>();
		Map<String, NotificationObj> notificationNames = new HashMap<String, NotificationObj>();
		// String definedCallSql =
		// "select notificationName,enabled from NotificationCommandMapping where theme='"+getCurrentTheme()+"' and notificationName in ('Incoming Call','Unknown  Call')";
		String currentTheme = UtilitiesDB.getInstance().getCurrentTheme();
		String definedCallSql = "select notificationName,enabled from NotificationCommandMapping where theme='" + currentTheme + "' and notificationName in ('"
				+ Constants.notification_type_IncomingCall + "','" + Constants.notification_type_UnknownCall + "')";
		// String sql =
		// "select notificationName,enabled from NotificationCommandMapping where theme='"+getCurrentTheme()+"' and (notificationName like 'Call_%' or notificationName in ('Incoming Call','Unknown Call'))";
		String sql = "select notificationName,enabled from NotificationCommandMapping where theme='" + currentTheme + "' and notificationName like 'Call_%'";
		Cursor curDefined = db.rawQuery(definedCallSql, null);
		while (curDefined.moveToNext()) {

			NotificationObj notiObj = new NotificationObj();
			String displayName = curDefined.getString(0);

			notiObj.setMsgName(displayName);
			notiObj.setEnabled(String.valueOf(curDefined.getInt(1)));

			notificationNames.put(displayName, notiObj);
			notificatinoNameList.add(displayName);

		}
		curDefined.close();

		Cursor cur = db.rawQuery(sql, null);

		while (cur.moveToNext()) {
			NotificationObj notiObj = new NotificationObj();
			String displayName = cur.getString(0);

			displayName = displayName.substring("Call_".length(), displayName.length());

			notiObj.setMsgName(displayName);
			notiObj.setEnabled(String.valueOf(cur.getInt(1)));

			// notificationNames.add(notiObj);
			notificationNames.put(displayName, notiObj);
			notificatinoNameList.add(displayName);
		}
		cur.close();
		NotificationWeirdObj obj = new NotificationWeirdObj();
		obj.setNotificatinoNameList(notificatinoNameList);
		obj.setNotificationNames(notificationNames);
		return obj;

	}

	public NotificationWeirdObj getClockNotifications() {
		List<String> notificatinoNameList = new ArrayList<String>();
		Map<String, NotificationObj> notificationNames = new HashMap<String, NotificationObj>();
		// String definedCallSql =
		// "select notificationName,enabled from NotificationCommandMapping where theme='"+getCurrentTheme()+"' and notificationName in ('Alarm','Timer','Grandpa Clock')";
		String currentTheme = UtilitiesDB.getInstance().getCurrentTheme();
		String definedCallSql = "select notificationName,enabled from NotificationCommandMapping where theme='" + currentTheme + "' and notificationName in ('" + Constants.notification_type_ALARM
				+ "','" + Constants.notification_type_Timer + "','" + Constants.notification_type_GRANPACLOCK + "')";
		// String sql =
		// "select notificationName,enabled from NotificationCommandMapping where theme='"+getCurrentTheme()+"' and (notificationName like 'Call_%' or notificationName in ('Incoming Call','Unknown Call'))";
		// String sql =
		// "select notificationName,enabled from NotificationCommandMapping where theme='"+getCurrentTheme()+"' and notificationName like 'Call_%'";
		Cursor curDefined = db.rawQuery(definedCallSql, null);
		while (curDefined.moveToNext()) {

			NotificationObj notiObj = new NotificationObj();
			String displayName = curDefined.getString(0);

			notiObj.setMsgName(displayName);
			notiObj.setEnabled(String.valueOf(curDefined.getInt(1)));

			notificationNames.put(displayName, notiObj);
			notificatinoNameList.add(displayName);

		}
		curDefined.close();

		NotificationWeirdObj obj = new NotificationWeirdObj();
		obj.setNotificatinoNameList(notificatinoNameList);
		obj.setNotificationNames(notificationNames);
		return obj;

	}



	// 用户为来电和短信找到对应的command
	public @NonNull
	Optional<EmbraceMsg> getExCommandByNotification(String notificationName) {
		
//		if (!getNotificatioinValidate()) {
		if (!UtilitiesDB.getInstance().getNotificatioinValidate()) {
			return new Optional<EmbraceMsg>();
		}

		// if the notification is incoming calls and unknown calls, then
		// "CALL_XXX", then check the table CallsAndClockSilentStatusTable to
		// see if the
		// calls notification was enabled.
		if (notificationName.equals(Constants.notification_type_IncomingCall) || notificationName.equals(Constants.notification_type_UnknownCall) || notificationName.startsWith("Call_")) {
//			String enable = getCurrentThemeCallsAndClockEnableStatus("Calls");
			String enable = CallsAndClockSilentStatusDB.getInstance().getCurrentThemeCallsAndClockEnableStatus("Calls");
			if (enable.equals("0")) {
				return new Optional<EmbraceMsg>();
			}
		}

		if (notificationName.equals(Constants.notification_type_ALARM) || notificationName.equals(Constants.notification_type_Timer)
				|| notificationName.equals(Constants.notification_type_GRANPACLOCK)) {
//			String enable = getCurrentThemeCallsAndClockEnableStatus("Clock");
			String enable = CallsAndClockSilentStatusDB.getInstance().getCurrentThemeCallsAndClockEnableStatus("Clock");
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

	public @NonNull
	Optional<EmbraceMsgWithEnabled> getExCommandByIncomingUserCall(String notificationName) {
//		if (!getNotificatioinValidate()) {
		if (!UtilitiesDB.getInstance().getNotificatioinValidate()) {
			return new Optional<EmbraceMsgWithEnabled>();
		}

		// if the notification is incoming calls and unknown calls, then
		// "CALL_XXX", then check the table CallsAndClockSilentStatusTable to
		// see if the
		// calls notification was enabled.
		if (notificationName.startsWith("Call_")) {
//			String enable = getCurrentThemeCallsAndClockEnableStatus("Calls");
			String enable = CallsAndClockSilentStatusDB.getInstance().getCurrentThemeCallsAndClockEnableStatus("Calls");
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
	}
}
