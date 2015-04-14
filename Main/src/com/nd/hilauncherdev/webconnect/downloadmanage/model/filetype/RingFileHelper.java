package com.nd.hilauncherdev.webconnect.downloadmanage.model.filetype;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.nd.hilauncherdev.datamodel.CommonGlobal;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageAdapter.ViewHolder;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcher.R;

public class RingFileHelper implements IFileTypeHelper {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void onClickWhenFinished(Context ctx, ViewHolder viewHolder, BaseDownloadInfo downloadInfo) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(ctx, "com.baidu.dx.personalize.PersonalizeActivityGroup"));
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("PERSONALIZE_TAB", 2);
		intent.putExtra("SECOND_TAB", 3);
		intent.putExtra("invoke_from_launcher_editor", true);
		ctx.startActivity(intent);
	}

	@Override
	public String getItemTextWhenFinished(BaseDownloadInfo downloadInfo) {
		return CommonGlobal.getApplicationContext().getResources().getString(R.string.common_button_set);
	}
	
	@Override
	public void onDownloadCompleted(final Context ctx, final BaseDownloadInfo info, String file) {
		ThreadUtil.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String content = info.getAdditionInfo().get("data");
					String[] arrayOfString = content.split(",");
					String path = info.getSavedDir() + info.getSavedName();
					MediaPlayer player = MediaPlayer.create(ctx, Uri.parse("file://" + path));
					long duration = 0;
					try {
						if(player != null){
							duration = player.getDuration();
						}
					} catch (Exception e) {
						
					}finally{
						if(player != null){
							player.release();
						}
					}
					
					StringBuffer sb = new StringBuffer("");
					sb.append(arrayOfString[0] + "," + changeSpliter(arrayOfString[1]) + 
							"," + duration + "," + changeSpliter(arrayOfString[3]) + 
							"," + changeSpliter(arrayOfString[4]) + "," + changeSpliter(path) + "\r\n");
					
					append(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
	}

	@Override
	public String getItemDefIconPath(BaseDownloadInfo downloadInfo) {
		return "drawable:downloadmanager_ring_icon";
	}
	
	
	private void append(String content) {
		try {
			final String BASE_DIR = BaseConfig.getBaseDir() + "/myphone/myring";
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(BASE_DIR + "/detail.dat" , true), "GBK");
			out.write(content);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String changeSpliter(String paramString) {
		String str;
		if (paramString != null)
			str = paramString.replaceAll("," , "，");
		else
			str = paramString;
		return str;
	}
	
	@Override
	public boolean fileExists(BaseDownloadInfo downloadInfo) {
		if (downloadInfo != null) {
			return downloadInfo.fileExists();
		}
		return false;
	}
}
