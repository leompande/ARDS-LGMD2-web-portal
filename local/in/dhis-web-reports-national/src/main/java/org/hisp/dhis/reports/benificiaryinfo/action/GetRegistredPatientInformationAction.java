package org.hisp.dhis.reports.benificiaryinfo.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.util.ProgramDetail;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class GetRegistredPatientInformationAction extends ActionPagingSupport<Patient>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------


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
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private List<String> searchText = new ArrayList<String>();

    public void setSearchText( List<String> searchText )
    {
        this.searchText = searchText;
    }

    private Boolean listAll;
    
    public void setListAll( Boolean listAll )
    {
        this.listAll = listAll;
    }

    public Boolean getListAll()
    {
        return listAll;
    }
   
    private List<Integer> searchingAttributeId = new ArrayList<Integer>();
    
    public void setSearchingAttributeId( List<Integer> searchingAttributeId )
    {
        this.searchingAttributeId = searchingAttributeId;
    }
    /*
    private List<Program> programs;
    
    public List<Program> getPrograms()
    {
        return programs;
    }
    */
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------



    private Integer total;
    
    public Integer getTotal()
    {
        return total;
    }
    
    private Map<String, String> mapPatientPatientAttr = new HashMap<String, String>();
    
    public Map<String, String> getMapPatientPatientAttr()
    {
        return mapPatientPatientAttr;
    }
    
    private Collection<Patient> patients = new ArrayList<Patient>();
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }
    /*
    private Map<Integer, List<Program>> mapPatientPrograms = new HashMap<Integer, List<Program>>();
    
    public Map<Integer, List<Program>> getMapPatientPrograms()
    {
        return mapPatientPrograms;
    }
    */
    
    
    // -------------------------------------------------------------------------
    // Getters/Setters
    // -------------------------------------------------------------------------


    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
    
    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }
    
    private Map<Integer, String> mapPatientOrgunit = new HashMap<Integer, String>();
    
    public Map<Integer, String> getMapPatientOrgunit()
    {
        return mapPatientOrgunit;
    }
    
    private String raFolderName;
    
    
    private List<ProgramDetail> programList;
    
    public List<ProgramDetail> getProgramList()
    {
        return programList;
    }
    
    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }
    
    private Boolean isSelectedOrg;
    
    public void setIsSelectedOrg( Boolean isSelectedOrg )
    {
        this.isSelectedOrg = isSelectedOrg;
    }
    
    private OrganisationUnit organisationUnit;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        // ---------------------------------------------------------------------
        // Get all of patients into the selected organisation unit
        // ---------------------------------------------------------------------
        /*
        if ( listAll != null && listAll )
        {
            listAllPatient( organisationUnit );

            return SUCCESS;
        }
    */
        // ---------------------------------------------------------------------
        // Search patients by attributes
        // ---------------------------------------------------------------------
        
        //System.out.println( "searchingAttributeId= "  + searchingAttributeId + "---,searchText= " + searchText );
        
        //System.out.println( "OrganisationUnit= "  + ouIDTB + "---,is Selected Org checked = " + isSelectedOrg );
        
        raFolderName = reportService.getRAFolderName();
        
        programList = new ArrayList<ProgramDetail>();
        
        for ( Integer attributeId : searchingAttributeId )
        {
            if ( attributeId != null && attributeId != 0 )
            {
                patientAttributes.add( patientAttributeService.getPatientAttribute( attributeId ) );
            }
        }
        
        
        if( isSelectedOrg )
        {
            searchPatientByNameAndOrgUnit( searchText , organisationUnit );
        }
        else
        {
            searchPatientByAttributes( searchingAttributeId, searchText );
            //searchPatientByNameAndOrgUnit( searchText , organisationUnit );
        }
        
        
        getProgramDetailList();
        
        return SUCCESS;

    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------
