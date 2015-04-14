package com.nd.hilauncherdev.app;

import android.content.Context;
import android.database.Cursor;

import com.nd.hilauncherdev.launcher.config.db.ConfigDataBase;
import com.nd.hilauncherdev.launcher.config.db.DefaultLauncherTable;

public class DefaultAppAssit {

	public static final String COMPONENT_STR = "com.bitants.mirrorlauncher/com.nd.hilauncherdev.app.AppResolverSelectActivity";
	/**
	 * 拨号Uri
	 */
	public static final String PHONE_SHORTCUT_URI = "#Intent;action=android.intent.action.DIAL;launchFlags=0x10000000;component=" + COMPONENT_STR + ";B.is_always_select=false;end";
	
	/**
	 * 短信Uri
	 */
	public static final String SMS_SHORTCUT_URI = "content://mms-sms/#Intent;action=android.intent.action.MAIN;launchFlags=0x10000000;component=" + COMPONENT_STR + ";B.is_always_select=false;end";
	
	/**
	 * 联系人Uri
	 */
	public static final String CONTACTS_SHORTCUT_URI = "content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;component=" + COMPONENT_STR + ";B.is_always_select=false;end";
	
	/**
	 * 浏览器Uri
	 */
	public static final String BROWSER_SHORTCUT_URI = "http://www.google.com#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;component=" + COMPONENT_STR + ";B.is_always_select=false;end";
	
	/**
	 * 拨号Intent
	 */
	public static final String PHONE_SHORTCUT_SPECIAL_INTENT = "Intent { act=android.intent.action.DIAL flg=0x10000000 cmp=" + COMPONENT_STR + " (has extras) }";
	/**
	 * 短信Intent
	 */
	public static final String SMS_SHORTCUT_SPECIAL_INTENT = "Intent { act=android.intent.action.MAIN dat=content://mms-sms/ flg=0x10000000 cmp=" + COMPONENT_STR + " (has extras) }";
	/**
	 * 联系人Intent
	 */
	public static final String CONTACTS_SHORTCUT_SPECIAL_INTENT = "Intent { act=android.intent.action.VIEW dat=content://com.android.contacts/contacts flg=0x10000000 cmp=" + COMPONENT_STR + " (has extras) }";
	/**
	 * 浏览器Intent
	 */
	public static final String BROWSER_SHORTCUT_SPECIAL_INTENT = "Intent { act=android.intent.action.VIEW dat=http://www.google.com flg=0x10000000 cmp=" + COMPONENT_STR + " (has extras) }";

	/**
	 * 短信Intent(部分HTC手机intent.toString()后dat冒号后的字符串丢失问题)
	 */
	public static final String SMS_SHORTCUT_SPECIAL_INTENT_EX = "Intent { act=android.intent.action.MAIN dat=content flg=0x10000000 cmp=" + COMPONENT_STR + " (has extras) }";
	/**
	 * 联系人Intent(部分HTC手机intent.toString()后dat冒号后的字符串丢失问题)
	 */
	public static final String CONTACTS_SHORTCUT_SPECIAL_INTENT_EX = "Intent { act=android.intent.action.VIEW dat=content flg=0x10000000 cmp=" + COMPONENT_STR + " (has extras) }";
	/**
	 * 浏览器Intent(部分HTC手机intent.toString()后dat冒号后的字符串丢失问题)
	 */
	public static final String BROWSER_SHORTCUT_SPECIAL_INTENT_EX = "Intent { act=android.intent.action.VIEW dat=http flg=0x10000000 cmp=" + COMPONENT_STR + " (has extras) }";
	
	/**
	 * <br>Description: 保存默认启动的应用程序
	 * <br>Author:caizp
	 * <br>Date:2013-5-7下午6:05:15
	 * @param packageName
	 * @param className
	 * @param appIntent
	 */
	public static void saveDefaultApp(Context context, String packageName, String className, String appIntent) {
		String sql = DefaultLauncherTable.SELECT_DEFAULT;
		int index = appIntent.indexOf(":");
		if(-1 == index) {
			index = appIntent.length()-1;
		}
		Cursor cs = null;
		ConfigDataBase db = new ConfigDataBase(context);
		try {
			cs = db.query(sql, new String[]{"%"+appIntent.substring(0, index)+"%"});
			if (null != cs && cs.getCount() > 0) {
				String updateSql = DefaultLauncherTable.UPDATE_DEFAULT;
				db.execSQL(updateSql, new String[]{packageName, className, "%"+appIntent.substring(0, index)+"%"});
			} else {
				String insertSql = DefaultLauncherTable.insertDefaultLauncherSql(appIntent, packageName, className);
				db.execSQL(insertSql);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cs) {
				cs.close();
			}
			if (null != db) {
				db.close();
			}
		}
	}
	
	/**
	 * <br>Description:清除默认打开应用
	 * <br>Author:caizp
	 * <br>Date:2013-5-9上午11:14:03
	 * @param context
	 * @param appIntent
	 */
	public static void clearDefaultApp(Context context, String appIntent) {
		String deleteSql = DefaultLauncherTable.DELETE_DEFAULT;
		ConfigDataBase db = new ConfigDataBase(context);
		try {
			int index = appIntent.indexOf(":");
			if(-1 == index) {
				index = appIntent.length()-1;
			}
			db.execSQL(deleteSql, new String[] {"%"+appIntent.substring(0, index)+"%"});
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (null != db) {
				db.close();
			}
		}
	}
	
}
