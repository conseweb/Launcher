package com.nd.launcherdev.widget;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.nd.launcherdev.datamodel.Global;
import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.launcherdev.kitset.invoke.ForeignPackage;
import com.nd.launcherdev.kitset.util.AndroidPackageUtils;
import com.nd.launcherdev.kitset.util.ComparatorUtil;
import com.nd.launcherdev.kitset.util.StringUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;

import com.android.internal.util.XmlUtils;
import com.nd.launcherdev.datamodel.Global;
import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.launcherdev.kitset.invoke.ForeignPackage;
import com.nd.launcherdev.kitset.util.AndroidPackageUtils;
import com.nd.launcherdev.kitset.util.ComparatorUtil;
import com.nd.launcherdev.kitset.util.StringUtil;

/**
 * 小部件数据管理类
 * 
 * @author zjf date 2012-5-22
 */
public final class LauncherWidgetInfoManager {
	//仅供测试使用
	public static final boolean DEBUG = false;
	
	public static final String CHECK_KEY = "apkmd5";

	private Context mContext;

	public static final String PANDA_WIDGET_CATEGORY_QUERY_INTENT = "com.nd.android.pandahome.widget.category";

	/**
	 * 所有小部件信息
	 */
	private ArrayList<LauncherItemInfo> mAllLauncherItemInfos = null;

	/**
	 * 匣子里面的小部件
	 */
	private ArrayList<LauncherWidgetInfo> mAllLauncherWidgetInfo = new ArrayList<LauncherWidgetInfo>();

	/**
	 * 所有推荐的小部件
	 */
	private ArrayList<LauncherWidgetInfo> mAllRecommentWidgetInfo = new ArrayList<LauncherWidgetInfo>();

	/**
	 * 忽略的小部件列表(不在匣子及下载更多的已安装中显示)
	 */
	private Map<String, String> ingoreInstalledWidgetMap = new HashMap<String, String>();
	
	/**
	 * 已经安装91标准小部件包名列表
	 */
	private Map<String, String> installedPandaWidgetPkgMap = new HashMap<String, String>();
	
	/**
	 * 小部件列表XML以及相关图标保存路径
	 */
	public static final String SAVE_PATH = Global.CACHES_HOME + "/WidgetListCache/";

	private static LauncherWidgetInfoManager _instance;

	private static final String WIDGET_INFO_FILE_NAME = "widget";

	private static final String TAB_PANDAWIDGETS = "pandawidgets";
	
	//MOBO桌面支持的第三方插件列表
	private ArrayList<String> mSupportWidgetPackage = new ArrayList<String>();

	private LauncherWidgetInfoManager() {
		mContext = Global.getApplicationContext();
		mSupportWidgetPackage.add(mContext.getPackageName());
	}

	public static LauncherWidgetInfoManager getInstance() {
		if (null == _instance) {
			_instance = new LauncherWidgetInfoManager();
		}
		return _instance;
	}

	/**
	 * 获取所有小部件
	 * 
	 * @return
	 */
	public ArrayList<LauncherItemInfo> getAllWidgetInfos() {
		checkWidgetDataHasInit();
		ArrayList<LauncherItemInfo> allItems = new ArrayList<LauncherItemInfo>(mAllLauncherItemInfos);
		return allItems;
	}

	/**
	 * 获取所有已经安装的小部件
	 * 
	 * @return
	 */
	public ArrayList<LauncherWidgetInfo> getAllInstalledWidgetInfos() {
		checkWidgetDataHasInit();
		ArrayList<LauncherWidgetInfo> installedWidgetInfos = new ArrayList<LauncherWidgetInfo>();
		for (LauncherItemInfo item : mAllLauncherItemInfos) {
			if (item instanceof LauncherWidgetInfo) {
				LauncherWidgetInfo widgetInfo = (LauncherWidgetInfo) item;
				if (widgetInfo.isInstalled()) {
					installedWidgetInfos.add(widgetInfo);
				}
			}
		}
		return installedWidgetInfos;
	}

	/**
	 * 获取所有未安装的小部件(匣子中的未安装推荐小部件)
	 * 
	 * @return
	 */
	public ArrayList<LauncherWidgetInfo> getAllNotInstalledWidgetInfos() {
		checkWidgetDataHasInit();
		ArrayList<LauncherWidgetInfo> notInstalledWidgetInfos = new ArrayList<LauncherWidgetInfo>();
		for (LauncherItemInfo item : mAllLauncherItemInfos) {
			if (item instanceof LauncherWidgetInfo) {
				LauncherWidgetInfo widgetInfo = (LauncherWidgetInfo) item;
				if (!widgetInfo.isInstalled()) {
					notInstalledWidgetInfos.add(widgetInfo);
				}
			}
		}
		return notInstalledWidgetInfos;
	}

