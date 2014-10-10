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
public class DefaultDataTypeService 
implements DataTypeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<DataType> dataTypeStore;

    public void setDataTypeStore( GenericIdentifiableObjectStore<DataType> dataTypeStore )
    {
        this.dataTypeStore = dataTypeStore;
    }

    // -------------------------------------------------------------------------
    // DataType
    // -------------------------------------------------------------------------

    public int saveDataType( DataType dataType )
    {
        return dataTypeStore.save( dataType );
    }

    public void updateDataType( DataType dataType )
    {
    	dataTypeStore.update( dataType );
    }

    public void deleteDataType( DataType dataType )
    {
    	dataTypeStore.delete( dataType );
    }

    public Collection<DataType> getAllDataType()
    {
        return dataTypeStore.getAll();
    }

    public DataType getDataType( int id )
    {
        return dataTypeStore.get( id );
    }

    public DataType getDataTypeByName( String name )
    {
        return dataTypeStore.getByName( name );
    }
    
    public Collection<DataType> getDataTypes( final Collection<Integer> identifiers )
    {
        Collection<DataType> dataTypes = getAllDataType();

        return identifiers == null ? dataTypes : FilterUtils.filter( dataTypes, new Filter<DataType>()
        {
            public boolean retain( DataType dataTypes )
            {
                return identifiers.contains( dataTypes.getId() );
            }
        } );
    }
}
