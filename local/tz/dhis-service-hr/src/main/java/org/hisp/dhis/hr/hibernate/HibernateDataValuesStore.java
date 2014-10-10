package org.hisp.dhis.hr.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.DataValuesStore;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateDataValuesStore 
extends HibernateGenericStore<DataValues>
implements DataValuesStore{
	
	@SuppressWarnings( "unchecked" )
	public Collection<DataValues> getByValue( String value ){
		return getCriteria( Restrictions.ilike( "value" , value ) ).list();
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<DataValues> getByPerson( Person person ){
		
		return getCriteria( Restrictions.eq( "person" , person ) ).list();
	}
	
	@SuppressWarnings( "unchecked" )
	public DataValues getByAttribute( Attribute attribute ){
		
		return (DataValues) getCriteria( Restrictions.eq( "attribute" , attribute ) );
	}
    
    public int countGetDataValuesByPerson( Person person ){
    	
    	Number rs =  (Number) getCriteria( Restrictions.eq( "person", person ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
    }
   
    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getAggregatedreportByPersonAndAttribute(Collection<Person> persons, Attribute attribute)
    {
    	Collection<Integer> rs =  (Collection<Integer>) getCriteria( Restrictions.eq( "person", persons ) ).add(Restrictions.eq( "attribute", attribute ) ).
        setProjection(Projections.rowCount() ).setProjection(Projections.groupProperty("attribute")).list();
    	return rs;
    }

    @SuppressWarnings( "unchecked" )
	public DataValues getDataValuesByPersonAndAttribute( Person person, Attribute attribute ){
		
		return (DataValues)getCriteria( Restrictions.eq( "person",person  ) ,
			            Restrictions.eq( "attribute", attribute ) ).uniqueResult() ;
	}
    
    @SuppressWarnings( "unchecked" )
	public Collection<DataValues> getDatavalues( String value, Attribute attribute ){
		
		return getCriteria( Restrictions.eq( "value",value  ) ,
			            Restrictions.eq( "attribute", attribute ) ).list() ;
	}
}
