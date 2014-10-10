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

import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.common.NameableObject.NameableProperty;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
public interface IdentifiableObjectManager
{
    final String ID = IdentifiableObjectManager.class.getName();
    
    void save( IdentifiableObject object );

    void update( IdentifiableObject object );

    <T extends IdentifiableObject> T get( String uid );
    
    <T extends IdentifiableObject> T get( Class<T> clazz, int id );

    <T extends IdentifiableObject> T get( Class<T> clazz, String uid );

    <T extends IdentifiableObject> boolean exists( Class<T> clazz, String uid );

    <T extends IdentifiableObject> T getByCode( Class<T> clazz, String code );

    <T extends IdentifiableObject> T getByName( Class<T> clazz, String name );

    <T extends IdentifiableObject> T search( Class<T> clazz, String query );

    <T extends IdentifiableObject> Collection<T> filter( Class<T> clazz, String query );

    <T extends IdentifiableObject> Collection<T> getAll( Class<T> clazz );

    <T extends IdentifiableObject> Collection<T> getAllSorted( Class<T> clazz );
    
    <T extends IdentifiableObject> List<T> getByUid( Class<T> clazz, Collection<String> uids );

    <T extends IdentifiableObject> Collection<T> getLikeName( Class<T> clazz, String name );

    <T extends IdentifiableObject> Collection<T> getLikeShortName( Class<T> clazz, String shortName );

    <T extends IdentifiableObject> List<T> getBetween( Class<T> clazz, int first, int max );

    <T extends IdentifiableObject> List<T> getBetweenByName( Class<T> clazz, String name, int first, int max );

    <T extends IdentifiableObject> Collection<T> getByLastUpdated( Class<T> clazz, Date lastUpdated );

    <T extends IdentifiableObject> Collection<T> getByLastUpdatedSorted( Class<T> clazz, Date lastUpdated );

    void delete( IdentifiableObject object );

    <T extends IdentifiableObject> Set<Integer> convertToId( Class<T> clazz, Collection<String> uids );

    <T extends IdentifiableObject> Map<String, T> getIdMap( Class<T> clazz, IdentifiableProperty property );

    <T extends NameableObject> Map<String, T> getIdMap( Class<T> clazz, NameableProperty property );

    <T extends IdentifiableObject> T getObject( Class<T> clazz, IdentifiableProperty property, String id );

    IdentifiableObject getObject( String uid, String simpleClassName );

    IdentifiableObject getObject( int id, String simpleClassName );

    <T extends IdentifiableObject> int getCount( Class<T> clazz );

    // -------------------------------------------------------------------------
    // NO ACL
    // -------------------------------------------------------------------------

    <T extends IdentifiableObject> T getNoAcl( Class<T> clazz, String uid );

    <T extends IdentifiableObject> void updateNoAcl( T object );
}
