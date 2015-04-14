package com.nd.hilauncherdev.theme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nd.hilauncherdev.core.model.AbstractDataBase;
import com.nd.hilauncherdev.launcher.support.DBHelperFactory;
import com.nd.hilauncherdev.launcher.support.DBHelperFactory.DataBaseHelper;
import com.nd.hilauncherdev.theme.module.ThemeModuleHelper;

/**
 * <br>Description: 本地主题数据库
 * <br>Author:caizp
 * <br>Date:2012-7-11上午09:28:34
 */
public class LauncherThemeDataBase extends AbstractDataBase {
	
	//old version = 1
	private static final String DB_NAME = "themes.db";
	
	/**
	 * 主题基本信息表创建SQL
	 */
	private static final String THEME_CREATE_SQL = "CREATE TABLE IF NOT EXISTS 'Theme' "
			+ "('ID' varchar(32) PRIMARY KEY  NOT NULL, 'NAME' varchar(16) NOT NULL,"
			+ " 'EN_NAME' varchar(16) NOT NULL, 'DESC' varchar(128), 'EN_DESC' varchar(128), "
			+ "'Version' varchar(16), 'type' INTEGER default -1, 'pandaflag' INTEGER default -1, " 
			+ "'versioncode' INTEGER default -1, 'base_density' FLOAT default 1.5, " 
			+ "ID_FLAG varchar(16) NOT NULL, 'PATH' varchar(128), 'install_time' INTEGER default 0, " 
			+ "'use_time' INTEGER default 0, 'use_count' INTEGER default 0, 'support_v6' INTEGER default 0,"
			+ "'guarded' INTEGER default 0, 'guarded_version' INTEGER default 1, 'res_type' INTEGER default 0,"
			+ "'launcher_min_version' INTEGER default 5998)";

	/**
	 * 主题配置信息表创建SQL
	 */
	public static final String KEY_CONFIG_CREATE_SQL = "CREATE TABLE IF NOT EXISTS 'KeyConfig' " 
			+ "('ThemeID' varchar(32), 'AppID' varchar(128), 'Text' varchar(32) )";
	
	/**
	 * 当前主题信息表创建SQL
	 */
	public static final String CURRENT_THEME_CREATE_SQL = "CREATE TABLE IF NOT EXISTS 'CurrentTheme' "
			+ "('module_key' varchar(128) PRIMARY KEY  NOT NULL, 'module_theme_id' varchar(128), "
			+ "'module_pkg_name' varchar(128), 'module_type' INTEGER default 0)";
	
	/**
	 * 模块信息表创建SQL
	 */
	public static final String MODULE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS 'Module' " 
			+ "('module_id' varchar(128) PRIMARY KEY  NOT NULL, 'module_key' varchar(128), " 
			+ "'version_name' varchar(16), version_code INTEGER default 1, 'install_time' INTEGER default 0, " 
			+ "'name' varchar(16) NOT NULL, 'en_name' varchar(16) NOT NULL, 'guarded' INTEGER default 0, " 
			+ "'guarded_version' INTEGER default 1, 'res_type' INTEGER default 0, "
			+ "'launcher_min_version' INTEGER default 5998, 'module_category' varchar(128))";
	
	public LauncherThemeDataBase(Context c) {
		super(c, DB_NAME, getDataBaseHelper() != null ? getDataBaseHelper().getDataBaseVersion() : 1);
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		db.execSQL(THEME_CREATE_SQL);
		db.execSQL(KEY_CONFIG_CREATE_SQL);
		db.execSQL(CURRENT_THEME_CREATE_SQL);
		db.execSQL(MODULE_CREATE_SQL);
		String[] insertSqls = ThemeModuleHelper.getInstance().getCurrentThemeInitSql();
		if(null != insertSqls) {
			for(int i=0; i<insertSqls.length; i++) {
				db.execSQL(insertSqls[i]);
			}
		}
		if(getDataBaseHelper() != null){			
			getDataBaseHelper().onDataBaseCreate(db);
		}
	}

	@Override
	public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		if(getDataBaseHelper() != null){
			getDataBaseHelper().onDataBaseUpgrade(db, oldVersion, newVersion);
		}
	}

	@Override
	public void onDataBaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public static DataBaseHelper getDataBaseHelper(){
		return DBHelperFactory.getInstance().getThemeDataBaseHelper();
	}

}
