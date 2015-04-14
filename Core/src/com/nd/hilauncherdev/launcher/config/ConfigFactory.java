package com.nd.hilauncherdev.launcher.config;

import android.content.Context;
import android.database.Cursor;

import com.nd.hilauncherdev.launcher.config.db.ConfigDataBase;
import com.nd.hilauncherdev.launcher.config.preference.BaseSettingsPreference;
import com.nd.hilauncherdev.launcher.config.preference.SettingsConstants;
import com.nd.hilauncherdev.launcher.screens.ScreenViewGroup;

/**
 * 标记位
 */
public class ConfigFactory {
	private static int flag_initApp = BaseConfig.NO_DATA;
	/**
	 * 暂时屏蔽多点触控提示
	 */
	private static int flag_readme_multimove = 1;
	/**
	 * 暂时屏蔽菜单
	 */
	private static int flag_readme_menu = BaseConfig.NO_DATA;
	private static int flag_readme_folder = BaseConfig.NO_DATA;
	private static int flag_readme_movetoedge = BaseConfig.NO_DATA;
	private static int flag_readme_drawer = BaseConfig.NO_DATA;
	private static int flag_readme_drawer_multi_choose = BaseConfig.NO_DATA;
	private static int flag_readme_topmenu = BaseConfig.NO_DATA;
	private static int flag_readme_sliding = BaseConfig.NO_DATA;
	private static int flag_readme_drawer_edit_mode = BaseConfig.NO_DATA;
	private static int flag_readme_scene = BaseConfig.NO_DATA;
	private static int flag_readme_one_key_wall = BaseConfig.NO_DATA;
	private static int flag_readme_one_key_deep_clear = BaseConfig.NO_DATA;
	
	public static final String ISINIT = "isInit";
	public static final String ISREADME = "isReadMe";
	public static final String SCREENCOUNT = "screenCount";
	public static final String READBOTTOMMENU = "readme_bottom_menu";
	public static final String READTOPMENU = "readme_top_menu";
	public static final String APPTITLEINITFLAG = "appTitleInitFlag";
	public static final String READMEFOLDER = "readme_folder";
	public static final String READMEMOVETOEDGE = "readme_move_to_edge";
	public static final String READMEMULMOVE = "readme_mul_move";
	public static final String READMETOPMENU = "readme_top_menu";
	public static final String READMEDRAWER = "readme_drawer";
	public static final String READMETOPMENUSLIDING ="readme_top_menu_sliding";
	public static final String READMEDRAWERMULTICHOOSE ="readme_drawer_multi_choose";
	public static final String README_DRAWER_EDIT_MODE = "readme_drawer_edit_mode";
	public static final String README_SCENE = "readme_scene";
	public static final String README_ONE_KEY_WALL = "readme_one_key_wall";
	public static final String README_ONE_KEY_DEEP_CLEAR = "readme_one_key_deep_clear";
	public static final String SCENE_ID = "scene_id";
	public static final String IS_WALLPAPER_ROLLING = "is_wallpaper_rolling";
	
	/**
	 * 是否壁纸滚动
	 */
	private static String flag_is_wallpaper_rolling = null;
	private static final String WALLPAPER_ROLLING_TRUE = "true";
	private static final String WALLPAPER_ROLLING_FALSE = "false";
	
	/**
	 * 查询桌面新手引导列表
	 */
	public static final String QUERY_README_ACTION = "select id from Config where id in ('readme_bottom_menu','readme_folder','readme_drawer'," +
			                         "'readme_drawer_edit_mode','readme_one_key_wall','readme_one_key_deep_clear')";
	
	/**
	 * 回调
	 */
	public interface ConfigCallback {
		public boolean onAction();
	}

	/**
	 * 是否已初始化过匣子
	 * @param ctx
	 * @return true表示已初始化
	 */
	public static boolean isInitApps(Context ctx) { 
		ConfigDataBase db = new ConfigDataBase(ctx);
		return !db.isNotExist(ISINIT);
	}

