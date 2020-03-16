package com.medas.rewamp.reportservice.business.vo;

import lombok.Data;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 16, 2020
 *
 */
@Data
public class LabReportResponse {
	private String filePath;
	private String opNumber;
	private String patientName;
	private String mailTo;
	private String patientMail;
	
	public LabReportResponse(String filePath) {
		this.filePath = filePath;
	}
}
