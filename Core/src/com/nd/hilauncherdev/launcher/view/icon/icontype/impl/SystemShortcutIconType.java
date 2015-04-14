/**
 * @author Michael
 * Date:2014-5-23下午4:14:11
 *
 */
package com.nd.hilauncherdev.launcher.view.icon.icontype.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import com.nd.hilauncherdev.launcher.view.icon.icontype.IconType;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;

/**
 * @author Michael Date:2014-5-23下午4:14:11
 * 系统快捷不进行蒙板的处理
 * 
 */
public class SystemShortcutIconType extends IconType {

	public Bitmap refreshIcon(final LauncherIconViewConfig config, Object tag, final Context context, final Handler handler) {
		return super.refreshIcon(config, tag, context, handler);
	}

}
