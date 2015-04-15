package com.bitants.launcherdev.kitset.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;

/**
 * 状态栏控制
 */
public class StatusBarUtil {
	public static final String STATUS_BAR_SERVICE = "statusbar";
	
	/**
	 * 状态栏开关
	 * @param ctx
	 * @param enable false隐藏/true显示
	 */
	public static void toggleStateBar(Activity ctx, boolean enable) {
		try{
			if (!enable) {
				hideStatusBar(ctx);
			} else {
				if (BaseSettingsPreference.getInstance().isNotificationBarVisible()) {
					showStatusBar(ctx);
				}
			}
		}catch (NoSuchMethodError e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 显示状态栏
	 * @param ctx
	 */
	public static void showStatusBar(Activity ctx){
		try{
			ctx.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 隐藏状态栏
	 * @param ctx
	 */
	public static void hideStatusBar(Activity ctx){
		try{
			ctx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	/**
	 * 展开状态栏
	 * @param ctx
	 */
	public static void expandStatusBar(Context ctx) {
		try {
			//wangcao修改2014.7.25
			if (!BaseSettingsPreference.getInstance().isNotificationBarVisible() && android.os.Build.VERSION.SDK_INT >= 16) {
				Toast.makeText(ctx, R.string.main_dock_notification_disable_hint, Toast.LENGTH_SHORT).show();
				return;
			}
			
			Object service = ctx.getSystemService(STATUS_BAR_SERVICE);
			Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
			Method expand = null;
			if (android.os.Build.VERSION.SDK_INT < 17) {
				expand = statusbarManager.getMethod("expand");
			} else {
				if("Lenovo A788t".equals(TelephoneUtil.getMachineName())){					
					expand = statusbarManager.getMethod("expandSettingsPanel");
				} else {					
					expand = statusbarManager.getMethod("expandNotificationsPanel");
				}

			}
			expand.invoke(service);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 收起通知栏,用于一些机型如htc one,点击通知栏消息后
	 * @param ctx
	 */
	public static void collapseStatusBar(Context ctx) {
		try {
			Object service = ctx.getSystemService(STATUS_BAR_SERVICE);
			Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
			Method collapse = null;
			if (android.os.Build.VERSION.SDK_INT <= 16) {
				collapse = statusbarManager.getMethod("collapse");
			} else {
				collapse = statusbarManager.getMethod("collapsePanels");
			}
			collapse.invoke(service);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 半透明状态栏, 4.4以上才支持
	 * @param ctx
	 * @return 是否成功
	 */
	public static boolean translucentStatusBar(Activity ctx){
		try {
			if (VERSION.SDK_INT >= 19) {
				WindowManager.LayoutParams lp = ctx.getWindow().getAttributes();
				Field FLAG_TRANSLUCENT_STATUS = lp.getClass().getField("FLAG_TRANSLUCENT_STATUS");
				ctx.getWindow().addFlags(FLAG_TRANSLUCENT_STATUS.getInt(lp));
				return true;
			} else if(VERSION.SDK_INT >= 14){//适配三星、HTC部分机型
				View view = ctx.getWindow().getDecorView();
				Field field = null;
				Field[] fieldS = View.class.getDeclaredFields();
				for (Field fieldTemp : fieldS) {
					if (fieldTemp.getName().equals("SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND")) {
						field = fieldTemp;
						break;
					}
				}

				if (field != null) {
					Class<?> c = field.getType();
					if (c == int.class) {
						int value = field.getInt(null);
						Method method = view.getClass().getMethod("setSystemUiVisibility", new Class[] { int.class });
						Object[] args1 = { Integer.valueOf(value) };
						method.invoke(view, args1);
						return true;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 半透明导航栏, 4.4以上才支持
	 * @param ctx
	 */
	public static boolean translucentAtionBar(Activity ctx){
		try {
			if (VERSION.SDK_INT >= 19) {
				WindowManager.LayoutParams lp = ctx.getWindow().getAttributes();
				Field FLAG_TRANSLUCENT_NAVIGATION = lp.getClass().getField("FLAG_TRANSLUCENT_NAVIGATION");
				ctx.getWindow().addFlags(FLAG_TRANSLUCENT_NAVIGATION.getInt(lp));
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取导航栏ActionBar的高度
	 * @param activity
	 * @return
	 */
	public static int getActionBarHeight(Activity activity) {
		try {
			TypedValue tv = new TypedValue();
			int actionBarHeight = 0;
			
			Class<?> localClass = Class.forName("android.R$attr");
			Object localObject = localClass.newInstance();
			int i = ((Integer) localClass.getField("actionBarSize").get(localObject)).intValue();
			if (activity.getTheme().resolveAttribute(i, tv, true)){
				actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
			}
			return actionBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int getTranslucentActionBarHeight(Activity act) {
		int height = getActionBarHeight(act);
		return  height != 0 ?  height : ScreenUtil.dip2px(act, 48);
	}
	
	/**
	 * <br>Description: 获取通知栏高度,全屏时返回0
	 * <br>Author:caizp
	 * <br>Date:2014年8月25日上午10:44:55
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity) {
		try {
			WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
	        if((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
				return 0;
	        }
			Class<?> localClass = Class.forName("com.android.internal.R$dimen");
			Object localObject = localClass.newInstance();
			int i = ((Integer) localClass.getField("status_bar_height").get(localObject)).intValue();
			return activity.getResources().getDimensionPixelSize(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
