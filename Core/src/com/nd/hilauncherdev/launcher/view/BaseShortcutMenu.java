package com.nd.hilauncherdev.launcher.view;

import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonViewHolder;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.launcher.BaseLauncher;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.info.ItemInfo;
import com.nd.hilauncherdev.launcher.model.BaseLauncherSettings;
import com.nd.hilauncherdev.launcher.screens.CellLayout;
import com.nd.hilauncherdev.launcher.screens.CellLayout.CellInfo;
import com.nd.hilauncherdev.launcher.touch.DropTarget;

public class BaseShortcutMenu implements View.OnClickListener {

	protected static final int SHOW_LOCATION_UP = 0;
	protected static final int SHOW_LOCATION_CENTER = 1;
	protected static final int SHOW_LOCATION_DOWN = 2;
	protected static final int SHOW_TYPE_LEFT = 0;
	protected static final int SHOW_TYPE_CENTER = 1;
	protected static final int SHOW_TYPE_RIGHT = 2;
	
	protected int mShowLocation = SHOW_LOCATION_UP;
	protected int mShowType = SHOW_TYPE_LEFT;
	
	private final String TAG = "ShortcutMenu";
	public BaseLauncher mLauncher;
	protected PopupWindow popup;
	public View layout;
	public View view;
	public DropTarget dt;
	
	/**
	 * 备份区域 
	 */

	public ItemInfo itemInfo;
	public TextView replace;
	public TextView rename;
	public TextView detail;
	public TextView resize;
	
	/**
	 * popupwindow 中一个图标的宽和高
	 */
	private final int oneIconWidth;
	private final int oneIconHeight;

	private final int edgeWidth;
	public int iconOuterSize;
	public int visibleCount;

	protected final int screenWidth;
	protected final int screenHeight;
	private final int notificationHeight;
	public CellInfo cellInfo;
	
	private int[] location;
	
	private int popupWidth;
	private int popupHeight;
	
	public Object dragInfo;

	
	public BaseShortcutMenu(BaseLauncher context) {
		mLauncher = context;
		oneIconWidth = (int) context.getResources().getDimension(R.dimen.popshortcut_menu_width);
		oneIconHeight = (int) context.getResources().getDimension(R.dimen.popshortcut_menu_height);

		/**
		 * popupWindow 背景中三角尖距离图片边缘的距离
		 */
		edgeWidth = (int) mLauncher.getResources().getDimension(R.dimen.shortcutmenu_offset_screen);
		
		screenWidth = ScreenUtil.getScreenWH()[0];
		screenHeight = ScreenUtil.getScreenWH()[1];
		notificationHeight = ScreenUtil.getNotificationHeight();
		location = new int[2];
		initView();
	}

	public boolean isShowing() {
		if (popup != null) {
			return popup.isShowing();
		}
		return false;
	}

	public void dismiss() {
		if (popup != null) {
			popup.dismiss();
		}
	}

	public void show(CellLayout.CellInfo cellInfo, View view, DropTarget dt) {
		if (popup != null && view != null) {
			this.dt = dt;
			this.view = view;
			this.cellInfo = cellInfo;	
			dragInfo = view.getTag();
			if(isFolderView(dt)){
				CommonViewHolder holder = (CommonViewHolder) view.getTag(R.id.common_view_holder);
				dragInfo =holder.item;
			}
			if (dragInfo instanceof ItemInfo) {
				itemInfo = (ItemInfo) dragInfo;
				if (itemInfo instanceof ApplicationInfo && 
						itemInfo.itemType != BaseLauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT &&
								!mLauncher.getScreenViewGroup().isAllAppsIndependence(itemInfo)) {
					ApplicationInfo appInfo = (ApplicationInfo) itemInfo;
					if (appInfo != null) {
						ResolveInfo ri = mLauncher.getPackageManager().resolveActivity(appInfo.intent, 0);
						/**
						 * 程序卸载完，桌面没有马上回收 长按图标显示删除选项
						 */
						if (ri == null)
							showDeleteIfNotRemoveAfterUninstall();
						else
							setViewByItemType(itemInfo);
					}
				} else {
					setViewByItemType(itemInfo);
				}	

				initPopupSize();
								
				popupWidth = popup.getWidth();
				popupHeight = popup.getHeight();
				view.getLocationOnScreen(location);
				popup.setOutsideTouchable(true);
				showAndSetAnimation();
				popup.update();
			}
		}
	}
	
	/**
	 * 初始化弹出菜单的大小
	 */
	protected void initPopupSize() {
		if(rename.getVisibility() == View.VISIBLE && visibleCount<=2){
			popup.setWidth((int)(oneIconWidth *1.1f)* visibleCount);
		}else{
			popup.setWidth(oneIconWidth * visibleCount);
		}
		popup.setHeight(oneIconHeight);
	}

	/**
	 * 显示并展示动画
	 */
	private void showAndSetAnimation() {
		if ( (location[1] - notificationHeight)> popup.getHeight()) {
			// 显示在上面
			mShowLocation = SHOW_LOCATION_UP;
			showUp();
		} else if ( (screenHeight - (location[1] + view.getHeight())) > popupHeight) {
			// 显示在下面
			mShowLocation = SHOW_LOCATION_DOWN;
			showDown();
		} else {
			// 显示在正中间
			mShowLocation = SHOW_LOCATION_CENTER;
			showCenter();
		}
	}

