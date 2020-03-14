package com.medas.rewamp.reportservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.medas.rewamp.reportservice.service.ExportLabTestReport;

@Configuration
public class ContextBeans {

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ExportLabTestReport getPDFReport() {
		return new ExportLabTestReport();
	}
	
}
