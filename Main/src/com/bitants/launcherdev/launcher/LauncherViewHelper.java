package com.bitants.launcherdev.launcher;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.support.BaseLauncherViewHelper;
import com.bitants.launcherdev.framework.view.bubble.LauncherBubbleView;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.support.BaseLauncherViewHelper;
import com.bitants.launcher.R;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.support.BaseLauncherViewHelper;

/**
 * 扩展处理显示到桌面的视图
 * <br>Author:ryan
 * <br>Date:2012-7-11下午09:02:43
 */
public class LauncherViewHelper extends BaseLauncherViewHelper {
	/**
	 * 创建冒泡View
	 * @param hint
	 * @param screen
	 * @param hostView
	 * @return
	 */
	public static LauncherBubbleView createBubbleViewInWorkspace(String hint, int screen, View hostView) {
		BaseLauncher mLauncher = BaseConfig.getBaseLauncher();
		if(mLauncher == null)
			return null;
		
		boolean isOnBottom = false;//是否显示于目标View的底部
		CellLayout.LayoutParams hostLP = (CellLayout.LayoutParams)hostView.getLayoutParams();
		if(hostLP.cellY == 0){
			isOnBottom = true;
		}
		
		CellLayout cl = mLauncher.mWorkspace.getCellLayoutAt(screen);
		int screenWidth = ScreenUtil.getScreenWH()[0];
		int cellWidth = cl.getCellWidth();
		int cellHeight = cl.getCellHeight();
		if(cellWidth == 0 || cellHeight == 0){
			cellWidth = BaseConfigPreferences.getInstance().getCellLayoutCellWidth();
			cellHeight = BaseConfigPreferences.getInstance().getCellLayoutCellHeight();
		}
		if(screenWidth == 0 || cellWidth == 0 || cellHeight == 0){
			Log.e("createBubbleView", "createBubbleView fail");
			return null;
		}
		int[] xy = new int[2];
		if(hostLP.x != 0 || hostLP.y != 0){
			xy[0] = hostLP.x;
			xy[1] = hostLP.y;
		}else{
			hostView.getLocationOnScreen(xy);
	        if(xy[0] > screenWidth){
	        	xy[0] = xy[0] % screenWidth;
	        }
	        if(xy[0] < 0){
	        	xy[0] = screenWidth - (Math.abs(xy[0]) % screenWidth);
	        }
		}
        
        //内容View
		final int fontSize = 14;
        Paint p = new Paint();
        final float densityMultiplier = ScreenUtil.getDensity();
        p.setTextSize(fontSize * densityMultiplier);
        Rect bounds = new Rect();
        p.getTextBounds(hint, 0, hint.length(), bounds);
        
		LauncherBubbleView favorite = new LauncherBubbleView(mLauncher);
		favorite.setOnClickListener(mLauncher);
		CellLayout.LayoutParams lp = new CellLayout.LayoutParams(new ViewGroup.LayoutParams(bounds.width()+ScreenUtil.dip2px(mLauncher, 30), 
				bounds.height() +ScreenUtil.dip2px(mLauncher, 30)));
		int line = lp.width / (cellWidth * 2);
		lp.width = Math.min(lp.width, cellWidth * 2);//冒泡view最宽不超过2个View
		lp.height += bounds.height() * line;
        lp.x = Math.max(0, xy[0] + cellWidth/2 - bounds.width()/2);
        if(lp.x + lp.width > screenWidth){
        	lp.x = screenWidth - lp.width;
        }
        if(isOnBottom){
        	lp.y = xy[1] - CellLayoutConfig.getMarginTop() + cellHeight;
        }else{        	
        	lp.y = xy[1] - CellLayoutConfig.getMarginTop() - lp.height;
        }
        lp.isOnXYAndWHMode = true;
		favorite.setGravity(Gravity.CENTER);
		favorite.setBackgroundResource(R.drawable.cleaner_widget_bg);
		favorite.setText(hint);
		favorite.setTextSize(fontSize);
		favorite.setLayoutParams(lp);
        //角标View
        ImageView iv = new ImageView(mLauncher);
        CellLayout.LayoutParams lp2 = new CellLayout.LayoutParams(new ViewGroup.LayoutParams(ScreenUtil.dip2px(mLauncher, 20), 
				ScreenUtil.dip2px(mLauncher, 22)));
        if(isOnBottom){
        	iv.setImageResource(R.drawable.launcher_notify_angle_down);
            lp2.x = xy[0] + cellWidth/2;
            lp2.y = lp.y - lp2.height;
            lp2.isOnXYAndWHMode = true;
        }else{
        	iv.setImageResource(R.drawable.launcher_notify_angle_up);
            lp2.x = xy[0] + cellWidth/2;
            lp2.y = lp.y + lp.height - ScreenUtil.dip2px(mLauncher, 8.5f);
            lp2.isOnXYAndWHMode = true;
        }
        iv.setLayoutParams(lp2);
        
        favorite.setAngleView(iv);
        return favorite;
	}
	
	
}
