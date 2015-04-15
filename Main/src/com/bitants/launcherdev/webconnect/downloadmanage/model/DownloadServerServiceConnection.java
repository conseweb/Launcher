package com.bitants.launcherdev.webconnect.downloadmanage.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerService;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.IDownloadManagerService;

/**
 * 绑定下载进程服务后回调逻辑类
 * 
 * @author pdw
 * @date 2013-1-8 上午11:19:45
 */
public class DownloadServerServiceConnection implements ServiceConnection {

	private static IDownloadManagerService sAidlService = null;

	private Context mContext = null;

	private CommonCallBack<Boolean> mCallBack;
	private boolean mIsConnectionBind = false;

	public DownloadServerServiceConnection(Context context) {
		mContext = context;
		try {
			if (sAidlService == null || !sAidlService.isServiceAlive()) {
				context.startService(new Intent(mContext, DownloadServerService.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		try {
			IDownloadManagerService remoteService = IDownloadManagerService.Stub.asInterface(service);
			DownloadManager.getInstance().initService(remoteService);
			if (mCallBack != null) {
				mCallBack.invoke(new Boolean(true));
			}
			sAidlService = remoteService;
			mIsConnectionBind = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		sAidlService = null;
		mIsConnectionBind = false;
	}

	/**
	 * 跨进程添加下载任务
	 * 
	 * @param info
	 * @return
	 */
	public boolean addDownloadTask(BaseDownloadInfo info) {

		if (!startBind())
			return false;

		if (sAidlService != null) {
			try {
				return sAidlService.addDownloadTask(info);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 跨进程批量添加下载任务
	 * 
	 * @param infos
	 * @return
	 */
	public boolean addDownloadTask(ArrayList<BaseDownloadInfo> infos) {
		if (!startBind())
			return false;

		if (sAidlService != null) {
			try {
				for (BaseDownloadInfo info : infos) {
					sAidlService.addDownloadTask(info);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 添加静默下载任务
	 * 
	 * @param info
	 * @return
	 */
	public boolean addSilentDownloadTask(BaseDownloadInfo info) {

		if (!startBind())
			return false;

		if (sAidlService != null) {
			try {
				return sAidlService.addSilentDownloadTask(info);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 批量添加静默下载任务
	 * 
	 * @param infos
	 * @return
	 */
	public boolean addSilentDownloadTask(ArrayList<BaseDownloadInfo> infos) {
		if (!startBind())
			return false;

		if (sAidlService != null) {
			try {
				for (BaseDownloadInfo info : infos) {
					sAidlService.addSilentDownloadTask(info);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 暂停下载任务
	 * 
	 * @param identification
	 * @return true 下载队列存在对应的下载任务，等待暂停广播处理<br>
	 *         false 下载队列不存在对应的下载任务，自行处理ui
	 */
	public boolean pause(String identification) {
		if (!startBind())
			return false;

		if (sAidlService != null) {
			try {
				return sAidlService.pause(identification);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 取消下载任务
	 * 
	 * @param identification
	 * @return true 下载队列存在对应的下载任务，等待取消广播处理<br>
	 *         false 下载队列不存在对应的下载任务，自行处理ui
	 */
	public boolean cancel(String identification) {
		if (!startBind())
			return false;

		if (sAidlService != null) {
			try {
				return sAidlService.cancel(identification);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 清除总队列
	 */
	public boolean clearAllDownloadTask() {
		if (!startBind())
			return false;

		try {
			sAidlService.clearAllDownloadTask();
			return true;
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * 获取下载总队列
	 * 
	 * @return
	 */
	public Map<String, BaseDownloadInfo> getDownloadTasks() {
		if (!startBind())
			return null;

		try {
			return (Map<String, BaseDownloadInfo>) sAidlService.getDownloadTasks();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 获取单个下载任务
	 * @param identification 标识
	 * @return 下载任务
	 */
	public BaseDownloadInfo getDownloadTask(String identification) {
		if (!startBind())
			return null;

		try {
			return sAidlService.getDownloadTask(identification);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 返回任务数，包括所有状态的
	 * 
	 * @return
	 */
	public int getTaskCount() {
		if (!startBind())
			return 0;

		try {
			return sAidlService.getTaskCount();
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 是否正在安装
	 * 
	 * @param packageName
	 *            标识：包名
	 * @return
	 */
	public boolean isApkInstalling(String packageName) {
		if (!startBind())
			return false;

		try {
			return sAidlService.isApkInstalling(packageName);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 静默安装
	 * 
	 * @param apkFile
	 */
	public void installAppSilent(File apkFile) {
		if (!startBind())
			return;

		try {
			sAidlService.installAppInThread(apkFile.getAbsolutePath());
		} catch (Exception e) {
		}
	}

	/**
	 * 绑定下载服务，异步型，绑定后服务实例不能马上获取到，需等绑定完成后回调通知才可确认服务实例可用
	 * 
	 * @param context
	 * @param handler
	 *            执行回调时切到UI线程的处理,
	 * @param callBack
	 *            回调的接口
	 */
	public void bindDownloadService(final CommonCallBack<Boolean> callBack) {
		mCallBack = callBack;
		if (isBind()) {
			// 回调接口
			if (callBack != null)
				callBack.invoke(new Boolean(true));
			return;
		}

		// 开始绑定
		boolean bindSuccess = mContext.bindService(new Intent(mContext, DownloadServerService.class), DownloadServerServiceConnection.this, Context.BIND_AUTO_CREATE);

		if (!bindSuccess) {
			// 回调接口
			if (callBack != null)
				callBack.invoke(new Boolean(false));
		}

	}// end bindDownloadService

	/**
	 * <br>Description:解除下载服务绑定
	 * <br>Author:zhuchenghua
	 * <br>Date:2013-4-19下午05:30:50
	 */
	public void unBindDownloadService() {
		try {
			if (mIsConnectionBind) {
				mContext.unbindService(DownloadServerServiceConnection.this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 开始绑定
	 * 
	 * @param context
	 * @return
	 */
	public synchronized boolean startBind() {
		if (isBind())
			return true;

		boolean confirmBindSuccess = false;
		try {
			boolean bindSuccess = mContext.bindService(new Intent(mContext, DownloadServerService.class), DownloadServerServiceConnection.this, Context.BIND_AUTO_CREATE);
			// 确认绑定成功，因为绑定需要一段时间，所以进行二次确认
			if (bindSuccess) {
				long begin = System.currentTimeMillis();
				while (true) {

					if (isBind()) {
						confirmBindSuccess = true;
						break;
					}

					// 超3秒还没绑定完成，当作绑定失败
					if ((System.currentTimeMillis() - begin) >= 3000)
						break;
				}

			} else {
				confirmBindSuccess = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				mContext.unbindService(DownloadServerServiceConnection.this);
			} catch (Exception e2) {
			}
		}

		return confirmBindSuccess;

	}// end startBind

	/**
	 * 注销绑定的下载服务
	 * 
	 * @param context
	 */
	/*
	 * public void unBindDownloadService() { try {
	 * mContext.unbindService(DownloadServerServiceConnection.this); } catch
	 * (Exception e) { } }
	 */

	/**
	 * 是否已绑定
	 * 
	 * @return
	 */
	public boolean isBind() {
		try {
			if (sAidlService != null && sAidlService.isServiceAlive())
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 获取下载状态
	 * 
	 * @param key
	 *            唯一标识
	 * @return
	 */
	public BaseDownloadInfo getDownloadState(String identification) {
		if (!startBind())
			return null;

		try {
			return sAidlService.getDownloadState(identification);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 继续下载
	 */
	public boolean continueDownload(String identification) {
		if (!startBind())
			return false;

		try {
			return sAidlService.continueDownload(identification);
		} catch (Exception e) {
		}

		return false;
	}
}
