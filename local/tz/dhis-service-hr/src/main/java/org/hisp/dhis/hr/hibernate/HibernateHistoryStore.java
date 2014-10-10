package org.hisp.dhis.hr.hibernate;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HistoryStore;
import org.hisp.dhis.hr.Person;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateHistoryStore 
extends HibernateGenericStore<History>
implements HistoryStore{
	
	@SuppressWarnings( "unchecked" )
	public Collection<History> getByHistory( String history ){
		
		return getCriteria( Restrictions.eq( "history" , history ) ).list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<History> getByStartDate( Date startDate ){
		
		return getCriteria( Restrictions.eq( "startDate" , startDate ) ).list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<History> getByPerson( Person person ){
		
		return getCriteria( Restrictions.eq( "person" , person ) ).list();
	}

	public int countGetHistoryByPerson( Person person ){
		
		Number rs =  (Number) getCriteria( Restrictions.eq( "person", person ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
	}
    
	@SuppressWarnings( "unchecked" )
	public Collection<History> getByAttribute( Attribute attribute ){
		
		return getCriteria( Restrictions.eq( "attribute" , attribute ) ).list();
	}
 
	public int countGetHistoryByAttribute( Attribute attribute ){
		
		Number rs =  (Number) getCriteria( Restrictions.eq( "attribute", attribute ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
	}

}
