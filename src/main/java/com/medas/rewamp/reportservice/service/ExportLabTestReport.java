package com.medas.rewamp.reportservice.service;

import static com.medas.rewamp.reportservice.utils.ItextPdfCellFactory.getNoBorderCell;
import static com.medas.rewamp.reportservice.utils.ItextPdfCellFactory.getPhrase;
import static com.medas.rewamp.reportservice.utils.StringUtils.isEmpty;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.medas.rewamp.reportservice.business.vo.LabReportData;
import com.medas.rewamp.reportservice.business.vo.OfficeLetterHeadBean;
import com.medas.rewamp.reportservice.business.vo.QueryParam;
import com.medas.rewamp.reportservice.business.vo.RegistrationBean;
import com.medas.rewamp.reportservice.business.vo.ReportParam;
import com.medas.rewamp.reportservice.business.vo.UserBean;
import com.medas.rewamp.reportservice.format.CultureReportFormat;
import com.medas.rewamp.reportservice.format.TestReportFormat;
import com.medas.rewamp.reportservice.utils.DateUtil;
import com.medas.rewamp.reportservice.utils.ReportHolder;

import eclinic.laboratory.presentation.action.reports.iface.impl.CommonCultureReportFormatTwo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 14, 2020
 *
 */
@Slf4j
public class ExportLabTestReport implements PdfPageEvent {
	
	@Autowired
	private LabReportService reportService;
	
	@Value("${app.path.attachments}")
	private String uploadPath;
	@Value("${app.path.image}")
	private String imagePath;

	static TestReportFormat format;
	static CultureReportFormat cultureFormat;

	private UserBean userDetails;
	private Map<Integer, List<LabReportData>> testwiseData = null;
	private Map<Integer, LabReportData> testDetailsMap = null;
	public String generateReport(ReportParam reportParam) throws Exception {
		ReportHolder.initialize();
		userDetails = new UserBean();
		userDetails.setUser_id(reportParam.getUserId());
		userDetails.setOffice_id(reportParam.getOfficeId());
		format = null;

		OfficeLetterHeadBean officeLetterHeadBean = reportService.getOfficeLetterHead(userDetails.getOffice_id());
		ReportHolder.setAttribute("officeLetterHeadBean", officeLetterHeadBean);
		ReportHolder.setAttribute("testDetailsIds", reportParam.getTestDetailsIds());
		ReportHolder.setAttribute("imagePath", imagePath);

		Document document = new Document(PageSize.A4, 15.0F, 15.0F, 5.0F, 215.0F);
		String fileName = DateUtil.formatDate("8", new Date()) + ".pdf";
		String filePath = uploadPath + fileName;
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		writer.setPageEvent(new ExportLabTestReport());

		document.addAuthor("MEDAS");
		document.addSubject("Lab report");
		document.addTitle("Lab report");

		document.open();
		// Checking multiple consultation
		boolean multiple = "Y".equals(reportParam.getMultiple());
		if (!multiple) {
			getReportForTest(document, writer);
		} else {
			String consultIds = reportParam.getConsultIds();
			String[] consultIdArr = consultIds.split(",");
			LabReportData params = new LabReportData();
			params.setDispatch_status("Y");
			int index = 0;
			for (String consultId : consultIdArr) {
				params.setConsult_id(consultId);
				String testDetailsIds = reportService.getTestDetailsIdsByCriteria(params);
				ReportHolder.setAttribute("testDetailsIds", testDetailsIds);
				getReportForTest(document, writer);
				index++;
				if (index != consultIdArr.length) {
					format = null;
					documentBreak(document);
				}
			}
		}
		printReportEndMessage(document);
		document.close();
		ReportHolder.remove();
		return filePath;
	}

