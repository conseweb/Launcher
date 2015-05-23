package com.bitants.common.launcher.support;

import java.lang.ref.WeakReference;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.bitants.common.framework.effect.WallpaperFilterView;
import com.bitants.common.kitset.util.BaseBitmapUtils;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.ConfigFactory;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.screens.DragLayer;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.launcher.screens.WorkspaceLayer;
import com.bitants.common.theme.ThemeManagerFactory;

/**
 * Description: 壁纸辅助类
 */
public class WallpaperHelper{
	private final static String TAG = "WallpaperHelper";
	private WallpaperManager mWallpaperManager = null;
	private float tempXOffset = 0;
	private int viewCount;
	private ScreenViewGroup workspace;
	private WorkspaceLayer workspaceLayer;
	
	private int screenWidth = 0;
	private boolean isEndlessScrolling;
	private int scrollRange = 0;
	
	private static boolean ONLY_UPDATE_WORKSPACE = false;//避免WorkspaceLayer和Workspace一起重绘壁纸
	private static boolean NOT_UPDATE_WORKSPACE = false;
	
	private boolean isWorkspaceLastToNavigation = false;//最后一屏滑向导航页时做特殊处理
	private boolean supportRecycleWallpaper = true;
	
	private static final int WALLPAPER_SCREENS_SPAN = 2;
	
	private static WallpaperHelper instance = new WallpaperHelper();
	/**
	 * 以下五个变量主要是用来自绘壁纸的
	 * */
	Bitmap mWallPaperBitmap;
	public float mWallPaperRate;
	public float mWallPaperScroll;
	Paint mPaint = new Paint();
	private Matrix mMatrix;
	DragLayer mDragLayer;
	int mWallPaperW=0;
	int mWallPaperH=0;
	/**
	 * 用来存放从canvas中取得的矩阵值
	 * */
	float matrixValue[]=new float[9];
	private WallpaperHelper(){}
	
	public static WallpaperHelper getInstance(){
		return instance;
	}
	
	public WallpaperManager getWallpaperManager() {
		if (mWallpaperManager == null) {
			mWallpaperManager = WallpaperManager.getInstance(BaseConfig.getApplicationContext());
		}		
		return mWallpaperManager;
	}
	
	public void setWorkspace(ScreenViewGroup workspace) {
		this.workspace = workspace;
	}
	
	public void setWorkspaceLayer(WorkspaceLayer workspaceLayer) {
		this.workspaceLayer = workspaceLayer;
	}
	
	//允许workspace更新壁纸
	public static void onlyUpdateWorkspace(){
		WallpaperHelper.ONLY_UPDATE_WORKSPACE = true;
		WallpaperHelper.NOT_UPDATE_WORKSPACE = false;
	}
	
	//不允许workspace更新壁纸
	public static void notUpdateWorkspace(){
		WallpaperHelper.ONLY_UPDATE_WORKSPACE = false;
		WallpaperHelper.NOT_UPDATE_WORKSPACE = true;
	}
	
	/**
	 * Description: 壁纸更新
	 * @param mScrollX
	 * @param isFromWorkspace (true: workspace请求更新壁纸， false: workspacelayer请求更新壁纸)
	 */
	public void updateWallpaperOffset(Context ctx, int mScrollX, boolean isFromWorkspace) {
		//开启导航页时，判断是否更新壁纸
		if(workspaceLayer.isShowZeroView() && ONLY_UPDATE_WORKSPACE && !isFromWorkspace){
			return;
		}
		if(workspaceLayer.isShowZeroView() && NOT_UPDATE_WORKSPACE && isFromWorkspace){
			return;
		}
		
		if (!ConfigFactory.isWallpaperRolling(ctx)){
			return;
		}
		
		isEndlessScrolling = BaseSettingsPreference.getInstance().isRollingCycle();
		
		if(screenWidth == 0){
			screenWidth = workspace.getScreenWidth();
		}
		
		if(workspaceLayer.isShowZeroView()){
			scrollRange = workspace.getChildCount() * screenWidth;
		}else{
			scrollRange = workspace.getChildCount() * screenWidth - screenWidth;
		}
		
		//开启导航页时
		if(workspaceLayer.isShowZeroView() || (BaseSettingsPreference.getInstance().isShowNavigationView() && workspace.isOnSpringMode())){
			viewCount = workspace.getChildCount() + 1;
			updateWallpaperOffset(scrollRange, mScrollX, isFromWorkspace);
		}else{
			viewCount = workspace.getChildCount();
			updateWallpaperOffset(scrollRange, mScrollX, isFromWorkspace);
		}
	}

