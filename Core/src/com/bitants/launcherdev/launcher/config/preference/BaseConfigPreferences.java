package com.bitants.launcherdev.launcher.config.preference;

import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;

import android.content.Context;
import android.content.SharedPreferences;

public class BaseConfigPreferences {

	public static final String NAME = "configsp";

	private static BaseConfigPreferences baseConfig;
	private static SharedPreferences baseSP;
	
	private static final String KEY_LAUNCHER_ON_START_DAY_TIME = "launcher_on_start_day_time"; 
	private static final String KEY_LAUNCHER_ON_START_DAY_TIME_NOT_NETWORK = "launcher_on_start_day_time_not_network"; 
	private static final String HAS_SPRING_ADD_SCREEN = "has_spring_add_screen";
	
	/**
	 * 桌面布局的单元格大小
	 */
	private static final String CELLLAYOUT_CELLWIDTH = "cellLayout_cellWidth";
	private static final String CELLLAYOUT_CELLHEIGHT = "cellLayout_cellHeight";
	private static final String DOCKBAR_CELLWIDTH = "dockbar_cellWidth";
	private static final String DOCKBAR_CELLHEIGHT = "dockbar_cellHeight";
	
	/**
	 * 桌面oncreate的时间
	 */
	private static final String KEY_LAUNCHER_CREATE_TIME = "launcher_create_time"; 
	/**
	 * 第一次启动桌面时间
	 */
	private static final String KEY_FIRST_LAUNCH_TIME = "first_launch_time";
	
	/**
	 * 默认屏
	 */
	private static final String KEY_DEFAULT_SCREEN = "default_screen";
	
	/**
	 * 编辑是否被锁定
	 * */
	private static boolean editIsLock=false;
	private static final String KEY_SETTINGS_EDIT_IS_LOCK="key_settings_edit_is_lock";
	
	private static final String KEY_VERSION_FROM = "is_resident";
	//用户上次安装的桌面版本，用于处理数据库升降级
	private static final String KEY_LAST_VERSION_CODE = "last_version_code";
	
	protected BaseConfigPreferences() {
		baseSP = BaseConfig.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
		editIsLock=baseSP.getBoolean(KEY_SETTINGS_EDIT_IS_LOCK,false);
	}
	
	public SharedPreferences getBaseSP() {
		return baseSP;
	}

	public synchronized static BaseConfigPreferences getInstance() {
		if (baseConfig != null)
			return baseConfig;

		baseConfig = new BaseConfigPreferences();
		return baseConfig;
	}
	/**
	 * 设置桌面启动监听每日一次类型的回调时间
	 * @param createTime 回调时间
	 */
	public void setLauncherOnStartDayTime(long createTime){
		baseSP.edit().putLong(KEY_LAUNCHER_ON_START_DAY_TIME, createTime).commit();
	}
	
	/**
	 * 获取上次桌面启动监听每日一次类型的回调时间
	 * @return 回调时间
	 */
	public long getLauncherOnStartDayTime(){
		return baseSP.getLong(KEY_LAUNCHER_ON_START_DAY_TIME, 0);
	}
	
	/**
	 * 设置桌面启动监听(无论有没网络，每天调用一次)类型的回调时间
	 * @param createTime 回调时间
	 */
	public void setLauncherOnStartDayTimeNotNetWork(long createTime){
		baseSP.edit().putLong(KEY_LAUNCHER_ON_START_DAY_TIME_NOT_NETWORK, createTime).commit();
	}
	
	/**
	 * 获取上次桌面启动监听(无论有没网络，每天调用一次)类型的回调时间
	 * @return 回调时间
	 */
	public long getLauncherOnStartDayTimeNotNetWork(){
		return baseSP.getLong(KEY_LAUNCHER_ON_START_DAY_TIME_NOT_NETWORK, 0);
	}
	
	/**
	 * 是否存在编辑模式下的添加屏
	 * @return
	 */
	public boolean hasSpringAddScreen(){
		return baseSP.getBoolean(HAS_SPRING_ADD_SCREEN, false);
	}
	
	/**
	 * 设置是否存在编辑模式下的添加屏
	 * @param version
	 */
	public void setHasSpringAddScreen(boolean flag){
		baseSP.edit().putBoolean(HAS_SPRING_ADD_SCREEN, flag).commit();
	}
	
	public int getCellLayoutCellWidth(){
		return baseSP.getInt(CELLLAYOUT_CELLWIDTH, -1);
	}
	
	public void setCellLayoutCellWidth(int w){
		if(w <= 0)
			return;
		baseSP.edit().putInt(CELLLAYOUT_CELLWIDTH, w).commit();
	}
	
	public int getCellLayoutCellHeight(){
		return baseSP.getInt(CELLLAYOUT_CELLHEIGHT, -1);
	}
	
	public void setCellLayoutCellHeight(int h){
		if(h <= 0)
			return;
		baseSP.edit().putInt(CELLLAYOUT_CELLHEIGHT, h).commit();
	}
	
	public int getDockbarCellWidth(){
		return baseSP.getInt(DOCKBAR_CELLWIDTH, -1);
	}
	
