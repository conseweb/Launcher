package com.nd.hilauncherdev.kitset.Analytics;

/**
 * 功能统计事件ID集合
 */
public class AnalyticsConstant {

	public static final int INVALID = -1;	
	/**
	 * 2014.1.13变更统计ID
	 * maolinnan_350804
	 * 格式：14年+01月+两位系列编号+两位编号
	 */
	
	//------------------------------------------------------------------------------------  ↓点心桌面活跃用户比对版本使用↓  -------------------------------------------------------------------\\
	/**
	 * 桌面异常
	 */
	public static final int LAUNCHER_EXCEPTION_NEW_DX_SPE = 30000001;
	/**
	 * 长按桌面空白处>壁纸滚动、长按桌面空白处>效果
	 * 壁纸滚动 标签值：1
	 * 图标排列 标签值：2  
	 * 图标设置 标签值：3
	 * 应用名设置 标签值：4
	 * 图标4行4列 标签值：5
	 * 图标5行4列 标签值：6
	 * 图标5行5列 标签值：7
	 * 隐藏图标背板 标签值：8
	 * 大图标 标签值：9
	 * 小图标 标签值：10
	 * 隐藏应用名 标签值：11
	 * 应用名大小 标签值：12 
	 * 应用名颜色 标签值：13
	 */
	public static final int WORKESPACE_EDIT_SETTING = 30000002;
	
	/**
	 * 桌面美化  功能ID：
	 * 进入桌面美化 标签值：1 
	 * 进入主题详情页面 标签值：2 
	 * 主题点击下载 标签值：3 
	 * 主题下载成功 标签值：4 
	 * 主题应用成功 标签值：5
	 * 进入壁纸详情页面 标签值：6 
	 * 壁纸点击下载 标签值：7 
	 * 壁纸下载成功 标签值：8 
	 * 壁纸应用成功 标签值：9  
	 * 进入锁屏详情页面 标签值：10 
	 * 锁屏点击下载 标签值：11 
	 * 锁屏下载成功 标签值：12 
	 * 锁屏应用成功 标签值：13 
	 * 
	 * 
	 */
	public static final int WORKESPACE_BEAUTIFY_SETTING = 30000003;
	
	/**
	 * 桌面美化网络情况  功能ID
	 * 无网 标签值：1 
	 * 2G 标签值：2  
	 * 3G 标签值：3 
	 * 4G 标签值：4 
	 * wifi 标签值：5
	 */
	public static final int WORKESPACE_NETWORK_SETTING = 30000004;
	
	
	/**
	 * 匣子里的交互  功能ID：  
	 * 
	 * 将app放入底部已有文件夹 标签值：1
	 * 合并文件夹 标签值：2 
	 * 
	 */
	public static final int APP_DRAWER_SETTING = 30000005;
	
	//------------------------------------------------------------------------------------  ↑点心桌面活跃用户比对版本使用↑  -------------------------------------------------------------------\\
	
	
	/**
	 * 00 一键系列
	 */
	//一键清理(1-1x1; 2-2x1)
	public static final int WIDGET_ONE_KEY_CLEAN = 14070001;
	//一键换壁纸
	public static final int WIDGET_WALLPAPER_ONE_CLICK_CHANGE = 14070002;
	
	/**
	 * 01 快捷系列
	 */
	//应用列表-（1-快捷）
	public static final int SWITCH_DRAWER = 14070101;
	//点心小秘-（1-快捷; 2-桌面设置）
	public static final int SWITCH_FEEDBACK = 14070102;
	//手电筒-（1-快捷）
	public static final int SWITCH_FLASHLIGHT = 14070103;
	//二维码-（1-快捷）
	public static final int SWITCH_QR_CODE = 14070104;
	//一键关屏-（1-快捷）
	public static final int SWITCH_ONE_KEY_LOCK_SCREEN = 14070105;
	//一键截图-（1-快捷）
	public static final int SWITCH_ONE_KEY_SCREENSHOT = 14070106;
	//一键关机-（1-快捷）
	public static final int SWITCH_ONE_KEY_SHUT_DOWN = 14070107;
	//一键重启-（1-快捷）
	public static final int SWITCH_ONE_KEY_RESTART = 14070108;
	//静音-（1-快捷）
	public static final int SWITCH_MUTE = 14070109;
	//振动-（1-快捷）
	public static final int SWITCH_VIBRATE = 14070110;
	//蓝牙-（1-快捷）
	public static final int SWITCH_BLUETOOTH = 14070111;
	//屏幕亮度（1-快捷）
	public static final int SWITCH_BRIGHT = 14070112;
	//GPS-（1-快捷）
	public static final int SWITCH_GPS = 14070113;
	//wifi-（1-快捷）
	public static final int SWITCH_WIFI = 14070114;	
	//移动网络-（1-快捷）
	public static final int SWITCH_GPRS = 14070115;
	//通知栏更多
	public static final int SWITCH_MORE = 14110116;
	//飞行模式
	public static final int SWITCH_FLY_MODE = 14110117;
	//自动转屏
	public static final int SWITCH_AUTOROTATE = 14110118;
	
