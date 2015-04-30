package com.embraceplus.utils;

import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;

import com.embraceplus.app.MainActivity;

public class BaseBackPressedListener implements OnBackPressedListener {
    private final MainActivity activity;

    public BaseBackPressedListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void doBack() {
        if(activity.getFragmentManager().getBackStackEntryCount() > 0) {
        	activity.getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
        	activity.moveTaskToBack (true);
        }
        
    }
}