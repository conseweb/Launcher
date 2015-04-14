package com.nd.hilauncherdev.webconnect.downloadmanage.model.state;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.nd.hilauncherdev.datamodel.CommonGlobal;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.simon.android.pandahome2.R;

/**
 * 下载已安装状态，这是下载的最终状态
 * 
 * @author pdw
 * @version
 * @date 2012-9-19 下午04:32:59
 */
public class StateInstalledHelper implements IDownloadStateHelper {
	
	private final int state = DownloadState.STATE_INSTALLED;

	@Override
	public boolean action(Context context, 
			                ViewHolder viewHolder, 
			                BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null || viewHolder == null) {
			return false;
		}
		String btnText = viewHolder.funBtn.getText().toString();
		if (null != btnText && btnText.equals(CommonGlobal.getApplicationContext().getResources().getString(R.string.common_installed))) {
			return true;
		}
		
		FileType filetype = FileType.fromId(downloadInfo.getFileType());
		if (filetype == FileType.FILE_DYNAMIC_APK) {
			return true;
		}
		
		String pkgName = downloadInfo.getPacakgeName(context);
		if (!TextUtils.isEmpty(pkgName)) {
			AndroidPackageUtils.runApplication(context, pkgName);
		} else {
			final String fileStr = downloadInfo.getSavedDir() + downloadInfo.getSavedName();		
			final File file = new File(fileStr);
			if (file.exists()) {
				ApkTools.installApplication(context, file);
			} else {
				MessageUtils.makeShortToast(context, R.string.download_install_error);
			}	
		}
		
		return true;
	}

	@Override
	public void initView(ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}
		
		viewHolder.desc.setText(R.string.download_finished);
		FileType fileType = FileType.fromId(downloadInfo.getFileType());
		if (fileType == FileType.FILE_DYNAMIC_APK) {
			viewHolder.setFunButtonContent(R.string.common_button_used);
		} else {
			viewHolder.setFunButtonContent(R.string.common_installed);
			try {
				String pkgName = downloadInfo.getPacakgeName(CommonGlobal.getApplicationContext());
				if (!StringUtil.isEmpty(pkgName)) {
					Intent intent = AndroidPackageUtils.getPackageMainIntent(CommonGlobal.getApplicationContext(), pkgName);
					if (intent != null) {
						viewHolder.setFunButtonContent(R.string.common_button_open);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		viewHolder.progress.setVisibility(View.GONE);
		viewHolder.state.setVisibility(View.INVISIBLE);
	}

	@Override
	public int getState() {
		return state;
	}
}