	/**
	 * 检查最新安装的软件是否是新标准的91小部件
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean checkLastUpdateSoftIsPandaWidget(String packageName) {
		if (AndroidPackageUtils.isPkgInstalled(mContext, packageName)) {
			PackageManager pm = Global.getApplicationContext().getPackageManager();
			Intent it = new Intent(PANDA_WIDGET_CATEGORY_QUERY_INTENT);
			it.setPackage(packageName);
			List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(it, 0);
			if (null != resolveInfoList && resolveInfoList.size() > 0) {
				boolean isPandaWidget = false;
				for (ResolveInfo rInfo : resolveInfoList) {
					ArrayList<LauncherWidgetInfo> list = parseInstalledWidgetXml(rInfo.activityInfo.packageName);
					if (list != null && list.size() > 0) {
						isPandaWidget = true;
						break;
					}
				}
				if (isPandaWidget) {
					return true;
				}
			}
			return false;
		} else {
			return checkLauncherWidgetInfoExistByPackageName(packageName);
		}
	}

	/**
	 * 更新91小部件数据
	 * 
	 * @param packageName
	 */
	public void updatePandaWidgetData(String packageName) {
		ArrayList<LauncherWidgetInfo> removes = new ArrayList<LauncherWidgetInfo>();
		for (LauncherWidgetInfo widgetInfo : mAllLauncherWidgetInfo) {
			if (widgetInfo.getPackageName().equals(packageName)) {
				removes.add(widgetInfo);
			}
		}
		if (removes.isEmpty()) {
			for (LauncherWidgetInfo widgetInfo : mAllRecommentWidgetInfo) {
				if (widgetInfo.getPackageName().equals(packageName)) {
					removes.add(widgetInfo);
				}
			}
		}

		mAllLauncherItemInfos.removeAll(removes);
		mAllLauncherWidgetInfo.removeAll(removes);

		if (AndroidPackageUtils.isPkgInstalled(mContext, packageName)) { // 安装或更新小部件
			ArrayList<LauncherWidgetInfo> listWidgets = loadLauncherWidgetInfoByPackageName(packageName);
			mAllLauncherWidgetInfo.addAll(listWidgets);
			mAllLauncherItemInfos.addAll(listWidgets);
			ArrayList<LauncherWidgetInfo> listWidgetInfo = getRecommentLauncherWidgetInfosByPackageName(packageName);
			for (LauncherWidgetInfo widgetInfo : listWidgetInfo) {
				widgetInfo.setInstalled(true);
			}

		} else { // 卸载小部件
			ArrayList<LauncherWidgetInfo> listWidgetInfo = getRecommentLauncherWidgetInfosByPackageName(packageName);
			for (LauncherWidgetInfo widgetInfo : listWidgetInfo) {
				widgetInfo.setInstalled(false);
			}
			mAllLauncherWidgetInfo.addAll(listWidgetInfo);
		}
	}

	/**
	 * 根据包名获取推荐小部件中包名一致的小部件
	 * 
	 * @param packageName
	 * @return
	 */
	private ArrayList<LauncherWidgetInfo> getRecommentLauncherWidgetInfosByPackageName(String packageName) {
		ArrayList<LauncherWidgetInfo> widgets = new ArrayList<LauncherWidgetInfo>();
		for (LauncherWidgetInfo widgetInfo : mAllRecommentWidgetInfo) { // 更新推荐小部件的安装状态
			if (widgetInfo.getPackageName().equals(packageName)) {
				widgetInfo.setInstalled(true);
			}
		}
		return widgets;
	}

	private void init() {
		initIngoreInstalledWidgetsData();
		initWidgetsData();
	}

