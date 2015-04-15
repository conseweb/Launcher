package com.bitants.launcherdev.kitset.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 用来解析外部APK包
 */
public class ForeignPackage {

	/** TAG */
	public final static String TAG = "ForeignPackage";

	/** packageName */
	private String packageName;

	/** foreignCtx */
	private Context foreignCtx;

	private static final String R_XML = "R.xml.";

	public ForeignPackage(Context ctx, String packageName, boolean includeCode) throws NameNotFoundException {
		this.packageName = packageName;
		foreignCtx = ctx.createPackageContext(packageName, includeCode ? Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY : Context.CONTEXT_IGNORE_SECURITY);
	}

	/**
	 * 获取该apk的上下文
	 */
	public Context getContext() {
		return foreignCtx;
	}

	/**
	 * 获取该apk的asset
	 */
	public AssetManager getAssets() {
		return foreignCtx.getAssets();
	}

	/**
	 * 获取该apk的Class
	 */
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return foreignCtx.getClassLoader().loadClass(className);
	}

	/**
	 * 获取布局配置信息
	 */
	public XmlResourceParser getXML(String xml) {
		if (!xml.contains(R_XML)) {
			xml = R_XML + xml;
		}
		int resourceId = getResourceID(xml);
		if(-1 == resourceId){
			return null;
		}
		return getXML(resourceId);
	}

	/**
	 * 获取布局配置信息
	 */
	public XmlResourceParser getXML(int xmlRes) {
		return foreignCtx.getResources().getXml(xmlRes);
	}

	/**
	 * 获取资源ID
	 */
	public int getResourceID(String sID) {
		int mID = -1;
		if (sID.indexOf('.') == -1 || sID.indexOf('.') == sID.length() - 1) {
			return -1;
		}

		String className = packageName + "." + sID.substring(0, sID.lastIndexOf('.')).replace('.', '$');
		String idName = sID.substring(sID.lastIndexOf('.') + 1);
		try {
			Class<?> c = loadClass(className);
			Field field = c.getField(idName);
			mID = field.getInt(null);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "className Not Found:" + className);
			return -1;
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException:" + sID);
			return -1;
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "The Field Not Found:" + idName);
			return -1;
		} catch (Exception e) {
			Log.e(TAG, "Exception:" + e);
			return -1;
		}

		return mID;
	}
	
	public int getResourceID(String resName,String defType){
    	return foreignCtx.getResources().getIdentifier(resName, defType, foreignCtx.getPackageName());
    }
	
	/**
     * 获取apk包中的String资源
     * @param id
     * @return String
     */
    public String getString(int id){
    	return	foreignCtx.getResources().getString(id);
    }

	/**
	 * 获取该apk包中的string资源
	 */
	public String getString(String id) {
		int mID = getResourceID(id);
		if (mID == -1) {
			return null;
		}

		String s = null;
		try {
			s = foreignCtx.getResources().getString(mID);
		} catch (Exception e) {
			Log.e(TAG, "Exception:" + e);
		}

		return s;
	}

	/**
	 * 获取该apk包中的color资源
	 */
	public int getColor(String id) {
		int mID = getResourceID(id);
		if (mID == -1) {
			return -1;
		}

		int color = -1;
		try {
			color = foreignCtx.getResources().getColor(mID);
		} catch (Exception e) {
			Log.e(TAG, "Exception:" + e);
		}

		return color;
	}

	/**
	 * 获取该apk包中的drawable资源
	 */
	public Drawable getDrawable(String id) {
		int mID = getResourceID(id);
		return getDrawable(mID);
	}

	/**
	 * 获取包中指定ID的资源
	 */
	public Drawable getDrawable(int resourceId) {
		if (resourceId == -1) {
			return null;
		}

		Drawable d = null;
		try {
			d = foreignCtx.getResources().getDrawable(resourceId);
		} catch(OutOfMemoryError error){
			Log.e(TAG, "Error:" + error);
		} catch (Exception e) {
			Log.e(TAG, "Exception:" + e);
		}

		return d;
	}

	/**
	 * 获取该apk包中的layout资源
	 */
	public View getLayout(String id) {
		int mID = foreignCtx.getResources().getIdentifier(id, "layout", packageName);
		if (mID == -1) {
			return null;
		}

		LayoutInflater inflate = (LayoutInflater) foreignCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			return inflate.inflate(mID, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public View getLayout(int id) {
		LayoutInflater inflate = (LayoutInflater) foreignCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflate.inflate(id, null);
	}

	/**
	 * 获取该apk包中的animation资源
	 */
	public Animation getAnimation(String id) {
		int mID = getResourceID(id);
		if (mID == -1) {
			return null;
		}

		Animation a = null;
		try {
			a = AnimationUtils.loadAnimation(foreignCtx, mID);
		} catch (Exception e) {
			Log.e(TAG, "Exception:" + e);
		}

		return a;
	}

	public int[] getAttrsStyleables(String styleables1) {
		int styleablesId = foreignCtx.getResources().getIdentifier(styleables1, "string", packageName);
		return foreignCtx.getResources().getIntArray(styleablesId);
	}

	/**
	 * 获取该apk包中的类实例，className为类名，args为类的构造函数参数
	 */
	public Object getClassInstance(String className, Object... args) throws ClassNotFoundException {
		Class<?> c = loadClass(className);
		try {
			Class<?>[] cs = new Class<?>[args.length];
			int i = 0;
			for (Object arg : args) {
				cs[i++] = InstanceMapping.classTypeFormat(arg.getClass());
			}
			Constructor<?> ct = c.getConstructor(cs);
			return ct.newInstance(args);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
