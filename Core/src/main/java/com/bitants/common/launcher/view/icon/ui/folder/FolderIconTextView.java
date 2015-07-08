/**
 *
 */
package com.bitants.common.launcher.view.icon.ui.folder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bitants.common.framework.AnyCallbacks;
import com.bitants.common.kitset.util.BaseBitmapUtils;
import com.bitants.common.kitset.util.ColorUtil;
import com.bitants.common.kitset.util.PaintUtils;
import com.bitants.common.kitset.util.PaintUtils2;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.broadcast.AntBroadcastReceiver;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.CellLayoutConfig;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.config.preference.SettingsConstants;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.launcher.model.BaseLauncherSettings;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.dockbar.DockbarCellLayout;
import com.bitants.common.launcher.support.BaseLauncherViewHelper;
import com.bitants.common.launcher.view.DragView;
import com.bitants.common.launcher.view.icon.ui.IconSizeManager;
import com.bitants.common.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.common.launcher.view.icon.ui.util.BeansContainer;
import com.bitants.common.theme.assit.ThemeUIRefreshAssit;
import com.bitants.common.theme.assit.ThemeUIRefreshListener;
import com.bitants.common.R;

/**
 * 文件夹 <br>
 */
public class FolderIconTextView extends EditableIconView implements AnyCallbacks.OnDragEventCallback, ThemeUIRefreshListener {
	
	/**
	 * iphone风格文件夹
	 */
	public final static int FOLDER_STYLE_IPHONE = 0;
	
	/**
	 * android 4.0风格文件夹
	 */
	public final static int FOLDER_STYLE_ANDROID_4 = 1;
	
	/**
	 * 全屏风格文件夹
	 */
	public final static int FOLDER_STYLE_FULL_SCREEN = 2;
	
	/**
	 *  从桌面打开文件夹
	 */
	public final static int OPEN_FOLDER_FROM_LAUNCHER = 1;
	
	private static final float ANI_SCALE = 1.2f;
	private static final int TEXT_BACKGROUND_ALPHA = 150;
	static final String TAG = "FolderIconTextView";
	public static final String EXTRA_SHOW_NEW_FLAG = "show_new_flag";

	private int iconAlpha = 255;
	private int iconSize, iconLeft, iconTop, bgSize, largeIconLeft, largeIconTop;
	private int textHeight, allHeight, largeAllHeight, textWidth;
	private int minMargin;
	/**
	 * 图标与文字间隔
	 */
	private int drawingPadding;
	private long aniBeginTime, aniDiffTime;
	private float iconCenterX, iconCenterY;
	private float scaleSize = BaseConfig.NO_DATA_FLOAT, textLeft = BaseConfig.NO_DATA_FLOAT;
	private boolean mFolderEnterAni, mFolderExitAni;
	private boolean showText = true;
	public boolean iconLoaded = false;

	private int textColor, shadowColor, hintPaintFontMeasureSize;
	private Paint paint, alphaPaint, textBackgroundPanit, hintColorPaint;
	private Bitmap folderBgBitmap, folderEncriptMask, androidFolderBgBitmap, androidFolderEncriptMask,
		           fullScreenFolderBgBitmap,fullScreenFolderEncriptMask;
	/**
	 * 显示用的图标标签
	 */
	public CharSequence text = "null";
	/**
	 * 实际保存的标签
	 */
	private CharSequence savedText;
	private Drawable mAnimationBackground;

	public BaseLauncher mLauncher;
	public FolderInfo mInfo;

	private static final int ICON_COUNT_FOR_ANDROID_STYLE = 3; // android 4.0风格文件夹可以显示的缩略图数
	private static final int ICON_COUNT_FOR_IPHONE_STYLE = 4; // iphone风格文件夹可以显示的缩略图数
	private static final int NUM_COL_FOR_IPHONE_STYLE = 2; // iphone风格文件夹每行显示的个数
	private static final int ICON_COUNT_FOR_FULL_SCREEN_STYLE = 9; // 全屏风格文件夹可以显示的缩略图数
	private static final int NUM_COL_FOR_FULL_SCREEN_STYLE = 3; // 全屏风格文件夹每行显示的个数
	private int paddingForIphoneStyle = 3; // iphone风格文件夹内边距
	private int marginForIphoneStyle = 4; // iphone风格外边距
	private int marginForAndroidStyle = 4; // android 4.0风格外边距
	private int paddingForFullscreenStyle = ScreenUtil.dip2px(mLauncher, 2); // 全屏风格文件夹内边距
	private int marginForFullscreenStyle = ScreenUtil.dip2px(mLauncher, 3); // 全屏风格外边距
	
	private final RectF mRect = new RectF();

	private final Rect srcRect = new Rect();

	private final Rect destRect = new Rect();

	private Rect iconDestRect = new Rect();

	private Rect largeIconDestRect = new Rect();
	
//	private Rect specialRect;

	/**
	 * 大图标模式是否开启
	 */
	private boolean isLargeIconMode = false;
	
	/**
	 * 是否文件夹图标填充整个View
	 */
	boolean isFillContentMode = false;
	
	/**
	 * 是否显示标题背景
	 */
	private boolean isShowTextBackground = true;

	/**
	 * android 4.0风格文件夹最小图标缩放倍数
	 */
	private static final float BEGIN_SCALE_FOR_ANDROID_STYLE = 0.4f;

	/**
	 * android 4.0风格文件夹最大图标缩放倍数
	 */
	private static final float END_SCALE_FOR_ANDROID_STYLE = 0.7f;

	/**
	 * android 4.0风格文件夹最小图标透明度
	 */
	private static final float BEGIN_ALPHA_FOR_ANDROID_STYLE = 180f;

	/**
	 * android 4.0风格文件夹最大图标透明度
	 */
	private static final float END_ALPHA_FOR_ANDROID_STYLE = 255f;

	/**
	 * android 4.0风格文件夹相邻图标相差倍数
	 */
	private float coefficientForAndroidStyle;

	/**
	 * android 4.0风格文件夹相邻图标X轴间距
	 */
	private float spaceXForAndroidStyle;

	/**
	 * android 4.0风格文件夹相邻图标Y轴间距
	 */
	private float spaceYForAndroidStyle;

	/**
	 * android 4.0风格文件夹最小图标X轴坐标
	 */
	private float beginXForAndroidStyle;

	/**
	 * android 4.0风格文件夹最大图标X轴坐标
	 */
	private float endXForAndroidStyle;

	/**
	 * android 4.0风格文件夹最小图标Y轴坐标
	 */
	private float beginYForAndroidStyle;

	/**
	 * android 4.0风格文件夹最大图标Y轴坐标
	 */
	private float endYForAndroidStyle;

	/**
	 * 开始绘制的Y坐标
	 */
	private float mDrawStartY = 0;
	
	/**
	 * 文件夹是否处于可打开状态
	 */
	private boolean isDisable = false;
	
	
	/**
	 * 是否被点击*/
	boolean isOnTouchScaleState=false;
	/**
	 * 点击地缩小的系数
	 * */
	float onTouchScale=0.9f;
	
	//显示红点
	private boolean showNewFlag = false;
	//红点图标宽度
	private int iconWidth;

	private RefreshIconReceiver mRefreshIconReceiver;
	private BaseFolderReceiver folderReceiver;

	/**
	 * 关闭文件夹边框抖动动画
	 */
	private boolean mFolderAni;
	
	/**
	 * 指定文件夹中的某个View不绘制
	 */
	private int mNoDrawIndex = -1;
	/**
	 * 是否绘制图标
	 * */
	private boolean mNotDrawIcon=false;
	
	/**
	 * 限制绘制缩略图标个数
	 */
	private int mDrawIconLimit = -1;

	/**
	 * 右上角提示值
	 */
	private int hintValue = -1;
	
	/**
	 * onDraw时，重新计算文件夹视图各参数
	 */
	private boolean initValueOnDraw = true;
	
	/**
	 * 表示不支持合并文件夹
	 */
	private boolean folderNotAvailableHint = false;
	
	private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
	
	public FolderIconTextView(Context context) {
		super(context);
		init(context);
	}

	public FolderIconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FolderIconTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		Resources res = context.getResources();

		textColor = BaseSettingsPreference.getInstance().getAppNameColor();
		shadowColor = ColorUtil.antiColorAlpha(255, textColor);
		paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
		paint.setColor(textColor);
		paint.setShadowLayer(1, 1, 1, shadowColor);
		paint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		PaintUtils2.assemblyTypeface(paint);

