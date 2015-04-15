package com.bitants.launcherdev.launcher.support;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.bitants.launcherdev.framework.view.commonsliding.CommonLightbar;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.nd.android.pandahome2.R;
import com.bitants.launcherdev.framework.ViewFactory;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLightbar;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLightbar;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.screens.CellLayout;

/**
 * Description: 桌面长按编辑模式
 * Author: guojy
 * Date: 2013-9-26 下午5:51:51
 */
public class WorkspaceSpring {
	private BaseLauncher mLauncher;
	private ScreenViewGroup mWorkspace;
	private Context mContext;

	private float springScale = 1.0f;
	/**
	 * 编辑模式动画后的CellLayout页面宽
	 */
	private int springPageWidth;
	/**
	 * 编辑模式下屏幕间隔
	 */
	private int springPageGap;
	/**
	 * 编辑模式下能看到的左右两屏宽度
	 */
	private int springPageSplit;
	/**
	 * 编辑模式屏幕移动的宽度
	 */
	private int snapMoveDistance;
	/**
	 * 编辑模式下,打开的初始页面
	 */
	private int mSpringStartPage;
	/**
	 * 编辑模式下,页面偏移量x
	 */
	private int springPageOffsetX;
	/**
	 * 各个编辑屏中心点X轴坐标（相对于整个画布的坐标）
	 */
	private int[] mSpringScreenCenterX;
	
	/**
	 * 编辑模式下,页面动画沿Y轴方向的距离
	 */
	private int springPageTranslationY;
	
	/**
	 * 编辑模式下，是否有最后一个“添加”屏
	 */
	public boolean hasSpringAddScreen;
	
	/**
	 * 编辑模式指示灯
	 */
	private CommonLightbar springLightbar;
	/**
	 * 编辑模式指示灯是否处于动画状态
	 */
	private boolean onSpringLightBarAnimation;
	
	/**
	 * 编辑模式进行缩放的中心点相对位置
	 */
	private static final float springScaleCenterX = 0.5f;
	private static final float springScaleCenterY = 0.1f;
	private static float springScaleForDrawerCenterY = 0.75f;
	
	/**
	 * 是否处于退出编辑模式的动画中
	 */
	private boolean isOnExitSpringModeAnimation;
	/**
	 * 是否处于打开编辑模式的动画中
	 */
	private boolean isOnEnterSpringModeAnimation;
	/**
	 * 编辑模式的动画默认时间
	 */
	private static final long springAniDefaultDruation = 400L;
	
	private boolean lockUpdateWallPaper = false;
	
	private int springAnimationType;
	public static final int SPRINGANIMATION_NORMAL = 0;
	public static final int SPRINGANIMATION_REBOOT = 1;
	public static final int SPRINGANIMATION_DRAWER = 2;
	
	private boolean springModeWithEditor = false;//标示是否处于有底部栏的编辑模式
	private float springGapFactor = 1/10f; //具有editor的编辑模式下屏幕间隔的缩放系数

	private float springPageSplitFactor = 1/10f;//具有editor的编辑模式下显示部分屏幕的缩放系数
	
	private boolean notDrawDelBtn = false;
	
	public WorkspaceSpring(ScreenViewGroup mWorkspace){
		this.mWorkspace = mWorkspace;
	}
	
	
	public void setLauncher(BaseLauncher mLauncher) {
		this.mLauncher = mLauncher;
		mContext = mLauncher;
	}

	/**
	 * Description: 关闭编辑模式动画
	 * Author: guojy
	 * Date: 2012-7-15 下午04:13:05
	 */
	public void animationNormalMode(boolean removeSpringAddScreen){
		mWorkspace.hideEditor();
		
		resolve2NormalSpringMode();
		animationNormalMode(removeSpringAddScreen, springAniDefaultDruation, false);
	}
	/**
	 * Description: 退回程序匣子时，关闭编辑模式动画
	 * Author: guojy
	 * Date: 2012-8-16 上午09:51:50
	 */
	public void animationNormalModeFromDrawer(boolean removeSpringAddScreen){
		animationNormalMode(removeSpringAddScreen, springAniDefaultDruation, true);
	}
	
