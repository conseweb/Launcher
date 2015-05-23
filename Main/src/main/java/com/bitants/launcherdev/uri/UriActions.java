package com.bitants.launcherdev.uri;

//import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
//import com.bitants.launcherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
//import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
//import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;

/**
 * 桌面动作响应调用
 *
 */
public class UriActions {

	/**
	 * 通用下载管理调用
	 * 
	 * @param type 文件类型
	 * @param downloadUrl 下载地址， 不能为null
	 * @param title 不能为null
	 * @param savedDir
	 * @param savedName
	 * @param iconPath
	 * @param pkgName 统计包名
	 * @param sp 统计sp
	 */
	public static void downloadManagerAction(int type, String downloadUrl, String title, String savedDir, String savedName,
			String iconPath, String pkgName, int sp) {
//		savedDir = StringUtil.isEmpty(savedDir) ? BaseConfig.WIFI_DOWNLOAD_PATH : savedDir;
//		savedName = StringUtil.isEmpty(savedName) ? "download_" + +System.currentTimeMillis() : savedName;
//		BaseDownloadInfo info = new BaseDownloadInfo(savedName, type, downloadUrl, title, savedDir, savedName, iconPath);
//		if (!StringUtil.isEmpty(pkgName)) {// 下载统计
//			if (sp < 0) {
////				sp = AppAnalysisConstant.SP_NOTIFICATION_PUSH_RECOMMEND_APP;
//			}
//			info.setDisId(pkgName);
//			info.setDisSp(sp);
//			AppDistributeUtil.logAppDisDownloadStart(BaseConfig.getApplicationContext(), pkgName, sp);
//		}
//		DownloadManager.getInstance().addNormalTask(info, null);
	}
}
