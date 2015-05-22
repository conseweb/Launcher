package com.bitants.launcherdev.theme.data;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.launcherdev.theme.db.LauncherThemeDataBase;
import com.bitants.launcherdev.theme.module.ThemeModuleHelper;
import com.bitants.launcherdev.theme.parse.apk.ExternalTheme;
import com.bitants.launcherdev.kitset.util.BaseBitmapUtils;
import com.bitants.launcherdev.theme.module.ModuleConstant;
import com.bitants.launcherdev.theme.module.ThemeModuleItem;
import com.bitants.launcherdev.theme.pref.ThemeSharePref;

/**
 * <br>Description: 基础主题
 */
public class BasePandaTheme {
	
    /**
     * 数据库操作对象
     */
    protected LauncherThemeDataBase db;

    /**context*/
    protected Context context;

    /**
     * 主题ID
     */
    private String themeId = ThemeGlobal.DEFAULT_THEME_ID;
    
    /**
     * 主题唯一标识
     */
    private String IDFlag = "" ;

	/**
     * 主题中文名称
     */
    private String themeName = "";

    /**
     * 主题英文名称
     */
    private String themeEnName = "";

    /**
     * 主题中文描述
     */
    private String themeDesc = "";
    
    /**
     * 主题英文描述
     */
    private String themeEnDesc = "";

    /**
     * 主题版本名称
     */
    private String version = "";
    
    /**
     * 主题版本号
     */
    private int versionCode = 1;

    /**
     * 主题文字信息
     */
    private HashMap<String, String> textMap = new HashMap<String, String>();
    
    /**
     * 主题类型(参看ThemeType)
     */
    private int type = ThemeType.DEFAULT;
    
    /**
     * 保存在库中是参数标识位,默认为-1
     */
    private int savedPandaFlag = -1; //保存在库中是参数标识位,默认为-1

    private float baseDensity = 1.5f;
    
    /**
     * apt主题资源相对路径
     */
    private String aptPath = "";
    
    /**
     * 是否支持V6.0主题分模块
     * (数据库1=true,0=false)
     */
    private boolean supportV6 = false;
    /**
     * 主题资源是否已被加密
     */
    private boolean guarded = false;
    /**
     * 主题资源加密算法版本号
     */
    private int guardedVersion = 1;
    /**
     * 服务端定义的资源类型ID(用于升级)
     */
    private int resType = 0;
    /**
     * 主题支持的桌面最低版本(该版本号大于桌面版本号时，表示桌面版本过低，不支持该主题)
     */
    private int launcherMinVersion = 5998;
    
    /**
     * 主题模块信息
     */
    private HashMap<String, ThemeModuleItem> moduleMap = new HashMap<String, ThemeModuleItem>();
    
    /**wrapper*/
    protected ThemeResourceWrapper wrapper;
    

	/** 新增主题的时候使用该构造函数
     * @param ctx Context
     */
    public BasePandaTheme(Context ctx) {
        this.context = ctx;
        db = new LauncherThemeDataBase(ctx);
        initData();
        wrapper = new ThemeResourceWrapper(ctx, this);
    }

    /**
     * 构造函数，并加载指定themeId
     * @param ctx Context
     * @param themeId String
     */
    public BasePandaTheme(Context ctx, String themeId) {
        this.context = ctx;
        setThemeId(themeId);
        db = new LauncherThemeDataBase(ctx);
        loadTheme(false);
    }
    
    /**
     * <br>Description: 构造函数，并加载指定themeId
     * @param ctx
     * @param themeId
     * @param isCurrentTheme 是否当前主题 
     */
    public BasePandaTheme(Context ctx, String themeId, boolean isCurrentTheme) {
    	this.context = ctx;
    	setThemeId(themeId);
        db = new LauncherThemeDataBase(ctx);
        loadTheme(isCurrentTheme);
    }
    
    protected void initData() {
    	IDFlag = "";
        themeName = "";
        themeEnName = "";
        themeDesc = "";
        version = "";
        textMap.clear();
        savedPandaFlag = -1;
        versionCode = -1;
        type = ThemeType.DEFAULT;
        baseDensity = 1.5f;
        aptPath = "";
    }
    
