package com.embraceplus.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.embraceplus.app.R;

public class SelectPicPopupWindow extends PopupWindow {


	private Button btn_edit, btn_add, btn_cancel;
	private View mMenuView;

	public SelectPicPopupWindow(Activity context,OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.alert_dialog, null);
		btn_edit = (Button) mMenuView.findViewById(R.id.button_edit);
		
		btn_edit.setVisibility(View.GONE);
		
		btn_add = (Button) mMenuView.findViewById(R.id.btn_add);
		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		//ȡ��ť
		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//��ٵ�����
				dismiss();
			}
		});
		//���ð�ť����
		btn_add.setOnClickListener(itemsOnClick);
		btn_edit.setOnClickListener(itemsOnClick);
		//����SelectPicPopupWindow��View
		this.setContentView(mMenuView);
		//����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.FILL_PARENT);
		//����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		//����SelectPicPopupWindow�������嶯��Ч��
		this.setAnimationStyle(R.style.PopupAnimation);
		//ʵ��һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0x00000000);
		//����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		//mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ�����������ٵ�����
		mMenuView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});

	}

	public Button getBtn_edit() {
		return btn_edit;
	}

	public void setBtn_edit(Button btn_edit) {
		this.btn_edit = btn_edit;
	}

	public Button getBtn_add() {
		return btn_add;
	}

	public void setBtn_add(Button btn_add) {
		this.btn_add = btn_add;
	}

	public Button getBtn_cancel() {
		return btn_cancel;
	}

	public void setBtn_cancel(Button btn_cancel) {
		this.btn_cancel = btn_cancel;
	}

}
