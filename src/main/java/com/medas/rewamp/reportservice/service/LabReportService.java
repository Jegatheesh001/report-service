package com.medas.rewamp.reportservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.QueryParam;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.business.vo.UserBean;
import com.medas.rewamp.reportservice.persistence.LabReportDao;

@Service
public class LabReportService {
	
	@Autowired
	private LabReportDao dao;

	public OfficeLetterHeadBean getOfficeLetterHead(Integer officeId) {
		return dao.getOfficeLetterHead(officeId);
	}

	public String getTestDetailsIdsByCriteria(LabReportData params) {
		return dao.getTestDetailsIdsByCriteria(params);
	}

	public List<LabReportData> getLabtestDetailsForReport(LabReportData params) {
		return dao.getLabtestDetailsForReport(params);
	}

	public List<LabReportData> getLabtestResultsForReport(LabReportData params) {
		return dao.getLabtestResultsForReport(params);
	}

	public String getClinicReportFormat(String clinicId) {
		return dao.getClinicReportFormat(clinicId);
	}

	public List<LabReportData> getProfileTestsTree(LabReportData params) {
		return dao.getProfileTestsTree(params);
	}

	public List<LabReportData> getTestBasicDetails(LabReportData params) {
		return dao.getTestBasicDetails(params);
	}

	public List<LabReportData> getLabtestDetailsForReportProfile(LabReportData params) {
		return dao.getLabtestDetailsForReportProfile(params);
	}

	public List<LabReportData> getProfileOrderByTestDetails(LabReportData params) {
		return dao.getProfileOrderByTestDetails(params);
	}

	public UserBean getUserDetailByUserID(String userId) {
		return dao.getUserDetailByUserID(userId);
	}

	public String getReportClassForClinic(QueryParam param) {
		return dao.getReportClassForClinic(param);
	}

	public List<RegistrationBean> getAntibioAndOrganisms(RegistrationBean registrationBean) {
		return dao.getAntibioAndOrganisms(registrationBean);
	}

	public RegistrationBean getTestDetailsById(RegistrationBean registrationBean) {
		return dao.getTestDetailsById(registrationBean);
	}

	public RegistrationBean getReferDoctorById(RegistrationBean setBean) {
		return dao.getReferDoctorById(setBean);
	}

	public RegistrationBean getClinicById(String clinic_id) {
		return dao.getClinicById(clinic_id);
	}

	public String getMicroTestRemarks(String lab_idno) {
		// fetchValueWithSingleParam("micro_test_remarks", "remarks", "lab_idno", lab_idno, null, null);
		return dao.getMicroTestRemarks(lab_idno);
	}

	public String isDepartmentLab(Integer Department_id) {
		// fetchValueWithSingleParam("department_setup", "dept_lab", "department_id", registrationBean.getDepartment_id(), null, null)
		return dao.isDepartmentLab(Department_id);
	}

	public String getPathologistByOffice(Integer office_id) {
		// common.fetchValueWithSingleParam("doctors_office", "doctors_id", "office_id", registrationBean.getOffice_id(), null, " doctors_office.lab_pathologist='Y'");
		return dao.getPathologistByOffice(office_id);
	}

	public List<RegistrationBean> getAllAbnormalResults(RegistrationBean abnormBean) {
		return dao.getAllAbnormalResults(abnormBean);
	}

	public String getReferenceRangeId(String valueOf, Integer age) {
		return dao.getReferenceRangeId(valueOf, age);
	}

	public String getMappedResultsetGender(String reference_id) {
		return dao.getMappedResultsetGender(reference_id);
	}

	public String getMappedResultsetValue(String reference_id, String sexType, Double result) {
		return dao.getMappedResultsetValue(reference_id, sexType, result);
	}

	public List<RegistrationBean> getOrganismNames(RegistrationBean regBean) {
		return dao.getOrganismNames(regBean);
	}

}