	/**
	 * 02 设置系列
	 */
	//系统设置使用情况-（1-WIFI; 2-移动数据; 3-网络共享; 4-蓝牙; 5-屏幕安全; 6-亮度; 7-铃声; 8-语言输入; 9-应用; 10-存储; 11-电池; 12-USB调试; 13-更多系统设置）
	public static final int SET_SYS_SETTINGS_USE_SITUATION = 14070201;
	
	/**
	 * 03 菜单系列
	 */
	//桌面菜单（1-menu; 2-上滑手势）
	public static final int MENU_LAUNCHER_MENU = 14070301;
	//桌面菜单使用情况（1-添加; 2-屏幕管理; 3-桌面美化; 4-下载管理; 5-桌面设置; 6-系统设置）
	public static final int MENU_LAUNCHER_MENU_USAGE = 14070302;
	//匣子菜单（1-图标; 2-menu键 ; 3-上滑手势）
	public static final int MENU_DRAWER_MENU = 14070303;
	//长按快捷菜单（1-换图标; 2-重命名; 3-详情; 4-缩放; 5-删除; 6-换风格; 7-隐藏; 8-卸载）
	public static final int MENU_LAUNCHER_SCREEN_ICON_LONGPRESS = 14070304;
	//匣子菜单使用情况（1-图标排序; 2-新建文件夹; 3-轻应用中心; 4-应用管理; 5-外观设置; 6-隐藏程序）
	public static final int MENU_DRAWER_USAGE = 14070305;
	
	/**
	 * 04 动作
	 */
	//捏屏幕-屏幕预览
	public static final int ACTION_LAUNCHER_MENU_NIE_SCREEN_PREVIEW = 14070401;
	//长按图标（1-移动位置; 2-交换位置; 3-删除; 4-卸载; 5-跨屏移动; 6-合成文件夹）
//	public static final int ACTION_ICON_LONGPRESS = 14070402;
	//长按插件（1-移动位置; 2-交换位置; 3-删除; 4-跨屏移动）
//	public static final int ACTION_WEIGHT_LONGPRESS = 14070403;
	//长按桌面
	public static final int LAUNCHER_SCREEN_EDIT_MODE = 14070404;
	//手势打开隐藏文件夹
	public static final int ACTION_OPEN_HIDE_FOLDER_GUSTURE = 14110405;
	
	/**
	 * 05 编辑模式
	 */
	//编辑模式-小部件（1-系统小部件; 2-更多小部件; 3-添加小部件）
	public static final int LAUNCHER_SCREEN_EDIT_WEIGHT = 14010501;
	//编辑模式-应用（1-10：各选项）
	public static final int LAUNCHER_SCREEN_EDIT_APP = 14010502;
	//编辑模式-个性化（1-主题; 2-壁纸; 3-字体; 4-铃声）
	public static final int LAUNCHER_SCREEN_EDIT_INDIVIDUATION = 14010503;
	//编辑模式-特效(1-指尖特效; 2-滑屏特效)
	public static final int LAUNCHER_SCREEN_EDIT_EFFECT = 14010504;
	//编辑模式-个性化-主题-本地主题
	public static final int LAUNCHER_SCREEN_EDIT_LOCAL_THEME = 14020505;
	//编辑模式-个性化-主题-在线主题
	public static final int LAUNCHER_SCREEN_EDIT_ONLINE_THEME = 14020506;
	//编辑模式-壁纸（jj-壁纸滚动，gd-更多壁纸，xg-壁纸效果，xc-相册，yy-直接应用壁纸）
	public static final int LAUNCHER_SCREEN_EDIT_INDIVIDUATION_WALLPAPER = 14042507;
	//编辑模式-自定义壁纸
	public static final int DX_CUSTOM_WALLPAPER_CLICK = 15010501;
	//生成自定义壁纸统计（标签0 代表生成单屏自定义壁纸，标签1 代表生成滚屏自定义壁纸）
	public static final int DX_CUSTOM_WALLPAPER_CREATE = 15010502;
	
	
	/**
	 * 06 DOCK栏
	 */
	//左右滑动
//	public static final int DOCK_BAR_SCROLL_SCREEN = 14070601;
	//图标替换（具体位置）
	public static final int DOCK_BAR_ICON_EXCHANGE = 14070602;
	//默认图标使用情况（1-15：具体位置，除了匣子按钮）
//	public static final int DOCK_BAR_ICON_USAGE = 14070603;
	//自定义图标使用情况（1-15：具体位置）
//	public static final int DOCK_BAR_CUSTOM_ICON_USAGE = 14070604;
	
