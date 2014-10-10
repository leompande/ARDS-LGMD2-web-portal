package org.hisp.dhis.mobile.api;

import java.util.Collection;

public interface ReceiveSMSService
{

    String ID = ReceiveSMSService.class.getName();
    
    void addReceiveSMS ( ReceiveSMS receiveSMS );
    
    void updateReceiveSMS ( ReceiveSMS receiveSMS );
    
    void deleteReceiveSMS ( ReceiveSMS receiveSMS );
    
    Collection<ReceiveSMS> getReceiveSMS( int start, int end );
    
    Collection<ReceiveSMS> getAllReceiveSMS();
    
    long getRowCount();
    
}
