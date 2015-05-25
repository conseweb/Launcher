package com.bitants.common.theme.parse.apk;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.util.Log;

import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.theme.data.ThemeType;
import com.bitants.common.theme.ThemeManagerFactory;
import com.bitants.common.theme.data.BasePandaTheme;
import com.bitants.common.theme.data.BaseThemeData;
import com.bitants.common.theme.db.LauncherThemeDataBase;
import com.bitants.common.theme.parse.apt.ThemeLoader;
import com.bitants.common.kitset.util.FileUtil;
import com.bitants.common.kitset.util.WallpaperUtil;
import com.bitants.common.theme.data.ThemeGlobal;

/**
 * <br>Description: 检测APK主题
 */
public class CheckThirdThemes extends Thread{
	
	private final String TAG = "CheckThirdThemes";
	
	private Context ctx;
	private static boolean isRunning = false;
	private static int totalTask = 0;
	private static int doneTask = 0;
	
	public CheckThirdThemes(){
		this.ctx = BaseConfig.getApplicationContext();
	}
	
	public static boolean isRunning(){
		return isRunning;
	}
	
	public static int getTotalTask(){
		return totalTask;
	}
	
	public static int getDoneTask(){
		return doneTask;
	}
	
	@Override
	public void run() {
		if(isRunning){
			return;
		}
		Log.v(TAG, "CheckApkThemes...");
		isRunning = true;
		try {
			
			//有优先级关系
		    
		    //检查已经删除的主题
			checkUninstallThemes();
			//安装主题
			installExternalThemes();
			
		} finally {
			Intent it = new Intent(ThemeGlobal.INTENT_THEME_LIST_REFRESH);
			ctx.sendBroadcast(it);
			isRunning = false;
			totalTask = 0;
			doneTask = 0;
		}
	}
	
