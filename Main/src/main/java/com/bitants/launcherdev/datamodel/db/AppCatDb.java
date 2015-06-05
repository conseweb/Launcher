package com.bitants.launcherdev.datamodel.db;

import android.content.Context;

import com.bitants.common.sqliteasset.SQLiteAssetHelper;

/**
 * Created by michael on 15/6/5.
 */
public class AppCatDb extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "appcat.db";
    private static final int DATABASE_VERSION = 1;

    public AppCatDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