    private void loadTheme(boolean isCurrentTheme) {
        initData();
        if(!ThemeGlobal.DEFAULT_THEME_ID.equals(getThemeId())) {
	        String sql = "select * from Theme where id='" + getThemeId() + "'";
	        Cursor cursor = db.query(sql);
	        if (cursor != null) {
	            if (cursor.moveToFirst()) {
	            	setThemeName(StringUtil.getString(cursor, "NAME"));
	                setThemeEnName(StringUtil.getString(cursor, "EN_NAME"));
	                setThemeDesc(StringUtil.getString(cursor, "DESC"));
	                setVersion(StringUtil.getString(cursor, "Version"));
	                setIDFlag(StringUtil.getString(cursor, "ID_FLAG"));
	                setType(cursor.getInt(cursor.getColumnIndex("type")));
	                setSavedPandaFlag(cursor.getInt(cursor.getColumnIndex("pandaflag")));
	                setVersionCode(cursor.getInt(cursor.getColumnIndex("versioncode")));
	                setBaseDensity(cursor.getFloat(cursor.getColumnIndex("base_density")));
	                setAptPath(StringUtil.getString(cursor, "PATH"));
	                setSupportV6(cursor.getInt(cursor.getColumnIndex("support_v6")) == 1);
	                setGuarded(cursor.getInt(cursor.getColumnIndex("guarded")) == 1);
	                setGuardedVersion(cursor.getInt(cursor.getColumnIndex("guarded_version")));
	                setResType(cursor.getInt(cursor.getColumnIndex("res_type")));
	                setLauncherMinVersion(cursor.getInt(cursor.getColumnIndex("launcher_min_version")));
	                loadOtherDataFromDb(cursor);
	            }
	            cursor.deactivate();
	            cursor.close();
	        }
	        
	        //读取KeyConfig配置
	        sql = "select AppID, Text from KeyConfig where ThemeID='" + getThemeId() + "'";
	        cursor = db.query(sql);
	        if (cursor != null) {
	            boolean ret = cursor.moveToFirst();
	            while (ret) {
	                String key = StringUtil.getString(cursor, "AppID").toLowerCase();
	                String text = StringUtil.getString(cursor, "Text");
	                if (text != null) {
	                    getTextMap().put(key, text);
	                }
	                ret = cursor.moveToNext();
	            }
	            cursor.deactivate();
	            cursor.close();
	        }
        }
        wrapper = new ThemeResourceWrapper(context, this);
        if(isCurrentTheme) {
        	// 读取当前主题模块信息 caizp 2014-6-13
    		List<ThemeModuleItem> moduleItems = ThemeModuleHelper.getInstance().getCurrentThemeModule();
    		for(int i=0; i<moduleItems.size(); i++) {
    			ThemeModuleItem item = moduleItems.get(i);
    			moduleMap.put(item.getKey(), item);
    		}
	        // 设置当前主题图标蒙板
    		BaseConfig.iconBackground = BaseBitmapUtils.drawable2Bitmap(
    				getIconMask(BaseThemeData.PANDA_ICON_BACKGROUND_MASK));
    		BaseConfig.iconFrontground = BaseBitmapUtils.drawable2Bitmap(
    				getIconMask(BaseThemeData.PANDA_ICON_FOREGROUND_MASK));
    		BaseConfig.iconMask = BaseBitmapUtils.drawable2Bitmap(
    				getIconMask(BaseThemeData.PANDA_ICON_CUT_MASK));
        } else {
        	// 设置主题模块信息 caizp 2014-6-13
        	for(int i=0; i<ModuleConstant.MODULE_KEY_ARRAY.length; i++) {
        		ThemeModuleItem item = new ThemeModuleItem();
        		item.setId(getThemeId());
        		item.setKey(ModuleConstant.MODULE_KEY_ARRAY[i][0]);
        		item.setPgk(ModuleConstant.MODULE_KEY_ARRAY[i][1]);
        		item.setType(ThemeModuleItem.TYPE_THEME);
        		moduleMap.put(ModuleConstant.MODULE_KEY_ARRAY[i][0], item);
        	}
        }
        db.close();
	}
    
    public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

    public String getIDFlag() {
		return IDFlag;
	}

