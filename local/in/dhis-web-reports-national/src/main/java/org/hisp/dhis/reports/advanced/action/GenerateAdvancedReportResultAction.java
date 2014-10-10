package org.hisp.dhis.reports.advanced.action;

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
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class GenerateAdvancedReportResultAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
/*
    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
*/    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
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
/*
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
*/
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
/*
    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }
*/
    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private List<String> serviceType;

    private List<String> deCodeType;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date sDate;

    private Date eDate;

 //   private List<Integer> ougmemberCountList;

    private String raFolderName;

    // private DeCodesXML deCodeTotalList;

    // private int[] monthCount = { 0, 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

//    private Connection con = null;

    // private String orgUnitInfo = "-1";
    // private String aggDataTableName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
       // Date sysStartDate = new Date();

        //con = (new DBConnection()).openConnection();

        statementManager.initialise();

        raFolderName = reportService.getRAFolderName();

        // Initialization
        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
//        ougmemberCountList = new ArrayList<Integer>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        
        String deCodesXMLFileName = "";
        String reportModelTB = "";
        String reportFileNameTB = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
       // deCodesXMLFileName = reportList + "DECodes.xml";
        
        
        // Getting Report Details
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();

        System.out.println( reportModelTB + " : " + reportFileNameTB + " : " + deCodesXMLFileName + " : " + ouIDTB );

        System.out.println( "Report Generation Start Time is : \t" + new Date() );
 
        // deCodeTotalList = new DeCodesXML();

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
        
        
        //System.out.println("RA FOLDER IS : \t" + raFolderName);
        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

        // WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );

        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

