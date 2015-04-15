package com.nd.launcherdev.webconnect.downloadmanage.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nd.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcher.R;
import com.nd.launcherdev.launcher.config.BaseConfig;

/**
 * 下载管理页面适配器
 * 
 * @author pdw
 * @date 2012-9-18 上午11:37:30
 */
public class DownloadManageAdapter {
		
	public static class ViewHolder {
		public ImageView icon;
		public TextView title;
		public TextView desc;
		public TextView state;
		public ProgressBar progress;
		public TextView funBtn;
		public ImageView funBtnImage;
		
		public void setFunButtonContent(int textId) {
			try {
				 String text = BaseConfig.getApplicationContext().getResources().getString(textId);
				 setFunButtonContent(text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void setFunButtonContent(String text) {
			if (funBtn == null || funBtnImage == null || text == null)
				return;
			
			String strSetting = BaseConfig.getApplicationContext().getResources().getString(R.string.common_button_set);
			if (strSetting != null && !strSetting.equals("") && strSetting.equals(text)) {
				funBtnImage.setVisibility(View.VISIBLE);
				funBtnImage.setImageResource(R.drawable.downloadmanager_btn_setting_selector);
				funBtn.setVisibility(View.GONE);
				funBtn.setText("");
			} else {
				funBtn.setVisibility(View.VISIBLE);
				funBtn.setText(text);
				funBtnImage.setVisibility(View.GONE);
			}
		}
	}
}
