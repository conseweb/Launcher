package com.bitants.launcherdev.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.config.preference.SettingsConstants;

/**
 * 手势设置
 */
public class GestureSettingsActivity extends BasePreferenceActivity 
      implements OnPreferenceChangeListener,OnPreferenceClickListener {

	public static final String GESTURE_DBCLK_NONE = "setting_gesture_double_click_none";
	public static final String GESTURE_DBCLK_SCREEN_LOCK = "setting_gesture_double_click_screen_lock";
	public static final String GESTURE_DBCLK_SCREEN_FILL_WALL = "setting_gesture_double_click_screen_fill_wall";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SettingsConstants.SETTINGS_NAME);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preference_activity_title);
		TextView textView = (TextView)findViewById(R.id.preference_activity_title_text);
	    textView.setText(R.string.launcher_settings_gesture);
		findViewById(R.id.preference_activity_title_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		addPreferencesFromResource(R.xml.preferences_gesture);
		initView();
	}

	private void initView() {
		CheckBoxPreference gestureUp = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_GESTURE_UP);
		gestureUp.setOnPreferenceChangeListener(this);
		gestureUp.setChecked(SettingsPreference.getInstance().isLauncherGestureUp());
		
		CheckBoxPreference gestureDown = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_GESTURE_DOWN);
		gestureDown.setOnPreferenceChangeListener(this);
		gestureDown.setChecked(SettingsPreference.getInstance().isLauncherGestureDown());
		
		CheckBoxPreference gestureDbFingerOut = (CheckBoxPreference)findPreference(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_FINGER_OUT);
		gestureDbFingerOut.setOnPreferenceChangeListener(this);
		gestureDbFingerOut.setChecked(SettingsPreference.getInstance().isLauncherGestureDoubleFingerOut());
 		
		findPreference(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_CLICK).setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference,Object newValue) {
		if (SettingsConstantsEx.SETTING_GESTURE_UP.equals(preference.getKey())) {
			SettingsPreference.getInstance().setLauncherGestureUp((Boolean)newValue);
		} else if (SettingsConstantsEx.SETTING_GESTURE_DOWN.equals(preference.getKey())) {
			SettingsPreference.getInstance().setLauncherGestureDown((Boolean)newValue);
		} else if (SettingsConstantsEx.SETTING_GESTURE_DOUBLE_FINGER_OUT.equals(preference.getKey())) {
			SettingsPreference.getInstance().setLauncherGestureDoubleFingerOut((Boolean)newValue);
		}
		return true;
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
	    if (preference.getKey().equals(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_CLICK)) {
	    	Intent intent = new Intent();
	    	intent.putExtra("itemType","preferences_gesture_doubleclick");
			intent.setClass(getApplicationContext(), SettingsItemSelectActivity.class);
			startActivity(intent);
	    }
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Preference dbClkPre = findPreference(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_CLICK);
		int val = SettingsPreference.getInstance().getLauncherGestureDoubleClickVal();
		if (val == SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_NONE) {
			dbClkPre.setSummary(getString(R.string.launcher_settings_gesture_double_click_none));
		} else if (val == SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_SCREEN_LOCK) {
			dbClkPre.setSummary(getString(R.string.launcher_settings_gesture_double_click_screen_lock));
		} else if (val == SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_SCREEN_FILL_WALL) {
			dbClkPre.setSummary(getString(R.string.launcher_settings_gesture_double_click_screen_fill_wall));
		}
	}
	
}
