package org.hisp.dhis.reports.feedback.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import com.opensymphony.xwork2.Action;

public class GenerateFeedbackReportAnalyserResultAction
    implements Action
{
    
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

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
/*
    private String aggCB;

    public void setAggCB( String aggCB )
    {
        this.aggCB = aggCB;
    }
*/
    private List<OrganisationUnit> orgUnitList;

    private Period selectedPeriod;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat monthFormat;

    private SimpleDateFormat simpleMonthFormat;

    private SimpleDateFormat yearFormat;

    private SimpleDateFormat simpleYearFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private Date sDateTemp;

    private Date eDateTemp;

    private PeriodType periodType;

    private String raFolderName;

    private List<OrganisationUnit> childOrgUnits;
    
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        // Initialization
        raFolderName = reportService.getRAFolderName();

        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleYearFormat = new SimpleDateFormat( "yy" );
        
        //getting Reports Details
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();

        String parentUnit = "";

        OrganisationUnit selOrgUnit = null;
        
        if( reportModelTB.equalsIgnoreCase( "STATIC" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( selOrgUnit );
        }
        else if( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-PARENT" ) )
        {
            selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.add( selOrgUnit );
        }
        else if( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-SIBLINGS" ) )
        {
            selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.addAll( selOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            orgUnitList.add( 0, selOrgUnit );
        }
        else if( reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" ) )
        {
            selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.addAll( selOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            orgUnitList.add( 0, selOrgUnit );
        }

        System.out.println( selOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );

        
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        int orgUnitCount = 0;

        int rowCounter = 0;

        // ---------------------------------------------------------------------------------------------------
        // Feedback without orgunit START
        // This part is for generating feedback reports for orgunits without any
        // children
        // ---------------------------------------------------------------------------------------------------

        OrganisationUnit checkChildOrgunit = new OrganisationUnit();

        checkChildOrgunit = organisationUnitService.getOrganisationUnit( ouIDTB );

        childOrgUnits = new ArrayList<OrganisationUnit>();

        childOrgUnits.addAll( checkChildOrgunit.getChildren() );

        int children = 1;

        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" )
            && (childOrgUnits == null || childOrgUnits.size() == 0) )
        {
            children = 0;
        }

        if ( children == 0 )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();
            int count1 = 0;

            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String deCodeString = report_inDesign.getExpression();
                String deType = report_inDesign.getPtype();
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
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

                if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = "";
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getName();
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getName();
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getName();
                }
                else if( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                {

                    sDateTemp = sDate;

                    eDateTemp = eDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    String startYear = "";

                    String endYear = "";

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    tempQuarterYear.setTime( sDateTemp );

                    if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" )) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, -1 );

                        sDateTemp = tempQuarterYear.getTime();

                    }

                    startYear = yearFormat.format( sDateTemp );

                    tempQuarterYear.setTime( eDateTemp );

                    if ( !(startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" )) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        eDateTemp = tempQuarterYear.getTime();

                    }
                    endYear = yearFormat.format( eDateTemp );

                    tempStr = startYear + " - " + endYear;

                }

                else
                {
                    tempStr = "";
                }

                WritableCellFormat wCellformat = new WritableCellFormat();
                wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                wCellformat.setWrap( true );
                wCellformat.setAlignment( Alignment.CENTRE );

                /*
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 ) + orgUnitGroupCount;
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                */
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }

                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );

                count1++;
            }

        }

        // ---------------------------------------------------------------------
        // Feedback without orgunit END
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // All other reports START
        // ---------------------------------------------------------------------

        while ( it.hasNext() && children != 0 )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            int count1 = 0;

            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() && children != 0 )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String deType = report_inDesign.getPtype();
                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
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
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) )
                {
                    tempStr = String.valueOf( tempStartDate.get( Calendar.WEEK_OF_MONTH ) );
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Quarter I";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Quarter II";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Quarter III";
                    }
                    else
                    {
                        tempStr = "Quarter IV";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Q1";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Q2";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Q3";
                    }
                    else
                    {
                        tempStr = "Q4";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Apr - Jun";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Jul - Sep";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Oct - Dec";
                    }
                    else
                    {
                        tempStr = "Jan - Mar";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "April - June";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "July - September";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "October - December";
                    }
                    else
                    {
                        tempStr = "January - March";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Apr";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Jul";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Oct";
                    }
                    else
                    {
                        tempStr = "Jan";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-START" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "April";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "July";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "October";
                    }
                    else
                    {
                        tempStr = "January";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) )
                {
                    String endMonth = "";

                    endMonth = monthFormat.format( eDate );

                    if ( endMonth.equalsIgnoreCase( "June" ) )
                    {
                        tempStr = "Jun";
                    }
                    else if ( endMonth.equalsIgnoreCase( "September" ) )
                    {
                        tempStr = "Sep";
                    }
                    else if ( endMonth.equalsIgnoreCase( "December" ) )
                    {
                        tempStr = "Dec";
                    }
                    else
                    {
                        tempStr = "Mar";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-END" ) )
                {
                    String endMonth = "";

                    endMonth = monthFormat.format( eDate );

                    if ( endMonth.equalsIgnoreCase( "June" ) )
                    {
                        tempStr = "June";
                    }
                    else if ( endMonth.equalsIgnoreCase( "September" ) )
                    {
                        tempStr = "September";
                    }
                    else if ( endMonth.equalsIgnoreCase( "December" ) )
                    {
                        tempStr = "December";
                    }
                    else
                    {
                        tempStr = "March";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                {
                    sDateTemp = sDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    tempQuarterYear.setTime( sDateTemp );

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }
                    else
                    {
                        if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" ))
                            && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, -1 );

                            sDateTemp = tempQuarterYear.getTime();

                        }
                    }

                    tempStr = yearFormat.format( sDateTemp );
                }
                else if ( deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) )
                {
                    sDateTemp = sDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    tempQuarterYear.setTime( sDateTemp );

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }
                    else
                    {
                        if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" ))
                            && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, -1 );

                            sDateTemp = tempQuarterYear.getTime();

                        }
                    }

                    tempStr = simpleYearFormat.format( sDateTemp );
                }
                else if ( deCodeString.equalsIgnoreCase( "YEAR-END" ) )
                {
                    sDateTemp = sDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    tempQuarterYear.setTime( sDate );

                    sDate = tempQuarterYear.getTime();

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        sDateTemp = tempQuarterYear.getTime();

                    }

                    if ( !(startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" )) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        sDateTemp = tempQuarterYear.getTime();

                    }

                    tempStr = yearFormat.format( sDateTemp );
                }

                else if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                {

                    sDateTemp = sDate;

                    eDateTemp = eDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    String startYear = "";

                    String endYear = "";

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    tempQuarterYear.setTime( sDateTemp );

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }

                    else
                    {
                        if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" )) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, -1 );

                            sDateTemp = tempQuarterYear.getTime();

                        }
                    }

                    startYear = yearFormat.format( sDateTemp );

                    tempQuarterYear.setTime( eDateTemp );

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        eDateTemp = tempQuarterYear.getTime();
                    }

                    if ( !(startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" )) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        eDateTemp = tempQuarterYear.getTime();

                    }
                    endYear = yearFormat.format( eDateTemp );

                    tempStr = startYear + " - " + endYear;

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
                    rowCounter += 1;

                    if( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            //tempStr = getIndividualResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                            tempStr = reportService.getIndividualResultDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            //tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                            tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( tempStartDate.getTime(), tempEndDate.getTime() ) );
                            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                            tempStr = reportService.getResultDataValueFromAggregateTable( deCodeString, periodIds, currentOrgUnit, reportModelTB );
                        }
                    }
                    else if ( sType.equalsIgnoreCase( "indicator-parent" ) )
                    {
                        if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            //tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                            tempStr = reportService.getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            //tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            //tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                        }
                    }
                    else if ( sType.equalsIgnoreCase( "survey" ) )
                    {
                        //tempStr = getResultSurveyValue( deCodeString, currentOrgUnit );
                        tempStr = reportService.getResultSurveyValue( deCodeString, currentOrgUnit );
                    }
                    else if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                    {
                        if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            //tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            //tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        }
                    }
                    else
                    {
                        if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            //tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                            tempStr = reportService.getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            //tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            //tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                        }
                    }
                }
                
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
                /*
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 ) + orgUnitGroupCount;
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                */

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
                    if ( reportModelTB.equalsIgnoreCase( "STATIC" ) || reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-PARENT" ) )
                    {
                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( tempStr );
                            l.setCellFormat( cellFormat );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-SIBLINGS" )
                        || reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" ) )
                    {

                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) 
                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
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

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );
                        
                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        
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
                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                            }
                            catch( Exception e )
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                    }
                }
                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnitList.get( 0 ).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }

}
