package com.nd.hilauncherdev.webconnect.downloadmanage.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.filetype.IFileTypeHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadBroadcastExtra;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DxDownloadBroadcastExtra;
import com.bitants.launcher.R;

/**
 * 下载项view
 * 
 * @author pdw
 * 
 */
public class DownloadItemView extends LinearLayout {

	private final String TAG = "DownloadItemView";

	private TextView mDownloadProgressDesc;
	private TextView mDownloadState;
	private TextView mActionBtn;
	private ImageView mActionBtnImage;
	private ProgressBar mProgress;
	private ViewHolder mHolder = new ViewHolder();

	private BroadcastReceiver mReceiver;

	/**
	 * 静默安装监听
	 */
	private BroadcastReceiver mSilentInstallReceiver;

	private String mFormatDownload;

	public DownloadItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		mDownloadProgressDesc = (TextView) findViewById(R.id.app_item_progress_desc);
		mDownloadState = (TextView) findViewById(R.id.app_item_state);
		mProgress = (ProgressBar) findViewById(R.id.download_progress);
		mActionBtn = (TextView) findViewById(R.id.app_item_fun_btn);
		mActionBtnImage = (ImageView) findViewById(R.id.app_item_fun_btn_image);
		
		mHolder.funBtn = mActionBtn;
		mHolder.funBtnImage = mActionBtnImage;
	}

	private void unRegistReceiver(BroadcastReceiver receiver) {
		try {
			if (receiver != null) {
				mContext.unregisterReceiver(receiver);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		unRegistReceiver(mReceiver);
		unRegistReceiver(mSilentInstallReceiver);
		mReceiver = null;
		mSilentInstallReceiver = null;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		final Object obj = getTag(R.id.common_view_holder);
		if (!(obj instanceof BaseDownloadInfo))
			return;

		// 注册静默安装监听
		if (mSilentInstallReceiver == null) {
			mSilentInstallReceiver = new SilentInstallReceiver();
			IntentFilter silentInstallFilter = new IntentFilter(DownloadState.RECEIVER_APP_SILENT_INSTALL);
			mContext.registerReceiver(mSilentInstallReceiver, silentInstallFilter);
		}

		//final ApkDownloadInfo downloadInfo = (ApkDownloadInfo) obj;
		/*
		 * final int state = downloadInfo.getState() ; if (state ==
		 * DownloadState.STATE_INSTALLED || state ==
		 * DownloadState.STATE_FINISHED ) //已下载完成的不监听下载广播 return ;
		 */
		mFormatDownload = new StringBuffer("%s").append("/").append("%s").toString();
		
		if (mReceiver == null) {
			mReceiver = new UIRefreshReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(DxDownloadBroadcastExtra.ACTION_DOWNLOAD_STATE);
			mContext.registerReceiver(mReceiver, filter);
		}
	}

	/**
	 * 监听下载广播更新ui
	 */
	class UIRefreshReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			freshItemState(intent);
		}
	}

	/**
	 * 静默安装的监听器
	 * 
	 * @author zhuchenghua
	 */
	private class SilentInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Object obj = getTag(R.id.common_view_holder);
			if (!(obj instanceof BaseDownloadInfo))
				return;
			BaseDownloadInfo downloadInfo = (BaseDownloadInfo) obj;
			if (downloadInfo == null || (downloadInfo.getState() != DownloadState.STATE_FINISHED && downloadInfo.getState() != DownloadState.INSTALL_STATE_INSTALLING))
				return;

			String packageName = intent.getStringExtra(DownloadState.EXTRA_APP_INSTALL_PACAKGE_NAME);
			if (TextUtils.isEmpty(packageName))
				return;

			try {

				if (packageName.equals(downloadInfo.getPacakgeName(mContext))) {
					int installState = intent.getIntExtra(DownloadState.EXTRA_APP_INSTALL_STATE, -1);
					switch (installState) {
					// 安装失败
					case DownloadState.INSTALL_STATE_INSTALL_FAILED:
						downloadInfo.setState(downloadInfo.getFinishedUninstalled());
						mHolder.setFunButtonContent(R.string.common_button_install);
						break;

					// 正在安装
					case DownloadState.INSTALL_STATE_INSTALLING:
						mHolder.setFunButtonContent(R.string.app_market_installing);
						downloadInfo.setState(downloadInfo.getInstallingState());
						break;
					}

				}// end if

			} catch (Exception e) {

				Log.w(TAG, "SilentInstallReceiver expose error!", e);
			}

		}// end onReceive

	}// end SilentInstallReceiver

	/**
	 * 刷新进度，状态
	 */
	private void freshItemState(Intent intent) {
		final String identification = intent.getStringExtra(DownloadBroadcastExtra.EXTRA_IDENTIFICATION);
		if (identification == null) {
			return;
		}
		Object obj = getTag(R.id.common_view_holder);
		if (!(obj instanceof BaseDownloadInfo)) {
			return;
		}
		final BaseDownloadInfo info = (BaseDownloadInfo) obj;
		if (info.getIdentification() == null || !identification.equals(info.getIdentification())) {
			return;
		}
			
		final String additionInfo = intent.getStringExtra(DownloadBroadcastExtra.EXTRA_ADDITION);
		if (additionInfo != null) {
			info.setAdditionInfo(additionInfo);
			return;
		}
		
		final int progress = intent.getIntExtra(DownloadBroadcastExtra.EXTRA_PROGRESS, 0);
		final int state = intent.getIntExtra(DownloadBroadcastExtra.EXTRA_STATE, DownloadState.STATE_NONE);
		final String downloadSize = intent.getStringExtra(DownloadBroadcastExtra.EXTRA_DOWNLOAD_SIZE);
		final String totalSize = intent.getStringExtra(DownloadBroadcastExtra.EXTRA_TOTAL_SIZE);
			
		info.progress = progress;
		if (downloadSize != null)
			info.downloadSize = downloadSize;
		if (totalSize != null)
			info.totalSize = totalSize;
			
		if (state != DownloadState.STATE_FAILED) {
			mProgress.setProgress(progress);
			mDownloadState.setText(progress + "%");
			mDownloadProgressDesc.setText(String.format(mFormatDownload, info.downloadSize,info.totalSize));
		}
			
		if (progress == 100 && state == DownloadState.STATE_FINISHED) {
			info.setState(info.getFinishedUninstalled());
			mDownloadProgressDesc.setText(R.string.download_finished);
			mProgress.setVisibility(View.GONE);
			mDownloadState.setVisibility(View.INVISIBLE);
//			mActionBtn.setText(R.string.common_button_install);
			IFileTypeHelper helper = FileType.fromId(info.getFileType()).getHelper();
			mHolder.setFunButtonContent(null != helper ? helper.getItemTextWhenFinished(info) : "");
		}
		if (state == DownloadState.STATE_DOWNLOADING) {
			mProgress.setVisibility(View.VISIBLE);
			mDownloadState.setVisibility(View.VISIBLE);

			mHolder.setFunButtonContent(R.string.myphone_download_parse);
			info.setState(info.getDownloadingState());
		}
		if (state == DownloadState.STATE_PAUSE || state == DownloadState.STATE_FAILED) {
			mProgress.setVisibility(View.VISIBLE);
			mDownloadState.setVisibility(View.VISIBLE);

			mDownloadState.setText(R.string.myphone_download_parse);
			mHolder.setFunButtonContent(R.string.common_button_continue);
			info.setState(info.getPauseState());
		}
		if (state == DownloadState.STATE_WAITING) {
			mProgress.setVisibility(View.VISIBLE);
			mDownloadState.setVisibility(View.VISIBLE);
			
			mDownloadState.setText(R.string.download_waiting);
			mHolder.setFunButtonContent(R.string.myphone_download_parse);
			info.setState(info.getWaitingState());
		}
	}
}
