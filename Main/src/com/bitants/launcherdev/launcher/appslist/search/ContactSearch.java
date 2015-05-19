package com.bitants.launcherdev.launcher.appslist.search;

import com.bitants.launcherdev.launcher.appslist.utils.CellLayoutItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactSearch {
	/**
	 * 搜索模式
	 */
	private interface SearchMode {
		public static final int MODE_PY = 0; // 按拼音搜索
		public static final int MODE_NUMBER = 1; // 按数字搜索
	}

	/**
	 * 匹配联系人和搜索关键字
	 * 
	 * @param key
	 * @param contact
	 * @return
	 */
	public static MatchType match(String key, CellLayoutItem contact) {
		MatchType type = MatchType.TYPE_NO_MATCH;
		int start = -1;

		// // 从号码中匹配
		// if (!StringUtil.isEmpty(contact.number) &&
		// StringUtil.replaceBlank(contact.number).contains(StringUtil.replaceBlank(key)))
		// {
		// return MatchType.TYPE_NUMBER_MATCH;
		// }

		if (StringUtil.isEmpty(contact.getName()))
			return MatchType.TYPE_NO_MATCH;

		// 在姓名中执行精确匹配
		if ((start = contact.getName().indexOf(key)) != -1) {
			contact.matchIndex = start;
			contact.matchKey = key;
			contact.matchLength = key.length();
			contact.matchType = MatchType.TYPE_NAME_PRECISION_MATCH;
			return MatchType.TYPE_NAME_PRECISION_MATCH;
		}

		// 在姓名中执行模糊匹配
		if (isFuzzyMatch(key.trim(), contact, SearchMode.MODE_PY)) {
			contact.matchType = MatchType.TYPE_NAME_FUZZY_MATCH;
			return MatchType.TYPE_NAME_FUZZY_MATCH;
		}

		return type;
	}

	/**
	 * 是否模糊匹配
	 * 
	 * @param contact
	 * @param key
	 * @param mode
	 *            1代表py搜索，2代表数字搜索
	 * @return
	 */
	private static boolean isFuzzyMatch(String key, CellLayoutItem contact, int searchMode) {
		if (StringUtil.isEmpty(contact.getName()) || StringUtil.isEmpty(key))
			return false;

		int start = -1, matchLen = 0, startPy = 0;
		String shortStr = (searchMode == SearchMode.MODE_PY) ? contact.shortPy : contact.shortPyNumber; // 简拼字串

		// 简拼中匹配关键字
		if ((start = shortStr.indexOf(key)) != -1) {
			contact.matchIndex = start;
			contact.matchKey = key;
			contact.matchLength = key.length();
			return true;
		}

		char firstChar = key.charAt(0);
		String patternStr = Character.toString(firstChar);
		if (StringUtil.isSpecialChar(firstChar)) { // 第一个字符为特殊字符，需加[]
			patternStr = "[" + Character.toString(firstChar) + "]";
		}

		// 第一个字符为中文字符，则从姓名中查找中文出现的位置；如果第一个字符为英文字符，则从简拼中查找英文字符出现的位置
		String searchStr = StringUtil.isChineseChar(firstChar) ? contact.getName() : shortStr;

		Matcher matcher = Pattern.compile(patternStr).matcher(searchStr);
		while (matcher.find()) {
			// 记录首个字符出现的位置
			startPy = matcher.start();

			matchLen = pyEntityMatch(contact, key, startPy, searchMode);
			if (matchLen > 0)
				break;
		}

		if (matchLen > 0) {
			contact.matchIndex = startPy;
			contact.matchLength = matchLen;
			contact.matchKey = key;
			return true;
		}

		return false;
	}

	/**
	 * 使用联系人拼音属性和关键字做匹配
	 * 
	 * @param contact
	 * @param key
	 * @param startPy
	 *            起始拼音
	 * @param searchMode
	 * @return
	 */
	private static int pyEntityMatch(CellLayoutItem contact, String key, int startPy, int searchMode) {
		PyEntity[] entities = contact.pyEntities;
		if (entities == null)
			return 0;

		PyEntity entity = null;
		int matchLen = 0;
		int pyLen = entities.length;
		int keyLen = key.length();
		int i = startPy;
		int keyIndex = 0; // key的下标
		int lastWholeIndex; // 上一个拼音全拼被命中，但未使用的拼音下标
		String py = null;

		// 起始位置大于拼音数组长度
		if (startPy >= pyLen)
			return 0;

		for (; i < pyLen && keyIndex < keyLen; i++) {
			// 拼音属性
			entity = entities[i];
			if (entity == null)
				continue;

			// 拼音
			py = (searchMode == SearchMode.MODE_PY) ? entity.py : entity.pyNumber;
			if (StringUtil.isEmpty(py))
				continue;

			// 当前拼音为特殊字符，且当前key为汉字或字母，则跳过该拼音
			if (StringUtil.isSpecialChar(entity.srcChar) && !StringUtil.isSpecialChar(key.charAt(keyIndex))) {
				entity.hitWhole = false;
				entity.wholeUsed = false;
				entity.hitShort = false;
				continue;
			}

			if (StringUtil.isChineseChar(key.charAt(keyIndex))) {
				/**
				 * 如果中文字符和拼音属性匹配，则认为该拼音属性被命中。
				 * 否则在拼音起止位置和当前位置之间查找未使用的拼音，且重置拼音和key的下标
				 */
				if (entity.srcChar == key.charAt(keyIndex)) {
					entity.hitWhole = true;
					entity.wholeUsed = true;

					keyIndex++;
				} else {
					entity.hitWhole = false;
					entity.wholeUsed = false;
					lastWholeIndex = getLastWholeIndex(contact, startPy, i);
					if (lastWholeIndex == -1) {
						break;
					}

					// 重置拼音和Key的下标
					i = lastWholeIndex;
					keyIndex = resetKeyIndex(entities[lastWholeIndex], searchMode);
				}

				continue;
			}

			// 判断拼音全拼是否匹配
			if (key.startsWith(py, keyIndex)) {
				entity.hitWhole = true;
				entity.wholeUsed = false;
			} else {
				entity.hitWhole = false;
				entity.wholeUsed = false;
			}

			if (py.charAt(0) == key.charAt(keyIndex)) { // 首字母匹配
				entity.hitIndex = keyIndex;
				entity.hitShort = true;
				keyIndex++;

				if ((i + 1) == pyLen) { // 已经是最后一个拼音
					if (py.startsWith(key.substring(keyIndex - 1))) { // 拼音包含剩余key，代表key被完全使用
						keyIndex = keyLen;
					} else {
						// 获取上一个未使用的全拼拼音，并重置拼音和key的下标
						lastWholeIndex = getLastWholeIndex(contact, startPy, i);
						if (lastWholeIndex == -1) {
							break;
						}

						// 重置拼音和Key的下标
						i = lastWholeIndex;
						keyIndex = resetKeyIndex(entities[lastWholeIndex], searchMode);
					}
				}
			} else {
				entity.hitShort = false;
				lastWholeIndex = getLastWholeIndex(contact, startPy, i);
				if (lastWholeIndex == -1) {
					break;
				}

				// 重置拼音和Key的下标
				i = lastWholeIndex;
				keyIndex = resetKeyIndex(entities[lastWholeIndex], searchMode);

			}
		}

		if (keyIndex >= keyLen) { // key使用完，代表所有key的字符都已被匹配
			matchLen = (i - startPy);
		} else {
			for (int k = (i - 1); k >= startPy; k--) { // key未使用完，则判断key剩余的部分是不是某个拼音的部分内容
				if (entities[k].hitWhole)
					break;

				if (!entities[k].hitShort)
					continue;

				py = (searchMode == SearchMode.MODE_PY) ? entities[k].py : entities[k].pyNumber;
				if (py.startsWith(key.substring(entities[k].hitIndex))) {
					matchLen = (k - startPy + 1);
					break;
				}
			}
		}

		return matchLen;
	}

	/**
	 * 获取上一个拼音全拼匹配，且未使用的拼音下标
	 * 
	 * @param contact
	 * @param start
	 * @param end
	 * @return
	 */
	private static int getLastWholeIndex(CellLayoutItem contact, int start, int end) {
		int index = -1;
		PyEntity[] entities = contact.pyEntities;

		for (int i = end; i >= start; i--) {
			if (entities[i].hitWhole && !entities[i].wholeUsed) { // 命中全拼，但未使用全拼
				index = i;
				entities[i].wholeUsed = true;
				break;
			}
		}

		return index;
	}

	/**
	 * 重置Key下标
	 * 
	 * @param entity
	 * @param searchMode
	 * @return
	 */
	private static int resetKeyIndex(PyEntity entity, int searchMode) {
		int index = 0;

		if (searchMode == SearchMode.MODE_PY) {
			index = entity.hitIndex + entity.py.length();
		} else {
			index = entity.hitIndex + entity.pyNumber.length();
		}

		return index;
	}
}
