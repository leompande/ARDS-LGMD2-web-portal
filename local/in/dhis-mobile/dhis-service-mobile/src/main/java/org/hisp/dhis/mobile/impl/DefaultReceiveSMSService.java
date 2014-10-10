package org.hisp.dhis.mobile.impl;

import java.util.Collection;

import org.hisp.dhis.mobile.api.ReceiveSMS;
import org.hisp.dhis.mobile.api.ReceiveSMSService;
import org.hisp.dhis.mobile.api.ReceiveSMSStore;

public class DefaultReceiveSMSService implements ReceiveSMSService
{
    private ReceiveSMSStore reveiveSMSStore;

    public void setReveiveSMSStore( ReceiveSMSStore reveiveSMSStore )
    {
        this.reveiveSMSStore = reveiveSMSStore;
    }
    
    // -------------------------------------------------------------------------
    // ReceiveSMS
    // -------------------------------------------------------------------------
    
    public void addReceiveSMS( ReceiveSMS receiveSMS )
    {
        reveiveSMSStore.addReceiveSMS( receiveSMS );
    }
    
    public void updateReceiveSMS( ReceiveSMS receiveSMS )
    {
        reveiveSMSStore.updateReceiveSMS( receiveSMS );
    }
    
    public void deleteReceiveSMS( ReceiveSMS receiveSMS )
    {
        reveiveSMSStore.deleteReceiveSMS( receiveSMS );
    }
    
    public Collection<ReceiveSMS> getReceiveSMS( int start, int end )
    {
        return reveiveSMSStore.getReceiveSMS( start, end );
    }
    
    public Collection<ReceiveSMS> getAllReceiveSMS()
    {
        return reveiveSMSStore.getAllReceiveSMS();
    }
    
    public long getRowCount()
    {
        return reveiveSMSStore.getRowCount();
    }

}
