package com.bitants.launcherdev.launcher.edit.data;

import com.bitants.launcherdev.theme.data.ThemeGlobal;


/**
 * 编辑模式特效数据集类型
 *
 */
public class LauncherEditEffectItemInfo extends LauncherEditItemInfo {
	
	public static final int SLIDE_EFFECT = 0;
	public static final int PARTICLE_EFFECT = 1;
	public static final int FILTER_EFFECT = 2;
	public static final int PARTICLE_EFFECT_SHOW_MORE = 3;
	//代表特效类型 默认滑屏特效
	private int category = SLIDE_EFFECT;

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	
	/**
	 * 指尖特效所属主题ID
	 */
	private String themeId = ThemeGlobal.DEFAULT_THEME_ID;

	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}
	
	public boolean isSlideEffect() {
		return category == SLIDE_EFFECT;
	}
	
	public boolean isParticleEffect() {
		return category == PARTICLE_EFFECT;
	}
	
	public boolean isDefaultTheme() {
		return ThemeGlobal.DEFAULT_THEME_ID.equals(themeId);
	}

}
