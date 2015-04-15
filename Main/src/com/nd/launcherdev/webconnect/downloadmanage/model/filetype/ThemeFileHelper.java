package com.nd.launcherdev.webconnect.downloadmanage.model.filetype;

import java.util.HashMap;

import android.content.Context;

import com.nd.launcherdev.datamodel.CommonGlobal;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.theme.ThemeManagerFactory;
import com.nd.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcher.R;
import com.nd.launcherdev.datamodel.CommonGlobal;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.theme.ThemeManagerFactory;

/**
 * 主题操作
 * @author cfb
 *
 */
public class ThemeFileHelper implements IFileTypeHelper {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String AdditionKey = "RESID";
	
	@Override
	public void onClickWhenFinished(Context context, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		//检测是否安装成功
	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return CommonGlobal.getApplicationContext().getResources().getString(R.string.downloadmanager_apply);
	}
	
	@Override
	public void onDownloadCompleted(Context context,
			BaseDownloadInfo downloadInfo, String file) {
		//下载完成安装
		ThemeShopV6ThemeInstallAPI.sendInstallAPT(context, file, (downloadInfo.getIdentification()+"").replaceAll("theme", ""), downloadInfo);
	}

	@Override
	public String getItemDefIconPath(BaseDownloadInfo downloadInfo) {
		return "drawable:downloadmanager_theme_icon";
	}

	@Override
	public boolean fileExists(BaseDownloadInfo downloadInfo) {
		if (downloadInfo != null && downloadInfo.fileExists()) {
			return true;
		}
		
		HashMap<String, String> redIdMap = downloadInfo.getAdditionInfo();
		if (redIdMap==null){
			return false;
		}
		
		String themeId = redIdMap.get(AdditionKey);
		if (redIdMap!=null && !StringUtil.isEmpty(themeId)){
			return ThemeManagerFactory.getInstance().isThemeIdLikeExist(CommonGlobal.getApplicationContext(), themeId);
		}
		
		return false;
	}
}
