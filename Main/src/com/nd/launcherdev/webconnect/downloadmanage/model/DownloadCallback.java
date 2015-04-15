package com.nd.launcherdev.webconnect.downloadmanage.model;

import java.util.HashMap;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.nd.launcherdev.analysis.AppAnalysisConstant;
import com.nd.launcherdev.datamodel.CommonGlobal;
import com.nd.launcherdev.framework.httplib.HttpCommon;
import com.nd.hilauncherdev.framework.httplib.HttpConstants;
import com.nd.launcherdev.kitset.AppDistributeUtil;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.AbstractDownloadCallback;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerService;
import com.nd.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.nd.launcherdev.webconnect.downloadmanage.model.filetype.IFileTypeHelper;
import com.nd.launcherdev.webconnect.downloadmanage.util.DxDownloadBroadcastExtra;
import com.nd.launcherdev.webconnect.downloadmanage.widget.DownloadNotification;
import com.nd.launcherdev.kitset.util.StringUtil;

/**
 * 
 * 下载周期状态回调
 * 
 */
public class DownloadCallback extends AbstractDownloadCallback {

	@Override
	public void onBeginDownload(BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}

		if (!downloadInfo.getIsSilent() && downloadInfo.getFileType() != FileType.FILE_NONE.getId()) {
			sendBeginNotice(downloadInfo);
		}
	}

	@Override
	public void onDownloadCompleted(final BaseDownloadInfo downloadInfo, boolean fileExist) {
		if (downloadInfo == null) {
			return;
		}
		
		if (!downloadInfo.getIsSilent() && downloadInfo.getFileType() != FileType.FILE_NONE.getId()) {
			sendSuccessNotice(downloadInfo);
		}
		
		Context ctx = CommonGlobal.getApplicationContext();
		
		IFileTypeHelper helper = FileType.fromId(downloadInfo.getFileType()).getHelper();
		if (null != helper) {
			helper.onDownloadCompleted(CommonGlobal.getApplicationContext(), downloadInfo, downloadInfo.getFilePath());
		}
		
		if (!fileExist) {
			HashMap<String,String> additionInfo = downloadInfo.getAdditionInfo();
			if (additionInfo != null) {
				String additionVal = additionInfo.get(AppAnalysisConstant.APP_ANALYSIS_KEY_FLAG);
				if (!StringUtil.isEmpty(additionVal)) {
					String pckName = downloadInfo.getIdentification();
					if (pckName.startsWith(DownloadServerService.RECOMMEND_PREFIX)) {
						pckName = pckName.substring(DownloadServerService.RECOMMEND_PREFIX.length());
					}
					AppDistributeUtil.logAppDisDownloadSucc(ctx,pckName,Integer.parseInt(additionVal));
				}
			}
		}
		
		if (downloadInfo.feedbackUrl != null && downloadInfo.feedbackUrl.startsWith("http")) {
			ThreadUtil.executeMore(new Runnable() {
				@Override
				public void run() {
					new HttpCommon(downloadInfo.feedbackUrl).httpFeedback();
				}
			});
		}
	}

	@Override
	public void onDownloadFailed(BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}
		
		if (!downloadInfo.getIsSilent() && downloadInfo.getFileType() != FileType.FILE_NONE.getId()) {
			sendFailNotice(downloadInfo);
		}
		
		Context ctx = CommonGlobal.getApplicationContext();
		
		HashMap<String,String> additionInfo = downloadInfo.getAdditionInfo();
		if (additionInfo != null) {
			String additionVal = additionInfo.get(AppAnalysisConstant.APP_ANALYSIS_KEY_FLAG);
			if (!StringUtil.isEmpty(additionVal)) {
				String pckName = downloadInfo.getIdentification();
				if (pckName.startsWith(DownloadServerService.RECOMMEND_PREFIX)) {
					pckName = pckName.substring(DownloadServerService.RECOMMEND_PREFIX.length());
				}
				AppDistributeUtil.logAppDisDownloadFail(ctx,pckName,Integer.parseInt(additionVal));
			}
		}
	}

	@Override
	public void onDownloadWorking(BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}
		
		if (!downloadInfo.getIsSilent() && downloadInfo.getFileType() != FileType.FILE_NONE.getId()) {
			sendNotice(downloadInfo);
		}
	}

	@Override
	public void onHttpReqeust(BaseDownloadInfo downloadInfo, int requestType) {
		if (downloadInfo == null) {
			return;
		}
		
		Context ctx = CommonGlobal.getApplicationContext();
		if (!downloadInfo.getIsSilent() 
			&& downloadInfo.getFileType() != FileType.FILE_NONE.getId()
			&& requestType == HttpConstants.HTTP_REQUEST_CANCLE) {
			DownloadNotification.downloadCancelledNotification(ctx, Math.abs(downloadInfo.getDownloadUrl().hashCode()));	
		}
	}
	
	private Intent createIntent(BaseDownloadInfo downloadInfo) {
		Intent intent = new Intent(DxDownloadBroadcastExtra.ACTION_SHOW);
		if (downloadInfo != null) {
			intent.putExtra(DownloadServerService.EXTRA_SHOW_TYPE, downloadInfo.getFileType());
			///intent.putExtra(DownloadManagerActivity.EXTRA_FROM, DownloadManagerActivity.FROM_TZL);
		}
		
		return intent;
	}
	
	private void sendNotice(BaseDownloadInfo downloadInfo) {
//		Context context = CommonGlobal.getApplicationContext();
//		String str = downloadInfo.getTitle() + context.getResources().getString(R.string.common_downloading) ;
//		PendingIntent PIntent = PendingIntent.getActivity(context, 0, createIntent(downloadInfo), PendingIntent.FLAG_UPDATE_CURRENT);
//		int noticePosition = Math.abs(downloadInfo.getDownloadUrl().hashCode());
//		DownloadNotification.downloadRunningNotificationWithProgress(context, noticePosition, str, null, PIntent, downloadInfo.progress);
	}
	
	private void sendBeginNotice(BaseDownloadInfo downloadInfo) {
		Context context = CommonGlobal.getApplicationContext();
		String str = downloadInfo.getTitle();
		PendingIntent PIntent = PendingIntent.getActivity(context, 0, createIntent(downloadInfo), PendingIntent.FLAG_UPDATE_CURRENT);
		int noticePosition = Math.abs(downloadInfo.getDownloadUrl().hashCode());
		DownloadNotification.downloadBeganNotification(context, noticePosition, str, null, PIntent, downloadInfo.progress);
	}
	
	private void sendSuccessNotice(BaseDownloadInfo downloadInfo) {
		Context context = CommonGlobal.getApplicationContext();
		String str = downloadInfo.getTitle();
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, createIntent(downloadInfo), PendingIntent.FLAG_UPDATE_CURRENT);
		int noticePosition = Math.abs(downloadInfo.getDownloadUrl().hashCode());
		DownloadNotification.downloadCompletedNotification(context, noticePosition, str, null, pIntent);
	}
	
	private void sendFailNotice(BaseDownloadInfo downloadInfo) {
		Context context = CommonGlobal.getApplicationContext();
		String str = downloadInfo.getTitle();
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, createIntent(downloadInfo), 0);
		int noticePosition = Math.abs(downloadInfo.getDownloadUrl().hashCode());
		DownloadNotification.downloadFailedNotification(context, noticePosition, str, pIntent);
	}

}
