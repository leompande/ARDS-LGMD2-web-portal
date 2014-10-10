package org.hisp.dhis.hr.action.indicator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.hr.CriteriaService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;

import com.opensymphony.xwork2.Action;

public class UpdateAggregateIndicatorAction implements Action
{

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private AggregateAttributeService aggregateAttributeService;
	
	public void setAggregateAttributeService( AggregateAttributeService aggregateAttributeService )
	{
		this.aggregateAttributeService  = aggregateAttributeService;
	}
	
	private AttributeOptionsService attributeOptionsService;
	
	public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
	{
		this.attributeOptionsService = attributeOptionsService;
	}
	
	private DataElementService dataElementService;
	
	public void setDataElementService( DataElementService dataElementService )
	{
		this.dataElementService = dataElementService;
	}

    private CriteriaService criteriaService;
    
    public void setCriteriaService( CriteriaService criteriaService )
    {
    	this.criteriaService = criteriaService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String nameField;

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }

    private Integer dataelementId;

    public void setDataelementId( Integer dataelementId )
    {
        this.dataelementId = dataelementId;
    }

    private Collection<String> selectedAttributeOptions = new HashSet<String> ();

    public void setSelectedAttributeOptions( Collection<String> selectedAttributeOptions )
    {
        this.selectedAttributeOptions = selectedAttributeOptions;
    }
    
    private Collection<String> selectedCriterias;
    
    public void setSelectedCriterias( Collection<String> selectedCriterias )
    {
    	this.selectedCriterias = selectedCriterias;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    	throws Exception
    {
    	AggregateAttribute aggregateAttribute = aggregateAttributeService.getAggregateAttribute(id);
    	
    	DataElement dataElement = dataElementService.getDataElement(dataelementId);
    	
    	aggregateAttribute.setDataelement(dataElement);
    	
        aggregateAttribute.setName( nameField );

        // Add attributeOpions only if there were any selected.
    	if( selectedAttributeOptions.size() > 0 ) {
    	
	        Set<AttributeOptions> attributeOptions = new HashSet<AttributeOptions>();
	        for ( String attributeOptionId : selectedAttributeOptions )
	        {
	            AttributeOptions attributeOption = attributeOptionsService.getAttributeOptions( Integer.parseInt( attributeOptionId ) );
	            attributeOptions.add( attributeOption );
	        }
	        
	        aggregateAttribute.setAttributeOptions( attributeOptions);
    	}
    	
    	// Add criterias only if there were any selected.
    	if( selectedCriterias.size() > 0 ) {
    	
	        Collection<Criteria> criterias = new HashSet<Criteria>();
	        for ( String criteriaId : selectedCriterias )
	        {
	            Criteria criteria = criteriaService.getCriteria( Integer.parseInt( criteriaId ) );
	            criterias.add( criteria );
	        }
	        
	        aggregateAttribute.setCriterias(new HashSet<Criteria>(criterias));
    	}
    	
    	aggregateAttributeService.updateAggregateAttribute(aggregateAttribute);
        return SUCCESS;
    }
}