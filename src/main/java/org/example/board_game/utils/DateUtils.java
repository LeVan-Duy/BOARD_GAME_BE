package org.example.board_game.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    public static final long MS = 1;
    public static final long SECOND_MS = MS * 1000;
    public static final long MINUTE_MS = SECOND_MS * 60;
    public static final long HOUR_MS = MINUTE_MS * 60;
    public static final long DAY_MS = HOUR_MS * 24;
    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
    public static final String NORM_2_DATE_PATTERN = "yyyy/MM/dd";
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String now() {
        return formatDateTime(new Date());
    }

    public static String today() {
        return formatDate(new Date());
    }

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static String formatDateTime(Date date) {
        return new SimpleDateFormat(NORM_DATETIME_PATTERN).format(date);
    }

    public static String formatHttpDate(Date date) {
        return new SimpleDateFormat(HTTP_DATETIME_PATTERN, Locale.US).format(date);
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat(NORM_DATE_PATTERN).format(date);
    }

    public static Date parse(String dateString, String format) {
        try {
            return (new SimpleDateFormat(format)).parse(dateString);
        } catch (ParseException e) {
            log.error("Parse " + dateString + " with format " + format + " error!", e);
        }
        return null;
    }

    public static Date parseDateTime(String dateString) {
        try {
            return new SimpleDateFormat(NORM_DATETIME_PATTERN).parse(dateString);
        } catch (ParseException e) {
            log.error("Parse " + dateString + " with format " + new SimpleDateFormat(NORM_DATETIME_PATTERN).toPattern() + " error!", e);
        }
        return null;
    }

    public static Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat(NORM_DATE_PATTERN).parse(dateString);
        } catch (ParseException e) {
            log.error("Parse " + dateString + " with format " + NORM_DATE_PATTERN + " or" + NORM_2_DATE_PATTERN + " error!", e);
        }

        try {
            return new SimpleDateFormat(NORM_2_DATE_PATTERN).parse(dateString);
        } catch (ParseException e) {
            log.error("Parse " + dateString + " with format " + NORM_2_DATE_PATTERN + " error!", e);
        }
        return null;
    }

    public static Date parseTime(String timeString) {
        try {
            return new SimpleDateFormat(NORM_TIME_PATTERN).parse(timeString);
        } catch (ParseException e) {
            log.error("Parse " + timeString + " with format " + NORM_TIME_PATTERN + " error!", e);
        }
        return null;
    }

    public static Date parse(String dateStr) {
        int length = dateStr.length();
        try {
            if (length == DateUtils.NORM_DATETIME_PATTERN.length()) {
                return parseDateTime(dateStr);
            } else if (length == DateUtils.NORM_DATE_PATTERN.length()) {
                return parseDate(dateStr);
            } else if (length == DateUtils.NORM_TIME_PATTERN.length()) {
                return parseTime(dateStr);
            }
        } catch (Exception e) {
            log.error("Parse " + dateStr + " with format normal error!", e);
        }
        return null;
    }

    public static Date yesterday() {
        return offsiteDate(new Date(), Calendar.DAY_OF_YEAR, -1);
    }

    public static Date lastWeek() {
        return offsiteDate(new Date(), Calendar.WEEK_OF_YEAR, -1);
    }

    public static Date lastMouth() {
        return offsiteDate(new Date(), Calendar.MONTH, -1);
    }

    public static Date offsiteDate(Date date, int calendarField, int offsite) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendarField, offsite);
        return cal.getTime();
    }

    public static long diff(Date subtrahend, Date minuend, long diffField) {
        long diff = minuend.getTime() - subtrahend.getTime();
        return diff / diffField;
    }

    public static long spendNt(long preTime) {
        return System.nanoTime() - preTime;
    }

    public static long spendMs(long preTime) {
        return System.currentTimeMillis() - preTime;
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Instant parseStartOfDay(String dateStr) {
        Date date = parse(dateStr);
        if (date != null) {
            date = atStartOfDay(date);
            return dateToLocalDateTime(date).toInstant(ZoneOffset.ofHours(+7));
        }
        return null;
    }

    public static Instant parseEndOfDay(String dateStr) {
        Date date = parse(dateStr);
        if (date != null) {
            date = atEndOfDay(date);
            return dateToLocalDateTime(date).toInstant(ZoneOffset.ofHours(+7));
        }
        return null;
    }

    public static Instant getTimeStart(String dateText) {
        LocalDateTime date = LocalDateTime.parse(dateText + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return date.toInstant(ZoneOffset.ofHours(+7));
    }

    public static Instant getTimeFinish(String dateText) {
        LocalDateTime date = LocalDateTime.parse(dateText + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return date.toInstant(ZoneOffset.ofHours(+7));
    }

    public static Date getFirstDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, 1);
        return DateUtils.atStartOfDay(calendar.getTime());
    }

    public static Date getFirstDayOfCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return DateUtils.atStartOfDay(calendar.getTime());
    }

    public static Integer getDayCount(String start, String end) {
        int diff = -1;
        try {
            Date dateStart = parseDate(start);
            Date dateEnd = parseDate(end);

            if (dateEnd == null || dateStart == null) {
                return 0;
            }

            //time is always 00:00:00, so rounding should help to ignore the missing hour when going from winter to summer time, as well as the extra hour in the other direction
            diff = Math.toIntExact(Math.round((dateEnd.getTime() - dateStart.getTime()) / (double) 86400000));
        } catch (Exception e) {
            //handle the exception according to your own situation
        }
        return diff + 1;
    }
}
