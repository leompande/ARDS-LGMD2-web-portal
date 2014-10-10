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
public class DefaultAttributeOptionGroupService 
implements AttributeOptionGroupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<AttributeOptionGroup> attributeOptionGroupStore;

    public void setAttributeOptionGroupStore( GenericIdentifiableObjectStore<AttributeOptionGroup> attributeOptionGroupStore )
    {
        this.attributeOptionGroupStore = attributeOptionGroupStore;
    }
    
    private GenericIdentifiableObjectStore<AttributeOptions> attributeOptionsStore;

    public void setAttributeOptionsStore( GenericIdentifiableObjectStore<AttributeOptions> attributeOptionsStore )
    {
        this.attributeOptionsStore = attributeOptionsStore;
    }

    // -------------------------------------------------------------------------
    // AttributeOptionGroup
    // -------------------------------------------------------------------------

    public int saveAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup )
    {
        return attributeOptionGroupStore.save( attributeOptionGroup );
    }

    public void updateAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup )
    {
    	attributeOptionGroupStore.update( attributeOptionGroup );
    }

    public void deleteAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup )
    {
    	attributeOptionGroupStore.delete( attributeOptionGroup );
    }

    public Collection<AttributeOptionGroup> getAllAttributeOptionGroup()
    {
        return attributeOptionGroupStore.getAll();
    }

    public AttributeOptionGroup getAttributeOptionGroup( int id )
    {
        return attributeOptionGroupStore.get( id );
    }

    public AttributeOptionGroup getAttributeOptionGroupByName( String name )
    {
        return attributeOptionGroupStore.getByName( name );
    }
    
    public Collection<AttributeOptionGroup> getAttributeOptionGroups( final Collection<Integer> identifiers )
    {
        Collection<AttributeOptionGroup> attributeOptionGroups = getAllAttributeOptionGroup(); 

        return identifiers == null ? attributeOptionGroups : FilterUtils.filter( attributeOptionGroups, new Filter<AttributeOptionGroup>()
        {
        	@Override
            public boolean retain( AttributeOptionGroup attributeOptionGroup )
            {
                return identifiers.contains( attributeOptionGroup.getId() );
            }

        } );
    }
    
    public AttributeOptions getGroupAttributeOptionByName( String name )
    {
        return attributeOptionsStore.getByName( name );
    }
    
    public Collection<AttributeOptions> getGroupAttributeOptions( final Collection<Integer> identifiers )
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
    
    public Collection<AttributeOptions> getAllGroupAttributeOptions()
    {
        return attributeOptionsStore.getAll();
    }
    
}
