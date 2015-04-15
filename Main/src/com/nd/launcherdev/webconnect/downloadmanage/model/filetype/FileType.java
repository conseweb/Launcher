package com.nd.launcherdev.webconnect.downloadmanage.model.filetype;

import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;

public enum FileType {

	FILE_APK(BaseDownloadInfo.FILE_TYPE_APK, new ApkFileHelper()), 
	FILE_RING(1, new RingFileHelper()), 
	FILE_FONT(2, new FontFileHelper()),
	FILE_WALLPAPER(3, new WallpaperFileHelper()),
	FILE_DYNAMIC_APK(4, new DynamicApkFileHelper()),
	FILE_UPDATE_ZIP(5, new WifiAutoFileHelper()),
	FILE_THEME(6, new ThemeFileHelper()),
	FILE_LOCK(7, new ThemeModuleFileHelper()),
	FILE_ICON(8, new ThemeModuleFileHelper()),
	FILE_INPUT(9, new ThemeModuleFileHelper()),
	FILE_SMS(10, new ThemeModuleFileHelper()),
	FILE_WEATHER(11, new ThemeModuleFileHelper()),
	FILE_THEME_ZIP(12, new ZipFileHelper()),
	FILE_NONE(BaseDownloadInfo.FILE_TYPE_NONE, null);

	IFileTypeHelper mHelper = null;
	int mId = 0;

	FileType(int id, IFileTypeHelper helper) {
		mId = id;
		mHelper = helper;
	};

	public IFileTypeHelper getHelper() {
		return mHelper;
	}
		
	public int getId() {
		return mId;
	}
		
	public static FileType fromId(int id) {
		for (FileType f : FileType.values()) {
			if (id == f.getId()) {
				return f;
			}
		}
		return FILE_NONE;
	}
}
