package com.medas.rewamp.reportservice.business.vo;

import java.util.Date;

import com.medas.rewamp.reportservice.utils.DateUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LabReportData {
	private Integer office_id;
	private Integer profile_id;
	private String clinic_id;
	private Integer test_id;
	private Integer test_order;
	private String consult_id;
	private Integer test_detailsid;
	private String test_detailsids;
	
	private Integer verified_by;
	private String test_format;
	private Integer category_id;
	private String category_name;
	private String sample_id;
	private String sample_type;
	private String culture_status;
	
	private String single_test;
	private String test_name;
	private String notes;
	private String remarks;
	private String closed_status;
	private String dispatch_status;
	private String reverse_auth_status;
	
	private String hide;
	private String bold;
	private String dac;
	private String iso;
	private String jci;
	private String cap;

	private Integer consult_labtest_id;
	private String current_level;
	private Integer profile_level;
	private String profile_ids;
	private String report_format;
	private String lab_idno;
	
	private String test_unit;
	private String parameter_name;
	private String test_result;
	private String normal_value;
	
	private Date collection_date;
	private Date accession_date;
	private Date verified_date;
	private Date dispatch_date;
	private Date printed_date;
	
	private Integer entered_by;
	private String forward_status;
	private String print_status;
	private String group_name;
	private String test_method;
	private String result_type;
	private String min_value;
	private String max_value;
	private String decimal_nos;
	
	private String test_code;
	private String parameter_code;
	private String param_mapping_id;

	public boolean isHide() {
		return "Y".equals(hide);
	}
	public boolean isBold() {
		return "Y".equals(bold);
	}
	public boolean isIso() {
		return "Y".equals(iso);
	}
	public boolean isDac() {
		return "Y".equals(dac);
	}
	public boolean isCap() {
		return "Y".equals(cap);
	}
	public boolean isJci() {
		return "Y".equals(jci);
	}
	public static String getFormatedDateTime(Date dateTime) {
		return DateUtil.formatDate("5", dateTime);
	}
}
