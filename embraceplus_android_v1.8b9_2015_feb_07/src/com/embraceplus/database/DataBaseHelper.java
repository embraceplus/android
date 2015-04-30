package com.embraceplus.database;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.embraceplus.app.R;
import com.embraceplus.model.EffectVO;
import com.embraceplus.model.MotionPresetVO;
import com.embraceplus.utils.EColor;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DB";
	
	private static final String XML_ITEM_TAG = "record";
	
	private static final String DB_NAME = "embrace_plus.db";
	private static final int DB_VERSION = 1;
	
	private static final String CREATE = "CREATE TABLE IF NOT EXISTS ";
	private static final String DROP = "DROP TABLE IF EXISTS ";
	
	private static final String CREATE_THEMES =
		CREATE + DataBaseContract.Theme.TABLE_NAME + " (" +
				DataBaseContract.Theme._ID + " INTEGER primary key autoincrement, " +
				DataBaseContract.Theme.NAME + " TEXT, " + 
				DataBaseContract.Theme.ICON + " INTEGER, " +
				DataBaseContract.Theme.BACKGROUND + " INTEGER " +
				" )";
	
	private static final String DROP_THEMES = DROP + DataBaseContract.Theme.TABLE_NAME;
	
	private static final String CREATE_EFFECTS =
		CREATE + DataBaseContract.Effect.TABLE_NAME + " (" +
				DataBaseContract.Effect._ID + " INTEGER primary key autoincrement, " +
				DataBaseContract.Effect.NAME + " TEXT, " + 
				DataBaseContract.Effect.ICON + " INTEGER, " +
				DataBaseContract.Effect.FADE_IN_TIME + " INTEGER, " +
				DataBaseContract.Effect.HOLD_TIME + " INTEGER, " +
				DataBaseContract.Effect.FADE_OUT_TIME + " INTEGER, " +
				DataBaseContract.Effect.PAUSE_TIME + " INTEGER, " +
				DataBaseContract.Effect.BLACKOUT_ON_PAUSE + " INTEGER, " +
				DataBaseContract.Effect.RANDOM + " INTEGER, " +
				DataBaseContract.Effect.COLOR_L1 + " INTEGER, " +
				DataBaseContract.Effect.COLOR_L2 + " INTEGER, " +
				DataBaseContract.Effect.COLOR_R1 + " INTEGER, " +
				DataBaseContract.Effect.COLOR_R2 + " INTEGER, " +
				DataBaseContract.Effect.EDITABLE + " INTEGER " +
				" )";
	
	private static final String DROP_EFFECTS = DROP + DataBaseContract.Effect.TABLE_NAME;
	
	private static final String CREATE_MOTION_PRESETS =
		CREATE + DataBaseContract.MotionPreset.TABLE_NAME + " (" +
				DataBaseContract.MotionPreset._ID + " INTEGER primary key autoincrement, " +
				DataBaseContract.MotionPreset.NAME + " TEXT, " +
				DataBaseContract.MotionPreset.DURATION + " INTEGER, " +
				DataBaseContract.MotionPreset.REVERSE + " INTEGER, " +
				DataBaseContract.MotionPreset.RANDOM + " INTEGER, " +
				DataBaseContract.MotionPreset.PAUSE + " INTEGER " +
				" )";
	
	private static final String DROP_MOTION_PRESETS = DROP + DataBaseContract.MotionPreset.TABLE_NAME;

	private Context context;
	
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		long startTime = System.currentTimeMillis();
		
		db.execSQL(CREATE_MOTION_PRESETS);
		Log.v(TAG, DataBaseContract.MotionPreset.TABLE_NAME + " table created");
		initDefaultMotionPresets(db);
		
