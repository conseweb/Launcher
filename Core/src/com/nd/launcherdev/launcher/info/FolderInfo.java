/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nd.launcherdev.launcher.info;

import java.util.ArrayList;
import java.util.List;

import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.launcherdev.launcher.model.BaseLauncherSettings;
import com.nd.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;

import android.content.ContentValues;
import android.view.View;

/**
 * Represents a folder containing shortcuts or apps.
 */
public class FolderInfo extends ItemInfo implements ICommonDataItem {

	public static final long INVALIDATE_FOLDER_ID = -1;

	/**
	 * Whether this folder has been opened
	 */
	public boolean opened;

	/**
	 * The folder name.
	 */
	public CharSequence title = BLANK;

	/**
	 * The folder name in pinyin
	 */
	public CharSequence pinyin;

	/**
	 * 文件夹是否加密
	 */
	public boolean isEncript = false;
	
	/**
	 * 代理视图
	 */
	public View proxyView;
	
	public int pos;
	
	/**
	 * 匣子中应用程序的container
	 */
	public long drawerContainer = CONTAINER_DRAWER;
	
    /**
     * The apps and shortcuts 
     */
    public List<ApplicationInfo> contents = new ArrayList<ApplicationInfo>();

    public FolderIconTextView mFolderIcon = null; 
    
    //右上角显示数字提示，值为文件夹中应用的个数
    public boolean showHint = false;
  	
	public FolderInfo() {
		itemType = BaseLauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER;
	}

	public FolderInfo(FolderInfo info) {
		super(info);
		opened = info.opened;
		title = info.title;
		
		mFolderIcon = info.mFolderIcon;
        pos = info.pos;
        drawerContainer = info.drawerContainer;
        contents = info.contents;
        isEncript = info.isEncript;
        showHint = info.showHint;
	}

	public FolderInfo copy() {
		FolderInfo folder = new FolderInfo(this);
    	folder.contents = new ArrayList<ApplicationInfo>();
        for(ApplicationInfo app : this.contents){
        	folder.add(app.copy());
        }
        return folder ;
	}
	
    /**
     * Add an app or shortcut
     * 
     * @param item
     */
    public void add(ApplicationInfo item) {
        contents.add(item);
    }
    
    public void clear(){
    	contents.clear();
    }
    
    public int getSize() {
    	return contents.size();
    }
    
    /**
     * Remove an app or shortcut. Does not change the DB.
     * 
     * @param item
     */
    public void remove(ApplicationInfo item) {
        contents.remove(item);
    }
    
    @Override
    public void onAddToDatabase(ContentValues values) { 
        super.onAddToDatabase(values);
        values.put(BaseLauncherSettings.Favorites.TITLE, title.toString());
    }
    
	public void setProxyView(View proxyView) {
		this.proxyView = proxyView;
	}

	@Override
	public int getPosition() {
		return pos;
	}

	@Override
	public void setPosition(int position) {
		this.pos = position;
	}

	@Override
	public boolean isFolder() {
		return true;
	}
	
	/**
	 * @return the proxyView
	 */
	public View getProxyView() {
		return proxyView;
	}
	
    
    public void setFolderIcon(FolderIconTextView icon) {
	    mFolderIcon = icon;
	}
    
	/**
	 * 兼容代理视图Bottom
	 * <br>Author:ryan
	 * <br>Date:2012-7-14上午09:50:51
	 */
	public int getViewBottom() {
		if (proxyView != null)
			return proxyView.getBottom();
		else if (mFolderIcon != null)
			return mFolderIcon.getBottom();
		
		return 0;
	}
	
	/**
	 * 兼容代理视图Top
	 * <br>Author:ryan
	 * <br>Date:2012-7-14上午09:50:51
	 */
	public int getViewTop() {
		if (proxyView != null)
			return proxyView.getTop();
		else if (mFolderIcon != null)
			return mFolderIcon.getTop();
		
		return 0;
	}
	
	/**
	 * 判断文件夹状态，若为空则自动删除，考虑代理视图情况
	 * <br>Author:ryan
	 * <br>Date:2012-7-14上午09:50:31
	 */
	public void checkFolderState() {
		if (mFolderIcon == null)
			return;
		
		mFolderIcon.checkUserFolderContents();
	}
	
	/**
	 * 兼容代理视图重绘
	 * <br>Author:ryan
	 * <br>Date:2012-7-14上午10:46:41
	 */
	public void invalidate() {
		if (mFolderIcon != null)
			mFolderIcon.invalidate();
	}
}
