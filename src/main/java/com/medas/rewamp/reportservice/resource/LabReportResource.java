package com.medas.rewamp.reportservice.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medas.rewamp.reportservice.business.vo.ReportParam;
import com.medas.rewamp.reportservice.service.ExportLabTestReport;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 14, 2020
 *
 */
@RestController
@RequestMapping("/report/lab")
public class LabReportResource {
	
	@Autowired
	ExportLabTestReport exportLabReport;

	@GetMapping("/generate")
	public String generateReport(ReportParam reportParam) throws Exception {
		return exportLabReport.generateReport(reportParam);
	}
	
}
