package com.medas.rewamp.reportservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.QueryParam;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.business.vo.UserBean;

@Service
public class LabReportService {

	public OfficeLetterHeadBean getOfficeLetterHead(Integer officeId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTestDetailsIdsByCriteria(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LabReportData> getLabtestDetailsForReport(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LabReportData> getLabtestResultsForReport(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getClinicReportFormat(String clinicId) {
		// TODO Auto-generated method stub
		return clinicId;
	}

	public List<LabReportData> getProfileTestsTree(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LabReportData> getTestBasicDetails(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LabReportData> getLabtestDetailsForReportProfile(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LabReportData> getProfileOrderByTestDetails(LabReportData param) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserBean getUserDetailByUserID(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReportClassForClinic(QueryParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<RegistrationBean> getAntibioAndOrganisms(RegistrationBean registrationBean) {
		// TODO Auto-generated method stub
		return null;
	}

	public RegistrationBean getTestDetailsById(RegistrationBean registrationBean) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMicroTestRemarks(String lab_idno) {
		// fetchValueWithSingleParam("micro_test_remarks", "remarks", "lab_idno", lab_idno, null, null);
		// TODO Auto-generated method stub
		return null;
	}

	public String isDepartmentLab(Integer Department_id) {
		// fetchValueWithSingleParam("department_setup", "dept_lab", "department_id", registrationBean.getDepartment_id(), null, null)
		// TODO Auto-generated method stub
		return null;
	}

	public RegistrationBean getReferDoctorById(RegistrationBean setBeanRef) {
		// TODO Auto-generated method stub
		return null;
	}

	public RegistrationBean getClinicById(String clinic_id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPathologistByOffice(Integer office_id) {
		// common.fetchValueWithSingleParam("doctors_office", "doctors_id", "office_id", registrationBean.getOffice_id(), null, " doctors_office.lab_pathologist='Y'");
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<RegistrationBean> getAllAbnormalResults(RegistrationBean abnormBean) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReferenceRangeId(String valueOf, int age) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMappedResultsetGender(String reference_id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMappedResultsetValue(String reference_id, String sexType, Double result) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<RegistrationBean> getOrganismNames(RegistrationBean regiBean) {
		// TODO Auto-generated method stub
		return null;
	}

}
