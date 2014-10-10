package org.hisp.dhis.datadictionary;

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

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultDataDictionaryService
    implements DataDictionaryService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<DataDictionary> dataDictionaryStore;

    public void setDataDictionaryStore( GenericIdentifiableObjectStore<DataDictionary> dataDictionaryStore )
    {
        this.dataDictionaryStore = dataDictionaryStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // DataDictionary
    // -------------------------------------------------------------------------

    public int saveDataDictionary( DataDictionary dataDictionary )
    {
        return dataDictionaryStore.save( dataDictionary );
    }

    public DataDictionary getDataDictionary( int id )
    {
        return i18n( i18nService, dataDictionaryStore.get( id ) );
    }

    public Collection<DataDictionary> getDataDictionaries( final Collection<Integer> identifiers )
    {
        Collection<DataDictionary> dictionaries = getAllDataDictionaries();

        return identifiers == null ? dictionaries : FilterUtils.filter( dictionaries, new Filter<DataDictionary>()
        {
            public boolean retain( DataDictionary object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public void deleteDataDictionary( DataDictionary dataDictionary )
    {
        dataDictionaryStore.delete( dataDictionary );
    }

    public List<DataDictionary> getDataDictionaryByName( String name )
    {
        return new ArrayList<DataDictionary>( i18n( i18nService, dataDictionaryStore.getAllEqName( name ) ) );
    }

    public List<DataDictionary> getAllDataDictionaries()
    {
        return new ArrayList<DataDictionary>( i18n( i18nService, dataDictionaryStore.getAll() ) );
    }

    public List<DataElement> getDataElementsByDictionaryId( int dictionaryId )
    {
        return new ArrayList<DataElement>( i18n( i18nService, dataDictionaryStore.get( dictionaryId ).getDataElements() ) );
    }

    public int getDataDictionaryCount()
    {
        return dataDictionaryStore.getCount();
    }

    public int getDataDictionaryCountByName( String name )
    {
        return dataDictionaryStore.getCountLikeName( name );
    }

    public List<DataDictionary> getDataDictionariesBetween( int first, int max )
    {
        return new ArrayList<DataDictionary>( i18n( i18nService, dataDictionaryStore.getAllOrderedName( first, max ) ) );
    }

    public List<DataDictionary> getDataDictionariesBetweenByName( String name, int first, int max )
    {
        return new ArrayList<DataDictionary>( i18n( i18nService, dataDictionaryStore.getAllLikeNameOrderedName( name, first, max ) ) );
    }
}
