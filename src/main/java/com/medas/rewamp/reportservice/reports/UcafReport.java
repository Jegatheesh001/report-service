package com.medas.rewamp.reportservice.reports;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.PatientDetails;
import com.medas.rewamp.reportservice.business.vo.ServiceVO;
import com.medas.rewamp.reportservice.business.vo.UserBean;
import com.medas.rewamp.reportservice.business.vo.VitalSigns;
import com.medas.rewamp.reportservice.business.vo.insurance.UcafReportParam;
import com.medas.rewamp.reportservice.service.InsuranceService;
import com.medas.rewamp.reportservice.service.LabReportService;
import com.medas.rewamp.reportservice.utils.DateUtil;
import com.medas.rewamp.reportservice.utils.ReportHolder;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 22, 2020
 *
 */
@Slf4j
@Service
public class UcafReport {
	
	@Autowired
	private LabReportService reportService;
	
	@Autowired
	private InsuranceService insuranceService;
	
	@Value("${app.path.image}")
	private String imagePath;
	
	@Value("${app.path.attachments}")
	private String attachmentPath;
	
	private static final String YES = "Y";
	private DecimalFormat df = new DecimalFormat("########0.00");

	public ByteArrayInputStream generateReport(UcafReportParam reportParam) throws DocumentException {
		ReportHolder.initialize();
		// GLobal Variable initialization
		ReportHolder.setAttribute("isPreAppAvail", false);
		ReportHolder.setAttribute("preAppBean", new ServiceVO());
		Document document = new Document(PageSize.A4, 10, 10, 8, 5);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		writer.setPageEvent(new DefaultPDFTemplate());
		document.addAuthor("MEDAS");
		document.addSubject("Ucaf report");
		document.addTitle("Ucaf report");
		document.open();
		OfficeLetterHeadBean officeLetterHeadBean = reportService.getOfficeLetterHead(reportParam.getOfficeId());
		ReportHolder.setAttribute("officeLetterHeadBean", officeLetterHeadBean);
		Image checkbox = null;
		Image checkboxChecked = null;
		String fname = imagePath + "check_box.png";
		try {
			checkbox = Image.getInstance(fname);
			checkbox.setAlignment(Element.ALIGN_RIGHT);
			checkbox.setWidthPercentage(8);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		ReportHolder.setAttribute("checkbox", checkbox);
		fname = imagePath + "checkbox_ticked.png";
		try {
			checkboxChecked = Image.getInstance(fname);
			checkboxChecked.setAlignment(Element.ALIGN_RIGHT);
			checkboxChecked.setWidthPercentage(8);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		ReportHolder.setAttribute("checkboxChecked", checkboxChecked);
		PatientDetails patientDetails = insuranceService.getConsultDetailByID(reportParam);
		PatientDetails ucafDetails = insuranceService.getConsult4EachSpeciality(reportParam);
		if(null == ucafDetails) {
			ucafDetails = new PatientDetails();
		}
		List<ServiceVO> diagList = insuranceService.getAllConsultDiagnosis(reportParam);
		List<ServiceVO> procList = insuranceService.getAllConsultProcedure(reportParam);
		List<ServiceVO> labList = insuranceService.getAllConsultLabTest(reportParam);
		
		PdfPTable dataTable = new PdfPTable(1);
		dataTable.setWidthPercentage(90f);
		PdfPCell cell = new PdfPCell(new Phrase("UCAF 1.0", FontFactory.getFont(FontFactory.HELVETICA, 20)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		dataTable.addCell(cell);
		document.add(dataTable);
		
		PdfPTable tempTable = new PdfPTable(2);
		tempTable.setWidthPercentage(90f);
		tempTable.addCell(getDataTable(patientDetails));
		tempTable.addCell(getDataTableInsurCardEmboss(patientDetails));
		document.add(tempTable);
		
		dataTable = new PdfPTable(1);
		dataTable.setWidthPercentage(90f);
		dataTable.addCell(getDataTableDoctor(patientDetails, ucafDetails, diagList));
		dataTable.addCell(getDataTableServices(ucafDetails, procList, labList));
		dataTable.addCell(getDataTableDeclaration(patientDetails));
		dataTable.addCell(getDataTable4InsurCompany());
		document.add(dataTable);
		
		document.close();
		ReportHolder.remove();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	private PdfPTable getDataTable(PatientDetails patientDetails) throws DocumentException {
		OfficeLetterHeadBean officeLetterHeadBean = ReportHolder.getAttribute("officeLetterHeadBean", OfficeLetterHeadBean.class);
		Image chkbx = ReportHolder.getAttribute("checkbox", Image.class);
		
		PdfPTable blankdatatable = new PdfPTable(3);
		int[] headerwidths = {30,30,40};
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100);

		PdfPCell cell = new PdfPCell(new Phrase("To be completed by Reception/Nurse", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 8)));
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(3);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Provider Name:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		//cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase(": " + officeLetterHeadBean.getOfficeName(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Fax:", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		//cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase(""+ officeLetterHeadBean.getFaxNumbers(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Phone:", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		//cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase(""+ officeLetterHeadBean.getPhoneNumbers(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Insurance Co:\nTPA Name :", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		//cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if (patientDetails.getInsurar_name() != null) {
			cell = new PdfPCell(new Phrase(patientDetails.getInsurar_name(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
		} else {
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
		}
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Patient File No:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7)));
		cell.setBorderWidth(0);
		//cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase(patientDetails.getOp_number(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Dept", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7)));
		cell.setBorderWidth(0);
		//cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase(patientDetails.getDepartment_name(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		Phrase p = new Phrase("Single ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8));
		p.add(new Chunk(chkbx, 0, 0));

		cell = new PdfPCell(p);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		p = new Phrase("Married ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8));
		p.add(new Chunk(chkbx, 0, 0));
		
		cell = new PdfPCell(p);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Plan type:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		//cell.setColspan(3);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase( patientDetails.getNetwork_type(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Date Of Visit:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		//cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase(DateUtil.formatDate("1", patientDetails.getConsult_date()), FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		return blankdatatable;
	}

	private PdfPTable getDataTableInsurCardEmboss(PatientDetails patientDetails) throws DocumentException {
		PdfPTable blankdatatable = new PdfPTable(2);
		float[] widths = new float[] { 20f, 30f };
		blankdatatable.setWidths(widths);
		blankdatatable.setSpacingBefore(4.0f);
		blankdatatable.setSpacingAfter(1.0f);
		blankdatatable.setSplitLate(true);

		PdfPCell cell = null;
		String documentFilename = insuranceService.getInsuranceCardAttachment(patientDetails);

		String fname = attachmentPath + documentFilename;
		Image img = null;
		try {
			img = Image.getInstance(fname);
			img.setAlignment(Element.ALIGN_LEFT);
			img.scaleAbsolute(220, 110);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		if (img != null)
			cell = new PdfPCell(img);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11)));
		cell.setColspan(2);
		cell.setRowspan(6);
		cell.setBorderWidth(0);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		blankdatatable.addCell(cell);
		return blankdatatable;
	}
	
	private PdfPTable getDataTableDoctor(PatientDetails patientDetails, PatientDetails ucafDetails,
			List<ServiceVO> diagList) throws DocumentException {
		String dottedLinesHalf = "....................................................................";
		String dottedLines = "..................................................................................................";
		String primaryDiag = "";
		String diagnosis = "" + dottedLines + "" + dottedLinesHalf + "...........";
		String secondDiag = "";
		String thirdDiag = "";
		String fourthDiag = "";
		for(ServiceVO setbean : diagList) {
			if (setbean.getDiagnosis_category().equalsIgnoreCase("P")) {
				primaryDiag = setbean.getIcd_code();
				diagnosis = setbean.getIcd_desc();
			} else if (secondDiag.equals("")) {
				secondDiag = setbean.getIcd_code();
			} else if (thirdDiag.equals("")) {
				thirdDiag = setbean.getIcd_code();
			} else if (fourthDiag.equals("")) {
				fourthDiag = setbean.getIcd_code();
			}
		}

		String bpsys = "";
		String bpdias = "";
		String pulse = "";
		String temp = "";
		String durOfIll = "";
		
		VitalSigns vitalsBean = insuranceService.getVitalSigns(patientDetails);
		if (null != vitalsBean) {
			if (null != vitalsBean.getBloodpressure())
				if (null != vitalsBean.getBloodPressureSyst() && !vitalsBean.getBloodPressureSyst().equals("")) {
					bpsys = vitalsBean.getBloodPressureSyst();
				}
			if (null != vitalsBean.getBloodpressure_dia() && !vitalsBean.getBloodpressure_dia().equals("")) {
				bpdias = vitalsBean.getBloodpressure_dia();
			}

			if (null != vitalsBean.getPulse())
				pulse = vitalsBean.getPulse();

			if (null != vitalsBean.getTemperature())
				temp = vitalsBean.getTemperature();
			
			if (null != vitalsBean.getDuration_of_ill())
				durOfIll = vitalsBean.getDuration_of_ill();
		}

		String chfCompl = insuranceService.getAllChiefComplaintsByConsultId(patientDetails.getConsult_id());
			
			VitalSigns hopiBean = new VitalSigns();
			hopiBean.setOp_number(patientDetails.getOp_number());
			hopiBean.setConsult_id(patientDetails.getConsult_id());
			
			if (null != patientDetails && null != patientDetails.getDepartment_id()) {
				hopiBean.setDepartment_id(patientDetails.getDepartment_id());
			}
			hopiBean.setType("EN");
			List<VitalSigns> exmNotesList = insuranceService.getPatientVisitDtlsByType(hopiBean);
			StringBuilder examNotes= new StringBuilder();
		if (null != exmNotesList && !exmNotesList.isEmpty()) {
			for (VitalSigns exmN : exmNotesList) {
				examNotes.append(exmN.getRemarks());
				examNotes.append("\n");
			}
		}
		String clinicalFindings = examNotes.toString();

		PdfPTable blankdatatable = new PdfPTable(10);

		float[] widths = new float[] { 10f, 10f, 10f, 12f, 8f, 10f, 5f, 10f, 5f, 20f };
		blankdatatable.setWidths(widths);

		blankdatatable.setSpacingBefore(4.0f);
		blankdatatable.setSpacingAfter(1.0f);
		blankdatatable.setSplitLate(true);

		PdfPCell cell = new PdfPCell(new Phrase("To be completed by Attending PHYSICIAN:", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("( Please Tick )", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		Image chkbx = ReportHolder.getAttribute("checkbox", Image.class);
		Image chkbxtickd = ReportHolder.getAttribute("checkboxChecked", Image.class);
		if(null != ucafDetails && null != ucafDetails.getIp_id() && !ucafDetails.getIp_id().equals("")){
			// IP
			if (chkbxtickd != null)
				cell = new PdfPCell(chkbxtickd);
			else
				cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("Inpatient", FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);

			if (chkbx != null)
				cell = new PdfPCell(chkbx);
			else
				cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("Outpatient", FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setColspan(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
			
			
		} else {
			// OP
			if (chkbx != null)
				cell = new PdfPCell(chkbx);
			else
				cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("Inpatient", FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);

			if (chkbxtickd != null)
				cell = new PdfPCell(chkbxtickd);
			else
				cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("Outpatient", FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setColspan(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		}

		

		cell = new PdfPCell(new Phrase("Emergency Case ?", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		if(null != ucafDetails.getEmergency() && ucafDetails.getEmergency().equals(YES)) {
			checkBoxYesTicked(cell, blankdatatable);
		} else {
			checkBoxNoTicked(cell, blankdatatable);
		}
		
		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(4);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		cell = new PdfPCell(new Phrase("BP Sys: " + bpsys + "             BP Dias : " + bpdias + "             Pulse : " + pulse + "             Temp : "
				+ temp + "              Duration Of Illness :  " + durOfIll, FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		/*
		 * Chunk sigUnderline = new
		 * Chunk("                                            ");
		 * sigUnderline.setUnderline(0.1f, -2f);
		 * 
		 * Paragraph para = new Paragraph("Authorized Signature: ");
		 * para.add(sigUnderline);
		 */

		cell = new PdfPCell(new Phrase("Chief Complaint & Main Symptoms : ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if (!chfCompl.equals("")) {
			cell = new PdfPCell(new Phrase("" + chfCompl, FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderWidth(0);
			cell.setColspan(10);
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		} else {
			cell = new PdfPCell(new Phrase("" + dottedLines + "" + dottedLines, FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderWidth(0);
			cell.setColspan(10);
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		}

		cell = new PdfPCell(new Phrase("Significant Signs :", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		if(clinicalFindings!=null && !clinicalFindings.equals(""))
		{
			cell = new PdfPCell(new Phrase(""+clinicalFindings,FontFactory.getFont(FontFactory.HELVETICA,8)) );
		}
		else
		{
			cell = new PdfPCell(new Phrase("",FontFactory.getFont(FontFactory.HELVETICA,8)) );
		}
		
		/*if(null==ucafDetails.getSignificant_signs()){
			ucafDetails.setSignificant_signs("");
		}*/
		
		/*cell = new PdfPCell(new Phrase("Significant Signs : " + ucafDetails.getSignificant_signs(), FontFactory.getFont(FontFactory.HELVETICA, 8)));*/
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if(null==ucafDetails.getSignificant_signs() || ucafDetails.getSignificant_signs().equals("")){
			cell = new PdfPCell(new Phrase("" + dottedLines + "" + dottedLines, FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderWidth(0);
			cell.setColspan(10);
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		}
		

		/*cell = new PdfPCell(new Phrase("Other Conditions : " + dottedLines + "" + dottedLinesHalf, FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);*/
		
		if(null==ucafDetails.getOther_cond()){
			ucafDetails.setOther_cond("");
		}
		
		cell = new PdfPCell(new Phrase("Other Conditions : " + ucafDetails.getOther_cond(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if(null==ucafDetails.getOther_cond() || ucafDetails.getOther_cond().equals("")){
			cell = new PdfPCell(new Phrase("" + dottedLines + "" + dottedLines, FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderWidth(0);
			cell.setColspan(10);
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		}
		

		cell = new PdfPCell(new Phrase("Diagnosis : " + diagnosis, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Principal Code : " + primaryDiag + "             2nd Code : " + secondDiag + "             3rd Code : " + thirdDiag
				+ "             4th Code : " + fourthDiag, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderWidth(0);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Please Tick ( X ) Where Appropriate :", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(10);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if(null != ucafDetails.getChronic() && ucafDetails.getChronic().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}
		/*if (chkbx != null)
			cell = new PdfPCell(chkbx);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));*/

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Chronic", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		/*if (chkbx != null)
			cell = new PdfPCell(chkbx);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));*/
		if(null != ucafDetails.getCongenital() && ucafDetails.getCongenital().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Congenital", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		/*if (chkbx != null)
			cell = new PdfPCell(chkbx);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));*/
		
		if(null != ucafDetails.getRta() && ucafDetails.getRta().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("RTA", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		/*if (chkbx != null)
			cell = new PdfPCell(chkbx);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));*/
		
		if(null != ucafDetails.getVaccine() && ucafDetails.getVaccine().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Vaccination", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		/*if (chkbx != null)
			cell = new PdfPCell(chkbx);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));*/
		
		if(null != ucafDetails.getWork_related() && ucafDetails.getWork_related().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Work Related", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if(null != ucafDetails.getCheckup() && ucafDetails.getCheckup().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Check-up", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);


		if(null != ucafDetails.getPsychiatric() && ucafDetails.getPsychiatric().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Psychiatric", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if(null != ucafDetails.getInfertility() && ucafDetails.getInfertility().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}
		
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Infertility", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		if(null != ucafDetails.getPregnancy() && ucafDetails.getPregnancy().equals("Y")){
			cell = new PdfPCell(chkbxtickd);
		}else {
			cell = new PdfPCell(chkbx);
		}

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		if(null== ucafDetails.getLmp()){
			ucafDetails.setLmp("");
		}
		cell = new PdfPCell(new Phrase("Pregnancy/LMP : "+ucafDetails.getLmp(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setColspan(5);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		blankdatatable.addCell(cell);

		return blankdatatable;
	}

	private PdfPTable getDataTableServices(PatientDetails ucafDetails, List<ServiceVO> procList,
			List<ServiceVO> labList) throws DocumentException {

		String dottedLines = "..................................................................................................";

		PdfPTable blankdatatable = new PdfPTable(5);
		float[] widths = new float[] { 10f, 50f, 10f, 20f, 10f };
		blankdatatable.setWidths(widths);
		blankdatatable.setSpacingBefore(4.0f);
		blankdatatable.setSpacingAfter(1.0f);
		blankdatatable.setSplitLate(true);

		PdfPCell cell = new PdfPCell(new Phrase(
				"\nSuggestive line (s) of management : Kindly, enumerate the recommended investigation, and / or procedures for outpatient approval only",
				FontFactory.getFont(FontFactory.HELVETICA, 6)));

		cell.setBorderColor(BaseColor.BLACK);
		cell.setBorderColorBottom(BaseColor.BLACK);

		cell.setColspan(5);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Code", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));

		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorderColor(BaseColor.BLACK);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Description/Service", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));

		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorderColor(BaseColor.BLACK);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Quantity", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));

		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorderColor(BaseColor.BLACK);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Type", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));

		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorderColor(BaseColor.BLACK);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Cost", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));

		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorderColor(BaseColor.BLACK);
		blankdatatable.addCell(cell);
		
		boolean isPreAppAvail = ReportHolder.getAttribute("isPreAppAvail", Boolean.class);
		ServiceVO preAppBean = ReportHolder.getAttribute("preAppBean", ServiceVO.class);

		double total = 0;
		int i = 0;
		for(ServiceVO setupBean : procList) {

			if (null != setupBean && null != setupBean.getIns_app() && setupBean.getIns_app().equals("Y") && null != setupBean.getApproval_status() && !setupBean.getApproval_status().equals("R")) {

				cell = new PdfPCell(new Phrase(setupBean.getInsurar_icd_code(), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorderColor(BaseColor.BLACK);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase(setupBean.getProcedure_name(), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setBorderColor(BaseColor.BLACK);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getQuantity()), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorderColor(BaseColor.BLACK);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase(getProcedureType(setupBean.getProcedure_type()), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorderColor(BaseColor.BLACK);
				blankdatatable.addCell(cell);

				if (null != setupBean.getApproval_status() && setupBean.getApproval_status().equals("Y")) {
					if (null != setupBean.getAppr_amt()) {
						cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getAppr_amt()), FontFactory.getFont(FontFactory.HELVETICA, 8)));
						total = total + setupBean.getAppr_amt();
					} else {
						cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getGross_amt()), FontFactory.getFont(FontFactory.HELVETICA, 8)));
						total = total + setupBean.getGross_amt();
					}
				} else {
					cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getGross_amt()), FontFactory.getFont(FontFactory.HELVETICA, 8)));
					total = total + setupBean.getGross_amt();
				}
				
				cell.setBorderColor(BaseColor.BLACK);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				blankdatatable.addCell(cell);

				i++;

				// Pre-Approval Details
				if (!isPreAppAvail) {
					if (null != setupBean.getApproval_status() && !setupBean.getApproval_status().equals("N")) {
						preAppBean.setApproval_status(setupBean.getApproval_status());

						if (null != setupBean.getAppr_no())
							preAppBean.setAppr_no(setupBean.getAppr_no());

						if (null != setupBean.getApproved_date())
							preAppBean.setApproved_date(setupBean.getApproved_date());

						if (null != setupBean.getAppr_remarks())
							preAppBean.setAppr_remarks(setupBean.getAppr_remarks());

						if (null != setupBean.getValid_upto())
							preAppBean.setValid_upto(setupBean.getValid_upto());

						isPreAppAvail = true;
					}
				}
			}
		}

		for(ServiceVO setupBean : labList) {

			if (null != setupBean && null != setupBean.getIns_app() && setupBean.getIns_app().equals("Y") && null != setupBean.getApproval_status() && !setupBean.getApproval_status().equals("R")) {
				cell = new PdfPCell(new Phrase(setupBean.getIcd_code(), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setBorderColor(BaseColor.BLACK);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase(setupBean.getLabtest_name(), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setBorderColor(BaseColor.BLACK);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getQuantity()), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorderColor(BaseColor.BLACK);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase(getInvestType(setupBean.getLab_type()), FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorderColor(BaseColor.BLACK);
				blankdatatable.addCell(cell);
				
				
				if (null != setupBean.getApproval_status() && setupBean.getApproval_status().equals("Y")) {
					if (null != setupBean.getAppr_amt()) {
						cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getAppr_amt()), FontFactory.getFont(FontFactory.HELVETICA, 8)));
						total = total + setupBean.getAppr_amt();
					} else {
						cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getGross_amt()), FontFactory.getFont(FontFactory.HELVETICA, 8)));
						total = total + setupBean.getGross_amt();
					}
				} else {
					cell = new PdfPCell(new Phrase(String.valueOf(setupBean.getGross_amt()), FontFactory.getFont(FontFactory.HELVETICA, 8)));
					total = total + setupBean.getGross_amt();
				}

				cell.setBorderColor(BaseColor.BLACK);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				blankdatatable.addCell(cell);

				i++;

				// Pre-Approval Details
				if (!isPreAppAvail) {
					if (null != setupBean.getApproval_status() && !setupBean.getApproval_status().equals("N")) {
						preAppBean.setApproval_status(setupBean.getApproval_status());

						if (null != setupBean.getAppr_no())
							preAppBean.setAppr_no(setupBean.getAppr_no());

						if (null != setupBean.getApproved_date())
							preAppBean.setApproved_date(setupBean.getApproved_date());

						if (null != setupBean.getAppr_remarks())
							preAppBean.setAppr_remarks(setupBean.getAppr_remarks());

						if (null != setupBean.getValid_upto())
							preAppBean.setValid_upto(setupBean.getValid_upto());

						if (null != setupBean.getApproved_date())
							preAppBean.setApproved_date(setupBean.getApproved_date());

						isPreAppAvail = true;
					}
				}
			}
		}
		ReportHolder.setAttribute("isPreAppAvail", isPreAppAvail);
		ReportHolder.setAttribute("preAppBean", preAppBean);

		// Empty Rows For manual Filling.
		for (int k = i; k < 3; k++) {
			cell = new PdfPCell(new Phrase("\n", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.BLACK);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.BLACK);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.BLACK);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.BLACK);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			blankdatatable.addCell(cell);

			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

			cell.setBorderColor(BaseColor.BLACK);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			blankdatatable.addCell(cell);
		}

		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

		cell.setBorderColor(BaseColor.BLACK);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

		cell.setBorderColor(BaseColor.BLACK);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

		cell.setBorderColor(BaseColor.BLACK);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));

		cell.setBorderColor(BaseColor.BLACK);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("" + df.format(total), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));

		cell.setBorderColor(BaseColor.BLACK);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("\n", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Is Case Management From ( CMFI.0 ) Included ", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		if(null != ucafDetails.getCmf_included() && ucafDetails.getCmf_included().equals(YES)) {
			checkBoxYesTicked(cell, blankdatatable);
		} else {
			checkBoxNoTicked(cell, blankdatatable);
		}

		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Please Specify Possible line of Management When Applicable :", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		if(null != ucafDetails.getLine_of_mgmt() && !ucafDetails.getLine_of_mgmt().trim().equals("")) {
			cell = new PdfPCell(new Phrase("" + ucafDetails.getLine_of_mgmt(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(5);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		
		} else {
			cell = new PdfPCell(new Phrase("" + dottedLines + "" + dottedLines, FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(5);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		}

		if(null == ucafDetails.getLength_of_stay()){
			ucafDetails.setLength_of_stay("");
		}
		if(null ==ucafDetails.getAdmission_date() || ucafDetails.getAdmission_date().equals("null") ){
			ucafDetails.setAdmission_date(""); 
		}
		
		cell = new PdfPCell(new Phrase("Estimated Length of Stay : "+ucafDetails.getLength_of_stay()+" Days                                    Expected Date of Admission : "+
		                                  ((null ==ucafDetails.getAdmission_date() || ucafDetails.getAdmission_date().equals("null")) ?"": ucafDetails.getAdmission_date()),
				FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		String intend="" ;
		if(i >= 8)
		{
			intend="\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		}
		
		cell = new PdfPCell(new Phrase(intend, FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		return blankdatatable;
	}

	private PdfPTable getDataTableDeclaration(PatientDetails patientDetails) throws DocumentException {

		PdfPTable blankdatatable = new PdfPTable(2);
		float[] widths = new float[] { 50f, 50f };
		blankdatatable.setWidths(widths);
		blankdatatable.setSpacingBefore(4.0f);
		blankdatatable.setSpacingAfter(1.0f);
		blankdatatable.setSplitLate(true);

		PdfPCell cell = new PdfPCell(
				new Phrase(
						"I hereby certify that ALL information mentioned are correct and that the medical services shown on this form were medically indicated and necessary for the management of this case. \n",
						FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(1);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase(
				"I hereby certify that all statements & information provided concerning patient Identification & the present illness or injury are TRUE."
						+ "\n\n", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(1);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		Image image = null;
		UserBean user = reportService.getUserDetailByUserID(String.valueOf(patientDetails.getDoctors_id()));
		 

		try {
			image = Image.getInstance(user.getUser_sign());
			image.setWidthPercentage(100);
			image.scaleToFit(50,50);
			image.setAlignment(Image.ALIGN_LEFT);

		} catch (Exception e) {
		}
		if (image != null)
			 cell = new PdfPCell( image);
		else
			 cell = new PdfPCell(new Phrase("\n", FontFactory.getFont(FontFactory.HELVETICA, 11)));
		cell.setBorderWidth(0);
		cell.setColspan(1);
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		/**Patient Sign **Nidhi **For saba**/
		String patientsign="";
		if(null != patientDetails.getPatient_sign() && !patientDetails.getPatient_sign().equals("")) {
			patientsign = patientDetails.getPatient_sign();
			
			String fname="";
	      	String data_uri = "";
			data_uri = patientsign;
			
			try {

				String target = attachmentPath;

				fname = target + "//patientsign" + patientDetails.getOp_number() + ".png";
				File f = new File(fname);
				String imageString = data_uri.substring(data_uri.indexOf(',') + 1);
				BufferedImage bimage = null;
				byte[] imageByte;
				BASE64Decoder decoder = new BASE64Decoder();
				imageByte = decoder.decodeBuffer(imageString);
				ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
				bimage = ImageIO.read(bis);
				bis.close();
				ImageIO.write(bimage, "png", f);

				image = Image.getInstance(fname);
				image.setWidthPercentage(100);
				image.scaleToFit(50, 50);
				image.setAlignment(Image.ALIGN_RIGHT);

			} catch (Exception e) {
			}
			if (image != null)
				cell = new PdfPCell(image);
			else
				cell = new PdfPCell(new Phrase("\n", FontFactory.getFont(FontFactory.HELVETICA, 11)));
			cell.setBorderWidth(0);
			cell.setColspan(1);
			cell.setBorderColor(BaseColor.WHITE);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			blankdatatable.addCell(cell);
		}else {
			cell = new PdfPCell(new Phrase("\n"));
			cell.setBorderWidth(0);
			cell.setColspan(1);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);
		}
		/**Patient Sign -END**/
		
		Date now = new Date();
		cell = new PdfPCell(
				new Phrase(
						""+patientDetails.getDoctors_name() + " \nSignature & Stamp \n" + "Date " + DateUtil.formatDate("2", now),
						FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(1);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		cell = new PdfPCell(new Phrase(
				"Name and Relationship(if Guardian)	       Signature" + "\n Date "
						+ DateUtil.formatDate("2", now), FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(1);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("__________________________________________________________________________________________________________________",
				FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase(
				"Provider’s Approval / coding staff must review / code the recommended service (s) & allocate cost and complete the following : ",
				FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Total cost : SR ...................................................................... As estimated / Package Deal ",
				FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(
				new Phrase(
						"Complete / code by : ............................................................ Signature : ..................................... Date ........../........ / .........",
						FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderWidth(0);
		cell.setColspan(2);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);

		return blankdatatable;

	}

	private PdfPTable getDataTable4InsurCompany() throws DocumentException {
		Image chkbx = ReportHolder.getAttribute("checkbox", Image.class);
		Image chkbxtickd = ReportHolder.getAttribute("checkboxChecked", Image.class);
		ServiceVO preAppBean = ReportHolder.getAttribute("preAppBean", ServiceVO.class);
		boolean isPreAppAvail = ReportHolder.getAttribute("isPreAppAvail", Boolean.class);
		PdfPTable blankdatatable = new PdfPTable(6);
		float[] widths = new float[] { 10f, 20f, 10f, 20f, 20f, 50f };
		blankdatatable.setWidths(widths);
		blankdatatable.setSpacingBefore(4.0f);
		blankdatatable.setSpacingAfter(1.0f);
		blankdatatable.setSplitLate(true);
		try {
			PdfPCell cell = new PdfPCell(new Phrase("For Insurance Company Use Only", FontFactory.getFont(FontFactory.HELVETICA, 8)));
			cell.setBorderColor(BaseColor.WHITE);
			cell.setColspan(6);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blankdatatable.addCell(cell);

			if (isPreAppAvail) {
				if (null != preAppBean.getApproval_status() && preAppBean.getApproval_status().equals("Y")) {
					if (chkbxtickd != null)
						cell = new PdfPCell(chkbxtickd);
					else
						cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11)));

				} else {
					if (chkbx != null)
						cell = new PdfPCell(chkbx);
					else
						cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11)));

				}
				cell.setBorderColor(BaseColor.WHITE);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Approved", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				// cell.setColspan(2);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				if (null != preAppBean.getApproval_status() && !preAppBean.getApproval_status().equals("Y")) {
					if (chkbxtickd != null)
						cell = new PdfPCell(chkbxtickd);
					else
						cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11)));

				} else {
					if (chkbx != null)
						cell = new PdfPCell(chkbx);
					else
						cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11)));

				}
				cell.setBorderColor(BaseColor.WHITE);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Not Approved", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(2);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Approval No : " + preAppBean.getAppr_no(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(1);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Comments( include approved days / services, if different from the requested ) ", FontFactory.getFont(
						FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(5);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				if (null != preAppBean.getValid_upto() && !preAppBean.getValid_upto().equals("")) {
					cell = new PdfPCell(new Phrase("Approval Validity : " + DateUtil.formatDate("2", DateUtil.parseDate("6", preAppBean.getValid_upto())), FontFactory.getFont(
							FontFactory.HELVETICA, 8)));

					cell.setBorderColor(BaseColor.WHITE);
					cell.setColspan(1);
					cell.setVerticalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					blankdatatable.addCell(cell);
				} else {
					cell = new PdfPCell(new Phrase("Approval Validity : ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

					cell.setBorderColor(BaseColor.WHITE);
					cell.setColspan(1);
					cell.setVerticalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					blankdatatable.addCell(cell);
				}

				if (null != preAppBean.getAppr_remarks() && !preAppBean.getAppr_remarks().equals("")) {
					cell = new PdfPCell(new Phrase(" " + preAppBean.getAppr_remarks(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
					cell.setBorderColor(BaseColor.WHITE);
					cell.setColspan(6);
					cell.setVerticalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					blankdatatable.addCell(cell);
				} else {
					for (int i = 1; i <= 3; i++) {
						cell = new PdfPCell(
								new Phrase(
										"------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ ",
										FontFactory.getFont(FontFactory.HELVETICA, 8)));
						cell.setBorderColor(BaseColor.WHITE);
						cell.setColspan(6);
						cell.setVerticalAlignment(Element.ALIGN_CENTER);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						blankdatatable.addCell(cell);
					}
				}

				cell = new PdfPCell(new Phrase("Insurance Officer __________________________", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(3);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Signature__________________", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(2);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				if (null != preAppBean.getApproved_date() && !preAppBean.getApproved_date().equals("")) {
					cell = new PdfPCell(new Phrase("Date " + DateUtil.formatDate("2", preAppBean.getApproved_date()), FontFactory.getFont(
							FontFactory.HELVETICA, 8)));
					cell.setBorderColor(BaseColor.WHITE);
					cell.setColspan(1);
					cell.setVerticalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					blankdatatable.addCell(cell);
				} else {
					cell = new PdfPCell(new Phrase("Date          /         /", FontFactory.getFont(FontFactory.HELVETICA, 8)));
					cell.setBorderColor(BaseColor.WHITE);
					cell.setColspan(1);
					cell.setVerticalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					blankdatatable.addCell(cell);
				}

			} else {
				if (chkbx != null)
					cell = new PdfPCell(chkbx);
				else
					cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11)));

				cell.setBorderColor(BaseColor.WHITE);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Approved", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				// cell.setColspan(2);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				if (chkbx != null)
					cell = new PdfPCell(chkbx);
				else
					cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11)));

				cell.setBorderColor(BaseColor.WHITE);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Not Approved", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(2);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Approval No : ", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(1);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Comments ( include approved days / services, if different from the requested )", FontFactory.getFont(
						FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(5);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Approval Validity : ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(1);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				for (int i = 1; i <= 3; i++) {
					cell = new PdfPCell(
							new Phrase(
									"------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ ",
									FontFactory.getFont(FontFactory.HELVETICA, 8)));
					cell.setBorderColor(BaseColor.WHITE);
					cell.setColspan(6);
					cell.setVerticalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					blankdatatable.addCell(cell);
				}

				cell = new PdfPCell(new Phrase("Insurance Officer __________________________", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(3);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Signature__________________", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(2);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

				cell = new PdfPCell(new Phrase("Date          /         /", FontFactory.getFont(FontFactory.HELVETICA, 8)));
				cell.setBorderColor(BaseColor.WHITE);
				cell.setColspan(1);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				blankdatatable.addCell(cell);

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return blankdatatable;

	}
	
	private String getProcedureType(String type) {
		if (null != type) {
			if (type.equals("I"))
				type = "Treatment";

			else if (type.equals("V"))
				type = "Medicine"; // Vaccine

			else if (type.equals("P"))
				type = "Procedure";

			else if (type.equals("C"))
				type = "Consultation";

			else if (type.equals("IY"))
				type = "Inventory";

			else if (type.equals("A"))
				type = "Service Charges";

			else if (type.equals("O"))
				type = "Other";

			else if (type.equals("G"))
				type = "Package";
		}

		return type;
	}

	private String getInvestType(String type) {
		if (null != type) {
			if (type.equals("L"))
				type = "Lab Test";

			else if (type.equals("X"))
				type = "X-Ray";

			else if (type.equals("S"))
				type = "Scan";

			else if (type.equals("E"))
				type = "ECG";
		}

		return type;
	}
	
	private void checkBoxYesTicked(PdfPCell cell, PdfPTable blankdatatable) {
		Image chkbx = ReportHolder.getAttribute("checkbox", Image.class);
		Image chkbxtickd = ReportHolder.getAttribute("checkboxChecked", Image.class);
		// YES TICKED
		if (chkbxtickd != null)
			cell = new PdfPCell(chkbxtickd);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Yes", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		//NO
		if (chkbx != null)
			cell = new PdfPCell(chkbx);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("No", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
	}
	
	private void checkBoxNoTicked(PdfPCell cell, PdfPTable blankdatatable) {
		Image chkbx = ReportHolder.getAttribute("checkbox", Image.class);
		Image chkbxtickd = ReportHolder.getAttribute("checkboxChecked", Image.class);
		// YES 
		if (chkbx != null)
			cell = new PdfPCell(chkbx);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("Yes", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
		
		//NO TICKED
		if (chkbxtickd != null)
			cell = new PdfPCell(chkbxtickd);
		else
			cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));

		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		blankdatatable.addCell(cell);

		cell = new PdfPCell(new Phrase("No", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setBorderColor(BaseColor.WHITE);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankdatatable.addCell(cell);
	}

}