/*
    private void listAllPatient( OrganisationUnit organisationUnit )
    {
        total = patientService.countGetPatientsByOrgUnit( organisationUnit );
        this.paging = createPaging( total );

        patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging
            .getPageSize() ) );
    }
*/
    
    private void searchPatientByNameAndOrgUnit( List<String> searchText , OrganisationUnit organisationUnit )
    {
       
        total = patientService.countGetPatientsByOrgUnit( organisationUnit );
        this.paging = createPaging( total );
       
        //Collection<Patient> tempPatients = new ArrayList<Patient>();
        for( String text : searchText )
        {
            //tPatients( OrganisationUnit organisationUnit, String searchText, int min, int max )
            
            List<Patient> tempPatients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, text, paging.getStartPos(), paging.getPageSize() ));
            patients.addAll( tempPatients ); 
        }
        
        total = patients.size();
        this.paging = createPaging( total );
        
        
        Collection<PatientAttributeValue> attributeValues = patientAttributeValueService.getPatientAttributeValues( patients );

        for ( Patient patient : patients )
        {
            //programs.addAll( patient.getPrograms() );
            
            mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );
    
            for ( PatientAttributeValue attributeValue : attributeValues )
            {
                mapPatientPatientAttr.put( patient.getId() + "-" + attributeValue.getPatientAttribute().getId(),
                    attributeValue.getValue() );
            }
            
            //mapPatientPrograms.put( patient.getId(), getProgramsByPatient( patient ) );            
        }
        
    }
    
    
    private void searchPatientByAttributes( List<Integer> searchingAttributeId, List<String> searchText )
    {
        //total = patientAttributeValueService.countSearchPatients( searchingAttributeId, searchText );
        
        //OrganisationUnit orgUnit = null;
        
        organisationUnit = (isSelectedOrg) ? organisationUnit : null;
        total = patientService.countSearchPatients( searchText, organisationUnit );
        //Collection<Patient> getPatients( String searchText, Integer min, Integer max );
        
        this.paging = createPaging( total );
        
        //patients = patientAttributeValueService.searchPatients( searchingAttributeId, searchText, paging.getStartPos(),paging.getPageSize() );
        patients = patientService.searchPatients( searchText, organisationUnit, paging.getStartPos(), paging.getPageSize() );
        
        /*
        if ( isSelectedOrg )
        {
            Iterator<Patient> patientIterator = patients.iterator();
            while ( patientIterator.hasNext() )
            {
                Patient patient = patientIterator.next();
                
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
                
                if ( patient.getOrganisationUnit().getId() != orgUnit.getId() )
                {
                    patientIterator.remove();
                }
            }
            total = patients.size();
            this.paging = createPaging( total );
        }
        */
        
        Collection<PatientAttributeValue> attributeValues = patientAttributeValueService.getPatientAttributeValues( patients );

        for ( Patient patient : patients )
        {
            //programs.addAll( patient.getPrograms() );
            
            mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );

            for ( PatientAttributeValue attributeValue : attributeValues )
            {
                mapPatientPatientAttr.put( patient.getId() + "-" + attributeValue.getPatientAttribute().getId(),
                    attributeValue.getValue() );
            }
            
            //mapPatientPrograms.put( patient.getId(), getProgramsByPatient( patient ) );            
        }
    }
    
    
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " / " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
    
/*    
    private List<Program> getProgramsByPatient( Patient patient )
    {
       
        List<Program> tempPrograms = new ArrayList<Program>( patient.getPrograms());
        List<Program> programsList = new ArrayList<Program>();
        
        if( tempPrograms != null && tempPrograms.size() != 0 )
        {
            for( Program program : tempPrograms )
            {
                programsList.add( program );
            }
            
        }

        return programsList;
    }
*/    

    public void getProgramDetailList()
    {
        String fileName = "NBITSProgramList.xml";
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // DHIS2_HOME set, which will throw a NPE
        }

        String programId = "";
        String programName = "";
        String excelTemplateName = "";
        String xmlTemplateName = "";

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return;
            }

            NodeList listOfReports = doc.getElementsByTagName( "program" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element programElement = (Element) reportNode;
                    programId = programElement.getAttribute( "id" );

                    NodeList programNameList = programElement.getElementsByTagName( "name" );
                    Element programNameElement = (Element) programNameList.item( 0 );
                    NodeList textProgramNameList = programNameElement.getChildNodes();
                    programName = ((Node) textProgramNameList.item( 0 )).getNodeValue().trim();


                    NodeList programExcelNameList = programElement.getElementsByTagName( "excelTemplateName" );
                    Element programExcelElement = (Element) programExcelNameList.item( 0 );
                    NodeList textProgramExcelNameList = programExcelElement.getChildNodes();
                    excelTemplateName = ((Node) textProgramExcelNameList.item( 0 )).getNodeValue().trim();

                    NodeList programXMLNameList = programElement.getElementsByTagName( "xmlTemplateName" );
                    Element programXMLElement = (Element) programXMLNameList.item( 0 );
                    NodeList textProgramXMLNameList = programXMLElement.getChildNodes();
                    xmlTemplateName = ((Node) textProgramXMLNameList.item( 0 )).getNodeValue().trim();


                    ProgramDetail programObj = new ProgramDetail(programId, programName, excelTemplateName, xmlTemplateName);
                    programList.add( programObj );

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

    }// getReportList end
    
    
    
}
