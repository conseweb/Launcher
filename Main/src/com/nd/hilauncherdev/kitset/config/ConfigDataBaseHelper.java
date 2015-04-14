package com.nd.hilauncherdev.kitset.config;


import android.database.sqlite.SQLiteDatabase;

import com.nd.hilauncherdev.launcher.support.DBHelperFactory;
import com.nd.hilauncherdev.push.PushMsgHandler;

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
