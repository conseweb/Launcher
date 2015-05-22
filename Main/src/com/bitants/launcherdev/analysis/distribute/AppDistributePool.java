package com.bitants.launcherdev.analysis.distribute;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * description: 应用分发池数据库<br/>
 * author: Michael<br/>
 */
public class AppDistributePool extends SQLiteOpenHelper {

    public static final String TAG = AppDistributePool.class.getSimpleName();

    private static final String DATABASE_NAME = "app_distribute.db";

    private static final int CUR_DATABASE_VERSION = 1;

    public static final int STATE_DOWNLOAD_ING = 0; //正在下载

    public static final int STATE_DOWNLOAD_SUC = 1; //下载成功状态

    public static final int STATE_INSTALL_SUC = 2; //安装成功状态

    public static interface Tables {
        String DISTRIBUTE_POOL = "distribute_pool";
    }

    public static interface DistributeColumns {

        String DISTRIBUTE_ID = "distribute_id";

        String DISTRIBUTE_SP = "distribute_sp";

        String DISTRIBUTE_STATE = "distribute_state";

        String UPDATE_TIME = "update_time";
    }

    public AppDistributePool(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.DISTRIBUTE_POOL + "  ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DistributeColumns.DISTRIBUTE_ID + " TEXT NOT NULL,"
                + DistributeColumns.DISTRIBUTE_SP + " TEXT NOT NULL,"
                + DistributeColumns.DISTRIBUTE_STATE + " INTEGER NOT NULL,"
                + DistributeColumns.UPDATE_TIME + " INTEGER NOT NULL,"
                + "UNIQUE (" + DistributeColumns.DISTRIBUTE_ID + ") ON CONFLICT REPLACE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
    }

}
