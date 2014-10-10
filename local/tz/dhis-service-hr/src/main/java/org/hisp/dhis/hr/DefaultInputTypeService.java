package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultInputTypeService
implements InputTypeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<InputType> inputTypeStore;

    public void setInputTypeStore( GenericIdentifiableObjectStore<InputType> inputTypeStore )
    {
        this.inputTypeStore = inputTypeStore;
    }

    // -------------------------------------------------------------------------
    // InputType
    // -------------------------------------------------------------------------

    public int saveInputType( InputType inputType )
    {
        return inputTypeStore.save( inputType );
    }

    public void updateInputType( InputType inputType )
    {
        inputTypeStore.update( inputType );
    }

    public void deleteInputType( InputType inputType )
    {
    	inputTypeStore.delete( inputType );
    }

    public Collection<InputType> getAllInputType()
    {
        return inputTypeStore.getAll();
    }

    public InputType getInputType( int id )
    {
        return inputTypeStore.get( id );
    }

    public InputType getInputTypeByName( String name )
    {
        return inputTypeStore.getByName( name );
    }
    
    public Collection<InputType> getInputTypes( final Collection<Integer> identifiers )
    {
        Collection<InputType> inputTypes = getAllInputType();

        return identifiers == null ? inputTypes : FilterUtils.filter( inputTypes, new Filter<InputType>()
        {
            public boolean retain( InputType inputTypes )
            {
                return identifiers.contains( inputTypes.getId() );
            }
        } );
    }

}