	/**
	 * 07 搜索 
	 */
	//进入搜索（1-0屏; 2-百度插件）
	public static final int SEARCH_INTO_SEARCH = 14070701;
//	//热词刷新
//	public static final int SEARCH_HOT_WORD_REFRESH = 14070702;
//	//搜索请求（1-本机; 2-网页; 3-应用）
//	public static final int  SEARCH_REQUEST = 14070703;
//	//语音搜索（1-0屏; 2-详情）
//	public static final int  SEARCH_VOICE_SEARCH = 14070704;
//	//二维码（1-0屏; 2-百度插件）
//	public static final int  SEARCH_QR_CODE = 14070705;
//	
	/**
	 * 08 热词 
	 */
	//热词点击（1-0屏; 2-百度插件; 3-搜索界面）
//	public static final int HOT_WORD_CLICK = 14070801;
	
	/**
	 * 09 0屏
	 */
	//0屏导航的开启率
	public static final int CLOSE_ZERO_SCREEN = 14070901;
	//0屏导航的流量分发效果
	public static final int NAVIGATION_CATEGORY_CLICK = 14070902;
	/**
	 * 10 匣子 
	 */
	//匣子搜索
	public static final int DRAWER_SEARCH = 14071001;
	//匣子搜索热词（1-点击; 2-刷新）
	public static final int DRAWER_SEARCH_HOT_WORD = 14071002;
	//匣子搜索请求（1-本地; 2-应用中心）
	public static final int DRAWER_SEARCH_REQUEST = 14071003;
	//匣子搜索结果（1-定位; 2-打开）
	public static final int DRAWER_SEARCH_RESULT = 14071004;
	//匣子搜索语音
	public static final int DRAWER_SEARCH_VOICE = 14071005;
	//匣子最近安装
	public static final int DRAWER_INSTALLED_RECENTLY = 14071006;
	//匣子最近打开
	public static final int DRAWER_RUNNING_TASK_RECENTLY = 14071007;
	//匣子编辑状态(1-图标; 2-文件夹)
	public static final int DRAWER_ENTER_EDIT_MODE = 14071008;
	//匣子添加到桌面(1-图标; 2-文件夹)
	public static final int DRAWER_ADD_ITEM_DESKTOP = 14071009;
	//匣子文件夹操作(1-重叠app生成文件夹; 2-app移入文件夹; 3-app移出文件夹; 4-点击文件夹+操作app,5-删除文件夹)
	public static final int DRAWER_FOLDER_OPERATE = 14071010;
	//匣子app移动位置
	public static final int DRAWER_ICON_POS_ADJUST = 14071011;
	
	/**
	 * 11 小部件和快捷等桌面图标
	 */
	//从编辑模式中添加小部件到桌面（标签区别具体小部件）
	public static final int WIDGET_ADD_TO_LAUNCHER_FROM_LAUNCHER_EDTOR = 14071101;
	//桌面搬家（1-桌面图标; 2-桌面设置）
	public static final int LAUNCHER_MOVE_HOUSE = 14071102;
	//桌面最近安装	
	public static final int FOLDER_RECENT_INSTALL = 14071103;
	//桌面最近打开
	public static final int FOLDER_RECENT_OPEN = 14071104;
    //应用商店（1-桌面图标; 2-匣子底部图标）
	public static final int APP_STORE_ENTRY = 14071105;
	//执行桌面搬家成功统计
	public static final int EVENT_MOVE_DESK_ITEM_MOVE_SUCCESS = 14071106;	
	
