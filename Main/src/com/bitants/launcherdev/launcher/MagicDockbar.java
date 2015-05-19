package com.bitants.launcherdev.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;
import com.bitants.launcher.R;
import com.bitants.launcherdev.datamodel.Global;
import com.bitants.launcherdev.folder.view.FolderBoxedViewGroup;
import com.bitants.launcherdev.folder.view.FolderSlidingView;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.launcherdev.launcher.screens.dockbar.DockbarCellLayout;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.AppMaskTextView;

import java.util.*;


public class MagicDockbar extends BaseMagicDockbar {
	
	public MagicDockbar(Context context, AttributeSet attrs) {
		super(context, attrs);	
		setEndlessScrolling(true);
		SharedPreferences sp = context.getSharedPreferences("settings", 0);
		if (sp == null) {
			return;
		}
		int count = sp.getInt("settings_dockbar_count", BaseMagicDockbar.DEFAULT_SCREEN_COUNT);
		if (count != BaseMagicDockbar.DEFAULT_SCREEN_COUNT){
			setDockBarNum(count);
		}
	}
	
	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		//从匣子或文件夹拖出判断
		Context mContext = getContext();
		if(source instanceof FolderSlidingView){
			View v = dragView.getDragingView();
			if(!(v instanceof FolderBoxedViewGroup
					|| v instanceof AppMaskTextView || v instanceof FolderIconTextView)
					|| mDragController.isOnMultiSelectedDrag()){
				Toast.makeText(mContext, R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		return super.acceptDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}
	public void setDockBarNum(int num){
		Context mContext = getContext();
		int childnum  = this.getChildCount();
		if (num <= 0) {
			return;
		}
		if (childnum > num){
			if(mCurrentScreen + 1 > num){
				setCurrentScreen(0);
				scrollTo(getScrollX() - getChildCount() * pageWidth, getScrollY());
			}
			for(int n = childnum - 1; n >= num; n--){
				DockbarCellLayout cellDockbar = (DockbarCellLayout)this.getChildAt(n);
				if(cellDockbar!=null){
					this.removeViewAt(n);
				}
			}
			if (num == 1){
				setEndlessScrolling(false);
			}
		}else if(childnum < num){
			for(int n = childnum ; n < num; n++){
				DockbarCellLayout cellDockbar = (DockbarCellLayout) View.inflate(mContext, R.layout.maindock_celllayout, null);
				addView(cellDockbar);
				List<ItemInfo> result = new ArrayList<ItemInfo>();
				LauncherModel.addDockbarItem(mContext,n,result);
				updataDockbarItem(result);
				bindItems(result,0,result.size());
			}
			setEndlessScrolling(true);
		}
	}
	
	
	public void bindItems(List<ItemInfo> shortcuts, int start, int end) {
		if (shortcuts == null) {
			return;
		}

		if (start < 0 || end > shortcuts.size()) {
			return;
		}
		LauncherConfig.getLauncherHelper().bindItems(shortcuts, start, end, mLauncher, mLauncher.getScreenViewGroup(), this);
	}
	
	@SuppressWarnings("rawtypes")
	public void updataDockbarItem(List<ItemInfo> shortcuts){
		if (shortcuts == null) {
			return;
		}
		int size = shortcuts.size();
		TreeMap<Integer, ItemInfo> posList = new TreeMap<Integer, ItemInfo>();
		for(int i=0;i<size;i++){
			posList.put(shortcuts.get(i).cellX, shortcuts.get(i));
		}
		Set set=posList.keySet();
		Iterator iter=set.iterator();
		int pos=0;
		while(iter.hasNext()){
			Object key=iter.next();
			posList.get(key).cellX = pos;
			pos++;
		}
		
	}
	
	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		super.onDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}
	
	@Override
	public boolean onLongClick(View v) {
		//编辑被锁定Dock上的任何长按事件都不响应
		if (!BaseConfig.allowEdit(getContext())) {
			return true;
		}
		((Launcher)mLauncher).setClickView(v);
		return super.onLongClick(v);
	}
	
	/**
	 * <br>Description: 应用主题皮肤
	 * <br>Author:caizp
	 * <br>Date:2012-7-19下午03:04:14
	 * @see com.bitants.launcherdev.theme.assit.ThemeUIRefreshListener#applyTheme()
	 */
	@Override
	public void applyTheme() {
		
	}
	
	@Override
	public boolean isShowDockbarText(){
		return Global.isShowDockbarText();
	}
	
	/**
	 * dock栏图标动画到Workspace时，查找目标位置
	 * @return
	 */
	@Override
	public int[] findCellXYForExchange(){
		if(mLauncher.mWorkspace.getCurrentCellLayout() == null)
			return null;
		
		return  mLauncher.mWorkspace.getCurrentCellLayout().findVacantCellFromBottom(1, 1, null);
	}
	
	/**
	 * 清除替换的虚框背景
	 * Create On 2014-7-26上午11:14:00
	 * Author : pdw
	 */
	public void cleanDirtyBackground() {
		DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
		if (cellLayout == null) {
			return;
		}
		int childCount = cellLayout.getChildCount();
		for (int i= 0; i < childCount; i++) {
			View child = cellLayout.getChildAt(i);
			child.setBackgroundResource(0);
		}
	}
}
