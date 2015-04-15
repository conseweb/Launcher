package com.nd.launcherdev.datamodel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nd.launcherdev.core.model.AbstractDataBase;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadLogTable;

/**
 * 数据库工具类
 * 
 * @author youy
 * 
 */
public class MyPhoneDB extends AbstractDataBase {

	/**
	 * db版本号在3.1版本升至2，之前版本号位1 by pdw 在3.2.1升至3，增加小部件列表表 by lx
	 * db版本号在3.5.2版本升至4，之前版本号位3 by pdw 2013-01-11 db版本号在3.62版本升至6 by youy 文件扫描记录
	 * db版本号在5.1版本升至7，修改通用的下载管理模块，by hjiang
	 */
	private static final int VERSION = 7;
	private static final String DB_NAME = "myphone.db";

	public static String CREATE_LOCK_TABLE = "CREATE TABLE IF NOT EXISTS 'AppLockTable' ('pkg' varchar(150) NOT NULL,'lock'INTEGER default 0)";
	public static final String CREATE_CONFIG_TABLE = "CREATE TABLE IF NOT EXISTS Config ('ID' varchar(10) PRIMARY KEY NOT NULL, 'value' varchar(10))";

	public MyPhoneDB(Context c) {
		super(c, DB_NAME, VERSION);
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_LOCK_TABLE);
		// 下载记录表
		db.execSQL(DownloadLogTable.CREATE_TABLE);
		db.execSQL(CREATE_CONFIG_TABLE);
		//572采用通用下载sdk,新字段为兼容历史版本,572后续版本不得采用该字段，
		//若需要为某下载记录定制个性化值时可考虑用addition_info,通用sdk中已有很好的支持。
		//wanggm 2015.02.03
        db.execSQL("alter table log_download add column 'extra' VARCHAR(32)");
	}

	@Override
	public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	@Override
	public void onDataBaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}