package org.hisp.dhis.hr.hibernate;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.hr.TrainingStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateTrainingStore 
extends HibernateGenericStore<Training>
implements TrainingStore{
	
	@SuppressWarnings( "unchecked" )
	public Collection<Training> getByStartDate( Date startDate ){
		
		return getCriteria( Restrictions.eq( "startDate" , startDate ) ).list();
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<Training> getByPerson( Person person ){
		
		return getCriteria( Restrictions.eq( "person" , person ) ).list();
	}
      
	public int countGetTrainingByPerson( Person person ){
		
		Number rs =  (Number) getCriteria( Restrictions.eq( "person", person ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
	}

}
