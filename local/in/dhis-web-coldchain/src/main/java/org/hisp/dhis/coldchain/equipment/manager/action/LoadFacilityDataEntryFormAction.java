package org.hisp.dhis.coldchain.equipment.manager.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version LoadFacilityDataEntryFormAction.javaOct 20, 2012 3:32:36 PM	
 */

public class LoadFacilityDataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------
/*
    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }
*/    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    public Map<String, String> dataValueMap;
    
    public Map<String, String> getDataValueMap()
    {
        return dataValueMap;
    }
    
    private List<Section> sections;

    public List<Section> getSections()
    {
        return sections;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private Period period;
    
    public Period getPeriod()
    {
        return period;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        
        dataValueMap = new HashMap<String, String>();
        
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        //period = PeriodType.createPeriodExternalId( selectedPeriodId );
        period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        //Collections.sort( dataElements, dataElementComparator );
        
       
        for( DataElement dataElement : dataElements )
        {
            DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
            //DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            
            DataValue dataValue = new DataValue();
            
            dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, decoc );
            
            String value = "";
            
            if ( dataValue != null )
            {
                value = dataValue.getValue();
            }
            
           // System.out.println( organisationUnit.getId() +" -- " + period.getId() + " -- " + dataElement.getName()  + " -- " + value );
            
            String key = organisationUnit.getId()+ ":" +  period.getId()  + ":" + dataElement.getId();
            
            dataValueMap.put( key, value );
            
        }
        
        //sections details
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        Collections.sort( sections, new SectionOrderComparator() );
        
        return SUCCESS;
    }


}
