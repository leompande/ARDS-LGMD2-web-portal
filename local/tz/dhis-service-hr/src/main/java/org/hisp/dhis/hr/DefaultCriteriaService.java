package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 *         John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
@Transactional
public class DefaultCriteriaService 
implements CriteriaService
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<Criteria> criteriaStore;

    public void setCriteriaStore( GenericIdentifiableObjectStore<Criteria> criteriaStore )
    {
        this.criteriaStore = criteriaStore;
    }
    
    private GenericIdentifiableObjectStore<AttributeOptions> attributeOptionsStore;

    public void setAttributeOptionsStore( GenericIdentifiableObjectStore<AttributeOptions> attributeOptionsStore )
    {
        this.attributeOptionsStore = attributeOptionsStore;
    }

    // -------------------------------------------------------------------------
    // Criteria
    // -------------------------------------------------------------------------

    public int saveCriteria( Criteria criteria )
    {
        return criteriaStore.save( criteria );
    }

    public void updateCriteria( Criteria criteria )
    {
    	criteriaStore.update( criteria );
    }

    public void deleteCriteria( Criteria criteria )
    {
    	criteriaStore.delete( criteria );
    }

    public Collection<Criteria> getAllCriteria()
    {
        return criteriaStore.getAll();
    }

    public Criteria getCriteria( int id )
    {
        return criteriaStore.get( id );
    }

    public Criteria getCriteriaByName( String name )
    {
        return criteriaStore.getByName( name );
    }
    
    public Collection<Criteria> getCriteria( final Collection<Integer> identifiers )
    {
        Collection<Criteria> criteria = getAllCriteria(); 

        return identifiers == null ? criteria : FilterUtils.filter( criteria, new Filter<Criteria>()
        {
        	@Override
            public boolean retain( Criteria criteria )
            {
                return identifiers.contains( criteria.getId() );
            }

        } );
    }
    
    public AttributeOptions getGroupAttributeOptionByName( String name )
    {
        return attributeOptionsStore.getByName( name );
    }
    
    public Collection<AttributeOptions> getAllGroupAttributes()
    {
        return attributeOptionsStore.getAll();
    }
    
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
	public Collection<AttributeOptions> getAllAttributeOptions() {
		return attributeOptionsStore.getAll();
	}


	@Override
	public AttributeOptions getAttributeOptionByName(String name) {
		return attributeOptionsStore.getByName(name);
	}

}
