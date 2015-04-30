package com.embraceplus.database;

import java.util.HashMap;
import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;

import com.embraceplus.model.NotificationObj;
import com.embraceplus.utils.PreDefineUtil;

public class ThemeMsgMappingDB extends Database{
	private static ThemeMsgMappingDB instance = new ThemeMsgMappingDB();

	public ThemeMsgMappingDB() {
		super();
	}

	public static ThemeMsgMappingDB getInstance() {
		if (instance == null)
			instance = new ThemeMsgMappingDB();
		return instance;
	}
	
	static public void create(SQLiteDatabase db) {

		String sql = "create table ThemeMsgMapping (id integer PRIMARY KEY autoincrement,theme varchar(30) " + ", msgName varchar(20));";
		db.execSQL(sql);

		HashMap<String, NotificationObj> preDefinedNotification = PreDefineUtil.getInstant().getPreDefineNotifications();
		Iterator<String> ite = (Iterator<String>) preDefinedNotification.keySet().iterator();
		db.beginTransaction();
		while (ite.hasNext()) {
			String keyName = ite.next();
			String msgName = preDefinedNotification.get(keyName).getMsgName();
			String theme = preDefinedNotification.get(keyName).getTheme();
			StringBuffer insertSql = new StringBuffer("insert into ThemeMsgMapping (theme,msgName) values (");
			insertSql.append("'" + theme + "',");
			insertSql.append("'" + msgName + "')");
			db.execSQL(insertSql.toString());
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void delCreatedMsg(String delMsgName) {
		StringBuffer delThemeMsgMappingSql = new StringBuffer("delete from ThemeMsgMapping where msgName=");
		delThemeMsgMappingSql.append("'" + delMsgName + "'");
		getWritableDatabase().execSQL(delThemeMsgMappingSql.toString());
	}
	
	public void updateMapping(String msgName, String oldMsgName) {
		String theme = UtilitiesDB.getInstance().getCurrentTheme();
		StringBuffer updateThemeMsgMappingSql = new StringBuffer("update ThemeMsgMapping set msgName= '" + msgName + "' where msgName='");
		updateThemeMsgMappingSql.append(oldMsgName + "' and theme='" + theme + "'");
		getWritableDatabase().execSQL(updateThemeMsgMappingSql.toString());
	}
}
