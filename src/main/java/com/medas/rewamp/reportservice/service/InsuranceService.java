package com.medas.rewamp.reportservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medas.rewamp.reportservice.business.vo.ConsultMedicine;
import com.medas.rewamp.reportservice.business.vo.PatientDetails;
import com.medas.rewamp.reportservice.business.vo.ServiceVO;
import com.medas.rewamp.reportservice.business.vo.VitalSigns;
import com.medas.rewamp.reportservice.business.vo.insurance.UcafReportParam;
import com.medas.rewamp.reportservice.persistence.InsuranceDao;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 22, 2020
 *
 */
@Service
public class InsuranceService {
	
	@Autowired
	private InsuranceDao dao;

	public PatientDetails getConsultDetailByID(UcafReportParam reportParam) {
		return dao.getConsultDetailByID(reportParam);
	}

	public PatientDetails getConsult4EachSpeciality(UcafReportParam reportParam) {
		return dao.getConsult4EachSpeciality(reportParam);
	}

	public List<ServiceVO> getAllConsultDiagnosis(UcafReportParam reportParam) {
		return dao.getAllConsultDiagnosis(reportParam);
	}

	public List<ServiceVO> getAllConsultProcedure(UcafReportParam reportParam) {
		return dao.getAllConsultProcedure(reportParam);
	}

	public List<ServiceVO> getAllConsultLabTest(UcafReportParam reportParam) {
		return dao.getAllConsultLabTest(reportParam);
	}

	public List<ConsultMedicine> getAllConsultMedicine(UcafReportParam reportParam) {
		return dao.getAllConsultMedicine(reportParam);
	}

	public String getInsuranceCardAttachment(PatientDetails regBean) {
		return dao.getInsuranceCardAttachment(regBean);
	}

	public VitalSigns getVitalSigns(PatientDetails regBean) {
		List<VitalSigns> vitals = dao.getVitalSigns(regBean);
		VitalSigns vitalSign = new VitalSigns();
		String bloodPressure = "";
		String bloodPressureSyst = "";
		String bloodPressureDia = "";
		if (!vitals.isEmpty()) {
			for (VitalSigns vital : vitals) {
				if (vital.getVital_sign().equalsIgnoreCase("Duration Of Illness")) {
					vitalSign.setDuration_of_ill(vital.getVital_value());
				} else if (vital.getVital_sign().equalsIgnoreCase("Pulse")) {
					vitalSign.setPulse(vital.getVital_value());
				} else if (vital.getVital_sign().equalsIgnoreCase("Temp")) {
					vitalSign.setTemperature(vital.getVital_value());
				} else if (vital.getVital_sign().equalsIgnoreCase("Blood Pressure")) {
					bloodPressure = vital.getVital_value();
				} else if (vital.getVital_sign().contains("Sys")) {
					if (!bloodPressure.trim().equals("")) {
						bloodPressure = bloodPressure.concat("/").concat(vital.getVital_value());
						bloodPressureSyst = vital.getVital_value();
					} else {
						bloodPressure = vital.getVital_value();
						bloodPressureSyst = vital.getVital_value();
					}
				} else if (vital.getVital_sign().contains("Dia")) {
					if (!bloodPressure.trim().equals("")) {
						bloodPressure = bloodPressure.concat("/").concat(vital.getVital_value());
						bloodPressureDia = vital.getVital_value();
					} else {
						bloodPressure = vital.getVital_value();
						bloodPressureDia = vital.getVital_value();
					}
				}
			}
			if (!bloodPressure.trim().equals("")) {
				vitalSign.setBloodPressureSyst(bloodPressureSyst);
				vitalSign.setBloodpressure_dia(bloodPressureDia);
				vitalSign.setBloodpressure(bloodPressure);
			}
		}
		return vitalSign;
	}

	public String getAllChiefComplaintsByConsultId(Integer consultId) {
		List<String> list = dao.getAllChiefComplaintsByConsultId(consultId);
		StringBuilder complaints = new StringBuilder();
		for (String complaint : list) {
			if (complaint != null && !complaint.trim().isEmpty()) {
				complaints.append(complaint.replace("<br />", "\n  "));
			}
		}
		return complaints.toString();
	}

	public List<VitalSigns> getPatientVisitDtlsByType(VitalSigns hopiBean) {
		return dao.getPatientVisitDtlsByType(hopiBean);
	}

	public PatientDetails getRegistrationDetails(PatientDetails regBean) {
		return dao.getRegistrationDetails(regBean);
	}
	
	public String getSubInsurarNameById(Integer subInsurarId) {
		return dao.getSubInsurarNameById(subInsurarId);
	}
	
}