	public void animationNormalMode(final boolean removeSpringAddScreen, long duration, boolean isFromDrawer){
		//显示导航页
		mLauncher.showNavigation();
		
		float scaleFrom = springScale;
		float scaleTo = 1.0f;
		
		setOnEnterSpringModeAnimation(false);
		setOnExitSpringModeAnimation(true);
		
		if(springLightbar != null){
			springLightbar.clearAnimation();
			springLightbar.setVisibility(View.GONE);
		}
		
		
		float scaleCenterY = isFromDrawer ? springScaleForDrawerCenterY : springScaleCenterY;
		final int screenCount = mWorkspace.getChildCount();
		final int mCurrentScreen = mWorkspace.getCurrentScreen();
		for (int i = 0; i < screenCount; i++) {
            final CellLayout cl = (CellLayout)mWorkspace.getChildAt(i);
            cl.setSpringAddScreen(false);
            cl.setSpringAnimationStartTime(0);
            Animation scaleAnimation = new ScaleAnimation(scaleFrom, scaleTo, scaleFrom, scaleTo, 
            		Animation.RELATIVE_TO_SELF, springScaleCenterX, Animation.RELATIVE_TO_SELF, scaleCenterY);
        	if(i == mCurrentScreen){
        		scaleAnimation.setDuration(duration);
        	}else{//退出编辑模式时，非当前屏动画时间为10，来提高退出后的滑屏效果
        		scaleAnimation.setDuration(10);
        	}
        	
        	if(i == mCurrentScreen){//当不在springStartPage屏退出编辑模式时，修复动画偏移效果
        		mWorkspace.scrollTo(mCurrentScreen * mWorkspace.getModeWidth(), 0);
        	}
        	
        	scaleAnimation.setFillAfter(true);
        	final int screen = i;
        	scaleAnimation.setAnimationListener(new AnimationListener() {  
        		private boolean isStartAni = false;
                public void onAnimationStart(Animation animation) {  
                	isStartAni = true;
                	if(screen == mCurrentScreen){
                		mWorkspace.updateWallpaperForSpring();//更新壁纸位置
                	}
                }  
                public void onAnimationRepeat(Animation animation) {  
                }  
                public void onAnimationEnd(Animation animation) {  
                	if (!isStartAni) {
                		return;
                	}
                	isStartAni = false;
                	if(screen == mCurrentScreen){//更新滚动条
                		// commonLightbar.refresh(getChildCount(), mCurrentScreen);
                		setOnExitSpringModeAnimation(false);
                		mWorkspace.cleanSpringResource();
                		cleanSpringAnimation();
                		
                		//移除添加新屏的空白屏
                		if(hasSpringAddScreen && removeSpringAddScreen && BaseConfigPreferences.getInstance().hasSpringAddScreen()){
                			int index = mWorkspace.getChildCount() - 1;
                			if(mWorkspace.getCellLayoutAt(index) != null && mWorkspace.getCellLayoutAt(index).getChildCount() == 0){                				
                				mWorkspace.removeScreenFromWorkspace(index);
                			}
                		}
                		BaseConfigPreferences.getInstance().setHasSpringAddScreen(false);
        				hasSpringAddScreen = false;
        				
                		mWorkspace.getLightBar().setSize(mWorkspace.getChildCount());
                		mWorkspace.updateLightbar();	
                		//刷新，防止可能存在边框
                		mWorkspace.getCurrentCellLayout().invalidate();
                		mWorkspace.onCurScreenAniNormalMode();
                	}
                }  
            });
        	cl.startAnimation(scaleAnimation);
        }
	}
	/**
	 * Description: 返回程序匣子时，退出编辑模式
	 * Author: guojy
	 * Date: 2012-8-6 上午10:24:15
	 */
	public void changeToNormalModeFromDrawer(boolean removeSpringAddScreen, boolean isShowDrawer) {
		mLauncher.showBottomContainer();
		// mLauncher.getCommonLightbar().setVisibility(View.VISIBLE);
		if(isShowDrawer){//当从桌面退回到程序匣子时
			animationNormalMode(removeSpringAddScreen, 50, true);
			setOnExitSpringModeAnimation(false);//动画不使用渐变
		}else{
			animationNormalModeFromDrawer(removeSpringAddScreen);
		}
	}
	/**
	 * Description: 从程序匣子进入编辑模式
	 * Author: guojy
	 * Date: 2012-8-6 上午10:24:08
	 */
	public void changeToSpringModeFromDrawer(int fromTab){
		mSpringStartPage = mWorkspace.getCurrentScreen();
		setTranslateYForDrawerSpring();//设置动画Y轴偏移量
		
		mWorkspace.initSpringResource(true, 0);
		animationSpringModeFromDrawer();
		
		mLauncher.hideBottomContainer();
		// mLauncher.getCommonLightbar().setVisibility(View.GONE);
		//EffectsType.setCurrentEffect(EffectsType.DEFAULT);//滑屏不使用特效
		//mWorkspace.showSpringMoveToEdgeHint();//第一次是显示提示信息
	}
	/**
	 * @param removeSpringAddScreen 是否增加一个“添加”屏
	 * @param tab - 默认显示数据集
	 * @return
	 */
	public void changeToSpringMode(boolean createSpringAddScreen, String tab) {
		change2SpringModeWithEditor();
		mSpringStartPage = mWorkspace.getCurrentScreen();
		setTranslateY();//设置动画Y轴偏移量
		
		mWorkspace.initSpringResource(false, 0);
		
//		mLauncher.setupEditor(calcEditorHeight());
		mWorkspace.showEditor(calcEditorHeight(), tab);
		animationSpringMode(createSpringAddScreen);
		
//		mLauncher.showEditorWithAnimation(tab);
	}
	/**
	 * Description: 打开编辑模式动画
	 * Author: guojy
	 * Date: 2012-7-12 下午09:26:41
	 */
	public void animationSpringMode(boolean createSpringAddScreen){
		springAnimationType = SPRINGANIMATION_NORMAL;
		animationSpringMode(createSpringAddScreen, springAniDefaultDruation, false);
	}
	
