package com.nd.launcherdev.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bitants.launcher.R;

/**
 * 桌面设置主界面
 * @author wgm 
 */
public class HomeSettingsActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_settings_view);
		initView();
	}
	
	
	private void initView() {
		findViewById(R.id.gestureSetting).setOnClickListener(this);
		findViewById(R.id.privacySetting).setOnClickListener(this);
		findViewById(R.id.cardSetting).setOnClickListener(this);
		findViewById(R.id.dynamicSetting).setOnClickListener(this);
		findViewById(R.id.moreSetting).setOnClickListener(this);
		findViewById(R.id.scoreSetting).setOnClickListener(this);
		findViewById(R.id.shareLauncherSetting).setOnClickListener(this);
		findViewById(R.id.aboutLauncherSetting).setOnClickListener(this);
	}


	@Override
	public void onClick(View view) {
		/**手势设置*/
		if (view.getId() == R.id.gestureSetting) {			
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), GestureSettingsActivity.class);
			startActivity(intent);
		}
		/**隐私设置*/
		else if (view.getId() == R.id.privacySetting) {
			
		}
		/**卡片设置*/
        else if (view.getId() == R.id.cardSetting) {
        	Intent intent = new Intent();
			intent.setClass(getApplicationContext(), CardScreenSettingsActivity.class);
			startActivity(intent);
		}
		/**动态设置*/
        else if (view.getId() == R.id.dynamicSetting) {
        	Intent intent = new Intent();
			intent.setClass(getApplicationContext(), DynamicSettingsActivity.class);
			startActivity(intent);
		}
		/**更多设置*/
        else if (view.getId() == R.id.moreSetting) {
        	Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MoreSettingsActivity.class);
			startActivity(intent);
		}
		/**给我们评分*/
        else if (view.getId() == R.id.scoreSetting) {
        	
		}
		/**分享桌面*/
        else if (view.getId() == R.id.shareLauncherSetting) {
        	Intent intent = new Intent();
			intent.setClass(getApplicationContext(), ShareLauncherActivity.class);
			startActivity(intent);
		}
		/**关于桌面*/
        else if (view.getId() == R.id.aboutLauncherSetting) {
        	Intent intent = new Intent();
			intent.setClass(this, AboutLauncherActivity.class);
			startActivity(intent);
		}
	}
}
