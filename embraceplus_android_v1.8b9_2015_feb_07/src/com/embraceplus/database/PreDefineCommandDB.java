package com.embraceplus.database;

import java.util.HashMap;
import java.util.Iterator;

import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.utils.PreDefineUtil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PreDefineCommandDB extends Database {

	private static PreDefineCommandDB instance = new PreDefineCommandDB();

	public PreDefineCommandDB() {
		super();
	}

	public static PreDefineCommandDB getInstance() {
		if (instance == null)
			instance = new PreDefineCommandDB();
		return instance;
	}

	static public void create(SQLiteDatabase db) {
		String sql = "create table PreDefineCommand (msgName varchar(20) not null " + ", C1 varchar(4)  " + ", C2 varchar(4) " + ", C3 varchar(4) " + ", C4 varchar(4) " + ", fadeIn varchar(4) "
				+ ", fadeOut varchar(4) " + ", flag varchar(4) " + ", hold varchar(4) " + ", loop varchar(4),flashtype varchar(40) " + ", pause varchar(4), msgIcon varchar(20) );";
		db.execSQL(sql);

		db.beginTransaction();

		HashMap<String, EmbraceMsg> preDefinedEffectCommandMapping = PreDefineUtil.getInstant().getPreDefineMsgs();
		Iterator<EmbraceMsg> ite = preDefinedEffectCommandMapping.values().iterator();
		while (ite.hasNext()) {
			StringBuffer insertSql = new StringBuffer("insert into PreDefineCommand (msgName,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause,msgIcon) values (");
			EmbraceMsg msg = ite.next();
			String msgName = msg.getMsgName();
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

			int fadeInDuration = fadeIn & 0xFF;
			int fadeOutDuration = fadeOut & 0xFF;
			int fadeHoldDuration = hold & 0xFF;
			int pauseDuration = pause & 0xFF;

			String icon = msg.getMsgIcon();
			insertSql.append("'" + msgName + "',");
			insertSql.append("'" + C1 + "',");
			insertSql.append("'" + C2 + "',");
			insertSql.append("'" + C3 + "',");
			insertSql.append("'" + C4 + "',");
			insertSql.append("'" + fadeInDuration + "',");
			insertSql.append("'" + fadeOutDuration + "',");
			insertSql.append("'" + flag + "',");
			insertSql.append("'" + fadeHoldDuration + "',");
			insertSql.append("'" + loop + "',");
			insertSql.append("'" + pauseDuration + "',");
			insertSql.append("'" + icon + "'");

			insertSql.append(")");
			db.execSQL(insertSql.toString());
		}
		db.setTransactionSuccessful();
		db.endTransaction();

		// String selectSql = "select * from PreDefineCommand";
		// Cursor cur = db.rawQuery(selectSql, null);
		// int count = cur.getCount();
		// cur.close();
	}

	public boolean isMsgExisted(String msgName) {
		String sql = "select * from PreDefineCommand where msgName='" + msgName + "'";
		Cursor cur = getWritableDatabase().rawQuery(sql, null);
		boolean flag = false;
		if (cur.getCount() > 0) {
			flag = true;
		} else {
			flag = false;
		}
		cur.close();
		return flag;
	}

	public void delCreatedMsg(String delMsgName) {
		StringBuffer delThemeMsgMappingSql = new StringBuffer("delete from PreDefineCommand where msgName=");
		delThemeMsgMappingSql.append("'" + delMsgName + "'");
		getWritableDatabase().execSQL(delThemeMsgMappingSql.toString());
	}
	
	// 生成用户自定义的command
	public void addNewFxMsg(EmbraceMsg msg) {
		StringBuffer insertSql = new StringBuffer("insert into PreDefineCommand (msgName,C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,msgIcon,flashtype,pause) values (");

		String msgName = msg.getMsgName();
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
		String msgIcon = msg.getMsgIcon();
		insertSql.append("'" + msgName + "',");
		insertSql.append("'" + C1 + "',");
		insertSql.append("'" + C2 + "',");
		insertSql.append("'" + C3 + "',");
		insertSql.append("'" + C4 + "',");
		insertSql.append("'" + fadeIn + "',");
		insertSql.append("'" + fadeOut + "',");
		insertSql.append("'" + flag + "',");
		insertSql.append("'" + hold + "',");
		insertSql.append("'" + loop + "',");
		insertSql.append("'" + msgIcon + "',");
		insertSql.append("'" + msg.getFlashtype() + "',");
		insertSql.append("'" + pause + "'");

		insertSql.append(")");
		getWritableDatabase().execSQL(insertSql.toString());

		addThemeMsgMapping(msgName);
	}

	// TODO:to be moved
	private void addThemeMsgMapping(String msgName) {
		// add new msg to the table ThemeMsgMapping
		String theme = UtilitiesDB.getInstance().getCurrentTheme();
		StringBuffer insertThemeMsgMappingSql = new StringBuffer("insert into ThemeMsgMapping (theme,msgName) values (");
		insertThemeMsgMappingSql.append("'" + theme + "',");
		insertThemeMsgMappingSql.append("'" + msgName + "')");
		getWritableDatabase().execSQL(insertThemeMsgMappingSql.toString());
	}

	public EmbraceMsg getMsgObjByMsgName(String msgName) {
		String sql = "select C1,C2,C3,C4,fadeIn,fadeOut,flag,hold,loop,pause,flashtype,msgIcon from PreDefineCommand" + " where PreDefineCommand.msgName='" + msgName + "'";
		Cursor cursor = getWritableDatabase().rawQuery(sql, null);
		cursor.moveToFirst();

		String strC1 = cursor.getString(0);
		String strC2 = cursor.getString(1);
		String strC3 = cursor.getString(2);
		String strC4 = cursor.getString(3);
		String fadeIn = cursor.getString(4);
		String fadeOut = cursor.getString(5);
		String flag = cursor.getString(6);
		String hold = cursor.getString(7);
		String loop = cursor.getString(8);
		String pause = cursor.getString(9);
		String flashtype = cursor.getString(10);
		String msgIcon = cursor.getString(11);
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
		msg.setFlashtype(flashtype);
		msg.setMsgIcon(msgIcon);
		cursor.close();
		return msg;
	}

	public void updateFxMsg(EmbraceMsg msg) {

		String msgName = msg.getMsgName();
		String oldMsgName = msg.getOldMsgName() == null ? msgName : msg.getOldMsgName();
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
		String msgIcon = msg.getMsgIcon();
		/*insertSql.append("'"+msgName+"',");
		insertSql.append("'"+C1+"',");
		insertSql.append("'"+C2+"',");
		insertSql.append("'"+C3+"',");
		insertSql.append("'"+C4+"',");
		insertSql.append("'"+fadeIn+"',");
		insertSql.append("'"+fadeOut+"',");
		insertSql.append("'"+flag+"',");
		insertSql.append("'"+hold+"',");
		insertSql.append("'"+loop+"',");
		insertSql.append("'"+msgIcon+"',");
		insertSql.append("'"+msg.getFlashtype()+"',");
		insertSql.append("'"+pause+"'");*/
		StringBuffer insertSql = new StringBuffer("update PreDefineCommand set C1='" + C1 + "',C2='" + C2 + "',C3='" + C3 + "',C4='" + C4 + "',msgName='" + msgName + "',fadeIn='" + fadeIn
				+ "',fadeOut='" + fadeOut + "',flag='" + flag + "',hold='" + hold + "',loop='" + loop + "',msgIcon='" + msgIcon + "',flashtype='" + msg.getFlashtype() + "',pause='" + pause
				+ "' where msgName='" + oldMsgName + "'  ");

		// insertSql.append(")");
		getWritableDatabase().execSQL(insertSql.toString());

	}
}
