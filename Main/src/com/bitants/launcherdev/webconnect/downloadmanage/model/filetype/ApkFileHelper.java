package com.bitants.launcherdev.webconnect.downloadmanage.model.filetype;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

import com.bitants.launcherdev.dynamic.util.LoaderUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcherdev.dynamic.util.LoaderUtil;
import com.bitants.launcherdev.kitset.util.ApkTools;
import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.kitset.util.ApkTools;
import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcher.R;
import com.bitants.launcherdev.dynamic.util.LoaderUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;

public class ApkFileHelper implements IFileTypeHelper {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void onClickWhenFinished(Context ctx, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		final String fileStr = downloadInfo.getSavedDir() + downloadInfo.getSavedName();
		final File file = new File(fileStr);
		if (file.exists()) {
			ApkTools.installApplication(ctx, file);
		} else {
			MessageUtils.makeShortToast(ctx, R.string.download_install_error);
		}
	}
	
	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return BaseConfig.getApplicationContext().getResources().getString(R.string.common_button_install);
	}
	
	@Override
	public void onDownloadCompleted(Context ctx, BaseDownloadInfo downloadInfo, String file) {
		String pkgName = null;
		if (downloadInfo != null) {
			try {
				pkgName = downloadInfo.getPacakgeName(ctx);
				int verCode = downloadInfo.getVersionCode(ctx);
				if (!TextUtils.isEmpty(pkgName)) {
					downloadInfo.putAdditionInfo(BaseDownloadInfo.KEY_PKG_NAME, pkgName);
					if (verCode > 0) {
						downloadInfo.putAdditionInfo(BaseDownloadInfo.KEY_PKG_VER_CODE, String.valueOf(verCode));
					}
					
					DownloadManager.getInstance().modifyAdditionInfo(downloadInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (downloadInfo != null && downloadInfo.getIsSilent()) {
			return;
		}
		
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		
		if (ApkTools.checkApkIfValidity(ctx, file) && LoaderUtil.readClientApkInfo(file)==null) {//动态插件不安装
			ApkTools.installApplication(ctx, new File(file));
		}
	}

	@Override
	public String getItemDefIconPath(BaseDownloadInfo downloadInfo) {
		return "drawable:downloadmanager_apk_icon";
	}

	@Override
	public boolean fileExists(BaseDownloadInfo downloadInfo) {
		if (downloadInfo != null) {
			return downloadInfo.fileExists();
		}
		return false;
	}
}