	/**
	 * 初始化忽略的小部件列表(不在匣子及下载更多的已安装中显示)
	 */
	private void initIngoreInstalledWidgetsData() {
		ingoreInstalledWidgetMap.put("com.nd.android.widget.pandahome.flashlight", ""); // 手电筒
		ingoreInstalledWidgetMap.put("com.nd.android.widget.pandahome.onekeyoffscreen", ""); // 一键关屏
		ingoreInstalledWidgetMap.put("com.nd.android.widget.pandahome.ionekeyoffscreen", ""); // 一键关屏
		ingoreInstalledWidgetMap.put("com.nd.android.launcher91", "");// 91桌面海外版的黄历天气
		ingoreInstalledWidgetMap.put("com.nd.android.smarthome", "");// 安卓桌面的黄历天气
		ingoreInstalledWidgetMap.put("com.nd.fjmobile.pandahome2", "");// 移动定制版的黄历天气
		ingoreInstalledWidgetMap.put("com.nd.android.launcher.core", "");// 安卓桌面调试包的黄历天气
		ingoreInstalledWidgetMap.put("com.nd.android.pandahome.hd", "");// 91桌面HD包的黄历天气
		ingoreInstalledWidgetMap.put("com.xtouch.android.launcher", "");// 91桌面海外定制包的黄历天气
		ingoreInstalledWidgetMap.put("com.nd.android.ilauncher", "");// 91爱桌面的黄历天气
		ingoreInstalledWidgetMap.put("com.baidu.android.launcher", "");// 百度桌面的黄历天气
		ingoreInstalledWidgetMap.put("com.nd.android.pandahome2", "");// 91桌面的黄历天气
	}

	
	/**
	 * 91桌面内部快捷方式widget
	 * 
	 * @return
	 */
	public ArrayList<LauncherWidgetInfo> loadAllLauncherWidgetShorcut(){
		ArrayList<LauncherWidgetInfo> listLauncherWidgetInside = new ArrayList<LauncherWidgetInfo>();
		return listLauncherWidgetInside;
	}
	
	/**
	 * MOBO桌面内部widget
	 * 
	 * @return
	 */
	public ArrayList<LauncherWidgetInfo> loadAllLauncherWidgetInside() {
		ArrayList<LauncherWidgetInfo> listLauncherWidgetInside = new ArrayList<LauncherWidgetInfo>();
		listLauncherWidgetInside.add(LauncherWidgetInfo.makeWeatherClockWidgetInfo());
		listLauncherWidgetInside.add(LauncherWidgetInfo.makeThemeWidgetInfo());
		listLauncherWidgetInside.add(LauncherWidgetInfo.makeMemoryClean1X1WidgetInfo()); // 一键清理1x1
		return listLauncherWidgetInside;
	}
	

	/**
	 * LauncherWidgetInfo list排序
	 * 
	 * @param list
	 */
	public void sortLauncherWidgetInfo(ArrayList<LauncherItemInfo> list) {
		if (null == list || list.size() == 0) {
			return;
		}
		Collections.sort(list, new Comparator<LauncherItemInfo>() {
			@Override
			public int compare(LauncherItemInfo item1, LauncherItemInfo item2) {
				if(item1 instanceof LauncherWidgetInfo&&item2 instanceof LauncherWidgetInfo)
				{
					Date date1=LauncherWidgetInfoManager.getInstance().getWidetInstallTime(((LauncherWidgetInfo) item1).getPackageName());
					Date date2=LauncherWidgetInfoManager.getInstance().getWidetInstallTime(((LauncherWidgetInfo) item2).getPackageName());
					if(date1!=null&&date2!=null)
					{
						if(date2.compareTo(date1)!=0)
						{						
							return date2.compareTo(date1);
						}						
					}
				}
				if (item1.getCatagoryNo() == item2.getCatagoryNo()) {
					if (item1.spanX == item2.spanX) {
						return item1.spanY - item2.spanY;
					}
					return item1.spanX - item2.spanX;
				}
				return item1.getCatagoryNo() - item2.getCatagoryNo();
			}
		});
	}
	
	public void sortDarwerWidgetInfo(ArrayList<LauncherWidgetInfo> list) {
		if (null == list || list.size() == 0) {
			return;
		}
		Collections.sort(list, new Comparator<LauncherWidgetInfo>() {
			@Override
			public int compare(LauncherWidgetInfo item1, LauncherWidgetInfo item2) {
				Date date1=LauncherWidgetInfoManager.getInstance().getWidetInstallTime(item1.getPackageName());
				Date date2=LauncherWidgetInfoManager.getInstance().getWidetInstallTime(item2.getPackageName());
				if(date1!=null&&date2!=null)
				{
					if(date2.compareTo(date1)!=0)
					{						
						return date2.compareTo(date1);
					}						
				}
				if (item1.getCatagoryNo() == item2.getCatagoryNo()) {
					if (item1.spanX == item2.spanX) {
						return item1.spanY - item2.spanY;
					}
					return item1.spanX - item2.spanX;
				}
				return item1.getCatagoryNo() - item2.getCatagoryNo();
			}
		});
	}

