package com.bitants.launcherdev.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.config.preference.SettingsConstants;

/**
 * 动态设置
 */
public class DynamicSettingsActivity extends BasePreferenceActivity implements OnPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SettingsConstants.SETTINGS_NAME);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preference_activity_title);
		TextView textView = (TextView)findViewById(R.id.preference_activity_title_text);
	    textView.setText(R.string.launcher_settings_dynamic);
		findViewById(R.id.preference_activity_title_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		addPreferencesFromResource(R.xml.preferences_dynamic);
		initView();
	}

	private void initView() {
		CheckBoxPreference dWall = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_DYNAMIC_WALLPAPER);
		dWall.setOnPreferenceChangeListener(this);
		dWall.setChecked(SettingsPreference.getInstance().isDynamicWallpaper());
		
		CheckBoxPreference dWeather = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_DYNAMIC_WEATHER);
		dWeather.setOnPreferenceChangeListener(this);
		dWeather.setChecked(SettingsPreference.getInstance().isDynamicWeather());
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference,Object newValue) {
		if (SettingsConstantsEx.SETTING_DYNAMIC_WALLPAPER.equals(preference.getKey())) {
			SettingsPreference.getInstance().setDynamicWallpaper((Boolean)newValue);
		} else if (SettingsConstantsEx.SETTING_DYNAMIC_WEATHER.equals(preference.getKey())) {
			SettingsPreference.getInstance().setDynamicWeather((Boolean)newValue);
		} 
		return true;
	}
	
}
