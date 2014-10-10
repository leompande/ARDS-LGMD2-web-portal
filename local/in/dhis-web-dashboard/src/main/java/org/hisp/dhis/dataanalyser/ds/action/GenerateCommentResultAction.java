package org.hisp.dhis.dataanalyser.ds.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class GenerateCommentResultAction implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    // ---------------------------------------------------------------
    // Input/Output Parameters
    // ---------------------------------------------------------------
     
    private String selectedDataSets;
    
    public void setSelectedDataSets( String selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }
    /*
    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }
    
    public String getIncludeZeros()
    {
        return includeZeros;
    }
    */
    private String selectedButton;

    public void setselectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }

    public String getSelectedButton()
    {
        return selectedButton;
    }

    private String immChildOption;
    
    public String getImmChildOption()
    {
        return immChildOption;
    }

    public void setImmChildOption( String immChildOption )
    {
        this.immChildOption = immChildOption;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    public int getSDateLB()
    {
        return sDateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    public int getEDateLB()
    {
        return eDateLB;
    }

    private String dataSetName;
    
    public String getDataSetName()
    {
        return dataSetName;
    }
    
    private Collection<Period> periodList;

    public Collection<Period> getPeriodList()
    {
        return periodList;
    }
    
    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }
    
    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }
    
    private Map<OrganisationUnit, List<DataElement>> ouMapDataElement;
    
    public Map<OrganisationUnit, List<DataElement>> getOuMapDataElement()
    {
        return ouMapDataElement;
    }
    
    private Map<OrganisationUnit, List<String>> ouMapComment;
    
    public Map<OrganisationUnit, List<String>> getOuMapComment()
    {
        return ouMapComment;
    }
    
    private List<String> comments;
    
    public List<String> getComments()
    {
        return comments;
    }
    
    private int dataSetMemberCount;
    
    public int getDataSetMemberCount()
    {
        return dataSetMemberCount;
    }
    /*
    private Map<String , List<DataElement>> ouPeriodDataElementMap;
    
    public Map<String, List<DataElement>> getOuPeriodDataElementMap()
    {
        return ouPeriodDataElementMap;
    }
    */
    private Map<String , List<String>> ouPeriodCommentMap;
    
    public Map<String, List<String>> getOuPeriodCommentMap()
    {
        return ouPeriodCommentMap;
    }
    
    private Map<String , List<String>> ouPeriodDataElementOptionComboMap;

    public Map<String, List<String>> getOuPeriodDataElementOptionComboMap()
    {
        return ouPeriodDataElementOptionComboMap;
    }

    String comment = "";
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute() throws Exception
    {
        statementManager.initialise();
        /*
        if( includeZeros.equalsIgnoreCase( "false" ))
        {
            includeZeros = null;
        }
        */
        ouMapDataElement = new HashMap<OrganisationUnit, List<DataElement>>();
        ouMapComment = new HashMap<OrganisationUnit, List<String>>();
        comments = new ArrayList<String>();
        
        //ouPeriodDataElementMap = new HashMap<String, List<DataElement>>();
        ouPeriodCommentMap = new HashMap<String,  List<String>>();
        ouPeriodDataElementOptionComboMap = new HashMap<String,  List<String>>();
        
        
        // DataSet Related Info
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( selectedDataSets ) );
        dataSetName = dataSet.getName();
        
        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        PeriodType dataSetPeriodType = dataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ) );
        
        periodNameList = new ArrayList<String>();
        periodNameList = dashBoardService.getPeriodNamesByPeriodType( dataSetPeriodType, periodList );
        
       // Collection<DataElement> dataElements = dataSet.getDataElements();
        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements()  );
        
        List<DataElement> deList = new ArrayList<DataElement>();
        List<DataElementCategoryOptionCombo> categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
        dataSetMemberCount = 0;
        for ( DataElement dataElement : dataElementList )
        {
            DataElement de1 = dataElementService.getDataElement( dataElement.getId() );
            dataSetMemberCount += de1.getCategoryCombo().getOptionCombos().size();
            
            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            
            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );
            categoryOptionCombos.addAll( optionCombos );
            deList.add( de1 );
        }
        
        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
            @SuppressWarnings( "unused" )
            int number;

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            number = selectedOrgUnit.getChildren().size();
            orgUnitList = new ArrayList<OrganisationUnit>();

            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            while ( orgUnitIterator.hasNext() )
            {
                OrganisationUnit o = organisationUnitService.getOrganisationUnit( Integer
                    .parseInt( (String) orgUnitIterator.next() ) );
                orgUnitList.add( o );
                List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( o.getChildren() );
                //Collections.sort( organisationUnits, new OrganisationUnitShortNameComparator() );
                Collections.sort( organisationUnits, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( organisationUnits );
            }
        }
        else
        {
            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            OrganisationUnit o;
            while ( orgUnitIterator.hasNext() )
            {
                o = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitIterator.next() ) );
                orgUnitList.add( o );
               // Collections.sort( orgUnitList, new OrganisationUnitShortNameComparator() );
                Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            }
        }
        
        System.out.println( "---Size of OrgUnit List :-- " + orgUnitList.size() );
        // Set<OrganisationUnit> dSetSource = selDataSet.getSources();
        List<OrganisationUnit> dSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
       
        Iterator<OrganisationUnit> ouIt = orgUnitList.iterator();
        while ( ouIt.hasNext() )
        {
            OrganisationUnit ou = ouIt.next();

            if ( !dSetSource.contains( ou ) )
            {
               ouIt.remove();
            }
        }

        System.out.println( "--- Final Size of OrgUnit List :-- " + orgUnitList.size() );
        // for comment start
        for ( OrganisationUnit orgUnit : orgUnitList )
        {            
            for ( Period period : periodList )
            {
                List<String> tempComment = new ArrayList<String>();
                //List<DataElement> tempDE = new ArrayList<DataElement>();
                List<String> tempString = new ArrayList<String>();
                
                List<DataValue> dataValues = new ArrayList<DataValue>( dataValueService.getDataValues( orgUnit, period, deList, categoryOptionCombos ));
                
                for( DataValue dataValue : dataValues )
                {
                    if ( dataValue != null && dataValue.getComment() != null )
                    {
                        //dataValue.getDataElement().getName();
                        //dataValue.getOptionCombo().getName();
                        comment = dataValue.getComment();
                        tempComment.add( comment );
                        //tempDE.add( dataElement );
                        tempString.add( dataValue.getDataElement().getName() + ":"  + dataValue.getOptionCombo().getName() );
                    }
                }
               /* 
                for ( DataElement dataElement : deList )
                {
                    DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();

                    List<DataElementCategoryOptionCombo> tempoptionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );

                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = tempoptionCombos.iterator();
                    while ( optionComboIterator.hasNext() )
                    {
                        DataElementCategoryOptionCombo deOptionCombo = (DataElementCategoryOptionCombo) optionComboIterator.next();
                            
                        DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, deOptionCombo );
                        
                        
                        //String tmpComment = dataValue.getComment();
                        if ( dataValue != null && dataValue.getComment() != null )
                        {
                            //dataValue.getDataElement().getName();
                            //dataValue.getOptionCombo().getName();
                            comment = dataValue.getComment();
                            tempComment.add( comment );
                            //tempDE.add( dataElement );
                            tempString.add( dataElement.getName() + ":"  + deOptionCombo.getName() );
                            
                            
                            //System.out.println( "OrgUnit :-- " + orgUnit.getName() + " , Period :-- " + period + " , DataElement :---" + dataElement.getName() + " , Comment :----" + comment );
                        }
                    }
                }
                */
                //ouPeriodDataElementMap.put( orgUnit.getId() + ":" + period.getId() , tempDE );
                ouPeriodCommentMap.put( orgUnit.getId() + ":" + period.getId(), tempComment );
                ouPeriodDataElementOptionComboMap.put( orgUnit.getId() + ":" + period.getId(), tempString );
            }
            System.out.println( "--- Size of Comment List :-- " + ouPeriodCommentMap.size() );
            
            //ouMapComment.put( orgUnit, tempComment );
            //ouMapDataElement.put( orgUnit, tempDE );
        }
        statementManager.destroy();
        return SUCCESS;
    }

    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        //Collections.sort( children, new OrganisationUnitNameComparator() );
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

}
