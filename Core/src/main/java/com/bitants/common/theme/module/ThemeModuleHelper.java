package com.bitants.common.theme.module;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.text.TextUtils;

import com.bitants.common.kitset.util.FileUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.theme.pref.ThemeSharePref;
import com.bitants.common.theme.db.LauncherThemeDataBase;

/**
 * <br>Description: 主题模块包管理
 */
public class ThemeModuleHelper {
	
	private static final String THEME_ADD_COLUMN_SUPPORT_V6 = "ALTER TABLE Theme ADD COLUMN support_v6 INTEGER DEFAULT 0";
	private static final String THEME_ADD_COLUMN_GUARDED = "ALTER TABLE Theme ADD COLUMN guarded INTEGER DEFAULT 0";
	private static final String THEME_ADD_COLUMN_GUARDED_VERSION = "ALTER TABLE Theme ADD COLUMN guarded_version INTEGER DEFAULT 0";
	private static final String THEME_ADD_COLUMN_RES_TYPE = "ALTER TABLE Theme ADD COLUMN res_type INTEGER DEFAULT 0";
	/**
	 * 主题包支持的桌面最低版本
	 */
	private static final String THEME_ADD_COLUMN_LAUNCHER_MIN_VERSION = "ALTER TABLE Theme ADD COLUMN launcher_min_version INTEGER DEFAULT 5998";
	/**
	 * 模块包支持的桌面最低版本
	 */
	private static final String MODULE_ADD_COLUMN_LAUNCHER_MIN_VERSION = "ALTER TABLE Module ADD COLUMN launcher_min_version INTEGER DEFAULT 5998";
	/**
	 * 模块信息表增加模块分类字段
	 */
	private static final String MODULE_ADD_COLUMN_CATEGORY = "ALTER TABLE Module ADD COLUMN module_category varchar(128)";
	/**
	 * 当前主题表查询SQL(所有记录)
	 */
	public static final String CURRENT_THEME_QUERY_SQL = "SELECT * FROM CurrentTheme";
	
	/**
	 * 当前主题表查询SQL(根据模块ID,模块类型查询)
	 */
	public static final String CURRENT_THEME_QUERY_ID_TYPE_SQL = "SELECT * FROM CurrentTheme WHERE module_theme_id='%s' AND module_type=%s";
	
	/**
	 * 当前主题表查询SQL(根据模块KEY查询)
	 */
	public static final String CURRENT_THEME_QUERY_KEY_SQL = "SELECT * FROM CurrentTheme WHERE module_key='%s'";
	
	/**
	 * 当前主题表查询SQL(根据第三方应用包名查询)
	 */
	public static final String CURRENT_THEME_QUERY_PKG_SQL = "SELECT * FROM CurrentTheme WHERE module_pkg_name LIKE '%s'";
	
	/**
	 * 当前主题表插入SQL
	 */
	public static final String CURRENT_THEME_INSERT_SQL = "INSERT INTO CurrentTheme ('module_key', 'module_theme_id', 'module_pkg_name', 'module_type') values ('%s', '%s' , '%s' , %s)";

	/**
	 * 当前主题表更新所有记录SQL
	 */
	public static final String CURRENT_THEME_UPDATE_ALL_SQL = "UPDATE CurrentTheme SET module_theme_id='%s', module_type=0";
	
	/**
	 * 当前主题表更新单个记录SQL
	 */
	public static final String CURRENT_THEME_UPDATE_SINGLE_SQL = "UPDATE CurrentTheme SET module_theme_id='%s', module_type=%s WHERE module_key='%s'";
	
	/**
	 * 模块信息表查询SQL
	 */
	public static final String MODULE_QUERY_CATEGORY_SQL = "SELECT * FROM Module WHERE module_category='%s'";
	
	/**
	 * 模块信息表查询SQL(所有)
	 */
	public static final String MODULE_ALL_QUERY_SQL = "SELECT * FROM Module";
	
	/**
	 * 模块信息表查询SQL(根据模块ID)
	 */
	public static final String MODULE_QUERY_ID_SQL = "SELECT * FROM Module WHERE module_id='%s'";
	
	/**
	 * 模块信息表删除SQL
	 */
	public static final String MODULE_DELETE_SQL = "DELETE FROM Module WHERE module_id='%s' and module_key='%s'";
	
	/**
	 * 模块信息表更新分类字段SQL
	 */
	public static final String MODULE_UPDATE_CATEGORY_SQL = "UPDATE Module SET module_category='%s' WHERE module_key='%s'";
	
