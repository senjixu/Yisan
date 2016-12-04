package com.yisan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	
	public static String format(Date date,String format){
		//TODO
		return null;
	}
	
	
	public static Date parseToDate(String date,String format) throws ParseException{
		
		format = StringUtils.isBlank(format)?yyyy_MM_dd_HH_mm_ss:format;
			
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(date);
	}
	
	public static Date parseToDate(String date) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
		Date d = null;
		d = sdf.parse(date);
		return d;
	}
	
}