	public void setDockbarCellWidth(int w){
		if(w <= 0)
			return;
		baseSP.edit().putInt(DOCKBAR_CELLWIDTH, w).commit();
	}
	
	public int getDockbarCellHeight(){
		return baseSP.getInt(DOCKBAR_CELLHEIGHT, -1);
	}
	
	public void setDockbarCellHeight(int h){
		if(h <= 0)
			return;
		baseSP.edit().putInt(DOCKBAR_CELLHEIGHT, h).commit();
	}
	
	/**
	 * 设置桌面创建时间
	 * @param createTime 创建时间
	 */
	public void setLauncherCreateTime(long createTime){
		baseSP.edit().putLong(KEY_LAUNCHER_CREATE_TIME, createTime).commit();
	}
	
	/**
	 * 获取桌面创建的时间，默认为当前时间
	 * @return 创建时间
	 */
	public long getLaucherCreateTime(){
		return baseSP.getLong(KEY_LAUNCHER_CREATE_TIME, System.currentTimeMillis());
	}
	
	/**
	 * 获取第一次使用桌面的时间
	 * @return 时间
	 */
	public long getFirstLaunchTime() {
		long current = System.currentTimeMillis();
		long time = baseSP.getLong(KEY_FIRST_LAUNCH_TIME, current);
		if(current == time) {
			setFirstLaunchTime(current);
		}
		return time;
	}
	
	/**
	 * 设置第一次使用桌面的时间
	 * @param time 时间
	 */
	public void setFirstLaunchTime(long time) {
		baseSP.edit().putLong(KEY_FIRST_LAUNCH_TIME, time).commit();
	}
	
	/**
	 * 某个版本是否已被启动过
	 * @param currentVer 版本
	 * @return true表示已被启动过
	 */
	public boolean isVersionShowed(String currentVer) {
		return baseSP.getBoolean(currentVer, false);
	}
	
	/**
	 * 设置某个版本是否被启动过
	 * @param version 版本
	 * @param showed true已启动过/false还未启动过
	 */
	public void setVersionShowed(String version, boolean showed) {
		baseSP.edit().putBoolean(version, showed).commit();
	}
	
	/**
	 * 获取某个版本是否被启动过
	 * @param versionCode 版本号
	 * @param showed true已启动过/false还未启动过
	 */
	public boolean isVersionCodeShowed(int versionCode) {
		return baseSP.getBoolean("#"+String.valueOf(versionCode), false);
	}
	
	/**
	 * 设置某个版本是否被启动过
	 * @param versionCode 版本号
	 * @param showed true已启动过/false还未启动过
	 */
	public void setVersionCodeShowed(int versionCode, boolean showed) {
		baseSP.edit().putBoolean("#"+String.valueOf(versionCode), showed).commit();
	}
	
	/**
	 * 获取默认屏
	 * @return 默认屏
	 */
	public int getDefaultScreen() {
		return getDefaultScreen(ScreenViewGroup.DEFAULT_SCREEN);
	}

	/**
	 * 获取默认屏
	 * @param screen 默认的默认屏
	 * @return 默认屏
	 */
	public int getDefaultScreen(int screen) {
		return baseSP.getInt(KEY_DEFAULT_SCREEN, screen);
	}
	
	/**
	 * 设置默认屏
	 * @param screen 默认屏
	 */
	public void setDefaultScreen(int screen) {
		baseSP.edit().putInt(KEY_DEFAULT_SCREEN, screen).commit();
	}
	
	/**
	 * 获得编辑是否被锁定
	 * @return true表示被锁定 
	 */
	public boolean getIsEditLock() {
		return editIsLock;
	}
	
	/**
	 * 设置编辑被锁定
	 * @param islock true锁定/false未锁定
	 */
	public void setIsEditLock(boolean islock) {
		editIsLock = islock;
		baseSP.edit().putBoolean(KEY_SETTINGS_EDIT_IS_LOCK, editIsLock).commit();
	}
	
	/**
	 * 获取新增用户时记录的桌面版本号
	 * @Title: getVersionCodeForResident
	 * @author lytjackson@gmail.com
	 * @date 2014-3-31
	 * @return
	 */
	public int getVersionCodeFrom() {
		return baseSP.getInt(KEY_VERSION_FROM, -1);
	}

	/**
	 * 新增时记录版本号
	 * @Title: setVersionCodeForResident
	 * @author lytjackson@gmail.com
	 * @date 2014-3-31
	 * @param versionCode
	 */
	public void setVersionCodeFrom(int versionCode) {
		baseSP.edit().putInt(KEY_VERSION_FROM, versionCode).commit();
	}
	
	/**
	 * 获取用户上次安装的桌面版本，用于处理数据库升降级
	 * @return
	 */
	public int getLastVersionCode() {
		return baseSP.getInt(KEY_LAST_VERSION_CODE, -1);
	}

	/**
	 * 设置用户上次安装的桌面版本，用于处理数据库升降级
	 * @param versionCode
	 */
	public void setLastVersionCode(int versionCode) {
		baseSP.edit().putInt(KEY_LAST_VERSION_CODE, versionCode).commit();
	}
}
