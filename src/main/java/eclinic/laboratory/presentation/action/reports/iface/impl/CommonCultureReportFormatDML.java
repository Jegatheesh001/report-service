package eclinic.laboratory.presentation.action.reports.iface.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfPTable;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.format.CultureReportFormat;
import com.medas.rewamp.reportservice.service.LabReportService;
import com.medas.rewamp.reportservice.utils.ItextPdfCellFactory;

/**
 *  Format for Culture Report (DML)
 */
@Component("CommonCultureReportFormatDML")
public class CommonCultureReportFormatDML extends CommonCultureReportFormatTwo implements CultureReportFormat {
	
	@Autowired
	private LabReportService reportService;
	
	@Override
	public PdfPTable getDataTable4AntibioAndOrganismNames(RegistrationBean regiBean) throws Exception {
		return null;
	}
	
	@Override
	public PdfPTable printOrganismAndAntibiotic(List<RegistrationBean> list, RegistrationBean regiBean) throws Exception {
		List<RegistrationBean> orgNameList = reportService.getOrganismNames(regiBean);
		PdfPTable blankdatatable = new PdfPTable(5);
		float headerwidths[] = { 25, 18.5f, 18.5f, 18.5f, 18.5f };
		blankdatatable.setWidths(headerwidths);
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100.0F);

		float fixedLeading = 10F;
		float paddingBottom = 5F;
		String orgNames = "";
		if(orgNameList.size() > 0) {
			AtomicInteger index = new AtomicInteger(0);
			
			blankdatatable.addCell(ItextPdfCellFactory.getCell("Organism Isolated", HELVETICA_BOLD_9, alignLeft, bordorWhite, 1, fixedLeading, paddingBottom));
			orgNames = orgNameList.stream().map(orgBean -> {
				if (orgBean.getColonieus() != null && !orgBean.getColonieus().equals(""))
					return index.incrementAndGet()+"."+orgBean.getOrganism_name() +" (" + orgBean.getColonieus() + ")";
				else
					return index.incrementAndGet()+"."+orgBean.getOrganism_name();
			}).collect(Collectors.joining("\n"));
			blankdatatable.addCell(ItextPdfCellFactory.getCell(orgNames, HELVETICA_BOLD_9, alignLeft, bordorWhite, 4, fixedLeading));
		
		}
		AtomicInteger index = new AtomicInteger(0);
		orgNameList.forEach(org -> {
			try {
				/** Anti-biogram heading */
				/*blankdatatable.addCell(ItextPdfCellFactory.getCell("Organism Isolated", HELVETICA_BOLD_9, alignLeft, bordorWhite, 1, fixedLeading, paddingBottom));
				blankdatatable.addCell(ItextPdfCellFactory.getCell(org.getOrganism_name(), HELVETICA_BOLD_9, alignLeft, bordorWhite, 4, fixedLeading));
*/
				blankdatatable.addCell(ItextPdfCellFactory.getCell("\n", HELVETICA_9, alignLeft, bordorWhite, 5, fixedLeading));
				if (org.getColonieus() != null && !org.getColonieus().equals(""))
				    blankdatatable.addCell(ItextPdfCellFactory.getCell(index.incrementAndGet()+"."+org.getOrganism_name()+" (" + org.getColonieus() + ")", HELVETICA_BOLD_10, alignLeft, bordorWhite, 5, fixedLeading));
				else
					blankdatatable.addCell(ItextPdfCellFactory.getCell(index.incrementAndGet()+"."+org.getOrganism_name(), HELVETICA_BOLD_10, alignLeft, bordorWhite, 5, fixedLeading));
				/** Fetching Antibiotic details for organism */
				List<RegistrationBean> antibiotic = reportService.getAntibioticDetailsByCriteria(org);

				if (antibiotic.size() > 0) {
					/** Printing heading String */
					blankdatatable.addCell(ItextPdfCellFactory.getCellWithUnderlinedText("Antibiogram ",
							HELVETICA_BOLDOblique_10, alignCenter, bordorWhite, 5, fixedLeading, paddingBottom));

					/** Printing heading table */
					blankdatatable.addCell(ItextPdfCellFactory.getCell("Antibiotic Name", HELVETICA_BOLD_9,
							alignCenter, bordorBlack, 1, fixedLeading, paddingBottom));
					blankdatatable.addCell(ItextPdfCellFactory.getCell(" Zone of Inhibition(mm)", HELVETICA_BOLD_9,
							alignCenter, bordorBlack, 1, fixedLeading, paddingBottom));
					blankdatatable.addCell(ItextPdfCellFactory.getCell(" Intermediate Value (mm)", HELVETICA_BOLD_9,
							alignCenter, bordorBlack, 1, fixedLeading));
					blankdatatable.addCell(ItextPdfCellFactory.getCell(" Sensitive Zone (mm)", HELVETICA_BOLD_9,
							alignCenter, bordorBlack, 1, fixedLeading));
					blankdatatable.addCell(ItextPdfCellFactory.getCell(" Interpretation", HELVETICA_BOLD_9,
							alignCenter, bordorBlack, 1, fixedLeading));

					/** Printing values starts here */
					for (RegistrationBean setupBean : antibiotic) {
						blankdatatable.addCell(ItextPdfCellFactory.getCell(" " + setupBean.getAntibiotic_name(),
										HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading, paddingBottom));
						
						if(setupBean.getZone_of_inhibition() != null && !setupBean.getZone_of_inhibition().equals(""))
						  blankdatatable.addCell(ItextPdfCellFactory.getCell(" " + setupBean.getZone_of_inhibition(),
										HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
						else
							blankdatatable.addCell(ItextPdfCellFactory.getCell("-",
									HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
						
						if(setupBean.getIntermediate_value() != null && !setupBean.getIntermediate_value().equals(""))
						    blankdatatable.addCell(ItextPdfCellFactory.getCell(" " + setupBean.getIntermediate_value(),
										HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
						else
							blankdatatable.addCell(ItextPdfCellFactory.getCell("-",
									HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
						
						if(setupBean.getSensitivity_zone() != null && !setupBean.getSensitivity_zone().equals(""))
						    blankdatatable.addCell(ItextPdfCellFactory.getCell(" " + setupBean.getSensitivity_zone(),
										HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
						else
							blankdatatable.addCell(ItextPdfCellFactory.getCell("-",
									HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
						
						if(setupBean.getSensitivity() != null && !setupBean.getSensitivity().equals(""))
						    blankdatatable.addCell(ItextPdfCellFactory.getCell(" " + setupBean.getSensitivity(),
								HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
						else
							blankdatatable.addCell(ItextPdfCellFactory.getCell("-",
									HELVETICA_BOLD_9, alignCenter, bordorBlack, 1, fixedLeading));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		});
		return blankdatatable;
	}

}
