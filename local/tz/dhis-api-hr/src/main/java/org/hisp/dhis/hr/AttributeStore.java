package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;


/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface AttributeStore extends GenericStore<Attribute>
{
	String ID = AttributeStore.class.getName();
	
	Attribute getByName( String name );
	
	Collection<Attribute> getAttributesNotInGroup( AttributeGroup attributeGroup);

}
