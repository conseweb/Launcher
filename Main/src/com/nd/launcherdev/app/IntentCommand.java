package com.nd.launcherdev.app;

import android.content.Context;

public interface IntentCommand {
	/**
	 * 未知
	 */
	public static final int ACTION_FROM_UNKNOW = 0;
	
	/**
	 * 91快捷
	 */
	public static final int ACTION_FROM_SHORTCUT = 1;
	
	/**
	 * 工具页面
	 */
	public static final int ACTION_FROM_TOOL_PAGE = 2;
	
	/**
	 * 通知栏
	 */
	public static final int ACTION_FROM_NITIFICATION = 3;
	
	/**
	 * 要执行的命令动作
	 * 于2014/1/14新增点击来源参数
	 * @date 2012-6-2 上午10:57:12
	 * @param actionFrom 点击来源
	 */
	public void action(Context ctx, int actionFrom);
	
	public void action4LongClick(Context ctx, int actionFrom);

	public String getCommandAction();

}