//        OrganisationUnitGroup ouGroup = new OrganisationUnitGroup();
//        List<OrganisationUnitGroup> orgUnitGroupList = new ArrayList<OrganisationUnitGroup>();

        /*
         * orgUnitGroupList = reportService.getOrgUnitGroupsFromXML(); if (
         * reportModelTB.equals( "DYNAMIC-GROUP" ) ) { int tempCount1 = 0;
         * orgUnitList = new ArrayList<OrganisationUnit>(); Iterator<OrganisationUnitGroup>
         * iterator1 = orgUnitGroupList.iterator(); while ( iterator1.hasNext() ) {
         * OrganisationUnitGroup oug = (OrganisationUnitGroup) iterator1.next();
         * List<OrganisationUnit> tempList = new ArrayList<OrganisationUnit>(
         * oug.getMembers() ); Collections.sort( tempList, new
         * OrganisationUnitShortNameComparator() ); orgUnitList.addAll( tempList );
         * ougmemberCountList.add( oug.getMembers().size() ); if ( tempCount1 !=
         * 0 && tempCount1 != orgUnitGroupList.size() )
         * outputReportWorkbook.copySheet( 0, oug.getName(), tempCount1 );
         * //System.out.println( "ougMemberCount : " + oug.getMembers().size() );
         * tempCount1++; } //System.out.println( "OrgUnitList Size : " +
         * orgUnitList.size() ); }
         * 
         */

        if ( reportModelTB.equals( "dynamicwithrootfacility" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            //Collections.sort( orgUnitList, new OrganisationUnitShortNameComparator() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            // Collections.sort( orgUnitList, new
            // OrganisationUnitCommentComparator() );
            orgUnitList.add( selectedOrgUnit );
        }
        else if ( reportModelTB.equals( "dynamicwithoutrootfacility" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            // Collections.sort( orgUnitList, new
            // OrganisationUnitCommentComparator() );
        }
        else
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.add( selectedOrgUnit );
        }

        // Period Info
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        String levelName = "Level";
        int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit.getId() ) + 1;
        //int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit ) + 1;

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

        while ( orgUnitIterator1.hasNext() )
        {
            OrganisationUnit orgUnit = (OrganisationUnit) orgUnitIterator1.next();

            if ( ouc1 == 0 )
            {
                ouGroupName = orgUnit.getComment();
                if ( ouGroupName == null )
                    ouGroupName = "NULL";
            }

            if ( !ouGroupName.equalsIgnoreCase( orgUnit.getComment() ) )
            {
                ouGroupMemList.add( ouc1 );
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
 //       int ouGroupCount = 0;
   //     int ouGroupMemCount = 0;
        int slNo = 0;
    //    double[][] groupTotal = new double[ouGroupMemList.size() + 1][deCodesList.size()];
    //    List<Double> ouGroupTotal = new ArrayList<Double>();

        Integer startRow = 10;

        Integer currentRow = startRow;

//        Integer last = 0;

        Integer rowCounter = 10;

        // Integer lastRow = startRow + orgUnitList.size() - 2;
        Integer lastRow = 10 + orgUnitList.size();

        while ( it.hasNext() )
        {
            // System.out.println( orgUnitCount + " :: " + orgUnitGroupCount );
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
                String firstFormula = "";
                String secondFormula = " ";
                String thirdFormula = " ";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( getStartingEndingPeriods( deType, sDate, eDate ) );
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
                else if ( deCodeString.equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" ) )
                {
                    tempStr = levelName;
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                    || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    tempStr = simpleDateFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + (slNo + 1);
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }

                else if ( deCodeString.equalsIgnoreCase( "RANK-ANC-REG" ) )
                {
                    firstFormula = "RANK(F" + currentRow + ",F" + startRow + ":F" + (lastRow - 2) + ",0)";
                }

                else if ( deCodeString.equalsIgnoreCase( "RANK-EARLY-REG" ) )
                {
                    secondFormula = "RANK(J" + currentRow + ",J" + startRow + ":J" + (lastRow - 2) + ",0)";
                }

                else if ( deCodeString.equalsIgnoreCase( "RANK-ANNUAL" ) )
                {
                    thirdFormula = "RANK(D" + currentRow + ",D" + startRow + ":D" + (lastRow - 2) + ",0)";
                }
                else
                {
                    int years = tempEndDate.get( Calendar.YEAR ) - tempStartDate.get( Calendar.YEAR ) - 1;
                    int months = (13 - tempStartDate.get( Calendar.MONTH )) + tempEndDate.get( Calendar.MONTH );
                    int tempMonthCount = months + (years * 12);
                    deCodeString = deCodeString.replaceAll( "MONTHCOUNT", "" + tempMonthCount );
                    if ( sType.equalsIgnoreCase( "dataelement" ) || sType.equalsIgnoreCase( "dataelement-percentage" ) )
                    {
                        tempDouble = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(),
                            orgUnit );
                        //System.out.println( "The current DATAVALUE for " + orgUnit.getShortName() + " is: "
                        //    + tempDouble );
                        //System.out.println(deCodeString);
                    }
                    else if ( sType.equalsIgnoreCase( "formula" ) )
                    {
                        tempStr = deCodeString;
                    }
                    else
                    {
                        tempDouble = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate
                            .getTime(), orgUnit );
                    }
                }
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 ) + orgUnitGroupCount;
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                /*
                 * if(tempStr == null || tempStr.equals( " " )) { } else
                 */
                {

                    if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" ) )
                        {

                        }
                        else
                        {
                            tempRowNo += slNo;
                        }

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
                                    || deCodeString.equalsIgnoreCase( "NA" ) || deCodeString.equalsIgnoreCase( "SLNO" ) )
                                {
                                    sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                }

                                else if ( deCodeString.equalsIgnoreCase( "RANK-ANC-REG" ) )
                                {
                                    if ( orgUnitCount == orgUnitList.size() - 1 )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, "", wCellformat ) );
                                    }
                                    else
                                    {
                                        sheet0.addCell( new Formula( tempColNo, tempRowNo, firstFormula,
                                            numberCellFormat ) );
                                    }
                                }
                                else if ( deCodeString.equalsIgnoreCase( "RANK-EARLY-REG" ) )
                                {
                                    if ( orgUnitCount == orgUnitList.size() - 1 )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, "", wCellformat ) );
                                    }
                                    else
                                    {
                                        sheet0.addCell( new Formula( tempColNo, tempRowNo, secondFormula,
                                            numberCellFormat ) );
                                    }
                                }
                                else if ( deCodeString.equalsIgnoreCase( "RANK-ANNUAL" ) )
                                {
                                    if ( orgUnitCount == orgUnitList.size() - 1 )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, "", wCellformat ) );
                                    }
                                    else
                                    {
                                        sheet0.addCell( new Formula( tempColNo, tempRowNo, thirdFormula,
                                            numberCellFormat ) );
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
                                    || deCodeString.equalsIgnoreCase( "NA" ) || deCodeString.equalsIgnoreCase( "SLNO" ) )
                                {
                                    sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                }

                                else if ( deCodeString.equalsIgnoreCase( "RANK-ANC-REG" ) )
                                {
                                    sheet0
                                        .addCell( new Formula( tempColNo, tempRowNo, firstFormula, numberCellFormat ) );
                                }
                                else if ( deCodeString.equalsIgnoreCase( "RANK-EARLY-REG" ) )
                                {
                                    sheet0
                                        .addCell( new Formula( tempColNo, tempRowNo, secondFormula, numberCellFormat ) );
                                }
                                else if ( deCodeString.equalsIgnoreCase( "RANK-ANNUAL" ) )
                                {
                                    sheet0
                                        .addCell( new Formula( tempColNo, tempRowNo, thirdFormula, numberCellFormat ) );
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
                                System.out.println(tempColNo + " : "+ tempRowNo + " : "+ tempStr );
                                sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );

                            }

                            //System.out.println( "RCH Report :" + tempColNo + " : " + tempRowNo + " : " + tempDouble );
                        }

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
        fileName += "_" + format.formatDate( sDate ) + " - " + format.formatDate( eDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

//        Date sysEndDate = new Date();

        //System.out.println( "*************************************" );
        //System.out.println( "StartDate : " + sysStartDate + " \nEndDate : " + sysEndDate );
        //System.out.println( "*************************************" );

        return SUCCESS;
    }

    /*
     * Returns a list which contains the DataElementCodes
     */
    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getenv( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
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
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
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

                // System.out.println(deCodes.get( s )+" : "+deCodeType.get( s
                // ));
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

    public Period getPreviousPeriod(Date sDate)
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = getPeriodTypeObject( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            periodType );

        return period;
    }

    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equals( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }        
        Date lastDay = new Date( cal.getTimeInMillis() );
        //System.out.println( lastDay.toString() );        
        Period newPeriod = new Period();
        newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );      
        return newPeriod;
    }
       
    public PeriodType getPeriodTypeObject( String periodTypeName )
    {
        Collection<PeriodType> periodTypes = periodService.getAllPeriodTypes();
        PeriodType periodType = null;
        Iterator<PeriodType> iter = periodTypes.iterator();
        while ( iter.hasNext() )
        {
            PeriodType tempPeriodType = (PeriodType) iter.next();
            if ( tempPeriodType.getName().toLowerCase().trim().equals( periodTypeName ) )
            {
                periodType = tempPeriodType;

                break;
            }
        }
        if ( periodType == null )
        {
            System.out.println( "No Such PeriodType" );
            return null;
        }
        return periodType;
    }

    public List<Calendar> getStartingEndingPeriods( String deType, Date sDate, Date eDate )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod( sDate );

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
            tempStartDate.setTime( sDate );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( eDate );
            // System.out.println("We are in PT if block");
        }
        else if ( deType.equalsIgnoreCase( "ccmpy" ) )
        {
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else if ( deType.equalsIgnoreCase( "cmpy" ) )
        {
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }

    private Double getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
        try
        {
            // System.out.println( "expression : " + formula + " ***** " +
            // String.valueOf( startDate ) + " **** "
            // + String.valueOf( endDate ) );

            int deFlag1 = 0;
           // int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            Double resultValue = 0.0;

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
                if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                {

                    //System.out.println("CURRENT DATA ELEMENT IS : " + dataElement.getName() + " and ID is : " + dataElementId);
                    
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );
                    
                    //System.out.println(dataElementId + " : " + organisationUnit.getId() + " : "+ startDate + " : "+ aggregatedValue);
                    
                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        //System.out.println("CURRENT DATAVALUE IS : \t" + String.valueOf( aggregatedValue ));
                        
                        replaceString = String.valueOf( aggregatedValue );

                        //deFlag2 = 1;
                    }
                }

                matcher.appendReplacement( buffer, replaceString );

            }

            matcher.appendTail( buffer );
            
            if ( deFlag1 == 0 )
            {

                System.out.print("Expression After Convertion : "+ buffer.toString());
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    System.out.println("Exception while convertin expression : "+ buffer.toString() );
                    d = 0.0;
                    resultValue = 0.0;
                }
                if ( d == -1 )
                {
                    d = 0.0;
                    resultValue = 0.0;
                }
                else
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                    resultValue = d;
                }
            }

            System.out.println(" : "+ resultValue);
            
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private Double getResultIndicatorValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        try
        {

            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            Double resultValue = 0.0;

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

            // String resultValue = "";
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
                    resultValue = d;
                }

                if ( deFlag2 == 0 )
                {
                    resultValue = 0.0;
                }
            }
            // else
            // {
            // resultValue = buffer.toString();
            // }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

}
