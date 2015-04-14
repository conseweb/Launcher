package com.nd.hilauncherdev.launcher.view.icon.icontype;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;

import com.nd.hilauncherdev.launcher.broadcast.HiBroadcastReceiver;
import com.nd.hilauncherdev.launcher.config.preference.SettingsConstants;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconView;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.impl.DockbarCell;
import com.nd.hilauncherdev.launcher.view.icon.ui.impl.IconMaskTextView;

/**
 * @author Michael
 * Date:2014-3-20上午10:28:33
 *
 */
public class IconType {
	
	public static IntentFilter[] noActionIntentFilters = new IntentFilter[]{};

	/**
	 * 注册广播的action
	 * @author Michael
	 * Date:2014-3-20上午11:53:34
	 *  @param launcherIconView
	 *  @return
	 */
	public IntentFilter[] getIntentFilter(LauncherIconView launcherIconView){
		if(launcherIconView instanceof IconMaskTextView
				|| launcherIconView instanceof DockbarCell){
			
			IntentFilter intentFilter = new IntentFilter();
			//接受刷新图标广播
			intentFilter.addAction(HiBroadcastReceiver.REFRESH_ICON_ACTION);
			//刷新文本广播
			intentFilter.addAction(SettingsConstants.ACTION_REFRESH_APP_NAME);
			//文件夹样式变化
			intentFilter.addAction(HiBroadcastReceiver.ACTION_CHANGE_FOLDER_STYLE);
			return new IntentFilter[]{intentFilter};
		}
		return noActionIntentFilters;
	}
	
	
	/**
	 * 处理广播的消息 返回是否处理成功 处理成功不再处理
	 * @author Michael
	 * Date:2014-3-20上午11:53:18
	 *  @param launcherIconView
	 */
	public boolean handleBroadcastAction(Context context, Intent intent, LauncherIconView launcherIconView){
		if(intent == null){
			return true;
		}
		String action = intent.getAction();
		//接受图标刷新广播
		if(HiBroadcastReceiver.REFRESH_ICON_ACTION.equals(action)){
			launcherIconView.udpateIconConfig();
			launcherIconView.updateText();
			launcherIconView.refreshUI();
			return true;
		}
		//文字变化广播
		if(SettingsConstants.ACTION_REFRESH_APP_NAME.equals(action)){
			launcherIconView.updateText();
			return true;
		}
		//文件夹样式变化
		if(HiBroadcastReceiver.ACTION_CHANGE_FOLDER_STYLE.equals(action)){
			launcherIconView.udpateIconConfig();
			return true;
		}
		return false;
	}
	
	public Bitmap refreshIcon(final LauncherIconViewConfig config, Object tag,
			final Context context, final Handler handler){
		return null;
	}
	
	/**
	 * 适用于无匣子桌面新安装app的new提示和两个热门类型的判断
	 * @author Michael
	 * Date:2014-3-24下午3:35:59
	 *  @param info
	 *  @param config
	 */
	public void ajustConfig(Context context, ApplicationInfo info, LauncherIconViewConfig config){
		
	}
	
	/**
	 * 提供一个画布接口供扩展
	 * @author Michael
	 * Date:2014-3-24下午3:59:49
	 *  @param context
	 *  @param info
	 *  @param canvas
	 *  @param config
	 *  @param data
	 */
	public void drawCanvas(Context context, ApplicationInfo info, Canvas canvas, LauncherIconView view, LauncherIconViewConfig config,
			LauncherIconData data){
		
	}

}
