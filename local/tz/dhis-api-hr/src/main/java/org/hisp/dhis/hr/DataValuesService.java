package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Set;

/**
 * @author Wilfred Felix Senyoni
 *         John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */

public interface DataValuesService {
	
	String ID = DataValuesService.class.getName();

    // -------------------------------------------------------------------------
    // DataValues
    // -------------------------------------------------------------------------

    
    int saveDataValues( DataValues dataValues );

    void updateDataValues( DataValues dataValues );

    void deleteDataValues( DataValues dataValues );

    DataValues getDataValues( int id );
    
    String getDataValuesByAttribute( Set<DataValues> dataValues , Attribute attribute );
    
    Collection<DataValues> getAllDataValues();
    
    Collection<DataValues> getDataValuesByName( String name );
    
    Collection<DataValues> getDataValues( Collection<Integer> identifiers );

    Collection<Integer> getAggregatedreportByPersonAndAttribute(Collection<Person> persons, Attribute attribute);
   
    DataValues getDataValuesByPersonAndAttribute(Person person, Attribute attribute);
    
    Collection<DataValues> getDataValuesByPerson(Person person);
    
    Collection<DataValues> getDatavalues(String value, Attribute attribute);

}