	/**
	 * 模块信息表插入SQL
	 */
	public static final String MODULE_INSERT_SQL = "INSERT INTO Module ('module_id', 'module_key', 'version_name', 'version_code', 'install_time', 'name', 'en_name', 'guarded', 'guarded_version', 'res_type', 'launcher_min_version', 'module_category') values ('%s', '%s', '%s', %s, %s, '%s', '%s', %s, %s, %s, %s, '%s')";
	
	/**
	 * 模块信息是否存在查询SQL(根据模块key、模块ID或模块文件夹名称)
	 */
	public static final String MODULE_IS_EXIST_SQL = "SELECT * FROM Module WHERE module_key='%s' AND (replace(module_id,' ','_') = '%s' OR module_id = '%s')";
	
	private static ThemeModuleHelper helper;
	
	public static ThemeModuleHelper getInstance() {
		if(null == helper) {
			helper = new ThemeModuleHelper();
		}
		return helper;
	}
	
	/**
	 * <br>Description: V5.7.2版本新主题标识，用于判断提示升级桌面
	 */
	public void upgrade2V5720ThemeDataBase() {
		LauncherThemeDataBase db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
		db.beginTransaction();
		try {
			db.execSQL(THEME_ADD_COLUMN_SUPPORT_V6);
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			db.endTransactionByException();
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: V6.0版本(内测包5998)新主题结构支持
	 */
	public void upgrade2V5998ThemeDataBase() {
		LauncherThemeDataBase db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
		db.beginTransaction();
		try {
			db.execSQL(THEME_ADD_COLUMN_GUARDED);
			db.execSQL(THEME_ADD_COLUMN_GUARDED_VERSION);
			db.execSQL(THEME_ADD_COLUMN_RES_TYPE);
			db.execSQL(LauncherThemeDataBase.CURRENT_THEME_CREATE_SQL);
			db.execSQL(LauncherThemeDataBase.MODULE_CREATE_SQL);
			String[] insertSqls = getCurrentThemeInitSql();
			if(null != insertSqls) {
				for(int i=0; i<insertSqls.length; i++) {
					db.execSQL(insertSqls[i]);
				}
			}
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			db.endTransactionByException();
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: V6.0版本(内测包5999)增加主题包支持的桌面最低版本标识
	 */
	public void upgrade2V5999ThemeDataBase() {
		LauncherThemeDataBase db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
		db.beginTransaction();
		try {
			db.execSQL(THEME_ADD_COLUMN_LAUNCHER_MIN_VERSION);
			db.execSQL(MODULE_ADD_COLUMN_LAUNCHER_MIN_VERSION);
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			db.endTransactionByException();
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: V6.3版本增加讯飞输入法及触宝拨号
	 */
	public void upgrade2V6298ThemeDataBase() {
		LauncherThemeDataBase db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
		db.beginTransaction();
		try {
			// 当前主题表增加讯飞输入法、触宝拨号
			String currentThemeId = ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).getCurrentThemeId();
			String sql = String.format(CURRENT_THEME_INSERT_SQL, ModuleConstant.MODULE_IFLYTEK_INPUT, currentThemeId,
					ModuleConstant.MODULE_IFLYTEK_INPUT_PKG, ThemeModuleItem.TYPE_THEME);
			db.execSQL(sql);
			sql = String.format(CURRENT_THEME_INSERT_SQL, ModuleConstant.MODULE_COOTEK_DIALER, currentThemeId,
					ModuleConstant.MODULE_COOTEK_DIALER_PKG, ThemeModuleItem.TYPE_THEME);
			db.execSQL(sql);
			// 模块信息表增加模块分类字段
			db.execSQL(MODULE_ADD_COLUMN_CATEGORY);
			// 更新模块信息表模块分类字段
			for(int i=0; i<ModuleConstant.MODULE_KEY_ARRAY.length; i++) {
				sql = String.format(MODULE_UPDATE_CATEGORY_SQL, ModuleConstant.MODULE_KEY_ARRAY[i][2], 
						ModuleConstant.MODULE_KEY_ARRAY[i][0]);
				db.execSQL(sql);
			}
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			db.endTransactionByException();
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: 获取当前主题表初始化SQL
	 * @return
	 */
	public String[] getCurrentThemeInitSql() {
		if(ModuleConstant.MODULE_KEY_ARRAY.length > 0) {
			String[] sqls = new String[ModuleConstant.MODULE_KEY_ARRAY.length];
			String currentThemeId = ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).getCurrentThemeId();
			for(int i=0; i<ModuleConstant.MODULE_KEY_ARRAY.length; i++) {
				sqls[i] = String.format(CURRENT_THEME_INSERT_SQL, ModuleConstant.MODULE_KEY_ARRAY[i][0], currentThemeId,
						ModuleConstant.MODULE_KEY_ARRAY[i][1], ThemeModuleItem.TYPE_THEME);
			}
			return sqls;
		}
		return null;
	}
	
	/**
	 * <br>Description: 获取当前主题模块信息
	 * @return
	 */
	public List<ThemeModuleItem> getCurrentThemeModule() {
		LauncherThemeDataBase db = null;
		Cursor cursor = null;
		List<ThemeModuleItem> items = new ArrayList<ThemeModuleItem>();
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			cursor = db.query(CURRENT_THEME_QUERY_SQL);
			if(null != cursor) {
				while(cursor.moveToNext()) {
					ThemeModuleItem item = new ThemeModuleItem();
					item.setKey(StringUtil.getString(cursor, "module_key"));
					item.setId(StringUtil.getString(cursor, "module_theme_id"));
					item.setPgk(StringUtil.getString(cursor, "module_pkg_name"));
					item.setType(cursor.getInt(cursor.getColumnIndex("module_type")));
					items.add(item);
				}
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
		return items;
	}
	
	/**
	 * <br>Description: 查询模块包或主题模块被当前主题使用
	 * @param modulePkg
	 * @param moduleType
	 * @return
	 */
	public boolean isModuleUsedInCurTheme(String moduleId, int moduleType) {
		LauncherThemeDataBase db = null;
		Cursor cursor = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			cursor = db.query(String.format(CURRENT_THEME_QUERY_ID_TYPE_SQL, moduleId, moduleType));
			if(null != cursor) {
				if(cursor.moveToNext()) {
					return true;
				}
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
		return false;
	}
	
	/**
	 * <br>Description: 根据模块KEY查询当前主题对应模块信息
	 * @param modulePkg
	 * @return null-无对应模块ID
	 */
	public ThemeModuleItem getCurrentThemeModuleByKey(String moduleKey) {
		LauncherThemeDataBase db = null;
		Cursor cursor = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			cursor = db.query(String.format(CURRENT_THEME_QUERY_KEY_SQL, moduleKey));
			if(null != cursor) {
				if(cursor.moveToNext()) {
					ThemeModuleItem item = new ThemeModuleItem();
					item.setKey(StringUtil.getString(cursor, "module_key"));
					item.setId(StringUtil.getString(cursor, "module_theme_id"));
					item.setPgk(StringUtil.getString(cursor, "module_pkg_name"));
					item.setType(cursor.getInt(cursor.getColumnIndex("module_type")));
					return item;
				}
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
		return null;
	}
	
	/**
	 * <br>Description: 根据包名查询当前主题对应模块信息
	 * @param modulePkg
	 * @return null-无对应模块ID
	 */
	public ThemeModuleItem getCurrentThemeModuleByPkg(String modulePkg) {
		LauncherThemeDataBase db = null;
		Cursor cursor = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			cursor = db.query(String.format(CURRENT_THEME_QUERY_PKG_SQL, "%" + modulePkg + "%"));
			if(null != cursor) {
				if(cursor.moveToNext()) {
					ThemeModuleItem item = new ThemeModuleItem();
					item.setKey(StringUtil.getString(cursor, "module_key"));
					item.setId(StringUtil.getString(cursor, "module_theme_id"));
					item.setPgk(StringUtil.getString(cursor, "module_pkg_name"));
					item.setType(cursor.getInt(cursor.getColumnIndex("module_type")));
					return item;
				}
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
		return null;
	}
	
	/**
	 * <br>Description: 更新当前主题模块信息(应用部分主题模块或单个模块包时调用该方法)
	 * @param modules
	 * @return
	 */
	public boolean updateCurrentThemeModule(List<ThemeModuleItem> modules) {
		LauncherThemeDataBase db = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			db.beginTransaction();
			for(int i=0; i<modules.size(); i++) {
				ThemeModuleItem module = modules.get(i);
				if(null != module) {
					db.execSQL(String.format(CURRENT_THEME_UPDATE_SINGLE_SQL, module.getId(), module.getType(), module.getKey()));
				}
			}
			db.endTransaction();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			db.endTransactionByException();
			return false;
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: 更新当前主题模块信息(应用主题整包时调用该方法)
	 * @param themeId
	 */
	public void updateCurrentThemeModule(String themeId) {
		LauncherThemeDataBase db = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			db.execSQL(String.format(CURRENT_THEME_UPDATE_ALL_SQL, themeId));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: 保存模块包信息
	 * @param moduleInfo
	 * @return
	 */
	public boolean saveModuleInfo(ModuleInfo moduleInfo) {
		if(null == moduleInfo)return false;
		LauncherThemeDataBase db = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			boolean result = db.execSQL(String.format(MODULE_INSERT_SQL,
					moduleInfo.getModuleId(), moduleInfo.getModuleKey(),
					moduleInfo.getVersionName(), moduleInfo.getVersionCode(),
					moduleInfo.getInstallTime(), moduleInfo.getName(),
					moduleInfo.getEnName(), moduleInfo.isGuarded() ? 1 : 0,
					moduleInfo.getGuardedVersion(), moduleInfo.getResType(), 
					moduleInfo.getLauncherMinVersion(), moduleInfo.getModuleCategory()));
			if(!result) {
				// 如果模块包已存在，返回true
				if(isModuleInfoExist(db, moduleInfo.getModuleKey(), moduleInfo.getModuleId())) {
					return true;
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: 更新模块包信息
	 * @param moduleInfo
	 * @return
	 */
	public boolean updateModuleInfo(ModuleInfo moduleInfo) {
		if(TextUtils.isEmpty(moduleInfo.getModuleId()) || TextUtils.isEmpty(moduleInfo.getModuleKey()))return false;
		LauncherThemeDataBase db = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			String[] sqls = new String[2];
			sqls[0] = String.format(MODULE_DELETE_SQL, moduleInfo.getModuleId(), moduleInfo.getModuleKey());
			sqls[1] = String.format(MODULE_INSERT_SQL,
					moduleInfo.getModuleId(), moduleInfo.getModuleKey(),
					moduleInfo.getVersionName(), moduleInfo.getVersionCode(),
					moduleInfo.getInstallTime(), moduleInfo.getName(),
					moduleInfo.getEnName(), moduleInfo.isGuarded() ? 1 : 0,
					moduleInfo.getGuardedVersion(), moduleInfo.getResType(), 
					moduleInfo.getLauncherMinVersion(), moduleInfo.getModuleCategory());
			return db.execBatchSQL(sqls, true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description:获取模块信息列表
	 * @param moduleCategory 模块分类
	 * @return
	 */
	public List<ModuleInfo> getModuleInfoByCategory(String moduleCategory) {
		if(TextUtils.isEmpty(moduleCategory))return null;
		LauncherThemeDataBase db = null;
		Cursor cursor = null;
		List<ModuleInfo> infos = new ArrayList<ModuleInfo>();
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			cursor = db.query(String.format(MODULE_QUERY_CATEGORY_SQL, moduleCategory));
			if(null != cursor) {
				while(cursor.moveToNext()) {
					ModuleInfo info = new ModuleInfo();
					info.setModuleId(StringUtil.getString(cursor, "module_id"));
					info.setModuleKey(StringUtil.getString(cursor, "module_key"));
					info.setInstallTime(cursor.getLong(cursor.getColumnIndex("install_time")));
					info.setVersionName(StringUtil.getString(cursor, "version_name"));
					info.setVersionCode(cursor.getInt(cursor.getColumnIndex("version_code")));
					info.setName(StringUtil.getString(cursor, "name"));
					info.setEnName(StringUtil.getString(cursor, "en_name"));
					info.setGuarded(cursor.getInt(cursor.getColumnIndex("guarded"))==1);
					info.setGuardedVersion(cursor.getInt(cursor.getColumnIndex("guarded_version")));
					info.setResType(cursor.getInt(cursor.getColumnIndex("res_type")));
					info.setLauncherMinVersion(cursor.getInt(cursor.getColumnIndex("launcher_min_version")));
					info.setModuleCategory(StringUtil.getString(cursor, "module_category"));
					infos.add(info);
				}
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
		return infos;
	}
	
	/**
	 * <br>Description: 获取模块数量
	 * @return
	 */
	public int getModuleInfosCount(){
        //读取Theme表
        LauncherThemeDataBase db = null;
        Cursor cursor = null;
        try{
	        db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
	        cursor = db.query(MODULE_ALL_QUERY_SQL);
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
	 * <br>Description:获取模块信息列表
	 * @param moduleKey 模块key
	 * @return
	 */
	public List<ModuleInfo> getAllModuleInfo() {
		LauncherThemeDataBase db = null;
		Cursor cursor = null;
		List<ModuleInfo> infos = new ArrayList<ModuleInfo>();
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			cursor = db.query(MODULE_ALL_QUERY_SQL);
			if(null != cursor) {
				while(cursor.moveToNext()) {
					ModuleInfo info = new ModuleInfo();
					info.setModuleId(StringUtil.getString(cursor, "module_id"));
					info.setModuleKey(StringUtil.getString(cursor, "module_key"));
					info.setInstallTime(cursor.getLong(cursor.getColumnIndex("install_time")));
					info.setVersionName(StringUtil.getString(cursor, "version_name"));
					info.setVersionCode(cursor.getInt(cursor.getColumnIndex("version_code")));
					info.setName(StringUtil.getString(cursor, "name"));
					info.setEnName(StringUtil.getString(cursor, "en_name"));
					info.setGuarded(cursor.getInt(cursor.getColumnIndex("guarded"))==1);
					info.setGuardedVersion(cursor.getInt(cursor.getColumnIndex("guarded_version")));
					info.setResType(cursor.getInt(cursor.getColumnIndex("res_type")));
					info.setLauncherMinVersion(cursor.getInt(cursor.getColumnIndex("launcher_min_version")));
					info.setModuleCategory(StringUtil.getString(cursor, "module_category"));
					infos.add(info);
				}
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
		return infos;
	}
	
	/**
	 * <br>Description:获取模块信息
	 * @param moduleId 模块ID
	 * @return
	 */
	public ModuleInfo getModuleInfoById(String moduleId) {
		if(null == moduleId)return null;
		LauncherThemeDataBase db = null;
		Cursor cursor = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			cursor = db.query(String.format(MODULE_QUERY_ID_SQL, moduleId));
			if(null != cursor) {
				while(cursor.moveToNext()) {
					ModuleInfo info = new ModuleInfo();
					info.setModuleId(StringUtil.getString(cursor, "module_id"));
					info.setModuleKey(StringUtil.getString(cursor, "module_key"));
					info.setInstallTime(cursor.getLong(cursor.getColumnIndex("install_time")));
					info.setVersionName(StringUtil.getString(cursor, "version_name"));
					info.setVersionCode(cursor.getInt(cursor.getColumnIndex("version_code")));
					info.setName(StringUtil.getString(cursor, "name"));
					info.setEnName(StringUtil.getString(cursor, "en_name"));
					info.setGuarded(cursor.getInt(cursor.getColumnIndex("guarded"))==1);
					info.setGuardedVersion(cursor.getInt(cursor.getColumnIndex("guarded_version")));
					info.setResType(cursor.getInt(cursor.getColumnIndex("res_type")));
					info.setLauncherMinVersion(cursor.getInt(cursor.getColumnIndex("launcher_min_version")));
					info.setModuleCategory(StringUtil.getString(cursor, "module_category"));
					return info;
				}
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
		return null;
	}
	
	/**
	 * <br>Description: 模块包是否已存在
	 * @param db.
	 * @param moduleKey
	 * @param moduleId
	 * @return
	 */
	public boolean isModuleInfoExist(LauncherThemeDataBase db, String moduleKey, String moduleId) {
		if(null == moduleId)return false;
		Cursor cursor = null;
		boolean shouldCloseDb = false;
		try {
			if(null == db) {
				db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
				shouldCloseDb = true;
			}
			cursor = db.query(String.format(MODULE_IS_EXIST_SQL, moduleKey, moduleId, moduleId));
			if(null != cursor && cursor.moveToNext()) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(null != cursor) {
				cursor.close();
				cursor = null;
			}
			if(shouldCloseDb && null != db) {
				db.close();
				db = null;
			}
		}
		return false;
	}
	
	/**
	 * <br>Description: 删除模块包数据库信息
	 * @param moduleInfo
	 * @return
	 */
	public boolean removeModuleInfo(String moduleId, String moduleKey) {
		if(TextUtils.isEmpty(moduleId) || TextUtils.isEmpty(moduleKey))return false;
		LauncherThemeDataBase db = null;
		try {
			db = new LauncherThemeDataBase(BaseConfig.getApplicationContext());
			return db.execSQL(String.format(MODULE_DELETE_SQL, moduleId, moduleKey));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(null != db) {
				db.close();
				db = null;
			}
		}
	}
	
	/**
	 * <br>Description: 删除模块包文件
	 * @param moduleInfo
	 * @return
	 */
	public void removeModuleAllFile(String moduleId, String moduleKey) {
		if(TextUtils.isEmpty(moduleId) || TextUtils.isEmpty(moduleKey))return;
		FileUtil.delFolder(BaseConfig.MODULE_DIR + moduleKey.replace("@", "/") + "/" + moduleId.replace(" ", "_"));
	}
	
}
