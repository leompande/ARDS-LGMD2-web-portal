package org.hisp.dhis.lookup;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface LookupStore extends GenericNameableObjectStore<Lookup>
{
    String ID = LookupStore.class.getName();
    
    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    Collection<Lookup> getAllLookupsByType( String type );
}
