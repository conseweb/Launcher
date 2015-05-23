package com.bitants.common.theme.assit;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>Title: 主题UI刷新控制器
 */
public class ThemeUIRefreshAssit {
	
	public static final String INTENT_REFRESH_ACTION = "ThemeAppRecomender.refresh";
	
	private static final ThemeUIRefreshAssit assit = new ThemeUIRefreshAssit();
	
	private List<ThemeUIRefreshListener> listeners = new ArrayList<ThemeUIRefreshListener>();
	
	/**
	 * <br>Description: 获取单例
	 * @return
	 */
	public static ThemeUIRefreshAssit getInstance() {
		return assit;
	}
	
	/**
	 * <br>Description: 根据当前刷新桌面UI
	 */
	public void refreshLauncherThemeUI(){
		for(int i=0; i<listeners.size(); i++) {
			ThemeUIRefreshListener listener = listeners.get(i);
			listener.applyTheme();
		}
	}
	
	/**
	 * <br>Description: 注册主题刷新监听器
	 * @param listener
	 */
	public void registerRefreshListener(ThemeUIRefreshListener listener){
		if(null != listener){
			listeners.add(listener);
		}
	}
	
	/**
	 * <br>Description: 移除主题刷新监听
	 * @param listener
	 */
	public void unregisterRefreshListener(ThemeUIRefreshListener listener){
		if(null != listener){
			listeners.remove(listener);
		}
	}
	
	/**
	 * <br>Description: 重置主题刷新监听器列表
	 */
	public void resetRefreshListeners(){
		listeners.clear();
	}
	
}
