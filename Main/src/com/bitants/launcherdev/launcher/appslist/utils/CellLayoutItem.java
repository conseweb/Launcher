package com.bitants.launcherdev.launcher.appslist.utils;

import java.util.Locale;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.bitants.launcherdev.launcher.appslist.search.MatchType;
import com.bitants.launcherdev.launcher.appslist.search.StringUtil;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.appslist.search.MatchType;
import com.bitants.launcherdev.launcher.appslist.search.PyEntity;
import com.bitants.launcherdev.launcher.appslist.search.StringUtil;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.appslist.search.MatchType;
import com.bitants.launcherdev.launcher.appslist.search.StringUtil;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;

public class CellLayoutItem {
	public String shortPy; // 姓名简拼
	public String shortPyNumber; // 姓名简拼对应数字
	public PyEntity[] pyEntities; // 姓名对应py属性

	// 以下字段用于姓名字段匹配智能搜索
	public int matchIndex = -1; // 匹配的下标
	public int matchLength = 0; // 匹配的长度
	public MatchType matchType; // 匹配的类型
	public String matchKey; // 匹配的关键字
	public String sortKey;
	public Boolean isShortAllMatch;
	private View view;
	private ApplicationInfo mAppinfo;
	private String itemName;
	private Drawable mIconDrawable;

	public CellLayoutItem() {
		isShortAllMatch = false;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void setAppInfo(ApplicationInfo appinfo) {
		mAppinfo = appinfo;
		itemName = mAppinfo.title.toString();
		buildPyProperties();
	}

	public ApplicationInfo getAppInfo() {
		return mAppinfo;
	}

	public String getName() {
		return itemName;
	}

	
	public Drawable getIcon(){
		return mIconDrawable;
	}
	
	public void setIocn(Drawable icon){
		mIconDrawable = icon;
	}
	
	/**
	 * 生成拼音属性
	 */
	private void buildPyProperties() {
		if (StringUtil.isEmpty(itemName))
			return;
		sortKey = PinyinHelper.getPinYin(itemName);
		StringBuilder sb = new StringBuilder();
		String py = null;
		int length = itemName.length();
		pyEntities = new PyEntity[length];

		boolean containDuoyin = false;
		int duoyinCount = 0;
		String[][] result = new String[length][3];
		for (int i = 0; i < length; i++) {
			char c = mAppinfo.title.charAt(i);
			if (StringUtil.isSpecialChar(c)) {
				py = Character.toString(c).trim();
			} else {
				result[i] = PinyinHelper.getPinYinUpMinArray(c);
				if (result[i].length > 1) {
					containDuoyin = true;
					duoyinCount = result[i].length;
				}
				py = result[i][0].toLowerCase(Locale.CHINA).trim();
			}
			if (py.length() == 0) {
				py = Character.toString(' ');
			}
			sb.append(py.charAt(0));
			pyEntities[i] = new PyEntity(c, py);
		}
		// 有多音字
		if (containDuoyin) {
			sb.delete(0, sb.length());
			for (int i = 0; i < duoyinCount; i++) {
				for (int j = 0; j < length; j++) {
					char c = mAppinfo.title.charAt(j);
					if (StringUtil.isSpecialChar(c)) {
						py = Character.toString(c);
					} else {
						if (result[j].length == duoyinCount) {
							py = result[j][i].toLowerCase(Locale.CHINA).trim();
						} else {
							py = result[j][0].toLowerCase(Locale.CHINA).trim();
						}
					}
					if (py.length() == 0) {
						py = Character.toString(' ');
					}
					sb.append(py.charAt(0));
				}

				if (i != duoyinCount - 1) {
					sb.append("|");
				}
			}
			shortPy = sb.toString();
			shortPyNumber = PyEntity.converPyToNumber(shortPy);
		} else {
			shortPy = sb.toString();
			shortPyNumber = PyEntity.converPyToNumber(shortPy);
		}
	}
}
