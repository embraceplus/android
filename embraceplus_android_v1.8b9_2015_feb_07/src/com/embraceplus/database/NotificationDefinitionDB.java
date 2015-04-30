package com.embraceplus.database;

import java.util.Iterator;
import java.util.List;

import com.embraceplus.model.NotificationDefineObj;
import com.embraceplus.utils.PreDefineUtil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NotificationDefinitionDB extends Database {
	private static NotificationDefinitionDB instance = new NotificationDefinitionDB();

	public NotificationDefinitionDB() {
		super();
	}

	public static NotificationDefinitionDB getInstance() {
		if (instance == null)
			instance = new NotificationDefinitionDB();
		return instance;
	}

	static public void create(SQLiteDatabase db) {
		String sql = "create table NotificationDefinition (id integer PRIMARY KEY autoincrement " + ", notificationName varchar(40)  " + ", notificationPkg varchar(100)" + ", icon varchar(40)); ";

		db.execSQL(sql);

		db.beginTransaction();

		List<NotificationDefineObj> notificaList = PreDefineUtil.getInstant().getNotificationDefineObjs();
		Iterator<NotificationDefineObj> ite = notificaList.iterator();
		while (ite.hasNext()) {
			StringBuffer insertSql = new StringBuffer("insert into NotificationDefinition (notificationName,notificationPkg,icon) values (");
			NotificationDefineObj msg = ite.next();
			String notificationName = msg.getNotificationName();
			String notificationPkg = msg.getNotificationPkg();

			insertSql.append("'" + notificationName + "',");
			insertSql.append("'" + notificationPkg + "',");
			insertSql.append("'" + msg.getIcon() + "'");

			insertSql.append(")");
			db.execSQL(insertSql.toString());
		}
		db.setTransactionSuccessful();
		db.endTransaction();

		String selectSql = "select * from NotificationDefinition";
		Cursor cur = db.rawQuery(selectSql, null);
		cur.close();
	}

	public String getResourceNameByNotificatioinName(String notificationName) {
		String sql = "select icon from NotificationDefinition where notificationName = '" + notificationName + "'";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		String resourceName = "";
		while (cur.moveToNext()) {
			resourceName = cur.getString(0);
		}
		cur.close();
		return resourceName;
	}
}
