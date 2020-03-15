package eclinic.laboratory.presentation.action.reports.iface.impl;

import static com.medas.rewamp.reportservice.utils.ItextPdfCellFactory.getNoBorderCell;
import static com.medas.rewamp.reportservice.utils.ItextPdfCellFactory.getPhrase;
import static com.medas.rewamp.reportservice.utils.ItextPdfCellFactory.lineBreak;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.UserBean;
import com.medas.rewamp.reportservice.format.TestReportFormat;
import com.medas.rewamp.reportservice.utils.ReportHolder;

/**
 * @author Jegatheesh <br>
 *         Created on 2019-07-21
 *
 */
@Component("CommonTestReportFormatClinic")
public class CommonTestReportFormatClinic extends CommonTestReportFormat implements TestReportFormat {
	@Override
	public PdfPTable getFooterTable() throws Exception {
		OfficeLetterHeadBean officeLetterHeadBean = ReportHolder.getAttribute("officeLetterHeadBean", OfficeLetterHeadBean.class);
		PdfPTable footerTable = new PdfPTable(new float[] {20, 55, 25});
		footerTable.setWidthPercentage(100f);
		footerTable.setTotalWidth(100f);
		
		float fixedLeading = 8f;
		PdfPCell contentCell = null;
		Image img = null;
		Boolean proceed = false;
		
		contentCell = lineBreak();
		contentCell.setBorder(0);
		contentCell.setColspan(3);
		footerTable.addCell(contentCell);
		
		 /************************************First Section**************************************************************/
		UserBean pathologist = ReportHolder.getAttribute("pathologist", UserBean.class);
		if(pathologist == null) {
			pathologist = new UserBean();
		}
		UserBean authUser = ReportHolder.getAttribute("authUser", UserBean.class);
		boolean sameUser = false;
		UserBean technician = ReportHolder.getAttribute("forwardUser", UserBean.class);
		if (authUser != null && authUser.getUser_id().equals(pathologist.getUser_id())) {
			// if pathologist authenticate the report
			/*proceed = true;
			authUser = technician;*/
			
			authUser = null;
			sameUser = true;
		}
		
		String path = ReportHolder.getAttribute("imagePath");
		String fname = null;
		
		if(sameUser) {
			img = null;
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
			contentCell.setRowspan(3);
			footerTable.addCell(contentCell);
		}
		else {
			contentCell = getNoBorderCell(getPhrase("", FontFactory.getFont("Helvetica", 11.0F)));
			contentCell.setRowspan(3);
			footerTable.addCell(contentCell);
		}
		
		
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
		contentCell.setRowspan(3);
		footerTable.addCell(contentCell);
		
		contentCell = getNoBorderCell(getPhrase("Sample processed on the same day of receipt unless specified otherwise.", fontStyle), fixedLeading);
		contentCell.setHorizontalAlignment(alignCenter);
		footerTable.addCell(contentCell);
		
		contentCell = getNoBorderCell(getPhrase("Test results pertains only the sample tested and to be correlated with clinical history.", fontStyle), fixedLeading);
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
		
		LabReportData registrationBean = ReportHolder.getAttribute("footerDetails", LabReportData.class);
		PdfPTable middle = new PdfPTable(2);
		middle.setWidthPercentage(100f);
		middle.setTotalWidth(100f);
		middle.getDefaultCell().setBorderWidth(0.0F);
		middle.getDefaultCell().setColspan(2);
		try {
			if(registrationBean.getCollection_date() != null){
				middle.addCell(new Phrase("H.Centre/Lab Collected on : " + LabReportData.getFormatedDateTime(registrationBean.getCollection_date()), fontStyleFooters2));
			} else{
				middle.addCell(new Phrase("H.Centre/Lab Collected on : " , fontStyleFooters2));
			}
		} catch (Exception e) {
			middle.addCell(new Phrase("H.Centre/Lab Collected on:", fontStyleFooters2));
		}
		middle.getDefaultCell().setColspan(1);
		if (registrationBean.getAccession_date() != null) {
			middle.addCell(new Phrase("Received on : " + LabReportData.getFormatedDateTime(registrationBean.getAccession_date()), fontStyleFooters2));
		} else {
			middle.addCell(new Phrase("Received on:", fontStyleFooters2));
		}
		middle.getDefaultCell().setColspan(1);
		if (registrationBean.getVerified_date() != null) {
			middle.addCell(new Phrase("Authenticated on : " + LabReportData.getFormatedDateTime(registrationBean.getVerified_date()), fontStyleFooters2));
		} else {
			middle.addCell(new Phrase("Authenticated on: ", fontStyleFooters2));
		}
		middle.getDefaultCell().setColspan(1);
		if(registrationBean.getPrinted_date()!= null)
			middle.addCell(new Phrase("Printed on : " + LabReportData.getFormatedDateTime(registrationBean.getPrinted_date()), fontStyleFooters2));
		else
			middle.addCell(new Phrase("Printed on : ", fontStyleFooters2));
		if(registrationBean.getPrint_status() != null && registrationBean.getPrint_status().equals("Y")) {
			middle.getDefaultCell().setColspan(1);
			if(registrationBean.getPrinted_date()!= null)
				middle.addCell(new Phrase("Reprinted on : " + LabReportData.getFormatedDateTime(new Date()), fontStyleFooters2));
			else
				middle.addCell(new Phrase("Reprinted on : ", fontStyleFooters2));
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
	
}
