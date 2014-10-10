package org.hisp.dhis.mobile.sms.api;

import java.util.Collection;

public interface ReceiveSMSStore
{

    String ID = ReceiveSMSService.class.getName();
    
    void addReceiveSMS ( ReceiveSMS receiveSMS );
    
    void updateReceiveSMS ( ReceiveSMS receiveSMS );
    
    void deleteReceiveSMS ( ReceiveSMS receiveSMS );
    
    Collection<ReceiveSMS> getReceiveSMS( int start, int end );
    
    Collection<ReceiveSMS> getAllReceiveSMS();
    
    long getRowCount();
    
}
