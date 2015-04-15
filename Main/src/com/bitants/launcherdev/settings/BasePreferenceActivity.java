package com.bitants.launcherdev.settings;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.bitants.launcherdev.framework.view.prompt.PromptPreferenceAction;
import com.bitants.launcherdev.framework.view.prompt.PromptPreferenceAction;
import com.bitants.launcherdev.framework.view.prompt.PromptPreferenceAction;

/**
 * @author pdw
 * @date 2012-6-15 下午03:09:03 活动类都要从此类派生，负责托管
 */
public class BasePreferenceActivity extends PreferenceActivity implements OnPreferenceClickListener {
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		setPrompVelue(preference);
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		setPrompVelue(preference);
		return true;
	}

	public void setPrompVelue(Preference preference) {
		if (preference instanceof PromptPreferenceAction)
			((PromptPreferenceAction) preference).onAction();

	}
}
