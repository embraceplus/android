package com.embraceplus.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import android.graphics.Color;

public class EColor2 {

	//public static final int WHITE = Color.parseColor("#FFFFFFFF");
	/*public static final int TRANSPARENT = Color.parseColor("#00000000");
	public static final int WHITE = Color.parseColor("#00000000");*/
	public static final int TRANSPARENT = Color.parseColor("#ff999999");
	public static final int WHITE = Color.parseColor("#ffffff");
	
	public static final int PINK_LIGHT = Color.parseColor("#FFFF99FF");
	public static final int PINK = Color.parseColor("#FFFF00FF");
	public static final int PURPLE = Color.parseColor("#FF9900FF");
	public static final int PINK_RED = Color.parseColor("#FFFF0066");
	public static final int RED = Color.parseColor("#FFFF0000");
	public static final int RED_BLOOD = Color.parseColor("#FFFF3333");
	public static final int YELLOW = Color.parseColor("#FFFFFF00");
	public static final int GREEN_ACID = Color.parseColor("#FF99FF00");
	public static final int GREEN = Color.parseColor("#FF00FF00");
	public static final int ORANGE = Color.parseColor("#FFFF9900");
	public static final int GOLD = Color.parseColor("#FFFFCC33");
	public static final int BLUE = Color.parseColor("#FF0000FF");
	public static final int BLUE_SKY = Color.parseColor("#FFCCFFFF");
	public static final int BLUE_LIGHT = Color.parseColor("#FF3399FF");
	public static final int SILVER = Color.parseColor("#999999");
	

	/*public static final int[] COLORS = new int[] {
		//TRANSPARENT,
		WHITE,
		-99,
		PINK_LIGHT,
		PINK,
		PURPLE,
		PINK_RED,
		RED,
		RED_BLOOD,
		YELLOW,
		GREEN_ACID,
		GREEN,
		ORANGE,
		GOLD,
		BLUE_SKY,
		BLUE_LIGHT,
		BLUE,
	};*/
	
	//because in the emulator, the silver color is as same as the white color, so in the 
	//simulator, there is no silver color,instead use the value -99 to stand the random color.
	public static final int[] COLORS = new int[] {
		//TRANSPARENT,
		WHITE,
		BLUE,
		GREEN,
		BLUE_LIGHT,
		GREEN_ACID,
		BLUE_SKY,
		YELLOW,
		RED,
		GOLD,
		RED_BLOOD,
		ORANGE,
		PINK,
		PINK_LIGHT,
		PURPLE,
		PINK_RED,
		TRANSPARENT
		
	};
	
	private static final Random random = new Random();
	
	/*private static final Map<String, Integer> table;
	
	static {
		Hashtable<String, Integer> map = new Hashtable<String, Integer>();
		
		map.put("WHITE", WHITE);
		map.put("TRANSPARENT", TRANSPARENT);
		map.put("PINK_LIGHT", PINK_LIGHT);
		map.put("PINK", PINK);
		map.put("PURPLE", PURPLE);
		map.put("PINK_RED", PINK_RED);
		map.put("RED", RED);
		map.put("RED_BLOOD", RED_BLOOD);
		map.put("YELLOW", YELLOW);
		map.put("GREEN_ACID", GREEN_ACID);
		map.put("GREEN", GREEN);
		map.put("ORANGE", ORANGE);
		map.put("GOLD", GOLD);
		map.put("BLUE", BLUE);
		map.put("BLUE_SKY", BLUE_SKY);
		map.put("BLUE_LIGHT", BLUE_LIGHT);
		map.put("SILVER", SILVER);
		
		table = Collections.unmodifiableMap(map);
	}*/
	
private static final Map<Integer, String> voColorValue_StringMapping;
private static final Map<String, Integer> exCommandColorString_ValueMapping;

private static final Map<String, Integer> voColorString_ValueMapping;
private static final Map<Integer, String> exCommandColorValue_StringMapping;
	
