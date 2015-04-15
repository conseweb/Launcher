package com.bitants.launcherdev.menu.personal;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.bitants.launcherdev.datamodel.db.DowningTaskItem;
import com.bitants.launcherdev.framework.ViewFactory;
import com.bitants.launcherdev.framework.view.commonview.FooterView;
import com.bitants.launcherdev.framework.view.commonview.MyphoneTabContainer;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.theme.shop.shop3.AsyncImageLoader;
import com.bitants.launcherdev.theme.shop.shop3.AsyncImageLoader.ImageCallback;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerService;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.bitants.launcherdev.webconnect.downloadmanage.model.filetype.FileType;
import com.bitants.launcher.R;
import com.bitants.launcherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;

/**
 * Title:所有下载的统合 包括软件和主题 <br>
 * Author:zhenghonglin <br>
 * Date:2013-4-18上午11:14:59
 */
public class AllDownloadManagerActivity extends Activity implements OnItemClickListener {

	private final String TAG = "AllDownloadManagerActivity";
	private enum Tab {
		TAB_APK(
				DownloadServerService.SHOW_TYPE_APK, new FileType[] {
				FileType.FILE_APK, FileType.FILE_DYNAMIC_APK },
				R.string.personal_downloadmanager_soft_tab_title, true), 
		TAB_THEME(
				DownloadServerService.SHOW_TYPE_THEME, null,
				R.string.personal_downloadmanager_theme_tab_title, false), 
		TAB_WALLPAPER(
				DownloadServerService.SHOW_TYPE_WALLPAPER,
				new FileType[] { FileType.FILE_WALLPAPER },
				R.string.personal_downloadmanager_wallpaper_tab_title, false),
		TAB_RING(
				DownloadServerService.SHOW_TYPE_RING,
				new FileType[] { FileType.FILE_RING },
				R.string.personal_downloadmanager_ring_tab_title, false),
		TAB_LOCK(
				DownloadServerService.SHOW_TYPE_LOCK,
				new FileType[] { FileType.FILE_LOCK },
				R.string.personal_downloadmanager_lock_tab_title, false);

		RelativeLayout mContainer;
		ListView mListView;
		BaseAdapter mListAdapter;
		FooterView mClearBtn;
		View mLoadingView;
		View mNoDataView;
		FileType[] mFileTypes = null;
		int mShowType;
		int mTitleId;
		boolean mShowClear = false;
		ArrayList<BaseDownloadInfo> mData;

		Tab(int showType, FileType[] fileTypes, int titleId, boolean showClear) {
			mShowType = showType;
			mFileTypes = fileTypes;
			mTitleId = titleId;
			mShowClear = showClear;
		};

		void showNoData(boolean show) {
			if (mShowClear && mClearBtn != null) {
				mClearBtn.setBottomEnabled(0, (show ? false : true));
			}
			if (mNoDataView != null) {
				mNoDataView.setVisibility(show ? View.VISIBLE : View.GONE);
			}
			if (mLoadingView != null) {
				mLoadingView.setVisibility(View.GONE);
			}
		}

		static Tab fromIndex(int index) {
			int i = 0;
			for (Tab t : getTabValues()) {
				if (i++ == index)
					return t;
			}
			return TAB_APK;
		}

		static int mapShowTypeToIndex(int showType) {
			int i = 0;
			for (Tab t : getTabValues()) {
				if (showType == t.mShowType)
					return i;
				i++;
			}
			return 0;
		}
	};
	private MyphoneTabContainer container;
	/**
	 * 下载服务
	 */
	private DownloadServerServiceConnection mDownloadService = null;
	private BroadcastReceiver mNewAppInstallReceiver;
	private Handler mHandler = new Handler();
	private Context context;
	private Dialog applyThemeDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		container = new MyphoneTabContainer(context);
		// setContentView(R.layout.all_downloadmanager_activity);
		setContentView(container);
		initServiceAndReceiver();

