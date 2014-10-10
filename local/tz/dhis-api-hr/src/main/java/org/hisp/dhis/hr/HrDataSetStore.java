package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface HrDataSetStore extends GenericStore<HrDataSet>
{
	String ID = HrDataSetStore.class.getName();

    Collection<HrDataSet> getByPerson( Person person );    
      
    int countGetHrDataSetByPerson( Person person );
    
    Collection<HrDataSet> getByAttribute( Attribute attribute );    
    
    int countGetHrDataSetByAttribute( Attribute attribute );

}
