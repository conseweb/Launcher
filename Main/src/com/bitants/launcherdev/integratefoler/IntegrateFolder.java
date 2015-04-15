package com.bitants.launcherdev.integratefoler;

import java.util.List;

import com.bitants.launcherdev.framework.OnKeyDownListenner;
import com.bitants.launcherdev.launcher.DragController;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.screens.DragLayer;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.framework.OnKeyDownListenner;
import com.bitants.launcherdev.launcher.DragController;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.screens.DragLayer;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.view.DragView;
import test.DataTest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitants.launcherdev.framework.OnKeyDownListenner;
import com.bitants.launcherdev.kitset.GpuControler;
import com.bitants.launcherdev.launcher.DragController;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.screens.DragLayer;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcher.R;

public class IntegrateFolder extends RelativeLayout implements DropTarget, DragSource, View.OnClickListener, View.OnLongClickListener, OnKeyDownListenner {

	public static final String TAG = "IntegrateFolder";
	private ViewPager viewPager;
	private IntegrateFolderTitleStrip pagerTabStrip;
	private List<FolderInfo> folderInfoList;
	private DragController mDragController;
	private Launcher mLauncher;
	// 状态值 显示文件夹是否处于打开状态
	public boolean isOpened = false;

	/**
	 * 从桌面上点击的文件夹视图
	 */
	private View clickFolderView;

	/**
	 * 从桌面上点击的文件夹信息
	 */
	private FolderInfo clickFolderInfo;

	private ImageView folderMenuImg;

	private Menu mMenu;

