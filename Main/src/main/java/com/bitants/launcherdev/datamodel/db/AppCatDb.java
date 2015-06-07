package com.bitants.launcherdev.datamodel.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.bitants.common.sqliteasset.SQLiteAssetHelper;
import com.bitants.launcher.R;

/**
 * Created by michael on 15/6/5.
 */
public class AppCatDb extends SQLiteAssetHelper {

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

        Cursor cursor = db.query(
                AppCatEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                AppCatEntry.COLUMN_NAME_PKG,              // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        cursor.moveToFirst();
        ret = cursor.getInt(
                cursor.getColumnIndex(AppCatEntry.COLUMN_NAME_CAT)
        );

        // maybe -1 : means does not exists in db ; 1-24
        return ret;
    }

    public final String queryCatNameByPkg(String pkg, Context ctx) {
        String result = "";
        final int cat = this.queryCatByPkg(pkg);

        // max folder num in one screen: 16 - 20
        switch (cat) {
            case -1:
                result = ctx.getString(R.string.cat_other);
                break;
            case 1: // Games
            case 7: // Entertainment
                result = ctx.getString(R.string.cat_game);
                break;
            case 2: // Books & Reference
            case 4: // Comics
                result = ctx.getString(R.string.cat_book);
                break;
            case 3: // Business
                result = ctx.getString(R.string.cat_bussiness);
                break;
            case 5:  // Communication
            case 19: // Social
                result = ctx.getString(R.string.cat_communication);
                break;
            case 6: // Education
                result = ctx.getString(R.string.cat_education);
                break;
            case 8: // Finance
                result = ctx.getString(R.string.cat_finance);
                break;
            case 9: // Health & Fitness
            case 12: // Medical
                result = ctx.getString(R.string.cat_health);
                break;
            case 10: // Lifestyle
                result = ctx.getString(R.string.cat_lifestyle);
                break;
            case 11: // Media & Video
                result = ctx.getString(R.string.cat_audiovideo);
                break;
            case 13: // Music & Audio
                result = ctx.getString(R.string.cat_music);
                break;
            case 14: // News & Magazines
                result = ctx.getString(R.string.cat_news);
                break;
            case 15: // Personalization
                result = ctx.getString(R.string.cat_personalization);
                break;
            case 16: // Photography
                result = ctx.getString(R.string.cat_photography);
                break;
            case 17: // Productivity
            case 21: // Tools
            case 24: // Weather
                result = ctx.getString(R.string.cat_tools);
                break;
            case 18: // Shopping
                result = ctx.getString(R.string.cat_shopping);
                break;
            case 20: // Sports
                result = ctx.getString(R.string.cat_sports);
                break;
            case 22: // Transportation
                result = ctx.getString(R.string.cat_transportation);
                break;
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
