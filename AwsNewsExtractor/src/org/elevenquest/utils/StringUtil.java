package org.elevenquest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtil {

	static SimpleDateFormat STANDARD_DATE = null;
	static SimpleDateFormat STANDARD_TIME = null;
	static SimpleDateFormat STANDARD_DATE_NEW_FORMAT = null;
	
	static {
		try {
			STANDARD_DATE = new SimpleDateFormat("yyyyMMdd");
			STANDARD_TIME = new SimpleDateFormat("HHmmss");
			STANDARD_DATE_NEW_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static Date convertToDateFromNewDateString(String standardDate) {
		java.util.Date rtn = null;
		try {
			rtn = STANDARD_DATE_NEW_FORMAT.parse(standardDate);
		} catch ( ParseException pe ) {
			pe.printStackTrace();
		}
		return rtn;
	}
	
	public static Date convertToDate(String standardDate) {
		java.util.Date rtn = null;
		try {
			rtn = STANDARD_DATE.parse(standardDate);
		} catch ( ParseException pe ) {
			pe.printStackTrace();
		}
		return rtn;
	}
	
	public static String convertToStandardDate(Date date) {
		String rtn = null;
		rtn = STANDARD_DATE.format(date);
		return rtn;
	}
	
	public static boolean isValidDate(String standardDate) {
		STANDARD_DATE.setLenient(false);
		try {
			STANDARD_DATE.parse(standardDate);
		} catch ( ParseException pe ) {
			return false;
		}
		return true;
	}
	
	public static String addDate(String standardDate, int addDate) {
		java.util.Date newDate = null;
		try {
			newDate = STANDARD_DATE.parse(standardDate);
			newDate.setDate(newDate.getDate() + addDate);
		} catch ( ParseException pe ) {
		}
		return STANDARD_DATE.format(newDate);
	}
	
	public static String getLastDayOfMonth(String standardDate) {
		Date date = convertToDate(standardDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return convertToStandardDate(calendar.getTime());
	}
	
	public static String getLastDayOfQuarter(String standardDate,int offset) {
		Date date = convertToDate(standardDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// TODO : REPLACE THIS CODE WITH NEW DATETIME CLASS IN JAVA8.
		// This issue ( add month ) is fixed in JSR 310.
		// But we dont'use java8 so. this trick is used.
		calendar.set(Calendar.DAY_OF_MONTH, 15);
		calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH) + offset);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return convertToStandardDate(calendar.getTime());
	}
	
	public static String getNextAnnualStandardDate(String standardDate) {
		int nextYear = Integer.parseInt(standardDate.substring(0, 4)) + 1;
		return nextYear + standardDate.substring(4);
	}
	
	public static String getPreviousAnnualStandardDate(String standardDate) {
		int nextYear = Integer.parseInt(standardDate.substring(0, 4)) - 1;
		return nextYear + standardDate.substring(4);
	}
	
	public static String getNumericValue(String org) {
		int tag_start = -1;
		int tag_end = -1;
		org = org.trim();
		while ( ( tag_start = org.indexOf("<") ) >= 0 ) {
			tag_end = org.indexOf(">");
			org = org.substring(0,tag_start) + org.substring(tag_end+1);
			System.out.println(org);
		}
		org = org.replace(",","").trim();
		return org;
	}

	public static String removeHtmlSpaceTag(String content) {
		return (content != null) ? content.trim().replaceAll("&nbsp;", "") : null;
	}
	
	public static void main(String[] args) {
		testGetLastDayOfMonth();
	}
	
	public static void testGetLastDayOfMonth() {
		System.out.println("20140228".equals(StringUtil.getLastDayOfMonth("20140205")));
		System.out.println("20110131".equals(StringUtil.getLastDayOfMonth("20140105")));
		System.out.println(getLastDayOfQuarter("20110131",3));
	}

}
