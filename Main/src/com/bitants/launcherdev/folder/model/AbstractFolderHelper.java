package com.bitants.launcherdev.folder.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;
import com.bitants.launcher.R;
import com.bitants.launcherdev.app.SerializableAppInfo;
import com.bitants.launcherdev.folder.view.FolderSlidingView;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;

import java.util.ArrayList;

/** 
 * 文件操作基类
 * 
 * @author pdw 
 * @version 
 * @date 2012-7-18 下午02:16:26 
 */
public abstract class AbstractFolderHelper implements IFolderHelper {
	
	protected int mAlpha = 140;
	/**
	 * 是否全屏
	 */
	protected boolean mIsFullScreen = true;
	/**
	 *  文件夹打开的顶层view
	 */
	protected View mTopFolderView;
	/**
	 * 被点击的folder view
	 */
	protected View mFolderView;
	/**
	 *  背景半透明bitmap
	 */
	protected Bitmap mAlphaBG;

	/**
	 * 获取当前显示屏幕的View，即整个屏幕对象
	 */
	protected View mWindowView;
	// 桌面顶层view
	protected View mDragLayer;
	
	protected FolderSlidingView mSlidingView ;
	
	protected Paint mPaint ;
	
	protected Launcher mLauncher ;
	
	protected int[] mLocation ;
	
	protected Rect mOutRect ;

	protected Rect mSrcRect = new Rect();
	
	protected Rect mDestRect = new Rect();
	
	protected int mScreenWidth ;
	
	protected int mScreenHeight ;
	
	@Override
	public Bitmap onPrepareAlphaBackground(int[] loc, Rect outVRect,
			boolean hilightClickView) {
		mLocation = loc;
		mFolderView.getLocationOnScreen(mLocation);

		mOutRect = outVRect ;
		mWindowView.getWindowVisibleDisplayFrame(mOutRect);
		// 如果全屏
		if (mIsFullScreen) {
			outVRect.top = 0;
		}
		// 屏宽
		mScreenWidth = mOutRect.right - mOutRect.left ;
		mScreenHeight = mOutRect.bottom - mOutRect.top ;
		
		return null;
	}
	
	protected void init(Launcher mLauncher) {
		init(mLauncher, FolderIconTextView.FOLDER_STYLE_FULL_SCREEN);
	}
	
	protected void init(Launcher mLauncher, int folderStyle){
		this.mLauncher = mLauncher;
//		if (folderStyle == FolderIconTextView.FOLDER_STYLE_IPHONE) {
//			this.mTopFolderView = mLauncher.getDragLayer().findViewById(R.id.folder_switch_layout);
//		} else if(folderStyle == FolderIconTextView.FOLDER_STYLE_ANDROID_4){
//			this.mTopFolderView = mLauncher.getDragLayer().findViewById(R.id.folder_switch_android_layout);
//		}else{
			this.mTopFolderView = mLauncher.getDragLayer().findViewById(R.id.folder_switch_fullscreen_layout);
//		}
		mSlidingView = (FolderSlidingView) this.mTopFolderView.findViewById(R.id.folder_scroll_view);
		
		Window window = mLauncher.getWindow();
		mWindowView = window.getDecorView(); // 获取当前显示屏幕的View，即整个屏幕对象
		mDragLayer = mLauncher.getDragLayer();
		
		mPaint = new Paint();
		mPaint.setAlpha(mAlpha);
	}

	/**
	 * 设置最终的渐变透明值
	 */
	@Override
	public void setAlpha(int alpha) {
		mAlpha = alpha ;
		mPaint.setAlpha(mAlpha);
	}

	@Override
	public void setIsFullScreen(boolean fullScreen) {
		this.mIsFullScreen = fullScreen ;
	}

	@Override
	public void onPreFolderOpen() {
		mFolderView.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onFolderOpen(int folderStyle) {
		
	}

	@Override
	public void onFolderClose(FolderInfo folder, boolean isAddMore) {

	}

	@Override
	public void setClickView(View view) {
		mFolderView = view ;
	}

	@Override
	public void addApps2Folder(FolderInfo folderInfo,
			ArrayList<SerializableAppInfo> list) {

	}

	@Override
	public void renameFolder(FolderInfo folderInfo, String name) {

	}

	@Override
	public void clipTop(Canvas canvas,Rect clip,int animDistance,int topMargin,int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void clipBottom(Canvas canvas,Rect clip,int animDistance,int topMargin,int alpha) {
		mPaint.setAlpha(alpha);
	}
	
	@Override
	public int getAlpha() {
		return mAlpha ;
	}
}
