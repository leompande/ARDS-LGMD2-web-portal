package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 *         John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
@Transactional
public class DefaultAggregateAttributeService 
implements AggregateAttributeService
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private AggregateAttributeStore aggregateAttributeStore;

    public void setAggregateAttributesStore( AggregateAttributeStore aggregateAttributeStore )
    {
        this.aggregateAttributeStore = aggregateAttributeStore;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private GenericIdentifiableObjectStore<AttributeOptions> attributeOptionsStore;

    public void setAttributeOptionsStore( GenericIdentifiableObjectStore<AttributeOptions> attributeOptionsStore )
    {
        this.attributeOptionsStore = attributeOptionsStore;
    }
    
    private GenericIdentifiableObjectStore<Criteria> criteriaStore;

    public void setCriteriaStore( GenericIdentifiableObjectStore<Criteria> criteriaStore )
    {
        this.criteriaStore = criteriaStore;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    
    // -------------------------------------------------------------------------
    // AggregateAttributes
    // -------------------------------------------------------------------------
    
    @Override
    public Collection<AttributeOptions> getAttributeOptions( final Collection<Integer> identifiers )
    {
        Collection<AttributeOptions> attributeOptions = attributeOptionsStore.getAll();

        return identifiers == null ? attributeOptions : FilterUtils.filter( attributeOptions,
            new Filter<AttributeOptions>()
            {
                public boolean retain( AttributeOptions object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

	@Override
	public void deleteAggregateAttribute( AggregateAttribute aggregateAttribute)
	{
		aggregateAttributeStore.delete( aggregateAttribute );
	}

	@Override
	public Collection<AttributeOptions> getAllAttributeOptions() {
		return attributeOptionsStore.getAll();
	}
	
	@Override
	public Collection <Criteria> getAllCriterias()
	{
		return criteriaStore.getAll();
	}

	@Override
	public Collection<AggregateAttribute> getAllAggregateAttribute() {
		return aggregateAttributeStore.getAll();
	}

	@Override
	public AttributeOptions getAttributeOptionByName(String name) {
		return attributeOptionsStore.getByName(name);
	}
	
	@Override
	public Criteria getCriteriaByName(String name) {
		return criteriaStore.getByName(name);
	}


	@Override
	public AggregateAttribute getAggregateAttribute(int id) {
		return aggregateAttributeStore.get(id);
	}

	@Override
	public AggregateAttribute getAggregateAttributeByName( String name) {
		return aggregateAttributeStore.getAggregateAttributeByName(name);
	}

	@Override
	public int saveAggregateAttribute(AggregateAttribute aggregateAttribute) {
		return aggregateAttributeStore.save(aggregateAttribute);
	}

	@Override
	public void updateAggregateAttribute( AggregateAttribute aggregateAttribute) {
		aggregateAttributeStore.update(aggregateAttribute);
	}

	@Override
	public Collection<AggregateAttribute> getAggregateAttribute(final Collection<Integer> identifiers) {
		Collection<AggregateAttribute> aggregateAttribute = aggregateAttributeStore.getAll();


        return identifiers == null ? aggregateAttribute : FilterUtils.filter( aggregateAttribute, new Filter<AggregateAttribute>()
        {
        	@Override
            public boolean retain( AggregateAttribute aggregateAttribute )
            {
                return identifiers.contains( aggregateAttribute.getId() );
            }

        } );
	}


	@Override
    public Collection<Criteria> getCriterias( final Collection<Integer> identifiers )
    {
        Collection<Criteria> criteria = criteriaStore.getAll();

        return identifiers == null ? criteria : FilterUtils.filter( criteria,
            new Filter<Criteria>()
            {
                public boolean retain( Criteria object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }  
	
	@Override
	public void getCountPersonByAggregateAttribute( AggregateAttribute aggregateAttribute, OrganisationUnit organisationUnit) {
		
		Collection<AttributeOptions> attributeOptions = aggregateAttribute.getAttributeOptions();
		 
		Collection<Criteria> criteria = aggregateAttribute.getCriterias();
		
		String user = currentUserService.getCurrentUsername();
		
		DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();
		
		for(DataElementCategoryOptionCombo dataelementCategotyOptionCombo:aggregateAttribute.getDataelement().getCategoryCombo().getOptionCombos()){
			optionCombo = dataelementCategotyOptionCombo;
		}		
		
		Period period = periodService.getPeriod(2067);
		
		for(OrganisationUnit unit:organisationUnitService.getOrganisationUnitWithChildren(organisationUnit.getId())){
			
			int count = aggregateAttributeStore.getCountPersonByAggregateAttribute(attributeOptions, criteria, unit.getId());
			
			if( count != 0 ){
				
				DataValue dataValue = dataValueService.getDataValue( organisationUnit, aggregateAttribute.getDataelement(), period, optionCombo );

		        if ( dataValue == null )
		        {		           
		                dataValue = new DataValue( aggregateAttribute.getDataelement(), period, organisationUnit, Integer.toString(count), user, new Date(), null, optionCombo );
		                dataValueService.addDataValue( dataValue );		           
		        }
		        else
		        {
		            dataValue.setValue( Integer.toString(count) );
		            dataValue.setTimestamp( new Date() );
		            dataValue.setStoredBy( user );

		            dataValueService.updateDataValue( dataValue );
		        }
			}
		}
	}

}
