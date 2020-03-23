package com.medas.rewamp.reportservice.persistence;

import java.util.List;

import com.medas.rewamp.reportservice.business.vo.ConsultMedicine;
import com.medas.rewamp.reportservice.business.vo.PatientDetails;
import com.medas.rewamp.reportservice.business.vo.ServiceVO;
import com.medas.rewamp.reportservice.business.vo.VitalSigns;
import com.medas.rewamp.reportservice.business.vo.insurance.UcafReportParam;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 23, 2020
 *
 */
public interface InsuranceDao {

	PatientDetails getConsultDetailByID(UcafReportParam reportParam);

	PatientDetails getConsult4EachSpeciality(UcafReportParam reportParam);

	List<ServiceVO> getAllConsultDiagnosis(UcafReportParam reportParam);

	List<ServiceVO> getAllConsultProcedure(UcafReportParam reportParam);

	List<ServiceVO> getAllConsultLabTest(UcafReportParam reportParam);

	List<ConsultMedicine> getAllConsultMedicine(UcafReportParam reportParam);

	PatientDetails getRegistrationDetails(PatientDetails regBean);

	String getInsuranceCardAttachment(PatientDetails regBean);

	List<VitalSigns> getVitalSigns(PatientDetails regBean);

	List<String> getAllChiefComplaintsByConsultId(Integer consultId);

	List<VitalSigns> getPatientVisitDtlsByType(VitalSigns hopiBean);

	String getSubInsurarNameById(Integer subInsurarId);
	
}
