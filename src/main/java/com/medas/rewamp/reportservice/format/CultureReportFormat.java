package com.medas.rewamp.reportservice.format;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;

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
	
	public PdfPTable printFormatTwoResult(ArrayList<RegistrationBean> list, RegistrationBean regiBean) throws Exception;
	public PdfPTable printFormatOneReport(ArrayList<RegistrationBean> list, String lname) throws Exception;
	public PdfPTable printProfileName(String profileName) throws Exception;
	public PdfPTable getDataTable4AntibioAndOrganismNames(RegistrationBean regiBean) throws Exception;
	public PdfPTable printOrganismAndAntibiotic(ArrayList<RegistrationBean> list, RegistrationBean regiBean) throws Exception;
	public PdfPTable printSensitivity(ArrayList<RegistrationBean> list) throws Exception;
	public PdfPTable printRemarks(String remarks) throws Exception;
	public PdfPTable getBlankLine(float fixedLeading) throws Exception;
	
	@SuppressWarnings("unchecked")
	public static CultureReportFormat getReportClass(String reportImplementationClass) {
		CultureReportFormat instance = null;
		try {
			Class<CultureReportFormat> clazz = (Class<CultureReportFormat>) Class.forName(reportImplementationClass);
			instance = clazz.newInstance();
		} catch (ClassNotFoundException cnfe) {
			log.error(cnfe.getMessage(), cnfe);
			instance = new CommonCultureReportFormatTwo();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return instance;
	}
}
