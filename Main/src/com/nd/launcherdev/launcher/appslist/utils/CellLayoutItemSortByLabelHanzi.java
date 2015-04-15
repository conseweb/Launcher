package com.nd.launcherdev.launcher.appslist.utils;

import java.text.Collator;
import java.util.Comparator;

public class CellLayoutItemSortByLabelHanzi implements Comparator<CellLayoutItem> {

	public final static Comparator<CellLayoutItem> SORT_BY_HANZI_NAME = new CellLayoutItemSortByLabelHanzi();

	private Collator mCollator;

	@Override
	public int compare(CellLayoutItem lhs, CellLayoutItem rhs) {
		if (lhs == rhs) {
			return 0;
		}

		if (mCollator == null) {
			mCollator = Collator.getInstance();
		}

		String aStr = lhs.getName();
		String bStr = rhs.getName();
		String aPinyin = PinyinHelper.getPinYin(aStr);
		String bPinyin = PinyinHelper.getPinYin(bStr);

		int result = mCollator.compare(aPinyin, bPinyin);
		return result;
	}
}
