package org.hisp.dhis.excelimport.portal.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.excelimport.util.ExcelImport_DeCode;
import org.hisp.dhis.excelimport.util.ExcelImport_Header;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class PortalExcelImportResultAction implements Action
{
    private static final String PHC_FORMAT = "PHC";
    private static final String CHC_FORMAT = "CHC";
    private static final String SDH_TH_FORMAT = "SUB-DIVISIONAL/TALUKA HOSPITAL";
    private static final String SC_FORMAT = "SC";
    private static final String DH_FORMAT = "DISTRICT HOSPITAL";
    private static final String BLOCK_FORMAT = "BLOCK";
    private static final String DISTRICT_FORMAT = "DISTRICT";
    
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

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
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
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    
    private String proforma;

    public void setProforma( String proforma )
    {
        this.proforma = proforma;
    }

    private String checkerTemplateName;

    public void setCheckerTemplateName( String checkerTemplateName )
    {
        this.checkerTemplateName = checkerTemplateName;
    }

    private String checkerRangeForHeader;

    public void setCheckerRangeForHeader( String checkerRangeForHeader )
    {
        this.checkerRangeForHeader = checkerRangeForHeader;
    }

    private String checkerRangeForData;

    public void setCheckerRangeForData( String checkerRangeForData )
    {
        this.checkerRangeForData = checkerRangeForData;
    }

    private Integer datasetId;

    public void setDatasetId( Integer datasetId )
    {
        this.datasetId = datasetId;
    }

    private Integer orgunitGroupId;

    public void setOrgunitGroupId( Integer orgunitGroupId )
    {
        this.orgunitGroupId = orgunitGroupId;
    }

    private String facilityStart;

    public void setFacilityStart( String facilityStart )
    {
        this.facilityStart = facilityStart;
    }

    private String importSheetId;
    
    public void setImportSheetId( String importSheetId )
    {
        this.importSheetId = importSheetId;
    }

    private String message = "";

    public String getMessage()
    {
        return message;
    }

    private File output;

    public File getOutput()
    {
        return output;
    }

    private File upload;

    public File getUpload()
    {
        return upload;
    }

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    private String raFolderName;
    
    private boolean lockStatus;
    
    public boolean isLockStatus()
    {
        return lockStatus;
    }
	
	String selectedPeriodicity;
    
    public void setSelectedPeriodicity( String selectedPeriodicity )
    {
        this.selectedPeriodicity = selectedPeriodicity;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        statementManager.initialise();

        message += "<br><font color=blue>Importing StartTime : " + new Date() + "  - By "+currentUserService.getCurrentUsername() + "</font><br>";
        System.out.println( message );
        
        raFolderName = reportService.getRAFolderName();

        String excelTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
        + "excelimport" + File.separator + "template" + File.separator + checkerTemplateName;

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        Workbook excelImportFile = Workbook.getWorkbook( upload );
        WritableWorkbook writableExcelImportFile = Workbook.createWorkbook( new File(outputReportPath), excelImportFile );
        Workbook excelTemplateFile = Workbook.getWorkbook( new File( excelTemplatePath ) );

        if( validateReport( excelImportFile, excelTemplateFile ) )
        {
            System.out.println("Uploaded ExcelSheet is matched with Template file.");
            importPortalData( writableExcelImportFile );
            
        }
        else
        {
            message = "The file you are trying to import is not the correct format";
        }
        
        try
        {
            
        }
        finally
        {
            excelImportFile.close();
            excelTemplateFile.close();
            writableExcelImportFile.close();
        }
        
        System.out.println("Importing has been completed which is started by : "+currentUserService.getCurrentUsername() + " at " + new Date() );
        message += "<br><br><font color=blue>Importing EndTime : " + new Date() + "  - By "+currentUserService.getCurrentUsername() + "</font>";
        
        statementManager.destroy();

        return SUCCESS;
    }
    
    
    public List<ExcelImport_Header> getHeaderInfo( String fileName )
    {
        String excelImportFolderName = "excelimport";
        List<ExcelImport_Header> headerInfoList = new ArrayList<ExcelImport_Header>();

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
            System.out.println("DHIS_HOME is not set");
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "There is no DECodes related XML file in the DHIS2 Home" );
                return null;
            }

            NodeList listOfHeaders = doc.getElementsByTagName( "header" );
            int totalHeaders = listOfHeaders.getLength();

            for( int s = 0; s < totalHeaders; s++ )
            {
                Element headerElement = (Element) listOfHeaders.item( s );
                NodeList textHeaderList = headerElement.getChildNodes();
                String headerExpression = ((Node) textHeaderList.item( 0 )).getNodeValue().trim();
                Integer sheetNo = Integer.parseInt( headerElement.getAttribute( "sheetno" ) );
                Integer rowNo = Integer.parseInt(  headerElement.getAttribute( "rowno" ) );
                Integer colNo = Integer.parseInt( headerElement.getAttribute( "colno" ) );
                ExcelImport_Header excelImport_Header = new ExcelImport_Header( sheetNo, rowNo, colNo, headerExpression );
                
                headerInfoList.add( excelImport_Header );
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
        
        return headerInfoList;
    }

    public List<ExcelImport_DeCode> getDataInfo( String fileName )
    {
        String excelImportFolderName = "excelimport";
        List<ExcelImport_DeCode> deCodeList = new ArrayList<ExcelImport_DeCode>();

        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println("DHIS_HOME is not set");
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "There is no DECodes related XML file in the DHIS2 Home" );
                return null;
            }

            NodeList listOfDeCodes = doc.getElementsByTagName( "de-code" );
            int totalDeCodes = listOfDeCodes.getLength();

            for( int s = 0; s < totalDeCodes; s++ )
            {
                Element deCodeElement = (Element) listOfDeCodes.item( s );
                NodeList textDeCodeList = deCodeElement.getChildNodes();
                String deCodeExpression = ((Node) textDeCodeList.item( 0 )).getNodeValue().trim();
                Integer sheetNo = Integer.parseInt( deCodeElement.getAttribute( "sheetno" ) );
                Integer rowNo = Integer.parseInt(  deCodeElement.getAttribute( "rowno" ) );
                Integer colNo = Integer.parseInt( deCodeElement.getAttribute( "colno" ) );
                ExcelImport_DeCode excelImport_DeCode = new ExcelImport_DeCode( sheetNo, rowNo, colNo, deCodeExpression );
                
                deCodeList.add( excelImport_DeCode );
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
        
        return deCodeList;
    }
    
    private void importPortalData( WritableWorkbook importWorkbook ) throws Exception
    {
        List<ExcelImport_Header> headerInfoList = new ArrayList<ExcelImport_Header>();
        headerInfoList = getHeaderInfo( importSheetId );

        List<ExcelImport_DeCode> deCodeList = new ArrayList<ExcelImport_DeCode>();
        deCodeList = getDataInfo( importSheetId );
        
        Map<String, String> monthMap = new HashMap<String, String>();
        monthMap.put( "January", "01" );
        monthMap.put( "February", "02" );
        monthMap.put( "March", "03" );
        monthMap.put( "April", "04" );
        monthMap.put( "May", "05" );
        monthMap.put( "June", "06" );
        monthMap.put( "July", "07" );
        monthMap.put( "August", "08" );
        monthMap.put( "September", "09" );
        monthMap.put( "October", "10" );
        monthMap.put( "November", "11" );
        monthMap.put( "December", "12" );
        
        String selectedMonth = "";
        
		String selectedFinancialYear = "";
        String selectedFormat = "";
        String selectedParentName = "";
        
        String query = "";
        String storedBy = currentUserService.getCurrentUsername();
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        DataSet dataSet = dataSetService.getDataSet( datasetId );
        
        Sheet sheet = importWorkbook.getSheet( 0 );

        for( ExcelImport_Header header : headerInfoList )
        {
            sheet = importWorkbook.getSheet( header.getSheetno() );
            String cellContent = sheet.getCell( header.getColno(), header.getRowno() ).getContents();
            
            if( cellContent.equalsIgnoreCase( "" ) || cellContent == null || cellContent.equalsIgnoreCase( " " ) )
            {
                continue;
            }

            if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_PERIOD ) )
            {
                selectedMonth = monthMap.get( cellContent );
            }
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_FINANCIALYEAR ) )
            {
                selectedFinancialYear = cellContent;
            }
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_FORMAT ) )
            {
                selectedFormat = cellContent;
            }
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_FACILITY_PARENT ) )
            {
                selectedParentName = cellContent;
            }
			/*
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_PERIODICITY ) )
            {
                selectedPeriodicity = cellContent;
            }
			*/
        }
        
        String selStartDate = "";
        String selEndDate = "";
        if( !selectedFinancialYear.trim().equalsIgnoreCase( "" ) )
        {
            if( selectedMonth.equals( "01" ) || selectedMonth.equals( "02" ) || selectedMonth.equals( "03" ) )
            {
                selStartDate = selectedFinancialYear.split( "-" )[1] + "-" + selectedMonth + "-" + "01";
            }
            else
            {
                selStartDate = selectedFinancialYear.split( "-" )[0] + "-" + selectedMonth + "-" + "01";
            }
        }

        System.out.println( "******* " +selStartDate + " : " + selectedMonth + " : " + selectedFinancialYear + " : " +  selectedPeriodicity );
        
        
        PeriodType periodType = periodService.getPeriodTypeByName( selectedPeriodicity );
        Period selectedPeriod = getSelectedPeriod( selStartDate, periodType );
        SimpleDateFormat periodFormat;
        if( periodType.getName().equalsIgnoreCase("Monthly") )
        {
            periodFormat = new SimpleDateFormat("MMM-yyyy");
        }
        else if( periodType.getName().equalsIgnoreCase("Monthly") )
        {
            periodFormat = new SimpleDateFormat("yyyy");
        }
        else
        {
            periodFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        
        Integer selectedBlockId = 0;
        if( selectedFormat.equals( CHC_FORMAT ) || selectedFormat.equals( SDH_TH_FORMAT ) )
        {
            selectedBlockId = getOrgUnitIdByURL( selectedParentName + ":" + DISTRICT_FORMAT );
        }
        else if( selectedFormat.equals( PHC_FORMAT ) || selectedFormat.equals( SC_FORMAT ) )
        {
            selectedBlockId = getOrgUnitIdByURL( selectedParentName + ":" +  BLOCK_FORMAT );
        }
        else
        {
            selectedBlockId = getOrgUnitIdByComment( selectedParentName );
        }
        
        if( selectedBlockId != null )
        {
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedBlockId ) );
            OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupId );
            orgUnitList.retainAll( orgUnitGroup.getMembers() );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
            
            System.out.println( orgUnitList.size() + " : " + orgUnitGroup.getMembers().size() + " : " + orgUnitList.size() );
            
            int facilityStartRow = Integer.parseInt( facilityStart.split( "," )[0] );
            int facilityStartCol = Integer.parseInt( facilityStart.split( "," )[1] );
            
            String facility = sheet.getCell( facilityStartCol, facilityStartRow ).getContents();
            int colCount = facilityStartCol;
            String facilityP = "";
            while( facility != null && !facility.trim().equalsIgnoreCase( "" ) )
            {
                String tempFacility = facility;
                if( selectedFormat.equals( CHC_FORMAT ) || selectedFormat.equals( SDH_TH_FORMAT ) )
                {
                    if( sheet.getCell( colCount, facilityStartRow-1 ).getContents() != null && !sheet.getCell( colCount, facilityStartRow-1 ).getContents().trim().equalsIgnoreCase( "" ) )
                    {
                        facilityP = sheet.getCell( colCount, facilityStartRow-1 ).getContents();
                    }
                    
                    facility = sheet.getCell( colCount, facilityStartRow ).getContents();
                    
                    tempFacility = selectedParentName+ ":" + facilityP + ":" + facility + ":" + selectedFormat;
                }
                else if( selectedFormat.equals( PHC_FORMAT ) || selectedFormat.equals( SC_FORMAT ) )
                {
                    if( sheet.getCell( colCount, facilityStartRow-2 ).getContents() != null && !sheet.getCell( colCount, facilityStartRow-2 ).getContents().trim().equalsIgnoreCase( "" ) )
                    {
                        facilityP = sheet.getCell( colCount, facilityStartRow-2 ).getContents();
                    }
                    
                    facility = sheet.getCell( colCount, facilityStartRow ).getContents();
                    
                    tempFacility = facilityP + ":" + selectedParentName + ":" + facility + ":" + selectedFormat;
                }
                
                if( facility.trim().equalsIgnoreCase( "Total" ) )
                {
                    colCount++;
                    facility = sheet.getCell( colCount, facilityStartRow ).getContents();

                    continue;
                }
                
                System.out.println("tempFacility: "+ tempFacility);
                Integer currentOrgunitId = getOrgUnitIdByURL( tempFacility );
                if( currentOrgunitId != null )
                {
                    OrganisationUnit portalOrgUnit = organisationUnitService.getOrganisationUnit( currentOrgunitId );
                    
                    if( portalOrgUnit != null && orgUnitList.contains( portalOrgUnit ) )
                    {
                        System.out.println("--------Importing started for :"+portalOrgUnit.getName() + "-------------" );
                        
						lockStatus = dataSetService.isLocked( dataSet, selectedPeriod, portalOrgUnit, null );
                        //DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSet, selectedPeriod, portalOrgUnit );
                        //if( dataSetLock != null )
                        if( lockStatus )
                        {
                            message += "<br><font color=red><strong>Unable to Import : Corresponding Dataset ( "+dataSet.getName()+" ) for " + portalOrgUnit.getName() + " and for period : " + periodFormat.format( selectedPeriod.getStartDate() ) + " is locked.</strong></font>";
                            System.out.println("Unable to Import : Corresponding Dataset ( "+dataSet.getName()+" ) for " + portalOrgUnit.getName() + " and for period : " + periodFormat.format( selectedPeriod.getStartDate() ) + " is locked.");
                            colCount++;
                            facility = sheet.getCell( colCount, facilityStartRow ).getContents();
                            
                            continue;
                        }

                        int insertFlag = 1;
                        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";

                        for( ExcelImport_DeCode deCode : deCodeList )
                        {
                            String deCodeExpression = deCode.getExpression();
			    System.out.println( deCodeExpression );
                            if( deCodeExpression != null && !deCodeExpression.trim().equals( "" ) )
                            {
                                Integer deId = Integer.parseInt( deCodeExpression.split( "\\." )[0] );
                                Integer deCOCId = Integer.parseInt( deCodeExpression.split( "\\." )[1] );
                                
                                String dataValue = sheet.getCell( colCount, deCode.getRowno() ).getContents();
                                
                                query = "SELECT value FROM datavalue WHERE dataelementid = " + deId + 
                                            " AND categoryoptioncomboid = " + deCOCId +
                                            " AND periodid = " + selectedPeriod.getId() +
                                            " AND sourceid = " + portalOrgUnit.getId();

                                long t;
                                Date d = new Date();
                                t = d.getTime();
                                java.sql.Date lastUpdatedDate = new java.sql.Date( t );

                                SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
                                if ( sqlResultSet1 != null && sqlResultSet1.next() )
                                {
                                    String updateQuery = "UPDATE datavalue SET value = '" + dataValue + "', storedby = '" + storedBy + "',lastupdated='" + lastUpdatedDate + "' WHERE dataelementid = "+ deId +" AND periodid = " + selectedPeriod.getId() + " AND sourceid = " + portalOrgUnit.getId() + " AND categoryoptioncomboid = "+deCOCId;
                                    jdbcTemplate.update( updateQuery );
                                }
                                else
                                {
                                    if( dataValue != null && !dataValue.trim().equalsIgnoreCase( "" ) )
                                    {
                                        insertQuery += "( "+ deId + ", " + selectedPeriod.getId() + ", "+ portalOrgUnit.getId() +", " + deCOCId + ", '" + dataValue + "', '" + storedBy + "', '" + lastUpdatedDate + "' ), ";
                                        insertFlag = 2;
                                    }
                                }
                            }
                        }
                        
                        if( insertFlag != 1 )
                        {
                            insertQuery = insertQuery.substring( 0, insertQuery.length()-2 );
                            jdbcTemplate.update( insertQuery );
                            System.out.println("Data is uploaded in DHIS for : "+ facility);
                        }
                        message += "<br>Data is uploaded into DHIS for : "+ facility + " and for period : "+ periodFormat.format( selectedPeriod.getStartDate() );
                    }
                    else
                    {
                        System.out.println( facility + " is not a memeber of orgunitgroup : "+ orgUnitGroup.getName() );
                        message += "<br><font color=red>" + facility + " is not a memeber of orgunitgroup : "+ orgUnitGroup.getName() + "</font>";
                    }
                }
                else
                {
                    System.out.println("No Mapping found in DHIS for :"+ facility + " : NULL");
                    message += "<br><font color=red>No Mapping found in DHIS for : "+ facility + "</font>";
                }
                
                colCount++;
                facility = sheet.getCell( colCount, facilityStartRow ).getContents();
                
            }
            
            
        }
        
    }


    /*
    private void importPortalData( WritableWorkbook importWorkbook ) throws Exception
    {
        List<ExcelImport_Header> headerInfoList = new ArrayList<ExcelImport_Header>();
        headerInfoList = getHeaderInfo( importSheetId );

        List<ExcelImport_DeCode> deCodeList = new ArrayList<ExcelImport_DeCode>();
        deCodeList = getDataInfo( importSheetId );
        
        Map<String, String> monthMap = new HashMap<String, String>();
        monthMap.put( "January", "01" );
        monthMap.put( "February", "02" );
        monthMap.put( "March", "03" );
        monthMap.put( "April", "04" );
        monthMap.put( "May", "05" );
        monthMap.put( "June", "06" );
        monthMap.put( "July", "07" );
        monthMap.put( "August", "08" );
        monthMap.put( "September", "09" );
        monthMap.put( "October", "10" );
        monthMap.put( "November", "11" );
        monthMap.put( "December", "12" );
        
        String selectedMonth = "";
        String selectedPeriodicity = "";
        String selectedFinancialYear = "";
        String selectedFormat = "";
        String selectedParentName = "";
        
        String query = "";
        String storedBy = currentUserService.getCurrentUsername();
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        DataSet dataSet = dataSetService.getDataSet( datasetId );
        
        Sheet sheet = importWorkbook.getSheet( 0 );

        for( ExcelImport_Header header : headerInfoList )
        {
            sheet = importWorkbook.getSheet( header.getSheetno() );
            String cellContent = sheet.getCell( header.getColno(), header.getRowno() ).getContents();
            
            if( cellContent.equalsIgnoreCase( "" ) || cellContent == null || cellContent.equalsIgnoreCase( " " ) )
            {
                continue;
            }

            if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_PERIOD ) )
            {
                selectedMonth = monthMap.get( cellContent );
            }
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_FINANCIALYEAR ) )
            {
                selectedFinancialYear = cellContent;
            }
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_FORMAT ) )
            {
                selectedFormat = cellContent;
            }
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_FACILITY_PARENT ) )
            {
                selectedParentName = cellContent;
            }
            else if( header.getExpression().equalsIgnoreCase( ExcelImport_Header.HEADER_PERIODICITY ) )
            {
                selectedPeriodicity = cellContent;
            }
        }
        
        String selStartDate = "";
        String selEndDate = "";
        if( !selectedFinancialYear.trim().equalsIgnoreCase( "" ) )
        {
            if( selectedMonth.equals( "01" ) || selectedMonth.equals( "02" ) || selectedMonth.equals( "03" ) )
            {
                selStartDate = selectedFinancialYear.split( "-" )[1] + "-" + selectedMonth + "-" + "01";
            }
            else
            {
                selStartDate = selectedFinancialYear.split( "-" )[0] + "-" + selectedMonth + "-" + "01";
            }
        }

        PeriodType periodType = periodService.getPeriodTypeByName( selectedPeriodicity );
        Period selectedPeriod = getSelectedPeriod( selStartDate, periodType );
        SimpleDateFormat periodFormat;
        if( periodType.getName().equalsIgnoreCase("Monthly") )
        {
            periodFormat = new SimpleDateFormat("MMM-yyyy");
        }
        else if( periodType.getName().equalsIgnoreCase("Monthly") )
        {
            periodFormat = new SimpleDateFormat("yyyy");
        }
        else
        {
            periodFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        
        Integer selectedBlockId = getOrgUnitIdByComment( selectedParentName );
        if( selectedBlockId != null )
        {
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedBlockId ) );
            OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupId );
            orgUnitList.retainAll( orgUnitGroup.getMembers() );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
            
            System.out.println( orgUnitList.size() + " : " + orgUnitGroup.getMembers().size() + " : " + orgUnitList.size() );
            
            int facilityStartRow = Integer.parseInt( facilityStart.split( "," )[0] );
            int facilityStartCol = Integer.parseInt( facilityStart.split( "," )[1] );
            
            String facility = sheet.getCell( facilityStartCol, facilityStartRow ).getContents();
            int colCount = facilityStartCol;
            String facilityP = "";
            while( facility != null && !facility.trim().equalsIgnoreCase( "" ) )
            {
                if( selectedFormat.equals( CHC_FORMAT ))
                {
                    if( sheet.getCell( colCount, facilityStartRow-1 ).getContents() != null && !sheet.getCell( colCount, facilityStartRow-1 ).getContents().trim().equalsIgnoreCase( "" ) )
                    {
                        facilityP = sheet.getCell( colCount, facilityStartRow-1 ).getContents();
                    }
                    
                    facility = sheet.getCell( colCount, facilityStartRow ).getContents();
                    
                    System.out.println( facility + " :: " + facilityP );
                    colCount++;
                    continue;
                }
                
                if( facility.trim().equalsIgnoreCase( "Total" ) )
                {
                    colCount++;
                    facility = sheet.getCell( colCount, facilityStartRow ).getContents();

                    continue;
                }
                
                Integer currentOrgunitId = getOrgUnitIdByComment( facility, orgUnitIds );
                if( currentOrgunitId != null )
                {
                    OrganisationUnit portalOrgUnit = organisationUnitService.getOrganisationUnit( currentOrgunitId );
                    
                    if( portalOrgUnit != null && orgUnitList.contains( portalOrgUnit ) )
                    {
                        System.out.println("--------Importing started for :"+portalOrgUnit.getName() + "-------------" );
                        DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSet, selectedPeriod, portalOrgUnit );
                        if( dataSetLock != null )
                        {
                            message += "<br><font color=red><strong>Unable to Import : Corresponding Dataset ( "+dataSet.getName()+" ) for " + portalOrgUnit.getName() + " and for period : " + periodFormat.format( selectedPeriod.getStartDate() ) + " is locked.</strong></font>";
                            System.out.println("Unable to Import : Corresponding Dataset ( "+dataSet.getName()+" ) for " + portalOrgUnit.getName() + " and for period : " + periodFormat.format( selectedPeriod.getStartDate() ) + " is locked.");
                            colCount++;
                            facility = sheet.getCell( colCount, facilityStartRow ).getContents();
                            
                            continue;
                        }

                        int insertFlag = 1;
                        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";

                        for( ExcelImport_DeCode deCode : deCodeList )
                        {
                            String deCodeExpression = deCode.getExpression();
                            if( deCodeExpression != null && !deCodeExpression.trim().equals( "" ) )
                            {
                                Integer deId = Integer.parseInt( deCodeExpression.split( "\\." )[0] );
                                Integer deCOCId = Integer.parseInt( deCodeExpression.split( "\\." )[1] );
                                
                                String dataValue = sheet.getCell( colCount, deCode.getRowno() ).getContents();
                                
                                query = "SELECT value FROM datavalue WHERE dataelementid = " + deId + 
                                            " AND categoryoptioncomboid = " + deCOCId +
                                            " AND periodid = " + selectedPeriod.getId() +
                                            " AND sourceid = " + portalOrgUnit.getId();

                                long t;
                                Date d = new Date();
                                t = d.getTime();
                                java.sql.Date lastUpdatedDate = new java.sql.Date( t );

                                SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
                                if ( sqlResultSet1 != null && sqlResultSet1.next() )
                                {
                                    String updateQuery = "UPDATE datavalue SET value = '" + dataValue + "', storedby = '" + storedBy + "',lastupdated='" + lastUpdatedDate + "' WHERE dataelementid = "+ deId +" AND periodid = " + selectedPeriod.getId() + " AND sourceid = " + portalOrgUnit.getId() + " AND categoryoptioncomboid = "+deCOCId;
                                    jdbcTemplate.update( updateQuery );
                                }
                                else
                                {
                                    if( dataValue != null && !dataValue.trim().equalsIgnoreCase( "" ) )
                                    {
                                        insertQuery += "( "+ deId + ", " + selectedPeriod.getId() + ", "+ portalOrgUnit.getId() +", " + deCOCId + ", '" + dataValue + "', '" + storedBy + "', '" + lastUpdatedDate + "' ), ";
                                        insertFlag = 2;
                                    }
                                }
                            }
                        }
                        
                        if( insertFlag != 1 )
                        {
                            insertQuery = insertQuery.substring( 0, insertQuery.length()-2 );
                            jdbcTemplate.update( insertQuery );
                            System.out.println("Data is uploaded in DHIS for : "+ facility);
                        }
                        message += "<br>Data is uploaded into DHIS for : "+ facility + " and for period : "+ periodFormat.format( selectedPeriod.getStartDate() );
                    }
                    else
                    {
                        System.out.println( facility + " is not a memeber of orgunitgroup : "+ orgUnitGroup.getName() );
                        message += "<br><font color=red>" + facility + " is not a memeber of orgunitgroup : "+ orgUnitGroup.getName() + "</font>";
                    }
                }
                else
                {
                    System.out.println("No Mapping found in DHIS for :"+ facility + " : NULL");
                    message += "<br><font color=red>No Mapping found in DHIS for : "+ facility + "</font>";
                }
                
                colCount++;
                facility = sheet.getCell( colCount, facilityStartRow ).getContents();
                
            }
            
            
        }
        
    }
*/

    public void setTextFormatForExcelShett( WritableWorkbook excelImportFile )
    {
        WritableSheet sheet = excelImportFile.getSheet( 0 );
        int facilityStartRow = Integer.parseInt( facilityStart.split( "," )[0] );
        int facilityStartCol = Integer.parseInt( facilityStart.split( "," )[1] );

        WritableCellFormat wCellformat = new WritableCellFormat (sheet.getCell( facilityStartCol, facilityStartRow ).getCellFormat() );
        int rowEnd = sheet.getRows();
        int colEnd = sheet.getColumns();
        
        for ( int c = 0; c <= colEnd; c++ )
        {
            for ( int r = 0; r <= rowEnd; r++ )
            {
                sheet.getWritableCell( c, r ).setCellFormat( wCellformat );
            }
        }
    }
    
    public Integer getOrgUnitIdByComment( String comment, List<Integer> orgUnitIds )
    {
        String query = "SELECT organisationunitid FROM organisationunit WHERE comment LIKE '"+ comment +"'";
        SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
        
        while ( sqlResultSet.next() )
        {
            Integer orgUnitId = sqlResultSet.getInt( 1 );
            if( orgUnitIds.contains( orgUnitId ) )
            {
                return orgUnitId;
            }
        }
			
        return null;
    }

    public Integer getOrgUnitIdByURL( String url )
    {
        url = url.replace("'","\\'");
	System.out.println(url);
        String query = "SELECT organisationunitid FROM organisationunit WHERE url LIKE '"+ url +"'";
        SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
        if ( sqlResultSet != null && sqlResultSet.next() )
        {
            return sqlResultSet.getInt( 1 );
        }
        return null;
    }

    public Integer getOrgUnitIdByComment( String comment )
    {
        String query = "SELECT organisationunitid FROM organisationunit WHERE shortname LIKE '"+ comment +"'";
        SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
        if ( sqlResultSet != null && sqlResultSet.next() )
        {
            return sqlResultSet.getInt( 1 );
        }
        return null;
    }
    
    public boolean validateReport( Workbook excelImportFile, Workbook excelTemplateFile )
    {
        boolean validator = true;

        int sheetNumber = 0;
        
        String headerParts[] = checkerRangeForHeader.split("-");
        int headerStartRow = Integer.parseInt( headerParts[0].split( "," )[0] );
        int headerEndRow = Integer.parseInt( headerParts[1].split( "," )[0] );;
        int headerStartCol = Integer.parseInt( headerParts[0].split( "," )[1] );;
        int headerEndCol = Integer.parseInt( headerParts[1].split( "," )[1] );;

        String dataParts[] = checkerRangeForData.split("-");
        int dataStartRow = Integer.parseInt( dataParts[0].split( "," )[0] );
        int dataEndRow = Integer.parseInt( dataParts[1].split( "," )[0] );;
        int dataStartCol = Integer.parseInt( dataParts[0].split( "," )[1] );;
        int dataEndCol = Integer.parseInt( dataParts[1].split( "," )[1] );;

        Sheet importFileSheet = excelImportFile.getSheet( sheetNumber );
        Sheet templateFileSheet = excelTemplateFile.getSheet( sheetNumber );

        if ( excelImportFile.getSheet( sheetNumber ).getRows() == excelTemplateFile.getSheet( sheetNumber ).getRows() )
        {
            // Checking Header Cells
            for ( int c = headerStartCol; c <= headerEndCol; c++ )
            {
                for ( int r = headerStartRow; r <= headerEndRow; r++ )
                {
                    String cellContent = importFileSheet.getCell( c, r ).getContents();
                    String templateContent = templateFileSheet.getCell( c, r ).getContents();

                    if ( templateContent.equalsIgnoreCase( cellContent ) && cellContent.equalsIgnoreCase( templateContent ) )
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

            // Checking Data Cells
            for ( int c = dataStartCol; c <= dataEndCol; c++ )
            {
                for ( int r = dataStartRow; r <= dataEndRow; r++ )
                {
                    String cellContent = importFileSheet.getCell( c, r ).getContents();
                    String templateContent = templateFileSheet.getCell( c, r ).getContents();

                    if ( templateContent.equalsIgnoreCase( cellContent ) && cellContent.equalsIgnoreCase( templateContent ) )
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

        return validator;
    }
    
    
    public Period getSelectedPeriod( String startDate, PeriodType periodType ) throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        List<Period> periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        for ( Period period : periods )
        {
            String tempDate = dateFormat.format( period.getStartDate() );
            if ( tempDate.equalsIgnoreCase( startDate ) )
            {
                return period;
            }
        }

        Period period = periodType.createPeriod( dateFormat.parse( startDate ) );
        period = reloadPeriodForceAdd( period );
        periodService.addPeriod( period );
    
        return period;
    }
    
    private final Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

}