	/**
	 * <br>Description: 检测所有已安装的PandaHome主题包并写入数据库
	 */
	private void installExternalThemes(){
        final List<String> pandaList = checkExternalThemes(ThemeGlobal.INTENT_PANDAHOME_THEME);
        
        Thread t0 = null;
        if(pandaList != null && pandaList.size() > 0){
            totalTask += pandaList.size();
            t0 = new Thread(){
                @Override
				public void run(){
                    saveExternalThemes(pandaList, ThemeType.PANDAHOME);
                }
            };
            t0.start();
        }
        
        if(t0 != null){
            try {
                t0.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	private  void saveExternalThemes(List<String> list, int type){
		saveExternalThemes(ctx, list, type);
    }

	public static void saveExternalThemes(Context context ,List<String> list, int type){
        if(list == null){
            return;
        }
        for(String packName : list){
            try {
                ExternalTheme et = new ExternalTheme(packName, type);
                //过滤安卓桌面主题包
                if(0 != et.getDrawableId("panda_dock_theme")
                		|| 0 != et.getDrawableId("panda_dock_sys_switch")
                		|| 0 != et.getDrawableId("bg_launcher_item")){
                	continue;
                }
                
                final BasePandaTheme theme = ThemeManagerFactory.getInstance().getThemeManagerHelper().allocatPandaThemeObj();
                theme.setThemeId(packName);
                theme.setIDFlag(packName);
                if(et.hasIHomeConfig()){
                    String name = et.getString(ExternalTheme.THEME_NAME);
                    if(name == null){
                        ApplicationInfo info = context.getPackageManager().getApplicationInfo(packName, 0);
                        name = "" + context.getPackageManager().getApplicationLabel(info);
                    }
                    theme.setThemeName(name);
                    theme.setThemeEnName(name);
                    theme.loadOtherDataFromExContext(et);
                    theme.setSavedPandaFlag(1);
                } else{
                    ApplicationInfo info = context.getPackageManager().getApplicationInfo(packName, 0);
                    String name = "" + context.getPackageManager().getApplicationLabel(info);
                    theme.setThemeName(name);
                    theme.setThemeEnName(name);
                    theme.setSavedPandaFlag(0);
                }
                PackageInfo packInfo = context.getPackageManager().getPackageInfo(packName, PackageManager.GET_INSTRUMENTATION);
                theme.setVersionCode(packInfo.versionCode);
                theme.setVersion(""+packInfo.versionName);
                theme.setType(type);
                //先删除原来的主题
                ThemeManagerFactory.getInstance().removeTheme(context, theme.getThemeId());
                theme.save();
                //保存APK壁纸至主题壁纸存放目录  caizp 2011-7-19
                int wallpaperResId = et.getWallpaperId();
                if(0 != wallpaperResId){
                	InputStream wallpaperStream = et.getContext().getResources().openRawResource(wallpaperResId);
                	if(null != wallpaperStream){
                		FileUtil.saveStream2File(wallpaperStream,WallpaperUtil.getWPPicHome() + "/" + packName + ".jpg");
                	}
                }
                Context etCtx = et.getContext();
                if(null != etCtx){
                	// 兼容安卓锁屏主题壁纸
                    int lockBgResId = etCtx.getResources().getIdentifier(BaseThemeData.PANDA_LOCK_MAIN_BACKGROUND, "drawable", et.getPackageName());
                    if(0 != lockBgResId){
                    	InputStream lockBgInputStream = null;
                    	lockBgInputStream = etCtx.getResources().openRawResource(lockBgResId);
                    	String lockSkinPath = BaseConfig.THEME_DIR + theme.getThemeId() + ThemeGlobal.THEME_91ZNS_PATH;
                    	File dir = new File(lockSkinPath);
                		if (!dir.isDirectory()) {
                			dir.mkdirs();
                		}
                    	FileUtil.saveStream2File(lockBgInputStream, lockSkinPath + BaseThemeData.ZNS_LOCK_BG + ".jpg");
                    }
                    //加载主题天气和锁屏皮肤包
                	int weatherResId = etCtx.getResources().getIdentifier("weather", "raw", et.getPackageName());
                	if(0 != weatherResId){
                		InputStream weatherZipInputStream = null;
                    	weatherZipInputStream = etCtx.getResources().openRawResource(weatherResId);
                    	String tempPath = createWeatherClockTempPath();
                    	//解压天气皮肤包
                    	FileUtil.saveStream2File(weatherZipInputStream, tempPath+ThemeGlobal.THEME_CLOCKWEATHER_SKIN);
                        ThemeLoader.loadThemeWeather(tempPath, et.getPackageName());
                        FileUtil.delFile(tempPath+ThemeGlobal.THEME_CLOCKWEATHER_SKIN);
                    }
                	int widgetResId = etCtx.getResources().getIdentifier("widget", "raw", et.getPackageName());
                	if(0 != widgetResId){
                		InputStream widgetZipInputStream = null;
                		widgetZipInputStream = etCtx.getResources().openRawResource(widgetResId);
                    	String tempPath = BaseConfig.THEME_DIR + theme.getThemeId() + "/";
                    	File dir = new File(tempPath);
                		if (!dir.isDirectory()) {
                			dir.mkdirs();
                		}
                		//解压第三方小插件皮肤包
                        FileUtil.saveStream2File(widgetZipInputStream, tempPath + ThemeGlobal.THEME_WIDGET_SKIN);
                        ThemeLoader.loadThemeWidget(tempPath, et.getPackageName());
                        FileUtil.delFile(tempPath + ThemeGlobal.THEME_WIDGET_SKIN);
                	}
                }
                Log.i("CheckApkTheme", "installd:" + theme.getThemeId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
	
    private List<String> checkExternalThemes(String action){
        //1. 查询
        Intent queryIntent = new Intent(action);
        final PackageManager manager = ctx.getPackageManager();
        final List<ResolveInfo> apps = manager.queryIntentActivities(
                queryIntent, 0);
        if (apps != null && apps.size() > 0){
            //1. 查询所有apk的主题包
            ArrayList<String> ids = new ArrayList<String>();
            HashMap<String, String> verMap = new HashMap<String, String>();
            String pkgName = "" ;
            for (ResolveInfo app : apps) {
            	pkgName = app.activityInfo.packageName ;
                ids.add(pkgName);
                try {
					verMap.put(pkgName, ctx.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_INSTRUMENTATION).versionName+"");
				} catch (NameNotFoundException e) {
					verMap.put(pkgName,"1.0");
					e.printStackTrace();
				}
            }
            //2. 判断主题包是否已安装
            if(ids.size() > 0){
                StringBuffer sb = new StringBuffer();
                sb.append("select ID from Theme where 1 = 0");
                for(String id:verMap.keySet()){
                    sb.append(" or (ID = '" + id + "' and Version = '"+verMap.get(id)+"')"); 
                }
                //String sql = sb.substring(0, sb.length() -1) + ")";
                String sql = sb.toString();
                LauncherThemeDataBase db = new LauncherThemeDataBase(ctx);
                Cursor c = db.query(sql);
                if(c != null){
                    if(c.getCount() == ids.size()){
                        c.close();
                        db.close();
                        return null;
                    }
                    boolean ret = c.moveToFirst();
                    while(ret){
                        ids.remove(c.getString(0));
                        ret = c.moveToNext();
                    }
                    c.close();
                }
                db.close();
            }
            return ids;
        }
        return null;
    }

    /**
     * <br>Description: 检测已被卸载的APK主题，删除数据库中遗留的数据
     */
	private void checkUninstallThemes(){
		String sql = "select id from Theme where type='" + ThemeType.PANDAHOME + "'";
		LauncherThemeDataBase db = new LauncherThemeDataBase(ctx);
		Cursor c = db.query(sql);
		if(c != null){ 
			boolean ret = c.moveToFirst();
			while(ret){
				String id = c.getString(0);
				try {
					ctx.createPackageContext(id, 0);
				} catch (NameNotFoundException e) {
					//删除卸载主题的 
					//先删除原来的主题
					ThemeManagerFactory.getInstance().removeTheme(ctx, id);
					Log.d(TAG, "removeTheme:" + id);
				}
				ret = c.moveToNext();
			}
			c.close();
		}
		db.close();
	}
	
	/**
	 * <br>
	 * Description: 创建91黄历天气皮肤数据存放目录 <br>
	 */
	public static String createWeatherClockTempPath() {
		final String baseDir = ThemeGlobal.BASE_DIR_CLOCKWEATHER;
		File dir = new File(baseDir);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return baseDir;
	}
	
}
