package com.embraceplus.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ContextUtil extends Application {
    private static ContextUtil instance;
    private List<Activity> activityList = new ArrayList<Activity>();

    public static ContextUtil getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
    }

	@Override
	public void onTerminate() {
		super.onTerminate();
		System.out.println("-----------------------------------");
		System.exit(0);
		
	}
    
    
}
