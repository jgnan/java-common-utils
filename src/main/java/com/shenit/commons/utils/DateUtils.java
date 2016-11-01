package com.shenit.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {
	/** format string "yyyy-MM-dd HH:mm:ss SSS" */
	public static final String ALL = "yyyy-MM-dd HH:mm:ss SSS";
	/** format string "yyyy-MM-dd HH:mm:ss" */
	public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
	/** format string "yyyy-MM-dd" */
	public static final String YMD = "yyyy-MM-dd";
	/** date format for cookie expire field */
	public static final String COOKIE_DATE = "EEE, dd-MMM-yyyy HH:mm:ss zzz";
	/** Milli second to one day */
	public static final long ONE_DAY_MILLISEC = 1000 * 3600 * 24;
	
	//默认使用格林威治时间
    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT");
    //DAY Field
    public static final int MILLISECOND = 0;
    public static final int SECOND = 1;
    public static final int MINUTE = 2;
    public static final int HOUR = 3;
    public static final int DAY = 4;
	/**
	 * 字符串转换为date格式（默认格式化{@link #YMDHMS}）
	 * @param date 字符串时间
	 * @return {@link Date} 默认返回当前时间
	 */ 
	public static Date getNowTime(String date) {
		return getDateTime(null, date);
	}

	/**
	 * 从时间转换为字符串（默认格式化{@link #YMDHMS}）
	 * @param date 默认当前时间 传入数据转换失败返回当前时间
	 * @return {@link String}
	 */
	public static String getNowTime(Date date) {
		return getDateTimeStr(null, null == date ? new Date() : date);
	}

	/**
	 * 获取当前时间字符串（默认格式化{@link #YMDHMS}）
	 * @return {@link String}
	 */
	public static String getDate() {
		return getDateTimeStr(YMD, new Date());
	}

	/**
	 * 从日期转换为字符串
	 * @param format 格式化字符(默认为{@link #YMDHMS})
	 * @param inDate 要转换的时间, <strong>如果 inDate==null 返回 ""</strong>
	 * @return {@link String}
	 */
	public static String getDateTimeStr(String format, Date inDate) {
		format = (StringUtils.isBlank(format) ? YMDHMS : format);
		return inDate == null ? "" : new SimpleDateFormat(format).format(inDate);
	}
	
	/**
	 * Get current timestamp string.
	 * @return
	 */
	public static String getNowStr(){
	    return getDateTimeStr(YMDHMS, new Date());
	}

	/**
	 *  从字符串转成日期
	 * @param format 格式化字符(默认为{@link #YMDHMS})
	 * @param dataStr 字符串时间
	 * @return {@link Date} 转换失败返回当前时间
	 */
	public static Date getDateTime(String format, String dataStr) {
		format = (StringUtils.isBlank(format) ? YMDHMS : format);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(dataStr);
		} catch (ParseException e) {
			return new Date();
		}
	}
	
	/**
	 * 获取当天时间
	 * @param isStart=true当前开始时间,false当天结束时间
	 * @author folo 2015年5月2日下午6:26:51
	 */
	public static long getNow(Calendar calendar, boolean isStart){
        calendar.set(Calendar.HOUR_OF_DAY, isStart ? 0 : 23);  
        calendar.set(Calendar.MINUTE, isStart ? 0 : 59);  
        calendar.set(Calendar.SECOND, isStart ? 0 : 59);
        calendar.set(Calendar.MILLISECOND, isStart ? 0 : 999);
        return calendar.getTimeInMillis();
	}
	
	/**
	 * 获取数字格式的年月日 例如：19990909
	 * @author folo 2015年5月6日下午9:15:20
	 */
	public static int getDate(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year*10000 + month*100 + day;
    }
	
    /**
     * 获取指定timezone地点的当前日期时间
     * @param zoneId 地点ID
     * @return
     */
    public static Date getCurrentDate(String zoneId){
        return getCurrentDate(StringUtils.isBlank(zoneId) ? DEFAULT_TIMEZONE : TimeZone.getTimeZone(zoneId));
    }
    
    /**
     * 获取指定timezone地点的指定日期时间
     * @param zoneId 地点ID
     * @return
     */
    public static Date getDate(Date date){
        return getDate(date,(TimeZone)null);
    }
    
    /**
     * 获取指定timezone地点的指定日期时间
     * @param zoneId 地点ID
     * @return
     */
    public static long getDateMillis(Date date){
        return getDateMillis(date,(TimeZone)null);
    }
    
    /**
     * 获取指定timezone地点的指定日期时间
     * @param zoneId 地点ID
     * @return
     */
    public static Date getDate(Date date,String zoneId){
        return getDate(date,TimeZone.getTimeZone(zoneId));
    }
    

    /**
     * 获取指定timezone地点的指定日期时间
     * @param zoneId 地点ID
     * @return
     */
    public static long getDateMillis(Date date,String zoneId){
        return getDateMillis(date,TimeZone.getTimeZone(zoneId));
    }
    
    /**
     * 获取指定timezone地点的指定时间
     * @param date
     * @param zone
     * @return
     */
    public static Date getDate(Date date,TimeZone zone){
        zone = zone == null ? DEFAULT_TIMEZONE : zone;
        Calendar cal = Calendar.getInstance();
        long timezoneTimestamp = date == null ? cal.getTimeInMillis() : date.getTime();
        long srcOffset = cal.getTimeZone().getRawOffset();
        long toOffset = zone.getRawOffset();
        return new Date(timezoneTimestamp + (toOffset - srcOffset));
    }
    
    /**
     * 获取指定timezone地点的指定时间
     * @param date
     * @param zone
     * @return
     */
    public static long getDateMillis(Date date,TimeZone zone){
        date = getDate(date,zone);
        return date == null ? -1 : date.getTime();
    }
    
    
    /**
     * 获取指定默认的当前日期时间
     * @return
     */
    public static Date getCurrentDate(){
        return getCurrentDate(DEFAULT_TIMEZONE);
    }
    
    /**
     * Get current timestamp.
     * @return
     */
    public static long getCurrentTimeMills(){
        return getCurrentDate().getTime();
    }
    
    /**
     * 获取指定timezone地点的当前日期时间
     * @param timezone 时间区域
     * @return
     */
    public static Date getCurrentDate(TimeZone timezone){
        return getDate(null,timezone);
    }
    /**
     * 添加秒数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static long addSeconds(long currentTime, int i) {
        return currentTime + i * 1000;
    }
    
    /**
     * 添加秒数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static Date addSeconds(Date currentTime, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.SECOND, i);
        return cal.getTime();
    }
    
    /**
     * 添加秒数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static long addMinutes(long currentTime, int i) {
        return currentTime + i * 60000 ;
    }
    /**
     * 添加分钟数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static Date addMinutes(Date currentTime, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.MINUTE, i);
        return cal.getTime();
    }
    /**
     * 添加秒数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static long addHours(long currentTime, int i) {
        return currentTime + i * 3600000 ;
    }
    /**
     * 添加时数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static Date addHours(Date currentTime, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.HOUR_OF_DAY, i);
        return cal.getTime();
    }
    
    /**
     * 添加天数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static Date addDays(long currentTime, int i) {
        return new Date(currentTime + i * 86400000l);
    }
    
    /**
     * 添加天数
     * @param currentTime 当前时间
     * @param i 增量
     * @return
     */
    public static Date addDays(Date currentTime, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }
    
    /**
     * 过滤时间部分
     * @param date
     * @return
     */
    public static long purgeTime(long date){
        return date / ONE_DAY_MILLISEC * ONE_DAY_MILLISEC;
    }
    
    /**
     * 过滤时间部分
     * @param date
     * @return
     */
    public static Date purgeTime(Date date){
        return new Date(purgeTime(date.getTime()));
    }

    /**
     * 根据timestamp生成
     * @param ts
     * @return
     */
    public static Date date(Long ts) {
        return ts == null ? null : new Date(ts);
    }
    
    /**
     * 判断日期对象是否在当前时间偏移指定时间量后的日期之前
     * @param date 日期
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果date在当前日期的偏移量之前，返回true，否则返回false
     */
    public static boolean noMoreThan(Date date, int range, int type){
        return date == null ? false : compare(date.getTime(), range, type, -1);
    }
    
    /**
     * 判断日期对象是否在当前时间偏移指定时间量后的日期之后
     * @param date 日期
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果date在当前日期的偏移量之后，返回true，否则返回false
     */
    public static boolean moreThan(Date date, int range, int type){
        return date == null ? false : compare(date.getTime(), range, type, -1);
    }
    
    /**
     * 判断日期对象是否和当前时间偏移指定时间量后的日期相等
     * @param ts 时间戳
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果ts在当前日期的偏移量之前，返回true，否则返回false
     */
    public static boolean noMoreThan(long ts, int range, int type){
        return compare(ts, range, type, -1);
    }
    
    /**
     * 判断日期对象是否和当前时间偏移指定时间量后的日期相等
     * @param ts 时间戳
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果ts在当前日期的偏移量之后，返回true，否则返回false
     */
    public static boolean moreThan(long ts, int range, int type){
        return compare(ts, range, type, 1);
    }
    
    
    /*
     * 对比时间搓和值之间的区别
     * @param ts - 要比较的时间搓
     * @param range - 要对比的值， 需要和type结合使用
     * @param compareType - 对比类型， > 0 判断ts是否大于range; <0 判断ts是否小于range
     */
    private static boolean compare(long ts, int range, int type, int compareType){
        int ms = 1;
        long now = System.currentTimeMillis();
        switch(type){
        case DAY: ms *= 24;
        case HOUR: ms *= 60;
        case MINUTE: ms *= 60;
        case MILLISECOND:
        case SECOND: ms *= 1000;
        }
        return compareType > 0 ? ts > now + range * ms : ts < now + range * ms; 
    }
    
}
