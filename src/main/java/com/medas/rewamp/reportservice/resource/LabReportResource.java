package com.medas.rewamp.reportservice.resource;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medas.rewamp.reportservice.business.vo.ApiResponse;
import com.medas.rewamp.reportservice.business.vo.LabReportResponse;
import com.medas.rewamp.reportservice.business.vo.ReportParam;
import com.medas.rewamp.reportservice.configuration.aspects.Loggable;
import com.medas.rewamp.reportservice.service.ExportLabTestReport;
import com.medas.rewamp.reportservice.service.LabTestReport;

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
	@Autowired
	LabTestReport labReport;

	@Loggable
	@GetMapping("/generate")
	public ApiResponse<LabReportResponse> generateReport(ReportParam reportParam) throws Exception {
		return new ApiResponse<>(exportLabReport.generateReport(reportParam));
	}
	
	@Loggable
	@CrossOrigin
	@GetMapping(value = "/view", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> htmlToPdf(ReportParam reportParam) throws Exception {
		ByteArrayInputStream bis = labReport.generateReport(reportParam);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=LabtestReport.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}
}
