package com.bitants.launcherdev.widget;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import com.bitants.launcher.R;
import com.bitants.launcherdev.datamodel.Global;
import com.bitants.launcherdev.launcher.LauncherSettings;

/**
 * 非标小部件信息
 */
public class LauncherWidgetInfo extends LauncherItemInfo {

	/**
	 * 系统小部件
	 */
	public static final int TYPE_SYSTEM = 1000;

	/**
	 * 桌面集成小部件
	 */
	public static final int TYPE_INSIDE = TYPE_SYSTEM + 1;

	/**
	 * 外部小部件
	 */
	public static final int TYPE_OUTSIDE = TYPE_SYSTEM + 2;
	
	/**
	 * 点心标准小部件
	 */
	public static final int TYPE_DXWIDGET = TYPE_SYSTEM + 3;
	
	/**
	 *  动态小部件
	 */
	public static final int TYPE_DYNAMIC = TYPE_SYSTEM + 4;

	/**
	 * 下载更多小部件
	 */
	public static final int TYPE_DOWNLOAD_MORE = TYPE_SYSTEM + 5;

	/**
	 *  系统小部件类别
	 */
	public static final int TYPE_SYSTEM_CATEGORY = TYPE_SYSTEM + 6;
	
	/**
	 *  系统小部件显示更多类别
	 */
	public static final int TYPE_WIDGET_MORE = TYPE_SYSTEM + 7;
	
	/**
	 *  91小部件显示更多类别
	 */
	public static final int TYPE_WIDGET_CATEGORY_MORE = TYPE_SYSTEM + 8;
	
	
	/**
	 * 包名
	 */
	protected String packageName;

	/**
	 * 类名
	 */
	protected String className;

	/**
	 * 布局文件名称
	 */
	private String layoutResName;
	
	
	/**
	 * 判断是否已经安装
	 */
	private boolean isInstalled;

	/**
	 * 是否存在新的版本
	 */
	private boolean hasNewVersion;

	/**
	 * 预览图资源名称
	 */
	private String previewImageResName;

	/**
	 * 标题资源名称
	 */
	private String titleResName;
	
	/**
	 * 预览图标题
	 */
	private String previewTitle = "";

	/**
	 * 版本号
	 */
	private int versionCode;
	
	/**
	 * 预览图资源名称
	 */
	private int previewImageResInt = -1;
	
	/**
	 * 第一次添加到小部件列表 时魔镜桌面的版本号
	 */
	private int launcherVersionCode;
	
	
	private boolean isShownNew = false;
	
	/**
	 * 图标资源名称
	 */
	private String iconResName;
	
	/**
	 * 图标
	 */
	private Drawable icon;
	

	private ComponentName configure;
		
	/**
	 * 编辑模式下点击添加小部件时弹出的界面
	 */
	protected Class<?> mJumpActivity;

	public Drawable getIcon() {
		if (icon == null) {
			icon = Global.getApplicationContext().getPackageManager().getDefaultActivityIcon();
		}
		return icon;
	}
	
	public String getIconResName() {
		return iconResName;
	}

