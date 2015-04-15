package com.nd.launcherdev.launcher.config.db;

import java.util.HashMap;

import com.nd.launcherdev.core.model.AbstractDataBase;
import com.nd.launcherdev.launcher.screens.ScreenViewGroup;
import com.nd.launcherdev.launcher.support.DBHelperFactory;
import com.nd.launcherdev.launcher.support.DBHelperFactory.DataBaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * 桌面配置数据库
 */
public class ConfigDataBase extends AbstractDataBase {
	private static final String TAG = "BaseConfigDataBase";
	
	public static final String DB_NAME = "config.db";
	public static final String TABLE_NAME = "Config";
	
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Config ('ID' varchar(10) PRIMARY KEY NOT NULL, 'value' varchar(10))";
	private static final String INSERT_CONFIG_SQL = "insert into Config values('%s', '%s')";
	private static final String UPDATE_CONFIG_SQL = "update Config set value = '%s' where id = '%s'";

	/**
	 * 用于维护config表的缓存
	 */
	private static HashMap<String, String> ConfigCache = new HashMap<String, String>();
	
	public ConfigDataBase(Context c) {
		super(c, DB_NAME, getDataBaseHelper() != null ? getDataBaseHelper().getDataBaseVersion() : 1);
	}
	
	public static DataBaseHelper getDataBaseHelper(){
		return DBHelperFactory.getInstance().getConfigDataBaseHelper();
	}
	
	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		// config表
		db.execSQL(CREATE_TABLE);
		db.execSQL("insert into Config values('screenCount', "+ ScreenViewGroup.DEFAULT_SCREEN_COUNT +")");
		
		// 默认打开程序
		db.execSQL(DefaultLauncherTable.CREATE_TABLE);
		
		//其它初始化内容
		if(getDataBaseHelper() != null){			
			getDataBaseHelper().onDataBaseCreate(db);
		}
		
		ConfigCache.clear();
		Cursor dbCursor = null;
		try{
			//将config表中的记录放入缓存
			dbCursor = db.query(TABLE_NAME, null, null, null, null, null, null);
			while(dbCursor != null && dbCursor.moveToNext()){
				ConfigCache.put(dbCursor.getString(0), dbCursor.getString(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbCursor != null){
				dbCursor.close();
			}
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
	
	
	/**
	 * 判断Config是否不存在
	 * @param id Config的id
	 * @return true表示不存在
	 */
	public boolean isNotExist(String id){
		if(ConfigCache.containsKey(id)){ //先判断缓存
			return false;
		}else{ //再到数据库查找
			Cursor cursor = null;
			try{
				//将config表中的记录放入缓存
				cursor = query(String.format("select * from Config where id = '%s'", id));
				while(cursor != null && cursor.moveToNext()){
					ConfigCache.put(cursor.getString(0), cursor.getString(1));
				}
				return !ConfigCache.containsKey(id);
			
			}catch(Exception e){
				e.printStackTrace();
				return true;
			}finally{
				if(cursor != null){
					cursor.close();
				}
				close();
			}
		}
	}
	
	
	/**
	 * 获取config数据有可能是null
	 * @param id Config的id
	 * @return Config的值
	 */
	public String getConfigData(String id){
		if(isNotExist(id)){
			return null;
		}
		return ConfigCache.get(id);
	}
	
	/**
	 * 增加Config值
	 * @param id Config的id
	 * @param value Config的值
	 * @return true表示添加成功
	 */
	public boolean addConfigData(String id, String value){
		//进行添加操作
		try{
			execSQL(String.format(INSERT_CONFIG_SQL, id, value));
			ConfigCache.put(id, value);
			return true;
		}catch(Exception e){
			Log.e(TAG, "addConfigData error");
			return false;
		}finally{
			close();
		}
		
	}
	
	/**
	 * 更新Config值 
	 * @param id Config的id
	 * @param value Config的值
	 * @return true表示更新成功
	 */
	public boolean updateConfigData(String id, String value){
		//进行更新操作
		try{
			execSQL(String.format(UPDATE_CONFIG_SQL, value, id));
			ConfigCache.put(id, value);
			return true;
		}catch(Exception e){
			Log.e(TAG, "addConfigData error");
			return false;
		}finally{
			close();
		}
	}
}
