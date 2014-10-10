package org.hisp.dhis.excelimport.tcs.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.excelimport.util.TCSXMLMap;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class TCSXmlImportResultAction implements Action
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
    
    private String message = "";

    public String getMessage()
    {
        return message;
    }

    private File outputFile;


    private File upload;

    public File getUpload()
    {
        return upload;
    }

    public void setUpload( File upload )
    {
        this.upload = upload;
    }
    
    private String fileName;
    
    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private String raFolderName;

    int insertCount = 0;
    int updateCount = 0;
    int facilityCount = 0;
    int importFacilityCount = 0;
    String missingFacilities = "";

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        statementManager.initialise();
        
        message += "<br><font color=blue>Importing StartTime : " + new Date() + "  - By "+currentUserService.getCurrentUsername() + "</font><br>";
        System.out.println( message );

        raFolderName = reportService.getRAFolderName();

        String outputPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xml";
        
        try
        {        
            outputFile = new File( outputPath );

            StreamUtils.write( upload, outputFile );

            if( importTCSData() !=0 )
            {
                message += "<br>Importing has been done successfully for the file : "+ fileName;
                message += "<br>Total number of Facilities for Importing : "+ facilityCount;
                message += "<br>Total number of Facilities that are Imported : "+ importFacilityCount;
                message += "<br>Total new records that are imported : "+insertCount;
                message += "<br>Total records that are updated : "+updateCount;
                message += "<br>Missing Facilities in DHIS : "+missingFacilities;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red><strong>Some problem occured while Importing the file : "+ fileName + "<br>Error Message: "+e.getMessage()+"</font></strong>";
        }
        
        System.out.println("Importing has been completed which is started by : "+currentUserService.getCurrentUsername() + " at " + new Date() );
        message += "<br><br><font color=blue>Importing EndTime : " + new Date() + "  - By "+currentUserService.getCurrentUsername() + "</font>";

        statementManager.destroy();

        return SUCCESS;
    }
    
    private int importTCSData( ) throws Exception
    {
        //List<TCSXMLMap> dataValueList = new ArrayList<TCSXMLMap>( getDataInfo() );
        List<TCSXMLMap> dataValueList = getDataInfo();
        if( dataValueList == null )
        {
            return 0;
        }
        
        
        Map<String, String> monthMap = new HashMap<String, String>( getPeriodMap() );        
        
        String facilityCode = "0";
        
        String storedBy = currentUserService.getCurrentUsername();
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        long t;
        Date d = new Date();
        t = d.getTime();
        java.sql.Date lastUpdatedDate = new java.sql.Date( t );

        String query = "";
        int insertFlag = 1;
        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
        int count = 1;
        for( TCSXMLMap tcsDataValue : dataValueList )
        {
            Integer deId = Integer.parseInt( tcsDataValue.getDhisDataElement().split( "\\." )[0] );
            Integer deCOCId = Integer.parseInt( tcsDataValue.getDhisDataElement().split( "\\." )[1] );
            
            String orgUnitCode = tcsDataValue.getOrgunitCode();
            Integer orgUnitId = getOrgUnitIdByCode( orgUnitCode );

            if( orgUnitId == null )
            {
                if( !facilityCode.equals( orgUnitCode ) )
                {
                        facilityCode = orgUnitCode;
                    facilityCount++;
                        missingFacilities += orgUnitCode+", ";
                }
                
                continue;
            }

            if( !facilityCode.equals( orgUnitCode ) )
            {
                facilityCode = orgUnitCode;
                facilityCount++;
                importFacilityCount++;
            }

            
            String tcsPeriod = tcsDataValue.getTscPeriod();
            String selMonth = monthMap.get( tcsPeriod.split( "-" )[0] );
            String startDate = tcsPeriod.split( "-" )[1] + "-" + selMonth + "-01";
            Period selectedPeriod = reportService.getSelectedPeriod( startDate, new MonthlyPeriodType() );
            
            String dataValue = tcsDataValue.getDataValue();

            query = "SELECT value FROM datavalue WHERE dataelementid = " + deId + 
                        " AND categoryoptioncomboid = " + deCOCId +
                        " AND periodid = " + selectedPeriod.getId() +
                        " AND sourceid = " + orgUnitId;

            SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
            if ( sqlResultSet1 != null && sqlResultSet1.next() )
            {
                String updateQuery = "UPDATE datavalue SET value = '" + dataValue + "', storedby = '" + storedBy + "',lastupdated='" + lastUpdatedDate + "' WHERE dataelementid = "+ deId +" AND periodid = " + selectedPeriod.getId() + " AND sourceid = " + orgUnitId + " AND categoryoptioncomboid = "+deCOCId;
                jdbcTemplate.update( updateQuery );
                updateCount++;
            }
            else
            {
                if( dataValue != null && !dataValue.trim().equalsIgnoreCase( "" ) )
                {
                    insertQuery += "( "+ deId + ", " + selectedPeriod.getId() + ", "+ orgUnitId +", " + deCOCId + ", '" + dataValue + "', '" + storedBy + "', '" + lastUpdatedDate + "' ), ";
                    insertFlag = 2;
                    insertCount++;
                }
            }

            if( count == 1000 )
            {
                count = 1;

                if( insertFlag != 1 )
                {
                    insertQuery = insertQuery.substring( 0, insertQuery.length()-2 );
                    jdbcTemplate.update( insertQuery );
                }

                insertFlag = 1;
                insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
            }

            count++;
        }
        
        if( insertFlag != 1 )
        {
            insertQuery = insertQuery.substring( 0, insertQuery.length()-2 );
            jdbcTemplate.update( insertQuery );
        }
        
        return 1;
    }

    
    public Integer getOrgUnitIdByCode( String orgUnitCode )
    {
        String query = "SELECT organisationunitid FROM organisationunit WHERE code LIKE '"+ orgUnitCode +"'";
        SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
        if ( sqlResultSet != null && sqlResultSet.next() )
        {
            return sqlResultSet.getInt( 1 );
        }
        return null;
    }

    
    
    public Map<String, String> getPeriodMap()
    {
        Map<String, String> periodMap = new HashMap<String, String>();
        
        String newpath = "";
        try
        {
            newpath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "excelimport" + File.separator + "tcs_dhis_mapping.xml";
        }
        catch ( NullPointerException npe )
        {
            System.out.println("DHIS_HOME is not set");             
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( newpath ) );
            if ( doc == null )
            {
                System.out.println( "There is no MAP XML file in the DHIS2 Home" );
                return null;
            }

            NodeList listOfPeriodMap = doc.getElementsByTagName( "periodmap" );
            int totalPeriodMap = listOfPeriodMap.getLength();

            for( int s = 0; s < totalPeriodMap; s++ )
            {
                Element element = (Element) listOfPeriodMap.item( s );
                String tcsPeriod = element.getAttribute( "tcsperiod" );
                String dhisPeriod = element.getAttribute( "dhisperiod" );

                if( tcsPeriod != null && dhisPeriod != null )
                {
                    periodMap.put( tcsPeriod, dhisPeriod );
                }
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
        
        return periodMap;
    }

    public Map<String, String> getDataElementMap()
    {
        Map<String, String> dataElementMap = new HashMap<String, String>();
        
        String newpath = "";
        try
        {
            newpath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "excelimport" + File.separator + "tcs_dhis_mapping.xml";
        }
        catch ( NullPointerException npe )
        {
            System.out.println("DHIS_HOME is not set");
            message += "<br><font color=red>DHIS_HOME is not set</font>";
            return null;
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( newpath ) );
            if ( doc == null )
            {
                System.out.println( "There is no MAP XML file in the DHIS2 Home" );
                message += "<br><font color=red>TNHMIS - DHIS mapping file is not found</font>";
                return null;
            }

            NodeList listOfDeMap = doc.getElementsByTagName( "demap" );
            int totalDeMap = listOfDeMap.getLength();

            for( int s = 0; s < totalDeMap; s++ )
            {
                Element element = (Element) listOfDeMap.item( s );
                String tcsDe = element.getAttribute( "tcsde" );
                String dhisDe = element.getAttribute( "dhisde" );

                if( tcsDe != null && dhisDe != null )
                {
                    dataElementMap.put( tcsDe, dhisDe );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
            message += "<br><font color=red>TNHMIS - DHIS mapping file is not found</font>";
            return null;
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            message += "<br><font color=red>TNHMIS - DHIS mapping file is not found</font>";
            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            message += "<br><font color=red>TNHMIS - DHIS mapping file is not found</font>";
            return null;
        }
        
        return dataElementMap;
    }

    
    public List<TCSXMLMap> getDataInfo()
    {
        List<TCSXMLMap> dataValueList = new ArrayList<TCSXMLMap>();
        
        //Map<String, String> tcs_dhis_deMap = new HashMap<String, String>( getDataElementMap() );
        Map<String, String> tcs_dhis_deMap = getDataElementMap();
        if( tcs_dhis_deMap == null )
        {
            return null;
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( outputFile );
            if ( doc == null )
            {
                System.out.println( "There is no Data XML file in the DHIS2 Home" );
                message += "<br><font color=red>TNHMIS - DHIS mapping file is not found</font>";
                return null;
            }

            NodeList listOfDataValues = doc.getElementsByTagName( "dataValue" );
            int totalDataValues = listOfDataValues.getLength();
            
            for( int s = 0; s < totalDataValues; s++ )
            {
                Element element = (Element) listOfDataValues.item( s );
                String tcsDataElement = element.getAttribute( "dataElement" );
                String orgunitCode = element.getAttribute( "source" );
                String tscPeriod = element.getAttribute( "period" );
                String dataValue = element.getAttribute( "value" );
                String dhisDataElement = tcs_dhis_deMap.get( tcsDataElement );
                
                if( dhisDataElement != null && dataValue != null && !dataValue.trim().equalsIgnoreCase( "" ) )
                {
                    TCSXMLMap tcsDataValue = new TCSXMLMap( tcsDataElement, dhisDataElement, orgunitCode, tscPeriod, dataValue );
                    dataValueList.add( tcsDataValue );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
            message += "<br><font color=red>"+ fileName +" is not proper format, please generate XML File from TNHMIS and try again</font>";
            return null;
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            message += "<br><font color=red>"+ fileName +" is not proper format, please generate XML File from TNHMIS and try again</font>";
            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            message += "<br><font color=red>"+ fileName +" is not proper format, please generate XML File from TNHMIS and try again</font>";
            return null;
        }
        
        return dataValueList;
    }

}