	/**
	 * 初始化匣子应用程序
	 * @param ctx
	 * @param callback
	 */
	public static void maybeSaveApps(Context ctx, ConfigCallback callback) {
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(ISINIT)){
			boolean suc = callback.onAction();
			if (suc)
			    db.addConfigData(ISINIT, "Y");
		}
	}

	/**
	 * 保存屏幕总数
	 * @param ctx
	 * @param screenCount
	 */
	public static void saveScreenCount(Context ctx, int screenCount) {
		ConfigDataBase db = new ConfigDataBase(ctx);
		db.updateConfigData(SCREENCOUNT,  "" + screenCount);
	}

	/**
	 * 是否初次安装
	 * @param ctx
	 * @return true表示初次安装
	 */
	public static boolean isNewInstall(Context ctx){
		ConfigDataBase db = new ConfigDataBase(ctx);
		return db.isNotExist(ISREADME);
			
	}
	
	/**
	 * 检查是否需要显示Readme,检查结束后会把桌面置成非初次安装
	 * @param ctx
	 * @param callback
	 * @param hasShowCallback
	 */
	public static void maybeShowReadme(Context ctx, ConfigCallback callback, ConfigCallback hasShowCallback) {
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(ISREADME)){
			if(null != callback){
				boolean suc = callback.onAction();
				if (suc)
					db.addConfigData(ISREADME, "Y");
			}
		}else{
			if(null != hasShowCallback){
				hasShowCallback.onAction();
			}
		}
	}

	/**
	 * 初始化Title
	 * @param ctx
	 * @param callback
	 */
	public static void initAppTitle(Context ctx, ConfigCallback callback) {
		if (flag_initApp != BaseConfig.NO_DATA)
			return;
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(APPTITLEINITFLAG)){
			boolean suc = callback.onAction();
			if (suc)
				db.addConfigData(APPTITLEINITFLAG, "1");
		}
		flag_initApp = 1;
	}

	/**
	 * 桌面屏幕数
	 * @param ctx
	 * @return 桌面屏幕数
	 */
	public static int getScreenCount(Context ctx) {
		ConfigDataBase db = new ConfigDataBase(ctx);
		String value = db.getConfigData(SCREENCOUNT);
		if(value == null){
			return ScreenViewGroup.DEFAULT_SCREEN_COUNT;
		}else{
			try{
				return Integer.parseInt(value);
			}catch(NumberFormatException  e){
				// 出错显示5屏
				return ScreenViewGroup.DEFAULT_SCREEN_COUNT;
			}
		}
	}

	/**
	 * 显示菜单引导
	 * @param ctx
	 * @param callback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowReadmeMenu(Context ctx, ConfigCallback callback) {
		if (flag_readme_menu != BaseConfig.NO_DATA)
			return false;

		boolean result = false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READBOTTOMMENU)){
			boolean suc = callback.onAction();
			if (suc)
				result = db.addConfigData(READBOTTOMMENU, "Y");
		}
		flag_readme_menu = 1;
		return result;

	}
	
	/**
	 * 已显示菜单引导
	 * @param ctx
	 */
