package org.hisp.dhis.hr.action.indicator;


import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.hr.CriteriaService;

import com.opensymphony.xwork2.Action;

public class GetAggregateIndicatorAction 
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private AggregateAttributeService aggregateAttributeService;
	
	public void setAggregateAttributeService(AggregateAttributeService aggregateAttributeService)
	{
		this.aggregateAttributeService = aggregateAttributeService;
	}
	
	
	private DataElementService dataElementService;
	
	public void setDataElementService( DataElementService dataElementService)
	{
		this.dataElementService = dataElementService;
	}
	
	private AttributeOptionsService attributeOptionsService;
	
	public void setAttributeOptionsService(AttributeOptionsService attributeOptionsService)
	{
		this.attributeOptionsService = attributeOptionsService;
	}
	
	private CriteriaService criteriaService;
	
	public void setCriteriaService( CriteriaService criteriaService)
	{
		this.criteriaService = criteriaService;
	}

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
    private DataElement dataElement;
    
    public DataElement getDataElement()
    {
    	return dataElement;
    }
    
    private Collection<AttributeOptions> attributeOptions;
    
    public Collection<AttributeOptions> getAttributeOptions()
    {
    	return attributeOptions;
    }
    
    private AggregateAttribute aggregateAttribute;
    
    public AggregateAttribute getAggregateAttribute()
    {
    	return aggregateAttribute;
    }
    
    private Collection<Criteria> criterias;
    
    public Collection<Criteria> getCriterias()
    {
    	return criterias;
    }
    
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	
    	aggregateAttribute = aggregateAttributeService.getAggregateAttribute(id);
        dataElement = aggregateAttribute.getDataelement();
        criterias = aggregateAttribute.getCriterias();
        attributeOptions = aggregateAttribute.getAttributeOptions();

        return SUCCESS;
    }

}
