package com.medas.rewamp.reportservice.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 14, 2020
 *
 */
@Component
public class ReportHolder {
	private static ThreadLocal<ReportContext> context = new ThreadLocal<>();
	
	public static void initialize() {
		context.set(new ReportContext());
	}
	private static ReportContext get() {
		return context.get();
	}
	public static void remove() {
		context.remove();
	}
	public static void setAttribute(String key, Object value) {
		get().setProperty(key, value);
	}
	public static String getLastFormat() {
		return get().lastFormat;
	}
	public static void setLastFormat(String lastFormat) {
		get().lastFormat = lastFormat;
	}
	public static String getTestFormat() {
		return get().testFormat;
	}
	public static void setTestFormat(String testFormat) {
		get().testFormat = testFormat;
	}
	public static boolean showHeader() {
		return get().showHeader;
	}
	public static void setShowHeader(boolean showHeader) {
		get().showHeader = showHeader;
	}
	public static String getAttribute(String key) {
		return (String) get().getProperty(key);
	}
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(String key, Class<T> clazz) {
		return (T) get().getProperty(key);
	}
}

class ReportContext {
	private Map<String, Object> properties = new HashMap<>();
	String lastFormat = null;
	String testFormat = null;
	boolean showHeader = false;
	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}
	public Object getProperty(String key) {
		return properties.get(key);
	}
}
