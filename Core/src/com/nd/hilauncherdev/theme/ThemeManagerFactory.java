package com.nd.hilauncherdev.theme;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nd.hilauncherdev.framework.exception.PandaDesktopException;
import com.nd.hilauncherdev.kitset.util.FileUtil;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.kitset.util.WallpaperUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.theme.data.BasePandaTheme;
import com.nd.hilauncherdev.theme.data.BaseThemeData;
import com.nd.hilauncherdev.theme.data.ThemeGlobal;
import com.nd.hilauncherdev.theme.db.LauncherThemeDataBase;
import com.nd.hilauncherdev.theme.module.ModuleConstant;
import com.nd.hilauncherdev.theme.module.ThemeModuleHelper;
import com.nd.hilauncherdev.theme.module.ThemeModuleItem;
import com.nd.hilauncherdev.theme.parse.apt.ThemeLoader;
import com.nd.hilauncherdev.theme.pref.ThemeSharePref;

/**
 * <br>Description: 主题管理接口
 * <br>Author:caizp
 * <br>Date:2014-4-4上午10:07:03
 */
public class ThemeManagerFactory {
	
	/**
	 * 换肤控件前景
	 */
	public static final int THEME_ITEM_FOREGROUND = 0;

	/**
	 * 换肤控件背景
	 */
	public static final int THEME_ITEM_BACKGROUND = 1;
	
	private static ThemeManagerFactory factory;
	
	private static BasePandaTheme mCurrentTheme;
	
	private ThemeManagerHelper themeManagerHelper;
	
	private ThemeManagerFactory(){};
	
	public static ThemeManagerFactory getInstance() {
		if (factory == null) {
			factory = new ThemeManagerFactory();
		}
		return factory;
	}
	
	public void setThemeManagerHelper(ThemeManagerHelper helper) {
		themeManagerHelper = helper;
	}
	
	public ThemeManagerHelper getThemeManagerHelper() {
		return themeManagerHelper;
	}
	
	/****************************当前主题相关begin******************************/
	
