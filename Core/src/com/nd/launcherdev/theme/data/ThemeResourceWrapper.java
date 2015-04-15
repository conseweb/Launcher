package com.nd.launcherdev.theme.data;

import java.io.InputStream;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.nd.launcherdev.kitset.util.BaseBitmapUtils;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.kitset.util.SystemUtil;
import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.theme.module.ModuleConstant;
import com.nd.launcherdev.theme.module.ThemeModuleItem;
import com.nd.launcherdev.theme.parse.apk.ExternalTheme;

/**
 * <br>Description:主题包装类
 * <br>Author:caizp
 * <br>Date:2011-6-27下午04:49:28
 */
public class ThemeResourceWrapper{
    /**
     * TAG
     */
    private final String TAG = "ThemeResourceWrapper";

    /**
     * ctx
     */
    private Context ctx;
    
    /**
     * theme
     */
    private BasePandaTheme theme;

    /**
     * 主题类型
     */
    private int type;

    private ExternalTheme extTheme = null;
    private float density;
    
    /**
     * 构造函数
     * @param ctx
     * @param theme
     */
    public ThemeResourceWrapper(Context ctx, BasePandaTheme theme) {
        this.ctx = ctx;
        this.theme = theme;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
    	type = theme.getType();
    	density = theme.getBaseDensity();
        extTheme = null;
        if(type >= ThemeType.PANDAHOME){
            try {
                extTheme = new ExternalTheme(theme.getThemeId(), type);
            } catch (NameNotFoundException e) {
                Log.w("TAG", "Error ThemeId=" + theme.getThemeId());
                e.printStackTrace();
            }
        }
    }

    /**
     * <br>Description:获取主题Id
     * <br>Author:caizp
     * <br>Date:2011-6-27下午04:19:41
     * @return
     */
    public String getThemeId(){
        return theme.getThemeId();
    }
    
	/**
	 * <br>Description:获取主题类型
	 * <br>Author:caizp
	 * <br>Date:2011-6-27下午04:38:35
	 * @return
	 */
    public int getType() {
        return type;
    }

    /**
     * 获取壁纸值
     * @return
     */
    public String getWallpaperValue() {
    	return getDrawableKeyValue(BaseThemeData.WALLPAPER, ModuleConstant.MODULE_WALLPAPER);
    }
    
    /**
     * <br>Description:获取壁纸输入流
     * <br>Author:caizp
     * <br>Date:2011-6-28下午03:46:18
     * @return
     */
    public InputStream getWallpaperStream(){
    	String path = null;
    	InputStream is = null;
        try {
            path = getWallpaperValue();
            if(!StringUtil.isEmpty(path)){
            	is = BaseBitmapUtils.getImageInputStream(ctx, BaseThemeData.WALLPAPER, path);
            }
        } catch (Throwable e) {
        	e.printStackTrace();
            Log.w(TAG, "getWallpaperBitmap fail.", e);
        }
        return is;
    }
    
    /**
     * 
     * <br>Description: 获取屏幕自适配壁纸
     * <br>Author:caizp
     * <br>Date:2011-8-22上午10:14:12
     * @return
     */
    public Bitmap getWallpaperBitmap() {
        Bitmap bp = null;
        String path = null;
        try {
            path = getWallpaperValue();
            if(!StringUtil.isEmpty(path)){
                bp = ThemeFormart.createFixWallBitmap(path);
            }
            if(bp == null){
            	Log.e("ThemeResourceWrapper", "getWallpaperBitmap bp:" + path);
            }
        } catch (Throwable e) {
            Log.w(TAG, "getWallpaperBitmap fail.", e);
        }
        return bp;
    }
    
    /**
     * 获取指定宽度和高度的壁纸
     * @param width
     * @param height
     * @return
     */
    public Bitmap getWallpaperBitmap( int width, int height) {
    	Bitmap bp = null;
        String path = null;
        try {
            path = getWallpaperValue();
            if(!StringUtil.isEmpty(path)){
                bp = ThemeFormart.createFixWallBitmap(path, width, height);
            }
            if(bp == null){
            	Log.e("ThemeResourceWrapper", "getWallpaperBitmap bp:" + path);
            }
        } catch (Throwable e) {
            Log.w(TAG, "getWallpaperBitmap fail.", e);
        }
        return bp;
    }