	/**
	 * 12 轻应用中心
	 */
	//打开轻应用中心 标签1 ， 2  1: 进入，2:数据返回
	public static final int WEBAPP_OPEN_CENTER = 14071201;
	//打开某个轻应用
	public static final int WEBAPP_OPEN_ITEM = 14071203;
	//添加某个轻应用
	public static final int WEBAPP_ADD_ITEM = 14071204;
	
	/**
	 * 13 桌面美化商城
	 */
	/**
	 * 个性化资源点击下载
	 * 91主题 (1) 非91主题(2) 壁纸 (3) 铃声(4) 锁屏(5)
	 */
	public static final int THEME_SHOP_CLICK_DOWNLOAD = 14071301;
	/**
	 * 个性化资源开始下载
	 * 91主题 (1) 非91主题(2) 壁纸 (3) 铃声(4) 锁屏(5)
	 */
	public static final int THEME_SHOP_BEGIN_DOWNLOAD = 14071302;
	/**
	 * 个性化资源下载成功
	 * 91主题 (1) 非91主题(2) 壁纸 (3) 铃声(4) 锁屏(5)
	 */
	public static final int THEME_SHOP_DOWNLOAD_SUCCESS = 14071303;
	/**
	 * 个性化资源应用成功
	 * 91主题 (1) 非91主题(2) 壁纸 (3) 铃声-来电铃声(4) 铃声-短信铃声(5) 铃声-闹钟铃声(6) 铃声-联系人铃声(7) 锁屏(8)
	 */
	public static final int THEME_SHOP_APPLY_SUCCESS = 14071304;
		
	//我的主题(wf-wifi;3g-移动网络;no-无网络)
	public static final int MYPHONE_THEME_SHOP = 14071305;
	
	/**
	 * 15其他
	 */
	//桌面异常次数,自553弃用改用#LAUNCHER_EXCEPTION_NEW	
	public static final int LAUNCHER_EXCEPTION = 14071501;
	//安卓智能升级（xz-开始下载安卓市场，az-成功安装安卓市场，sj-跳转至安卓市场详情界面升级）
	public static final int SMART_UPDATE_HIMARKET = 14071502;
	//桌面自升级
	public static final int LAUNCHER_UPDATE_NOTIFI = 14071503;
	//桌面自升级窗口（az-安卓市场智能升级，zs-91助手智能升级，pt-普通升级，zd-我知道了）
	public static final int LAUNCHER_UPDATE_DIALOG = 14071504;
	//在应用其它主题	
	public static final int APPLY_OTHER_THEME = 14071505;
	//在使用其它滑屏特效	
	public static final int APPLY_OTHER_SCREEN_EFFECT = 14071506;
	//设置为默认桌面	
	public static final int SET_AS_DEFAULT_LAUNCHER = 14071507;
	//使用多个桌面，v5.6.1去除该项统计
	public static final int INSTALL_MULTI_LAUNCHER = 14071508;
	//没下载过主题	
	public static final int NEVER_DOWNLOAD_THEME = 14071509;
	//没下载过壁纸	
	public static final int NEVER_DOWNLOAD_WALLPAPER = 14071510;
	//没下载过铃声
	public static final int NEVER_DOWNLOAD_RING = 14071511;
	//没使用黄历天气小部件
	public static final int NOT_USE_WEATHER_WIDGET = 14071512;
	//没使用百度小部件
	public static final int NOT_USE_BAIDU_WIDGET = 14071513;
	//桌面上的图标数
	public static final int APP_ON_WORKSPACE_COUNT = 14071514;
	//默认屏的图标数
	public static final int APP_ON_DEFAULT_SCREEN_COUNT = 14071515;
	//没使用一键清理
	public static final int NOT_USE_CLEANER_WIDGET = 14071516;
	//没使用一键换壁纸
	public static final int NOT_USE_WALLPAPER_WIDGET = 14071517;
	//统计用户安装过的桌面(360-360桌面，go-go桌面,bd-百度桌面,dx-点心桌面,mx-魔秀桌面,qu-Q立方,mi-MIUI)
	public static final int INSTALLED_OTHER_LAUNCHER = 14071518;
	//新增用户心跳统计
	public static final int CHANNEL_ACTIVITY_REALTIME_NEW =  14071519;	
	//升级用户心跳统计
	public static final int CHANNEL_ACTIVITY_REALTIME_OLD =  14071520;
	//网址导航首页(1-点击浏览器，2-成功打开91网址)
	public static final int URL_NAVIGATION_HOME_PAGE =  14071521;
	//未设置点心桌面为默认桌面	
	public static final int NO_SET_AS_DEFAULT_LAUNCHER = 14071522;
	//桌面启动
	public static final int LAUNCHER_STARTUP =  14071523;
	//桌面第三方桌面名单,v5.6.1屏蔽 
	public static final int OTHER_LAUNCHERS_NAMES = 14071524;
	//桌面第三方桌面个数，v5.6.1屏蔽
	public static final int OTHER_LAUNCHERS_COUNTS = 14071525;
	//统计通过通知栏点击，进入桌面自升级界面的次数
	public static final int LAUNCHER_UPDATES_FROM_NOTIFICATION = 14071526;
	//统计匣子数据迁移是否成功，1-成功，2-失败 ,该id弃用
	//	public static final int MIGRATE_DRAWER_DB = 14071527;
	//统计匣子数据迁移是否成功，1-成功，2-失败 
	public static final int MIGRATE_DRAWER_DB = 14071534;
	//统计桌面数据迁移是否成功，1-成功，2-失败 
	public static final int MIGRATE_LAUNCHER_DB = 14071528;

