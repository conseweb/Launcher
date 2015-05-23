package com.bitants.launcherdev.launcher;

import android.appwidget.AppWidgetHost;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.common.kitset.util.BaseBitmapUtils;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.kitset.util.SystemUtil;
import com.bitants.launcher.R;
import com.bitants.launcherdev.kitset.config.ConfigPreferences;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.CellLayoutConfig;
import com.bitants.common.launcher.config.ConfigFactory;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.settings.SettingsPreference;
import com.bitants.common.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.common.theme.data.BaseThemeData;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class LauncherProviderHelper {
	private static final String TAG = "LauncherProviderHelper";
	
	/**
	 * 加载初次进入桌面, 解析缺省配置所使用的标签
	 */
	public static final String ANYTHING = "anything";
	public static final String APP = "app";
	public static final String TAG_FAVORITES = "favorites";
	public static final String TAG_FAVORITE = "favorite";
	public static final String TAG_CLOCK = "clock";
	public static final String TAG_SEARCH = "search";
	public static final String TAG_APPWIDGET = "appwidget";
	public static final String TAG_SHORTCUT = "shortcut";
	public static final String TAG_91SHORTCUT = "shortcut91";
	public static final String TAG_DOCK_SHORTCUT = "dock_shortcut";
	public static final String TAG_PANDAWIDGET_PREVIEW = "pandawidgetpreview";
	
	//底部托盘
	public static final String[] defaultDockbarApp = {BaseThemeData.ICON_PHONE, BaseThemeData.ICON_CONTACTS, 
		BaseThemeData.ICON_MMS, BaseThemeData.ICON_BROWSER};
		
	/**
	 * Loads the default set of favorite packages from an xml file.
	 * 
	 * @param db
	 *            The database to write the values into
	 */
	public static void loadFavorites(SQLiteDatabase db, AppWidgetHost mAppWidgetHost, Context mContext) {
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		ContentValues values = new ContentValues();

		try {
			XmlResourceParser parser = null;
			if (!BaseConfig.isZh()) {
				parser = mContext.getResources().getXml(R.xml.default_workspace_en);
			} else {
				parser = mContext.getResources().getXml(R.xml.default_workspace);
			}
			AttributeSet attrs = Xml.asAttributeSet(parser);
			XmlUtils.beginDocument(parser, TAG_FAVORITES);

			final int depth = parser.getDepth();
			final boolean isExLardgeScreen = ScreenUtil.isExLardgeScreen();
			final boolean isSuperLargeScreen = ScreenUtil.isSuperLargeScreenAndLowDensity();
			int type;
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
				if (type != XmlPullParser.START_TAG) {
					continue;
				}

				final String name = parser.getName();

				TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.Favorite);
				int screen = a.getInt(R.styleable.Favorite_screen, 0);
				int cellX = a.getInt(R.styleable.Favorite_x, 0);
				int cellY = a.getInt(R.styleable.Favorite_y, 0);
				if ((isExLardgeScreen||isSuperLargeScreen) &&  (cellY == 2 || cellY == 3)) {// 如果是大屏幕，则将屏幕设置为5x4的
					cellY ++;
				}
				
				values.clear();
				values.put(LauncherSettings.Favorites.CONTAINER, LauncherSettings.Favorites.CONTAINER_DESKTOP);
				values.put(LauncherSettings.Favorites.SCREEN, screen);
				if (TAG_FAVORITE.equals(name)) {
					insertContentValues(values, cellX, cellY, 1, 1);
				} 
				else if (TAG_SHORTCUT.equals(name)) {
					insertContentValues(values, cellX, cellY, 1, 1);
				} 
				else if (TAG_DOCK_SHORTCUT.equals(name)) {
					insertContentValues(values, cellX, cellY, 1, 1);
					addUriDockShortcut(mContext, db, values, a);
				} 
				else if (APP.equals(name)) {
					insertContentValues(values, cellX, cellY, 1, 1);
					addApp(mContext, db, values, a);
				} 
				a.recycle();
			}

			initLauncherApp(db, mContext);
		} catch (XmlPullParserException e) {
			Log.w(TAG, "Got exception parsing favorites.", e);
		} catch (IOException e) {
			Log.w(TAG, "Got exception parsing favorites.", e);
		}

		return;
	}
	
	public static long createSysAppFolder(SQLiteDatabase db, int cellX, int cellY, int screen){
		ContentValues values = new ContentValues();
		values.put(Favorites.CELLX, cellX);
		values.put(Favorites.CELLY, cellY);
		values.put(Favorites.SPANX, 1);
		values.put(Favorites.SPANY, 1);
		values.put(Favorites.SCREEN, screen);
		values.put(Favorites.CONTAINER, Favorites.CONTAINER_DESKTOP);
		values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_USER_FOLDER);
		values.put(Favorites.TITLE, "系统应用");
		return db.insert(LauncherProvider.TABLE_FAVORITES, null, values);
	}
	
	public static void createAppslist(Context mContext , SQLiteDatabase db, int cellX, int cellY, int screen) {
		final Resources r = mContext.getResources();
		int title = R.string.dockbar_dock_drawer;
		ApplicationInfo info = LauncherMainDockShortcutHelper.createLauncherAppslistInfo(mContext, mContext.getString(title));
		ContentValues values = new ContentValues();
		LauncherProviderHelper.insertContentValues(values, cellX, cellY, 1, 1);
		values.put(Favorites.SCREEN,screen);
		values.put(Favorites.CONTAINER, Favorites.CONTAINER_DESKTOP);
		values.put(Favorites.TITLE, r.getString(title));
		values.put(Favorites.INTENT, info.intent != null ? info.intent.toUri(0) : null);
		values.put(Favorites.ITEM_TYPE, info.itemType);
		values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, LauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE);
		if (info.iconResource != null) {
			values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, info.iconResource.packageName);
			values.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, info.iconResource.resourceName);
		}
		db.insert(LauncherProvider.TABLE_FAVORITES, null, values);
	}
	
	public static void initLauncherApp(SQLiteDatabase db, final Context mContext){
		PackageManager pm = mContext.getPackageManager();
		final List<ResolveInfo> allApps = AndroidPackageUtils.queryMainIntentActivity(pm);
		ContentValues values;
		int countX = BaseSettingsPreference.getInstance().getScreenCountX();
		int countY = BaseSettingsPreference.getInstance().getScreenCountY();
		int startScreen = 2;
		int startCount = countX * (countY - 2);
		
		int folderID = (int) createSysAppFolder(db, startCount % countX, (startCount / countX) % countY,
				startCount / (countX*countY) + startScreen);
		int folderAppCount = 0;
		startCount++;
		
		for(ResolveInfo resInfo : allApps){
			String pck = resInfo.activityInfo.packageName ;
			String clazz = resInfo.activityInfo.name ;
			if(isDefalutDockbarApp(pck, clazz))//默认Dock栏4个图标
				continue;
			
			values = new ContentValues();
			
			if(resInfo.activityInfo.applicationInfo != null 
					&& SystemUtil.isSystemApplication(resInfo.activityInfo.applicationInfo.flags)){
				values.put(Favorites.CELLX, 1);
				values.put(Favorites.CELLY, 1);
				values.put(Favorites.SCREEN, folderAppCount ++);
				values.put(Favorites.CONTAINER, folderID);
			}else{
				values.put(Favorites.CELLX, startCount % countX);
				values.put(Favorites.CELLY, (startCount / countX) % countY);
				values.put(Favorites.SCREEN, startCount / (countX*countY) + startScreen);
				values.put(Favorites.CONTAINER, Favorites.CONTAINER_DESKTOP);
				startCount ++;
			}
			
			values.put(Favorites.SPANX, 1);
			values.put(Favorites.SPANY, 1);
			values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_APPLICATION);
			values.put(Favorites.INTENT, AndroidPackageUtils.getNewTaskIntent(new ComponentName(pck, clazz)).toUri(0));
			CharSequence title = resInfo.activityInfo.loadLabel(pm);
			if (title == null)
				title = clazz ;
			values.put(Favorites.TITLE, title.toString());
			values.put(Favorites.ICON_TYPE, Favorites.ICON_TYPE_RESOURCE);
//			boolean isSystemApp = AndroidPackageUtils.isSystemPackage(resInfo.activityInfo.applicationInfo);
//			values.put(Favorites.IS_SYSTEM_APP, isSystemApp ? Favorites.APP_TYPE_SYSTEM : Favorites.APP_TYPE_USER);
//			try {
//				String sourceDir = resInfo.activityInfo.applicationInfo.sourceDir;
//				if (!StringUtil.isEmpty(sourceDir)) {
//					long installTime = new File(sourceDir).lastModified();
//					values.put(Favorites.INSTALL_TIME, installTime);
//				}
//			} catch (Exception e) {
//				Log.e(TAG, sourceDir + " can't open!");
//			}
			db.insert(LauncherProvider.TABLE_FAVORITES, null, values);
			
		}
		
		createAppslist(mContext , db, startCount % countX, (startCount / countX) % countY,startCount / (countX*countY) + startScreen);
		
		int screenCount = (startCount - 1) / (countX*countY) + startScreen + 1;
		if(ScreenViewGroup.DEFAULT_SCREEN  != screenCount){			
			ScreenViewGroup.DEFAULT_SCREEN = screenCount;
			ConfigFactory.saveScreenCount(mContext, ScreenViewGroup.DEFAULT_SCREEN);
			if(BaseConfig.getBaseLauncher() != null && ((Launcher)BaseConfig.getBaseLauncher()).getScreenViewGroup() != null){
				((Launcher)BaseConfig.getBaseLauncher()).handler.post(new Runnable(){
					@Override
					public void run() {
						Workspace mWorkspace = (Workspace) ((Launcher)BaseConfig.getBaseLauncher()).getScreenViewGroup();
						for(int i = mWorkspace.getChildCount(); i < ScreenViewGroup.DEFAULT_SCREEN; i ++){
							CellLayout cl = new CellLayout(mContext);
							mWorkspace.addView(cl);
							cl.setCellLayoutLocation(i);
							cl.setWorkspace(mWorkspace);
							cl.setOnLongClickListener((Launcher)BaseConfig.getBaseLauncher());
						}
						
						mWorkspace.getLightBar().setSize(ScreenViewGroup.DEFAULT_SCREEN);
					}
					
				});
				
			}
		}
	}
	
	public static boolean isDefalutDockbarApp(String pck, String cls) {
		return isDefaultApp(defaultDockbarApp, pck, cls);
	}
	
	private static boolean isDefaultApp(String[] defaultApp, String pck, String cls) {
		if (pck == null || cls == null)
			return false;
		//这个顺序就是在底部托盘的顺序
		final String[] pckClsArr = defaultApp;
		ThemeIconIntentAdaptation ada = ThemeIconIntentAdaptation.getInstance();
		for (String pckCls : pckClsArr) {
			String[] arr = ada.getActualApplicationPackageAndClassName(pckCls);
			if (null != arr && pck.equals(arr[0]) && cls.equals(arr[1])) {
				return true;
			}
		}
		return false;
	}
	
	public static void insertContentValues(ContentValues values, int cellX, int cellY, int spanX, int spanY) {	
		values.put(Favorites.CELLX, cellX);
		values.put(Favorites.CELLY, cellY);
		values.put(Favorites.SPANX, spanX);
		values.put(Favorites.SPANY, spanY);
	}
	
	/**
	 * 添加指定应用到屏幕
	 */
	public static void addApp(Context mContext, SQLiteDatabase db, ContentValues values, TypedArray a) {
		Intent intent = AndroidPackageUtils.getNewTaskIntent(ThemeIconIntentAdaptation.getInstance().getActualComponent(
				a.getString(R.styleable.Favorite_uri)));
		values.put(Favorites.INTENT, intent.toUri(0));
		db.insert(LauncherProvider.TABLE_FAVORITES, null, values);
	}
	
	/**
	 * 添加到底部托盘
	 */
	public static boolean addUriDockShortcut(Context mContext, SQLiteDatabase db, ContentValues values, TypedArray a) {
		String uri = a.getString(R.styleable.Favorite_uri);
		if (StringUtil.isEmpty(uri))
			return false;

		String iconPackage = a.getString(R.styleable.Favorite_iconPackage);
		String iconResource = a.getString(R.styleable.Favorite_iconResource);
		if (!StringUtil.isEmpty(iconPackage) && !StringUtil.isEmpty(iconResource)) {
			return createHiDockShortcut(mContext, db, values, a);
		}

		Intent intent = null;
		ComponentName componentName = ThemeIconIntentAdaptation.getInstance().getActualComponent(uri);
		if (componentName == null && ThemeIconIntentAdaptation.isDefaultDockAppByUri(uri)) {
			try {
				intent = Intent.parseUri(uri, 0);
				values.put(Favorites.TITLE, mContext.getResources().getString(a.getResourceId(R.styleable.Favorite_title, 0)));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			intent = AndroidPackageUtils.getNewTaskIntent(componentName);
		}

		if (null != componentName) {// 360手机图标适配
			if (componentName.getPackageName().equals("com.qihoo360.contacts")
					&& componentName.getClassName().equals("com.qihoo360.contacts.ui.mainscreen.MainTabBase")) {
				if (uri.equals("com.android.contacts|com.android.contacts.dialtactsactivity")) {
					intent.setAction(Intent.ACTION_DIAL);
				} else if (uri.equals("com.android.mms|com.android.mms.ui.conversationlist")) {
					intent.setAction(Intent.ACTION_MAIN);
					intent.setData(Uri.parse("content://mms-sms/"));
				} else if (uri.equals("com.android.contacts|com.android.contacts.dialtactscontactsentryactivity")) {
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("content://com.android.contacts/contacts"));
				}
			}
		}
		return createAppDockShortcut(intent, db, values);
	}
	
	private static boolean createHiDockShortcut(Context mContext, SQLiteDatabase db, ContentValues values, TypedArray a) {
		Resources r = mContext.getResources();
		Bitmap bitmap = null;
		final int iconResId = a.getResourceId(R.styleable.Favorite_icon, 0);
		String iconPackage = a.getString(R.styleable.Favorite_iconPackage);
		String iconResource = a.getString(R.styleable.Favorite_iconResource);
		final int titleResId = a.getResourceId(R.styleable.Favorite_title, 0);

		Intent intent;
		String uri = null;
		try {
			uri = a.getString(R.styleable.Favorite_uri);
			intent = Intent.parseUri(uri, 0);
		} catch (URISyntaxException e) {
			Log.w(TAG, "Shortcut has malformed uri: " + uri);
			return false; // Oh well
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		values.put(Favorites.INTENT, intent.toUri(0));
		values.put(Favorites.TITLE, r.getString(titleResId));
		values.put(Favorites.CONTAINER, Favorites.CONTAINER_DOCKBAR);
//		if (CustomIntent.ACTION_OPEN_DRAWER.equals(intent.getAction())) {
//			values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_INDEPENDENCE);
//		} else 
		if (intent.getComponent() == null) {
			values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_CUSTOM_INTENT);
		} else {
			values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_SHORTCUT);
		}
		values.put(Favorites.ICON_TYPE, Favorites.ICON_TYPE_RESOURCE);
		values.put(Favorites.ICON_PACKAGE, iconPackage);
		values.put(Favorites.ICON_RESOURCE, iconResource);
		if (iconResId != 0) {
			values.put(Favorites.ICON_TYPE, Favorites.ICON_TYPE_BITMAP);
			bitmap = BaseBitmapUtils.createIconBitmapThumbnail(r.getDrawable(iconResId), mContext);
			if (bitmap != null) {
				values.put(Favorites.ICON, BaseBitmapUtils.bitmap2Bytes(bitmap));
			}
		}

		db.insert(LauncherProvider.TABLE_FAVORITES, null, values);
		return true;
	}
	
	private static boolean createAppDockShortcut(Intent intent, SQLiteDatabase db, ContentValues values) {
		values.put(Favorites.INTENT, intent.toUri(0));
		values.put(Favorites.CONTAINER, Favorites.CONTAINER_DOCKBAR);
		values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_SHORTCUT); // 默认托盘四应用为shortcut类型
		db.insert(LauncherProvider.TABLE_FAVORITES, null, values);

		return true;
	}

	/**
	 * 为新安装的应用找安装位置
	 * @param context
	 * @return null 如果查找失败; 否则返回int[3]; int[0]=screen, int[1]=cellX, int[2]=cellY;
	 */
	public static int[] findVacantCell4AppAdd(Context context) {
		SQLiteDatabase db = null;
		try {
			final SQLiteOpenHelper dbHelper = new LauncherProvider.DatabaseHelper(context);
			db = dbHelper.getReadableDatabase();
			int startScreen = getLastScreenWithApp(db);
			if (startScreen == -1)
				return null;
			return getFirstVacantCell(db, 1, 1, startScreen); 
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return null;
	}
	
	/**
	 * 查找有图标的最后一屏
	 * @param db
	 * @return 返回屏数,失败返回-1
	 */
	public static int getLastScreenWithApp(SQLiteDatabase db) {
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer("select max(");
			sql.append(Favorites.SCREEN).append(")").append(" from ")
			.append(LauncherProvider.TABLE_FAVORITES).append(" where ")
			.append(Favorites.CONTAINER).append(" = ").append(Favorites.CONTAINER_DESKTOP);
			cursor = db.rawQuery(sql.toString(), null);
			if (cursor != null && cursor.moveToFirst())
				return cursor.getInt(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		}
		return -1;
	}
	
	/**
	 * Description: 在屏幕上找第一个空闲位置 (若不存在空闲区并且屏幕总数小于{@link LauncherStateSet#MAX_SCREEN}，则新增一屏并返回空闲区)<br> 
	 * return data: data[0] 屏幕编号; data[1]cellX位置; data[2] cellY位置 如果data为null，则不存在空闲位置
	 */
	public static int[] getFirstVacantCell(SQLiteDatabase db, int spanX, int spanY, int startFromScreen) {
		int[] data = new int[3];
		int[] vacantCell = null;
		int screen ;
		for (screen = startFromScreen; screen < ScreenViewGroup.MAX_SCREEN ; screen++) {
			vacantCell = getVacantCellOnScreen(db, screen, spanX, spanY);
			if (vacantCell != null)
				break;
		}
		if (vacantCell == null) {
			return null;
		} else {
			data[0] = screen;
			data[1] = vacantCell[0];
			data[2] = vacantCell[1];
			return data;
		}
	}
	
	/**
	 * 从数据库查找第一个位置
	 * @param db
	 * @param screen
	 * @param mSpanX
	 * @param mSpanY
	 * @return int[2] : int[0] = cellX, int[1] = cellY
	 */
	public static int[] getVacantCellOnScreen(SQLiteDatabase db, int screen, int mSpanX, int mSpanY) {
		int counX = CellLayoutConfig.getCountX();
		int counY = CellLayoutConfig.getCountY();
		boolean[][] occupied = new boolean[counX][counY];
		
		Cursor c = null;
		try {
			c = db.rawQuery("select " + Favorites.CELLX + "," + Favorites.CELLY + "," + Favorites.SPANX + "," + Favorites.SPANY + " from favorites where " + Favorites.CONTAINER + " = "
					+ LauncherSettings.Favorites.CONTAINER_DESKTOP + " and " + Favorites.SCREEN + "=" + screen, null);
			if (null != c && c.getCount() > 0) {
				boolean ret = c.moveToFirst();
				while (ret) {
					int cellX = c.getInt(c.getColumnIndex(Favorites.CELLX));
					int cellY = c.getInt(c.getColumnIndex(Favorites.CELLY));
					int spanX = c.getInt(c.getColumnIndex(Favorites.SPANX));
					int spanY = c.getInt(c.getColumnIndex(Favorites.SPANY));
					for (int j = cellY; j < cellY + spanY; j++) {
						for (int i = cellX; i < cellX + spanX; i++) {
							if (i >= counX || j >= counY) {
								continue;
							}
							occupied[i][j] = true;
						}
					}
					ret = c.moveToNext();
				}
			}

			return CellLayoutHelper.findFirstVacantCell(occupied, mSpanX, mSpanY);
		} catch (Exception e) {
			Log.e("LauncherProvider", e.toString());
			return null;
		} finally {
			if(null != c){
				c.close();
			}
		}
	}
	
	/**
     * Description: 从屏幕底部开始找第一个空闲位置 (若不存在空闲区并且屏幕总数小于9，则新增一屏并返回空闲区)
     * return data: data[0] 屏幕编号; data[1]
     * cellX位置; data[2] cellY位置 如果data为null，则不存在空闲位置
     */
    public static int[] getVacantCellFromBottom(SQLiteDatabase db, int spanX, int spanY, boolean startFromDefaultScreen) {
        return getVacantCell(db, spanX, spanY, startFromDefaultScreen, true);
    }
    
    public static int[] getVacantCell(SQLiteDatabase db, int spanX, int spanY, boolean startFromDefaultScreen, boolean fromBottom) {
        int[] data = new int[3];

        // 先从默认屏开始找空闲位置
        int defaultScreen = ConfigPreferences.getInstance().getDefaultScreen();
        int screen = defaultScreen;
        int[] vacantCell = null;
        if(startFromDefaultScreen){
            vacantCell = getVacantCellOnScreenEx(db, screen, spanX, spanY, fromBottom);
        }
        // 如果默认屏无没找到空闲位置， 从默认屏的左右两边屏幕开始找
        if (vacantCell == null) {
            for (int i = 1; i <= 8; i++) {
                screen = defaultScreen - i;
                if (screen >= 0) {
                    vacantCell = getVacantCellOnScreenEx(db, screen, spanX, spanY, fromBottom);
                    if (vacantCell != null)
                        break;
                }

                screen = defaultScreen + i;
                if (screen <= 8) {
                    vacantCell = getVacantCellOnScreenEx(db, screen, spanX, spanY, fromBottom);
                    if (vacantCell != null)
                        break;
                }
            }
        }

        if (vacantCell == null) {
            return null;
        } else {
            data[0] = screen;
            data[1] = vacantCell[0];
            data[2] = vacantCell[1];
            return data;
        }
    }
    /**
	 * 
	 * @param db
	 * @param screen
	 * @param mSpanX
	 * @param mSpanY
	 * @param fromBotoom
	 * @return
	 */
	private static int[] getVacantCellOnScreenEx(SQLiteDatabase db, int screen, int mSpanX, int mSpanY, boolean fromBotoom) {
		int[] countXY = SettingsPreference.getInstance().getScreenCountXY();
		int counX = countXY[0];
		int counY = countXY[1];
		boolean[][] occupied = new boolean[counX][counY];

		Cursor c = null;
		try {
			c = db.rawQuery("select " + Favorites.CELLX + "," + Favorites.CELLY + "," + Favorites.SPANX + "," + Favorites.SPANY + " from favorites where " + Favorites.CONTAINER + " = " + LauncherSettings.Favorites.CONTAINER_DESKTOP + " and " + Favorites.SCREEN + "=" + screen, null);
			if (null != c && c.getCount() > 0) {
				boolean ret = c.moveToFirst();
				while (ret) {
					int cellX = c.getInt(c.getColumnIndex(Favorites.CELLX));
					int cellY = c.getInt(c.getColumnIndex(Favorites.CELLY));
					int spanX = c.getInt(c.getColumnIndex(Favorites.SPANX));
					int spanY = c.getInt(c.getColumnIndex(Favorites.SPANY));
					for (int j = cellY; j < cellY + spanY; j++) {
						for (int i = cellX; i < cellX + spanX; i++) {
							if (i >= counX || j >= counY) {
								continue;
							}
							occupied[i][j] = true;
						}
					}
					ret = c.moveToNext();
				}
			}

			return fromBotoom ? CellLayoutHelper.findVacantCellFromBottom(occupied, mSpanX, mSpanY) : CellLayoutHelper.findFirstVacantCell(occupied, mSpanX, mSpanY);
		} catch (Exception e) {
			Log.e("getVacantCellOnScreen30", e.toString());
			return null;
		} finally {
			if (null != c) {
				c.close();
			}
		}

	}
    
    
}
