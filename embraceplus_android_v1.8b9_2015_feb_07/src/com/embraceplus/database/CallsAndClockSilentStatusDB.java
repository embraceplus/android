package com.embraceplus.database;

import java.util.HashMap;
import java.util.Iterator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.embraceplus.model.ThemeObj;
import com.embraceplus.utils.PreDefineUtil;

public class CallsAndClockSilentStatusDB extends Database{

	private static CallsAndClockSilentStatusDB callsAndClockSilentStatusDB = new CallsAndClockSilentStatusDB();

	public CallsAndClockSilentStatusDB() {
		super();
	}

	public static CallsAndClockSilentStatusDB getInstance() {
		if (callsAndClockSilentStatusDB == null)
			callsAndClockSilentStatusDB = new CallsAndClockSilentStatusDB();
		return callsAndClockSilentStatusDB;
	}

	static public void create(SQLiteDatabase db) {

		String sql = "create table CallsAndClockSilentStatusTable (id integer PRIMARY KEY autoincrement,notificationName varchar(20) " + " ,enabled smallint,theme varchar(30));";
		db.execSQL(sql);

		HashMap<String, ThemeObj> preDefinedTheme = PreDefineUtil.getInstant().getPreDefineTheme();
		Iterator<String> ite = (Iterator<String>) preDefinedTheme.keySet().iterator();
		db.beginTransaction();

		while (ite.hasNext()) {
			String key = ite.next();
			String theme = preDefinedTheme.get(key).getThemeName();
			getInstance().addNewCallsAndClockSilentStatusRecord(db, theme);
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		String selectSql = "select notificationName,enabled,theme from CallsAndClockSilentStatusTable";
		Cursor cur = db.rawQuery(selectSql, null);
		int count = cur.getCount();
		while (cur.moveToNext()) {
			String notificationName = cur.getString(0);
			int enable = cur.getInt(1);
			String theme = cur.getString(2);
			System.out.println(theme + "-----" + notificationName + "---------" + enable);
		}
		cur.close();
		System.out.println("this is the testing....");

	}

	public void addNewCallsAndClockSilentStatusRecord(SQLiteDatabase db, String theme) {
		String notificationNameCalls = "Calls";
		String notificationNameClock = "Clock";
		String enabled = "1";
		StringBuffer insertSql = new StringBuffer("insert into CallsAndClockSilentStatusTable (notificationName,enabled,theme) values (");
		insertSql.append("'" + notificationNameCalls + "',");
		insertSql.append("" + enabled + ",");
		insertSql.append("'" + theme + "')");
		db.execSQL(insertSql.toString());

		StringBuffer insertSqlClock = new StringBuffer("insert into CallsAndClockSilentStatusTable (notificationName,enabled,theme) values (");
		insertSqlClock.append("'" + notificationNameClock + "',");
		insertSqlClock.append("" + enabled + ",");
		insertSqlClock.append("'" + theme + "')");
		db.execSQL(insertSqlClock.toString());
	}

	public void silentNotificationOnCurrentTheme(String notificationName, boolean silent) {
		StringBuffer notificationNames = new StringBuffer();
		String currentTheme = getCurrentTheme();

		// If silent is true, then it's not enabled
		int enabled = 0;
		if (silent == false)
			enabled = 1;

		if (notificationName.equals("Calls")) {

			String silentCallsSql = "update CallsAndClockSilentStatusTable set enabled=" + enabled + " where notificationName ='Calls' and theme='" + currentTheme + "'";
			;
			getWritableDatabase().execSQL(silentCallsSql);
		} else if (notificationName.equalsIgnoreCase("Clock")) {

			String silentCallsSql = "update CallsAndClockSilentStatusTable set enabled=" + enabled + " where notificationName ='Clock' and theme='" + currentTheme + "'";
			;
			getWritableDatabase().execSQL(silentCallsSql);
		} else {

			notificationNames = notificationNames.append("'" + notificationName + "'");

			String sql = "update NotificationCommandMapping set enabled=" + enabled + " where notificationName in (" + notificationNames + ") and theme='" + currentTheme + "'";

			getWritableDatabase().execSQL(sql);
		}

	}
	
	public String getCurrentThemeCallsAndClockEnableStatus(String notificationName) {
		String sql = "select enabled from CallsAndClockSilentStatusTable where notificationName='" + notificationName + "' and theme='" + getCurrentTheme() + "'";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);

		int enabled = 1;
		while (cur.moveToNext()) {
			enabled = cur.getInt(0);
		}
		cur.close();

		return String.valueOf(enabled);
	}

	private String getCurrentTheme() {
		// TODO test
//		assert(false);
//		return "test";
		return UtilitiesDB.getInstance().getCurrentTheme();
	}
}
