package org.hisp.dhis.hr.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.InputTypeStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateInputTypeStore 
extends HibernateGenericStore<InputType>
implements InputTypeStore{
	
	@SuppressWarnings( "unchecked" )
	public Collection<InputType> getByAttribute( Attribute attribute ){
		
		return getCriteria( Restrictions.eq( "attribute" , attribute ) ).list();
	}
    
    public int countGetInputTypeByAttribute( Attribute attribute ){
    	
    	Number rs =  (Number) getCriteria( Restrictions.eq( "attribute", attribute ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
    }
    

}
