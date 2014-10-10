package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface AttributeOptionsStore extends GenericStore<AttributeOptions>
{
	String ID = AttributeOptionsStore.class.getName();

    AttributeOptions getByValue( String value );

    Collection<AttributeOptions> getByAttribute( Attribute attribute );    
      
    int countGetAttributeOptionsByAttribute( Attribute attribute );

}
