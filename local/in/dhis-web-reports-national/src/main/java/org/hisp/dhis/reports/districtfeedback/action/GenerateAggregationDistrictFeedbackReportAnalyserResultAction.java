package org.hisp.dhis.reports.districtfeedback.action;

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
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

public class GenerateAggregationDistrictFeedbackReportAnalyserResultAction
    implements Action
{

    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportOrgSpecificDataService reportOrgSpecificDataService;

    public void setReportOrgSpecificDataService( ReportOrgSpecificDataService reportOrgSpecificDataService )
    {
        this.reportOrgSpecificDataService = reportOrgSpecificDataService;
    }

    private StateDistrictFeedbackReportService stateDistrictFeedbackReportService;

    public void setStateDistrictFeedbackReportService(
        StateDistrictFeedbackReportService stateDistrictFeedbackReportService )
    {
        this.stateDistrictFeedbackReportService = stateDistrictFeedbackReportService;
    }

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

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

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

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private String aggData;

    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    private Period selectedPeriod;

    private String reportModelTB;

    private List<OrganisationUnit> orgUnitList;

    private Date sDate;

    private Date eDate;

    private Date sDateTemp;

    private Date eDateTemp;

    private PeriodType periodType;

    private String raFolderName;

    private Integer monthCount;

    private Date sAggDate;

    private String periodTypeId;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        statementManager.initialise();

        // Initialization

        raFolderName = reportService.getRAFolderName();
        String ReportXMLFileName = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        SimpleDateFormat dayFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat monthFormat = new SimpleDateFormat( "MMMM" );
        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat( "MMM" );
        SimpleDateFormat yearFormat = new SimpleDateFormat( "yyyy" );
        SimpleDateFormat simpleYearFormat = new SimpleDateFormat( "yy" );

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        ReportXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        periodTypeId = "Monthly";

        String parentUnit = "";

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;

        File newDir = new File( outputReportPath );

        if ( !newDir.exists() )
        {
            newDir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
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

        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        System.out.println( orgUnitList.get( 0 ).getName() + " : " + selReportObj.getName()
            + " : Report Generation Start Time is : " + new Date() );

        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        Calendar cal = Calendar.getInstance();
        cal.setTime( sDate );
        int month = cal.get( Calendar.MONTH );

        if ( month < 4 )
        {
            cal.roll( Calendar.YEAR, -1 );
        }

        cal.set( Calendar.MONTH, 3 );

        sAggDate = cal.getTime();

        periodType = periodService.getPeriodTypeByName( periodTypeId );

        List<Period> periodList = new ArrayList<Period>( periodService
            .getPeriodsBetweenDates( periodType, sDate, eDate ) );

        System.out.println( "* Period List Size: [" + periodList.size() + "]" );

        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );

        String periodIdsByComma = getCommaDelimitedString( periodIds );

        List<Period> aggPeriodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sAggDate,
            eDate ) );

        System.out.println( "* Aggregate Period List Size: [" + aggPeriodList.size() + "]" );

        for ( Period period : aggPeriodList )
        {
            System.out.println( "* Agg-Month [ " + period.getStartDateString() + " ]" );
        }

        Collection<Integer> aggPeriodIds = new ArrayList<Integer>( getIdentifiers( Period.class, aggPeriodList ) );

        String aggPeriodIdsByComma = getCommaDelimitedString( aggPeriodIds );

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();
        tempStartDate.setTime( sDate );
        tempEndDate.setTime( eDate );

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

        Map<String, String> feedbackTemplateMap;

        Map<String, String> orgSpecificMap;

        feedbackTemplateMap = stateDistrictFeedbackReportService.getDistrictFeedbackData( orgUnit, sDate, eDate,
            ReportXMLFileName );

        orgSpecificMap = reportOrgSpecificDataService.getOrgSpecificData( orgUnit, sDate, eDate, sAggDate, periodType,
            ReportXMLFileName );

        WritableSheet writableSheet = outputReportWorkbook.getSheet( 0 );

        // --------------------------------------------------- Write Data
        // Quality Entries----------------------------------------

        for ( Map.Entry<String, String> element : feedbackTemplateMap.entrySet() )
        {
            String cellPosString[] = element.getKey().split( ":" );

            int tempColNo = Integer.parseInt( cellPosString[0].trim() );

            int tempRowNo = Integer.parseInt( cellPosString[1].trim() );

            String tempStr = element.getValue();

            WritableCell cell = writableSheet.getWritableCell( tempColNo, tempRowNo );

            CellFormat cellFormat = cell.getCellFormat();
            WritableCellFormat writableCellFormat = new WritableCellFormat();
            writableCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
            writableCellFormat.setWrap( true );
            writableCellFormat.setAlignment( Alignment.CENTRE );

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
                    writableSheet.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ),
                        writableCellFormat ) );
                }
                catch ( Exception e )
                {
                    writableSheet.addCell( new Label( tempColNo, tempRowNo, tempStr, writableCellFormat ) );
                }
            }
        }

        // --------------------------------------------------- Write
        // OrgUnit-Specific Entries----------------------------------------

        for ( Map.Entry<String, String> element : orgSpecificMap.entrySet() )
        {
            String cellPosString[] = element.getKey().split( ":" );

            int tempColNo = Integer.parseInt( cellPosString[0].trim() );

            int tempRowNo = Integer.parseInt( cellPosString[1].trim() );

            String tempStr = element.getValue();

            WritableCell cell = writableSheet.getWritableCell( tempColNo, tempRowNo );

            CellFormat cellFormat = cell.getCellFormat();
            WritableCellFormat writableCellFormat = new WritableCellFormat();
            writableCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
            writableCellFormat.setWrap( true );
            writableCellFormat.setAlignment( Alignment.CENTRE );

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
                    writableSheet.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ),
                        writableCellFormat ) );
                }
                catch ( Exception e )
                {
                    writableSheet.addCell( new Label( tempColNo, tempRowNo, tempStr, writableCellFormat ) );
                }
            }
        }

        List<Report_inDesign> reportDesignList = reportService.getDistrictFeedbackReportDesign( ReportXMLFileName );
        String dataElementIdsByComma = reportService.getDataelementIds( reportDesignList );

        int orgUnitCount = 0;
        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            // ---------------------------------------------------- Map for
            // aggregate Period Data Data
            // --------------------------------------------------------------------

            Map<String, String> aggDeMap = new HashMap<String, String>();
            if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( currentOrgUnit.getId(),
                    dataElementIdsByComma, aggPeriodIdsByComma ) );
            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService
                    .getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                    childOrgUnitTree ) );
                String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma,
                    dataElementIdsByComma, aggPeriodIdsByComma ) );
            }
            else if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( "" + currentOrgUnit.getId(),
                    dataElementIdsByComma, aggPeriodIdsByComma ) );
            }

            // ---------------------------------------------------- Map for only
            // current month Data
            // --------------------------------------------------------------------

            Map<String, String> DeMap = new HashMap<String, String>();
            if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                DeMap.putAll( reportService.getResultDataValueFromAggregateTable( currentOrgUnit.getId(),
                    dataElementIdsByComma, periodIdsByComma ) );
            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService
                    .getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                    childOrgUnitTree ) );
                String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

                DeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElementIdsByComma,
                    periodIdsByComma ) );
            }
            else if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                DeMap.putAll( reportService.getAggDataFromDataValueTable( "" + currentOrgUnit.getId(),
                    dataElementIdsByComma, periodIdsByComma ) );
            }

            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String sType = report_inDesign.getStype();
                String pType = report_inDesign.getPtype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTHCOUNT" ) )
                {
                    int endYear = tempEndDate.get( Calendar.YEAR );
                    int startYear = tempStartDate.get( Calendar.YEAR );
                    int endMonth = tempEndDate.get( Calendar.MONTH );
                    int startMonth = tempStartDate.get( Calendar.MONTH );

                    monthCount = ((endYear - startYear) * 12) - startMonth + endMonth + 1;
                    tempStr = monthCount.toString();
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
                else if ( deCodeString.equalsIgnoreCase( "PERIODSDED" ) )
                {
                    tempStr = simpleDateFormat.format( sDate ) + " To " + simpleDateFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "DATE-START-END" ) )
                {
                    tempStr = dayFormat.format( sDate ) + " To " + dayFormat.format( eDate );
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
                    String startMonth = monthFormat.format( sDate );

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
                    String startMonth = monthFormat.format( sDate );

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
                    String startMonth = monthFormat.format( sDate );

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
                    String startMonth = monthFormat.format( sDate );

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
                    String startMonth = monthFormat.format( sDate );

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
                    String startMonth = monthFormat.format( sDate );

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
                    String endMonth = monthFormat.format( eDate );

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
                    String endMonth = monthFormat.format( eDate );

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

                    String startMonth = monthFormat.format( sDateTemp );

                    periodType = periodService.getPeriodTypeByName( periodTypeId );

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }
                    else if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" ))
                        && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, -1 );
                        sDateTemp = tempQuarterYear.getTime();
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

                    periodType = periodService.getPeriodTypeByName( periodTypeId );

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }
                    else if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" ))
                        && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, -1 );
                        sDateTemp = tempQuarterYear.getTime();
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

                    periodType = periodService.getPeriodTypeByName( periodTypeId );

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

                    String startMonth = monthFormat.format( sDateTemp );

                    System.out.println( "The value for periodType is " + periodTypeId );

                    periodType = periodService.getPeriodTypeByName( periodTypeId );

                    tempQuarterYear.setTime( sDateTemp );

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }
                    else if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" )) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, -1 );

                        sDateTemp = tempQuarterYear.getTime();
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
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if ( pType.equalsIgnoreCase( "CCMCY" ) )
                        {
                            if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                            {
                                tempStr = reportService.getAggVal( deCodeString, aggDeMap );
                            }
                            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                            {
                                tempStr = reportService.getAggVal( deCodeString, aggDeMap );
                            }
                            else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                            {
                                tempStr = reportService.getAggVal( deCodeString, aggDeMap );
                            }
                        }
                        else if ( pType.equalsIgnoreCase( "CMCY" ) )
                        {
                            if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                            {
                                tempStr = reportService.getAggVal( deCodeString, DeMap );
                            }
                            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                            {
                                tempStr = reportService.getAggVal( deCodeString, DeMap );
                            }
                            else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                            {
                                tempStr = reportService.getAggVal( deCodeString, DeMap );
                            }
                        }
                    }
                    else
                    {
                        if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            tempStr = reportService.getIndividualResultIndicatorValue( deCodeString, tempStartDate
                                .getTime(), tempEndDate.getTime(), currentOrgUnit );
                        }
                        else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                tempEndDate.getTime(), currentOrgUnit );
                        }
                        else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                tempEndDate.getTime(), currentOrgUnit );
                        }
                    }
                }

                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();

                if ( tempStr == null || tempStr.trim().equals( "" ) )
                {
                    tempColNo += orgUnitCount;

                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    writableSheet.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
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
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" )
                            || deCodeString.equalsIgnoreCase( "DATE-START-END" ) )
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
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" )
                            || deCodeString.equalsIgnoreCase( "DATE-START-END" ) )
                        {
                        }
                        else
                        {
                            tempRowNo += orgUnitCount;
                        }
                    }

                    WritableCell cell = writableSheet.getWritableCell( tempColNo, tempRowNo );

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
                        try
                        {
                            writableSheet.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ),
                                wCellformat ) );
                        }
                        catch ( Exception e )
                        {
                            writableSheet.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }
                }

            }
            orgUnitCount++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnitList.get( 0 ).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + "_";
        fileName += "_" + simpleDateFormat.format( eDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( orgUnitList.get( 0 ).getName() + " : " + selReportObj.getName()
            + " Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }
}
