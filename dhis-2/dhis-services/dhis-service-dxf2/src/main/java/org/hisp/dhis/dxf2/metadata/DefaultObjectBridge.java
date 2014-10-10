package org.hisp.dhis.dxf2.metadata;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.timer.SystemNanoTimer;
import org.hisp.dhis.dxf2.timer.Timer;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.deletion.DeletionManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultObjectBridge
    implements ObjectBridge
{
    private static final Log log = LogFactory.getLog( DefaultObjectBridge.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private PeriodStore periodStore;

    @Autowired
    private UserService userService;

    @Autowired
    private DeletionManager deletionManager;

    //-------------------------------------------------------------------------------------------------------
    // Internal and Semi-Public maps
    //-------------------------------------------------------------------------------------------------------

    private static final List<Class<?>> registeredTypes = new ArrayList<Class<?>>();

    private HashMap<Class<?>, Set<?>> masterMap;

    private Map<String, PeriodType> periodTypeMap;

    private Map<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>> uidMap;

    private Map<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>> codeMap;

    private Map<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>> nameMap;

    private Map<String, UserCredentials> usernameMap;

    private boolean writeEnabled = true;

    private boolean preheatCache = true;

    //-------------------------------------------------------------------------------------------------------
    // Build maps
    //-------------------------------------------------------------------------------------------------------

    static
    {
        registeredTypes.add( PeriodType.class );
        registeredTypes.add( UserCredentials.class );

        for ( Class<?> clazz : ExchangeClasses.getImportClasses() )
        {
            registeredTypes.add( clazz );
        }
    }

    @Override
    public void init()
    {
        log.info( "Building object-bridge maps (preheatCache: " + preheatCache + ")." );
        Timer timer = new SystemNanoTimer();
        timer.start();

        masterMap = new HashMap<Class<?>, Set<?>>();
        periodTypeMap = new HashMap<String, PeriodType>();
        uidMap = new HashMap<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>>();
        codeMap = new HashMap<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>>();
        nameMap = new HashMap<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>>();
        usernameMap = new HashMap<String, UserCredentials>();

        populatePeriodTypeMap( PeriodType.class );
        populateUsernameMap( UserCredentials.class );

        for ( Class<?> type : registeredTypes )
        {
            populateIdentifiableObjectMap( type );
            populateIdentifiableObjectMap( type, IdentifiableObject.IdentifiableProperty.UID );
            populateIdentifiableObjectMap( type, IdentifiableObject.IdentifiableProperty.CODE );
            populateIdentifiableObjectMap( type, IdentifiableObject.IdentifiableProperty.NAME );
        }

        timer.stop();
        log.info( "Building object-bridge maps took " + timer.toString() + "." );
    }

    @Override
    public void destroy()
    {
        masterMap = null;
        uidMap = null;
        codeMap = null;
        nameMap = null;
        periodTypeMap = null;

        writeEnabled = true;
        preheatCache = true;
    }

    //-------------------------------------------------------------------------------------------------------
    // Populate Helpers
    //-------------------------------------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private void populateIdentifiableObjectMap( Class<?> clazz )
    {
        Set<IdentifiableObject> map = new HashSet<IdentifiableObject>();

        if ( preheatCache && IdentifiableObject.class.isAssignableFrom( clazz ) )
        {
            map = new HashSet<IdentifiableObject>( manager.getAll( (Class<IdentifiableObject>) clazz ) );
        }

        masterMap.put( clazz, map );
    }

    @SuppressWarnings( "unchecked" )
    private void populateIdentifiableObjectMap( Class<?> clazz, IdentifiableObject.IdentifiableProperty property )
    {
        Map<String, IdentifiableObject> map = new HashMap<String, IdentifiableObject>();

        if ( preheatCache && IdentifiableObject.class.isAssignableFrom( clazz ) )
        {
            map = (Map<String, IdentifiableObject>) manager.getIdMap( (Class<? extends IdentifiableObject>) clazz, property );
        }

        if ( !preheatCache || map != null )
        {
            if ( property == IdentifiableObject.IdentifiableProperty.UID )
            {
                uidMap.put( (Class<? extends IdentifiableObject>) clazz, map );
            }
            else if ( property == IdentifiableObject.IdentifiableProperty.CODE )
            {
                codeMap.put( (Class<? extends IdentifiableObject>) clazz, map );
            }
            else if ( property == IdentifiableObject.IdentifiableProperty.NAME )
            {
                if ( !preheatCache )
                {
                    nameMap.put( (Class<? extends IdentifiableObject>) clazz, map );
                }
                else
                {
                    try
                    {
                        IdentifiableObject identifiableObject = (IdentifiableObject) clazz.newInstance();

                        if ( identifiableObject.haveUniqueNames() )
                        {
                            nameMap.put( (Class<? extends IdentifiableObject>) clazz, map );
                        }
                        else
                        {
                            // add an empty map here, since we could still have some auto-generated properties
                            nameMap.put( (Class<? extends IdentifiableObject>) clazz, new HashMap<String, IdentifiableObject>() );

                            // find all auto-generated props and add them
                            for ( Map.Entry<String, IdentifiableObject> entry : map.entrySet() )
                            {
                                if ( entry.getValue().isAutoGenerated() )
                                {
                                    nameMap.get( clazz ).put( entry.getKey(), entry.getValue() );
                                }
                            }
                        }
                    }
                    catch ( InstantiationException ignored )
                    {
                    }
                    catch ( IllegalAccessException ignored )
                    {
                    }
                }
            }
        }
    }

    private void populatePeriodTypeMap( Class<?> clazz )
    {
        Collection<Object> periodTypes = new ArrayList<Object>();

        if ( PeriodType.class.isAssignableFrom( clazz ) )
        {
            for ( PeriodType periodType : periodStore.getAllPeriodTypes() )
            {
                periodTypes.add( periodType );
                periodTypeMap.put( periodType.getName(), periodType );
            }
        }

        masterMap.put( clazz, new HashSet<Object>( periodTypes ) );
    }

    private void populateUsernameMap( Class<?> clazz )
    {
        if ( UserCredentials.class.isAssignableFrom( clazz ) )
        {
            Collection<UserCredentials> allUserCredentials = userService.getAllUserCredentials();

            for ( UserCredentials userCredentials : allUserCredentials )
            {
                if ( userCredentials.getUsername() != null )
                {
                    usernameMap.put( userCredentials.getUsername(), userCredentials );
                }
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
    // ObjectBridge Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    public void saveObject( Object object )
    {
        if ( _typeSupported( object.getClass() ) && IdentifiableObject.class.isInstance( object ) )
        {
            if ( writeEnabled )
            {
                manager.save( (IdentifiableObject) object );
            }

            _updateInternalMaps( object, false );
        }
        else
        {
            log.warn( "Trying to save unsupported type + " + object.getClass() + " with object " + object + " object discarded." );
        }
    }

    @Override
    public void updateObject( Object object )
    {
        if ( _typeSupported( object.getClass() ) && IdentifiableObject.class.isInstance( object ) )
        {
            if ( writeEnabled )
            {
                manager.update( (IdentifiableObject) object );
            }

            _updateInternalMaps( object, false );
        }
        else
        {
            log.warn( "Trying to update unsupported type + " + object.getClass() + " with object " + object + " object discarded." );
        }
    }

    @Override
    public void deleteObject( Object object )
    {
        if ( _typeSupported( object.getClass() ) && IdentifiableObject.class.isInstance( object ) )
        {
            if ( writeEnabled )
            {
                deletionManager.execute( object );
                manager.delete( (IdentifiableObject) object );
            }

            _updateInternalMaps( object, true );
        }
        else
        {
            log.warn( "Trying to delete unsupported type + " + object.getClass() + " with object " + object + " object discarded." );
        }
    }

    @Override
    public <T> T getObject( T object )
    {
        Set<T> objects = _findMatches( object );

        if ( objects.size() == 1 )
        {
            return objects.iterator().next();
        }
        else
        {
            String objectName = null;

            try
            {
                // several of our domain objects build toString based on several properties, which is not checked for
                // null, which means that a NPE is very likely.
                objectName = object.toString();
            }
            catch ( NullPointerException ignored )
            {
            }

            if ( objects.size() > 1 )
            {
                log.debug( "Multiple objects found for " +
                    (objectName == null ? objectName : "UNKNOWN_NAME (" + object.getClass().getName() + ")") + ", object discarded, returning null." );
            }
            else
            {
                log.debug( "No object found for " +
                    (objectName == null ? objectName : "UNKNOWN_NAME (" + object.getClass().getName() + ")") + ", returning null." );
            }
        }

        return null;
    }

    @Override
    public <T> Set<T> getObjects( T object )
    {
        return _findMatches( object );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> Set<T> getAllObjects( Class<T> clazz )
    {
        return (Set<T>) masterMap.get( clazz );
    }

    @Override
    public void setWriteEnabled( boolean enabled )
    {
        this.writeEnabled = enabled;
    }

    @Override
    public boolean isWriteEnabled()
    {
        return writeEnabled;
    }

    @Override
    public void setPreheatCache( boolean enabled )
    {
        this.preheatCache = enabled;
    }

    @Override
    public boolean isPreheatCache()
    {
        return preheatCache;
    }

    //-------------------------------------------------------------------------------------------------------
    // Internal Methods
    //-------------------------------------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private <T> Set<T> _findMatches( T object )
    {
        Set<T> objects = new HashSet<T>();

        if ( PeriodType.class.isInstance( object ) )
        {
            PeriodType periodType = (PeriodType) object;
            periodType = periodTypeMap.get( periodType.getName() );

            if ( periodType != null )
            {
                objects.add( (T) periodType );
            }
        }

        if ( User.class.isInstance( object ) )
        {
            User user = (User) object;
            UserCredentials userCredentials = usernameMap.get( user.getUsername() );

            if ( userCredentials != null && userCredentials.getUser() != null )
            {
                objects.add( (T) userCredentials.getUser() );
            }
        }

        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;

            if ( identifiableObject.getUid() != null )
            {
                IdentifiableObject match = getUidMatch( identifiableObject );

                if ( match != null )
                {
                    objects.add( (T) match );
                }
            }

            if ( identifiableObject.getCode() != null )
            {
                IdentifiableObject match = getCodeMatch( identifiableObject );

                if ( match != null )
                {
                    objects.add( (T) match );
                }
            }

            if ( (identifiableObject.haveUniqueNames() || identifiableObject.isAutoGenerated()) && identifiableObject.getName() != null )
            {
                IdentifiableObject match = getNameMatch( identifiableObject );

                if ( match != null )
                {
                    objects.add( (T) match );
                }
            }
        }

        return objects;
    }

    private <T> void _updateInternalMaps( T object, boolean delete )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;

            if ( identifiableObject.getUid() != null )
            {
                Map<String, IdentifiableObject> map = uidMap.get( identifiableObject.getClass() );

                if ( map == null )
                {
                    // might be dynamically sub-classed by javassist or cglib, fetch superclass and try again
                    map = uidMap.get( identifiableObject.getClass().getSuperclass() );
                }

                if ( !delete )
                {
                    map.put( identifiableObject.getUid(), identifiableObject );
                }
                else
                {
                    try
                    {
                        map.remove( identifiableObject.getUid() );
                    }
                    catch ( NullPointerException ignored )
                    {
                    }
                }
            }

            if ( identifiableObject.getCode() != null )
            {
                Map<String, IdentifiableObject> map = codeMap.get( identifiableObject.getClass() );

                if ( map == null )
                {
                    // might be dynamically sub-classed by javassist or cglib, fetch superclass and try again
                    map = codeMap.get( identifiableObject.getClass().getSuperclass() );
                }

                if ( !delete )
                {
                    map.put( identifiableObject.getCode(), identifiableObject );
                }
                else
                {
                    try
                    {
                        map.remove( identifiableObject.getCode() );
                    }
                    catch ( NullPointerException ignored )
                    {
                    }
                }
            }

            if ( (identifiableObject.haveUniqueNames() || identifiableObject.isAutoGenerated()) && identifiableObject.getName() != null )
            {
                Map<String, IdentifiableObject> map = nameMap.get( identifiableObject.getClass() );

                if ( map == null )
                {
                    // might be dynamically sub-classed by javassist or cglib, fetch superclass and try again
                    map = nameMap.get( identifiableObject.getClass().getSuperclass() );
                }

                if ( !delete )
                {
                    map.put( identifiableObject.getName(), identifiableObject );
                }
                else
                {
                    try
                    {
                        map.remove( identifiableObject.getName() );
                    }
                    catch ( NullPointerException ignored )
                    {
                    }
                }

            }
        }
    }

    private IdentifiableObject getUidMatch( IdentifiableObject identifiableObject )
    {
        Map<String, IdentifiableObject> map = uidMap.get( identifiableObject.getClass() );
        IdentifiableObject entity = null;

        if ( map != null )
        {
            entity = map.get( identifiableObject.getUid() );
        }

        if ( !preheatCache && entity == null )
        {
            entity = manager.get( identifiableObject.getClass(), identifiableObject.getUid() );
        }

        return entity;
    }

    private IdentifiableObject getCodeMatch( IdentifiableObject identifiableObject )
    {
        Map<String, IdentifiableObject> map = codeMap.get( identifiableObject.getClass() );
        IdentifiableObject entity = null;

        if ( map != null )
        {
            entity = map.get( identifiableObject.getCode() );
        }

        if ( !preheatCache && entity == null )
        {
            entity = manager.getByCode( identifiableObject.getClass(), identifiableObject.getCode() );
        }

        return entity;
    }

    private IdentifiableObject getNameMatch( IdentifiableObject identifiableObject )
    {
        Map<String, IdentifiableObject> map = nameMap.get( identifiableObject.getClass() );
        IdentifiableObject entity = null;

        if ( map != null )
        {
            entity = map.get( identifiableObject.getName() );
        }

        if ( !preheatCache && identifiableObject.haveUniqueNames() && entity == null )
        {
            entity = manager.getByName( identifiableObject.getClass(), identifiableObject.getName() );
        }

        return entity;
    }

    private boolean _typeSupported( Class<?> clazz )
    {
        for ( Class<?> c : registeredTypes )
        {
            if ( c.isAssignableFrom( clazz ) )
            {
                return true;
            }
        }

        return false;
    }
}
