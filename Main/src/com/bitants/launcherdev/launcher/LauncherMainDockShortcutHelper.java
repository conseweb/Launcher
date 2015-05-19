package com.bitants.launcherdev.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import com.bitants.launcher.R;
import com.bitants.launcherdev.app.CustomIntent;
import com.bitants.launcherdev.kitset.util.BitmapUtils;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.theme.data.ThemeData;

/**
 * 界面管理快捷方式辅助类
 * 
 * @author Anson
 */
public class LauncherMainDockShortcutHelper {
	
	static Object[][] shortcuts = new Object[][]{
		new Object[]{R.string.dockbar_dock_drawer, R.drawable.main_dock_allapps, CustomIntent.ACTION_OPEN_DRAWER, ThemeData.MAIN_DOCK_ALLAPPS},
	};

	public static final int ERROR_TYPE = -1;

	/**
	 * 生成界面管理快捷方式信息
	 * 
	 * @param launcher
	 * @param cellInfo
	 * @param titleId
	 * @return	
	 */
	public static ApplicationInfo addMainDockShortcutToWorkspace(Launcher launcher, int cellX, int cellY, String title) {
		Workspace workspace = launcher.getWorkspace();
		ApplicationInfo info = createLauncherEditMainDockInfo(launcher, title);
		int currentScreen = workspace.getCurrentScreen();
		info.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
		info.cellX = cellX;
		info.cellY = cellY;
		int[] size = CellLayoutConfig.spanXYMather(1, 1, info);
		info.spanX = size[0];
		info.spanY = size[1];
		info.screen = currentScreen;
		LauncherModel.addItemToDatabase(launcher, info, false);
		View view = LauncherViewHelper.createCommonAppView(launcher, info);
		workspace.addInScreen(view, currentScreen, info.cellX, info.cellY, info.spanX, info.spanY);
		return info;
	}

	public static ApplicationInfo createLauncherEditMainDockInfo(Context ctx, String title) {
		ApplicationInfo info = new ApplicationInfo();

		info.customIcon = false;
		
		info.title = title;
		info.iconResource = Intent.ShortcutIconResource.fromContext(ctx, getDrawId(ctx, title));
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT;
		String action = String.valueOf(getAction(ctx, title));
		String themeKey = getThemeKeyByTitle(ctx, title);
		info.iconBitmap = BitmapUtils.drawable2Bitmap(ctx.getResources().getDrawable(getDrawId(ctx, title)));
		info.intent = new Intent(action);
		
		return info;
	}
	
	
	public static ApplicationInfo createLauncherAppslistInfo(Context ctx, String title) {
		ApplicationInfo info = new ApplicationInfo();

		info.customIcon = false;
		
		info.title = title;
		info.iconResource = Intent.ShortcutIconResource.fromContext(ctx, getDrawId(ctx, title));
		info.itemType = Favorites.ITEM_TYPE_INDEPENDENCE;
		String action = String.valueOf(getAction(ctx, title));
		String themeKey = getThemeKeyByTitle(ctx, title);
		info.iconBitmap = BitmapUtils.drawable2Bitmap(ctx.getResources().getDrawable(getDrawId(ctx, title)));
		info.intent = new Intent(action);
		
		return info;
	}

	public static int getDrawId(Context ctx, String title) {
		Resources res = ctx.getResources();
		for (Object[] shortcut : shortcuts) {
			if (res.getString((Integer) shortcut[0]).equals(title)){
				return (Integer) shortcut[1];
			}
		}
		return -1;
	}
	
	/**
	 * 获取快捷对应的key
	 * @author Michael
	 * Date:2014-1-7下午3:19:58
	 *  @param ctx
	 *  @param title
	 *  @return
	 */
	private static String getThemeKeyByTitle(Context ctx, String title) {
		Resources res = ctx.getResources();
		for (Object[] shortcut : shortcuts) {
			if (res.getString((Integer) shortcut[0]).equals(title)){
				return (String) shortcut[3];
			}
		}
		return "";
	}
	
	/**
	 * 获取快捷对应的key
	 * @author Michael
	 * Date:2014-1-7下午3:19:58
	 *  @param ctx
	 *  @param title
	 *  @return
	 */
	public static String getThemeKeyByAction(Context ctx, String action) {
		for (Object[] shortcut : shortcuts) {
			if (((String) shortcut[2]).equals(action)){
				return (String) shortcut[3];
			}
		}
		return "";
	}

	private static String getAction(Context ctx, String title) {
		Resources res = ctx.getResources();
		for (Object[] shortcut : shortcuts) {
			if (res.getString((Integer) shortcut[0]).equals(title)){
				return (String) shortcut[2];
			}
		}
		return "";
	}

	/**
	 * 通过action去获取桌面快捷的相关信息
	 * 
	 * @param ctx
	 * @param action
	 * @return
	 */
	public static ApplicationInfo getLauncherEditMainDockInfoByAction(Context ctx, String action) {
		ApplicationInfo info = new ApplicationInfo();
		info.customIcon = false;
		info.title = getTitleByAction(ctx, action);
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT;
		info.intent = new Intent(action);
		return info;
	}

	
	/**
	 * 通过action获取标题
	 * @author wangguomei
	 * 
	 * @param Context
	 * @param 91快捷action
	 * 
	 * @return 91快捷标题
	 */
	public static String get91ShortcutTitleByAction(Context ctx, String action) {
		return getTitleByAction(ctx,action);
	}
	
	
	/**
	 * 通过action获取标题
	 * 
	 * @param ctx
	 * @param action
	 * @return
	 */
	private static String getTitleByAction(Context ctx, String action) {
		Resources res = ctx.getResources();
		for (Object[] shortcut : shortcuts) {
			if (shortcut[2].equals(action)){
				return (String) res.getString((Integer) shortcut[0]);
			}
		}
		return "";
	}

}
