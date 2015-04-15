package com.bitants.launcherdev.framework.effect;

import java.util.Random;

import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.config.BaseConfig;

/**
 * 特效类型
 */
public class EffectsType {

	/**
	 * 默认
	 */
	public static final int DEFAULT = 0;

	/**
	 * 随机
	 */
	public static final int RANDOM = 1;
	/**
	 * 层叠
	 */
	public static final int CASCADE = 2;

	/**
	 * 翻滚
	 */
	public static final int ROLL = 3;

	/**
	 * 翻转
	 */
	public static final int TURN = 4;

	/**
	 * 风车
	 */
	public static final int WINDOWER = 5;
	/**
	 * 盒子(内)
	 */
	public static final int CUBE_INSIDE = 6;
	/**
	 * 盒子(外)
	 */
	public static final int CUBE_OUTSIDE = 7;
	/**
	 * 百叶窗
	 */
	public static final int SHUTTER = 8;
	/**
	 * 弦
	 */
	public static final int CHORD = 9;
	/**
	 * 双子星
	 */
	public static final int BINARY_STAR = 10;
	/**
	 * 滚咕噜滚(车轮)
	 */
	public static final int WHEEL = 11;
	/**
	 * 球
	 */
	public static final int GLOBAL = 12;
	/**
	 * 圆柱
	 */
	public static final int CYLINDER = 13;
	/**
	 * 龙卷风
	 */
	public static final int TORNADO = 14;

	/**
	 * 平移
	 */
	public static final int TRANSFER = 15;

	/**
	 * 转盘
	 */
	public static final int TURNTABLE = 16;

	/**
	 * 贪吃蛇
	 */
	public static final int SNAKE = 17;
	/**
	 * 时光隧道
	 * */
	public static final int TIMETUNNEL = 18;
	
	/**
	 * 开门
	 * */
	public static final int OPEN_DOOR = 19;
	
	/**
	 * LG褶皱内特效
	 * */
	public static final int LG_CUBE_INSIDE = 20;
	
	
	
	//==============================点心桌面特效========================================
	/**
	 * 折扇
	 */
	public static final int DX_SQUASH = 21;
	
	/**
	 * 旋转木马
	 */
	public static final int DX_CAROUSEL = 22;
	
	/**
	 * 淡入淡出
	 */
	public static final int DX_CROSSFADE = 23;
	
	/**
	 * 荷兰风车
	 */
	public static final int DX_WINDMILL = 24;
	/**
	 * 时空穿越
	 */
	public static final int DX_PAGEZOOM = 25;
	
	/**
	 * 滑行(向下)
	 */
	public static final int DX_PAGESLIDEDOWN = 26;
	
	/**
	 * 滑行(向上)
	 */
	public static final int DX_PAGESLIDEUP = 27;
	
	/**
	 * 滑梯
	 */
	public static final int DX_VERTICALSCROLLING = 28;
	
	/**
	 *  楼梯(向下)
	 */
	public static final int DX_STAIRDOWNLEFT = 29;
	
	/**
	 *  楼梯(向上)
	 */
	public static final int DX_STAIRDOWNRIGHT = 30;
	
	/**
	 *  立方体
	 */
	public static final int DX_CUBEOUTSIDE = 31;
	
	/**
	 * 扇面
	 */
	public static final int DX_TURNTABLE = 32;
	/**
	 *  摩天轮
	 */
	public static final int DX_ROTATING = 33;
	
	/**
	 *  百叶窗
	 */
	public static final int DX_LOUVERWINDOW = 34;
	
	/**
	 * 旋转翻页
	 */
	public static final int DX_PAGEWAVE = 35;
	
	//==============================点心桌面特效========================================
		
		
	private static int mCurrentScreenEffect;
	private static int mCurrentDrawerEffect;
	
	private static Random rand;
	private static int[] screenEffects;
	private static int[] drawerEffects;
	private static final int EFFECT_SPLIT = 2 ;

	/**
	 * 获取当前特效
	 * @return 特效类型代码
	 */
	public static int getCurrentEffect() {
		if(BaseConfig.isOnScene()){
			mCurrentScreenEffect = EffectsType.DEFAULT;
		}
		return mCurrentScreenEffect;
	}

	/**
	 * 设置当前特效
	 * @param type 特效类型代码
	 */
	public static void setCurrentEffect(int type) {
		if (type != RANDOM) {
			mCurrentScreenEffect = type;
		} else {
			if(rand == null){
				rand = new Random();
			}
			if(screenEffects == null){
				screenEffects = LauncherConfig.getLauncherHelper().getScreenEffectsForRandom();
			}
			if(rand == null || screenEffects == null || screenEffects.length <= EFFECT_SPLIT)
				return;
			int i = rand.nextInt(screenEffects.length - EFFECT_SPLIT);
			mCurrentScreenEffect = screenEffects[i + EFFECT_SPLIT];
		}
	}

	/**
	 * 是否左边屏幕优先
	 * @return true表示左边屏幕优先
	 */
	public static boolean isLeftScreenFirst() {
		if (mCurrentScreenEffect == CASCADE)
			return false;
		return true;
	}
	
	
	/**
	 * 获取匣子当前特效
	 * @return 特效类型代码
	 */
	public static int getCurrentDrawerEffect() {
		return mCurrentDrawerEffect;
	}

	/**
	 * 设置匣子当前特效
	 * @param type 特效类型代码
	 */
	public static void setCurrentDrawerEffect(int type) {
		if (type != RANDOM) {
			mCurrentDrawerEffect = type;
		} else {
			if(rand == null){
				rand = new Random();
			}
			if(drawerEffects == null){
				drawerEffects = LauncherConfig.getLauncherHelper().getDrawerEffectsForRandom();
			}
			if(rand == null || drawerEffects == null || drawerEffects.length <= EFFECT_SPLIT)
				return;
			int i = rand.nextInt(drawerEffects.length - EFFECT_SPLIT);
			mCurrentDrawerEffect = drawerEffects[i + EFFECT_SPLIT];
		}
	}

	/**
	 * 是否匣子左边屏幕优先
	 * @return true表示左边屏幕优先
	 */
	public static boolean isDrawerLeftScreenFirst() {
		if (mCurrentDrawerEffect == CASCADE)
			return false;
		return true;
	}
}
