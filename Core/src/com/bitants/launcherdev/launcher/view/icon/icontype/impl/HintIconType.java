/**
 *
 */
package com.bitants.launcherdev.launcher.view.icon.icontype.impl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.bitants.launcherdev.framework.BaseThirdPackage;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.broadcast.HiBroadcastReceiver;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.framework.BaseThirdPackage;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.broadcast.HiBroadcastReceiver;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.view.icon.icontype.IconType;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.framework.BaseThirdPackage;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.broadcast.HiBroadcastReceiver;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;

/**
 *
 */
public class HintIconType extends AppIconType{
	
	private String hintType;
	
	public HintIconType(String hintType){
		this.hintType = hintType;
	}

	@Override
	public IntentFilter[] getIntentFilter(LauncherIconView launcherIconView) {
		IntentFilter[] filters = super.getIntentFilter(launcherIconView);
		
		if(noActionIntentFilters == filters){
			IntentFilter filter = new IntentFilter();
			filter.addAction(HiBroadcastReceiver.APP_HINT_FILTER);
			IntentFilter[] rtnFilters = new IntentFilter[]{filter};
			return rtnFilters;
		}
		filters[0].addAction(HiBroadcastReceiver.APP_HINT_FILTER);
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
	
}
