package com.nd.launcherdev.integratefoler;

import java.util.HashMap;
import java.util.List;

import android.database.DataSetObservable;
import android.database.DataSetObserver;


public abstract class AbstractDynamicGridAdapter implements DynamicGridAdapterInterface{
    public static final int INVALID_ID = -1;

    private int nextStableId = 0;

    private HashMap<Object, Integer> mIdMap = new HashMap<Object, Integer>();

    private DataSetObservable mObservable = new DataSetObservable();
    
    /**
     * 创建item的StableId
     * @param item
     */
    protected void addStableId(Object item) {
        mIdMap.put(item, nextStableId++);
    }

    /**
     * 创建items的StableId
     * @param items
     */
    protected void addAllStableId(List<?> items) {
        for (Object item : items) {
            addStableId(item);
        }
    }

    /**
     * 根据position获取相应位置的ItemId
     * @param position
     * @return
     */
    public final long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        Object item = getItem(position);
        return mIdMap.get(item);
    }

    
    /**
     * 根据item获取相应位置的ItemId
     * @param position
     * @return
     */
    public final long getItemId(Object item) {
        return mIdMap.get(item);
    }
    
    
	/**
     * 清除掉mIdMap 当adapter数据清理的时候
     * 
     */
    protected void clearStableIdMap() {
        mIdMap.clear();
    }
    
    /**
     * 移除掉item对应的itemId 当item从列表中移除的时候 itemId也要相应移除
     * @param item
     */
    protected void removeStableID(Object item) {
        mIdMap.remove(item);
    }

    public void notifyDataSetChanged() {
        mObservable.notifyChanged();
    }

    void registerDataSetObserver(DataSetObserver observer) {
        mObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(DataSetObserver observer) {
        mObservable.unregisterObserver(observer);
    }
    
}
