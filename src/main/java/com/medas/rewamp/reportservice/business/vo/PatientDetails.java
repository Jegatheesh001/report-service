package com.medas.rewamp.reportservice.business.vo;

import java.util.Date;

import lombok.Data;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 23, 2020
 *
 */
@Data
public class PatientDetails {
	private Integer patient_age;
	private Integer patient_agedays;
	private Integer patient_agemonth;
	private Integer patient_ageweek;

	private Integer insurar_id;
	private String insurar_name;
	private String op_number;
	private Integer department_id;
	private String department_name;
	private String network_type;
	private Date consult_date;
	private Integer networkoffice_id;
	private Integer consult_id;
	private Integer ip_id;
	
	private String emergency;
	private String significant_signs;
	private String other_cond;
	private String chronic;
	private String congenital;
	private String rta;
	private String vaccine;
	private String Work_related;
	private String psychiatric;
	private String infertility;
	private String pregnancy;
	private String lmp;
	private String checkup;
	private String cmf_included;
	private String line_of_mgmt;
	private String length_of_stay;
	private String admission_date;
	private Integer doctors_id;
	private String doctors_name;
	private String patient_sign;
}
