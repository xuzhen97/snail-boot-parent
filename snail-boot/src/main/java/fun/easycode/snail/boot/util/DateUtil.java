package fun.easycode.snail.boot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * 日期工具类
 *
 * @author xuzhen97
 */
public final class DateUtil {

    public static final String FORMAT_STR1 = "yyyy-MM";
    public static final String FORMAT_STR2 = "yyyy-MM-dd";
    public static final String FORMAT_STR3 = "yyyy-MM-dd HH";
    public static final String FORMAT_STR4 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_STR5 = "yyyy-MM-dd HH:mm:ss";

    // yyyy-MM
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL1 = new ThreadLocal<>();
    // yyyy-MM-dd
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL2 = new ThreadLocal<>();
    // yyyy-MM-dd HH
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL3 = new ThreadLocal<>();
    // yyyy-MM-dd HH:mm
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL4 = new ThreadLocal<>();
    // yyyy-MM-dd HH:mm:ss
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL5 = new ThreadLocal<>();

    private static final ZoneId DEFAULT_ZONEID =  ZoneId.systemDefault();

    /**
     * 根据日期字符串匹配Format
     * @param source
     * @return
     */
    private static SimpleDateFormat matchingFormat(String source){
        if(source == null){
            return null;
        }
        if (source.matches("^\\d{4}-\\d{1,2}$")) {
            return getFormat(FORMAT_STR1);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return getFormat(FORMAT_STR2);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}$")) {
            return getFormat(FORMAT_STR3);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return getFormat(FORMAT_STR4);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return getFormat(FORMAT_STR5);
        }else{
            return null;
        }
    }

    /**
     * 根据日期字符串从ThreadLocal中获取Format
     * @param formatStr
     * @return
     */
    private static SimpleDateFormat getFormat(String formatStr){
        ThreadLocal<SimpleDateFormat> formatThreadLocal = null;
        if(Objects.equals(formatStr, FORMAT_STR1)){
            formatThreadLocal = DATE_FORMAT_THREAD_LOCAL1;
        }else if(Objects.equals(formatStr, FORMAT_STR2)){
            formatThreadLocal = DATE_FORMAT_THREAD_LOCAL2;
        }else if(Objects.equals(formatStr, FORMAT_STR3)){
            formatThreadLocal = DATE_FORMAT_THREAD_LOCAL3;
        }else if(Objects.equals(formatStr, FORMAT_STR4)){
            formatThreadLocal = DATE_FORMAT_THREAD_LOCAL4;
        }else if(Objects.equals(formatStr, FORMAT_STR5)){
            formatThreadLocal = DATE_FORMAT_THREAD_LOCAL5;
        }else {
            throw new RuntimeException("Unsupported Format Str!!!");
        }
        SimpleDateFormat format = formatThreadLocal.get();
        if(format == null){
            format = new SimpleDateFormat(formatStr);
            formatThreadLocal.set(format);
        }
        return format;
    }

    /**
     * 清空工具类的ThreadLocal
     */
    public static void clearThreadLocal(){
        DATE_FORMAT_THREAD_LOCAL1.remove();
        DATE_FORMAT_THREAD_LOCAL2.remove();
        DATE_FORMAT_THREAD_LOCAL3.remove();
        DATE_FORMAT_THREAD_LOCAL4.remove();
        DATE_FORMAT_THREAD_LOCAL5.remove();
    }

    /**
     * 获取指定日期【前】day天的日期
     *
     * @param now 指定日期
     * @param day 天数
     * @return
     */
    public static Date getPrevNDay(Date now, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, -day);
        return calendar.getTime();
    }

    /**
     * 获取指定日期【前】day天的日期
     *
     * @param date 指定日期
     * @param day 天数
     * @return
     */
    public static LocalDate getPrevNDay(LocalDate date, int day){
        return  asLocalDate(getPrevNDay(asDate(date), day));
    }


    /**
     * 获取指定日期【前】day天的日期
     *
     * @param date 指定日期
     * @param day 天数
     * @return
     */
    public static LocalDateTime getPrevNDay(LocalDateTime date, int day){
        return  asLocalDateTime(getPrevNDay(asDate(date), day));
    }

    /**
     * 获取指定日期【后】day天的日期
     *
     * @param now 指定日期
     * @param day 天数
     * @return
     */
    public static Date getNextNDay(Date now, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * 获取指定日期【后】day天的日期
     *
     * @param date 指定日期
     * @param day 天数
     * @return
     */
    public static LocalDate getNextNDay(LocalDate date, int day){
        return  asLocalDate(getNextNDay(asDate(date), day));
    }

    /**
     * 获取指定日期【后】day天的日期
     *
     * @param date 指定日期
     * @param day 天数
     * @return
     */
    public static LocalDateTime getNextNDay(LocalDateTime date, int day){
        return  asLocalDateTime(getNextNDay(asDate(date), day));
    }

    /**
     * 获取指定日期前minute分钟的日期
     * @param now 指定日期
     * @param minute 分钟
     * @return
     */
    public static Date getPrevNMinute(Date now, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, -minute);
        return calendar.getTime();
    }

    /**
     * 日期格式化为YYYY-MM-DD字符串
     *
     * @param date
     * @return
     */
    public static <T> String format(T date, String formatStr) {
        return getFormat(formatStr).format(date);
    }

    /**
     * 字符串日期转换为Date
     * yyyy-MM
     * yyyy-MM-dd
     * yyyy-MM-dd HH
     * yyyy-MM-dd HH:mm
     * yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static Date parse(String date) {
        try {
            SimpleDateFormat format = matchingFormat(date);
            if(format != null){
                return format.parse(date);
            }
            return null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * LocalDate转Date
     * @param localDate
     * @return
     */
    public static Date asDate(LocalDate localDate) {
        if(localDate == null){
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(DEFAULT_ZONEID).toInstant());
    }

    /**
     * LocalDateTime转Date
     * @param localDateTime
     * @return
     */
    public static Date asDate(LocalDateTime localDateTime) {
        if(localDateTime == null){
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONEID).toInstant());
    }

    /**
     * Date转LocalDate
     * @param date
     * @return
     */
    public static LocalDate asLocalDate(Date date) {
        if(date == null){
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(DEFAULT_ZONEID).toLocalDate();
    }

    /**
     * Date转LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime asLocalDateTime(Date date) {
        if(date == null){
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(DEFAULT_ZONEID).toLocalDateTime();
    }

    /**
     * 时间戳转换成LocalDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime asLocalDateTime(Long timestamp){
        return Instant.ofEpochMilli(timestamp).atZone(DEFAULT_ZONEID).toLocalDateTime();
    }

    /**
     * 将字符串时间戳转换成LocalDateTime
     *  字符串格式不正确，为null或者空字符都会返回 null
     *  方法已经处理 NumberFormatException
     * @param timestamp 时间戳
     * @return
     */
    public static LocalDateTime asLocalDateTime(String timestamp){
        try {
            return asLocalDateTime(Long.valueOf(timestamp));
        }catch (NumberFormatException e){
            return null;
        }
    }

    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     * @param start
     * @param end
     * @return
     */
    public static List<LocalDate> collectLocalDates(LocalDate start, LocalDate end){
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, localDate -> localDate.plusDays(1))
            // 截断无限流，长度为起始时间和结束时间的差+1个
            .limit(DAYS.between(start, end) + 1)
            // 由于最后要的是字符串，所以map转换一下
            // 把流收集为List
            .collect(Collectors.toList());
    }

    /**
     * 计算start 和 end之间的间隔天数, 不包含end
     * @param start
     * @param end
     * @return
     */
    public static long between(LocalDate start, LocalDate end){
        return DAYS.between(start, end);
    }

    /**
     * 转换为时间戳
     * @param localDateTime
     * @return
     */
    public static long toMilli(LocalDateTime localDateTime){
        return localDateTime.atZone(DEFAULT_ZONEID).toInstant().toEpochMilli();
    }

    /**
     * 转换为时间戳
     * @param localDate
     * @return
     */
    public static long toMilli(LocalDate localDate){
        return localDate.atStartOfDay(DEFAULT_ZONEID).toInstant()
            .toEpochMilli();
    }

    /**
     * 获取当前日期上下几个月的日期，例如获取当前时间前6个月的那一天日期
     * 以后的月份日期正数，例如6 , 代表后6个月
     * 以前的月份日期复数，例如-6 , 代表前6个月
     * @param mouth
     * @return
     */
    public static Date getCurrPrevNextMonth(int mouth){
        GregorianCalendar now = new GregorianCalendar();
        //可以是天数或月数  数字自定 -6前6个月
        now.add(GregorianCalendar. MONTH, mouth);
        return now.getTime();
    }

    /**
     * 获取指定月份的第一天
     * @param year 年
     * @param month 月
     * @return Date
     */
    public static Date getMonthFirstDay(int year, int month){
        Calendar calendar = Calendar.getInstance();

        //设置年份
        calendar.set(Calendar.YEAR,year);
        //设置月份
        calendar.set(Calendar.MONTH, month - 1);

        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        return calendar.getTime();
    }

    /**
     * 获取指定月份的最后一天
     * @param year 年
     * @param month 月
     * @return Date
     */
    public static Date getMonthLastDay(int year, int month){
        Calendar calendar = Calendar.getInstance();

        //设置年份
        calendar.set(Calendar.YEAR,year);
        //设置月份
        calendar.set(Calendar.MONTH, month - 1);

        calendar.set(Calendar.DAY_OF_MONTH,0);
        calendar.add(Calendar.MONTH,1);
        return calendar.getTime();
    }
}
