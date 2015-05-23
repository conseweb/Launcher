package com.bitants.launcherdev.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.common.kitset.util.BaseBitmapUtils;
import com.bitants.launcherdev.launcher.LauncherSettings;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.support.BaseIconCache;
import com.bitants.common.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.common.theme.assit.BaseThemeAssit;

import java.util.List;

/**
 * UIHandler工厂
 */
public class UIHandlerFactory {
	static final String TAG = "UIHandlerFactory";

	/**
	 * 延迟批量刷新应用程序图标
	 * @param refresh
	 * @param ctx
	 * @param mq
	 * @param mAllAppsList
	 * @param handler
	 */
	public static Runnable getRefreshIconRunnable(final int refresh, final Context ctx, final MessageQueue mq, final List<ApplicationInfo> mAllAppsList, final IdleHandler handler) {
		return new Runnable() {
			public void run() {
				final PackageManager pm = ctx.getPackageManager();
				final BaseIconCache mIconCache = (BaseIconCache) BaseConfig.getIconCache();

				int i = 0;
				for (int index = 0 ; index < mAllAppsList.size() ; index++) {
					ApplicationInfo info = mAllAppsList.get(index);
					if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_HI_APPLICATION)
						continue;
					ResolveInfo resolve = AndroidPackageUtils.getResolveInfo(info.intent, pm);
					if (resolve == null) {
						info.iconBitmap = BaseBitmapUtils.getAlwaysDefaultAppIcon(ctx.getResources());
					} else {
						mIconCache.getTitleAndIcon(info, resolve);
						processDefaultDockAppRefresh(ctx, info);
					}
					
					if (refresh != BaseConfig.NO_DATA && i++ == refresh) {
						i = 0;
						mq.addIdleHandler(handler);
					}
				}
				mq.addIdleHandler(handler);
			}
		};
	}

	/**
	 * 托盘默认四应用特殊刷新处理
	 * @param ctx
	 * @param info
	 */
	private static void processDefaultDockAppRefresh(Context ctx, ApplicationInfo info) {
		// 托盘默认四应用图标获取
		if(!info.customIcon){
			String themeKey = ThemeIconIntentAdaptation.getDefaultDockAppThemeKey(info.intent.toUri(0));
			
			if (themeKey != null) 
				info.iconBitmap = BaseThemeAssit.getDefaultDockAppIcon(ctx, themeKey, info);
		}
	}
	
}