//	public static void alreadyShowReadmeMenu(Context ctx) {
//		if (flag_readme_menu != BaseConfig.NO_DATA)
//			return ;
//
//		BaseConfigDataBase db = new BaseConfigDataBase(ctx);
//		if(db.isNotExist(READBOTTOMMENU)){
//			if(!db.addConfigData(READBOTTOMMENU, "Y")){
//				return;
//			}
//		}
//		flag_readme_menu = 1;
//	}
	
	
	/**
	 * 显示文件夹引导
	 * @param ctx
	 * @param callback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowFolder(Context ctx, ConfigCallback callback) {
		if (flag_readme_folder != BaseConfig.NO_DATA)
			return false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READMEFOLDER)){
			boolean suc = callback.onAction();
			if (suc){
				if(!db.addConfigData(READMEFOLDER, "Y")){
					return false;
				}
			}
		}
		
		flag_readme_folder = 1;
		return true;

	}

	/**
	 * 显示移动到桌面边缘提示
	 * @param ctx
	 * @param callback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowMoveToEdge(Context ctx, ConfigCallback callback) {
		if (flag_readme_movetoedge != BaseConfig.NO_DATA)
			return false;

		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READMEMOVETOEDGE)){
			boolean suc = callback.onAction();
			if (suc){
				if(!db.addConfigData(READMEMOVETOEDGE, "Y")){
					return false;
				}
			}
		}
		flag_readme_movetoedge = 1;
		return true;
	
	}
	
	/**
	 * 显示多点触控
	 * 暂时屏蔽该提示
	 * @param ctx
	 * @param callback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowReadmeMultimove(Context ctx, ConfigCallback callback) {
		if (flag_readme_multimove != BaseConfig.NO_DATA)
			return false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READMEMULMOVE)){
			boolean suc = callback.onAction();
			if (suc){
				 if(!db.addConfigData(READMEMULMOVE, "Y")){
					 return false;
				 }
			}
		}
		flag_readme_multimove = 1;
		return true;

	}

	/**
	 * 是否显示顶部菜单提示
	 * @param ctx
	 * @return true表示需要显示
	 */
	public static boolean isShowReadmeTopMenu(Context ctx) {
		ConfigDataBase db = new ConfigDataBase(ctx);
		return db.isNotExist(READTOPMENU);
	}

	public static void alreadyShowReadmeTopMenu(Context ctx) {
		ConfigDataBase db = new ConfigDataBase(ctx);
		db.addConfigData(READTOPMENU, "Y");
	}
	
	/**
	 * 下拉菜单提示
	 * @param ctx
	 * @param configCallback
	 * @return true表示需要显示
	 */
	public static boolean maybeTopMenu(Context ctx, ConfigCallback configCallback) {
		if (flag_readme_topmenu != BaseConfig.NO_DATA)
			return false;

		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READTOPMENU)){
			boolean suc = configCallback.onAction();
			if(suc){
				if(!db.addConfigData(READTOPMENU, "Y")){
					return false;
				}
			}
		}
		flag_readme_topmenu = 1;
		return true;
	}

	/**
	 * 匣子提示
	 * @param ctx
	 * @param configCallback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowDrawer(Context ctx, ConfigCallback configCallback) {
		if (flag_readme_drawer != BaseConfig.NO_DATA)
			return false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READMEDRAWER)){
			boolean suc = configCallback.onAction();
			if(suc){
				if(!db.addConfigData(READMEDRAWER, "Y")){
					return false;
				}
			}
		}
		
		flag_readme_drawer = 1;
		return true;
	}

	/**
	 * 快捷菜单滑动提示
	 * @param ctx
	 * @return true表示需要显示
	 */
	public static boolean maybeShowTopMenuSliding(Context ctx) {
		if (flag_readme_sliding != BaseConfig.NO_DATA)
			return false;

		boolean rtn = false;
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READMETOPMENUSLIDING)){
			if(db.addConfigData(READMETOPMENUSLIDING, "Y")){
				flag_readme_sliding = 1;
				rtn = true;
			}
		}else{
			flag_readme_sliding = 1;
		}
		return rtn;
	}
	
	/**
	 * 是否显示匣子多点触控
	 * @param ctx
	 * @return true表示需要显示
	 */
	public static boolean isShowDrawerMultiChoose(Context ctx) {
		if (flag_readme_drawer_multi_choose != BaseConfig.NO_DATA)
			return false;
		ConfigDataBase db = new ConfigDataBase(ctx);
		return db.isNotExist(READMEDRAWERMULTICHOOSE);
		
	}
	
	/**
	 * 匣子多选提示
	 * @param ctx
	 * @param configCallback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowDrawerMultiChoose(Context ctx, ConfigCallback configCallback) {
		if (flag_readme_drawer_multi_choose != BaseConfig.NO_DATA)
			return false;

		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(READMEDRAWERMULTICHOOSE)){
			boolean suc = configCallback.onAction();
			if (suc){
				if(!db.addConfigData(READMEDRAWERMULTICHOOSE, "Y")){
					return false;
				}
			}	
		}
		flag_readme_drawer_multi_choose = 1;
		return true;
	}
	
	/**
	 * 查询桌面新手引导动作列表
	 * @param ctx
	 * @return 桌面新手引导动作列表
	 */
	public static String[] queryReadMeAction(Context ctx) {
		String[] readMeActions = null;
		ConfigDataBase db = null;
		Cursor c = null;
		try {
			db = new ConfigDataBase(ctx);
			c = db.query(QUERY_README_ACTION);
			if (c.getCount() > 0) {
				int i = 0;
				readMeActions = new String[c.getCount()];
				while(c.moveToNext()) {
					readMeActions[i] = c.getString(0);
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
			if (db != null)
				db.close();
		}

		return readMeActions;
	}
	
	/**
	 * 匣子编辑模式提示
	 * @param ctx
	 * @param configCallback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowDrawerEditMode(Context ctx, ConfigCallback configCallback) {
		if (flag_readme_drawer_edit_mode != BaseConfig.NO_DATA)
			return false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(README_DRAWER_EDIT_MODE)){
			boolean suc = configCallback.onAction();
			if(suc){
				if(!db.addConfigData(README_DRAWER_EDIT_MODE, "Y")){
					return false;
				}
			}
		}
		
		flag_readme_drawer_edit_mode = 1;
		return true;
	}
	
	/**
	 * 情景模式提示
	 * @param ctx
	 * @param configCallback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowScene(Context ctx, ConfigCallback configCallback) {
		if (flag_readme_scene != BaseConfig.NO_DATA)
			return false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(README_SCENE)){
			boolean suc = configCallback.onAction();
			if(suc){
				if(!db.addConfigData(README_SCENE, "Y")){
					return false;
				}
			}
		}
		
		flag_readme_scene = 1;
		return true;
	}
	
	
	/**
	 * 一健换壁纸提示
	 * @param ctx
	 * @param configCallback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowOneKeyWall(Context ctx, ConfigCallback configCallback) {
		if (flag_readme_one_key_wall != BaseConfig.NO_DATA)
			return false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(README_ONE_KEY_WALL)){
			boolean suc = configCallback.onAction();
			if(suc){
				if(!db.addConfigData(README_ONE_KEY_WALL, "Y")){
					return false;
				}
			}
		}
		
		flag_readme_one_key_wall = 1;
		return true;
	}
	
	
	/**
	 * 一健深度清理提示
	 * @param ctx
	 * @param configCallback
	 * @return true表示需要显示
	 */
	public static boolean maybeShowOneKeyDeepClear(Context ctx, ConfigCallback configCallback) {
		if (flag_readme_one_key_deep_clear != BaseConfig.NO_DATA)
			return false;
		
		ConfigDataBase db = new ConfigDataBase(ctx);
		if(db.isNotExist(README_ONE_KEY_DEEP_CLEAR)){
			boolean suc = configCallback.onAction();
			if(suc){
				if(!db.addConfigData(README_ONE_KEY_DEEP_CLEAR, "Y")){
					return false;
				}
			}
		}
		
		flag_readme_one_key_deep_clear = 1;
		return true;
	}
	
	/**
	 * 壁纸是否滚动
	 */
	public static boolean isWallpaperRolling(Context ctx) {
		if (flag_is_wallpaper_rolling == null) {
			ConfigDataBase db = new ConfigDataBase(ctx);
			String id = ConfigFactory.IS_WALLPAPER_ROLLING;
			flag_is_wallpaper_rolling = db.getConfigData(id);
			if (flag_is_wallpaper_rolling == null) {
				boolean oldSetting = BaseSettingsPreference.getInstance().getBaseSP().getBoolean(SettingsConstants.SETTING_SCREEN_WALLPAPER_ROLLING, true);
				setWallpaperRolling(ctx, oldSetting);
			}
		}
		
		return (flag_is_wallpaper_rolling != null && flag_is_wallpaper_rolling.equals(WALLPAPER_ROLLING_FALSE)) ? false : true;
	}

	/**
	 * 设置壁纸是否滚动
	 */
	public static void setWallpaperRolling(Context ctx, boolean isWallpaperRolling) {
		ConfigDataBase db = new ConfigDataBase(ctx);
		String id = ConfigFactory.IS_WALLPAPER_ROLLING;
		flag_is_wallpaper_rolling = isWallpaperRolling ? WALLPAPER_ROLLING_TRUE : WALLPAPER_ROLLING_FALSE;
		if (db.isNotExist(id)) {
			db.addConfigData(id, flag_is_wallpaper_rolling);
		}else{
			db.updateConfigData(id, flag_is_wallpaper_rolling);
		}
	}
}
