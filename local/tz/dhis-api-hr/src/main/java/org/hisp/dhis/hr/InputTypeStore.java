package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface InputTypeStore extends GenericStore<InputType>
{
	String ID = InputTypeStore.class.getName();
	
    Collection<InputType> getByAttribute( Attribute attribute );    
      
    int countGetInputTypeByAttribute( Attribute attribute );

}
