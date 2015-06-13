package com.bitants.launcherdev.datamodel.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.bitants.common.sqliteasset.SQLiteAssetHelper;
import com.bitants.launcher.R;

/**
 * Created by michael on 15/6/5.
 */
public class AppCatDb extends SQLiteAssetHelper {

    private String TAG = AppCatDb.class.getSimpleName();

    private static final String DATABASE_NAME = "appcat.db";
    private static final int DATABASE_VERSION = 1;

    /* Inner class that defines the table contents */
    public static abstract class AppCatEntry implements BaseColumns {
        public static final String TABLE_NAME = "appcats";
        public static final String COLUMN_NAME_PKG = "pkg";
        public static final String COLUMN_NAME_CAT = "cat";
    }

    public AppCatDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public final int queryCatByPkg(String pkg) {
        int ret = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                AppCatEntry.COLUMN_NAME_CAT
        };

        String[] selectionArgs = {
                pkg
        };

        try {
            Cursor cursor = db.rawQuery("select cat from appcats where pkg = '" + pkg.trim() + "'",
                    null);

            cursor.moveToFirst();
            ret = cursor.getInt(
                    cursor.getColumnIndex(AppCatEntry.COLUMN_NAME_CAT)
            );

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            ret = -1;
        }

        // maybe -1 : means does not exists in db ; 1-24
        return ret;
    }

    public final String queryCatNameByPkg(String pkg, Context ctx) {
        String result = "";
        final int cat = this.queryCatByPkg(pkg);

        // max folder num in one screen: 16 - 20
        switch (cat) {
            case 100:
            case -1:
                result = ctx.getString(R.string.cat_other);
                break;
            case 101:
            case 1: // Games
            case 7: // Entertainment
                result = ctx.getString(R.string.cat_game);
                break;
            case 102:
            case 2: // Books & Reference
            case 4: // Comics
            case 14: // News & Magazines
                result = ctx.getString(R.string.cat_newsreading);
                break;
            case 103:
            case 3: // Business
                result = ctx.getString(R.string.cat_bussiness);
                break;
            case 104: // cloud storage
                result = ctx.getString(R.string.cat_cloud);
                break;
            case 105:
            case 5:  // Communication
            case 19: // Social
                result = ctx.getString(R.string.cat_social);
                break;
            case 106:
            case 6: // Education
                result = ctx.getString(R.string.cat_education);
                break;
            case 107:
            case 8: // Finance
                result = ctx.getString(R.string.cat_finance);
                break;
            case 108:
            case 9: // Health & Fitness
            case 12: // Medical
                result = ctx.getString(R.string.cat_health);
                break;
            case 109:
            case 10: // Lifestyle
                result = ctx.getString(R.string.cat_life);
                break;
            case 110:
            case 11: // Media & Video
            case 13: // Music & Audio
                result = ctx.getString(R.string.cat_av);
                break;
            case 111:
            case 15: // Personalization
                result = ctx.getString(R.string.cat_personalization);
                break;
            case 112:
            case 16: // Photography
                result = ctx.getString(R.string.cat_photography);
                break;
            case 113:
            case 17: // Productivity
            case 21: // Tools
            case 24: // Weather
                result = ctx.getString(R.string.cat_tools);
                break;
            case 114:
            case 18: // Shopping
                result = ctx.getString(R.string.cat_shopping);
                break;
            case 115:
            case 20: // Sports
                result = ctx.getString(R.string.cat_sports);
                break;
            case 116:
            case 22: // Transportation
            case 23: // Travel & Local
                result = ctx.getString(R.string.cat_travelnlocal);
                break;
            default: // Invalid category
                result = ctx.getString(R.string.cat_other);
                break;
        }

        return result;
    }
}
