package com.embraceplus.utils;

import android.graphics.drawable.Drawable;

public class Item {
		
		private String title;
		private int content;
		private boolean selected;
		

//		public Item(String title, int context) {
//			
//			this.title = title;
//			this.content = context;
//		}


		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		public int getContent() {
			if( content ==0)
			{
				System.out.println();
			}
			
			return content;
		}


		public void setDrawable(int content) {
			this.content = content;
		}


		public boolean isSelected() {
			return selected;
		}


		public void setSelected(boolean selected) {
			this.selected = selected;
		}

}
