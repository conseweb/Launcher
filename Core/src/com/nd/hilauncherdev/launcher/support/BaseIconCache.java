/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nd.hilauncherdev.launcher.support;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.DisplayMetrics;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.cache.DiskCache;
import com.nd.hilauncherdev.cache.DiskCacheSchemaB;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.BaseBitmapUtils;
import com.nd.hilauncherdev.kitset.util.ReflectUtil;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.config.preference.BaseSettingsPreference;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.theme.ThemeManagerFactory;
import com.nd.hilauncherdev.theme.data.BaseThemeData;
import com.nd.hilauncherdev.theme.data.ThemeGlobal;
import com.nd.hilauncherdev.theme.module.ModuleConstant;
import com.nd.hilauncherdev.theme.module.ThemeModuleItem;
import com.nd.hilauncherdev.theme.pref.ThemeSharePref;

/**
 * Cache of application icons. Icons can be made from any thread.
 */
public class BaseIconCache{
	static final String TAG = "Launcher.IconCache";

	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
	
	public static final String DEFAULT_THEME_NAME_LARGE = "v6_large";
	
	public static final String DEFAULT_THEME_NAME_SMALL = "v6_small";
	
	//主线程消息队列
	public MessageQueue messageQueue;

	/**
	 * 缓存的应用程序图标与名称 <br>
	 * Author:ryan <br>
	 * Date:2012-7-25上午11:43:19
	 */
	public static class CacheEntry {
		public Bitmap icon;
		public String title;
		/**
		 * 是否为主题图标，主要用于判断是否使用蒙版
		 */
		public boolean isThemeIcon;
		/**
		 * 当一个Package包含两个Main的时候，Icon和Title均不可用
		 */
		public boolean isDirty;

        /**
         * 圖標需要重新獲取
         */
        public boolean isNeedRefresh;
	}

