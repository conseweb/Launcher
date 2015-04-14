package com.nd.hilauncherdev.theme.adaption;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.app.data.AppDataBase;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.theme.data.BaseThemeData;

/**
 * Description: 主题图标机型适配 <br>
 * Author:caizp <br>
 * Date:2011-7-4下午04:22:44
 */
public class ThemeIconIntentAdaptation {
	/**
	 * 未接电话
	 */
	public static final String APP_HINT_TYPE_PHONE = "apphinttypephone";
	/**
	 * 未读短信
	 */
	public static final String APP_HINT_TYPE_MMS = "apphinttypemms";
	/**
	 * 可升级应用(应用商店)
	 */
	public static final String APP_HINT_TYPE_UPDATE_APP = "apphinttypeupdateapps";
	
	private static ThemeIconIntentAdaptation instance;
	/**
	 * {主题图标的key,包名+"|" + 类名}
	 */
	private HashMap<String, String> mapThemeIntentCache = new HashMap<String, String>();
	/**
	 * 需要监听的应用程序
	 */
	private HashMap<ComponentName, String> mapComponentCache = new HashMap<ComponentName, String>();

	private ThemeIconIntentAdaptation() {

	}

	public static ThemeIconIntentAdaptation getInstance() {
		if (instance == null) {
			instance = new ThemeIconIntentAdaptation();
		}

		return instance;
	}

