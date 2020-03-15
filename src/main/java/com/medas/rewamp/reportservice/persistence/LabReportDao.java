package com.medas.rewamp.reportservice.persistence;

import java.util.List;

import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.QueryParam;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.business.vo.UserBean;

public interface LabReportDao {

	OfficeLetterHeadBean getOfficeLetterHead(Integer officeId);

	String getTestDetailsIdsByCriteria(LabReportData params);

	List<LabReportData> getLabtestDetailsForReport(LabReportData params);

	List<LabReportData> getLabtestResultsForReport(LabReportData params);

	String getClinicReportFormat(Integer clinicId);

	List<LabReportData> getProfileTestsTree(LabReportData params);

	List<LabReportData> getTestBasicDetails(LabReportData params);

	List<LabReportData> getLabtestDetailsForReportProfile(LabReportData params);

	List<LabReportData> getProfileOrderByTestDetails(LabReportData params);

	UserBean getUserDetailByUserID(String userId);

	String getReportClassForClinic(QueryParam param);

	List<RegistrationBean> getAntibioAndOrganisms(RegistrationBean registrationBean);

	RegistrationBean getTestDetailsById(RegistrationBean registrationBean);

	RegistrationBean getReferDoctorById(RegistrationBean setBean);

	RegistrationBean getClinicById(Integer clinicId);

	String getMicroTestRemarks(String labIdno);

	String isDepartmentLab(Integer departmentId);

	Integer getPathologistByOffice(Integer officeId);

	List<RegistrationBean> getAllAbnormalResults(RegistrationBean abnormBean);

	String getReferenceRangeId(String valueOf, Integer age);

	String getMappedResultsetGender(String referenceId);

	String getMappedResultsetValue(String referenceId, String sexType, Double result);

	List<RegistrationBean> getOrganismNames(RegistrationBean regBean);

	List<RegistrationBean> getAntibioticDetailsByCriteria(RegistrationBean organism);

}