		textBackgroundPanit = new Paint();
		textBackgroundPanit.setDither(true);
		textBackgroundPanit.setAntiAlias(true);
		textBackgroundPanit.setColor(Color.BLACK);

		alphaPaint = new Paint();
		alphaPaint.setDither(true);
		alphaPaint.setAntiAlias(true);
		alphaPaint.setColor(Color.WHITE);
		alphaPaint.setShadowLayer(1, 1, 1, Color.BLACK);
		alphaPaint.setTextSize(res.getDimensionPixelSize(R.dimen.text_size));
		
		hintColorPaint = new Paint();
		hintColorPaint.setTextSize(getContext().getResources().
				getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize));
		hintColorPaint.setColor(Color.WHITE);
		hintColorPaint.setAntiAlias(true);
		hintPaintFontMeasureSize = hintColorPaint.getFontMetricsInt(null);

		marginForIphoneStyle = res.getDimensionPixelSize(R.dimen.folder_icon_margin);
		marginForAndroidStyle = res.getDimensionPixelSize(R.dimen.android_folder_icon_margin);
		minMargin = res.getDimensionPixelSize(R.dimen.min_padding);
		drawingPadding = res.getDimensionPixelSize(R.dimen.text_drawpadding);
		iconSize = IconSizeManager.getIconSizeBySp(getContext());
		bgSize = getContext().getResources().getDimensionPixelSize(R.dimen.app_background_size);
		folderBgBitmap = LauncherIconSoftReferences.getInstance().getDefIconFolderBackground(res);
		folderEncriptMask = LauncherIconSoftReferences.getInstance().getDefIconFolderEncriptMask(res);
		androidFolderBgBitmap = LauncherIconSoftReferences.getInstance().getDefIconAndroidFolderBackground(res);
		androidFolderEncriptMask = LauncherIconSoftReferences.getInstance().getDefIconAndroidFolderEncriptMask(res);
	    fullScreenFolderBgBitmap = LauncherIconSoftReferences.getInstance().getDefIconFullScreenFolderBackground(res);
		fullScreenFolderEncriptMask = LauncherIconSoftReferences.getInstance().getDefIconFullScreenFolderEncriptMask(res);
		if (BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_IPHONE) {
			mAnimationBackground = new BitmapDrawable(getContext().getResources(), folderBgBitmap);
		} else if(BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_ANDROID_4){
			mAnimationBackground = new BitmapDrawable(getContext().getResources(), androidFolderBgBitmap);
		} else {
			mAnimationBackground = new BitmapDrawable(getContext().getResources(), fullScreenFolderBgBitmap);
		}

		isLargeIconMode = BaseConfig.isLargeIconMode();
		Bitmap softAndGameUpdateIcon = LauncherIconSoftReferences.getInstance().getSoftAndGameUpdateIcon();
		iconWidth = softAndGameUpdateIcon.getWidth();
		folderReceiver = BeansContainer.getInstance().getFolderReceiverFactory().getFolderReceiver();
		
		mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		initValaueByWH(width, height);
	}
	
	/**
	 * 通过宽度和长度来设置值
	 *  @param width
	 *  @param height
	 */
	public void initValaueByWH(int width, int height){
		if(!BaseConfig.isOnScene()){			
			initValue(width, height);
		}else{
			initValueOnScene(width, height);
		}
	}
	
	/**
	 * 是否为dock栏上的文件夹
	 * @return
	 */
	private boolean isOnDockbar(){
		return mInfo != null && mInfo.container == BaseLauncherSettings.Favorites.CONTAINER_DOCKBAR;
	}

	private int getIconSizeBySp(Context context){
		if(isOnDockbar()){
			return IconSizeManager.getDockbarIconSizeBySp(context);
		}else{
			return IconSizeManager.getIconSizeBySp(context);
		}
	}
	
	private void initValue(int widthSize, int heightSize) {
		if(!BaseConfig.isOnScene()){
			iconSize = getIconSizeBySp(getContext());
		}
		int paintFontMeasureSize = paint.getFontMetricsInt(null);
		boolean showText = isShowTextOnDockbarOrScene();
		if(showText){			
			textHeight = paintFontMeasureSize < BaseConfig.defaultFontMeasureSize ? BaseConfig.defaultFontMeasureSize : paintFontMeasureSize;
		}else{
			textHeight = 0;
		}
		allHeight = iconSize + minMargin + minMargin + textHeight;
		largeAllHeight = bgSize + minMargin + minMargin + textHeight;

		scaleSize = BaseConfig.NO_DATA_FLOAT;
		textLeft = BaseConfig.NO_DATA_FLOAT;

		float scaleW = 1, scaleH = 1, largeScaleW = 1, largeScaleH = 1;
		if (widthSize < iconSize + minMargin) {
			scaleW = widthSize * 1.0f / (iconSize + minMargin);
			iconLeft = minMargin;
		} else {
			iconLeft = (widthSize - iconSize) / 2;
		}
		if (heightSize < allHeight) {
			scaleH = heightSize * 1.0f / allHeight;
			iconTop = minMargin;
		} else {
			iconTop = (heightSize - allHeight) / 2;
		}
		if (widthSize < bgSize + minMargin) {
			largeScaleW = widthSize * 1.0f / (bgSize + minMargin);
			largeIconLeft = minMargin;
		} else {
			largeIconLeft = (widthSize - bgSize) / 2;
		}
		if (heightSize < largeAllHeight) {
			largeScaleH = heightSize * 1.0f / largeAllHeight;
			largeIconTop = minMargin;
		} else {
			largeIconTop = (heightSize - largeAllHeight) / 2;
		}

		iconCenterX = widthSize / 2;
		iconCenterY = isLargeIconMode ? largeIconTop + bgSize / 2 : iconTop + iconSize / 2;

		if (isLargeIconMode) {
			if (largeScaleW != largeScaleH) {
				scaleSize = largeScaleW < largeScaleH ? largeScaleW : largeScaleH;
				iconTop = largeIconTop + (bgSize - iconSize) / 2;
				iconLeft = largeIconLeft + (bgSize - iconSize) / 2;
			}
		} else {
			if (scaleW != scaleH) {
				scaleSize = scaleW < scaleH ? scaleW : scaleH;
			}
		}

		destRect.top = 0;
		destRect.left = 0;
		destRect.bottom = iconSize;
		destRect.right = iconSize;

		iconDestRect.top = iconTop;
		iconDestRect.left = iconLeft;
		iconDestRect.bottom = iconTop + iconSize;
		iconDestRect.right = iconLeft + iconSize;

		largeIconDestRect.top = largeIconTop;
		largeIconDestRect.left = largeIconLeft;
		largeIconDestRect.bottom = largeIconTop + bgSize;
		largeIconDestRect.right = largeIconLeft + bgSize;

		float tempScale = (float) bgSize / iconSize;
		coefficientForAndroidStyle = (float) (END_SCALE_FOR_ANDROID_STYLE - BEGIN_SCALE_FOR_ANDROID_STYLE) / (ICON_COUNT_FOR_ANDROID_STYLE - 1);
		if (isLargeIconMode) {
			beginXForAndroidStyle = widthSize - largeIconLeft - marginForAndroidStyle * tempScale - bgSize * BEGIN_SCALE_FOR_ANDROID_STYLE;
			endXForAndroidStyle = largeIconLeft + marginForAndroidStyle * tempScale;
			beginYForAndroidStyle = largeIconTop + marginForAndroidStyle * tempScale;
			endYForAndroidStyle = largeIconTop + bgSize - marginForAndroidStyle * tempScale - bgSize * END_SCALE_FOR_ANDROID_STYLE;
		} else {
			beginXForAndroidStyle = widthSize - iconLeft - marginForAndroidStyle - iconSize * BEGIN_SCALE_FOR_ANDROID_STYLE;
			endXForAndroidStyle = iconLeft + marginForAndroidStyle;
			beginYForAndroidStyle = iconTop + marginForAndroidStyle;
			endYForAndroidStyle = iconTop + iconSize - marginForAndroidStyle - iconSize * END_SCALE_FOR_ANDROID_STYLE;
		}
		spaceXForAndroidStyle = (beginXForAndroidStyle - endXForAndroidStyle) / (ICON_COUNT_FOR_ANDROID_STYLE - 1);
		spaceYForAndroidStyle = (endYForAndroidStyle - beginYForAndroidStyle) / (ICON_COUNT_FOR_ANDROID_STYLE - 1);

//		specialRect = LauncherIconDataCache.calcSpecilRectAndScale(largeIconDestRect, LauncherIconDataCache.DEFAULT_THEME_MASK_SCALE);
		//图标开始绘制的Y坐标，使图标在纵向居中
		if(!showText)
			return;
		/*if (!isLargeIconMode) {
			mDrawStartY = ((float) (heightSize - (iconTop + iconSize + drawingPadding + textHeight * 2))) / 2;
			mDrawStartY = mDrawStartY < 0 ? 0 : mDrawStartY;
			iconDestRect.top += (int) mDrawStartY;
		} else {
			mDrawStartY = ((float) (heightSize - (largeIconTop + bgSize + textHeight * 2))) / 2;
			mDrawStartY = mDrawStartY < 0 ? 0 : mDrawStartY;
			largeIconDestRect.top += (int) mDrawStartY;
		}*/
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//dock栏上图标
		if(isOnDockbar()){
			//防止未初始化
			initValue(getWidth(), getHeight());
			//文字间距无该padding
			drawingPadding = 0;
		}
				
		/**
		 * 去除画布锯齿
		 */
		canvas.setDrawFilter(mPaintFlagsDrawFilter);

		if (scaleSize != BaseConfig.NO_DATA_FLOAT && !isFillContentMode)
			canvas.scale(scaleSize, scaleSize, iconCenterX, 0);
		if(isOnTouchScaleState){
			isOnTouchScaleState = false;
			canvas.scale(onTouchScale, onTouchScale, iconCenterX, iconCenterY);
		}
		drawingAni(canvas);
		drawingOutFolderAni(canvas);
		if (mIsEditMode && mIsTouchDown && !mIsTouchDownInEditFlag) {
			//设置透明的alpha值
			iconAlpha = BaseConfig.ALPHA_155;
			paint.setAlpha(BaseConfig.ALPHA_155);
		} else {

			if (!mFolderEnterAni) //非文件合并状态，图标的画笔才设成不透明。
				iconAlpha = 255;
			paint.setAlpha(255);
		}
		if (BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_IPHONE) {
			/**
			 * iphone风格图标
			 */
			drawingIPhoneContent(canvas);
		} else if(BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_ANDROID_4){
			/**
			 * android 4.0风格图标
			 */
			drawingAndroidContent(canvas);
		}else{
			/**
			 * 全屏风格图标
			 */
			drawingFullScreenContent(canvas);
		}

		//绘制左上角的编辑图标
		drawingEditModeFlag(canvas, 0f, 0f, R.drawable.remove_folder_normal_btn, R.drawable.remove_folder_pressed_btn);
		
		//绘制new图标
		if(isShowNewFlag()){
			Bitmap softAndGameUpdateIcon = LauncherIconSoftReferences.getInstance().getSoftAndGameUpdateIcon();
			canvas.drawBitmap(softAndGameUpdateIcon, getWidth()-iconWidth, iconTop, null);
		}
		//是否绘制右上角提示
		if(isShowHint()){
			drawingHint(canvas);
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ThemeUIRefreshAssit.getInstance().registerRefreshListener(this);
		registerRefreshIconListenner();
		folderReceiver.registerReceiver(this);
		if (iconLoaded)
			return;

		if (mInfo == null || mInfo.contents.size() == 0)
			return;

		/**
		 * 刷新图标区之前需对内容排序，因为加载workspace时没有进行预排序
		 */
		Collections.sort(mInfo.contents, new ApplicationInfoPositionAscComparator());
		refresh();
	}





	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		ThemeUIRefreshAssit.getInstance().unregisterRefreshListener(this);
		if (mRefreshIconReceiver != null) {
			getContext().unregisterReceiver(mRefreshIconReceiver);
			mRefreshIconReceiver = null;
		}
		folderReceiver.unRegisterReceiver(this);
	}


	/**
	 * 刷新图标
	 */
	public void refresh() {
		folderReceiver.refresh(this);
	}

	private void drawingAni(Canvas canvas) {
		if (!(mFolderEnterAni || mFolderExitAni)) {
			return;
		}

		aniDiffTime = System.currentTimeMillis() - aniBeginTime;
		if (aniDiffTime >= BaseConfig.ANI_255) {
			if (mFolderExitAni) {
				mFolderExitAni = false;
				return;
			}

			if(isFillContentMode){
				mAnimationBackground.setBounds(iconDestRect);
			}else if (isLargeIconMode) {
				mAnimationBackground.setBounds(BaseBitmapUtils.caculateRect(iconCenterX, iconCenterY, bgSize * ANI_SCALE, bgSize * ANI_SCALE));
			} else {
				mAnimationBackground.setBounds(BaseBitmapUtils.caculateRect(iconCenterX, iconCenterY, iconSize * ANI_SCALE, iconSize * ANI_SCALE));
			}
			
		} else if (mFolderEnterAni) {
			float scale = aniDiffTime * ANI_SCALE / BaseConfig.ANI_255;
			float size = isLargeIconMode ? bgSize * scale : iconSize * scale;
			final int alpha = (int) (255 - aniDiffTime);
			final float shadow = alpha / 255;
			Rect rect = BaseBitmapUtils.caculateRect(iconCenterX, iconCenterY, size, size);
			if(isFillContentMode && rect.width() > iconDestRect.width()){
				mAnimationBackground.setBounds(iconDestRect);
			} else {				
				mAnimationBackground.setBounds(rect);
			}
			alphaPaint.setAlpha(alpha);
			alphaPaint.setShadowLayer(shadow, 1, 1, Color.BLACK);
			if(isShowTextOnDockbarOrScene()){
				drawingTextBackground(canvas, (int) ((float) TEXT_BACKGROUND_ALPHA * alpha / 255));
				if (isLargeIconMode) {
					canvas.drawText(text.toString(), getTextLeft(), largeIconTop + bgSize + textHeight, alphaPaint);
				} else {
					canvas.drawText(text.toString(), getTextLeft(), iconTop + iconSize + drawingPadding + textHeight, alphaPaint);
				}
			}
			
			invalidate();
		} else if (mFolderExitAni) {
			float scale = ANI_SCALE - (aniDiffTime * (ANI_SCALE - 1) / BaseConfig.ANI_255);
			float size = isLargeIconMode ? bgSize * scale : iconSize * scale;
			final float shadow = aniDiffTime / 255;
			Rect rect = BaseBitmapUtils.caculateRect(iconCenterX, iconCenterY, size, size);
			if(isFillContentMode && rect.width() > iconDestRect.width()){
				mAnimationBackground.setBounds(iconDestRect);
			}else{				
				mAnimationBackground.setBounds(rect);
			}
			alphaPaint.setAlpha((int) (aniDiffTime));
			alphaPaint.setShadowLayer(shadow, 1, 1, Color.BLACK);
			if(isShowTextOnDockbarOrScene()){
				drawingTextBackground(canvas, (int) ((float) TEXT_BACKGROUND_ALPHA * aniDiffTime / 255));
				if (isLargeIconMode) {
					canvas.drawText(text.toString(), getTextLeft(), largeIconTop + bgSize + textHeight, alphaPaint);
				} else {
					canvas.drawText(text.toString(), getTextLeft(), iconTop + iconSize + drawingPadding + textHeight, alphaPaint);
				}
			}
			
			invalidate();
		}

		if ((getScrollX() | getScrollY()) == 0) {
			mAnimationBackground.draw(canvas);
		} else {
			canvas.translate(getScrollX(), getScrollY());
			mAnimationBackground.draw(canvas);
			canvas.translate(-getScrollX(), -getScrollY());
		}
	}

	/**
	 * iphone风格图标
	 */
	private void drawingIPhoneContent(Canvas canvas) {
		if (folderBgBitmap != null) {
			srcRect.top = 0;
			srcRect.left = 0;
			srcRect.bottom = folderBgBitmap.getHeight();
			srcRect.right = folderBgBitmap.getWidth();
			if(!mFolderExitAni){
				Paint tmpPaint = PaintUtils.getStaticAlphaPaint(iconAlpha);
				drawBitmapWithColorFilter(canvas, folderBgBitmap, null, iconDestRect, tmpPaint);
			}
		}

		float x, y;
		float tempScale = (float) bgSize / iconSize;
		float scaleWidth = 0f;
		//是否使用大图标模式来绘制缩略图
		boolean useLargeMode = isOnDockbar() ? false : isLargeIconMode;
		if (useLargeMode) {
			scaleWidth = (bgSize - marginForIphoneStyle * tempScale * 2) / NUM_COL_FOR_IPHONE_STYLE - 2 * paddingForIphoneStyle * tempScale;
		} else {
			scaleWidth = (iconSize - marginForIphoneStyle * 2) / NUM_COL_FOR_IPHONE_STYLE - 2 * paddingForIphoneStyle;
		}
		//计算缩略图的宽(高与宽相同)
		float scale = scaleWidth / iconSize; // 计算缩放比例

		for (int i = 0; i < ICON_COUNT_FOR_IPHONE_STYLE; i++) {
			if (i < mInfo.contents.size()) {
				if (i == mNoDrawIndex) {
					continue;
				}
				if(mDrawIconLimit >= 0 && i >= mDrawIconLimit){
					continue;
				}
				if (useLargeMode) {
					x = largeIconLeft + marginForIphoneStyle * tempScale + paddingForIphoneStyle * tempScale * (2 * (i % NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i % NUM_COL_FOR_IPHONE_STYLE);
				} else {
					x = iconLeft + marginForIphoneStyle + paddingForIphoneStyle * (2 * (i % NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i % NUM_COL_FOR_IPHONE_STYLE);
				}
				if (useLargeMode) {
					y = largeIconTop + marginForIphoneStyle * tempScale + paddingForIphoneStyle * tempScale * (2 * (i / NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i / NUM_COL_FOR_IPHONE_STYLE);
				} else {
					y = iconTop + marginForIphoneStyle + paddingForIphoneStyle * (2 * (i / NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i / NUM_COL_FOR_IPHONE_STYLE);
				}
				canvas.save();
				canvas.translate(x, y);
				canvas.scale(scale, scale);
				Bitmap iconbmp = mInfo.contents.get(i).iconBitmap; //获取缩略图标
				if (iconbmp != null && !iconbmp.isRecycled()) {
					srcRect.top = 0;
					srcRect.left = 0;
					srcRect.bottom = iconbmp.getHeight();
					srcRect.right = iconbmp.getWidth();
					drawBitmapWithColorFilter(canvas, iconbmp, srcRect, destRect, paint);
				}
				canvas.restore();
			}
		}
		
		drawText(canvas, paint);
		
		//绘制加密蒙版
		if (mInfo != null && mInfo.isEncript && folderEncriptMask != null) {
			srcRect.top = 0;
			srcRect.left = 0;
			srcRect.bottom = folderEncriptMask.getHeight();
			srcRect.right = folderEncriptMask.getWidth();
			Paint tmpPaint = PaintUtils.getStaticAlphaPaint(iconAlpha);
			if (isLargeIconMode) {
				drawBitmapWithColorFilter(canvas, folderEncriptMask, srcRect, largeIconDestRect, tmpPaint);
			} else {
				drawBitmapWithColorFilter(canvas, folderEncriptMask, srcRect, iconDestRect, tmpPaint);
			}
		}
	}

	/**
	 * android 4.0风格图标
	 */
	private void drawingAndroidContent(Canvas canvas) {
		if (androidFolderBgBitmap != null) {
			srcRect.top = 0;
			srcRect.left = 0;
			srcRect.bottom = androidFolderBgBitmap.getHeight();
			srcRect.right = androidFolderBgBitmap.getWidth();
			if(!mFolderExitAni){
				Paint tmpPaint = PaintUtils.getStaticAlphaPaint(iconAlpha);
				drawBitmapWithColorFilter(canvas, androidFolderBgBitmap, srcRect, iconDestRect, tmpPaint);
			}
		}

		float x, y, scale;
		float tempScale = (float) bgSize / iconSize;
		float scaleWidth = 0f;
		int end = mInfo.contents.size() >= ICON_COUNT_FOR_ANDROID_STYLE ? ICON_COUNT_FOR_ANDROID_STYLE : mInfo.contents.size();
		for (int i = end - 1; i >= 0; i--) {
			if (i == mNoDrawIndex) {
				continue;
			}
			if(mDrawIconLimit >= 0 && i >= mDrawIconLimit){
				continue;
			}
			if (isLargeIconMode) {
				scaleWidth = (float) (bgSize * (BEGIN_SCALE_FOR_ANDROID_STYLE + coefficientForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i)));
				x = beginXForAndroidStyle - spaceXForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
				y = largeIconTop + marginForAndroidStyle * tempScale + spaceYForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
			} else {
				scaleWidth = (float) (iconSize * (BEGIN_SCALE_FOR_ANDROID_STYLE + coefficientForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i)));
				x = beginXForAndroidStyle - spaceXForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
				y = iconTop + marginForAndroidStyle + spaceYForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
			}
			scale = scaleWidth / iconSize;
			canvas.save();
			canvas.translate(x, y);
			canvas.scale(scale, scale);
			Bitmap iconbmp = mInfo.contents.get(i).iconBitmap; //获取缩略图标
			if (iconbmp != null && !iconbmp.isRecycled()) {
				srcRect.top = 0;
				srcRect.left = 0;
				srcRect.bottom = iconbmp.getHeight();
				srcRect.right = iconbmp.getWidth();
				int alpha = paint.getAlpha();
				paint.setAlpha((int) (BEGIN_ALPHA_FOR_ANDROID_STYLE + (END_ALPHA_FOR_ANDROID_STYLE - BEGIN_ALPHA_FOR_ANDROID_STYLE) / (ICON_COUNT_FOR_ANDROID_STYLE - 1) * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i)));
				ColorFilter cf = paint.getColorFilter();
				if (isDisable) {			
					paint.setColorFilter(new PorterDuffColorFilter(getContext().getResources().getColor(R.color.icon_color_filter_for_drawer_folder), PorterDuff.Mode.SRC_ATOP));
				}
				canvas.drawBitmap(iconbmp, srcRect, destRect, paint);
				paint.setAlpha(alpha);
				paint.setColorFilter(cf);
			}
			canvas.restore();
		}

		drawText(canvas, paint);
		
		//绘制加密蒙版
		if (mInfo != null && mInfo.isEncript && androidFolderEncriptMask != null) {
			srcRect.top = 0;
			srcRect.left = 0;
			srcRect.bottom = folderEncriptMask.getHeight();
			srcRect.right = folderEncriptMask.getWidth();
			Paint tmpPaint = PaintUtils.getStaticAlphaPaint(iconAlpha);
			if (isLargeIconMode) {
				drawBitmapWithColorFilter(canvas, androidFolderEncriptMask, srcRect, largeIconDestRect, tmpPaint);
			} else {
				drawBitmapWithColorFilter(canvas, androidFolderEncriptMask, srcRect, iconDestRect, tmpPaint);
			}
		}
	}
	
	/**
	 * 全屏风格图标
	 */
	private void drawingFullScreenContent(Canvas canvas) {
		if (fullScreenFolderBgBitmap != null) {
			srcRect.top = 0;
			srcRect.left = 0;
			srcRect.bottom = fullScreenFolderBgBitmap.getHeight();
			srcRect.right = fullScreenFolderBgBitmap.getWidth();
			if(mFolderAni || mFolderExitAni){
				//no draw
			}else{
				Paint tmpPaint = PaintUtils.getStaticAlphaPaint(iconAlpha);
//				if (isLargeIconMode) {
//					canvas.drawBitmap(fullScreenFolderBgBitmap, null, largeIconDestRect, tmpPaint);
//				} 
				drawBitmapWithColorFilter(canvas, fullScreenFolderBgBitmap, null, iconDestRect, tmpPaint);
			}
		}

		float x, y;
		float tempScale = (float) bgSize / iconSize;
		float scaleWidth = 0f;
		//是否使用大图标模式来绘制缩略图
		boolean useLargeMode = isOnDockbar() ? false : isLargeIconMode;
		if (useLargeMode) {
			scaleWidth = (bgSize - marginForFullscreenStyle * tempScale * 2) / NUM_COL_FOR_FULL_SCREEN_STYLE - 2 * paddingForFullscreenStyle * tempScale;
		} else {
			scaleWidth = (iconSize - marginForFullscreenStyle * 2) / NUM_COL_FOR_FULL_SCREEN_STYLE - 2 * paddingForFullscreenStyle;
		}
		//计算缩略图的宽(高与宽相同)
		float scale = scaleWidth / iconSize; // 计算缩放比例
		for (int i = 0; i < ICON_COUNT_FOR_FULL_SCREEN_STYLE; i++) {
			if (i < mInfo.contents.size()) {
				if (i == mNoDrawIndex || mNotDrawIcon) {
					continue;
				}
				if(mDrawIconLimit >= 0 && i >= mDrawIconLimit){
					continue;
				}
				if (useLargeMode) {
					x = largeIconLeft + marginForFullscreenStyle * tempScale + paddingForFullscreenStyle * tempScale * (2 * (i % NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth * (i % NUM_COL_FOR_FULL_SCREEN_STYLE);
				} else {
					x = iconLeft + marginForFullscreenStyle + paddingForFullscreenStyle * (2 * (i % NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth * (i % NUM_COL_FOR_FULL_SCREEN_STYLE);
				}
				if (useLargeMode) {
					y = largeIconTop + marginForFullscreenStyle * tempScale + paddingForFullscreenStyle * tempScale * (2 * (i / NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth * (i / NUM_COL_FOR_FULL_SCREEN_STYLE);
				} else {
					y = iconTop + marginForFullscreenStyle + paddingForFullscreenStyle * (2 * (i / NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth * (i / NUM_COL_FOR_FULL_SCREEN_STYLE);
				}
				canvas.save();
                if(i%3 == 0) {
                    x = x + ScreenUtil.dip2px(getContext(), 1);
                }else if ((i+1)%3 == 0) {
                    x = x - ScreenUtil.dip2px(getContext(), 1);
                }
				canvas.translate(x, y);
				canvas.scale(scale, scale);
				Bitmap iconbmp = mInfo.contents.get(i).iconBitmap; //获取缩略图标
				if (iconbmp != null && !iconbmp.isRecycled()) {
					srcRect.top = 0;
					srcRect.left = 0;
					srcRect.bottom = iconbmp.getHeight();
					srcRect.right = iconbmp.getWidth();
					drawBitmapWithColorFilter(canvas, iconbmp, srcRect, destRect, paint);
				}
				canvas.restore();
			}
		}

		drawText(canvas, paint);
		
		//绘制加密蒙版
		if (mInfo != null && mInfo.isEncript && fullScreenFolderEncriptMask != null) {
			srcRect.top = 0;
			srcRect.left = 0;
			srcRect.bottom = fullScreenFolderEncriptMask.getHeight();
			srcRect.right = fullScreenFolderEncriptMask.getWidth();
			Paint tmpPaint = PaintUtils.getStaticAlphaPaint(iconAlpha);
			if (isLargeIconMode) {
				drawBitmapWithColorFilter(canvas, fullScreenFolderEncriptMask, srcRect, largeIconDestRect, tmpPaint);
			} else {
				drawBitmapWithColorFilter(canvas, fullScreenFolderEncriptMask, srcRect, iconDestRect, tmpPaint);
			}
		}
	}
	
	protected void drawText(Canvas canvas, Paint paint) {
		if (needDrawTextAndTextBg()) {
			drawingTextBackground(canvas, TEXT_BACKGROUND_ALPHA);
			if (isLargeIconMode) {
				canvas.drawText(text.toString(), getTextLeft(), largeIconTop + bgSize + textHeight, paint);
			} else {
				canvas.drawText(text.toString(), getTextLeft(), iconTop + iconSize + drawingPadding + textHeight, paint);
			}
		}
	}
	
	protected void drawBitmapWithColorFilter(Canvas canvas, Bitmap bitmap, Rect src, Rect dst, Paint paint){
		ColorFilter cf = paint.getColorFilter();
		int alpha = paint.getAlpha();
		if (isDisable) {	
			paint.setColorFilter(new PorterDuffColorFilter(getContext().getResources().getColor(R.color.icon_color_filter_for_drawer_folder), PorterDuff.Mode.SRC_ATOP));
		}
		if(folderNotAvailableHint){
			paint.setColorFilter(PaintUtils.getNotMergeFolderPaintFilter());
			paint.setAlpha(PaintUtils.getNotMergeFolderPaintAlpha());
		}
		canvas.drawBitmap(bitmap, src, dst, paint);
		paint.setColorFilter(cf);
		paint.setAlpha(alpha);
	}
	
	/**
	 * 设置右上角提示值
	 * @param hintValue
	 */
	public void setHintValue(int hintValue){
		this.hintValue = hintValue;
	}
	
	/**
	 * 画右上角提示
	 * @param canvas
	 */
	private void drawingHint(Canvas canvas){
		int size = 0;
		if(hintValue > 0){
			size = hintValue;
		}else{
			if(mInfo == null)
				return;
			size = mInfo.contents.size();
		}
		if(size <= 0)
			return;
		
		int hintPadding = ScreenUtil.dip2px(getContext(), 6);
		int hintTextHeight = hintPaintFontMeasureSize < BaseConfig.defaultFontMeasureSize ? 
				BaseConfig.defaultFontMeasureSize : hintPaintFontMeasureSize;
		Bitmap mNoticeBg = LauncherIconSoftReferences.getInstance().getDefNoticeBg();
		int iconTop = iconDestRect.top-hintPadding;
		canvas.drawBitmap(mNoticeBg, (getWidth()-iconDestRect.width())/2+iconDestRect.width()*0.8f-hintPadding,
				iconTop, null);
		float left = (getWidth()-iconDestRect.width())/2+iconDestRect.width()*0.8f-hintPadding + 
				(mNoticeBg.getWidth() - hintColorPaint.measureText(size + "")) / 2;
		float top = iconTop + mNoticeBg.getHeight() / 2 + hintTextHeight/ 3;
		canvas.drawText(size + "", left, top, hintColorPaint);
	}
	
	/**
	 * 是否需要绘制文件夹title与背景
	 * @return
	 */
	private boolean needDrawTextAndTextBg(){
		return showText && !StringUtil.isEmpty(text) && isShowTextOnDockbarOrScene();
	}
	
	/**
	 * 画文字背景
	 * 
	 * @param canvas
	 */
	private void drawingTextBackground(Canvas canvas, int alpha) {
		if (!isShowTextBackground || !BaseSettingsPreference.getInstance().isShowTitleBackaground()) {
			paint.setShadowLayer(1, 1, 1, shadowColor);
			alphaPaint.setShadowLayer(1, 1, 1, shadowColor);
			return;
		}
		textBackgroundPanit.setAlpha(alpha);
		float left = getTextLeft() - ScreenUtil.dip2px(getContext(), 5);
		float right = textWidth > getWidth() ? getTextLeft() + getWidth() : getTextLeft() + textWidth + ScreenUtil.dip2px(getContext(), 5);
		left = left < 0 ? 0 : left;
		right = right > getWidth() ? getWidth() : right;
		float top = isLargeIconMode ? largeIconTop + bgSize : iconTop + iconSize + drawingPadding;
		mRect.set(left, top + ScreenUtil.dip2px(getContext(), 1), right, top + textHeight + ScreenUtil.dip2px(getContext(), 4));
		canvas.drawRoundRect(mRect, 8.0f, 8.0f, textBackgroundPanit);

		paint.clearShadowLayer();
		alphaPaint.clearShadowLayer();
	}

	private float getTextLeft() {
		if (getWidth() == 0)
			return 0;

		//		if (textLeft != BaseConfig.NO_DATA_FLOAT)
		//			return textLeft;

		final int width = getWidth();
		textLeft = (width - textWidth) / 2;
		if (textLeft < 0)
			textLeft = 0;

		return textLeft;
	}

	public CharSequence getText() {
		return savedText;
	}

	public void setText(CharSequence title) {
		savedText = title;
		this.text = title;
		if (StringUtil.isEmpty(text))
			textWidth = 0;
		else {
			textWidth = (int) paint.measureText(text.toString());
			if (getWidth() != 0 && textWidth > getWidth()) {
				int mid = 0;
				for (int i = 0; i < text.length(); i++) {
					int len = (int) paint.measureText(text, 0, i);
					if (len > getWidth()) {
						mid = i;
						break;
					}
				}
				if (mid != 0) {
					this.text = text.subSequence(0, mid - 1);
				} else {
					this.text = title;
				}
				textWidth = getWidth();
			}
		}
	}

	public void addItem(ApplicationInfo item) {
		item.cellX = 1;
		item.cellY = 1;
		item.spanX = 1;
		item.spanY = 1;
		item.screen = mInfo.getSize();
		
		BaseLauncherModel.addOrMoveItemInDatabase(mLauncher, item, mInfo.id);
		
		mInfo.add(item);
	}
	
	public void addTmpItem(ApplicationInfo item) {
		item.cellX = 1;
		item.cellY = 1;
		item.spanX = 1;
		item.spanY = 1;
		item.screen = mInfo.getSize();
		
		mInfo.add(item);
	}
	
	/**
	 * 批量添加ApplicationInfo
	 * @param items
	 */
	public void addItems(ArrayList<ApplicationInfo> items){

		if(items != null){
			for (ApplicationInfo item : items) {
				item.cellX = 1;
				item.cellY = 1;
				item.container = mInfo.id;
				item.screen = mInfo.getSize();
				mInfo.add(item);
			}
			BaseLauncherModel.addItemsToDatabase(mLauncher, items);
		}
		
	}
	
	
	
	
	public void deleteItem(ApplicationInfo item) {
		item.cellX = 1;
		item.cellY = 1;
		BaseLauncherModel.deleteItemFromDatabase(mLauncher, item);
		mInfo.remove(item);
	}

	public boolean acceptDrop(Object dragInfo) {
		return (dragInfo instanceof ApplicationInfo && dragInfo != mInfo && !mInfo.opened);
	}

	@Override
	public void onDropAni(DragView view) {
		iconAlpha = 255;
		showText = true;
		mFolderEnterAni = false;
		mFolderExitAni = false;

		invalidate();
	}

	@Override
	public void onEnterAni(DragView view) {
		if (this.getVisibility() != View.VISIBLE)
			return;

		if(notAllowMergeFolder(view.getDragingView())){
			onNotAllowDragEnter();
			return;
		}
		iconAlpha = 0;
		showText = false;
		mFolderEnterAni = true;
		mFolderExitAni = false;

		view.update(DragView.MODE_MIN);
		aniBeginTime = System.currentTimeMillis();
		invalidate();
	}

	@Override
	public void onExitAni(DragView view) {
		if (this.getVisibility() != View.VISIBLE)
			return;

		if(notAllowMergeFolder(view.getDragingView())){
			onNotAllowDragExit();
			return;
		}
		
		iconAlpha = 255;
		showText = true;
		mFolderEnterAni = false;
		mFolderExitAni = true;

		if(view != null){			
			view.update(DragView.MODE_NORMAL);
		}
		aniBeginTime = System.currentTimeMillis();
		invalidate();
	}
	
	private boolean notAllowMergeFolder(View v) {
		return v != null && (!(v.getTag() instanceof ApplicationInfo) || mLauncher.mWorkspace.isAllAppsIndependence((ItemInfo) v.getTag()));
	}

	public boolean isOnMergeFolderAni() {
		return mFolderEnterAni || mFolderExitAni;
	}

	public boolean isFolderExitAni() {
		return mFolderExitAni;
	}

	public boolean isFolderEnterAni() {
		return mFolderEnterAni;
	}

	/**
	 * 文件夹中只剩一个图标，将该图标移到桌面，并删除文件夹 <br>
	 */
	public void checkUserFolderContents() {
		final int size = mInfo.getSize();
		if (size > 1) {
			return;
		}
		// 文件夹中添加应用程序时，可以删除已选择的，因此存在0个
		if (size == 0) {
			removeSelf();
		} else { // 通过添加改变剩余1个时不走此函数
			FolderInfo mUserInfo = mInfo;
			
			ApplicationInfo shortcutInfo = mInfo.contents.get(0);
			shortcutInfo.container = mUserInfo.container;
			shortcutInfo.screen = mUserInfo.screen;
			shortcutInfo.cellX = mUserInfo.cellX;
			shortcutInfo.cellY = mUserInfo.cellY;
			int[] wh = CellLayoutConfig.spanXYMather(1, 1, shortcutInfo);
			shortcutInfo.spanX = wh[0];
			shortcutInfo.spanY = wh[1];
			
			if(isOnDockbar()){//dock栏上文件夹
				ViewGroup vp = (ViewGroup)mLauncher.getDockbar().getChildAt(mUserInfo.screen);
				vp.removeView(this);
				
				int cellX = shortcutInfo.cellX;
				if(getLayoutParams() instanceof DockbarCellLayout.LayoutParams){//使用该值修正可能存在的偏差
					DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) getLayoutParams();
					cellX = lp.cellX;
				}
				mLauncher.getDockbar().addInDockbarByCell(BaseLauncherViewHelper.createDockShortcut(mLauncher, shortcutInfo),
						shortcutInfo.screen, cellX, true);
			}else{
				final CellLayout cellLayout = mLauncher.mWorkspace.getCellLayoutAt(mUserInfo.screen);
				if(cellLayout == null)
					return;
				CellLayout.LayoutParams lp = (CellLayout.LayoutParams) getLayoutParams();
				//适配4.0以下固件，挤动动画过程中无法移除问题
				if(Build.VERSION.SDK_INT < 14 && (lp.isOnReorderAnimation || lp.isOnPending)){
					this.clearAnimation();
					mLauncher.mWorkspace.post(new Runnable(){
						@Override
						public void run() {
							cellLayout.removeView(FolderIconTextView.this);
						}
					});
				}else{				
					cellLayout.removeView(this);
				}
				
				LauncherConfig.getLauncherHelper().getLeftTopXYByHoleType(this, shortcutInfo, wh);
				final View shortcut = BaseLauncherViewHelper.createCommonAppView(mLauncher, shortcutInfo);
				mLauncher.mWorkspace.addInScreen(shortcut, shortcutInfo.screen, shortcutInfo.cellX, shortcutInfo.cellY,
						shortcutInfo.spanX, shortcutInfo.spanY, false, true);
			}
			
			BaseLauncherModel.moveItemInDatabase(getContext(), shortcutInfo);
			BaseLauncherModel.deleteItemFromDatabase(getContext(), mUserInfo);
		}
		folderReceiver.clearEncript(this);
	}

	/**
	 * 从workspace中移除掉自己，当自己文件夹内容为空时
	 * 
	 */
	public void removeSelf() {
		if (mInfo.getSize() != 0)
			return;

		if(isOnDockbar()){//dock栏上文件夹
			ViewGroup vp = (ViewGroup)mLauncher.getDockbar().getChildAt(mInfo.screen);
			vp.removeView(this);
			
			mLauncher.getDockbar().fixCellAfterRemove(mInfo.screen);
		}else{
			final CellLayout cellLayout = mLauncher.mWorkspace.getCellLayoutAt(mInfo.screen);
			if(cellLayout != null){				
				cellLayout.removeView(FolderIconTextView.this);
			}
		}
		
		BaseLauncherModel.deleteItemFromDatabase(getContext(), mInfo);
	}

	@Override
	public void applyTheme() {
		textColor = BaseSettingsPreference.getInstance().getAppNameColor();
		shadowColor = ColorUtil.antiColorAlpha(255, textColor);
		paint.setColor(textColor);
		paint.setShadowLayer(1, 1, 1, shadowColor);
		folderEncriptMask = LauncherIconSoftReferences.getInstance().getDefIconFolderEncriptMask(getContext().getResources());
		folderBgBitmap = LauncherIconSoftReferences.getInstance().getDefIconFolderBackground(getContext().getResources());
		androidFolderBgBitmap = LauncherIconSoftReferences.getInstance().getDefIconAndroidFolderBackground(getContext().getResources());
		androidFolderEncriptMask = LauncherIconSoftReferences.getInstance().getDefIconAndroidFolderEncriptMask(getContext().getResources());
		fullScreenFolderBgBitmap = LauncherIconSoftReferences.getInstance().getDefIconFullScreenFolderBackground(getContext().getResources());
		fullScreenFolderEncriptMask = LauncherIconSoftReferences.getInstance().getDefIconFullScreenFolderEncriptMask(getContext().getResources());
		if (BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_IPHONE) {
			mAnimationBackground = new BitmapDrawable(getContext().getResources(), folderBgBitmap);
		} else if(BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_ANDROID_4){
			mAnimationBackground = new BitmapDrawable(getContext().getResources(), androidFolderBgBitmap);
		}else {
			mAnimationBackground = new BitmapDrawable(getContext().getResources(), fullScreenFolderBgBitmap);
		}
		isLargeIconMode = BaseConfig.isLargeIconMode();
		if(isOnDockbar()){
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) getLayoutParams();
			initValue(lp.width, lp.height);
		}else{			
			initValue(FolderIconTextView.this.getWidth(), FolderIconTextView.this.getHeight());
		}
		refresh();
	}

	public void setShowTextBackground(boolean isShowTextBackground) {
		this.isShowTextBackground = isShowTextBackground;
	}

	/**
	 * 注册应用主题图标刷新监听
	 */
	private void registerRefreshIconListenner() {
		mRefreshIconReceiver = new RefreshIconReceiver();
		IntentFilter filter = new IntentFilter(AntBroadcastReceiver.REFRESH_ICON_ACTION);
		filter.addAction(SettingsConstants.ACTION_REFRESH_APP_NAME);
		filter.addAction(AntBroadcastReceiver.ACTION_CHANGE_FOLDER_STYLE);
		getContext().registerReceiver(mRefreshIconReceiver, filter);
	}
	
	/**
	 * 图标刷新广播接收器 
	 */
	private class RefreshIconReceiver extends AntBroadcastReceiver {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			if (null != intent.getAction() && SettingsConstants.ACTION_REFRESH_APP_NAME.equals(intent.getAction())) {
				updateText();
			}else if (null != intent.getAction() && AntBroadcastReceiver.REFRESH_ICON_ACTION.equals(intent.getAction())) {
                refresh();
            }
			isLargeIconMode = BaseConfig.isLargeIconMode();
			initValaueByWH(FolderIconTextView.this.getWidth(), FolderIconTextView.this.getHeight());
			if (BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_IPHONE) {
				mAnimationBackground = new BitmapDrawable(getContext().getResources(), folderBgBitmap);
			} else if(BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_ANDROID_4){
				mAnimationBackground = new BitmapDrawable(getContext().getResources(), androidFolderBgBitmap);
			}else {
				mAnimationBackground = new BitmapDrawable(getContext().getResources(), fullScreenFolderBgBitmap);
			}
			invalidate();
		}
	}
	
	
	
	public void updateText() {
		textColor = BaseSettingsPreference.getInstance().getAppNameColor();
		shadowColor = ColorUtil.antiColorAlpha(255, textColor);
		paint.setColor(textColor);
		paint.setShadowLayer(1, 1, 1, shadowColor);
		paint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		PaintUtils2.assemblyTypeface(paint);
		setText(getText());
	}
	
	public void setDisable(boolean isDisable) {
		this.isDisable = isDisable;
	}

	public boolean isDisable() {
		return isDisable;
	}
	
	public void setUserFolderInfo(FolderInfo info) {
		this.mInfo = info;
	}
	
	public FolderInfo getUserFolderInfo() {
		return mInfo;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			isOnTouchScaleState=true;
			this.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			isOnTouchScaleState=false;
			this.invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			isOnTouchScaleState=false;
			this.invalidate();
			break;
		default:
			break;
			
		}
		return super.onTouchEvent(event);
	}
	
	//在dock栏或情景桌面中，桌面上的文件夹是否显示名称
	private boolean isShowTextOnDockbarOrScene(){
		ItemInfo info = (ItemInfo) getTag();
		if(!BaseConfig.isOnScene()){
			if(isOnDockbar()){
				return LauncherConfig.getLauncherHelper().isShowDockbarText();
			}else{
				return true;
			}
		}

		if(info.container != BaseLauncherSettings.Favorites.CONTAINER_DESKTOP){
			return true;
		}
		
		return LauncherConfig.getLauncherHelper().isShowTextOnScene();
	}
	
	private void initValueOnScene(int widthSize, int heightSize){
		
		ItemInfo info = (ItemInfo) getTag();
		if(LauncherConfig.getLauncherHelper().isSceneFillContentView(this)
				&& info.container == BaseLauncherSettings.Favorites.CONTAINER_DESKTOP){//是否icon图标填充整个View
			isFillContentMode = true;
			isLargeIconMode = false;
			boolean isLandSize = widthSize > heightSize; //是否比较宽
			int size = isLandSize ? heightSize : widthSize;
			
			int padding = getResources().getDimensionPixelSize(R.dimen.text_drawpadding);
			int compIconSize = getResources().getDimensionPixelSize(R.dimen.app_icon_size);
			drawingPadding = (int) (padding * ((float)size / compIconSize));
			paddingForIphoneStyle = (int) (3 * ((float)size / compIconSize));
			marginForIphoneStyle = (int) (4 * ((float)size / compIconSize));
			iconSize = size - 3 * drawingPadding;
			initValue(widthSize, heightSize);
			
			if(LauncherConfig.getLauncherHelper().isSceneFillContentFitCenter(this)){//短边缩放
				iconDestRect = new Rect(0, 0, size, size);
//					iconDestRect.inset(margin, margin);
				iconTop = drawingPadding + paddingForIphoneStyle;
				iconLeft = drawingPadding + paddingForIphoneStyle;
			}else{//长边缩放
				initValue(widthSize, heightSize);
				iconDestRect = new Rect(0, 0, widthSize, heightSize);
				iconDestRect.inset(marginForIphoneStyle, marginForIphoneStyle);
				if(isLandSize){
					iconTop = paddingForIphoneStyle + drawingPadding;
				}else{						
					iconLeft = paddingForIphoneStyle + drawingPadding;
				}
			}

		}else{
			initValue(widthSize, heightSize);
		}
		
	}
	
	/**
	 * 根据componentName查找文件夹中的ApplicationInfo
	 *  @param componentName
	 *  @return
	 */
	public ApplicationInfo getOneSpecialApplicationInfoByComponentName(ComponentName componentName){
		if(mInfo != null){
			for(ApplicationInfo info : mInfo.contents){
				if(info.componentName.equals(componentName)){
					return info;
				}
			}
		}
		return null;
	}

	public boolean isShowNewFlag() {
		return showNewFlag;
	}

	public void setShowNewFlag(boolean showNewFlag) {
		this.showNewFlag = showNewFlag;
	}

	public boolean isShowHint() {
		return mInfo != null && mInfo.showHint;
	}

	/**
	 * 获取图标的绘图区域
	 * */
	public Rect getIconRect() {
		if (isLargeIconMode) {
            Rect rect = new Rect(largeIconDestRect);
            if(BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_FULL_SCREEN) {
                rect.left = rect.left + ScreenUtil.dip2px(getContext(), 1);
                rect.right = rect.right - ScreenUtil.dip2px(getContext(), 1);
            }
            return rect;
		} else {
            Rect rect = new Rect(iconDestRect);
            if(BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_FULL_SCREEN) {
                rect.left = rect.left + ScreenUtil.dip2px(getContext(), 1);
                rect.right = rect.right - ScreenUtil.dip2px(getContext(), 1);
            }
			return rect;
		}
	}
	
	/** 按排序顺序升序,screen 是排序字段  */
	public static class ApplicationInfoPositionAscComparator implements Comparator<ApplicationInfo> {
		@Override
		public int compare(ApplicationInfo a, ApplicationInfo b) {
			return a.screen - b.screen ;
		}
	}
	
	private void drawingOutFolderAni(Canvas canvas) {
		if (!mFolderAni) {
			return;
		}
		aniDiffTime = System.currentTimeMillis() - aniBeginTime;
		float scale = 1.13f - (aniDiffTime * 0.13f / BaseConfig.ALPHA_155);
		if (aniDiffTime >= BaseConfig.ALPHA_155) {
			if (mFolderAni) {
				mFolderAni = false;
				return;
			}

			if(isFillContentMode){
				mAnimationBackground.setBounds(iconDestRect);
			}else if (isLargeIconMode) {
				mAnimationBackground.setBounds(BaseBitmapUtils.caculateRect(iconCenterX, iconCenterY, bgSize * ANI_SCALE, bgSize * ANI_SCALE));
			} else {
				mAnimationBackground.setBounds(BaseBitmapUtils.caculateRect(iconCenterX, iconCenterY, iconSize * ANI_SCALE, iconSize * ANI_SCALE));
			}
			
		}else if (mFolderAni) {
			float size = isLargeIconMode ? bgSize * scale : iconSize * scale;
			Rect rect = BaseBitmapUtils.caculateRect(iconCenterX, iconCenterY, size, size);
			if(isFillContentMode && rect.width() > iconDestRect.width()){
				mAnimationBackground.setBounds(iconDestRect);
			}else{				
				mAnimationBackground.setBounds(rect);
			}
			alphaPaint.setAlpha((int) (aniDiffTime));
//			alphaPaint.setShadowLayer(shadow, 1, 1, Color.BLACK);
			if(isShowTextOnDockbarOrScene()){
				drawingTextBackground(canvas, (int) ((float) TEXT_BACKGROUND_ALPHA * aniDiffTime / 255));
				if (isLargeIconMode) {
					canvas.drawText(text.toString(), getTextLeft(), largeIconTop + bgSize + textHeight, alphaPaint);
				} else {
					canvas.drawText(text.toString(), getTextLeft(), iconTop + iconSize + drawingPadding + textHeight, alphaPaint);
				}
			}
			
			invalidate();
		}
		
		if ((getScrollX() | getScrollY()) == 0) {
			mAnimationBackground.draw(canvas);
		} else {
			canvas.translate(getScrollX(), getScrollY());
			mAnimationBackground.draw(canvas);
			canvas.translate(-getScrollX(), -getScrollY());
		}
		
	}
	
	public void onBeginDrawOutFolderAni() {
		if (this.getVisibility() != View.VISIBLE)
			return;
		mFolderAni = true;
		
		iconAlpha = 255;
		showText = true;
		aniBeginTime = System.currentTimeMillis();
		invalidate();
	}
	
	
	
	public void setNoDrawIndex(int index) {
		mNoDrawIndex = index;
	}
	
	public float[] getThumbsInfo() {
		if (BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_IPHONE) {
			return getThumbsInfoForIphone();
		} else if (BaseSettingsPreference.getInstance().getFolderStyle() == FOLDER_STYLE_FULL_SCREEN) {
			return getThumbsInfoForFullScreen();
		} else {
			return getThumbsInfoForAndroid();
		}
	}

	/**
	 * 获取全屏模式下的缩略图位置
	 * */
	private float[] getThumbsInfoForFullScreen() {
		float position[] = { 0, 0, -1 };
		float x, y;
		float tempScale = (float) bgSize / iconSize;
		float scaleWidth = 0f;
		if (isLargeIconMode) {
			scaleWidth = (bgSize - marginForFullscreenStyle * tempScale * 2) / NUM_COL_FOR_FULL_SCREEN_STYLE - 2 * paddingForFullscreenStyle * tempScale;
		} else {
			scaleWidth = (iconSize - marginForFullscreenStyle * 2) / NUM_COL_FOR_FULL_SCREEN_STYLE - 2 * paddingForFullscreenStyle;
		}
		int i = 0;
		int size = mInfo.contents.size();
		if (size <= 2) {
			i = 0;
		} else if (size > ICON_COUNT_FOR_FULL_SCREEN_STYLE) {
			i = 4;
		} else {
			i = size - 1;
		}
		if (isLargeIconMode) {
			x = largeIconLeft + marginForFullscreenStyle * tempScale + paddingForFullscreenStyle * tempScale * (2 * (i % NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth
					* (i % NUM_COL_FOR_FULL_SCREEN_STYLE);

			y = largeIconTop + marginForFullscreenStyle * tempScale + paddingForFullscreenStyle * tempScale * (2 * (i / NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth
					* (i / NUM_COL_FOR_FULL_SCREEN_STYLE);

		} else {
			x = iconLeft + marginForFullscreenStyle + paddingForFullscreenStyle * (2 * (i % NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth * (i % NUM_COL_FOR_FULL_SCREEN_STYLE);
			y = iconTop + marginForFullscreenStyle + paddingForFullscreenStyle * (2 * (i / NUM_COL_FOR_FULL_SCREEN_STYLE) + 1) + scaleWidth * (i / NUM_COL_FOR_FULL_SCREEN_STYLE);

		}
		position[0] = x;
		position[1] = y;
		position[2] = scaleWidth;
		return position;
	}

	/**
	 * 获取iphone模式下的缩略图位置
	 */
	private float[] getThumbsInfoForIphone() {
		float position[] = { 0, 0, -1 };
		float x, y;
		float tempScale = (float) bgSize / iconSize;
		float scaleWidth = 0f;
		if (isLargeIconMode) {
			scaleWidth = (bgSize - marginForIphoneStyle * tempScale * 2) / NUM_COL_FOR_IPHONE_STYLE - 2 * paddingForIphoneStyle * tempScale;
		} else {
			scaleWidth = (iconSize - marginForIphoneStyle * 2) / NUM_COL_FOR_IPHONE_STYLE - 2 * paddingForIphoneStyle;
		}
		int i = 0;
		int size = mInfo.contents.size();
		if (size <= 2) {
			i = 0;
		} else if (size > ICON_COUNT_FOR_IPHONE_STYLE) {
			// 对于超过数量的缩略图位置要取中心点，先求第一个点位置在以此为基准
			i = 0;
		} else {
			i = size - 1;
		}
		if (isLargeIconMode) {
			x = largeIconLeft + marginForIphoneStyle * tempScale + paddingForIphoneStyle * tempScale * (2 * (i % NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i % NUM_COL_FOR_IPHONE_STYLE);
			y = largeIconTop + marginForIphoneStyle * tempScale + paddingForIphoneStyle * tempScale * (2 * (i / NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i / NUM_COL_FOR_IPHONE_STYLE);

		} else {
			x = iconLeft + marginForIphoneStyle + paddingForIphoneStyle * (2 * (i % NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i % NUM_COL_FOR_IPHONE_STYLE);
			y = iconTop + marginForIphoneStyle + paddingForIphoneStyle * (2 * (i / NUM_COL_FOR_IPHONE_STYLE) + 1) + scaleWidth * (i / NUM_COL_FOR_IPHONE_STYLE);

		}

		if (size > ICON_COUNT_FOR_IPHONE_STYLE) {
			// 对于超过数量的缩略图位置要取中心点，以第一个点位置为基准再向左和下移
			if (isLargeIconMode) {
				x = x + paddingForIphoneStyle * tempScale + scaleWidth / 2f;
				y = y + paddingForIphoneStyle * tempScale + scaleWidth / 2f;
			} else {
				x = x + paddingForIphoneStyle + scaleWidth / 2f;
				y = y + paddingForIphoneStyle + scaleWidth / 2f;
			}

		}

		position[0] = x;
		position[1] = y;
		position[2] = scaleWidth;
		return position;
	}

	private float[] getThumbsInfoForAndroid() {
		float position[] = { 0, 0, -1 };
		float x, y;
		float tempScale = (float) bgSize / iconSize;
		float scaleWidth = 0f;

		int i = 0;
		int size = mInfo.contents.size();
		if (size <= 2) {
			i = 0;
		} else if (size > ICON_COUNT_FOR_ANDROID_STYLE) {
			// 对于超过数量的缩略图位置要取中心点，先求第一个点位置在以此为基准
			i = 1;
		} else {
			i = size - 1;
		}
		if (isLargeIconMode) {
			scaleWidth = (float) (bgSize * (BEGIN_SCALE_FOR_ANDROID_STYLE + coefficientForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i)));
			x = beginXForAndroidStyle - spaceXForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
			y = largeIconTop + marginForAndroidStyle * tempScale + spaceYForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
		} else {
			scaleWidth = (float) (iconSize * (BEGIN_SCALE_FOR_ANDROID_STYLE + coefficientForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i)));
			x = beginXForAndroidStyle - spaceXForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
			y = iconTop + marginForAndroidStyle + spaceYForAndroidStyle * (ICON_COUNT_FOR_ANDROID_STYLE - 1 - i);
		}

		position[0] = x;
		position[1] = y;
		position[2] = scaleWidth;
		return position;
	}
	
	public void setNotDrawIcon(boolean isDraw){
		mNotDrawIcon=isDraw;
	}

	public void setInitValueOnDraw(boolean init) {
		this.initValueOnDraw = init;
	}
	
	/**
	 * 设置限制绘制缩略图标个数
	 * @param mDrawIconLimit
	 */
	public void setDrawIconLimit(int mDrawIconLimit) {
		this.mDrawIconLimit = mDrawIconLimit;
	}

	public void onNotAllowDragEnter() {
		folderNotAvailableHint = true;
		invalidate();
	}

	public void onNotAllowDragExit() {
		folderNotAvailableHint = false;
		invalidate();
	}
}
