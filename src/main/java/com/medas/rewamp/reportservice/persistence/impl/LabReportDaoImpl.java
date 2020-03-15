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
				.setParameter("testDetailsids", params.getTestDetailsids())
				.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public List<LabReportData> getLabtestResultsForReport(LabReportData param) {
		StringBuilder queryBuilder = new StringBuilder(LabQueryContstants.getLabtestResultsForReport);
		Map<String, Object> params = new HashMap<>();
		params.put("testDetailsids", param.getTestDetailsids());
		if(param.getHide() != null) {
			queryBuilder.append("and tr.is_hide = :hide ");
			params.put("hide", param.getHide());
		}
		Query<?> query = getSession().createNativeQuery(queryBuilder.toString());
		params.forEach(query::setParameter);
		return (List<LabReportData>) query.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public String getClinicReportFormat(Integer clinicId) {
		return (String) getSession().createNativeQuery(LabQueryContstants.getClinicReportFormat)
				.setParameter("clinicId", clinicId).getSingleResult();
	}

	@Override
	public List<LabReportData> getProfileTestsTree(LabReportData params) {
		return getSession().createNativeQuery(LabQueryContstants.getProfileTestsTree)
				.setParameter("testDetailsids", params.getTestDetailsids())
				.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public List<LabReportData> getTestBasicDetails(LabReportData params) {
		return getSession().createNativeQuery(LabQueryContstants.getTestBasicDetails)
				.setParameter("profileIds", params.getProfileIds())
				.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public List<LabReportData> getLabtestDetailsForReportProfile(LabReportData params) {
		return getSession().createNativeQuery(LabQueryContstants.getLabtestDetailsForReportProfile)
				.setParameter("officeId", params.getOffice_id())
				.setParameter("testDetailsids", params.getTestDetailsids())
				.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public List<LabReportData> getProfileOrderByTestDetails(LabReportData params) {
		return getSession().createNativeQuery(LabQueryContstants.getProfileOrderByTestDetails)
				.setParameter("profileId", params.getProfile_id())
				.setParameter("testDetailsids", params.getTestDetailsids())
				.setResultTransformer(Transformers.aliasToBean(LabReportData.class)).getResultList();
	}

	@Override
	public UserBean getUserDetailByUserID(String userId) {
		return (UserBean) getSession().createNativeQuery(LabQueryContstants.getUserDetailByUserID)
				.setParameter("userId", userId)
				.setResultTransformer(Transformers.aliasToBean(UserBean.class)).getSingleResult();
	}

	@Override
	public String getReportClassForClinic(QueryParam param) {
		return (String) getSession().createNativeQuery(LabQueryContstants.getReportClassForClinic)
				.setParameter("clinicId", param.getOfficeId())
				.setParameter("testId", param.getId()).getSingleResult();
	}

	@Override
	public List<RegistrationBean> getAntibioAndOrganisms(RegistrationBean registrationBean) {
		return getSession().createNativeQuery(LabQueryContstants.getAntibioAndOrganisms)
				.setParameter("labIdno", registrationBean.getLab_idno())
				.setParameter("testId", registrationBean.getTestId())
				.setResultTransformer(Transformers.aliasToBean(RegistrationBean.class)).getResultList();
	}

	@Override
	public RegistrationBean getTestDetailsById(RegistrationBean registrationBean) {
		return (RegistrationBean) getSession().createNativeQuery(LabQueryContstants.getTestDetailsById)
				.setParameter("testDetailsid", registrationBean.getTestDetailsid())
				.setResultTransformer(Transformers.aliasToBean(RegistrationBean.class)).getSingleResult();
	}

	@Override
	public RegistrationBean getReferDoctorById(RegistrationBean setBean) {
		return (RegistrationBean) getSession().createNativeQuery(LabQueryContstants.getReferDoctorById)
				.setParameter("rdoctorId", setBean.getRdoctor_id())
				.setResultTransformer(Transformers.aliasToBean(RegistrationBean.class)).getSingleResult();
	}

	@Override
	public RegistrationBean getClinicById(Integer clinicId) {
		return (RegistrationBean) getSession().createNativeQuery(LabQueryContstants.getClinicById)
				.setParameter("clinicId", clinicId)
				.setResultTransformer(Transformers.aliasToBean(RegistrationBean.class)).getSingleResult();
	}

	@Override
	public String getMicroTestRemarks(String labIdno) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String isDepartmentLab(Integer departmentId) {
		return (String) getSession().createNativeQuery(LabQueryContstants.isDepartmentLab)
				.setParameter("departmentId", departmentId).getSingleResult();
	}

	@Override
	public Integer getPathologistByOffice(Integer officeId) {
		return (Integer) getSession().createNativeQuery(LabQueryContstants.getPathologistByOffice)
				.setParameter("officeId", officeId).getSingleResult();
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
		return getSession().createNativeQuery(LabQueryContstants.getOrganismNames)
				.setParameter("labIdno", regBean.getLab_idno())
				.setParameter("testId", regBean.getTestId())
				.setResultTransformer(Transformers.aliasToBean(RegistrationBean.class)).getResultList();
	}

	@Override
	public List<RegistrationBean> getAntibioticDetailsByCriteria(RegistrationBean organism) {
		// TODO Auto-generated method stub
		return null;
	}

}
