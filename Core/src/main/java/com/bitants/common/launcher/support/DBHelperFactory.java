package com.bitants.common.launcher.support;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库辅助类工厂,主要负责扩展数据库的创建和升级
 */
public class DBHelperFactory {
	public interface DataBaseHelper {

		/**
		 * 获取数据库版本
		 */
		public int getDataBaseVersion();
		
		/**
		 * 除构造其它需初始化的内容
		 * @param db
		 */
		public void onDataBaseCreate(SQLiteDatabase db);
		
		/**
		 * 处理ConfigDataBase升级
		 * @param db
		 * @param oldVersion
		 * @param newVersion
		 */
		public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}

	private static DBHelperFactory factory;
	
	private DataBaseHelper mConfigDataBaseHelper;
	private DataBaseHelper mAppDataBaseHelper;
	private DataBaseHelper mThemeDataBaseHelper;
	
	private DBHelperFactory(){};
	
	public static synchronized DBHelperFactory getInstance() {
		if (factory == null) {
			factory = new DBHelperFactory();
		}
		return factory;
	}

	public DataBaseHelper getConfigDataBaseHelper() {
		return mConfigDataBaseHelper;
	}

	public void setConfigDataBaseHelper(DataBaseHelper mConfigDataBaseHelper) {
		this.mConfigDataBaseHelper = mConfigDataBaseHelper;
	}

	public DataBaseHelper getAppDataBaseHelper() {
		return mAppDataBaseHelper;
	}

	public void setAppDataBaseHelper(DataBaseHelper mAppDataBaseHelper) {
		this.mAppDataBaseHelper = mAppDataBaseHelper;
	}

	public DataBaseHelper getThemeDataBaseHelper() {
		return mThemeDataBaseHelper;
	}

	public void setThemeDataBaseHelper(DataBaseHelper mThemeDataBaseHelper) {
		this.mThemeDataBaseHelper = mThemeDataBaseHelper;
	}
}
