package com.nd.hilauncherdev.webconnect.downloadmanage.model;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class PluginDownloadForwardServiceConnection implements ServiceConnection {

	private IPluginDownloadForwardService sAidlService = null;
	private CommonCallBack<Boolean> mCallBack;
	private boolean mIsConnectioned = false;

	public PluginDownloadForwardServiceConnection() {
	}

	public PluginDownloadForwardServiceConnection(CommonCallBack<Boolean> callBack) {
		this.mCallBack = callBack;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		try {
			sAidlService = (IPluginDownloadForwardService) IPluginDownloadForwardService.Stub.asInterface(service);
			mIsConnectioned = true;
			if (mCallBack != null) {
				mCallBack.invoke(new Boolean(true));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		sAidlService = null;
		mIsConnectioned = false;
	}

	public boolean isConnectioned() {
		return mIsConnectioned;
	}

	public String getData(String identification) throws RemoteException {
		if (null != sAidlService && isConnectioned()) {
			return sAidlService.getData(identification);
		}
		return "";
	}
}
