/**
 * @author Michael
 * Date:2013-9-26下午4:26:22
 *
 */
package com.nd.launcherdev.kitset.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;

/**
 * 反射类
 */
public class ReflectUtil {
	
	private static final String TAG = "ReflectUtil";

	/**
	 * 根据实例获取属性值
	 * @param instance
	 * @param fieldName
	 * @return Object
	 */
	public static Object getFieldValueByFieldName(Object instance,
			String fieldName) {
		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (fieldName.equals(field.getName())) {
				try {
					return field.get(instance);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}
		return null;
	}

    /**
     * 根据实例設置属性值
     * @param instance
     * @param fieldName
     * @return Object
     */
    public static void setFieldValueByFieldName(Object instance,
                                                  String fieldName, Object value) {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                try {
                    boolean accessFlag = field.isAccessible();// 获得原始权限
                    field.setAccessible(true);// 权限设置为可访问
                    field.set(instance, value);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

	/**
	 * 调用对象的某个方法
	 * @param method
	 * @param obj
	 * @param args
	 * @return Object
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentExceptio
	 */
	public static Object invokeMethod(Method method, Object obj, Object... args)
			 throws IllegalArgumentException, IllegalAccessException,
			 InvocationTargetException {
		return method.invoke(obj, args);
	}
	
	
	public static String toMB(long size){
		return String.format("%.2f", size/(1024*1024f));
	}
	
	public static void dumpMemoryUsed(){
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long max = Runtime.getRuntime().maxMemory();
		Log.e(TAG, "total:"+toMB(total) + "|" +
				"free:"+toMB(free) + "|" + "max:"+
				toMB(max)+"|"+"used:"+toMB(total-free));
	}

}
