package com.bitants.launcherdev.launcher.appslist.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.appslist.search.ContactSearch;
import com.bitants.launcherdev.launcher.appslist.search.MatchType;
import com.bitants.launcherdev.launcher.appslist.utils.CellLayoutItem;
import com.bitants.launcherdev.launcher.appslist.utils.CellLayoutItemSortByLabelHanzi;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.view.icon.ui.impl.AppMaskTextView;
import com.bitants.launcherdev.util.ActivityActionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AllappsListview extends RelativeLayout implements OnScrollListener {
	private ListView mAppsList;
	private ScrollAlphalbetView mAlpalbetView;
	public final static int NUM_PER_LINE = 4;
	private Context mContext;
	private AppsListAdapter appsAdapter;
	private ArrayList<CellLayoutItem> mAllApplist;
	private ArrayList<CellLayoutItem> mShowApplist;
	private DXSplitAppList mDxSplitAppList;
	private RelativeLayout mShowAppsLayout;
	private LinearLayout mSearchAppsLayout;
	private ImageView mIvDeleteText;
	private EditText mEtSearch;
	private GridView mSearchGridView;
	private WindowManager mWManger;
	private float mGridCellLenth = 100;
	private boolean isFirstLoadGridview = true;
	private GridviewAdapter mGridviewAdapter;

	public AllappsListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mWManger = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dd = new DisplayMetrics();
		mWManger.getDefaultDisplay().getMetrics(dd);
		mGridCellLenth = dd.heightPixels / 6;
	}

	public AllappsListview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setHapticFeedbackEnabled(false);
		this.mContext = context;
		mAllApplist = new ArrayList<CellLayoutItem>();
		mShowApplist = new ArrayList<CellLayoutItem>();

	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		dxSetupScrollView();
	}

	@SuppressLint("NewApi")
	public void dxSetupScrollView() {

		mAppsList = (ListView) this.findViewById(R.id.apps_listview);
		mAlpalbetView = (ScrollAlphalbetView) this.findViewById(R.id.scrollView);
		mSearchAppsLayout = (LinearLayout) this.findViewById(R.id.searchAppsLayout);
		mShowAppsLayout = (RelativeLayout) this.findViewById(R.id.showAppsLayout);
		mIvDeleteText = (ImageView) findViewById(R.id.ivDeleteText);
		mEtSearch = (EditText) findViewById(R.id.etSearch);
		mSearchGridView = (GridView) this.findViewById(R.id.gridview);
		// try {
		// mAppsList.setOverScrollMode(View.OVER_SCROLL_NEVER);
		// } catch (Exception e) {
		//
		// }
		Configuration cf = this.getResources().getConfiguration();
		if (cf.orientation == Configuration.ORIENTATION_LANDSCAPE && mAlpalbetView != null) {
			mAlpalbetView.setVisibility(View.GONE);
			mAlpalbetView = null;
		}

		if (mAlpalbetView == null) {
			return;
		}
		mAlpalbetView.ConfigView(mAppsList, null);
		mIvDeleteText.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mEtSearch.setText("");
			}
		});
		mEtSearch.addTextChangedListener(watcher);
		mSearchGridView.setOnItemClickListener(searchGrid);
		mGridviewAdapter = new GridviewAdapter();
	}

	private OnItemClickListener searchGrid = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			ApplicationInfo cell = (ApplicationInfo) view.getTag();
			ActivityActionUtil.startActivitySafelyForRecored(view, mContext, cell.intent);
		}
	};
	private TextWatcher watcher = new TextWatcher() {

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!s.equals("") && s.length() > 0)
				new SearchTask(s.toString().toLowerCase()).execute();
			else {
				mShowAppsLayout.setVisibility(View.VISIBLE);
				mSearchAppsLayout.setVisibility(View.GONE);
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				mIvDeleteText.setVisibility(View.GONE);
			} else {
				mIvDeleteText.setVisibility(View.VISIBLE);
			}
		}
	};

	class SearchTask extends AsyncTask<String, Integer, ArrayList<CellLayoutItem>> {
		protected long taskSN;
		protected String input;
		protected int target;

		public SearchTask(String input) {
			super();
			this.input = input;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected ArrayList<CellLayoutItem> doInBackground(String... params) {
			return search();
		}

		@Override
		protected void onPostExecute(ArrayList<CellLayoutItem> result) {
			if (result.size() >= 0) {
				if (isFirstLoadGridview) {
					mGridviewAdapter.setApplist(result);
					mSearchGridView.setAdapter(mGridviewAdapter);
					isFirstLoadGridview = false;
				} else {
					mGridviewAdapter.setApplist(result);
					mGridviewAdapter.notifyDataSetChanged();
				}
				mShowAppsLayout.setVisibility(View.GONE);
				mSearchAppsLayout.setVisibility(View.VISIBLE);
			}

		}

		private ArrayList<CellLayoutItem> search() {
			ArrayList<CellLayoutItem> result = new ArrayList<CellLayoutItem>();
			for (int i = 0; i < mAllApplist.size(); i++) {
				MatchType type = ContactSearch.match(input, mAllApplist.get(i));
				if (type != MatchType.TYPE_NO_MATCH) {
					result.add(mAllApplist.get(i));
				}
			}
			return result;
		}

	}

	private class GridviewAdapter extends BaseAdapter {
		List<CellLayoutItem> list;

		public GridviewAdapter() {

		}

		public void setApplist(List<CellLayoutItem> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CellLayoutItem cellItem = list.get(position);
			ApplicationInfo info = cellItem.getAppInfo();
			AppMaskTextView itv = new AppMaskTextView(mContext);
			itv.setText(info.title);
			itv.setTag(info);
			itv.setIconBitmap(info.iconBitmap);
			itv.setLazy(info.usingFallbackIcon);
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) mGridCellLenth);
			itv.setLayoutParams(lp);
			return itv;
			// return appView;
		}
	}

	public class AppsListAdapter extends BaseAdapter implements SectionIndexer {
		public AppItemSectionIndexer mIndexer;
		private Context mContext;
		public AppsListAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mIndexer == null || mIndexer.mStartPositions == null ? 0 : mIndexer.mStartPositions.length;// 将每一行看作一个item
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppListItemView view;
			BubbleTextView[] views;

			final int startPos = mIndexer.mStartPositions[position];// 每一行的第一个应用程序的位置
			// 当listview到达最底端时，最后一行起始终止位置为appslist.size();
			final int endPos = position < mIndexer.mStartPositions.length - 1 ? mIndexer.mStartPositions[position + 1] : mShowApplist.size();// 当行最后一个应用程序的位置
			if (convertView == null) {
				view = (AppListItemView) LayoutInflater.from(getContext()).inflate(R.layout.dx_appslist_item, null);
				// 点击事件
				view.setOnClickListener(mEmptyListener);
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) mGridCellLenth);
				view.setLayoutParams(lp);
				views = new BubbleTextView[NUM_PER_LINE];
				for (int i = 0; i < NUM_PER_LINE; i++) {
					views[i] = view.getPhotoView(i);
				}
				view.setTag(views);
			} else {
				view = (AppListItemView) convertView;
				views = (BubbleTextView[]) view.getTag();
			}

			for (int i = 0; i < NUM_PER_LINE; i++) {
				// 断开之前的关联关系
				if (views[i].getTag() instanceof CellLayoutItem) {
					((CellLayoutItem) views[i].getTag()).setView(null);
					views[i].setTag(null);
				}
				int pos = startPos + i;
				if (pos < endPos) {// listview还没滑到最低端时
					views[i].setVisibility(View.VISIBLE);
					CellLayoutItem cellitem = mShowApplist.get(pos);// 这个CellLayoutItem是否是经过排序之后的，答案是！
					ApplicationInfo info = cellitem.getAppInfo();
					views[i].setTextViewTitle(info.title.toString());
					views[i].setBubbleTextIcon(cellitem.getIcon());
					views[i].setTag(info);
					views[i].setOnClickListener(mOnClickListener);
				} else {
					views[i].setVisibility(View.INVISIBLE);
				}
			}
			bindSectionHeader(view, startPos, true);
			return view;

		}

		private View.OnClickListener mEmptyListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		};

		// 当行显示所有图标，包括字母
		private void bindSectionHeader(View itemView, int position, boolean displaySectionHeaders) {
			final AppListItemView view = (AppListItemView) itemView;
			if (!displaySectionHeaders) {
				view.getHeaderView().setAlphalBet(null);
				view.setDividerVisible(true);
			} else {
				// 获取指点位置所在的字母的位置
				final int section = getSectionForPosition(position);

				// 当当前的view所在的位置是字母的开头时
				if (getPositionForSection(section) == position) {
					String title = (String) mIndexer.getSections()[section];

					view.getHeaderView().setAlphalBet(title);

				} else {
					view.getHeaderView().setAlphalBet(null);
				}
			}
		}

		// 在getView执行之前调用，在setapps（）中调用
		protected void updateIndexer(DXSplitAppList cursor) {
			Bundle bundle = cursor.getExtras();
			if (bundle.containsKey("titles")) {
				String sections[] = bundle.getStringArray("titles");
				int counts[] = bundle.getIntArray("counts");
				mIndexer = new AppItemSectionIndexer(sections, counts);
			} else {
				mIndexer = null;
			}

			// 用于右边字母类表的配置
			dxEndLoadingScrollView();
		}

		private void dxEndLoadingScrollView() {
			if (mAppsList == null || mDxSplitAppList == null) {
				return;
			}
			String[] sections = null;
			int[] counts = null;
			Bundle bundle = mDxSplitAppList.getExtras();
			if (bundle.containsKey("titles")) {

				sections = bundle.getStringArray("titles");// 获取A-Z的数组
				counts = bundle.getIntArray("counts");// A-Z各自所对应的应用程序数量
			}

			if (sections == null) {
				sections = new String[0];
				counts = new int[0];
			}

			boolean visible = false;

			if (sections.length > 0) {
				visible = true;
			}
			// DXSplitAppList.getAlphaBetCount28()获取首个应用程序在总的当中的位置
			mAlpalbetView.ConfigData(sections, DXSplitAppList.getAlphaBetCounts29(sections, counts, true), counts, visible);
		}

		@Override
		public Object[] getSections() {
			if (mIndexer == null) {
				return new String[] { " " };
			} else {
				return mIndexer.getSections();
			}
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			if (mIndexer == null) {
				return -1;
			}

			return mIndexer.getPositionForSection(sectionIndex);
		}

		@Override
		public int getSectionForPosition(int position) {

			if (mIndexer == null) {
				return -1;
			}

			return mIndexer.getSectionForPosition(position);
		}
	}

	public class AppItemSectionIndexer implements SectionIndexer {
		private final String[] mSections;// A-Z的数组
		private final int[] mPositions;// A-Z排序中第一个在总应用程序中的位置
		private final int mCount;// 应用程序总数
		private final int[] mStartPositions; // A-Z每一行的第一个应用程序的位置

		/**
		 * Constructor.
		 * 
		 * @param sections
		 *            a non-null array
		 * @param counts
		 *            a non-null array of the same size as <code>sections</code>
		 */
		public AppItemSectionIndexer(String[] sections, int[] counts) {
			if (sections == null || counts == null) {
				throw new NullPointerException();
			}

			if (sections.length != counts.length) {
				throw new IllegalArgumentException("The sections and counts arrays must have the same length");
			}

			this.mSections = sections;
			mPositions = new int[counts.length];
			int position = 0;
			int lineCount = 0;
			ArrayList<Integer> startArrays = new ArrayList<Integer>();
			for (int i = 0; i < counts.length; i++) {
				if (mSections[i] == null) {
					mSections[i] = " ";
				} else {
					mSections[i] = mSections[i].trim();
				}

				mPositions[i] = position;
				position += counts[i];
				int lines = (counts[i] % NUM_PER_LINE == 0 ? counts[i] / NUM_PER_LINE : counts[i] / NUM_PER_LINE + 1);
				lineCount += lines;
				for (int j = 0; j < lines; j++) {
					int refPosition = mPositions[i] + j * NUM_PER_LINE;
					startArrays.add(refPosition);
				}
			}
			mCount = position;
			int startPositions[] = new int[lineCount];
			for (int i = 0; i < startPositions.length; i++) {
				startPositions[i] = startArrays.get(i);
			}
			mStartPositions = startPositions;
		}

		public Object[] getSections() {
			return mSections;
		}

		public int getRealPosition(int position) {
			int originalPosition = position;
			int length = mStartPositions.length;
			for (int i = 0; i < length - 1; i++) {
				if (originalPosition >= mStartPositions[i] && originalPosition < mStartPositions[i + 1]) {
					return i;
				}
			}
			if (originalPosition >= mStartPositions[length - 1]) {
				return length - 1;
			}
			return -1;
		}

		public int getPositionForSection(int section) {
			if (section < 0 || section >= mSections.length) {
				return -1;
			}

			return mPositions[section];
		}

		public int getSectionForPosition(int position) {
			if (position < 0 || position >= mCount) {
				return -1;
			}

			int index = Arrays.binarySearch(mPositions, position);// 通过查找每行第一个所在的位置来确定

			return index >= 0 ? index : -index - 2;
		}
	}

	public void setApps(ArrayList<CellLayoutItem> list, ArrayList<CellLayoutItem> recentList) {
		if (null == list)
			list = new ArrayList<CellLayoutItem>();
		if (null == recentList)
			recentList = new ArrayList<CellLayoutItem>();
		int recentLenght = recentList == null ? 0 : recentList.size();
		if (list.size() > 0) {
			Collections.sort(list, CellLayoutItemSortByLabelHanzi.SORT_BY_HANZI_NAME);
			list = DXSplitAppList.sortByAppsFistName(list);
		}
		mAllApplist = list;
		for (CellLayoutItem cell : list) {
			recentList.add(cell);
		}
		mShowApplist = recentList;
		dxSetLoadingScrollView();
		appsAdapter = new AppsListAdapter(getContext());
		mDxSplitAppList = new DXSplitAppList(mContext, mShowApplist, recentLenght);
		appsAdapter.updateIndexer(mDxSplitAppList);
		mAppsList.setAdapter(appsAdapter);
		mAlpalbetView.setBackground(0, 5, true);
		// 以下用于滚动监听
		mAppsList.setOnScrollListener(this);
	}

	private void dxSetLoadingScrollView() {
		if (mAlpalbetView != null) {
			mAlpalbetView.setLoading();
		}
	}

	// 通过点击启动应用程序
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ApplicationInfo cell = (ApplicationInfo) v.getTag();
			ActivityActionUtil.startActivitySafelyForRecored(v, mContext, cell.intent);
		}
	};

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
		if (appsAdapter != null && appsAdapter.mIndexer != null) {

			if (lastVisibleItem > 0) {
				int firstPos = appsAdapter.mIndexer.mStartPositions[ScrollAlphalbetView.isListScroll() ? firstVisibleItem : firstVisibleItem + 1];
				int lastPos = appsAdapter.mIndexer.mStartPositions[lastVisibleItem];
				int first = appsAdapter.getSectionForPosition(firstPos);
				int last = appsAdapter.getSectionForPosition(lastPos);
				if (ScrollAlphalbetView.isListScroll() && mAlpalbetView.getChoose() != -1) {
					mAlpalbetView.setChoose(-1);
				}
				mAlpalbetView.setBackground(first, last, true);

			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getKeyCode();
		Log.i("onKeyDown", "onKeyDown");
		if (KeyEvent.KEYCODE_BACK == action && mSearchAppsLayout.getVisibility() == View.VISIBLE) {
			mEtSearch.setText("");
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

}
