package com.bitants.launcherdev.webconnect.downloadmanage.model.filetype;

import java.io.File;

import android.content.Context;
import android.content.Intent;

import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcher.R;

public class DynamicApkFileHelper implements IFileTypeHelper {

	private static final long serialVersionUID = 1L;
	public static final String PLUGIN_NAME = "plugin_name";
	public static final String EXTRAS_WIDGET_TYPE = "extras_widget_type";
	public static final String EXTRAS_WIDGET_POS_TYPE = "extras_widget_pos_type";
	public static final String WIDGET_TYPE = "widget_type";
	public static final String WIDGET_POS_TYPE = "widget_pos_type";
	public static final String DYNAMIC_WIDGET_ENABLE = "com.baidu.android.action.DYNAMIC_WIDGET_ENABLE";

	@Override
	public void onClickWhenFinished(Context ctx, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		installPlugin(ctx, downloadInfo);
	}


	private void installPlugin(Context ctx, BaseDownloadInfo downloadInfo) {
		String fileStr = downloadInfo.getSavedDir() + downloadInfo.getSavedName();
		File file = new File(fileStr);
		if (file.exists()) {
			Intent intent = new Intent(DYNAMIC_WIDGET_ENABLE);
			intent.putExtra(PLUGIN_NAME, downloadInfo.getSavedName());
			intent.putExtra(WIDGET_TYPE, downloadInfo.getAdditionInfo().get(EXTRAS_WIDGET_TYPE));
			intent.putExtra(WIDGET_POS_TYPE, downloadInfo.getAdditionInfo().get(EXTRAS_WIDGET_POS_TYPE));
			ctx.sendBroadcast(intent);
		} else {
			MessageUtils.makeShortToast(ctx, R.string.download_install_error);
		}
	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return BaseConfig.getApplicationContext().getResources().getString(R.string.common_button_use);
	}

	@Override
	public void onDownloadCompleted(Context ctx, BaseDownloadInfo downloadInfo, String file) {
		/**
		 * widget目录存在包名.jar文件,则判断为更新插件下载的情况,直接覆盖升级插件
		 */
		String oldStr = CommonGlobal.WIDGET_PLUGIN_DIR + downloadInfo.getIdentification() + ".jar";
		File f = new File(oldStr);
		if (f.exists()) {
			installPlugin(ctx, downloadInfo);
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
