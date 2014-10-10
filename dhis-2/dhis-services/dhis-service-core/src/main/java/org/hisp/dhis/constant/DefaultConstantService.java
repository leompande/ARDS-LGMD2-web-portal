package org.hisp.dhis.constant;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id DefaultConstantService.java July 29, 2011$
 */
@Transactional
public class DefaultConstantService
    implements ConstantService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<Constant> constantStore;

    public void setConstantStore( GenericIdentifiableObjectStore<Constant> constantStore )
    {
        this.constantStore = constantStore;
    }

    // -------------------------------------------------------------------------
    // Constant
    // -------------------------------------------------------------------------

    public int saveConstant( Constant constant )
    {
        return constantStore.save( constant );
    }

    public void updateConstant( Constant constant )
    {
        constantStore.update( constant );
    }

    public void deleteConstant( Constant constant )
    {
        constantStore.delete( constant );
    }

    public Constant getConstant( int constantId )
    {
        return constantStore.get( constantId );
    }

    public Constant getConstant( String uid )
    {
        return constantStore.getByUid( uid );
    }

    public Constant getConstantByName( String constantName )
    {
        return constantStore.getByName( constantName );
    }

    public Collection<Constant> getAllConstants()
    {
        return constantStore.getAll();
    }
    
    public Map<String, Double> getConstantMap()
    {
        Map<String, Double> map = new HashMap<String, Double>();
        
        for ( Constant constant : getAllConstants() )
        {
            map.put( constant.getUid(), constant.getValue() );
        }
        
        return map;
    }
    
    public Map<String, Double> getConstantParameterMap()
    {
        Map<String, Double> map = new HashMap<String, Double>();
        
        for ( Constant constant : getAllConstants() )
        {
            map.put( constant.getName(), constant.getValue() );
        }
        
        return map;
    }

    // -------------------------------------------------------------------------
    // Constant expanding
    // -------------------------------------------------------------------------
    
    public int getConstantCount()
    {
        return constantStore.getCount();
    }

    public int getConstantCountByName( String name )
    {
        return constantStore.getCountLikeName( name );
    }

    public Collection<Constant> getConstantsBetween( int first, int max )
    {
        return constantStore.getAllOrderedName( first, max );
    }

    public Collection<Constant> getConstantsBetweenByName( String name, int first, int max )
    {
        return constantStore.getAllLikeNameOrderedName( name, first, max );
    }
}