/**
 *
 */
package com.bitants.common.launcher.view.icon.icontype.impl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;

import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.framework.BaseThirdPackage;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.broadcast.AntBroadcastReceiver;
import com.bitants.common.launcher.view.icon.icontype.IconType;
import com.bitants.common.launcher.view.icon.ui.LauncherIconView;
import com.bitants.common.theme.assit.BaseThemeAssit;
import com.bitants.common.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.common.launcher.view.icon.ui.util.IconTypeFactoryManager;

/**
 * 托盘四图标类型 存在托盘的才是系统快捷方式
 *
 */
public class DockDefaultFourIconType extends IconType {
	
	private String hintType;
	
	public DockDefaultFourIconType(String hintType){
		this.hintType = hintType;
	}

	@Override
	public IntentFilter[] getIntentFilter(LauncherIconView launcherIconView) {
		IntentFilter[] filters = super.getIntentFilter(launcherIconView);
		
		if(noActionIntentFilters == filters){
			IntentFilter filter = new IntentFilter();
			filter.addAction(AntBroadcastReceiver.APP_HINT_FILTER);
			IntentFilter[] rtnFilters = new IntentFilter[]{filter};
			return rtnFilters;
		}
		//判断如果是我的手机或是信息 监听hint广播
		if(IconTypeFactoryManager.isAppHintNeed(launcherIconView.getAppInfo())){
			filters[0].addAction(AntBroadcastReceiver.APP_HINT_FILTER);
		}
		return filters;
	}

	@Override
	public boolean handleBroadcastAction(Context context, Intent intent,
			LauncherIconView launcherIconView) {
		if(super.handleBroadcastAction(context, intent, launcherIconView)){
			return true;
		}
		
		String type = intent.getStringExtra(BaseThirdPackage.APP_HINT_TYPE);
		if (StringUtil.isEmpty(type))
			return false;
		if (!type.equals(hintType))
			return false;
		int hint = intent.getIntExtra(BaseThirdPackage.APP_HINT_COUNT, 0);
		// 未接电话开关未开启
		if (BaseThirdPackage.APP_HINT_TYPE_PHONE.equals(hintType) && !BaseSettingsPreference.getInstance().isShowCommunicatePhone()) {
			hint = 0;
		}
		// 未读短信开关未开启
		if (BaseThirdPackage.APP_HINT_TYPE_MMS.equals(hintType) && !BaseSettingsPreference.getInstance().isShowCommunicateMms()) {
			hint = 0;
		}
		launcherIconView.updateHintConfig(hint);
		
		return false;
	}

	public void setHintType(String hintType) {
		this.hintType = hintType;
	}
	
	@Override
	public Bitmap refreshIcon(LauncherIconViewConfig config, Object o, Context context, Handler handler) {
		super.refreshIcon(config, o, context, handler);
		if (o != null && o instanceof ApplicationInfo) {
			ApplicationInfo info = (ApplicationInfo) o;
			if(!info.customIcon){
				String themeKey = ThemeIconIntentAdaptation.getDefaultDockAppThemeKey(info.intent.toUri(0));
				if (themeKey != null) {
					info.intent.removeExtra("sourceBounds");
					Bitmap bmp = BaseThemeAssit.getDefaultDockAppIcon(context, themeKey, info);
					if(bmp != null){
						info.iconBitmap = bmp;
						handler.sendEmptyMessage(0);
						return bmp;
					}
				}
			}
		}
		return null;
	}


}