	/**
	 * Description: 从程序匣子进入编辑模式动画
	 * Author: guojy
	 * Date: 2012-8-16 上午09:46:13
	 */
	public void animationSpringModeFromDrawer(){
		resolve2NormalSpringMode();
		springAnimationType = SPRINGANIMATION_DRAWER;
		animationSpringMode(true, 50, true);
	}

	/**
	 * Description: 在编辑模式下重新进入编辑模式动画
	 * Author: guojy
	 * Date: 2012-8-16 上午09:46:36
	 */
	public void animationSpringModeReboot(){
		springAnimationType = SPRINGANIMATION_REBOOT;
		animationSpringMode(true, 200, false);
	}
	
	public void animationSpringMode(boolean createSpringAddScreen, long duration, boolean isFromDrawer){
		mWorkspace.inflateSpringLightbar();
		
		//隐藏导航页
		mLauncher.hideNavigation();
		
		final float scaleFrom = (springAnimationType == SPRINGANIMATION_REBOOT) ? springScale : 1.0f;
		final float scaleTo = springScale;
		//添加新屏的空白屏
		if(mWorkspace.getChildCount() < ScreenViewGroup.MAX_SCREEN && createSpringAddScreen){
			mWorkspace.createScreenToWorkSpace();
			BaseConfigPreferences.getInstance().setHasSpringAddScreen(true);
			hasSpringAddScreen = true;
		}else{
			hasSpringAddScreen = false;
		}
		//计算各个编辑屏中心点X轴坐标（相对于整个画布的坐标）
		initSpringScreenCenterX();
		//编辑模式的指示灯动画
		final int mCurrentScreen = mWorkspace.getCurrentScreen();
		springLightbar.setVisibility(View.VISIBLE);
		springLightbar.refresh(mWorkspace.getChildCount(), mCurrentScreen);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(1000);
		alphaAnimation.setAnimationListener(new AnimationListener() {  
            public void onAnimationStart(Animation animation) { 
            	onSpringLightBarAnimation = true;
            }  
            public void onAnimationRepeat(Animation animation) {  
            }  
            public void onAnimationEnd(Animation animation) {  
            	onSpringLightBarAnimation = false;
            }  
        });
		springLightbar.startAnimation(alphaAnimation);
		
		setOnExitSpringModeAnimation(false);
		setOnEnterSpringModeAnimation(true);
		//各屏动画
		float scaleCenterY = isFromDrawer ? springScaleForDrawerCenterY : springScaleCenterY;
		final int screenCount = mWorkspace.getChildCount();
		
		for (int i = 0; i < screenCount; i++) {
			final CellLayout cl = (CellLayout)mWorkspace.getChildAt(i);
            if(i == screenCount-1 && hasSpringAddScreen){//标示为“添加”屏
            	cl.setSpringAddScreen(true);
            }else{
            	cl.setSpringAddScreen(false);
            }
            cl.setCellLayoutLocation(i);
            cl.setAcceptDropOnSpringMode();
            cl.resetScreen();
            cl.setSpringAnimationStartTime(0);
            
            AnimationSet animatorSet = new AnimationSet(true);
            Animation scaleAnimation = new ScaleAnimation(scaleFrom, scaleTo, scaleFrom, scaleTo, 
            		Animation.RELATIVE_TO_SELF, springScaleCenterX, Animation.RELATIVE_TO_SELF, scaleCenterY);
            animatorSet.addAnimation(scaleAnimation);
            
        	final int toX = (mSpringStartPage - i)*springPageOffsetX;
            float translationXFrom = 0;
            float translationXTo = toX;
            TranslateAnimation translateAnimation = new TranslateAnimation(translationXFrom, translationXTo,  0, 0);
        	animatorSet.addAnimation(translateAnimation);
        	animatorSet.setFillAfter(true);
        	final int screen = i;
        	animatorSet.setAnimationListener(new AnimationListener() {  
                public void onAnimationStart(Animation animation) {  
                	if(screen == mSpringStartPage){
                		mWorkspace.updateWallpaperOffset();//更新壁纸位置
                	}
                }  
                public void onAnimationRepeat(Animation animation) {  
                }  
                public void onAnimationEnd(Animation animation) {
                	if(screen == mSpringStartPage){
                		setOnEnterSpringModeAnimation(false);
                		mWorkspace.delayRefreshSpringScreen(50);//因添加抗锯齿，小部件显示可能有框，故刷新
                		mWorkspace.onCurScreenAniSpringMode();
                	}
                	mWorkspace.destoryChildHardwareLayer(screen);
                }  
            });
        	if(i >= mCurrentScreen-1 && i <= mCurrentScreen +1){
        		animatorSet.setDuration(duration);
        	}else{
        		animatorSet.setDuration(0);
        	}
        	cl.startAnimation(animatorSet);
        	
        	
        }
	}
	
