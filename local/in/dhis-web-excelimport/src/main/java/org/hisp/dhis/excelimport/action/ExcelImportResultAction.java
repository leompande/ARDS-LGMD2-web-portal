package org.hisp.dhis.excelimport.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class ExcelImportResultAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

    private static final String NOT_VALID = "notvalid";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    /*
    private DataSetLockService dataSetLockService;
    
    public void setDataSetLockService( DataSetLockService dataSetLockService )
    {
        this.dataSetLockService = dataSetLockService;
    }
    */
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String contentType;

    public String getContentType()
    {
        return contentType;
    }

    public void setUploadContentType( String contentType )
    {
        this.contentType = contentType;
    }

    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

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

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
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

    private SimpleDateFormat simpleMonthFormat;

    public SimpleDateFormat getSimpleMonthFormat()
    {
        return simpleMonthFormat;
    }

    private SimpleDateFormat yearFormat;

    public SimpleDateFormat getYearFormat()
    {
        return yearFormat;
    }

    private SimpleDateFormat simpleYearFormat;

    public SimpleDateFormat getSimpleYearFormat()
    {
        return simpleYearFormat;
    }

    private List<String> deCodeType;

    private List<String> serviceType;

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }

    private String checkerFileNameTB;

    public void setCheckerFileNameTB( String checkerFileNameTB )
    {
        this.checkerFileNameTB = checkerFileNameTB;
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

    private String riRadio;

    public void setRiRadio( String riRadio )
    {
        this.riRadio = riRadio;
    }

    public String getRiRadio()
    {
        return riRadio;
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

    private File file;

    public void setUpload( File file )
    {
        this.file = file;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    private Hashtable<String, String> serviceList;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date sDate;

    private Date eDate;

    private Date sDateTemp;

    private Date eDateTemp;

    private PeriodType periodType;

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    private String raFolderName;

    private List<OrganisationUnit> childOrgUnits;

    public List<OrganisationUnit> getChildOrgUnits()
    {
        return childOrgUnits;
    }

    Integer startMonth;

    Integer endMonth;

    private Map<String, String> months;

    public Map<String, String> getMonths()
    {
        return months;
    }

    private Map<String, Integer> monthOrder;

    public Map<String, Integer> getMonthOrder()
    {
        return monthOrder;
    }

    private boolean excelValidator;

    public boolean getExcelValidator()
    {
        return excelValidator;
    }

    private String checkerSheetNumber;

    public String getCheckerSheetNumber()
    {
        return checkerSheetNumber;
    }

    private String rowStart;

    public String getRowStart()
    {
        return rowStart;
    }

    private String rowEnd;

    public String getRowEnd()
    {
        return rowEnd;
    }

    private String columnStart;

    public String getColumnStart()
    {
        return columnStart;
    }

    private String columnEnd;

    public String getColumnEnd()
    {
        return columnEnd;
    }
    
    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    OrganisationUnit orgUnit;
    
    private boolean lockStatus;
    
    public boolean isLockStatus()
    {
        return lockStatus;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // Initialization

        statementManager.initialise();
        raFolderName = reportService.getRAFolderName();

        InputStream inputStream = null;

        excelValidator = true;

        mathTool = new MathTool();
        services = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        String deCodesImportXMLFileName = "";
        String deCodesCheckerXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleYearFormat = new SimpleDateFormat( "yy" );
        deCodesImportXMLFileName = reportList + "DECodes.xml";
        deCodesCheckerXMLFileName = checkerFileNameTB + "DECodes.xml";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        checkerSheetNumber = "";
        rowStart = "";
        rowEnd = "";
        columnStart = "";
        columnEnd = "";

        String excelImportFolderName = "excelimport";

        inputStream = new BufferedInputStream( new FileInputStream( file ) );
        
       
        String excelTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + excelImportFolderName + File.separator + "template" + File.separator + reportFileNameTB;

        System.out.println( excelTemplatePath );

        String excelFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + excelImportFolderName + File.separator + "pending" + File.separator + fileName;

        file.renameTo( new File( excelFilePath ) );

        moveFile( file, new File( excelFilePath ) );

        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale( new Locale( "en", "EN" ) );

        String fileType = fileName.substring(fileName.indexOf( '.' )+1, fileName.length());
        
        if (!fileType.equalsIgnoreCase( "xls" ))
        {
            message = "The file you are trying to import is not an excel file";
            
            return SUCCESS;
        }
        
        //Workbook excelImportFile = Workbook.getWorkbook( file );
        
        Workbook excelImportFile = Workbook.getWorkbook( new File( excelFilePath ) );
        
        Workbook excelTemplateFile = Workbook.getWorkbook( new File( excelTemplatePath ) );

        excelValidator = validateReport( deCodesImportXMLFileName, excelImportFile, excelTemplateFile );

        if ( excelValidator == false )
        {
            message = "The file you are trying to import is not the correct format";

            return SUCCESS;
        }

        if ( reportModelTB.equalsIgnoreCase( "STATIC" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        //DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSet, selectedPeriod, orgUnit );
        
        lockStatus = dataSetService.isLocked( dataSet, selectedPeriod, orgUnit, null );
        
        //if( dataSetLock != null )
        if( lockStatus )
        {
            message = "Unable to Import : Corresponding Dataset ( "+dataSet.getName()+" ) for the selected Excel Template is locked.";
            
            return SUCCESS;
        }

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = new ArrayList<String>();

        deCodesList.clear();

        if ( deCodesList.isEmpty() )
            deCodesList = getDECodes( deCodesImportXMLFileName );

        Iterator<OrganisationUnit> it = orgUnitList.iterator();

        OrganisationUnit currentOrgUnit = new OrganisationUnit();

        while ( it.hasNext() )
        {
            currentOrgUnit = (OrganisationUnit) it.next();

            Iterator<String> it1 = deCodesList.iterator();
            int count1 = 0;
            while ( it1.hasNext() )
            {

                String deCodeString = (String) it1.next();

                String deType = (String) deCodeType.get( count1 );
                String sType = (String) serviceType.get( count1 );
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 );

                Calendar tempStart = Calendar.getInstance();
                Calendar tempEnd = Calendar.getInstance();

                Map<DataElement, DataElementCategoryOptionCombo> deAndComboMap = new HashMap<DataElement, DataElementCategoryOptionCombo>();

                DataElement currentDataElement = new DataElement();

                storedBy = currentUserService.getCurrentUsername();

                DataElementCategoryOptionCombo currentOptionCombo = new DataElementCategoryOptionCombo();

                String value = "";

                System.out.println( deCodeString );
                
                deAndComboMap = getDeAndCombo( deCodeString );

                for ( DataElement de : deAndComboMap.keySet() )
                {
                    currentDataElement = de;

                    currentOptionCombo = deAndComboMap.get( de );
                }

                DataValue dataValue = new DataValue();

                dataValue.setDataElement( currentDataElement );

                dataValue.setPeriod( selectedPeriod );
                dataValue.setSource( currentOrgUnit );
                dataValue.setOptionCombo( currentOptionCombo );
                dataValue.setTimestamp( new Date() );
                dataValue.setStoredBy( storedBy );

                Sheet sheet = excelImportFile.getSheet( sheetNo );

                String cellContent = sheet.getCell( tempColNo, tempRowNo ).getContents();
                
                System.out.println( tempColNo + " : " + tempRowNo + " : " + cellContent );
                
                value = cellContent;

                if ( cellContent.equalsIgnoreCase( "" ) || cellContent == null || cellContent.equalsIgnoreCase( " " ) )
                {
                    count1++;

                    continue;
                }

                dataValue.setValue( cellContent );

                DataValue oldValue = new DataValue();

                oldValue = dataValueService.getDataValue( currentOrgUnit, currentDataElement, selectedPeriod,
                    currentOptionCombo );

                if ( oldValue == null )
                {
                    try
                    {
                        dataValueService.addDataValue( dataValue );
                    }
                    catch ( Exception ex )
                    {
                        throw new RuntimeException( "Cannot add datavalue", ex );
                    }
                }
                else if ( oldValue != null && (!riRadio.equalsIgnoreCase( "reject" )) )
                {
                    try
                    {
                        oldValue.setValue( value );
                        oldValue.setTimestamp( new Date() );
                        oldValue.setStoredBy( storedBy );

                        dataValueService.updateDataValue( oldValue );
                    }
                    catch ( Exception ex )
                    {
                        throw new RuntimeException( "Cannot add datavalue", ex );
                    }
                }
                else
                {
                    count1++;

                    continue;
                }

                count1++;
            }// inner while loop end

        }// outer while loop end

        excelImportFile.close();

        statementManager.destroy();
        
        message = "The report has been imported successfully";

        try
        {
        }
        catch( Exception e )
        {
        }
        finally
        {
            if( inputStream != null )
            inputStream.close();             
        }

        return SUCCESS;
    }

    public List<Calendar> getStartingEndingPeriods( String deType, Calendar tempStartDate, Calendar tempEndDate )
    {
        List<Calendar> calendarList = new ArrayList<Calendar>();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod( tempStartDate.getTime() );

        if ( deType.equalsIgnoreCase( "ccmcy" ) )
        {
            // tempStartDate.setTime( selectedPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
            // System.out.println("CCMCY : "+ String.valueOf(
            // tempStartDate.getTime()) +" ------ "+String.valueOf(
            // tempEndDate.getTime()));
        }
        else if ( deType.equalsIgnoreCase( "cpmcy" ) )
        {
            // tempStartDate.setTime( previousPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        }
        else if ( deType.equalsIgnoreCase( "cmpy" ) )
        {
            // tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else if ( deType.equalsIgnoreCase( "ccmpy" ) )
        {
            // tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );

            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );

        }
        else if ( deType.equalsIgnoreCase( "pmcy" ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            tempEndDate.setTime( previousPeriod.getEndDate() );

        }

        else
        {

            tempStartDate.setTime( tempStartDate.getTime() );
            tempEndDate.setTime( tempEndDate.getTime() );
        }

        // System.out.print(deType+" -- ");
        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }

    public Period getPreviousPeriod( Date tempStartDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        // tempDate.setTime( selectedPeriod.getStartDate() );
        tempDate.setTime( tempStartDate );
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
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

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

        Period newPeriod = new Period();

        newPeriod.setStartDate( firstDay );
        newPeriod.setEndDate( lastDay );
        newPeriod.setPeriodType( periodType );

        return newPeriod;
    }

    public List<String> getDECodes( String fileName )
    {

        String excelImportFolderName = "excelimport";

        List<String> deCodes = new ArrayList<String>();

        deCodes.clear();
        deCodes.clear();
        serviceType.clear();
        deCodeType.clear();
        sheetList.clear();
        rowList.clear();
        colList.clear();

        // String excelTemplatePath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + excelImportFolderName + File.separator + reportFileNameTB;

        // String excelFilePath = System.getenv( "DHIS2_HOME" ) + File.separator
        // + raFolderName + excelImportFolderName
        // + File.separator + "pending" + File.separator + fileName;

        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + excelImportFolderName + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + excelImportFolderName
                    + File.separator + fileName;
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

    public List<String> getCheckerDECodes( String fileName )
    {

        String excelImportFolderName = "excelimport";

        List<String> rangeList = new ArrayList<String>();

        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + excelImportFolderName + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + excelImportFolderName
                    + File.separator + fileName;
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

            NodeList listOfDRanges = doc.getElementsByTagName( "range-info" );
            int ranges = listOfDRanges.getLength();

            for ( int s = 0; s < ranges; s++ )
            {
                Element deCodeElement = (Element) listOfDRanges.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                checkerSheetNumber = (((Node) textDECodeList.item( 0 )).getNodeValue().trim());
                rowStart = deCodeElement.getAttribute( "rowStart" );
                rowEnd = deCodeElement.getAttribute( "rowEnd" );
                columnStart = deCodeElement.getAttribute( "columnStart" );
                columnEnd = deCodeElement.getAttribute( "columnEnd" );

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
        return rangeList;
    }// getCheckerDECodes end

    /*
     * Returns the PeriodType Object for selected DataElement, If no PeriodType
     * is found then by default returns Monthly Period type
     */
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

    public boolean validateReport( String deCodesImportXMLFileName, Workbook excelImportFile, Workbook excelTemplateFile )
    {

        boolean validator = true;

        List<String> checkerDeCodes = new ArrayList<String>();

        checkerDeCodes = getCheckerDECodes( deCodesImportXMLFileName );

        int sheetNumber = Integer.valueOf( checkerSheetNumber );
        int firstRow = Integer.valueOf( rowStart );
        int lastRow = Integer.valueOf( rowEnd );
        int firstColumn = Integer.valueOf( columnStart );
        int lastColumn = Integer.valueOf( columnEnd );

        if ( excelImportFile.getSheet( sheetNumber ).getRows() == excelTemplateFile
            .getSheet( sheetNumber ).getRows() )
        {

            for ( int c = firstColumn; c <= lastColumn; c++ )
            {
                for ( int r = firstRow; r <= lastRow; r++ )
                {

                    Sheet importFileSheet = excelImportFile.getSheet( sheetNumber );
                    Sheet templateFileSheet = excelTemplateFile.getSheet( sheetNumber );

                    String cellContent = importFileSheet.getCell( c, r ).getContents();

                    String templateContent = importFileSheet.getCell( c, r ).getContents();

                    if ( templateContent.equalsIgnoreCase( cellContent )
                        && cellContent.equalsIgnoreCase( templateContent ) )
                    {
                        continue;
                    }

                    else
                    {
                        validator = false;
                        break;

                    }
                }
            }
        }
        
        else
        {
            validator = false;
        }

        System.out.println( "Getting out with validator : \t" + validator );

        return validator;
    }// validateReport end

    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @return The generated expression
     */

    public Map<DataElement, DataElementCategoryOptionCombo> getDeAndCombo( String formula )
    {
        Map<DataElement, DataElementCategoryOptionCombo> deAndOptionMap = new HashMap<DataElement, DataElementCategoryOptionCombo>();

        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );

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
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    return null;
                }

                else
                {

                    deAndOptionMap.put( dataElement, optionCombo );

                }

            }
        }

        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }

        return deAndOptionMap;
    }

    private final Period reloadPeriod( Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        if ( session.contains( period ) )
        {
            return period; // Already in session, no reload needed
        }

        return periodStore.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodStore.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

    public int moveFile( File source, File dest )
        throws IOException
    {

        if ( !dest.exists() )
        {
            dest.createNewFile();
        }

        InputStream in = null;

        OutputStream out = null;

        try
        {

            in = new FileInputStream( source );

            out = new FileOutputStream( dest );

            byte[] buf = new byte[1024];

            int len;

            while ( (len = in.read( buf )) > 0 )
            {
                out.write( buf, 0, len );
            }
        }

        catch ( Exception e )
        {
            return -1;
        }

        finally
        {
            if( in != null ) 
                in.close();
            if( out != null )                       
                out.close();
        }

        return 1;

    }

}
