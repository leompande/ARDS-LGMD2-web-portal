package org.hisp.dhis.hr.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetStore;
import org.hisp.dhis.hr.Person;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateHrDataSetStore 
extends HibernateGenericStore<HrDataSet>
implements HrDataSetStore{

	@SuppressWarnings( "unchecked" )
	public Collection<HrDataSet> getByPerson( Person person ){
		
		return getCriteria( Restrictions.eq( "person" , person ) ).list();
	}

	public int countGetHrDataSetByPerson( Person person ){
		
		Number rs =  (Number) getCriteria( Restrictions.eq( "person", person ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
	}
    
	@SuppressWarnings( "unchecked" )
	public Collection<HrDataSet> getByAttribute( Attribute attribute ){
		
		return getCriteria( Restrictions.eq( "attribute" , attribute ) ).list();
	}
 
	public int countGetHrDataSetByAttribute( Attribute attribute ){
		
		Number rs =  (Number) getCriteria( Restrictions.eq( "attribute", attribute ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
	}
	
}
