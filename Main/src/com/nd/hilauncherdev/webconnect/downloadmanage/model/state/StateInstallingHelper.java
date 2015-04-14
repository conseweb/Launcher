package com.nd.hilauncherdev.webconnect.downloadmanage.model.state;

import android.content.Context;
import android.view.View;

import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcher.R;

/**
 * 正在安装状态，应用于静默安装
 * 
 * @author zhuchenghua
 * 
 */
public class StateInstallingHelper implements IDownloadStateHelper {

	private final int state = DownloadState.INSTALL_STATE_INSTALLING;

	@Override
	public boolean action(Context ctx, 
			                ViewHolder viewHolder, 
			                BaseDownloadInfo downloadInfo) {
		return true;
	}

	@Override
	public void initView(ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return;
		}
		
		viewHolder.desc.setText(R.string.download_finished);
		viewHolder.setFunButtonContent(R.string.app_market_installing);

		viewHolder.progress.setVisibility(View.GONE);
		viewHolder.state.setVisibility(View.INVISIBLE);
	}

	@Override
	public int getState() {
		return state;
	}

}
