package com.embraceplus.model;

import com.embraceplus.utils.SlideView;

public class MessageItem {
	public int iconRes;
	public String title;
	public int arrow;
	public SlideView slideView;
	public int getIconRes() {
		return iconRes;
	}
	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getArrow() {
		return arrow;
	}
	public void setArrow(int arrow) {
		this.arrow = arrow;
	}
	public SlideView getSlideView() {
		return slideView;
	}
	public void setSlideView(SlideView slideView) {
		this.slideView = slideView;
	}
}
