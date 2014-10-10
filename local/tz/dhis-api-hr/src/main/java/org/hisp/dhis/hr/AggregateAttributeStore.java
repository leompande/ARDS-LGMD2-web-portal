package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */

public interface AggregateAttributeStore extends GenericStore<AggregateAttribute>
{
	String ID = AggregateAttributeStore.class.getName();	
	
	AggregateAttribute getAggregateAttributeByName(String name);
	
    int getCountPersonByAggregateAttribute(Collection<AttributeOptions> attributeOptions, Collection<Criteria> criteria, int organisationunitid);

}
