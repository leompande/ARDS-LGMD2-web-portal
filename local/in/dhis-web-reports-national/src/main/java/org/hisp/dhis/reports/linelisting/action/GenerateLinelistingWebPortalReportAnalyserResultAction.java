package org.hisp.dhis.reports.linelisting.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateLinelistingWebPortalReportAnalyserResultAction
    implements Action
{
    // private static final String NULL_REPLACEMENT = "0";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    /*
     * private DataSetService dataSetService;
     * 
     * public void setDataSetService( DataSetService dataSetService ) {
     * this.dataSetService = dataSetService; }
     */
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    /*
     * private AggregationService aggregationService;
     * 
     * public void setAggregationService( AggregationService aggregationService
     * ) { this.aggregationService = aggregationService; }
     */
    /*
     * private IndicatorService indicatorService;
     * 
     * public void setIndicatorService( IndicatorService indicatorService ) {
     * this.indicatorService = indicatorService; }
     */
    /*
     * private DataValueService dataValueService;
     * 
     * public void setDataValueService( DataValueService dataValueService ) {
     * this.dataValueService = dataValueService; }
     */
    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    /*
     * private DataElementCategoryService dataElementCategoryService;
     * 
     * public void setDataElementCategoryService( DataElementCategoryService
     * dataElementCategoryService ) { this.dataElementCategoryService =
     * dataElementCategoryService; }
     */
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * private DBConnection dbConnection;
     * 
     * public void setDbConnection( DBConnection dbConnection ) {
     * this.dbConnection = dbConnection; }
     */

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    /*
     * private PeriodStore periodStore;
     * 
     * public void setPeriodStore( PeriodStore periodStore ) { this.periodStore
     * = periodStore; }
     */
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    /*
     * private String contentType;
     * 
     * public String getContentType() { return contentType; }
     */

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
     * private int bufferSize;
     * 
     * public int getBufferSize() { return bufferSize; }
     */

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    // private OrganisationUnit selectedOrgUnit;

    // public OrganisationUnit getSelectedOrgUnit()
    // {
    // return selectedOrgUnit;
    // }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private List<String> dataValueList;

    public List<String> getDataValueList()
    {
        return dataValueList;
    }

    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }

    private List<String> slNos;

    public List<String> getSlNos()
    {
        return slNos;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private SimpleDateFormat monthFormat;

    public SimpleDateFormat getMonthFormat()
    {
        return monthFormat;
    }

    private SimpleDateFormat yearFormat;

    public SimpleDateFormat getYearFormat()
    {
        return yearFormat;
    }

    // private List<String> deCodeType;

    // private List<String> serviceType;

    private String reportFileNameTB;

    /*
     * public void setReportFileNameTB( String reportFileNameTB ) {
     * this.reportFileNameTB = reportFileNameTB; }
     */
    private String reportModelTB;

    /*
     * public void setReportModelTB( String reportModelTB ) { this.reportModelTB
     * = reportModelTB; }
     */
    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    /*
     * private String startDate;
     * 
     * public void setStartDate( String startDate ) { this.startDate =
     * startDate; }
     * 
     * private String endDate;
     * 
     * public void setEndDate( String endDate ) { this.endDate = endDate; }
     * 
     * private List<String> orgUnitListCB;
     * 
     * public void setOrgUnitListCB( List<String> orgUnitListCB ) {
     * this.orgUnitListCB = orgUnitListCB; }
     */
    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    // private Hashtable<String, String> serviceList;
    /*
     * private List<Integer> sheetList;
     * 
     * private List<Integer> rowList;
     * 
     * private List<Integer> colList;
     * 
     * private List<Integer> rowMergeList;
     * 
     * private List<Integer> colMergeList;
     */
    private Date sDate;

    private Date eDate;

    // private List<Integer> totalOrgUnitsCountList;

    private OrganisationUnit currentOrgUnit;

    private Map<String, String> resMap;

    private Map<String, String> resMapForDeath;

    Connection con = null;

    private String raFolderName;

    private SimpleDateFormat simpleMonthFormat;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        System.out.println( "OrgUnit  is : " + ouIDTB );
        statementManager.initialise();
        // con = dbConnection.openConnection();

        // Initialization
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        // deCodeType = new ArrayList<String>();
        // serviceType = new ArrayList<String>();
        // totalOrgUnitsCountList = new ArrayList<Integer>();
        // String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        // deCodesXMLFileName = reportList + "DECodes.xml";

        initializeResultMap();

        initializeLLDeathResultMap();

        List<Integer> llrecordNos = new ArrayList<Integer>();

        // String parentUnit = "";

        // Getting Report Details
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        String parentUnit = "";

        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" )
            || reportModelTB.equalsIgnoreCase( "DYNAMIC-DATAELEMENT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        }
        else if ( reportModelTB.equalsIgnoreCase( "STATIC" ) || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" )
            || reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            orgUnitList.add( orgUnit );

            parentUnit = orgUnit.getName();
        }

        System.out.println( reportModelTB + " : " + reportFileNameTB + " : " + deCodesXMLFileName + " : " + ouIDTB );
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        System.out.println( "Size of OrgUnit List is : " + orgUnitList.size() );

        /*
         * sheetList = new ArrayList<Integer>(); rowList = new
         * ArrayList<Integer>(); colList = new ArrayList<Integer>();
         * rowMergeList = new ArrayList<Integer>();
         * 
         * colMergeList = new ArrayList<Integer>();
         */
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
        //    + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        // OrgUnit Info
        currentOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        System.out.println( "orgunit" + currentOrgUnit.getName() + ",Start Date " + sDate + ",End Date " + eDate
            + " XML File : " + deCodesXMLFileName + ",selected period is " + selectedPeriod.getId() );
        //llrecordNos = reportService.getLinelistingRecordNos( currentOrgUnit, selectedPeriod, deCodesXMLFileName );
        
        llrecordNos = getLinelistingDeathRecordNos( currentOrgUnit, selectedPeriod );
        
        // Getting DataValues
        dataValueList = new ArrayList<String>();
        // List<String> deCodesList = getDECodes( deCodesXMLFileName );
        System.out.println( "Line Listing Death Record Count is :" + llrecordNos.size() );

        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        List<Report_inDesign> reportDesignListLLDeath = reportService.getReportDesign( deCodesXMLFileName );
        List<Report_inDesign> reportDesignListLLMaternalDeath = reportService.getReportDesign( deCodesXMLFileName );
        int orgUnitCount = 0;

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            int count1 = 0;
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String deType = report_inDesign.getPtype();
                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType,
                    selectedPeriod ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    tempStr = parentUnit;
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                    || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                {
                    tempStr = yearFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-START" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-END" ) )
                {
                    tempStr = monthFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + (orgUnitCount + 1);
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate
                            .getTime(), currentOrgUnit, reportModelTB );
                    }
                    else if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                    {
                        tempStr = reportService.getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate
                            .getTime(), currentOrgUnit, reportModelTB );
                    }
                    else
                    {
                        tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                            tempEndDate.getTime(), currentOrgUnit );
                    }
                }

                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                // System.out.println( ",Temp Row no is : " + tempRowNo +
                // ", Temp Col No is : " + tempColNo );

                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    tempColNo += orgUnitCount;

                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }

                else
                {
                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                            || deCodeString.equalsIgnoreCase( "YEAR-END" )
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        }
                        else
                        {
                            tempColNo += orgUnitCount;
                        }
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                            || deCodeString.equalsIgnoreCase( "YEAR-END" )
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        }
                        else
                        {
                            tempRowNo += orgUnitCount;
                        }
                    }

                    // System.out.println( ",Temp Row no is : " + tempRowNo +
                    // ", Temp Col No is : " + tempColNo + ", Data Value is : "
                    // + tempStr );
                    WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                    CellFormat cellFormat = cell.getCellFormat();
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    // System.out.println( ",Temp Row no is : " + tempRowNo +
                    // ", Temp Col No is : " + tempColNo );

                    if ( cell.getType() == CellType.LABEL )
                    {
                        Label l = (Label) cell;
                        l.setString( tempStr );
                        l.setCellFormat( cellFormat );
                    }
                    else
                    {
                        try
                        {
                            sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ),
                                wCellformat ) );
                        }
                        catch ( Exception e )
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }
                }

                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end

        // for Line Listing Death DataElements

        int tempLLDeathRowNo = 0;
        int flag = 0;
        if ( llrecordNos.size() == 0 )
            flag = 1;
        Iterator<Integer> itlldeath = llrecordNos.iterator();
        int recordCount = 0;
        int currentRowNo = 0;
        while ( itlldeath.hasNext() )
        {
            Integer recordNo = -1;
            if ( flag == 0 )
            {
                recordNo = (Integer) itlldeath.next();
            }
            flag = 0;

            // Iterator<String> it1 = deCodesList.iterator();
            Iterator<Report_inDesign> reportDesignIterator = reportDesignListLLDeath.iterator();
            int count1 = 0;
            while ( reportDesignIterator.hasNext() )
            {
                // String deCodeString = (String) it1.next();

                // String deType = (String) deCodeType.get( count1 );
                // String sType = (String) serviceType.get( count1 );
                // int count = 0;
                // double sum = 0.0;
                // int flag1 = 0;
                // String tempStr = "";

                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String deType = report_inDesign.getPtype();
                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";
                String tempLLDeathValuStr = "";
                String tempStr1 = "";
                String tempStr2 = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                // List<Calendar> calendarList = new ArrayList<Calendar>(
                // getStartingEndingPeriods( deType ) );
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType,
                    selectedPeriod ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                    tempLLDeathValuStr = " ";
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "lldeathdataelement" ) )
                    {
                        tempStr = getLLDataValue( deCodeString, selectedPeriod, currentOrgUnit, recordNo );
                    }

                    else if ( sType.equalsIgnoreCase( "lldeathdataelementage" ) )
                    {
                        tempLLDeathValuStr = getLLDataValue( deCodeString, selectedPeriod, currentOrgUnit, recordNo );
                    }
                    else
                    {
                        // tempStr = reportService.getResultIndicatorValue(
                        // deCodeString, tempStartDate.getTime(),
                        // tempEndDate.getTime(), currentOrgUnit );
                        // System.out.println( tempStr );
                    }
                }
                tempLLDeathRowNo = report_inDesign.getRowno();
                int tempRowNo = report_inDesign.getRowno();
                // int tempRowNo = 136;
                currentRowNo = tempLLDeathRowNo;
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                // System.out.println( ",Temp Row no is : " + tempRowNo );
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                if ( tempStr == null || tempStr.equals( " " ) )
                {

                }
                else
                {

                    // System.out.println( deCodeString + " : " + tempStr );
                    // System.out.println( deCodeString + " : " + tempStr );

                    /*
                     * String tstr = resMapForDeath.get( tempStr.trim() );
                     * 
                     * if ( tstr != null ) { String temp1 = tstr.split( ":"
                     * )[0].trim(); String temp2 = tstr.split( ":" )[1].trim();
                     * 
                     * tempStr1 = temp1; tempStr = temp2;
                     * 
                     * }
                     */
                    String tstr1 = resMap.get( tempStr.trim() );
                    if ( tstr1 != null )
                        tempStr = tstr1;

                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-DATAELEMENT" )
                        || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                        {

                        }
                        else if ( sType.equalsIgnoreCase( "dataelementnorepeat" ) )
                        {

                        }
                        else
                        {

                            tempLLDeathRowNo += recordCount;
                            currentRowNo += recordCount;
                            tempRowNo += recordCount;
                        }

                        // WritableCell cell = sheet0.getWritableCell(
                        // tempColNo, tempRowNo );

                        // CellFormat cellFormat = cell.getCellFormat();

                        WritableCellFormat wCellformat = new WritableCellFormat();

                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
                        /*
                         * if ( cell.getType() == CellType.LABEL ) { Label l =
                         * (Label) cell; l.setString( tempStr );
                         * l.setCellFormat( cellFormat ); }
                         *//*
                            * if ( cellFormat != null ) { try { sheet0.addCell(
                            * new Number( tempColNo, tempRowNo,
                            * Integer.parseInt( tempStr ), cellFormat ) ); }
                            * catch( Exception e ) { sheet0.addCell( new Label(
                            * tempColNo, tempRowNo, tempStr, cellFormat ) ); }
                            * 
                            * //System.out.println( "In Pre-Formatted: " +
                            * tempStr ); }
                            */
                        // else
                        // {
                        if ( sType.equalsIgnoreCase( "lldeathdataelementage" ) )
                        {
                            // System.out.println(
                            // "In Side lldeathdataelementage" );
                            String tstr = resMapForDeath.get( tempLLDeathValuStr.trim() );

                            // System.out.println( ",LL Death Map is : " + tstr
                            // + ", temp str is : " + tempLLDeathValuStr.trim()
                            // );
                            if ( tstr != null )
                            {
                                tempStr1 = tstr.split( ":" )[0].trim();
                                tempStr2 = tstr.split( ":" )[1].trim();
                                // System.out.println( "tempStr1 is : " +
                                // tempStr1 + ", tempStr2 is : " + tempStr2 );
                            }
                            try
                            {
                                sheet0.addCell( new Label( tempColNo - 1, tempRowNo, tempStr1, getCellFormat1() ) );
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr2, getCellFormat1() ) );
                            }
                            catch ( Exception e )
                            {
                                // System.out.println(
                                // "In Side lldeathdataelementage Exception" );
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr1, getCellFormat1() ) );
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "lldeathdataelement" ) )
                        {
                            try
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, Integer.parseInt( tempStr ),
                                    getCellFormat1() ) );
                            }
                            catch ( Exception e )
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                            }
                        }

                        // System.out.println( "In Cur-Formatted: " + tempStr );
                        // }
                    }

                    // }
                }
                count1++;
            }// inner while loop end
            recordCount++;
            // System.out.println("End Row no for ll Death Death is  : " +
            // recordCount );
        }// outer while loop end

        // int tempRowNollm = currentRowNo+1;
        // System.out.println("Temp Row No is   : " + tempLLDeathRowNo );
        // int tempRowNollm = recordCount + tempLLDeathRowNo;

        // System.out.println("Start Row no for ll maternal Death is  : " +
        // tempRowNollm + ",Record count is : " + recordCount );

        // Line Listing Matarnal Death DataElements

        List<Integer> llMaternalDeathrecordNos = new ArrayList<Integer>();
        //llMaternalDeathrecordNos = getLinelistingMateralanRecordNos( currentOrgUnit, selectedPeriod, deCodesXMLFileName );
        
        llMaternalDeathrecordNos = getLinelistingMateralanRecordNos( currentOrgUnit, selectedPeriod );
        System.out.println( "Line Listing Maternal Death Record Count is :" + llMaternalDeathrecordNos.size() );

        // int testRowNo = 0;

        int flagmdeath = 0;
        if ( llMaternalDeathrecordNos.size() == 0 )
            flagmdeath = 1;
        Iterator<Integer> itllmaternaldeath = llMaternalDeathrecordNos.iterator();
        int maternalDeathRecordCount = 0;
        while ( itllmaternaldeath.hasNext() )
        {
            Integer maternalDeathRecordNo = -1;
            if ( flagmdeath == 0 )
            {
                maternalDeathRecordNo = (Integer) itllmaternaldeath.next();
            }
            flagmdeath = 0;

            // Iterator<String> it1 = deCodesList.iterator();
            Iterator<Report_inDesign> reportDesignIterator = reportDesignListLLMaternalDeath.iterator();
            int count1 = 0;
            while ( reportDesignIterator.hasNext() )
            {
                // String deCodeString = (String) it1.next();

                // String deType = (String) deCodeType.get( count1 );
                // String sType = (String) serviceType.get( count1 );
                // int count = 0;
                // double sum = 0.0;
                // int flag1 = 0;
                // String tempStr = "";

                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String deType = report_inDesign.getPtype();
                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";
                // String tempStr1 = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                // List<Calendar> calendarList = new ArrayList<Calendar>(
                // getStartingEndingPeriods( deType ) );
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType,
                    selectedPeriod ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else if ( deCodeString.equalsIgnoreCase( "F" ) )
                {
                    tempStr = "Female";
                }
                else if ( deCodeString.equalsIgnoreCase( "Y" ) )
                {
                    tempStr = "Years";
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "llmaternaldeathdataelement" ) )
                    {
                        tempStr = getLLDataValue( deCodeString, selectedPeriod, currentOrgUnit, maternalDeathRecordNo );
                    }
                    /*
                     * else { tempStr = reportService.getResultIndicatorValue(
                     * deCodeString, tempStartDate.getTime(),
                     * tempEndDate.getTime(), currentOrgUnit );
                     * //System.out.println( tempStr ); }
                     */
                }
                // testRowNo = report_inDesign.getRowno() + recordCount ;
                int tempRowNo1 = report_inDesign.getRowno();
                // int tempRowNo1 = 136;
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                if ( tempStr == null || tempStr.equals( " " ) )
                {

                }
                else
                {
                    String tstr1 = resMap.get( tempStr.trim() );
                    if ( tstr1 != null )
                        tempStr = tstr1;
                    // System.out.println( deCodeString + " : " + tempStr );
                    /*
                     * String tstr = resMapForDeath.get( tempStr.trim() ); if (
                     * tstr != null ) { String temp1 = tstr.split( ":"
                     * )[0].trim(); String temp2 = tstr.split( ":" )[1].trim();
                     * 
                     * tempStr = temp1; tempStr1 = temp2;
                     * 
                     * }
                     */

                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-DATAELEMENT" )
                        || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                        {

                        }
                        else if ( sType.equalsIgnoreCase( "dataelementnorepeat" ) )
                        {

                        }

                        else
                        {

                            tempRowNo1 += maternalDeathRecordCount + recordCount;
                        }

                        // WritableCell cell = sheet0.getWritableCell(
                        // tempColNo, tempRowNo1 );

                        // CellFormat cellFormat = cell.getCellFormat();

                        WritableCellFormat wCellformat = new WritableCellFormat();

                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

                        /*
                         * if ( cell.getType() == CellType.LABEL ) { Label l =
                         * (Label) cell; l.setString( tempStr );
                         * l.setCellFormat( cellFormat ); }
                         */
                        /*
                         * if ( cellFormat != null ) { try { sheet0.addCell( new
                         * Number( tempColNo, tempRowNo, Integer.parseInt(
                         * tempStr ), cellFormat ) ); } catch( Exception e ) {
                         * sheet0.addCell( new Label( tempColNo, tempRowNo,
                         * tempStr, cellFormat ) ); }
                         * 
                         * //System.out.println( "In Pre-Formatted: " + tempStr
                         * ); }
                         */
                        // else
                        // {
                        if ( sType.equalsIgnoreCase( "llmaternaldeathdataelement" ) )
                        {
                            try
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo1, Integer.parseInt( tempStr ),
                                    getCellFormat1() ) );
                            }
                            catch ( Exception e )
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo1, tempStr, getCellFormat1() ) );
                            }
                        }
                        // }
                    }

                    // }
                }
                count1++;
            }// inner while loop end
            maternalDeathRecordCount++;
        }// outer while loop end
        
        int noOfRecords = llrecordNos.size();

        System.out.println( "Current org unit id : " + currentOrgUnit.getId() + ": Selected Period is : "
            + selectedPeriod.getId() + ": No of Record :" + noOfRecords + ": Report List : " + reportList );

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + currentOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

        try
        {

        }
        finally
        {
            if ( con != null )
                con.close();
        }

        statementManager.destroy();
        System.out.println( "Report Generation End Time is : \t" + new Date() );

        return SUCCESS;
    }

    public void initializeResultMap()
    {
        resMap = new HashMap<String, String>();

        resMap.put( "NONE", "---" );
        resMap.put( "M", "Male" );
        resMap.put( "F", "Female" );
        resMap.put( "Y", "YES" );
        resMap.put( "N", "NO" );
        resMap.put( "NK", "A14-Causes not known" );
        resMap.put( "B1DAY", "C01-WITHIN 24 HOURS OF BIRTH" );
        resMap.put( "B1WEEK", "1 DAY - 1 WEEK" );
        resMap.put( "B1MONTH", "1 WEEK - 1 MONTH" );
        resMap.put( "B1YEAR", "1 MONTH - 1 YEAR" );
        resMap.put( "B5YEAR", "1 YEAR - 5 YEARS" );
        resMap.put( "O5YEAR", "6 YEARS - 14 YEARS" );

        resMap.put( "O15YEAR", "15 YEARS - 55 YEARS" );
        resMap.put( "O55YEAR", "OVER 55 YEARS" );

        resMap.put( "ASPHYXIA", "C03-ASPHYXIA" );
        resMap.put( "SEPSIS", "C02-SEPSIS" );
        resMap.put( "LOWBIRTHWEIGH", "C04-Low Birth Wight(LBW) for Children upto 4 weeks of age only" );
        resMap.put( "IMMREAC", "Immunization reactions" );
        resMap.put( "PNEUMONIA", "C05-Pneumonia" );
        resMap.put( "DIADIS", "C06-Diarrhoea" );
        resMap.put( "MEASLES", "C08-Measles" );
        resMap.put( "TUBER", "A02-Tuberculosis" );
        resMap.put( "MALARIA", "A04-Malaria" );
        resMap.put( "HIVAIDS", "A06-HIV/AIDS" );
        resMap.put( "OFR", "A05-Other Fever related" );
        resMap.put( "PRD", "Pregnancy Related Death( maternal mortality)" );
        resMap.put( "SRD", "Sterilisation related deaths" );
        resMap.put( "AI", "Accidents or Injuries" );
        resMap.put( "SUICIDES", "A10-Suicides" );
        resMap.put( "ABS", "A11-Animal Bites or Stings" );
        resMap.put( "RID", "A03-Respiratory disease including infections(other than TB)" );
        resMap.put( "HDH", "A07-Heart disease/Hypertension related" );
        resMap.put( "SND", "A08-Neurological disease including Strokes" );
        resMap.put( "OKAD", "A12-Known Acute Disease" );
        resMap.put( "OKCD", "A13-Known Chronic Disease" );
        resMap.put( "OTHERS", "C09-Others" );
        resMap.put( "FTP", "FIRST TRIMESTER PREGNANCY" );
        resMap.put( "STP", "SECOND TRIMESTER PREGNANCY" );
        resMap.put( "TTP", "THIRD TRIMESTER PREGNANCY" );
        resMap.put( "DELIVERY", "DELIVERY" );
        resMap.put( "ADW42D", "AFTER DELIVERY WITHIN 42 DAYS" );
        resMap.put( "HOME", "HOME" );
        resMap.put( "SC", "SUBCENTER" );
        resMap.put( "PHC", "PHC" );
        resMap.put( "CHC", "CHC" );
        resMap.put( "MC", "MEDICAL COLLEGE" );
        resMap.put( "UNTRAINED", "UNTRAINED" );
        resMap.put( "TRAINED", "TRAINED" );
        resMap.put( "ANM", "ANM" );
        resMap.put( "NURSE", "NURSE" );
        resMap.put( "DOCTOR", "DOCTOR" );
        resMap.put( "ABORTION", "M01-Abortion" );
        resMap.put( "OPL", "M02-Obstructed/Prolonged labour" );
        resMap.put( "FITS", "M03-Severe hypertension/fits" );
        resMap.put( "SH", "M03-Severe hypertension/fits" );
        resMap.put( "BBCD", "M04-Bleeding" );
        resMap.put( "BACD", "M04-Bleeding" );
        resMap.put( "HFBD", "M05-High fever" );
        resMap.put( "HFAD", "M05-High fever" );
        resMap.put( "MDNK", "M06-Other Causes (including cause not known)" );
        /*resMap.put( "FITS", "FITS" );
        resMap.put( "SH", "SEVERE HYPERTENSION" );
        resMap.put( "BBCD", "BLEEDING BEFORE CHILD DELIVERY" );
        resMap.put( "BACD", "BLEEDING AFTER CHILD DELIVERY" );
        resMap.put( "HFBD", "HIGH FEVER BEFORE DELIVERY" );
        resMap.put( "HFAD", "HIGH FEVER AFTER DELIVERY" );
        */
        
    }

    public void initializeLLDeathResultMap()
    {
        resMapForDeath = new HashMap<String, String>();

        resMapForDeath.put( "B1DAY", "Hrs:12" );
        resMapForDeath.put( "B1WEEK", "Weeks:1" );
        resMapForDeath.put( "B1MONTH", "Weeks:3" );
        resMapForDeath.put( "B1YEAR", "Months:6" );
        resMapForDeath.put( "B5YEAR", "Years:3" );
        resMapForDeath.put( "O5YEAR", "Years:10" );
        resMapForDeath.put( "O15YEAR", "Years:40" );
        resMapForDeath.put( "O55YEAR", "Years:60" );
    }

    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public String getLLDataValue( String formula, Period period, OrganisationUnit organisationUnit, Integer recordNo )
    {
        Statement st1 = null;
        ResultSet rs1 = null;
        // System.out.println( "Inside LL Data Value Method" );
        String query = "";
        try
        {

            // int deFlag1 = 0;
            // int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService
                    .getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }

                // DataValue dataValue = dataValueService.getDataValue(
                // organisationUnit, dataElement, period,
                // optionCombo );
                // st1 = con.createStatement();

                // System.out.println(
                // "Before getting value : OrganisationUnit Name : " +
                // organisationUnit.getName() + ", Period is : " +
                // period.getId() + ", DataElement Name : " +
                // dataElement.getName() + ", Record No: " + recordNo );

                query = "SELECT value FROM lldatavalue WHERE sourceid = " + organisationUnit.getId()
                    + " AND periodid = " + period.getId() + " AND dataelementid = " + dataElement.getId()
                    + " AND recordno = " + recordNo;
                // rs1 = st1.executeQuery( query );

                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

                String tempStr = "";

                if ( sqlResultSet.next() )
                {
                    tempStr = sqlResultSet.getString( 1 );
                }

                replaceString = tempStr;

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            /*
             * if ( deFlag1 == 0 ) { double d = 0.0; try { d =
             * MathUtils.calculateExpression( buffer.toString() ); } catch (
             * Exception e ) { d = 0.0; } if ( d == -1 ) d = 0.0; else { d =
             * Math.round( d Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
             * resultValue = "" + (int) d; }
             * 
             * if ( deFlag2 == 0 ) { resultValue = " "; }
             * 
             * } else { resultValue = buffer.toString(); }
             */

            resultValue = buffer.toString();

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st1 != null )
                    st1.close();

                if ( rs1 != null )
                    rs1.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end
    }
