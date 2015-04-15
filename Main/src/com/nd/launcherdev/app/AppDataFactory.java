package com.nd.launcherdev.app;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.nd.launcherdev.app.data.AppDataBase;
import com.nd.launcherdev.app.data.AppTable;
import com.nd.launcherdev.datamodel.Global;
import com.nd.launcherdev.kitset.util.BaseBitmapUtils;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.support.BaseIconCache;
import com.nd.launcherdev.app.data.AppDataBase;
import com.nd.launcherdev.app.data.AppTable;
import com.nd.launcherdev.launcher.info.ApplicationInfo;

/**
 * 应用程序工厂 <br>
 * Author:ryan
 */
public class AppDataFactory extends BaseAppDataFactory{
	private static final String TAG = "AppDataFactory";	
	
	private static AppDataFactory sInstance;
	
	public static AppDataFactory getInstance() {
		if (sInstance == null) {
			sInstance = new AppDataFactory();
		}
		return sInstance;
	}
	
	private AppDataFactory() {
		
	}
	/**
	 * 数据库中加载所有程序(语言切换需要重新设置应用程序名称)
	 * 
	 * @param ctx
	 * @return List<ApplicationInfo>
	 */
	public List<ApplicationInfo> loadDrawerAppFromDBForLocale(Context ctx) {
		AppDataBase db = instanceDatabase(ctx);
		Cursor c = null;
		try {
		    c = db.query(AppTable.SELECT_ALL_APP_FOR_LOCALE);
			if (c.getCount() == 0) {
				return null;
			}
			List<ApplicationInfo> result = new ArrayList<ApplicationInfo>(c.getCount());
			while (c.moveToNext()) {
				ApplicationInfo info = new ApplicationInfo();
				info.id = c.getInt(AppTable.INDEX_ID);
				info.componentName = new ComponentName(c.getString(AppTable.INDEX_PCK), c.getString(AppTable.INDEX_CLS));
				result.add(info);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (c != null) {
				c.close();
			}
			if (db != null) {
				db.close();
			}			
		}
	}
	
	public AppDataBase instanceDatabase(Context ctx) {
		return new AppDataBase(ctx);
	}
	
}
