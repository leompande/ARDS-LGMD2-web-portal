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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author Lars Helge Overland
 */
public class ListUtils
{
    /**
     * Removes from the given list the elements at the given indexes.
     * 
     * @param list the list to remove elements from.
     * @param indexes the indexes for the elements to remove.
     */
    public static <T> void removeAll( List<T> list, List<Integer> indexes )
    {
        if ( list == null || indexes == null )
        {
            return;
        }
        
        Collections.sort( indexes, Collections.reverseOrder() );
        
        for ( Integer index : indexes )
        {            
            list.remove( (int) index );
        }
    }
    
    /**
     * Removes from the given list the elements at the given indexes.
     * 
     * @param indexes the list to remove elements from.
     * @param indexes the indexes for the elements to remove.
     */
    public static <T> void removeAll( List<T> list, Integer... indexes )
    {
        List<Integer> inx = new ArrayList<Integer>( Arrays.asList( indexes ) );
        
        removeAll( list, inx );
    }
    
    /**
     * Returns a sublist of the given list with the elements at the given indexes.
     * 
     * @param list the list to select from.
     * @param indexes the indexes of the elements in the list to select.
     */
    public static <T> List<T> getAtIndexes( List<T> list, List<Integer> indexes )
    {
        List<T> elements = new ArrayList<T>();
        
        for ( Integer index : indexes )
        {
            elements.add( list.get( index ) );
        }
        
        return elements;
    }
    
    /**
     * Checks whether the given list contains duplicates. List entries are compared
     * using the given comparator.
     * 
     * @param list the list.
     * @param comparator the comparator.
     * @return true if the list contains duplicates, false if not.
     */
    public static <T> boolean containsDuplicates( List<T> list, Comparator<T> comparator )
    {
        Collections.sort( list, comparator );
        
        T previous = null;
        
        for ( T entry : list )
        {
            if ( previous != null && previous.equals( entry ) )
            {
                return true;
            }
            
            previous = entry;
        }
        
        return false;
    }
    
    /**
     * Returns the duplicates in the given list. List entries are compared
     * using the given comparator.
     * 
     * @param list the list.
     * @param comparator the comparator.
     * @return a set of duplicates from the given list.
     */
    public static <T> Collection<T> getDuplicates( List<T> list, Comparator<T> comparator )
    {
        Set<T> duplicates = new HashSet<T>();
        
        Collections.sort( list, comparator );
        
        T previous = null;
        
        for ( T entry : list )
        {
            if ( previous != null && previous.equals( entry ) )
            {
                duplicates.add( entry );
            }
            
            previous = entry;
        }
        
        return duplicates;
    }
    
    /**
     * Returns a Collection with the given items.
     * 
     * @param items the items which should be included in the Collection.
     * @return a Collection.
     */
    public static <T> Collection<T> getCollection( T... items )
    {
        List<T> list = new ArrayList<T>();
        
        for ( T item : items )
        {
            list.add( item );
        }
        
        return list;
    }

    /**
     * Returns a List with the given items.
     * 
     * @param items the items which should be included in the List.
     * @return a List.
     */
    public static <T> List<T> getList( T... items )
    {
        List<T> list = new ArrayList<T>();
        
        for ( T item : items )
        {
            list.add( item );
        }
        
        return list;
    }
    
    /**
     * Removes empty strings from the given list. Empty includes null.
     * 
     * @param list the list of strings.
     */
    public static void removeEmptys( List<String> list )
    {
        if ( list != null && !list.isEmpty() )
        {
            Iterator<String> iterator = list.iterator();
            
            while ( iterator.hasNext() )
            {
                if ( StringUtils.isEmpty( iterator.next() ) )
                {
                    iterator.remove();
                }
            }
        }
    }
}