	static {
		Hashtable<Integer, String> map = new Hashtable<Integer, String>();
		
		map.put(WHITE, "WHITE");
		map.put(TRANSPARENT, "TRANSPARENT");
		map.put(PINK_LIGHT, "PINK_LIGHT");
		map.put(PINK, "PINK");
		map.put(PURPLE, "PURPLE");
		map.put(PINK_RED, "PINK_RED");
		map.put(RED, "RED");
		map.put(RED_BLOOD, "RED_BLOOD");
		map.put(YELLOW, "YELLOW");
		map.put(GREEN_ACID, "GREEN_ACID");
		map.put(GREEN, "GREEN");
		map.put(ORANGE, "ORANGE");
		map.put(GOLD, "GOLD");
		map.put(BLUE, "BLUE");
		map.put(BLUE_SKY, "BLUE_SKY");
		map.put(BLUE_LIGHT, "BLUE_LIGHT");
		map.put(SILVER ,"SILVER");
		
        Hashtable<String, Integer> exCommandMap = new Hashtable<String, Integer>();
		
        exCommandMap.put("WHITE",1);
        exCommandMap.put("TRANSPARENT",1);
        exCommandMap.put("SILVER",2);
        exCommandMap.put("PINK_LIGHT", 3);
        exCommandMap.put("PINK", 4);
        exCommandMap.put("PURPLE", 5);
        exCommandMap.put("PINK_RED", 6);
        exCommandMap.put("RED", 7);
        exCommandMap.put("RED_BLOOD", 8);
        exCommandMap.put("YELLOW", 9);
        exCommandMap.put("GREEN_ACID", 10);
        exCommandMap.put("GREEN", 11);
        exCommandMap.put("ORANGE", 12);
        exCommandMap.put("GOLD", 13);
        exCommandMap.put("BLUE", 16);
        exCommandMap.put("BLUE_SKY", 14);
        exCommandMap.put("BLUE_LIGHT", 15);
      //  exCommandMap.put("GREEN_ACID", 10);
		
		voColorValue_StringMapping = Collections.unmodifiableMap(map);
		exCommandColorString_ValueMapping = Collections.unmodifiableMap(exCommandMap);
		
		

		Hashtable<String, Integer> map3 = new Hashtable<String, Integer>();
		
		map3.put("WHITE", WHITE);
		map3.put("TRANSPARENT", TRANSPARENT);
		map3.put( "PINK_LIGHT",PINK_LIGHT);
		map3.put("PINK", PINK);
		map3.put("PURPLE", PURPLE);
		map3.put("PINK_RED", PINK_RED);
		map3.put("RED", RED);
		map3.put("RED_BLOOD", RED_BLOOD);
		map3.put("YELLOW", YELLOW);
		map3.put("GREEN_ACID", GREEN_ACID);
		map3.put("GREEN", GREEN);
		map3.put("ORANGE", ORANGE);
		map3.put("GOLD", GOLD);
		map3.put("BLUE", BLUE);
		map3.put("BLUE_SKY", BLUE_SKY);
		map3.put( "BLUE_LIGHT",BLUE_LIGHT);
		map3.put( "SILVER",WHITE);
		
        Hashtable<Integer,String > exCommandMap4 = new Hashtable<Integer,String >();
		
        exCommandMap4.put(0,"WHITE");
        exCommandMap4.put(1,"WHITE");
        exCommandMap4.put(2,"SILVER");
        exCommandMap4.put(3, "PINK_LIGHT");
        exCommandMap4.put( 4,"PINK");
        exCommandMap4.put(5, "PURPLE");
        exCommandMap4.put(6, "PINK_RED");
        exCommandMap4.put(7, "RED");
        exCommandMap4.put(8, "RED_BLOOD");
        exCommandMap4.put(9, "YELLOW");
        exCommandMap4.put(10, "GREEN_ACID");
        exCommandMap4.put(11, "GREEN");
        exCommandMap4.put(12, "ORANGE");
        exCommandMap4.put(13, "GOLD");
        exCommandMap4.put(16, "BLUE");
        exCommandMap4.put(14, "BLUE_SKY");
        exCommandMap4.put(15, "BLUE_LIGHT");
      //  exCommandMap.put("GREEN_ACID", 10);
		
        voColorString_ValueMapping = Collections.unmodifiableMap(map3);
        exCommandColorValue_StringMapping = Collections.unmodifiableMap(exCommandMap4);
	
	}
	
	public static int getInterpolatedColor(float fraction, int c1, int c2) {
		if (fraction > 1)
			fraction = 1;
		
		int resultColor = 0;
		
		final int alpha = Color.alpha(c1) - Color.alpha(c2);
		final int red = Color.red(c1) - Color.red(c2);
		final int green = Color.green(c1) - Color.green(c2);
		final int blue = Color.blue(c1) - Color.blue(c2);
		
		resultColor = Color.argb(
				Color.alpha(c1) - (int) (alpha * fraction),
				Color.red(c1) - (int) (red * fraction),
				Color.green(c1) - (int) (green * fraction),
				Color.blue(c1) - (int) (blue * fraction)
			);
		
		return resultColor;
	}
	
	public static int getRandomColor() {
		return COLORS[random.nextInt(COLORS.length)];
	}
	/*
	public static int getColorByString(String colorName) {
		return table.containsKey(colorName) ? table.get(colorName) : Color.BLACK;
	}*/
	
	public static  int getExCommandColorValueFromColorValue(int voColorValue)
	{
		//if the voColorValue is -99,means user selected the random model.
		if (voColorValue == -99)
		{
			return -99;
		}else if(voColorValue == 0)
		{
			return 0;
		}
		String colorName = voColorValue_StringMapping.get(voColorValue);
		int exCommandColorValue = exCommandColorString_ValueMapping.get(colorName);
		
		
		return exCommandColorValue;
	}
	
	public static int getVoColorValueFromExCommandColorValue(int exCommandColor)
	{
		String colorName = exCommandColorValue_StringMapping.get(exCommandColor);
		int voColor = voColorString_ValueMapping.get(colorName);
		return voColor;
	}
}