	private void updateWallpaperOffset(int scrollRange, int mScrollX, boolean isFromWorkspace) {
		//有导航页时，修正偏移量
		if((workspaceLayer.isShowZeroView() || (BaseSettingsPreference.getInstance().isShowNavigationView() && workspace.isOnSpringMode()))
				&& isFromWorkspace){
			scrollRange += screenWidth;
			mScrollX += screenWidth;
		}
		//最后一屏滑向导航页时做特殊处理
		if(workspaceLayer.isShowZeroView() && mScrollX <= 2*screenWidth && mScrollX >= screenWidth 
				&& workspace.getCurrentScreen() == workspace.getChildCount()-1 && !isFromWorkspace){
			mScrollX += (workspace.getChildCount() - 1)*screenWidth;
		}
		
		
		
		IBinder token = workspace.getWindowToken();
		if (token != null) {
			getWallpaperManager().setWallpaperOffsetSteps(1.0f / (viewCount - 1), 0);
			float xOffset = 0f;
			if (!isEndlessScrolling || (isEndlessScrolling && (mScrollX >= 0) && (mScrollX <= scrollRange))) {
				xOffset = Math.max(0.f, Math.min(getWallpaperScrollX(mScrollX), 1.f));
			} else {
				if (mScrollX > scrollRange) {
					if (mScrollX <= scrollRange + screenWidth / 2) {
						/**
						 * 向右滑动前半屏
						 */
						xOffset = getWallpaperScrollX(Math.min(scrollRange + (mScrollX - scrollRange) / 2, scrollRange + screenWidth / 2));
					} else {
						/**
						 * 向右滑动后半屏
						 */
						xOffset = getWallpaperScrollX(Math.max((mScrollX - scrollRange - screenWidth) / 2, -screenWidth / 2));
					}
				} else if (mScrollX < 0) {
					if (mScrollX >= -screenWidth / 2) {
						/**
						 * 向左滑动前半屏
						 */
						xOffset = getWallpaperScrollX(Math.max(mScrollX / 2, -screenWidth / 2));
					} else {
						/**
						 * 向左滑动后半屏
						 */
						xOffset = getWallpaperScrollX(Math.min(scrollRange + (screenWidth + mScrollX) / 2, scrollRange + screenWidth / 2));
					}
				}
			}
			setWallPaperOffset(token, xOffset, 0);
			tempXOffset = xOffset;
		}
	}
	
	private float getWallpaperScrollX(int mScrollX) {
		return (((float) mScrollX) / screenWidth + 0.5f) / viewCount;
	}
	
