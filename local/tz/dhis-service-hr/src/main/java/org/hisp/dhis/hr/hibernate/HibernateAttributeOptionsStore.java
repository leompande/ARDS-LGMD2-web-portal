package org.hisp.dhis.hr.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateAttributeOptionsStore 
extends HibernateGenericStore<AttributeOptions>
implements AttributeOptionsStore{
	
	
	public AttributeOptions getByValue( String value ){
		
		return (AttributeOptions) getCriteria( Restrictions.eq( "value", value ) ).uniqueResult();
	}
	
	@SuppressWarnings( "unchecked" )
    public Collection<AttributeOptions> getByAttribute( Attribute attribute ){
    	
		return getCriteria( Restrictions.eq( "attribute" , attribute ) ).list();
    }
    	
    public int countGetAttributeOptionsByAttribute( Attribute attribute ){
		
		Number rs =  (Number) getCriteria( Restrictions.eq( "attribute", attribute ) )
        .setProjection(Projections.rowCount() ).uniqueResult();
		return rs != null ? rs.intValue() : 0;
    	
    }

}
