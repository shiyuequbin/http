package com.lonzh.crawler.tool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/**
 * 格式转换工具类
 * 
 * @author LZ
 *
 */
public class FormatConvertor {
	/**
	 * 字符串转为正则表达式
	 *
	 * @param str
	 * @return
	 */
	public static final String str2regex(String str) {
		return str.replaceAll("\\\\", "\\\\").replaceAll("\\*", "\\*").replaceAll("\\+", "\\+").replaceAll("\\|", "\\|").replaceAll("\\{", "\\{").replaceAll("\\}", "\\}")
				.replaceAll("\\(", "\\(").replaceAll("\\)", "\\)").replaceAll("\\^", "\\^").replaceAll("\\$", "\\$").replaceAll("\\[", "\\[").replaceAll("\\]", "\\]")
				.replaceAll("\\?", "\\?").replaceAll("\\,", "\\,").replaceAll("\\.", "\\.").replaceAll("\\&", "\\&");
	}

	/**
	 * 格式化日期时间
	 *
	 * @param date
	 * @return
	 */
	public static final String formatDateTime(Date date) {
		if (date == null) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(date);
	}

	/**
	 * 日期时间字符串转换为Date
	 *
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static final Date parseDateTime(String date) throws ParseException {
		if (date == null) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.parse(date);
	}

	/**
	 * 格式化日期
	 *
	 * @param date
	 * @return
	 */
	public static final String formatDate(Date date) {
		if (date == null) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}

	/**
	 * 日期字符串转换为Date
	 *
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static final Date parseDate(String date) throws ParseException {
		if (date == null) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.parse(date);
	}

	/**
	 * 格式化时间
	 *
	 * @param date
	 * @return
	 */
	public static final String formatTime(Date date) {
		if (date == null) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(date);
	}

	/**
	 * 时间字符串转换为Date
	 *
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static final Date parseTime(String time) throws ParseException {
		if (time == null) {
			return null;
		}
		return new SimpleDateFormat("HH:mm").parse(time);
	}

	/**
	 * 时间戳转换成字符串
	 *
	 * @param time
	 * @return
	 */
	public static final String formatDateTime(Long time) {
		if (time == null) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
	}

	/**
	 * 格式化MAC地址为AA-AA-AA-AA-AA-AA格式
	 *
	 * @param mac
	 * @return
	 */
	public static String formatMac(String mac) {
		if (mac != null && mac != "") {
			mac = mac.trim().toUpperCase();
			if (mac.length() == 12) {
				StringBuffer buf = new StringBuffer();
				for (int x = 0; x < 6; x++) {
					buf.append(mac.substring(2 * x, 2 * x + 2));
					if (x != 5) {
						buf.append("-");
					}
				}
				return buf.toString();
			} else if (mac.length() == 17) {
				return mac.replaceAll("\\:", "-");
			} else {
				return "";
			}
		}
		return "";
	}

	/**
	 * 得到几天前的时间
	 *
	 * @param nowTime
	 * @param day
	 * @return Date
	 */
	public static Date getDateAfter(Date nowTime, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(nowTime);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	public static Date getTimeAfter(Date nowTime, int hour) {
		Calendar now = Calendar.getInstance();
		now.setTime(nowTime);
		now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) + hour);
		return now.getTime();
	}

}
