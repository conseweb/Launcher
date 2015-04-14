package com.nd.hilauncherdev.launcher.edit.data;

import android.content.Context;

import com.bitants.launcher.R;


/**
 * 编辑模式壁纸数据集类型
 *
 * @author Anson
 */
public class LauncherEditWallpaperItemInfo extends LauncherEditItemInfo {
	
	public static final String NO_FILTER_KEY = "no_filter";
	
	/**
	 * 壁纸滚动
	 */
	public static final int TYPE_WALLPAPER_SCROLL = 0;
	
	/**
	 * 在线壁纸
	 */
	public static final int TYPE_WALLPAPER_ONLINE = 1;
	
	/**
	 * 相册
	 */
	public static final int TYPE_PHOTO  = 2;
	
	/**
	 * 桌面壁纸
	 */
	public static final int TYPE_WALLPAPER_MOBO = 3;
	
	
	public String path;
	
	
	/**
	 * 壁纸滚动
	 * 
	 * @return
	 */
	public static LauncherEditWallpaperItemInfo makeWallpaperScrollItemInfo(Context context) {
		LauncherEditWallpaperItemInfo info = new LauncherEditWallpaperItemInfo();
		info.title = context.getString(R.string.launcher_edit_mode_wallpaper_scroll);
		info.icon = context.getResources().getDrawable(R.drawable.edit_mode_wallpaper_scroll);
		info.type = TYPE_WALLPAPER_SCROLL;
		return info;
	}
	

	/**
	 * 在线壁纸
	 * 
	 * @return
	 */
	public static LauncherEditWallpaperItemInfo makeWallpaperOnlineItemInfo(Context context) {
		LauncherEditWallpaperItemInfo info = new LauncherEditWallpaperItemInfo();
		info.title = context.getString(R.string.launcher_edit_mode_wallpaper_online);
		info.icon = context.getResources().getDrawable(R.drawable.edit_mode_wallpaper_online);
		info.type = TYPE_WALLPAPER_ONLINE;
		return info;
	}
	
	
	/**
	 * 相册
	 * @return
	 */
	public static LauncherEditWallpaperItemInfo makeWallpaperPhotoItemInfo(Context context) {
		LauncherEditWallpaperItemInfo info = new LauncherEditWallpaperItemInfo();
		info.title = context.getString(R.string.launcher_edit_mode_wallpaper_photo);
		info.icon = context.getResources().getDrawable(R.drawable.edit_mode_wallpaper_gallery);
		info.type = TYPE_PHOTO;
		return info;
	}
	

}
