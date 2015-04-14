package com.nd.hilauncherdev.kitset.util;

import java.text.Collator;

/**
 * 比较器 通用类 默认都是升序
 */
public class ComparatorUtil {
	private static final Collator sCollator = Collator.getInstance();

	public static int compare(String o1, String o2) {
		return sCollator.compare(o1, o2);
	}

	public static int compare(int o1, int o2) {
		return (o1 == o2 ? 0 : (o1 < o2 ? -1 : 1));
	}

	public static int compare(long o1, long o2) {
		return (o1 == o2 ? 0 : (o1 < o2 ? -1 : 1));
	}

	public static int compare(float o1, float o2) {
		return (o1 == o2 ? 0 : (o1 < o2 ? -1 : 1));
	}

	public static int compare(Boolean o1, Boolean o2) {
		return (o1.equals(o2) ? 0 : (o1.booleanValue() == true ? 1 : -1));
	}

	/**
	 * 根据应用程序名称排序
	 * @return Comparator<CommonApplicationInfo>
	 */
//	public static Comparator<CommonApplicationInfo> getAppTitleComparator() {
//		return new Comparator<CommonApplicationInfo>() {
//			@Override
//			public int compare(CommonApplicationInfo object1, CommonApplicationInfo object2) {
//				return ComparatorUtil.compare(object1.title.toString(), object2.title.toString());
//			}
//		};
//	}
}
