package com.bitants.launcherdev.kitset;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * GPU控制工具类
 */
public class GpuControler {

	private static final int LAYER_TYPE_NONE = 0;//普通渲染
	private static final int LAYER_TYPE_SOFTWARE = 1;//软件渲染
	private static final int LAYER_TYPE_HARDWARE = 2;//硬件渲染
	/**
	 * 是否开启GPU, 0为未初始化，1为开启，-1为关闭
	 */
	private static int isOpenGPU = 0;

	/**
	 * 硬件加速开启的情况下, View通过硬件渲染为硬件纹理；硬件加速没开启时,同enableSoftwareLayers方法, View通过软件渲染为bitmap
	 * @param view
	 */
	public static void enableHardwareLayers(View view) {
		if(hasDestroyHardwareLayers(view)){
			setLayerType(view, LAYER_TYPE_HARDWARE);
		}
	}

	/**
	 * 关闭硬件渲染模式, View将被按普通的方式进行渲染
	 * @param view
	 */
	public static void destroyHardwareLayer(View view) {
		if(!hasDestroyHardwareLayers(view)){
			setLayerType(view, LAYER_TYPE_NONE);
		}
	}

	/**
	 * View通过软件渲染为bitmap, 主要是为了硬件加速渲染出现兼容性问题时,使用该方法限制硬件绘制来解决
	 * @param view
	 */
	public static void enableSoftwareLayers(View view) {
		setLayerType(view, LAYER_TYPE_SOFTWARE);
	}
	
	/**
	 * View是否关闭Gpu离屏硬件缓存
	 * @param view
	 * @return true关闭
	 */
	public static boolean hasDestroyHardwareLayers(View view){
		try {
			if (isLower14()) {
				return true;
			}
			
			if(view == null)
				return false;
			
			Method method = view.getClass().getMethod("getLayerType", (Class[])null);
			int result = (Integer) method.invoke(view, (Object[])null);
			return result != LAYER_TYPE_HARDWARE;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 设置View的Gpu离屏缓存
	 * type 0: LAYER_TYPE_NONE 1: LAYER_TYPE_SOFTWARE 2:LAYER_TYPE_HARDWARE
	 * @param view
	 * @param type
	 */
	private static void setLayerType(View view, int type) {
		try {
			if (isLower14()) {
				return;
			}
			
			if(view == null)
				return;
			
			Method method = view.getClass().getMethod("setLayerType", new Class[] { int.class, Paint.class });
			Object[] args1 = { Integer.valueOf(type), null };
			method.invoke(view, args1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 系统window层级是否开启的硬件加速
	 * @param view
	 * @return true开启
	 */
	public static boolean isOpenGpu(View view) {
		try {
			if (isOpenGPU != 0) {// 如果已初始化
				return isOpenGPU == 1;
			}

			if (isLower14()) {// android4.0以下不支持GPU
				isOpenGPU = -1;
			} else {
				if (view == null) {
					isOpenGPU = 0;
				} else {
					Method method = view.getClass().getMethod("isHardwareAccelerated", (Class[])null);
					Boolean result = (Boolean) method.invoke(view, (Object[])null);
					isOpenGPU = result ? 1 : -1;
				}
			}
		} catch (Exception ex) {
			isOpenGPU = -1;
			ex.printStackTrace();
		}
		return isOpenGPU == 1;
	}

	/**
	 * 用于android4.0以上未开启GPU，重置状态后重新检测
	 * @param view
	 * @return true打开
	 */
	public static boolean isOpenGpuMore(View view){
		if(Build.VERSION.SDK_INT >= 14 && isOpenGPU != 1){//如android4.0以上未开启GPU，重置状态后重新检测
			Log.w("isOpenGpuMore", "Gpu reset");
			isOpenGPU = 0;
		}
		return isOpenGpu(view);
	}
	
	/**
	 * 系统window层级上开启硬件加速
	 * @param act
	 */
	public static void openGpu(Activity act) {
		if (isLower14()) {
			return;
		}

		if (act == null || act.getWindow() == null || act.getWindow().getAttributes() == null) {
			return;
		}

		try {
			WindowManager.LayoutParams lp = act.getWindow().getAttributes();
			Field field = lp.getClass().getField("FLAG_HARDWARE_ACCELERATED");
			int flag_hardware_accelerated = field.getInt(lp);
			act.getWindow().addFlags(flag_hardware_accelerated);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}

	
	private static boolean isLower14() {
		return Build.VERSION.SDK_INT < 14;
	}
	
}
