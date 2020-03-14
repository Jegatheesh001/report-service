package com.medas.rewamp.reportservice.format;

import static com.medas.rewamp.reportservice.utils.StringUtils.isEmpty;
import static com.medas.rewamp.reportservice.utils.ItextPdfCellFactory.*;

import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.business.vo.UserBean;
import com.medas.rewamp.reportservice.service.LabReportService;
import com.medas.rewamp.reportservice.utils.BarcodePdf;

/**
 * @author Jegatheesh <br>
 *         Created on 2019-06-27
 *
 */
public class CommonTestReportFormat implements TestReportFormat {
	
	DecimalFormat df0 = new DecimalFormat("#########");
	
	@Autowired
	private LabReportService reportService;
	
	@Autowired
	private BarcodePdf barcodeService;
	
	@Override
	public PdfPTable getHeaderTable(HttpServletRequest request) throws Exception {
		RegistrationBean registrationBean = (RegistrationBean) request.getAttribute("headerDetails");
		if(registrationBean == null) {
			return null;
		}
		
		String path = null;
		Image img = null;
		Image img1 = null;

		OfficeLetterHeadBean officeLetterHeadBean = (OfficeLetterHeadBean) request.getAttribute("officeLetterHeadBean");
		path = (String) request.getAttribute("imagePath");
		PdfPTable blankdatatable = new PdfPTable(8);
		int[] headerwidths = { 13, 1, 20, 21, 15, 2, 15, 20 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);

		String fname = path;
		fname = path + officeLetterHeadBean.getReport_header_logo();

		PdfPCell cell = new PdfPCell();
		try {
			img = Image.getInstance(fname);
			img.scalePercent(55);
			img.setAlignment(0);
		} catch (Exception localException) {
		}
		if (img != null) {
			cell = new PdfPCell(img);
		} else {
			cell = getCellForReport(getPhrase("\n\n\n", FontFactory.getFont("Helvetica", 11.0F)));
		}
		cell.setBorderWidth(0.0F);
		cell.setColspan(7);
		cell.setRowspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		try {
			fname = barcodeService.generateBarCode(registrationBean.getLab_idno());
			img1 = Image.getInstance(fname);
			img1.scalePercent(40);
			img1.setAlignment(0);
		} catch (Exception localException1) {
		}
		if (img1 != null) {
			cell = new PdfPCell(img1);
		} else {
			cell = getCellForReport(getPhrase("", FontFactory.getFont("Helvetica", 11.0F)));
		}
		cell.setBorderWidth(0.0F);
		cell.setRowspan(1);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(1);
		blankdatatable.addCell(cell);

		String reportStatus = "";
		if (registrationBean.getClosed_status().equals("N")) {
			reportStatus = "Preliminary Report";
		} else if (registrationBean.getClosed_status().equals("Y")) {
			if (registrationBean.getDispatch_status().equals("N")) {
				reportStatus = "Not Dispatched";
			} else if (registrationBean.getDispatch_status().equals("Y")) {
				reportStatus = "Final Report";
			}
		}
		if ("Y".equals(registrationBean.getReverse_authent())) {
			reportStatus += " (A)";
		}
		float fixedLeading = 10;
		cell = getCellForReport(getPhrase("  " + reportStatus, FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		cell.setRowspan(1);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("LABORATORY REPORT", FontFactory.getFont("Helvetica-Bold", 11.0F)), fixedLeading);
		cell.setBorderWidth(0.0F);
		cell.setColspan(8);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(1);
		blankdatatable.addCell(cell);

		cell = lineBreak();
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		cell.setColspan(8);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("Name", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(registrationBean.getPatient_name(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setColspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("File. No.", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(registrationBean.getOp_number(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setColspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("Age/Sex", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(registrationBean.getPatient_age() + "/" + registrationBean.getSex(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setColspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("Referral Doctor", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(registrationBean.getDoctors_name(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setColspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		if (registrationBean.getTest_format().equals("3")) {
			cell = getCellForReport(getPhrase("ID", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		} else {
			cell = getCellForReport(getPhrase("Lab No.", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		}
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		
		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(registrationBean.getBarcode_no(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setColspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("Referral Clinic", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(registrationBean.getClinic_name(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setColspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("Request Date", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(LabReportData.getFormatedDateTime(registrationBean.getEntered_date()), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setColspan(2);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase("Clinic File No", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(": ", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(registrationBean.getFile_no(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		cell.setColspan(2);
		blankdatatable.addCell(cell);
		
		cell = getCellForReport(getPhrase("Insurance", FontFactory.getFont("Helvetica", 10.0F)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		cell = getCellForReport(getPhrase(":", FontFactory.getFont("Helvetica", normalFont)), fixedLeading);
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(1);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);

		if(registrationBean.getInsurar_name()!=null && !registrationBean.getInsurar_name().equals("") ) {
			cell = getCellForReport(getPhrase(registrationBean.getInsurar_name(), FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
			cell.setBorderColor(bordorWhite);
			cell.setColspan(6);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
		}
		else {
			cell = getCellForReport(getPhrase("No", FontFactory.getFont("Helvetica-Bold", 10.0F)), fixedLeading);
			cell.setBorderColor(bordorWhite);
			cell.setColspan(6);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
		}
		

		cell = lineBreak();
		cell.setBorderColor(bordorWhite);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		cell.setColspan(8);
		blankdatatable.addCell(cell);

		return blankdatatable;
	}

	@Override
	public PdfPTable getProfileName(String profileName, boolean underline) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(new float[] {100});
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		
		PdfPCell cell = getCellForReport(getPhrase(profileName.toUpperCase(), FontFactory.getFont("Helvetica-Bold", 11.0F)), 10);
		cell.setPaddingBottom(2);
		if(underline)
			setBorderWidth(cell, new Integer[] {0, 0, 1, 0});
		else
			cell.setBorder(0);
		cell.setHorizontalAlignment(alignCenter);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		return blankdatatable;
	}

	@Override
	public PdfPTable getCategoryName(String categoryName) throws Exception {
		return getCategoryName(categoryName, new Integer[] {0, 0, 1, 0});
	}
	
	public PdfPTable getCategoryName(String categoryName, Integer[] border) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(new float[] {100});
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		
		PdfPCell cell = new PdfPCell();
		cell = getCellForReport(getPhrase(categoryName.toUpperCase(), FontFactory.getFont("Helvetica-Bold", 11.0F)), 10);
		cell.setPaddingBottom(2);
		if (border != null) {
			setBorderWidth(cell, border);
		} else {
			cell.setBorder(0);
		}
		cell.setHorizontalAlignment(alignCenter);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		return blankdatatable;
	}

	@Override
	public PdfPTable getTestName(String testName) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(new float[] {100});
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		
		PdfPCell cell = new PdfPCell();
		Chunk ch = new Chunk(testName.toUpperCase(), FontFactory.getFont("Helvetica-BoldOblique", 9.0F));
		ch.setUnderline(1f, -2f);
		Phrase phrase = new Phrase(ch);
		cell = getNoBorderCell(phrase, 8);
		cell.setBorder(0);
		cell.setHorizontalAlignment(alignLeft);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		return blankdatatable;
	}

	@Override
	public PdfPTable getSampleType(String sampleType) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(new float[] { 100 });
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		
		PdfPCell cell = new PdfPCell();
		cell = getNoBorderCell(getPhrase("      Sample Type : " + sampleType, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9)), 10);
		cell.setHorizontalAlignment(alignLeft);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		/*cell = getNoBorderCell(getPhrase(" ", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9)), 5);
		cell.setHorizontalAlignment(alignLeft);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);*/
		return blankdatatable;
	}
	
	@Override
	public PdfPTable getFormatHeader(String format, HttpServletRequest request) throws Exception {
		if("1".equals(format)) {
			return getFormatOneHeader(request);
		} else if("2".equals(format)) {
			return getFormatTwoHeader();
		}
		return null;
	}

	@Override
	public PdfPTable getFormatOneHeader(HttpServletRequest request) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(5);
		int[] headerwidths = { 30, 17, 15, 25, 13 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		
		float fontSize = 10.0F;
		Integer[] border = new Integer[] {0, 0, 1, 0};
		PdfPCell cell = new PdfPCell();
		cell = getCellForReport(getPhrase("Test Name ", FontFactory.getFont("Helvetica", fontSize)));
		cell.setPaddingBottom(4);
		setBorderWidth(cell, border);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		cell = getCellForReport(getPhrase("Result", FontFactory.getFont("Helvetica", fontSize)));
		setBorderWidth(cell, border);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		cell = getCellForReport(getPhrase("Units", FontFactory.getFont("Helvetica", fontSize)));
		setBorderWidth(cell, border);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		cell = getCellForReport(getPhrase("Ref. Range", FontFactory.getFont("Helvetica", fontSize)));
		setBorderWidth(cell, border);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		cell = getCellForReport(getPhrase("Method", FontFactory.getFont("Helvetica", fontSize)));
		setBorderWidth(cell, border);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		return blankdatatable;
	}

	@Override
	public PdfPTable getFormatTwoHeader() throws Exception {
		PdfPTable blankdatatable = new PdfPTable(2);
		int[] headerwidths = { 40, 60 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		
		float fontSize = 10.0F;
		Integer[] border = new Integer[] {0, 0, 1, 0};
		PdfPCell cell = new PdfPCell();
		cell = getCellForReport(getPhrase("Test Name ", FontFactory.getFont("Helvetica", fontSize)));
		cell.setPaddingBottom(4);
		setBorderWidth(cell, border);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		cell = getCellForReport(getPhrase("Result", FontFactory.getFont("Helvetica", fontSize)));
		setBorderWidth(cell, border);
		cell.setHorizontalAlignment(0);
		cell.setVerticalAlignment(0);
		blankdatatable.addCell(cell);
		return blankdatatable;
	}

	@Override
	public PdfPTable getFormatOneData(HttpServletRequest request, List<LabReportData> dataList) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(5);
		int[] headerwidths = { 30, 17, 15, 25, 13 };
		try {
			blankdatatable.setWidths(headerwidths);
			blankdatatable.setWidthPercentage(100f);
			blankdatatable.setTotalWidth(100f);

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return getFormatOneData(request, dataList, blankdatatable);
	}

	@Override
	public PdfPTable getFormatOneData(HttpServletRequest request, List<LabReportData> dataList, PdfPTable blankdatatable) throws Exception {
		float fontSize = 9.0F;
		float fixedLeading = 10;
		PdfPCell cell = null;
		for (LabReportData testResult : dataList) {
			if ("Y".equals(testResult.getGroup_name())) {
				Chunk ch = new Chunk(testResult.getParameter_name().toUpperCase(),
						FontFactory.getFont("Helvetica-BoldOblique", fontSize));
				ch.setUnderline(1f, -2f);
				Phrase phrase = new Phrase(ch);
				cell = getCellForReport(phrase, fixedLeading);
				cell.setPaddingBottom(4);
				cell.setBorder(0);
				cell.setHorizontalAlignment(0);
				cell.setVerticalAlignment(0);
				cell.setColspan(5);
				blankdatatable.addCell(cell);
				continue;
			}
			if(testResult.isHide())
				continue;

			if (testResult.getSingle_test() != null && testResult.getSingle_test().equals("Y") && !testResult.isDac())
			  cell = getCellForReport(getPhrase("*"+testResult.getParameter_name(), FontFactory.getFont("Helvetica", fontSize)), fixedLeading);
			else
			  cell = getCellForReport(getPhrase(testResult.getParameter_name(), FontFactory.getFont("Helvetica", fontSize)), fixedLeading); 
			//cell.setPaddingBottom(4);
			cell.setBorder(0);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
			if(testResult.isBold())
				cell = getCellForReport(getTestResult(request, testResult, FontFactory.getFont("Helvetica-Bold", fontSize)), fixedLeading);
			else
				cell = getCellForReport(getTestResult(request, testResult, FontFactory.getFont("Helvetica", fontSize)), fixedLeading);
			cell.setBorder(0);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
			cell = getCellForReport(getPhrase(testResult.getTest_unit(), FontFactory.getFont("Helvetica", fontSize)), fixedLeading);
			cell.setBorder(0);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
			cell = getCellForReport(getPhrase(testResult.getNormal_value(), FontFactory.getFont("Helvetica", fontSize)), fixedLeading);
			cell.setBorder(0);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
			cell = getCellForReport(getPhrase(testResult.getTest_method(), FontFactory.getFont("Helvetica", fontSize)), fixedLeading);
			cell.setBorder(0);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
		}
		return blankdatatable;
	}
	
	public Paragraph getTestResult(HttpServletRequest request, LabReportData testResult, Font font) {
		Paragraph para = null, temp = null;
		boolean numeric = false;
		double result = 0.0, min = 0.0, max = 0.0;
		int fontSize = 7;
		if ("N".equals(testResult.getResult_type())) {
			try {
				result = Double.parseDouble(testResult.getTest_result().replaceAll("[ > <]",""));
				min = Double.parseDouble(testResult.getMin_value());
				max = Double.parseDouble(testResult.getMax_value());
				numeric = true;
				Chunk ck = null;
				para = new Paragraph();
				if(testResult.getTest_result().trim().indexOf('>') == 0) {
					if (result >= max) {
						ck = new Chunk(" H");
						ck.setTextRise(5);
						ck.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize));
						para.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, font.getSize(), BaseColor.RED));
					}else {
						para.setFont(font);
					}
				}
				else if(testResult.getTest_result().trim().indexOf('<') == 0) {
					if(result <= min) {
						ck = new Chunk(" L");
						ck.setTextRise(5);
						ck.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize));
						para.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, font.getSize(), BaseColor.BLUE));
					} else {
						para.setFont(font);
					}
				}
				else {
					if (result > max) {
						ck = new Chunk(" H");
						ck.setTextRise(5);
						ck.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize));
						para.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, font.getSize(), BaseColor.RED));
					} else if(result < min) {
						ck = new Chunk(" L");
						ck.setTextRise(5);
						ck.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize));
						para.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, font.getSize(), BaseColor.BLUE));
					} else {
						para.setFont(font);
					}
				}
				
				// Decimal Formatting
				if (isEmpty(testResult.getDecimal_nos())) {
					para.add(testResult.getTest_result());
				} else {
					if(testResult.getDecimal_nos().equals("0")) {
						if(testResult.getTest_result().trim().indexOf('>') == 0){
							para.add(">"+df0.format(result));
						}
						else if(testResult.getTest_result().trim().indexOf('<') == 0) {
							para.add("<"+df0.format(result));
						}
						else {
							para.add(df0.format(result));
						}
						
					}
					else {
						if(testResult.getTest_result().trim().indexOf('>') == 0){
							para.add(">"+String.format("%." + testResult.getDecimal_nos().trim() + "f", result));
						}
						else if(testResult.getTest_result().trim().indexOf('<') == 0) {
							para.add("<"+String.format("%." + testResult.getDecimal_nos().trim() + "f", result));
						}
						else {
							para.add(String.format("%." + testResult.getDecimal_nos().trim() + "f", result));
						}
						
					}
					
				}
				para.add(ck);
				temp = criticalValueCheck(request, testResult, font);
				if (temp != null) {
					para.add(temp);
				}
			} catch (Exception ex) {
				
			}
		} else {
			para = abnormalDataCheck(testResult, font);
			if(para != null) {
				numeric = true;
			}
		}
		if(!numeric) {
			para = new Paragraph(testResult.getTest_result(), font);
		}
		return para;
	}

	/**
	 * Abnormal result check for test parameter
	 * 
	 * @param testResult
	 * @param font
	 * @return Paragraph
	 */
	private Paragraph abnormalDataCheck(LabReportData testResult, Font font) {
		Paragraph para = null;
		try {
			String abnormalResult = null;
			boolean abnormal = false;
			
			RegistrationBean abnormBean = new RegistrationBean();
			abnormBean.setLis_test_code(testResult.getTest_code());

			abnormBean.setLis_parameter_code(testResult.getParameter_code());
			
			ArrayList<RegistrationBean> abnormalTestList = reportService.getAllAbnormalResults(abnormBean);
			
			if (abnormalTestList != null && abnormalTestList.size() > 0) {
				for(RegistrationBean regBean1 : abnormalTestList) {
					
					abnormalResult = regBean1.getTest_Result();
					
			if (abnormalResult != null && testResult.getTest_result() != null) {
				boolean abnormalResults = abnormalResult.contains("-");
				boolean testResults = testResult.getTest_result().contains("-");

				if (abnormalResults && testResults) {
					String[] abnormalRange = abnormalResult.split("-");
					String[] testResultRange = testResult.getTest_result().split("-");

					/*if (!((Double.parseDouble(testResultRange[0].trim()) >= Double.parseDouble(abnormalRange[0].trim()))
							&& (Double.parseDouble(testResultRange[1].trim()) <= Double
									.parseDouble(abnormalRange[1].trim())))) {
						abnormal = true;
					}*/
					
					if (((Double.parseDouble(testResultRange[0].trim()) > Double.parseDouble(abnormalRange[0].trim()))
							&& (Double.parseDouble(testResultRange[1].trim()) > Double
									.parseDouble(abnormalRange[1].trim())))) {
						abnormal = true;
					}
					
				} else {
					if (abnormalResult != null && abnormalResult.equalsIgnoreCase(testResult.getTest_result())) {
						abnormal = true;
					} 
				}
				if(abnormal) {
					para = new Paragraph(testResult.getTest_result(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, font.getSize(), BaseColor.RED));
					break;
				}
			}}
			}
		} catch (Exception e) {
		}
		return para;
	}
	
	/**
	 * Critical value check for numeric results
	 * 
	 * @param request
	 * @param testResult
	 * @param font
	 * @return
	 */
	private Paragraph criticalValueCheck(HttpServletRequest request, LabReportData testResult, Font font) {
		RegistrationBean header = (RegistrationBean) request.getAttribute("headerDetails");
		int age = header.getAge();
		String sex = header.getSex();
		String reference_id = reportService.getReferenceRangeId(String.valueOf(testResult.getParam_mapping_id()), age);

		Paragraph para = null;
		if (reference_id != null) {
			String altres = null;
			String sexType = null;
			try {
				Double result = Double.valueOf(testResult.getTest_result());
				sexType = reportService.getMappedResultsetGender(reference_id);
				if (!"B".equals(sexType)) {
					sexType = "Male".equals(sex) ? "M" : "F";
				}
				altres = reportService.getMappedResultsetValue(reference_id, sexType, result);
				if (altres != null)
					para = new Paragraph(" [" + altres + "]", FontFactory.getFont(FontFactory.HELVETICA_BOLD, font.getSize(), BaseColor.RED));
			} catch (Exception e) {
			}
		}
		return para;
	}

	@Override
	public PdfPTable getFormatTwoData(HttpServletRequest request, List<LabReportData> dataList) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(2);
		int[] headerwidths = { 40, 60 };
		try {
			blankdatatable.setWidths(headerwidths);
			blankdatatable.setWidthPercentage(100f);
			blankdatatable.setTotalWidth(100f);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		if (dataList == null) return blankdatatable;

		float fontSize = 9.0F;
		float fixedLeading = 8;
		PdfPCell cell = new PdfPCell();
		Paragraph para = null;
		Font font = FontFactory.getFont("Helvetica", fontSize);;
		for (LabReportData testResult : dataList) {
			if(testResult.isHide())
				continue;
			
			para = null;
			cell = getCellForReport(getPhrase(testResult.getParameter_name(), FontFactory.getFont("Helvetica-Bold", fontSize)), fixedLeading);
			cell.setPaddingBottom(4);
			cell.setBorder(0);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
			
			if ("A".equals(testResult.getResult_type())) {
				para = abnormalDataCheck(testResult, font);
			}
			if(para != null) {
				cell = getCellForReport(para, fixedLeading);
			} else {
				cell = getCellForReport(getPhrase(testResult.getTest_result(), font), fixedLeading);
			}
			cell.setBorder(0);
			cell.setHorizontalAlignment(0);
			cell.setVerticalAlignment(0);
			blankdatatable.addCell(cell);
		}
		return blankdatatable;
	}

	@Override
	public void getFormatThreeData(Document document, LabReportData testDetails) throws Exception {
		document.add(getCategoryName(testDetails.getCategory_name(), null));
		
		// Highlighting abnormal results
		RegistrationBean abnormBean = new RegistrationBean();
		abnormBean.setLis_test_code(testDetails.getTest_code());
    	ArrayList<RegistrationBean> abnormalTestList = reportService.getAllAbnormalResults(abnormBean);
    	String remarks = testDetails.getRemarks();
		if (remarks != null && !remarks.trim().equals("null")) {
			if (abnormalTestList != null && !abnormalTestList.isEmpty()) {
				for (RegistrationBean ab : abnormalTestList) {
					if (remarks.contains(ab.getTest_Result().trim())) {
						remarks = remarks.replace(ab.getTest_Result().trim(), "<span style='color:#ff0000;'>Detected</span>");
					}
				}
			}
			parseHtmlToDocument(document, remarks);
		}
	}
	
	/**
	 * Parse html content to pdf document
	 * 
	 * @param document
	 * @param content
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private void parseHtmlToDocument(Document document, String content) throws Exception {
		Reader reader = new StringReader(content);
		List<Element> elements = HTMLWorker.parseToList(reader, new StyleSheet());
		// new XMLWorkerHelper().parseXHtml(document, document, reader);
		
		for (Element element : elements) {
			document.add(element);
		}
	}

	@Override
	public PdfPTable getFooterTable(HttpServletRequest request) throws Exception {
		OfficeLetterHeadBean officeLetterHeadBean = (OfficeLetterHeadBean) request.getAttribute("officeLetterHeadBean");
		PdfPTable footerTable = new PdfPTable(new float[] {20, 55, 25});
		footerTable.setWidthPercentage(100f);
		footerTable.setTotalWidth(100f);
		
		float fixedLeading = 8f;
		PdfPCell contentCell = null;
		Image img = null;
		
		contentCell = lineBreak();
		contentCell.setBorder(0);
		contentCell.setColspan(3);
		footerTable.addCell(contentCell);
		
		 /************************************First Section**************************************************************/
		UserBean pathologist = (UserBean) request.getAttribute("pathologist");
		if(pathologist == null) {
			pathologist = new UserBean();
		}
		UserBean authUser = (UserBean) request.getAttribute("authUser");
		boolean sameUser = false;
		if (authUser != null && authUser.getUser_id().equals(pathologist.getUser_id())) {
			authUser = null;
			sameUser = true;
		}
		
		String path = (String) request.getAttribute("imagePath");
		String fname = null;
		// Only if pathologist authenticate the report
		if (sameUser) {
			fname = path + pathologist.getUser_sign();
			try {
				img = Image.getInstance(fname);
				img.setAlignment(0);
				img.setSpacingBefore(fixedLeading);
				img.scalePercent(40.0F);
			} catch (Exception ex) {
			}
		}
		if (img != null) {
			contentCell = new PdfPCell(img);
			contentCell.setBorder(0);
		} else {
			contentCell = getNoBorderCell(getPhrase("", FontFactory.getFont("Helvetica", 11.0F)));
		}
		contentCell.setRowspan(4);
		footerTable.addCell(contentCell);
		
		Font fontStyle = FontFactory.getFont("Helvetica", 8.0F, 0);
		contentCell = getNoBorderCell(getPhrase("These tests are accredited under ISO 15189:2012 unless specified by (*)", fontStyle), fixedLeading);
		contentCell.setHorizontalAlignment(alignCenter);
		footerTable.addCell(contentCell);
		
		img = null;
		if (authUser != null) {
			fname = path + authUser.getUser_sign();
			try {
				img = Image.getInstance(fname);
				img.setAlignment(0);
				img.setSpacingBefore(fixedLeading);
				img.scalePercent(40.0F);
			} catch (Exception ex) {
			}
		}
		if (img != null) {
			contentCell = new PdfPCell(img);
			contentCell.setBorder(0);
		} else {
			contentCell = getNoBorderCell(getPhrase("", FontFactory.getFont("Helvetica", 11.0F)));
		}
		contentCell.setRowspan(4);
		footerTable.addCell(contentCell);
		
		contentCell = getNoBorderCell(getPhrase("Sample processed on the same day of receipt unless specified otherwise.", fontStyle), fixedLeading);
		contentCell.setHorizontalAlignment(alignCenter);
		footerTable.addCell(contentCell);
		
		contentCell = getNoBorderCell(getPhrase("Test results pertains only the sample tested and to be correlated with clinical history.", fontStyle), fixedLeading);
		contentCell.setHorizontalAlignment(alignCenter);
		footerTable.addCell(contentCell);
		
		contentCell = getNoBorderCell(getPhrase("Reference range related to Age/Gender.", fontStyle), fixedLeading);
		contentCell.setHorizontalAlignment(alignCenter);
		footerTable.addCell(contentCell);
		
		/************************************Second Section**************************************************************/
    	
		Font fontStyleFooters = FontFactory.getFont("Helvetica",10.0F, 1);
		Font fontStyleFooters1 = FontFactory.getFont("Helvetica", 9.0F, 0);
		Font fontStyleFooters2 = FontFactory.getFont("Helvetica", 8.0F, 0);
		
		PdfPTable left = new PdfPTable(1);
		left.setWidthPercentage(100f);
		left.setTotalWidth(100f);
		left.getDefaultCell().setBorderWidth(0.0F);
		left.getDefaultCell().setColspan(1);
		if ((pathologist != null) && (pathologist.getUser_label() != null)) {
			left.addCell(new Phrase(pathologist.getUser_label(), fontStyleFooters));
		} else {
			left.addCell(new Phrase("", fontStyleFooters));
		}
		left.getDefaultCell().setColspan(1);
		if ((pathologist != null) && (pathologist.getUser_desig() != null)) {
			left.addCell(new Phrase(pathologist.getUser_desig(), fontStyleFooters1));
		} else {
			left.addCell(new Phrase("", fontStyleFooters));
		}
		left.getDefaultCell().setColspan(1);
		if ((pathologist != null) && (pathologist.getUser_license() != null)) {
			left.addCell(new Phrase(pathologist.getUser_license(), fontStyleFooters1));
		} else {
			left.addCell(new Phrase("", fontStyleFooters1));
		}
		footerTable.addCell(getNoBorderCell(left));
		
		LabReportData registrationBean = (LabReportData) request.getAttribute("footerDetails");
		PdfPTable middle = new PdfPTable(2);
		middle.setWidthPercentage(100f);
		middle.setTotalWidth(100f);
		middle.getDefaultCell().setBorderWidth(0.0F);
		middle.getDefaultCell().setColspan(1);
		if(registrationBean.getCollection_date() != null) {
			middle.addCell(new Phrase("H/L Collected On : " + LabReportData.getFormatedDateTime(registrationBean.getCollection_date()), fontStyleFooters2));
		} else {
			middle.addCell(new Phrase("H/L Collected On : " , fontStyleFooters2));
		}
		middle.getDefaultCell().setColspan(1);
		if (registrationBean.getAccession_date() != null) {
			middle.addCell(new Phrase("Received On : " + LabReportData.getFormatedDateTime(registrationBean.getAccession_date()), fontStyleFooters2));
		} else {
			middle.addCell(new Phrase("Received On :", fontStyleFooters2));
		}
		middle.getDefaultCell().setColspan(1);
		if (registrationBean.getVerified_date() != null) {
			middle.addCell(new Phrase("Authenticated On : " + LabReportData.getFormatedDateTime(registrationBean.getVerified_date()), fontStyleFooters2));
		} else {
			middle.addCell(new Phrase("Authenticated On : ", fontStyleFooters2));
		}
		middle.getDefaultCell().setColspan(1);
		if(registrationBean.getDispatch_date() != null) {
			middle.addCell(new Phrase("Released On : " + LabReportData.getFormatedDateTime(registrationBean.getDispatch_date()), fontStyleFooters2));
		} else {
			middle.addCell(new Phrase("Released On : " , fontStyleFooters2));
		}
		if (registrationBean.getPrinted_date() != null) {
			middle.getDefaultCell().setColspan(1);
			middle.addCell(new Phrase("Printed On : " + LabReportData.getFormatedDateTime(registrationBean.getPrinted_date()), fontStyleFooters2));
			middle.getDefaultCell().setColspan(1);
			if(registrationBean.getPrint_status() != null && registrationBean.getPrint_status().equals("Y")) {
				middle.addCell(new Phrase("Reprinted On : " + LabReportData.getFormatedDateTime(new Date()), fontStyleFooters2));
			} else {
				middle.addCell(new Phrase("", fontStyleFooters2));
			}
		}
		footerTable.addCell(getNoBorderCell(middle));
		
		PdfPTable right = new PdfPTable(1);
		right.setWidthPercentage(100f);
		right.setTotalWidth(100f);
		right.getDefaultCell().setBorderWidth(0.0F);
		right.getDefaultCell().setColspan(1);
		if ((authUser != null) && (authUser.getUser_label() != null)) {
			right.addCell(new Phrase(authUser.getUser_label(), fontStyleFooters));
		} else {
			right.addCell(new Phrase("", fontStyleFooters));
		}
		right.getDefaultCell().setColspan(1);
		if ((authUser != null) && (authUser.getUser_desig() != null)) {
			right.addCell(new Phrase(authUser.getUser_desig(), fontStyleFooters1));
		} else {
			right.addCell(new Phrase("", fontStyleFooters1));
		}
		right.getDefaultCell().setColspan(1);
		if ((authUser != null) && (authUser.getUser_license() != null)) {
			right.addCell(new Phrase(authUser.getUser_license(), fontStyleFooters1));
		} else {
			right.addCell(new Phrase("", fontStyleFooters1));
		}
		footerTable.addCell(getNoBorderCell(right));
		
		contentCell = lineBreak();
		contentCell.setBorder(0);
		contentCell.setColspan(3);
		footerTable.addCell(contentCell);
		
		/************************************Third Section**************************************************************/
		PdfPTable accrTable = new PdfPTable(new float[] {15, 70, 15});
		accrTable.setWidthPercentage(100f);
		accrTable.setTotalWidth(100f);
		img = null;
		if (registrationBean != null && registrationBean.getJci() != null && registrationBean.getJci().equals("Y")) {
			contentCell = new PdfPCell();
			contentCell.setBorderWidth(0.0F);
			try {
				img = Image.getInstance(path + officeLetterHeadBean.getJci_logo());
				img.setAlignment(0);
				img.scalePercent(60.0F);
			} catch (Exception ex) {
			}
			if (img != null) {
				contentCell.addElement(img);
			} else {
				contentCell.addElement(new Phrase("", fontStyleFooters));
			}
			contentCell.setColspan(1);
		} else {
			contentCell = new PdfPCell();
			contentCell.setBorderWidth(0.0F);
		}
		accrTable.addCell(contentCell);
		img = null;

		contentCell = new PdfPCell();
		contentCell.setBorderWidth(0.0F);
		contentCell.setColspan(1);
		contentCell.setHorizontalAlignment(0);
		try {
			if (officeLetterHeadBean.getReport_footer_logo() != null)
				img = Image.getInstance(path + officeLetterHeadBean.getReport_footer_logo());
			else
				img = null;
			img.setAlignment(0);
			img.scalePercent(45.0F);
		} catch (Exception ex) {
		}
		if (img != null) {
			contentCell.addElement(img);
		} else {
			contentCell.addElement(new Phrase("", fontStyleFooters));
		}
		contentCell.setColspan(1);
		accrTable.addCell(contentCell);
		img = null;

		if ((registrationBean != null) && (registrationBean.getDac() != null)
				&& registrationBean.getDac().equals("Y")) {
			contentCell = new PdfPCell();
			contentCell.setBorderWidth(0.0F);
			try {
				img = Image.getInstance(path + officeLetterHeadBean.getDac_logo());
				img.setAlignment(alignCenter);
				img.scalePercent(60.0F);
			} catch (Exception ex) {
			}
			if (img != null) {
				contentCell.addElement(img);
			} else {
				contentCell.addElement(new Phrase("", fontStyleFooters));
			}
			contentCell.setColspan(1);
		} else {
			contentCell = new PdfPCell();
			contentCell.setBorderWidth(0.0F);
		}
		accrTable.addCell(contentCell);
		contentCell = getNoBorderCell(accrTable);
		contentCell.setColspan(3);
		footerTable.addCell(contentCell);
		return footerTable;
	}

	@Override
	public PdfPTable getRemarksTable(String remarks) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(new float[] { 100 });
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);

		PdfPCell cell = new PdfPCell();
		cell = getNoBorderCell(getPhrase("Remarks:", FontFactory.getFont("Helvetica", 10.0F)), 10);
		cell.setHorizontalAlignment(alignLeft);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		cell = getNoBorderCell(getPhrase(remarks, FontFactory.getFont("Helvetica", 9.0F)), 8);
		cell.setHorizontalAlignment(alignLeft);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		return blankdatatable;
	}
	
	@Override
	public void setNotes(Document document, String notes) throws Exception {
		parseHtmlToDocument(document, notes);
	}
	
}
