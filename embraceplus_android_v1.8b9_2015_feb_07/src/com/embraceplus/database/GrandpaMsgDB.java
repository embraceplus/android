package com.embraceplus.database;

import org.eclipse.jdt.annotation.NonNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.GrandFatherStatus;
import com.embraceplus.utils.Optional;

public class GrandpaMsgDB extends Database {

	private static GrandpaMsgDB grandpaMsgDB = new GrandpaMsgDB();

	public GrandpaMsgDB() {
		super();
	}

	public static GrandpaMsgDB getInstance() {
		if (grandpaMsgDB == null)
			grandpaMsgDB = new GrandpaMsgDB();
		return grandpaMsgDB;
	}

	public static void create(SQLiteDatabase db) {
		createGrandFatherMsgTable(db);
		createGrandFatherStatusTable(db);
	}

	public static void createGrandFatherMsgTable(SQLiteDatabase db) {
		String sql = "create table GrandFatherMsgTable (id integer PRIMARY KEY autoincrement " + ", theme varchar(40)  " + ", C1 varchar(4)  " + ", C2 varchar(4) " + ", C3 varchar(4) "
				+ ", C4 varchar(4) " + ", fadeIn varchar(4) " + ", fadeOut varchar(4) " + ", flag varchar(4) " + ", hold varchar(4) " + ", loop varchar(4) " + ", pause varchar(4));";
		db.execSQL(sql);
	}
	public @NonNull
	Optional<EmbraceMsg> getGrandFatherMsg() {
		String theme = getCurrentTheme();
		String sql = "select theme,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause from GrandFatherMsgTable where " + "theme='" + theme + "'";

		Cursor cursor = getWritableDatabase().rawQuery(sql, null);
		int count = cursor.getCount();
		EmbraceMsg msg = null;
		while (cursor.moveToNext()) {
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
		// When something can be null, we should package it in a Optional to
		// make it very clear that it can be null
		return new Optional<EmbraceMsg>(msg);
	}
	
	public void updateGrandFatherMsg(EmbraceMsg msg) {
		String sql = "select * from GrandFatherMsgTable where theme='" + getCurrentTheme() + "'";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		if (cur.getCount() <= 0) {
			addGrandFatherMsg(msg);
		} else {
			modifyGrandFatherMsg(msg);
		}
		cur.close();
	}

	private String getCurrentTheme() {
//		assert(false);
//		return "test";//TODO test
		return UtilitiesDB.getInstance().getCurrentTheme();
	}

	public void addGrandFatherMsg(EmbraceMsg msg) {
		StringBuffer insertSql = new StringBuffer("insert into GrandFatherMsgTable (theme,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause) values (");
		String theme = getCurrentTheme();
		byte C1 = msg.getC1();
		byte C2 = msg.getC2();
		byte C3 = msg.getC3();
		byte C4 = msg.getC4();
		byte fadeIn = msg.getFadeIn();
		byte fadeOut = msg.getFadeOut();
		byte flag = msg.getFlag();
		;
		byte hold = msg.getHold();
		byte loop = msg.getLoop();
		byte pause = msg.getPause();
		insertSql.append("'" + theme + "',");
		insertSql.append("'" + C1 + "',");
		insertSql.append("'" + C2 + "',");
		insertSql.append("'" + C3 + "',");
		insertSql.append("'" + C4 + "',");
		insertSql.append("'" + fadeIn + "',");
		insertSql.append("'" + fadeOut + "',");
		insertSql.append("'" + flag + "',");
		insertSql.append("'" + hold + "',");
		insertSql.append("'" + loop + "',");
		insertSql.append("'" + pause + "'");

		insertSql.append(")");
		getWritableDatabase().execSQL(insertSql.toString());
	}

	public void modifyGrandFatherMsg(EmbraceMsg msg) {
		String theme = getCurrentTheme();
		byte C1 = msg.getC1();
		byte C2 = msg.getC2();
		byte C3 = msg.getC3();
		byte C4 = msg.getC4();
		byte fadeIn = msg.getFadeIn();
		byte fadeOut = msg.getFadeOut();
		byte flag = msg.getFlag();
		;
		byte hold = msg.getHold();
		byte loop = msg.getLoop();
		byte pause = msg.getPause();
		String sql = "update GrandFatherMsgTable set C1='" + C1 + "',C2='" + C2 + "',C3='" + C3 + "',C4='" + C4 + "',fadeIn='" + fadeIn + "',fadeOut='" + fadeOut + "',flag='" + flag + "',hold='"
				+ hold + "',loop='" + loop + "',pause='" + pause + "' where theme='" + theme + "'";

		getWritableDatabase().execSQL(sql);
	}
	

	static public void createGrandFatherStatusTable(SQLiteDatabase db) {
		String sql = "create table GrandFatherStatusTable (id integer PRIMARY KEY autoincrement " + ", status smallint  " + ", theme varchar(40)  " + ", type varchar(40) "
				+ ", count varchar(10) " + ", vibration smallint);";
		db.execSQL(sql);

	}

	private void modifyGrandFatherStatus(GrandFatherStatus status) {
		String sql = "update GrandFatherStatusTable set count='" + status.getCount() + "',vibration='" + status.getVibration() + "' ,status=" + status.getStatusInt() + "  where theme='"
				+ getCurrentTheme() + "' and type='" + status.getType() + "'";
		getWritableDatabase().execSQL(sql);
	}

	public @NonNull
	Optional<GrandFatherStatus> getGrandFatherStatus(String type) {
		String theme = getCurrentTheme();
		String sql = "select theme,type,count,vibration from GrandFatherStatusTable where theme='" + theme + "' and " + " type='" + type + "' and status=1";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		GrandFatherStatus status = null;
		while (cur.moveToNext()) {
			status = new GrandFatherStatus();
			String count = cur.getString(2);
			int vibration = cur.getInt(3);
			status.setCount(count);
			status.setVibration(String.valueOf(vibration));
			status.setStatus(true);
		}
		cur.close();
		return new Optional<GrandFatherStatus>(status);
	}
	

	public void updateGrandFatherStatus(GrandFatherStatus status) {
		String theme = getCurrentTheme();
		String sql = "select theme,type,count,vibration from GrandFatherStatusTable where theme='" + theme + "' and " + " type='" + status.getType() + "'";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		if (cur.getCount() <= 0) {
			addGrandFatherStatus(status);
		} else {
			modifyGrandFatherStatus(status);
		}
		cur.close();

	}
	
	private void addGrandFatherStatus(GrandFatherStatus status) {
		String theme = getCurrentTheme();
		String sql = "insert into GrandFatherStatusTable (theme,type,count,vibration,status) values ('" + theme + "','" + status.getType() + "','" + status.getCount() + "'," + status.getVibration()
				+ "," + status.getStatusInt() + ")";
		getWritableDatabase().execSQL(sql);
	}
	
	public void deleteGrandFatherStatus(){
		String sql = "delete from GrandFatherStatusTable";
		getWritableDatabase().execSQL(sql);
	}
}
