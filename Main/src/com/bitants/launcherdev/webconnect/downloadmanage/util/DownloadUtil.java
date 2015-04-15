package com.bitants.launcherdev.webconnect.downloadmanage.util;

import android.content.Context;
import android.os.Looper;

import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.webconnect.downloadmanage.model.AppDownloadItem;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.webconnect.downloadmanage.model.AppDownloadItem;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.bitants.launcher.R;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.webconnect.downloadmanage.model.AppDownloadItem;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.FileType;

public class DownloadUtil {

	/**
	 * @deprecated
	 */
	public static void startDownload(Context context,AppDownloadItem item) {
		DownloadServerServiceConnection mDownloadService = new DownloadServerServiceConnection(context);
		boolean downloadSuc=false;
		BaseDownloadInfo downloadInfo=makeDownloadInfo(item);
		downloadSuc=mDownloadService.addDownloadTask(downloadInfo);
		
		if(!downloadSuc)
		{
			//下载失败
			boolean isNewLooper=false;
			if(Looper.myLooper()==null)
			{
				Looper.prepare();
				isNewLooper=true;
			}
			
			MessageUtils.makeShortToast(context, R.string.common_download_failed);
			
			Looper.loop();
			if(isNewLooper)
				Looper.myLooper().quit();
		}else{
			item.setDownloadState(DownloadState.STATE_WAITING);
		}
	}
	
	/**
	 * @deprecated
	 */
	public static BaseDownloadInfo makeDownloadInfo(AppDownloadItem item) {
		BaseDownloadInfo downloadInfo=new BaseDownloadInfo(item.getKey(),
				                                           FileType.FILE_APK.getId(),
				                                           item.getApkUrl(),
				                                           item.getTitle(),
				                                           CommonGlobal.PACKAGE_DOWNLOAD_DIR,
				                                           item.getApkFileName(),
				                                           item.getIconFilePath());
		downloadInfo.totalSize=item.getSize();
		downloadInfo.feedbackUrl=item.getFeedbackUrl();
		return downloadInfo;
	}
}
