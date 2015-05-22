package com.bitants.launcherdev.launcher.view.icon.ui.folder;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.kitset.util.ScreenUtil;

/**
 * 可编辑图标的抽象父类，如程序匣子中的应用图标，长按后会显示一个打X的图标，代表可编辑状态
 *
 */
public class EditableIconView extends View {

	/**
	 * 划动体
	 */
	private CommonSlidingView mCommonSlidingView;
	
	
	/**
	 * 是否是新安装应用，默认是否
	 */
	private boolean mIsNewInstall=false;
	
	/**
	 * 是否是新推出的功能，默认是否
	 */
	private boolean mIsNewFunction=false;
	
	/**
	 * 是否是编辑模式，默认是否
	 */
	protected boolean mIsEditMode=false;
	
	/**
	 * 编辑模式的按下图标
	 */
	protected WeakReference<Bitmap> mEditModeFlagDownIcon;
	/**
	 * 编辑模式的图标
	 */
	protected WeakReference<Bitmap> mEditModeFlagNormalIcon;
	
	/**
	 * 编辑模式下选中的图标
	 */
	protected WeakReference<Bitmap> mEditChoosedFlagNormalIcon;
	
	/**
	 * 新安装应用的new图标
	 */
	private WeakReference<Bitmap> mNewInstallFlagIcon;
	
	/**
	 * 新推出功能的new图标
	 */
	private WeakReference<Bitmap> mNewFunctionFlagIcon;
	
	
	/**
	 * 编辑模式的图标所处的区域，供按下判断是否按到该区域
	 */
	protected Rect mEditIconRect = new Rect();;
	
	/**
	 * 编辑图标可点击区域外扩距离，以方便点中
	 */
	private int mEditIconRectPadding=3;
	
	/**
	 * 按下的位置是否落入编辑图标区域
	 */
	protected boolean mIsTouchDownInEditFlag=false;
	/**
	 * 手放开的位置是否落入编辑图标区域
	 */
	private boolean mIsTouchUpInEditFlag=false;
	
	/**
	 * 是否按下
	 */
	protected boolean mIsTouchDown=false;
	
	/**
	 * 是否是系统应用
	 */
	protected boolean mIsSystemApp=false;
	
	/**
	 * 是否是匣子myphone应用
	 */
	protected boolean mIsMyPhoneApp=false;

    /**
     * 是否是自生桌面应用
     */
    protected boolean mIsLauncher = false;
	
	/**
	 * 自定义长按的时间间隔
	 */
	private final long LONG_CLICK_TIME_SPAN=150;
	
	/**
	 * 是否可以进行长按，防止点击与提起事件的冲突
	 */
	private AtomicBoolean mCanLongClick=new AtomicBoolean(false);
	/**
	 * 是否处于长按中，防止点击与提起事件的冲突
	 */
	private AtomicBoolean mIsInLongClick=new AtomicBoolean(false);
	
	/**
	 * 编辑图标的画笔
	 */
	private Paint mEditIconPaint=new Paint();
	
	private Handler mHandler=new Handler();
	
	public EditableIconView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EditableIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditableIconView(Context context) {
		super(context);
	}
	
	/**
	 * 设置划动体
	 * @param commonSlidingView
	 */
	public void setCommonSlidingView(CommonSlidingView commonSlidingView)
	{
		mCommonSlidingView=commonSlidingView;
	}

	/**
	 * 设置是否是新安装
	 * @param isNewInstall
	 */
	public void setIsNewInstall(boolean isNewInstall)
	{
		mIsNewInstall=isNewInstall;
		postInvalidate();
	}
	
	/**
	 * 设置是否是新推出的功能(我的手机中新功能图标加new标记)
	 * @param isNewInstall
	 */
	public void setIsNewFunction(boolean isNewFunction)
	{
		mIsNewFunction=isNewFunction;
		postInvalidate();
	}
	
	/**
	 * 绘制新安装应用的new标记
	 * @param canvas
	 */
	protected void drawNewInstallFlag(Canvas canvas)
	{
		if(mIsNewInstall)
		{
			if(mNewInstallFlagIcon==null || mNewInstallFlagIcon.get()==null || mNewInstallFlagIcon.get().isRecycled()){
				Bitmap bmp=BitmapFactory.decodeResource(getContext().getResources(), R.drawable.new_installed_flag);
				mNewInstallFlagIcon=new WeakReference<Bitmap>(bmp);
			}
			float startX=getWidth()-mNewInstallFlagIcon.get().getWidth();
			float startY= ScreenUtil.dip2px(getContext(), 20);
			canvas.drawBitmap(mNewInstallFlagIcon.get(), startX, startY, null);
		}else{
			if(mNewInstallFlagIcon!=null && mNewInstallFlagIcon.get()!=null)
			{
				mNewInstallFlagIcon.get().recycle();
				mNewInstallFlagIcon.clear();
			}
			mNewInstallFlagIcon=null;
		}
	}
	
