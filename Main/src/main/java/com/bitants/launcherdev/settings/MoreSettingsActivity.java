package com.bitants.launcherdev.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.common.launcher.config.preference.SettingsConstants;

/**
 * 更多设置
 */
public class MoreSettingsActivity extends BasePreferenceActivity implements OnPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SettingsConstants.SETTINGS_NAME);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preference_activity_title);
		TextView textView = (TextView)findViewById(R.id.preference_activity_title_text);
	    textView.setText(R.string.launcher_settings_more);
		findViewById(R.id.preference_activity_title_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		addPreferencesFromResource(R.xml.preferences_more);
		initView();
	}

	private void initView() {
		CheckBoxPreference folderInnerRecom = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_FOLDER_INNER_RECOMMEND);
		folderInnerRecom.setOnPreferenceChangeListener(this);
		folderInnerRecom.setChecked(SettingsPreference.getInstance().isFolderInnerRecommend());
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference,Object newValue) {
		if (SettingsConstantsEx.SETTING_FOLDER_INNER_RECOMMEND.equals(preference.getKey())) {
			SettingsPreference.getInstance().setFolderInnerRecommend((Boolean)newValue);
		} 
		return true;
	}
	
}