	public void updateRollingCycleWallpaper(Canvas canvas, boolean isScrollToRight, int mScrollX, int mRight, int mLeft) {
		if (!supportRecycleWallpaper || getWallpaperManager().getWallpaperInfo() != null) {
			return;
		}
		
		try{
			//最后一屏滑向导航页时做特殊处理
			if(workspaceLayer.isShowZeroView() && mScrollX <= 2*screenWidth && mScrollX >= screenWidth 
					&& workspace.getCurrentScreen() == workspace.getChildCount()-1){
				mScrollX += (workspace.getChildCount() - 1)*screenWidth;
				isWorkspaceLastToNavigation = true;
			}
			
//			Log.e("xxxxxxxxx scrollRange : mScrollX : viewCount : tempXOffset", "" + scrollRange +":" + mScrollX + ":" + viewCount + ":" +tempXOffset);
			
			Rect rect = new Rect();
			workspace.getWindowVisibleDisplayFrame(rect);
			rect.top = 0;

			Paint p = new Paint();
			
			//获取壁纸Bitmap
			Bitmap wallpaperBitmap = getWallPaper();
			if (wallpaperBitmap == null) {
				return;
			}

			if (!isScrollToRight) {
				/**
				 * 向左滑动
				 */
				int alpha = (int) ((float) Math.min(Math.abs(mScrollX), screenWidth) / screenWidth * 255);
				// if (alpha <= 15) {
				// alpha = 0;
				// }
				if (mScrollX >= -screenWidth / 2) {

					/**
					 * 向左滑动前半屏
					 */
					/**
					 * 画右屏
					 */
					p.setAlpha(255);

					Rect srcRect = new Rect();
					srcRect.left = (int) (tempXOffset * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					Rect destRect = new Rect();
					destRect.left = mLeft + mScrollX;
					destRect.right = mRight + mScrollX;
					if(!workspaceLayer.isShowZeroView()){//减去第0屏所占的位置
						destRect.left -= screenWidth;
						destRect.right -= screenWidth;
					}
					destRect.top = 0;
					destRect.bottom = srcRect.bottom - srcRect.top;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);

					/**
					 * 画左屏
					 */
					p.setAlpha(Math.max(Math.min(alpha, 255), 0));

					srcRect.left = (int) ((tempXOffset + getWallpaperScrollX(scrollRange)) * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);
				} else {
					/**
					 * 向左滑动后半屏
					 */
					/**
					 * 画左屏
					 */
					p.setAlpha(255);

					Rect srcRect = new Rect();
					srcRect.left = (int) (tempXOffset * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					Rect destRect = new Rect();
					destRect.left = mLeft + mScrollX;
					destRect.right = mRight + mScrollX;
					if(!workspaceLayer.isShowZeroView()){//减去第0屏所占的位置
						destRect.left -= screenWidth;
						destRect.right -= screenWidth;
					}
					destRect.top = 0;
					destRect.bottom = srcRect.bottom - srcRect.top;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);

					/**
					 * 画右屏
					 */
					p.setAlpha(Math.max(Math.min(255 - alpha, 255), 0));

					srcRect.left = (int) ((tempXOffset - getWallpaperScrollX(scrollRange)) * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);
				}
			} else {
				/**
				 * 向右滑动
				 */
				int alpha = (int) ((float) Math.min(Math.abs(mScrollX - scrollRange), screenWidth) / screenWidth * 255);
				// if (alpha <= 15) {
				// alpha = 0;
				// }
				if (mScrollX <= scrollRange + screenWidth / 2) {

					/**
					 * 向右滑动前半屏
					 */
					/**
					 * 画左屏
					 */
					p.setAlpha(255);

					Rect srcRect = new Rect();
					srcRect.left = (int) (tempXOffset * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					Rect destRect = new Rect();
					destRect.left = mLeft + mScrollX;
					destRect.right = mRight + mScrollX;
					if(!workspaceLayer.isShowZeroView()){//减去第0屏所占的位置
						destRect.left -= screenWidth;
						destRect.right -= screenWidth;
					}
					if(isWorkspaceLastToNavigation){//最后一屏滑向导航页时做特殊处理
						isWorkspaceLastToNavigation = false;
						destRect.left -= (workspace.getChildCount() - 1)*screenWidth;
						destRect.right -= (workspace.getChildCount() - 1)*screenWidth;
					}
					destRect.top = 0;
					destRect.bottom = srcRect.bottom - srcRect.top;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);

					/**
					 * 画右屏
					 */
					p.setAlpha(Math.max(Math.min(alpha, 255), 0));

					srcRect.left = (int) ((tempXOffset - getWallpaperScrollX(scrollRange)) * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);
				} else {
					/**
					 * 向左滑动后半屏
					 */
					/**
					 * 画右屏
					 */
					p.setAlpha(255);

					Rect srcRect = new Rect();
					srcRect.left = (int) (tempXOffset * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					Rect destRect = new Rect();
					destRect.left = mLeft + mScrollX;
					destRect.right = mRight + mScrollX;
					if(!workspaceLayer.isShowZeroView()){//减去第0屏所占的位置
						destRect.left -= screenWidth;
						destRect.right -= screenWidth;
					}
					if(isWorkspaceLastToNavigation){//最后一屏滑向导航页时做特殊处理
						isWorkspaceLastToNavigation = false;
						destRect.left -= (workspace.getChildCount() - 1)*screenWidth;
						destRect.right -= (workspace.getChildCount() - 1)*screenWidth;
					}
					destRect.top = 0;
					destRect.bottom = srcRect.bottom - srcRect.top;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);

					/**
					 * 画左屏
					 */
					p.setAlpha(Math.max(Math.min(255 - alpha, 255), 0));

					srcRect.left = (int) ((tempXOffset + getWallpaperScrollX(scrollRange)) * (wallpaperBitmap.getWidth() - screenWidth));
					srcRect.right = srcRect.left + screenWidth;
					srcRect.top = rect.top;
					srcRect.bottom = rect.bottom;

					canvas.drawBitmap(wallpaperBitmap, srcRect, destRect, p);
				}
			}
		}catch(Exception e){
			supportRecycleWallpaper = false;
			Log.w("updateRollingCycleWallpaper error", e.toString());
		}
		drawFiterIfNeed(canvas);
	}
	
