package com.embraceplus.app.fragment.utils;

import com.embraceplus.app.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MySwitch extends LinearLayout implements OnClickListener {

	protected boolean isCheck = false;
//	private String checkedText = "";
//	private String uncheckedText = "";
//	private TextView checkedTextView, uncheckedTextView;
	SwitchOnCheckedChangeListener onCheckedChangeListener;
	int swithOnColor, swithOffColor;

	public MySwitch(Context context) {
		this(context, null);
	}

	public MySwitch(Context context, AttributeSet attrs) {
		super(context, attrs);

		swithOnColor = context.getResources().getColor(R.color.switch_on);
		swithOffColor = context.getResources().getColor(R.color.switch_off);

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.my_switch, null);
		this.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MySwitch);
//		checkedText = a.getString(R.styleable.MySwitch_checkedtext);
//		uncheckedText = a.getString(R.styleable.MySwitch_uncheckedtext);

//		checkedTextView = (TextView) view.findViewById(R.id.checkedTextView);
//		uncheckedTextView = (TextView) view.findViewById(R.id.uncheckedTextView);
//		if (checkedText != null) {
//			checkedTextView.setText(checkedText);
//		}
//
//		if (uncheckedText != null) {
//			uncheckedTextView.setText(uncheckedText);
//		}

//		float textsize = a.getDimension(R.styleable.MySwitch_textSize, 14);
//		checkedTextView.setTextSize(textsize);
//		uncheckedTextView.setTextSize(textsize);

		setCheckButton();
		a.recycle();

		this.setOnClickListener(this);
	}

	public boolean isChecked() {
		return isCheck;
	}

	public void setChecked(boolean checked) {
		this.isCheck = checked;
		setCheckButton();
	}

	public void setOnCheckedChangeListener(SwitchOnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	@Override
	public void onClick(View v) {
		isCheck = !isCheck;
		setCheckButton();

		onCheckedChangeListener.onCheckedChanged(this, isCheck);
	}

	private void setCheckButton() {
		if (isCheck) {
			this.setBackgroundResource(R.drawable.on1);
//			checkedTextView.setTextColor(swithOnColor);
//			uncheckedTextView.setTextColor(swithOffColor);
		} else {
			this.setBackgroundResource(R.drawable.off1);
//			checkedTextView.setTextColor(swithOffColor);
//			uncheckedTextView.setTextColor(swithOnColor);
		}
	}

}
