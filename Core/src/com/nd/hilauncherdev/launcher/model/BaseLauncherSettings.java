package com.nd.hilauncherdev.launcher.model;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Settings related utilities.
 */
public class BaseLauncherSettings {
	
	public static interface BaseLauncherColumns extends BaseColumns {
		/**
		 * Descriptive name of the gesture that can be displayed to the user.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String TITLE = "title";

		/**
		 * The Intent URL of the gesture, describing what it points to. This
		 * value is given to
		 * {@link android.content.Intent#parseUri(String, int)} to create an
		 * Intent that can be launched.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String INTENT = "intent";

		/**
		 * The type of the gesture
		 * 
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		static final String ITEM_TYPE = "itemType";

		/**
		 * The gesture is an application
		 */
		public static final int ITEM_TYPE_APPLICATION = 0;

        /**
         * The gesture is an placeholder application
         */
        public static final int ITEM_TYPE_PLACE_HOLDER_APPLICATION = 60000;

		/**
		 * The gesture is an application created shortcut
		 */
		public static final int ITEM_TYPE_SHORTCUT = 1;

		/**
		 * The icon type.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		static final String ICON_TYPE = "iconType";

		/**
		 * The icon is a resource identified by a package name and an integer
		 * id.
		 */
		static final int ICON_TYPE_RESOURCE = 0;

		/**
		 * The icon is a bitmap.
		 */
		static final int ICON_TYPE_BITMAP = 1;

		/**
		 * The icon package name, if icon type is ICON_TYPE_RESOURCE.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String ICON_PACKAGE = "iconPackage";

		/**
		 * The icon resource id, if icon type is ICON_TYPE_RESOURCE.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String ICON_RESOURCE = "iconResource";

		/**
		 * The custom icon bitmap, if icon type is ICON_TYPE_BITMAP.
		 * <P>
		 * Type: BLOB
		 * </P>
		 */
		static final String ICON = "icon";
		
	}

	/**
	 * Favorites.
	 */
	public static class Favorites implements BaseLauncherColumns {
		/**
		 * 该值从config.xml读取，不可直接赋给静态变量使用！
		 */
		public static String AUTHORITY = "com.nd.android.launcher2.settings";
		/**
		 * 该值从config.xml读取，不可直接赋给静态变量使用！
		 */
		public static String AUTHORITY_SUB = "com.nd.hilauncherdev";
		
		public static String TABLE_FAVORITES = "favorites";
		public static final String PARAMETER_NOTIFY = "notify";

		/**
		 * The content:// style URL for this table
		 */
		public static Uri getContentUri(){
			return Uri.parse("content://" + AUTHORITY + "/" + AUTHORITY_SUB + "/" + TABLE_FAVORITES + "?" + PARAMETER_NOTIFY + "=true");
		}
		
		/**
		 * The content:// style URL for this table. When this Uri is used, no
		 * notification is sent if the content changes.
		 */
		public static Uri getContentUriNoNotify(){
			return Uri.parse("content://" + AUTHORITY + "/" + AUTHORITY_SUB + "/" + TABLE_FAVORITES + "?" + PARAMETER_NOTIFY + "=false");
		}
		
		/**
		 * The content:// style URL for a given row, identified by its id.
		 * 
		 * @param id
		 *            The row id.
		 * @param notify
		 *            True to send a notification is the content changes.
		 * 
		 * @return The unique content URL for the specified row.
		 */
		public static Uri getContentUri(long id, boolean notify) {
			return Uri.parse("content://" + AUTHORITY + "/" + AUTHORITY_SUB + "/" + TABLE_FAVORITES + "/" + id + "?" + PARAMETER_NOTIFY + "=" + notify);
		}

		/**
		 * The container holding the favorite
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String CONTAINER = "container";

		/**
		 * The icon is a resource identified by a package name and an integer
		 * id.
		 */
		public static final int CONTAINER_DESKTOP = -100;
        /**
         * CONTAINER ID FOR DOCKBAR
         */
        public static final int CONTAINER_DOCKBAR = -101 ;

		/**
		 * The screen holding the favorite (if container is CONTAINER_DESKTOP)
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String SCREEN = "screen";

		/**
		 * The X coordinate of the cell holding the favorite (if container is
		 * CONTAINER_DESKTOP or CONTAINER_DOCK)
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String CELLX = "cellX";

		/**
		 * The Y coordinate of the cell holding the favorite (if container is
		 * CONTAINER_DESKTOP)
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String CELLY = "cellY";

		/**
		 * The X span of the cell holding the favorite
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String SPANX = "spanX";

		/**
		 * The Y span of the cell holding the favorite
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String SPANY = "spanY";

		/**
		 * The appWidgetId of the widget
		 * 
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String APPWIDGET_ID = "appWidgetId";

		/**
		 * Indicates whether this favorite is an application-created shortcut or
		 * not. If the value is 0, the favorite is not an application-created
		 * shortcut, if the value is 1, it is an application-created shortcut.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		@Deprecated
		static final String IS_SHORTCUT = "isShortcut";

		/**
		 * The URI associated with the favorite. It is used, for instance, by
		 * live folders to find the content provider.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String URI = "uri";

		/**
		 * The display mode if the item is a live folder.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * 
		 * @see android.provider.LiveFolders#DISPLAY_MODE_GRID
		 * @see android.provider.LiveFolders#DISPLAY_MODE_LIST
		 */
		public static final String DISPLAY_MODE = "displayMode";

		/**
		 * The favorite is a user created folder
		 */
		public static final int ITEM_TYPE_USER_FOLDER = 2;

		/**
		 * The favorite is a live folder
		 */
		public static final int ITEM_TYPE_LIVE_FOLDER = 3;
		
		/**
		 * The favorite is a widget
		 */
		public static final int ITEM_TYPE_APPWIDGET = 4;
		
		/**
         * 91小部件预览图(兼容熊猫桌面2.X小插件) edit by caizp 2012-8-28
         */
        public static final int ITEM_TYPE_PANDA_PREVIEW_WIDGET = 1012;
        
        /**
         * 91小部件(兼容熊猫桌面2.X小插件) edit by caizp 2012-8-28
         */
        public static final int ITEM_TYPE_PANDA_WIDGET = 10000;
		
        /**
         * 自定义Intent
         */
        public static final int ITEM_TYPE_CUSTOM_INTENT = 2015;
        
	}
}