	public void setIDFlag(String iDFlag) {
		IDFlag = iDFlag;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public String getThemeEnName() {
		return themeEnName;
	}

	public void setThemeEnName(String themeEnName) {
		this.themeEnName = themeEnName;
	}

	public String getThemeDesc() {
		return themeDesc;
	}

	public void setThemeDesc(String themeDesc) {
		this.themeDesc = themeDesc;
	}

	public String getThemeEnDesc() {
		return themeEnDesc;
	}

	public void setThemeEnDesc(String themeEnDesc) {
		this.themeEnDesc = themeEnDesc;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public HashMap<String, String> getTextMap() {
		return textMap;
	}

	public void setTextMap(HashMap<String, String> textMap) {
		this.textMap = textMap;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSavedPandaFlag() {
		return savedPandaFlag;
	}

	public void setSavedPandaFlag(int savedPandaFlag) {
		this.savedPandaFlag = savedPandaFlag;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public float getBaseDensity() {
		return baseDensity;
	}

	public void setBaseDensity(float baseDensity) {
		this.baseDensity = baseDensity;
	}
	
	public String getAptPath() {
		return aptPath;
	}

	public void setAptPath(String aptPath) {
		this.aptPath = aptPath;
	}
	
	public boolean isSupportV6() {
		return supportV6;
	}

	public void setSupportV6(boolean supportV6) {
		this.supportV6 = supportV6;
	}

	public boolean isGuarded() {
		return guarded;
	}

	public void setGuarded(boolean guarded) {
		this.guarded = guarded;
	}

	public int getGuardedVersion() {
		return guardedVersion;
	}

	public void setGuardedVersion(int guardedVersion) {
		this.guardedVersion = guardedVersion;
	}
	
	public int getResType() {
		return resType;
	}

	public void setResType(int resType) {
		this.resType = resType;
	}

	public int getLauncherMinVersion() {
		return launcherMinVersion;
	}

	public void setLauncherMinVersion(int launcherMinVersion) {
		this.launcherMinVersion = launcherMinVersion;
	}

	public HashMap<String, ThemeModuleItem> getModuleMap() {
		return moduleMap;
	}

	public void setModuleMap(HashMap<String, ThemeModuleItem> moduleMap) {
		this.moduleMap = moduleMap;
	}

	/**
     * 获取Wrapper
     * @return ThemeResourceWrapper
     */
    public ThemeResourceWrapper getWrapper() {
        return wrapper;
    }
    
    /**
     * <br>Description: 获取所有主题预览图路径(由主题预览图key(模块预览图为模块key)与路径组成)
     * @return
     */
    public String[][] getAllPreviewPath() {
    	//主题预览图
    	String[] themePreviews = {BaseThemeData.PREVIEW0, BaseThemeData.PREVIEW1, BaseThemeData.PREVIEW2};
    	String[][] result = new String[themePreviews.length+ModuleConstant.MODULE_KEY_ARRAY.length][2];
    	for(int i=0; i<themePreviews.length; i++) {
    		result[i][0] = themePreviews[i];
    		result[i][1] = wrapper.getDrawableKeyValue(themePreviews[i], "");
    	}
    	for(int i=themePreviews.length; i<ModuleConstant.MODULE_KEY_ARRAY.length+themePreviews.length; i++) {
    		result[i][0] = ModuleConstant.MODULE_KEY_ARRAY[i-themePreviews.length][0];
    		result[i][1] = wrapper.getDrawableKeyValue("preview", ModuleConstant.MODULE_KEY_ARRAY[i-themePreviews.length][0]);
    	}
    	return result;
    }
    
    /**
	 * <br>Description: 获取主题壁纸stream,无壁纸时返回null
	 * @return
	 */
	public InputStream getWallpaperStream(){
		return wrapper.getWallpaperStream();
	}
	
	/**
	 * <br>Description: 获取主题壁纸Bitmap,无壁纸时返回null
	 * @return
	 */
	public Bitmap getWallpaperBitmap(){
		return wrapper.getWallpaperBitmap();
	}
	
	/**
	 * <br>Description:获取主题图片接口
	 * (包括图标、普通图片)
	 * @param key 请参考{@link ThemeData}中的主题常量定义
	 * @return
	 */
	public Drawable getIconOrDrawableByKey(String key){
		key = key.toLowerCase();
		if(null != BaseThemeData.largeIconMap.get(key)){
			return getIconByKey(key, true);
		}else if(null != BaseThemeData.drawableMap.get(key)){
			return getDrawableByKey(key, false, true);
		}else{
			return getIconByKey(key, true);
		}
	}
	
	/**
	 * <br>Description:获取主题切割蒙板, 前蒙板， 后蒙板接口
	 * @return
	 */
	public Drawable getIconMask(String key){
		boolean isDefaultTheme = ThemeSharePref.getInstance(context).isDefaultTheme();
		key = key.toLowerCase();
		if(isDefaultTheme){
			return getIconOrDrawableByKey(key);
		}else{
			return getIconByKey(key, false);
		}
	}

	
	/**
	 * <br>Description:获取主题文本(如颜色，获取不到时返回null)
	 * @param key
	 * @return
	 */
	public String getTextByKey(String key){
		key = key.toLowerCase();
		return wrapper.getKeyThemeText(key);
	}
	
	/**
	 * <br>Description:获取主题图标
	 * @param key
	 * @return
	 */
	private Drawable getIconByKey(String key, boolean getDefaultWhenNull){
		Drawable d = null;
		if(null != BaseThemeData.largeIconMap.get(key)){
			return wrapper.getIconDrawable(key, getDefaultWhenNull);
		}
		String sThemeIconKey = ThemeIconIntentAdaptation.getInstance().getThemeIconKey(key);
		if (null == sThemeIconKey){
			d = wrapper.getIconDrawable(key, getDefaultWhenNull);
		} else {
			d = wrapper.getIconDrawable(sThemeIconKey, getDefaultWhenNull);
		}
		return d;
	}
	
	/**
	 * <br>Description:获取主题图片(非图标)
	 * @param key
	 * @param isStateListDrawable  是否多态图片
	 * @param getDefaultWhenNull  获取不到图片时是否获取默认图片
	 * @return
	 */
	public Drawable getDrawableByKey(String key, boolean isStateListDrawable, boolean getDefaultWhenNull){
		if(isStateListDrawable){
			String[] states = {"_normal","_focused","_pressed"};
			// normal
			Drawable normal = wrapper.getKeyDrawable(key + states[0], true);
			// selected
			Drawable selected = wrapper.getKeyDrawable(key + states[1], true);
			// pressed
			Drawable pressed = wrapper.getKeyDrawable(key + states[2], true);
			if (pressed == null) {
				pressed = wrapper.getKeyDrawable(key + "_selected", true);
			}
			// 组合
			StateListDrawable sd = new StateListDrawable();
			int[] stateSet = new int[2];
			if(null != normal){
				stateSet = new int[1];
				stateSet[0] = -android.R.attr.state_pressed;
				sd.addState(stateSet, normal);
			}
			if(null != pressed){
				// c.
				stateSet = new int[1];
				stateSet[0] = android.R.attr.state_pressed;
				sd.addState(stateSet, pressed);
			}
			if(null != selected){
				stateSet = new int[2];
				stateSet[0] = android.R.attr.state_focused;
				stateSet[1] = -android.R.attr.state_pressed;
				sd.addState(stateSet, selected);
			}

			return sd;
		}
		Drawable d = wrapper.getKeyDrawable(key, getDefaultWhenNull);
		return d;
	}
	
	/**
	 * <br>Description: 从数据库中读取其他主题数据
	 */
	protected void loadOtherDataFromDb(Cursor cursor) {
		
	}
	
	/**
	 * <br>Description: 从主题包的xml中读取信息
	 * @param map
	 */
	public void loadOtherDataFromXml(Map<String, String> attrMap) {
		
	}
	
	/**
	 * <br>Description: 从apk主题包中读取信息
	 */
	public void loadOtherDataFromExContext(ExternalTheme et) {
		
	}
	
	/**
	 * <br>Description: 是否需要缩放图标
	 * @return
	 */
	protected boolean needScaleIcon() {
		return true;
	}
	
	/**
     * 删除主题数据库记录
     * @return
     */
    public boolean delete() {

        String[] sqls = new String[3];

        // 1. 删除Theme表
        sqls[0] = "DELETE FROM 'Theme' WHERE ID='" + getThemeId() + "'";

        // 2. 删除KeyConfig表
        sqls[1] = "DELETE FROM 'KeyConfig' WHERE ThemeID='" + getThemeId() + "'";
        
        // 3. 带事务批量执行
        boolean ret = db.execBatchSQL(sqls, true);

        return ret;
    }
    
    /**
     * <br>Description:保存主题数据至数据库
     * @return
     */
    public int save() {
        // 1. 判断该ID是否已存在
        try {
            String sql = "select * from Theme where ID='" + getThemeId() + "'";
            Cursor cursor = db.query(sql);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.deactivate();
                cursor.close();
                cursor = null;
                db.close();
                return ThemeGlobal.THEME_EXIST;
            } else if (cursor != null) {
                cursor.deactivate();
                cursor.close();
                cursor = null;
            }

            // SQL语句数组
            String[] sqls = new String[getTextMap().size() + 1];

            // 2. 添加到Theme表中
            String themeSQL = "INSERT INTO 'Theme'('ID','NAME','EN_NAME','DESC','EN_DESC','Version'," +
	    		"'type','pandaflag','versioncode','base_density','ID_FLAG','PATH','install_time'," +
	    		"'use_time','use_count','support_v6','guarded','guarded_version','res_type','launcher_min_version') VALUES('%s','%s','%s','%s','%s','%s'," + 
	    		"'%s',%s,%s,%s,'%s','%s',%s,%s,%s,%s,%s,%s,%s,%s)";
            sqls[0] = String.format(themeSQL, StringUtil.getNotNullString(getThemeId()), 
            		StringUtil.getNotNullString(getThemeName()), StringUtil.getNotNullString(getThemeEnName()), 
            		StringUtil.getNotNullString(getThemeDesc()), StringUtil.getNotNullString(getThemeEnDesc()),
            		StringUtil.getNotNullString(getVersion()), getType(), getSavedPandaFlag(), 
            		getVersionCode(), getBaseDensity(),StringUtil.getNotNullString(getIDFlag()), getAptPath(), 
            		System.currentTimeMillis(), System.currentTimeMillis(), 0, isSupportV6()?1:0, 
            		isGuarded()?1:0, getGuardedVersion(), getResType(), getLauncherMinVersion());

            // 3. 添加到KeyConfig表中
            int i = 1;
            for (Iterator<String> iterator = getTextMap().keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                String tmp = "INSERT INTO 'KeyConfig'('ThemeID','AppID','Text') VALUES('%s','%s','%s')";
                sqls[i++] = String.format(tmp, StringUtil.getNotNullString(getThemeId()), 
                		StringUtil.getNotNullString(key), StringUtil.getNotNullString(getTextMap().get(key)));
            }

            // 4. 带事务批量执行
            boolean ret = db.execBatchSQL(sqls, true);
            if (!ret) {
                return ThemeGlobal.EXEC_FAIL;
            }
        } finally {
            db.close();
        }
        return ThemeGlobal.EXEC_SUCCESS;
    }
    
    /**
     * <br>Description: 更新主题数据
     * @return
     */
    public int update() {
        try {
        	// 1. 删除原有主题记录
        	String[] sqls = new String[getTextMap().size() + 3];
            sqls[0] = "DELETE FROM 'Theme' WHERE ID='" + getThemeId() + "'";
            sqls[1] = "DELETE FROM 'KeyConfig' WHERE ThemeID='" + getThemeId() + "'";

            // 2. 添加到Theme表中
            String themeSQL = "INSERT INTO 'Theme'('ID','NAME','EN_NAME','DESC','EN_DESC','Version'," +
	    		"'type','pandaflag','versioncode','base_density','ID_FLAG','PATH','install_time'," +
	    		"'use_time','use_count','support_v6','guarded','guarded_version','res_type','launcher_min_version') VALUES('%s','%s','%s','%s','%s','%s'," + 
	    		"'%s',%s,%s,%s,'%s','%s',%s,%s,%s,%s,%s,%s,%s,%s)";
            sqls[2] = String.format(themeSQL, StringUtil.getNotNullString(getThemeId()), 
            		StringUtil.getNotNullString(getThemeName()), StringUtil.getNotNullString(getThemeEnName()), 
            		StringUtil.getNotNullString(getThemeDesc()), StringUtil.getNotNullString(getThemeEnDesc()),
            		StringUtil.getNotNullString(getVersion()), getType(), getSavedPandaFlag(), 
            		getVersionCode(), getBaseDensity(),StringUtil.getNotNullString(getIDFlag()), getAptPath(), 
            		System.currentTimeMillis(), System.currentTimeMillis(), 0, isSupportV6()?1:0, 
            		isGuarded()?1:0, getGuardedVersion(), getResType(), getLauncherMinVersion());

            // 3. 添加到KeyConfig表中
            int i = 3;
            for (Iterator<String> iterator = getTextMap().keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                String tmp = "INSERT INTO 'KeyConfig'('ThemeID','AppID','Text') VALUES('%s','%s','%s')";
                sqls[i++] = String.format(tmp, StringUtil.getNotNullString(getThemeId()), 
                		StringUtil.getNotNullString(key), StringUtil.getNotNullString(getTextMap().get(key)));
            }

            // 4. 带事务批量执行
            boolean ret = db.execBatchSQL(sqls, true);
            if (!ret) {
                return ThemeGlobal.EXEC_FAIL;
            }
        } finally {
            db.close();
        }
    	return ThemeGlobal.EXEC_SUCCESS;
    }
    
}
