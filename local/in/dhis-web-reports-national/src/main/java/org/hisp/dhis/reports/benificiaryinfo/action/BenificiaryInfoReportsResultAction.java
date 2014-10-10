package org.hisp.dhis.reports.benificiaryinfo.action;

//@23-06-2010 - Date from and to is solved.
//Todo merging of cells for same village

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.reports.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class BenificiaryInfoReportsResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
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

    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Patient, Set<ProgramStageInstance>> visitsByPatients = new HashMap<Patient, Set<ProgramStageInstance>>();

    public Map<Patient, Set<ProgramStageInstance>> getVisitsByPatients()
    {
        return visitsByPatients;
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

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
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

    private int periodList;

    public int getPeriodList()
    {
        return periodList;
    }

    public void setPeriodList( int periodList )
    {
        this.periodList = periodList;
    }

    private Boolean includePeriod;

    public void setIncludePeriod( Boolean includePeriod )
    {
        this.includePeriod = includePeriod;
    }

    private List<String> serviceType;

    private List<String> deCodeType;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private String raFolderName;

    private String inputTemplatePath;

    private String outputReportPath;

    private String deCodesXMLFileName;

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

    private Date sDate;

    private Date eDate;

    private int rowCount;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        statementManager.initialise();
        raFolderName = reportService.getRAFolderName();

        // Initialization
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        deCodesXMLFileName = reportList + "DECodes.xml";

        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        if ( includePeriod != null )
        {
            Calendar c = Calendar.getInstance();
            c.setTime( format.parseDate( startDate ) );
            c.add( Calendar.DATE, -1 ); // number of days to add
            startDate = format.formatDate( c.getTime() ); 
            
            c.setTime( format.parseDate( endDate ) );
            c.add( Calendar.DATE, 1 ); // number of days to add
            endDate = format.formatDate( c.getTime() ); 
            
            sDate = format.parseDate( startDate );
            eDate = format.parseDate( endDate );
        }
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template"
            + File.separator + reportFileNameTB;
        //outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output"  + File.separator + UUID.randomUUID().toString() + ".xls";
        
        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        generatFeedbackReport();
        statementManager.destroy();

        return SUCCESS;
    }

    public void generatFeedbackReport()
        throws Exception
    {
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

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
        deWCellformat.setBackground( Colour.GREY_40_PERCENT );

        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        Collection<OrganisationUnit> ouList = new ArrayList<OrganisationUnit>();

        // Getting Programs
        rowCount = 0;
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        Program curProgram = programService.getProgram( Integer.parseInt( reportLevelTB ) );

        String tempStr = "";

        Map<OrganisationUnit, Integer> ouAndLevel = new HashMap<OrganisationUnit, Integer>();
        if ( curProgram != null )
        {
            WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );

            int count1 = 0;
            int rowStart = 0;

            Map<String, List<Patient>> ouPatientList = new HashMap<String, List<Patient>>();
            Map<Patient, ProgramInstance> patientPIList = new HashMap<Patient, ProgramInstance>();

            orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
            List<Integer> levelsList = new ArrayList<Integer>();
            
            for ( OrganisationUnit ou : orgUnitList )
            {
                //int level = organisationUnitService.getLevelOfOrganisationUnit( ou );
                int level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
                ouAndLevel.put( ou, level );
                if ( !levelsList.contains( level ) )
                {
                    levelsList.add( level );
                }

                List<Patient> patientListByOuProgram = new ArrayList<Patient>();
                List<Patient> patientListByOu = new ArrayList<Patient>();
                //patientListByOu.addAll( patientService.getPatients( ou ) );
                patientListByOu.addAll( patientService.getPatients( ou, null, null ) );
                Iterator<Patient> patientIterator = patientListByOu.iterator();
                while ( patientIterator.hasNext() )
                {
                    Patient patient = patientIterator.next();
                    Set<Program> patientProgramList = patient.getPrograms();
                    Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();
                    programInstances = programInstanceService.getProgramInstances( patient, curProgram );

                    for ( ProgramInstance pi : programInstances )
                    {
                        if ( patientProgramList != null )
                        {
                            if ( includePeriod != null )
                            {
                                if ( patientProgramList.contains( curProgram ) && pi.getEnrollmentDate().after( sDate )
                                    && pi.getEnrollmentDate().before( eDate ) )
                                {
                                    patientListByOuProgram.add( patient );
                                    patientPIList.put( patient, pi );
                                }
                            }
                            else
                            {
                                if ( patientProgramList.contains( curProgram ) )
                                {
                                    patientListByOuProgram.add( patient );
                                    patientPIList.put( patient, pi );
                                }
                            }
                        }
                    }
                }

                if ( patientListByOuProgram.size() > 0 )
                {
                    ouList.add( ou );
                    ouPatientList.put( ou.getName(), patientListByOuProgram );
                }
            }

            for ( String deCodeString : deCodesList )
            {
                String sType = (String) serviceType.get( count1 );
                if ( sType.equalsIgnoreCase( "rowStart" ) )
                {
                    rowStart = Integer.parseInt( deCodeString );
                }
                count1++;
            }

            int lastColNo = colList.get( colList.size() - 1 );
            for ( int i = levelsList.size() - 1; i >= 0; i-- )
            {
                int level = levelsList.get( i );
                lastColNo++;
                sheet0.addCell( new Label( lastColNo, rowStart - 1, organisationUnitService
                    .getOrganisationUnitLevelByLevel( level ).getName(), deWCellformat ) );
            }

            for ( OrganisationUnit ou : ouList )
            {
                List<Patient> patientsList = ouPatientList.get( ou.getName() );
                for ( Patient patient : patientsList )
                {
                    int colNo = 0;
                    int rowNo = rowStart + rowCount;
                    count1 = 0;

                    for ( String deCodeString : deCodesList )
                    {
                        tempStr = "";
                        String sType = (String) serviceType.get( count1 );

                        if ( sType.equalsIgnoreCase( "caseProperty" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "Name" ) )
                            {
                                tempStr = patient.getFullName();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "IncidentDate" ) )
                            {
                                Date dateOfIncident = patientPIList.get( patient ).getDateOfIncident();
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
                                tempStr = simpleDateFormat1.format( dateOfIncident );
                            }
                            else if ( deCodeString.equalsIgnoreCase( "RegistrationDate" ) )
                            {
                                Date enrollmentDate = patientPIList.get( patient ).getEnrollmentDate();
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
                                tempStr = simpleDateFormat1.format( enrollmentDate );
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Age" ) )
                            {
                                tempStr = patient.getAge();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Sex" ) )
                            {
                                tempStr = patient.getGender();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Active" ) )
                            {
                                if ( patientPIList.get( patient ).getEndDate() != null
                                    || patientPIList.get( patient ).isCompleted() )
                                {
                                    tempStr = "NO";
                                }
                                else
                                {
                                    tempStr = "YES";
                                }
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "caseAttribute" ) )
                        {
                            int deCodeInt = Integer.parseInt( deCodeString );

                            PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                            PatientAttributeValue patientAttributeValue = patientAttributeValueService
                                .getPatientAttributeValue( patient, patientAttribute );
                            if ( patientAttributeValue != null )
                            {
                                tempStr = patientAttributeValue.getValue();
                            }
                            else
                            {
                                tempStr = " ";
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "identifiertype" ) )
                        {
                            int deCodeInt = Integer.parseInt( deCodeString );
                            PatientIdentifierType patientIdentifierType = patientIdentifierTypeService
                                .getPatientIdentifierType( deCodeInt );
                            if ( patientIdentifierType != null )
                            {
                                PatientIdentifier patientIdentifier = patientIdentifierService.getPatientIdentifier(
                                    patientIdentifierType, patient );
                                if ( patientIdentifier != null )
                                {
                                    tempStr = patientIdentifier.getIdentifier();
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "srno" ) )
                        {
                            int tempNum = 1 + rowCount;
                            tempStr = String.valueOf( tempNum );
                        }
                        if ( !sType.equalsIgnoreCase( "rowStart" ) && !sType.equalsIgnoreCase( "reportProperty" ) )
                        {
                            int tempColNo = colList.get( count1 );
                            int sheetNo = sheetList.get( count1 );
                            sheet0 = outputReportWorkbook.getSheet( sheetNo );
                            WritableCell cell = sheet0.getWritableCell( tempColNo, rowNo );
                            sheet0.addCell( new Label( tempColNo, rowNo, tempStr, wCellformat ) );
                            colNo = tempColNo;
                        }
                        count1++;
                    }// end of decodelist for loop

                    OrganisationUnit ouname = ou;
                    for ( int i = levelsList.size() - 1; i >= 0; i-- )
                    {
                        colNo++;
                        int level = organisationUnitService.getLevelOfOrganisationUnit( ouname.getId() );
                        //int level = organisationUnitService.getLevelOfOrganisationUnit( ouname );
                        if ( levelsList.get( i ) == level )
                        {
                            sheet0.addCell( new Label( colNo, rowNo, ouname.getName(), wCellformat ) );
                        }
                        ouname = ouname.getParent();
                    }
                    // </editor-fold>
                    rowCount++;
                    rowNo++;
                }// end of patientlist
            }// end of oulist
        }// end of if program not null loop

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";

        File outputReportFile = new File( outputReportPath );

        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    /*
     * Returns a list which contains the DataElementCodes
     */

    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );

        for ( OrganisationUnit child : children )
        {
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end


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
    }

    public Period getPreviousPeriod( Date sDate )
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

        PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

        return period;
    }

    public Period getNextPeriod( Date sDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.DECEMBER )
        {
            tempDate.set( Calendar.MONTH, Calendar.JANUARY );
            tempDate.roll( Calendar.YEAR, +1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, +1 );
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
        else
        {
            if ( periodType.getName().equals( "Yearly" ) )
            {
                cal.set( year, Calendar.DECEMBER, 31 );
            }
        }
        Date lastDay = new Date( cal.getTimeInMillis() );
        Period newPeriod = new Period();
        newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );
        return newPeriod;
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
}
