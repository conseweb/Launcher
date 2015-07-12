package com.bitants.common.theme;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bitants.common.kitset.util.WallpaperUtil;
import com.bitants.common.launcher.broadcast.AntBroadcastReceiver;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.common.theme.data.BaseTheme;
import com.bitants.common.theme.data.ThemeGlobal;
import com.bitants.common.theme.module.ModuleConstant;
import com.bitants.common.theme.db.LauncherThemeDataBase;
import com.bitants.common.theme.module.ThemeModuleHelper;
import com.bitants.common.theme.module.ThemeModuleItem;
import com.bitants.common.theme.pref.ThemeSharePref;

/**
 * 
 * <br>Title: 基础主题管理类
 */
public class ThemeManagerHelper {
	
	/**
     * <br>Description: 分配主题对象供解析主题时装载(获取未填充内容的主题对象)
     * @return
     */
    public BaseTheme allocatThemeObj() {
    	return new BaseTheme(BaseConfig.getApplicationContext());
    }
	
	/**
	 * <br>Description: 通过主题Id获取主题对象(获取完整主题对象)
	 * @param themeId
	 * @return
	 */
	public BaseTheme createTheme(String themeId, boolean isCurrentTheme) {
		if (themeId != null) {
			try {
				return new BaseTheme(BaseConfig.getApplicationContext(), themeId, isCurrentTheme);
			} catch (Exception e) {
				return new BaseTheme(BaseConfig.getApplicationContext(), ThemeGlobal.DEFAULT_THEME_ID, isCurrentTheme);
			}
		} else {
			return new BaseTheme(BaseConfig.getApplicationContext(), ThemeGlobal.DEFAULT_THEME_ID, isCurrentTheme);
		}
	}
	
	/**
	 * <br>Description: 删除主题相关数据
	 * @param ctx
	 * @param themeId
	 */
	public void removeTheme(Context ctx, String themeId) {
		ThemeManagerFactory.getInstance().removeThemeDatabaseRecord(ctx, themeId);
		ThemeManagerFactory.getInstance().removeThemeAllFile(themeId);
	}
	
	/**
	 * <br>Description: 删除主题模块包相关数据
	 * @param ctx
	 * @param moduleId
	 * @param moduleKey
	 */
	public void removeModule(Context ctx, String moduleId, String moduleKey) {
		ThemeModuleHelper.getInstance().removeModuleInfo(moduleId, moduleKey);
		ThemeModuleHelper.getInstance().removeModuleAllFile(moduleId, moduleKey);
	}
	
	/**
	 * <br>Description: 增加当前主题信息广播参数
	 * @param themeInfoIntent
	 */
	public void addThemeInfoIntentExtra(Intent themeInfoIntent) {
		
	}
	
	/**
	 * <br>
	 * Description: 应用主题(不显示等待框) <br>
	 *
	 * @param ctx
	 * @param themeId
	 *            主题ID
	 * @param applyWallpaper
	 *            是否应用壁纸
	 * @param sendThemeChangeBroadcast
	 *            是否发送主题更换广播(发送该广播可通知其他部件同步换肤)
	 * @param autoDirection
	 *            是否自动跳转至桌面(为false时请自己处理跳转)
	 * @param applyScene
	 *            是否是应用情景时调用该方法（预留参数）
	 * @param changeRolling 是否需要改变壁纸滚动设置          
	 */
	public void applyThemeWithOutWaitDialog(final Context ctx, 
												 final String themeId, 
												 boolean applyWallpaper, 
												 boolean sendThemeChangeBroadcast, 
												 boolean autoDirection, 
												 boolean applyScene,
												 boolean changeRolling) {
		// 保存主题Id至数据文件
		ThemeSharePref.getInstance(ctx).setCurrentThemeId(themeId);
		ThemeModuleHelper.getInstance().updateCurrentThemeModule(themeId);
		// 更新当前主题数据
		LauncherThemeDataBase db = new LauncherThemeDataBase(ctx);
		// 更新当前主题使用次数和最近使用时间
		ThemeManagerFactory.getInstance().updateUseTimeAndUseCount(db, themeId);
		db.close();
		// 重置当前主题对象
		ThemeManagerFactory.getInstance().resetCurrentTheme();
		// 应用主题壁纸
		if (applyWallpaper) {
			WallpaperUtil.applyWallpaperInThread(BaseConfig.getApplicationContext(), ThemeManagerFactory.getInstance().getCurrentTheme().getWrapper().getWallpaperValue());
		}
		BaseConfig.getIconCache().refreshThemeIcon();
		// 重置文件夹图标
		LauncherIconSoftReferences.getInstance().resetDefIconFolderEncriptMask(ctx.getResources());
		LauncherIconSoftReferences.getInstance().resetDefIconFolderBackground(ctx.getResources());
		LauncherIconSoftReferences.getInstance().resetDefIconAndroidFolderEncriptMask(ctx.getResources());
		LauncherIconSoftReferences.getInstance().resetDefIconAndroidFolderBackground(ctx.getResources());
		LauncherIconSoftReferences.getInstance().resetDefIconFullScreenFolderEncriptMask(ctx.getResources());
		LauncherIconSoftReferences.getInstance().resetDefIconFullScreenFolderBackground(ctx.getResources());
		// 刷新图标
		ctx.sendBroadcast(new Intent(AntBroadcastReceiver.REFRESH_ICON_ACTION));
		// 通知桌面刷新UI
		Intent launcherUIRefreshIntent = new Intent(ThemeGlobal.LAUNCHER_UI_REFRESH_ACTION);
		launcherUIRefreshIntent.putExtra("applyScene", applyScene);
		if(!applyWallpaper) {// 不换壁纸时的设置不改变滚屏模式
			changeRolling = false;
		}
		launcherUIRefreshIntent.putExtra("changeRolling", changeRolling);
		ctx.sendBroadcast(launcherUIRefreshIntent);
		// 发送主题切换广播
		if (sendThemeChangeBroadcast) {
			ThemeManagerFactory.getInstance().sendCurrentThemeInfoBroadcast(ctx, null);
		}
		// 通知主题管理列表刷新
		Intent themeListRefreshIntent = new Intent(ThemeGlobal.INTENT_THEME_LIST_REFRESH);
		ctx.sendBroadcast(themeListRefreshIntent);
	}
	
