package com.bitants.launcherdev.menu.personal;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitants.launcherdev.datamodel.CommonApplicationWeakReferences;
import com.bitants.launcherdev.kitset.util.AndroidPackageUtils;
import com.bitants.launcherdev.kitset.util.ApkTools;
import com.bitants.launcherdev.kitset.util.BaseBitmapUtils;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.IFileTypeHelper;
import com.bitants.launcherdev.webconnect.downloadmanage.model.state.IDownloadStateHelper;
import com.bitants.launcherdev.webconnect.downloadmanage.model.state.StateHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcher.R;

public class BaseDownloadAdapter extends BaseAdapter {

	private Context ctx;

	private LayoutInflater inflater;

	private ArrayList<BaseDownloadInfo> downloadList = new ArrayList<BaseDownloadInfo>();

	private DownloadServerServiceConnection mDownloadService;

	public BaseDownloadAdapter(Context ctx, DownloadServerServiceConnection downloadService) {
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
		mDownloadService = downloadService;
	}

	@Override
	public int getCount() {
		return downloadList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position > downloadList.size() - 1)
			return null;
		return downloadList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		if (position < downloadList.size()) {
			downloadList.remove(position);
		}
	}

	public void remove(BaseDownloadInfo info) {
		downloadList.remove(info);
	}

	public void addAll(ArrayList<BaseDownloadInfo> list) {
		downloadList.clear();
		downloadList.addAll(list);
	}

	public void clear() {
		downloadList.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BaseDownloadInfo downloadInfo = downloadList.get(position);
		setDownloadState(ctx, downloadInfo);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.download_manage_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.app_item_image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.app_item_name);
			viewHolder.desc = (TextView) convertView.findViewById(R.id.app_item_progress_desc);
			viewHolder.state = (TextView) convertView.findViewById(R.id.app_item_state);
			viewHolder.progress = (ProgressBar) convertView.findViewById(R.id.download_progress);
			viewHolder.funBtn = (TextView) convertView.findViewById(R.id.app_item_fun_btn);
			viewHolder.funBtnImage = (ImageView) convertView.findViewById(R.id.app_item_fun_btn_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		convertView.setTag(R.id.common_view_holder, downloadInfo);
		Bitmap icon = null;
		if (downloadInfo.getIconPath() != null) {
			// icon = BitmapFactory.decodeFile(downloadInfo.iconPath);
			icon = getIcon(downloadInfo.getIconPath());
		}
		String ident = downloadInfo.getIdentification();
		Drawable drawable = ApkTools.loadApkIcon(ctx, ident);
		if (drawable != null) {
			icon = BaseBitmapUtils.createIconBitmap(drawable, ctx, false);
		}
		if (icon == null) {
			IFileTypeHelper helper = FileType.fromId(downloadInfo.getFileType()).getHelper();
			if (null != helper) {
				icon = getIcon(helper.getItemDefIconPath(downloadInfo));
			}
		}
		if (icon == null) {
			icon = CommonApplicationWeakReferences.getInstance().getDefAppIcon(ctx.getResources());
		}
		if (icon == null) {
			// 用于下载二维码扫描结果
			icon = ((BitmapDrawable) ctx.getResources().getDrawable(R.drawable.app_market_qrcode_scan_download_icon)).getBitmap();
		}
		viewHolder.icon.setImageBitmap(icon);
		viewHolder.title.setText(downloadInfo.getTitle());
		final IDownloadStateHelper stateHelper = StateHelper.fromState(downloadInfo.getState()).getHelper();
		if (stateHelper != null) {
			stateHelper.initView(viewHolder,downloadInfo);
		}
		
		final ViewHolder holder = viewHolder;
		View funLayout = convertView.findViewById(R.id.app_item_fun_layout);
		if (funLayout != null) {
			funLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final IDownloadStateHelper stateHelper2 = StateHelper.fromState(downloadInfo.getState()).getHelper();
					if (stateHelper2 != null) {
						stateHelper2.initView(holder,downloadInfo);
						stateHelper2.action(ctx, holder, downloadInfo);
					}
				}
			});
		}

		return convertView;
	}

	private Bitmap getIcon(String iconPath) {
		if (iconPath == null)
			return null;
		if (iconPath.startsWith("drawable:")) {
			Resources res = ctx.getResources();
			String name = iconPath.substring(iconPath.lastIndexOf(":") + 1);
			int resId = res.getIdentifier(name, "drawable", ctx.getPackageName());
			if (resId == 0)
				return null;
			return BitmapFactory.decodeResource(res, resId);
		/**
		 * hcf
		 * 
		} else if (iconPath.startsWith("table_recommend:")) {
			String packageName = iconPath.substring(iconPath.lastIndexOf(":") + 1);
			return RecommendAppTable.getRecommendIcon(ctx, packageName);
		*/
		} else {
			return BitmapFactory.decodeFile(iconPath);
		}
	}

	// TODO linqiang AppMarketUtil.setDownloadState(ctx, downloadInfo);
	/**
	 * 设置下载状态
	 * 
	 * @param downloadInfo
	 */
	private void setDownloadState(Context context, BaseDownloadInfo downloadInfo) {
		if (mDownloadService == null)
			return;

//		BaseDownloadInfo dlInfo = mDownloadService.getDownloadState(downloadInfo.getIdentification());
//		if (dlInfo != null) {
//			int dlState = dlInfo.getState();
//			downloadInfo.progress = dlInfo.progress;
		
		int state = downloadInfo.getState();
			switch (state) {
			case DownloadState.STATE_DOWNLOADING:
				downloadInfo.setState(downloadInfo.getDownloadingState());
				break;
			case DownloadState.STATE_FINISHED:
				downloadInfo.setState(downloadInfo.getFinishedUninstalled());
				break;
			case DownloadState.STATE_INSTALLED:
				downloadInfo.setState(downloadInfo.getFinishedInstalled());
				break;
			case DownloadState.STATE_PAUSE:
				downloadInfo.setState(downloadInfo.getPauseState());
				break;
			case DownloadState.STATE_WAITING:
				downloadInfo.setState(downloadInfo.getWaitingState());
				break;
			}
//		}

//		int state = downloadInfo.getState();

		// 下载完成、已安装、未下载状态下判断是否正在安装，是否已安装，如果是其他状态，保持不变
		switch (state) {
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_FINISHED:
		case DownloadState.STATE_INSTALLED:
		case DownloadState.INSTALL_STATE_INSTALLING:
		case DownloadState.STATE_NONE:

			if (downloadInfo.getFileType() == FileType.FILE_APK.getId()) {
				// 获取包信息
				String packageName = downloadInfo.getPacakgeName(context);
				int versionCode = downloadInfo.getVersionCode(context);

				if (mDownloadService.isApkInstalling(packageName)) {
					// 正在安装
					downloadInfo.setState(downloadInfo.getInstallingState());
				} else if (AndroidPackageUtils.isPkgInstalled(context, packageName, versionCode)) {
					// 已安装
					downloadInfo.setState(downloadInfo.getFinishedInstalled());
				} else {

				}
				/**
			} else if (downloadInfo.getFileType() == FileType.FILE_DYNAMIC_APK.getId()) {
				if (LauncherWidgetInfoManager.isDynamicWidgetReady(context, downloadInfo.getIdentification())) {
					downloadInfo.setState(downloadInfo.getFinishedInstalled());
				}
				*/
			}

			break;
		}
	}
}
