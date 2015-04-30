package com.embraceplus.model;

import android.graphics.Color;

public class EffectVO {
	
	public int _id = -1;
	
	public String name;
	
	public int fadeInTime = 0;
	public int holdTime = 0;
	public int fadeOutTime = 0;
	public int pauseTime = 0;
	
	public boolean blackoutOnPause = false;
	public boolean random = false;
	
	public int colorL1 = Color.RED;
	public int colorL2 = Color.TRANSPARENT;
	public int colorR1 = Color.RED;
	public int colorR2 = Color.TRANSPARENT;
	
	public boolean vibrate = false;
	public boolean editable = false;
	public boolean revert = false;
	
	public int getLoopDuration() {
		return fadeInTime + holdTime + fadeOutTime + pauseTime;
	}
}
