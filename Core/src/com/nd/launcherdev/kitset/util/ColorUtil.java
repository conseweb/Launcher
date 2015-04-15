package com.nd.launcherdev.kitset.util;

import android.graphics.Color;
import android.text.TextUtils;
/**
 * 颜色相关 工具类
 */
public final class ColorUtil {
	/**
	 * 颜色设置
	 * @param color
	 * @param alpha
	 * @return int
	 */
	public static int argbColorAlpha(int color, int alpha) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return Color.argb(alpha, r, g, b);
	}
	
	/**
	 * 取相反颜色
	 * @param alpha
	 * @param color
	 * @return int
	 */
	public static int antiColorAlpha(int alpha, int color) {
		if(-1 == alpha){
			alpha = Color.alpha(color);
			if(255 == alpha){
				alpha = 200;
			}
		}
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return Color.argb(alpha, 255 - r, 255 - g, 255 - b);
	}
	
	/**
	 * 解析颜色值
	 * @param colorStr
	 * @return int
	 */
	public static int parseColor(String colorStr) {
		int color = 0xff000000;
		try {
			color = Color.parseColor(colorStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return color;
	}
	
	/**
	 * 解析颜色值
	 * @param colorStr
	 * @param defaultColor
	 * @return int
	 */
	public static int parseColor(String colorStr, int defaultColor) {
		if(TextUtils.isEmpty(colorStr)) {
			return defaultColor;
		}
		int color = defaultColor;
		try {
			color = Color.parseColor(colorStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return color;
	}
	
	/**
	 * 解析颜色值
	 * @param colorStr
	 * @return int
	 */
	public static float[] parseColor2Array(String colorStr) {
		float[] colorArray = new float[3];
		int color = parseColor(colorStr);
		colorArray[0] = (1.0f * Color.red(color))/255.0f;
		colorArray[1] = (1.0f * Color.green(color))/255.0f;
		colorArray[2] = (1.0f * Color.blue(color))/255.0f;
		return colorArray;
	}
	
}
