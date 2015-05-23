package com.bitants.common.theme.parse.apk;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.bitants.common.launcher.config.BaseConfig;

/**
 * <br>Description:扩展主题类
 */
public class ExternalTheme {

    /**PANDAHOME_FLAG*/
    public final static String PANDAHOME_FLAG = "pandahome";
    
    public final static String THEME_DESC = "theme_desc";
    
    public final static String TEXT_COLOR = "text_color";
    
    public final static String TEXT_SIZE = "text_size";
    
    public final static String THEME_NAME = "theme_name";
    
    public final static String THEME_VERSION = "theme_version";
    
    private Context remoteCtx;

    private String packageName;

    private String pandahomeFlag = null;

    public ExternalTheme(String packageName, int type) throws NameNotFoundException {
        remoteCtx = BaseConfig.getApplicationContext().createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

        this.packageName = packageName;
        init();
    }

    private void init() {
        int resId = getResourceId(PANDAHOME_FLAG, "string");
        if (resId != 0) {
        	pandahomeFlag = remoteCtx.getString(resId);
        } else {
        	pandahomeFlag = null;
        }
    }

    /**
     * 获取图片资源id
     * @param key
     * @return
     */
    public int getDrawableId(String key) {
        int resId = 0;

        String skey = key.replace('.', '_').replace('|', '_').toLowerCase();
        resId = getResourceId(skey, "drawable");
        if (resId != 0) {
            return resId;
        }

        return resId;
    }
    
    /**
     * 获取图片资源
     * @param key
     * @return
     */
    public Drawable getDrawable(String key) {
    	Drawable d = null;
    	int resId = getDrawableId(key);
    	if(0 != resId) {
    		d = remoteCtx.getResources().getDrawable(resId);
    	}
    	return d;
    }
    
    /**
     * 获取主题文本
     * @param key
     * @return
     */
    public String getString(String key) {
    	String result = null;
    	int resId = getTextResId(key);
    	if( 0 != resId) {
    		result = remoteCtx.getString(resId);
    	}
    	return result;
    }
    
    /**
     * 获取主题文本的资源Id(如颜色值)
     * @param key
     * @return
     */
    public int getTextResId(String key) {
    	int resId = 0;

        resId = getResourceId(key, "string");
        if (resId != 0) {
            return resId;
        }
    	return 0;
    }

    /**
     * 获取壁纸id
     * @return
     */
    public int getWallpaperId() {
        int resId = 0;

        resId = getResourceId("wallpaper", "drawable");
        if (resId != 0) {
            return resId;
        }

        return resId;
    }

    /**
     * 是否有IHome标记
     * @return
     */
    public boolean hasIHomeConfig() {
        if (pandahomeFlag != null) {
            return true;
        }
        return false;
    }

    private int getResourceId(String key, String type) {
        return remoteCtx.getResources().getIdentifier(key, type, packageName);
    }

    /**
     * 获取external theme 上下文
     * @return
     */
    public Context getContext() {
        return remoteCtx;
    }
    
    public String getPackageName(){
    	return packageName;
    }
    
}