	/**
	 * <br>
	 * Description: 应用主题模块(不显示等待框),应用整个主题包时请勿调用此方法 <br>
	 *
	 * @param ctx
	 * @param modules
	 *            模块信息
	 * @param newThemeId
	 *            新的主题ID, null时表示只更换主题模块, 否则当前主题ID更改为newThemeId(其他模块使用该主题皮肤)
	 * @param autoDirection
	 *            是否自动跳转至桌面(为false时请自己处理跳转)
	 */
	public void applyThemeModuleWithOutWaitDialog(final Context ctx, 
												 final List<ThemeModuleItem> modules,
												 String newThemeId,
												 boolean autoDirection) {
		if(null == modules) return;
		// 保存新的当前主题Id(其他模块使用该主题皮肤)
		if(!TextUtils.isEmpty(newThemeId)) {
			ThemeSharePref.getInstance(ctx).setCurrentThemeId(newThemeId);
			// 通知主题管理列表刷新
			Intent themeListRefreshIntent = new Intent(ThemeGlobal.INTENT_THEME_LIST_REFRESH);
			ctx.sendBroadcast(themeListRefreshIntent);
		}
		// 保存模块数据至当前主题表
		ThemeModuleHelper.getInstance().updateCurrentThemeModule(modules);
		// 重置当前主题对象
		ThemeManagerFactory.getInstance().resetCurrentTheme();
		// 默认不更改壁纸的滚屏模式
		boolean changeRolling = false;
		for(int i=0; i<modules.size(); i++ ){
			ThemeModuleItem module = modules.get(i);
			if(null != module) {
				if(ModuleConstant.MODULE_ICONS.equals(module.getKey())) {//图标模块
					changeRolling = true;// 更改壁纸时的设置为滚屏模式
					BaseConfig.getIconCache().refreshThemeIcon();
					// 重置文件夹图标
					LauncherIconSoftReferences.getInstance().resetDefIconFolderEncriptMask(ctx.getResources());
					LauncherIconSoftReferences.getInstance().resetDefIconFolderBackground(ctx.getResources());
					LauncherIconSoftReferences.getInstance().resetDefIconAndroidFolderEncriptMask(ctx.getResources());
					LauncherIconSoftReferences.getInstance().resetDefIconAndroidFolderBackground(ctx.getResources());
					LauncherIconSoftReferences.getInstance().resetDefIconFullScreenFolderEncriptMask(ctx.getResources());
					LauncherIconSoftReferences.getInstance().resetDefIconFullScreenFolderBackground(ctx.getResources());
					// 刷新图标
					ctx.sendBroadcast(new Intent(AntBroadcastReceiver.REFRESH_ICON_ACTION));
				} else if(ModuleConstant.MODULE_WALLPAPER.equals(module.getKey())) {//壁纸模块
					changeRolling = true;// 更改壁纸时的设置为滚屏模式
					WallpaperUtil.applyWallpaperInThread(BaseConfig.getApplicationContext(), ThemeManagerFactory.getInstance().getCurrentTheme().getWrapper().getWallpaperValue());
				} else {
					ThemeModuleItem currentThemeModule = ThemeManagerFactory.getInstance().getCurrentTheme().getModuleMap().get(module.getKey());
					if(null != currentThemeModule) {
						String currentThemeModulePkg = currentThemeModule.getPgk();
						if(!TextUtils.isEmpty(currentThemeModulePkg)) {//通知第三方插件或应用换肤
							String[] pkgs = currentThemeModulePkg.split(";");
							for(int k=0; k<pkgs.length; k++) {
								ThemeManagerFactory.getInstance().sendCurrentThemeInfoBroadcast(ctx, pkgs[k]);
							}
						}
					}
				}
			}
		}
		// 通知桌面刷新UI
		Intent launcherUIRefreshIntent = new Intent(ThemeGlobal.LAUNCHER_UI_REFRESH_ACTION);
		launcherUIRefreshIntent.putExtra("applyScene", false);
		launcherUIRefreshIntent.putExtra("changeRolling", changeRolling);
		ctx.sendBroadcast(launcherUIRefreshIntent);
	}
	

}
