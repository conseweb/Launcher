package com.bitants.launcherdev.launcher.view.icon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bitants.launcherdev.launcher.view.icon.icontype.IconType;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.launcher.view.icon.icontype.IconType;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.launcher.view.icon.icontype.IconType;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;

/**
 * 桌面View广播集中处理类
 * @author Michael
 * Date:2013-10-14下午5:43:57
 *
 */
public class LauncherIconViewReceiver extends BroadcastReceiver {
	
	protected LauncherIconView launcherIconView;
	
	/**
	 * 
	 */
	public LauncherIconViewReceiver(LauncherIconView launcherIconView) {
		super();
		this.launcherIconView = launcherIconView;
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent == null){
			return;
		}
		IconType iconType = launcherIconView.getIconType();
		if(iconType != null){
			iconType.handleBroadcastAction(context, intent, launcherIconView);
		}

	}

	
	/**
	 * 配置改变的监听器用于广播
	 * @author Michael
	 * @createtime 2013-8-7
	 */
	public static interface IconMaskUpdateListener {
		
		/**
		 * 更新整个View 包括重新获取图标 文字等
		 * @author Michael
		 * @createtime 2013-8-7
		 */
		void refreshUI();
		/**
		 * 文字大小、内容、颜色改变时调用
		 * @author Michael
		 * @createtime 2013-8-7
		 */
		void updateText();
		/**
		 * 大小图标、文字背景、是否支持蒙板配置改变时调用
		 * @author Michael
		 * @createtime 2013-8-7
		 */
		void udpateIconConfig();
		void updateNewMaskConfig();
		void updateHintConfig(int hintCount);
		void updateDraw();
	}
	
	
}
