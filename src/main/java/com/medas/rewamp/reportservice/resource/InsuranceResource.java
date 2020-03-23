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

import com.medas.rewamp.reportservice.business.vo.insurance.UcafReportParam;
import com.medas.rewamp.reportservice.configuration.aspects.Loggable;
import com.medas.rewamp.reportservice.reports.UcafReport;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 22, 2020
 *
 */
@RestController
@RequestMapping("/report/insurance")
public class InsuranceResource {
	
	@Autowired
	private UcafReport ucafReport;
	
	@CrossOrigin
	@Loggable
	@GetMapping(value = "/ucaf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> htmlToPdf(UcafReportParam reportParam) throws Exception {
		ByteArrayInputStream bis = ucafReport.generateReport(reportParam);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=UcafReport.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}
	
}
