package com.nd.launcherdev.webconnect.downloadmanage.model.state;

import android.content.Context;
import android.view.View;

import com.nd.launcherdev.datamodel.CommonGlobal;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.nd.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.nd.launcherdev.webconnect.downloadmanage.model.filetype.IFileTypeHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcher.R;
import com.nd.launcherdev.datamodel.CommonGlobal;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter;
import com.nd.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.nd.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.nd.launcherdev.webconnect.downloadmanage.model.filetype.IFileTypeHelper;

/** 
 * 已下载但未安装状态
 * 
 * @author pdw 
 * @version 
 * @date 2012-9-19 下午04:33:24 
 */
public class StateFinishedHelper implements IDownloadStateHelper {
	
	private final int state=DownloadState.STATE_FINISHED;
	
	@Override
	public boolean action(Context ctx, 
			                DownloadManageAdapter.ViewHolder viewHolder,
			                BaseDownloadInfo downloadInfo) {	
		if (downloadInfo == null) {
			return false;
		}
		
		String btnText = viewHolder.funBtn.getText().toString();
		
		if (null != btnText && btnText.equals(ctx.getResources().getString(R.string.download_notify_finish))) {
			return false;
		}
		
		if (null != btnText && btnText.equals(ctx.getResources().getString(R.string.common_button_redownload))) {
			StateHelper.redownload(ctx, viewHolder, downloadInfo);
		} else {
			FileType fileType = FileType.fromId(downloadInfo.getFileType());
			IFileTypeHelper helper = fileType.getHelper();
			if (null != helper) {
				helper.onClickWhenFinished(ctx, viewHolder, downloadInfo);
			}
		}
		
		return true;
	}
	
	@Override
	public void initView(DownloadManageAdapter.ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		boolean exists = false;
		FileType fileType = FileType.fromId(downloadInfo.getFileType());
		IFileTypeHelper helper = fileType.getHelper();
		if (helper != null) {
			exists = helper.fileExists(downloadInfo);
		} else {
			exists = downloadInfo.fileExists();
		}
		
		if (!exists) {
			if(downloadInfo.getFileType() == FileType.FILE_LOCK.getId()){
				//下载附加参数newThemeId，由com.baidu.dx.personalize.theme.shop.shop3.
				//ThemeShopV3LauncherExAPI.onReceiveHandler方法附加进去
				String newThemeId = DownloadManager.isInstallModule(CommonGlobal.getApplicationContext(),
                        downloadInfo.getIdentification());
				if(!StringUtil.isEmpty(newThemeId)) {
					viewHolder.desc.setText(R.string.download_finished);
					viewHolder.setFunButtonContent(null != helper ? helper.getItemTextWhenFinished(downloadInfo) : "");
				} else {
					viewHolder.desc.setText("");
					viewHolder.setFunButtonContent(R.string.common_button_redownload);
				}
			} else {
				viewHolder.desc.setText("");
				viewHolder.setFunButtonContent(R.string.common_button_redownload);
			}
		} else {
			viewHolder.desc.setText(R.string.download_finished);
			viewHolder.setFunButtonContent(helper != null ? helper.getItemTextWhenFinished(downloadInfo) : "");
		}
		viewHolder.progress.setVisibility(View.GONE);
		viewHolder.state.setVisibility(View.INVISIBLE);
	}

	@Override
	public int getState() {
		return state;
	}
	
}
