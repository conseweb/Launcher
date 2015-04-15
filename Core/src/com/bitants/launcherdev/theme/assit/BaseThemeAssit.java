package com.bitants.launcherdev.theme.assit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.theme.ThemeManagerFactory;
import com.bitants.launcherdev.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.launcherdev.kitset.util.BaseBitmapUtils;

public class BaseThemeAssit {

	public static Bitmap getDefaultDockAppIcon(Context ctx, String themeKey) {
		return getDefaultDockAppIcon(ctx, themeKey, null);
	}
	
	public static Bitmap getDefaultDockAppIcon(Context ctx, String themeKey, ApplicationInfo info) {
		Drawable d = ThemeManagerFactory.getInstance().getThemeAppIcon(themeKey); // 取主题图标
		
		if(info != null){
			info.useIconMask = (d == null) ? true : false;
		}
		
		if (d == null) {// 无主题图标, 取Activity默认图标
			d = ThemeIconIntentAdaptation.getActivityIcon(ctx, themeKey);
			if (d == null)
				d = BaseBitmapUtils.getDefaultAppDrawable(ctx.getResources());
		}
		
		return BaseBitmapUtils.drawable2Bitmap(d); 
	}
}