	//统计匣子数据迁移发生异常 
	public static final int MIGRATE_EXCEPTION_DRAWER_DB = 14071529;
	//统计桌面数据迁移发生异常 
	public static final int MIGRATE_EXCEPTION_LAUNCHER_DB = 14071530;
	//下载成功数,5.7.2弃用
	public static final int DOWNLOAD_COUNT =  14071531;
	//升级用户数,5.5.1验证版本已用，5.5.2弃用
	public static final int UPGRADE_USERS_COUNT = 14071532;
	//sp升级异常
	public static final int MIGRATE_LAUNCHER_SP = 14071533;
	//升级用户数,5.5.2启用
	public static final int UPGRADE_USERS_COUNT_552 = 14071535;
	//91助手智能升级（xz-开始下载91助手，az-成功安装91助手，sj-跳转至91助手详情界面升级）
	public static final int SMART_UPDATE_HI91MARKET = 14071536;
	//异常报告id,自553启用
	public static final int LAUNCHER_EXCEPTION_NEW = 14071537;
	//桌面异常数据监控,自553启用
	public static final int LAUNCHER_EXCEPTION_DETECT = 14071538;
	
	//匣子换背景（1 进入匣子背景设置 ，2 随主题更换 ，3 透明背景 ，4 从相册中选一张 5 相册图片应用成功 ）
	public static final int DRAWER_BACKGROUND_SET = 14071539;
	
	//零屏是否关闭 5.6.1 添加测试活跃用户量
	public static final int IS_ZERO_SCREEN_CLOSED = 14071540;
	//是否是默认桌面5.6.1 添加测试活跃用户量
	public static final int IS_DEFAULT_LAUNCHER = 14071541;
	
	//通知中心推送
	public static final int NOTIFICATION_MESSAGE_PUSH = 15011501;	
	//通知中心点击量
	public static final int NOTIFICATION_MESSAGE_PUSH_CLICK = 15011502;
	//桌面推送图标点击量
	public static final int LAUNCHER_CLICK_PUSH_ICON = 15011503;

	//桌面是否包含点心标准小部件
	public static final int WORKSPACE_HAS_DX_WIDGET = 15011504;
	
	//91天气点击分布功能统计ID（1-时间；2-日历；3-天气简版；4-天气通版；5-城市；6-点击下载天气通版）
	public static final int weather_click_distribute = 15031501;
	//天气SDK内下载黄历天气客户端点击位置功能统计ID（1-关于界面；2-主界面天气指数；3-主界面宜忌；4-主界面农历；5-主界面多日天气图表；6-主界面主界面右下角更多图标；7-主界面多日温度图表）
	public static final int weather_download_huangli = 15031502;
	//天气SDK内下载黄历天气客户端安装成功打点 
	public static final int weather_install_huangli = 15031503;
	
