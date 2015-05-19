package com.bitants.launcherdev.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.config.preference.SettingsConstants;

import java.util.HashMap;

public class SettingsItemSelectActivity extends BasePreferenceActivity {
	
	private static interface SettingsItemSelectAdapter {
		public int getPrefTitle();
		public int getPrefRes();
		public void initView(SettingsItemSelectActivity ctx);
		public void onPreferenceClick(Preference preference);
	}
	
	private static final HashMap<String,SettingsItemSelectAdapter> settingsAdapters = new HashMap<String,SettingsItemSelectAdapter>();
	static {
		settingsAdapters.put("preferences_gesture_doubleclick",new SettingsItemSelectAdapter() {
					@Override
					public int getPrefTitle() {
						return R.string.launcher_settings_gesture_double_click_title;
					}
					@Override
					public int getPrefRes() {
						return R.xml.preferences_gesture_doubleclick_items;
					}
					@Override
					public void initView(SettingsItemSelectActivity ctx) {
						int selectedVal = SettingsPreference.getInstance().getLauncherGestureDoubleClickVal();
						Preference pref = ctx.findPreference(GestureSettingsActivity.GESTURE_DBCLK_NONE);
						if (SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_NONE == selectedVal) {
							pref.setWidgetLayoutResource(R.layout.preferences_listpreferences_selected_btn);
						}
						pref.setOnPreferenceClickListener(ctx);
						pref = ctx.findPreference(GestureSettingsActivity.GESTURE_DBCLK_SCREEN_LOCK);
						if (SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_SCREEN_LOCK == selectedVal) {
							pref.setWidgetLayoutResource(R.layout.preferences_listpreferences_selected_btn);
						}
						pref.setOnPreferenceClickListener(ctx);
						pref = ctx.findPreference(GestureSettingsActivity.GESTURE_DBCLK_SCREEN_FILL_WALL);
						if (SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_SCREEN_FILL_WALL == selectedVal) {
							pref.setWidgetLayoutResource(R.layout.preferences_listpreferences_selected_btn);
						}
						pref.setOnPreferenceClickListener(ctx);
					}
					@Override
					public void onPreferenceClick(Preference preference) {
						if (preference.getKey().equals(GestureSettingsActivity.GESTURE_DBCLK_NONE)) {
							SettingsPreference.getInstance().setLauncherGestureDoubleClickVal(
									SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_NONE);
						}
						if (preference.getKey().equals(GestureSettingsActivity.GESTURE_DBCLK_SCREEN_LOCK)) {
							SettingsPreference.getInstance().setLauncherGestureDoubleClickVal(
									SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_SCREEN_LOCK);
						}
						if (preference.getKey().equals(GestureSettingsActivity.GESTURE_DBCLK_SCREEN_FILL_WALL)) {
							SettingsPreference.getInstance().setLauncherGestureDoubleClickVal(
									SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_SCREEN_FILL_WALL);
						}
					}
		    });
	}

	private SettingsItemSelectAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		adapter = settingsAdapters.get(getIntent().getStringExtra("itemType"));
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SettingsConstants.SETTINGS_NAME);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preference_activity_title);
		TextView textView = (TextView)findViewById(R.id.preference_activity_title_text);
	    textView.setText(adapter.getPrefTitle());
		findViewById(R.id.preference_activity_title_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		addPreferencesFromResource(adapter.getPrefRes());
		initView();
	}
	
	private void initView() {
		adapter.initView(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		adapter.onPreferenceClick(preference);	
		finish();
		return true;
	}
}
