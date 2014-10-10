package org.hisp.dhis.system.util;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hisp.dhis.system.util.functional.Function1;
import org.hisp.dhis.system.util.functional.Predicate;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class CollectionUtils
{
    public static String[] STRING_ARR = new String[0];
    public static String[][] STRING_2D_ARR = new String[0][];
    
    public static <T> void forEach( Collection<T> collection, Function1<T> function )
    {
        for ( T object : collection )
        {
            if ( object == null )
            {
                continue;
            }

            function.apply( object );
        }
    }

    public static <T> void filter( Collection<T> collection, Predicate<T> predicate )
    {
        Iterator<T> iterator = collection.iterator();

        while ( iterator.hasNext() )
        {
            T object = iterator.next();

            if ( !predicate.evaluate( object ) )
            {
                iterator.remove();
            }
        }
    }
    
    public static <T> Collection<T> intersection( Collection<T> c1, Collection<T> c2 )
    {
        Set<T> set1 = new HashSet<T>( c1 );
        set1.retainAll( new HashSet<T>( c2 ) );
        return set1;
    }
    
    public static <T> Collection<T> emptyIfNull( Collection<T> collection )
    {
        return collection != null ? collection : new HashSet<T>();
    }
}
