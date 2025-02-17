package org.example.board_game.utils;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public class ConvertUtil {

    public static Long convertLocalDateTimeToLong(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    public static LocalDateTime convertLongToLocalDateTime(Long longTime) {
        return LocalDateTime
                .ofInstant(Instant.ofEpochMilli(longTime), TimeZone.getDefault().toZoneId());
    }

    public static String convertFloatToVnd(float amount) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(locale);
        String formattedAmount = currencyVN.format((double) amount);
        formattedAmount = formattedAmount.replace("đ", " VNĐ");
        return formattedAmount;
    }

    public static double toDouble(String str) {
        return Double.parseDouble(str);
    }

    public static int toInt(String str) {
        return Integer.parseInt(str);
    }

    public static long toLong(String str) {
        return Long.parseLong(str);
    }

    public static float toFloat(String str) {
        return Float.parseFloat(str);
    }

    public static short toShort(String str) {
        return Short.parseShort(str);
    }

    public static char toChar(String str) {
        return str.charAt(0);
    }

    public static byte toByte(String str) {
        return Byte.parseByte(str);
    }

    public static String toString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    public static boolean toBoolean(String str) {
        return Boolean.parseBoolean(str);
    }

    public static boolean toBooleanByObject(Object oj) {
        return toBoolean(toString(oj));
    }

    public static double toDoubleByObject(Object oj) {
        return toDouble(toString(oj));
    }

    public static String toString(int str) {
        return String.valueOf(str);
    }

    public static double toDoubleByInt(int number) {
        return toDouble(toString(number));
    }

    public static int toIntByLong(long number) {
        return toInt(String.valueOf(number));
    }

    public static long toLongByFloat(float number) {
        return toLong(String.valueOf(number));
    }

    public static float toFloatByShort(short number) {
        return toFloat(String.valueOf(number));
    }

    public static short toShortByChar(char number) {
        return toShort(String.valueOf(number));
    }

    public static char toCharByByte(byte number) {
        return toChar(String.valueOf(number));
    }

    public static byte toByteByChar(char number) {
        return toByte(String.valueOf(number));
    }

    public static char toCharByShort(short number) {
        return toChar(String.valueOf(number));
    }

    public static byte toByteByShort(byte number) {
        return toByte(String.valueOf(number));
    }

    public static short toShortByInt(int number) {
        return toShort(String.valueOf(number));
    }
}
