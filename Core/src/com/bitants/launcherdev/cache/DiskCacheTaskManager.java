/**
 * @author Michael
 * Date:2014-4-30下午4:12:50
 *
 */
package com.bitants.launcherdev.cache;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Process;

import com.bitants.launcherdev.launcher.config.BaseConfig;

/**
 * @author Michael Date:2014-4-30下午4:12:50
 * 
 */
public class DiskCacheTaskManager {

	private static DiskCacheTaskManager instance;

	private static final ConcurrentHashMap<String, Runnable> saveToDiskQueue = new ConcurrentHashMap<String, Runnable>();
	private static final ConcurrentHashMap<String, Runnable> fileValidDiskQueue = new ConcurrentHashMap<String, Runnable>();

	public static DiskCacheTaskManager getInstance() {
		if (instance == null) {
			instance = new DiskCacheTaskManager();
		}
		return instance;
	}

	private WorkThread sWorkerThread;

	private Handler workThreadHandler;

	private DiskCacheTaskManager() {
		sWorkerThread = new WorkThread("disk_cache");
		sWorkerThread.start();
		workThreadHandler = new Handler(sWorkerThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
                try {
                    if (msg.what == 0) {
                        Iterator iterator = saveToDiskQueue.keySet().iterator();
                        if (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Runnable runnable = saveToDiskQueue.get(key);
                            if (runnable != null) runnable.run();
                            saveToDiskQueue.remove(key);
                        }
                    } else if (msg.what == 1) {
                        Iterator iterator = fileValidDiskQueue.keySet().iterator();
                        if (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Runnable runnable = fileValidDiskQueue.get(key);
                            if (runnable != null) runnable.run();
                            fileValidDiskQueue.remove(key);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
		};
	}

	/**
	 * 加入写文件任务
	 * 
	 * @author Michael Date:2014-5-16下午4:12:22
	 * @param key
	 * @param runnable
	 */
	public void postTask(String key, Runnable runnable) {
		saveToDiskQueue.put(key, runnable);
		// 往主线程中发送一条更新的信息
		BaseConfig.getIconCache().messageQueue.addIdleHandler(new MessageQueue.IdleHandler() {
			@Override
			public boolean queueIdle() {
				getWorkThreadHandler().sendEmptyMessage(0);
				return false;
			}
		});
	}

	/**
	 * 加入判断文件是否被卸载任务
	 * 
	 * @author Michael Date:2014-5-16下午4:12:36
	 * @param fileName
	 * @param runnable
	 */
	public void postCheckFileValid(String fileName, Runnable runnable) {
		fileValidDiskQueue.put(fileName, runnable);
		// 往主线程中发送一条更新的信息
		BaseConfig.getIconCache().messageQueue.addIdleHandler(new MessageQueue.IdleHandler() {
			@Override
			public boolean queueIdle() {
				getWorkThreadHandler().sendEmptyMessage(1);
				return false;
			}
		});
	}

	// 设置线程为低优先级
	public static class WorkThread extends HandlerThread {

		/**
		 * @param name
		 */
		public WorkThread(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			super.run();
		}
	}

	public Handler getWorkThreadHandler() {
		return workThreadHandler;
	}

}