	/**
	 * 绘制新功能的new标记
	 * @param canvas
	 */
	protected void drawNewFunctionFlag(Canvas canvas)
	{
		if(mIsNewFunction)
		{
			if(mNewFunctionFlagIcon==null || mNewFunctionFlagIcon.get()==null || mNewFunctionFlagIcon.get().isRecycled()){
				Bitmap bmp=BitmapFactory.decodeResource(getContext().getResources(), R.drawable.new_label_red);
				mNewFunctionFlagIcon=new WeakReference<Bitmap>(bmp);
			}
			
			float startX=getWidth()-mNewFunctionFlagIcon.get().getWidth();
			canvas.drawBitmap(mNewFunctionFlagIcon.get(), startX, 0, null);
			
		}else{
			if(mNewFunctionFlagIcon!=null && mNewFunctionFlagIcon.get()!=null)
			{
				mNewFunctionFlagIcon.get().recycle();
				mNewFunctionFlagIcon.clear();
			}
			mNewFunctionFlagIcon=null;
		}
	}
	
	/**
	 * 绘制编辑模式的打叉标记图标
	 */
	protected void drawingEditModeFlag(Canvas canvas,float x,float y,int normalIconRes,int pressedIconRes)
	{
		//编辑模式、非系统应用，绘制打叉的图标
		if(mIsEditMode && !mIsSystemApp && !mIsMyPhoneApp && !mIsLauncher){
			Bitmap drawBmp;
			if(!mIsTouchDownInEditFlag){
				//普通态的打叉图标
				if(mEditModeFlagNormalIcon==null || mEditModeFlagNormalIcon.get()==null || mEditModeFlagNormalIcon.get().isRecycled())
				{
					Bitmap bmp=BitmapFactory.decodeResource(getContext().getResources(), normalIconRes);
					mEditModeFlagNormalIcon=new WeakReference<Bitmap>(bmp);
				}
				
				drawBmp=mEditModeFlagNormalIcon.get();
			}else{ 
				//按下态的打叉图标
				if(mEditModeFlagDownIcon==null || mEditModeFlagDownIcon.get()==null || mEditModeFlagDownIcon.get().isRecycled())
				{
					Bitmap bmp=BitmapFactory.decodeResource(getContext().getResources(), pressedIconRes);
					mEditModeFlagDownIcon=new WeakReference<Bitmap>(bmp);
				}
				drawBmp=mEditModeFlagDownIcon.get();
				
			}
			
			//按图标，但未点中编辑图标，将编辑图标设成半透明
			if(mIsTouchDown && !mIsTouchDownInEditFlag)	
				mEditIconPaint.setAlpha(BaseConfig.ALPHA_155);
			else
				mEditIconPaint.setAlpha(255);
			
			canvas.drawBitmap(drawBmp, getPaddingLeft(),getPaddingTop(), mEditIconPaint);
			
			//记录编辑图标的位置,外扩2像素，方便点击
			if(mEditIconRect.isEmpty()){				
				mEditIconRect.top=getPaddingTop()-mEditIconRectPadding;
				mEditIconRect.left=getPaddingLeft()-mEditIconRectPadding;
				mEditIconRect.right=mEditIconRect.left+drawBmp.getWidth()+mEditIconRectPadding;
				mEditIconRect.bottom=mEditIconRect.top+drawBmp.getHeight()+mEditIconRectPadding;
			}
			
		}else{
			
			//非编辑态，回收图标
			if(mEditModeFlagNormalIcon!=null && mEditModeFlagNormalIcon.get()!=null){
				mEditModeFlagNormalIcon.get().recycle();
			}
			mEditModeFlagNormalIcon=null;
			
			if(mEditModeFlagDownIcon!=null && mEditModeFlagDownIcon.get()!=null){
				mEditModeFlagDownIcon.get().recycle();
			}
			mEditModeFlagDownIcon=null;
		}
		
	}//end drawingEditModeFlag
	
