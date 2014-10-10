package org.hisp.dhis.reports.portal.action;

//@23-06-2010 - Date from and to is solved.
//@23-06-2010 - for doses checking if dose is given before enddate then show it
//@23-06-2010 - formating done as per eclipse
//@23-06-2010 - for tt dates checking if execution date is before end date
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
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
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
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.reports.util.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class PortalReportsResultAction implements Action
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

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
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

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format ) {
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
    private List<String> serviceType;
    private List<String> deCodeType;
    private List<Integer> sheetList;
    private List<Integer> rowList;
    private List<Integer> colList;
    private List<Integer> progList;
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

    private String includePeriod;

    public void setIncludePeriod( String includePeriod )
    {
        this.includePeriod = includePeriod;
    }

    private String startHour;

    public void setStartHour( String startHour )
    {
        this.startHour = startHour;
    }

    private String startMinute;

    public void setStartMinute( String startMinute )
    {
        this.startMinute = startMinute;
    }

    private String endHour;
    
    public void setEndHour( String endHour )
    {
        this.endHour = endHour;
    }

    private String endMinute;

    public void setEndMinute( String endMinute )
    {
        this.endMinute = endMinute;
    }

    private int rowCount;
    private Date sDate;
    private Date eDate;

    private long sRegDate;
    private long eRegDate;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
            throws Exception 
    {
        statementManager.initialise();
        raFolderName = reportService.getRAFolderName();

        // Initialization
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        deCodesXMLFileName = reportList + "DECodes.xml";
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        progList = new ArrayList<Integer>();
        if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
        {
            Calendar c = Calendar.getInstance();
            c.setTime( format.parseDate( startDate ) );
            c.add( Calendar.DATE, -1 );  // number of days to add
            startDate = format.formatDate( c.getTime() );  // dt is now the new date
            c.setTime( format.parseDate( endDate ) );
            c.add( Calendar.DATE, 1 );  // number of days to add
            endDate = format.formatDate( c.getTime() );  // dt is now the new date
            sDate = format.parseDate( startDate );
            eDate = format.parseDate( endDate );
        }
        else if ( includePeriod.equalsIgnoreCase( "uploadingmcts" ) )
        {
            Calendar c = Calendar.getInstance();
            c.setTime( format.parseDate( startDate ) );
            c.set( Calendar.HOUR, Integer.parseInt( startHour ) );
            c.set( Calendar.MINUTE, Integer.parseInt( startMinute )  );
            c.set( Calendar.SECOND, 0 );
            c.set( Calendar.MILLISECOND, 0 );
            
            //String tempDate = format.formatDate( c.getTime() );;
            sRegDate = c.getTimeInMillis();
            
            c.setTime( format.parseDate( endDate ) );
            c.set( Calendar.HOUR, Integer.parseInt( endHour ) );
            c.set( Calendar.MINUTE, Integer.parseInt( endMinute )  );             
            eRegDate = c.getTimeInMillis();
        }
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        //outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        System.out.println( inputTemplatePath );

        generatPortalReport();
        
        statementManager.destroy();

        return SUCCESS;
    }

    
    public void generatPortalReport() throws Exception
    {
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        if( templateWorkbook == null )
        {
            System.out.println( "template work book is null");
        }
        else
        {
            System.out.println( outputReportPath );
        }

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

        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        // Getting Programs
        rowCount = 0;
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        String[] programIds = reportLevelTB.split( "," );

        String tempStr = "";
        String dataelementWithStage = "";
        Collection<ProgramStage> programStagesList = new ArrayList<ProgramStage>();
        if ( programIds.length != 0 )
        {
            for ( int pn = 0; pn < programIds.length; pn++ )
            {
                Program curProgram = programService.getProgram( Integer.parseInt( programIds[pn] ) );

                if ( curProgram != null )
                {
                    int count1 = 0;

                    Map<String, String> childPhoneNo = new HashMap<String, String>();
                    childPhoneNo.put( "Others", "Immediate Relations" );
                    childPhoneNo.put( "Neighbor", "Neighbor" );
                    childPhoneNo.put( "Mother", "Parents" );
                    childPhoneNo.put( "Father", "Parents" );
                    childPhoneNo.put( "Husband", "Immediate Relations" );

                    Map<String, String> dhisPortalMap = new HashMap<String, String>();

                    //phone no of whom
                    dhisPortalMap.put( "Others", "Others" );
                    dhisPortalMap.put( "Neighbor", "Neighbor" );
                    dhisPortalMap.put( "Mother", "Relative" );
                    dhisPortalMap.put( "Father", "Relative" );
                    dhisPortalMap.put( "Husband", "Relative" );
                    dhisPortalMap.put( "Self", "Self" );

                    //putting jsy beneficiary / rti/sti / complication / pnc checkup / breast feeded in map
                    dhisPortalMap.put( "true", "Yes" );
                    dhisPortalMap.put( "false", "No" );

                    //putting linked facility / place of delivery
                    dhisPortalMap.put( "(Sub Centre)", "Sub-center" );
                    dhisPortalMap.put( "(PHC)", "PHC" );
                    dhisPortalMap.put( "(CHC)", "CH" );
                    dhisPortalMap.put( "(SDH)", "SDH" );
                    dhisPortalMap.put( "(DH)", "DH" );
                    //anemia
                    dhisPortalMap.put( "(Normal)", "Normal" );
                    dhisPortalMap.put( "(Moderate <11)", "( Moderate<11" );
                    dhisPortalMap.put( "(Severe <7)", "Severe<7" );
                    //anc Complication
                    dhisPortalMap.put( "(ANC None)", "None" );
                    dhisPortalMap.put( "(Hypertensive)", "Hypertensive" );
                    dhisPortalMap.put( "(Diabetics)", "Diabetics" );
                    dhisPortalMap.put( "(APH)", "APH" );
                    dhisPortalMap.put( "(Malaria)", "Malaria" );
                    //place of delivery home type
                    dhisPortalMap.put( "(Home non SBA)", "Non SBA" );
                    dhisPortalMap.put( "(Home SBA)", "SBA" );
                    //place of delivery public
                    dhisPortalMap.put( "(Sub Centre)", "Sub Centre" );
                    dhisPortalMap.put( "(PHC.)", "PHC" );
                    dhisPortalMap.put( "(CHC.)", "CH" );
                    dhisPortalMap.put( "(SDH.)", "SDH" );
                    dhisPortalMap.put( "(DH.)", "DH" );
                    //place of delivery private
                    dhisPortalMap.put( "(Private)", "Private" );
                    List<String> podHomeList = new ArrayList<String>();
                    podHomeList.add( "Non SBA" );
                    podHomeList.add( "SBA" );

                    List<String> podPublicList = new ArrayList<String>();
                    podPublicList.add( "Sub Centre" );
                    podPublicList.add( "PHC" );
                    podPublicList.add( "CH" );
                    podPublicList.add( "SDH" );
                    podPublicList.add( "DH" );

                    List<String> podPrivateList = new ArrayList<String>();
                    podPrivateList.add( "Private" );
                    
                    //delivery type
                    dhisPortalMap.put( "(Normal.)", "Normal" );
                    dhisPortalMap.put( "(C Section)", "CS" );
                    dhisPortalMap.put( "(Instrumental)", "Instrumental" );
                    //abortion
                    dhisPortalMap.put( "(MTP < 12 Weeks)", "MTP<12" );
                    dhisPortalMap.put( "(MTP > 12 Weeks)", "MTP>12" );
                    dhisPortalMap.put( "(Spontaneous)", "Spontaneous" );//not thr in excel sheet
                    dhisPortalMap.put( "(None)", "None" );
                    //pnc visit
                    dhisPortalMap.put( "(with in 7 days)", "Within 7 days" );
                    dhisPortalMap.put( "(With in 48 hrs)", "Within 48 hours" );
                    //pnc complications
                    dhisPortalMap.put( "(None.)", "None" );
                    dhisPortalMap.put( "(Sepsis)", "Sepsis" );
                    dhisPortalMap.put( "(PPH)", "PPH" );
                    dhisPortalMap.put( "(Death)", "PPH" );
                    dhisPortalMap.put( "(Others.)", "Others" );
                    //pp contrapception
                    dhisPortalMap.put( "(Other method)", "None" );
                    dhisPortalMap.put( "(Sterilisation)", "Sterilisation" );
                    dhisPortalMap.put( "(IUD)", "IUD" );
                    dhisPortalMap.put( "(Injectibles)", "Injectibles" );
                    //child health
                    //blood group
                    dhisPortalMap.put( "A+", "A+" );
                    dhisPortalMap.put( "A-", "A-" );
                    dhisPortalMap.put( "AB+", "AB+" );
                    dhisPortalMap.put( "AB-", "AB-" );
                    dhisPortalMap.put( "B+", "B+" );
                    dhisPortalMap.put( "B-", "B-" );
                    dhisPortalMap.put( "O+", "O+" );
                    dhisPortalMap.put( "O-", "O-" );
                    //gender
                    dhisPortalMap.put( "M", "Male" );
                    dhisPortalMap.put( "F", "Female" );

                    Map<String, String> mFNameMap = new HashMap<String, String>();
                    mFNameMap.put( "Father", "Father's" );
                    mFNameMap.put( "Mother", "Mother's" );

                    programStagesList = curProgram.getProgramStages();
                    if ( programStagesList == null || programStagesList.isEmpty() )
                    {
                    }

                    List<Patient> patientList = new ArrayList<Patient>();
                    Map<Patient, ProgramInstance> patientPIList = new HashMap<Patient, ProgramInstance>();
                    Map<ProgramInstance, Collection<ProgramStageInstance>> PIPSIList = new HashMap<ProgramInstance, Collection<ProgramStageInstance>>();
                    Map<ProgramInstance, Collection<ProgramStageInstance>> PIAllPSIList = new HashMap<ProgramInstance, Collection<ProgramStageInstance>>();
                    Map<Patient, OrganisationUnit> patientOuList = new HashMap<Patient, OrganisationUnit>();
                    
                    orgUnitList = getChildOrgUnitTree( selectedOrgUnit );

                    for ( OrganisationUnit ou : orgUnitList )
                    {
                        Collection<Patient> patientListByOrgUnit = new ArrayList<Patient>();
                        //patientListByOrgUnit.addAll( patientService.getPatients( ou ) );
                        patientListByOrgUnit.addAll( patientService.getPatients( ou, null, null ) );
                        Iterator<Patient> patientIterator = patientListByOrgUnit.iterator();
                        while ( patientIterator.hasNext() )
                        {
                            Patient patient = patientIterator.next();
                            
                            if( includePeriod.equalsIgnoreCase( "uploadingmcts" ) )
                            {
                                if ( patient.getRegistrationDate() != null )
                                {
                                    Calendar c = Calendar.getInstance();
                                    c.setTime( patient.getRegistrationDate() );
                                    long regTime = c.getTimeInMillis();
                                    if ( regTime >= sRegDate && regTime <= eRegDate )
                                    {
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                            }
                            
                            //checking if patient is enrolled to curprog then adding them in one list
                            Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();
                            programInstances = programInstanceService.getProgramInstances( patient, curProgram );

                            for ( ProgramInstance pi : programInstances )
                            {
                                Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();
                                Collection<ProgramStageInstance> allProgramStageInstances = new ArrayList<ProgramStageInstance>();
                                
                                Iterator<ProgramStage> itr1 = programStagesList.iterator();
                                while ( itr1.hasNext() )
                                {
                                    ProgramStage PSName = ( ProgramStage ) itr1.next();
                                    ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( pi, PSName );
                                    if ( programStageInstance != null )
                                    {
                                        //putting all stageinstances in one list
                                        allProgramStageInstances.add( programStageInstance );

                                        //taking programstageinstace which are between startdate and enddate
                                        if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                        {
                                            if ( programStageInstance.getExecutionDate() != null )
                                            {
                                                if ( programStageInstance.getExecutionDate().after( sDate ) && programStageInstance.getExecutionDate().before( eDate ) )
                                                {
                                                    programStageInstances.add( programStageInstance );
                                                }
                                            }
                                        } 
                                        else
                                        {
                                            programStageInstances.add( programStageInstance );
                                        }
                                    }
                                }
                                if ( pi != null )
                                {
                                    //putting pi and psi together
                                    PIPSIList.put( pi, programStageInstances );
                                    PIAllPSIList.put( pi, allProgramStageInstances );
                                    //putting patient and pi together
                                    patientPIList.put( patient, pi );
                                    patientOuList.put(patient, ou);
                                    patientList.add( patient );

                                }
                            }
                        }
                    }

                    int sheetNo = 0;
                    rowCount = 0;
                    //running patient loop
                    for ( Patient patient : patientList )
                    {
                        if( includePeriod.equalsIgnoreCase( "uploadingmcts" ) )
                        {
                            if ( patient.getRegistrationDate() != null )
                            {
                                Calendar c = Calendar.getInstance();
                                c.setTime( patient.getRegistrationDate() );
                                long regTime = c.getTimeInMillis();
                                if ( regTime >= sRegDate && regTime <= eRegDate )
                                {
                                }
                                else
                                {
                                    continue;
                                }
                            }
                        }

                        ProgramInstance programInstance = patientPIList.get( patient );

                        String cAPhoneNumberName = "";
                        count1 = 0;
                        int rowNo = rowList.get( 1 ) + rowCount;
                        for ( String deCodeString : deCodesList )
                        {
                            int tempColNo = colList.get( count1 );
                            sheetNo = sheetList.get( count1 );
                            tempStr = "";
                            String sType = ( String ) serviceType.get( count1 );
                            if ( progList.get( count1 ) == curProgram.getId() )
                            {
                                if ( sType.equalsIgnoreCase( "srno" ) )
                                {
                                    int tempNum = 1 + rowCount;
                                    tempStr = String.valueOf( tempNum );
                                }
                                if ( !deCodeString.equalsIgnoreCase( "NA" ) )
                                {
                                    if ( sType.equalsIgnoreCase( "identifiertype" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( deCodeInt );
                                        if ( patientIdentifierType != null ) {
                                            PatientIdentifier patientIdentifier = patientIdentifierService.getPatientIdentifier( patientIdentifierType, patient );
                                            if ( patientIdentifier != null ) {
                                                tempStr = patientIdentifier.getIdentifier();
                                            } else {
                                                tempStr = " ";
                                            }
                                        }
                                    } // </editor-fold>
                                    else if ( sType.equalsIgnoreCase( "caseAttribute" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );

                                        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                                        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                                        if ( patientAttributeValue != null ) {
                                            tempStr = patientAttributeValue.getValue();
                                            if ( dhisPortalMap.containsKey( tempStr ) ) {
                                                tempStr = dhisPortalMap.get( tempStr );
                                            }
                                        } else {
                                            tempStr = " ";
                                        }
                                    } // </editor-fold>
                                    else if ( sType.equalsIgnoreCase( "dataelementstage" ) || sType.equalsIgnoreCase( "dataelementstagePODPublic" ) || sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) || sType.equalsIgnoreCase( "dataelementstagePODHome" ) )
                                    {
                                        dataelementWithStage = deCodeString;
                                        String[] deAndPs = dataelementWithStage.split( "\\." );
                                        int psId = Integer.parseInt( deAndPs[0] );
                                        int deId = Integer.parseInt( deAndPs[1] );
                                        DataElement d1e = dataElementService.getDataElement( deId );
                                        ProgramStageInstance pStageInstance = programStageInstanceService.getProgramStageInstance( programInstance, programStageService.getProgramStage( psId ) );
                                        if ( pStageInstance != null && ( PIPSIList.get( programInstance ).contains( pStageInstance ) || programInstance.isCompleted() == false ) ) {
                                            if ( pStageInstance.getExecutionDate() != null ) {
                                                if ( includePeriod.equalsIgnoreCase( "periodincluding" ) ) {
                                                    if ( pStageInstance.getExecutionDate().before( eDate ) ) {
                                                        //PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e, patientOuList.get(patient) );
                                                        PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e );

                                                        if ( patientDataValue1 == null ) {
                                                            tempStr = " ";
                                                        } else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() ) {
                                                            DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue1.getValue() ) );
                                                            String decocName = dataElementCategoryOptionCombo.getName();
                                                            if ( dhisPortalMap.containsKey( decocName ) ) {
                                                                decocName = dhisPortalMap.get( decocName );
                                                                if ( sType.equalsIgnoreCase( "dataelementstagePODPublic" ) ) {
                                                                    if ( podPublicList.contains( decocName ) ) {
                                                                        tempStr = decocName;
                                                                    }
                                                                } else if ( sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) ) {
                                                                    if ( podPrivateList.contains( decocName ) ) {
                                                                        tempStr = decocName;
                                                                    }
                                                                } else if ( sType.equalsIgnoreCase( "dataelementstagePODHome" ) ) {
                                                                    if ( podHomeList.contains( decocName ) ) {
                                                                        tempStr = decocName;
                                                                    }
                                                                } else {
                                                                    tempStr = decocName;
                                                                }
                                                            }
                                                        } else {
                                                            tempStr = patientDataValue1.getValue();
                                                            if ( dhisPortalMap.containsKey( tempStr ) ) {
                                                                tempStr = dhisPortalMap.get( tempStr );
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    //PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e, patientOuList.get(patient) );
                                                    PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e );

                                                    if ( patientDataValue1 == null ) {
                                                        tempStr = " ";
                                                    } else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() ) {
                                                        DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue1.getValue() ) );
                                                        String decocName = dataElementCategoryOptionCombo.getName();
                                                        if ( dhisPortalMap.containsKey( decocName ) ) {
                                                            decocName = dhisPortalMap.get( decocName );
                                                            if ( sType.equalsIgnoreCase( "dataelementstagePODPublic" ) ) {
                                                                if ( podPublicList.contains( decocName ) ) {
                                                                    tempStr = decocName;
                                                                }
                                                            } else if ( sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) ) {
                                                                if ( podPrivateList.contains( decocName ) ) {
                                                                    tempStr = decocName;
                                                                }
                                                            } else if ( sType.equalsIgnoreCase( "dataelementstagePODHome" ) ) {
                                                                if ( podHomeList.contains( decocName ) ) {
                                                                    tempStr = decocName;
                                                                }
                                                            } else {
                                                                tempStr = decocName;
                                                            }
                                                        }
                                                    } else {
                                                        tempStr = patientDataValue1.getValue();
                                                        if ( dhisPortalMap.containsKey( tempStr ) ) {
                                                            tempStr = dhisPortalMap.get( tempStr );
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            tempStr = " ";
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = dataelement">
                                    else if ( sType.equalsIgnoreCase( "dataelement" ) )
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                                        {
                                            tempStr = patientOuList.get(patient).getName();
                                        } 
                                        else
                                        {
                                            int deCodeInt = Integer.parseInt( deCodeString );
                                            //System.out.println("deCode = "+deCodeString);
                                            DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                            Collection<ProgramStageInstance> programStageInstances = PIAllPSIList.get( programInstance );
                                            Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                            while ( itrPSI.hasNext() )
                                            {
                                                ProgramStageInstance programStageInstance = itrPSI.next();

                                                //PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e, patientOuList.get(patient) );
                                                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e );
                                                //System.out.println("psi = "+programStageInstance.getId() + " de = "+d1e + " ou = "+patientOuList.get(patient));
                                                if ( patientDataValue != null )
                                                {
                                                    //System.out.println("tempStr = "+patientDataValue.getValue() + " de = "+d1e.getId());
                                                    if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() )
                                                    {

                                                        DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue.getValue() ) );
                                                        tempStr = dataElementCategoryOptionCombo.getName();
                                                        
                                                        if ( dhisPortalMap.containsKey( tempStr ) )
                                                        {
                                                            tempStr = dhisPortalMap.get( tempStr );
                                                        }
                                                    } 
                                                    else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_DATE ) )
                                                    {
                                                        String str = patientDataValue.getValue();
                                                        if ( str != null )
                                                        {
                                                            SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                                            Date doseDate = simpleLmpDateFormat.parse( str );
                                                            if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                                            {
                                                                if ( doseDate.before( eDate ) )
                                                                {
                                                                    tempStr = simpleLmpDateFormat.format( doseDate );
                                                                } 
                                                                
                                                            } 
                                                            else
                                                            {
                                                                tempStr = simpleLmpDateFormat.format( doseDate );
                                                            }

                                                        } 
                                                        
                                                    } 
                                                    else
                                                    {

                                                        tempStr = patientDataValue.getValue();
                                                        if ( dhisPortalMap.containsKey( tempStr ) )
                                                        {
                                                            tempStr = dhisPortalMap.get( tempStr );
                                                        }

                                                    }
                                                   
                                                } 
                                                else
                                                {
                                                    continue;
                                                }
                                                
                                            }
                                            if(tempStr.trim().equals(""))
                                            {
                                                tempStr = "";
                                            }
                                        }
                                        //
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = caseProperty">
                                    else if ( sType.equalsIgnoreCase( "caseProperty" ) )
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "Name" ) )
                                        {
                                            tempStr = patient.getFullName();
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "DOB" ) )
                                        {
                                            Date patientDate = patient.getBirthDate();
                                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "dd/MM/yyyy" );
                                            tempStr = simpleDateFormat1.format( patientDate );
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "LMP" ) )
                                        {
                                            Date lmpDate = programInstance.getDateOfIncident();
                                            SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                            tempStr = simpleLmpDateFormat.format( lmpDate );
                                        }
                                        else if( deCodeString.equalsIgnoreCase( "PROGRAM_ENROLLMENT" ) )
                                        {
                                            Date enrollmentDate = programInstance.getEnrollmentDate();
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                            tempStr = simpleDateFormat.format( enrollmentDate );
                                        }
                                        else if ( deCodeString.equalsIgnoreCase( "PNCCheck" ) )
                                        {
                                            ProgramStage ps = programStageService.getProgramStage( 7 );
                                            if ( curProgram.getProgramStages().contains( ps ) )
                                            {
                                                ProgramStageInstance psi = programStageInstanceService.getProgramStageInstance( programInstance, ps );
                                                if ( psi.getExecutionDate() != null )
                                                {
                                                    if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                                    {
                                                        if ( psi.getExecutionDate().before( eDate ) )
                                                        {
                                                            tempStr = "Yes";
                                                        }
                                                    } 
                                                    else
                                                    {
                                                        tempStr = "Yes";
                                                    }
                                                } 
                                                else
                                                {
                                                    tempStr = "No";
                                                }
                                            }
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "BloodGroup" ) )
                                        {
                                            /**
                                             * TODO
                                             * BloodGroup is removed from Patient Object, so need to change this accordingly
                                             */
                                            String bloodGroup = ""; 
                                                //patient.getBloodGroup();
                                            if ( !bloodGroup.trim().equalsIgnoreCase( "" ) )
                                            {
                                                if ( dhisPortalMap.containsKey( bloodGroup ) )
                                                {
                                                    tempStr = dhisPortalMap.get( bloodGroup );
                                                }
                                            } 
                                            else
                                            {
                                                tempStr = "N.A";
                                            }
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "MotherId" ) )
                                        {
                                            Patient representative = patient.getRepresentative();
                                            if ( representative != null )
                                            {
                                                String gender = representative.getGender();
                                                if ( gender.equalsIgnoreCase( "F" ) )
                                                {
                                                    tempStr = patientIdentifierService.getPatientIdentifier( representative ).getIdentifier();
                                                }
                                            }
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = dataelementIFADate">
                                    else if ( sType.equalsIgnoreCase( "dataelementIFADate" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                        Collection<ProgramStageInstance> programStageInstances = PIAllPSIList.get( programInstance );
                                        Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                        while ( itrPSI.hasNext() )
                                        {
                                            ProgramStageInstance programStageInstance = itrPSI.next();
                                            //PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e, patientOuList.get(patient) );
                                            PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e );
                                            if ( patientDataValue != null )
                                            {
                                                if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_DATE ) )
                                                {
                                                    String str = patientDataValue.getValue();
                                                    if ( str != null )
                                                    {
                                                        SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                                        Date doseDate = simpleLmpDateFormat.parse( str );
                                                        if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                                        {
                                                            if ( doseDate.before( eDate ) )
                                                            {
                                                                tempStr = simpleLmpDateFormat.format( doseDate );
                                                            } 
                                                            else
                                                            {
                                                                tempStr = "";
                                                            }
                                                        } 
                                                        else
                                                        {
                                                            tempStr = simpleLmpDateFormat.format( doseDate );
                                                        }

                                                    } 
                                                    else
                                                    {
                                                        tempStr = "";
                                                    }
                                                }
                                            } 
                                            else
                                            {
                                                continue;
                                            }
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = dataelementIFA">
                                    else if ( sType.equalsIgnoreCase( "dataelementIFA" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                        Collection<ProgramStageInstance> programStageInstances = PIAllPSIList.get( programInstance );
                                        Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                        int ifaCount = 0;

                                        while ( itrPSI.hasNext() )
                                        {

                                            ProgramStageInstance programStageInstance = itrPSI.next();
                                            //PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( programStageInstance, d1e, patientOuList.get(patient) );
                                            PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( programStageInstance, d1e );
                                            if ( patientDataValue1 != null )
                                            {
                                                ifaCount = Integer.parseInt( patientDataValue1.getValue() ) + ifaCount;
                                                SimpleDateFormat simpleIfaDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                                if ( ifaCount >= 100 )
                                                {
                                                    tempStr = simpleIfaDateFormat.format( programStageInstance.getExecutionDate() );
                                                    break;
                                                }
                                            }
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = caseAttributePN,caseAttributeHusband,caseAttributeMFName">
                                    else if ( sType.equalsIgnoreCase( "caseAttributePN" ) || sType.equalsIgnoreCase( "caseAttributeHusband" ) || sType.equalsIgnoreCase( "caseAttributeMFName" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                                        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                                        //System.out.println("patient "+patient +" pattr = "+patientAttribute.getName());

                                        String name = "";
                                        if ( patientAttributeValue != null )
                                        {
                                            if ( sType.equalsIgnoreCase( "caseAttributePN" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                System.out.println("name = "+name);
                                                if ( curProgram.getId() == 1 && dhisPortalMap.containsKey( name ) )
                                                {
                                                    cAPhoneNumberName = name;
                                                    tempStr = dhisPortalMap.get( name );
                                                }
                                                if ( curProgram.getId() == 2 && childPhoneNo.containsKey( name ) )
                                                {
                                                    cAPhoneNumberName = name;
                                                    tempStr = childPhoneNo.get( name );
                                                }
                                            }
                                            if ( sType.equalsIgnoreCase( "caseAttributeHusband" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                if ( cAPhoneNumberName.equals( "Husband" ) )
                                                {
                                                    tempStr = name;
                                                }
                                            }
                                            if ( sType.equalsIgnoreCase( "caseAttributeMFName" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                if ( cAPhoneNumberName.equals( "Mother" ) || cAPhoneNumberName.equals( "Father" ) )
                                                {
                                                    tempStr = mFNameMap.get( cAPhoneNumberName );
                                                }
                                            }
                                        } 
                                        else
                                        {
                                            tempStr = " ";
                                        }
                                    }
                                    // </editor-fold>
                                }// end of if for excluding NA and program stages
                                sheet = outputReportWorkbook.getSheet( sheetNo );
                                WritableCell cell = sheet.getWritableCell( tempColNo, rowNo );
                                sheet.addCell( new Label( tempColNo, rowNo, tempStr, wCellformat ) );
                                //System.out.println(  "tempColNo = " + tempColNo + " rowNo = " + rowNo + " value = " + tempStr  );
                            }// end of checking program no is same or not
                            count1++;
                        }//end of decodelist for loop
                        // </editor-fold>
                        rowCount++;
                        rowNo++;
                    }//end of patient for loop
                    // </editor-fold>
                }//end of curprogram if loop
                // </editor-fold>
            }//end of for loop for programs
        }//end of programs if   

        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
    }
    
    
    public void generatPortalReport1() throws Exception
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

        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        //Collection<PatientIdentifier> patientIdentifiers = patientIdentifierService.getPatientIdentifiersByOrgUnit(  selectedOrgUnit  );

        
        //patientListByOrgUnit.addAll(  patientService.getPatientsByOrgUnit(  selectedOrgUnit  )  );

        // Getting Programs
        rowCount = 0;
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        String[] programNames = reportLevelTB.split( "," );

        String tempStr = "";
        String dataelementWithStage = "";
        Collection<ProgramStage> programStagesList = new ArrayList<ProgramStage>();
        if ( programNames.length != 0 )
        {
            for ( int pn = 0; pn < programNames.length; pn++ )
            {
                Program curProgram = programService.getProgram( Integer.parseInt( programNames[pn] ) );

                // <editor-fold defaultstate="collapsed" desc="if curprog!=null">
                if ( curProgram != null )
                {
                    int count1 = 0;

                    // <editor-fold defaultstate="collapsed" desc="all maps">
                    Map<String, String> childPhoneNo = new HashMap<String, String>();
                    childPhoneNo.put( "Others", "Immediate Relations" );
                    childPhoneNo.put( "Neighbor", "Neighbor" );
                    childPhoneNo.put( "Mother", "Parents" );
                    childPhoneNo.put( "Father", "Parents" );
                    childPhoneNo.put( "Husband", "Immediate Relations" );

                    Map<String, String> dhisPortalMap = new HashMap<String, String>();

                    //phone no of whom
                    dhisPortalMap.put( "Others", "Others" );
                    dhisPortalMap.put( "Neighbor", "Neighbor" );
                    dhisPortalMap.put( "Mother", "Relative" );
                    dhisPortalMap.put( "Father", "Relative" );
                    dhisPortalMap.put( "Husband", "Relative" );
                    dhisPortalMap.put( "Self", "Self" );

                    //putting jsy beneficiary / rti/sti / complication / pnc checkup / breast feeded in map
                    dhisPortalMap.put( "true", "Yes" );
                    dhisPortalMap.put( "false", "No" );

                    //putting linked facility / place of delivery
                    dhisPortalMap.put( "(Sub Centre)", "Sub-center" );
                    dhisPortalMap.put( "(PHC)", "PHC" );
                    dhisPortalMap.put( "(CHC)", "CH" );
                    dhisPortalMap.put( "(SDH)", "SDH" );
                    dhisPortalMap.put( "(DH)", "DH" );
                    //anemia
                    dhisPortalMap.put( "(Normal)", "Normal" );
                    dhisPortalMap.put( "(Moderate <11)", "( Moderate<11" );
                    dhisPortalMap.put( "(Severe <7)", "Severe<7" );
                    //anc Complication
                    dhisPortalMap.put( "(ANC None)", "None" );
                    dhisPortalMap.put( "(Hypertensive)", "Hypertensive" );
                    dhisPortalMap.put( "(Diabetics)", "Diabetics" );
                    dhisPortalMap.put( "(APH)", "APH" );
                    dhisPortalMap.put( "(Malaria)", "Malaria" );
                    //place of delivery home type
                    dhisPortalMap.put( "(Home non SBA)", "Non SBA" );
                    dhisPortalMap.put( "(Home SBA)", "SBA" );
                    //place of delivery public
                    dhisPortalMap.put( "(Sub Centre)", "Sub Centre" );
                    dhisPortalMap.put( "(PHC.)", "PHC" );
                    dhisPortalMap.put( "(CHC.)", "CH" );
                    dhisPortalMap.put( "(SDH.)", "SDH" );
                    dhisPortalMap.put( "(DH.)", "DH" );
                    //place of delivery private
                    dhisPortalMap.put( "(Private)", "Private" );
                    List<String> podHomeList = new ArrayList<String>();
                    podHomeList.add( "Non SBA" );
                    podHomeList.add( "SBA" );

                    List<String> podPublicList = new ArrayList<String>();
                    podPublicList.add( "Sub Centre" );
                    podPublicList.add( "PHC" );
                    podPublicList.add( "CH" );
                    podPublicList.add( "SDH" );
                    podPublicList.add( "DH" );

                    List<String> podPrivateList = new ArrayList<String>();
                    podPrivateList.add( "Private" );
                    //delivery type

                    dhisPortalMap.put( "(Normal.)", "Normal" );
                    dhisPortalMap.put( "(C Section)", "CS" );
                    dhisPortalMap.put( "(Instrumental)", "Instrumental" );
                    //abortion
                    dhisPortalMap.put( "(MTP < 12 Weeks)", "MTP<12" );
                    dhisPortalMap.put( "(MTP > 12 Weeks)", "MTP>12" );
                    dhisPortalMap.put( "(Spontaneous)", "Spontaneous" );//not thr in excel sheet
                    dhisPortalMap.put( "(None)", "None" );
                    //pnc visit
                    dhisPortalMap.put( "(with in 7 days)", "Within 7 days" );
                    dhisPortalMap.put( "(With in 48 hrs)", "Within 48 hours" );
                    //pnc complications
                    dhisPortalMap.put( "(None.)", "None" );
                    dhisPortalMap.put( "(Sepsis)", "Sepsis" );
                    dhisPortalMap.put( "(PPH)", "PPH" );
                    dhisPortalMap.put( "(Death)", "PPH" );
                    dhisPortalMap.put( "(Others.)", "Others" );
                    //pp contrapception
                    dhisPortalMap.put( "(Other method)", "None" );
                    dhisPortalMap.put( "(Sterilisation)", "Sterilisation" );
                    dhisPortalMap.put( "(IUD)", "IUD" );
                    dhisPortalMap.put( "(Injectibles)", "Injectibles" );
                    //child health
                    //blood group
                    dhisPortalMap.put( "A+", "A+" );
                    dhisPortalMap.put( "A-", "A-" );
                    dhisPortalMap.put( "AB+", "AB+" );
                    dhisPortalMap.put( "AB-", "AB-" );
                    dhisPortalMap.put( "B+", "B+" );
                    dhisPortalMap.put( "B-", "B-" );
                    dhisPortalMap.put( "O+", "O+" );
                    dhisPortalMap.put( "O-", "O-" );
                    //gender
                    dhisPortalMap.put( "M", "Male" );
                    dhisPortalMap.put( "F", "Female" );

                    Map<String, String> mFNameMap = new HashMap<String, String>();
                    mFNameMap.put( "Father", "Father's" );
                    mFNameMap.put( "Mother", "Mother's" );
                    // </editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="Taking programStagesList Checking whether empty">
                    programStagesList = curProgram.getProgramStages();
                    if ( programStagesList == null || programStagesList.isEmpty() )
                    {
                    }
                    // </editor-fold>

                    List<Patient> patientList = new ArrayList<Patient>();
                    Map<Patient, ProgramInstance> patientPIList = new HashMap<Patient, ProgramInstance>();
                    Map<ProgramInstance, Collection<ProgramStageInstance>> PIPSIList = new HashMap<ProgramInstance, Collection<ProgramStageInstance>>();
                    Map<ProgramInstance, Collection<ProgramStageInstance>> PIAllPSIList = new HashMap<ProgramInstance, Collection<ProgramStageInstance>>();
                    Map<Patient, OrganisationUnit> patientOuList = new HashMap<Patient, OrganisationUnit>();
                    orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
                    //System.out.println("curprogram "+curProgram.getName());
                    // <editor-fold defaultstate="collapsed" desc="for loop for Orgunitlist">
                    for ( OrganisationUnit ou : orgUnitList )
                    {
                        Collection<Patient> patientListByOrgUnit = new ArrayList<Patient>();
                        //patientListByOrgUnit.addAll( patientService.getPatients( ou ) );
                        patientListByOrgUnit.addAll( patientService.getPatients( ou, null, null ) );
                        Iterator<Patient> patientIterator = patientListByOrgUnit.iterator();

                        // <editor-fold defaultstate="collapsed" desc="while patientIterator">
                        while ( patientIterator.hasNext() )
                        {
                            Patient patient = patientIterator.next();
                            
                            //checking if patient is enrolled to curprog then adding them in one list
                            Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();
                            programInstances = programInstanceService.getProgramInstances( patient, curProgram );

                            // <editor-fold defaultstate="collapsed" desc="for loop for programInstances">
                            for ( ProgramInstance pi : programInstances )
                            {
                                //System.out.println("_________________________________id = "+patient.getId() + " ou = "+ou.getName());
                                Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();
                                Collection<ProgramStageInstance> allProgramStageInstances = new ArrayList<ProgramStageInstance>();
                                Iterator<ProgramStage> itr1 = programStagesList.iterator();
                                while ( itr1.hasNext() )
                                {
                                    ProgramStage PSName = ( ProgramStage ) itr1.next();
                                    ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( pi, PSName );
                                    if ( programStageInstance != null )
                                    {
                                        //putting all stageinstances in one list
                                        allProgramStageInstances.add( programStageInstance );

                                        //taking programstageinstace wich are between startdate and enddate
                                        if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                        {
                                            if ( programStageInstance.getExecutionDate() != null )
                                            {
                                                if ( programStageInstance.getExecutionDate().after( sDate ) && programStageInstance.getExecutionDate().before( eDate ) )
                                                {
                                                    programStageInstances.add( programStageInstance );
                                                    //System.out.println("srno: "+programStageInstances.size() + " patient: "+patient + " pi "+pi.getId() + " psi "+programStageInstance.getId());
                                                }
                                            }
                                        } 
                                        else
                                        {
                                            programStageInstances.add( programStageInstance );
                                        }
                                    }
                                }
                                if ( pi != null )
                                {
                                    //putting pi and psi together
                                    PIPSIList.put( pi, programStageInstances );
                                    PIAllPSIList.put( pi, allProgramStageInstances );
                                    //System.out.println("allProgramStageInstances size = "+allProgramStageInstances.size()+ " programStageInstances "+programStageInstances.size() + " pi = "+pi.getId() );
                                    //putting patient and pi together
                                    patientPIList.put( patient, pi );
                                    patientOuList.put(patient, ou);
                                    patientList.add( patient );

                                }
                            }
                            // </editor-fold>
                        }
                        // </editor-fold>
                    }
                    // </editor-fold>

                    int sheetNo = 0;
                    rowCount = 0;
                    //running patient loop
                    // <editor-fold defaultstate="collapsed" desc="for loop of PatientList">
                    for ( Patient patient : patientList )
                    {
                        ProgramInstance programInstance = patientPIList.get( patient );

                        String cAPhoneNumberName = "";
                        //System.out.println( "==================== patient = " + patient.getFullName() + " id = " + patient.getId() + " piid = "+ patientPIList.get(patient).getId()+  " psilist size = "+ PIPSIList.get(patientPIList.get(patient)).size() );
                        count1 = 0;
                        int rowNo = rowList.get( 1 ) + rowCount;
                        // <editor-fold defaultstate="collapsed" desc="for loop of deCodesList">
                        for ( String deCodeString : deCodesList )
                        {
                            int tempColNo = colList.get( count1 );
                            sheetNo = sheetList.get( count1 );
                            tempStr = "";
                            String sType = ( String ) serviceType.get( count1 );
                            if ( progList.get( count1 ) == curProgram.getId() )
                            {
                                // <editor-fold defaultstate="collapsed" desc="sType = srno">
                                if ( sType.equalsIgnoreCase( "srno" ) )
                                {
                                    int tempNum = 1 + rowCount;
                                    tempStr = String.valueOf( tempNum );
                                }
                                // </editor-fold>
                                if ( !deCodeString.equalsIgnoreCase( "NA" ) )
                                {
                                    // <editor-fold defaultstate="collapsed" desc="sType = identifiertype">
                                    if ( sType.equalsIgnoreCase( "identifiertype" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        //_______________________Id. no._______________________
                                        PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( deCodeInt );
                                        if ( patientIdentifierType != null ) {
                                            PatientIdentifier patientIdentifier = patientIdentifierService.getPatientIdentifier( patientIdentifierType, patient );
                                            if ( patientIdentifier != null ) {
                                                tempStr = patientIdentifier.getIdentifier();
                                            } else {
                                                tempStr = " ";
                                            }
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = caseAttribute">
                                    else if ( sType.equalsIgnoreCase( "caseAttribute" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );

                                        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                                        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                                        if ( patientAttributeValue != null ) {
                                            tempStr = patientAttributeValue.getValue();
                                            if ( dhisPortalMap.containsKey( tempStr ) ) {
                                                tempStr = dhisPortalMap.get( tempStr );
                                            }
                                        } else {
                                            tempStr = " ";
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = dataelementstage,dataelementstagePODPublic,dataelementstagePODPrivate,dataelementstagePODHome">
                                    else if ( sType.equalsIgnoreCase( "dataelementstage" ) || sType.equalsIgnoreCase( "dataelementstagePODPublic" ) || sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) || sType.equalsIgnoreCase( "dataelementstagePODHome" ) )
                                    {
                                        //_______________________dataelementstage_______________________
                                        dataelementWithStage = deCodeString;
                                        String[] deAndPs = dataelementWithStage.split( "\\." );
                                        int psId = Integer.parseInt( deAndPs[0] );
                                        int deId = Integer.parseInt( deAndPs[1] );
                                        DataElement d1e = dataElementService.getDataElement( deId );
                                        ProgramStageInstance pStageInstance = programStageInstanceService.getProgramStageInstance( programInstance, programStageService.getProgramStage( psId ) );
                                        if ( pStageInstance != null && ( PIPSIList.get( programInstance ).contains( pStageInstance ) || programInstance.isCompleted() == false ) ) {
                                            if ( pStageInstance.getExecutionDate() != null ) {
                                                if ( includePeriod.equalsIgnoreCase( "periodincluding" ) ) {
                                                    if ( pStageInstance.getExecutionDate().before( eDate ) ) {
                                                        //PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e, patientOuList.get(patient) );
                                                        PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e );

                                                        if ( patientDataValue1 == null ) {
                                                            tempStr = " ";
                                                        } else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() ) {
                                                            DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue1.getValue() ) );
                                                            String decocName = dataElementCategoryOptionCombo.getName();
                                                            if ( dhisPortalMap.containsKey( decocName ) ) {
                                                                decocName = dhisPortalMap.get( decocName );
                                                                if ( sType.equalsIgnoreCase( "dataelementstagePODPublic" ) ) {
                                                                    if ( podPublicList.contains( decocName ) ) {
                                                                        tempStr = decocName;
                                                                    }
                                                                } else if ( sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) ) {
                                                                    if ( podPrivateList.contains( decocName ) ) {
                                                                        tempStr = decocName;
                                                                    }
                                                                } else if ( sType.equalsIgnoreCase( "dataelementstagePODHome" ) ) {
                                                                    if ( podHomeList.contains( decocName ) ) {
                                                                        tempStr = decocName;
                                                                    }
                                                                } else {
                                                                    tempStr = decocName;
                                                                }
                                                            }
                                                        } else {
                                                            tempStr = patientDataValue1.getValue();
                                                            if ( dhisPortalMap.containsKey( tempStr ) ) {
                                                                tempStr = dhisPortalMap.get( tempStr );
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    //PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e, patientOuList.get(patient) );
                                                    PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e );
                                                    if ( patientDataValue1 == null ) {
                                                        tempStr = " ";
                                                    } else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() ) {
                                                        DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue1.getValue() ) );
                                                        String decocName = dataElementCategoryOptionCombo.getName();
                                                        if ( dhisPortalMap.containsKey( decocName ) ) {
                                                            decocName = dhisPortalMap.get( decocName );
                                                            if ( sType.equalsIgnoreCase( "dataelementstagePODPublic" ) ) {
                                                                if ( podPublicList.contains( decocName ) ) {
                                                                    tempStr = decocName;
                                                                }
                                                            } else if ( sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) ) {
                                                                if ( podPrivateList.contains( decocName ) ) {
                                                                    tempStr = decocName;
                                                                }
                                                            } else if ( sType.equalsIgnoreCase( "dataelementstagePODHome" ) ) {
                                                                if ( podHomeList.contains( decocName ) ) {
                                                                    tempStr = decocName;
                                                                }
                                                            } else {
                                                                tempStr = decocName;
                                                            }
                                                        }
                                                    } else {
                                                        tempStr = patientDataValue1.getValue();
                                                        if ( dhisPortalMap.containsKey( tempStr ) ) {
                                                            tempStr = dhisPortalMap.get( tempStr );
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            tempStr = " ";
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = dataelement">
                                    else if ( sType.equalsIgnoreCase( "dataelement" ) )
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                                        {
                                            tempStr = patientOuList.get(patient).getName();
                                        } 
                                        else
                                        {
                                            int deCodeInt = Integer.parseInt( deCodeString );
                                            //System.out.println("deCode = "+deCodeString);
                                            DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                            Collection<ProgramStageInstance> programStageInstances = PIAllPSIList.get( programInstance );
                                            Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                            while ( itrPSI.hasNext() )
                                            {
                                                ProgramStageInstance programStageInstance = itrPSI.next();

                                                //PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e, patientOuList.get(patient) );
                                                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e );
                                                //System.out.println("psi = "+programStageInstance.getId() + " de = "+d1e + " ou = "+patientOuList.get(patient));
                                                if ( patientDataValue != null )
                                                {
                                                    //System.out.println("tempStr = "+patientDataValue.getValue() + " de = "+d1e.getId());
                                                    if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() )
                                                    {

                                                        DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue.getValue() ) );
                                                        tempStr = dataElementCategoryOptionCombo.getName();
                                                        
                                                        if ( dhisPortalMap.containsKey( tempStr ) )
                                                        {
                                                            tempStr = dhisPortalMap.get( tempStr );
                                                        }
                                                    } 
                                                    else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_DATE ) )
                                                    {
                                                        String str = patientDataValue.getValue();
                                                        if ( str != null )
                                                        {
                                                            SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                                            Date doseDate = simpleLmpDateFormat.parse( str );
                                                            if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                                            {
                                                                if ( doseDate.before( eDate ) )
                                                                {
                                                                    tempStr = simpleLmpDateFormat.format( doseDate );
                                                                } 
                                                                
                                                            } 
                                                            else
                                                            {
                                                                tempStr = simpleLmpDateFormat.format( doseDate );
                                                            }

                                                        } 
                                                        
                                                    } 
                                                    else
                                                    {

                                                        tempStr = patientDataValue.getValue();
                                                        if ( dhisPortalMap.containsKey( tempStr ) )
                                                        {
                                                            tempStr = dhisPortalMap.get( tempStr );
                                                        }

                                                    }
                                                   
                                                } 
                                                else
                                                {
                                                    continue;
                                                }
                                                
                                            }
                                            if(tempStr.trim().equals(""))
                                            {
                                                tempStr = "";
                                            }
                                        }
                                        //
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = caseProperty">
                                    else if ( sType.equalsIgnoreCase( "caseProperty" ) )
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "Name" ) )
                                        {
                                            tempStr = patient.getFullName();
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "DOB" ) )
                                        {
                                            Date patientDate = patient.getBirthDate();
                                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "dd/MM/yyyy" );
                                            tempStr = simpleDateFormat1.format( patientDate );
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "LMP" ) )
                                        {
                                            Date lmpDate = programInstance.getDateOfIncident();
                                            SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                            tempStr = simpleLmpDateFormat.format( lmpDate );
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "PROGRAM_ENROLLMENT" ) )
                                        {
                                            Date enrollDate = programInstance.getEnrollmentDate();
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                            tempStr = simpleDateFormat.format( enrollDate );
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "PNCCheck" ) )
                                        {
                                            ProgramStage ps = programStageService.getProgramStage( 7 );
                                            if ( curProgram.getProgramStages().contains( ps ) )
                                            {
                                                ProgramStageInstance psi = programStageInstanceService.getProgramStageInstance( programInstance, ps );
                                                if ( psi.getExecutionDate() != null )
                                                {
                                                    if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                                    {
                                                        if ( psi.getExecutionDate().before( eDate ) )
                                                        {
                                                            tempStr = "Yes";
                                                        }
                                                    } 
                                                    else
                                                    {
                                                        tempStr = "Yes";
                                                    }
                                                } 
                                                else
                                                {
                                                    tempStr = "No";
                                                }
                                            }
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "BloodGroup" ) )
                                        {
                                            /**
                                             * TODO
                                             * BloodGroup is removed from Patient Object, so need to change this accordingly
                                             */

                                            String bloodGroup = ""; 
                                                //patient.getBloodGroup();
                                            if ( !bloodGroup.trim().equalsIgnoreCase( "" ) )
                                            {
                                                if ( dhisPortalMap.containsKey( bloodGroup ) )
                                                {
                                                    tempStr = dhisPortalMap.get( bloodGroup );
                                                }
                                            } 
                                            else
                                            {
                                                tempStr = "N.A";
                                            }
                                        } 
                                        else if ( deCodeString.equalsIgnoreCase( "MotherId" ) )
                                        {
                                            Patient representative = patient.getRepresentative();
                                            if ( representative != null )
                                            {
                                                String gender = representative.getGender();
                                                if ( gender.equalsIgnoreCase( "F" ) )
                                                {
                                                    tempStr = patientIdentifierService.getPatientIdentifier( representative ).getIdentifier();
                                                }
                                            }
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = dataelementIFADate">
                                    else if ( sType.equalsIgnoreCase( "dataelementIFADate" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                        Collection<ProgramStageInstance> programStageInstances = PIAllPSIList.get( programInstance );
                                        Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                        while ( itrPSI.hasNext() )
                                        {
                                            ProgramStageInstance programStageInstance = itrPSI.next();
                                           // PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e, patientOuList.get(patient) );
                                            PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e );
                                            if ( patientDataValue != null )
                                            {
                                                if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_DATE ) )
                                                {
                                                    String str = patientDataValue.getValue();
                                                    if ( str != null )
                                                    {
                                                        SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                                        Date doseDate = simpleLmpDateFormat.parse( str );
                                                        if ( includePeriod.equalsIgnoreCase( "periodincluding" ) )
                                                        {
                                                            if ( doseDate.before( eDate ) )
                                                            {
                                                                tempStr = simpleLmpDateFormat.format( doseDate );
                                                            } 
                                                            else
                                                            {
                                                                tempStr = "";
                                                            }
                                                        } 
                                                        else
                                                        {
                                                            tempStr = simpleLmpDateFormat.format( doseDate );
                                                        }

                                                    } 
                                                    else
                                                    {
                                                        tempStr = "";
                                                    }
                                                }
                                            } 
                                            else
                                            {
                                                continue;
                                            }
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = dataelementIFA">
                                    else if ( sType.equalsIgnoreCase( "dataelementIFA" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                        Collection<ProgramStageInstance> programStageInstances = PIAllPSIList.get( programInstance );
                                        Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                        int ifaCount = 0;

                                        while ( itrPSI.hasNext() )
                                        {

                                            ProgramStageInstance programStageInstance = itrPSI.next();
                                           // PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( programStageInstance, d1e, patientOuList.get(patient) );
                                            PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( programStageInstance, d1e );
                                            if ( patientDataValue1 != null )
                                            {
                                                ifaCount = Integer.parseInt( patientDataValue1.getValue() ) + ifaCount;
                                                SimpleDateFormat simpleIfaDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                                                if ( ifaCount >= 100 )
                                                {
                                                    tempStr = simpleIfaDateFormat.format( programStageInstance.getExecutionDate() );
                                                    break;
                                                }
                                            }
                                        }
                                    } // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="sType = caseAttributePN,caseAttributeHusband,caseAttributeMFName">
                                    else if ( sType.equalsIgnoreCase( "caseAttributePN" ) || sType.equalsIgnoreCase( "caseAttributeHusband" ) || sType.equalsIgnoreCase( "caseAttributeMFName" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                                        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                                        //System.out.println("patient "+patient +" pattr = "+patientAttribute.getName());

                                        String name = "";
                                        if ( patientAttributeValue != null )
                                        {
                                            if ( sType.equalsIgnoreCase( "caseAttributePN" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                System.out.println("name = "+name);
                                                if ( curProgram.getId() == 1 && dhisPortalMap.containsKey( name ) )
                                                {
                                                    cAPhoneNumberName = name;
                                                    tempStr = dhisPortalMap.get( name );
                                                }
                                                if ( curProgram.getId() == 2 && childPhoneNo.containsKey( name ) )
                                                {
                                                    cAPhoneNumberName = name;
                                                    tempStr = childPhoneNo.get( name );
                                                }
                                            }
                                            if ( sType.equalsIgnoreCase( "caseAttributeHusband" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                if ( cAPhoneNumberName.equals( "Husband" ) )
                                                {
                                                    tempStr = name;
                                                }
                                            }
                                            if ( sType.equalsIgnoreCase( "caseAttributeMFName" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                if ( cAPhoneNumberName.equals( "Mother" ) || cAPhoneNumberName.equals( "Father" ) )
                                                {
                                                    tempStr = mFNameMap.get( cAPhoneNumberName );
                                                }
                                            }
                                        } 
                                        else
                                        {
                                            tempStr = " ";
                                        }
                                    }
                                    // </editor-fold>
                                }// end of if for excluding NA and program stages
                                sheet = outputReportWorkbook.getSheet( sheetNo );
                                WritableCell cell = sheet.getWritableCell( tempColNo, rowNo );
                                sheet.addCell( new Label( tempColNo, rowNo, tempStr, wCellformat ) );
                                //System.out.println(  "tempColNo = " + tempColNo + " rowNo = " + rowNo + " value = " + tempStr  );
                            }// end of checking program no is same or not
                            count1++;
                        }//end of decodelist for loop
                        // </editor-fold>
                        rowCount++;
                        rowNo++;
                    }//end of patient for loop
                    // </editor-fold>
                }//end of curprogram if loop
                // </editor-fold>
            }//end of for loop for programs
        }//end of programs if   

        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
    }
    // </editor-fold>

    /*
     * Returns a list which contains the DataElementCodes
     */

    // <editor-fold defaultstate="collapsed" desc="getChildOrgUnitTree method">
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = ( OrganisationUnit ) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getDECodes method">
    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getenv( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName + File.separator + fileName;
        try {
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

        try {
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getPreviousPeriod method">
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getNextPeriod method">
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getPeriodByMonth method">
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
        newPeriod =
                periodService.getPeriod( firstDay, lastDay, periodType );
        return newPeriod;
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="getStartingEndingPeriods method">
    public List<Calendar> getStartingEndingPeriods( String deType, Date sDate, Date eDate )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod =
                getPreviousPeriod( sDate );

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
