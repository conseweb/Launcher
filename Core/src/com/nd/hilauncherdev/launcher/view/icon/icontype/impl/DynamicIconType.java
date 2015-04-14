/**
 * @author dingdj
 * Date:2014-3-20上午11:01:55
 *
 */
package com.nd.hilauncherdev.launcher.view.icon.icontype.impl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Handler;

import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.launcher.broadcast.HiBroadcastReceiver;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.view.icon.icontype.IconType;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconView;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.util.IconTypeFactoryManager;
import com.nd.hilauncherdev.theme.pref.ThemeSharePref;

/**
 * @author dingdj
 * Date:2014-3-20上午11:01:55
 *
 */
public class DynamicIconType extends AppIconType{
	

	@Override
	public IntentFilter[] getIntentFilter(LauncherIconView launcherIconView) {
		IntentFilter[] filters = super.getIntentFilter(launcherIconView);
		if(noActionIntentFilters == filters){
			return noActionIntentFilters;
		}
		
		filters[0].addAction(HiBroadcastReceiver.ACTION_REFRESH_DYNAMIC_ICON);
		return filters;
	}

	@Override
	public boolean handleBroadcastAction(Context context, Intent intent,
			LauncherIconView launcherIconView) {
		if(super.handleBroadcastAction(context, intent, launcherIconView)){
			return true;
		}
		return false;
	}
	
	@Override
	public Bitmap refreshIcon(final LauncherIconViewConfig config, Object tag,
			final Context context, final Handler handler){
		if(tag != null && tag instanceof ApplicationInfo){
			final ApplicationInfo info = (ApplicationInfo)tag;
			// 延迟加载应用程序图标
			if(!info.customIcon){//非自定义图标
				if (info.intent == null || info.componentName == null)
					return null;
		
				ThreadUtil.execute(
						new Runnable(){
							@Override
							public void run() {
								final PackageManager pm = context.getPackageManager();
								ResolveInfo resolve = AndroidPackageUtils.getResolveInfo(info.intent, pm);
								BaseConfig.getIconCache().getTitleAndIcon(info, resolve);
								handler.post(new Runnable() {
									@Override
									public void run() {
										handler.sendEmptyMessage(0);
									}
								});
							}
							
						}	
					);
			}
		}
		return null;
		
	}
	
}
