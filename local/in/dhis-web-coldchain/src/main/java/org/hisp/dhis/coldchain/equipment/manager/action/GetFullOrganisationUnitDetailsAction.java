package org.hisp.dhis.coldchain.equipment.manager.action;

import static org.hisp.dhis.system.util.ValidationUtils.coordinateIsValid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ValidationUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetFullOrganisationUnitDetailsAction.javaOct 17, 2012 1:20:15 PM	
 */

public class GetFullOrganisationUnitDetailsAction implements Action
{
    
    //private final String HEALTHFACILITY = "Health Facality";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
 
    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    
    private EquipmentType_AttributeService equipmentType_AttributeService;
    
    public void setEquipmentType_AttributeService( EquipmentType_AttributeService equipmentType_AttributeService )
    {
        this.equipmentType_AttributeService = equipmentType_AttributeService;
    }

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
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private Map<Integer, String> orgunitHierarchyMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getOrgunitHierarchyMap()
    {
        return orgunitHierarchyMap;
    }
    
    private List<Attribute> attributes;

    public List<Attribute> getAttributes()
    {
        return attributes;
    }
 
    
    public Map<String, String> selectedOrgUnitAttribDataValueMap;
    
    public Map<String, String> getSelectedOrgUnitAttribDataValueMap()
    {
        return selectedOrgUnitAttribDataValueMap;
    }
    
    String attributedsByComma;
    String orgUnitIdsByComma;
    
    private boolean point;

    public boolean isPoint()
    {
        return point;
    }
    
    private String longitude;

    public String getLongitude()
    {
        return longitude;
    }

    private String latitude;
    
    public String getLatitude()
    {
        return latitude;
    }
    
