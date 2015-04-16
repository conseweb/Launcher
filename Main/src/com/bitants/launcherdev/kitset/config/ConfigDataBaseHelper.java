package com.bitants.launcherdev.kitset.config;


import android.database.sqlite.SQLiteDatabase;

import com.bitants.launcherdev.launcher.support.DBHelperFactory;
import com.bitants.launcherdev.push.PushMsgHandler;

/**
 * 桌面配置数据库
 */
public class ConfigDataBaseHelper implements DBHelperFactory.DataBaseHelper {

	@Override
	public int getDataBaseVersion() {
		return 2;
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		db.execSQL(PushMsgHandler.CREATE_PUSH_DB);
	}
	
	@Override
	public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}
