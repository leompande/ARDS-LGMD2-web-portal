package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface HistoryStore extends GenericStore<History>
{
	String ID = HistoryStore.class.getName();

    Collection<History> getByHistory( String history );

    Collection<History> getByStartDate( Date startDate );

    Collection<History> getByPerson( Person person );    
      
    int countGetHistoryByPerson( Person person );
    
    Collection<History> getByAttribute( Attribute attribute );    
    
    int countGetHistoryByAttribute( Attribute attribute );

}