    private List<DataElement> dataElementList;
    
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }

    private Period period;
    
    public Period getPeriod()
    {
        return period;
    }

    public Map<String, String> dataValueMap;
       
    public Map<String, String> getDataValueMap()
    {
        return dataValueMap;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private List<Section> sections;

    public List<Section> getSections()
    {
        return sections;
    }
    
    private List<EquipmentType> equipmentTypes;

    public List<EquipmentType> getEquipmentTypes()
    {
        return equipmentTypes;
    }
    
    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    private Boolean isDataSetAssign;
    
    public Boolean getIsDataSetAssign()
    {
        return isDataSetAssign;
    }
    
    public Map<Integer, String> equipmentTypeCountMap;
    
    public Map<Integer, String> getEquipmentTypeCountMap()
    {
        return equipmentTypeCountMap;
    }
    
    private int year;
    
    public int getYear()
    {
        return year;
    }
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        attributedsByComma = "-1";
        orgUnitIdsByComma = "-1";
        
        isDataSetAssign = false;
        
        selectedOrgUnitAttribDataValueMap = new HashMap<String, String>();
        dataElementList = new ArrayList<DataElement>();
        period = new Period();
        dataValueMap = new HashMap<String, String>();
        equipmentTypeCountMap = new HashMap<Integer, String>();
        
        // OrganisationUnit and its Attribute Information
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
	List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) );
	OrganisationUnitGroup ouGroup = ouGroups.get( 0 ); 
        //OrganisationUnitGroup ouGroup = organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY );
       
        if ( ouGroup != null )
        {
            orgUnitList.retainAll( ouGroup.getMembers() );
        }
        
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
           orgUnitIdsByComma += "," + orgUnit.getId();
        }
        
        
        attributes = new ArrayList<Attribute>( attributeService.getOrganisationUnitAttributes() );
        Collections.sort( attributes, IdentifiableObjectNameComparator.INSTANCE );
        
        for ( Attribute attribute : attributes )
        {
            //attribute.getValueType().equalsIgnoreCase( "bool" );
            //attribute.isMandatory();
            attributedsByComma += "," + attribute.getId();
        }
        
        selectedOrgUnitAttribDataValueMap = new HashMap<String, String>( equipmentType_AttributeService.getOrgUnitAttributeDataValue( ""+organisationUnit.getId(), attributedsByComma ) );
        
        point = organisationUnit.getCoordinates() == null || coordinateIsValid( organisationUnit.getCoordinates() );
        longitude = ValidationUtils.getLongitude( organisationUnit.getCoordinates() );
        latitude = ValidationUtils.getLatitude( organisationUnit.getCoordinates() );
        
        // Data set and sections Information
	List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getDataSetByShortName( "FMD" ) ); 
        dataSet = dataSets.get( 0 );
        
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        Collections.sort( sections, new SectionOrderComparator() );
        
        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        
        if( dataSetSource != null && dataSetSource.size() > 0 )
        {
            if( dataSetSource.contains( organisationUnit) )
            {
                isDataSetAssign = true;
            }
        }
        
        //System.out.println( dataSet.getName()  + " -- " + dataSet.getPeriodType().getName() + " -- " + dataSet.getDataElements().size() );
        
        //Period Information
        
        PeriodType periodType = dataSet.getPeriodType();
        
        Calendar cal = Calendar.getInstance();
        
        year = cal.get( Calendar.YEAR );
        
        //int year = 2011;
        
        cal.set( year, Calendar.JANUARY, 1 );
        
        Date firstDay = new Date( cal.getTimeInMillis() );
        
        if ( periodType.getName().equals( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }
        
        Date lastDay = new Date( cal.getTimeInMillis() );
        
        //System.out.println( year  + " -- " + firstDay + " -- " + lastDay );
        
        period = periodService.getPeriod( firstDay, lastDay, periodType );
        
        //String createNewYearlyPeroid = "Yearly" + "_" + year + "-01-01";
        
        String createNewYearlyPeroid =  ""+year;
        
        if( period == null )
        {
            //period = PeriodType.createPeriodExternalId( createNewYearlyPeroid );
            period = PeriodType.getPeriodFromIsoString( createNewYearlyPeroid );
        }
        
        
        
        /*
        if ( periodType.getName().equalsIgnoreCase( "yearly" ) )
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy" );
            int periodValue = Integer.parseInt( simpleDateFormat.format( period.getStartDate() ) );
        }
        */
       
        //System.out.println( period.getId()  + " -- " + year + " -- " + period.getStartDateString() + " -- " + period.getEndDate().toString() );
        
        // DataElement and DataValue Information
        dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        //String value = "";
        for( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
            //DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            
            DataValue dataValue = new DataValue();
            
            /*
            if ( dataElement.getOptionSet().getOptions() != null && dataElement.getOptionSet().getOptions().size() > 0  )
            {
                for ( String option : dataElement.getOptionSet().getOptions() )
                {
                    System.out.println( option );
                }
            }
            */    
            
            
            dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, decoc );
            
           // System.out.println( dataElement.getName()  + " -- " + dataValue  );
            String value = "";
            
            if ( dataValue != null )
            {
                value = dataValue.getValue();
                /*
                if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_BOOL) )
                {
                    if( dataValue.getValue().equalsIgnoreCase("false") )
                    {
                        value = "No"; 
                    }
                        
                    else
                    {
                        value = "Yes";
                    }
                        
                }
                else
                {
                    value = dataValue.getValue();
                }
                */
            }
            
            String key = organisationUnit.getId()+ ":" +  period.getId()  + ":" + dataElement.getId();
            
            dataValueMap.put( key, value );
        }
        
        /*
        for( DataElement dataElement : dataElementList )
        {
            System.out.println( dataElement.getName()  + " -- " + dataValueMap.get( organisationUnit.getId()+ ":" +  period.getId()  + ":" + dataElement.getId() ) );
        }
        */
        // EquipmentType and EquipmentAttributeValue Information
        equipmentTypes = new ArrayList<EquipmentType>( equipmentTypeService.getAllEquipmentTypes() );
        
        equipmentTypeCountMap = new HashMap<Integer, String>( equipmentType_AttributeService.getEquipmentCountByOrgUnitList( orgUnitIdsByComma ) );
        /*
        for( EquipmentType equipmentType :  equipmentTypes )
        {
            System.out.println( equipmentType.getName()  + " -- " + equipmentTypeCountMap.get( equipmentType.getId() ) );
            
        }
        */
        return SUCCESS;
    }
    

}

