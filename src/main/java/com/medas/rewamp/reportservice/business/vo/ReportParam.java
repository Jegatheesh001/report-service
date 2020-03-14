package com.medas.rewamp.reportservice.business.vo;

import lombok.Data;

@Data
public class ReportParam {
	private Integer officeId;
	private Integer userId;
	private String testDetailsIds;
	private String multiple; 
	private String consultIds; 
}
