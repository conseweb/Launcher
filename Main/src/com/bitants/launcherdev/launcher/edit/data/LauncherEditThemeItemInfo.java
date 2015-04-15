package com.bitants.launcherdev.launcher.edit.data;

import android.content.Context;

import com.bitants.launcherdev.theme.data.ThemeType;
import com.bitants.launcherdev.theme.data.ThemeGlobal;
import com.bitants.launcherdev.theme.data.ThemeGlobal;
import com.bitants.launcherdev.theme.data.ThemeType;
import com.bitants.launcher.R;
import com.bitants.launcherdev.theme.data.ThemeType;

/**
 * 编辑模式主题数据集类型
 * 
 * @author Anson
 */
public class LauncherEditThemeItemInfo extends LauncherEditItemInfo {

	/**
	 * 主题
	 */
	public static final int TYPE_THEME = 0;

	/**
	 * 本地主题
	 */
	public static final int TYPE_MORE_THEME = 1;
	
	/**
	 * 在线主题
	 */
	public static final int TYPE_ONLINE_THEME = 3;

	/**
	 * 显示更多
	 */
	public static final int TYPE_THEME_SHOW_MORE = 2;
	
	/**
	 * 推荐主题
	 */
	public static final int TYPE_THEME_RECOMMEND = 5;

	/**
	 * 主题ID
	 */
	public String themeId = ThemeGlobal.DEFAULT_THEME_ID;
	
	/**
	 * 主题类型
	 */
	public int themeType = ThemeType.DEFAULT;
	
	/**
	 * apt主题资源相对路径
	 */
	public String aptPath = null;
	
	/**
	 * 服务器ID
	 */
	public String serverThemeId = "";
	
	public static LauncherEditThemeItemInfo makeOnLineThemeItem(Context context) {
		LauncherEditThemeItemInfo onLineThemeItemInfo = new LauncherEditThemeItemInfo();
		onLineThemeItemInfo.type = LauncherEditThemeItemInfo.TYPE_ONLINE_THEME;
		onLineThemeItemInfo.title = context.getResources().getString(R.string.launcher_edit_mode_theme_online);
		onLineThemeItemInfo.icon = context.getResources().getDrawable(R.drawable.edit_mode_theme_online);
		return onLineThemeItemInfo;
	}
}
