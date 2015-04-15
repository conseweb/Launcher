package com.nd.launcherdev.launcher.support;

import java.util.ArrayList;
import java.util.List;

import com.nd.launcherdev.kitset.util.DateUtil;
import com.nd.launcherdev.kitset.util.TelephoneUtil;
import com.nd.launcherdev.launcher.config.preference.BaseConfigPreferences;

import android.content.Context;
import com.nd.launcherdev.kitset.util.TelephoneUtil;
import com.nd.launcherdev.launcher.config.preference.BaseConfigPreferences;


/**
 * Description: 桌面启动时，回调注册的监听
 * Author: guojy
 * Date: 2013-10-25 下午3:27:44
 */
public class LauncherOnStartDispatcher {

	private static LauncherOnStartDispatcher instance = new LauncherOnStartDispatcher();
	
	private List<OnLauncherStartListener> listenerList = null;
	
	private static final long ONEHOUR = 1000 * 60 * 60; 
	
	public static LauncherOnStartDispatcher getInstance(){
		return instance;
	}
	
	private LauncherOnStartDispatcher(){
		if(listenerList == null){
			listenerList = new ArrayList<OnLauncherStartListener>();
		}
	}
	
	public void dispatch(final Context ctx){
		try{
			long lastTime = BaseConfigPreferences.getInstance().getLauncherOnStartDayTime();
			long lastTimeNotNetWork = BaseConfigPreferences.getInstance().getLauncherOnStartDayTimeNotNetWork();
			if(lastTimeNotNetWork <= 0 && lastTime > 0){//lastTimeNotNetWork为后添加数据，为老用户设置初值
				BaseConfigPreferences.getInstance().setLauncherOnStartDayTimeNotNetWork(lastTime);
				lastTimeNotNetWork = lastTime;
			}
			long currentTime = DateUtil.getTodayTime();
			boolean resetDayStatTime = false;
			boolean resetDayStatTimeNotNetWork = false;
			
			for(OnLauncherStartListener listener : listenerList){
				if(listener == null)
					continue;
				int type = listener.getType();
				if(type == OnLauncherStartListener.TYPE_EVERY_TIME){//每次启动都调用		
					listener.onLauncherStart(ctx);
				} else if(type == OnLauncherStartListener.TYPE_ONCE_A_DAY && TelephoneUtil.isNetworkAvailable(ctx)){//网络开启状态下，每天调用一次
					if (currentTime - lastTime >= ONEHOUR * 24) {
						listener.onLauncherStart(ctx);
						resetDayStatTime = true;
					}
				} else if(type == OnLauncherStartListener.TYPE_ONCE_A_DAY_NOT_NETWORK){//无论网络是否开启，每天调用一次
					if (currentTime - lastTimeNotNetWork >= ONEHOUR * 24) {
						listener.onLauncherStart(ctx);
						resetDayStatTimeNotNetWork = true;
					}
				}
			}
			
			if(resetDayStatTime){
				BaseConfigPreferences.getInstance().setLauncherOnStartDayTime(currentTime);
			}
			
			if(resetDayStatTimeNotNetWork){
				BaseConfigPreferences.getInstance().setLauncherOnStartDayTimeNotNetWork(currentTime);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void addListener(OnLauncherStartListener listener){
		if(!listenerList.contains(listener)){			
			listenerList.add(listener);
		}
	}
	
	public void removeListener(OnLauncherStartListener listener){
		listenerList.remove(listener);
	}
	
	public interface OnLauncherStartListener{
		/**
		 * 无论有没网络，每次启动都调用
		 */
		public static final int TYPE_EVERY_TIME = 0;
		/**
		 * 有网络情况下，每天调用一次
		 */
		public static final int TYPE_ONCE_A_DAY = 1;
		/**
		 * 无论有没网络，每天调用一次
		 */
		public static final int TYPE_ONCE_A_DAY_NOT_NETWORK = 2;
		
		void onLauncherStart(Context ctx);
		/**
		 * Description: 返回调用类型：TYPE_EVERY_TIME(每次启动都调用), TYPE_ONCE_A_DAY(每天调用一次)
		 * Author: guojy
		 * Date: 2013-10-28 下午3:10:30
		 */
		int getType();
	}
}
