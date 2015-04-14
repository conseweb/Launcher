package com.nd.hilauncherdev.webconnect.downloadmanage.model.state;

import android.content.Context;
import android.view.View;

import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadManager;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcher.R;

/** 
 * 正在下载状态
 * 
 * @author pdw  
 * @version 
 * @date 2012-9-19 下午04:31:34 
 */
public class StateDownloadingHelper implements IDownloadStateHelper {
	
	private final int state = DownloadState.STATE_DOWNLOADING;

	@Override
	public boolean action(Context ctx, 
			                final ViewHolder viewHolder, 
			                final BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return false;
		}
		
		DownloadManager.getInstance().pauseNormalTask(downloadInfo.getIdentification(), new DownloadManager.ResultCallback() {
			@Override
			public void getResult(Object arg0) {
				if (arg0 == null || !(arg0 instanceof Boolean)) {
					return;
				}
				if (((Boolean) arg0).booleanValue()) {
					downloadInfo.setState(downloadInfo.getPauseState());
					
					viewHolder.progress.setVisibility(View.VISIBLE);
					viewHolder.state.setVisibility(View.VISIBLE);
					
					viewHolder.setFunButtonContent(R.string.common_button_continue);
					viewHolder.state.setText(R.string.myphone_download_parse);
				}
			}
		});
		
		return true;
	}

	@Override
	public void initView(ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}
		
		viewHolder.progress.setVisibility(View.VISIBLE);
		viewHolder.state.setVisibility(View.VISIBLE);
		
		final int progress = downloadInfo.progress;
		if (downloadInfo.totalSize == null) {
			downloadInfo.totalSize = "0.0MB" ;
		}
		
		StringBuffer desc = new StringBuffer("");
		desc.append(downloadInfo.downloadSize);
		desc.append("/").append(downloadInfo.totalSize);
		viewHolder.desc.setText(desc.toString());
		viewHolder.state.setText(progress + "%");

		viewHolder.progress.setProgress(progress);
		viewHolder.setFunButtonContent(R.string.myphone_download_parse);
	}

	@Override
	public int getState() {
		return state ;
	}
}
