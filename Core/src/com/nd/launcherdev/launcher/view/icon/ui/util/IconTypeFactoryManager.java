package com.nd.launcherdev.launcher.view.icon.ui.util;

import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.model.BaseLauncherSettings.BaseLauncherColumns;
import com.nd.launcherdev.launcher.view.icon.icontype.IconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.AppIconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.DockDefaultFourIconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.HintIconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.SystemShortcutIconType;
import com.nd.launcherdev.launcher.view.icon.ui.folder.BaseFolderReceiver;
import com.nd.launcherdev.theme.adaption.ThemeIconIntentAdaptation;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.view.icon.icontype.IconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.AppIconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.DockDefaultFourIconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.HintIconType;
import com.nd.launcherdev.launcher.view.icon.icontype.impl.SystemShortcutIconType;
import com.nd.launcherdev.launcher.view.icon.ui.folder.BaseFolderReceiver;
import com.nd.launcherdev.theme.adaption.ThemeIconIntentAdaptation;

/**
 * IconMask帮助类
 * @author Michael
 * @createtime 2013-7-26
 */
public class IconTypeFactoryManager {
	
	
	/**
	 * 子类继承来实现对不同类型图标的适配
	 * @author Michael
	 * Date:2014-3-21下午3:36:40
	 *
	 */
	public static class IconTypeFactory{
		
		public IconType getIconType(Object o){
			if(o != null){
				if(o instanceof ApplicationInfo){
					ApplicationInfo info = (ApplicationInfo)o;
					
					if(BaseLauncherColumns.ITEM_TYPE_APPLICATION == info.itemType){
						String type = ThemeIconIntentAdaptation.getInstance().getAppHintType(info.intent);
						if(!StringUtil.isEmpty(type)){
							return new HintIconType(type);
						}
						return new AppIconType();
					}
					
					//系统快捷 包括dock栏默认的4个图标
					if(BaseLauncherColumns.ITEM_TYPE_SHORTCUT == info.itemType){
						if(isDockBarFourIcon(info)){
							String type = ThemeIconIntentAdaptation.getInstance().getAppHintType(info.intent);
							return new DockDefaultFourIconType(type);
						}else{ //普通系统快捷
							return new SystemShortcutIconType();
						}
					}
				}
			}
			return null;
		}
	}
	
	
	public static class FolderReceiverFactory{
		public BaseFolderReceiver getFolderReceiver(){
			return new BaseFolderReceiver();
		}
	}

	
	
	/**
	 * 标准应用程序信息 <br>
	 * Author:ryan <br>
	 * Date:2012-11-26上午10:39:46
	 */
	public static ApplicationInfo getAppFromTag(Object tag) {
		if (tag == null)
			return null;

		if (!(tag instanceof ApplicationInfo))
			return null;

		return (ApplicationInfo) tag;
	}
	
	/**
	 * 是否需要接受hint广播
	 * @author Michael
	 * Date:2013-10-14下午5:20:52
	 *  @param appInfo
	 *  @return
	 */
	public static boolean isAppHintNeed(ApplicationInfo appInfo){
		if(appInfo != null){
			String type = ThemeIconIntentAdaptation.getInstance().getAppHintType(appInfo.intent);
			if(type != null){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否dock栏四图标
	 * @author Michael
	 * Date:2014-3-21下午5:21:01
	 *  @param mAppInfo
	 *  @return
	 */
	public static boolean isDockBarFourIcon(ApplicationInfo mAppInfo){
		if (mAppInfo == null || mAppInfo.intent == null) {
			return false;
		}
		if(BaseLauncherColumns.ITEM_TYPE_SHORTCUT == mAppInfo.itemType){
			return ThemeIconIntentAdaptation.getDefaultDockAppThemeKey(mAppInfo.intent.toUri(0)) != null;
		}
		return false;
	}
	
	
	
	
	/**
	 * 根据图标类型获取不同的behavior来进行图标刷新
	 * @author Michael
	 * Date:2013-10-28下午3:36:07
	 *  @param o
	 *  @return
	 */
	public static IconType getIconType(Object o){
		return BeansContainer.getInstance().getDefaultIconTypeFactory().getIconType(o);
	}

	/**
	 * 判断info是否需要画蒙板
	 * @author Michael
	 * Date:2014-3-7下午4:49:36
	 *  @param info
	 *  @return
	 */
	public static boolean getDrawMask(ApplicationInfo info){
		if(info == null){
			return false;
		}
		if(info.customIcon){
			return false;
		}
		if(!info.useIconMask){
			return false;
		}
		return true;
	}


}