    /**
     * <br>Description: 获取主题文本(如颜色值)
     * <br>Author:caizp
     * <br>Date:2011-8-26下午04:39:39
     * @param key
     * @return
     */
    public String getKeyThemeText(String key) {
    	String value = theme.getTextMap().get(key);
        if (!StringUtil.isEmpty(value)) {
            if(value.indexOf('@') == 0){
                value = theme.getThemeId() + value;
            }
            return value;
        }
        if(extTheme != null){
            int resId = extTheme.getTextResId(key);
            if(resId != 0){
                return extTheme.getContext().getString(resId);
            }
        }
        if(null == value) {//获取默认文本
        	value = getDefaultString(key);
        }
        return value;
    }
    
    /**
     * <br>Description: 根据图片或图标key获取主题图片所在位置
     * <br>Author:caizp
     * <br>Date:2014-6-19下午1:51:33
     * @param key 图片key
     * @param moduleKey 图片所属模块
     * @return
     */
    public String getDrawableKeyValue(String key, String moduleKey) {
    	String skey = key.replace('.', '_').replace('|', '_').toLowerCase();
    	if(TextUtils.isEmpty(moduleKey)) {//不属于任何模块(主题预览图或无皮肤定义资源)
    		return getThemeDrawablePath(skey, StringUtil.getNotNullString(moduleKey), false);
    	} else {
    		ThemeModuleItem moduleItem = theme.getModuleMap().get(moduleKey);
    		if(null == moduleItem) {//不属于已拆分模块
    			return getThemeDrawablePath(skey, moduleKey, false);
    		} else {//属于已拆分模块(图标、壁纸等)
    			int moduleType = moduleItem.getType();
    			String moduleId = moduleItem.getId();
    			if(ThemeModuleItem.TYPE_MODULE == moduleType) {//属于单独模块包资源
    				String moduleRootPath = moduleKey;
    				if(ModuleConstant.MODULE_WALLPAPER.equals(moduleKey)) {//不单独拆出壁纸模块包，而是包含在单独图标模块包中 caizp 2014-8-1
    					moduleRootPath = ModuleConstant.MODULE_ICONS;
    				}
    				return ResParser.getDrawablePath(BaseConfig.MODULE_DIR + moduleRootPath.replace("@", "/") + "/" + moduleId.replace(" ", "_") + "/", skey, moduleKey, true, true);
    			} else {//属于主题包中的模块资源
    				if(theme.getThemeId().equals(moduleId)) {// 当前主题模块资源
    					if(ThemeGlobal.DEFAULT_THEME_ID.equals(moduleId) && ModuleConstant.MODULE_WALLPAPER.equals(key)) {//默认壁纸
    	    				return BaseConfig.getApplicationContext().getPackageName() + "@" + SystemUtil.getResourceId(ctx, "wallpaper", "drawable");
    					}
    					return getThemeDrawablePath(skey, moduleKey, true);
    				} else {// 其他主题模块资源
    					return ResParser.getDrawablePath(BaseConfig.THEME_DIR + moduleId.replace(" ", "_") + "/", skey, moduleKey, true, true);
    				}
    			}
    		}
    	}
    }
    
    /**
     * <br>Description: 解析图片或图标所属主题目录，并获取其所在位置
     * <br>Author:caizp
     * <br>Date:2014-6-19下午2:02:24
     * @param key 图片或图标key
     * @param moduleKey 图片或图标所属模块
     * @param scanEncryptFile 是否扫描加密文件
     * @return
     */
	private String getThemeDrawablePath(String key, String moduleKey, boolean scanEncryptFile) {
		if (theme.isSupportV6()) {// V6.0新结构主题包
			if(scanEncryptFile && !theme.isGuarded()){//未加密主题包，不扫描加密文件
				scanEncryptFile = false;
			}
			return ResParser.getDrawablePath(BaseConfig.THEME_DIR + theme.getAptPath(), key, moduleKey, true, scanEncryptFile);
		} else {// 旧主题包
			if (extTheme != null) {// apk主题包
				int resId = extTheme.getDrawableId(key);
				if (resId != 0) {
					return theme.getThemeId() + "@" + resId;
				}
			} else {// apt主题包
				return ResParser.getDrawablePath(BaseConfig.THEME_DIR + theme.getAptPath(), key, moduleKey, false, false);
			}
		}
		return null;
	}
	
