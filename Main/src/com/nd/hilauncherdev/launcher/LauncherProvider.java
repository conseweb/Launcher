package com.nd.hilauncherdev.launcher;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.model.BaseLauncherProvider;

public class LauncherProvider extends BaseLauncherProvider {
	/**
	 * 数据库版本 -- 桌面版本
	 */
	public static int DATABASE_VERSION = 1;

	/**
	 * favorites建表sql
	 */
	public static String CREATE_TABLE_FAVORITES = String.format(CREATE_TABLE_FAVORITES_MODEL, "favorites", "");
	
	@Override
	public boolean onCreate() {
		super.onCreate();
		switchToScene();
		return true;
	}
	
	private void switchToScene(){
		Context mContext = getContext();
		if(BaseConfig.getApplicationContext()==null){
			BaseConfig.setApplicationContext(mContext);
			//HiAnalytics.init(mContext);
		}
		
		//初始化数据库辅助类
		((LauncherApplication)BaseConfig.getApplicationContext()).initDBHelper();
		
	}

	@Override
	public SQLiteHelper getSQLiteHelperInstance(){
		return new DatabaseHelper(getContext());
	}
	
	public static class DatabaseHelper extends SQLiteHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			setCreateTableSql(CREATE_TABLE_FAVORITES);
//			setLauncherUpgrader(new LauncherProviderUpgrader(context, this));
		}

		@Override
		public void loadDefaultData(SQLiteDatabase db) {
			//处理循环崩溃数据恢复
//			int converted = LauncherProviderHelper.exceptionResetDataFromSdcard(db);
//			if (converted <= 0) {// Populate favorites table with initial favorites
				loadFavorites(db);
//			}
		}
		
		public void loadFavorites(SQLiteDatabase db){
			LauncherProviderHelper.loadFavorites(db, getAppWidgetHost(), BaseConfig.getApplicationContext());
		}
	}
}
