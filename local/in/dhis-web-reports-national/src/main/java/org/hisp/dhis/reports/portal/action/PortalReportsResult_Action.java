package org.hisp.dhis.reports.portal.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.reports.util.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class PortalReportsResult_Action implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

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
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
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

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }
    
    private String reportLevelTB;

    public void setReportLevelTB( String reportLevelTB )
    {
        this.reportLevelTB = reportLevelTB;
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

    private String inputTemplatePath;
    private String outputReportPath;
    private String raFolderName;
    private List<String> serviceType;
    private List<String> deCodeType;
    private List<Integer> sheetList;
    private List<Integer> rowList;
    private List<Integer> colList;
    private List<Integer> progList;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
            throws Exception 
    {
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        progList = new ArrayList<Integer>();

    	raFolderName = reportService.getRAFolderName();
    	
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        generateReport();
        
        return SUCCESS;
    }
    
    public void generateReport() throws Exception
    {
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        // Cell formatting
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        WritableCellFormat deWCellformat = new WritableCellFormat();
        deWCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        deWCellformat.setAlignment( Alignment.CENTRE );
        deWCellformat.setVerticalAlignment( VerticalAlignment.JUSTIFY );
        deWCellformat.setWrap( true );

        //WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        
        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        // Getting Programs
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        int rowCount = 0;
        String deCodesXMLFileName = reportList + "DECodes.xml";
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        String[] programIds = reportLevelTB.split( "," );
        
        String query = "SELECT patient.patientid, patient.firstname, patient.gender, patient.birthdate, programstageinstanceid, executiondate FROM programstageinstance " +
        				" INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " +
        				" INNER JOIN patient on programinstance.patientid = patient.patientid " +
        				" WHERE programinstance.programid IN ("+ reportLevelTB +") " +
        				" AND executiondate >= '"+startDate+"'" +
        				" AND executiondate <= '"+endDate+"' "+
        				" AND patient.organisationunitid = "+ ouIDTB +" ORDER BY executiondate" ;
        
        //System.out.println( query );
        
        SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
        if ( sqlResultSet1 != null )
        {
            int rowNo = 1;
            sqlResultSet1.beforeFirst();
            while ( sqlResultSet1.next() )
            {
            	int patientId = sqlResultSet1.getInt( 1 );
            	String patientName = sqlResultSet1.getString( 2 );
            	String patinetGender = sqlResultSet1.getString( 3 );
            	Date patientBirthDate = sqlResultSet1.getDate( 4 );
                int programStageInstanceId = sqlResultSet1.getInt( 5 );
                Date executionDate = sqlResultSet1.getDate( 6 );
                
                Patient patient = patientService.getPatient( patientId );
                List<PatientIdentifier> patientIdentifiers = new ArrayList<PatientIdentifier>( patientIdentifierService.getPatientIdentifiers( patient ) );
                ProgramStageInstance prgStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
                int count1 = 0;
                Iterator<String> deCodeIterator = deCodesList.iterator();
                while( deCodeIterator.hasNext() )
                {
                	String deCode = deCodeIterator.next();
                	int tempColNo = colList.get( count1 );
                    int sheetNo = sheetList.get( count1 );
                    String tempStr = "";
                    String sType = serviceType.get( count1 );
                    int tempRowNo = rowList.get( count1 );
                    
                    if( sType.equalsIgnoreCase("slno") )
                    {
                    	tempStr = "" + rowNo;
                    }
                    else if( sType.equalsIgnoreCase("executiondate") )
                    {
                    	tempStr = simpleDateFormat.format( executionDate );
                    }
                    else if( deCode.equalsIgnoreCase("GENDER") )
                    {
                    	tempStr = patinetGender;
                    }
                    else if( deCode.equalsIgnoreCase("AGE") )
                    {
                    	tempStr = getAge( patientBirthDate );
                    }
                    else if( sType.equalsIgnoreCase("PI-PART0") )
                    {
                    	//System.out.println( "Inside pipart0" );
                    	tempStr = " ";
                    	for( PatientIdentifier patientIdentifier : patientIdentifiers )
                    	{
                    		//System.out.println( patientIdentifier.getId() + " : " + deCode );
                    		if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCode) )
                    		{
                    			try
                    			{
                    				tempStr = patientIdentifier.getIdentifier().split("-")[0];
                    			}
                    			catch( Exception e )
                    			{
                    			}
                    			break;
                    		}
                    	}
                    }
                    else if( sType.equalsIgnoreCase("PI-PART1") )
                    {
                    	tempStr = " ";
                    	for( PatientIdentifier patientIdentifier : patientIdentifiers )
                    	{
                    		if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCode) )
                    		{
                    			try
                    			{
                    				tempStr = patientIdentifier.getIdentifier().split("-")[1];
                    			}
                    			catch( Exception e )
                    			{
                    			}
                    			break;
                    		}
                    	}
                    }
                    else if( sType.equalsIgnoreCase("PI-PART2") )
                    {
                    	tempStr = " ";
                    	for( PatientIdentifier patientIdentifier : patientIdentifiers )
                    	{
                    		if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCode) )
                    		{
                    			try
                    			{
                    				tempStr = patientIdentifier.getIdentifier().split("-")[2];
                    			}
                    			catch( Exception e )
                    			{
                    			}
                    			break;
                    		}
                    	}
                    }
                    else if( sType.equalsIgnoreCase("PI-PART3") )
                    {
                    	tempStr = " ";
                    	for( PatientIdentifier patientIdentifier : patientIdentifiers )
                    	{
                    		if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCode) )
                    		{
                    			try
                    			{
                    				tempStr = patientIdentifier.getIdentifier().split("-")[3];
                    			}
                    			catch( Exception e )
                    			{
                    			}
                    			break;
                    		}
                    	}
                    }
                    else if( sType.equalsIgnoreCase("PI-PART4") )
                    {
                    	tempStr = " ";
                    	for( PatientIdentifier patientIdentifier : patientIdentifiers )
                    	{
                    		if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCode) )
                    		{
                    			try
                    			{
                    				tempStr = patientIdentifier.getIdentifier().split("-")[4];
                    			}
                    			catch( Exception e )
                    			{
                    			}
                    			break;
                    		}
                    	}
                    }
                    else if( sType.equalsIgnoreCase("PI-PART5") )
                    {
                    	tempStr = " ";
                    	for( PatientIdentifier patientIdentifier : patientIdentifiers )
                    	{
                    		if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCode) )
                    		{
                    			try
                    			{
                    				tempStr = patientIdentifier.getIdentifier().split("-")[5];
                    			}
                    			catch( Exception e )
                    			{
                    			}
                    			break;
                    		}
                    	}
                    }
                    else if( sType.equalsIgnoreCase("PI-PART6") )
                    {
                    	tempStr = " ";
                    	for( PatientIdentifier patientIdentifier : patientIdentifiers )
                    	{
                    		if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCode) )
                    		{
                    			try
                    			{
                    				tempStr = patientIdentifier.getIdentifier().split("-")[6];
                    			}
                    			catch( Exception e )
                    			{
                    			}
                    			break;
                    		}
                    	}
                    }
                    else if( sType.equalsIgnoreCase("dataelement") )
                    {
                        DataElement de = dataElementService.getDataElement( Integer.parseInt( deCode ) );
                        //PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de, selectedOrgUnit );
                        PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de );
                        if( patientDV != null && patientDV.getValue() != null )
                        {
                        	tempStr = patientDV.getValue();
                        }
                    }
                    else if( sType.equalsIgnoreCase("dataelement-dd") )
                    {
                        DataElement de = dataElementService.getDataElement( Integer.parseInt( deCode ) );
                        //PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de, selectedOrgUnit );
                        PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de );
                        if( patientDV != null && patientDV.getValue() != null )
                        {
                        	Integer optionComboId = Integer.parseInt( patientDV.getValue() );
                        	DataElementCategoryOptionCombo deCOC = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );
                        	tempStr = deCOC.getName();
                        }
                    }
                    else if( sType.equalsIgnoreCase("dataelement-b") )
                    {
                        DataElement de = dataElementService.getDataElement( Integer.parseInt( deCode ) );
                        //PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de, selectedOrgUnit );
                        PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de );
                        if( patientDV != null && patientDV.getValue() != null )
                        {
                        	if( patientDV.getValue().equalsIgnoreCase("false") )
                        		tempStr = "N";
                        	else
                        		tempStr = "Y";
                        }
                    }

                    //System.out.println( sType + " : " + tempStr );
                    	
                    WritableSheet sheet = outputReportWorkbook.getSheet( sheetNo );
                    try
                    {
                    	double tempDouble = Double.parseDouble( tempStr );
                    	sheet.addCell( new Number( tempColNo, tempRowNo+rowNo, tempDouble ) );
                    }
                    catch( Exception e )
                    {
                    	sheet.addCell( new Label( tempColNo, tempRowNo+rowNo, tempStr ) );
                    }
                    
                    count1++;
                }
                
                rowNo++;
            }
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
    }
    
    
    public String getAge( Date birthDate )
    {
        if ( birthDate == null )
        {
            return "0";
        }

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime( birthDate );

        Calendar todayCalendar = Calendar.getInstance();

        int age = todayCalendar.get( Calendar.YEAR ) - birthCalendar.get( Calendar.YEAR );

        if ( todayCalendar.get( Calendar.MONTH ) < birthCalendar.get( Calendar.MONTH ) )
        {
            age--;
        }
        else if ( todayCalendar.get( Calendar.MONTH ) == birthCalendar.get( Calendar.MONTH )
            && todayCalendar.get( Calendar.DAY_OF_MONTH ) < birthCalendar.get( Calendar.DAY_OF_MONTH ) )
        {
            age--;
        }

        if ( age < 1 )
        {
            return "( < 1 yr )";
        }
        else
        {
            return "( " + age + " yr )";
        }
    }
    
    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + fileName;

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
                Element deCodeElement = ( Element ) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                deCodes.add( ( ( Node ) textDECodeList.item( 0 ) ).getNodeValue().trim() );
                serviceType.add( deCodeElement.getAttribute( "stype" ) );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );
                progList.add( new Integer( deCodeElement.getAttribute( "progno" ) ) );
            }// end of for loop with s var

        }// try block end
        catch ( SAXParseException err )
        {
        } 
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ( ( x == null ) ? e : x ).printStackTrace();
        } 
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return deCodes;
    }
}
