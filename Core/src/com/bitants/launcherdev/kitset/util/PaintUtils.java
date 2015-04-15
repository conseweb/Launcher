package com.bitants.launcherdev.kitset.util;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;

import com.nd.android.pandahome2.R;

/**
 * 画笔工厂类
 */
public final class PaintUtils {
	private static Paint meausepaint = new Paint();

	private static Paint destin, dstover, alphaPaint, iconTextPaint, iconPaint, clearPaint;
	private static PorterDuffColorFilter notMergeFolderHintColorFilter;

	/**
	 * 获取DST_IN类型画笔，非主线程获取时，注意可能因同步问题产生alpha值不一致！
	 * @param alpha 画笔透明度
	 * @return Paint
	 */
	public static final Paint getDestin(int alpha) {
		if (destin != null) {
			destin.setAlpha(alpha);
			return destin;
		}

		destin = new Paint();
		destin.setAlpha(alpha);
		destin.setFilterBitmap(true);
		destin.setAntiAlias(true);
		destin.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		return destin;
	}

	/**
	 * 获取DST_OVER类型画笔，非主线程获取时，注意可能因同步问题产生alpha值不一致！
	 * @param alpha 画笔透明度
	 * @return Paint
	 */
	public static final Paint getDstover(int alpha) {
		if (dstover != null) {
			dstover.setAlpha(alpha);
			return dstover;
		}

		dstover = new Paint();
		dstover.setAlpha(alpha);
		dstover.setFilterBitmap(true);
		dstover.setAntiAlias(true);
		dstover.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		return dstover;
	}

	/**
	 * 获取画笔，非主线程获取时，注意可能因同步问题产生alpha值不一致！
	 * @param alpha 画笔透明度
	 * @return Paint
	 */
	public static final Paint getStaticAlphaPaint(int alpha) {
		if (alphaPaint != null) {
			alphaPaint.setAlpha(alpha);
			return alphaPaint;
		}

		alphaPaint = new Paint();
		alphaPaint.setAlpha(alpha);
		alphaPaint.setFilterBitmap(true);
		alphaPaint.setAntiAlias(true);
		return alphaPaint;
	}

	/**
	 * 获取新画笔
	 * @return Paint
	 */
	public static final Paint getNewPaint() {
		Paint alphaPaint = new Paint();
		alphaPaint.setAntiAlias(true);
		return alphaPaint;
	}

	/**
	 * 获取画笔文字占用空间大小
	 * @param size 画笔的文字size
	 * @return int
	 */
	public static final int getLineHeight(float size) {
		meausepaint.setTextSize(size);
		return meausepaint.getFontMetricsInt(null);
	}

	/**
	 * 获取图标画笔
	 * @param res
	 * @return Paint
	 */
	public static final Paint getIconPaint(Resources res) {
		if (iconTextPaint != null) {
			return iconTextPaint;
		}

		iconTextPaint = new Paint();
		iconTextPaint.setAntiAlias(true);
		iconTextPaint.setDither(true);
		iconTextPaint.setShadowLayer(1, 1, 1, Color.BLACK);
		iconTextPaint.setColor(Color.WHITE);
		iconTextPaint.setTextSize(res.getDimensionPixelSize(R.dimen.text_size));
		return iconTextPaint;
	}
	
	/**
	 * 获取图标文字画笔
	 * @param res
	 * @return Paint
	 */
	public static final Paint getIconTextPaint(Resources res) {
		if (iconTextPaint != null) {
			return iconTextPaint;
		}

		iconTextPaint = new Paint();
		iconTextPaint.setAntiAlias(true);
		iconTextPaint.setDither(true);
		iconTextPaint.setShadowLayer(1, 1, 1, Color.BLACK);
		iconTextPaint.setColor(Color.WHITE);
		iconTextPaint.setTextSize(res.getDimensionPixelSize(R.dimen.text_size));
		return iconTextPaint;
	}
	
	/**
	 * 获取图标画笔
	 * @param res
	 * @return Paint
	 */
	public static final Paint getIconPaint() {
		if (iconPaint != null) {
			return iconPaint;
		}
		
		iconPaint = new Paint();
		iconPaint.setAntiAlias(true);
		iconPaint.setDither(true);
		return iconPaint;
	}

    /**
     * 获取DST_IN类型画笔，非主线程获取时，注意可能因同步问题产生alpha值不一致！
     * @return Paint
     */
    public static final Paint getClearPaint() {
        if (clearPaint != null) {
            return clearPaint;
        }

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return clearPaint;
    }
    
    
    /**
     * 用于无法合成文件夹色值过滤
     * @return
     */
    public static PorterDuffColorFilter getNotMergeFolderPaintFilter() {
        if (notMergeFolderHintColorFilter != null) {
            return notMergeFolderHintColorFilter;
        }

        notMergeFolderHintColorFilter = new PorterDuffColorFilter(Color.parseColor("#A5FF0000"), PorterDuff.Mode.SRC_ATOP);
        return notMergeFolderHintColorFilter;
    }
    
    /**
     * 用于无法合成文件夹透明度设置
     * @return
     */
    public static int getNotMergeFolderPaintAlpha(){
    	return 120;
    }
}