	//针对某些小部件(如天气4x2)，位置需要调整
	protected int getYOffset(int showLocation, ItemInfo itemInfo) {
		return 0;
	}
	
	private void showCenter() {
		int x,y;
		x = location[0] + view.getWidth() / 2 - popup.getWidth() / 2;
		y = (location[1]+view.getHeight()) / 2 - popupHeight + getYOffset(SHOW_LOCATION_CENTER, itemInfo);
		initPopupBeforeUpdate(R.drawable.shortcut_menu_center,x,y,R.style.ShortCutMenuGrowFromBottom);
		mShowType = SHOW_TYPE_CENTER;
	}

	private void showDown() {
		int x,y;
		y = (location[1] + view.getHeight()) + getYOffset(SHOW_LOCATION_DOWN, itemInfo);
		if ((location[0] + view.getWidth() / 2) > popupWidth / 2 && ((screenWidth - (location[0]+view.getWidth()) + view.getWidth() / 2) > popupWidth / 2)) {
			// 居中显示
			x = location[0] + view.getWidth() / 2 - popup.getWidth() / 2;		
			initPopupBeforeUpdate(R.drawable.shortcut_menu_top_center,x,y,R.style.ShortCutMenuGrowFromTop);
			mShowType = SHOW_TYPE_CENTER;
		} else if ((screenWidth - (location[0]+view.getWidth())) > screenWidth / 2) {
			// 左边边缘显示
			x = location[0]+view.getWidth()/2-edgeWidth;
			initPopupBeforeUpdate(R.drawable.shortcut_menu_top_left,x,y,R.style.ShortCutMenuGrowFromTopLeftToBottomRight);
			mShowType = SHOW_TYPE_LEFT;
		} else if (location[0] > screenWidth / 2) {
			// 右边边缘显示
			x = (location[0]+view.getWidth())-view.getWidth()/2- (popupWidth-edgeWidth);
			initPopupBeforeUpdate(R.drawable.shortcut_menu_top_right,x,y,R.style.ShortCutMenuGrowFromTopRightToBottomLeft);
			mShowType = SHOW_TYPE_RIGHT;
		} else {
			Log.e(TAG, "not catch");
		}
	}
	
	protected void initPopupBeforeUpdate(int drawableId,int x,int y,int animationStyle){
		popup.setBackgroundDrawable(mLauncher.getResources().getDrawable(drawableId));
		popup.showAtLocation(view, 0, x,y);
		popup.setAnimationStyle(animationStyle);
	}

	private void showUp() {
		int x,y;
		y = location[1] - popupHeight + getYOffset(SHOW_LOCATION_UP, itemInfo);
		if (((location[0] + view.getWidth() / 2) > popupWidth / 2) && ((screenWidth - (location[0]+view.getWidth()) + view.getWidth() / 2) > popupWidth / 2)) {
			// 居中显示
			x = location[0] + view.getWidth() / 2 - popup.getWidth() / 2;
			initPopupBeforeUpdate(R.drawable.shortcut_menu_center,x,y,R.style.ShortCutMenuGrowFromBottom);
			mShowType = SHOW_TYPE_CENTER;
		} else if ((screenWidth -(location[0]+view.getWidth())) > screenWidth / 2) {
			// 左边边缘显示
			x = location[0]+view.getWidth()/2-edgeWidth;
			initPopupBeforeUpdate(R.drawable.shortcut_menu_left,x,y,R.style.ShortCutMenuGrowFromBottomLeftToTopRight);
			mShowType = SHOW_TYPE_LEFT;
		} else if (location[0] > screenWidth / 2) {
			// 右边边缘显示
			x = (location[0]+view.getWidth())-view.getWidth()/2- (popupWidth-edgeWidth);
			initPopupBeforeUpdate(R.drawable.shortcut_menu_right,x,y,R.style.ShortCutMenuGrowFromBottomRightToTopLeft);
			mShowType = SHOW_TYPE_RIGHT;
		} else {
			Log.e(TAG, "not catch");
		}
	}
	
	public Object getDragInfo(){
		return dragInfo;
	}
	
	
	//===============================================需重写的方法=====================================//
	/**
	 * 是否文件夹展开视图
	 * @return
	 */
	public boolean isFolderView(DropTarget dt){
		return false;
	}
	
	/**
	 * 初始化布局内容，子类需重写
	 */
	public void initView() {
		popup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popup.setOutsideTouchable(true);
	}

	/**
	 * 卸载后长按，显示删除区
	 */
	public void showDeleteIfNotRemoveAfterUninstall(){
	}
	
	/**
	 * 
	 * <br>
	 * Description:根据itemType 来设置每个功能的可见 <br>
	 * Author:zhenghonglin <br>
	 * Date:2012-5-15下午08:20:06
	 * 
	 * @param item
	 */
	public void setViewByItemType(ItemInfo item) {
	}



	@Override
	public void onClick(View v) {
		dismiss();
	}

}
