package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.common.NameableObject.NameableProperty;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultIdentifiableObjectManager
    implements IdentifiableObjectManager
{
    private static final Log log = LogFactory.getLog( DefaultIdentifiableObjectManager.class );

    @Autowired
    private Set<GenericIdentifiableObjectStore<IdentifiableObject>> identifiableObjectStores;

    @Autowired
    private Set<GenericNameableObjectStore<NameableObject>> nameableObjectStores;

    private Map<Class<IdentifiableObject>, GenericIdentifiableObjectStore<IdentifiableObject>> identifiableObjectStoreMap;

    private Map<Class<NameableObject>, GenericNameableObjectStore<NameableObject>> nameableObjectStoreMap;

    //--------------------------------------------------------------------------
    // IdentifiableObjectManager implementation
    //--------------------------------------------------------------------------

    @Override
    public void save( IdentifiableObject object )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( object.getClass() );

        if ( store != null )
        {
            store.save( object );
        }
    }

    @Override
    public void update( IdentifiableObject object )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( object.getClass() );

        if ( store != null )
        {
            store.update( object );
        }
    }

    @Override
    public void delete( IdentifiableObject object )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( object.getClass() );

        if ( store != null )
        {
            store.delete( object );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> T get( String uid )
    {
        for ( GenericIdentifiableObjectStore<IdentifiableObject> store : identifiableObjectStores )
        {
            T object = (T) store.getByUid( uid );

            if ( object != null )
            {
                return object;
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> T get( Class<T> clazz, int id )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return null;
        }

        return (T) store.get( id );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> T get( Class<T> clazz, String uid )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return null;
        }

        return (T) store.getByUid( uid );
    }

    @Override
    public <T extends IdentifiableObject> boolean exists( Class<T> clazz, String uid )
    {
        return get( clazz, uid ) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> T getByCode( Class<T> clazz, String code )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return null;
        }

        return (T) store.getByCode( code );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> T getByName( Class<T> clazz, String name )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return null;
        }

        return (T) store.getByName( name );
    }

    @Override
    public <T extends IdentifiableObject> T search( Class<T> clazz, String query )
    {
        T object = get( clazz, query );

        if ( object == null )
        {
            object = getByCode( clazz, query );
        }

        if ( object == null )
        {
            object = getByName( clazz, query );
        }

        return object;
    }

    @Override
    public <T extends IdentifiableObject> Collection<T> filter( Class<T> clazz, String query )
    {
        Set<T> uniqueObjects = new HashSet<T>();

        T uidObject = get( clazz, query );

        if ( uidObject != null )
        {
            uniqueObjects.add( uidObject );
        }

        T codeObject = getByCode( clazz, query );

        if ( codeObject != null )
        {
            uniqueObjects.add( codeObject );
        }

        uniqueObjects.addAll( getLikeName( clazz, query ) );

        List<T> objects = new ArrayList<T>( uniqueObjects );

        Collections.sort( objects, IdentifiableObjectNameComparator.INSTANCE );

        return objects;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> Collection<T> getAll( Class<T> clazz )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (Collection<T>) store.getAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> Collection<T> getAllSorted( Class<T> clazz )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (Collection<T>) store.getAllOrderedName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> List<T> getByUid( Class<T> clazz, Collection<String> uids )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (List<T>) store.getByUid( uids );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> Collection<T> getLikeName( Class<T> clazz, String name )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (Collection<T>) store.getAllLikeName( name );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> Collection<T> getLikeShortName( Class<T> clazz, String shortName )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (Collection<T>) store.getAllLikeShortName( shortName );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> List<T> getBetween( Class<T> clazz, int first, int max )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (List<T>) store.getAllOrderedName( first, max );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> List<T> getBetweenByName( Class<T> clazz, String name, int first, int max )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (List<T>) store.getAllLikeNameOrderedName( name, first, max );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> Collection<T> getByLastUpdated( Class<T> clazz, Date lastUpdated )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (Collection<T>) store.getAllGeLastUpdated( lastUpdated );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> Collection<T> getByLastUpdatedSorted( Class<T> clazz, Date lastUpdated )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return new ArrayList<T>();
        }

        return (Collection<T>) store.getAllGeLastUpdatedOrderedName( lastUpdated );
    }

    @Override
    public <T extends IdentifiableObject> Set<Integer> convertToId( Class<T> clazz, Collection<String> uids )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        Set<Integer> ids = new HashSet<Integer>();

        for ( String uid : uids )
        {
            IdentifiableObject object = store.getByUid( uid );

            if ( object != null )
            {
                ids.add( object.getId() );
            }
        }

        return ids;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> Map<String, T> getIdMap( Class<T> clazz, IdentifiableProperty property )
    {
        Map<String, T> map = new HashMap<String, T>();

        GenericIdentifiableObjectStore<T> store = (GenericIdentifiableObjectStore<T>) getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return map;
        }

        Collection<T> objects = store.getAll();

        for ( T object : objects )
        {
            if ( IdentifiableProperty.ID.equals( property ) )
            {
                if ( object.getId() > 0 )
                {
                    map.put( String.valueOf( object.getId() ), object );
                }
            }
            else if ( IdentifiableProperty.UID.equals( property ) )
            {
                if ( object.getUid() != null )
                {
                    map.put( object.getUid(), object );
                }
            }
            else if ( IdentifiableProperty.CODE.equals( property ) )
            {
                if ( object.getCode() != null )
                {
                    map.put( object.getCode(), object );
                }
            }
            else if ( IdentifiableProperty.NAME.equals( property ) )
            {
                if ( object.getName() != null )
                {
                    map.put( object.getName(), object );
                }
            }
        }

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends NameableObject> Map<String, T> getIdMap( Class<T> clazz, NameableProperty property )
    {
        Map<String, T> map = new HashMap<String, T>();

        GenericNameableObjectStore<T> store = (GenericNameableObjectStore<T>) getNameableObjectStore( clazz );

        Collection<T> objects = store.getAll();

        for ( T object : objects )
        {
            if ( property == NameableProperty.SHORT_NAME )
            {
                if ( object.getShortName() != null )
                {
                    map.put( object.getShortName(), object );
                }
            }
        }

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> T getObject( Class<T> clazz, IdentifiableProperty property, String id )
    {
        GenericIdentifiableObjectStore<T> store = (GenericIdentifiableObjectStore<T>) getIdentifiableObjectStore( clazz );

        if ( id != null )
        {
            if ( IdentifiableProperty.ID.equals( property ) )
            {
                if ( Integer.valueOf( id ) > 0 )
                {
                    return store.get( Integer.valueOf( id ) );
                }
            }
            else if ( IdentifiableProperty.UID.equals( property ) )
            {
                return store.getByUid( id );
            }
            else if ( IdentifiableProperty.CODE.equals( property ) )
            {
                return store.getByCode( id );
            }
            else if ( IdentifiableProperty.NAME.equals( property ) )
            {
                return store.getByName( id );
            }
        }

        throw new IllegalArgumentException( String.valueOf( property ) );
    }

    @Override
    public IdentifiableObject getObject( String uid, String simpleClassName )
    {
        for ( GenericIdentifiableObjectStore<IdentifiableObject> objectStore : identifiableObjectStores )
        {
            if ( simpleClassName.equals( objectStore.getClass().getSimpleName() ) )
            {
                return objectStore.getByUid( uid );
            }
        }

        return null;
    }

    @Override
    public IdentifiableObject getObject( int id, String simpleClassName )
    {
        for ( GenericIdentifiableObjectStore<IdentifiableObject> objectStore : identifiableObjectStores )
        {
            if ( simpleClassName.equals( objectStore.getClazz().getSimpleName() ) )
            {
                return objectStore.get( id );
            }
        }

        return null;
    }

    @Override
    public <T extends IdentifiableObject> int getCount( Class<T> clazz )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store != null )
        {
            return store.getCount();
        }

        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentifiableObject> T getNoAcl( Class<T> clazz, String uid )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( clazz );

        if ( store == null )
        {
            return null;
        }

        return (T) store.getByUidNoAcl( uid );
    }

    @Override
    public <T extends IdentifiableObject> void updateNoAcl( T object )
    {
        GenericIdentifiableObjectStore<IdentifiableObject> store = getIdentifiableObjectStore( object.getClass() );

        if ( store != null )
        {
            store.updateNoAcl( object );
        }
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private <T extends IdentifiableObject> GenericIdentifiableObjectStore<IdentifiableObject> getIdentifiableObjectStore( Class<T> clazz )
    {
        initMaps();
        
        GenericIdentifiableObjectStore<IdentifiableObject> store = identifiableObjectStoreMap.get( clazz );

        if ( store == null )
        {
            store = identifiableObjectStoreMap.get( clazz.getSuperclass() );

            if ( store == null )
            {
                log.warn( "No IdentifiableObjectStore found for class: " + clazz );
            }
        }

        return store;
    }

    private <T extends NameableObject> GenericNameableObjectStore<NameableObject> getNameableObjectStore( Class<T> clazz )
    {
        initMaps();
        
        GenericNameableObjectStore<NameableObject> store = nameableObjectStoreMap.get( clazz );

        if ( store == null )
        {
            store = nameableObjectStoreMap.get( clazz.getSuperclass() );

            if ( store == null )
            {
                log.warn( "No NameableObjectStore found for class: " + clazz );
            }
        }

        return store;
    }

    private void initMaps()
    {
        if ( identifiableObjectStoreMap != null )
        {
            return; // Already initialized
        }
        
        identifiableObjectStoreMap = new HashMap<Class<IdentifiableObject>, GenericIdentifiableObjectStore<IdentifiableObject>>();

        for ( GenericIdentifiableObjectStore<IdentifiableObject> store : identifiableObjectStores )
        {
            identifiableObjectStoreMap.put( store.getClazz(), store );
        }

        nameableObjectStoreMap = new HashMap<Class<NameableObject>, GenericNameableObjectStore<NameableObject>>();

        for ( GenericNameableObjectStore<NameableObject> store : nameableObjectStores )
        {
            nameableObjectStoreMap.put( store.getClazz(), store );
        }
    }
}
