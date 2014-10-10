package org.hisp.dhis.pbf.dataentry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;
import org.hisp.dhis.pbf.api.PBFDataValue;
import org.hisp.dhis.pbf.api.PBFDataValueService;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadDataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    

	private PBFDataValueService pbfDataValueService;
	
	public void setPbfDataValueService(PBFDataValueService pbfDataValueService) 
	{
		this.pbfDataValueService = pbfDataValueService;
	}

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

    private LookupService lookupService;
    
    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }
    
    private DataElementCategoryService dataElementCategoryService;
    
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private TariffDataValueService tariffDataValueService;
    
    public void setTariffDataValueService( TariffDataValueService tariffDataValueService )
    {
        this.tariffDataValueService = tariffDataValueService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
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

    private Map<DataElement, PBFDataValue> pbfDataValueMap;
    
    public Map<DataElement, PBFDataValue> getPbfDataValueMap() 
    {
		return pbfDataValueMap;
	}

	private String orgUnitId;
  
    public void setOrgUnitId( String orgUnitId )
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
    
    private List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>();
    
    public List<DataElementCategoryOptionCombo> getOptionCombos()
    {
        return optionCombos;
    }
    
    private DataElementCategoryOptionCombo tariffOptCombo;
    
    public DataElementCategoryOptionCombo getTariffOptCombo()
    {
        return tariffOptCombo;
    }
    
    private DataElementCategoryOptionCombo qValOptCombo;
    
    public DataElementCategoryOptionCombo getqValOptCombo() 
    {
		return qValOptCombo;
	}

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
 
	public String execute()
    {    	
        dataValueMap = new HashMap<String, String>();
        
        
        Lookup lookup = lookupService.getLookupByName( Lookup.OC_TARIFF );
        
        Lookup lookup2 = lookupService.getLookupByName( Lookup.QV_TARIFF );
        
        tariffOptCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( lookup.getValue() ) );

        qValOptCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( lookup.getValue() ) );
        
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        optionCombos = new ArrayList<DataElementCategoryOptionCombo>();
        
        Map<Integer, Double> tariffDataValueMap = new HashMap<Integer, Double>();
        
        tariffDataValueMap.putAll( tariffDataValueService.getTariffDataValues( organisationUnit, dataSet, period ) );
        
        pbfDataValueMap = new HashMap<DataElement, PBFDataValue>();

        Set<PBFDataValue> pbfDataValues = new HashSet<PBFDataValue>( pbfDataValueService.getPBFDataValues(organisationUnit, dataSet, period) );
        for( PBFDataValue pbfDataValue : pbfDataValues )
        {
        	DataElement de = pbfDataValue.getDataElement();
        	if( pbfDataValue.getTariffAmount() == null )
        	{
        		Double tariffAmount = tariffDataValueMap.get( de.getId() );
        		if( tariffAmount != null )
        		{
        			pbfDataValue.setStoredBy( currentUserService.getCurrentUsername() );
        			pbfDataValue.setTariffAmount( tariffAmount );
        			pbfDataValue.setTimestamp( new Date() );
        			pbfDataValueService.updatePBFDataValue( pbfDataValue );
        		}
        	}
        	pbfDataValueMap.put( de, pbfDataValue );
        }
        
        Set<DataElement> tempDes = new HashSet<DataElement>();
        tempDes.addAll( dataElements );
        
        tempDes.removeAll( pbfDataValueMap.keySet() );
        
        for( DataElement de : tempDes )
        {
        	Double tariffAmount = tariffDataValueMap.get( de.getId() );
        	if( tariffAmount != null )
        	{
        		PBFDataValue pbfDataValue = new PBFDataValue();
                
                pbfDataValue.setDataElement( de );
                pbfDataValue.setPeriod( period );
                pbfDataValue.setOrganisationUnit(organisationUnit);
                pbfDataValue.setTariffAmount( tariffAmount );
                pbfDataValue.setStoredBy( currentUserService.getCurrentUsername() );
                pbfDataValue.setTimestamp( new Date() );
                
                pbfDataValueService.addPBFDataValue( pbfDataValue );
                pbfDataValueMap.put( de, pbfDataValue );
        	}
        }
        
        /*
        for( DataElement dataElement : dataElements ) 
        {
            //DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            
            optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );
            
            for( DataElementCategoryOptionCombo decombo : optionCombos )
            {
                DataValue dataValue = new DataValue();
                
                dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, decombo );
                
                String value = "";
                
                if ( dataValue != null )
                {
                    value = dataValue.getValue();
                }
                else
                {                    
                    if( decombo.getId() == tariffOptCombo.getId() )
                    {
                    	Double tariffValue = tariffDataValueMap.get( dataElement.getId() );
                        
                        if( tariffValue != null )
                        {
                            value = tariffValue+"";
                            
                            dataValue = new DataValue( );
                            dataValue.setDataElement(dataElement);
                            dataValue.setPeriod(period);
                            dataValue.setSource(organisationUnit);
                            dataValue.setValue( value );
                            dataValue.setStoredBy( currentUserService.getCurrentUsername() );
                            dataValue.setTimestamp( new Date() );
                            dataValue.setCategoryOptionCombo( decombo );
                            
                            dataValueService.addDataValue( dataValue );                            
                        }
                    }
                }
                
                String key = dataElement.getId()+ ":" +  decombo.getId();
                
                dataValueMap.put( key, value );
            }
            
        }
        */
        
        /*
        for( DataElementCategoryOptionCombo decombo : optionCombos )
        {
            System.out.println(" decombo ---" + decombo.getId() +" -- " + decombo.getName() );
        }
        */
        
        
        return SUCCESS;
    }

}