	public boolean isHasSpringAddScreen() {
		return hasSpringAddScreen;
	}

	private void cleanSpringAnimation(){
		final int screenCount = mWorkspace.getChildCount();
		for (int i = 0; i < screenCount; i++) {
            CellLayout cl = (CellLayout)mWorkspace.getChildAt(i);
            if(null != cl){
            	cl.clearAnimation();
            }
		}
	}
	
	/**
	 * Description: 是否处于打开编辑模式的动画中
	 * Author: guojy
	 * Date: 2012-7-27 下午02:07:57
	 */
	public boolean isOnEnterSpringModeAnimation(){
		return isOnEnterSpringModeAnimation;
	}
	public void setOnEnterSpringModeAnimation(boolean animationState){
		isOnEnterSpringModeAnimation = animationState;
	}
	
	/**
	 * Description: 是否处于退出编辑模式的动画中
	 * Author: guojy
	 * Date: 2012-7-26 上午10:21:00
	 */
	public boolean isOnExitSpringModeAnimation(){
		return isOnExitSpringModeAnimation;
	}
	public void setOnExitSpringModeAnimation(boolean animationState){
		isOnExitSpringModeAnimation = animationState;
	}
	
	/**
	 * Description: 修复退出编辑模式动画中断时的错误状态
	 * Author: guojy
	 * Date: 2012-8-8 下午03:52:27
	 */
	public void fixExitSpringModeAnimationState(){
		if(isOnExitSpringModeAnimation() && !mWorkspace.isOnSpringMode())
			setOnExitSpringModeAnimation(false);
	}
	
