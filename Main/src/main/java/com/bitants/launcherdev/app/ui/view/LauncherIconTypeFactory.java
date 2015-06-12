/**
 *
 */
package com.bitants.launcherdev.app.ui.view;

import com.bitants.launcherdev.app.ui.view.icontype.DockAllAppIconType;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.view.icon.icontype.IconType;
import com.bitants.common.launcher.view.icon.ui.util.IconTypeFactoryManager.IconTypeFactory;



/**
 * 桌面图标的类型工厂
 *
 */
public class LauncherIconTypeFactory extends IconTypeFactory {

	@Override
	public IconType getIconType(Object o) {
		if(o != null) {
			if(o instanceof ApplicationInfo) {
				ApplicationInfo info = (ApplicationInfo)o;
				//匣子图标
				if(Favorites.ITEM_TYPE_INDEPENDENCE == info.itemType) {
					return DockAllAppIconType.getInstance();
				}
			}
		}
		return super.getIconType(o);
	}
}
