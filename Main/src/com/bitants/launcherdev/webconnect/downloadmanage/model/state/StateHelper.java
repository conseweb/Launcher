package com.bitants.launcherdev.webconnect.downloadmanage.model.state;

import android.content.Context;

import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;


public enum StateHelper {

	STATE_DOWNLOADING(new StateDownloadingHelper()), 
	STATE_PAUSE(new StatePauseHelper()), 
	STATE_FINISHED(new StateFinishedHelper()), 
	STATE_WAITING(new StateWaitingHelper()), 
	STATE_INSTALLED(new StateInstalledHelper()), 
	STATE_INSTALLING(new StateInstallingHelper()), 
	STATE_NONE(null);
	
	IDownloadStateHelper mHelper = null;

	StateHelper(IDownloadStateHelper helper) {
		mHelper = helper;
	};

	public IDownloadStateHelper getHelper() {
		return mHelper;
	}
		
	public int getId() {
		return mHelper != null ? mHelper.getState() : -1;
	}
	
	public static StateHelper fromState(int state) {
		for (StateHelper s : StateHelper.values()) {
			if (s.mHelper != null 
				&& s.mHelper.getState() == state) {
				return s;
			}
		}
		return STATE_NONE;
	}
	
	public static void redownload(final Context ctx, ViewHolder viewHolder, final BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}
		
		StringBuffer sb = new StringBuffer("0.0MB");
		sb.append("/").append(downloadInfo.totalSize);
		viewHolder.desc.setText(sb.toString());
		
		ThreadUtil.executeMore(new Runnable() { 
			public void run() {
				DownloadManager downloadManager = DownloadManager.getInstance();
				downloadManager.cancelNormalTask(downloadInfo.getIdentification(), null);
				downloadInfo.downloadSize = null;
				downloadInfo.progress = 0;
				downloadInfo.setState(null);
				downloadManager.addNormalTask(downloadInfo, null);
			}
		});
	}
}
