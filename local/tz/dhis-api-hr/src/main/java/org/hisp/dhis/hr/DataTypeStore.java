package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface DataTypeStore extends GenericStore<DataType>
{
	String ID = DataTypeStore.class.getName();

    Collection<DataType> getByAttribute( Attribute attribute );    
      
    int countGetDataTypeByAttribute( Attribute attribute );

}