		Intent intent = getIntent();
		int showType = (intent != null) ? intent.getIntExtra(DownloadServerService.EXTRA_SHOW_TYPE, -1) : -1;
		initTabContent(Tab.mapShowTypeToIndex(showType));

		container.setGoBackListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				finish();
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		int showType = intent.getIntExtra(DownloadServerService.EXTRA_SHOW_TYPE, -1);
		if (showType != -1 && container != null) {
			container.getPagerTab().setToScreen(Tab.mapShowTypeToIndex(showType));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadDownloadList();
		loadThemeData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			clearTabContent();
			if (mNewAppInstallReceiver != null) {
				unregisterReceiver(mNewAppInstallReceiver);
				mNewAppInstallReceiver = null;
			}
			if (mRefreshReceiver != null) {
				unregisterReceiver(mRefreshReceiver);
				mRefreshReceiver = null;
			}
			mDownloadService.unBindDownloadService();
		} catch (Exception e) {

		}
	}

	private void clearTabContent() {
		Tab[] tabs = getTabValues();
		for (Tab t : tabs) {
			t.mContainer = null;
			t.mListView = null;
			t.mListAdapter = null;
			t.mClearBtn = null;
			t.mLoadingView = null;
			t.mNoDataView = null;
		}
	}

	private void initTabContent(int initTab) {
		Tab[] tabs = getTabValues();
		int size = tabs.length;
		if (size > 0) {
			String[] tabTitles = new String[size];
			View[] views = new View[size];

			Tab t;
			for (int i = 0; i < size; i++) {
				t = tabs[i];
				tabTitles[i] = getString(t.mTitleId);
				initSingleTabView(t);
				views[i] = t.mContainer;
			}

			container.initContainer(null, getString(R.string.personal_downloadmanager_title), views, tabTitles);
			container.setInitTab(initTab);
		}
	}

	private void initSingleTabView(Tab tab) {
		if (tab == Tab.TAB_THEME) {
			initThemeTabView(tab);
		} /* 待修改if (tab == Tab.TAB_LOCK){
			initLockTabView(tab);
		}*/ else if (tab.mFileTypes != null) {
			initDownloadTypeTabView(tab);
		}
	}

	private void initServiceAndReceiver() {
		mNewAppInstallReceiver = new NewAppInstallReceiver();
		IntentFilter itFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		itFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		itFilter.addDataScheme("package");
		registerReceiver(mNewAppInstallReceiver, itFilter);

		/**
		 * HCF
		IntentFilter refreshFilter = new IntentFilter(DyanmicWidgetReceiver.ACTION_ENABLE_PLUGIN);
		registerReceiver(mRefreshReceiver, refreshFilter);
		*/

		// 绑定下载服务
		mDownloadService = new DownloadServerServiceConnection(this);
		mDownloadService.bindDownloadService(new DownloadServiceBindCallBack());
	}

	/**
	 * 初始化主题页
	 */
	private void initLockTabView(Tab tab) {
		tab.mContainer = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.all_downloadmanager_theme_tab_container, null);
		tab.mListView = (ListView) tab.mContainer.findViewById(R.id.listView);
		tab.mListAdapter = new ThemeTabListAdaper(this);
		tab.mListView.setAdapter(tab.mListAdapter);
		tab.mNoDataView = ViewFactory.getNomalErrInfoView(this, tab.mContainer, ViewFactory.DOWNLOAD_NO_LOG_VIEW);

		loadThemeData();
	}
	
	/**
	 * 初始化主题页
	 */
	private void initThemeTabView(Tab tab) {
		tab.mContainer = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.all_downloadmanager_theme_tab_container, null);
		tab.mListView = (ListView) tab.mContainer.findViewById(R.id.listView);
		tab.mListAdapter = new ThemeTabListAdaper(this);
		tab.mListView.setAdapter(tab.mListAdapter);
		tab.mNoDataView = ViewFactory.getNomalErrInfoView(this, tab.mContainer, ViewFactory.DOWNLOAD_NO_LOG_VIEW);

		loadThemeData();
	}

	/**
	 * 初始化字体、铃声等通过下载管理下载的类型的页
	 */
	private void initDownloadTypeTabView(Tab tab) {
		RelativeLayout container = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.all_downloadmanager_soft_tab_container, null);
		tab.mContainer = container;
		tab.mClearBtn = (FooterView) container.findViewById(R.id.clear_btn);
		if (tab.mShowClear) {
			tab.mClearBtn.createSingleButton(getString(R.string.download_manage_clear), -1, new OnClickListener() {
				@Override
				public void onClick(View paramView) {
					confirmClear();
				}
			});
		} else {
			tab.mClearBtn.setVisibility(View.GONE);
		}
		tab.mListView = (ListView) container.findViewById(R.id.listView);
		tab.mListAdapter = new BaseDownloadAdapter(this, mDownloadService);
		tab.mListView.setAdapter(tab.mListAdapter);
		tab.mListView.setOnItemClickListener(this);
		tab.mLoadingView = ViewFactory.getNomalErrInfoView(this, container, ViewFactory.LOADING_DATA_INFO_VIEW);
		tab.mLoadingView.setVisibility(View.VISIBLE);
		tab.mNoDataView = ViewFactory.getNomalErrInfoView(this, container, ViewFactory.DOWNLOAD_NO_LOG_VIEW);
		tab.mNoDataView.setVisibility(View.GONE);
	}

	/**
	 * 加载已下载完成的主题
	 */
	private void loadThemeData() {
		Tab tab = Tab.TAB_THEME;
		if (tab.mListAdapter == null)
			return;

//		try {
//			ArrayList<DowningTaskItem> finishList = LocalAccessor.getInstance(this).getDowningTaskByState(DowningTaskItem.DownState_Finish);
//			((ThemeTabListAdaper) tab.mListAdapter).updateData(finishList);
//			((ThemeTabListAdaper) tab.mListAdapter).notifyDataSetChanged();
//		} catch (Exception e) {
//			e.printStackTrace();
//			tab.showNoData(true);
//		}
	}

	/**
	 * 复制一份数据
	 * 
	 * @param srcMap
	 * @return
	 */
	private void copyDonwloadInfo(Map<String, BaseDownloadInfo> srcMap) {
		if (srcMap == null)
			return;

		Tab[] tabs = getTabValues();
		for (Tab t : tabs) {
			if (t.mFileTypes != null) {
				t.mData = new ArrayList<BaseDownloadInfo>();
			}
		}

		Set<Entry<String, BaseDownloadInfo>> entrySet = srcMap.entrySet();
		for (Entry<String, BaseDownloadInfo> entry : entrySet) {
			BaseDownloadInfo newInfo = new BaseDownloadInfo(entry.getValue(),context);
			for (Tab t : tabs) {
				if (addToTab(t, newInfo))
					break;
			}
		}
	}

	/**
	 * 将BaseDownloadInfo添加到tab中
	 * 
	 * @return true表示添加成功
	 */
	private boolean addToTab(Tab tab, BaseDownloadInfo info) {
		if (tab.mFileTypes != null) {
			for (int i = 0; i < tab.mFileTypes.length; i++) {
				FileType type = tab.mFileTypes[i];
				if (type.getId() == info.getFileType()) {
					tab.mData.add(info);
					return true;
				}
			}
		}

		return false;
	}

	private synchronized void loadDownloadList() {
		if (!mDownloadService.isBind())
			return;

		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {

				copyDonwloadInfo(mDownloadService.getDownloadTasks());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						for (Tab t : getTabValues()) {
							if (t.mFileTypes == null || t.mListAdapter == null)
								continue;

							if (t.mData == null || t.mData.size() <= 0) {
								t.showNoData(true);
							} else {
								t.showNoData(false);
								((BaseDownloadAdapter) t.mListAdapter).addAll(t.mData);
								t.mListAdapter.notifyDataSetChanged();
							}
						}
					}
				});
			}
		});
	}

	private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null == intent)
				return;

