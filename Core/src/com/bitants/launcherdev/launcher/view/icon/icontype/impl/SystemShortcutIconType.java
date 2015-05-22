/**
 *
 */
package com.bitants.launcherdev.launcher.view.icon.icontype.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import com.bitants.launcherdev.launcher.view.icon.icontype.IconType;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.icontype.IconType;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.icontype.IconType;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;

/**
 * 系统快捷不进行蒙板的处理
 * 
 */
public class SystemShortcutIconType extends IconType {

	public Bitmap refreshIcon(final LauncherIconViewConfig config, Object tag, final Context context, final Handler handler) {
		return super.refreshIcon(config, tag, context, handler);
	}

}
