package com.medas.rewamp.reportservice.persistence.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.medas.rewamp.reportservice.business.constants.InsuranceQueryContstants;
import com.medas.rewamp.reportservice.business.vo.ConsultMedicine;
import com.medas.rewamp.reportservice.business.vo.PatientDetails;
import com.medas.rewamp.reportservice.business.vo.ServiceVO;
import com.medas.rewamp.reportservice.business.vo.VitalSigns;
import com.medas.rewamp.reportservice.business.vo.insurance.UcafReportParam;
import com.medas.rewamp.reportservice.persistence.InsuranceDao;
import com.medas.rewamp.reportservice.utils.QueryUtil;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 23, 2020
 *
 */
@Repository
@SuppressWarnings({"deprecation", "unchecked"})
public class InsuranceDaoImpl implements InsuranceDao {
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional
	private Session getSession() {
		return em.unwrap(Session.class);
	}

	@Override
	public PatientDetails getConsultDetailByID(UcafReportParam reportParam) {
		return (PatientDetails) getSession().createNativeQuery(InsuranceQueryContstants.getConsultDetailByID)
				.setParameter("consultId", reportParam.getConsultId())
				.setResultTransformer(Transformers.aliasToBean(PatientDetails.class)).getSingleResult();
	}
	
	@Override
	public PatientDetails getConsult4EachSpeciality(UcafReportParam reportParam) {
		return QueryUtil.skipNREWithRT(getSession().createNativeQuery(InsuranceQueryContstants.getConsultForUcaf)
				.setParameter("consultId", reportParam.getConsultId()), PatientDetails.class, null);
	}

	@Override
	public List<ServiceVO> getAllConsultDiagnosis(UcafReportParam reportParam) {
		return getSession().createNativeQuery(InsuranceQueryContstants.getAllConsultDiagnosis)
				.setParameter("consultId", reportParam.getConsultId())
				.setResultTransformer(Transformers.aliasToBean(ServiceVO.class)).getResultList();
	}

	@Override
	public List<ServiceVO> getAllConsultProcedure(UcafReportParam reportParam) {
		return getSession().createNativeQuery(InsuranceQueryContstants.getAllConsultProcedure)
				.setParameter("consultId", reportParam.getConsultId())
				.setResultTransformer(Transformers.aliasToBean(ServiceVO.class)).getResultList();
	}

	@Override
	public List<ServiceVO> getAllConsultLabTest(UcafReportParam reportParam) {
		return getSession().createNativeQuery(InsuranceQueryContstants.getAllConsultLabTest)
				.setParameter("consultId", reportParam.getConsultId())
				.setResultTransformer(Transformers.aliasToBean(ServiceVO.class)).getResultList();
	}

	@Override
	public String getInsuranceCardAttachment(PatientDetails regBean) {
		return QueryUtil.skipNRE(getSession().createNativeQuery(InsuranceQueryContstants.getInsuranceCardAttachment)
				.setParameter("opNumber", regBean.getOp_number())
				.setParameter("networkOfficeId", regBean.getNetworkoffice_id()), String.class, null);
	}

	@Override
	public List<VitalSigns> getVitalSigns(PatientDetails regBean) {
		return getSession().createNativeQuery(InsuranceQueryContstants.getVitalSigns)
				.setParameter("consultId", regBean.getConsult_id())
				.setResultTransformer(Transformers.aliasToBean(VitalSigns.class)).getResultList();
	}

	@Override
	public List<VitalSigns> getPatientVisitDtlsByType(VitalSigns hopiBean) {
		return getSession().createNativeQuery(InsuranceQueryContstants.getPatientVisitDtlsByType)
				.setParameter("consultId", hopiBean.getConsult_id())
				.setParameter("type", hopiBean.getType()).getResultList();
	}

	@Override
	public List<String> getAllChiefComplaintsByConsultId(Integer consultId) {
		return getSession().createNativeQuery(InsuranceQueryContstants.getAllChiefComplaintsByConsultId)
				.setParameter("consultId", consultId).getResultList();
	}

	@Override
	public List<ConsultMedicine> getAllConsultMedicine(UcafReportParam reportParam) {
		return Collections.emptyList();
	}

	@Override
	public PatientDetails getRegistrationDetails(PatientDetails regBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubInsurarNameById(Integer subInsurarId) {
		// "SELECT insurar_subname FROM insurar_sub where   insurar_sub='" + regBean.getInsurar_sub() + "'"
		return null;
	}

}
