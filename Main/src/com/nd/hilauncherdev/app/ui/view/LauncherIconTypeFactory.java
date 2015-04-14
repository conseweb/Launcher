/**
 * @author dingdj
 * Date:2014-3-21下午3:40:54
 *
 */
package com.nd.hilauncherdev.app.ui.view;

import com.nd.hilauncherdev.app.ui.view.icontype.DockAllAppIconType;
import com.nd.hilauncherdev.launcher.LauncherSettings.Favorites;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.view.icon.icontype.IconType;
import com.nd.hilauncherdev.launcher.view.icon.ui.util.IconTypeFactoryManager.IconTypeFactory;



/**
 * 桌面图标的类型工厂
 * @author dingdj
 * Date:2014-3-21下午3:40:54
 *
 */
public class LauncherIconTypeFactory extends IconTypeFactory{

	@Override
	public IconType getIconType(Object o) {
		if(o != null){
			if(o instanceof ApplicationInfo){
				ApplicationInfo info = (ApplicationInfo)o;
				//匣子图标
				if(Favorites.ITEM_TYPE_INDEPENDENCE == info.itemType){
					return DockAllAppIconType.getInstance();
				}
			}
		}
		return super.getIconType(o);
	}

	

}