	/**
	 * Description: 清除循环滚屏时壁纸切换的Bitmap
	 */
	public void cleanWallpaper(){
//		if(!isEndlessScrolling){
//			return;
//		}
//		//4.0GPU开启情况下可进行回收
//		if(GpuControler.isOpenGpu(workspace) && wallpaperBitmap != null && !wallpaperBitmap.isRecycled()){
//			wallpaperBitmap.recycle();
//			wallpaperBitmap = null;
//			System.gc();
//		}
		
	}
	
	public void updateWallpaperForSpring(Context ctx, int mScrollX, int mRight, int mLeft) {
		if (!ConfigFactory.isWallpaperRolling(ctx)){
			return;
		}
		
		int count = workspace.getChildCount() - 1;
		if(count - 1 < 0)
			return;
		int scrollRange = workspace.getChildAt(count - 1).getRight() - (mRight - mLeft);
		boolean isEndlessScrolling = BaseSettingsPreference.getInstance().isRollingCycle();
		IBinder token = workspace.getWindowToken();
		if (token != null) {
			getWallpaperManager().setWallpaperOffsetSteps(1.0f / count, 0);
			float xOffset = 0f;
			if (!isEndlessScrolling || (isEndlessScrolling && (mScrollX >= 0) && (mScrollX <= scrollRange))) {
				if(BaseSettingsPreference.getInstance().isShowNavigationView()){//开启导航页时，做特殊处理
					mScrollX += screenWidth;
					count += 1;
				}
				xOffset = Math.max(0.f, Math.min((((float) mScrollX) / screenWidth + 0.5f) / count, 1.f));
			}
			setWallPaperOffset(token, xOffset, 0);
			tempXOffset = xOffset;
		}
	}
	
	/**
	 * 将壁纸居中显示
	 */
	public void updateWallpaperToCenter() {
		updateWallpaperOffset(0.5f);
	}
	
	private void updateWallpaperOffset(float offset){
		IBinder token = workspace.getWindowToken();

		if (token != null) {
			setWallPaperOffset(token, offset, 0);
		}
	}

	/**
	 * 设置壁纸展示范围
	 * @param ctx
	 */
	public void suggestWallpaperDimensions(Context ctx) {
		BaseLauncher mLauncher = BaseConfig.getBaseLauncher();
		if(mLauncher == null)
			return;
		WallpaperManager wpm = WallpaperManager.getInstance(mLauncher);

		Display display = mLauncher.getWindowManager().getDefaultDisplay();
		boolean isPortrait = display.getWidth() < display.getHeight();

		int width = isPortrait ? display.getWidth() : display.getHeight();
		int height = isPortrait ? display.getHeight() : display.getWidth();
		if (needScaleWallpaper() && !notScaleWallpaper()) {// 如果是超大分辨率
//			float yScale = (float) height / ((float) width / 480 * 800);// 按默认壁纸为960x800来计算1.06
			float yScale = 1.06f;
			if (yScale > 1) {
				height = (int) (height * yScale);
			}
		}
		wpm.suggestDesiredDimensions(width * WALLPAPER_SCREENS_SPAN, height);
		mWallPaperW=width*WALLPAPER_SCREENS_SPAN;
		mWallPaperH=height;
	}
	
