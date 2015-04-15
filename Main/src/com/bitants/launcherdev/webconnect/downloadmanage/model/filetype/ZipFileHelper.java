package com.bitants.launcherdev.webconnect.downloadmanage.model.filetype;

import java.io.File;

import android.content.Context;
import android.content.Intent;

import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.kitset.util.FileUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;

/**
 * 宠物主题包下载
 * @author linyt
 *
 */
public class ZipFileHelper implements IFileTypeHelper {

	private static final String PET_DIR = CommonGlobal.PLUGIN_DIR + "pet";
	private static final long serialVersionUID = 1L;

	@Override
	public void onClickWhenFinished(Context ctx, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {

	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return "";
	}

	@Override
	public void onDownloadCompleted(final Context ctx, final BaseDownloadInfo downloadInfo, String file) {
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				try {
					String identification = downloadInfo.getIdentification();

					File pet = new File(PET_DIR);
					if (!pet.exists()) {
						pet.mkdirs();
					}

					String extractDir = PET_DIR + File.separator + identification + ".zip";
					File fileExtractDir = new File(extractDir);
					if (fileExtractDir.exists()) {
						FileUtil.delFile(fileExtractDir.getAbsolutePath());
					}
					FileUtil.copy(downloadInfo.getSavedDir() + identification + ".zip", extractDir);
					Intent intent = new Intent("com.wifi.change.theme");
					intent.putExtra("id", identification);
					ctx.sendBroadcast(intent);
					FileUtil.delFile(downloadInfo.getSavedDir() + identification + ".zip");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public String getItemDefIconPath(BaseDownloadInfo downloadInfo) {
		return null;
	}
	
	@Override
	public boolean fileExists(BaseDownloadInfo downloadInfo) {
		if (downloadInfo != null) {
			return downloadInfo.fileExists();
		}
		return false;
	}
}
