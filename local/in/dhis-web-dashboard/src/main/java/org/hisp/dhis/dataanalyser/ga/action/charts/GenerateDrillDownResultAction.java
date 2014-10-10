package org.hisp.dhis.dataanalyser.ga.action.charts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.ConfigurationService;
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
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;


/**
 * 
 * @author Administrator
 */
public class GenerateDrillDownResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CaseAggregationConditionService caseAggregationConditionService;
    
    public void setCaseAggregationConditionService( CaseAggregationConditionService caseAggregationConditionService )
    {
        this.caseAggregationConditionService = caseAggregationConditionService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
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

    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    public String selectedValues;

    public String getSelectedValues()
    {
        return selectedValues;
    }

    public void setSelectedValues( String selectedValues )
    {
        this.selectedValues = selectedValues;
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

    public String[] values;

    private String raFolderName;

    private String inputTemplatePath;

    private String outputReportPath;

    private OrganisationUnit selectedOrgUnit;

    private DataElement de;

    private DataElementCategoryOptionCombo coc;

    private List<String> serviceType;

    private List<String> deCodeType;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private String deCodesXMLFileName;

    private String reportFileNameTB;
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private Date tempStartDate;
    
    private Date tempEndDate;
    
    private Collection<Period> periods;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        // Initialization

        statementManager.initialise();

        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        deCodesXMLFileName = "NBITS_DrillDownToCaseBasedDECodes.xml";
        reportFileNameTB = "DrillDownToCaseBased.xls";
        raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();

        values = selectedValues.split( ":" );
        int orgunit = Integer.parseInt( values[0] );
        int deid = Integer.parseInt( values[1] );
        int cocid = Integer.parseInt( values[2] );
        String periodTypeName = values[3];
        tempStartDate = format.parseDate( values[4] );
        tempEndDate   = format.parseDate( values[5] );
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );
        periods = periodService.getPeriodsBetweenDates( periodType, tempStartDate, tempEndDate );
        
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgunit );
        de = dataElementService.getDataElement( deid );
        coc = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( cocid );
        
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template"
            + File.separator + reportFileNameTB;

        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output"
            + File.separator + UUID.randomUUID().toString() + ".xls";

        generatDrillDownReport();

        statementManager.destroy();
        
        return SUCCESS;
    }

    public void generatDrillDownReport()
        throws Exception
    {
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );

        // Cell formatting
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        // taking expression for selected de and decoc
        CaseAggregationCondition caseAggregationCondition = caseAggregationConditionService.getCaseAggregationCondition( de, coc );

        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        List<OrganisationUnit> orgUnitDataList = new ArrayList<OrganisationUnit>();
        orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
        Map<OrganisationUnit, Integer> ouAndLevel = new HashMap<OrganisationUnit, Integer>();

        List<Integer> levelsList = new ArrayList<Integer>();
        Map<OrganisationUnit, List<PatientDataValue>> ouPatientDataValueMap = new HashMap<OrganisationUnit, List<PatientDataValue>>();
        List<DataElement> des = new ArrayList<DataElement>();
        String tempStr = "";
        for ( OrganisationUnit ou : orgUnitList )
        {
            int level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            ouAndLevel.put( ou, level );
            if ( !levelsList.contains( level ) )
            {
                levelsList.add( level );
            }

            List<PatientDataValue> patientDataValues = new ArrayList<PatientDataValue>();
            
            for( Period period : periods )
            {
                patientDataValues.addAll( caseAggregationConditionService.getPatientDataValues( caseAggregationCondition, ou, period ) );
            }
            
            if ( patientDataValues != null )
            {
                ouPatientDataValueMap.put( ou, patientDataValues );
                orgUnitDataList.add( ou );
                for ( PatientDataValue patientDataValue : patientDataValues )
                {
                    if ( !des.contains( patientDataValue.getDataElement() ) )
                    {
                        des.add( patientDataValue.getDataElement() );
                    }
                }
            }
        }

        WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
        WritableCellFormat wCellformat1 = new WritableCellFormat();
        wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat1.setAlignment( Alignment.CENTRE );
        wCellformat1.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat1.setWrap( true );
        wCellformat1.setBackground( Colour.GREY_40_PERCENT );

        int count1 = 0;
        for ( DataElement de : des )
        {
            // DEName
            sheet0.addCell( new Label( 7 + count1, 1, "" + de.getName(), wCellformat1 ) );
            count1++;
        }

        sheet0.addCell( new Label( 7 + count1, 1, "Execution Date", wCellformat1 ) );

        for ( int i = levelsList.size() - 1; i >= 0; i-- )
        {
            int level = levelsList.get( i );
            count1++;
            sheet0.addCell( new Label( 7 + count1, 1, organisationUnitService.getOrganisationUnitLevelByLevel( level )
                .getName(), wCellformat1 ) );
        }

        int rowNo = rowList.get( 0 );
        int srno = 0;
        for ( OrganisationUnit ou : orgUnitDataList )
        {
            List<PatientDataValue> pdvList = ouPatientDataValueMap.get( ou );

            for ( PatientDataValue patientDataValue : pdvList )
            {
                ProgramStageInstance psi = patientDataValue.getProgramStageInstance();
                ProgramInstance pi = psi.getProgramInstance();
                String value = patientDataValue.getValue();
                Date executionDate = psi.getExecutionDate();
                Patient patient = pi.getPatient();
                int colNo = 0;
                int rowCount = 0;
                for ( String deCodeString : deCodesList )
                {
                    tempStr = "";
                    String sType = (String) serviceType.get( rowCount );
                    if ( !deCodeString.equalsIgnoreCase( "NA" ) )
                    {
                        if ( sType.equalsIgnoreCase( "caseProperty" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "Name" ) )
                            {
                                tempStr = patient.getFullName();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Age" ) )
                            {
                                tempStr = patient.getAge();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Sex" ) )
                            {
                                if ( patient.getGender().equalsIgnoreCase( "M" ) )
                                {
                                    tempStr = "Male";
                                }
                                else if ( patient.getGender().equalsIgnoreCase( "F" ) )
                                {
                                    tempStr = "Female";
                                }
                                else
                                {
                                    tempStr = "";
                                }
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "caseAttribute" ) )
                        {
                            int deCodeInt = Integer.parseInt( deCodeString );

                            PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                            PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                            if ( patientAttributeValue != null && patientAttributeValue.getValue() != null )
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

                            PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( deCodeInt );
                            if ( patientIdentifierType != null )
                            {
                                PatientIdentifier patientIdentifier = patientIdentifierService.getPatientIdentifier( patientIdentifierType, patient );
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
                    }
                    else
                    {
                        if ( sType.equalsIgnoreCase( "srno" ) )
                        {
                            int tempNum = 1 + srno;
                            tempStr = String.valueOf( tempNum );
                        }
                    }

                    int tempColNo = colList.get( rowCount );
                    int sheetNo = sheetList.get( rowCount );
                    sheet0 = outputReportWorkbook.getSheet( sheetNo );
                    sheet0.addCell( new Label( tempColNo, rowNo, tempStr, wCellformat ) );
                    colNo = tempColNo;

                    rowCount++;
                }

                int count = 0;
                for ( count = 0; count < des.size(); count++ )
                {
                    colNo++;
                    // DE Value
                    sheet0.addCell( new Label( colNo, rowNo, value, wCellformat ) );

                }
                colNo++;

                // Execution date
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                String eDate = simpleDateFormat.format( executionDate );
                sheet0.addCell( new Label( colNo, rowNo, "" + eDate, wCellformat ) );

                OrganisationUnit ouname = ou;
                for ( int i = levelsList.size() - 1; i >= 0; i-- )
                {
                    colNo++;
                    int level = organisationUnitService.getLevelOfOrganisationUnit( ouname.getId() );
                    if ( levelsList.get( i ) == level )
                    {
                        sheet0.addCell( new Label( colNo, rowNo, ouname.getName(), wCellformat ) );
                    }
                    ouname = ouname.getParent();
                }
                rowNo++;
                srno++;
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
            child = (OrganisationUnit) childIterator.next();
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
                System.out.println( "There is no DECodes related XML file in the user home" );
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
}
