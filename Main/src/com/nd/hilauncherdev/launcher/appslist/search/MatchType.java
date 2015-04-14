package com.nd.hilauncherdev.launcher.appslist.search;

/**
 * 搜索结果匹配类型
 */
public enum MatchType {
	TYPE_NO_MATCH, // 未匹配
	TYPE_NUMBER_MATCH, // 号码匹配
	TYPE_NAME_PRECISION_MATCH, // 姓名精准匹配
	TYPE_NAME_FUZZY_MATCH, // 姓名模糊匹配
}
