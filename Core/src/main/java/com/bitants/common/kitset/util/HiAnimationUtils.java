package com.bitants.common.kitset.util;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;

/**
 * 动画工具类
 */
public class HiAnimationUtils {
	/**
	 * 创建缩放动画
	 * @param from
	 * @param to
	 * @param pivot
	 * @param duration
	 * @return AnimationSet
	 */
	public static AnimationSet createScaleEnterAnamation(float from, float to, float pivot, int duration) {
		return createScaleEnterAnamation(from, to, from, to, pivot, pivot, duration, null, null);
	}
	
	/**
	 * 创建缩放动画
	 * @param from
	 * @param to
	 * @param pivot
	 * @param duration
	 * @param lis
	 * @return AnimationSet
	 */
	public static AnimationSet createScaleEnterAnamation(float from, float to, float pivot, int duration, AnimationListener lis) {
		return createScaleEnterAnamation(from, to, from, to, pivot, pivot, duration, null, lis);
	}
	
	/**
	 * 创建缩放动画
	 * @param fromX
	 * @param toX
	 * @param fromY
	 * @param toY
	 * @param pivotX
	 * @param pivotY
	 * @param duration
	 * @param r
	 * @return AnimationSet
	 */
	public static AnimationSet createScaleEnterAnamation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, int duration, Interpolator r) {
		return createScaleEnterAnamation(fromX, toX, fromY, toY, pivotX, pivotY, duration, null, null);
	}
	
	/**
	 * 创建缩放动画
	 * @param fromX 动画前水平方向缩放比例
	 * @param toX 动画后水平方向缩放比例
	 * @param fromY  动画前垂直方向缩放比例
	 * @param toY 动画后垂直方向缩放比例
	 * @param pivotX x轴方向基于Animation.RELATIVE_TO_PARENT类型值
	 * @param pivotY y轴方向基于Animation.RELATIVE_TO_PARENT类型值
	 * @param duration 动画时间
	 * @param r 动画变速器
	 * @param lis 动画监听器
	 * @return AnimationSet
	 */
	public static AnimationSet createScaleEnterAnamation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY, int duration, Interpolator r, AnimationListener lis) {
		AnimationSet set = new AnimationSet(true);
		if (r != null) {
			set.setInterpolator(r);
		}
		if (lis != null) {
			set.setAnimationListener(lis);
		}
		ScaleAnimation scale = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_PARENT, pivotX, Animation.RELATIVE_TO_PARENT, pivotY);
		scale.setDuration(duration);
		set.addAnimation(scale);
		return set;
	}
}
