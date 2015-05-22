package com.bitants.launcherdev.app;

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
	 * @param actionFrom 点击来源
	 */
	public void action(Context ctx, int actionFrom);
	
	public void action4LongClick(Context ctx, int actionFrom);

	public String getCommandAction();

}
