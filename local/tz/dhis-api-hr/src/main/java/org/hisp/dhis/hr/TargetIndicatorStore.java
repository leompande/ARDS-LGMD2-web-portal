package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface TargetIndicatorStore extends GenericStore<TargetIndicator>
{
	String ID = TargetIndicatorStore.class.getName();

    Collection<TargetIndicator> getByYear( int year );

    Collection<TargetIndicator> getByOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup ); 
    
    Collection<TargetIndicator> getByAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup );
      
    int countGetTargetIndicatorByOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup );

}
