package org.hisp.dhis.hr;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface CompletenessStore 
	extends GenericStore<Completeness>
	{
	
	String ID = CompletenessStore.class.getName();
	
	Completeness getCompletenessByPerson( Person person );
}
