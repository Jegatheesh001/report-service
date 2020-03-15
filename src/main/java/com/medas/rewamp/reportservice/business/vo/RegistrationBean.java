package com.medas.rewamp.reportservice.business.vo;

import java.math.BigInteger;
import java.util.Date;

import lombok.Data;

@Data
public class RegistrationBean {
	private Date entered_date;
	private Integer age;
	private Integer department_id;
	private Integer office_id;
	private String antibiotic_id;
	private String antibiotic_name;
	private String barcode_no;
	private String clinic_email;
	private String clinic_id;
	private String clinic_name;
	private String closed_status;
	private Integer consult_id;
	private String colonieus;
	private String dispatch_status;
	private String doctors_name;
	private String file_no;
	private String insurar_name;
	private String is_hide;
	private String lab_idno;
	private String lis_parameter_code;
	private String lis_test_code;
	private String mail_to;
	private String measure;
	private String normalValue;
	private String op_number;
	private String organism_id;
	private String organism_name;
	private String patientAge;
	private Integer patient_age;
	private Integer patient_agedays;
	private Integer patient_agemonth;
	private Integer patient_ageweek;
	private String patient_name;
	private String rdoctor_id;
	private String rdoctor_name;
	private String refer_status;
	private String reverse_authent;
	private String sensitivity;
	private String sex;
	private String testDetailsid;
	private String testId;
	private String testName;
	private String testResult;
	private String test_Result;
	private String test_format;
	
	public void setLab_idno(BigInteger lab_idno) {
		this.lab_idno = String.valueOf(lab_idno);
	}
	public void setBarcode_no(BigInteger barcode_no) {
		this.barcode_no = String.valueOf(barcode_no);
	}
	public void setPatient_ageweek(BigInteger patient_ageweek) {
		this.patient_ageweek = patient_ageweek.intValue();
	}
	public void setPatient_agedays(BigInteger patient_agedays) {
		this.patient_agedays = patient_agedays.intValue();
	}
}
