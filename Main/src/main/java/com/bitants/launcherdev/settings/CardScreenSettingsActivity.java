package com.bitants.launcherdev.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.common.launcher.config.preference.SettingsConstants;

/**
 * 卡片屏设置
 */
public class CardScreenSettingsActivity extends BasePreferenceActivity 
      implements OnPreferenceChangeListener,OnPreferenceClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SettingsConstants.SETTINGS_NAME);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preference_activity_title);
		TextView textView = (TextView)findViewById(R.id.preference_activity_title_text);
	    textView.setText(R.string.launcher_settings_card);
		findViewById(R.id.preference_activity_title_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		addPreferencesFromResource(R.xml.preferences_cardscreen);
		initView();
	}

	private void initView() {
		CheckBoxPreference cardScreen = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_CARD_SCREEN);
		cardScreen.setOnPreferenceChangeListener(this);
		cardScreen.setChecked(SettingsPreference.getInstance().isCardScreen());
		
		CheckBoxPreference navi = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_NAVIGATION);
		navi.setOnPreferenceChangeListener(this);
		navi.setChecked(SettingsPreference.getInstance().isNavigation());
 		
		findPreference(SettingsConstantsEx.SETTING_CARD_NEWS).setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference,Object newValue) {
		if (SettingsConstantsEx.SETTING_CARD_SCREEN.equals(preference.getKey())) {
			SettingsPreference.getInstance().setCardScreen((Boolean)newValue);
		} else if (SettingsConstantsEx.SETTING_NAVIGATION.equals(preference.getKey())) {
			SettingsPreference.getInstance().setNavigation((Boolean)newValue);
		} 
		return true;
	}
	
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
	    if (preference.getKey().equals(SettingsConstantsEx.SETTING_CARD_NEWS)) {
	    	
	    } 
		return true;
	}

}
