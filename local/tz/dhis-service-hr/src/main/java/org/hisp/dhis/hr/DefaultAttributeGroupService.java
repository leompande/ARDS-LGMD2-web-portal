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
public class DefaultAttributeGroupService 
implements AttributeGroupService
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<AttributeGroup> attributeGroupStore;

    public void setAttributeGroupStore( GenericIdentifiableObjectStore<AttributeGroup> attributeGroupStore )
    {
        this.attributeGroupStore = attributeGroupStore;
    }
    
    private GenericIdentifiableObjectStore<Attribute> attributeStore;

    public void setAttributeStore( GenericIdentifiableObjectStore<Attribute> attributeStore )
    {
        this.attributeStore = attributeStore;
    }

    // -------------------------------------------------------------------------
    // AttributeGroup
    // -------------------------------------------------------------------------

    public int saveAttributeGroup( AttributeGroup attributeGroup )
    {
        return attributeGroupStore.save( attributeGroup );
    }

    public void updateAttributeGroup( AttributeGroup attributeGroup )
    {
    	attributeGroupStore.update( attributeGroup );
    }

    public void deleteAttributeGroup( AttributeGroup attributeGroup )
    {
    	attributeGroupStore.delete( attributeGroup );
    }

    public Collection<AttributeGroup> getAllAttributeGroup()
    {
        return attributeGroupStore.getAll();
    }

    public AttributeGroup getAttributeGroup( int id )
    {
        return attributeGroupStore.get( id );
    }

    public AttributeGroup getAttributeGroupByName( String name )
    {
        return attributeGroupStore.getByName( name );
    }
    
    public Collection<AttributeGroup> getAttributeGroups( final Collection<Integer> identifiers )
    {
        Collection<AttributeGroup> attributeGroups = getAllAttributeGroup(); 

        return identifiers == null ? attributeGroups : FilterUtils.filter( attributeGroups, new Filter<AttributeGroup>()
        {
        	@Override
            public boolean retain( AttributeGroup attributeGroup )
            {
                return identifiers.contains( attributeGroup.getId() );
            }

        } );
    }
    
    public Attribute getGroupAttributeByName( String name )
    {
        return attributeStore.getByName( name );
    }
    
    public Collection<Attribute> getAllGroupAttributes()
    {
        return attributeStore.getAll();
    }
    
    public Collection<Attribute> getGroupAttributes( final Collection<Integer> identifiers )
    {
        Collection<Attribute> attributes = attributeStore.getAll();

        return identifiers == null ? attributes : FilterUtils.filter( attributes,
            new Filter<Attribute>()
            {
                public boolean retain( Attribute object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }
    
    

}
