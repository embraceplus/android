package com.embraceplus.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.embraceplus.utils.ContextUtil;

//public class Database extends SQLiteOpenHelper{
public class Database{
	private static final String DB_NAME = "embraceDb.db";
//	static private SQLiteDatabase sqLiteDatabase;
	static SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(ContextUtil.getInstance(), DB_NAME, null, 1) {
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
		//TODO: get sub class of Database add create method here automatically
		@Override
		public void onCreate(SQLiteDatabase db) {
			UtilitiesDB.create(db);			
			GrandpaMsgDB.create(db);
			CallsAndClockSilentStatusDB.create(db);
			DefinedThemeDB.create(db);
			PreDefineCommandDB.create(db);
			ThemeMsgMappingDB.create(db);
			NotificationCommandMappingDB.create(db);
			NotificationDefinitionDB.create(db);
		}
	};
	
	public Database() {
//		super(ContextUtil.getInstance(), DB_NAME, null, 1);
	}
	
	protected static SQLiteDatabase getWritableDatabase() {
//		return sqLiteDatabase;
		return sqLiteOpenHelper.getWritableDatabase();
	}
	
	
//	void createTables(SQLiteDatabase db){
//	}
//	
//	
//	void create(SQLiteDatabase db) {
//	}
}