	/**
	 * <br>
	 * Description:获取当前主题 <br>
	 * Author:caizp <br>
	 * Date:2014-4-2下午6:39:35
	 * 
	 * @return
	 */
	public BasePandaTheme getCurrentTheme() {
		if (null != themeManagerHelper) {
			if (null == mCurrentTheme) {
				mCurrentTheme = themeManagerHelper.createTheme(ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).getCurrentThemeId(),
						true);
			}
			return mCurrentTheme;
		}
		return new BasePandaTheme(BaseConfig.getApplicationContext(), ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).getCurrentThemeId(), true);
	}

	/**
	 * <br>
	 * Description: 重置当前主题 <br>
	 * Author:caizp <br>
	 * Date:2014-4-3上午9:44:53
	 */
	public void resetCurrentTheme() {
		if (null != themeManagerHelper) {
			mCurrentTheme = themeManagerHelper.createTheme(ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).getCurrentThemeId(), true);
		}
	}
	
	/**
	 * <br>Description: 获取当前主题图片
	 * <br>Author:caizp
	 * <br>Date:2014-4-3上午9:40:54
	 * @param key
	 * @return
	 */
	public Drawable getThemeDrawable(String key) {
		return getCurrentTheme().getIconOrDrawableByKey(key);
	}
	
	/**
	 * <br>Description: 获取当前主题图标
	 * <br>Author:caizp
	 * <br>Date:2014-4-3上午9:41:12
	 * @param key
	 * @return
	 */
	public Drawable getThemeAppIcon(String key) {
		return getCurrentTheme().getIconOrDrawableByKey(key);
	}

	/**
	 * <br>Description: 获取当前主题文本
	 * <br>Author:caizp
	 * <br>Date:2014-4-3上午9:41:12
	 * @param key
	 * @return
	 */
	public String getThemeText(String key) {
		return getCurrentTheme().getTextByKey(key);
	}
	
	/**
	 * <br>Description: 获取当前主题图标文字颜色
	 * <br>Author:caizp
	 * <br>Date:2012-10-10下午03:49:10
	 * @return 默认白色
	 */
	public int getThemeIconTextColor() {
		String colorStr = getThemeText(BaseThemeData.TEXT_COLOR);
		if(!StringUtil.isEmpty(colorStr)){
			try{
				return Color.parseColor(colorStr);
			}catch(Exception e){
				return Color.WHITE;
			}
		}
		return Color.WHITE;
	}
	
	/**
	 * <br>Description: 获取当前主题配置指示灯是否横条型指示灯
	 * <br>Author:caizp
	 * <br>Date:2014-2-26下午6:13:54
	 * @return
	 */
	public boolean isLineLight() {
		if (ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).isDefaultTheme())
			return true;
		
		String launcherLightType = getThemeText(BaseThemeData.LAUNCHER_LIGHT_TYPE);
		if (null == launcherLightType) {
			return true;
		} else {
			if ("0".equals(launcherLightType)) {
				return true;
			} else if ("1".equals(launcherLightType)) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * <br>
	 * Description: 加载当前主题图片资源(自动释放原有资源) <br>
	 * Author:caizp <br>
	 * Date:2011-6-29下午02:38:28
	 * 
	 * @param key
	 *            需换肤控件主题图片key {@link ThemeData}
	 * @param item
	 *            需换肤的控件
	 * @param viewItemType
	 *            需换肤控件图片设置类型 {@link ThemeManager},
	 *            ImageView前景:THEME_ITEM_FOREGROUND, 背景：THEME_ITEM_BACKGROUND
	 */
	public void loadThemeResource(String key, View item,
			int viewItemType) {
		Drawable d = getThemeDrawable(key);
		if (viewItemType == THEME_ITEM_FOREGROUND && item instanceof ImageView) {// ImageView前景图
			// 移除旧图片
			Drawable tmp = ((ImageView) item).getDrawable();
			if (tmp != null) {
				tmp.setCallback(null);
				tmp = null;
			}
			((ImageView) item).setImageDrawable(d);
		} else {// 背景图
			// 移除旧图片
			Drawable tmp = item.getBackground();
			if (tmp != null) {
				tmp.setCallback(null);
				tmp = null;
			}
			item.setBackgroundDrawable(d);
		}
	}
	
	/**
	 * <br>Description: 发送当前主题信息广播
	 * <br>Author:caizp
	 * <br>Date:2014-4-4上午11:08:25
	 * @param context
	 * @param packageName 指定接收应用包名，4.0以下过滤主题询问方包名，只有主题询问方才能收到广播
	 */
	public void sendCurrentThemeInfoBroadcast(final Context context, final String packageName) {
		String themeId = ThemeSharePref.getInstance(context).getCurrentThemeId();
		final Intent themeIntent = new Intent();
		themeIntent.setAction(ThemeGlobal.INTENT_CURRENT_THEME_INFO);
		//主题资源目录
		String currentThemePath = BaseConfig.THEME_DIR + themeId.replace(" ", "_") + "/";
		//天气皮肤目录
		String weatherSkinPath = BaseConfig.BASE_DIR_91CLOCKWEATHER + themeId + "/";
		if(null != packageName) {
			if(Build.VERSION.SDK_INT < 14){//4.0以下过滤主题询问方包名，只有主题询问方才能收到广播
				themeIntent.setPackage(packageName);
			}
			//主题询问方包名
			themeIntent.putExtra("packageName", packageName);
			//查询询问方当前主题模块ID caizp 2014-6-20
			ThemeModuleItem moduleItem = ThemeModuleHelper.getInstance().getCurrentThemeModuleByPkg(packageName);
			if(null != moduleItem) {
				//模块皮肤为默认主题，主题ID修改为默认主题ID，保证第三方应用切换至默认皮肤 caizp 2014-6-25
				if(ThemeGlobal.DEFAULT_THEME_ID.equals(moduleItem.getId())){
					themeId = moduleItem.getId();
				}
				if(ThemeModuleItem.TYPE_MODULE == moduleItem.getType()) {//单独模块包
					currentThemePath = BaseConfig.MODULE_DIR + moduleItem.getKey().replace("@", "/") + "/" + moduleItem.getId().replace(" ", "_") + "/";
					if(ModuleConstant.MODULE_WEATHER.equals(moduleItem.getKey())){//天气模块
						weatherSkinPath = BaseConfig.MODULE_DIR + moduleItem.getKey().replace("@", "/") + "/" + moduleItem.getId().replace(" ", "_") + "/" + moduleItem.getKey().replace("@", "/") + "/";
					}
				} else {//主题模块
					currentThemePath = BaseConfig.THEME_DIR + moduleItem.getId().replace(" ", "_") + "/";
					if(ModuleConstant.MODULE_WEATHER.equals(moduleItem.getKey())){//天气模块
						weatherSkinPath = BaseConfig.BASE_DIR_91CLOCKWEATHER + moduleItem.getId() + "/";
					}
				}
				// 当前主题是默认主题，但第三方模块皮肤为其他主题或单独模块包资源时，主题ID修改为moduleItem.getId()，防止第三方应用切换至默认皮肤 caizp 2014-6-25
				if(ThemeGlobal.DEFAULT_THEME_ID.equals(themeId) && !ThemeGlobal.DEFAULT_THEME_ID.equals(moduleItem.getId())) {
					themeId = moduleItem.getId();
				}
			}
		}
		themeIntent.addFlags(32);//解决4.0以上SDK静态广播无法响应的问题
		themeIntent.putExtra(ThemeGlobal.INTENT_THEME_PARAM_THEME_ID, themeId);
		themeIntent.putExtra("skinPath", currentThemePath);
		themeIntent.putExtra("weatherSkinPath", weatherSkinPath);
		//第三方小插件换肤交互 未开启 caizp 2013-3-11(天天动听插件使用到该参数)
		themeIntent.putExtra("widgetSkinPath", currentThemePath);
		//增加额外信息
		if (null != themeManagerHelper) {
			themeManagerHelper.addThemeInfoIntentExtra(themeIntent);
		}
		context.sendBroadcast(themeIntent);
		if(null != themeId && themeId.contains("/NOID")){//兼容覆盖2.X桌面后主题Id包含"/NOID"的问题
			themeId = themeId.replace("/NOID", "");
			themeIntent.putExtra(ThemeGlobal.INTENT_THEME_PARAM_THEME_ID, themeId);
			themeIntent.putExtra("skinPath", currentThemePath);
			themeIntent.putExtra("weatherSkinPath", weatherSkinPath);
			//第三方小插件换肤交互 未开启 caizp 2013-3-11
			themeIntent.putExtra("widgetSkinPath", currentThemePath + ThemeGlobal.THEME_WIDGET_PATH);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					context.sendBroadcast(themeIntent);
				}
			}, 1500);
		}
	}
	
	/****************************当前主题相关end******************************/
	
	/****************************安装、应用、删除主题begin******************************/
	
	/**
	 * <br>
	 * Description: 应用主题(不显示等待框) <br>
	 * Author:caizp <br>
	 * Date:2012-7-12上午10:43:51
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
	 */
	public synchronized void applyThemeWithOutWaitDialog(final Context ctx, final String themeId, 
			boolean applyWallpaper, boolean sendThemeChangeBroadcast, boolean autoDirection, boolean applyScene) {
		applyThemeWithOutWaitDialog(ctx, themeId, applyWallpaper, sendThemeChangeBroadcast, autoDirection, applyScene, true);
	}
	
	/**
	 * <br>
	 * Description: 应用主题(不显示等待框) <br>
	 * Author:caizp <br>
	 * Date:2012-7-12上午10:43:51
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
	public synchronized void applyThemeWithOutWaitDialog(final Context ctx, 
																final String themeId, 
																boolean applyWallpaper, 
																boolean sendThemeChangeBroadcast, 
																boolean autoDirection, 
																boolean applyScene,
																boolean changeRolling) {
		if (null != themeManagerHelper) {
			themeManagerHelper.applyThemeWithOutWaitDialog(ctx, themeId, applyWallpaper, sendThemeChangeBroadcast, autoDirection, applyScene, changeRolling);
		}
	}
	
	/**
	 * 安装apt主题包
	 * 
	 * @return
	 */
	public synchronized String installAptTheme(String aptPath) {
		try {
			BasePandaTheme theme = ThemeLoader.getInstance().loaderThemeZip(aptPath);
			return theme.getThemeId();
		} catch (PandaDesktopException pe) {
			pe.printStackTrace();
			Log.e("installAptTheme", "error code = " + pe.getErrorCode() + ", message=" + pe.getErrorMesg());
			FileUtil.delFile(aptPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 重新安装文件夹的导入主题 <br>
	 * Author:caizp <br>
	 * Date:2012-7-12下午05:23:00
	 * 
	 * @param ctx
	 * @param file
	 *            主题文件夹
	 */
	public void reInstallAptTheme(Context ctx, File file) {
		try {
			BasePandaTheme pandaTheme = ThemeLoader.getInstance().loaderThemeFolder(
					file.getName());
			String[] themeInfo = ThemeManagerFactory.getInstance().getThemeInfoByIdFlag(ctx,
					pandaTheme.getIDFlag());
			if (!themeInfo[1].equals(pandaTheme.getVersion())) {
				pandaTheme.save();
			} else {
				
			}
		} catch (PandaDesktopException pe) {
			pe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <br>Description: 删除主题相关数据
	 * <br>Author:caizp
	 * <br>Date:2014-3-3上午11:22:13
	 * @param ctx
	 * @param themeId
	 */
	public void removeTheme(Context ctx, String themeId) {
		if(null != themeManagerHelper) {
			themeManagerHelper.removeTheme(ctx, themeId);
		}
	}
	
	/**
	 * <br>Description: 删除指定主题Id的主题数据库数据
	 * <br>Author:caizp
	 * <br>Date:2011-6-30下午06:06:44
	 * @param themeId
	 * @return
	 */
	public boolean removeThemeDatabaseRecord(Context ctx, String themeId) {
		if (themeId == null) {
            return true;
        }
        //删除Theme
        String sql1 = "delete from Theme where id='" + StringUtil.filtrateInsertParam(themeId) + "'";
        //删除KeyConfig
        String sql2 = "delete from KeyConfig where ThemeID='" + StringUtil.filtrateInsertParam(themeId) + "'";
        String sqls[] = new String[] { sql1, sql2 };

        LauncherThemeDataBase db = new LauncherThemeDataBase(ctx);
        boolean ret = false;
        try {
        	ret = db.execBatchSQL(sqls, true);
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
        	if(db != null) {
				db.close() ;
				db = null ;
			}
        }
        return ret;
	}
	
	/**
	 * <br>Description: 删除数据库中的所有主题记录
	 * <br>Author:caizp
	 * <br>Date:2013-5-20下午3:08:53
	 */
	public void deleteAllThemeRecords() {
		Cursor cursor = null;
		LauncherThemeDataBase db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
		try{
			db.execSQL("delete from Theme");
			db.execSQL("delete from KeyConfig");
		} catch(Exception e){
			db.endTransactionByException();
			e.printStackTrace();
		}finally{
			if(cursor != null) {
				cursor.close() ;
				cursor = null ;
			}
			if(db != null) {
				db.close() ;
				db = null ;
			}
		}
	}
	
	/**
	 * <br>Description: 删除在SD卡上的主题数据
	 * <br>Author:caizp
	 * <br>Date:2011-7-13下午05:44:43
	 * @param themeId
	 */
	public void removeThemeAllFile(String themeId) {
		String newThemeId = themeId.replace(" ", "_");
        int indexOf = newThemeId.indexOf("/");
        final String fileName;
        if (-1 != indexOf) {
            fileName = newThemeId.substring(0, indexOf);
        } else {
            fileName = newThemeId;
        }

        //删除主题文件夹
        FileUtil.delFolder(BaseConfig.THEME_DIR + "/" + fileName);
        //删除黄历天气皮肤文件夹
        FileUtil.delFolder(ThemeGlobal.BASE_DIR_CLOCKWEATHER + themeId.replace(" ", "_") + "/");
        //删除91天气秀皮肤文件夹(历史冗余数据)
        FileUtil.delFolder(BaseConfig.BASE_DIR_91CLOCKWEATHER + themeId.replace("/", "_") + "/");
        //删除91天气秀皮肤文件夹(历史冗余数据)
        FileUtil.delFolder(BaseConfig.BASE_DIR_91CLOCKWEATHER + themeId.replace("_", " ") + "/");
        //删除91天气秀皮肤文件夹
        FileUtil.delFolder(BaseConfig.BASE_DIR_91CLOCKWEATHER + themeId + "/");
        //删除备份的图片背景文件
        File picFile = new File(WallpaperUtil.getWPPicHome());
        if ((null != picFile) && picFile.exists()) {
            File[] picFiles = picFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String fileNameTemp = file.getName();
                    return fileNameTemp.startsWith(fileName);
                }
            });
            if (null != picFiles) {
                for (File pic : picFiles) {
                    pic.delete();
                }
            }
        }
    }
	
	/****************************安装、应用、删除主题end******************************/
	
	/****************************获取、更新主题数据begin******************************/
	
	/**
	 * <br>Description:更新theme表中主题的最新使用时间和使用次数
	 * <br>Author:caizp
	 * <br>Date:2014-4-3下午03:46:15
	 */
	public boolean updateUseTimeAndUseCount(LauncherThemeDataBase db, String themeId){
		Cursor cursor = null;
		boolean ret = false ;
		try{
			ret = db.execSQL("update theme set use_time='"+System.currentTimeMillis()+"',use_count=use_count+1 where ID = '"+StringUtil.filtrateInsertParam(themeId)+"'");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor != null) {
				cursor.close() ;
				cursor = null ;
			}
		}
		return ret;
	}
	
	/**
	 * <br>Description: 获取主题数量
	 * <br>Author:caizp
	 * <br>Date:2013-10-14下午05:44:08
	 * @return
	 */
	public int getThemesCount(){
        //读取Theme表
        String sql = "select ID from Theme";
        LauncherThemeDataBase db = null;
        Cursor cursor = null;
        try{
	        db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
	        cursor = db.query(sql);
	        if (cursor != null) {
	            return cursor.getCount();
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	if(null != cursor) {
        		cursor.close();
        		cursor = null;
        	}
        	if(null != db) {
        		db.close();
        		db = null;
        	}
        }
        return 0;
	}
	
	/**
	 * <br>Description: 主题是否存在
	 * <br>Author:caizp
	 * <br>Date:2011-7-13下午05:44:08
	 * @return
	 */
    public boolean isThemeIdLikeExist(Context ctx, String themeId) {
    	if(ThemeGlobal.DEFAULT_THEME_ID.equals(themeId))return true;
        boolean flag = false;
        LauncherThemeDataBase db = new LauncherThemeDataBase(ctx);
        String sql = "select ID from Theme where ID like '" + StringUtil.filtrateInsertParam(themeId) + "%' or ID_FLAG like '" + StringUtil.filtrateInsertParam(themeId) + "%' or ID like '" + StringUtil.filtrateInsertParam(themeId.replace("_", " ")) + "%'";
        Cursor cursor = null;
        try {
        	cursor = db.query(sql);
	        if (cursor != null) {
	            flag = cursor.moveToFirst();
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(null != cursor) {
        		cursor.close();
        		cursor = null;
        	}
        	if(null != db) {
        		db.close();
        		db = null;
        	}
        }
        return flag;
    }
    
    /**
	 * Description: 获取指定主题Id的主题对象实例 <br>
	 * Author:caizp <br>
	 * Date:2014-4-3下午02:34:57
	 * @param themeId
	 * @return
	 */
	public BasePandaTheme getThemeById(String themeId) {
		if(null != themeManagerHelper) {
			if (!StringUtil.isEmpty(themeId)) {
				return themeManagerHelper.createTheme(themeId, false);
			}
			return themeManagerHelper.createTheme(ThemeGlobal.DEFAULT_THEME_ID, false);
		}
		return null;
	}
    
    /**
     * 根据包的唯一标示找到主题基本信息
     * @param idFlag
     * @return 容量为2的字符串数字,string[0] = ID,String[1] = Version
     */
    public String[] getThemeInfoByIdFlag(Context ctx,String idFlag){
    	LauncherThemeDataBase db = null ;
    	Cursor cursor = null ;
    	String[] theme = {"",""} ;
    	try{
    		db = new LauncherThemeDataBase(ctx);
    		cursor = db.query("select ID,Version from theme where ID_FLAG = '"+StringUtil.filtrateInsertParam(idFlag)+"'");
    		if(cursor != null && cursor.getCount() > 0){
    			if(cursor.moveToFirst()){
    				theme[0] = cursor.getString(0);
    				theme[1] = cursor.getString(1);
    			}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}finally{
    		if(cursor != null){
    			cursor.close();
    			cursor = null;
    		}
    		if(db != null) {
    			db.close();
    			db = null;
    		}
    	}
    	return theme ;
    }
    
    /**
	 * <br>Description: 主题名称对应的主题ID
	 * <br>Author:caizp
	 * <br>Date:2011-7-13下午05:44:08
	 * @return
	 */
    public String getThemeIdByName(Context ctx, String themeName) {
    	if(StringUtil.isEmpty(themeName))return null;
    	String themeId = null;
        LauncherThemeDataBase db = new LauncherThemeDataBase(ctx);
        String sql = "select ID from Theme where NAME='" + StringUtil.filtrateInsertParam(themeName) + "' or EN_NAME = '" + StringUtil.filtrateInsertParam(themeName) + "'";
        Cursor cursor = null;
        try {
        	cursor = db.query(sql);
        if (cursor != null && cursor.moveToFirst()) {
        	themeId = cursor.getString(cursor.getColumnIndex("ID"));
        }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(cursor != null){
    			cursor.close();
    			cursor = null;
    		}
    		if(db != null) {
    			db.close();
    			db = null;
    		}
        }
        return themeId;
    }
    
    /****************************获取、更新主题数据end******************************/
    
    //---------------------------  安装、应用、删除模块包begin  --------------------------//
    
    /**
	 * 安装apt主题模块包
	 * 
	 * @return
	 */
	public synchronized String installAptThemeModule(String aptPath, String moduleKey) {
		try {
			String moduleId = ThemeLoader.getInstance().loaderThemeModuleZip(aptPath, moduleKey);
			return moduleId;
		} catch (PandaDesktopException pe) {
			pe.printStackTrace();
			Log.e("installAptThemeModule", "error code = " + pe.getErrorCode() + ", message=" + pe.getErrorMesg());
			FileUtil.delFile(aptPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 重新安装文件夹的模块 <br>
	 * Author:caizp <br>
	 * Date:2014-8-16下午05:23:00
	 * 
	 * @param ctx
	 * @param moduleKey 模块KEY
	 * @param file
	 *            模块文件夹
	 */
	public String reinstallAptThemeModule(Context ctx, String moduleKey, File file) {
		try {
			String moduleId = ThemeLoader.getInstance().loaderThemeModuleFolder(file.getAbsolutePath(), moduleKey);
			return moduleId;
		} catch (PandaDesktopException pe) {
			pe.printStackTrace();
			Log.e("restallAptThemeModule", "error code = " + pe.getErrorCode() + ", message=" + pe.getErrorMesg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <br>
	 * Description: 应用主题模块(不显示等待框) <br>
	 * Author:caizp <br>
	 * Date:2012-7-12上午10:43:51
	 * 
	 * @param ctx
	 * @param modules
	 *            主题模块
	 * @param newThemeId
	 *            新的主题ID, null时表示只更换主题模块, 否则当前主题ID更改为newThemeId(其他模块使用该主题皮肤)
	 * @param autoDirection
	 *            是否自动跳转至桌面(为false时请自己处理跳转)
	 */
	public synchronized void applyThemeModuleWithOutWaitDialog(
			final Context ctx, final List<ThemeModuleItem> modules,
			final String newThemeId, boolean autoDirection) {
		if (null != themeManagerHelper) {
			themeManagerHelper.applyThemeModuleWithOutWaitDialog(ctx, modules, newThemeId, autoDirection);
		}
	}
	
	/**
	 * <br>Description: 删除主题模块相关数据
	 * <br>Author:caizp
	 * <br>Date:2014-3-3上午11:22:13
	 * @param ctx
	 * @param themeId
	 */
	public void removeModule(Context ctx, String moduleId, String moduleKey) {
		if(null != themeManagerHelper) {
			themeManagerHelper.removeModule(ctx, moduleId, moduleKey);
		}
	}
	
	//---------------------------  安装、应用、删除模块包end  --------------------------//
    
}