    /**
     * <br>Description:通过图片键名获取图片Drawable对象
     * <br>Author:caizp
     * <br>Date:2011-6-28下午04:34:28
     * @param key
     * @param getDefaultWhenNull 图片为空时获取默认图片
     * @return
     */
    public Drawable getKeyDrawable(String key, boolean getDefaultWhenNull) {
    	if(null == key)return null;
		if (ThemeGlobal.DEFAULT_VALUE.equals(key)) {
			return null;
		}
		String value = getDrawableKeyValue(key, BaseThemeData.drawableMap.get(key));

		boolean nodpi = false;
		if(BaseThemeData.nodpiDrawableMap.containsKey(key)){//读原图，不做屏幕像素自动转换
			nodpi = true;
		}
		Drawable d = ResParser.getImageDrawable(ctx, key, value, density, nodpi);
		if(null == d && getDefaultWhenNull){//获取默认图片
			d = getDefaultResource(key);
		}
		return d;
	}
    
    /**
     * <br>Description:通过图标键名获取图片Drawable对象
     * <br>Author:caizp
     * <br>Date:2011-6-28下午04:34:28
     * @param key
     * @param getDefaultWhenNull 图片为空时获取默认图片
     * @return
     */
    public Drawable getIconDrawable(String key, boolean getDefaultWhenNull) {
    	if(null == key)return null;
		if (ThemeGlobal.DEFAULT_VALUE.equals(key)) {
			return null;
		}
		String value = getDrawableKeyValue(key, ModuleConstant.MODULE_ICONS);
		if (StringUtil.isEmpty(value)) {
			ThemeModuleItem item = theme.getModuleMap().get(ModuleConstant.MODULE_ICONS);
			if (null != item && !ThemeGlobal.DEFAULT_THEME_ID.equals(item.getId()) && key.contains("|")) {
				// 非默认主题下应用程序图标不获取默认自定义图标
				return null;
			}
			if(getDefaultWhenNull){
				return getDefaultResource(key);
			}
		}
		boolean isThemeIconKey = false;
		if(BaseThemeData.largeIconMap.containsKey(key)){
			isThemeIconKey = true;
		}
		Drawable d = ResParser.getIconDrawable(ctx, StringUtil.renameThemeData(key), value, isThemeIconKey, theme.needScaleIcon());
		
		if(null == d && getDefaultWhenNull){ // 获取默认图标
			ThemeModuleItem item = theme.getModuleMap().get(ModuleConstant.MODULE_ICONS);
			if (null != item && !ThemeGlobal.DEFAULT_THEME_ID.equals(item.getId()) && key.contains("|")) {
				// 非默认主题下应用程序图标不获取默认自定义图标
				return null;
			}
			d = getDefaultResource(key);
		}
		return d;
	}

    /**
     * 是否有91桌面的设置，默认为有，第三方主题可能会没有
     * @return
     */
    public boolean hasPandaConfig(){
        if(extTheme != null){
            return extTheme.hasIHomeConfig();
        }
        return true;
    }
    
    /**
     * 
     * <br>Description:获取默认主题资源
     * <br>Author:zhenghonglin
     * <br>Date:2012-3-23下午03:08:59
     * @param key
     * @return
     */
    public Drawable getDefaultResource(String key){
		Drawable drawable = null;
		try{
			String skey = key.replace('.', '_').replace('|', '_').toLowerCase();
			int resId = ctx.getResources().getIdentifier(skey, "drawable", ctx.getPackageName());
			if(resId == 0)return null;
			drawable = ctx.getResources().getDrawable(resId);
		}catch(Resources.NotFoundException e){
			return null;
		} 		    		
		return drawable;
    }
    
    /**
     * 
     * <br>Description:获取默认主题字符串资源
     * <br>Author:zhenghonglin
     * <br>Date:2012-3-23下午03:08:59
     * @param key
     * @return
     */
    public String getDefaultString(String key){
		if(null == key) return key;
		String result = null;
		try{
			String skey = key.replace('.', '_').replace('|', '_').toLowerCase();
			int resId = ctx.getResources().getIdentifier(skey, "string", ctx.getPackageName());
			if(resId == 0)return null;
			result = ctx.getResources().getString(resId);
		}catch(Resources.NotFoundException e){
			return null;
		} 		    		
		return result;
    }

	
}
