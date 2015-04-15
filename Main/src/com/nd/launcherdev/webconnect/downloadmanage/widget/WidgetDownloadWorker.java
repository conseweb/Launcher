package com.nd.launcherdev.webconnect.downloadmanage.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.nd.hilauncherdev.framework.httplib.CommonDownloadWorker;
import com.nd.hilauncherdev.framework.httplib.CommonDownloadWorkerSupervisor;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadBroadcastExtra;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.nd.launcherdev.webconnect.downloadmanage.util.DxDownloadBroadcastExtra;
import com.nd.launcherdev.webconnect.downloadmanage.util.DxDownloadBroadcastExtra;

/**
 * widget下载类
 * 
 * @author pdw
 * @version 1.0
 * @date 2012-7-19 下午04:56:53
 */
public class WidgetDownloadWorker extends CommonDownloadWorker {

	// 通知位置
	private int noticePosition;

	/**
	 * 构造下载线程 由此构造的下载线程将统一由{@link CommonDownloadWorkerSupervisor}管理
	 * 
	 * @param url
	 * @param savePath
	 *            以"/"结束，如"/sdcard/PandaHome3/Theme/"
	 * @param tipName
	 *            可用于提示显示的文件名字，如在通知栏显示提示：微博小助手，91天气秀
	 */
	// public WidgetDownloadWorker(String url, String savePath, String tipName)
	// {
	// super(url, savePath, tipName);
	// noticePosition = Math.abs(url.hashCode());
	// }

	/**
	 * 构造下载线程 由此构造的下载线程将统一由{@link CommonDownloadWorkerSupervisor}管理
	 * 
	 * @param url
	 * @param savePath
	 *            以"/"结束，如"/sdcard/PandaHome3/Theme/"
	 * @param specifyFileName
	 *            指定下载文件名
	 * @param tipName
	 *            提示名称
	 */
	public WidgetDownloadWorker(Context context,String url, String savePath, String specifyFileName, String tipName) {
		super(context, url, savePath, specifyFileName, tipName);
	}

	@Override
	protected void onHttpReqeust(String identification, String url, int requestType, long totalSize, long downloadSize) {
		super.onHttpReqeust(identification, url, requestType, totalSize, downloadSize);
	}

	@Override
	protected void onDownloadWorking(String identification, String url, long totalSize, long downloadSize, final int progress) {
		super.onDownloadWorking(identification, url, totalSize, downloadSize, progress);
		sendNotice(identification,downloadSize,totalSize, progress, DownloadState.STATE_DOWNLOADING);
	}

	@Override
	protected void onDownloadCompleted(String identification, String url, String file, long totalSize) {
		super.onDownloadCompleted(identification, url, file, totalSize);
		sendSuccessNotice(file);
	}

	@Override
	protected void onBeginDownload(String identification, String url, long downloadSize,int progress) {
		super.onBeginDownload(identification, url, downloadSize, progress);
		sendBeginNotice(identification,downloadSize,0,progress,DownloadState.STATE_DOWNLOADING);
	}

	@Override
	protected void onDownloadFailed(String identification, String url) {
		super.onDownloadFailed(identification, url);
		sendFailNotice();
	}

	private void sendFailNotice() {
//		String str = tipName + "," + ctx.getResources().getString(R.string.common_download_failed);
		String str = tipName;
		PendingIntent pi = PendingIntent.getActivity(mAppContext, 0, new Intent(), 0);
		DownloadNotification.downloadFailedNotification(mAppContext, noticePosition, str, pi);
	}
	/**
	 * update by lhy
	 * @param identification
	 * @param downloadSize
	 * @param totalSize
	 * @param progress
	 * @param state
	 */
	private void sendNotice(String identification,long downloadSize,long totalSize,int progress,int state) {
//		String str = tipName+ctx.getResources().getString(R.string.common_downloading) ;
		String str = tipName;
		Intent intent = new Intent(DxDownloadBroadcastExtra.ACTION_SHOW);
		PendingIntent PIntent = PendingIntent.getActivity(mAppContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		DownloadNotification.downloadRunningNotificationWithProgress(mAppContext, noticePosition, str, null, PIntent, progress);
	
		sendStateBroatcast(identification,downloadSize,totalSize,progress,state);
	}
    
	private void sendBeginNotice(String identification,long downloadSize,long totalSize,int progress,int state) {
//		String str = tipName+ctx.getResources().getString(R.string.common_downloading) ;
		String str = tipName;
		Intent intent = new Intent(DxDownloadBroadcastExtra.ACTION_SHOW);
		PendingIntent PIntent = PendingIntent.getActivity(mAppContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		DownloadNotification.downloadBeganNotification(mAppContext, noticePosition, str, null, PIntent, progress);
	
		sendStateBroatcast(identification,downloadSize,totalSize,progress,state);
	}
	
	private void sendSuccessNotice(String file) {
//		String str = tipName + "," + ctx.getResources().getString(R.string.common_download_ended);
		String str = tipName;
		
		Intent mIntent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse("file://" + file);
		mIntent.setDataAndType(uri, "application/vnd.android.package-archive");

		PendingIntent pIntent = PendingIntent.getActivity(mAppContext, 0, mIntent, 0);
		DownloadNotification.downloadCompletedNotification(mAppContext, noticePosition, str, null, pIntent);
	}
	/**
	 * update by lyh
	 * @param identification
	 * @param downloadSize
	 * @param totalSize
	 * @param progress
	 * @param state
	 */
	private void sendStateBroatcast(String identification, long downloadSize,long totalSize, int progress, int state) {
		Intent intent = null ;
		intent = new Intent(DxDownloadBroadcastExtra.ACTION_DOWNLOAD_STATE);
		intent.putExtra(DownloadBroadcastExtra.EXTRA_IDENTIFICATION, identification);
		intent.putExtra(DownloadBroadcastExtra.EXTRA_DOWNLOAD_URL, this.url);
		intent.putExtra(DownloadBroadcastExtra.EXTRA_PROGRESS, progress);
		intent.putExtra(DownloadBroadcastExtra.EXTRA_STATE, state);
		if (downloadSize != -1) {
			intent.putExtra(DownloadBroadcastExtra.EXTRA_DOWNLOAD_SIZE, downloadSize);
		}
		
		if(totalSize>0)
			intent.putExtra(DownloadBroadcastExtra.EXTRA_TOTAL_SIZE, totalSize);
		
		mAppContext.sendBroadcast(intent);
	}
}
