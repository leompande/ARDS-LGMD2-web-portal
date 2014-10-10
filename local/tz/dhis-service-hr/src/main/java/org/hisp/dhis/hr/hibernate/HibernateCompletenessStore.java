package org.hisp.dhis.hr.hibernate;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Completeness;
import org.hisp.dhis.hr.CompletenessStore;
import org.hisp.dhis.hr.Person;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateCompletenessStore 
extends HibernateGenericStore<Completeness>
implements CompletenessStore
{
	@SuppressWarnings( "unchecked" )
	public Completeness getCompletenessByPerson( Person person ){
		
		return (Completeness) getCriteria( Restrictions.eq( "person" , person ) ).uniqueResult();
	}

}
