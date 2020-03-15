package com.medas.rewamp.reportservice.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.medas.rewamp.reportservice.format.TestReportFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 15, 2020
 *
 */
@Slf4j
@Component
public class ReportInstance {
	private static ApplicationContext context;

	@Autowired
	public ReportInstance(ApplicationContext ac) {
		context = ac;
	}

	public static TestReportFormat getReportClass(String reportImplementationClass) {
		TestReportFormat object = null;
		try {
			@SuppressWarnings("unchecked")
			Class<TestReportFormat> clazz = (Class<TestReportFormat>) Class.forName(reportImplementationClass);
			object = context.getBean(clazz.getSimpleName(), TestReportFormat.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			object = context.getBean("CommonTestReportFormat", TestReportFormat.class);
		}
		return object;
	}
}
