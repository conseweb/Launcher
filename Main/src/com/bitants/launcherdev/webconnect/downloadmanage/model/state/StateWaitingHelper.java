package com.bitants.launcherdev.webconnect.downloadmanage.model.state;

import android.content.Context;
import android.view.View;

import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.AbstractDownloadManager;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcher.R;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;

/** 
 * 等待状态 
 * 
 * @author pdw 
 * @version 
 * @date 2012-9-19 下午04:32:05 
 */
public class StateWaitingHelper implements IDownloadStateHelper {
	
	private final int state = DownloadState.STATE_WAITING;

	@Override
	public boolean action(Context ctx, 
			                final DownloadManageAdapter.ViewHolder viewHolder,
			                final BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return false;
		}
		
		DownloadManager.getInstance().pauseNormalTask(downloadInfo.getIdentification(), new AbstractDownloadManager.ResultCallback() {
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
	public void initView(DownloadManageAdapter.ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}
		
		StringBuffer sb = new StringBuffer(downloadInfo.downloadSize);
		sb.append("/").append(downloadInfo.totalSize);
		viewHolder.desc.setText(sb.toString());
		viewHolder.state.setText(R.string.download_waiting);
		viewHolder.progress.setProgress(downloadInfo.progress);
		viewHolder.setFunButtonContent(R.string.myphone_download_parse);
		
		viewHolder.state.setVisibility(View.VISIBLE);
		viewHolder.progress.setVisibility(View.VISIBLE);
		
	}

	@Override
	public int getState() {
		return state ;
	}

}
