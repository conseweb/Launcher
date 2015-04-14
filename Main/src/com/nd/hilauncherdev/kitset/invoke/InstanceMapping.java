package com.nd.hilauncherdev.kitset.invoke;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 实例映射
 */
public abstract class InstanceMapping {

	/** instance */
	protected Object instance;

	/** methodList */
	protected ArrayList<MethodStructure> methodList = new ArrayList<MethodStructure>();

	/**
	 * 构造函数
	 */
	public InstanceMapping(Object instance) throws NotTargetInstanceException {
		this.instance = instance;
		setMethods();
		try {
			for (MethodStructure ms : methodList) {
				Method method = instance.getClass().getMethod(ms.name, ms.parameterTypes);
				if (ms.returnType != method.getReturnType()) {
					throw new NotTargetInstanceException("Return type does not match.");
				}
			}
		} catch (Exception e) {
			throw new NotTargetInstanceException(e.toString());
		}
	}

	public InstanceMapping(Object object, Class<?> classType) throws NotTargetInstanceException {
		this(object);
		if (!classTypeFormat(classType).isInstance(object)) {
			throw new NotTargetInstanceException("The object is not the classType:" + classType.getName());
		}
	}

	/**
	 * 设置方法
	 */
	protected abstract void setMethods();

	/**
	 * 方法结构
	 * 
	 */
	public final class MethodStructure {
		/** name */
		public String name;

		/** returnType */
		public Class<?> returnType;

		/** parameterTypes */
		public Class<?>[] parameterTypes;

		public MethodStructure(String name, Class<?> returnType, Class<?>... parameterTypes) {
			super();
			this.name = name;
			this.parameterTypes = parameterTypes;
			for (int i = 0; i < this.parameterTypes.length; i++) {
				this.parameterTypes[i] = classTypeFormat(this.parameterTypes[i]);
			}
			this.returnType = classTypeFormat(returnType);
		}
	}

	/**
	 * 类类型格式
	 */
	public static Class<?> classTypeFormat(Class<?> type) {
		if (type == Void.class || type == void.class) {
			return Void.TYPE;
		}
		if (type == Integer.class || type == int.class) {
			return Integer.TYPE;
		}
		if (type == Float.class || type == float.class) {
			return Float.TYPE;
		}
		if (type == Double.class || type == double.class) {
			return Double.TYPE;
		}
		if (type == Boolean.class || type == boolean.class) {
			return Boolean.TYPE;
		}
		if (type == Character.class || type == char.class) {
			return Character.TYPE;
		}
		if (type == Long.class || type == long.class) {
			return Long.TYPE;
		}
		if (type == Short.class || type == short.class) {
			return Short.TYPE;
		}
		if (type == Byte.class || type == byte.class) {
			return Byte.TYPE;
		}
		return type;
	}

}