	/**
	 * Description: 计算各个编辑屏中心点X轴坐标（相对于整个画布的坐标）
	 * Author: guojy
	 * Date: 2012-7-19 下午05:05:52
	 */
	private void initSpringScreenCenterX(){
		int arrayLen = mWorkspace.getChildCount();
		mSpringScreenCenterX = new int[arrayLen];
		int leftPadding = getLeftPaddingForSpringMode();
		for(int i=0; i<arrayLen; i++){
			mSpringScreenCenterX[i] = leftPadding + i*(springPageWidth + springPageGap) + springPageWidth/2;
			((CellLayout)mWorkspace.getChildAt(i)).setSpringScreenCenterX(mSpringScreenCenterX[i]);
		}
	}
	
	/**
	 * 
	 * Description: 编辑模式下，第一屏离父View的左边距
	 * Author: guojy
	 * Date: 2012-7-18 下午08:34:05
	 */
	private int getLeftPaddingForSpringMode(){
		return springPageSplit + springPageGap+mSpringStartPage*(springPageGap + 2*springPageSplit);
	}
	/**
	 * Description: 编辑模式下，当前屏的动画前与动画后的位置偏移量
	 * Author: guojy
	 * Date: 2012-7-15 下午04:34:01
	 */
	public int getAdjustXBySpringMode(){
		return (mWorkspace.getCurrentScreen()-mSpringStartPage)*springPageOffsetX;
	}
	
	public int getAdjustXBySpringMode(int i){
		return (i-mSpringStartPage)*springPageOffsetX;
	}
	
	public void countSpringLightbarPost(){
		if(springLightbar == null)
			return;
		
		int topPadding = 0;
		ViewGroup.LayoutParams lParams=springLightbar.getLayoutParams();
		topPadding = mLauncher.getResources().getDimensionPixelSize(R.dimen.workspace_spring_lightbar_toppadding);
		lParams = springLightbar.getLayoutParams();
		lParams.height = mLauncher.getResources().getDimensionPixelSize(R.dimen.workspace_spring_lightbar_height);
		
		springLightbar.setPadding(springLightbar.getPaddingLeft(), topPadding,
				springLightbar.getPaddingRight(), springLightbar.getPaddingBottom());
	}
	
	public void setSpringLightbar(CommonLightbar springLightbar) {
		countSpringLightbarPost();
		this.springLightbar = springLightbar;
	}
	
	public void updateSpringLightbar(){
		if(mWorkspace.isOnSpringMode() && !onSpringLightBarAnimation && springLightbar != null){
			springLightbar.update(mWorkspace.getCurrentScreen());
		}
	}
	
	/**
	 * Description: 获取编辑模式的CellLayout缩放比例
	 * Author: guojy
	 * Date: 2012-7-22 上午11:36:27
	 */
	public float getSpringScale(){
		return springScale;
	}
	/**
	 * Description: 获取编辑模式缩放后左右CellLayout露出的距离
	 * Author: guojy
	 * Date: 2012-7-21 下午02:43:55
	 */
	public int getSpringPageSplit(){
		return springPageSplit;
	}
	/**
	 * Description: 获取编辑模式缩放后CellLayout的间距
	 * Author: guojy
	 * Date: 2012-7-24 上午10:17:46
	 */
	public int getSpringPageGap(){
		return springPageGap;
	}
	
	
	/**
	 * 
	 * @description 设置编辑模式下的页面间距和左右显示出来的页面大小
	 * @author Michael
	 * @date 2013-05-28 上午8:19:00
	 */
	private void setPageSplitAndSpaceGap(int pageSplit, int pageGap){
		springPageSplit = pageSplit;
		springPageGap = pageGap;
		springPageWidth = mWorkspace.getScreenWidth() - (springPageSplit + springPageGap) * 2;
		springScale = springPageWidth * 1.0f / mWorkspace.getScreenWidth();

		if(BaseConfig.isOnScene()){//情景模式，重新计算编辑模式下页间距
			springScale = fixSpringScaleOnScene(springScale);
			//springPageSplit = springPageGap = (int) (mWorkspace.getScreenWidth()*(1 - springScale)/4);
			//springPageWidth = mWorkspace.getScreenWidth() - (springPageSplit + springPageGap) * 2;
		}
		snapMoveDistance = springPageWidth + springPageGap;
		springPageOffsetX = 2*springPageSplit + springPageGap;
	}
	
	

	/**
	 * 
	 * @description 设置无editor的编辑模式下的页面间距和左右显示出来的页面默认大小
	 * @author Michael
	 * @date 2013-05-28 上午8:19:00
	 */
	public void setPageSplitAndSpaceGapDefault(){
		setPageSplitAndSpaceGap(mWorkspace.getScreenWidth()/14, mWorkspace.getScreenWidth()/30);
	}
	