	/**
	 * Populating data for test details
	 * 
	 * @param request
	 * @param response
	 * @param document
	 * @param writer
	 * @return
	 * @throws Exception
	 */
	Map<Integer, OfficeLetterHeadBean> officeMap = new LinkedHashMap<>();
	public String getReportForTest(Document document, PdfWriter writer) throws Exception {
		ReportHolder.setLastFormat(null);
		ReportHolder.setTestFormat(null);
		consultProfiles = null;

		String test_detailsids = ReportHolder.getAttribute("testDetailsIds");
		LabReportData params = new LabReportData();
		params.setTest_detailsids(test_detailsids);
		params.setHide("N");
		List<LabReportData> testList = reportService.getLabtestDetailsForReport(params);
		List<LabReportData> testResultList = reportService.getLabtestResultsForReport(params);
		
		// Setting Office details
		OfficeLetterHeadBean officeLetterHeadBean = null;
		Integer office_id = testList.get(0).getOffice_id();
		if (officeMap.get(office_id) == null) {
			officeLetterHeadBean = reportService.getOfficeLetterHead(testList.get(0).getOffice_id());
			officeMap.put(office_id, officeLetterHeadBean);
		} else {
			officeLetterHeadBean = officeMap.get(office_id);
		}
		ReportHolder.setAttribute("officeLetterHeadBean", officeLetterHeadBean);
		format = TestReportFormat.getReportClass(officeLetterHeadBean.getReport_class());
		String clinicId = testList.get(0).getClinic_id();
		if (clinicId != null && !clinicId.equals("")) {
			String clinicReportFormat = reportService.getClinicReportFormat(clinicId);
		    if(clinicReportFormat!=null && !clinicReportFormat.equals(""))
		    	format=TestReportFormat.getReportClass(clinicReportFormat);
		}
		
		// Filtering normal and profile tests
		List<LabReportData> nonProfileTestList = testList.stream().filter(e -> e.getProfile_id() == null).collect(Collectors.toList());
		List<LabReportData> profileTestList = testList.stream().filter(e -> e.getProfile_id() != null).collect(Collectors.toList());
		if (!profileTestList.isEmpty()) {
			fetchProfileData(params, test_detailsids, profileTestList);
		}
		// Setting test details list to map by keeping ordering from query
		AtomicInteger i = new AtomicInteger(0);
		testDetailsMap = testList.stream().map(e -> {
			e.setTest_order(i.incrementAndGet());
			return e;
		}).collect(Collectors.toMap(LabReportData::getTest_detailsid, Function.identity()));
		// Mapping test results against test
		testwiseData = new LinkedHashMap<>();
		Integer key = null;
		List<LabReportData> resultList = null;
		for (LabReportData testResult : testResultList) {
			key = testResult.getTest_detailsid();
			resultList = testwiseData.get(key);
			if(resultList == null) {
				resultList = new ArrayList<>();
			}
			resultList.add(testResult);
			testwiseData.put(key, resultList);
		}
		printDataTable(document, writer, testList, nonProfileTestList);
		return null;
	}
	
	/**
	 * Will print the test details to pdf document
	 * 
	 * @param document
	 * @param writer
	 * @param testList
	 * @param nonProfileTestList
	 * @return
	 * @throws Exception
	 */
	private String printDataTable(Document document, PdfWriter writer, List<LabReportData> testList,
			List<LabReportData> nonProfileTestList) throws Exception {
		setHeaderInitialDetails(testList.get(0).getTest_detailsid());
		onStartPage(writer, document);
		printNormalTest(document, writer, nonProfileTestList, true);
		loopConsultProfile(document, writer, !nonProfileTestList.isEmpty());
		return null;
	}

	/**
	 * Method will print non-profile content
	 * 
	 * @param document
	 * @param writer
	 * @param testList
	 * @param groupCategory
	 * @return
	 * @throws Exception
	 */
	private String printNormalTest(Document document, PdfWriter writer, List<LabReportData> testList, boolean groupCategory) throws Exception {
		Integer lastCategory = 0,  category = 0;
		ReportHolder.setLastFormat(null);
		ReportHolder.setTestFormat(null);
		Integer lastAuth = null,  auth = null;
		boolean newPage = false, resultExist = false;
		PdfPTable temp = null;
		int index = 0;
		List<LabReportData> testResults = null;
		getCurrentPageDetails(index, testList, true, groupCategory);
		for (LabReportData test : testList) {
			category = test.getCategory_id();
			ReportHolder.setTestFormat(test.getTest_format());
			auth = test.getVerified_by() == null ? 0 : test.getVerified_by();
			ReportHolder.setShowHeader(true);
			// Condition check for page break
			if (groupCategory && lastCategory != 0 && !category.equals(lastCategory)) {
				newPage = true;
				lastCategory = 0;
				ReportHolder.setLastFormat(null);
				ReportHolder.setShowHeader(false);
			}
			if (ReportHolder.getLastFormat() != null && !ReportHolder.getTestFormat().equals(ReportHolder.getLastFormat())) {
				newPage = true;
				ReportHolder.setLastFormat(null);
				ReportHolder.setShowHeader(false);
			}
			if (lastAuth != null && !auth.equals(lastAuth)) {
				newPage = true;
				resultExist = false;
			}
			// Header contents will be set before document break and footer contents after that
			if (newPage) {
				List<LabReportData> testDetails = getCurrentPageDetails(index, testList, false, groupCategory);
				newPage = false;
				documentBreak(document);
				setFooterDetails(testDetails);
				ReportHolder.setShowHeader(true);
			}
			if (!"3".equals(test.getTest_format())) {
				// Printing category name
				if (groupCategory && !category.equals(lastCategory)) {
					document.add(format.getCategoryName(test.getCategory_name()));
				}
				// Printing Headers
				if (!ReportHolder.getTestFormat().equals(ReportHolder.getLastFormat())) {
					temp = format.getFormatHeader(test.getTest_format());
					if (temp != null) {
						document.add(temp);
					}
				}
				// For single test
				if ("N".equals(test.getSingle_test())) {
					//document.add(format.getTestName(test.getTest_name()));
					if(test.getDac() != null && test.getDac().equals("N"))
						document.add(format.getTestName("*"+test.getTest_name()));
					else
						document.add(format.getTestName(test.getTest_name()));
				}
				// Printing Test Results
				testResults = testwiseData.get(test.getTest_detailsid());
				if (testResults != null) {
					resultExist = true;
					if ("1".equals(test.getTest_format())) {
						document.add(format.getFormatOneData(testResults));
					} else if ("2".equals(test.getTest_format())) {
						document.add(format.getFormatTwoData(testResults));
					}
					// Printing Remarks and Notes
					if (!isEmpty(test.getNotes())) {
						format.setNotes(document, test.getNotes());
					}
					if (!isEmpty(test.getRemarks())) {
						document.add(format.getRemarksTable(test.getRemarks()));
					}
				}
				// Printing sample type
				if (resultExist && printSampleTypePrecheck(index, testList, groupCategory)) {
					document.add(format.getSampleType(test.getSample_type()));
					resultExist = false;
				}
			} else {
				// Format should be displayed in individual page
				ReportHolder.setTestFormat("");
				format.getFormatThreeData(document, test);
			}
			if ("Y".equals(test.getCulture_status())) {
				getCultureTestData(document, test);
			}
			lastCategory = category;
			ReportHolder.setLastFormat(ReportHolder.getTestFormat());
			lastAuth = auth;
			index++;
		}
		return null;
	}

