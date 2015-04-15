package com.bitants.launcherdev.webconnect.downloadmanage.model.state;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcher.R;
import com.bitants.launcherdev.kitset.util.MessageUtils;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;

/** 
 * 暂停状态
 *  
 * @author pdw 
 * @version 
 * @date 2012-9-19 下午04:32:28 
 */
public class StatePauseHelper implements IDownloadStateHelper{
	
	private final int state = DownloadState.STATE_PAUSE;

	@Override
	public boolean action(Context ctx, 
			                final DownloadManageAdapter.ViewHolder viewHolder,
			                final BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return false;
		}
		
		if (!TelephoneUtil.isNetworkAvailable(ctx)) {
			MessageUtils.makeShortToast(ctx, R.string.frame_viewfacotry_net_break_text);
			return true;
		}
		
		DownloadManager.getInstance().continueNormalTask(downloadInfo.getIdentification(), new DownloadManager.ResultCallback() {
			@Override
			public void getResult(Object arg0) {
				if (arg0 == null || !(arg0 instanceof Boolean)) {
					return;
				}
				if (((Boolean) arg0).booleanValue()) {
					viewHolder.state.setText(R.string.download_waiting);
					viewHolder.progress.setProgress(downloadInfo.progress);
					viewHolder.setFunButtonContent(R.string.myphone_download_parse);
					
					viewHolder.progress.setVisibility(View.VISIBLE);
					viewHolder.state.setVisibility(View.VISIBLE);
					
					downloadInfo.setState(downloadInfo.getWaitingState());
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
		
		StringBuffer desc = new StringBuffer(downloadInfo.downloadSize);
		desc.append("/").append(downloadInfo.totalSize);
		viewHolder.desc.setText(desc.toString());
		Resources resource = viewHolder.state.getResources();
		viewHolder.state.setText(resource.getText(R.string.myphone_download_parse));
		viewHolder.progress.setProgress(downloadInfo.progress);
		viewHolder.setFunButtonContent(R.string.common_button_continue);
		
		viewHolder.state.setVisibility(View.VISIBLE);
		viewHolder.progress.setVisibility(View.VISIBLE);
		
	}

	@Override
	public int getState() {
		return state ;
	}

}
