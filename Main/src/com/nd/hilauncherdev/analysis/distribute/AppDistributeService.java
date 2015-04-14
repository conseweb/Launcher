package com.nd.hilauncherdev.analysis.distribute;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;

/**
 * description: 应用分发对外接口<br/>
 * author: dingdj<br/>
 * date: 2014/10/16<br/>
 */
public class AppDistributeService {


    private static AppDistributeService instance;

    private AppDistributeService(Context context) {
        appDistributePool = new AppDistributePool(context);
    }

    public static AppDistributeService getInstance() {
        if (instance == null) {
            instance = new AppDistributeService(BaseConfig.getApplicationContext());
        }
        return instance;
    }

    public AppDistributePool appDistributePool;

    private static final String Add_RECORD = "insert into " + AppDistributePool.Tables.DISTRIBUTE_POOL +
            " (" + AppDistributePool.DistributeColumns.DISTRIBUTE_ID + ", "
            + AppDistributePool.DistributeColumns.DISTRIBUTE_SP + ", "
            + AppDistributePool.DistributeColumns.DISTRIBUTE_STATE + ", "
            + AppDistributePool.DistributeColumns.UPDATE_TIME + ") values ('%s', %d, %d, %d)";

    private static final String DELETE_RECORD = "delete from " + AppDistributePool.Tables.DISTRIBUTE_POOL +
            " where " + AppDistributePool.DistributeColumns.DISTRIBUTE_ID + " = '%s'";

    private static final String UPDATE_RECORD = "update " + AppDistributePool.Tables.DISTRIBUTE_POOL + " set " + AppDistributePool.DistributeColumns.DISTRIBUTE_STATE
            + " = %d where " + AppDistributePool.DistributeColumns.DISTRIBUTE_ID + "= '%s'";


    private static final String SELECT_RECORD = "select * from " + AppDistributePool.Tables.DISTRIBUTE_POOL + " where " +
            AppDistributePool.DistributeColumns.DISTRIBUTE_ID + " = '%s' and " + AppDistributePool.DistributeColumns.DISTRIBUTE_SP + " = %d and " +
            AppDistributePool.DistributeColumns.DISTRIBUTE_STATE + " = %d";

    private static final String SELECT_RECORD_BY_DISTRIBUTE_ID = "select * from " + AppDistributePool.Tables.DISTRIBUTE_POOL + " where " +
            AppDistributePool.DistributeColumns.DISTRIBUTE_ID + " = '%s'";


    /**
     * @param pkg   应用包名
     * @param sp    应用sp
     * @param state 应用分发状态
     * @return 是否存在指定安装成功记录
     */
    public boolean isRecordExist(String pkg, int sp, int state) {
        if(StringUtil.isEmpty(pkg)) return false;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            String sql = String.format(SELECT_RECORD, pkg, sp, state);
            db = appDistributePool.getReadableDatabase();
            cursor = db.rawQuery(sql, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }


    /**
     * 删除Record记录
     *
     * @param pkg 包名
     */
    public void deleteRecord(String pkg) {
        if(StringUtil.isEmpty(pkg)) return;
        SQLiteDatabase db = null;
        try {
            db = appDistributePool.getWritableDatabase();
            String sql = String.format(DELETE_RECORD, pkg);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    /**
     * @param pkg 包名
     * @param sp  渠道
     * @return 是否添加成功
     */
    public boolean addRecord(String pkg, int sp) {
        if(StringUtil.isEmpty(pkg)) return false;
        SQLiteDatabase db = null;
        try {
            db = appDistributePool.getWritableDatabase();
            String sql = String.format(DELETE_RECORD, pkg);
            db.execSQL(sql);
            sql = String.format(Add_RECORD, pkg, sp, AppDistributePool.STATE_DOWNLOAD_SUC, System.currentTimeMillis());
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    public void updateRecord(String pkg, int state) {
        if(StringUtil.isEmpty(pkg)) return;
        SQLiteDatabase db = null;
        try {
            String sql = String.format(UPDATE_RECORD, state, pkg);
            db = appDistributePool.getWritableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * @param pkg 包名
     * @return AppDistributeRecord对象
     */
    public AppDistributeRecord getRecord(String pkg) {
        if(StringUtil.isEmpty(pkg)) return null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = appDistributePool.getReadableDatabase();
            String sql = String.format(SELECT_RECORD_BY_DISTRIBUTE_ID, pkg);
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int sp = cursor.getInt(2);
                int state = cursor.getInt(3);
                return new AppDistributeRecord(pkg, sp, state);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    public static class AppDistributeRecord {
        private String pkg;
        private int sp;
        private int state;

        public AppDistributeRecord(String pkg, int sp, int state) {
            this.pkg = pkg;
            this.sp = sp;
            this.state = state;
        }

        public String getPkg() {
            return pkg;
        }

        public int getSp() {
            return sp;
        }

        public int getState() {
            return state;
        }


    }

}
