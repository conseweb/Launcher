package com.bitants.launcherdev.settings;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.bitants.launcher.R;
/**
 * 将自定义分隔栏样式提升到CommonLibrary包。
 * <p>Title: PromptPreferenceCategory</p>
 * <p>Description: </p>
 * <p>Company: ND</p>
 */
public class PromptPreferenceCategory extends PreferenceCategory {
	private Context context = null;
	public PromptPreferenceCategory(Context context,AttributeSet attrs) {
		super(context,attrs);
		this.context = context;
	}
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		view.setBackgroundResource(R.drawable.prompt_preference_category);
        if (view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setTextColor(context.getResources().getColor(R.color.common_title_little_text_color));
            tv.setPadding(15, 5, 0, 5);
            tv.setTextSize(15);
        }
	}
}