	/**
	 * 特殊需适配的手机
	 * @return
	 */
	private boolean needScaleWallpaper(){
		return ScreenUtil.isSuperLargeScreen() || TelephoneUtil.isHuaweiMT1() || TelephoneUtil.isCoolpad8908();
	}

	private boolean notScaleWallpaper(){
		return ScreenUtil.isSuperLargeScreen() && (TelephoneUtil.isCoolpadPhone() || TelephoneUtil.isVivoPhone());
	}
	/**
	 * 设置避纸的偏移量，根据是否要自绘调用系统或者自定义
	 * */
	float lastOffset=0;
	private void setWallPaperOffset(IBinder token, float xOffset, float yOffset) {
		if (BaseConfig.isDrawWallPaper) {
			setWallPaperOffset(xOffset);
			if(lastOffset!=xOffset)
			{	
				mDragLayer.invalidate();
				lastOffset=xOffset;
			}
		} else {
			try{				
				getWallpaperManager().setWallpaperOffsets(token, xOffset, yOffset);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 循环滚动时获取壁纸
	 * 
	 * */
	private Bitmap getWallPaper() {
		Bitmap wallpaperBitmap=null ;
		if (BaseConfig.isDrawWallPaper) {
			wallpaperBitmap=getWallPaperBitmap();
		} else {
			WeakReference<Drawable> wallpaperRef = new WeakReference<Drawable>(getWallpaperManager().peekDrawable());
			if (wallpaperRef == null || wallpaperRef.get() == null) {
				return null;
			}

			 wallpaperBitmap = ((BitmapDrawable) wallpaperRef.get()).getBitmap();

			if (wallpaperBitmap.getHeight() < ScreenUtil.getCurrentScreenHeight(BaseConfig.getApplicationContext())
					|| wallpaperBitmap.getWidth() < ScreenUtil.getCurrentScreenWidth(BaseConfig.getApplicationContext())) {
				return null;
			}
		}

		return wallpaperBitmap;
	}

	public void drawWallPaper(Canvas canvas) {
		if (BaseConfig.isDrawWallPaper) {// 是否自绘壁纸
			if (mWallPaperBitmap == null) {
				initWallPaperBitmap();
			}
			if (mWallPaperBitmap != null && mWallPaperBitmap.isRecycled()) {
				mWallPaperBitmap = null;
			}
			if (mWallPaperBitmap != null) {
				canvas.save();
				float x = mWallPaperScroll * mWallPaperRate;
				if(mMatrix == null){
					mMatrix = new Matrix();
				}
				mMatrix.setTranslate(0, 0);
				canvas.setMatrix(mMatrix);
				canvas.drawBitmap(mWallPaperBitmap, -x, 0, mPaint);
				//canvas.drawRect(0,200,200,400,mPaint);
				canvas.restore();
			}
		}
	}
	/**
	 * 设置自绘壁纸的偏移
	 * 
	 * */
	public void setWallPaperOffset(float xOffset) {
		mWallPaperRate = xOffset;
	}

	/**
	 * 获取自绘壁纸
	 */
	public Bitmap getWallPaperBitmap() {
		return mWallPaperBitmap;
	}
	
	/**
	 * 生成自绘壁纸所需的bitmap
	 */
	private void initWallPaperBitmap(){
		if(BaseSettingsPreference.getInstance().isDrawWallpaperFromTheme()){			
			setWallpaperBitmapFromTheme();
		}else{			
			setWallpaperBitmapFromSystem();
		}
	}
	
	/**
	 * 从系统中获取壁纸，用于自绘壁纸
	 */
	private void setWallpaperBitmapFromSystem(){
		try {
			BitmapDrawable drawable = (BitmapDrawable) (getWallpaperManager().getDrawable());
			mWallPaperBitmap = drawable.getBitmap();
			mWallPaperScroll = mWallPaperBitmap.getWidth() - ScreenUtil.getScreenWH()[0];
//		 Log.e("zhou", "setWallpaper W="+mWallPaperBitmap.getWidth()+" H="+mWallPaperBitmap.getHeight());
		 
			if (mWallPaperBitmap != null && mWallPaperBitmap.getWidth() != mWallPaperW && mWallPaperW != 0 && mWallPaperH != 0) {
				mWallPaperBitmap = BaseBitmapUtils.resizeImage(mWallPaperBitmap, mWallPaperW, mWallPaperH);
				mWallPaperScroll = mWallPaperBitmap.getWidth() - ScreenUtil.getScreenWH()[0];
//				Log.e("zhou", "resizeImage");
			}
			if (mWallPaperBitmap == null || mWallPaperBitmap.getWidth() != mWallPaperW) {
				BaseConfig.isDrawWallPaper = false;
			} 
		} catch (OutOfMemoryError e) {
			BaseConfig.isDrawWallPaper = false;
			mWallPaperBitmap = null;
			e.printStackTrace();
		} catch (Exception e) {
			BaseConfig.isDrawWallPaper = false;
			mWallPaperBitmap = null;
			e.printStackTrace();
		}
		mDragLayer.invalidate();
	}
	
	/**
	 * 从当前主题获取壁纸，来自绘壁纸
	 */
	private void setWallpaperBitmapFromTheme(){
		try {
			Bitmap b = ThemeManagerFactory.getInstance().getCurrentTheme().getWallpaperBitmap();
			float xScale = ScreenUtil.getScreenWH()[0]*2f / b.getWidth();
			float yScale = ScreenUtil.getScreenWH()[1]*1.0f / b.getHeight();
			mWallPaperBitmap = BaseBitmapUtils.resizeImage(b, xScale > yScale ? xScale : yScale);
			mWallPaperScroll = mWallPaperBitmap.getWidth() - ScreenUtil.getScreenWH()[0];
		} catch (OutOfMemoryError e) {
//			BaseConfig.isDrawWallPaper = false;
			mWallPaperBitmap = null;
			e.printStackTrace();
		} catch (Exception e) {
//			BaseConfig.isDrawWallPaper = false;
			mWallPaperBitmap = null;
			e.printStackTrace();
		}
		mDragLayer.invalidate();
	}

	/**
	 * 重置自绘壁纸
	 */
	public void resetWallPaper() {
		//4.0以上且为动态壁纸时自绘
//		if (Build.VERSION.SDK_INT >= 14 && getWallpaperManager().getWallpaperInfo() == null) {
//			BaseConfig.isDrawWallPaper = true;
//		}
		
		int wh[];
		wh=ScreenUtil.getScreenWH();
		if ((wh[0] == 320 && wh[1]==480) && getWallpaperManager().getWallpaperInfo() == null) {
			BaseConfig.isDrawWallPaper = true;
		}
		
		if(BaseSettingsPreference.getInstance().isDrawWallpaperFromTheme()){
			BaseConfig.isDrawWallPaper = true;
		}
		
		if (BaseConfig.isDrawWallPaper) {
			if (mWallPaperBitmap != null) {
				Bitmap bitmap = mWallPaperBitmap;
				mWallPaperBitmap = null;
				BaseBitmapUtils.destoryBitmap(bitmap);
			}
			initWallPaperBitmap();
		}

	}
	public void setDragLayer(DragLayer dragLayer)
	{
		mDragLayer=dragLayer;
	}

	/**
	 * 假如存在壁纸滤镜，存获取当前滤镜并绘制
	 * */
	private void drawFiterIfNeed(Canvas canvas) {
		if (mDragLayer != null) {
			View view = mDragLayer.getChildAt(0);
			if (view != null && view instanceof WallpaperFilterView && !BaseConfig.isOnScene()) {
				WallpaperFilterView filterView = (WallpaperFilterView) view;
				Bitmap bitmap = filterView.getCurrentBitmap();
				if (bitmap != null && !bitmap.isRecycled()) {
					Matrix matrix = canvas.getMatrix();
					matrix.getValues(matrixValue);
					int x = 0;
					int y = 0;
					int wh[];
					wh = ScreenUtil.getScreenWH();
					x = (wh[0] - bitmap.getWidth()) / 2;
					y = (wh[1] - bitmap.getHeight()) / 2;
					int alpha = mPaint.getAlpha();
					canvas.save();
					canvas.translate(-matrixValue[2], 0);
					canvas.drawBitmap(bitmap, x, y, mPaint);
					canvas.restore();
					mPaint.setAlpha(alpha);
				}
			}
		}
	}
}
