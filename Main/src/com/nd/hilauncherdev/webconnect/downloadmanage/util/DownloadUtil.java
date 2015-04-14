package com.nd.hilauncherdev.webconnect.downloadmanage.util;

import android.content.Context;
import android.os.Looper;

import com.nd.hilauncherdev.datamodel.CommonGlobal;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.AppDownloadItem;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.bitants.launcher.R;

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
