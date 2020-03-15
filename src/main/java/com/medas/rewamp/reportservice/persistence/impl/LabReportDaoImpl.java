package com.medas.rewamp.reportservice.persistence.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.medas.rewamp.reportservice.business.constants.LabQueryContstants;
import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.QueryParam;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.business.vo.UserBean;
import com.medas.rewamp.reportservice.persistence.LabReportDao;

@SuppressWarnings({"unchecked", "deprecation"})
@Repository
public class LabReportDaoImpl implements LabReportDao {
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional
	private Session getSession() {
		return em.unwrap(Session.class);
	}

	@Override
	public OfficeLetterHeadBean getOfficeLetterHead(Integer officeId) {
		return (OfficeLetterHeadBean) getSession().createNativeQuery(LabQueryContstants.getOfficeLetterHead)
				.setParameter("officeId", officeId)
				.setResultTransformer(Transformers.aliasToBean(OfficeLetterHeadBean.class)).getSingleResult();
	}

	@Override
	public String getTestDetailsIdsByCriteria(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LabReportData> getLabtestDetailsForReport(LabReportData params) {
		return getSession().createNativeQuery(LabQueryContstants.getLabtestDetailsForReport)
				.setParameter("testDetailsids", params.getTest_detailsids())
				.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public List<LabReportData> getLabtestResultsForReport(LabReportData param) {
		StringBuilder queryBuilder = new StringBuilder(LabQueryContstants.getLabtestResultsForReport);
		Map<String, Object> params = new HashMap<>();
		params.put("testDetailsids", param.getTest_detailsids());
		if(param.getHide() != null) {
			queryBuilder.append("and tr.is_hide = :hide ");
			params.put("hide", param.getHide());
		}
		Query<?> query = getSession().createNativeQuery(queryBuilder.toString());
		params.forEach(query::setParameter);
		return (List<LabReportData>) query.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public String getClinicReportFormat(String clinicId) {
		return (String) getSession().createNativeQuery(LabQueryContstants.getClinicReportFormat)
				.setParameter("clinicId", clinicId).getSingleResult();
	}

	@Override
	public List<LabReportData> getProfileTestsTree(LabReportData params) {
		return getSession().createNativeQuery(LabQueryContstants.getProfileTestsTree)
				.setParameter("testDetailsids", params.getTest_detailsids())
				.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public List<LabReportData> getTestBasicDetails(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LabReportData> getLabtestDetailsForReportProfile(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LabReportData> getProfileOrderByTestDetails(LabReportData params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserBean getUserDetailByUserID(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReportClassForClinic(QueryParam param) {
		return (String) getSession().createNativeQuery(LabQueryContstants.getReportClassForClinic)
				.setParameter("clinicId", param.getOfficeId())
				.setParameter("testId", param.getId()).getSingleResult();
	}

	@Override
	public List<RegistrationBean> getAntibioAndOrganisms(RegistrationBean registrationBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegistrationBean getTestDetailsById(RegistrationBean registrationBean) {
		return (RegistrationBean) getSession().createNativeQuery(LabQueryContstants.getTestDetailsById)
				.setParameter("testDetailsid", registrationBean.getTestDetailsid())
				.setResultTransformer(Transformers.aliasToBean(RegistrationBean.class)).getSingleResult();
	}

	@Override
	public RegistrationBean getReferDoctorById(RegistrationBean setBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegistrationBean getClinicById(String clinicId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMicroTestRemarks(String labIdno) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String isDepartmentLab(Integer departmentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathologistByOffice(Integer officeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistrationBean> getAllAbnormalResults(RegistrationBean abnormBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReferenceRangeId(String valueOf, Integer age) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMappedResultsetGender(String referenceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMappedResultsetValue(String referenceId, String sexType, Double result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistrationBean> getOrganismNames(RegistrationBean regBean) {
		// TODO Auto-generated method stub
		return null;
	}

}
