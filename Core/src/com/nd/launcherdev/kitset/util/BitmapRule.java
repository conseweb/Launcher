package com.nd.launcherdev.kitset.util;

import android.graphics.Bitmap;

public class BitmapRule {

	/**
	 * 当透明度大小这个值为才认变是有效的颜色
	 * */
	private static int EFFEC_ALPHA = 100;

	/**
	 * 每个角自身的 宽和高的比较阀值 长宽差值 delta< SELF_DELTA_THRESHOLD
	 * */
	private static float SELF_DELTA_THRESHOLD = 5;
	private static final float BASE_SELF_DELTA_THRESHOLD = 5;

	/**
	 * 每个角自身的 宽和高的比较阀值 长宽比例
	 * 
	 * */
	private static float SELF_RATE_THRESHOLD = 1.3f;

	/**
	 * 四个角位置的比较时用的阀值
	 * */
	private static float COMPARE_POSTION_THRESHOLD = 4;
	private static final float BASE_COMPARE_POSTION_THRESHOLD = 4;

	/**
	 * 四个角大小的比较时用的阀值
	 * */
	private static float COMPARE_SIZE_THRESHOLD = 4;
	private static final float BASE_COMPARE_SIZE_THRESHOLD = 4;

	/**
	 * 矩形是正方形还是长方形的阀值,长宽比
	 * 
	 * */
	private static float SHAPE_THRESHOLD = 1.2f;

	/**
	 * 上次的图像尺寸，
	 * 因为图标都是正方形，所以这里以仅以宽为装
	 * */
	private static float mLastSize=0;
	
	/**
	 * 基础尺寸480*800下为 72像素
	 * */
	private static final int BASIC_SIZE=72;
	
