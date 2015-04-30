package com.embraceplus.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.embraceplus.model.ThemeObj;
import com.embraceplus.utils.PreDefineUtil;

public class DefinedThemeDB extends Database {

	private static DefinedThemeDB definedThemeDB = new DefinedThemeDB();

	public DefinedThemeDB() {
		super();
	}

	public static DefinedThemeDB getInstance() {
		if (definedThemeDB == null)
			definedThemeDB = new DefinedThemeDB();
		return definedThemeDB;
	}

	static public void create(SQLiteDatabase db) {
		String sql = "create table DefinedTheme (themeName varchar(20) not null , themeOrder integer, themeIcon varchar(20),themeBackground varchar(20) );";
		db.execSQL(sql);

		HashMap<String, ThemeObj> preDefinedTheme = PreDefineUtil.getInstant().getPreDefineTheme();
		Iterator<String> ite = (Iterator<String>) preDefinedTheme.keySet().iterator();
		db.beginTransaction();
		while (ite.hasNext()) {
			String keyName = ite.next();
			String themeName = preDefinedTheme.get(keyName).getThemeName();
			String themeBackground = preDefinedTheme.get(keyName).getThemeBackground();
			StringBuffer insertSql = new StringBuffer("insert into DefinedTheme (themeName,themeOrder,themeIcon,themeBackground) values (");
			insertSql.append("'" + themeName + "'," + preDefinedTheme.get(keyName).getThemeOrder() + ",'" + preDefinedTheme.get(keyName).getThemeIcon() + "','" + themeBackground + "')");
			db.execSQL(insertSql.toString());
		}
		db.setTransactionSuccessful();
		db.endTransaction();

		// String selectSql = "select * from DefinedTheme";
		// Cursor cur = db.rawQuery(selectSql, null);
		// int count = cur.getCount();
		// cur.close();

	}

	public List<ThemeObj> getThemes() {
		String themeSql = "select themeName,themeIcon,themeBackground from DefinedTheme order by themeOrder asc";
		Cursor cur = getWritableDatabase().rawQuery(themeSql, null);
		List<ThemeObj> themeObjList = new ArrayList<ThemeObj>();
		while (cur.moveToNext()) {
			ThemeObj themeObj = new ThemeObj();
			String themeName = cur.getString(0);
			themeObj.setThemeName(themeName);
			themeObj.setThemeIcon(cur.getString(1));
			themeObj.setThemeBackground(cur.getString(2));
			themeObjList.add(themeObj);
		}
		cur.close();

		String currentTheme = UtilitiesDB.getInstance().getCurrentTheme();
		for (ThemeObj obj : themeObjList) {
			if (obj.getThemeName().equals(currentTheme)) {
				obj.setSelected(true);
				break;
			}
		}

		return themeObjList;
	}

	public void delTheme(String title) {
		String sql = "delete from DefinedTheme where  themeName='" + title + "'";
		getWritableDatabase().execSQL(sql);
	}

	public boolean isThemeExisted(String newThemeName) {
		String sql = "select * from DefinedTheme where themeName='" + newThemeName + "'";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		boolean themeExisted = false;
		if (cur.getCount() > 0) {
			themeExisted = true;
		} else {
			themeExisted = false;
		}
		cur.close();
		return themeExisted;
	}


	public void addNewDefinedTheme(SQLiteDatabase db,String themeName) {
		// add the theme item on
		Cursor countCur = db.rawQuery("select count(*) from DefinedTheme", null);
		countCur.moveToNext();
		int count = countCur.getInt(0) + 1;

		StringBuffer insertSql = new StringBuffer("insert into DefinedTheme (themeName,themeOrder,themeIcon,themeBackground) values (");
		insertSql.append("'" + themeName + "'," + count + ",'','')");
		db.execSQL(insertSql.toString());
		countCur.close();
	}
}