	/**
	 * 初始化匣子里面的小部件
	 */
	private void initWidgetsData() {
		mAllLauncherWidgetInfo.clear();
		ArrayList<LauncherWidgetInfo> listAllInstalledWidgetInfo = loadAllInstalledLauncherWidgetInfos(false);
		sortDarwerWidgetInfo(listAllInstalledWidgetInfo);
		ArrayList <LauncherWidgetInfo> listLauncherWidgetInside = loadAllLauncherWidgetInside();
		mAllLauncherWidgetInfo.addAll(listAllInstalledWidgetInfo);
		mAllLauncherWidgetInfo.addAll(listLauncherWidgetInside);
		mAllLauncherItemInfos.addAll(mAllLauncherWidgetInfo);
		Collections.sort(mAllLauncherItemInfos, WIDGET_TITLE_COMPARATOR);
	}

	/**
	 * 加载已经安装的小部件信息
	 * 
	 * @return
	 */
	public ArrayList<LauncherWidgetInfo> loadAllInstalledLauncherWidgetInfos(boolean execepHuangli) {
		ArrayList<LauncherWidgetInfo> listAllWidgetInfos = new ArrayList<LauncherWidgetInfo>();
		PackageManager pm = Global.getApplicationContext().getPackageManager();
		Intent it = new Intent(PANDA_WIDGET_CATEGORY_QUERY_INTENT);

		installedPandaWidgetPkgMap.clear();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(it, 0);
		for (int i = 0; i < resolveInfoList.size(); i++) {
			try {
				boolean addToList = true;
				ResolveInfo resolveInfo = resolveInfoList.get(i);
				ActivityInfo ai = resolveInfo.activityInfo;
				String packageName = ai.applicationInfo.packageName;
				//为避免ingoreInstalledWidgetMap还未初始化完就进入到编辑模式引起无法过滤的问题
				if(ingoreInstalledWidgetMap.size()==0){
					initIngoreInstalledWidgetsData();
				}
				if (!mSupportWidgetPackage.contains(packageName)) {
					continue;
				}
				installedPandaWidgetPkgMap.put(packageName, "");
				ArrayList<LauncherWidgetInfo> listWidgetInfo = parseInstalledWidgetXml(packageName);
				if (listWidgetInfo == null)
					continue;
				
				for (LauncherWidgetInfo info : listWidgetInfo) {
					// 英文版屏蔽内置黄历天气 caizp 2013-5-6
					if (!Global.isZh() || execepHuangli) {
						if(Global.getApplicationContext().getPackageName().equals(info.getPackageName()) 
								&& ("weather_widget_panda_4x1".equals(info.getLayoutResName()) || "weather_widget_panda_4x2".equals(info.getLayoutResName())) ) {
							addToList = false;
							break;
						}
					}
					info.setPreviewTitle(info.getTitle() + "(" + info.getTip() + ")");
				}
				if (addToList) {
					listAllWidgetInfos.addAll(listWidgetInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listAllWidgetInfos;
	}
	
	/**
	 * 加载所有已经安装的小部件包名
	 * 
	 * @return
	 */
	public static List<String> loadAllInstalledWidgetPackageName(){
		HashSet<String> packageNames = new HashSet<String>();
		PackageManager pm = Global.getApplicationContext().getPackageManager();
		Intent it = new Intent(PANDA_WIDGET_CATEGORY_QUERY_INTENT);
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(it, 0);
		for (ResolveInfo resolveInfo : resolveInfoList) {
			if(resolveInfo.activityInfo != null){
				String packageName = resolveInfo.activityInfo.packageName;
				if(!StringUtil.isEmpty(packageName)){
					if(Global.getApplicationContext().getPackageName().equals(packageName))
					{
						continue;
					}else{
						packageNames.add(packageName);
					}
				}
			}
		}
		String[] rtns = new String[0];
		rtns = packageNames.toArray(rtns);
		return Arrays.asList(rtns);
	}

	/**
	 * 加载特定小部件的信息
	 * 
	 * @param packageName
	 * @return
	 */
	private ArrayList<LauncherWidgetInfo> loadLauncherWidgetInfoByPackageName(String packageName) {
		return parseInstalledWidgetXml(packageName);
	}

	/**
	 * 读取已经安装的小部件配置数据
	 * 
	 * @param packageName
	 * @return 如果小部件不包含widget.xml配置数据，则返回null
	 */
	public ArrayList<LauncherWidgetInfo> parseInstalledWidgetXml(String packageName) {
		try {
			ForeignPackage fp = new ForeignPackage(mContext, packageName, false);
			int xmlResId = fp.getResourceID(WIDGET_INFO_FILE_NAME, "xml");
			if (xmlResId != 0xffffffff && xmlResId != 0) {
				// 在XML文件里若包含了widget.xml信息文件，则取小部件里的相关信息
				XmlResourceParser fpXrp = fp.getXML(xmlResId);
				return parserPandaWidgetXML(fp, fpXrp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析外部包里面的panda_widget.xml信息
	 * 
	 * @param fp
	 * @param xrp
	 * @return
	 */
	private ArrayList<LauncherWidgetInfo> parserPandaWidgetXML(ForeignPackage fp, XmlResourceParser xrp) {
		ArrayList<LauncherWidgetInfo> infos = new ArrayList<LauncherWidgetInfo>();
		int type;
		try {
			int depth = xrp.getDepth();
			XmlUtils.beginDocument(xrp, TAB_PANDAWIDGETS);
			while (((type = xrp.next()) != XmlPullParser.END_TAG || xrp.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
				if (type != XmlPullParser.START_TAG) {
					continue;
				}
				LauncherWidgetInfo info = new LauncherWidgetInfo();
				for (int i = 0; i < xrp.getAttributeCount(); i++) {
					String attrName = xrp.getAttributeName(i);
					String attrValue = xrp.getAttributeValue(i);
					if (attrName.equalsIgnoreCase("packageName")) {
						info.setPackageName(attrValue);
					} else if (attrName.equalsIgnoreCase("layout")) {
						info.setLayoutResName(attrValue);
					} else if (attrName.equalsIgnoreCase("previewName")) {
						info.setPreviewImageResName(attrValue);
					} else if (attrName.equalsIgnoreCase("title")) {
						info.setTitleResName(attrValue);
						info.setTitle(fp.getString(fp.getResourceID(attrValue, "string")));
						info.setPreviewTitle(info.getTitle());
					} else if (attrName.equalsIgnoreCase("spanX")) {
						info.setSpanX(Integer.parseInt(attrValue));
					} else if (attrName.equalsIgnoreCase("spanY")) {
						info.setSpanY(Integer.parseInt(attrValue));
					}
				}
				info.setInstalled(true);
				info.setType(LauncherWidgetInfo.TYPE_OUTSIDE);
				info.setTip(info.getSpanX() + "x" + info.getSpanY());
				infos.add(info);
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infos;
	}

	/**
	 * 检测小部件数据是否已经初始化
	 */
	private void checkWidgetDataHasInit() {
		if (null == mAllLauncherItemInfos) {
			mAllLauncherItemInfos = new ArrayList<LauncherItemInfo>();
			init();
		}
	}

	public void clearAllLauncherItemInfos() {
		if (mAllLauncherItemInfos == null)
			return;

		mAllLauncherItemInfos.clear();
		mAllLauncherItemInfos = null;
		mAllLauncherWidgetInfo.clear();
		mAllRecommentWidgetInfo.clear();
	}

	/**
	 * 判断此包名对应的程序是否是外部小部件
	 * 
	 * @param packageName
	 */
	private boolean checkLauncherWidgetInfoExistByPackageName(String packageName) {
		for (LauncherWidgetInfo widgetInfo : mAllLauncherWidgetInfo) {
			if (LauncherWidgetInfo.TYPE_OUTSIDE == widgetInfo.type && widgetInfo.getPackageName().equals(packageName))
				return true;
		}
		return false;
	}
	
	/**
	 * 获取安装时间
	 * @param packageName
	 * @return
	 */
	public Date getWidetInstallTime(String packageName)
	{
		PackageManager pm = Global.getApplicationContext().getPackageManager();
		try
		{
			PackageInfo pi=pm.getPackageInfo(packageName, 0);
			Field field=PackageInfo.class.getField("firstInstallTime");
			long timestamp = field.getLong(pi);
		    return new Date(timestamp);

		} catch (NameNotFoundException e)
		{
			return null;
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Comparator<ICommonDataItem> WIDGET_TITLE_COMPARATOR = new ApplicationInfoTitleComparator();

	private static class ApplicationInfoTitleComparator implements Comparator<ICommonDataItem> {

		@Override
		public int compare(ICommonDataItem a, ICommonDataItem b) {
			return ComparatorUtil.compare(getTitle(a), getTitle(b));
		}
		
		public boolean isWidget(ICommonDataItem item){
			return item instanceof LauncherWidgetInfo;
		}
		
		public String getTitle(ICommonDataItem item){
			if(isWidget(item)){
				return ((LauncherWidgetInfo)item).getTitle();
			}	
			return "";
		}
	}
	
}
