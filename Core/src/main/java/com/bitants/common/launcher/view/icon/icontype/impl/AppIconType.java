/**
 *
 */
package com.bitants.common.launcher.view.icon.icontype.impl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Handler;

import com.bitants.common.kitset.util.ThreadUtil;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.launcher.view.icon.icontype.IconType;
import com.bitants.common.kitset.util.AndroidPackageUtils;

/**
 *
 */
public class AppIconType extends IconType {
	
	public Bitmap refreshIcon(final LauncherIconViewConfig config, Object tag,
			final Context context, final Handler handler) {
		super.refreshIcon(config, tag, context, handler);
		if(tag != null && tag instanceof ApplicationInfo){
			final ApplicationInfo info = (ApplicationInfo)tag;
			// 延迟加载应用程序图标
			if(!info.customIcon){//非自定义图标
				if (info.intent == null || info.componentName == null)
					return null;
				ThreadUtil.execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                final PackageManager pm = context.getPackageManager();
                                ResolveInfo resolve = AndroidPackageUtils.getResolveInfo(info.intent, pm);
                                if (resolve == null) {
                                    info.iconBitmap = BaseConfig.getIconCache().getIcon(info);
                                } else {
                                    BaseConfig.getIconCache().getTitleAndIcon(info, resolve);
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(0);
                                    }
                                });
                            }

                        }
                );
			}
		}
		return null;
	}

	public void ajustConfig(Context context, ApplicationInfo info, LauncherIconViewConfig config) {
		if(!BaseLauncher.hasDrawer){
			config.setNewInstall(LauncherConfig.getLauncherHelper().isNewInstallApp(info));
		}
	}
}