	public static boolean test(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int mBuff[];

		mBuff = new int[width * height];
		bitmap.getPixels(mBuff, 0, width, 0, 0, width, height);
		return bitmapIsRegular(mBuff, width, height);

	}
	/**
	 * 根据不同的图标大小规格，修正各个阀值
	 * 如果大小没有出现变化则不用再修正了
	 * */
	private static void correctValue(int size) {
		if (size == mLastSize) {
			return;
		}
		float scale = size / (float) BASIC_SIZE;

		SELF_DELTA_THRESHOLD = BASE_SELF_DELTA_THRESHOLD * scale;
		COMPARE_POSTION_THRESHOLD = BASE_COMPARE_POSTION_THRESHOLD * scale;
		COMPARE_SIZE_THRESHOLD = BASE_COMPARE_SIZE_THRESHOLD * scale;

	}
	public static boolean bitmapIsRegular(int bitmapBuf[], int width, int height) {
		
		int value = 0;
		boolean isFinish = false;
		
		// 圆角的最大宽度
		int maxCornerW = width / 2;

		// 圆角的最大高度
		int maxCornerH = height / 2;
		
		correctValue(width);
		
		// 先试图找到左上角的水平点
		FilletInfo first = new FilletInfo();
		
		for (int i = 0; i < height && !isFinish; i++) {
			for (int j = 0; j < width; j++) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j < maxCornerW && i < maxCornerH) {
						first.mHx = j;
						first.mHY = i;
					} else {
						// Log.e("zhou", "左上角水平点超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}

		isFinish = false;
		for (int j = 0; j < width && !isFinish; j++) {
			for (int i = 0; i < height; i++) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j < maxCornerW && i < maxCornerH) {
						first.mVx = j;
						first.mVY = i;
					} else {
						// Log.e("zhou", "左上角垂直点超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}

		first.countSize();
		if (!isLegal(first)) {
			// Log.e("zhou", "左上角不规则");
			return false;
		}

		// 先试图找右上角
		isFinish = false;
		FilletInfo second = new FilletInfo();
		for (int i = 0; i < height && !isFinish; i++) {
			for (int j = width - 1; j >= 0; j--) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j >= maxCornerW && i < maxCornerH) {
						second.mHx = j;
						second.mHY = i;
					} else {
						// Log.e("zhou", "右上角水平点超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}

		isFinish = false;
		for (int j = width - 1; j >= 0 && !isFinish; j--) {
			for (int i = 0; i < height; i++) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j >= maxCornerW && i < maxCornerH) {
						second.mVx = j;
						second.mVY = i;
					} else {
						// Log.e("zhou", "右上角垂直超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}
		second.countSize();
		if (!isLegal(second)) {
			// Log.e("zhou", "右上角不规则");
			return false;
		}

		// 先试图找到左下角
		FilletInfo third = new FilletInfo();
		value = 0;
		isFinish = false;
		for (int i = height - 1; i >= 0 && !isFinish; i--) {
			for (int j = 0; j < width; j++) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j < width / 2 && i >= height / 2) {
						third.mHx = j;
						third.mHY = i;
					} else {
						// Log.e("zhou", "左下角水平点超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}

		isFinish = false;
		for (int j = 0; j < width && !isFinish; j++) {
			for (int i = height - 1; i >= 0; i--) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j < width / 2 && i >= height / 2) {
						third.mVx = j;
						third.mVY = i;
					} else {
						// Log.e("zhou", "左下角垂直点超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}

		third.countSize();
		if (!isLegal(third)) {
			// Log.e("zhou", "左下角不规则");
			return false;
		}

		// 先试图找到右下角
		FilletInfo four = new FilletInfo();
		value = 0;
		isFinish = false;
		for (int i = height - 1; i >= 0 && !isFinish; i--) {
			for (int j = width - 1; j >= 0; j--) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j >= width / 2 && i >= height / 2) {
						four.mHx = j;
						four.mHY = i;
					} else {
						// Log.e("zhou", "右下角水平点超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}

		isFinish = false;
		for (int j = width - 1; j >= 0; j--) {
			for (int i = height - 1; i >= 0 && !isFinish; i--) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFEC_ALPHA) {
					// 判断这个圆角是否合法
					if (j >= width / 2 && i >= height / 2) {
						four.mVx = j;
						four.mVY = i;
					} else {
						// Log.e("zhou", "右下角垂直点超过中线");
						return false;
					}
					isFinish = true;
					break;
				}
			}
		}

		four.countSize();
		if (!isLegal(four)) {
			// Log.e("zhou", "右下角不规则");
			return false;
		}

		// Log.e("zhou", "判断四个角的相互关系");
		if (isSamePostion(first, second, third, four, width, height)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否为非法的圆角
	 * */

	private static boolean isLegal(FilletInfo info) {

		int delta = 0;
		delta = info.widht > info.height ? info.widht - info.height : info.height - info.widht;
		if (delta < SELF_DELTA_THRESHOLD) {
			return true;
		}

		float rate = 0;
		if (info.widht > info.height) {
			rate = (float) info.widht / info.height;
		} else {
			rate = (float) info.height / info.widht;
		}
		if (rate > SELF_RATE_THRESHOLD) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 分析四个角是否对称以及相应位置是否正确
	 * */
	private static boolean isSamePostion(FilletInfo first, FilletInfo second, FilletInfo third, FilletInfo four, int width, int height) {

		int deltaX = 0;
		int deltaY = 0;

		// 四个角是否全是直角,全是直角等于0
		int value = first.widht + first.height + second.widht + second.height + third.widht + third.height + four.widht + four.height;
		// Log.e("zhou", "value=" + value);
		if (value == 0) {

			int dx = second.mHx - first.mHx;
			int dy = third.mHY - first.mHx;

			int dxy = dx - dy;

			if (dxy < 2 && dxy > -2) {
				// 判断结果近似正方体
				// Log.e("zhou", "正方体");
				return true;
			}
		}

		// 右上角和左上角比

		deltaX = second.widht - first.widht;
		deltaY = second.height - first.height;
		deltaX = deltaX > 0 ? deltaX : -deltaX;
		deltaY = deltaY > 0 ? deltaY : -deltaY;
		if (deltaX > COMPARE_SIZE_THRESHOLD || deltaY > COMPARE_SIZE_THRESHOLD) {
			// Log.e("zhou", "右上角和左上角大小不一样");
			return false;
		}

		// 是否同一水平位置
		deltaY = second.mHY - first.mHY;
		deltaY = deltaY > 0 ? deltaY : -deltaY;
		if (deltaY > COMPARE_POSTION_THRESHOLD) {
			// Log.e("zhou", "右上角和左上角不在同一水平位置");
			return false;
		}

		// 左下角和左上角比
		deltaX = third.widht - first.widht;
		deltaY = third.height - first.height;
		deltaX = deltaX > 0 ? deltaX : -deltaX;
		deltaY = deltaY > 0 ? deltaY : -deltaY;
		if (deltaX > COMPARE_SIZE_THRESHOLD || deltaY > COMPARE_SIZE_THRESHOLD) {
			// Log.e("zhou", "左下角和左上角大小不一致");
			return false;
		}
		// 是否同一垂直位置
		deltaX = third.mHx - first.mHx;
		deltaX = deltaX > 0 ? deltaX : -deltaX;
		if (deltaX > COMPARE_POSTION_THRESHOLD) {
			// Log.e("zhou", "左下角和左上角不在同一垂直位置");
			return false;
		}

		// 右下角和左上角比
		deltaX = four.widht - first.widht;
		deltaY = four.height - first.height;
		deltaX = deltaX > 0 ? deltaX : -deltaX;
		deltaY = deltaY > 0 ? deltaY : -deltaY;
		if (deltaX > COMPARE_SIZE_THRESHOLD || deltaY > COMPARE_SIZE_THRESHOLD) {
			// Log.e("zhou", "右下角和左上角大小不一致");
			return false;
		}

		// 是否同一垂直位置
		deltaX = four.mHx - second.mHx;
		deltaX = deltaX > 0 ? deltaX : -deltaX;
		deltaY = four.mHY - third.mHY;
		deltaY = deltaY > 0 ? deltaY : -deltaY;

		if (deltaX > COMPARE_POSTION_THRESHOLD) {
			// Log.e("zhou", "右下角和右上角不在同一垂直位置");
			return false;
		}
		if (deltaY > COMPARE_POSTION_THRESHOLD) {
			// Log.e("zhou", "右下角和左下角不在同一水平位置");
			return false;
		}

		deltaX = second.mHx - first.mHx;
		deltaX = deltaX > 0 ? deltaX : -deltaX;
		deltaY = third.mVY - first.mVY;
		deltaY = deltaY > 0 ? deltaY : -deltaY;
		if ((float) deltaX / deltaY > SHAPE_THRESHOLD || (float) deltaY / deltaX > SHAPE_THRESHOLD) {
			// Log.e("zhou", "为圆角长方形，认为不规则 x=" + ((float) deltaX / deltaY) +
			// "y=" + ((float) deltaY / deltaX));
			return false;
		}

		return true;
	}

	static class FilletInfo {

		public FilletInfo() {
		}

		/**
		 * 圆角的水平点X轴坐标
		 * */
		public int mHx;
		/**
		 * 圆角的水平点Y轴坐标
		 * */
		public int mHY;

		/**
		 * 圆角的垂直点X轴坐标
		 * */
		public int mVx;
		/**
		 * 圆角的Y轴垂直点坐标
		 * */
		public int mVY;

		public int widht;
		public int height;

		void countSize() {
			widht = mHx - mVx;
			height = mHY - mVY;
			widht = widht > 0 ? widht : -widht;
			height = height > 0 ? height : -height;
		}

	}
}
