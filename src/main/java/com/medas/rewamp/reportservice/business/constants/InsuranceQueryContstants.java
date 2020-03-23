package com.medas.rewamp.reportservice.business.constants;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 23, 2020
 *
 */
public interface InsuranceQueryContstants {
	String getConsultDetailByID = "select " + 
			"		insurance_provider.insurar_name, " +
			"		department_setup.department_id, " + 
			"		department_setup.department_name, " + 
			"		doctor_consult.network_type, " + 
			"		doctor_consult.consult_date,		 " + 
			"		doctor_consult.consult_id,		 " + 
			"		doctor_consult.networkoffice_id, " + 
			"		doctor_consult.insurar_id, " + 
			"		doctor_consult.patient_age, " + 
			"		doctor_consult.op_number, " +
			"		doctors_setup.doctors_id, " + 
			"		doctors_setup.doctors_name, " + 
			"		new_registration.patient_sign " + 
			"	from " + 
			"		doctors_setup, department_setup, new_registration, doctor_consult " + 
			"	left outer join "
			+ "		insurance_provider on doctor_consult.insurar_id=insurance_provider.insurar_id " + 
			"	where " +
			"		doctor_consult.doctors_id=doctors_setup.doctors_id " + 
			"		and new_registration.op_number=doctor_consult.op_number " +
			"		and doctor_consult.department_id=department_setup.department_id " + 
			"		and doctor_consult.consult_id=:consultId";
	String getConsultForUcaf = "SELECT  " + 
	"			consult_id,  " + 
	"			emergency,  " + 
	"			significant_signs,  " + 
	"			other_cond,  " + 
	"			chronic,  " + 
	"			congenital,  " + 
	"			rta,  " + 
	"			vaccine,  " + 
	"			work_related,  " + 
	"			checkup,  " + 
	"			psychiatric,  " + 
	"			infertility,  " + 
	"			pregnancy,  " + 
	"			lmp,  " + 
	"			cmf_included,  " + 
	"			line_of_mgmt,  " + 
	"			length_of_stay  " + 
	"		FROM " + 
	"			consult_ucaf " + 
	"		WHERE  " + 
	"			consult_id=:consultId ";
	String getAllConsultDiagnosis = "select "
	+ "		icd_codes.icd_code, " + 
	"		icd_codes.icd_desc, " + 
	"		consult_diagnosis.diagnosis_category" +
	"		FROM " + 
	"			consult_diagnosis, icd_codes " + 
	"		WHERE  " + 
	"			consult_diagnosis.diagnosis_id=icd_codes.icd_code" + 
	"			and consult_id=:consultId ";
	
	String getAllChiefComplaintsByConsultId = "select present_complaint from pain_rate "
			+ "where type ='C' and consult_id=:consultId "
			+ "order by entered_date desc ";
	String getAllConsultProcedure = "select " + 
			"		procedure_setup.procedure_name, " + 
			"		consult_procedure.approval_status, " + 
			"		consult_procedure.ins_app,  " + 
			"		if(consult_procedure.insurar_icd_code is null,procedure_setup.icd_code,consult_procedure.insurar_icd_code) as insurar_icd_code, " + 
			"		consult_procedure.quantity, " + 
			"		consult_procedure.approved_date, " + 
			"		consult_procedure.valid_upto, " + 
			"		procedure_setup.procedure_type, " + 
			"		consult_procedure.gross_amt, " + 
			"		if(consult_procedure.appr_amt is null,0.00,round(consult_procedure.appr_amt,3)) as appr_amt, " + 
			"		consult_procedure.appr_no, " + 
			"		consult_procedure.appr_remarks " +
			"	from " + 
			"		consult_procedure  " + 
			"	JOIN  " + 
			"		procedure_setup " + 
			"    ON " + 
			"		consult_procedure.procedure_id=procedure_setup.procedure_id " + 
			"	WHERE  " + 
			"			consult_id=:consultId ";
	String getAllConsultLabTest = "select " + 
			"		test_setup.test_name as labtest_name, " + 
			"		consult_labtest.approval_status, " + 
			"		consult_labtest.ins_app,  " + 
			"		if(consult_labtest.insurar_icd_code is null,test_setup.icd_code,consult_labtest.insurar_icd_code) as insurar_icd_code, " + 
			"		consult_labtest.quantity, " + 
			"		consult_labtest.approved_date, " + 
			"		consult_labtest.valid_upto, " + 
			"		test_setup.lab_type, " + 
			"		consult_labtest.gross_amt, " + 
			"		if(consult_labtest.appr_amt is null,0.00,round(consult_labtest.appr_amt,3)) as appr_amt, " + 
			"		consult_labtest.appr_no, " + 
			"		consult_labtest.appr_remarks " +
			"	from " + 
			"		consult_labtest  " + 
			"	JOIN  " + 
			"		test_setup " + 
			"    ON " + 
			"		consult_labtest.labtest_id=test_setup.test_id " + 
			"	WHERE  " + 
			"			consult_id=:consultId ";
	String getInsuranceCardAttachment = "select document_Filename from attacheddocuments "
			+ "where op_number=:opNumber and  networkoffice_id=:networkOfficeId and document_id = 'Insurance Card' "
			+ "order by id desc "
			+ "limit 1";
	String getVitalSigns = "SELECT  " + 
			"			vital_sign, vital_value  " + 
			"		FROM " + 
			"		( " + 
			"			(SELECT " + 
			"				max(vit_sign_id) as vit_sign_id, " + 
			"				max(entry_id) as entry_id, " + 
			"				vital_sign as vital_sign_master_id " + 
			"			 FROM " + 
			"			 ( " + 
			"				SELECT " + 
			"			  		vital_signs.id as vit_sign_id, " + 
			"			  		entry_id, " + 
			"					vital_signs.vital_sign " + 
			"				FROM " + 
			"			  		vital_signs " + 
			"				WHERE " + 
			"			  		vital_signs.consult_id=:consultId " + 
			"				GROUP BY  " + 
			"					vital_signs.vital_sign, entry_id,vital_signs.id " + 
			"				ORDER BY entry_id desc " + 
			"			  )table_1 " + 
			"			 " + 
			"			GROUP BY  " + 
			"				vital_sign_master_id) table_last_entry " + 
			"		JOIN " + 
			"		 " + 
			"		(SELECT " + 
			"			vital_signs.id as vit_sign_id, " + 
			"			vital_signs.entry_id, " + 
			"			vitalsign_master.vital_sign,  " + 
			"			vitalsign_master.type, " + 
			"			if(vital_signs.value is null, '' ,vital_signs.value) as vital_value, " + 
			"			vitalsign_master.display_order " + 
			"		FROM  " + 
			"			vital_signs,vitalsign_master " + 
			"		WHERE " + 
			"			vital_signs.vital_sign=vitalsign_master.id " + 
			"		and consult_id=:consultId and vitalsign_master.type='C' "
			+ ") patient_vitals " + 
			"		 " + 
			"		ON " + 
			"			table_last_entry.vit_sign_id=patient_vitals.vit_sign_id  " + 
			" " + 
			"		) " + 
			"			order by patient_vitals.display_order	";
	String getPatientVisitDtlsByType = "select remarks from patient_visit_dtls, history_master "
			+ "WHERE symptom_id=history_master.id and type=:type and "
			+ "consult_id=:consultId ";
}