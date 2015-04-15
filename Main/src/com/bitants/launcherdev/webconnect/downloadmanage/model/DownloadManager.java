package com.bitants.launcherdev.webconnect.downloadmanage.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;

import com.bitants.launcherdev.core.model.AbstractDataBase;
import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.datamodel.db.MyPhoneDB;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.AbstractDownloadCallback;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.AbstractDownloadManager;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.IDownloadManagerService;
import com.bitants.launcherdev.webconnect.downloadmanage.util.DxDownloadBroadcastExtra;

public class DownloadManager extends AbstractDownloadManager {

	private static DownloadManager sInstance = null;
	
	public static DownloadManager getInstance() {
		if (sInstance == null) {
			sInstance = new DownloadManager(CommonGlobal.getApplicationContext());
		}
		return sInstance;
	}
	
	private DownloadManager(Context context) {
		super(context);
	}

	@Override
	protected Class<? extends AbstractDataBase> getDownloadDb() {
		return MyPhoneDB.class;
	}

	@Override
	protected Class<? extends AbstractDownloadCallback> getDownloadCallback() {
		return DownloadCallback.class;
	}

	void initService(IDownloadManagerService service) {
		if (service == null) {
			return;
		}
		
		try {
			service.setDownloadCallback(getDownloadCallback().getName());
			service.setDownloadDb(getDownloadDb().getName());
			service.setBroadcastAction(getBroadcastAction());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getBroadcastAction() {
		return DxDownloadBroadcastExtra.ACTION_DOWNLOAD_STATE;
	}
	
	public static List<String> loadAppDownloadLog(Context context) {
		List<String> logs = new ArrayList<String>();
		Cursor c = null;
		MyPhoneDB db = null;
		try {
			db = new MyPhoneDB(context);
			String sql = "select _id from log_download where file_type = 0";
			c = db.query(sql);
			boolean hasNext = c.moveToFirst();
			while (hasNext) {
				logs.add(c.getString(0));
				hasNext = c.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null){
				c.close();
				c = null;
			}
			if(db != null){
				db.close();
				db = null;
			}
		}
		return logs;
	}
	
	public static boolean logDown(Context context,String id, String themeId) {
    	String format = "update log_download set extra = '%s' where _id='%s'";
    	String sql = String.format(format, themeId,id);
    	MyPhoneDB db = null;
    	boolean rslt = false;
		try {
			db = new MyPhoneDB(context);
			rslt = db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(db != null){
				db.close();
				db = null;
			}
		}
		return rslt;
    }
    
	
	public static String isInstallModule(Context context,String id) {
		String newThemeId = "";
		Cursor c = null;
		MyPhoneDB db = null;
		try {
			db = new MyPhoneDB(context);
			String sql = "select extra from log_download where _id = '" + id + "'";
			c = db.query(sql);
			if(c.moveToFirst()){
				newThemeId = c.getString(0);
			}
			c.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
				c= null;
			}
			if(db!=null){
				db.close();
				db= null;
			}
		}
		return newThemeId;
	}

}
