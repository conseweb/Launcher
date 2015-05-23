package com.bitants.launcherdev.app;

import android.content.Context;
import android.util.Log;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;
import com.bitants.common.launcher.info.ApplicationInfo;

import java.util.ArrayList;

/**
 * 自定义快捷方式开关控制器 负责自定义快捷方式的跳转
 * 
 */
public class CustomIntentSwitcherController {

	private ArrayList<IntentCommand> commands;
	private static CustomIntentSwitcherController mController;
	private static Object mSynObj = new Object();

	private CustomIntentSwitcherController() {
		commands = new ArrayList<IntentCommand>();
	}

	public static CustomIntentSwitcherController getNewInstance() {
		synchronized (mSynObj) {
			if (mController == null)
				mController = new CustomIntentSwitcherController();
			return mController;
		}
	}

	/**
	 * 注册自定义快捷方式 <br>
	 * 桌面生成的一切自定义快捷方式需调用此方法向控制器注册 <br>
	 * 如果不注册可能不会响应快捷方式的点击事件
	 * 
	 * @param command
	 *            注册的命令
	 */
	public void registerCustomIntent(IntentCommand command) {
		if (!commands.contains(command)) {
			commands.add(command);
			Log.e("CustomIntentSwitcherController", "commands:" + commands.size());
		}
	}

	/**
	 * 移除自定义快捷方式命令 <br>
	 * 当快捷方式从桌面移除的时候你可能要调用此方法 <br>
	 * 
	 * @param command
	 *            快捷方式命令
	 */
	public void unRegisterCustomIntent(IntentCommand command) {
		commands.remove(command);
	}

	/**
	 * 移除自定义快捷方式命令 <br>
	 * 当快捷方式从桌面移除的时候你可能要调用此方法 <br>
	 * 
	 * @param commandAct
	 *            快捷方式命令action字符串 Intent.getAction()
	 */
	public void unRegisterCustomIntent(String commandAct) {
		IntentCommand com = null;
		for (IntentCommand command : commands) {
			if (commandAct.equals(command.getCommandAction())) {
				com = command;
			}
		}
		if (com != null)
			unRegisterCustomIntent(com);
	}

	/**
	 * 点击事件之后调用action <br>
	 * eg.如要打开匣子，则调用action()
	 * @param actionFrom 点击来源
	 * @param action
	 *            自定义快捷方式action
	 */
	public void onAction(Context ctx,ApplicationInfo app, int actionFrom) {
		if (app.itemType != Favorites.ITEM_TYPE_CUSTOM_INTENT &&
				app.itemType != Favorites.ITEM_TYPE_INDEPENDENCE)
			return;
		final String action = app.intent.getAction();
		if (StringUtil.isEmpty(action))
			return;
		for (IntentCommand command : commands) {
			if (command == null)
				continue;
			if (action.equals(command.getCommandAction())) {
				command.action(ctx, actionFrom);
				break;
			}
		}
	}
	
	/**
	 * 长按回调
	 * @param ctx
	 * @param app
	 * @param actionFrom
	 */
	public boolean onLongClickAction(Context ctx,ApplicationInfo app, int actionFrom) {
		if (app.itemType != Favorites.ITEM_TYPE_CUSTOM_INTENT)
			return true;
		final String action = app.intent.getAction();
		if (StringUtil.isEmpty(action))
			return true;
		for (IntentCommand command : commands) {
			if (command == null)
				continue;
			if (action.equals(command.getCommandAction())) {
				command.action4LongClick(ctx, actionFrom);
				break;
			}
		}
		return true;
	}

	/**
	 * 查看命令
	 * 
	 * @param app
	 * @return
	 */
	public IntentCommand findCommand(ApplicationInfo app) {
		if (StringUtil.isEmpty(app.intent.getAction()))
			return null;
		if (commands != null && commands.size() != 0) {
			for (IntentCommand intentCommand : commands) {
				if (intentCommand.getCommandAction().equals(app.intent.getAction()))
					return intentCommand;

			}
		}
		return null;
	}
}