	/**
	 * 加载主题图片对应程序信息数据
	 */
	public void loadThemeIntentData() {
		
		String sql = "select * from THEME_APP_ADAPTATION";
		AppDataBase dbUtil = null;
		Cursor cursor = null;
		try {
			dbUtil = new AppDataBase(BaseConfig.getApplicationContext());
			cursor = dbUtil.query(sql);
			int iAppKey = cursor.getColumnIndex("app_key");
			int iPackageName = cursor.getColumnIndex("app_package_name");
			int iClsName = cursor.getColumnIndex("app_class_name");
			if (cursor.getCount() == 0) {
				saveThemeIntentDataAtLauncherFirstLoad(dbUtil);
			} else {
				while (cursor.moveToNext()) {
					String appKey = cursor.getString(iAppKey);
					mapThemeIntentCache.put(appKey, cursor.getString(iPackageName) + "|" + cursor.getString(iClsName));
					if (appKey.equals(BaseThemeData.ICON_PHONE))
						mapComponentCache.put(new ComponentName(cursor.getString(iPackageName), cursor.getString(iClsName)), APP_HINT_TYPE_PHONE);
					else if (appKey.equals(BaseThemeData.ICON_MMS)) 
						mapComponentCache.put(new ComponentName(cursor.getString(iPackageName), cursor.getString(iClsName)), APP_HINT_TYPE_MMS);
					else if (appKey.equals(BaseThemeData.ICON_APP_STORE)) 
						mapComponentCache.put(new ComponentName("com.nd.android.pandahome2", "com.nd.hilauncherdev.appstore.AppStoreSwitchActivity"), APP_HINT_TYPE_UPDATE_APP);					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != cursor){
				cursor.close();
			}
			if (null != dbUtil){
				dbUtil.close();
			}
		}
	}

	/**
	 * 第一次初始化时，将数据保存到数据库中
	 */
	public void saveThemeIntentDataAtLauncherFirstLoad(AppDataBase dbUtil) {
		Object[][] metadatas = ThemeIntentMetaData.METADATA;
		String[] sRtns = null;
		for (Object[] metadata : metadatas) {
			if (BaseThemeData.ICON_MMS.equals((String) metadata[0]) || BaseThemeData.ICON_CONTACTS.equals((String) metadata[0]) || BaseThemeData.ICON_PHONE.equals((String) metadata[0])
					|| BaseThemeData.ICON_BROWSER.equals((String) metadata[0]) || BaseThemeData.ICON_MAPS.equals((String) metadata[0])) {
				sRtns = getValidPackageAndClassNameByPackageAndClassName(metadata[1], metadata[2]);
				if (null == sRtns) {
					sRtns = getApplicationPackageNameAndClassName((String) metadata[3], (Uri) metadata[4], (String) metadata[5], (Boolean) metadata[6], true);
				}
			} else {
				sRtns = getApplicationPackageNameAndClassName((String) metadata[3], (Uri) metadata[4], (String) metadata[5], (Boolean) metadata[6], true);
			}
			if (null == sRtns) {
				sRtns = getValidPackageAndClassNameByPackageAndClassName(metadata[1], metadata[2]);
				if (null == sRtns) {
					sRtns = getApplicationPackageNameAndClassName((String) metadata[3], (Uri) metadata[4], (String) metadata[5], (Boolean) metadata[6], false);
					if (null == sRtns)
						sRtns = new String[] { " ", " " };
				}
			}
			mapThemeIntentCache.put((String) metadata[0], sRtns[0] + "|" + sRtns[1]);
			if (BaseThemeData.ICON_MMS.equals((String) metadata[0]))
				mapComponentCache.put(new ComponentName(sRtns[0], sRtns[1]), APP_HINT_TYPE_MMS);
			else if (BaseThemeData.ICON_PHONE.equals((String) metadata[0])) 
				mapComponentCache.put(new ComponentName(sRtns[0], sRtns[1]), APP_HINT_TYPE_PHONE);
		}

		try {
			String[] sqls = new String[mapThemeIntentCache.size()];
			int i = 0;
			for (String info : mapThemeIntentCache.keySet()) {
				String key = mapThemeIntentCache.get(info);
				String[] arrs = key.split("\\|");
				sqls[i++] = "insert into THEME_APP_ADAPTATION values('" + info + "','" + arrs[0] + "','" + arrs[1] + "')";
			}
			dbUtil.execBatchSQL(sqls, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在系统上查找符合此包名、类名的程序，并返回对应的包名和类名，
	 * 
	 * @param packageNames
	 *            包名
	 * @param clsNames
	 *            类名
	 * @return 存在返回true，否则返回false
	 */
	private static String[] getValidPackageAndClassNameByPackageAndClassName(Object packageNames, Object clsNames) {
		if (packageNames.getClass().isArray()) {
			String[] arrPackage = (String[]) packageNames;
			String[] arrClsName = (String[]) clsNames;
			for (int i = 0; i < arrPackage.length; i++) {
				String packageName = arrPackage[i];
				String clsName = arrClsName[i];
				if (checkApplicationByApplicationPackageAndClassName(packageName, clsName)) {
					return new String[] { packageName, clsName };
				}
			}
		} else {
			String packageName = (String) packageNames;
			String clsName = (String) clsNames;
			if (checkApplicationByApplicationPackageAndClassName(packageName, clsName)) {
				return new String[] { packageName, clsName };
			}
		}
		return null;
	}

	/**
	 * 在系统上查找符合此包名、类名的程序，并返回对应的包名和类名，
	 * 
	 * @param packageName
	 *            包名
	 * @param clsName
	 *            类名
	 * @return 存在返回true，否则返回false
	 */
	private static boolean checkApplicationByApplicationPackageAndClassName(String packageName, String clsName) {
		ComponentName component = new ComponentName(packageName, clsName);
		try {
			//验证该界面为匣子图标入口 caizp 2013-9-4
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.setPackage(packageName);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> resolveInfo = BaseConfig.getApplicationContext().getPackageManager().queryIntentActivities(intent, 0);
			if(!isAppEntrance(resolveInfo, packageName, clsName)){
				return false;
			}
			//验证该界面可以由其他应用开启
			ActivityInfo activityInfo = BaseConfig.getApplicationContext().getPackageManager().getActivityInfo(component, 0);
			if(!activityInfo.exported){
				return false;
			}
		} catch (NameNotFoundException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * <br>Description: 验证Activity是否APP主入口
	 * <br>Author:caizp
	 * <br>Date:2014年8月11日上午11:56:31
	 * @param resolveInfo
	 * @param packageName
	 * @param clsName
	 * @return
	 */
	private static boolean isAppEntrance(List<ResolveInfo> resolveInfo, String packageName, String clsName) {
		if(null == resolveInfo){
			return false;
		} else {
			for(int i=0; i<resolveInfo.size(); i++) {
				ActivityInfo activityInfo = resolveInfo.get(i).activityInfo;
				if(null != activityInfo && activityInfo.packageName.equals(packageName) && activityInfo.name.equals(clsName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据action、uri查找到匹配的程序，如果有多个程序适合，则返回修改时间最早的程序
	 */
	private static String[] getApplicationPackageNameAndClassName(String action, Uri uri, String type, boolean needMainAcitivty, boolean needLauncherCatagory) {

		if (action.equals(Intent.ACTION_VIEW) && uri.equals(Uri.EMPTY) && type.equals(""))
			return null;

		Intent intent = new Intent(action);
		if (uri != Uri.EMPTY)
			intent.setData(uri);
		if (!type.equals("")) {
			intent.setType(type);
		}

		List<ResolveInfo> listResolveInfo = BaseConfig.getApplicationContext().getPackageManager().queryIntentActivities(intent, 0);
		if (listResolveInfo.isEmpty())
			return null;
		if (listResolveInfo.size() == 1) {
			ResolveInfo resolveInfo = listResolveInfo.get(0);
			if (needMainAcitivty) {
				return findMainActivityPackageNameAndClsNameByPackageName(resolveInfo.activityInfo.packageName, action, needLauncherCatagory);
			}
			if (null != resolveInfo.activityInfo) {
				return new String[] { resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name };
			}
			return null;
		}

		ResolveInfo resolveInfoResult = null;
		if (listResolveInfo.size() > 1) {
			for (ResolveInfo resolveInfo : listResolveInfo) {
				ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					resolveInfoResult = resolveInfo;
					break;
				}

			}

			if (null == resolveInfoResult) {
				Collections.sort(listResolveInfo, new Comparator<ResolveInfo>() {
					@Override
					public int compare(ResolveInfo info1, ResolveInfo info2) {
						String sFile1 = info1.activityInfo.applicationInfo.sourceDir;
						String sFile2 = info2.activityInfo.applicationInfo.sourceDir;

						File file1 = new File(sFile1);
						File file2 = new File(sFile2);

						return (int) (file1.lastModified() - file2.lastModified());
					}
				});
				resolveInfoResult = listResolveInfo.get(0);
			}
			if (needMainAcitivty) {
				return findMainActivityPackageNameAndClsNameByPackageName(resolveInfoResult.activityInfo.packageName, action, needLauncherCatagory);
			}
			return new String[] { resolveInfoResult.activityInfo.packageName, resolveInfoResult.activityInfo.name };
		}
		return null;
	}

	/**
	 * 根据包名找到程序启动时调用的Activity信息
	 */
	private static String[] findMainActivityPackageNameAndClsNameByPackageName(String packageName, String action, boolean needLauncherCatagory) {
		Intent intent = new Intent();
		intent.setPackage(packageName);
		intent.setAction(action);
		if (needLauncherCatagory) {
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
		}
		ResolveInfo resolveInfo = BaseConfig.getApplicationContext().getPackageManager().resolveActivity(intent, 0);

		if (null != resolveInfo) {
			return new String[] { resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name };
		}
		return null;
	}

	/**
	 * 获取主题图标的KEY
	 * 
	 * @param appkey
	 *            包名 + "|" + 类名
	 * @return
	 */
	public String getThemeIconKey(String appkey) {
		if (null == appkey)
			return null;
		String result = null;
		for (String key : mapThemeIntentCache.keySet()) {
			String sActualAppKey = mapThemeIntentCache.get(key);
			if (null == sActualAppKey)
				continue;
			if (sActualAppKey.equalsIgnoreCase(appkey)) {
				result = key;
			}
		}
		return result;
	}

	/**
	 * 根据主题图标key获取实际的程序
	 */
	public String[] getActualApplicationPackageAndClassName(String iconKey) {
		if (null == iconKey)
			return null;

		String value = mapThemeIntentCache.get(iconKey);
		if (value == null || value.trim().equals("|"))
			return null;

		return value.split("\\|");
	}

	/**
	 * 获得具体适配后的应用程序信息
	 * <br>Author:ryan
	 * <br>Date:2012-7-17下午05:12:31
	 */
	public ComponentName getActualComponent(String key) {
		if (null == key)
			return null;
		
		String value = mapThemeIntentCache.get(key);
		if (value == null || value.trim().equals("|"))
			return null;

		String[] result = value.split("\\|");
		return new ComponentName(result[0], result[1]);
	}
	
	/**
	 * 根据传入的ThemeKey获取该应用(Activity内置)默认图标
	 */
	public static Drawable getActivityIcon(Context ctx, String themeKey) {
		ComponentName cmp = ThemeIconIntentAdaptation.getInstance().getActualComponent(themeKey);
		try {
			return ctx.getPackageManager().getActivityIcon(cmp);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据某一个应用的Intent 判断其"提示信息" 类型值
	 * @param intent Intent 对象
	 * @return "提示信息" 类型值
	 */
	public String getAppHintType(Intent intent) {
		if (intent == null) 
			return null;
		
		if (isDefaultDockAppByIntent(intent)) {
			if (BaseThemeData.INTENT_PHONE.equals(intent.toUri(0)))
				return APP_HINT_TYPE_PHONE;
			else if (BaseThemeData.INTENT_MMS.equals(intent.toUri(0)))
				return APP_HINT_TYPE_MMS;
			else 
				return null;
		} else {
			return getHintApp(intent.getComponent());
		}
	}
	
	/**
	 * 是否监听提示的应用程序
	 * <br>Author:ryan
	 * <br>Date:2012-7-17下午05:13:04
	 */
	public String getHintApp(ComponentName cn) {
		if (null == cn)
			return null;

		String value = mapComponentCache.get(cn);
		return value;
	}
	
	/**
	 * 根据传入的ThemeKey判断是否是托盘默认四应用之一
	 */
	public static boolean isDefaultDockApp(String themeKey) {
		if ( BaseThemeData.ICON_BROWSER.equals(themeKey) 
				|| BaseThemeData.ICON_PHONE.equals(themeKey)
				|| BaseThemeData.ICON_CONTACTS.equals(themeKey)
				|| BaseThemeData.ICON_MMS.equals(themeKey)
			)
			return true;
		else
			return false;
		
	}

	/**
	 * 根据主题key获取 intent描述串, 只适用于托盘默认四应用, 非该四应用则返回null
	 * @param themeKey 主题key, 由 包名 | 类名 组成
	 * @return
	 * @author Yu.F
	 */
	public static String getDefaultDockAppUriString(String themeKey) {
		if (TextUtils.isEmpty(themeKey))
			return null;
		
		if (BaseThemeData.ICON_PHONE.equals(themeKey))
			return BaseThemeData.INTENT_PHONE;
		else if (BaseThemeData.ICON_MMS.equals(themeKey))
			return BaseThemeData.INTENT_MMS;
		else if (BaseThemeData.ICON_CONTACTS.equals(themeKey))
			return BaseThemeData.INTENT_CONTACTS;
		else if (BaseThemeData.ICON_BROWSER.equals(themeKey))
			return BaseThemeData.INTENT_BROWSER;
		else 
			return null;
	}

	/**
	 * 根据传入的Intent URI判断是否是托盘默认四应用之一
	 * */
	public static boolean isDefaultDockAppByUri(String uri) {
		if (BaseThemeData.INTENT_BROWSER.equals(uri)
				|| BaseThemeData.INTENT_CONTACTS.equals(uri)
				|| BaseThemeData.INTENT_MMS.equals(uri)
				|| BaseThemeData.INTENT_PHONE.equals(uri)) 
			
			return true;
		else
			return false;
	}

	/**
	 * 根据传入的Intent URI获取托盘默认四应用的当前名称
	 */
	public static String getDefaultDockTitleAppByUri(Context context,String uri) {
		PackageManager pm = context.getPackageManager();
		if (BaseThemeData.INTENT_BROWSER.equals(uri)) {
			String[] comp = ThemeIconIntentAdaptation.getInstance().getActualApplicationPackageAndClassName(BaseThemeData.ICON_BROWSER);
			if(null != comp) {
				Intent intent = AndroidPackageUtils.getNewTaskIntent(new ComponentName(comp[0], comp[1]));
				String label = AndroidPackageUtils.getIntentLabel(intent, pm);
				if(!TextUtils.isEmpty(label)){
					return label;
				}
			}
			return context.getString(R.string.dockbar_dock_browser);
		}
		if (BaseThemeData.INTENT_CONTACTS.equals(uri)) {
			String[] comp = ThemeIconIntentAdaptation.getInstance().getActualApplicationPackageAndClassName(BaseThemeData.ICON_CONTACTS);
			if(null != comp) {
				Intent intent = AndroidPackageUtils.getNewTaskIntent(new ComponentName(comp[0], comp[1]));
				String label = AndroidPackageUtils.getIntentLabel(intent, pm);
				if(!TextUtils.isEmpty(label)){
					return label;
				}
			}
			return context.getString(R.string.dockbar_dock_contacts);
		}
		if (BaseThemeData.INTENT_MMS.equals(uri)) {
			String[] comp = ThemeIconIntentAdaptation.getInstance().getActualApplicationPackageAndClassName(BaseThemeData.ICON_MMS);
			if(null != comp) {
				Intent intent = AndroidPackageUtils.getNewTaskIntent(new ComponentName(comp[0], comp[1]));
				String label = AndroidPackageUtils.getIntentLabel(intent, pm);
				if(!TextUtils.isEmpty(label)){
					return label;
				}
			}
			return context.getString(R.string.dockbar_dock_sms);
		}
		if (BaseThemeData.INTENT_PHONE.equals(uri)) {
			String[] comp = ThemeIconIntentAdaptation.getInstance().getActualApplicationPackageAndClassName(BaseThemeData.ICON_PHONE);
			if(null != comp) {
				Intent intent = AndroidPackageUtils.getNewTaskIntent(new ComponentName(comp[0], comp[1]));
				String label = AndroidPackageUtils.getIntentLabel(intent, pm);
				if(!TextUtils.isEmpty(label)){
					return label;
				}
			}
			return context.getString(R.string.dockbar_dock_dial);
		}
		return null;
	}

	public static boolean isDefaultDockAppByIntent(Intent intent) {
		if (intent == null) 
			return false;
		
		return isDefaultDockAppByUri(intent.toUri(0));
	}

	/**
	 * 获取Dock四个默认应用(拨号, 联系人, 短信, 浏览器)的主题key
	 * @param uri 传入Dock四个默认应用的intent uri用于判断
	 * @return 主题key
	 * @author Yu.F@2012.11.16
	 */
	public static String getDefaultDockAppThemeKey(String uri) {
		if (TextUtils.isEmpty(uri))
			return null;
		
		uri = uri.replaceAll("sourceBounds=.*;B\\.", "B.");
		
		if (BaseThemeData.INTENT_BROWSER.equals(uri))
			return BaseThemeData.ICON_BROWSER;
		else if (BaseThemeData.INTENT_CONTACTS.equals(uri))
			return BaseThemeData.ICON_CONTACTS;
		else if (BaseThemeData.INTENT_MMS.equals(uri))
			return BaseThemeData.ICON_MMS;
		else if (BaseThemeData.INTENT_PHONE.equals(uri))
			return BaseThemeData.ICON_PHONE;
		else 
			return null;
	}

	/**
	 * 获取托盘默认四应用(初始状态下, 即未被改动intent的情况下)的主题key
	 * @param cn 原ComponentName, 并非被改动过的
	 * @return 如果不是托盘默认四应用的初始状态, 则返回null
	 * @author Yu.F
	 */
	public static String getDefaultDockAppThemeKeyByOriginalStateComponentName(ComponentName cn) {
		if (cn == null)
			return null;
		
		String pck = cn.getPackageName();
		String cls = cn.getClassName();	
		if(null == pck || null == cls){
			return null;
		}
		String[] pckClsArr = {BaseThemeData.ICON_BROWSER, BaseThemeData.ICON_CONTACTS, BaseThemeData.ICON_MMS, BaseThemeData.ICON_PHONE};
		ThemeIconIntentAdaptation ada = ThemeIconIntentAdaptation.getInstance();
	
		for (String pckCls : pckClsArr) {
			String[] arr = ada.getActualApplicationPackageAndClassName(pckCls);
			if (null != arr && pck.equals(arr[0]) && cls.equals(arr[1]))
				return pckCls;
		}
		
		return null;
	}
	
	/**
	 * 适配指定应用，返回应用的包名与类名
	 * @param metadata 为ThemeIntentMetaData.METADATA里的元素
	 * @return
	 */
	public static String[] getAppPkgNameAndClassName(Object[] metadata) {
		if (metadata == null || metadata.length < 7)
			return null;
		String[] sRtns = null;
		sRtns = getValidPackageAndClassNameByPackageAndClassName(metadata[1], metadata[2]);
		if (null == sRtns) {
			sRtns = getApplicationPackageNameAndClassName((String) metadata[3], (Uri) metadata[4], (String) metadata[5],
					(Boolean) metadata[6], true);
		}
		return sRtns;
	}
	
}
