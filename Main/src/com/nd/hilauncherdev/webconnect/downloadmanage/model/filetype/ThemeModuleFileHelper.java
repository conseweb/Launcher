package com.nd.hilauncherdev.webconnect.downloadmanage.model.filetype;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.nd.hilauncherdev.datamodel.CommonGlobal;
import com.nd.hilauncherdev.theme.module.ModuleConstant;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.simon.android.pandahome2.R;

public class ThemeModuleFileHelper implements IFileTypeHelper{
	
	private static final long serialVersionUID = 1L;

	@Override
	public void onClickWhenFinished(Context context, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(context, "com.baidu.dx.personalize.PersonalizeActivityGroup"));
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("PERSONALIZE_TAB", 3);
		intent.putExtra("SECOND_TAB", 1);
		context.startActivity(intent);
	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return CommonGlobal.getApplicationContext().getResources().getString(R.string.common_button_set);
	}
	
	@Override
	public void onDownloadCompleted(Context context,
			BaseDownloadInfo downloadInfo, String file) {
		ThemeShopV6ThemeInstallAPI.sendInstallModule(context, file, downloadInfo.getIdentification(), FileTypeToModuleType(downloadInfo.getFileType()), downloadInfo);
	}

	@Override
	public String getItemDefIconPath(BaseDownloadInfo downloadInfo) {
		return "drawable:downloadmanager_theme_icon";
	}

	public static String FileTypeToModuleType(int fileTypeId) {
		FileType fileType = FileType.fromId(fileTypeId);
		String moduleKey = "";
		switch (fileType) {
		case FILE_LOCK:
			moduleKey = ModuleConstant.MODULE_LOCKSCREEN;
			break;
		case FILE_ICON:
			moduleKey = ModuleConstant.MODULE_ICONS;
			break;
		case FILE_INPUT:
			moduleKey = ModuleConstant.MODULE_BAIDU_INPUT;
			break;
		case FILE_SMS:
			moduleKey = ModuleConstant.MODULE_SMS;
			break;
		case FILE_WEATHER:
			moduleKey = ModuleConstant.MODULE_WEATHER;
			break;			
		default:
			break;
		}
		return moduleKey;
	}
	
	@Override
	public boolean fileExists(BaseDownloadInfo downloadInfo) {
		if (downloadInfo != null && downloadInfo.fileExists()) {
			return true;
		}
		return false;
	}
}