/*
    public List<Integer> getLinelistingMateralanRecordNos( OrganisationUnit organisationUnit, Period period,
        String lltype )
    {
        List<Integer> recordNosList = new ArrayList<Integer>();

        String query = "";

        int dataElementid = 1032;

        if ( lltype.equalsIgnoreCase( "monthly_SCWebPortalDECodes.xml" ) )
        {
            dataElementid = 1032;
        }

        try
        {
            query = "SELECT recordno FROM lldatavalue WHERE dataelementid = " + dataElementid + " AND periodid = "
                + period.getId() + " AND sourceid = " + organisationUnit.getId();

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );

            while ( rs1.next() )
            {
                recordNosList.add( rs1.getInt( 1 ) );
            }

            Collections.sort( recordNosList );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }

        return recordNosList;
    }
*/
 
    public List<Integer> getLinelistingMateralanRecordNos( OrganisationUnit organisationUnit, Period period )
    {
        List<Integer> recordNosList = new ArrayList<Integer>();

        String query = "";

        int dataElementid = 1032;

        try
        {
            query = "SELECT recordno FROM lldatavalue WHERE dataelementid = " + dataElementid + " AND periodid = "
                + period.getId() + " AND sourceid = " + organisationUnit.getId();

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );

            while ( rs1.next() )
            {
                recordNosList.add( rs1.getInt( 1 ) );
            }

            Collections.sort( recordNosList );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }

        return recordNosList;
    }
    
 
    public List<Integer> getLinelistingDeathRecordNos( OrganisationUnit organisationUnit, Period period )
    {
        List<Integer> recordNosList = new ArrayList<Integer>();
        
        int  dataElementid = 1027;
        String query = "";

        try
        {
            query = "SELECT recordno FROM lldatavalue WHERE dataelementid = " + dataElementid + " AND periodid = "
                + period.getId() + " AND sourceid = " + organisationUnit.getId();

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );

            while ( rs1.next() )
            {
                recordNosList.add( rs1.getInt( 1 ) );
            }

            Collections.sort( recordNosList );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }

        return recordNosList;
    }   
 /*   
    public List<Integer> getLineListingIDSPLabTestRecordNos( OrganisationUnit organisationUnit, Period period )
    {
        List<Integer> recordNosList = new ArrayList<Integer>();

        String query = "";

        int dataElementid = 1053;

        try
        {
            query = "SELECT recordno FROM lldatavalue WHERE dataelementid = " + dataElementid + " AND periodid = "
                + period.getId() + " AND sourceid = " + organisationUnit.getId();

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );

            while ( rs1.next() )
            {
                recordNosList.add( rs1.getInt( 1 ) );
            }

            Collections.sort( recordNosList );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }

        return recordNosList;
    }   
  */  
}
