package com.embraceplus.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UtilitiesDB extends Database{

	private static UtilitiesDB utilitiesDB = new UtilitiesDB();
	
	public UtilitiesDB() {
		super();
	}

	public static UtilitiesDB getInstance() {
		if (utilitiesDB == null)
			utilitiesDB = new UtilitiesDB();
		return utilitiesDB;
	}

	static public void create(SQLiteDatabase db) {
		createAllNotificationSwitchDb(db);
		createInitFlagTable(db);
		createSeletedTheme(db);
	}

	static public void createAllNotificationSwitchDb(SQLiteDatabase db) {
		String sql = "create table notificationSwitchTable (id integer PRIMARY KEY autoincrement,validate integer );";
		db.execSQL(sql);
		String initSql = "insert into notificationSwitchTable (validate) values (1);";
		db.execSQL(initSql);
	}
	static public void createInitFlagTable(SQLiteDatabase db) {
		String sql = "create table initFlagTable (id integer PRIMARY KEY autoincrement,initField varchar(20) );";
		db.execSQL(sql);
	}

	static public void createSeletedTheme(SQLiteDatabase db) {
		String sql = "create table selectedTheme (themeName varchar(20) not null );";
		db.execSQL(sql);

		StringBuffer insertSql = new StringBuffer("insert into selectedTheme (themeName) values (");
		insertSql.append("'Business')");
		db.execSQL(insertSql.toString());
	}

	
	public void changeNotificationValidate(boolean status) {
		int validate = 1;
		if (status) {
			validate = 1;
		} else {
			validate = 0;
		}
		String sql = "update notificationSwitchTable set validate=" + validate;
		getWritableDatabase().execSQL(sql);
	}

	public boolean getNotificatioinValidate() {
		boolean result = false;
		String sql = "select validate from notificationSwitchTable";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		int value = -1;
		while (cur.moveToNext()) {
			value = cur.getInt(0);
		}
		if (value == 0) {
			result = false;
		} else {
			result = true;
		}
		cur.close();
		return result;
	}
	
	public void initSystem() {
		String sql = "insert into initFlagTable (initField) values ('inited');";
		getWritableDatabase().execSQL(sql);
	}

	public boolean isSystemInited() {
		String sql = "select * from initFlagTable ";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		int number = cur.getCount();
		boolean isInited = false;
		if (number > 0) {
			isInited = true;
		}
		cur.close();
		return isInited;
	}
	
	// 获取当前theme的名称
	public String getCurrentTheme() {
		String sql = "select * from selectedTheme";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		cur.moveToFirst();
		String themeName = cur.getString(0);
		cur.close();
		return themeName;
	}
	
	public void selectCurrentTheme(String themeName) {
		String sql = "update selectedTheme set themeName='" + themeName + "'";
		getWritableDatabase().execSQL(sql);
	}
}