//			String action = intent.getAction();
//			if (action.equals(DyanmicWidgetReceiver.ACTION_ENABLE_PLUGIN)) {
//				BaseAdapter adapter = Tab.TAB_APK.mListAdapter;
//				if (adapter != null) {
//					adapter.notifyDataSetChanged();
//				}
//			}
		}
	};

	/**
	 * 新应用安装监听
	 */
	private class NewAppInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				final String packageName = intent.getData().getSchemeSpecificPart();
				Tab tab = Tab.TAB_APK;
				if (packageName == null || tab.mData == null || tab.mListAdapter == null)
					return;

				boolean needRefresh = false;
				Iterator<BaseDownloadInfo> it = tab.mData.iterator();
				while (it.hasNext()) {
					BaseDownloadInfo info = it.next();
					if (info != null && packageName.equals(info.getPacakgeName(context))) {
						needRefresh = true;
						break;
					}
				}
				if (needRefresh) {
					tab.mListAdapter.notifyDataSetChanged();
				}
			} catch (Exception e) {
				Log.w(TAG, "NewAppInstallReceiver expose error!", e);
			}

		}// end onReceiver

	}// end class NewAppInstallReceiver

	/**
	 * 绑定下载服务的回调
	 */
	private class DownloadServiceBindCallBack implements CommonCallBack<Boolean> {

		@Override
		public void invoke(final Boolean... arg) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					boolean bindSuccess = false;
					if (arg != null && arg.length > 0)
						bindSuccess = arg[0].booleanValue();
					// Log.d(Global.TAG, "bind download service:"+bindSuccess);
					if (bindSuccess) {
						loadDownloadList();
					} else {
						// 绑定失败，当是数据加载失败了，因为后续的操作无法正常进行
						for (Tab t : getTabValues()) {
							if (t.mFileTypes == null)
								continue;

							t.showNoData(true);
						}
					}
				}
			});
		}

	}// end DownloadServiceBindCallBack

	class ThemeTabListAdaper extends BaseAdapter {

		private Context context;

		private ArrayList<DowningTaskItem> mData = new ArrayList<DowningTaskItem>();

		private AsyncImageLoader mAsyncImageLoader;

		ThemeTabListAdaper(Context ctx) {
			this.context = ctx;
			mAsyncImageLoader = new AsyncImageLoader();
		}

		ThemeTabListAdaper(Context ctx, ArrayList<DowningTaskItem> data) {
			this.context = ctx;
			if (data != null) {
				mData.addAll(data);
			}
			mAsyncImageLoader = new AsyncImageLoader();
		}

		/**
		 * 刷新主题
		 * 
		 * @param data
		 */
		public void updateData(ArrayList<DowningTaskItem> data) {
			if (data != null) {
				mData.clear();
				mData.addAll(data);
			}
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

			Tab.TAB_THEME.showNoData(getCount() == 0 ? true : false);
		}

		@Override
		public int getCount() {
			if (null == mData) {
				return 0;
			}
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			DowningTaskItemCache cache = null;
			if (null == convertView) {
				convertView = LayoutInflater.from(context).inflate(R.layout.all_downloadmanager_themetab_list_item, null);
				cache = new DowningTaskItemCache(convertView);
				convertView.setTag(cache);
			} else {
				cache = (DowningTaskItemCache) convertView.getTag();
			}

			final DowningTaskItem downingTaskItem = mData.get(position);

			cache.downingTaskItem = downingTaskItem;
			cache.themeTitle.setText(downingTaskItem.themeName);
			String strThemeVersion = String.format(getString(R.string.theme_shop_v2_manage_downtasks_theme_version_txt), downingTaskItem.themeVersion);
			cache.themeVersion.setText(strThemeVersion);
			// cache.themeDownloadBtn.setTag(cache);

			cache.downprocess_percent.setVisibility(View.INVISIBLE);
			cache.downprocess_horizontal.setVisibility(View.INVISIBLE);
			cache.themeDownTaskDeleteBtn.setVisibility(View.VISIBLE);

			cache.themeDownloadBtn.setText(R.string.theme_shop_v2_manage_downstate_finish);
			cache.themeDownloadBtn.setBackgroundResource(R.drawable.theme_shop_v2_manage_downtask_action_btn);
			cache.themeDownTaskDeleteBtn.setEnabled(true);

			cache.themeDownloadBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					/**
					 * HCF
					 * 
					String strResult = ThemeShopV2AppThemeTool.ShopV2InitApplyTheme(context, downingTaskItem.newThemeID, downingTaskItem.themeID, downingTaskItem.themeName,
							downingTaskItem.tmpFilePath);

					// ThemeShopV3LauncherExAPI.sendImportAndApplyAPK(context,
					// downingTaskItem.newThemeID);
					// 判断新主题ID是否为空，为空则通过表取值更新
					if (strResult != null) {
						downingTaskItem.newThemeID = strResult;
					}
					// 统计
					// HiAnalytics.submitEvent(context,
					// AnalyticsConstant.EVENT_THEME_SHOPV2_MANAGE_APPLY,
					// AnalyticsConstant.EVENT_THEME_SHOPV2_MANAGE_APPLY_TAG_APPLYTHEME);
					 *
					 */
				}
			});

			cache.themeDownTaskDeleteBtn.setTag(cache);
			cache.themeDownTaskDeleteBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					applyThemeDialog = createConfimDialog(downingTaskItem, mData, position, (ThemeTabListAdaper) Tab.TAB_THEME.mListAdapter);
					applyThemeDialog.show();

					// 统计
					// HiAnalytics.submitEvent(context,
					// AnalyticsConstant.EVENT_THEME_SHOPV2_MANAGE_DELETE,
					// AnalyticsConstant.EVENT_THEME_SHOPV2_MANAGE_DELETE_TAG_THEME);
				}
			});

			// 设置主题图片
			cache.themeLargeImg.setTag(downingTaskItem.picUrl);
			Drawable cachedImage = mAsyncImageLoader.loadDrawable(downingTaskItem.picUrl, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ListView listView = Tab.TAB_THEME.mListView;
					if (listView == null)
						return;
					ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
					if (imageViewByTag != null && imageDrawable != null) {
						imageViewByTag.setImageDrawable(imageDrawable);
						// 渐变
						AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
						aa.setDuration(500);// 设置动画执行的时间
						imageViewByTag.startAnimation(aa);
					}
				}
			});
			if (cachedImage == null) {
//				cache.themeLargeImg.setImageResource(R.drawable.theme_shop_v2_theme_no_find_small);
			} else {
				cache.themeLargeImg.setImageDrawable(cachedImage);
			}

			return convertView;
		}

		class DowningTaskItemCache {
			public ImageView themeLargeImg;
			public TextView themeTitle;
			public TextView themeVersion;
			public Button themeDownloadBtn;
			public Button themeDownTaskDeleteBtn;
			public DowningTaskItem downingTaskItem;
			public ProgressBar downprocess_horizontal;
			public TextView downprocess_percent;

			public DowningTaskItemCache(View view) {
				themeLargeImg = (ImageView) view.findViewById(R.id.themeLargeImg);
				themeTitle = (TextView) view.findViewById(R.id.themeTitle);
				themeVersion = (TextView) view.findViewById(R.id.themeVersion);
				themeDownloadBtn = (Button) view.findViewById(R.id.themeDownloadBtn);
				themeDownTaskDeleteBtn = (Button) view.findViewById(R.id.themeDownTaskDeleteBtn);
				downprocess_horizontal = (ProgressBar) view.findViewById(R.id.downprocess_horizontal);
				downprocess_percent = (TextView) view.findViewById(R.id.downprocess_percent);
			}
		}
	}

	public Dialog createConfimDialog(final DowningTaskItem downingTaskItem, final ArrayList<DowningTaskItem> mListDowningTask, final int iPosition, final ThemeTabListAdaper mDownAdapter) {
		return ViewFactory.getAlertDialog(this, context.getString(R.string.common_tip), context.getString(R.string.alert_dialog_confim_del), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				// 删除硬盘临时文件
				if (downingTaskItem.tmpFilePath != null) {
					File tempFile = new File(downingTaskItem.tmpFilePath);
					if (tempFile.exists()) {
						tempFile.delete();
					} else {
						// 已下载完成的情况
						String diskFile = downingTaskItem.tmpFilePath;
						if (diskFile.endsWith(".temp")) {
							diskFile = downingTaskItem.tmpFilePath.substring(0, diskFile.indexOf(".temp"));
						}
						File downFile = new File(diskFile);
						if (downFile.exists()) {
							downFile.delete();
						}
					}
				}

				/**
				 * HCF
				 
				// 删除数据库信息
				try {
					LocalAccessor.getInstance(context).deleteDowningTask(downingTaskItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				*/

				// 动态移除列表信息
				mListDowningTask.remove(iPosition);
				mDownAdapter.notifyDataSetChanged();

				/**hcf
				 * 
				// 发送广播到桌面删除主题.
				if (downingTaskItem.state == DowningTaskItem.DownState_Finish) {
					ThemeShopV3LauncherExAPI.sendDeleteThemeAptAndApk(context, downingTaskItem.newThemeID);
				}
				// 发送广播通知主题商城删除记录
				Intent intent = new Intent(ThemeShopV3LauncherExAPI.THEME_DELETE_DOWN_LOG_ACTION);
				intent.putExtra("themeid", downingTaskItem.newThemeID + "");
				intent.addFlags(32);
				context.sendBroadcast(intent);
				**/
			}
		});
	}

	// 确认清除下载记录操作
	private void confirmClear() {
		ViewFactory.getAlertDialog(this, getString(R.string.download_delete_title), getString(R.string.download_task_clear), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除下载记录
				dialog.dismiss();
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {

						/**
						 * 依次删除下载
						 */
						int index = container.getPagerTab().getSelectedTab();
						final Tab tab = Tab.fromIndex(index);
						if (tab.mFileTypes == null)
							return;
						final BaseDownloadAdapter adapter = (BaseDownloadAdapter) tab.mListAdapter;
						if (adapter == null || adapter.getCount() <= 0)
							return;

						int count = adapter.getCount();
						for (int i = 0; i < count; i++) {
							BaseDownloadInfo info = (BaseDownloadInfo) adapter.getItem(i);
							if (info == null) {
								continue;
							}
							mDownloadService.cancel(info.getIdentification());
						}

						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (adapter != null) {
									adapter.clear();
									adapter.notifyDataSetChanged();
									tab.showNoData(true);
								}
							}
						});
					}
				});
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = container.getPagerTab().getSelectedTab();
		final Tab tab = Tab.fromIndex(index);
		if (tab.mFileTypes == null)
			return;
		final BaseDownloadAdapter adapter = (BaseDownloadAdapter) tab.mListAdapter;
		if (adapter == null || adapter.getCount() - 1 < position)
			return;

		final BaseDownloadInfo baseInfo = (BaseDownloadInfo) adapter.getItem(position);
		final String msg = getString(R.string.download_delete_msg, baseInfo.getTitle());
		ViewFactory.getAlertDialog(this, getString(R.string.download_delete_title), msg, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mDownloadService.cancel(baseInfo.getIdentification());
				adapter.remove(baseInfo);
				adapter.notifyDataSetChanged();
				if (adapter.getCount() == 0) {
					tab.showNoData(true);
				}
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	private static Tab[] getTabValues() {
		return Tab.values();
	}
}
