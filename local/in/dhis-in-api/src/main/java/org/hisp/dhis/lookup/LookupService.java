package org.hisp.dhis.lookup;

import java.util.Collection;

public interface LookupService
{
    String ID = LookupService.class.getName();

    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    int addLookup( Lookup lookup );

    void updateLookup( Lookup lookup );

    void deleteLookup( Lookup lookup );

    Lookup getLookup( int id );

    Lookup getLookupByName( String name );
    
    Collection<Lookup> getAllLookupsByType( String type );
    
    Collection<Lookup> getAllLookups();

}
