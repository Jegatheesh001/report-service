package eclinic.laboratory.presentation.action.reports.iface.impl;

import static com.medas.rewamp.reportservice.utils.ItextPdfCellFactory.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.format.CultureReportFormat;
import com.medas.rewamp.reportservice.service.LabReportService;
import com.medas.rewamp.reportservice.utils.ItextPdfCellFactory;

@Component("CommonCultureReportFormatTwo")
public class CommonCultureReportFormatTwo  implements CultureReportFormat {
	
	@Autowired
	private LabReportService reportService;
	
	@Override
	public PdfPTable printFormatTwoResult(List<RegistrationBean> list, RegistrationBean regiBean) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(2);
		int headerwidths[] = { 25, 75 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);

		for (RegistrationBean result : list) {
			if(result.getIs_hide() != null && result.getIs_hide().equals("Y"))
				continue;
			blankdatatable.addCell(ItextPdfCellFactory.getCell(result.getTestName(), HELVETICA_BOLD_9, alignLeft, bordorWhite, 1));
			blankdatatable.addCell(getFormatTwoResult(result));
		}
		return blankdatatable;
	}
	
	/**
	 * Will return formated test result content
	 * 
	 * @param result
	 * @return
	 */
	private PdfPCell getFormatTwoResult(RegistrationBean result) {
		String testResult = null;
		Font font = result.getTestName().toLowerCase().startsWith("sample type") ? HELVETICA_BOLD_9 : HELVETICA_9;
		if(result.getTestResult().contains(",")) {
			List<String> paramters = Arrays.asList(result.getTestResult().split(",")).stream().filter(e -> e.trim().length()>0).collect(Collectors.toList());
			if (paramters.size() > 0) {
				if (paramters.size() == 1) {
					testResult = paramters.get(0);
				} else {
					AtomicInteger index = new AtomicInteger(0);
					testResult = paramters.stream().map(e -> index.incrementAndGet() + ". " + e).collect(Collectors.joining("\n"));
				}
			}
		} else {
			testResult = result.getTestResult();
		}
		return ItextPdfCellFactory.getCell(testResult, font, alignLeft, bordorWhite, 1);
	}

	@Override
	public PdfPTable printFormatOneReport(List<RegistrationBean> list, String lname) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(4);
		int headerwidths[] = { 30, 25, 25,20 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);

		blankdatatable.addCell(ItextPdfCellFactory.getCellWithUnderlinedText("\n", HELVETICA_5, alignLeft, bordorWhite, 4));
		blankdatatable.addCell(ItextPdfCellFactory.getCellWithUnderlinedText("", HELVETICA_5, alignLeft, bordorBlack, 4));
		blankdatatable.addCell(ItextPdfCellFactory.getCell("Test Name ", HELVETICA_9, alignLeft, bordorWhite, 1));
		blankdatatable.addCell(ItextPdfCellFactory.getCell("Result", HELVETICA_9, alignLeft, bordorWhite, 1));
		blankdatatable.addCell(ItextPdfCellFactory.getCell("Units", HELVETICA_9, alignLeft, bordorWhite, 1));
		blankdatatable.addCell(ItextPdfCellFactory.getCell("Ref. Range", HELVETICA_9, alignLeft, bordorWhite, 1));
		blankdatatable.addCell(ItextPdfCellFactory.getCellWithUnderlinedText("", HELVETICA_5, alignLeft, bordorBlack, 4));
		
		if (lname != null && !lname.isEmpty())
			blankdatatable.addCell(ItextPdfCellFactory.getCellWithUnderlinedText(lname.toUpperCase(),HELVETICA_BOLDOblique_10, 0, bordorWhite, 4));

		list.forEach(result -> {
			blankdatatable.addCell(ItextPdfCellFactory.getCell(result.getTestName(), HELVETICA_BOLD_9, alignLeft, bordorWhite, 1));
			blankdatatable.addCell(ItextPdfCellFactory.getCell(result.getTestResult(), HELVETICA_9, alignLeft, bordorWhite, 1));
			blankdatatable.addCell(ItextPdfCellFactory.getCell(result.getMeasure(), HELVETICA_9, alignLeft, bordorWhite, 1));
			blankdatatable.addCell(ItextPdfCellFactory.getCell(result.getNormalValue(), HELVETICA_9, alignLeft, bordorWhite, 1));
		});
		return blankdatatable;
	}

	/**
	 * For printing profile name
	 */
	@Override
	public PdfPTable printProfileName(String profileName) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(1);
		int headerwidths[] = { 100 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);

		blankdatatable.addCell(
				ItextPdfCellFactory.getCell(profileName.toUpperCase(), HELVETICA_BOLD_10, alignCenter, bordorWhite, 1));
		return blankdatatable;
	}
	
	@Override
	public PdfPTable getDataTable4AntibioAndOrganismNames(RegistrationBean regiBean) throws Exception {
		List<RegistrationBean> orgNameList = reportService.getOrganismNames(regiBean);
		PdfPTable blankdatatable = new PdfPTable(2);
		int[] headerwidths = { 25, 75 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(98.0F);

		String orgNames = "";
		PdfPCell cell = null;

		orgNames = orgNameList.stream().map(orgBean -> {
			if (orgBean.getColonieus() != null && !orgBean.getColonieus().equals(""))
				return orgBean.getOrganism_name() + "[" + orgBean.getColonieus() + "]";
			else
				return orgBean.getOrganism_name();
		}).collect(Collectors.joining(", "));

		cell = getNoBorderCell(getPhrase("Organism Isolated", FontFactory.getFont("Helvetica-Bold", sensitivityFont)), 7);
		cell.setVerticalAlignment(alignLeft);
		cell.setHorizontalAlignment(alignLeft);
		blankdatatable.addCell(cell);

		cell = getNoBorderCell(getPhrase(orgNames, FontFactory.getFont("Helvetica", normalBold)), 10);
		cell.setVerticalAlignment(alignLeft);
		cell.setHorizontalAlignment(alignLeft);
		blankdatatable.addCell(cell);

		return blankdatatable;
	}

	@Override
	public PdfPTable printOrganismAndAntibiotic(List<RegistrationBean> list, RegistrationBean regiBean) throws Exception {
		List<RegistrationBean> orgNameList = reportService.getOrganismNames(regiBean);
		PdfPTable blankdatatable = new PdfPTable(orgNameList.size() + 1);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100.0F);

		PdfPCell cell = new PdfPCell();
		float fixedLeading = 8;
		int paddingBottom = 5;

		cell = getCellForReport(getPhrase("Antibiotics", FontFactory.getFont("Helvetica-Bold", sensitivityFont)), fixedLeading);
		cell.setHorizontalAlignment(alignCenter);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPaddingBottom(paddingBottom);
		blankdatatable.addCell(cell);

		for(RegistrationBean nameBean : orgNameList) {
			cell = getCellForReport(getPhrase(nameBean.getOrganism_name(), FontFactory.getFont("Helvetica-Bold", sensitivityFont)), fixedLeading);
			cell.setHorizontalAlignment(alignCenter);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingBottom(paddingBottom);
			blankdatatable.addCell(cell);
		}
		
		Map<String, RegistrationBean> antibioticMap = new LinkedHashMap<>();
		Map<String, RegistrationBean> orgAntibioticMap = new LinkedHashMap<>();
		String key = null;
		for (RegistrationBean setBean : list) {
			if (antibioticMap.get(setBean.getAntibiotic_id()) == null) {
				antibioticMap.put(setBean.getAntibiotic_id(), setBean);
			}
			key = setBean.getAntibiotic_id() + "_" + setBean.getOrganism_id();
			if (orgAntibioticMap.get(key) == null) {
				orgAntibioticMap.put(key, setBean);
			}
		}

		RegistrationBean setBean = null;
		for (RegistrationBean antibiotic : antibioticMap.values()) {
			if(antibiotic.getAntibiotic_name()!=null) {
				cell = getCellForReport(getPhrase(" " + antibiotic.getAntibiotic_name(), FontFactory.getFont("Helvetica", sensitivityFont)), fixedLeading);
			}
			else {
				cell = getCellForReport(getPhrase("-", FontFactory.getFont("Helvetica", sensitivityFont)), fixedLeading);
				cell.setHorizontalAlignment(alignCenter);
			}
			cell.setColspan(1);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingBottom(paddingBottom);
			blankdatatable.addCell(cell);
			for (RegistrationBean org : orgNameList) {
				setBean = orgAntibioticMap.get(antibiotic.getAntibiotic_id() + "_" + org.getOrganism_id());
				if (setBean != null) {
					if (setBean.getSensitivity() != null) {
						cell = getCellForReport(getPhrase(setBean.getSensitivity(), FontFactory.getFont("Helvetica", sensitivityFont)), fixedLeading);
					} else {
						cell = getCellForReport(getPhrase("-", FontFactory.getFont("Helvetica", sensitivityFont)), fixedLeading);
					}
				} else {
					cell = getCellForReport(getPhrase("-", FontFactory.getFont("Helvetica", sensitivityFont)), fixedLeading);
				}
				cell.setHorizontalAlignment(alignCenter);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPaddingBottom(paddingBottom);
				blankdatatable.addCell(cell);
			}
		}

		cell = getCellForReport(getPhrase("S � Sensitive\t\t\t\t\t\t\t\t\t\t\t I � Intermediate\t\t\t\t\t\t\t\t\t\t\t R - Resistant\t\t\t\t\t\t\n\n",
				FontFactory.getFont("Helvetica", sensitivityFont)));
		cell.setColspan(orgNameList.size() + 1);
		cell.setHorizontalAlignment(alignCenter);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		return blankdatatable;

	}

	@Override
	public PdfPTable printSensitivity(List<RegistrationBean> list) throws Exception {
		// LabReportsFormatClinic report = new LabReportsFormatClinic();
		// report.getSensitivityTable(list)
		return null;
	}

	@Override
	public PdfPTable printRemarks(String remarks) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(2);
		int headerwidths[] = { 10, 90 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);

		blankdatatable.addCell(ItextPdfCellFactory.getCell("Remarks : ", HELVETICA_BOLD_9, alignLeft, bordorWhite, 1));
		blankdatatable.addCell(ItextPdfCellFactory.getCell(remarks, HELVETICA_9, alignLeft, bordorWhite, 1));
		return blankdatatable;
	}

	@Override
	public PdfPTable getBlankLine(float fixedLeading) throws Exception {
		PdfPTable blankdatatable = new PdfPTable(1);
		int headerwidths[] = { 100 };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		blankdatatable.addCell(ItextPdfCellFactory.getCell(" ", HELVETICA_BOLD_9, alignLeft, bordorWhite, 1, fixedLeading));
		return blankdatatable;
	}

}
