package com.bitants.launcherdev.datamodel.db;


public class DowningTaskItem {

	public static final int DownState_Downing = 1;
	public static final int DownState_Pause   = 2;
	public static final int DownState_Finish  = 3;
	public static final int DownState_Fail    = -1;
	
	public String themeName;
	public String themeID;
	public int startID; 			//状态栏消息ID
	public int isUpdate;
	public int state; 				//1 正在下载,2 暂停, 3完成 ,-1 下载失败 
	public int themeVersion = 1;
	public String downUrl;
	public String picUrl;			//预览图地址
	public String price;
	public String marketUrl;
	public String tmpFilePath;
	public long totalSize;
	public int progress;
	public String newThemeID;		//下载完成本地生成的新主题ID
	public int usedFlag = 0;  		//0未使用过	 
	public String cid = "-1"; 		//分类ID -1表示未分类	
}
