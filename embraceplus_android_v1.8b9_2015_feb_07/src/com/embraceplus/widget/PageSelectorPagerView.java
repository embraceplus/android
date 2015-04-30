package com.embraceplus.widget;

import com.embraceplus.app.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PageSelectorPagerView extends View {
	
	private int mPageCount = 0;
	private int mCurrentPage = 0;
	int dotWidth = 8;
	
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public PageSelectorPagerView(Context context) {
		super(context);
		
		dotWidth = (int) context.getResources().getDimension(R.dimen.customer_fx_pager_dot_width);
	}
	
	public PageSelectorPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		dotWidth = (int) context.getResources().getDimension(R.dimen.customer_fx_pager_dot_width);
	}
	
	public PageSelectorPagerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		dotWidth = (int) context.getResources().getDimension(R.dimen.customer_fx_pager_dot_width);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mPaint.setColor(Color.WHITE);
		
	    int h = canvas.getHeight() / 2;
	    if(h > 30) h = 6;
		final int startX = (int) (canvas.getWidth() / 2.0 - 
				(mPageCount * dotWidth / 2.0) / 2 - (h * 2.0 * (mPageCount - 1)) / 2) + 3;
	
		if(mPageCount == 1) return;
		for (int i = 0; i < mPageCount; i++) {
			mPaint.setAlpha(i == mCurrentPage ? 255 : 100);
			
			canvas.drawCircle(startX + h * 2 * i, h, dotWidth, mPaint);
		}
	}
	
	public void setData(int pageCount, int currentPage) {
		mPageCount = pageCount;
		mCurrentPage = currentPage;
		
		invalidate();
	}

}
