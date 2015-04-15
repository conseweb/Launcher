package com.bitants.launcherdev.webconnect.downloadmanage.model.filetype;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter;
import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcher.R;
import com.bitants.launcherdev.datamodel.CommonGlobal;
import com.bitants.launcherdev.webconnect.downloadmanage.activity.DownloadManageAdapter;

public class FontFileHelper implements IFileTypeHelper {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void onClickWhenFinished(Context ctx, DownloadManageAdapter.ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(ctx, "com.nd.hilauncherdev.myphone.myfont.activity.FontMainActivity"));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//原本打算通过字体id判断，但通过Identification解析出来的id与字体解压后font.json中的id不一致
		intent.putExtra("font_name", downloadInfo.getTitle());
		ctx.startActivity(intent);
	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return CommonGlobal.getApplicationContext().getResources().getString(R.string.common_button_set);
	}
	
	@Override
	public void onDownloadCompleted(Context ctx, BaseDownloadInfo downloadInfo, String file) {
	}

	@Override
	public String getItemDefIconPath(BaseDownloadInfo downloadInfo) {
		return "drawable:downloadmanager_font_icon";
	}

	@Override
	public boolean fileExists(BaseDownloadInfo downloadInfo) {
		if (downloadInfo != null) {
			return downloadInfo.fileExists();
		}
		return false;
	}
}
