package com.medas.rewamp.reportservice.business.vo;

import java.math.BigInteger;
import java.util.Date;

import lombok.Data;

@Data
public class RegistrationBean {
	private String clinic_id;
	private String closed_status;
	private String dispatch_status;
	private String reverse_authent;
	private String test_format;
	private String testDetailsid;
	private String is_hide;
	
	private String measure;
	private String testName;
	private String testResult;
	private String normalValue;
	
	private String op_number;
	private String patient_name;
	private String sex;
	private String barcode_no;
	private String file_no;
	
	private String lab_idno;
	private String testId;
	private Integer office_id;
	
	private String patient_age;
	private Integer age;
	private String patient_agemonth;
	private String patient_ageweek;
	private String patient_agedays;
	
	private String refer_status;
	private String rdoctor_id;
	private String rdoctor_name;
	private Integer department_id;
	private String clinic_name;
	private String clinic_email;
	private String doctors_name;
	private String mail_to;
	
	private Date entered_date;
	private String insurar_name;
	private String lis_test_code;
	private String lis_parameter_code;
	private String test_Result;
	
	private String colonieus;
	private String organism_id;
	private String organism_name;
	private String antibiotic_id;
	private String antibiotic_name;
	private String sensitivity;
	
	public void setLab_idno(BigInteger lab_idno) {
		this.lab_idno = String.valueOf(lab_idno);
	}
}
