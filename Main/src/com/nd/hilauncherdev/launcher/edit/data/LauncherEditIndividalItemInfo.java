package com.nd.hilauncherdev.launcher.edit.data;

public class LauncherEditIndividalItemInfo extends LauncherEditItemInfo{

	/**
	 * 主题
	 */
	public static final int TYPE_INDIVIDAL_THEME = 0;
	
	/**
	 * 壁纸
	 */
	public static final int TYPE_INDIVIDAL_WALLPAPER = 1;
	
	/**
	 * 字体
	 */
	public static final int TYPE_INDIVIDAL_FONT = 2;
	
	/**
	 * 铃声
	 */
	public static final int TYPE_INDIVIDAL_RING = 3;
	/**
	 * 情景布局样式
	 */
	public static final int TYPE_INDIVIDAL_HOLE_STYLE = 4;
	
	
//	public static LauncherEditIndividalItemInfo getTheme(Context context){
//		LauncherEditIndividalItemInfo  itemInfo = new LauncherEditIndividalItemInfo();
//		itemInfo.type = TYPE_INDIVIDAL_THEME;
//		itemInfo.title = context.getString(R.string.launcher_edit_theme);
//		itemInfo.icon = context.getResources().getDrawable(R.drawable.edit_mode_individal_theme_top);
//		return itemInfo;
//	}
//	
//	public static LauncherEditIndividalItemInfo getWallPaper(Context context){
//		LauncherEditIndividalItemInfo  itemInfo = new LauncherEditIndividalItemInfo();
//		itemInfo.type = TYPE_INDIVIDAL_WALLPAPER;
//		itemInfo.title = context.getString(R.string.launcher_edit_wallpaper);
//		itemInfo.icon = context.getResources().getDrawable(R.drawable.edit_mode_individal_wallpaper_top);
//		return itemInfo;
//	}
//	
//	public static LauncherEditIndividalItemInfo getFont(Context context){
//		LauncherEditIndividalItemInfo  itemInfo = new LauncherEditIndividalItemInfo();
//		itemInfo.type = TYPE_INDIVIDAL_FONT;
//		itemInfo.title = context.getString(R.string.launcher_edit_font);
//		itemInfo.icon = context.getResources().getDrawable(R.drawable.edit_mode_individal_font);
//		return itemInfo;
//	}
//	
//	public static LauncherEditIndividalItemInfo getRing(Context context){
//		LauncherEditIndividalItemInfo  itemInfo = new LauncherEditIndividalItemInfo();
//		itemInfo.type = TYPE_INDIVIDAL_RING;
//		itemInfo.title = context.getString(R.string.launcher_edit_ring);
//		itemInfo.icon = context.getResources().getDrawable(R.drawable.edit_mode_individal_ring_top);
//		return itemInfo;
//	}
//	
//	/**
//	 * 情景布局样式
//	 * @param context
//	 * @return
//	 */
//	public static LauncherEditIndividalItemInfo getHoleStyle(Context context){
//		LauncherEditIndividalItemInfo  itemInfo = new LauncherEditIndividalItemInfo();
//		itemInfo.type = TYPE_INDIVIDAL_HOLE_STYLE;
//		itemInfo.title = context.getString(R.string.scene_style_choose);
//		itemInfo.icon = context.getResources().getDrawable(R.drawable.edit_mode_individal_choose_style);
//		return itemInfo;
//	}
	
}
