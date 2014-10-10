package org.hisp.dhis.hr.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.DataTypeStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateDataTypeStore 
extends HibernateGenericStore<DataType>
implements DataTypeStore{
	
	@SuppressWarnings( "unchecked" )
	public Collection<DataType> getByAttribute( Attribute attribute ){
		
		return getCriteria( Restrictions.eq( "attribute" , attribute ) ).list();
	}
    
    public int countGetDataTypeByAttribute( Attribute attribute ){
    	
    	Number rs =  (Number) getCriteria( Restrictions.eq( "attribute", attribute ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
    }

}
