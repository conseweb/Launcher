/**
 *
 */
package com.bitants.launcherdev.app.ui.view.icontype;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import com.bitants.launcherdev.kitset.util.BitmapUtils;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.view.icon.icontype.IconType;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.theme.ThemeManagerFactory;
import com.bitants.launcherdev.theme.data.ThemeData;

/**
 * 匣子图标
 *
 */
public class DockAllAppIconType extends IconType {
	
	private static DockAllAppIconType instance;
	
	private DockAllAppIconType(){
		super();
	}
	
	public static DockAllAppIconType getInstance(){
		if(instance == null){
			instance = new DockAllAppIconType();
		}
		return instance;
	}
	

	@Override
	public Bitmap refreshIcon(LauncherIconViewConfig config, Object o,
			Context context, Handler handler) {
		super.refreshIcon(config, o, context, handler);
		config.setDrawFrontIconMask(false);
		if (o != null && o instanceof ApplicationInfo) {
			ApplicationInfo info = (ApplicationInfo) o;
			if (info.itemType == Favorites.ITEM_TYPE_INDEPENDENCE) {
				info.iconBitmap = BitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(ThemeData.PANDAHOME_STYLE_ICON_TRAY_EXPAND));
		
		return info.iconBitmap;
			}
		}
		return null;
	}


	

}
