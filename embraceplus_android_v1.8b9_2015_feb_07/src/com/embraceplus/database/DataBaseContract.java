package com.embraceplus.database;

import android.provider.BaseColumns;

public final class DataBaseContract {

	public DataBaseContract() {}

	public static abstract class Theme implements BaseColumns {
		
		public static final String TABLE_NAME = "theme";
		
		public static final String NAME = "name";
		public static final String ICON = "icon";
		public static final String BACKGROUND = "background";
		
		public static final String[] PROJECTION = new String[] {
			_ID,
			NAME,
			ICON,
			BACKGROUND
		};
	}
	
	public static abstract class Effect implements BaseColumns {
		public static final String TABLE_NAME = "effect";
		
		public static final String NAME = "title";
		public static final String ICON = "icon";
		
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
		
		public static final String EDITABLE = "is_editable";
		
		public static final String[] PROJECTION = new String[] {
			_ID,
			NAME,
			ICON,
			FADE_IN_TIME,
			HOLD_TIME,
			FADE_OUT_TIME,
			PAUSE_TIME,
			BLACKOUT_ON_PAUSE,
			RANDOM,
			COLOR_L1,
			COLOR_L2,
			COLOR_R1,
			COLOR_R2,
			EDITABLE
		};
	}
	
	public static abstract class MotionPreset implements BaseColumns {
		public static final String TABLE_NAME = "motion_preset";
		
		public static final String NAME = "name";
		public static final String DURATION = "duration";
		public static final String REVERSE = "reverse";
		public static final String RANDOM = "random";
		public static final String PAUSE = "pause";
		
		public static final String[] PROJECTION = new String[] {
			_ID,
			NAME,
			DURATION,
			REVERSE,
			RANDOM,
			PAUSE,
		};
	}
}
