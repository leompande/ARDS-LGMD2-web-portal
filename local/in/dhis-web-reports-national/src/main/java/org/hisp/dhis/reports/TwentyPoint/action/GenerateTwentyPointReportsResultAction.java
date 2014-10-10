package org.hisp.dhis.reports.TwentyPoint.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.util.OrganisationUnitCommentComparator;
import org.hisp.dhis.reports.util.ReportService;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @author Brajesh Murari
 * 
 */
public class GenerateTwentyPointReportsResultAction
    implements Action
{
    // private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private static final String NULL_REPLACEMENT = "0";

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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    /*
    private String contentType;

    public String getContentType()
    {
        return contentType;
    }
    */

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }
    */

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    private OrganisationUnit selectedOrgUnit;

    public OrganisationUnit getSelectedOrgUnit()
    {
        return selectedOrgUnit;
    }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
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

    private SimpleDateFormat simpleDateFormat1;

    public SimpleDateFormat getSimpleDateFormat1()
    {
        return simpleDateFormat1;
    }

    private List<String> deCodeType;

    private List<String> serviceType;

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }

    private String reportModelTB;

    public void setReportModelTB( String reportModelTB )
    {
        this.reportModelTB = reportModelTB;
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

    private String prevDate;

    public void setPrevDate( String prevDate )
    {
        this.prevDate = prevDate;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private int periodId;

    public void setPeriodId( int periodId )
    {
        this.periodId = periodId;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date pDate;

    private Date sDate;

    private Date eDate;

    private int tempMonthCount;

    private int tempMonthCount1;

    private List<Integer> ougmemberCountList;

    private String raFolderName;

    private SimpleDateFormat monthDateFormat;

    private SimpleDateFormat yearDateFormat;

    private Connection con = null;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Date sysStartDate = new Date();

        statementManager.initialise();

        raFolderName = reportService.getRAFolderName();

        // Initialization
        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        ougmemberCountList = new ArrayList<Integer>();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        simpleDateFormat1 = new SimpleDateFormat( "MMM-yyyy" );
        deCodesXMLFileName = reportList + "DECodes.xml";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        String parentUnit = "";

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator  + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
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

        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        List<OrganisationUnitGroup> orgUnitGroupList = new ArrayList<OrganisationUnitGroup>();
        // orgUnitGroupList = reportService.getOrgUnitGroupsFromXML();
        if ( reportModelTB.equals( "DYNAMIC-GROUP" ) )
        {
            int tempCount1 = 0;
            orgUnitList = new ArrayList<OrganisationUnit>();
            Iterator<OrganisationUnitGroup> iterator1 = orgUnitGroupList.iterator();
            while ( iterator1.hasNext() )
            {
                OrganisationUnitGroup oug = (OrganisationUnitGroup) iterator1.next();
                List<OrganisationUnit> tempList = new ArrayList<OrganisationUnit>( oug.getMembers() );
                Collections.sort( tempList, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( tempList );
                ougmemberCountList.add( oug.getMembers().size() );
                if ( tempCount1 != 0 && tempCount1 != orgUnitGroupList.size() )
                    outputReportWorkbook.copySheet( 0, oug.getName(), tempCount1 );
                tempCount1++;
            }
        }
        else if ( reportModelTB.equals( "dynamicwithrootfacility" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            Collections.sort( orgUnitList, new OrganisationUnitCommentComparator() );
            orgUnitList.add( selectedOrgUnit );
        }
        else if ( reportModelTB.equals( "dynamicwithoutrootfacility" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            Collections.sort( orgUnitList, new OrganisationUnitCommentComparator() );
        }
        else
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.add( selectedOrgUnit );
        }

        // Period Info1

        selectedPeriod = periodService.getPeriod( availablePeriods );
        getPreviousPeriod( selectedPeriod );

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthDateFormat = new SimpleDateFormat( "MMM" );
        yearDateFormat = new SimpleDateFormat( "yyyy" );

        Calendar periodInfoSDate = Calendar.getInstance();
        Calendar periodInfoEDate = Calendar.getInstance();
        periodInfoSDate.setTime( selectedPeriod.getStartDate() );
        if ( periodInfoSDate.get( Calendar.MONTH ) < Calendar.APRIL )
        {
            periodInfoSDate.roll( Calendar.YEAR, -1 );
        }
        periodInfoSDate.set( Calendar.MONTH, Calendar.APRIL );
        periodInfoEDate.setTime( selectedPeriod.getEndDate() );

        // Period Info
        // pDate = format.parseDate( prevDate );
        Calendar tempPrevEndDate = Calendar.getInstance();
        tempPrevEndDate.setTime( selectedPeriod.getEndDate() );
        tempPrevEndDate.roll( Calendar.YEAR, -1 );
        pDate = tempPrevEndDate.getTime();

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        String levelName = "Level";
        //int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit ) + 1;
        int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit.getId() ) + 1;

        if ( selOULevel <= organisationUnitService.getNumberOfOrganisationalLevels() )
        {
            OrganisationUnitLevel ouL = organisationUnitService.getOrganisationUnitLevel( selOULevel );
            if ( ouL != null )
                levelName = ouL.getName();
            else
                levelName += selOULevel;
        }

        Iterator<OrganisationUnit> orgUnitIterator1 = orgUnitList.iterator();
        int ouc1 = 0;
        String ouGroupName = "";
        List<Integer> ouGroupMemList = new ArrayList<Integer>();

        String facilityGroupNames = "";
        while ( orgUnitIterator1.hasNext() )
        {
            OrganisationUnit orgUnit = (OrganisationUnit) orgUnitIterator1.next();

            if ( ouc1 == 0 )
            {
                ouGroupName = orgUnit.getComment();
                if ( ouGroupName == null )
                    ouGroupName = "NULL";
                facilityGroupNames += ouGroupName;
            }

            if ( !ouGroupName.equalsIgnoreCase( orgUnit.getComment() ) )
            {
                ouGroupMemList.add( ouc1 );

                if ( orgUnit.getComment() != null )
                    facilityGroupNames += "/ " + orgUnit.getComment();
            }

            //System.out.println( orgUnit.getShortName() + " ----- " + ouGroupName + " -------- " + orgUnit.getComment() );
            ouGroupName = orgUnit.getComment();
            if ( ouGroupName == null )
                ouGroupName = "NULL";

            ouc1++;
        }

        Iterator<OrganisationUnit> it = orgUnitList.iterator();

        int orgUnitCount = 0;
        int orgUnitGroupCount = 0;
        int ouGroupCount = 0;
        int ouGroupMemCount = 0;
        int slNo = 0;
        double[][] groupTotal = new double[ouGroupMemList.size() + 1][deCodesList.size()];

        Integer startRow = 10;
        Integer currentRow = 0;
        Integer last = 0;
        Integer rowCounter = 10;
        Integer lastRow = 9 + orgUnitList.size();

        while ( it.hasNext() )
        {
            OrganisationUnit orgUnit = (OrganisationUnit) it.next();

            Iterator<String> it1 = deCodesList.iterator();
            int count1 = 0;

            currentRow = rowCounter;

            while ( it1.hasNext() )
            {
                String deCodeString = (String) it1.next();
                String deType = (String) deCodeType.get( count1 );
                String sType = (String) serviceType.get( count1 );
                String tempStr = "";
                Double tempDouble = 0.0;

                String Formula = " ";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( getStartingEndingPeriods( deType ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = orgUnit.getShortName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    tempStr = selectedOrgUnit.getShortName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" ) )
                {
                    tempStr = facilityGroupNames;
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                    || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    tempStr = simpleDateFormat.format( selectedPeriod.getStartDate() ).toString();
                }
                else if ( deCodeString.equalsIgnoreCase( "PREV-PER" ) )
                {
                    tempStr = simpleDateFormat.format( pDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MPERIOD" ) )
                {
                    tempStr = monthDateFormat.format( selectedPeriod.getStartDate() ).toString();
                }
                else if ( deCodeString.equalsIgnoreCase( "YPERIOD" ) )
                {
                    tempStr = yearDateFormat.format( selectedPeriod.getStartDate() ).toString();
                }
                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + (slNo + 1);
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = "";
                }
                else if ( deCodeString.equalsIgnoreCase( "RANK-ANNUAL" ) )
                {
                    Formula = "RANK(I" + (currentRow + 1) + ",I" + (startRow + 1) + ":I" + (lastRow) + ",0)";
                }
                else if ( deCodeString.equalsIgnoreCase( "PERSENTAGE-PLUS-MINUS" ) )
                {
                    Formula = "ROUND((((G" + (currentRow + 1) + "-K" + (currentRow + 1) + ")/K" + (currentRow + 1)
                        + ")*100),2)";
                }
                else if ( deCodeString.equalsIgnoreCase( "TT-PERCENTAGE" ) )
                {
                    // String tem1 = "ISBLANK(G" + (currentRow+1)+ "),ISBLANK(D"
                    // + (currentRow+1) + ")";
                    // String tem2 = "ROUND((((G" + (currentRow+1) + ")/D" +
                    // (currentRow+1) + ")*100),2)";
                    // Formula = "IF(OR("+ tem1 +"),\" \""+","+ tem2;
                    Formula = "ROUND((((G" + (currentRow + 1) + ")/D" + (currentRow + 1) + ")*100),2)";
                    // Formula = "IFERROR("+ tem2 +","+ "\" \")";
                }
                else if ( deCodeString.equalsIgnoreCase( "TT-PROPORTIONAL-PERCENTAGE" ) )
                {
                    Formula = "ROUND((((G" + (currentRow + 1) + ")/E" + (currentRow + 1) + ")*100),2)";
                }
                else if ( deCodeString.equalsIgnoreCase( "PROPORTIONAL-WORK-LOAD" ) )
                {
                    int years1 = tempEndDate.get( Calendar.YEAR ) - tempStartDate.get( Calendar.YEAR ) - 1;
                    int months1 = (13 - 3) + tempEndDate.get( Calendar.MONTH );

                    if ( tempEndDate.get( Calendar.MONTH ) >= 3 && tempEndDate.get( Calendar.MONTH ) <= 11 )
                    {
                        tempMonthCount1 = months1 + (years1 * 12);
                    }
                    else if ( tempEndDate.get( Calendar.MONTH ) == 0 )
                    {
                        tempMonthCount1 = 10;
                    }
                    else if ( tempEndDate.get( Calendar.MONTH ) == 1 )
                    {
                        tempMonthCount1 = 11;
                    }
                    else
                    {
                        tempMonthCount1 = 12;
                    }

                    Formula = "ROUND(((D" + (currentRow + 1) + "*" + tempMonthCount1 + ")/" + 12 + "),0)";
                }
                else
                {
                    int years = tempEndDate.get( Calendar.YEAR ) - tempStartDate.get( Calendar.YEAR ) - 1;
                    int months = (13 - 3) + tempEndDate.get( Calendar.MONTH );

                    if ( tempEndDate.get( Calendar.MONTH ) >= 3 && tempEndDate.get( Calendar.MONTH ) <= 11 )
                    {
                        tempMonthCount = months + (years * 12);
                    }
                    else if ( tempEndDate.get( Calendar.MONTH ) == 0 )
                    {
                        tempMonthCount = 10;
                    }
                    else if ( tempEndDate.get( Calendar.MONTH ) == 1 )
                    {
                        tempMonthCount = 11;
                    }
                    else
                    {
                        tempMonthCount = 12;
                    }

                    deCodeString = deCodeString.replaceAll( "MONTHCOUNT", "" + tempMonthCount );
                    if ( sType.equalsIgnoreCase( "dataelement" ) || sType.equalsIgnoreCase( "dataelement-percentage" ) )
                    {
                        tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(),
                            orgUnit );

                        try
                        {
                            tempDouble = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempDouble = 0.0;
                        }

                        //System.out.println( "The current DATAVALUE for " + orgUnit.getShortName() + "is: " + tempStr );
                    }
                    else if ( sType.equalsIgnoreCase( "formula" ) )
                    {
                        tempStr = deCodeString;
                    }
                    else
                    {
                        tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                            tempEndDate.getTime(), orgUnit.getParent() );

                        try
                        {
                            tempDouble = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempDouble = 0.0;
                        }

                    }
                }
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 ) + orgUnitGroupCount;
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                /*
                 * if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-GROUP" ) ) { if (
                 * deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) ||
                 * deCodeString.equalsIgnoreCase( "PREV-PER" ) ||
                 * deCodeString.equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" )) { }
                 * else { tempRowNo += slNo; } WritableCell cell =
                 * sheet0.getWritableCell( tempColNo, tempRowNo ); CellFormat
                 * cellFormat = cell.getCellFormat(); WritableCellFormat
                 * wCellformat = new WritableCellFormat(); WritableCellFormat
                 * numberCellFormat = new WritableCellFormat();
                 * 
                 * wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                 * numberCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
                 * numberCellFormat.setAlignment( Alignment.CENTRE );
                 * 
                 * if ( cell.getType() == CellType.LABEL ) { Label l = (Label)
                 * cell; l.setString( tempStr ); l.setCellFormat( cellFormat ); }
                 * else { if ( sType.equalsIgnoreCase( "dataelement" ) ||
                 * sType.equalsIgnoreCase( "dataelement-percentage" ) ) { if (
                 * deCodeString.equalsIgnoreCase( "FACILITY" ) ||
                 * deCodeString.equalsIgnoreCase( "PERIOD" ) ||
                 * deCodeString.equalsIgnoreCase( "PERIOD-NO-REPEAT" ) ||
                 * deCodeString.equalsIgnoreCase( "PREV-PER" ) ||
                 * deCodeString.equalsIgnoreCase( "NA" ) ||
                 * deCodeString.equalsIgnoreCase( "SLNO" ) ) { sheet0.addCell(
                 * new Label( tempColNo, tempRowNo, tempStr, wCellformat ) ); }
                 * else if ( deCodeString.equalsIgnoreCase( "RANK-ANNUAL" ) ||
                 * deCodeString.equalsIgnoreCase( "PROPORTIONAL-WORK-LOAD" ) ||
                 * deCodeString.equalsIgnoreCase( "PERSENTAGE-PLUS-MINUS" )) {
                 * sheet0.addCell( new Formula( tempColNo, tempRowNo, Formula,
                 * numberCellFormat ) ); } else { sheet0.addCell( new Number(
                 * tempColNo, tempRowNo, tempDouble, numberCellFormat ) ); } }
                 * else if ( sType.equalsIgnoreCase( "indicator" ) ) {
                 * sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble,
                 * numberCellFormat ) ); } else { sheet0.addCell( new Label(
                 * tempColNo, tempRowNo, tempStr, wCellformat ) ); } } try {
                 * groupTotal[orgUnitGroupCount][count1] += tempDouble; } catch (
                 * Exception e ) { groupTotal[orgUnitGroupCount][count1] += 0.0; } }
                 */

                if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                {
                    //System.out.println( "In Dynamic withroot Facility : " + tempStr );

                    if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                        || deCodeString.equalsIgnoreCase( "PREV-PER" )
                        || deCodeString.equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" )
                        || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                    {
                    }
                    else
                    {
                        tempRowNo += slNo;
                    }

                    // tempRowNo += orgUnitCount;
                    WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                    CellFormat cellFormat = cell.getCellFormat();
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    WritableCellFormat numberCellFormat = new WritableCellFormat();

                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    numberCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    numberCellFormat.setAlignment( Alignment.CENTRE );

                    if ( cell.getType() == CellType.LABEL )
                    {
                        Label l = (Label) cell;
                        l.setString( tempStr );
                        l.setCellFormat( cellFormat );
                    }
                    else
                    {
                        if ( sType.equalsIgnoreCase( "dataelement" )
                            || sType.equalsIgnoreCase( "dataelement-percentage" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITY" )
                                || deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NO-REPEAT" )
                                || deCodeString.equalsIgnoreCase( "PREV-PER" ) || deCodeString.equalsIgnoreCase( "NA" )
                                || deCodeString.equalsIgnoreCase( "SLNO" )
                                || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                            else if ( deCodeString.equalsIgnoreCase( "PROPORTIONAL-WORK-LOAD" )
                                || deCodeString.equalsIgnoreCase( "PERSENTAGE-PLUS-MINUS" )
                                || deCodeString.equalsIgnoreCase( "TT-PERCENTAGE" )
                                || deCodeString.equalsIgnoreCase( "TT-PROPORTIONAL-PERCENTAGE" ) )
                            {
                                sheet0.addCell( new Formula( tempColNo, tempRowNo, Formula, numberCellFormat ) );
                            }
                            else if ( deCodeString.equalsIgnoreCase( "RANK-ANNUAL" ) )
                            {
                                if ( orgUnitCount == orgUnitList.size() - 1 )
                                {
                                    sheet0.addCell( new Label( tempColNo, tempRowNo, "", wCellformat ) );
                                }
                                else
                                {
                                    sheet0.addCell( new Formula( tempColNo, tempRowNo, Formula, numberCellFormat ) );
                                }
                            }
                            else
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, numberCellFormat ) );
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "indicator" ) )
                        {
                            sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, numberCellFormat ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }
                }
                /*
                 * else if ( reportModelTB.equalsIgnoreCase(
                 * "dynamicwithoutrootfacility" ) ) { tempRowNo += orgUnitCount;
                 * if ( count1 == 0 ) { sheet0.addCell( new Label( tempColNo,
                 * tempRowNo, "" + (orgUnitCount + 1) ) ); } else if ( count1 ==
                 * 1 ) sheet0.addCell( new Label( tempColNo, tempRowNo,
                 * orgUnit.getName() ) ); else { if ( sType.equalsIgnoreCase(
                 * "dataelement" ) || sType.equalsIgnoreCase(
                 * "dataelement-percentage" ) ) { sheet0.addCell( new Number(
                 * tempColNo, tempRowNo, tempDouble ) ); if (
                 * deCodeString.equalsIgnoreCase( "FACILITY" ) ||
                 * deCodeString.equalsIgnoreCase( "PERIOD" ) ||
                 * deCodeString.equalsIgnoreCase( "PERIOD-NO-REPEAT" ) ||
                 * deCodeString.equalsIgnoreCase( "PREV-PER" ) ||
                 * deCodeString.equalsIgnoreCase( "NA" ) ||
                 * deCodeString.equalsIgnoreCase( "SLNO" ) ) { sheet0.addCell(
                 * new Label( tempColNo, tempRowNo, tempStr ) ); } else if (
                 * deCodeString.equalsIgnoreCase( "RANK-ANNUAL" ) ||
                 * deCodeString.equalsIgnoreCase( "PROPORTIONAL-WORK-LOAD" ) ||
                 * deCodeString.equalsIgnoreCase( "PERSENTAGE-PLUS-MINUS" )) {
                 * sheet0.addCell( new Formula( tempColNo, tempRowNo, Formula ) ); }
                 * else { sheet0.addCell( new Number( tempColNo, tempRowNo,
                 * tempDouble ) ); } } else if ( sType.equalsIgnoreCase(
                 * "indicator" ) ) { sheet0.addCell( new Number( tempColNo,
                 * tempRowNo, tempDouble ) ); } else { sheet0.addCell( new
                 * Label( tempColNo, tempRowNo, tempStr ) ); } } }
                 */
                else
                {
                    WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                    CellFormat cellFormat = cell.getCellFormat();
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    WritableCellFormat numberCellFormat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setAlignment( Alignment.CENTRE );
                    numberCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    numberCellFormat.setAlignment( Alignment.CENTRE );

                    if ( cell.getType() == CellType.LABEL && !sType.equalsIgnoreCase( "formula" ) )
                    {
                        Label l = (Label) cell;
                        l.setString( tempStr );
                        l.setCellFormat( cellFormat );
                    }
                    else
                    {
                        if ( sType.equalsIgnoreCase( "dataelement" )
                            || sType.equalsIgnoreCase( "dataelement-percentage" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITY" )
                                || deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NO-REPEAT" )
                                || deCodeString.equalsIgnoreCase( "PREV-PER" ) || deCodeString.equalsIgnoreCase( "NA" )
                                || deCodeString.equalsIgnoreCase( "SLNO" ) )
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                            else if ( deCodeString.equalsIgnoreCase( "RANK-ANNUAL" )
                                || deCodeString.equalsIgnoreCase( "PROPORTIONAL-WORK-LOAD" )
                                || deCodeString.equalsIgnoreCase( "PERSENTAGE-PLUS-MINUS" ) )
                            {
                                sheet0.addCell( new Formula( tempColNo, tempRowNo, Formula, numberCellFormat ) );
                            }
                            else
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, numberCellFormat ) );
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "indicator" ) )
                        {
                            sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, wCellformat ) );
                        }
                        else if ( sType.equalsIgnoreCase( "formula" ) )
                        {
                            sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, cellFormat ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                        //System.out.println( "TPR Report : " + tempColNo + " : " + tempRowNo + " : " + tempDouble );
                    }
                }
                dataValueList.add( tempStr );
                // dataValueList.add(deCodeString);
                count1++;
            }// inner while loop end
            orgUnitCount++;
            rowCounter++;
            slNo++;
        }// outer while loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_"
            + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();

        try
        {

        }
        finally
        {
            try
            {
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {

            }
        }// finally block end

        statementManager.destroy();
        return SUCCESS;
    }

    public List<Calendar> getStartingEndingPeriods( String deType )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod();

        if ( deType.equalsIgnoreCase( "cpmcy" ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        }
        else if ( deType.equalsIgnoreCase( "ccmcy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
            // System.out.println("We are in PT if block");
        }
        else if ( deType.equalsIgnoreCase( "ccmpy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
        }
        else if ( deType.equalsIgnoreCase( "cmpy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }

    public Period getPreviousPeriod()
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( selectedPeriod.getStartDate() );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
        period = reportService.getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            periodType );

        return period;
    }

    public Period getPreviousPeriod( Period selectedPeriod )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( selectedPeriod.getStartDate() );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
        period = reportService.getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            periodType );

        return period;
    }

    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + File.separator + raFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
                serviceType.add( deCodeElement.getAttribute( "stype" ) );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end

    private String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
        //System.out.println( "In 20-point getResultDataValue : " );
        try
        {
            // System.out.println( "expression : " + formula + " ***** " +
            // String.valueOf( startDate ) + " **** "
            // + String.valueOf( endDate ) );

            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = 0;
                int optionComboId = 0;

                try
                {
                    dataElementId = Integer.parseInt( replaceString );
                    optionComboId = Integer.parseInt( optionComboIdStr );
                }
                catch ( Exception e )
                {
                    dataElementId = 0;
                    optionComboId = 0;
                }

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                {
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );

                    //System.out.println( dataElement.getId() + " : " + organisationUnit.getId() + " : " + startDate
                    //    + " : " + endDate + " : " + aggregatedValue );

                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );

                        deFlag2 = 1;
                    }

                }
                else
                {
                    deFlag1 = 1;
                    PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                        dePeriodType, startDate, endDate ) );
                    Period tempPeriod = new Period();
                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        replaceString = dataValue.getValue();
                    }

                    else
                        replaceString = "";

                    if ( replaceString == null )
                        replaceString = "";
                }
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );

            if ( deFlag1 == 0 )
            {

                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                    resultValue = "";
                }
                if ( d == -1 )
                {
                    d = 0.0;
                    resultValue = "";
                }
                else
                {

                    // This is to display financial data as it is like 2.1476838
                    resultValue = "" + d;

                    // These lines are to display financial data that do not
                    // have decimals
                    d = d * 10;

                    if ( d % 10 == 0 )
                    {
                        resultValue = "" + (int) d / 10;
                    }

                    d = d / 10;

                    // These line are to display non financial data that do not
                    // require decimals
                    if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                        resultValue = "" + (int) d;

                    // if ( resultValue.equalsIgnoreCase( "0" ) )
                    // {
                    // resultValue = "";
                    // }
                }

            }
            else
            {
                resultValue = buffer.toString();
            }

            if ( resultValue.equalsIgnoreCase( "" ) )
                resultValue = " ";

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getResultIndicatorValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        try
        {

            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int indicatorId = Integer.parseInt( replaceString );

                Indicator indicator = indicatorService.getIndicator( indicatorId );

                if ( indicator == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;

                }

                Double aggregatedValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,
                    organisationUnit );

                if ( aggregatedValue == null )
                {
                    replaceString = NULL_REPLACEMENT;
                }
                else
                {
                    replaceString = String.valueOf( aggregatedValue );
                    deFlag2 = 1;
                }
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            if ( deFlag1 == 0 )
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                }
                if ( d == -1 )
                    d = 0.0;
                else
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = "" + d;
                }

                if ( deFlag2 == 0 )
                {
                    resultValue = " ";
                }
            }
            else
            {
                resultValue = buffer.toString();
            }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Iterator it = dataSetList.iterator();
        while ( it.hasNext() )
        {
            DataSet ds = (DataSet) it.next();
            List<DataElement> dataElementList = new ArrayList<DataElement>( ds.getDataElements() );
            if ( dataElementList.contains( de ) )
            {
                return ds.getPeriodType();
            }
        }

        return null;

    } // getDataElementPeriodType end

}
