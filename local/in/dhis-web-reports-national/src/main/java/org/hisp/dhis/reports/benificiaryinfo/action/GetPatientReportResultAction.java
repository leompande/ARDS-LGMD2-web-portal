package org.hisp.dhis.reports.benificiaryinfo.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_inDesign;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class GetPatientReportResultAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ProgramService programService;
    
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
 /*   
    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
 */   
   private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output and its Getter / Setter
    // -------------------------------------------------------------------------
    
 
    private int patientId;

    public void setPatientId( int patientId )
    {
        this.patientId = patientId;
    }

    private int selProgramId;
   
    public void setSelProgramId( int selProgramId )
    {
        this.selProgramId = selProgramId;
    }

    private String excelTemplateName;
    
    public void setExcelTemplateName( String excelTemplateName )
    {
        this.excelTemplateName = excelTemplateName;
    }
    
    private String xmlTemplateName;
    
    public void setXmlTemplateName( String xmlTemplateName )
    {
        this.xmlTemplateName = xmlTemplateName;
    }

    private String inputTemplatePath;
    private String outputReportPath;
    private String raFolderName;
    
    
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    
    private List<String> serviceType;
    private List<String> deCodeType;
    private List<Integer> sheetList;
    private List<Integer> rowList;
    private List<Integer> colList;
    private List<Integer> progList;
    
    private Patient patient;
    private Program program;
    
    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    private Map<String, String> programStageDataElementValueMap = new HashMap<String, String>();
    
    public Map<String, String> getProgramStageDataElementValueMap()
    {
        return programStageDataElementValueMap;
    }
    
    
    private Date executionDate;
    
    private String systemIdentifier;
    
    private Map<Integer, String> identiferMap;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------



    public String execute() throws Exception 
    {
        
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        
        raFolderName = reportService.getRAFolderName();
        
        
        //System.out.println( "PatientId= " + patientId + "----SelProgramId= " + selProgramId );
       // System.out.println( "-----excelTemplateName=" + excelTemplateName + "-----xmlTemplateName=" + xmlTemplateName );
        patient = patientService.getPatient( patientId );
        //OrganisationUnit orgunit = patient.getOrganisationUnit();
        
        program = programService.getProgram( selProgramId );
        
        String reportFileNameTB = excelTemplateName;
       // String deCodesXMLFileName = xmlTemplateName;
        
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
       
        PatientIdentifierType idType = null;
        
       
        
        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                //identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
        }
        
        
        
        /*
        select identifier from patientidentifier where patientid = 122748 and patientidentifiertypeid = 1;


        select patientdatavalue.programstageinstanceid,programstageinstance.programstageid,
        dataelementid,organisationunitid,value from patientdatavalue
        inner join programstageinstance on patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid
       inner join programinstance on programstageinstance.programinstanceid = programinstance.programinstanceid
        where programinstance.patientid = 122748;
       
        select patientdatavalue.programstageinstanceid,programstageinstance.programstageid,programinstance.enrollmentdate,programinstance.programinstanceid,
        dataelementid,organisationunitid,value from patientdatavalue
        inner join programstageinstance on patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid
        inner join programinstance on programstageinstance.programinstanceid = programinstance.programinstanceid
        where programinstance.patientid = 6417;
       
        select patientdatavalue.programstageinstanceid,programstageinstance.programstageid,programinstance.enrollmentdate,programinstance.programinstanceid,
        dataelementid,organisationunitid,value from patientdatavalue
        inner join programstageinstance on patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid
        inner join programinstance on programstageinstance.programinstanceid = programinstance.programinstanceid
        where programinstance.patientid = 6417 and organisationunitid = 13692;
       
       
       
       final query
       
        select patientdatavalue.programstageinstanceid,programstageinstance.programstageid,
        dataelementid,organisationunitid,programstageinstance.executiondate,value from patientdatavalue
        inner join programstageinstance on patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid
        inner join programinstance on programstageinstance.programinstanceid = programinstance.programinstanceid
        where programinstance.patientid = 6417;

       query for ACS Report
       
       SELECT patient.patientid, patient.firstname, patient.gender, patient.birthdate, programstageinstanceid, executiondate,patient.middlename,patient.lastname,patient.registrationdate FROM programstageinstance 
                                        INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid 
                                       INNER JOIN patient on programinstance.patientid = patient.patientid 
                                         WHERE programinstance.programid IN (1) 
                                         AND executiondate >= '2012-03-30'
                                        AND executiondate <= '2012-03-30'
                                         AND patient.organisationunitid = 1 ORDER BY executiondate ;
       
       
        *
        *
        */
        
        String query = "SELECT patientdatavalue.programstageinstanceid,programstageinstance.programstageid,dataelementid,value,programstageinstance.executiondate from patientdatavalue " +
                       " INNER JOIN programstageinstance on patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid " +
                       " INNER JOIN programinstance on programstageinstance.programinstanceid = programinstance.programinstanceid " +
                       " WHERE programinstance.patientid = " + patient.getId() + " ORDER BY executiondate" ;
                       

       
        SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
        
        if ( sqlResultSet != null )
        {
            sqlResultSet.beforeFirst();
            while ( sqlResultSet.next() )
            {
                String programStageDataElement = "";
                
                int programStageInstanceId = sqlResultSet.getInt( 1 );
                int programStageId = sqlResultSet.getInt( 2 );
                int dataElementId = sqlResultSet.getInt( 3 );
                String deValue = sqlResultSet.getString( 4 );
                executionDate = sqlResultSet.getDate( 5 );
                
                programStageDataElement = programStageId + ":" + dataElementId;
                
                programStageDataElementValueMap.put( programStageDataElement, deValue );
                
            }
        }
        
        for ( PatientAttribute patientAttribute : patient.getAttributes() )
        {
            patientAttributeValueMap.put( patientAttribute.getId(), PatientAttributeValue.UNKNOWN );
        }

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute()
                .getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getValue() );
            }
        }
        
        
        generateReport();
        
        //System.out.println( "PatientId=" + patientId + "----SelProgramId= " + selProgramId );
        //System.out.println( "-----excelTemplateName=" + excelTemplateName + "-----xmlTemplateName=" + xmlTemplateName );
        System.out.println( "Report Generation End Time is : \t" + new Date() );
        
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
        
       // SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
        
        String deCodesXMLFileName = xmlTemplateName;
        
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        
        
      //  List<String> deCodesList = getDECodes( deCodesXMLFileName );
        /*
        String query = "SELECT patient.patientid, patient.firstname, patient.gender, patient.birthdate, programstageinstanceid, executiondate FROM programstageinstance " +
                        " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " +
                        " INNER JOIN patient on programinstance.patientid = patient.patientid " +
                        " WHERE programinstance.programid IN ("+ reportLevelTB +") " +
                        " AND executiondate >= '"+startDate+"'" +
                        " AND executiondate <= '"+endDate+"' "+
                        " AND patient.organisationunitid = "+ ouIDTB +" ORDER BY executiondate" ;

       
        SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
        if ( sqlResultSet1 != null )
        {
         */
            int rowNo = 1;
           // sqlResultSet1.beforeFirst();
            /*
            while ( sqlResultSet1.next() )
            {
                int patientId = sqlResultSet1.getInt( 1 );
                String patientName = sqlResultSet1.getString( 2 );
                String patinetGender = sqlResultSet1.getString( 3 );
                Date patientBirthDate = sqlResultSet1.getDate( 4 );
                int programStageInstanceId = sqlResultSet1.getInt( 5 );
                Date executionDate = sqlResultSet1.getDate( 6 );
                
                Patient patient = patientService.getPatient( patientId );
              
                ProgramStageInstance prgStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
               
                */
                List<PatientIdentifier> patientIdentifiers = new ArrayList<PatientIdentifier>( patientIdentifierService.getPatientIdentifiers( patient ) );
                int count1 = 0;
                Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
                while( reportDesignIterator.hasNext() )
                {
                    Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();
                    
                    String deType = report_inDesign.getPtype();
                    String sType = report_inDesign.getStype();
                    String deCodeString = report_inDesign.getExpression();
                    String tempStr = "";
                    
                    //int tempColNo = colList.get( count1 );
                   // int sheetNo = sheetList.get( count1 );
                   // String tempStr = "";
                   // String sType = serviceType.get( count1 );
                   // int tempRowNo = rowList.get( count1 );
                    
                    if( sType.equalsIgnoreCase("slno") )
                    {
                        tempStr = "" + rowNo;
                    }
                    else if( deCodeString.equalsIgnoreCase("GENDER") )
                    {
                        tempStr = patient.getGender();
                    }
                    else if( deCodeString.equalsIgnoreCase("AGE") )
                    {
                        if( patient.getDobType() == 'V' || patient.getDobType() == 'D')
                        {
                            tempStr =  simpleDateFormat.format(patient.getBirthDate());
                        }
                        else
                        {
                            tempStr = patient.getAge();
                          
                            
                        }
                    }
                    
                    /*
                    else if( deCodeString.equalsIgnoreCase("DOB") )
                    {
                        tempStr =  simpleDateFormat.format(patient.getBirthDate());
                    }
                    */
                    
                    else if( deCodeString.equalsIgnoreCase("ADMISSION") )
                    {
                        //tempStr = program.getDateOfEnrollmentDescription();
                        tempStr = simpleDateFormat.format( executionDate );
                    }
                    
                    
                    else if( deCodeString.equalsIgnoreCase("FACILITY") )
                    {
                        tempStr = patient.getOrganisationUnit().getName();
                        //System.out.println( "FACILITY ----" + tempStr);
                    }
                    
                    else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                    {
                        tempStr = patient.getOrganisationUnit().getParent().getName();
                        //System.out.println( "FACILITY - P---" + tempStr);
                    } 
                    else if( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                    {
                        tempStr = patient.getOrganisationUnit().getParent().getParent().getName();
                        //System.out.println( "FACILITY - PP---" + tempStr);
                    } 
                    
                    else if( deCodeString.equalsIgnoreCase("PATIENTNAME") )
                    {
                        tempStr = patient.getFullName();
                    }

                    else if( deCodeString.equalsIgnoreCase("SYSTEMIDENTIFIER") )
                    {
                        tempStr = systemIdentifier;
                    }
                    else if( deCodeString.equalsIgnoreCase("NA") )
                    {
                        tempStr = "";
                    }
                    
                    
                    else if( sType.equalsIgnoreCase("identifier") )
                    {
                        //System.out.println( "Inside pipart0" );
                        tempStr = " ";
                        for( PatientIdentifier patientIdentifier : patientIdentifiers )
                        {
                                //System.out.println( patientIdentifier.getId() + " : " + deCode );
                                if( patientIdentifier.getIdentifierType() != null && patientIdentifier.getIdentifierType().getId() == Integer.parseInt(deCodeString) )
                                {
                                        try
                                        {
                                                tempStr = patientIdentifier.getIdentifier();
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                        break;
                                }
                        }
                    }
                    else if( sType.equalsIgnoreCase("attributes") )
                    {
                        tempStr = " ";
                        for( PatientAttribute patientAttribute : patient.getAttributes() )
                        {
                                if( patientAttribute.getId() == Integer.parseInt(deCodeString) )
                                {
                                        try
                                        {
                                                tempStr = patientAttributeValueMap.get( patientAttribute.getId() );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                        break;
                                }
                        }
                    }
                    /*
                    else if( sType.equalsIgnoreCase("PI-PART6") )
                    {
                        tempStr = " ";
                        for( PatientAttribute patientAttribute : patient.getAttributes() )
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
                        PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de, selectedOrgUnit );
                        if( patientDV != null && patientDV.getValue() != null )
                        {
                                tempStr = patientDV.getValue();
                        }
                    }
                    else if( sType.equalsIgnoreCase("dataelement-dd") )
                    {
                        DataElement de = dataElementService.getDataElement( Integer.parseInt( deCode ) );
                        PatientDataValue patientDV = patientDataValueService.getPatientDataValue( prgStageInstance, de, selectedOrgUnit );
                        if( patientDV != null && patientDV.getValue() != null )
                        {
                                Integer optionComboId = Integer.parseInt( patientDV.getValue() );
                                DataElementCategoryOptionCombo deCOC = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );
                                tempStr = deCOC.getName();
                        }
                    }
                    */
                    else if( sType.equalsIgnoreCase("dataelement") )
                    {
                        tempStr = " ";
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                    }
                    
                    else if( sType.equalsIgnoreCase("dataelementdate") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                        }
                        else
                        {
                            tempStr = " ";
                        }
                        
                    }
                    else if( sType.equalsIgnoreCase("after1stday") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.DATE, 1);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                        
                    }
                    else if( sType.equalsIgnoreCase("after3rdday") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.DATE, 3);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                        
                    }
 
                    else if( sType.equalsIgnoreCase("after7thday") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.DATE, 7);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                        
                    }
                    else if( sType.equalsIgnoreCase("after14thday") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.DATE, 14);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                        
                    }
                    else if( sType.equalsIgnoreCase("after15thday") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.DATE, 15);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                        
                    }
                    
                    else if( sType.equalsIgnoreCase("1stmonth") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString ); 
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.MONTH, 1);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }

                    }
                    
                    else if( sType.equalsIgnoreCase("after45thday") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.DATE, 45);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                    }
                    
                    else if( sType.equalsIgnoreCase("2ndmonth") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.MONTH, 2);
                           // tempStr = "" + tempSDate.get(Calendar.DATE);
                           // tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                    }
                    
                    else if( sType.equalsIgnoreCase("3rdmonth") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.MONTH, 3);
                           // tempStr = "" + tempSDate.get(Calendar.DATE);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                    }
                   
                    else if( sType.equalsIgnoreCase("6thmonth") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.MONTH, 6);
                           // tempStr = "" + tempSDate.get(Calendar.DATE);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" + ( tempSDate.get(Calendar.MONTH) + 1) + "-" +tempSDate.get(Calendar.DATE);
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                    }
                    else if( sType.equalsIgnoreCase("1year") )
                    {
                        tempStr = programStageDataElementValueMap.get( deCodeString );
                        
                        if (  tempStr != null )
                        {
                            Date tempDate = format.parseDate( tempStr );
                            Calendar tempSDate = Calendar.getInstance();
                            tempSDate.setTime( tempDate );
                            tempSDate.add(Calendar.YEAR, 1);
                           // tempStr = "" + tempSDate.get(Calendar.DATE);
                            //tempStr = "" + tempSDate.get(Calendar.YEAR) + "-" +tempSDate.get(Calendar.MONTH) + "-" +tempSDate.get(Calendar.DATE);
                            
                            tempStr =  simpleDateFormat.format( tempSDate.getTime() );
                            
                        }
                        else
                        {
                            tempStr = " ";
                        }
                    }
                    
                    //System.out.println( sType + " : " + tempStr );
                     
                    //System.out.println( sType + " : " + deCodeString + " : " + tempStr );
                    
                    int tempRowNo = report_inDesign.getRowno();
                    int tempColNo = report_inDesign.getColno();
                    int sheetNo = report_inDesign.getSheetno();
                    WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                    if ( tempStr == null || tempStr.equals( " " ) )
                    {
                        
            
                        sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                    } 
                    else
                    {
                    
                        if ( deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || deCodeString.equalsIgnoreCase( "PERIOD-TO" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                        {
                            
                        } 
                        
                        else
                        {   
                           // System.out.println( sType + " : " + deCode + " : " + tempStr );
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
                }
                
                rowNo++;
            //}
        //}
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = excelTemplateName;
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
        
    }

/*    
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
    
*/    
    
    
}
