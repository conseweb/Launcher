package com.bitants.launcherdev.webconnect.downloadmanage.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadDBManager;
import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.FileType;

public class PluginDownloadForwardService extends Service {
	/**
	 * 下载管理进程通讯连接
	 */
	private DownloadServerServiceConnection mConnection;
	/**
	 * 总的队列，包括正在下载，等待、已下载
	 */
	public Map<String, BaseDownloadInfo> mAllDownloadTasks = null;
	private boolean mIsServiceAlive = false;

	@Override
	public IBinder onBind(Intent intent) {
		return impl;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mIsServiceAlive = true;
		bindService();
	}

	/**
	 * 绑定下载服务
	 */
	private void bindService() {
		if (mConnection != null)
			return;
		try {
			// 绑定下载进程
			mConnection = new DownloadServerServiceConnection(this);
			mConnection.bindDownloadService(new CommonCallBack<Boolean>() {
				@Override
				public void invoke(Boolean... arg) {
					boolean bindSuccess = false;
					if (arg != null && arg.length > 0) {
						bindSuccess = arg[0].booleanValue();
					}
					if (bindSuccess) {
						mAllDownloadTasks = DownloadDBManager.getDownloadLoadTask(getApplicationContext());
					}
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if (null != intent) {
				if (null != mConnection) {
					String operation = intent.getStringExtra("operation");
					String identification = intent.getStringExtra("identification");
					if ("pause".equals(operation)) {
						DownloadManager.getInstance().pauseNormalTask(identification, null);
					} else if ("continue".equals(operation)) {
						DownloadManager.getInstance().continueNormalTask(identification, null);
					} else if ("cancel".equals(operation)) {
						DownloadManager.getInstance().cancelNormalTask(identification, null);
					} else {
						BaseDownloadInfo info = generateDownloadInfoFromIntent(intent);
						DownloadManager.getInstance().addNormalTask(info, null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private BaseDownloadInfo generateDownloadInfoFromIntent(Intent intent) {
		String identification = intent.getStringExtra("identification");
		int fileTypeStr = intent.getIntExtra("fileType", 0);
		FileType fileType = FileType.fromId(fileTypeStr);
		String downloadUrl = intent.getStringExtra("downloadUrl");
		String title = intent.getStringExtra("title");
		String savedDir = intent.getStringExtra("savedDir");
		String savedName = intent.getStringExtra("savedName");
		String iconPath = intent.getStringExtra("iconPath");
		HashMap<String, String> extras = ((HashMap<String, String>) intent.getSerializableExtra("additionInfo"));
		BaseDownloadInfo info = new BaseDownloadInfo(identification, fileType.getId(), downloadUrl, title, savedDir, savedName, iconPath);
		info.setAdditionInfo(extras);
		String disId = intent.getStringExtra("disId");
		if (null != disId && !"".equals(disId)) {
			info.setDisId(disId);
		}
		int sp = intent.getIntExtra("sp", -1);
		if (sp != -1) {
			info.setDisSp(sp);
		}
		if (null != mAllDownloadTasks && null != identification && !mAllDownloadTasks.containsKey(identification)) {
			mAllDownloadTasks.put(identification, info);
		}
		return info;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService();
		mAllDownloadTasks.clear();
		mAllDownloadTasks = null;
		mIsServiceAlive = false;
	}

	/**
	 * 解绑下载服务
	 */
	private void unbindService() {
		try {
			if (mConnection != null) {
				this.unbindService(mConnection);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private IPluginDownloadForwardService.Stub impl = new IPluginDownloadForwardService.Stub() {

        @Override
        public String getDownloadTaskById(String Id) throws RemoteException {
            if (null != mConnection && mConnection.isBind()) {
                BaseDownloadInfo downloadInfo = mConnection.getDownloadTask(Id);
                if (downloadInfo != null) {
                    return downloadTaskToJson(downloadInfo);
                }
            }
            return "";
        }

        /**
		 * 判断服务是否活动中
		 */
		public boolean isServiceAlive() {
			return mIsServiceAlive;
		}

		/**
		 * 获取下载的BaseDownloadInfo信息--返回json格式
		 */
		@Override
		public String getData(String identification) throws RemoteException {
			if (null != mConnection && mConnection.isBind()) {
				BaseDownloadInfo info = mConnection.getDownloadTask(identification);
				return toJson(info);
			}
			return "";
		}
		
		/**
		 * 获取所有的download tasks信息--返回json格式
		 */
		@Override
		public String getDownloadTasks() throws RemoteException {
			if (null != mConnection && mConnection.isBind()) {
				Map<String, BaseDownloadInfo> downloadInfoMap = mConnection.getDownloadTasks();
				if (downloadInfoMap != null) {
					return downloadTasksToJson(downloadInfoMap);
				}
			}

			return "";
		}
	};

	private String toJson(BaseDownloadInfo info) {
		if (info == null) {
			return "";
		}
		try {
			JSONObject object = new JSONObject();
			object.put("additionInfo", info.mAdditionInfo);
			object.put("state", info.getState());
			object.put("progress", info.progress);
			object.put("savedDir", info.getSavedDir());
			object.put("savedName", info.getSavedName());
			object.put("apkPath", info.getFilePath());
			return object.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	protected String downloadTasksToJson(Map<String, BaseDownloadInfo> downloadInfoMap) {
		JSONArray tasksArray = new JSONArray();
		Collection<BaseDownloadInfo> c = downloadInfoMap.values();
		Iterator<BaseDownloadInfo> it = c.iterator();
		BaseDownloadInfo info;
		for (; it.hasNext();) {
			info = it.next();
			try {
				JSONObject object = new JSONObject();
				object.put("resId", info.getIdentification());
				object.put("state", info.getState());
				object.put("progress", info.progress);
				object.put("packageName", info.getPacakgeName(this.getApplicationContext()));
				object.put("filepath", info.getFilePath());
				tasksArray.put(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return tasksArray.toString();
	}

    protected String downloadTaskToJson(BaseDownloadInfo info) {

        JSONObject object = new JSONObject();
        try {
            object.put("resId", info.getIdentification());
            object.put("state", info.getState());
            object.put("progress", info.progress);
            object.put("packageName", info.getPacakgeName(this.getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

}
