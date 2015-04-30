package com.embraceplus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.AssetManager;

import com.embraceplus.model.EmbraceMsg;
import com.embraceplus.model.NotificationDefineObj;
import com.embraceplus.model.NotificationObj;
import com.embraceplus.model.ThemeObj;
import com.google.gson.Gson;

public class PreDefineUtil {
	private static PreDefineUtil instant = new PreDefineUtil();
	Map<String,String> preDefinedMsgs = new HashMap<String,String>();
	Map<String,String> preDefinedThemes = new HashMap<String,String>();
	private PreDefineUtil()
	{
		getPreDefinedMsgs();
		getPreDefinedThemes();
	}
 
	public static PreDefineUtil getInstant()
	{
		return instant;
	}
	
	public HashMap<String,EmbraceMsg> getPreDefineMsgs()
	{
		HashMap<String,EmbraceMsg> preDefinedEffectCommandMapping;
		AssetManager assetManager = ContextUtil.getInstance().getAssets(); 
		StringBuffer preDefineMsgs = new StringBuffer();
		try {
			InputStream is = assetManager.open("preDefinedCommand");
			 InputStreamReader ir = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(ir);
		        String s;
		        while((s=br.readLine())!=null)
		        {
		        	
		        	preDefineMsgs.append(s);
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HashMap<String,EmbraceMsg>>() {  
        }.getType();  
        Gson gson = new Gson();  
        String jsonContent = preDefineMsgs.toString();
        System.out.println(jsonContent);
        jsonContent = jsonContent.replaceAll("\"", "\\\"");
      
        preDefinedEffectCommandMapping  = gson.fromJson(jsonContent,type);
        return preDefinedEffectCommandMapping;
	}
	
	public HashMap<String,NotificationObj> getPreDefineNotifications()
	{
		HashMap<String,NotificationObj> preDefinedEffectCommandMapping;
		AssetManager assetManager = ContextUtil.getInstance().getAssets(); 
		StringBuffer preDefineMsgs = new StringBuffer();
		try {
			InputStream is = assetManager.open("preDefinedNotification");
			 InputStreamReader ir = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(ir);
		        String s;
		        while((s=br.readLine())!=null)
		        {
		        	
		        	preDefineMsgs.append(s);
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HashMap<String,NotificationObj>>() {  
        }.getType();  
        Gson gson = new Gson();  
        String jsonContent = preDefineMsgs.toString();
        System.out.println(jsonContent);
        jsonContent = jsonContent.replaceAll("\"", "\\\"");
      
        preDefinedEffectCommandMapping  = gson.fromJson(jsonContent,type);
        return preDefinedEffectCommandMapping;
	}
	
	public HashMap<String,ThemeObj> getPreDefineTheme()
	{
		HashMap<String,ThemeObj> preDefinedThemeObj;
		AssetManager assetManager = ContextUtil.getInstance().getAssets(); 
		StringBuffer preDefineThemes = new StringBuffer();
		try {
			InputStream is = assetManager.open("preDefinedTheme");
			 InputStreamReader ir = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(ir);
		        String s;
		        while((s=br.readLine())!=null)
		        {
		        	
		        	preDefineThemes.append(s);
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HashMap<String,ThemeObj>>() {  
        }.getType();  
        Gson gson = new Gson();  
        String jsonContent = preDefineThemes.toString();
        System.out.println(jsonContent);
        jsonContent = jsonContent.replaceAll("\"", "\\\"");
      
        preDefinedThemeObj  = gson.fromJson(jsonContent,type);
        return preDefinedThemeObj;
	}

	public List<NotificationDefineObj> getNotificationDefineObjs() {
		
		List<NotificationDefineObj> preDefinedThemeObj;
		AssetManager assetManager = ContextUtil.getInstance().getAssets(); 
		StringBuffer str = new StringBuffer();
		try {
			InputStream is = assetManager.open("NotificationDefinition");
			 InputStreamReader ir = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(ir);
		        String s;
		        while((s=br.readLine())!=null)
		        {
		        	
		        	str.append(s);
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<NotificationDefineObj>>() {  
        }.getType();  
        Gson gson = new Gson();  
        String jsonContent = str.toString();
        System.out.println(jsonContent);
        jsonContent = jsonContent.replaceAll("\"", "\\\"");
      
        preDefinedThemeObj  = gson.fromJson(jsonContent,type);
        return preDefinedThemeObj;
	}
	
	public void getPreDefinedMsgs()
	{
		
		AssetManager assetManager = ContextUtil.getInstance().getAssets(); 
		StringBuffer str = new StringBuffer();
		try {
			InputStream is = assetManager.open("preDefinedMsgs");
			 InputStreamReader ir = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(ir);
		        String s;
		        while((s=br.readLine())!=null)
		        {
		        	
		        	str.append(s);
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] msgs = str.toString().split(",");
		for(String str1:msgs)
		{
			preDefinedMsgs.put(str1, str1);
		}
		
	}
	
	public void getPreDefinedThemes()
	{
		
		AssetManager assetManager = ContextUtil.getInstance().getAssets(); 
		StringBuffer str = new StringBuffer();
		try {
			InputStream is = assetManager.open("preDefinedThemes");
			 InputStreamReader ir = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(ir);
		        String s;
		        while((s=br.readLine())!=null)
		        {
		        	
		        	str.append(s);
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] msgs = str.toString().split(",");
		for(String str1:msgs)
		{
			preDefinedThemes.put(str1, str1);
		}
		
	}
	
	public boolean isPreDefinedMsg(String msgName)
	{
		return preDefinedMsgs.containsKey(msgName);
	}
	
	public boolean isPreDefinedTheme(String themeName)
	{
		return preDefinedThemes.containsKey(themeName);
	}
	
	
	
}
