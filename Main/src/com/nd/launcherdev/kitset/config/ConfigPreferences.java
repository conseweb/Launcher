package com.nd.launcherdev.kitset.config;

import android.content.SharedPreferences;

import com.nd.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.nd.launcherdev.launcher.config.preference.BaseConfigPreferences;

/**
 * 通用配置文件
 */
public class ConfigPreferences extends BaseConfigPreferences {
	private static ConfigPreferences ap;
	private static SharedPreferences sp;

    
    /**
     * 获取推送的间隔时间，单位秒
     */
    private static final String KEY_PUSH_INTERVAL = "key_push_interval";
    
    /**
     * 桌面推送消息的版本
     */
    private static final String KEY_PUSH_NOTIFY_ID = "key_push_notify_id";
    private static final String KEY_PUSH_POPUP_ID = "key_push_popup_id";
    private static final String KEY_PUSH_NOTIFY_ICON_ID = "key_push_notify_icon_id";
    
   
	/**
	 * 桌面第0屏配置
	 */
	protected ConfigPreferences() {
		super();
	}

	
	public synchronized static ConfigPreferences getInstance() {
		if(sp == null){
			sp = BaseConfigPreferences.getInstance().getBaseSP();
		}
		if (ap == null){			
			ap = new ConfigPreferences();
		}
		return ap;
	}
	
	public SharedPreferences getSP() {
		return sp;
	}
	
	public int getPushInterval(){
		//默认间隔时间为4小时
		return sp.getInt(KEY_PUSH_INTERVAL, 14400);
	}
	
	public void setPushInterval(int version){
		sp.edit().putInt(KEY_PUSH_INTERVAL, version).commit();
	}
	
	public int getPushNotifyId(){
		return sp.getInt(KEY_PUSH_NOTIFY_ID, -1);
	}
	
	public void setPushNotifyId(int id){
		sp.edit().putInt(KEY_PUSH_NOTIFY_ID, id).commit();
	}
	
	public int getPushPopupId(){
		return sp.getInt(KEY_PUSH_POPUP_ID, -1);
	}
	
	public void setPushPopupId(int id){
		sp.edit().putInt(KEY_PUSH_POPUP_ID, id).commit();
	}
	
	public int getPushNotifyIconId(){
		return sp.getInt(KEY_PUSH_NOTIFY_ICON_ID, -1);
	}
	
	public void setPushNotifyIconId(int id){
		sp.edit().putInt(KEY_PUSH_NOTIFY_ICON_ID, id).commit();
	}
	
}
