package com.medas.rewamp.reportservice.format;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.utils.ReportInstance;

import eclinic.laboratory.presentation.action.reports.iface.impl.CommonCultureReportFormatTwo;

public interface CultureReportFormat {
	
	public Logger log = LoggerFactory.getLogger(CultureReportFormat.class);
	
	public float normalFont = 10.0F;
	public float catHeadFont = 12.0F;
	public float normalBold = 9.0F;
	public float sensitivityFont = 8.0F;
	
	BaseColor bordorWhite = new BaseColor(255, 255, 255);
	BaseColor bordorBlack = new BaseColor(0, 0, 0);
	
	Font HELVETICA_BOLDOblique_10 = FontFactory.getFont("Helvetica-BoldOblique", 10);
	Font HELVETICA_BOLD_10 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
	Font HELVETICA_BOLD_9 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, normalBold);
	Font HELVETICA_9 = FontFactory.getFont(FontFactory.HELVETICA, 9);
	Font HELVETICA_5= FontFactory.getFont(FontFactory.HELVETICA, 5);
	
	int alignLeft = Element.ALIGN_LEFT, alignRight = Element.ALIGN_RIGHT, alignCenter = Element.ALIGN_CENTER;
	
	public PdfPTable printFormatTwoResult(List<RegistrationBean> list, RegistrationBean regiBean) throws Exception;
	public PdfPTable printFormatOneReport(List<RegistrationBean> list, String lname) throws Exception;
	public PdfPTable printProfileName(String profileName) throws Exception;
	public PdfPTable getDataTable4AntibioAndOrganismNames(RegistrationBean regiBean) throws Exception;
	public PdfPTable printOrganismAndAntibiotic(List<RegistrationBean> list, RegistrationBean regiBean) throws Exception;
	public PdfPTable printSensitivity(List<RegistrationBean> list) throws Exception;
	public PdfPTable printRemarks(String remarks) throws Exception;
	public PdfPTable getBlankLine(float fixedLeading) throws Exception;
	
	public static CultureReportFormat getReportClass(String reportImplementationClass) {
		return ReportInstance.getCultureReportClass(reportImplementationClass);
	}
}
