package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultAttributeService 
implements AttributeService
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeStore attributeStore;

    public void setAttributeStore( AttributeStore attributeStore )
    {
        this.attributeStore = attributeStore;
    }

    // -------------------------------------------------------------------------
    // Attribute
    // -------------------------------------------------------------------------
    
    public int saveAttribute( Attribute attribute )
    {
        return attributeStore.save( attribute );
    }

    public void updateAttribute( Attribute attribute )
    {
    	attributeStore.update( attribute );
    }

    public void deleteAttribute( Attribute attribute )
    {
    	attributeStore.delete( attribute );
    }

    public Collection<Attribute> getAllAttribute()
    {
        return attributeStore.getAll();
    }

    public Attribute getAttribute( int id )
    {
        return attributeStore.get( id );
    }

    public Attribute getAttributeByName( String name )
    {
        return attributeStore.getByName( name );
    } 

    public Collection<Attribute> getAttributesNotInGroup( AttributeGroup attributeGroup )
    {
        return attributeStore.getAttributesNotInGroup( attributeGroup );
    }
    
    public Collection<Attribute> getAttributes( final Collection<Integer> identifiers )
    {
        Collection<Attribute> attributes = getAllAttribute(); 

        return identifiers == null ? attributes : FilterUtils.filter( attributes, new Filter<Attribute>()
        {
        	@Override
            public boolean retain( Attribute attribute )
            {
                return identifiers.contains( attribute.getId() );
            }

        } );
    }
    
}
