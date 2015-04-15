package com.bitants.launcherdev.launcher.broadcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bitants.launcherdev.launcher.BaseLauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Launcher动态注册广播监听管理
 * Author: guojy
 */
public class LauncherBroadcastReceiverManager{
	private static final String TAG = "LauncherBroadcastReceiverManager";
	private static LauncherBroadcastReceiverManager instance = new LauncherBroadcastReceiverManager();
	
	private BaseLauncher launcher;
	
	/**
	 * 带DataScheme的filter广播监听Map(可能包含多个监听，一种DataScheme对应一个监听)
	 */
	private Map<String, LauncherBroadcastReceiver> dataSchemeReceiverMap;
	/**
	 * 普通带action的filter广播监听(只有一个监听)
	 */
	private LauncherBroadcastReceiver actionReceiver = new LauncherBroadcastReceiver();
	
	public interface LauncherBroadcastReceiverHandler{
		public void onReceive(Context context, Intent intent);
	}
	
	private LauncherBroadcastReceiverManager() {
	}
	
	public void setLauncher(BaseLauncher launcher) {
		this.launcher = launcher;
	}
	
	public static LauncherBroadcastReceiverManager getInstance(){
		return instance;
	}
	
	/**
	 * 注册广播监听
	 * @param receiverHandler
	 * @param filter 该filter可带有cation或datasScheme过滤
	 */
	public void registerReceiver(LauncherBroadcastReceiverHandler receiverHandler, IntentFilter filter){
		try{
			boolean needToUnregister = false;
			if(filter.countDataSchemes() > 0){
				if(dataSchemeReceiverMap == null){
					dataSchemeReceiverMap = new HashMap<String, LauncherBroadcastReceiver>();
				}
				String dataScheme = filter.getDataScheme(0);
				if(dataSchemeReceiverMap.containsKey(dataScheme)){
					needToUnregister = true;
				}
				for(int i = 0; i < filter.countActions(); i ++){
					registerDataSchemeReceiver(receiverHandler, filter.getAction(i), dataScheme);
				}
				LauncherBroadcastReceiver receiver = dataSchemeReceiverMap.get(dataScheme);
				if(needToUnregister){
					launcher.unregisterReceiver(receiver);
				}
				launcher.registerReceiver(receiver, receiver.actionFilter);
			}else{
				if(actionReceiver.actionReceiverMap.size() > 0){	
					needToUnregister = true;
				}
				for(int i = 0; i < filter.countActions(); i ++){
					registerActionReceiver(receiverHandler, filter.getAction(i));
				}
				if(needToUnregister){
					try{
						launcher.unregisterReceiver(actionReceiver);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				launcher.registerReceiver(actionReceiver, actionReceiver.actionFilter);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//桌面重启
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		
	}
	
	/**
	 * 取消注册广播监听
	 * @param receiverHandler
	 */
	public void unregisterReceiver(LauncherBroadcastReceiverHandler receiverHandler){
		try{
			unregisterDataSchemeReceiver(receiverHandler);
			unregisterActionReceiver(receiverHandler, actionReceiver);
		}catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册带DataScheme过滤的广播监听
	 * @param receiverHandler
	 * @param action
	 * @param dataScheme
	 */
	private void registerDataSchemeReceiver(LauncherBroadcastReceiverHandler receiverHandler, String action, String dataScheme){
		if(dataSchemeReceiverMap == null){
			dataSchemeReceiverMap = new HashMap<String, LauncherBroadcastReceiver>();
		}
		
		LauncherBroadcastReceiver actionReceiver = null;
		if(!dataSchemeReceiverMap.containsKey(dataScheme)){//新建广播接收
			actionReceiver = new LauncherBroadcastReceiver();
			dataSchemeReceiverMap.put(dataScheme, actionReceiver);
			actionReceiver.actionFilter.addDataScheme(dataScheme);
		}
		
		if(actionReceiver == null){			
			actionReceiver = dataSchemeReceiverMap.get(dataScheme);
		}
		
		registerActionReceiver(receiverHandler, action, actionReceiver);
	}
	
	/**
	 * 注册普通只有action过滤的广播监听
	 * @param receiverHandler
	 * @param action
	 */
	private void registerActionReceiver(LauncherBroadcastReceiverHandler receiverHandler, String action){
		registerActionReceiver(receiverHandler, action, actionReceiver);
	}
	
	private void registerActionReceiver(LauncherBroadcastReceiverHandler receiverHandler, String action, LauncherBroadcastReceiver actionReceiver){
		Map<String, List<LauncherBroadcastReceiverHandler>> actionReceiverMap = actionReceiver.actionReceiverMap;
		IntentFilter actionFilter = actionReceiver.actionFilter;
		
		if(!actionReceiverMap.containsKey(action)){
			actionReceiverMap.put(action, new ArrayList<LauncherBroadcastReceiverHandler>());
			actionFilter.addAction(action);
		}
		List<LauncherBroadcastReceiverHandler> list = actionReceiverMap.get(action);
		if(!list.contains(receiverHandler)){			
			list.add(receiverHandler);
		}
	}
	
	/**
	 * 取消注册带DataScheme过滤的广播监听
	 * @param receiverHandler
	 */
	private void unregisterDataSchemeReceiver(LauncherBroadcastReceiverHandler receiverHandler){
		if(dataSchemeReceiverMap == null)
			return;
				
		Iterator<Map.Entry<String, LauncherBroadcastReceiver>> it = dataSchemeReceiverMap.entrySet().iterator();  
		while (it.hasNext()) {  
			Map.Entry<String, LauncherBroadcastReceiver> entry = it.next();  
			LauncherBroadcastReceiver actionReceiver = entry.getValue();
			unregisterActionReceiver(receiverHandler, actionReceiver);
			if (actionReceiver.actionReceiverMap.size() == 0) {
				launcher.unregisterReceiver(actionReceiver);
				it.remove();
			}
		}
	}
	
	private void unregisterActionReceiver(LauncherBroadcastReceiverHandler receiverHandler, LauncherBroadcastReceiver actionReceiver){
		Map<String, List<LauncherBroadcastReceiverHandler>> actionReceiverMap = actionReceiver.actionReceiverMap;
		
		Iterator<Map.Entry<String, List<LauncherBroadcastReceiverHandler>>> it = actionReceiverMap.entrySet().iterator();  
		while (it.hasNext()) {  
			Map.Entry<String, List<LauncherBroadcastReceiverHandler>> entry = it.next();  
			List<LauncherBroadcastReceiverHandler> list = entry.getValue();
			list.remove(receiverHandler);
			if (list.size() <=0) {
				it.remove();
			}
		}
	}
	
	class LauncherBroadcastReceiver extends BroadcastReceiver{
		public IntentFilter actionFilter = new IntentFilter();
		public Map<String, List<LauncherBroadcastReceiverHandler>> actionReceiverMap = 
				new HashMap<String, List<LauncherBroadcastReceiverHandler>>();
		
		/**
		 * StickyBroadcast做过滤处理
		 */
		private static final long stickyBroadcastGap = 1000;//两次接收的间隔时间
		private long stickyBroadcastReceiveLastTime = 0;//上次接收时间
		private int stickyBroadcastReceiverSize = 0;//上次接收该广播的数量
		
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				String action = intent.getAction();
				if(action != null && actionReceiverMap.containsKey(action)){
					List<LauncherBroadcastReceiverHandler> list = actionReceiverMap.get(action);
					if(list == null){
						return;
					}
					
					//StickyBroadcast做过滤处理，以免重复接收广播
					if(isStickyBroadcast(action)){
						long nowTime = System.currentTimeMillis();
						long lastTime = stickyBroadcastReceiveLastTime;
						stickyBroadcastReceiveLastTime = nowTime;
						if(nowTime - lastTime < stickyBroadcastGap && stickyBroadcastReceiverSize == list.size()){
							return;
						}else{
							stickyBroadcastReceiverSize = list.size();
						}
					}
					
					//分发广播
					for(LauncherBroadcastReceiverHandler receiver : list){
						receiver.onReceive(context, intent);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		/**
		 * 是否为StickyBroadcast
		 * @param action
		 * @return
		 */
		private boolean isStickyBroadcast(String action){
			return Intent.ACTION_BATTERY_CHANGED.equals(action);
		}
	}
	
	/**
	 * 打印注册的广播监听类型
	 */
	public void printAllActions(){
		IntentFilter filter = actionReceiver.actionFilter;
		for(int i = 0; i < filter.countActions(); i ++){
			Log.e("Action", filter.getAction(i) + "    count : " + actionReceiver.actionReceiverMap.get(filter.getAction(i)).size());
		}
		Log.e("printAllAction", "==================");
		if(dataSchemeReceiverMap != null){
			Set<String> actions = dataSchemeReceiverMap.keySet();
			Iterator<String> iter = actions.iterator();
			while(iter.hasNext()){
				String action = iter.next();
				LauncherBroadcastReceiver receiver = dataSchemeReceiverMap.get(action);
				IntentFilter filter2 = receiver.actionFilter;
				Log.e("DataScheme", filter2.getDataScheme(0));
				for(int i = 0; i < filter2.countActions(); i ++){
					Log.e("Action", filter2.getAction(i));
				}
			}
		}
	}
	
	/**
	 * 清理所有广播
	 */
	public void clear() {
		try {
			clearDataSchemeReceiver();
			clearActionReceiver();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void clearDataSchemeReceiver() {
		if(dataSchemeReceiverMap == null)
			return;
				
		Iterator<Map.Entry<String, LauncherBroadcastReceiver>> it = dataSchemeReceiverMap.entrySet().iterator();  
		while (it.hasNext()) {  
			Map.Entry<String, LauncherBroadcastReceiver> entry = it.next();  
			LauncherBroadcastReceiver actionReceiver = entry.getValue();
			actionReceiver.actionReceiverMap.clear();
			launcher.unregisterReceiver(actionReceiver);
			it.remove();
		}
	}
	
	private void clearActionReceiver() {
		actionReceiver.actionReceiverMap.clear();
		launcher.unregisterReceiver(actionReceiver);
	}
}
