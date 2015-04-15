package com.bitants.launcherdev.webconnect.downloadmanage.model.filetype;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.kitset.DigestUtils;
import com.bitants.launcherdev.kitset.util.FileUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.kitset.util.WallpaperUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.config.BaseConfig;

public class WallpaperFileHelper implements IFileTypeHelper {
	
	private static final long serialVersionUID = 1L;
	
	private static final String LOCAL_WALLPAPER_NOTIFY_BROADCAST = "com.nd.hilauncherdev.myphone.mytheme.wallpaper.localWallpaperNotify";
	
	private final static String CACHES_HOME_MARKET = BaseConfig.getBaseDir() + "/caches/91space/";
	
	@Override
	public void onClickWhenFinished(Context ctx, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(ctx, "com.baidu.dx.personalize.PersonalizeActivityGroup"));
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("PERSONALIZE_TAB", 1);
		intent.putExtra("SECOND_TAB", 3);
		intent.putExtra("wallPaperLocalFlag", true);
		ctx.startActivity(intent);
	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return CommonGlobal.getApplicationContext().getResources().getString(R.string.common_button_set);
	}
	
	@Override
	public void onDownloadCompleted(final Context ctx, final BaseDownloadInfo downloadInfo, String file) {
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				String thumbUrl = downloadInfo.getAdditionInfo().get("thumbUrl");
				String localThumbPath = WallpaperUtil.getWPPicHomeCachePath() + "/" + downloadInfo.getSavedName();
				String srcFile = url2path(thumbUrl, CACHES_HOME_MARKET);
				FileUtil.copy(srcFile, localThumbPath);
				ctx.sendBroadcast(new Intent(LOCAL_WALLPAPER_NOTIFY_BROADCAST));
			}
		});
	}

	@Override
	public String getItemDefIconPath(BaseDownloadInfo downloadInfo) {
		return "drawable:downloadmanager_wallpaper_icon";
	}
	
	/**
	 * 
	 * @param url
	 * @param rootpath  结尾带"/"
	 * @return
	 */
	public static String url2path (String url, String rootpath){
		if (url==null)
			url = "";
		String rs = rootpath;
		String picname = getMd5FileName(url);
		rs = rs+picname;
		//TODO 兼容旧版本
		//rs = StringUtil.renameRes(rs);
		return rs;
	}
	
	private static String getMd5FileName(String url){
		if ( url==null || "".equals(url) )
			return "";
		String str = DigestUtils.md5Hex(url)+"";
		return str; 
	}
	
	@Override
	public boolean fileExists(BaseDownloadInfo downloadInfo) {
		if (downloadInfo != null) {
			return downloadInfo.fileExists();
		}
		return false;
	}
}