	/**
	 * @description 进入到编辑模式 修改编辑模式下的参数
	 * @author Michael
	 * @createtime 2013-6-5
	 */
	public void change2SpringModeWithEditor(){
		springModeWithEditor = true;
		setPageSplitAndSpaceGap((int)(mWorkspace.getScreenWidth()*springPageSplitFactor), 
				(int)(mWorkspace.getScreenWidth()*springGapFactor));
	}
	
	public void setSpringPageSplitAndGap(){
		if(springModeWithEditor){
			change2SpringModeWithEditor();
		}else{
			setPageSplitAndSpaceGapDefault();
		}
	}
	
	public void setSpringModeWithEditor(boolean springModeWithEditor) {
		this.springModeWithEditor = springModeWithEditor;
	}

	/**
	 * Description: 修复无dock栏桌面的缩放比例
	 * Author: guojy
	 * Date: 2013-8-28 下午3:58:39
	 */
	private float fixSpringScaleOnScene(float springScale){
		if(BaseConfig.isOnScene()){
			int dockbarHeight = mLauncher.getResources().getDimensionPixelSize(R.dimen.button_bar_height);
			float rate = (float)(mWorkspace.getScreenHeight() - CellLayoutConfig.getMarginTop() - dockbarHeight)/mWorkspace.getPageHeight();
			springScale = rate > 1 ? springScale : springScale * rate;
			
			if(CellLayoutConfig.getMarginBottom() == 0){//无底部dock栏
				springScaleForDrawerCenterY = 0.5f;
			}
		}
		return springScale;
	}
	
	/**
	 * 计算LauncherEditorView的高度
	 * @author Michael
	 * @createtime 2013-6-25 
	 * @return
	 */
	public float calcEditorHeight(){
		float m_workspaceTopPadding = CellLayoutConfig.getMarginTop();
		float m_topDiffNormalAndSpring = BaseCellLayoutHelper.topDiffNormalAndSpring;
		int screenWidth = mWorkspace.getScreenWidth();
		float springScale = (screenWidth - 2*(screenWidth*springPageSplitFactor+
				screenWidth*springGapFactor))/screenWidth;
		if(BaseConfig.isOnScene()){
			springScale = fixSpringScaleOnScene(springScale);
		}
		float m_cellHeight = mWorkspace.getPageHeight() * springScale;
		return mWorkspace.getScreenHeight() -
				(m_workspaceTopPadding + m_topDiffNormalAndSpring + m_cellHeight)- ScreenUtil.dip2px(mContext, 15) ;
	}
	
