package com.nd.hilauncherdev.webconnect.downloadmanage.model.filetype;

import java.io.File;

import android.content.Context;
import android.util.Log;

import com.nd.hilauncherdev.datamodel.CommonGlobal;
import com.nd.hilauncherdev.kitset.util.FileUtil;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.kitset.util.WallpaperUtil;
import com.nd.hilauncherdev.kitset.util.ZipUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;

/**
 * @author Michael Date:2014-3-12下午5:54:15
 * 
 */
public class WifiAutoFileHelper implements IFileTypeHelper {

	public static final String WIFI_DOWNLOAD_FILE_SUCCESS = "wifi_download_file_success";

	public static final String WIFI_DOWNLOAD_FILE_ID = "wifi_download_file_id";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onClickWhenFinished(Context context, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		// do nothing
	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return null;
	}

	@Override
	public void onDownloadCompleted(Context context, final BaseDownloadInfo downloadInfo, final String file) {
		// 将文件夹中的文件解压到对应的目录中
	
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				
				try {
					Log.e("WifiFileAutoDownloadStateHelper", "解压文件");
					String fileIdentification = downloadInfo.getIdentification();
					String extractDir = CommonGlobal.DOWNLOAD_DIR + File.separator + fileIdentification + File.separator;
					File fileExtractDir = new File(extractDir);
					if (fileExtractDir.exists()) {
						if (fileExtractDir.isDirectory()) {
							FileUtil.delFolder(fileExtractDir.getAbsolutePath());
						} else {
							FileUtil.delFile(fileExtractDir.getAbsolutePath());
						}
					}
					fileExtractDir.mkdir();
					ZipUtil.ectract(file, extractDir, false);

					// 处理滤镜
					File filtersFile = new File(extractDir + "Filters");
					if (filtersFile.exists() && filtersFile.isDirectory()) {// 拷贝到指定位置
						FileUtil.copyFolder(filtersFile.getAbsolutePath(), WallpaperUtil.getFilterPicHome());
					}
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
