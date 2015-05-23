package com.bitants.common.launcher.model;

import com.bitants.common.core.model.AbstractDataBaseSuper;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.config.LauncherConfig;

import android.appwidget.AppWidgetHost;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;

public class BaseLauncherProvider extends ContentProvider{

	public static String DATABASE_NAME = "launcher.db";

	public static String TABLE_FAVORITES = "favorites";
	
	protected static final String PARAMETER_NOTIFY = "notify";
	
	protected SQLiteOpenHelper mOpenHelper;
	
	public static String CREATE_TABLE_FAVORITES_MODEL="CREATE TABLE IF NOT EXISTS %1$s (" 
			 + "_id INTEGER PRIMARY KEY," // 主键 
			 + "title TEXT,"  // 标题
			 + "intent TEXT," // 打开方式
			 + "container INTEGER," // 属于桌面还是文件夹
			 + "screen INTEGER," //屏幕
// 			 + "x INTEGER," + "y INTEGER," + "width INTEGER," + "height INTEGER," //位置和大小
			 + "cellX INTEGER," + "cellY INTEGER," + "spanX INTEGER," + "spanY INTEGER," //坐标位置和大小
			 + "itemType INTEGER," // 元素类型
			 + "appWidgetId INTEGER NOT NULL DEFAULT -1," // widgetID 
			 + "isShortcut INTEGER," // 未知
			 + "iconType INTEGER," // 快捷方式图片类型
			 + "iconPackage TEXT," // 快捷方式图片资源包名
			 + "iconResource TEXT," // 快捷方式图片资源ID
			 + "icon BLOB," // 快捷方式图片与换图标图片
			 + "uri TEXT,"  // 实时文件夹
			 + "displayMode INTEGER," // 实时文件夹
			 + "defaultIcon BLOB" // 换图标后记录默认图标使用
			 + "%2$s"//扩展字段
			 + ");";
	
	public static String getAuthority(){
		return BaseLauncherSettings.Favorites.AUTHORITY;
	}
	
	/**
	 * {@link Uri} triggered at any registered
	 * {@link android.database.ContentObserver} when
	 * {@link AppWidgetHost#deleteHost()} is called during database creation.
	 * Use this to recall {@link AppWidgetHost#startListening()} if needed.
	 */
	public static Uri getContentAppWidgetResetUri(){
		return Uri.parse("content://" + getAuthority() + "/appWidgetReset");
	}
	
	@Override
	public boolean onCreate() {
		LauncherConfig.init(getContext());
		mOpenHelper = getSQLiteHelperInstance();
		return true;
	}

	@Override
	public String getType(Uri uri) {
		SqlArguments args = new SqlArguments(uri, null, null);
		if (TextUtils.isEmpty(args.where)) {
			return "vnd.android.cursor.dir/" + args.table;
		} else {
			return "vnd.android.cursor.item/" + args.table;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(args.table);
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
		result.setNotificationUri(getContext().getContentResolver(), uri);
		
		return result;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SqlArguments args = new SqlArguments(uri);
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final long rowId = db.insert(args.table, null, initialValues);
		if (rowId <= 0)
			return null;
		
		uri = ContentUris.withAppendedId(uri, rowId);
		sendNotify(uri);
		
		return uri;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SqlArguments args = new SqlArguments(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			int numValues = values.length;
			for (int i = 0; i < numValues; i++) {
				if (db.insert(args.table, null, values[i]) < 0)
					return 0;
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		sendNotify(uri);
		return values.length;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.delete(args.table, args.where, args.args);
		if (count > 0)
			sendNotify(uri);
		
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.update(args.table, values, args.where, args.args);
		if (count > 0)
			sendNotify(uri);

		return count;
	}

	private void sendNotify(Uri uri) {
		String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
		if (notify == null || "true".equals(notify)) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}
	
	
	static class SqlArguments {
		public final String table;
		public final String where;
		public final String[] args;

		SqlArguments(Uri url, String where, String[] args) {
			if (url.getPathSegments().size() == 2) {
				this.table = url.getPathSegments().get(1);
				this.where = where;
				this.args = args;
			} else if (url.getPathSegments().size() != 3) {
				throw new IllegalArgumentException("Invalid URI: " + url);
			} else if (!TextUtils.isEmpty(where)) {
				throw new UnsupportedOperationException("WHERE clause not supported: " + url);
			} else {
				this.table = url.getPathSegments().get(1);
				this.where = "_id=" + ContentUris.parseId(url);
				this.args = null;
			}
		}

		SqlArguments(Uri url) {
			if (url.getPathSegments().size() == 2) {
				table = url.getPathSegments().get(1);
				where = null;
				args = null;
			} else {
				throw new IllegalArgumentException("Invalid URI: " + url);
			}
		}
	}

	public interface ProviderUpgrader{
		/**
		 * 数据库版本升级处理
		 * @param db
		 * @param oldVersion
		 */
		public void onUpgrade(SQLiteDatabase db, int oldVersion);
	}
	
	public SQLiteHelper getSQLiteHelperInstance(){
		return new SQLiteHelper(getContext());
	}
	
	public static class SQLiteHelper extends SQLiteOpenHelper {
		private Context mContext;
		private String createTableSql = null;
		private AppWidgetHost mAppWidgetHost;
		private ProviderUpgrader launcherUpgrader;
		
		public SQLiteHelper(Context context){
			super(context, DATABASE_NAME, null, 1);
		}
		
		public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
			mContext = context;
			mAppWidgetHost = new AppWidgetHost(context, BaseLauncher.APPWIDGET_HOST_ID);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			if(createTableSql == null){				
				createTableSql = String.format(CREATE_TABLE_FAVORITES_MODEL, "favorites", "");
			}
			db.execSQL(createTableSql);
			
			// Database was just created, so wipe any previous widgets
			if (mAppWidgetHost != null) {
				mAppWidgetHost.deleteHost();
				sendAppWidgetResetNotify();
			}
			
			loadDefaultData(db);
		}
		
		/**
		 * Send notification that we've deleted the {@link AppWidgetHost},
		 * probably as part of the initial database creation. The receiver may
		 * want to re-call {@link AppWidgetHost#startListening()} to ensure
		 * callbacks are correctly set.
		 */
		private void sendAppWidgetResetNotify() {
			final ContentResolver resolver = mContext.getContentResolver();
			resolver.notifyChange(getContentAppWidgetResetUri(), null);
		}

		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(launcherUpgrader != null){
				launcherUpgrader.onUpgrade(db, oldVersion);
			}
		}
		
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
			//不支持降级，抛出异常
			throw new SQLiteException(AbstractDataBaseSuper.DOWNGRADE_EXCEPTION +" Can't downgrade database from version " +
	                oldVersion + " to " + newVersion);
//			throw new Exception(AbstractDataBaseSuper.DOWNGRADE_EXCEPTION);
		}
		
		/**
		 * 加载初始数据入库
		 * @param db
		 */
		public void loadDefaultData(SQLiteDatabase db){
		}
		
		public AppWidgetHost getAppWidgetHost() {
			return mAppWidgetHost;
		}
		
		public void setLauncherUpgrader(ProviderUpgrader launcherUpgrader) {
			this.launcherUpgrader = launcherUpgrader;
		}
		
		public void setCreateTableSql(String createTableSql) {
			this.createTableSql = createTableSql;
		}
	}
}