//		db.execSQL(CREATE_EFFECTS);
//		Log.v(TAG, DataBaseContract.Effect.TABLE_NAME + " table created");
//		initDefaultEffects(db);
//		
//		db.execSQL(CREATE_THEMES);
//		Log.v(TAG, DataBaseContract.Theme.TABLE_NAME + " table created");
//		initDefaultThemes(db);
		
		Log.v(TAG, String.format("successfully created in %dms", System.currentTimeMillis() - startTime));
	}
	
	private void initDefaultMotionPresets(SQLiteDatabase db) {
		Log.v(TAG, "initDefaultMotionPresets");
		
		XmlResourceParser xmlParser = null;
		ContentValues cv = new ContentValues();
		
		xmlParser = context.getResources().getXml(R.xml.default_motion_presets);
		try {
			int eventType = xmlParser.next();
			
			String name = "";
			int duration;
			int reverse;
			int random;
			int pause;
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
			    if (eventType == XmlPullParser.START_TAG && xmlParser.getName().equalsIgnoreCase(XML_ITEM_TAG)) {
			    	name = xmlParser.getAttributeValue(null, XmlParserContract.MotionPreset.NAME);
			    	duration = xmlParser.getAttributeIntValue(null, XmlParserContract.MotionPreset.DURATION, 0);
			    	reverse = xmlParser.getAttributeIntValue(null, XmlParserContract.MotionPreset.REVERSE, 0);
			    	random = xmlParser.getAttributeIntValue(null, XmlParserContract.MotionPreset.RANDOM, 0);
			    	pause = xmlParser.getAttributeIntValue(null, XmlParserContract.MotionPreset.PAUSE, 0);
			    	
			    	Log.v(TAG, "reading motion preset " + name + " data");
			    	
			    	if (name.isEmpty() == false) {
			    		cv.clear();
			    		
			    		cv.put(DataBaseContract.MotionPreset.NAME, name);
			    		cv.put(DataBaseContract.MotionPreset.DURATION, duration);
			    		cv.put(DataBaseContract.MotionPreset.REVERSE, reverse);
			    		cv.put(DataBaseContract.MotionPreset.RANDOM, random);
			    		cv.put(DataBaseContract.MotionPreset.PAUSE, pause);
			    		
			    		long id = db.insert(DataBaseContract.MotionPreset.TABLE_NAME,
			    				null, cv);
			    		
			    		if (id > 0) {
			    			Log.v(TAG, "Succesfully saved " + name + " motion preset to database " + String.valueOf(id));
			    		} else {
			    			Log.v(TAG, "Failed to save " + name + " motion preset to database");
			    		}
			    		
			    	} else {
			    		Log.v(TAG, "Failed to read motion preset record from xml");
			    	}
			    }
			    
			    eventType = xmlParser.next();
			}
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private void initDefaultEffects(SQLiteDatabase db) {
//		
//		Log.v(TAG, "initDefaultEffects");
//		
//		XmlResourceParser xmlParser = null;
//		ContentValues cv = new ContentValues();
//		
//		xmlParser = context.getResources().getXml(R.xml.default_effects);
//		try {
//			int eventType = xmlParser.next();
//			
//			String name = "";
//			
//			int fadeInTime;
//			int holdTime;
//			int fadeOutTime;
//			int pauseTime;
//			
//			int blackoutOnPause;
//			int random;
//			
//			int colorL1;
//			int colorL2;
//			int colorR1;
//			int colorR2;
//			
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//			    if (eventType == XmlPullParser.START_TAG && xmlParser.getName().equalsIgnoreCase(XML_ITEM_TAG)) {
//			    	name = xmlParser.getAttributeValue(null, XmlParserContract.Effect.NAME);
//			    	
//			    	fadeInTime = xmlParser.getAttributeIntValue(null, XmlParserContract.Effect.FADE_IN_TIME, 0);
//			    	holdTime = xmlParser.getAttributeIntValue(null, XmlParserContract.Effect.HOLD_TIME, 0);
//			    	fadeOutTime = xmlParser.getAttributeIntValue(null, XmlParserContract.Effect.FADE_OUT_TIME, 0);
//			    	pauseTime = xmlParser.getAttributeIntValue(null, XmlParserContract.Effect.PAUSE_TIME, 0);
//			    	
//			    	blackoutOnPause = xmlParser.getAttributeIntValue(null, XmlParserContract.Effect.BLACKOUT_ON_PAUSE, 0);
//			    	random = xmlParser.getAttributeIntValue(null, XmlParserContract.Effect.RANDOM, 0);
//			    	
//			    	colorL1 = parseColor(xmlParser.getAttributeValue(null, XmlParserContract.Effect.COLOR_L1));
//			    	colorL2 = parseColor(xmlParser.getAttributeValue(null, XmlParserContract.Effect.COLOR_L2));
//			    	colorR1 = parseColor(xmlParser.getAttributeValue(null, XmlParserContract.Effect.COLOR_R1));
//			    	colorR2 = parseColor(xmlParser.getAttributeValue(null, XmlParserContract.Effect.COLOR_R2));
//			    	
//			    	Log.v(TAG, "reading effect " + name + " data�);
//			    	
//			    	if (name.isEmpty() == false) {
//			    		cv.clear();
//			    		
//			    		cv.put(DataBaseContract.Effect.NAME, name);
//			    		
//			    		cv.put(DataBaseContract.Effect.FADE_IN_TIME, fadeInTime);
//			    		cv.put(DataBaseContract.Effect.HOLD_TIME, holdTime);
//			    		cv.put(DataBaseContract.Effect.FADE_OUT_TIME, fadeOutTime);
//			    		cv.put(DataBaseContract.Effect.PAUSE_TIME, pauseTime);
//			    		
//			    		cv.put(DataBaseContract.Effect.BLACKOUT_ON_PAUSE, blackoutOnPause);
//			    		cv.put(DataBaseContract.Effect.RANDOM, random);
//			    		
//			    		cv.put(DataBaseContract.Effect.COLOR_L1, colorL1);
//			    		cv.put(DataBaseContract.Effect.COLOR_L2, colorL2);
//			    		cv.put(DataBaseContract.Effect.COLOR_R1, colorR1);
//			    		cv.put(DataBaseContract.Effect.COLOR_R2, colorR2);
//			    		
//			    		cv.put(DataBaseContract.Effect.EDITABLE, 0); //Default effects are not editable
//			    		
//			    		long id = db.insert(DataBaseContract.Effect.TABLE_NAME,
//			    				null, cv);
//			    		
//			    		if (id > 0) {
//			    			Log.v(TAG, "Succesfully saved " + name + " effect to database " + String.valueOf(id));
//			    		} else {
//			    			Log.v(TAG, "Failed to save " + name + " effect to database");
//			    		}
//			    		
//			    	} else {
//			    		Log.v(TAG, "Failed to read effect record from xml");
//			    	}
//			    }
//			    
//			    eventType = xmlParser.next();
//			}
//			
//		} catch (XmlPullParserException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	/*
	private int parseColor(String colorName) {
		return EColor.getColorByString(colorName);
	}*/
	
//	private void initDefaultThemes(SQLiteDatabase db) {
//		ContentValues cv = new ContentValues();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Business");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_business);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Fashionista");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_fashionista);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Vampire");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_vampire);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Clubber");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_clubber);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Student");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_student);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Magician");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_magician);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Adventurer");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_adventurer);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Athlete");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_athlete);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//		
//		cv.put(DataBaseContract.Theme.NAME, "Entertainer");
//		cv.put(DataBaseContract.Theme.BACKGROUND, R.drawable.bg_entertainer);
//		db.insert(DataBaseContract.Theme.TABLE_NAME, null, cv);
//		cv.clear();
//	}
//	
//	public List<ThemeVO> getDefaultThemes() {
//		LinkedList<ThemeVO> list = new LinkedList<ThemeVO>();
//		
//		SQLiteDatabase r = getReadableDatabase();
//		
//		Cursor c = r.query(
//				DataBaseContract.Theme.TABLE_NAME,
//				DataBaseContract.Theme.PROJECTION,
//				null, null, null, null, null);
//		
//		c.moveToFirst();
//		
//		ThemeVO item;
//		
//		while (!c.isAfterLast()) {
//			item = new ThemeVO();
//			
//			item._id = c.getInt(c.getColumnIndex(DataBaseContract.Theme._ID));
//			item.name = c.getString(c.getColumnIndex(DataBaseContract.Theme.NAME));
//			item.icon = c.getInt(c.getColumnIndex(DataBaseContract.Theme.ICON));
//			item.background = c.getInt(c.getColumnIndex(DataBaseContract.Theme.BACKGROUND));
//			
//			list.add(item);
//			c.moveToNext();
//		}
//		
//		c.close();
//		r.close();
//		
//		return list;
//	}
	
	public List<MotionPresetVO> getDefaultMotionPresets() {
		LinkedList<MotionPresetVO> list = new LinkedList<MotionPresetVO>();
		
		SQLiteDatabase r = getReadableDatabase();
		
		Cursor c = r.query(
				DataBaseContract.MotionPreset.TABLE_NAME,
				DataBaseContract.MotionPreset.PROJECTION,
				null, null, null, null, null);
		
		c.moveToFirst();
		
		MotionPresetVO item;
		
		while (!c.isAfterLast()) {
			item = new MotionPresetVO();
			
			item._id = c.getInt(c.getColumnIndex(DataBaseContract.MotionPreset._ID));
			item.name = c.getString(c.getColumnIndex(DataBaseContract.MotionPreset.NAME));
			
			item.duration = c.getInt(c.getColumnIndex(DataBaseContract.MotionPreset.DURATION));
			item.pause = c.getInt(c.getColumnIndex(DataBaseContract.MotionPreset.PAUSE));
			
			item.random = c.getInt(c.getColumnIndex(DataBaseContract.MotionPreset.RANDOM)) == 1;
			item.reverse = c.getInt(c.getColumnIndex(DataBaseContract.MotionPreset.REVERSE)) == 1;
			
			list.add(item);
			c.moveToNext();
		}
		
		c.close();
		r.close();
		
		return list;
	}
	
//	public ThemeVO getThemeById(int themeId) {
//		SQLiteDatabase r = getReadableDatabase();
//		
//		Cursor c = r.query(
//				DataBaseContract.Theme.TABLE_NAME,
//				DataBaseContract.Theme.PROJECTION,
//				DataBaseContract.Theme._ID + " = ?",
//				new String[] { String.valueOf(themeId) }, null, null, null);
//		
//		c.moveToFirst();
//		
//		ThemeVO item = null;
//		
//		if (c.getCount() > 0) {
//			item = new ThemeVO();
//			
//			item._id = themeId;
//			item.name = c.getString(c.getColumnIndex(DataBaseContract.Theme.NAME));
//			item.icon = c.getInt(c.getColumnIndex(DataBaseContract.Theme.ICON));
//			item.background = c.getInt(c.getColumnIndex(DataBaseContract.Theme.BACKGROUND));
//		}
//		
//		c.close();
//		r.close();
//		
//		return item;
//	}
	
//	public EffectVO getEffectById(int effectId) {
//		
//		SQLiteDatabase r = getReadableDatabase();
//		
//		Cursor c = r.query(
//				DataBaseContract.Effect.TABLE_NAME,
//				DataBaseContract.Effect.PROJECTION,
//				DataBaseContract.Effect._ID + " = ?",
//				new String[] { String.valueOf(effectId) }, null, null, null);
//		
//		c.moveToFirst();
//		
//		EffectVO item = null;
//		
//		if (c.getCount() > 0) {
//			item = new EffectVO();
//			
//			fillEffectItem(c, item);
//		}
//		
//		c.close();
//		r.close();
//		
//		return item;
//	}
	
//	public List<EffectVO> getDefaultEffects() {
//		LinkedList<EffectVO> list = new LinkedList<EffectVO>();
//		
//		SQLiteDatabase r = getReadableDatabase();
//		
//		Cursor c = r.query(
//				DataBaseContract.Effect.TABLE_NAME,
//				DataBaseContract.Effect.PROJECTION,
//				null, null, null, null, null);
//		
//		c.moveToFirst();
//		
//		EffectVO item;
//		
//		while (!c.isAfterLast()) {
//			item = new EffectVO();
//			
//			fillEffectItem(c, item);
//			
//			list.add(item);
//			c.moveToNext();
//		}
//		
//		c.close();
//		r.close();
//		
//		return list;
//	}
	
	private void fillEffectItem(Cursor c, EffectVO item) {
		item._id = c.getInt(c.getColumnIndex(DataBaseContract.Effect._ID));
		item.name = c.getString(c.getColumnIndex(DataBaseContract.Effect.NAME));
		
		item.fadeInTime = c.getInt(c.getColumnIndex(DataBaseContract.Effect.FADE_IN_TIME));
		item.holdTime = c.getInt(c.getColumnIndex(DataBaseContract.Effect.HOLD_TIME));
		item.fadeOutTime = c.getInt(c.getColumnIndex(DataBaseContract.Effect.FADE_OUT_TIME));
		item.pauseTime = c.getInt(c.getColumnIndex(DataBaseContract.Effect.PAUSE_TIME));
		
		item.blackoutOnPause = c.getInt(c.getColumnIndex(DataBaseContract.Effect.BLACKOUT_ON_PAUSE)) == 1;
		item.random = c.getInt(c.getColumnIndex(DataBaseContract.Effect.RANDOM)) == 1;
		
		item.colorL1 = c.getInt(c.getColumnIndex(DataBaseContract.Effect.COLOR_L1));
		item.colorL2 = c.getInt(c.getColumnIndex(DataBaseContract.Effect.COLOR_L2));
		item.colorR1 = c.getInt(c.getColumnIndex(DataBaseContract.Effect.COLOR_R1));
		item.colorR2 = c.getInt(c.getColumnIndex(DataBaseContract.Effect.COLOR_R2));
		
		item.editable = c.getInt(c.getColumnIndex(DataBaseContract.Effect.EDITABLE)) == 1;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Log.v(TAG, String.format("Upgrading from v%d to v%d�, oldVersion, newVersion));
		
//		db.execSQL(DROP_THEMES);
//		Log.v(TAG, DataBaseContract.Theme.TABLE_NAME + " table dropped");
//		db.execSQL(DROP_EFFECTS);
//		Log.v(TAG, DataBaseContract.Effect.TABLE_NAME + " table dropped");
		db.execSQL(DROP_MOTION_PRESETS);
		Log.v(TAG, DataBaseContract.MotionPreset.TABLE_NAME + " table dropped");
		
		onCreate(db);
	}

	public static class XmlParserContract {
		public static abstract class Effect {
			public static final String NAME = "name";
			
			public static final String FADE_IN_TIME = "fade_in_time";
			public static final String HOLD_TIME = "hold_time";
			public static final String FADE_OUT_TIME = "fade_out_time";
			public static final String PAUSE_TIME = "pause_time";
			
			public static final String BLACKOUT_ON_PAUSE = "blackout_on_pause";
			public static final String RANDOM = "random";
			
			public static final String COLOR_L1 = "color_l1";
			public static final String COLOR_L2 = "color_l2";
			public static final String COLOR_R1 = "color_r1";
			public static final String COLOR_R2 = "color_r2";
		}
		
		public static abstract class MotionPreset {
			public static final String NAME = "name";
			public static final String DURATION = "duration";
			public static final String REVERSE = "reverse";
			public static final String RANDOM = "random";
			public static final String PAUSE = "pause";
		}
	}

}