	public IntegrateFolder(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDragController(DragController dragController) {
		mDragController = dragController;
	}

	public static IntegrateFolder fromXml(Launcher context) {
		IntegrateFolder integrateFolder = (IntegrateFolder) LayoutInflater.from(context).inflate(R.layout.user_folder_integrate, null);
		integrateFolder.mLauncher = context;
		integrateFolder.mDragController = (DragController) context.getDragController();
		return integrateFolder;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		viewPager = (ViewPager) findViewById(R.id.folder_group);
		pagerTabStrip = (IntegrateFolderTitleStrip) findViewById(R.id.folder_pager_strip);
		// pagerTabStrip.setBackgroundColor(Color.parseColor("#5500ff00"));
		pagerTabStrip.setOnClickListener(this);
		pagerTabStrip.setNonePrimaryAlpha(0.5f);

		folderMenuImg = (ImageView) findViewById(R.id.folder_menu);
		folderMenuImg.setOnClickListener(this);
		mMenu = new Menu();
//		GpuControler.enableHardwareLayers(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// for (int i = viewPager.getChildCount() - 1; i >= 0; i--) {
		// View view = viewPager.getChildAt(i);
		// IntegrateFolderPage localIntegrateFolderPage =
		// (IntegrateFolderPage)view ;
		// localIntegrateFolderPage.setPadding(10, 10, 10, 0);
		// }
	}

	private void initData() {
		setFolderInfoList(DataTest.getFolderInfoList(mLauncher.getWorkspace()));
		PagerAdapter pagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return folderInfoList.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				IntegrateFolderPage localIntegrateFolderPage = (IntegrateFolderPage) object;
				container.removeView(localIntegrateFolderPage);
			}

			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}

			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				// IntegrateFolder.f(IntegrateFolder.this).b();
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return folderInfoList.get(position).title;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				IntegrateFolderPage page = IntegrateFolderPage.fromXml(mLauncher, container, IntegrateFolder.this, folderInfoList.get(position).contents);
				page.setTag(position);
				container.addView(page);
				return page;
			}
		};
		viewPager.setAdapter(pagerAdapter);
		// viewPager.setOnPageChangeListener(pagerTabStrip);
		pagerTabStrip.setViewPager(viewPager);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				pagerTabStrip.onPageSelected(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub
				pagerTabStrip.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				pagerTabStrip.onPageScrollStateChanged(state);
			}
		});
	}

	private void setFolderInfoList(List<FolderInfo> folderInfoList) {
		this.folderInfoList = folderInfoList;
	}

	/**
	 * 关闭文件夹
	 */
	public boolean closeFolder() {
		DragLayer parent = (DragLayer) getParent();
		if (parent != null) {
			parent.removeView(this);
			mLauncher.visiableWorkspace();
			mDragController.removeDropTarget(this);
			clearFocus();
			mMenu.dismiss();
			return true;
		}
		return false;
		// mDragController.removeDropTarget((DropTarget) this);
		// clearFocus();
		// mFolderIcon.requestFocus();
	}

	/**
	 * 打开文件夹
	 */
	public void openFolder(View view, FolderInfo folderInfo) {
		if (getParent() == null) {
			initData();
			setClickFolder(view, folderInfo);
			mLauncher.invisiableWorkspace();
			mLauncher.getDragLayer().addView(this);
			mDragController.addDropTarget(this);
			Rect rect = new Rect();
			int loc[] = new int[2];
			getHitRect(rect);
			getLocationOnScreen(loc);
			Log.e("zhenghonglin", "" + rect + "," + loc[0] + "," + loc[1] + "," + getLeft() + "," + getTop());
			requestFocus();
		} else {

		}
	}

	/**
	 * 这是点击的文件夹
	 * 
	 * @param view
	 * @param folderInfo
	 */
	public void setClickFolder(View view, FolderInfo folderInfo) {
		this.clickFolderView = view;
		this.clickFolderInfo = folderInfo;
		viewPager.setCurrentItem(folderInfoList.indexOf(folderInfo));
	}

	/**
	 * 物理按键监听
	 */
	@Override
	public boolean onKeyDownProcess(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return closeFolder();
		}
		return false;
	}

	@Override
	public int getState() {
		Log.e(TAG, "getState");
		return 0;
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		Log.e(TAG, "onDrop:" + x + "," + y + "," + xOffset + "," + yOffset);
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		Log.e(TAG, "onDragEnter:" + x + "," + y + "," + xOffset + "," + yOffset);
		getCurrentView().onDragEnter(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		Log.e(TAG, "onDragOver:" + x + "," + y + "," + xOffset + "," + yOffset);
		getCurrentView().onDragOver(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		Log.e(TAG, "onDragExit:" + x + "," + y + "," + xOffset + "," + yOffset);
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		Log.e(TAG, "acceptDrop:" + x + "," + y + "," + xOffset + "," + yOffset);
		return true;
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		Log.e(TAG, "onDropCompleted:");
		getCurrentView().onDropCompleted(target, success);
	}

	@Override
	public boolean onLongClick(View v) {
		Log.e("zhenghonglin", "长按======");
		// Return if global dragging is not enabled
		Object tag = v.getTag();
		if (tag instanceof ApplicationInfo) {
			ApplicationInfo item = (ApplicationInfo) tag;
			if (!v.isInTouchMode()) {
				return false;
			}
			getCurrentView().startDrag(v, this);
			// mDragController.startDrag(v, this);
		}
		return true;
	}

	private IntegrateFolderPage getCurrentView() {
		if (viewPager != null) {
			return (IntegrateFolderPage) viewPager.findViewWithTag(viewPager.getCurrentItem());
		}
		return null;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// 背景变灰处理
		canvas.drawARGB(100, 50, 50, 50);
		super.dispatchDraw(canvas);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == folderMenuImg) {
			mMenu.toggleMenu();
		}
	}

	class Menu {

		private PopupWindow menuPopupWindow;

		private TextView rename;
		private TextView sortAlphaBeta;
		private TextView sortFrequency;

		public void dismiss() {
			if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
				menuPopupWindow.dismiss();
			}
		}

		public void toggleMenu() {
			if (menuPopupWindow == null) {
				setupMenuPopup(getContext());
			}

			if (menuPopupWindow.isShowing()) {
				menuPopupWindow.dismiss();
			} else {
				menuPopupWindow.showAtLocation(IntegrateFolder.this, 0, folderMenuImg.getLeft(), folderMenuImg.getBottom());
			}
		}

		/**
		 * 初始化匣子菜单
		 */
		private void setupMenuPopup(Context context) {
			if (menuPopupWindow != null)
				return;

			View view = LayoutInflater.from(context).inflate(R.layout.user_folder_menu, null);
			menuPopupWindow = new PopupWindow(view);
			menuPopupWindow.setAnimationStyle(R.style.menu_popup_animation);
			menuPopupWindow.setWidth(context.getResources().getDimensionPixelSize(R.dimen.folder_view_overflow_menu_width));
			menuPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);

			rename = (TextView) view.findViewById(R.id.folder_view_overflow_menu_rename);
			sortAlphaBeta = (TextView) view.findViewById(R.id.folder_view_overflow_menu_sort_alpha_beta);
			sortFrequency = (TextView) view.findViewById(R.id.folder_view_overflow_menu_sort_frequency);

			rename.setOnClickListener(onMenuClickListener);
			sortAlphaBeta.setOnClickListener(onMenuClickListener);
			sortFrequency.setOnClickListener(onMenuClickListener);
		}

		/**
		 * 菜单点击事件
		 */
		private OnClickListener onMenuClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		};

	}

}