	public void setIconResName(String iconResName) {
		this.iconResName = iconResName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getLayoutResName() {
		return layoutResName;
	}

	public void setLayoutResName(String layoutResName) {
		this.layoutResName = layoutResName;
	}

	public boolean isHasNewVersion() {
		return hasNewVersion;
	}

	public void setHasNewVersion(boolean hasNewVersion) {
		this.hasNewVersion = hasNewVersion;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getPreviewImageResName() {
		return previewImageResName;
	}

	public void setPreviewImageResName(String previewImageResName) {
		this.previewImageResName = previewImageResName;
	}

	public String getTitleResName() {
		return titleResName;
	}

	public void setTitleResName(String titleResName) {
		this.titleResName = titleResName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getPreviewTitle() {
		return previewTitle;
	}

	public void setPreviewTitle(String previewTitle) {
		this.previewTitle = previewTitle;
	}
	
	public int getLauncherVersionCode() {
		return launcherVersionCode;
	}

	public void setLauncherVersionCode(int launcherVersionCode) {
		this.launcherVersionCode = launcherVersionCode;
	}
	
	
	public boolean isShownNew(){
		return isShownNew;
	}
	
	public void setIsShownNew(boolean flag){
		isShownNew = flag;
	}
	
	public int getPreviewImageResInt() {
		return previewImageResInt;
	}
	
	public void setPreviewImageResInt(int previewImageResInt) {
		this.previewImageResInt = previewImageResInt;
	}
	
	public Class<?> getJumpActivity() {
		return mJumpActivity;
	}
	
	public LauncherWidgetInfo() {
		this.catagoryNo = CATAGORY_CUSTOM_WIDGET;
		this.itemType = LauncherSettings.Favorites.ITEM_TYPE_PANDA_WIDGET;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + type + spanY * prime + spanX;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LauncherWidgetInfo other = (LauncherWidgetInfo) obj;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (type != other.type)
			return false;
		if (spanX != other.spanX)
			return false;
		if (spanY != other.spanY)
			return false;
		return true;
	}
	
	
	/**
	 * 系统小部件
	 * 
	 * @return
	 */
	public static LauncherWidgetInfo makeSystemWidget() {
		LauncherWidgetInfo info = new LauncherWidgetInfo();
		info.type = TYPE_SYSTEM;
		info.title = Global.getApplicationContext().getString(R.string.launcher_edit_mode_widget_sys);
		info.previewImageResInt = R.drawable.widget_default_preview_img;
		return info;
	}
	
	/**
	 * 1x1 一键清理小部件
	 * 
	 * @return
	 */
	public static LauncherWidgetInfo makeMemoryClean1X1WidgetInfo() {
		LauncherWidgetInfo info = new LauncherWidgetInfo();
		info.catagoryNo = LauncherItemInfo.CATAGORY_CUSTOM_WIDGET;
		info.type = TYPE_INSIDE;
		info.title ="一键清理"; //Global.getApplicationContext().getString(R.string.widget_memoryclean)+"(1x1)";
//		info.previewImage = Global.getApplicationContext().getResources().getDrawable(R.drawable.widget_panda_memory_clean);
		info.previewImageResInt = R.drawable.widget_default_preview_img;//1;R.drawable.widget_panda_memory_clean_1x1;
		info.tip = "1x1";
		info.layoutResName = "widget_memory_clean_1x1";
		info.packageName = Global.getApplicationContext().getPackageName();
		info.spanX = 1;
		info.spanY = 1;
		info.launcherVersionCode = 5000;
		info.setInstalled(true);
		return info;
	}
	
	
	/**
	 *  时钟天气小部件
	 * 
	 * @return
	 */
	public static LauncherWidgetInfo makeWeatherClockWidgetInfo() {
		LauncherWidgetInfo info = new LauncherWidgetInfo();
		info.catagoryNo = LauncherItemInfo.CATAGORY_CUSTOM_WIDGET;
		info.type = TYPE_INSIDE;
		info.title ="时钟天气";
		info.previewImageResInt = R.drawable.widget_default_preview_img;//1;R.drawable.widget_panda_memory_clean_1x1;
		info.tip = "1x1";
		info.layoutResName = "widget_weather_clock_1x1";
		info.packageName = Global.getApplicationContext().getPackageName();
		info.spanX = 1;
		info.spanY = 1;
		info.launcherVersionCode = 5000;
		info.setInstalled(true);
		return info;
	}
	
	
	/**
	 *  时钟天气小部件
	 * 
	 * @return
	 */
	public static LauncherWidgetInfo makeThemeWidgetInfo() {
		LauncherWidgetInfo info = new LauncherWidgetInfo();
		info.catagoryNo = LauncherItemInfo.CATAGORY_CUSTOM_WIDGET;
		info.type = TYPE_INSIDE;
		info.title ="美化手机";
		info.previewImageResInt = R.drawable.widget_default_preview_img;//1;R.drawable.widget_panda_memory_clean_1x1;
		info.tip = "1x1";
		info.layoutResName = "widget_theme_1x1";
		info.packageName = Global.getApplicationContext().getPackageName();
		info.spanX = 1;
		info.spanY = 1;
		info.launcherVersionCode = 5000;
		info.setInstalled(true);
		return info;
	}
	
	
	public ComponentName getConfigure() {
		return configure;
	}

	public void setConfigure(ComponentName configure) {
		this.configure = configure;
	}
	
	/**
	 * 是否是内置动态插件
	 * @return
	 */
	public boolean isInsideDynamicWidget(){
		return this.type == TYPE_DYNAMIC;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	public void setShownNew(boolean isShownNew) {
		this.isShownNew = isShownNew;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	/**
	 * 是否是可安装插件
	 *  @param widgetInfo
	 */
	public static boolean isOutSideWidget(LauncherWidgetInfo widgetInfo){
		if(widgetInfo == null){
			return false;
		}
		return widgetInfo.type == LauncherWidgetInfo.TYPE_OUTSIDE;
	}
	
	/**
	 * 是否91插件如百度插件等
	 *  @param widgetInfo
	 *  @return
	 */
	public static boolean is91Widget(LauncherWidgetInfo widgetInfo){
		if(widgetInfo == null){
			return false;
		}
		return widgetInfo.type == LauncherWidgetInfo.TYPE_INSIDE;
	}
	
	/**
	 * 是否通过安装插件或是天气类型
	 *  @param widgetInfo
	 *  @return
	 */
	public static boolean isNormalInstalledWidget(LauncherWidgetInfo widgetInfo){
		if(widgetInfo == null){
			return false;
		}
		return widgetInfo.type == LauncherWidgetInfo.TYPE_OUTSIDE;
	}
	
	/**
	 * 是否动态桌面内置插件(放置在SD卡上)
	 *  @param widgetInfo
	 *  @return
	 */
	public static boolean isDynamicWidget(LauncherWidgetInfo widgetInfo){
		if(widgetInfo == null){
			return false;
		}
		return widgetInfo.type == LauncherWidgetInfo.TYPE_DYNAMIC;
	}
	
}
