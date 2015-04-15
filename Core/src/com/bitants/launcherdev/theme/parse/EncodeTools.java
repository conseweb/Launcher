package com.bitants.launcherdev.theme.parse;

import com.bitants.launcherdev.launcher.config.BaseConfig;

import android.util.Log;

public class EncodeTools {

	private static native byte[] getItemInfos(String name);
	private static native byte[] getResourceByPos(String name,int startPos,int len);
	private static native byte[] getResourceByName(String name,String resName);
	private static native boolean addImei(String name,String imei);
	
	public static ITEM [] getItemInfo(String name)
	{
		byte[] result = getItemInfos(name);
		
		if(result==null)
		{
			return null;
		}
		
		int itemCount=result.length/144;
		ITEM[] items;
		items=new ITEM[itemCount];
		for (int i = 0; i < itemCount; i++) {
			items[i] = new ITEM();
		}
		
		int index=0;
		for(int i=0;i<itemCount;i++)
		{
			ITEM item=items[i];
			item.name=new String(result, index, 64);
			index=index+128;
			
		
			
			item.startPost=byteToInt2(result,index);
			index=index+4;
			
			item.len=byteToInt2(result,index);
			index=index+4;
			
			item.type=new String(result, index, 8);
			index=index+8;
			Log.e("zhou", "EncodeTools:name"+item.name);
			Log.e("zhou" ,"EncodeTools:startPos="+item.startPost+" len="+item.len+" type="+item.type);
		}
		
		return items;
	}
	
	
	public  static byte[] getResource(String name,int startPos,int len)
	{
		return getResourceByPos(name, startPos, len);
	}
	
	
	public  static byte[] getResource(String name,String resname)
	{
		return getResourceByName(name, resname);
	}
	
	public static  boolean WriteImei(String name ,String imei)
	{
		return addImei(name, imei);
	}
	public static class ITEM
	{
		public String name;
		public int startPost;
		public int len;
		public String type;
		
	}

	public static int byteToInt2(byte[] b,int start) {

		int mask = 0x00ff;
		int result = 0;
		int temp = 0;
		int bit=0;
		for (int i = start; i < 4+start; i++) {
			temp=((b[i])&mask);
			temp=temp<<(8*bit);
			result=result+temp;
			bit++;
		}
		return result;
	}
	
	static {
		try {
			System.loadLibrary("Encode");
		} catch (Throwable t) {
			t.printStackTrace();
			BaseConfig.isThemeDecoderAvaliable  = false;
		}
	}
	
}
