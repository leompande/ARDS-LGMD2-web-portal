package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface TrainingStore 
extends GenericStore<Training>
{
	String ID = TrainingStore.class.getName();

    Collection<Training> getByStartDate( Date startDate );

    Collection<Training> getByPerson( Person person );    
      
    int countGetTrainingByPerson( Person person );

}
