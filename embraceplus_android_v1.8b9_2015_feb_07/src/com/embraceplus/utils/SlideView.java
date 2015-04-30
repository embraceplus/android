package com.embraceplus.utils;

import com.embraceplus.app.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * SlideView �̳���LinearLayout
 */
public class SlideView extends LinearLayout {

   // private static final String TAG = "SlideView";

    private Context mContext;

    // ������������view������
    private LinearLayout mViewContent;

    // ������������view������������ɾ�� ��ť
    private RelativeLayout mHolder;

    // ���Ի��������ṩ���Ի���Ч��
    private Scroller mScroller;

    // �����ص��ӿڣ��������ϲ�֪ͨ�����¼�
    private OnSlideListener mOnSlideListener;

    // ���������Ŀ�� ��λ��dp
    private int mHolderWidth = 180;
   // private int mHolderWidth = 230;

    // �ֱ��¼�ϴλ��������
    private int mLastX = 0;
    private int mLastY = 0;

    // �������ƻ����Ƕȣ������Ƕ�a�������������Ž��л�����tan a = deltaX / deltaY > 2
    private static final int TAN = 1;

    public interface OnSlideListener {
        // SlideView������״̬����ʼ�������򿪣��ر�
        public static final int SLIDE_STATUS_OFF = 0;
        public static final int SLIDE_STATUS_START_SCROLL = 1;
        public static final int SLIDE_STATUS_ON = 2;

        /**
         * @param view
         *            current SlideView
         * @param status
         *            SLIDE_STATUS_ON, SLIDE_STATUS_OFF or
         *            SLIDE_STATUS_START_SCROLL
         */
        public void onSlide(View view, int status);
    }

    public SlideView(Context context) {
        super(context);
        initView();
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mContext = getContext();
        // ��ʼ�����Ի�������
        mScroller = new Scroller(mContext);
        // �����䷽��Ϊ����
        setOrientation(LinearLayout.HORIZONTAL);
        // ��slide_view_merge���ؽ���
        View.inflate(mContext, R.layout.slide_view_merge, this);
        mViewContent = (LinearLayout) findViewById(R.id.view_content);
        
        //test ///
        
        
        mHolderWidth = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources()
                        .getDisplayMetrics()));
    }

    public void setWidth(int width){
   	 mHolderWidth = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, width, getResources()
                        .getDisplayMetrics()));
    }
    
    // ���ð�ť�����ݣ�Ҳ��������ͼ��ɶ�ģ���ûд
    public void setButtonText(CharSequence text) {
        ((TextView) findViewById(R.id.delete)).setText(text);
        ((TextView) findViewById(R.id.silent)).setText(text);
    }

    // ��view���뵽ViewContent��
    public void setContentView(View view) {
        mViewContent.addView(view);
    }

    // ���û����ص�
    public void setOnSlideListener(OnSlideListener onSlideListener) {
        mOnSlideListener = onSlideListener;
    }

    // ����ǰ״̬��Ϊ�ر�
    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	if((!mScroller.isFinished() || getScrollX() != 0) && getScrollX() != mHolderWidth)
    	   return true;
    	else 
    	   return super.onInterceptTouchEvent(ev);
    }
    
    public boolean isFinishDrag(){
    	if(!mScroller.isFinished() && getScrollX() != 0)
     	   return false;
     	else
     	   return true;
    }
    
    // ���MotionEvent�����л�������������������൱��onTouchEvent
    // ����㲻��Ҫ���?����ͻ������ֱ������������������
    public void onRequireTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int scrollX = getScrollX();
        //Log.d(TAG, "x=" + x + "  y=" + y);

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            if (mOnSlideListener != null) {
                mOnSlideListener.onSlide(this,
                        OnSlideListener.SLIDE_STATUS_START_SCROLL);
            }
            break;
        }
        case MotionEvent.ACTION_MOVE: {
            int deltaX = x - mLastX;
            int deltaY = y - mLastY;
            if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                // �����������������������򻬶�
                break;
            }

            // ���㻬���յ��Ƿ�Ϸ�����ֹ����Խ��
            int newScrollX = scrollX - deltaX;
            if (deltaX != 0) {
                if (newScrollX < 0) {
                    newScrollX = 0;
                } else if (newScrollX > mHolderWidth) {
                    newScrollX = mHolderWidth;
                }
                this.scrollTo(newScrollX, 0);
            }
            break;
        }
        case MotionEvent.ACTION_UP: {
            int newScrollX = 0;
            // �����������жϣ����ɿ��ֵ�ʱ�򣬻��Զ������߻������������ı߻���Ҫ����ǰ���λ��
            if (scrollX - mHolderWidth * 0.55 > 0) {
                newScrollX = mHolderWidth;
            }
            // �������յ�
            this.smoothScrollTo(newScrollX, 0);
            // ֪ͨ�ϲ㻬���¼�
            if (mOnSlideListener != null) {
                mOnSlideListener.onSlide(this,
                        newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF
                                : OnSlideListener.SLIDE_STATUS_ON);
            }
            break;
        }
        default:
            break;
        }

        mLastX = x;
        mLastY = y;
    }

    private void smoothScrollTo(int destX, int destY) {
        // ���������ָ��λ��
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        // ����ʱ������destX��Ч���������
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

}
