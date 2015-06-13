package com.bitants.launcherdev.folder.model;

import com.bitants.launcherdev.integratefoler.IntegrateFolder;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by michael on 15/6/11.
 */
public class FolderModel {

    private int mFolderID;
    private String mName;
    private int mCount;
    private ArrayList<String> mApps;

    public FolderModel(String name) {
        mName = name;
        mCount = 0;
        mFolderID = -1;
        mApps = new ArrayList<String>();
    }

    public int getTotal() {
        return mCount;
    }

    public void setID(int id) {
        mFolderID = id;
    }

    public int getID() {
        return mFolderID;
    }

    public int getIndex(String pkg) {
        return mApps.indexOf(pkg);
    }

    public boolean addApp(String pkg) {
        boolean result = mApps.add(pkg);
        if (result) {
            mCount ++ ;
        }
        return result;
    }
}
