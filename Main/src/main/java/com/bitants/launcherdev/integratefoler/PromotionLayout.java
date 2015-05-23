package com.bitants.launcherdev.integratefoler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.bitants.launcher.R;
import com.bitants.launcherdev.integratefoler.model.PromotionAppInfo;
import com.bitants.common.launcher.info.ApplicationInfo;

import java.util.ArrayList;
import java.util.List;

public class PromotionLayout extends ViewGroup {

	private final String TAG = "PromotionLayout";
	private PromotionGridView gridview;
	private LinearLayout topLayout;

	private Context context;

	public PromotionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public PromotionLayout(Context context) {
		super(context, null);
		this.context = context;
	}

	public static PromotionLayout fromXml(Context context, ViewGroup viewGroup, List<ApplicationInfo> appList) {
		PromotionLayout layout = (PromotionLayout) LayoutInflater.from(context).inflate(R.layout.user_folder_promotion_layout, viewGroup, false);
		return layout;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		gridview = (PromotionGridView) findViewById(R.id.promotion_gridview);

		final List<PromotionAppInfo> list = new ArrayList<PromotionAppInfo>();
		for (int i = 0; i < 20; i++) {
			PromotionAppInfo info = new PromotionAppInfo();
			info.name = "item" + i;
			list.add(info);
		}

		gridview.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				if (convertView == null) {
					View view = LayoutInflater.from(context).inflate(R.layout.user_folder_promotion_gridview_item, null);
					holder = new ViewHolder();
					holder.iconImg = (ImageView) view.findViewById(R.id.icon_img);
					holder.nameTv = (TextView) view.findViewById(R.id.name_tv);
					view.setTag(holder);
					convertView = view;
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.iconImg.setImageResource(R.drawable.ic_launcher);
				holder.nameTv.setText(list.get(position).name);
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return list.get(position);
			}

			@Override
			public int getCount() {
				return list.size();
			}

			class ViewHolder {
				public ImageView iconImg;
				public TextView nameTv;
			}

		});

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.e(TAG, "onItemClick:position:" + position);
			}
		});

		topLayout = (LinearLayout) findViewById(R.id.promotion_top_bar);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		// topLayout.layout(0, 0, topLayout.getMeasuredWidth(),
		// topLayout.getMeasuredHeight());
		// gridview.layout(l, t, r, b);

		int count = getChildCount();
		int left = 0;
		int top = 0;
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				int width = view.getMeasuredWidth();
				int height = view.getMeasuredHeight();
				view.layout(left, top, left + width, top + height);
				top = top + height;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeigth);
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			int widthSpec = 0;
			int heightSpec = 0;
			LayoutParams params = v.getLayoutParams();
			if (params.width > 0) {
				widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
			} else if (params.width == -1) {
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY);
			} else if (params.width == -2) {
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
			}
			if (params.height > 0) {
				heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
			} else if (params.height == -1) {
				heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth, MeasureSpec.EXACTLY);
			} else if (params.height == -2) {
				heightSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST);
			}
			v.measure(widthSpec, heightSpec);
		}
	}

}
