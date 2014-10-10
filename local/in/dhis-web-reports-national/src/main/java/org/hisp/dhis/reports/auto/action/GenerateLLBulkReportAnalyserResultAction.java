package org.hisp.dhis.reports.auto.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateLLBulkReportAnalyserResultAction implements Action
{
    
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
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
/*    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
*/    
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
    
    private List<OrganisationUnit> orgUnitList;
    
    
    
    private String raFolderName;
    
    private SimpleDateFormat simpleDateFormat;
    
    private SimpleDateFormat monthFormat;

    private SimpleDateFormat simpleMonthFormat;
    
    private SimpleDateFormat yearFormat;
    
    private String reportFileNameTB;

    private String reportModelTB;
    
    private Period selectedPeriod;
    
    private Date sDate;

    private Date eDate;
    
    private Map<String, String> resMap;

    private Map<String, String> resMapForDeath;
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        statementManager.initialise();

        // Initialization
        raFolderName = reportService.getRAFolderName();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        String parentUnit = "";
        
        Report_in selReportObj =  reportService.getReport( Integer.parseInt( reportList ) );
        
        deCodesXMLFileName = selReportObj.getXmlTemplateName();

        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        initializeResultMap();

        initializeLLDeathResultMap();
        
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        //String outputReportFolderPath = System.get e n v( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString();
        String outputReportFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER + File.separator + UUID.randomUUID().toString();
        File newdir = new File( outputReportFolderPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        // Org Unit INFO
        if( reportModelTB.equalsIgnoreCase( "STATIC" ) || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) || reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ouIDTB ) );
            OrganisationUnitGroup orgUnitGroup = selReportObj.getOrgunitGroup();
            
            orgUnitList.retainAll( orgUnitGroup.getMembers() );
        }
        else
        {
            return INPUT;
        }
        
        System.out.println(  "---Size of Org Unit List ----: " + orgUnitList.size() + ",Report Group name is :---" + selReportObj.getOrgunitGroup().getName() + ", Size of Group member is ----:" + selReportObj.getOrgunitGroup().getMembers().size()  );
        
        OrganisationUnit selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        System.out.println( selOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
        
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
       
        // collect periodId by commaSepareted
        List<Period> tempPeriodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
        
        Collection<Integer> tempPeriodIds = new ArrayList<Integer>( getIdentifiers(Period.class, tempPeriodList ) );
        
        String periodIdsByComma = getCommaDelimitedString( tempPeriodIds );
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        List<Report_inDesign> reportDesignListLLDeath = reportService.getReportDesign( deCodesXMLFileName );
        List<Report_inDesign> reportDesignListLLMaternalDeath = reportService.getReportDesign( deCodesXMLFileName );
        
     // collect dataElementIDs by commaSepareted
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        String dataElmentIdsForLLDeathByComma = reportService.getDataelementIds( reportDesignList );
        String dataElmentIdsForMaternalDeathByComma = reportService.getDataelementIds( reportDesignList );
        
        int orgUnitCount = 0;
        
        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            String outPutFileName = reportFileNameTB.replace( ".xls", "" );
            outPutFileName += "_" + currentOrgUnit.getShortName();
            outPutFileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";

            String outputReportPath = outputReportFolderPath + File.separator + outPutFileName;
            WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
            
            Map<String, String> aggDeMap = new HashMap<String, String>();
            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            
            
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
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
                if( calendarList == null || calendarList.isEmpty() )
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
                    tempStr = currentOrgUnit.getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    tempStr = parentUnit;
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                {
                    tempStr = yearFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( eDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-START" ) )
                {
                    tempStr = monthFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-END" ) )
                {
                    tempStr = monthFormat.format( eDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + ( orgUnitCount + 1 );
                } 
                else if( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                } 
                else
                {
                    if( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        tempStr = getAggVal( deCodeString, aggDeMap );
                        //tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                    } 
                    else if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                    {
                        tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                    }
                    else
                    {
                        tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                    }
                }
        
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
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
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        } 
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        }
                        else
                        {
                            tempColNo += orgUnitCount;
                        }
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) ||  deCodeString.equalsIgnoreCase( "FACILITYPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        } 
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        } 
                        else
                        {
                            tempRowNo += orgUnitCount;
                        }
                    }
    
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
                
                count1++;
            }// inner while loop end
            
            // Line list death records
            List<Integer> llrecordNos = new ArrayList<Integer>();
            llrecordNos = getLinelistingDeathRecordNos( currentOrgUnit, selectedPeriod );
            
            String llDeathRecordNoByComma = getRecordNoByComma( llrecordNos );
            
            
            Map<String, String> aggDeForLLDeathMap = new HashMap<String, String>();
            
            aggDeForLLDeathMap.putAll( reportService.getLLDeathDataFromLLDataValueTable( currentOrgUnit.getId(), dataElmentIdsForLLDeathByComma, periodIdsByComma, llDeathRecordNoByComma ) );
            
            
            
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
                
                //Map<String, String> aggDeForLLDeathMap = new HashMap<String, String>();
                
               // aggDeForLLDeathMap.putAll( reportService.getLLDeathDataFromLLDataValueTable( currentOrgUnit.getId(), dataElmentIdsForLLDeathByComma, periodIdsByComma, recordNo ) );
                
                flag = 0;
                Iterator<Report_inDesign> reportDesignIterator1 = reportDesignListLLDeath.iterator();
                int count2 = 0;
                while ( reportDesignIterator1.hasNext() )
                {

                    Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator1.next();

                    String deType = report_inDesign.getPtype();
                    String sType = report_inDesign.getStype();
                    String deCodeString = report_inDesign.getExpression();
                    String tempStr = "";
                    String tempLLDeathValuStr = "";
                    String tempStr1 = "";
                    String tempStr2 = "";

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

                    if ( deCodeString.equalsIgnoreCase( "NA" ) )
                    {
                        tempStr = " ";
                        tempLLDeathValuStr = " ";
                    }
                    else
                    {
                        if ( sType.equalsIgnoreCase( "lldeathdataelement" ) )
                        {
                            tempStr = getLlDeathVal( deCodeString, recordNo, aggDeForLLDeathMap );
                            //tempStr = getLLDataValue( deCodeString, selectedPeriod, currentOrgUnit, recordNo );
                        }

                        else if ( sType.equalsIgnoreCase( "lldeathdataelementage" ) )
                        {
                            tempLLDeathValuStr = getLlDeathVal( deCodeString, recordNo, aggDeForLLDeathMap );
                            //tempLLDeathValuStr = getLLDataValue( deCodeString, selectedPeriod, currentOrgUnit, recordNo );
                        }
                        else
                        {
                        }
                    }
                    tempLLDeathRowNo = report_inDesign.getRowno();
                    int tempRowNo = report_inDesign.getRowno();
                    currentRowNo = tempLLDeathRowNo;
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

                            WritableCellFormat wCellformat = new WritableCellFormat();

                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                            wCellformat.setWrap( true );
                            wCellformat.setAlignment( Alignment.CENTRE );
                            wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
                            if ( sType.equalsIgnoreCase( "lldeathdataelementage" ) )
                            {
                                String tstr = resMapForDeath.get( tempLLDeathValuStr.trim() );
                                if ( tstr != null )
                                {
                                    tempStr1 = tstr.split( ":" )[0].trim();
                                    tempStr2 = tstr.split( ":" )[1].trim();
                                }
                                try
                                {
                                    sheet0.addCell( new Label( tempColNo - 1, tempRowNo, tempStr1, getCellFormat1() ) );
                                    sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr2, getCellFormat1() ) );
                                }
                                catch ( Exception e )
                                {
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
                        }

                    }
                    count2++;
                }// inner while loop end
                recordCount++;
               
            }// outer while loop end
            
            // Line Listing Matarnal Death DataElements

            List<Integer> llMaternalDeathrecordNos = new ArrayList<Integer>();
            //llMaternalDeathrecordNos = getLinelistingMateralanRecordNos( currentOrgUnit, selectedPeriod, deCodesXMLFileName );
            
            llMaternalDeathrecordNos = getLinelistingMateralanRecordNos( currentOrgUnit, selectedPeriod );
            //System.out.println( "Line Listing Maternal Death Record Count is :" + llMaternalDeathrecordNos.size() );
            
            String llMaternalDeathRecordNoByComma = getRecordNoByComma( llMaternalDeathrecordNos );
            
            Map<String, String> aggDeForLLMaternalDeathMap = new HashMap<String, String>();
            
            aggDeForLLMaternalDeathMap.putAll( reportService.getLLDeathDataFromLLDataValueTable( currentOrgUnit.getId(), dataElmentIdsForMaternalDeathByComma, periodIdsByComma, llMaternalDeathRecordNoByComma ) );
            
            
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
                
                
               // Map<String, String> aggDeForLLMaternalDeathMap = new HashMap<String, String>();
                
                //aggDeForLLMaternalDeathMap.putAll( reportService.getLLDeathDataFromLLDataValueTable( currentOrgUnit.getId(), dataElmentIdsForMaternalDeathByComma, periodIdsByComma, maternalDeathRecordNo ) );
                
                
                // Iterator<String> it1 = deCodesList.iterator();
                Iterator<Report_inDesign> reportDesignIterator2 = reportDesignListLLMaternalDeath.iterator();
                int count3 = 0;
                while ( reportDesignIterator2.hasNext() )
                {

                    Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator2.next();

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
                            tempStr = getLlDeathVal( deCodeString, maternalDeathRecordNo, aggDeForLLMaternalDeathMap );
                            //tempStr = getLLDataValue( deCodeString, selectedPeriod, currentOrgUnit, maternalDeathRecordNo );
                        }
                    }
                    int tempRowNo1 = report_inDesign.getRowno();
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

                            WritableCellFormat wCellformat = new WritableCellFormat();

                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                            wCellformat.setWrap( true );
                            wCellformat.setAlignment( Alignment.CENTRE );
                            wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
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
                           
                        }
                    }
                    count3++;
                }// inner while loop end
                maternalDeathRecordCount++;
            }// outer while loop end
            
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            orgUnitCount++;
        }
        statementManager.destroy();
        
        
        if( zipDirectory( outputReportFolderPath, outputReportFolderPath+".zip" ) )
        {
            System.out.println( selOrgUnit.getName()+ " : " + selReportObj.getName()+" Report Generation End Time is : " + new Date() );
            
            fileName = reportFileNameTB.replace( ".xls", "" );
            fileName += "_" + selOrgUnit.getShortName();
            fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".zip";

            File outputReportFile = new File( outputReportFolderPath+".zip" );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            return SUCCESS;
        }
        else
        {
            return INPUT;
        }
    }      
        
    public boolean zipDirectory( String dir, String zipfile ) throws IOException, IllegalArgumentException 
    {
        
        try
        {
            // Check that the directory is a directory, and get its contents
            
            File d = new File( dir );
            if( !d.isDirectory() )
            {            
                System.out.println( dir + " is not a directory" );
                return false;
            }
            
            String[] entries = d.list();
            byte[] buffer = new byte[4096]; // Create a buffer for copying
            int bytesRead;
    
            ZipOutputStream out = new ZipOutputStream( new FileOutputStream( zipfile ) );
    
            for (int i = 0; i < entries.length; i++) 
            {
                File f = new File( d, entries[i] );
                if ( f.isDirectory() )
                {
                    continue;//Ignore directory
                }
                
                FileInputStream in = new FileInputStream( f ); // Stream to read file
                ZipEntry entry = new ZipEntry( f.getName() ); // Make a ZipEntry
                out.putNextEntry( entry ); // Store entry
                while ( (bytesRead = in.read(buffer)) != -1 )
                {
                    out.write(buffer, 0, bytesRead);
                }
                in.close(); 
            }
            
            out.close();
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage() );
            return false;
        }
        
        return true;
    }

 // Supported Methods
    
    
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
        resMap.put( "OPL", "OBSTRUCTED/PROLONGED LABOUR" );
        resMap.put( "FITS", "FITS" );
        resMap.put( "SH", "SEVERE HYPERTENSION" );
        resMap.put( "BBCD", "BLEEDING BEFORE CHILD DELIVERY" );
        resMap.put( "BACD", "BLEEDING AFTER CHILD DELIVERY" );
        resMap.put( "HFBD", "HIGH FEVER BEFORE DELIVERY" );
        resMap.put( "HFAD", "HIGH FEVER AFTER DELIVERY" );
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
    // getting data value using Map
    private String getAggVal( String expression, Map<String, String> aggDeMap )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                replaceString = aggDeMap.get( replaceString );
                
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
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
            
            resultValue = "" + (double) d;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    
    private String getLlDeathVal( String expression,Integer recordNo, Map<String, String> aggDeForLLDeathMap )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                
                
                replaceString = aggDeForLLDeathMap.get( replaceString+":"+recordNo );
                
                if( replaceString == null )
                {
                    replaceString = "";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
           
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
    }

    public String getRecordNoByComma( List<Integer> recordNosList )
    {
        String recordNoByComma = "-1";
        
        for( Integer recordNo : recordNosList )
        {
            recordNoByComma += "," + recordNo;
        }
        
        return recordNoByComma;
    }

}
