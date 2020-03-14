package com.medas.rewamp.reportservice.business.vo;

import lombok.Data;

@Data
public class UserBean {
	private Integer user_id;
	private Integer office_id;
	private String user_sign;
	private String user_label;
	private String user_desig;
	private String user_license;
}
