package com.bitants.common.kitset.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;

public class MaskUtils {

	private static int oldW = -1;
	private static int oldH = -1;
	private static int bitmap_red;
	private static int bitmap_orange;
	private static int bitmap_yellow;
	private static int bitmap_greep;
	
	private static int bitmap_cyan;
	private static int bitmap_blue;
	private static int bitmap_purple;
	
	private static int bitmap_white;
	private static int bitmap_black;
	private static int bitmap_gray;
	private static int bitmap_default;
	private static boolean isInit = false;
	private static final float[] mHsv = new float[3];

	private static int EFFECT_ALPHA = 70;
	public static int getDefaultMaskID(Context Context) {
		if (!isInit) {
			LoadIconMaskBitmap(Context);
		}

		return bitmap_default;
	}
	
	private static int[] mBuff;

	/**
	 * 返回值为数组 int[2] = {-1,           -1,                    -1}
	 *                      -1:不画背板     -1:没有对应的背板值     -1:不满
	 *                       1:画背板       >0:对应的背板值         1:满
	 *  @param context
	 *  @param bitmap
	 *  @return
	 */

	public synchronized static int[] MatchMask(Context context, Bitmap bitmap) {
		int[] rtn = new int[]{-1, -1,-1, 0, 0};
		
		if (!isInit) {
			LoadIconMaskBitmap(context);
		}
		if (bitmap == null)
			return rtn;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (oldW != width || oldH != height) {
			mBuff = new int[width * height];
		}
		bitmap.getPixels(mBuff, 0, width, 0, 0, width, height);
		int bitmap_size = mBuff.length;
		int i = 0;
		int sum_blue = 0;
		int sum_green = 0;
		int sum_red = 0;
		int validCount = 0;
		while (i < bitmap_size) {
			int ARGB = mBuff[i];
			if (ARGB >>> 24 > 0) {
				int R = Color.red(ARGB);
				int G = Color.green(ARGB);
				int B = Color.blue(ARGB);
				if (!isGray(R, G, B)) {
					sum_red += R;
					sum_green += G;
					sum_blue += B;
					validCount++;
				}
			}
			i=i+2;
		}

		int red;
		int green;
		int blue;
		if (validCount == 0) {
			red = 0;
			green = 0;
			blue = 0;
		} else {
			green = sum_green / validCount;
			red = sum_red / validCount;
			blue = sum_blue / validCount;
		}
		int resid=hitMaskID(red, green, blue);
		if (BitmapRule.bitmapIsRegular(mBuff, width, height)) {
			rtn[0] = -1;
		}else{
			rtn[0] = 1;
		}
		
		rtn[1] = resid;
		if (rtn[0] == -1) {
			rtn[2] = isFull(mBuff, width, height);
			rtn[3] = TopBlankRowNum(mBuff, width, height);
			rtn[4] = BottomBlankRowNum(mBuff, width, height);
		} else {
			rtn[2] = -1;
			rtn[3] = 0;
			rtn[4] = 0;
		}
		return rtn;
	}

	private static int isFull(int bitmapBuf[], int width, int height) {
		int value = 0;
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < width; j++) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > 0) {
					return 1;
				}
			}
		}
		return -1;
	}
	/**图标上空了几行*/
	private static int TopBlankRowNum(int bitmapBuf[], int width, int height)
	{
		boolean isFinish=false;
		int topBlankCount=0;
		int value = 0;
		int i=0;
		int j=0;
		
		for (i = 0; i < height && !isFinish; i++) {
			for (j = 0; j < width; j++) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFECT_ALPHA) {
					isFinish = true;
					break;
				}
			}
			if (j == width) {
				topBlankCount++;
			}
		}
		return topBlankCount;
		
	}
	/**图标下空了几行*/
	private static int BottomBlankRowNum(int bitmapBuf[], int width, int height)
	{
		boolean isFinish=false;
		int bottomBlankCount=0;
		int value = 0;
		int i=0;
		int j=0;
		for (i = height - 1; i >= 0 && !isFinish; i--) {
			for (j = 0; j < width; j++) {
				value = bitmapBuf[i * width + j];
				value = value >>> 24;
				if (value > EFFECT_ALPHA) {
					isFinish = true;
					break;
				}
			}
			if (j == width) {
				bottomBlankCount++;
			}
		}
		return bottomBlankCount;
		
	}
	
	private static final int hitMaskID(int red, int green, int blue) {
        return bitmap_white;
		/*Color.RGBToHSV(red, green, blue, mHsv);

		// Log.e("MaskUtils","H(色相)="+mHsv[0]+"S(饱和度)="+mHsv[2]+"V(亮度)="+mHsv[1]);
		if (mHsv[1] < 0.1F) {
			if (mHsv[2] > 0.5F) {
				// Log.e("MaskUtils", "色相为灰亮度大为白");
				//白配红
				return bitmap_red;

			}
			// Log.e("MaskUtils", "色相灰 亮度小为黑");
			//黑配白
			return bitmap_white;
		}
		if (mHsv[2] < 0.1F) {
			// Log.e("MaskUtils", "亮度太低为黑色");
			//黑配白
			return bitmap_white;
		}
		float H = mHsv[0];
		// 红配黑
		if ((H < 20.0F) || (H >= 330.0F)) {
			// Log.e("MaskUtils", "红色");
			return bitmap_black;
		}
		// 橙配绿
		if (H < 42.0F) {
			// Log.e("MaskUtils", "橙色");
			return bitmap_greep;
		}
		// 黄配橙
		if (H < 65.0F) {
			// Log.e("MaskUtils", "黄色");
			return bitmap_yellow;
		}
		// 绿配灰
		if (H < 145.0F) {
			// Log.e("MaskUtils", "绿色");
			return bitmap_gray;
		}
		// 清配蓝
		if (H < 180.0F) {
			// Log.e("MaskUtils", "青色");
			return bitmap_blue;
		}
		// 蓝配清
		if (H < 245.0F) {
			// Log.e("MaskUtils", "蓝色 ");
			return bitmap_cyan;
		}
		// 紫配红
		// Log.e("MaskUtils", "紫色");
		return bitmap_yellow;*/
	}

	private static void LoadIconMaskBitmap(Context paramContext) {
		Resources localResources = paramContext.getResources();
		String str = paramContext.getPackageName();
		bitmap_red = localResources.getIdentifier("icon_mask_red", "drawable", str);
		bitmap_orange = localResources.getIdentifier("icon_mask_orange", "drawable", str);
		bitmap_yellow = localResources.getIdentifier("icon_mask_yellow", "drawable", str);
		bitmap_greep = localResources.getIdentifier("icon_mask_green", "drawable", str);
		bitmap_cyan = localResources.getIdentifier("icon_mask_cyan", "drawable", str);
		bitmap_blue = localResources.getIdentifier("icon_mask_blue", "drawable", str);
		bitmap_purple = localResources.getIdentifier("icon_mask_purple", "drawable", str);
		bitmap_black = localResources.getIdentifier("icon_mask_black", "drawable", str);
		bitmap_white = localResources.getIdentifier("icon_mask_white", "drawable", str);
		bitmap_default = localResources.getIdentifier("icon_mask_default", "drawable", str);
		bitmap_gray = localResources.getIdentifier("icon_mask_gray", "drawable", str);
		isInit = true;
	}
	private static final int section(int color) {
		return 51 * Math.round((0.01F + color) / 51.0F);
	}

	private static final boolean isGray(int R, int G, int B) {
		int r = section(R);
		int g = section(G);
		int b = section(B);
		return (r == g) && (r == b);
	}
}