	private Bitmap mDefaultIcon;
	protected final Context mContext;
	protected final PackageManager mPackageManager;
	// private final Utilities.BubbleText mBubble;
	protected final Map<ComponentName, CacheEntry> mCache = new ConcurrentHashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);

	protected final Map<String, Bitmap> mIconCache91 = new ConcurrentHashMap<String, Bitmap>(30);

	/**
	 * 用于只有PackageName的应用使用，如"我的存储"中的内容
	 */
	private final Map<String, CacheEntry> mPackageCache = new ConcurrentHashMap<String, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);

	private int mIconDpi;
	
	public BaseIconCache(Context context) {
		mContext = context;
		mPackageManager = context.getPackageManager();
		// mBubble = new Utilities.BubbleText(context);
		mIconDpi = getLauncherLargeIconDensity(mContext);
		messageQueue = Looper.getMainLooper().myQueue();
	}

	private Bitmap makeDefaultIcon() {
		Drawable d = mPackageManager.getDefaultActivityIcon();
		if(d == null)
			return null;
		Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1), Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		d.setBounds(0, 0, b.getWidth(), b.getHeight());
		d.draw(c);
		return b;
	}

	/**
	 * Remove any records for the supplied ComponentName.
	 */
	public void remove(ComponentName componentName) {
		mCache.remove(componentName);
		mPackageCache.remove(componentName.getPackageName());
	}

	/**
	 * Empty out the cache.
	 */
	public void flush() {
		mCache.clear();
		mPackageCache.clear();
		mIconCache91.clear();

	}

	/**
	 * 加载时候优先初始化 <br>
	 * Description:TODO 方法功能描述 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:29:16
	 */
	public void getTitleAndIcon(ApplicationInfo application) {
		if (application == null || application.intent == null || application.componentName == null)
			return;

		final PackageManager pm = mContext.getPackageManager();
		ResolveInfo resolve = AndroidPackageUtils.getResolveInfo(application.intent, pm);
		if (resolve == null)
			return;

		CacheEntry entry = cacheLocked(application.title, application.componentName, resolve);
		if (entry != null) {
			if (StringUtil.isEmpty(application.title)) {
				application.title = entry.title;
			}

			if (!application.customIcon) {
				application.iconBitmap = entry.icon;
				application.useIconMask = !entry.isThemeIcon;
			}
		}
	}

	/**
	 * Fill in "application" with the icon and label for "info."
	 */
	public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info) {
		if (null == application || info == null) {
			return;
		}

		CacheEntry entry = cacheLocked(application.title, application.componentName, info);
		if (entry != null) {
			if (StringUtil.isEmpty(application.title)) {
				application.title = entry.title;
			}

			if (!application.customIcon) {
				application.iconBitmap = entry.icon;
				application.useIconMask = !entry.isThemeIcon;
			}
		}
	}

	/**
	 * 兼容接口 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:17:23
	 * 
	 * @param application
	 * @return
	 */
	public Bitmap getIcon(ApplicationInfo application) {
		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(application.intent, 0);
		ComponentName component = application.intent.getComponent();

		if (resolveInfo == null || component == null) {
			return getmDefaultIcon();
		}

		CacheEntry entry = cacheLocked(component, resolveInfo);
		application.useIconMask = !entry.isThemeIcon;
		return entry.icon;
	}

	/**
	 * 搜索结果使用 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:20:55
	 */
	public Bitmap getIcon(Intent intent) {
		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
		ComponentName component = intent.getComponent();

		if (resolveInfo == null || component == null) {
			return getmDefaultIcon();
		}

		CacheEntry entry = cacheLocked(component, resolveInfo);
		return entry.icon;
	}

	/**
	 * 重命名的不取名称 <br>
	 * Author:ryan <br>
	 * Date:2012-5-9下午10:34:34
	 */
	private CacheEntry cacheLocked(CharSequence title, ComponentName componentName, ResolveInfo info) {
		if (StringUtil.isEmpty(title)) {
			return cacheLocked(componentName, info);
		}

		if (componentName == null || info == null) {
			return null;
		}
		CacheEntry entry = mCache.get(componentName);
		if (entry == null) {
			entry = new CacheEntry();
			mCache.put(componentName, entry);
			entry.title = title.toString();
			Drawable d = getThemeAppIcon(StringUtil.getAppKey(componentName));
			if (null == d) {//不在主题中
				String key = DiskCache.getCacheFilename(componentName);
				String themeName = getThemeId();
				entry.icon = DiskCacheSchemaB.getInstance().getBitmapFromDiskCache(DiskCache.APK_TYPE, key, themeName, info, this, null);
			} else {
				entry.isThemeIcon = true;
				entry.icon = BaseBitmapUtils.drawable2Bitmap(d);
			}
			// 更新动态图标
			updateDynamicIcon(mContext, entry, componentName, false);
		}else { //判斷是否需要刷新
            if(entry.isNeedRefresh && Build.VERSION.SDK_INT > 10) {
                entry.isNeedRefresh = false;
                Drawable d = getThemeAppIcon(StringUtil.getAppKey(componentName));
                if (null == d) {//不在主题中
                    String key = DiskCache.getCacheFilename(componentName);
                    String themeName = getThemeId();
                    entry.icon = DiskCacheSchemaB.getInstance().getBitmapFromDiskCache(DiskCache.APK_TYPE, key, themeName, info, this, entry.icon);
                } else {
                    entry.isThemeIcon = true;
                    entry.icon = BaseBitmapUtils.drawable2Bitmap(d);
                }
                // 更新动态图标
                updateDynamicIcon(mContext, entry, componentName, false);
            }
        }
		return entry;
	}

	/**
	 * 获取应用程序图标与名称 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:19:20
	 */
	private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info) {
		if(componentName == null)
			return null;
		CacheEntry entry = mCache.get(componentName);
		if (entry == null) {
			entry = new CacheEntry();

			mCache.put(componentName, entry);
			// componentName为空但PackageName不为空说明一个Package带多个Main，
			if (mPackageCache.get(componentName.getPackageName()) != null) {
				CacheEntry dirtyEntry = mPackageCache.get(componentName.getPackageName());
				dirtyEntry.isDirty = true;
			} else {
				mPackageCache.put(componentName.getPackageName(), entry);
			}
			entry.title = info.loadLabel(mPackageManager).toString();
			if (entry.title == null) {
				entry.title = info.activityInfo.name;
			}
			Drawable d = getThemeAppIcon(StringUtil.getAppKey(componentName));
			if (null == d) {
				String key = DiskCache.getCacheFilename(componentName);
				String themeName = getThemeId();
				entry.icon = DiskCacheSchemaB.getInstance().getBitmapFromDiskCache(DiskCache.APK_TYPE, key,
                        themeName, info, this, null);
			} else {
				entry.isThemeIcon = true;
				entry.icon = BaseBitmapUtils.drawable2Bitmap(d);
			}
			// 更新动态图标
			updateDynamicIcon(mContext, entry, componentName, false);
		}else { //判斷是否需要刷新
            if(entry.isNeedRefresh && Build.VERSION.SDK_INT > 10) {
                entry.isNeedRefresh = false;
                Drawable d = getThemeAppIcon(StringUtil.getAppKey(componentName));
                if (null == d) {
                    String key = DiskCache.getCacheFilename(componentName);
                    String themeName = getThemeId();
                    entry.icon = DiskCacheSchemaB.getInstance().getBitmapFromDiskCache(DiskCache.APK_TYPE, key,
                            themeName, info, this, entry.icon);
                } else {
                    entry.isThemeIcon = true;
                    entry.icon = BaseBitmapUtils.drawable2Bitmap(d);
                }
                // 更新动态图标
                updateDynamicIcon(mContext, entry, componentName, false);
            }
        }
		return entry;
	}

	public Bitmap getCachedIcon(ComponentName cn) {
		if (cn == null)
			return null;

		CacheEntry entry = mCache.get(cn);

		return entry == null ? null : entry.icon;
	}

	public String getCachedTitle(ComponentName cn) {
		if (cn == null)
			return null;

		CacheEntry entry = mCache.get(cn);

		return entry == null ? null : entry.title;
	}

	/**
	 * 创建缓存 <br>
	 * Author:ryan <br>
	 * Date:2012-7-27上午10:03:08
	 */
	public void makeCache(ApplicationInfo app) {
		if (app == null)
			return;

		if (app.intent == null || app.intent.getComponent() == null)
			return;

		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(app.intent, 0);
		if (resolveInfo == null)
			return;

		this.getTitleAndIcon(app, resolveInfo);
	}

	/**
	 * 通过包名获取图标与标题信息<br>
	 * 1. 图标未加载时返回null<br>
	 * 2. 脏数据，即一个package包含两个main，返回null<br>
	 * 3. 无此数据，返回null<br>
	 * <br>
	 * Author:ryan <br>
	 * Date:2012-7-25下午09:31:26
	 * 
	 * @param packageName
	 *            应用程序包名
	 */
	public CacheEntry getPackageIconAndTitle(String packageName) {
		CacheEntry cache = mPackageCache.get(packageName);
		if (cache == null)
			return null;

		if (cache.isDirty)
			return null;

		return cache;
	}
	
	/**
	 * 刷新单个应用图标
	 */
	public Bitmap refreshTheCache(ApplicationInfo app) {
		if (app == null)
			return null;

		if (app.intent == null || app.intent.getComponent() == null)
			return null;

		CacheEntry ce = mCache.get(app.intent.getComponent());
		if (ce == null)
			return null;
		
		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(app.intent, 0);
		if (resolveInfo == null)
			return null;

		Drawable d = getThemeAppIcon(StringUtil.getAppKey(app.intent.getComponent()));
		if (null == d) {
			d = resolveInfo.loadIcon(mPackageManager);
			ce.isThemeIcon = false;
			String key = DiskCache.getCacheFilename(app.intent.getComponent());
			String themeName = getThemeId();
			ce.icon = DiskCacheSchemaB.getInstance().getBitmapFromDiskCache(DiskCache.APK_TYPE, key, themeName,
                    resolveInfo, this,  ce.icon);
		} else {
			ce.isThemeIcon = true;
			ce.icon = BaseBitmapUtils.drawable2Bitmap(d);
		}
		return ce.icon;
	}

	/**
	 * 延迟加载 <br>
	 * Author:ryan <br>
	 * Date:2012-10-30下午08:06:20
	 * 
	 * @return
	 */
	public Bitmap getmDefaultIcon() {
		if (mDefaultIcon == null || mDefaultIcon.isRecycled()) {
			mDefaultIcon = makeDefaultIcon();
		}

		return mDefaultIcon;
	}

	public CacheEntry getCacheEntry(ComponentName cn) {
		return mCache.get(cn);
	}
	
	
	
	
	/**
	 * 更新动态图标
	 * @param ctx
	 * @param cacheEntry
	 * @param comp
	 * @param needBroadcast
	 */
	public void updateDynamicIcon(Context ctx, CacheEntry cacheEntry, ComponentName comp, boolean needBroadcast){
		
	}
	
	/**
	 * 获取主题图标
	 * @param key
	 * @return
	 */
	public Drawable getThemeAppIcon(String key){
        return ThemeManagerFactory.getInstance().getThemeAppIcon(key);
    }

    /**
	 * 刷新主题图标 
	 */
	public void refreshThemeIcon() {
		boolean isLargeIconTheme = false;
		int largeIconSize = (int) mContext.getResources().getDimensionPixelSize(R.dimen.app_background_size);
		clearIconCache();
		if (!isLargeIconTheme) {// 无主题图标时，判断图标蒙板背景
			Drawable phoneIcon = getThemeAppIcon(BaseThemeData.ICON_PHONE);
			if (null != phoneIcon) {
				if (null != phoneIcon && phoneIcon.getIntrinsicWidth() >= largeIconSize && phoneIcon.getIntrinsicHeight() >= largeIconSize) {
					isLargeIconTheme = true;
				}
			}
		}
		BaseSettingsPreference.getInstance().setLargeIconTheme(isLargeIconTheme);
	}
	
	/**
	 * 获取图标接口适配4.1以上
	 * @author Michael
	 * Date:2014-4-2下午4:24:47
	 *  @param info
	 *  @return
	 */
	public Drawable loadDrawable(ResolveInfo info) {
		if (Build.VERSION.SDK_INT < 16) {
			return info.loadIcon(mPackageManager);
		} else {
			Resources resources;
			try {
				resources = mPackageManager
						.getResourcesForApplication(info.activityInfo.applicationInfo);
			} catch (PackageManager.NameNotFoundException e) {
				resources = null;
			}
			if (resources != null) {
				int iconId = info.activityInfo.icon == 0 ?
						info.activityInfo.applicationInfo.icon : info.activityInfo.icon;
				if (iconId != 0) {
					return getFullResIcon(resources, iconId, mPackageManager,
							mIconDpi);
				}
			}
			return mPackageManager.getDefaultActivityIcon();
		}
	}

	/**
	 * 
	 * @author Michael
	 * Date:2014-4-2下午4:25:21
	 *  @param resources
	 *  @param iconId
	 *  @param mPackageManager
	 *  @param mIconDpi
	 *  @return
	 */
	private Drawable getFullResIcon(Resources resources, int iconId,
			PackageManager mPackageManager, int mIconDpi) {
		Drawable d;
		try {
			d = (Drawable) ReflectUtil.invokeMethod(Resources.class.getMethod(
					"getDrawableForDensity", new Class[] { Integer.TYPE,
							Integer.TYPE }), resources, iconId, mIconDpi);
		} catch (Exception e) {
			e.printStackTrace();
			d = null;
		}
		return (d != null) ? d : mPackageManager.getDefaultActivityIcon();
	}

	/**
	 * 获取屏幕dpi
	 * @author Michael
	 * Date:2014-4-2下午4:25:36
	 *  @param mContext
	 *  @return
	 */
	public int getLauncherLargeIconDensity(Context mContext) {
		final Resources res = mContext.getResources();
		if(res == null){
			return 320;
		}
		
		final int density = res.getDisplayMetrics().densityDpi;
		if (Build.VERSION.SDK_INT < 16) {
			return density;
		} else {
			int sw = 0;
			try {
				sw = (Integer) (ReflectUtil.getFieldValueByFieldName(
						res.getConfiguration(), "smallestScreenWidthDp"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (sw < 600) {
				return density;
			}

			switch (density) {
			case DisplayMetrics.DENSITY_LOW:
				return DisplayMetrics.DENSITY_MEDIUM;
			case DisplayMetrics.DENSITY_MEDIUM:
				return DisplayMetrics.DENSITY_HIGH;
			case 213/* DisplayMetrics.DENSITY_TV */:
				return 320/* DisplayMetrics.DENSITY_XHIGH */;
			case DisplayMetrics.DENSITY_HIGH:
				return 320/* DisplayMetrics.DENSITY_XHIGH */;
			case 320/* DisplayMetrics.DENSITY_XHIGH */:
				return 320/* DisplayMetrics.DENSITY_XXHIGH */;
			case 480/* DisplayMetrics.DENSITY_XXHIGH */:
				return 320 * 2/* DisplayMetrics.DENSITY_XHIGH * 2 */;
			default:
				// The density is some abnormal value. Return some other
				// abnormal value that is a reasonable scaling of it.
				return (int) ((density * 1.5f) + .5f);
			}
		}
	}
	
	public PackageManager getmPackageManager() {
		return mPackageManager;
	}

	/**
	 * 获取主题名称
	 * @author Michael
	 * Date:2014-5-26上午8:53:45
	 *  @return
	 */
	public String getThemeId() {
        String themeId = getThemeModuleId();
		boolean isDefaultThemeWithDefaultModuleId = isDefaultThemeWithDefaultModuleId(mContext);
		if (isDefaultThemeWithDefaultModuleId) {
			boolean isLargeIconMode = BaseConfig.isLargeIconMode();
			if (isLargeIconMode) {
				themeId = DEFAULT_THEME_NAME_LARGE;
			} else {
				themeId = DEFAULT_THEME_NAME_SMALL;
			}
		}else{
			if(!BaseConfig.isOnScene() && !BaseSettingsPreference.getInstance().isIconMaskEnabled()){
				return DEFAULT_THEME_NAME_SMALL;
			}
 		}
		return themeId;
	}

    /**
     * 换主题时对IconCache做清除动作
     * @author Michael
     * @date 2014-7-21
     */
    public void clearIconCache() {
        //當在2.3.3及以下時，使用bitmap.recycle()進行內存回收
        if(Build.VERSION.SDK_INT < 11) {
           /* try {
                Iterator<Map.Entry<ComponentName, CacheEntry>> iterator = mCache.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<ComponentName, CacheEntry> entry = iterator.next();
                    CacheEntry cacheEntry = entry.getValue();
                    if(cacheEntry.icon != null &&  !cacheEntry.icon.isRecycled()) {
                        cacheEntry.icon.recycle();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            mCache.clear();
			mIconCache91.clear();
        }else { //3.0及以上使用BitmapFactory.Options.inBitmap來對bitmap進行回收使用
            Iterator<Map.Entry<ComponentName, CacheEntry>> iterator = mCache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ComponentName, CacheEntry> entry = iterator.next();
                CacheEntry cacheEntry = entry.getValue();
                cacheEntry.isNeedRefresh = true;
            }
			mIconCache91.clear();
        }
    }

    /**
     * 默认主题且图标模块是默认
     * @param mContext
     * @return
     */
    public static Boolean isDefaultThemeWithDefaultModuleId(Context mContext) {
        String themeId = getThemeModuleId();
        return ThemeSharePref.getInstance(mContext).isDefaultTheme() &&
                themeId.equals(ThemeGlobal.DEFAULT_THEME_ID);
    }

    /**
     * 获取主题的图标模块ID
     * @return
     */
    public static String getThemeModuleId() {
		String themeId = ThemeManagerFactory.getInstance().getCurrentTheme().getThemeId();
		ThemeModuleItem iconModuleItem = ThemeManagerFactory.getInstance().getCurrentTheme().getModuleMap().
				get(ModuleConstant.MODULE_ICONS);
		if (null != iconModuleItem) {
			themeId = iconModuleItem.getId();
		}
		return themeId;
	}

	//外部实现逻辑
	public Bitmap get91IconByKey(String key) {
		return null;
	}
	//外部实现逻辑
	public void put91IconInCache(String key, Bitmap bitmap){

	}
}
