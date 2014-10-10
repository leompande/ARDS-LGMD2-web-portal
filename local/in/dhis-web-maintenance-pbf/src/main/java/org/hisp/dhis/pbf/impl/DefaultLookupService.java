package org.hisp.dhis.pbf.impl;

import java.util.Collection;

import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;
import org.hisp.dhis.pbf.api.LookupStore;

public class DefaultLookupService implements LookupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private LookupStore lookupStore;

    public void setLookupStore( LookupStore lookupStore )
    {
        this.lookupStore = lookupStore;
    }

    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    @Override
    public int addLookup( Lookup lookup )
    {
        return lookupStore.save( lookup );
    }

    @Override
    public void updateLookup( Lookup lookup )
    {
        lookupStore.update( lookup );
    }

    @Override
    public void deleteLookup( Lookup lookup )
    {
        lookupStore.delete( lookup );
    }

    @Override
    public Lookup getLookup( int id )
    {
        return lookupStore.get( id );
    }

    @Override
    public Lookup getLookupByName( String name )
    {
        return lookupStore.getByName( name );
    }

    @Override
    public Collection<Lookup> getAllLookupsByType( String type )
    {
        return lookupStore.getAllLookupsByType( type );
    }

    @Override
    public Collection<Lookup> getAllLookups()
    {
        return lookupStore.getAll( );
    }
    
}
