package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface DataValuesStore extends GenericStore<DataValues>
{
	String ID = DataValuesStore.class.getName();

    Collection<DataValues> getByValue( String value );

    Collection<DataValues> getByPerson( Person person );  
    
    DataValues getByAttribute( Attribute attribute );
      
    int countGetDataValuesByPerson( Person person );
  
    Collection<Integer> getAggregatedreportByPersonAndAttribute(Collection<Person> persons, Attribute attribute);
    
    DataValues getDataValuesByPersonAndAttribute( Person person, Attribute attribute );
    
    Collection<DataValues> getDatavalues(String value, Attribute attribute);

}
