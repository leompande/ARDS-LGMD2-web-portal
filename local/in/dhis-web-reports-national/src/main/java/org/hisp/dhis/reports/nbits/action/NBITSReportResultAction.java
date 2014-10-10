package org.hisp.dhis.reports.nbits.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.comparator.PatientAttributeComparator;
import org.hisp.dhis.patient.comparator.PatientIdentifierTypeComparator;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.comparator.ProgramStageOrderComparator;
import org.hisp.dhis.reports.util.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class NBITSReportResultAction implements Action
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

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private ProgramService programService;
    
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
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
    // Getter & Setter
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

    private int programList;
    
    public void setProgramList( int programList )
    {
        this.programList = programList;
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
    
    private Boolean includePeriod;

    public void setIncludePeriod( Boolean includePeriod )
    {
        this.includePeriod = includePeriod;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        statementManager.initialise();

        Program selProgram = programService.getProgram( programList );
        
        OrganisationUnit selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        System.out.println("NBITS Report_" + selOrgUnit.getName() + "_" + selProgram.getName() + "_StartTime: " + new Date() );

        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ouIDTB )  );

        List<OrganisationUnit> programOrgUnits = new ArrayList<OrganisationUnit>( selProgram.getOrganisationUnits() );
        
        orgUnitList.retainAll( programOrgUnits );
        
        Date sDate = format.parseDate( startDate );
        
        Date eDate = format.parseDate( endDate );

        System.out.println("Start Date" + sDate + "-----"  + "End Date: " + eDate );
        generateReport( selProgram, orgUnitList, sDate, eDate );

        System.out.println("NBITS Report_" + selOrgUnit.getName() + "_" + selProgram.getName() + "_EndTime: " + new Date() );

        statementManager.destroy();
        
        return SUCCESS;
    }
    
    public void generateReport( Program selProgram, List<OrganisationUnit> orgUnitList, Date sDate, Date eDate ) throws Exception
    {
        String raFolderName = reportService.getRAFolderName();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String query = "";
        int rowStart = 3;
        int colStart = 1;
        int rowCount = rowStart;
        int colCount = colStart;
        
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( selProgram.getName(), 0 );

        try
        {
            List<PatientIdentifierType> patientIdentifierTypes = new ArrayList<PatientIdentifierType>( patientIdentifierTypeService.getAllPatientIdentifierTypes() );
            Collections.sort( patientIdentifierTypes, new PatientIdentifierTypeComparator() );
            
            List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
            Collections.sort( patientAttributes, new PatientAttributeComparator() );
            
            List<ProgramStage> programStages = new ArrayList<ProgramStage>( selProgram.getProgramStages() );
            Collections.sort( programStages, new ProgramStageOrderComparator() );
            
            Map<ProgramStage, List<DataElement>> programStageDataElementMap = new HashMap<ProgramStage, List<DataElement>>();
            for( ProgramStage programStage : programStages )
            {
                List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
                
                List<DataElement> dataElements =  new ArrayList<DataElement>();
                for( ProgramStageDataElement programStageDataElement : programStageDataElements )
                {
                    dataElements.add( programStageDataElement.getDataElement() );
                }
                
                Collections.sort( dataElements, new IdentifiableObjectNameComparator() );
                programStageDataElementMap.put( programStage, dataElements );
            }
            
            // Printing Header Information
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "OrgUnit Hierarchy", getCellFormat1() ) );
            colCount++;
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "OrgUnit", getCellFormat1() ) );
            colCount++;
            for( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
            {
                sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
                sheet0.addCell( new Label( colCount, rowCount-1, patientIdentifierType.getName(), getCellFormat1() ) );
                colCount++;
            }
            
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Benificiary ID", getCellFormat1() ) );
            colCount++;
            
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Benificiary Name", getCellFormat1() ) );
            colCount++;
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Gender", getCellFormat1() ) );
            colCount++;
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Age", getCellFormat1() ) );
            colCount++;
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Data of Birth", getCellFormat1() ) );
            colCount++;
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Blood Group", getCellFormat1() ) );
            colCount++;
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Registration Date", getCellFormat1() ) );
            colCount++;
            
            
            for( PatientAttribute patientAttribute : patientAttributes )
            {
                sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
                sheet0.addCell( new Label( colCount, rowCount-1, patientAttribute.getName(), getCellFormat1() ) );
                colCount++;
            }
            
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Incident Date", getCellFormat1() ) );
            colCount++;
            
            sheet0.mergeCells( colCount, rowCount-1, colCount, rowCount );
            sheet0.addCell( new Label( colCount, rowCount-1, "Enrollment Date", getCellFormat1() ) );
            colCount++;
            for( ProgramStage programStage : programStages )
            {
                List<DataElement> dataElementList = new ArrayList<DataElement>( programStageDataElementMap.get( programStage ) );
                sheet0.mergeCells( colCount, rowCount-1, colCount+dataElementList.size()+1, rowCount-1 );
                sheet0.addCell( new Label( colCount, rowCount-1, programStage.getName(), getCellFormat1() ) );
                
                sheet0.addCell( new Label( colCount, rowCount, "Due Date", getCellFormat1() ) );
                colCount++;
                sheet0.addCell( new Label( colCount, rowCount, "Execution Date", getCellFormat1() ) );
                colCount++;

                for( DataElement dataElement : dataElementList )
                {
                    sheet0.addCell( new Label( colCount, rowCount, dataElement.getName() + "--" + dataElement.getType() , getCellFormat1() ) );
                    colCount++;
                    
                }
            }
            
            rowCount++;
            
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                if( sDate != null && eDate != null)
                {
                    query = "SELECT patient.patientid, programinstance.programinstanceid,programinstance.dateofincident,programinstance.enrollmentdate FROM programinstance INNER JOIN patient " +
                            " ON programinstance.patientid = patient.patientid " +
                            " WHERE patient.organisationunitid = "+ orgUnit.getId() +
                            " AND programinstance.programid = "+ selProgram.getId() +
                            " AND patient.registrationdate >= '"+startDate+"'" +
                            " AND patient.registrationdate <= '"+endDate+"' "+
                            " AND enddate IS NULL";
                }
                else
                {
                    query = "SELECT patient.patientid, programinstance.programinstanceid,programinstance.dateofincident,programinstance.enrollmentdate FROM programinstance INNER JOIN patient " +
                            " ON programinstance.patientid = patient.patientid " +
                            " WHERE patient.organisationunitid = "+ orgUnit.getId() +
                            " AND programinstance.programid = "+ selProgram.getId() +
                            " AND enddate IS NULL";
                }

                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
                
                if ( sqlResultSet != null )
                {
                    int count = 1;
                    String orgUnitBranch = "";
                    sqlResultSet.beforeFirst();
                    while ( sqlResultSet.next() )
                    {
                        colCount = colStart;

                        if( orgUnit.getParent() != null )
                        {
                            orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                        }
                        else
                        {
                            orgUnitBranch = " ";
                        }
                        
                        sheet0.addCell( new Label( colCount, rowCount, orgUnitBranch, getCellFormat2() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, orgUnit.getName(), getCellFormat2() ) );
                        colCount++;

                        int patientId = sqlResultSet.getInt( 1 );
                        int programInstanceId = sqlResultSet.getInt( 2 );
                        Date dateOfIncident = sqlResultSet.getDate( 3 );
                        Date dateOfEnrollment = sqlResultSet.getDate( 4 );
                        
                        Patient patient = patientService.getPatient( patientId );
                        
                        //Patient Identifier Details
                        for( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
                        {
                            query = "SELECT identifier from patientidentifier WHERE patientidentifiertypeid = " + patientIdentifierType.getId() + 
                                            " AND patientid = " + patient.getId();
        
                            SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
                            if ( sqlResultSet1 != null && sqlResultSet1.next() )
                            {
                                String value = sqlResultSet1.getString( 1 );
                                if( value != null && !value.trim().equalsIgnoreCase("") )
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, value, getCellFormat2() ) );
                                }
                                else
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat2() ) );
                                }
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat2() ) );
                            }
                            
                            colCount++;
                        }
                        
                        //Patient Properties
                        
                        sheet0.addCell( new Label( colCount, rowCount, patient.getId().toString(), getCellFormat2() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, patient.getFullName(), getCellFormat2() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, patient.getTextGender(), getCellFormat2() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, patient.getAge(), getCellFormat2() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( patient.getBirthDate() ), getCellFormat2() ) );
                        colCount++;
                        /**
                         * TODO
                         * BloodGroup is removed from Patient Object, so need to change this accordingly
                         */
                        sheet0.addCell( new Label( colCount, rowCount, ""/*patient.getBloodGroup()*/, getCellFormat2() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( patient.getRegistrationDate() ), getCellFormat2() ) );
                        colCount++;

                        //Patient Attribute Values
                        for( PatientAttribute patientAttribute : patientAttributes )
                        {
                            query = "SELECT value from patientattributevalue WHERE patientid = " + patient.getId() + 
                                            " AND patientattributeid = " + patientAttribute.getId();
                            
                            SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
                            if ( sqlResultSet1 != null && sqlResultSet1.next() )
                            {
                                String value = sqlResultSet1.getString( 1 );
                                if( value != null && !value.trim().equalsIgnoreCase("") )
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, value, getCellFormat2() ) );
                                }
                                else
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat2() ) );
                                }
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat2() ) );
                            }
                
                            colCount++;
                        }
                        
                        //Program Enrollment Details
                        sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( dateOfIncident ), getCellFormat2() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( dateOfEnrollment ), getCellFormat2() ) );
                        colCount++;
                        
                        //ProgramStage Values
                        for( ProgramStage programStage : programStages )
                        {
                            query = "SELECT programstageinstanceid,duedate,executiondate from programstageinstance " +
                                        " WHERE programinstanceid = " + programInstanceId + 
                                        " AND programstageid = " + programStage.getId();
                    
                            SqlRowSet sqlResultSet2 = jdbcTemplate.queryForRowSet( query );
                            Integer programStageInstanceId = 0;
                            if ( sqlResultSet2 != null && sqlResultSet2.next() )
                            {
                                programStageInstanceId = sqlResultSet2.getInt( 1 );
                                
                                //ProgramStage DueDate and Execution Date
                                Date dueDate = sqlResultSet2.getDate( 2 );
                                Date exeDate = sqlResultSet2.getDate( 3 );
                                
                                if( dueDate != null )
                                {
                                    String dueDateStr = simpleDateFormat.format( dueDate );
                                    sheet0.addCell( new Label( colCount, rowCount, dueDateStr, getCellFormat3() ) );
                                }
                                else
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat3() ) );
                                }
                                colCount++;
                                
                                if( exeDate != null )
                                {
                                    String exeDateStr = simpleDateFormat.format( exeDate );
                                    sheet0.addCell( new Label( colCount, rowCount, exeDateStr, getCellFormat3() ) );
                                }
                                else
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat3() ) );
                                }
                                
                                colCount++;
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat3() ) );
                                colCount++;
                                sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat3() ) );
                                colCount++;
                            }

                            for( DataElement dataElement : programStageDataElementMap.get( programStage ) )
                            {
                                query = "SELECT value from patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId + 
                                                " AND dataelementid = " + dataElement.getId(); 
                                                //" AND organisationunitid = " + orgUnit.getId(); 

                                SqlRowSet sqlResultSet3 = jdbcTemplate.queryForRowSet( query );
                                
                                if ( sqlResultSet3 != null && sqlResultSet3.next() )
                                {
                                    String value = sqlResultSet3.getString( 1 );
                                    
                                    if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_BOOL) )
                                    {
                                        if( value.equalsIgnoreCase("false") )
                                            value = "No";
                                        else
                                            value = "Yes";
                                    }

                                    
                                    if( value != null && !value.trim().equalsIgnoreCase("") )
                                    {
                                        sheet0.addCell( new Label( colCount, rowCount, value, getCellFormat2() ) );
                                    }
                                    else
                                    {
                                        sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat2() ) );
                                    }
                                }
                                else
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, "-", getCellFormat2() ) );
                                }
                                
                                colCount++;
                            }
                        }
                        
                        rowCount++;
                    }
                }
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception: "+e.getMessage() );
            e.printStackTrace();
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = selProgram.getName() + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();

    }
    
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_50 );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.NO_BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat3() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.NO_BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );

        return wCellformat;
    }
    
    private String getOrgunitBranch( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " -> " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
    
    
}
