package com.medas.rewamp.reportservice.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 14, 2020
 *
 */
public class ReportHolder {
	private static final ReportHolder holder = new ReportHolder();
	private ThreadLocal<ReportContext> context = null;
	public void init() {
		context = ThreadLocal.withInitial(ReportContext::new);
	}
	public ReportContext get() {
		return holder.context.get();
	}
	public static void remove() {
		holder.context.remove();
	}
	public static void initialize() {
		holder.init();
	}
	public static void setAttribute(String key, Object value) {
		holder.get().setProperty(key, value);
	}
	public static String getLastFormat() {
		return holder.get().lastFormat;
	}
	public static void setLastFormat(String lastFormat) {
		holder.get().lastFormat = lastFormat;
	}
	public static String getTestFormat() {
		return holder.get().testFormat;
	}
	public static void setTestFormat(String testFormat) {
		holder.get().testFormat = testFormat;
	}
	public static boolean showHeader() {
		return holder.get().showHeader;
	}
	public static void setShowHeader(boolean showHeader) {
		holder.get().showHeader = showHeader;
	}
	public static String getAttribute(String key) {
		return (String) holder.get().getProperty(key);
	}
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(String key, Class<T> clazz) {
		return (T) holder.get().getProperty(key);
	}
	private class ReportContext {
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
}
