package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultHistoryService
implements HistoryService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<History> historyStore;

    public void setHistoryStore( GenericIdentifiableObjectStore<History> historyStore )
    {
        this.historyStore = historyStore;
    }

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    public int saveHistory( History history )
    {
        return historyStore.save( history );
    }

    public void updateHistory( History history )
    {
    	historyStore.update( history );
    }

    public void deleteHistory( History history )
    {
    	historyStore.delete( history );
    }

    public Collection<History> getAllHistory()
    {
        return historyStore.getAll();
    }

    public History getHistory( int id )
    {
        return historyStore.get( id );
    }

    public History getHistoryByName( String name )
    {
        return historyStore.getByName( name );
    }
    
    public Collection<History> getHistories( final Collection<Integer> identifiers )
    {
        Collection<History> histories = getAllHistory();

        return identifiers == null ? histories : FilterUtils.filter( histories, new Filter<History>()
        {
            public boolean retain( History histories )
            {
                return identifiers.contains( histories.getId() );
            }
        } );
    }
}
