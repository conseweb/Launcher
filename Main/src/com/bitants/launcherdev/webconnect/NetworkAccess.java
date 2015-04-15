package com.bitants.launcherdev.webconnect;

import java.util.HashMap;

import com.bitants.launcherdev.framework.httplib.HttpCommon;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.framework.httplib.HttpCommon;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bitants.launcherdev.framework.httplib.HttpCommon;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;

/**
 * 网络访问入口
 */
public class NetworkAccess {
	
	private static final String CONFIG_ID_ConnectStatusIn23G = "network_ConnectStatusIn23G";
	private static final String CONFIG_ID_PostFunStatus= "network_PostFunStatus";
	private static final String CONFIG_ID_NextUpdateTime = "network_NextUpdateTime";
	private static final String CONFIG_VALUE_ON = "0";
	private static final String CONFIG_VALUE_OFF = "1";
	private static final String CONFIG_DB_NAME = "config.db";
	private static final String QUERY_SINGLE_SQL = "select * from Config where ID = '%s'";
	private static final String QUERY_ALL_SQL = "select * from Config";
	private static final String INSERT_SQL = "insert into Config values('%s', '%s')";
	private static final String UPDATE_SQL = "update Config set value = '%s' where id = '%s'";
	private static final String GET_CONFIG_URL = "http://pandahome.sj.91.com/android/getdata.aspx?action=1001";
	
	private static NetworkAccess sInstance;
			
	public static synchronized NetworkAccess getInstance() {
		if (sInstance == null) {
			sInstance = new NetworkAccess();
		}
		return sInstance;
	}
	
	private NetworkAccess() {
	}
		
	/**
	 * 执行网络访问（不启动新线程）
	 * @param runnable 执行的代码块
	 * @return 执行结果
	 */
	public Result execute(Runnable runnable) {
		Result result = new Result();
		if (!isNetorkAccessPermitted()) {
			result.code = Result.Code.NOT_PERMITTED;
		} else {
			if (runnable != null) {
				runnable.run();
			}
			result.code = Result.Code.SUCCEED;
		}
		return result;
	}
				
	/**
	 * 网络访问结果
	 */
	public static class Result {
		/**
		 * 结果代码
		 */
		public static enum Code {
			/**
			 * 不允许进行网络访问
			 */
			NOT_PERMITTED,
			/**
			 * 执行成功
			 */
			SUCCEED,
		};
		
		public Code code = Code.SUCCEED;
	}
		
	/**
	 * 检查是否需要更新网络配置
	 */
	public void checkConfig() {
		try {
			long nextUpdateTime = Long.parseLong(getLocalConfig(CONFIG_ID_NextUpdateTime, "0"));
			if (System.currentTimeMillis() >= nextUpdateTime) {
				ThreadUtil.executeMore(new Runnable() {
                    @Override
                    public void run() {
                        String content = getBeckendConfig();
                        parseBeckendConfig(content);
                    }
                });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getBeckendConfig() {		
		HttpCommon http = new HttpCommon(GET_CONFIG_URL);
		return http.getResponseAsStringPost(new HashMap<String, String>());
	}
	
	private void parseBeckendConfig(String content) {
		if (content == null) {
			return;
		}
		
		try {
			JSONObject json = new JSONObject(content);
			int code = json.getInt("Code");
			JSONObject result = json.getJSONObject("Result");
			if (code == 0 && result != null) {
				JSONObject config = result.getJSONObject("config");
				updateLoacalConfig(config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isNetorkAccessPermitted() {
		String result = getLocalConfig(CONFIG_ID_ConnectStatusIn23G, CONFIG_VALUE_ON);
		boolean isPermittedIn23G = (result != null && result.equals(CONFIG_VALUE_ON)) ? true : false;
		return (!isPermittedIn23G && !TelephoneUtil.isWifiEnable(BaseConfig.getApplicationContext()) ) ? false : true;
	}
	
	private static void updateLoacalConfig(JSONObject config) {
		if (config == null) 
			return;
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			String connectStatusIn23G = config.getString("ConnectStatusIn23G");
			String postFunStatus = config.getString("PostFunStatus");
			long interval = config.getLong("RequestSpaceOfTime");
           	HashMap<String, String> configCache = new HashMap<String, String>();
			
			db = SQLiteDatabase.openDatabase(BaseConfig.getApplicationContext().getDatabasePath(CONFIG_DB_NAME).getAbsolutePath(), 
            							     null, 
                                             SQLiteDatabase.OPEN_READWRITE);
            cursor = db.rawQuery(QUERY_ALL_SQL, null);
            if (cursor != null) {
            	while (cursor.moveToNext()) {
    				configCache.put(cursor.getString(0), cursor.getString(1));
            	}
            }
           	updateSingleConfig(db, configCache, CONFIG_ID_ConnectStatusIn23G, connectStatusIn23G);
           	updateSingleConfig(db, configCache, CONFIG_ID_PostFunStatus, postFunStatus);
           	updateSingleConfig(db, configCache, CONFIG_ID_NextUpdateTime, String.valueOf(System.currentTimeMillis()+interval*1000));
		} catch (Exception e) {
			e.printStackTrace();
		}  finally{
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
	}
	
	private static void updateSingleConfig(SQLiteDatabase db, HashMap<String, String> configCache, String id, String value) {
		if (configCache.containsKey(id)) {
			db.execSQL(String.format(UPDATE_SQL, value, id));
		} else {
			db.execSQL(String.format(INSERT_SQL, id, value));
		}
	}
	
	private static String getLocalConfig(String id, String defaultValue) {
		String value = null;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = SQLiteDatabase.openDatabase(BaseConfig.getApplicationContext().getDatabasePath(CONFIG_DB_NAME).getAbsolutePath(), 
                                             null, 
                                             SQLiteDatabase.OPEN_READONLY);
			cursor = db.rawQuery(String.format(QUERY_SINGLE_SQL, id), null);
			if (cursor != null && cursor.moveToNext()) {
				value = cursor.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return (value != null) ? value : defaultValue;
	}
}
