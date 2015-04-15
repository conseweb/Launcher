package com.bitants.launcherdev.kitset.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;

/**
 * 日期工具类
 */
public class DateUtil {
	
	public static final String SHORT_DATE_FORMAT_1 = "yyyy年MM月dd日";
	
	public static final String SHORT_DATE_FORMAT_2 = "yyyy-MM-dd";
	
	/**
	 * 24小时制
	 */
	public static final String C_TIME_PATTON_24HHMM = "HH:mm";
	
	/**
	 * 12小时制
	 */
	public static final String C_TIME_PATTON_12HHMM = "hh:mm";
	
	/**
	 * 中文月份数组
	 */
	public static final String[] monthsZh = {"1月","2月","3月","4月","5月","6月"
		,"7月","8月","9月","10月","11月","12月"};
	
	/**
	 * 英文月份数组
	 */
	public static final String[] monthsEn = {"Jan","Feb","Mar","Apr","May","Jun"
		,"Jul","Aug","Sep","Oct","Nov","Dec"};

    /**
     * 获取现在时间
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }
    
    /**
     * 获取现在时间
     * @param format 时间字符串输出格式
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateByFormat(String format) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    
    /**
     * 获取现在日期
     * @return 返回短时间字符串格式MM-dd
     */
    public static String getStringDay() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     * @return  HH:mm:ss
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    
    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd
     * @param strDate
     * @return Date
     */
    public static Date strToDateShort(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * @param strDate
     * @return Date
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     * @param dateDate
     * @return String
     */
    public static String dateToStrLong(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyyMM
     * @param dateDate
     * @return String
     */
    public static String dateToStr(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyyMM
     * @param strDate
     * @return Date
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到现在时间
     * @return Date
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 提取一个月中的最后一天
     * @param day
     * @return Date
     */
    public static Date getLastDate(long day) {
        Date date = new Date();
        long date_3_hm = date.getTime() - 3600000 * 34 * day;
        Date date_3_hm_date = new Date(date_3_hm);
        return date_3_hm_date;
    }

    /**
     * 得到现在时间
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到现在小时
     * @return HH
     */
    public static String getHour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 得到现在分钟
     * @return mm
     */
    public static String getTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写
     * @param sformat yyyyMMddhhmmss
     * @return String
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    
    /**
     * 根据年月标识，返回前length个月的年月信息
     * @param yearMonthTag
     * @param length
     * @param isZh
     * @return List<YearMonthInfo>
     */
    public static List<YearMonthInfo> getYearMonthInfo(String yearMonthTag, int length, boolean isZh) {
    	List<YearMonthInfo> yearMonthInfoList = new ArrayList<YearMonthInfo>();
    	if(6 != yearMonthTag.length())return null;
    	int month = Integer.parseInt(yearMonthTag.substring(4));
    	Date date = strToDate(yearMonthTag);
    	Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
    	for(int i=0; i<length; i++){
    		YearMonthInfo yearMonthInfo = new YearMonthInfo();
    		yearMonthInfo.tag = dateToStr(calendar.getTime());
    		if(isZh){
    			yearMonthInfo.label = monthsZh[month-1];
    		}else{
    			yearMonthInfo.label = monthsEn[month-1];
    		}
    		month--;
    		if(month==0){
    			month = 12;
    		}
    		calendar.add(Calendar.MONTH, -1);
    		yearMonthInfoList.add(yearMonthInfo);
    	}
    	return yearMonthInfoList;
    }
    
    /**
     * 是否双十二
     * @return boolean
     */
    public static boolean isDoubleTwelve() {
    	String nowDate = getStringDay();
    	if("12-11".equals(nowDate) || "12-12".equals(nowDate)) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * 是否在两个日期之间
     * @param dateStr1
     * @param dateStr2
     * @return boolean
     */
    public static boolean isBetweenDate(String dateStr1, String dateStr2) {
    	try{
	    	long date1 = strToDateShort(dateStr1).getTime();
	    	long date2 = strToDateShort(dateStr2).getTime();
	    	long now = new Date().getTime();
	    	if(date1 < now && now < date2){
	    		return true;
	    	}
    	}catch(Exception e){
    		return false;
    	}
    	return false;
    }
    
    /**
     * 年月信息
     */
    public static class YearMonthInfo {
    	/**
    	 * 年月标识，例201108,199912
    	 */
    	public String tag;
    	/**
    	 * 年月显示标签，中文:3月；英文：Mar
    	 */
    	public String label;
    };

    /**
     * 获取今天日期的毫秒数
     * @return long
     */
    public static long getTodayTime(){
		try {
			Date date = new Date();
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = dateformat.format(date);
			return dateformat.parse(dateStr).getTime();
		} catch (Exception e) {
			Log.e("getTodayTime", e.toString());
			return 0;
		}
	}
}
