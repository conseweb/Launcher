package com.bitants.launcherdev.analysis;

/**
 * description: <br/>
 * author: Michael<br/>
 * data: 2014/8/28<br/>
 */
public class AppAnalysisConstant {

	public static final String APP_ANALYSIS_KEY_FLAG = "app_analysis_key_flag";
	
    /**
     * APP 统计状态
     */
    /**
     * 应用分发通用下载源：助手
     */
    public static final int DOWNLOAD_FROM_91ASSIST_POOL = 0;

    /**
     * 应用分发-开始下载
     */
    public static final int APP_DISTRIBUTE_STEP_START = 1;

    /**
     * 应用分发-浏览
     */
    public static final int APP_DISTRIBUTE_STEP_BROWSE = 2;

    /**
     * 应用分发-下载成功
     */
    public static final int APP_DISTRIBUTE_STEP_DOWNLOAD_SUCCESS = 4;

    /**
     * 应用分发-安装成功
     */
    public static final int APP_DISTRIBUTE_STEP_INSTALLED_SUCCESS = 5;

    /**
     * 应用分发-安装失败
     */
    public static final int APP_DISTRIBUTE_STEP_INSTALLED_FAILED = 6;

    /**
     * 应用分发-下载失败
     */
    public static final int APP_DISTRIBUTE_STEP_DOWNLOAD_FAILED = 8;

    /**
     * 应用分发-激活
     */
    public static final int APP_DISTRIBUTE_STEP_ACTIVE = 10;


    /**
     * 资源类型
     */
    public static final int TYPE_SOFT = 1;                 //软件
    public static final int TYPE_THEME = 2;                //主题
    public static final int TYPE_RING = 3;                 //铃声
    public static final int TYPE_WALLPAPER = 4;            //壁纸
    public static final int TYPE_THEME_FONT = 24;          //主题资源
    public static final int TYPE_RESOURCE_MODULE = 50;     //资源模块


    /**
     * 渠道 服务端根据上传的sp来生成对应的placeId 代表不同的位置
     * 统一请求下载包的sp
     */
    public static final int SP_ASSIST_91 = 0;                   //从91助手下载的包
    public static final int SP_LAUNCHER_ICON_APP = 110;         //桌面icon推荐
    public static final int SP_LAUNCHER_WIDGET_APP = 111;       //桌面预置插件推荐
    public static final int SP_LAUNCHER_FOLDER_APP = 103;       //桌面文件夹
    public static final int SP_NAV_HOTWORD_BROSWER_APP = 112;   //导航或热词推荐浏览器
    public static final int SP_DOCK_APP = 113;                  //dock栏推荐
    public static final int SP_DRAWER_TOOL_FOLDER = 101;        //匣子工具文件夹
    public static final int SP_DRAWER_GAME_FOLDER = 102;        //匣子游戏文件夹
    public static final int SP_T9_HOTWORD = 114;                //t9搜索热词
    public static final int SP_LAUNCHER_FUNC_APP = 115;         //桌面功能推荐，应用升级推荐百度手机卫士、root推荐等
    public static final int SP_THEME_APP = 116;                 //同步换肤,91锁屏、91通讯录
    public static final int SP_WIFI_DOWNLOAD_APP =117;          //WIFI预下载
    public static final int SP_INTELLIJ_UPGRADE_RECOMMEND_APP =118;  //智能升级
    public static final int SP_DRAWER_UPGRADE_FODLER = 119;     //匣子应用升级文件夹
    public static final int SP_UPDATE_BIND =120;                //升级捆绑

    /**
     * 由服务端来入库数据 sp保留
     * 现在各桌面推荐列表统一，下载的量都会统计到魔镜桌面上
     */
    public static final int SP_LAUNCHER_UNINSTALL_RECOMMEND_APP = 98;    //桌面卸载推荐


}
