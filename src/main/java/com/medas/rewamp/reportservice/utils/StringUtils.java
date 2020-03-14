package com.medas.rewamp.reportservice.utils;

public class StringUtils {
	private StringUtils() { }
	/**
	 * Will check for null & empty value
	 * 
	 * @param value
	 * @return boolean
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}
}
