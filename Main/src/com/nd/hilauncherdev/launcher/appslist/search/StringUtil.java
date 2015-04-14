package com.nd.hilauncherdev.launcher.appslist.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	/**
	 * 
	 * @description 去除号码中的空格 如： 138 1234 5678 → 13812345678
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * Returns true if the string is null or 0-length.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0) ? true : false;
	}

	/**
	 * 是否为特殊字符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isSpecialChar(char c) {
		return " `~!@#$%^&*()-+._\"\\=|{}':;',//[//]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？".indexOf(c) != -1;
	}

	/**
	 * 是否为中文字符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChineseChar(char c) {
		return (c >= 0x4e00 && c < 0x9fa5) ? true : false;
	}
}
