package com.bitants.launcherdev.integratefoler;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public abstract class AbstractFolderGridAdapter extends AbstractDynamicGridAdapter {
    private Context mContext;
    private ArrayList<Object> mItems = new ArrayList<Object>();
    private int mColumnCount;

    protected AbstractFolderGridAdapter(Context context, int columnCount) {
        this.mContext = context;
        this.mColumnCount = columnCount;
    }

    public AbstractFolderGridAdapter(Context context, List<?> items, int columnCount) {
        mContext = context;
        mColumnCount = columnCount;
        init(items);
    }

    private void init(List<?> items) {
        addAllStableId(items);
        this.mItems.addAll(items);
    }

    public void set(List<?> items) {
        clear();
        init(items);
        notifyDataSetChanged();
    }

    public void clear() {
        clearStableIdMap();
        mItems.clear();
        notifyDataSetChanged();
    }

    public void add(Object item) {
        addStableId(item);
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void add(int position, Object item) {
        addStableId(item);
        mItems.add(position, item);
        notifyDataSetChanged();
    }

    public void add(List<?> items) {
        addAllStableId(items);
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }


    public void remove(Object item) {
        mItems.remove(item);
        removeStableID(item);
        notifyDataSetChanged();
    }
    
    public Object getItem(int position) {
        return mItems.get(position);
    }

    public int getCount() {
        return mItems.size();
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
        notifyDataSetChanged();
    }

    public List<Object> getItems() {
        return mItems;
    }
    
    public void reorderItems(int originalPosition, int newPosition) {
        if (newPosition < getCount()) {
            DynamicGridUtils.reorder(mItems, originalPosition, newPosition);
            notifyDataSetChanged();
        }
    }
    
}
