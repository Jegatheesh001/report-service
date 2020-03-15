package com.medas.rewamp.reportservice.format;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.utils.ReportInstance;

/**
 * @author Jegatheesh <br>
 *         Created on 2019-06-27
 *
 */
public interface TestReportFormat {
	
	public Logger log = LoggerFactory.getLogger(TestReportFormat.class);

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

	int alignLeft = Element.ALIGN_LEFT, alignRight = Element.ALIGN_RIGHT, alignCenter = Element.ALIGN_CENTER;
	
	public PdfPTable getHeaderTable() throws Exception;

	public PdfPTable getProfileName(String profileName, boolean underline) throws Exception;

	public PdfPTable getCategoryName(String categoryName) throws Exception;

	public PdfPTable getTestName(String categoryName) throws Exception;

	public PdfPTable getSampleType(String sampleType) throws Exception;

	public PdfPTable getFormatHeader(String format) throws Exception;
	public PdfPTable getFormatOneHeader() throws Exception;
	public PdfPTable getFormatTwoHeader() throws Exception;

	public PdfPTable getFormatOneData(List<LabReportData> list) throws Exception;
	public PdfPTable getFormatOneData(List<LabReportData> list, PdfPTable table) throws Exception;
	public PdfPTable getFormatTwoData(List<LabReportData> list) throws Exception;

	public void getFormatThreeData(Document document, LabReportData testDetails) throws Exception;
	
	public PdfPTable getRemarksTable(String remarks) throws Exception;
	
	public void setNotes(Document document, String notes) throws Exception;
	
	public PdfPTable getFooterTable() throws Exception;
	
	public static TestReportFormat getReportClass(String reportImplementationClass) {
		return ReportInstance.getReportClass(reportImplementationClass);
	}
}