	/**
	 * @description  恢复进入到编辑模式前的状态
	 * @author Michael
	 * @createtime 2013-6-5
	 */
	private void resolve2NormalSpringMode(){
		springModeWithEditor = false;
		mWorkspace.cleanSpringOtherStuff();
	}
	
	
	/**
	 * Description: 编辑模式下，新增、删除或退出编辑屏
	 * Author: guojy
	 * Date: 2012-8-9 上午10:02:17
	 */
	public void handleOnSpringActionUp(MotionEvent ev) {
		//计算编辑模式当前页面范围
		int left = springPageSplit + springPageGap;
		int top = CellLayoutConfig.getMarginTop()+(int)(mWorkspace.getPageHeight() * (1 - springScale) * springScaleCenterY);
		Rect springPageRect = new Rect(left, top,  left+springPageWidth, top+ (int)(mWorkspace.getPageHeight()*springScale));
		
		final int x = (int) ev.getX() - getAdjustXBySpringMode();
		final int y = (int) ev.getY();
		if (mWorkspace.isOnSpringAddScreen()) {// 点击"新增"屏
			if (springPageRect.contains(x, y)) {
				animationSpringModeReboot();
			}
		} else {// 点击其它屏时
			int[] xy = { x, y };
			BaseCellLayoutHelper.springToNormalCoordinateEx(xy);
			CellLayout curCellLayout = mWorkspace.getCurrentCellLayout();
			if(!BaseLauncher.hasDrawer && curCellLayout.getChildCount() > 0 && springPageRect.contains(x, y)){
				mWorkspace.changeToNormalMode();
				return;
			}
			if (mWorkspace.getChildCount() > 2 && curCellLayout.isOnSpringDelScreenBtn(xy[0], xy[1])) {// 删除当前屏，最后一屏不能删除
				if (curCellLayout.getChildCount() > 0) {// 删除非空白屏
					for(int i = 0; i < curCellLayout.getChildCount(); i ++){//含有应用列表无法删除
						if(mWorkspace.isAllAppsIndependence(curCellLayout.getChildAt(i))){
							ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.common_tip), 
									mContext.getString(R.string.message_preview_delete_screen_not_allow),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}},
									true).show();
							return;
						}
					}
					ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.common_tip), 
							mContext.getString(R.string.message_preview_delete_screen), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteScreenOnSpringMode();
						}
					}, null).show();
				} else {// 删除空白屏
					deleteScreenOnSpringMode();
				}
			} else{
				if (springPageRect.contains(x, y)) {
					mWorkspace.changeToNormalMode();
				}
			}
		}
	}
	/**
	 * Description: 在编辑模式下删除当前屏
	 * Author: guojy
	 * Date: 2012-7-18 下午08:22:39
	 */
	public void deleteScreenOnSpringMode(){
		int curScreen = mWorkspace.getCurrentScreen();
		final int targetScreen = Math.max(curScreen - 1, 0);
		
		lockUpdateWallPaper = true;//防止多次更新壁纸产生跳跃
		if(hasSpringAddScreen){
			mWorkspace.removeScreenFromWorkspace(mWorkspace.getChildCount() - 1);//删除"添加"屏
		}
		mWorkspace.removeScreenFromWorkspace(curScreen);//删除当前屏
		lockUpdateWallPaper = false;
		//scroll到targetScreen屏位置
		mWorkspace.scrollTo(targetScreen * snapMoveDistance + getLeftPaddingForSpringMode()- (springPageSplit + springPageGap), 0);
		
		mSpringStartPage = targetScreen;
		animationSpringModeReboot();
		mWorkspace.setCurrentScreen(targetScreen);
		
		int defaultScreen = mWorkspace.getDefaultScreen();
		if(curScreen <= defaultScreen){
			defaultScreen --;
		}
		mWorkspace.setDefaultScreen(Math.max(0, defaultScreen));
//		HiAnalytics.submitEvent(mContext, AnalyticsConstant.LAUNCHER_SCREEN_EDIT_DEL);
	}
	
	public int getSnapMoveDistance() {
		return snapMoveDistance;
	}

	public boolean isLockUpdateWallPaper() {
		return lockUpdateWallPaper;
	}

	public boolean isSpringReboot(){
		return springAnimationType == SPRINGANIMATION_REBOOT; 
	}
	
	public boolean isSpringFromDrawer(){
		return springAnimationType == SPRINGANIMATION_DRAWER; 
	}
	
	/**
	 * Description: 获取编辑模式下,页面动画沿Y轴方向的距离
	 * Author: guojy
	 * Date: 2012-7-24 下午03:27:45
	 */
	public int getSpringPageTranslationY(){
		return springPageTranslationY;
	}
	
	public void setTranslateY(){
		springPageTranslationY = -(int) (mWorkspace.getPageHeight()*(springScaleCenterY-springScaleCenterX)*(1-springScale));
	}
	
	public void setTranslateYForDrawerSpring(){
		springPageTranslationY = -(int) (mWorkspace.getPageHeight()*(springScaleForDrawerCenterY-springScaleCenterX)*(1-springScale));
	}
	
	public static float getSpringScaleCenterY() {
		return springScaleCenterY;
	}
	
	public static float getSpringScaleForDrawerCenterY() {
		return springScaleForDrawerCenterY;
	}
	
	public void setSpringGapFactor(float springGapFactor) {
		this.springGapFactor = springGapFactor;
	}

	public void setSpringPageSplitFactor(float springPageSplitFactor) {
		this.springPageSplitFactor = springPageSplitFactor;
	}
	
	public boolean isNotDrawDelBtn() {
		return notDrawDelBtn;
	}

	public void setNotDrawDelBtn(boolean notDrawDelBtn) {
		this.notDrawDelBtn = notDrawDelBtn;
	}
}
