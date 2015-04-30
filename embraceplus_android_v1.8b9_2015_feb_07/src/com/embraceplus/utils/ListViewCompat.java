package com.embraceplus.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.embraceplus.model.MessageItem;

public class ListViewCompat extends ListView {

	private SlideView mFocusedItemView;

	  public ListViewCompat(Context paramContext)
	  {
	    super(paramContext);
	  }

	  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet)
	  {
	    super(paramContext, paramAttributeSet);
	    //setDividerHeight(0);
	    setDividerHeight(5);
	    setDivider(new ColorDrawable(0x00ff00));
	    
	  }

	  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	  {
	    super(paramContext, paramAttributeSet, paramInt);
	  }

	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	    	
	        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN: {
	            int x = (int) event.getX();
	            int y = (int) event.getY();

	            int position = pointToPosition(x, y);
	            
	            // System.out.println("in the action action_down...");	         
	            // System.out.println("the position is ......." + position);
	            
	            if (position != INVALID_POSITION ) {
	            	MessageItem data = (MessageItem) getItemAtPosition(position);
	                if ( data != null ) mFocusedItemView = data.slideView;
	           
	            
	            }else{
	            	return false;
	            }
	        }
	        
	        case MotionEvent.ACTION_MOVE:{
	        	System.out.println("in the action move...");
	        	//return false;
	        }
	        default:
	            break;
	        }

	        if (mFocusedItemView != null) {
	            mFocusedItemView.onRequireTouchEvent(event);
	            if(!mFocusedItemView.isFinishDrag())
	               return true;
	        }

	        return super.onTouchEvent(event);
	    }
	    
	    /*@Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {
	    	if(ev.getAction() == MotionEvent.ACTION_UP){
	    		 if (mFocusedItemView != null) {
	 	            //mFocusedItemView.onRequireTouchEvent(ev);
	 	            return true;
	 	        }
	    	}
	    	return super.onInterceptTouchEvent(ev);
	    }*/

	  public void shrinkListItem(int paramInt)
	  {
	    View localView = getChildAt(paramInt);
	    try
	    {
	    	if (localView != null) ((SlideView)localView).shrink();
	      return;
	    }
	    catch (ClassCastException localClassCastException)
	    {
	      while (true)
	        localClassCastException.printStackTrace();
	    }
	  }

}
