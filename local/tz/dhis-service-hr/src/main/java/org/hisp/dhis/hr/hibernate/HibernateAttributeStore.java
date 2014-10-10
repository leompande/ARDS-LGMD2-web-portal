package org.hisp.dhis.hr.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class HibernateAttributeStore
extends HibernateGenericStore<Attribute>
implements AttributeStore{
	
	
	@SuppressWarnings( "unchecked" )
    public Collection<Attribute> getAttributesNotInGroup( AttributeGroup attributeGroup)
    {
        return getCriteria( Restrictions.eq( "attributeGroup", attributeGroup ) ).list();
    }

}