	/**
	 * Report End message
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void printReportEndMessage(Document document) throws DocumentException {
		PdfPTable blankdatatable = new PdfPTable(new float[] {100});
		blankdatatable.setWidthPercentage(100f);
		blankdatatable.setTotalWidth(100f);
		
		PdfPCell cell = getNoBorderCell(getPhrase("---------------- End Of Report ----------------", FontFactory.getFont("Helvetica-Bold", 10.0F)), 10);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		blankdatatable.addCell(cell);
		document.add(blankdatatable);
	}
	
	/**
	 * Condition check for sample type break
	 * 
	 * @param current
	 * @param next
	 * @param groupCategory
	 * @return boolean
	 */
	private boolean printSampleTypePrecheck(int index, List<LabReportData> testList, boolean groupCategory) {
		LabReportData current = testList.get(index);
		LabReportData next = null;
		if (!isEmpty(current.getSample_type())) {
			// End of List
			if (testList.size() == index + 1) {
				return true;
			}
			next = testList.get(index + 1);
			// Check Page break
			if (!current.getSample_id().equals(next.getSample_id())
					|| isPageBreak(current, next, groupCategory)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Condition check for document page break
	 * 
	 * @param current
	 * @param next
	 * @param groupCategory
	 * @return boolean
	 */
	private boolean isPageBreak(LabReportData current, LabReportData next, boolean groupCategory) {
		if (current.getVerified_by() == null)
			current.setVerified_by(0);
		if (next.getVerified_by() == null)
			next.setVerified_by(0);
		// Check Page break
		if (!current.getTest_format().equals(next.getTest_format())
				|| groupCategory && !current.getCategory_id().equals(next.getCategory_id()) 
				|| !current.getVerified_by().equals(next.getVerified_by())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Manual Document Page break
	 * 
	 * @param document
	 */
	private void documentBreak(Document document) {
		document.newPage();
	}
	
	/**
	 * Will fetch current page tests and set footer and header details
	 * 
	 * @param fromIndex
	 * @param testList
	 * @param setBoth 
	 * @param groupCategory 
	 * @return 
	 */
	private List<LabReportData> getCurrentPageDetails(int fromIndex, List<LabReportData> testList, boolean setBoth, boolean groupCategory) {
		List<LabReportData> remainList = testList.subList(fromIndex, testList.size());
		// End of List
		String testDetailsIds = null;
		if (remainList.size() == 1) {
			testDetailsIds = String.valueOf(remainList.get(0).getTest_detailsid());
		} else {
			LabReportData next = null;
			int index = 0;
			StringJoiner testDetails = new StringJoiner(",");
			for (LabReportData current : remainList) {
				index++;
				testDetails.add(String.valueOf(current.getTest_detailsid()));
				next = remainList.get(index);
				if (isPageBreak(current, next, groupCategory)) {
					break;
				}
				// End of List
				if(remainList.size() == index + 1) {
					break;
				}
			}
			testDetailsIds = testDetails.toString();
		}
		List<LabReportData> testDetails = getTestDetailsForIds(testDetailsIds);
		setHeaderDetails(testDetails);
		if(setBoth)
			setFooterDetails(testDetails);
		return testDetails;
	}

	Map<String, List<LabReportData>> testLevelMap;
	Map<Integer, LabReportData> profileMap;
	Map<String, List<String>> profileInProfileMap;
	List<LabReportData> consultProfiles;
	
	/**
	 * Setting required profile test data
	 * 
	 * @param params
	 * @param test_detailsids
	 * @param testList
	 * @throws DaoException
	 */
	private void fetchProfileData(LabReportData params, String test_detailsids, List<LabReportData> testList) {
		params.setTest_detailsids(test_detailsids);
		// Getting all profile tests tree
		List<LabReportData> profileTestList = reportService.getProfileTestsTree(params);
		// Group by level
		String key = null;
		testLevelMap = new LinkedHashMap<>();
		consultProfiles = new ArrayList<>();
		List<String> profileInLevel = null;
		List<LabReportData> testInLevel = null;
		Set<Integer> profiles = new LinkedHashSet<>();
		Set<String> consultProfiles = new LinkedHashSet<>();
		for (LabReportData test : profileTestList) {
			String[] profileArr = test.getCurrent_level().split(",");
			// Capturing all profiles
			for (String profile : profileArr) {
				profiles.add(Integer.valueOf(profile));
			}
			// capturing consult profile (0'th level profiles)
			consultProfiles.add(test.getConsult_labtest_id() + "_" + profileArr[0]);
		}
		params.setProfile_ids(profiles.stream().map(String::valueOf).collect(Collectors.joining(",")));
		// Getting profile test basic details
		List<LabReportData> profileTest = reportService.getTestBasicDetails(params);
		// profile test map
		profileMap = profileTest.stream().collect(Collectors.toMap(LabReportData::getTest_id, Function.identity()));
		// Collecting consult profile data
		this.consultProfiles = consultProfiles.stream().map(e -> {
			String[] temp = e.split("_");
			LabReportData profile = profileMap.get(Integer.parseInt(temp[1]));
			profile.setConsult_labtest_id(Integer.parseInt(temp[0]));
			return profile;
		}).collect(Collectors.toList());
		
		profileInProfileMap = new LinkedHashMap<>();
		String parent = null;
		for (LabReportData test : profileTestList) {
			// Level wise test map
			key = getTestKey(test);
			testInLevel = testLevelMap.get(key);
			if (testInLevel == null) {
				testInLevel = new ArrayList<>();
			}
			testInLevel.add(test);
			testLevelMap.put(key, testInLevel);
			// Profile in profile map
			if (test.getProfile_level() > 0) {
				List<String> profileArr = Arrays.asList(test.getCurrent_level().split(","));
				for (int level = 1; level <= test.getProfile_level(); level++) {
					parent = test.getConsult_labtest_id() + "_"
							+ profileArr.stream().limit(level).collect(Collectors.joining("_"));
					profileInLevel = profileInProfileMap.get(parent);
					if (profileInLevel == null) {
						profileInLevel = new ArrayList<>();
					}
					key = parent + "_" + profileArr.get(level);
					if (!profileInLevel.contains(key)) {
						profileInLevel.add(key);
					}
					profileInProfileMap.put(parent, profileInLevel);
				}
			}
		}
	}
	
	private String getTestKey(LabReportData test) {
		String tree = test.getCurrent_level().contains(",") ? test.getCurrent_level().replaceAll(",", "_") : test.getCurrent_level();
		return test.getConsult_labtest_id() + "_" + tree;
	}
	
	/**
	 * Printing Profile Test Data
	 * 
	 * @param document
	 * @param writer
	 * @param nonProfile 
	 * @throws Exception
	 */
	private void loopConsultProfile(Document document, PdfWriter writer, boolean nonProfile) throws Exception {
		if (consultProfiles != null && consultProfiles.size() > 0) {
			ReportHolder.setShowHeader(false);
			if(nonProfile) {
				documentBreak(document);
			}
			Integer profileIndex = 0;
			for (LabReportData profileDetails : consultProfiles) {
				ReportHolder.setShowHeader(false);
				loopProfile(profileDetails, profileIndex, profileDetails.getConsult_labtest_id().toString(), document, writer);
				profileIndex++;
			}
		}
	}
	
	/**
	 * To Check and print profile inside profile
	 * 
	 * @param document
	 * @param writer
	 * @param nonProfile 
	 * @throws Exception
	 */
	private void loopProfile(LabReportData profileDetails, Integer profileIndex, String parent, Document document, PdfWriter writer) throws Exception {
		String key = null;
		List<LabReportData> testList = null;
		key = parent + "_" + profileDetails.getTest_id();
		testList = testLevelMap.get(key);
		boolean culture = false, groupCategory = true;
		
		// Normal Tests
		if (profileDetails != null && testList != null) {
			culture = "Y".equals(profileDetails.getCulture_status());
			groupCategory = "Y".equals(profileDetails.getReport_format());
			if (groupCategory || culture) {
				// Order by category
				/*testList = testList.stream().map(e -> testDetailsMap.get(e.getTest_detailsid()))
						.sorted(Comparator.comparingInt(LabReportData::getTest_order)).collect(Collectors.toList());*/
				
				int[] testDtlsIds = testList.stream().mapToInt(LabReportData::getTest_detailsid).toArray();
				String test_detailsids = IntStream.of(testDtlsIds)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(","));
				LabReportData params = new LabReportData();
				params.setTest_detailsids(test_detailsids);
				params.setOffice_id(userDetails.getOffice_id());
				testList = reportService.getLabtestDetailsForReportProfile(params);
			} else {
				// Order by Format
				testList = testList.stream().map(e -> testDetailsMap.get(e.getTest_detailsid()))
						.sorted(Comparator.comparing(LabReportData::getReport_format)).collect(Collectors.toList());
			}
			if (!culture) {
				if (profileIndex > 0) {
					documentBreak(document);
				}
				document.add(format.getProfileName(profileDetails.getTest_name(), true));
				printNormalTest(document, writer, testList, groupCategory);
			} else {
				List<LabReportData> details = getCurrentPageDetails(0, testList, false, false);
				if (profileIndex > 0) {
					documentBreak(document);
				}
				setFooterDetails(details);
				getCultureTestProfileData(document, writer, profileDetails, testList);
			}
			profileIndex++;
		}
		// Profile Tests
		List<String> profileList = profileInProfileMap.get(key);
		if (profileList != null) {
			LabReportData profile = null;
			Integer profile_id = 0;
			int index = 0;
			for (String profile_level : profileList) {
				index = profile_level.lastIndexOf("_");
				profile_id = Integer.parseInt(profile_level.substring(index + 1));
				profile = profileMap.get(profile_id);
				profile.setConsult_labtest_id(profileDetails.getConsult_labtest_id());
				ReportHolder.setShowHeader(false);
				loopProfile(profile, profileIndex, key, document, writer);
			}
		}
	}
	
	/**
	 * Non profile Culture test logic
	 * 
	 * @param document
	 * @param test
	 */
	private void getCultureTestData(Document document, LabReportData test) {
		try {
			RegistrationBean registrationBean = new RegistrationBean();
			cultureFormat = new CommonCultureReportFormatTwo();
			// Non profile Culture test logic
			registrationBean.setLab_idno(test.getLab_idno());
			registrationBean.setTestId(test.getTest_id().toString());
			// printing test organism and antibiotics for the test
			List<RegistrationBean> list = reportService.getAntibioAndOrganisms(registrationBean);
			if (list != null && !list.isEmpty()) {
				PdfPTable tableData = cultureFormat.getDataTable4AntibioAndOrganismNames(registrationBean);
				if (tableData != null) {
					document.add(cultureFormat.getBlankLine(1f));
					document.add(tableData);
				}
				document.add(cultureFormat.getBlankLine(5f));
				document.add(cultureFormat.printOrganismAndAntibiotic(list, registrationBean));
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * Culture profile data
	 * 
	 * @param document
	 * @param writer
	 * @param profileTest
	 * @param testList
	 */
	private void getCultureTestProfileData(Document document, PdfWriter writer, LabReportData profileTest,
			List<LabReportData> testList) {
		RegistrationBean header = ReportHolder.getAttribute("headerDetails", RegistrationBean.class);
		try {
			RegistrationBean registrationBean = new RegistrationBean();
			cultureFormat = getCultureReportClass(header, profileTest);
			// Printing Culture profile name
			document.add(cultureFormat.printProfileName(profileTest.getTest_name()));

			if (testList.size() > 0) {
				String lab_idno = String.valueOf(testList.get(0).getLab_idno());
				testList = getProfileOrder(profileTest.getTest_id(), testList);
				for (LabReportData test : testList) {
					ArrayList<RegistrationBean> testResultList = copyResultsToRegBean(testwiseData.get(test.getTest_detailsid()));
					PdfPTable tableData = null;
					// Printing test formats data for the test
					if ("1".equals(test.getTest_format())) {
						if ("Y".equals(test.getSingle_test())) {
							tableData = cultureFormat.printFormatOneReport(testResultList, test.getTest_name());
						} else {
							tableData = cultureFormat.printFormatOneReport(testResultList, null);
						}
					} else if ("2".equals(test.getTest_format())) {
						tableData = cultureFormat.printFormatTwoResult(testResultList, registrationBean);
					}
					document.add(tableData);
					registrationBean.setLab_idno(test.getLab_idno());
					registrationBean.setTestId(test.getTest_id().toString());
					// printing test organism and antibiotics for the test
					List<RegistrationBean> list = reportService.getAntibioAndOrganisms(registrationBean);
					if (list != null && list.size() > 0) {
						tableData = cultureFormat.getDataTable4AntibioAndOrganismNames(registrationBean);
						if (tableData != null) {
							document.add(cultureFormat.getBlankLine(1f));
							document.add(tableData);
						}
						document.add(cultureFormat.getBlankLine(5f));
						document.add(cultureFormat.printOrganismAndAntibiotic(list, registrationBean));
					}
					// Printing notes
					if (!isEmpty(test.getNotes())) {
						format.setNotes(document, test.getNotes());
					}
				}
				// Printing culture profile remarks
				String microRemarks = reportService.getMicroTestRemarks(lab_idno);
				if (!isEmpty(microRemarks)) {
					document.add(cultureFormat.printRemarks(microRemarks));
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Return culture report format for test
	 * 
	 * @param header
	 * @param test
	 * @return
	 * @throws DaoException 
	 */
	private CultureReportFormat getCultureReportClass(RegistrationBean header, LabReportData test) {
		QueryParam param = new QueryParam();
		param.setId(test.getTest_id());
		param.setOfficeId(Integer.parseInt(header.getClinic_id()));
		String impl = reportService.getReportClassForClinic(param);
		return impl == null ? new CommonCultureReportFormatTwo() : CultureReportFormat.getReportClass(impl);
	}

	/**
	 * Will copy details for LabReportData bean to RegistrationBean bean for further process
	 * 
	 * @param testResults
	 * @return
	 */
	private ArrayList<RegistrationBean> copyResultsToRegBean(List<LabReportData> testResults) {
		ArrayList<RegistrationBean> regBeanList = new ArrayList<>();
		RegistrationBean regBean = null;
		if (testResults != null && !testResults.isEmpty()) {
			for (LabReportData result : testResults) {
				regBean = new RegistrationBean();
				regBean.setIs_hide(result.getHide());
				regBean.setMeasure(result.getTest_unit());
				regBean.setTestName(result.getParameter_name());
				regBean.setTestResult(result.getTest_result());
				regBean.setNormalValue(result.getNormal_value());
				regBeanList.add(regBean);
			}
		}
		return regBeanList;
	}
	
	/**
	 * Getting test details by profile order
	 * 
	 * @param profileId
	 * @param testList
	 * @return
	 * @throws DaoException
	 */
	private List<LabReportData> getProfileOrder(Integer profileId, List<LabReportData> testList) {
		if (testList.size() > 1) {
			LabReportData param = new LabReportData();
			param.setProfile_id(profileId);
			param.setTest_detailsids(testList.stream().map(e -> String.valueOf(e.getTest_detailsid())).collect(Collectors.joining(",")));
			List<LabReportData> temp = reportService.getProfileOrderByTestDetails(param);
			if (temp.size() == testList.size()) {
				testList = temp.stream().map(e -> testDetailsMap.get(e.getTest_detailsid())).collect(Collectors.toList());
			}
		}
		return testList;
	}
	
	/**
	 * Setting initial header details
	 * 
	 * @param testDetailsId
	 */
	private void setHeaderInitialDetails(Integer testDetailsId) {
		RegistrationBean registrationBean = new RegistrationBean();
		registrationBean.setTestDetailsid(String.valueOf(testDetailsId));
		try {
			registrationBean = reportService.getTestDetailsById(registrationBean);
			Integer age = 0; Integer days = 0; String dispAge = "";
			if ((registrationBean.getPatient_age() != null) && (registrationBean.getPatient_age() != 0)) {
				age = registrationBean.getPatient_age() * 365;
				dispAge = registrationBean.getPatient_age() + " Yrs ";
			}
			if ((registrationBean.getPatient_agemonth() != null) && (registrationBean.getPatient_agemonth() != 0)) {
				age += registrationBean.getPatient_agemonth() * 30;
				dispAge = dispAge+registrationBean.getPatient_agemonth() + " Month ";
			}
			if ((registrationBean.getPatient_ageweek() != null) && (registrationBean.getPatient_ageweek() != 0)) {
				age += registrationBean.getPatient_ageweek() * 7;
				days += registrationBean.getPatient_ageweek() * 7;
			}
			if ((registrationBean.getPatient_agedays() != null) && (registrationBean.getPatient_agedays() != 0)) {
				age += registrationBean.getPatient_agedays();
				days += registrationBean.getPatient_agedays();
			}
			if (days > 0) {
				dispAge = dispAge + days + " Days";
			}
			RegistrationBean setBeanRef = null;
			if ((registrationBean.getRefer_status().equals("Y")) && (registrationBean.getRdoctor_id() != null)) {
				String depLab = reportService.isDepartmentLab(registrationBean.getDepartment_id());
				setBeanRef = new RegistrationBean();
				if (depLab != null && !depLab.equals("") && depLab.equals("Y")) {
					setBeanRef.setRdoctor_id(registrationBean.getRdoctor_id());
					setBeanRef = reportService.getReferDoctorById(setBeanRef);
					if (setBeanRef != null && setBeanRef.getRdoctor_name() != null) {
						registrationBean.setDoctors_name(setBeanRef.getRdoctor_name());
						registrationBean.setClinic_id(setBeanRef.getClinic_id());
						if (setBeanRef.getClinic_id() != null) {
							setBeanRef = reportService.getClinicById(setBeanRef.getClinic_id());
							if (setBeanRef != null && setBeanRef.getClinic_name() != null) {
								registrationBean.setClinic_name(setBeanRef.getClinic_name());
								registrationBean.setMail_to(setBeanRef.getClinic_email());
							}
						}
					}
				}
			}
			registrationBean.setPatientAge(dispAge);
			registrationBean.setAge(age);
			ReportHolder.setAttribute("headerDetails", registrationBean);
			ReportHolder.setAttribute("footerDetails", new LabReportData());
			UserBean doc = null;
			if (registrationBean.getOffice_id() != null) {
				String userId = reportService.getPathologistByOffice(registrationBean.getOffice_id());
				if (userId != null) {
					doc = reportService.getUserDetailByUserID(userId);
				}
			}
			ReportHolder.setAttribute("pathologist", doc);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Setting page wise header content
	 * 
	 * @param testDetails
	 */
	private void setHeaderDetails(List<LabReportData> testDetails) {
		RegistrationBean registrationBean = ReportHolder.getAttribute("headerDetails", RegistrationBean.class);
		if (testDetails != null && !testDetails.isEmpty()) {
			boolean status = testDetails.stream().allMatch(e -> "Y".equals(e.getClosed_status()));
			registrationBean.setClosed_status(status ? "Y" : "N");
			status = testDetails.stream().allMatch(e -> "Y".equals(e.getDispatch_status()));
			registrationBean.setDispatch_status(status ? "Y" : "N");
			status = testDetails.stream().allMatch(e -> "Y".equals(e.getReverse_auth_status()));
			registrationBean.setReverse_authent(status ? "Y" : "N");
			registrationBean.setTest_format(testDetails.get(0).getTest_format());
		}
		ReportHolder.setAttribute("headerDetails", registrationBean);
	}
	
	/**
	 * Will return test details list for given test details id's from the map
	 * 
	 * @param testDetailsIds
	 * @return
	 */
	private List<LabReportData> getTestDetailsForIds(String testDetailsIds) {
		if(testDetailsIds == null || testDetailsIds.trim().equals(""))
			return null;
		String[] arr = testDetailsIds.split(",");
		List<LabReportData> testDetails = new ArrayList<>();
		LabReportData details = null;
		for (String testDetailsId : arr) {
			details = testDetailsMap.get(Integer.valueOf(testDetailsId));
			if (details != null) {
				testDetails.add(details);
			}
		}
		return testDetails;
	}
	
	Map<Integer, UserBean> userMap = new LinkedHashMap<>();
	/**
	 * Setting page wise footer content
	 * 
	 * @param testDetails
	 */
	private void setFooterDetails(List<LabReportData> testDetails) {
		LabReportData registrationBean = ReportHolder.getAttribute("footerDetails", LabReportData.class);
		ReportHolder.setAttribute("authUser", null);
		ReportHolder.setAttribute("forwardUser", null);
		boolean isIso = false, isJci = false, isDac = false, isCap = false, isPrinted = false;
		Date collectedOn = null, receivedOn = null, authenticatedOn = null, dispatchedOn = null, printedOn = null;
		Integer enteredBy = null, verifiedBy = null; 
		if (testDetails != null && !testDetails.isEmpty()) {
			isPrinted = true;
			for (LabReportData test : testDetails) {
				// Setting accreditation details
				// Any match
				if (!isIso && test.isIso())
					isIso = true;
				if (!isJci && test.isJci())
					isJci = true;
				if (!isDac && test.isDac())
					isDac = true;
				if (!isCap && test.isCap())
					isCap = true;
				// All match
				if (isPrinted && !"Y".equals(test.getPrint_status()))
					isPrinted = false;
				// Setting Authentication details
				// Latest date
				if (test.getCollection_date() != null && (collectedOn == null || test.getCollection_date().compareTo(collectedOn) > 0))
					collectedOn = test.getCollection_date();
				if (test.getAccession_date() != null && (receivedOn == null || test.getAccession_date().compareTo(receivedOn) > 0))
					receivedOn = test.getAccession_date();
				if (test.getVerified_date() != null && (authenticatedOn == null || test.getVerified_date().compareTo(authenticatedOn) > 0))
					authenticatedOn = test.getVerified_date();
				if (test.getDispatch_date() != null && (dispatchedOn == null || test.getDispatch_date().compareTo(dispatchedOn) > 0))
					dispatchedOn = test.getDispatch_date();
				if (test.getPrinted_date() != null && (printedOn == null || test.getPrinted_date().compareTo(printedOn) > 0))
					printedOn = test.getPrinted_date();
				// Last person
				if (test.getEntered_by() != null && "Y".equals(test.getForward_status()))
					enteredBy = test.getEntered_by();
				if (test.getVerified_by() != null && "Y".equals(test.getClosed_status()))
					verifiedBy = test.getVerified_by();
			}
			registrationBean.setIso(isIso ? "Y" : "N");
			registrationBean.setJci(isJci ? "Y" : "N");
			registrationBean.setDac(isDac ? "Y" : "N");
			registrationBean.setCap(isCap ? "Y" : "N");
			registrationBean.setPrint_status(isPrinted ? "Y" : "N");

			registrationBean.setCollection_date(collectedOn);
			registrationBean.setAccession_date(receivedOn);
			registrationBean.setVerified_date(authenticatedOn);
			registrationBean.setDispatch_date(dispatchedOn);
			registrationBean.setPrinted_date(printedOn);

			if (enteredBy != null) {
				UserBean authUser = userMap.get(enteredBy);
				if (authUser == null) {
					authUser = reportService.getUserDetailByUserID(enteredBy.toString());
					userMap.put(enteredBy, authUser);
				}
				ReportHolder.setAttribute("forwardUser", authUser);
			}
			if (verifiedBy != null) {
				UserBean authUser = userMap.get(verifiedBy);
				if (authUser == null) {
					authUser = reportService.getUserDetailByUserID(verifiedBy.toString());
					userMap.put(verifiedBy, authUser);
				}
				ReportHolder.setAttribute("authUser", authUser);
			}
		}
	}
	
	/**
	 * Will Print page header
	 */
	@Override
	public void onStartPage(PdfWriter arg0, Document document) {
		try {
			if (format != null) {
				PdfPTable headTable = format.getHeaderTable();
				document.add(headTable);
				// Printing header
				if (ReportHolder.showHeader() && "1".equals(ReportHolder.getTestFormat())) {
					document.add(format.getFormatOneHeader());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			setPageNumber(writer, document);
			PdfPTable footerTable = format.getFooterTable();
			if (footerTable != null) {
				footerTable.setTotalWidth(100.0F);
				footerTable.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
				footerTable.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	PdfTemplate tpl;
	BaseFont bf = null;
	PdfContentByte cb;
	int fontSize = 8;
	/**
	 * Setting page number at footer
	 * 
	 * @param writer
	 * @param document
	 */
	private void setPageNumber(PdfWriter writer, Document document) {
		int pageN = writer.getPageNumber();
		float y = 63;
		String text = "Page " + pageN + " of ";
		float len = this.bf.getWidthPoint(text, fontSize);
		this.cb.beginText();
		this.cb.setFontAndSize(this.bf, fontSize);
		float width = document.getPageSize().getWidth() - 78.0F;
		this.cb.setTextMatrix(width, y);
		this.cb.showText(text);
		this.cb.endText();
		this.cb.addTemplate(this.tpl, width + len, y);
	}

	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		try {
			this.bf = BaseFont.createFont("Helvetica", "Cp1252", false);
			this.cb = writer.getDirectContent();
			this.tpl = this.cb.createTemplate(50.0F, 50.0F);
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
		this.tpl.beginText();
		this.tpl.setFontAndSize(this.bf, fontSize);
		this.tpl.showText(String.valueOf(writer.getPageNumber() - 1));
		this.tpl.endText();
	}
	
	@Override
	public void onChapter(PdfWriter arg0, Document arg1, float arg2, Paragraph arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChapterEnd(PdfWriter arg0, Document arg1, float arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGenericTag(PdfWriter arg0, Document arg1, Rectangle arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onParagraph(PdfWriter arg0, Document arg1, float arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onParagraphEnd(PdfWriter arg0, Document arg1, float arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSection(PdfWriter arg0, Document arg1, float arg2, int arg3, Paragraph arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSectionEnd(PdfWriter arg0, Document arg1, float arg2) {
		// TODO Auto-generated method stub

	}
	
}
