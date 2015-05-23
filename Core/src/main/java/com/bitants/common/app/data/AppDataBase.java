package com.bitants.common.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bitants.common.core.model.AbstractDataBase;
import com.bitants.common.launcher.support.DBHelperFactory;
import com.bitants.common.launcher.support.DBHelperFactory.DataBaseHelper;

public class AppDataBase extends AbstractDataBase  {

	private static final String DB_NAME = "app.db";
	
	public AppDataBase(Context c) {
		super(c, DB_NAME, getDataBaseHelper() != null ? getDataBaseHelper().getDataBaseVersion() : 1);
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS 'THEME_APP_ADAPTATION' ('app_key' varchar(256) " + "PRIMARY KEY  NOT NULL ,'app_package_name' varchar(128), " + "'app_class_name' varchar(128))");
		
		//其它初始化内容
		if(getDataBaseHelper() != null){			
			getDataBaseHelper().onDataBaseCreate(db);
		}
	}

	@Override
	public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(getDataBaseHelper() != null){				
			getDataBaseHelper().onDataBaseUpgrade(db, oldVersion, newVersion);
		}
	}

	@Override
	public void onDataBaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public static DataBaseHelper getDataBaseHelper(){
		return DBHelperFactory.getInstance().getAppDataBaseHelper();
	}
}
