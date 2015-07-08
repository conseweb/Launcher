/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bitants.common.launcher.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.R;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.BaseLauncherApplication;

import java.util.ArrayList;

public class InstallShortcutReceiver extends AntBroadcastStaticReceiver {
	private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String EXCLUDE_91_LAUNCHER = "exclude_91_launcher";

    public static ArrayList<Intent> intents = new ArrayList<Intent>();

    public static InterceptNotification interceptNotification;

	/**
	 * 生成桌面自定义快捷方式 caizp 2013-12-03
	 */
	public static final String ACTION_INSTALL_ND_SHORTCUT = "com.nd.pandahome.install_shortcut";

	@Override
	public void onReceiveHandler(final Context context, Intent data) {
        if(data == null || (!ACTION_INSTALL_SHORTCUT.equals(data.getAction()) && !ACTION_INSTALL_ND_SHORTCUT.equals(data.getAction()))) {
            return;
        }
        if(data.getBooleanExtra(EXCLUDE_91_LAUNCHER, false)) {
            return;
        }

        //不拦截桌面自身添加的快捷
        if(ACTION_INSTALL_ND_SHORTCUT.equals(data.getAction())) {
            installShortcut(context, data);
            return;
        }

        if(interceptNotification != null) {
            intents.add(data);
            interceptNotification.sendInterceptNotifications(context, intents);
        }else{
            installShortcut(context, data);
        }
	}

	public static boolean installShortcut(Context context, Intent data) {
		if (((BaseLauncherApplication) context.getApplicationContext()).getModel() == null) {
			// Toast.makeText(context, "桌面接收快捷方式数据异常",
			// Toast.LENGTH_SHORT).show();
			return false;
		}

		// safety check 1
		String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		if (StringUtil.isEmpty(name))
			return false;

		// safety check 2
		if (name.length() >= 150) {
			Toast.makeText(context, context.getString(R.string.shortcut_toolong), Toast.LENGTH_SHORT).show();
		}

		// safety check 3
		Parcelable p = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		if (p == null || !(p instanceof Intent))
			return false;

		// 寻找放置位置
		BaseLauncher launcher = BaseConfig.getBaseLauncher();
		if (launcher == null || launcher.getScreenViewGroup() == null)
			return false;

		int[] loc = launcher.getScreenViewGroup().getLocationForNewInstallApp(context);

		// 添加到桌面
		if (loc != null) {
			int screen = loc[0];
			int[] cellXY = new int[] { loc[1], loc[2] };

			Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

			if (intent.getAction() == null) {
				intent.setAction(Intent.ACTION_VIEW);
			}
			boolean duplicate = data.getBooleanExtra(BaseLauncher.EXTRA_SHORTCUT_DUPLICATE, true);
			if (duplicate || !BaseLauncherModel.shortcutExists(context, name, intent)) {
				ApplicationInfo info = launcher.getLauncherModel().addShortcut(launcher, data, screen, cellXY, false);
				View view = launcher.createCommonAppView(info);
				launcher.getScreenViewGroup().addInScreen(view, screen, cellXY[0], cellXY[1], info.spanX, info.spanY, launcher.isWorkspaceLocked(), false);
				launcher.delayRefreshWorkspaceSpringScreen(300);
				Toast.makeText(context, context.getString(R.string.shortcut_installed, name), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, context.getString(R.string.shortcut_duplicate, name), Toast.LENGTH_SHORT).show();
			}

			return true;
		} else {
			Toast.makeText(context, context.getString(R.string.out_of_space), Toast.LENGTH_SHORT).show();
		}

		return false;
	}

    /**
     * 通知拦截接口
     */
    public static interface InterceptNotification  {
        public void sendInterceptNotifications(Context mContext, ArrayList<Intent> intents);
    }


}
