package com.nd.launcherdev.framework.view.commonsliding;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.launcherdev.theme.assit.ThemeUIRefreshListener;
import com.nd.launcherdev.theme.assit.ThemeUIRefreshListener;

/**
 * 类似4.0桌面的屏幕指示灯
 */
public class BaseLineLightbar extends View implements ThemeUIRefreshListener {
	private int size, hlLineWidth, lineHeight, hlLineHeight, lineTop, hlLineTop;
	private float distanceScale;
	private Drawable lines[], hlLine;
	private int width, height;
	private CommonSlidingView slidingView;

	public BaseLineLightbar(Context context) {
		super(context);
		init();
	}

	public BaseLineLightbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BaseLineLightbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	protected void init() {
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);

		lineTop = (height - lineHeight) / 2;
		hlLineTop = (height - hlLineHeight) / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (lines[0] != null) {
			lines[0].setBounds(0, lineTop, width, lineTop + lineHeight);
			lines[0].draw(canvas);
		}
		if (slidingView != null) {
			List<ICommonData> list = slidingView.getList();
			if (list != null && list.size() > 1) {
				for (int i = 1; i < list.size(); i++) {
					if (i > lines.length - 1) {
						break;
					}
					if (lines[i] == null) {
						continue;
					}
					ICommonData data = list.get(i);
					int[] pageInfo = slidingView.getDataPageInfo(data);
					lines[i].setBounds((int) (pageInfo[0] * width * distanceScale), lineTop, (int) (pageInfo[1] * width * distanceScale), lineTop + lineHeight);
					lines[i].draw(canvas);
				}
			}
		}

		hlLine.draw(canvas);
	}

	/**
	 * 刷新
	 * @param slidingView
	 * @param scrollX x轴偏移
	 */
	public void refresh(CommonSlidingView slidingView, int scrollX) {
		this.slidingView = slidingView;
		this.size = slidingView.getPageCount();
		distanceScale = 1.0f / this.size;

		hlLineWidth = width / this.size;

		update(scrollX);
	}

	/**
	 * 更新
	 * @param scrollX x轴偏移
	 */
	public void update(int scrollX) {
		if (slidingView != null && slidingView.getWidth() != 0) {
			final int left = (int) (scrollX * ((float) width / slidingView.getWidth()) * distanceScale);
			hlLine.setBounds(left, hlLineTop, left + hlLineWidth, hlLineTop + hlLineHeight);
			invalidate();
		}
	}

	/**
	 * 设置指示灯的图片
	 * @param lines 图片列表
	 */
	public void setLines(Drawable[] lines) {
		if (lines == null)
			return;
		this.lines = new Drawable[lines.length];
		for (int i = 0; i < lines.length; i++) {
			this.lines[i] = lines[i];
		}
		lineHeight = lines[0].getIntrinsicHeight();
	}

	/**
	 * 设置高亮的图片
	 * @param hlLine 图片
	 */
	public void setHighLine(Drawable hlLine) {
		if (hlLine == null)
			return;
		this.hlLine = hlLine;
		hlLineHeight = hlLine.getIntrinsicHeight();
	}

	/**
	 * 应用主题
	 */
	@Override
	public void applyTheme() {
	}
	
}