	/**
	 * 桌面推荐应用状态 16
	 */
	//显示下载对话框
	public static final int APP_RECOMMEND_GUIDE = 14071601;
	//点击对话框下载按钮
	public static final int APP_RECOMMEND_CLICK_DOWNLOAD = 14071602;
	//点击对话框安装按钮
	public static final int APP_RECOMMEND_CLICK_INSTALL = 14071603;
	//安装完成
	public static final int APP_RECOMMEND_INSTALLED = 14071604;
	
	/**
	 * 应用分发 99
	 */
	//引导下载
	public static final int APP_DISTRIBUTE_GUIDE = 14079901;
	//点击下载
	public static final int APP_DISTRIBUTE_CLICK_DOWNLOAD = 14079902;
	//开始下载
	public static final int APP_DISTRIBUTE_START_DOWNLOAD = 14079903;
	//标签区分入口
	//桌面icon推荐
	public static final String APP_DISTRIBUTE_TAG_ZMTJ = "zmtj";
	//插件推荐
	public static final String APP_DISTRIBUTE_TAG_CJTJ = "cjtj";
	//推荐文件夹
	public static final String APP_DISTRIBUTE_TAG_TJWJJ = "tjwjj";
	//匣子热词
	public static final String APP_DISTRIBUTE_TAG_XZRC = "xzrc";
	//应用中心
	public static final String APP_DISTRIBUTE_TAG_YYZX = "yyzx";
	//应用升级
	public static final String APP_DISTRIBUTE_TAG_YYSJ = "yysj";
	//匣子工具文件夹
	public static final String APP_DISTRIBUTE_TAG_GJWJJ = "gjwjj";
	//匣子游戏文件夹
	public static final String APP_DISTRIBUTE_TAG_YXWJJ = "yxwjj";
	
	/** t9搜索 17  */
	//t9综合， 打开，搜索成功，搜索失败(没有打开应用即推出) v5.7.1弃用
//	public static final int T9_SEARCH_SUMMERY = 14111701;
	
	//跳转至应用商店
	public static final int T9_SEARCH_APP_STORE = 14111702;
	
	//t9综合， 打开，搜索成功，搜索失败(没有打开应用即推出) v5.7.1启用
	public static final int T9_SEARCH_SUMMERY_NEW = 14121703;
	
	//t9入口分布
	public static final int T9_ENTERENCES = 15011701;
	
	/**
	 * 18 隐藏应用
	 */
    //隐藏应用使用人数（匣子设置中进入+手势打开），fromGesture - 手势进入，fromdraw - 匣子菜单进入
    public static final int APP_HIDE_ENTER = 15011801;
    //隐藏应用加密，setPwd - 设置密码，findPwd - 找回密码，setMIBAO - 设置密保
    public static final int APP_HIDE_ENCRIPT = 15011802;
	
	
	/**
	 * 20 点心小秘
	 */
	//打开点心小秘
	public static final int OPEN_DIANXIN_XIAOMI = 14112001;
	
	//界面图标点击情况(标签值为名字)
	public static final int DIANXIN_XIAOMI_ICON_CLICK = 14112002;
	
	/**
	 * 自动换壁纸 功能id 14080001
	 * (1)进入换壁纸设置  (2)手动开启换壁纸 (3)手动关闭换壁纸
	 */
	public static final int AUTO_CHANGE_WALLPAPER = 14080001;
	/**
	 * 自动换壁纸 换壁纸频率 14080002
	 * 0---9
	 * 依次为一天 12 6 2 1 小时 30 15 10 5 1分钟
	 */
	public static final int AUTO_CHANGE_WALLPAPER_ALARM_TIME = 14080002;
	
	/**
	 * 点心轮播插件每日统计
	 * 
	 * 标签0代表桌面上没有轮播插件
	 * 
	 * 标签1代表桌面上有轮播插件
	 */
	public static final int DX_CAROUSEL_WIDGET = 14121501;
	
	/**
	 * 点心轮播插件点击统计
	 * 
	 * 标签101代表APK
	 * 标签102代表主题
	 * 标签103代表专辑
	 * 标签104代表壁纸
	 * 标签105代表锁屏
	 * 
	 * 标签其他数字代表未定义类型
	 */
	public static final int DX_CAROUSEL_ITEM_CLICK = 14121901;
	
	
	
}
