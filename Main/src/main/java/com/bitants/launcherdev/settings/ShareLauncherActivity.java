package com.bitants.launcherdev.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.common.launcher.config.preference.SettingsConstants;

/**
 * 分享桌面
 */
public class ShareLauncherActivity extends BasePreferenceActivity implements OnPreferenceClickListener {

	private static final String KEY_GOOGLE_PLUS = "shareToGooglePlus";
	private static final String KEY_FACEBOOK = "shareToFacebook";
	private static final String KEY_TWITTER = "shareToTwitter";
	private static final String KEY_INSTAGRAM = "shareToInstagram";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SettingsConstants.SETTINGS_NAME);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preference_activity_title);
		TextView textView = (TextView)findViewById(R.id.preference_activity_title_text);
	    textView.setText(R.string.launcher_settings_shareLauncher);
		findViewById(R.id.preference_activity_title_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		addPreferencesFromResource(R.xml.preferences_share);
		initView();
	}

	private void initView() {
		findPreference(KEY_GOOGLE_PLUS).setOnPreferenceClickListener(this);
		findPreference(KEY_FACEBOOK).setOnPreferenceClickListener(this);
		findPreference(KEY_TWITTER).setOnPreferenceClickListener(this);
		findPreference(KEY_INSTAGRAM).setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
	    if (preference.getKey().equals(KEY_GOOGLE_PLUS)) {
	    	
	    } else if (preference.getKey().equals(KEY_FACEBOOK)) {
	    	
	    } else if (preference.getKey().equals(KEY_TWITTER)) {
	    	
	    } else if (preference.getKey().equals(KEY_INSTAGRAM)) {
	    	
	    } 
		return true;
	}
	
}
