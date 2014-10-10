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
public class DefaultAttributeOptionsService 
implements AttributeOptionsService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeOptionsStore attributeOptionsStore;

    public void setAttributeOptionsStore( AttributeOptionsStore attributeOptionsStore )
    {
        this.attributeOptionsStore = attributeOptionsStore;
    }

    // -------------------------------------------------------------------------
    // AttributeOptions
    // -------------------------------------------------------------------------

    public int saveAttributeOptions( AttributeOptions attributeOptions )
    {
        return attributeOptionsStore.save( attributeOptions );
    }

    public void updateAttributeOptions( AttributeOptions attributeOptions )
    {
    	attributeOptionsStore.update( attributeOptions );
    }

    public void deleteAttributeOptions( AttributeOptions attributeOptions )
    {
    	attributeOptionsStore.delete( attributeOptions );
    }

    public Collection<AttributeOptions> getAllAttributeOptions()
    {
        return attributeOptionsStore.getAll();
    }
    
    public Collection<AttributeOptions> getAttributeOptionsByAttribute( Attribute attribute )
    {
        return attributeOptionsStore.getByAttribute(attribute);
    }
    
    public AttributeOptions getAttributeOptions( int id )
    {
        return attributeOptionsStore.get( id );
    }

    public AttributeOptions getAttributeOptionsByValue( String value )
    {
        return attributeOptionsStore.getByValue( value );
    }
    
    public Collection<AttributeOptions> getAttributeOptions( final Collection<Integer> identifiers )
    {
        Collection<AttributeOptions> attributeOptions = getAllAttributeOptions();

        return identifiers == null ? attributeOptions : FilterUtils.filter( attributeOptions, new Filter<AttributeOptions>()
        {
            public boolean retain( AttributeOptions attributeOptions )
            {
                return identifiers.contains( attributeOptions.getId() );
            }
        } );
    }

}
