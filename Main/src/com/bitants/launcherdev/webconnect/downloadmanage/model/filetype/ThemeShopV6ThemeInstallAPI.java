package com.bitants.launcherdev.webconnect.downloadmanage.model.filetype;



import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bitants.launcherdev.kitset.util.FileUtil;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.theme.ThemeManagerFactory;
import com.bitants.launcherdev.theme.module.ModuleConstant;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcherdev.kitset.util.FileUtil;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.theme.ThemeManagerFactory;
import com.bitants.launcherdev.theme.data.ThemeGlobal;
import com.bitants.launcherdev.theme.data.ThemeGlobal;
import com.bitants.launcherdev.theme.module.ModuleConstant;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;
import com.bitants.launcherdev.kitset.util.FileUtil;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.theme.ThemeManagerFactory;
import com.bitants.launcherdev.theme.module.ModuleConstant;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadManager;


/**
 * 与6.0桌面的交互接口
 * @author cfb
 *
 */
public class ThemeShopV6ThemeInstallAPI  {

	/**
	 * 接收商城安装APT主题请求后返回处理结果(参数为themeid)
	 */
	public static final String THEME_APT_INSTALL_RESULT = "com.dianxinos.dxhome.response.theme.apt.install";
	
	/**安装失败*/
	public static final String THEME_APT_INSTALL_RESULT_FAIL = "nd.pandahome.response.theme.apt.install.fail";

	/**
	 * 广播参数主题ID
	 */
	private static final String THEME_PARAMETER_THEME_ID = "themeid";
	
	/**
	 * 广播参数服务端主题ID
	 */
	private static final String THEME_PARAMETER_SERVER_THEME_ID = "serverThemeID";
	
	public static final Set<String> installIngSet = Collections.synchronizedSet(new HashSet<String>());
	
	public static void sendInstallAPT(final Context context, final String aptPath, final String serverThemeID, final BaseDownloadInfo downloadInfo){
		
		if (installIngSet.contains(serverThemeID)){
			return;
		}
		
		ThreadUtil.executeDrawer(new Runnable() {
            @Override
            public void run() {
                try {
                    if (aptPath == null) {
                        return;
                    }
                    String diskFile = aptPath;
                    if (diskFile.endsWith(".temp")) {
                        diskFile = diskFile.substring(0, diskFile.indexOf(".temp"));
                    }
                    if (diskFile.endsWith(ThemeGlobal.SUFFIX_APT_THEME)) {
                        installIngSet.add(serverThemeID);
                        String resultThemeId = ThemeManagerFactory.getInstance().installAptTheme(diskFile);
                        installIngSet.remove(serverThemeID);
                        if (!StringUtil.isEmpty(resultThemeId)) {

                            downloadInfo.putAdditionInfo(ThemeFileHelper.AdditionKey, resultThemeId);
                            DownloadManager.getInstance().modifyAdditionInfo(downloadInfo);

                            Intent aptInstallResultIntent = new Intent(THEME_APT_INSTALL_RESULT);
                            aptInstallResultIntent.putExtra(THEME_PARAMETER_SERVER_THEME_ID, serverThemeID);
                            aptInstallResultIntent.putExtra(THEME_PARAMETER_THEME_ID, resultThemeId);
                            aptInstallResultIntent.addFlags(32);
                            context.sendBroadcast(aptInstallResultIntent);

                            Intent themeListRefreshIntent = new Intent(ThemeGlobal.INTENT_THEME_LIST_REFRESH);
                            context.sendBroadcast(themeListRefreshIntent);
                            FileUtil.delFile(aptPath);
                        } else {
                            Intent aptInstallResultIntent = new Intent(THEME_APT_INSTALL_RESULT_FAIL);
                            aptInstallResultIntent.putExtra(THEME_PARAMETER_SERVER_THEME_ID, serverThemeID);
                            aptInstallResultIntent.putExtra(THEME_PARAMETER_THEME_ID, resultThemeId);
                            aptInstallResultIntent.addFlags(32);
                            context.sendBroadcast(aptInstallResultIntent);

                            Log.d("ThemeShopV3LauncherExAPI", "主题安装失败");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}

	public static void sendInstallModule(final Context context, final String aptPath, final String serverThemeID, final String moduleKey, final BaseDownloadInfo downloadInfo){
		
		if (installIngSet.contains(serverThemeID)){
			return;
		}
		
		ThreadUtil.executeDrawer(new Runnable() {
			@Override
			public void run() {
				try {
					if (aptPath==null){
						return;
					}
					String diskFile = aptPath;
					if (diskFile.endsWith(".temp")) {
						diskFile = diskFile.substring(0, diskFile.indexOf(".temp"));
					}
					if (diskFile.endsWith(ThemeGlobal.SUFFIX_APT_THEME)) {
						installIngSet.add(serverThemeID);
						String resultThemeId = ThemeManagerFactory.getInstance().installAptThemeModule(diskFile, moduleKey);
						installIngSet.remove(serverThemeID);
						if(!StringUtil.isEmpty(resultThemeId)){
							
							downloadInfo.putAdditionInfo(ThemeFileHelper.AdditionKey, resultThemeId);
							DownloadManager.getInstance().modifyAdditionInfo(downloadInfo);
							
							Intent aptInstallResultIntent = new Intent(THEME_APT_INSTALL_RESULT);
							aptInstallResultIntent.putExtra(THEME_PARAMETER_SERVER_THEME_ID, serverThemeID);
							aptInstallResultIntent.putExtra(THEME_PARAMETER_THEME_ID, resultThemeId);
							aptInstallResultIntent.addFlags(32);
							context.sendBroadcast(aptInstallResultIntent);
							
							Intent moduleListRefreshIntent = new Intent(ModuleConstant.INTENT_MODULE_LIST_REFRESH);
							context.sendBroadcast(moduleListRefreshIntent);
							FileUtil.delFile(aptPath);
						}else{
							Intent aptInstallResultIntent = new Intent(THEME_APT_INSTALL_RESULT_FAIL);
							aptInstallResultIntent.putExtra(THEME_PARAMETER_SERVER_THEME_ID, serverThemeID);
							aptInstallResultIntent.putExtra(THEME_PARAMETER_THEME_ID, resultThemeId);
							aptInstallResultIntent.addFlags(32);
							context.sendBroadcast(aptInstallResultIntent);
							
							Log.d("ThemeShopV3LauncherExAPI", "模块安装失败");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
