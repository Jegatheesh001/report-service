package com.medas.rewamp.reportservice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;


/**
 * Date Utility
 * 
 * @UserMethod Use <b>parseDate</b> method to get Date of Corresponding String
 *             (dd-MM-yyyy)<br>
 *             Formats:<br>
 *             1 : dd-MM-yyyy<br>
 *             2 : dd/MM/yyyy<br>
 *             3 : dd-MM-yyyy HH:mm<br>
 *             4 : dd-MM-yyyy HH:mm:ss<br>
 * @Created on Dec 10, 2017 for eLab-MOPA
 * @author Jegatheesh
 */
public class DateUtil {

	//private static final Logger logger = Logger.getLogger(AuditLogHandler.class);
	private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("dd-MM-yyyy");
	private static final SimpleDateFormat FORMAT2 = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat FORMAT3 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	private static final SimpleDateFormat FORMAT4 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private static final SimpleDateFormat FORMAT5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat FORMAT6 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat FORMAT7 = new SimpleDateFormat("ddMMMyy");
	private static final SimpleDateFormat FORMAT8 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final DateTimeFormatter FORMATTTER1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	public static Date parseDate(String dateString) {
		Date output = null;
		if (dateString == null || (dateString != null && dateString.equals(""))) {
			return output;
		}
		output = convertToDate(FORMAT1, dateString);
		return output;
	}

	/**
	 * <b>1 : dd-MM-yyyy <br>
	 * 2 : dd/MM/yyyy <br>
	 * 3 : dd-MM-yyyy HH:mm <br>
	 * 4 : dd-MM-yyyy HH:mm:ss </b>
	 * @param format
	 * @param dateString
	 * @return Date
	 */
	public static Date parseDate(String format, String dateString) {
		Date output = null;
		if (dateString == null) {
			return output;
		}
		SimpleDateFormat sdf = getSDFAccordingToFormat( format);
		output = convertToDate(sdf, dateString);
		return output;
	}

	/**
	 * @purpose 
	 * @author Indrajith G R
	 * @created Jun 9, 2018
	 * @param format
	 * @param date
	 * @return
	 */
	public static String formatDate(String format, Date date) {
		String output = null;
		if (date == null) {
			return output;
		}
		SimpleDateFormat sdf = getSDFAccordingToFormat(format);
		output = convertToDateString(sdf, date);
		return output;
	}

	private static Date convertToDate(SimpleDateFormat sdf, String dateString) {
		Date output = null;
		try {
			output = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String convertToDateString(SimpleDateFormat sdf, Date date) {
		return sdf.format(date);
	}
	
	public static LocalDateTime parseDateLDT(String dateString) {
		LocalDateTime output = null;
		if (dateString == null) {
			return output;
		}
		output = LocalDateTime.parse(dateString, FORMATTTER1);
		return output;
	}
	
	/**
	 * @author Indrajith G R
	 * to convert LocalDateTime to Date
	 * @param dateTime
	 * @return
	 */
	public static Date parseLDTtoDate(LocalDateTime dateTime) {
		return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * For returning last day of current month in a specified format
	 * default format is 'dd-MM-yyyy'
	 * @author Indrajith G R
	 * @created Jun 9, 2018
	 * @return
	 */
	public static String getLastDayOfCurrentMonth(String format) {
		if(format == null || format.trim().length()==0)
			format = "1";
		return formatDate(format,parseLDtoDate(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())));
	}

	/**
	 * To convert LocalDate to Date
	 * @author Indrajith G R
	 * @created Jun 9, 2018
	 * @param day
	 * @return
	 */
	public static Date parseLDtoDate(LocalDate day) {
		return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * For returning first day of current month in a specified format
	 * default format is 'dd-MM-yyyy'
	 * @author Indrajith G R
	 * @created Jun 9, 2018
	 * @param format
	 * @return
	 */
	public static String getFirstDayOfCurrentMonth(String format) {
		if(format == null || format.trim().length()==0)
			format = "1";
		return formatDate(format,parseLDtoDate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())));
	}
	
	/**
	 * Purpose :- For finding relevant Date Format
	 * @param format
	 * @return SimpleDateFormat
	 */
	private static SimpleDateFormat getSDFAccordingToFormat(String format) {
		SimpleDateFormat sdf = null;
		switch (format) {
		case "1": {
			sdf = FORMAT1;
			break;
		}
		case "2": {
			sdf = FORMAT2;
			break;
		}
		case "3": {
			sdf = FORMAT3;
			break;
		}
		case "4": {
			sdf = FORMAT4;
			break;
		}
		case "5": {
			sdf = FORMAT5;
			break;
		}
		case "6":{
			sdf = FORMAT6;
			break;
		}
		case "7":{
			sdf = FORMAT7;
			break;
		}
		case "8":{
			sdf = FORMAT8;
			break;
		}
		default: {
			sdf = FORMAT1;
		}
		}
		return sdf;
	}
	
	/**
	 * To convert Date to LocalDate
	 * 
	 * @created Sep 25, 2019
	 * @param Date
	 * @return LocalDate
	 */
	public static LocalDate parseDateToLDT(Date day) {
		return day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
