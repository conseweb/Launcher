package com.nd.hilauncherdev.theme.assit;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>Title: 主题UI刷新控制器
 * <br>Author:caizp
 * <br>Date:2012-7-14下午02:27:29
 */
public class ThemeUIRefreshAssit {
	
	public static final String INTENT_REFRESH_ACTION = "ThemeAppRecomender.refresh";
	
	private static final ThemeUIRefreshAssit assit = new ThemeUIRefreshAssit();
	
	private List<ThemeUIRefreshListener> listeners = new ArrayList<ThemeUIRefreshListener>();
	
	/**
	 * <br>Description: 获取单例
	 * <br>Author:caizp
	 * <br>Date:2012-7-14下午02:28:56
	 * @return
	 */
	public static ThemeUIRefreshAssit getInstance() {
		return assit;
	}
	
	/**
	 * <br>Description: 根据当前刷新桌面UI
	 * <br>Author:caizp
	 * <br>Date:2012-7-14下午02:34:53
	 */
	public void refreshLauncherThemeUI(){
		for(int i=0; i<listeners.size(); i++) {
			ThemeUIRefreshListener listener = listeners.get(i);
			listener.applyTheme();
		}
	}
	
	/**
	 * <br>Description: 注册主题刷新监听器
	 * <br>Author:caizp
	 * <br>Date:2012-7-14下午02:31:57
	 * @param listener
	 */
	public void registerRefreshListener(ThemeUIRefreshListener listener){
		if(null != listener){
			listeners.add(listener);
		}
	}
	
	/**
	 * <br>Description: 移除主题刷新监听
	 * <br>Author:caizp
	 * <br>Date:2012-7-14下午02:33:53
	 * @param listener
	 */
	public void unregisterRefreshListener(ThemeUIRefreshListener listener){
		if(null != listener){
			listeners.remove(listener);
		}
	}
	
	/**
	 * <br>Description: 重置主题刷新监听器列表
	 * <br>Author:caizp
	 * <br>Date:2012-7-14下午02:33:18
	 */
	public void resetRefreshListeners(){
		listeners.clear();
	}
	
}
