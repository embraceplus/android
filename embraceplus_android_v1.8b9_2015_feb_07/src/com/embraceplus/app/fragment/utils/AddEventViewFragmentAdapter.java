package com.embraceplus.app.fragment.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.embraceplus.app.R;
import com.embraceplus.database.NotificationDefinitionDB;
import com.embraceplus.model.NotificationSelectedObj;
import com.embraceplus.utils.ResourceUtils;

public class AddEventViewFragmentAdapter extends BaseAdapter {
	private Context context;
	private List<NotificationSelectedObj> list;
	private LayoutInflater mInflater;
	public AddEventViewFragmentAdapter(Context context,List<NotificationSelectedObj> list){
		this.context = context;
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null){
			convertView = mInflater.inflate(R.layout.add_event_list_item, null);
			holder = new ViewHolder();
			holder.textView = (TextView)convertView.findViewById(R.id.title);
			holder.image = (ImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		}else{
			
			holder = (ViewHolder) convertView.getTag(); 
		}
		String notificationName = list.get(position).getNotificationName();
		holder.textView.setText(notificationName);
		holder.textView.setTextColor(android.graphics.Color.WHITE);
		
//		String resourceName = DbBuilder.getInstant().getResourceNameByNotificatioinName(notificationName);
		String resourceName = NotificationDefinitionDB.getInstance().getResourceNameByNotificatioinName(notificationName);
		int resoureceId =  -1;
		if (notificationName.equalsIgnoreCase("Calls"))
		{
			resoureceId = R.drawable.call_icon;
		}else if (notificationName.equalsIgnoreCase("Clock"))
		{
			resoureceId = R.drawable.clock_icon;
		}else
		{
			resoureceId = ResourceUtils.getInstance().getResourceId(resourceName);
		}
		
		holder.image.setBackgroundResource(resoureceId);
		//holder.image.setImageBitmap(BitmapFactory.decodeResource(ContextUtil.getInstance().getResources(),resoureceId));
		/*if (list.get(position).isSelected())
		{
			//holder.image.setImageAlpha(180);
			holder.image.setBackgroundColor(Color.BLACK);
		}else
		{
			holder.image.setBackgroundColor(Color.GRAY);
		}*/
		// TODO Auto-generated method stub
		return convertView;
	}
	
	
	private class ViewHolder {  
		private TextView textView;
		private ImageView image;
    }
}
