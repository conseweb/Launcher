/**
 * @author Michael
 * Date:2014-3-20上午8:15:32
 *
 */
package com.nd.hilauncherdev.launcher.view.icon.receiver;


import android.content.Context;
import android.content.IntentFilter;
import android.view.View;

/**
 * @author Michael
 * Date:2014-3-20上午8:15:32
 *
 */
public class DefaultReceiverManager {
	
	/**
	 * 注销广播
	 * @author Michael
	 * @createtime 2013-7-25
	 * @param iconMaskTextView
	 */
	public static void unRegisterReceiver(LauncherIconViewReceiver receiver, View view) {
		Context mContext = view.getContext();
		if (receiver != null) {
			mContext.unregisterReceiver(receiver);
			receiver = null;
		}
	}
	
	/**
	 * 注册广播
	 * @author Michael
	 * Date:2014-3-20上午8:16:46
	 *  @param mContext
	 *  @param tag
	 *  @param iconMaskSupport
	 */
	public static boolean registerReceiver(LauncherIconViewReceiver receiver, Context mContext, IntentFilter[] intentFilters){
		boolean flag = false;
		if(intentFilters != null && intentFilters.length > 0){
			for (IntentFilter intentFilter : intentFilters) {
				mContext.registerReceiver(receiver, intentFilter);
				flag = true;
			}
		}
		return flag;
	}

}
