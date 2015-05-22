package com.bitants.launcherdev.launcher.config.db;

/**
 * dockbar默认启动table
 */
public class DefaultLauncherTable {
	/**
	 * 创表语句
	 */
	public static final String CREATE_TABLE = 
		"CREATE TABLE IF NOT EXISTS 'app_default_launcher' ('app_intent' varchar(128), 'app_pkg' varchar(64), 'app_cls' varchar(64))";

	public static final String SELECT_DEFAULT = "select * from app_default_launcher where app_intent like ?";
	
	public static final String UPDATE_DEFAULT = "update app_default_launcher set app_pkg = ?, app_cls = ? where app_intent like ?";
	
	public static final String INSERT_DEFAULT = "insert into app_default_launcher values('%s', '%s', '%s')";
	
	public static final String DELETE_DEFAULT = "delete from app_default_launcher where app_intent like ?";
	
	public static String insertDefaultLauncherSql(String intent,String pkg,String clz){
		return String.format(INSERT_DEFAULT, intent,pkg,clz);
	}
	
	public static String getDefaultLauncherSql(String intent){
		return String.format(SELECT_DEFAULT, intent);
	}
	
	public static String updateDefaultLauncherSql(String pkg,String clz,String intent){
		return String.format(UPDATE_DEFAULT, pkg,clz,intent);
	}
}
