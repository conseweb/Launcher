package com.bitants.launcherdev.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.launcherdev.datamodel.Global;
import com.bitants.launcherdev.kitset.util.HiLauncherEXUtil;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.config.preference.SettingsConstants;

/**
 * 关于桌面
 */
public class AboutLauncherActivity extends BasePreferenceActivity implements OnPreferenceClickListener {

	private static final String ABOUT_MOBO_SITE = "aboutMoboSite";
	private static final String ABOUT_MOBO_FB = "aboutMoboFacebook";
	private static final String ABOUT_MOBO_GP = "aboutMoboGooglePlus";
	private static final String ABOUT_MOBO_EMAIL = "aboutMoboEmail";
	private static final String ABOUT_MOBO_VERSION = "aboutMoboVersion";
	private static final String ABOUT_MOBO_RESTART = "aboutMoboRestart";
	private static final String ABOUT_MOBO_UPGRADE_CK = "aboutMoboUpgradeCheck";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SettingsConstants.SETTINGS_NAME);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preference_activity_title);
		TextView textView = (TextView)findViewById(R.id.preference_activity_title_text);
	    textView.setText(R.string.launcher_settings_aboutLauncher);
		findViewById(R.id.preference_activity_title_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		addPreferencesFromResource(R.xml.preferences_about);
		initView();
	}

	private void initView() {
		findPreference(ABOUT_MOBO_SITE).setOnPreferenceClickListener(this);
		findPreference(ABOUT_MOBO_FB).setOnPreferenceClickListener(this);
		findPreference(ABOUT_MOBO_GP).setOnPreferenceClickListener(this);
		findPreference(ABOUT_MOBO_EMAIL).setOnPreferenceClickListener(this);
		Preference versionPf = findPreference(ABOUT_MOBO_VERSION);
		versionPf.setSummary(TelephoneUtil.getVersionName(this));
		findPreference(ABOUT_MOBO_RESTART).setOnPreferenceClickListener(this);
		findPreference(ABOUT_MOBO_UPGRADE_CK).setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
	    if (preference.getKey().equals(ABOUT_MOBO_SITE)) {
	    	SystemUtil.openPage(this, preference.getSummary().toString());
	    } else if (preference.getKey().equals(ABOUT_MOBO_FB)) {
	    	
	    } else if (preference.getKey().equals(ABOUT_MOBO_GP)) {
	    	
	    } else if (preference.getKey().equals(ABOUT_MOBO_EMAIL)) {
	    	final Uri uri = Uri.parse(HiLauncherEXUtil.MAILTO_EMAIL);
			Intent in = new Intent(Intent.ACTION_SENDTO,uri);
			in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    } else if (preference.getKey().equals(ABOUT_MOBO_RESTART)) {
	       finish();
	       Launcher launcher = Global.getLauncher();
	       if (launcher != null) {
	    	   launcher.restartMoboLauncher();
	       }
	    } else if (preference.getKey().equals(ABOUT_MOBO_UPGRADE_CK)) {
	    	
	    } 
		return true;
	}
}
