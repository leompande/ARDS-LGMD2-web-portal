package org.hisp.dhis.reports.upward.action;


import com.opensymphony.xwork2.Action;
import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.*;
import jxl.write.Number;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

public class GenerateUpwardReportAnalyserResultAction
        implements Action {

    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager(StatementManager statementManager) {
        this.statementManager = statementManager;
    }

    private ReportService reportService;

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat(I18nFormat format) {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    private String reportList;

    public void setReportList(String reportList) {
        this.reportList = reportList;
    }

    private int ouIDTB;

    public void setOuIDTB(int ouIDTB) {
        this.ouIDTB = ouIDTB;
    }

    private int availablePeriods;

    public void setAvailablePeriods(int availablePeriods) {
        this.availablePeriods = availablePeriods;
    }

    /*
        private String aggCB;

        public void setAggCB( String aggCB )
        {
            this.aggCB = aggCB;
        }
    */
    private String reportFileNameTB;

    private String reportModelTB;

    private List<OrganisationUnit> orgUnitList;

    private Period selectedPeriod;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat monthFormat;

    private SimpleDateFormat simpleMonthFormat;

    private Date sDate;

    private Date eDate;

    private String raFolderName;

    private String aggData;

    public void setAggData(String aggData) {
        this.aggData = aggData;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute()
            throws Exception {
        statementManager.initialise();

        // Initialization
        raFolderName = reportService.getRAFolderName();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat("MMM-yyyy");
        monthFormat = new SimpleDateFormat("MMMM");
        simpleMonthFormat = new SimpleDateFormat("MMM");
        String parentUnit = "";

        Report_in selReportObj = reportService.getReport(Integer.parseInt(reportList));

        deCodesXMLFileName = selReportObj.getXmlTemplateName();

        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();


        String inputTemplatePath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;

        String outputReportPath = System.getenv("DHIS2_HOME") + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;

        File newdir=new File(outputReportPath);

        if (!newdir.exists()) {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        if (reportModelTB.equalsIgnoreCase("DYNAMIC-ORGUNIT")) {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit(ouIDTB);
            orgUnitList = new ArrayList<OrganisationUnit>(orgUnit.getChildren());
            Collections.sort(orgUnitList, new IdentifiableObjectNameComparator());
        } else if (reportModelTB.equalsIgnoreCase("STATIC") || reportModelTB.equalsIgnoreCase("STATIC-DATAELEMENTS") || reportModelTB.equalsIgnoreCase("STATIC-FINANCIAL")) {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit(ouIDTB);
            orgUnitList.add(orgUnit);
        } else if (reportModelTB.equalsIgnoreCase("dynamicwithrootfacility")) {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit(ouIDTB);
            orgUnitList = new ArrayList<OrganisationUnit>(orgUnit.getChildren());
            Collections.sort(orgUnitList, new IdentifiableObjectNameComparator());
            orgUnitList.add(orgUnit);

            parentUnit = orgUnit.getName();
        }

        System.out.println(orgUnitList.get(0).getName() + " : " + selReportObj.getName() + " : Report Generation Start Time is : " + new Date());

        selectedPeriod = periodService.getPeriod(availablePeriods);

        sDate = format.parseDate(String.valueOf(selectedPeriod.getStartDate()));

        eDate = format.parseDate(String.valueOf(selectedPeriod.getEndDate()));

        Workbook templateWorkbook = Workbook.getWorkbook(new File(inputTemplatePath));

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook(new File(outputReportPath), templateWorkbook);

        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign(deCodesXMLFileName);
        int orgUnitCount = 0;

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while (it.hasNext()) {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            int count1 = 0;
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while (reportDesignIterator.hasNext()) {
                Report_inDesign report_inDesign = reportDesignIterator.next();

                String deType = report_inDesign.getPtype();
                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>(reportService.getStartingEndingPeriods(deType, selectedPeriod));
                if (calendarList == null || calendarList.isEmpty()) {
                    tempStartDate.setTime(selectedPeriod.getStartDate());
                    tempEndDate.setTime(selectedPeriod.getEndDate());
                    return SUCCESS;
                } else {
                    tempStartDate = calendarList.get(0);
                    tempEndDate = calendarList.get(1);
                }

                if (deCodeString.equalsIgnoreCase("FACILITY")) {
                    tempStr = currentOrgUnit.getName();
                } else if (deCodeString.equalsIgnoreCase("FACILITY-NOREPEAT")) {
                    tempStr = parentUnit;
                } else if (deCodeString.equalsIgnoreCase("FACILITYP")) {
                    tempStr = currentOrgUnit.getParent().getName();
                } else if (deCodeString.equalsIgnoreCase("FACILITYPP")) {
                    tempStr = currentOrgUnit.getParent().getParent().getName();
                } else if (deCodeString.equalsIgnoreCase("FACILITYPPP")) {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getName();
                } else if (deCodeString.equalsIgnoreCase("FACILITYPPPP")) {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getParent().getName();
                } else if (deCodeString.equalsIgnoreCase("PERIOD") || deCodeString.equalsIgnoreCase("PERIOD-NOREPEAT")) {
                    tempStr = simpleDateFormat.format(sDate);
                } else if (deCodeString.equalsIgnoreCase("PERIOD-MONTH")) {
                    tempStr = monthFormat.format(sDate);
                } else if (deCodeString.equalsIgnoreCase("MONTH-START-SHORT")) {
                    tempStr = simpleMonthFormat.format(sDate);
                } else if (deCodeString.equalsIgnoreCase("MONTH-END-SHORT")) {
                    tempStr = simpleMonthFormat.format(eDate);
                } else if (deCodeString.equalsIgnoreCase("MONTH-START")) {
                    tempStr = monthFormat.format(sDate);
                } else if (deCodeString.equalsIgnoreCase("MONTH-END")) {
                    tempStr = monthFormat.format(eDate);
                } else if (deCodeString.equalsIgnoreCase("SLNO")) {
                    tempStr = "" + (orgUnitCount + 1);
                } else if (deCodeString.equalsIgnoreCase("NA")) {
                    tempStr = " ";
                } else {
                    if (sType.equalsIgnoreCase("dataelement")) {
                        if (aggData.equalsIgnoreCase(USECAPTUREDDATA)) {
                            tempStr = reportService.getIndividualResultDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        } else if (aggData.equalsIgnoreCase(GENERATEAGGDATA)) {
                            tempStr = reportService.getResultDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        } else if (aggData.equalsIgnoreCase(USEEXISTINGAGGDATA)) {
                            List<Period> periodList = new ArrayList<Period>(periodService.getPeriodsBetweenDates(tempStartDate.getTime(), tempEndDate.getTime()));
                            Collection<Integer> periodIds = new ArrayList<Integer>(getIdentifiers(Period.class, periodList));
                            tempStr = reportService.getResultDataValueFromAggregateTable(deCodeString, periodIds, currentOrgUnit, reportModelTB);
                        }
                    } else if (sType.equalsIgnoreCase("dataelement-boolean")) {
                        if (aggData.equalsIgnoreCase(USECAPTUREDDATA)) {
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        } else if (aggData.equalsIgnoreCase(GENERATEAGGDATA)) {
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        } else if (aggData.equalsIgnoreCase(USEEXISTINGAGGDATA)) {
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        }

                    } else {
                        if (aggData.equalsIgnoreCase(USECAPTUREDDATA)) {
                            tempStr = reportService.getIndividualResultIndicatorValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit);
                        } else if (aggData.equalsIgnoreCase(GENERATEAGGDATA)) {
                            tempStr = reportService.getResultIndicatorValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit);
                        } else if (aggData.equalsIgnoreCase(USEEXISTINGAGGDATA)) {
                            List<Period> periodList = new ArrayList<Period>(periodService.getPeriodsBetweenDates(tempStartDate.getTime(), tempEndDate.getTime()));
                            Collection<Integer> periodIds = new ArrayList<Integer>(getIdentifiers(Period.class, periodList));
                            tempStr = reportService.getResultDataValueFromAggregateTable(deCodeString, periodIds, currentOrgUnit, reportModelTB);
                        }
                    }
                }

                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet(sheetNo);

                if (tempStr == null || tempStr.equals(" ")) {
                    tempColNo += orgUnitCount;

                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
                    wCellformat.setWrap(true);
                    wCellformat.setAlignment(Alignment.CENTRE);

                    sheet0.addCell(new Blank(tempColNo, tempRowNo, wCellformat));
                }
                else {
                    if (reportModelTB.equalsIgnoreCase("DYNAMIC-ORGUNIT")) {
                        if (deCodeString.equalsIgnoreCase("FACILITYP") || deCodeString.equalsIgnoreCase("FACILITYPP") || deCodeString.equalsIgnoreCase("FACILITYPPP") || deCodeString.equalsIgnoreCase("FACILITYPPPP")) {
                        } else if (deCodeString.equalsIgnoreCase("PERIOD") || deCodeString.equalsIgnoreCase("PERIOD-NOREPEAT") || deCodeString.equalsIgnoreCase("PERIOD-WEEK") || deCodeString.equalsIgnoreCase("PERIOD-MONTH") || deCodeString.equalsIgnoreCase("PERIOD-QUARTER") || deCodeString.equalsIgnoreCase("PERIOD-YEAR") || deCodeString.equalsIgnoreCase("MONTH-START") || deCodeString.equalsIgnoreCase("MONTH-END") || deCodeString.equalsIgnoreCase("MONTH-START-SHORT") || deCodeString.equalsIgnoreCase("MONTH-END-SHORT") || deCodeString.equalsIgnoreCase("SIMPLE-QUARTER") || deCodeString.equalsIgnoreCase("QUARTER-MONTHS-SHORT") || deCodeString.equalsIgnoreCase("QUARTER-MONTHS") || deCodeString.equalsIgnoreCase("QUARTER-START-SHORT") || deCodeString.equalsIgnoreCase("QUARTER-END-SHORT") || deCodeString.equalsIgnoreCase("QUARTER-START") || deCodeString.equalsIgnoreCase("QUARTER-END") || deCodeString.equalsIgnoreCase("SIMPLE-YEAR") || deCodeString.equalsIgnoreCase("YEAR-END") || deCodeString.equalsIgnoreCase("YEAR-FROMTO")) {
                        } else {
                            tempColNo += orgUnitCount;
                        }
                    } else if (reportModelTB.equalsIgnoreCase("dynamicwithrootfacility")) {
                        if (deCodeString.equalsIgnoreCase("FACILITYP") || deCodeString.equalsIgnoreCase("FACILITY-NOREPEAT") || deCodeString.equalsIgnoreCase("FACILITYPP") || deCodeString.equalsIgnoreCase("FACILITYPPP") || deCodeString.equalsIgnoreCase("FACILITYPPPP")) {
                        } else if (deCodeString.equalsIgnoreCase("PERIOD") || deCodeString.equalsIgnoreCase("PERIOD-NOREPEAT") || deCodeString.equalsIgnoreCase("PERIOD-WEEK") || deCodeString.equalsIgnoreCase("PERIOD-MONTH") || deCodeString.equalsIgnoreCase("PERIOD-QUARTER") || deCodeString.equalsIgnoreCase("PERIOD-YEAR") || deCodeString.equalsIgnoreCase("MONTH-START") || deCodeString.equalsIgnoreCase("MONTH-END") || deCodeString.equalsIgnoreCase("MONTH-START-SHORT") || deCodeString.equalsIgnoreCase("MONTH-END-SHORT") || deCodeString.equalsIgnoreCase("SIMPLE-QUARTER") || deCodeString.equalsIgnoreCase("QUARTER-MONTHS-SHORT") || deCodeString.equalsIgnoreCase("QUARTER-MONTHS") || deCodeString.equalsIgnoreCase("QUARTER-START-SHORT") || deCodeString.equalsIgnoreCase("QUARTER-END-SHORT") || deCodeString.equalsIgnoreCase("QUARTER-START") || deCodeString.equalsIgnoreCase("QUARTER-END") || deCodeString.equalsIgnoreCase("SIMPLE-YEAR") || deCodeString.equalsIgnoreCase("YEAR-END") || deCodeString.equalsIgnoreCase("YEAR-FROMTO")) {
                        } else {
                            tempRowNo += orgUnitCount;
                        }
                    }

                    WritableCell cell = sheet0.getWritableCell(tempColNo, tempRowNo);

                    CellFormat cellFormat = cell.getCellFormat();
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
                    wCellformat.setWrap(true);
                    wCellformat.setAlignment(Alignment.CENTRE);

                    if (cell.getType() == CellType.LABEL) {
                        Label l = (Label) cell;
                        l.setString(tempStr);
                        l.setCellFormat(cellFormat);
                    } else {
                        try {
                            sheet0.addCell(new Number(tempColNo, tempRowNo, Double.parseDouble(tempStr), wCellformat));
                        } catch (Exception e) {
                            sheet0.addCell(new Label(tempColNo, tempRowNo, tempStr, wCellformat));
                        }
                    }
                }

                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace(".xls", "");
        fileName += "_" + orgUnitList.get(0).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format(selectedPeriod.getStartDate()) + ".xls";
        File outputReportFile = new File(outputReportPath);
        inputStream = new BufferedInputStream(new FileInputStream(outputReportFile));

        System.out.println(orgUnitList.get(0).getName() + " : " + selReportObj.getName() + " Report Generation End Time is : " + new Date());

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }
}