	/**
	 * 绘制编辑模式下选中图标
	 */
	protected void drawingEditChoosedFlag(Canvas canvas, float x, float y, int normalIconRes, Paint paint) {
		if (mIsEditMode) {

			Bitmap drawBmp;
			// 选中图标
			if (mEditChoosedFlagNormalIcon == null || mEditChoosedFlagNormalIcon.get() == null || mEditChoosedFlagNormalIcon.get().isRecycled()) {
				Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), normalIconRes);
				mEditChoosedFlagNormalIcon = new WeakReference<Bitmap>(bmp);
			}

			drawBmp = mEditChoosedFlagNormalIcon.get();

			canvas.drawBitmap(drawBmp, getPaddingLeft(), getPaddingTop(), paint);
			
			/**
			 * 清除打叉标记点击区域
			 */
			mEditIconRect.setEmpty();

		} else {

			// 非编辑态，回收图标
			if (mEditChoosedFlagNormalIcon != null && mEditChoosedFlagNormalIcon.get() != null) {
				mEditChoosedFlagNormalIcon.get().recycle();
			}
			mEditChoosedFlagNormalIcon = null;
		}

	}// end drawingEditChoosedFlag
	
	/**
	 * 检测触点是否落在编辑标记区
	 * @param pointX
	 * @param pointY
	 */
	private boolean checkIsTouchInEditFlagRect(int pointX,int pointY)
	{
		
		if(mEditIconRect!=null && mEditIconRect.contains(pointX, pointY))
			return true;
		else
			return false;
			
	}
	
	/**
	 * 设置 编辑模式
	 * @param isEditMode
	 */
	public void setEditMode(boolean isEditMode) {
		mIsEditMode = isEditMode;
		if (mIsEditMode) {
			setBackgroundResource(0);
		} else {
			setBackgroundResource(R.drawable.icon_bg_selector);
		}
		postInvalidate();
	}
	
	@Override
	public void setSelected(boolean selected) {
		if (mIsEditMode && !selected) {
			setBackgroundResource(0);
		} else {
			setBackgroundResource(R.drawable.icon_bg_selector);
		}
		super.setSelected(selected);
	}

	/**
	 * 是否是在编辑图标区区域放开，
	 * @return
	 */
	public boolean isTouchUpInEditFlagRect()
	{
		return mIsTouchUpInEditFlag;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//非编辑模式，事件照默认事件执行。
		if(!mIsEditMode){
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mCanLongClick.set(true);
			mIsInLongClick.set(false);
			mIsTouchDown=true;
			mIsTouchUpInEditFlag=false;
			mIsTouchDownInEditFlag=checkIsTouchInEditFlagRect((int)event.getX(),(int)event.getY());
			invalidate();
			
			//自定义长按，在短时间内触发长按，
			mHandler.postDelayed(longClickRunnable, LONG_CLICK_TIME_SPAN);
			
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			synchronized (mCanLongClick) {
				mCanLongClick.set(false);
				mIsTouchDown=false;
				mIsTouchDownInEditFlag=false;
				mIsTouchUpInEditFlag=checkIsTouchInEditFlagRect((int)event.getX(),(int)event.getY());
				//移除自定义长按
				mHandler.removeCallbacks(longClickRunnable);
				
				//延迟重绘，为了防止在柱体、球体特效下划动时图标抖一下的问题
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						invalidate();
					}
				},  50);
				
				if(mIsInLongClick.get()){
					return true;
				}
			}
			
			break;
		}
		
		return super.onTouchEvent(event);
		//return false;
	}
	

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		Object tag = this.getTag();
		if (tag == null)
			return;

		if (!(tag instanceof ApplicationInfo)) {
			return;
		}
		
		mIsSystemApp = ((ApplicationInfo)tag).isSystem == 1;
		mIsMyPhoneApp = LauncherConfig.getLauncherHelper().isMyPhoneItem((ApplicationInfo)tag);

    }
	
	/**
	 * 自定长按事件
	 */
	private Runnable longClickRunnable=new Runnable() {
		
		@Override
		public void run() {
			synchronized (mCanLongClick) {
				if(!mCanLongClick.get())
					return;
				mIsInLongClick.set(true);
				if(!mIsEditMode || !mIsTouchDown || mIsTouchDownInEditFlag) return;
				if(mCommonSlidingView!=null)
					mCommonSlidingView.onLongClick(EditableIconView.this);
			}
			
		}
	};
	
}