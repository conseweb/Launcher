package com.bitants.launcherdev.kitset.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.bitants.common.kitset.util.MessageUtils;
import com.bitants.common.kitset.util.OpenRootUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.launcher.R;
import com.bitants.launcherdev.datamodel.db.MyPhoneDB;
import com.bitants.common.framework.ViewFactory;
import com.bitants.common.framework.view.dialog.CommonDialog;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.preference.SettingsConstants;

import java.io.File;

//import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
//import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
//import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;

/**
 * apk包安装应用工具类
 */
public class ApkInstaller {

	/**
	 * 应用安装的广播action
	 */
	public final static String RECEIVER_APP_SILENT_INSTALL = "receiver_app_silent_install";
	/**
	 * Intent传递安装状态
	 */
	public final static String EXTRA_APP_INSTALL_STATE = "extra_app_install_state";

	/**
	 * Intent传递安装应用的包名Key
	 */
	public final static String EXTRA_APP_INSTALL_PACAKGE_NAME = "extra_app_install_pacakge_name";

	/**
	 * Intent传递安装应用的包路径Key
	 */
	public final static String EXTRA_APP_INSTALL_APK_PATH = "extra_app_install_apk_path";

	/** 安装状态--正在安装 */
	public final static int INSTALL_STATE_INSTALLING = 10000;
	/** 安装状态--安装成功 */
	public final static int INSTALL_STATE_INSTALL_SUCCESS = 20000;
	/** 安装状态--安装失败 */
	public final static int INSTALL_STATE_INSTALL_FAILED = 30000;

	/**
	 * 提示过静默安装
	 */
	private static int flag_silent_install_tiped = BaseConfig.NO_DATA;
	public static final String SILENT_INSTALL_TIPED_SELECT = "select value from Config where id = '" + SettingsConstants.SETTINGS_SILENT_INSTALL_TIP + "'";
	public static final String SILENT_INSTALL_TIPED_INSERT = "insert into Config values('" + SettingsConstants.SETTINGS_SILENT_INSTALL_TIP + "', 'Y')";

	public static final String SILENT_INSTALL_OPENED_SELECT = "select value from Config where id = '" + SettingsConstants.SETTINGS_SILENT_INSTALL + "'";
	public static final String SILENT_INSTALL_OPENED_INSERT = "insert into Config values('" + SettingsConstants.SETTINGS_SILENT_INSTALL + "', 'Y')";
	public static final String SILENT_INSTALL_OPENED_DELETE = "delete from Config where id='" + SettingsConstants.SETTINGS_SILENT_INSTALL + "'";
	
	/**
	 * 具备静默安装的应用安装方法
	 * @param context
	 * @param apkFile
	 */
	public static void installApplicationShoudSilent(final Context context, final File apkFile) {
		if (TelephoneUtil.hasRootPermission())// 有root权限
		{
			// 是否开启静默安装
			boolean isSilentInstall = isSilentInstallable(context);
			if (isSilentInstall) {
				// 已开启静默安装，使用静默安装
				installAppInThread(context, apkFile);
			} else
				// 未开启静默安装，则采用普通安装
				installApplicationNormal(context, apkFile);

		} else {
			installApplicationNormal(context, apkFile);
		}

	}// end installApplicationShoudSilent

	private static void installAppInThread(Context context, File apkFile) {
//		DownloadServerServiceConnection mConnection = new DownloadServerServiceConnection(context);
//		mConnection.installAppSilent(apkFile);
//		mConnection = null;
	}

	/**
	 * 显示静默安装提示框
	 * @param context
	 */
	public static void showSilentInstallTipDialog(final Context context) {
		// 未开启静默安装，且未提示
		String title = context.getString(R.string.settings_advanced_silent_install);
		CommonDialog alertd = ViewFactory.getAlertDialog(context, -1, title, context.getString(R.string.settings_advanced_silent_install_request),
				context.getString(R.string.settings_advanced_silent_install_open_now), context.getString(R.string.common_button_cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						dialog.dismiss();
						// 设置已提示过
						setSilentInstallTiped(context);
						// 开启静默安装
						boolean open = OpenRootUtil.openSuperShell(context, 15 * 1000);
						if (!open) {
							// 开启失败，采用普通安装
							MessageUtils.makeShortToast(context, context.getString(R.string.settings_advanced_silent_install_open_failed));
							// installApplicationNormal(context, apkFile);
							return;
						}

						// 设置是开启静默安装
						setSilentInstallable(context, true);

						// 开始静默安装
						// installAppInThread(context,apkFile);

					}
				},

				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						// 设置已提示过
						setSilentInstallTiped(context);
						// 设置是关闭静默安装
						setSilentInstallable(context, false);
						// installApplicationNormal(context, apkFile);
					}
				});

		alertd.setCanceledOnTouchOutside(false);
		alertd.setCancelable(false);
		alertd.show();
	}

	/**
	 * 查询APK是否正在安装中
	 * @param context
	 * @param packageName
	 * @return boolean
	 */
	public static synchronized boolean isApkInstalling(Context context, String packageName) {
//		DownloadServerServiceConnection mConnection = new DownloadServerServiceConnection(context);
//		boolean isInstalling = mConnection.isApkInstalling(packageName);
//		mConnection = null;
//		return isInstalling;
		return false;
	}

	/**
	 * 安装应用程序,普通安装方式
	 * @param ctx
	 * @param mainFile
	 * @return boolean
	 */
	public static boolean installApplicationNormal(Context ctx, File mainFile) {
		try {
			Uri data = Uri.fromFile(mainFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(data, "application/vnd.android.package-archive");
			ctx.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 是否开启了静默安装
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSilentInstallable(Context context) {
		/*
		 * if(flag_silent_install_opened!=Global.NO_DATA) return
		 * flag_silent_install_opened==1?true:false;
		 */
		try {
			MyPhoneDB db = new MyPhoneDB(context);
			Cursor c = db.query(SILENT_INSTALL_OPENED_SELECT);
			boolean hadInit = c.moveToNext();
			c.close();
			db.close();
			// flag_silent_install_opened=hadInit?1:0;
			return hadInit;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * 设置已提示过静默安装
	 * 
	 * @param context
	 * @param isTiped
	 */
	public static void setSilentInstallTiped(Context context) {
		try {
			MyPhoneDB db = new MyPhoneDB(context);
			db.execSQL(SILENT_INSTALL_TIPED_INSERT);
			db.close();
			flag_silent_install_tiped = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开启/关闭静默安装
	 * 
	 * @param context
	 * @param silentInstallable
	 */
	public static void setSilentInstallable(Context context, boolean silentInstallable) {
		try {
			MyPhoneDB db = new MyPhoneDB(context);
			if (silentInstallable)
				db.execSQL(SILENT_INSTALL_OPENED_INSERT);
			else
				db.execSQL(SILENT_INSTALL_OPENED_DELETE);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否已提示过静默安装
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSilentInstallTiped(Context context) {
		if (flag_silent_install_tiped != BaseConfig.NO_DATA)
			return flag_silent_install_tiped == 1 ? true : false;

		try {
			MyPhoneDB db = new MyPhoneDB(context);
			Cursor c = db.query(SILENT_INSTALL_TIPED_SELECT);
			boolean hadInit = c.moveToNext();
			c.close();
			db.close();
			flag_silent_install_tiped = hadInit ? 1 : 0;
			return hadInit;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
		
	}//end isSilentInstallTiped

}
