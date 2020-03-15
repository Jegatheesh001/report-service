package com.medas.rewamp.reportservice.business.constants;

public interface LabQueryContstants {
	String getOfficeLetterHead = "select " + "report_class," + "report_header_logo," + "report_footer_logo,"
			+ "jci_logo," + "dac_logo " + "from " + "office_details where office_Id= :officeId ";
	String getLabtestDetailsForReport = "select "
			+ "dc.clinic_id,td.test_detailsid, ts.test_id, ts.test_name, td.consult_lab_test_id as consult_labtest_id, "
			+ "ts.culture_status, ts.test_format, ts.report_format, ts.single_test, td.profile_id, "
			+ "tc.category_id, tc.category_name, st.sample_id, st.sample_name as sample_type, td.forward_toauth as forward_status, "
			+ "td.remarks, tso.notes, ts.lis_test_code as test_code, td.verified_by, td.enteredby as entered_by, td.lab_idno, "
			+ "td.closed_status, td.dispatch_status, td.reverse_authent as reverse_auth_status, td.print_status, "
			+ "td.sample_collect_date as collection_date, td.sample_received_date as accession_date, td.office_id, "
			+ "td.verified_date, td.printed_date, td.dispatch_date, is_iso as iso, is_dac as dac, is_cap as cap, is_jci as jci "
			+ "from " + "test_details td " + "inner join doctor_consult dc on dc.consult_id=td.consult_id "
			+ "inner join test_setup ts on td.test_id = ts.test_id "
			+ "inner join test_setup_office tso on tso.test_id = ts.test_id and td.office_id = tso.office_id "
			+ "inner join test_category tc on tc.category_id = ts.category_id "
			+ "inner join test_sample_setup st on st.sample_id = ts.sample_id "
			+ "where test_detailsid in (:testDetailsids) " 
			+ "group by td.test_detailsid "
			+ "order by tc.category_order, td.verified_by, st.sample_name,ts.test_order ";
	String getLabtestResultsForReport = "select " 
			+ "test_detailsid, param_mapping_name as parameter_name, group_name, "
			+ "tr.measure as test_unit, tr.testmethod as test_method, map.resultvalue as result_type, "
			+ "decimal_nos, tr.lis_test_code as test_code, tr.lis_parameter_code as parameter_code, "
			+ "tr.is_hide as hide, tr.is_bold as bold, tr.param_mapping_id, tso.is_dac dac, ts.single_test, "
			+ "(case when tr.test_Result is null then '' else tr.test_Result end) as test_result, "
			+ "(case when tr.min_value is null then '' else tr.min_value end) as min_value, "
			+ "(case when tr.max_value is null then '' else tr.max_value end) as max_value, "
			+ "(case when tr.fnormal_value is null then '' else tr.fnormal_value end) as normal_value "
			+ "from test_results tr inner join test_parameter_mapping map on map.id = tr.param_mapping_id "
			+ "inner join test_setup ts on tr.test_id = ts.test_id "
			+ "inner join test_setup_office tso on tr.test_id = tso.test_id and tr.office_id=tso.office_id "
			+ "where test_detailsid in (:testDetailsids) and ts.test_format != '3' ";
	String getTestDetailsById = "SELECT " + 
			"		doctor_consult.patient_name, " + 
			"		new_registration.op_number, " + 
			"		new_registration.sex, " + 
			"		doctors_setup.doctors_name, " + 
			"		doctor_consult.barcode_no, " + 
			"		doctor_consult.patient_age, " + 
			"		doctor_consult.patient_agemonth, " + 
			"		if(doctor_consult.patient_ageweek is null,0,doctor_consult.patient_ageweek) as patient_ageweek, " + 
			"		if(doctor_consult.patient_agedays is null,0,doctor_consult.patient_agedays) as patient_agedays, " + 
			"		doctor_consult.consult_id, " + 
			"		doctor_consult.refer_status, " +
			"		test_details.closed_status, " + 
			"		test_details.reverse_authent, " + 
			"		test_details.dispatch_status, " + 
			"		test_setup.test_format, " + 
			"       if(doctor_consult.refer_status='Y', doctor_consult.rdoctor_id, '') as rdoctor_id, " +
			"		doctor_consult.file_no, " + 
			"		doctor_consult.entered_date, " +
			"		insurance_provider.insurar_name " + 
			"	from  " + 
			"		test_details,new_registration,country_setup,doctors_setup,test_setup,doctor_consult " + 
			"	left join " + 
			"		refer_doctors " + 
			"		on " + 
			"			doctor_consult.rdoctor_id = refer_doctors.rdoctor_id " + 
			"		and " + 
			" 			refer_doctors.type='E' " + 
			" 	left outer join  " + 
			"		insurance_provider " + 
			"	on " + 
			"		doctor_consult.insurar_id=insurance_provider.insurar_id " + 
			" 	where " + 
			" 		new_registration.nationality=country_setup.country_id " + 
			" 	and " + 
			" 		test_details.op_number=new_registration.op_number " + 
			" 	and " + 
			" 		test_details.consult_id=doctor_consult.consult_id " + 
			" 	and " + 
			" 		doctor_consult.doctors_id=doctors_setup.doctors_id " + 
			" 	and " + 
			" 		test_setup.test_id=test_details.test_id "
			+ " and test_details.test_Detailsid=:testDetailsid ";
}