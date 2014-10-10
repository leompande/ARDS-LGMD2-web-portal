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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ConversionUtils
{
    /**
     * Creates a collection of the identifiers of the objects passed as argument.
     * The object are assumed to have a <code>int getId()</code> method.
     *
     * @param clazz   the clazz of the argument objects.
     * @param objects for which to get the identifiers.
     */
    public static Collection<Integer> getIdentifiers( Class<?> clazz, Collection<?> objects )
    {
        try
        {
            Collection<Integer> identifiers = new ArrayList<Integer>();

            Method method = clazz.getMethod( "getId", new Class[0] );

            for ( Object object : objects )
            {
                Integer identifier = (Integer) method.invoke( object, new Object[0] );

                identifiers.add( identifier );
            }

            return identifiers;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to convert objects", ex );
        }
    }

    /**
     * Creates a collection of the identifiers of the objects passed as argument.
     * The object are assumed to have a <code>int getId()</code> method.
     *
     * @param clazz   the clazz of the argument objects.
     * @param objects for which to get the identifiers.
     */
    public static Collection<String> getUids( Class<?> clazz, Collection<?> objects )
    {
        try
        {
            Collection<String> identifiers = new ArrayList<String>();

            Method method = clazz.getMethod( "getUid", new Class[0] );

            for ( Object object : objects )
            {
                String identifier = (String) method.invoke( object, new Object[0] );

                identifiers.add( identifier );
            }

            return identifiers;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to convert objects", ex );
        }
    }

    /**
     * Returns the identifier of the argument object. The object is assumed to
     * have a <code>int getId()</code> method.
     *
     * @param object the object.
     * @return the identifier of the argument object.
     */
    public static Integer getIdentifier( Object object )
    {
        try
        {
            Method method = object.getClass().getMethod( "getId", new Class[0] );

            return (Integer) method.invoke( object, new Object[0] );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to convert object", ex );
        }
    }

    /**
     * Creates a collection of Integers out of a collection of Strings.
     *
     * @param strings the collection of Strings.
     * @return a collection of Integers.
     */
    public static Collection<Integer> getIntegerCollection( Collection<String> strings )
    {
        Collection<Integer> integers = new ArrayList<Integer>();

        if ( strings != null )
        {
            for ( String string : strings )
            {
                integers.add( Integer.valueOf( string ) );
            }
        }

        return integers;
    }

    /**
     * Creates an array of Integers out of an array of Strings.
     *
     * @param strings the array of Strings.
     * @return an array of Integers, an empty array if input is null.
     */
    public static Integer[] getIntegerArray( String[] strings )
    {
        if ( strings == null )
        {
            return new Integer[0];
        }

        Integer[] integers = new Integer[strings.length];

        for ( int i = 0; i < strings.length; i++ )
        {
            integers[i] = Integer.valueOf( strings[i] );
        }
        return integers;
    }

    /**
     * Parses the string argument as a signed decimal integer. Null is returedn
     * if the argument is null or has zero length.
     *
     * @param string the string to parse.
     * @return an integer or null.
     */
    public static Integer parseInt( String string )
    {
        if ( string == null || string.isEmpty() )
        {
            return null;
        }

        return Integer.parseInt( string );
    }

    /**
     * Creates a Set of objects out of a Collection of objects.
     *
     * @param objects the Collection of objects.
     * @return a Set of objects.
     */
    public static <T> Set<T> getSet( Collection<T> objects )
    {
        Set<T> set = new HashSet<T>();

        for ( T object : objects )
        {
            set.add( object );
        }

        return set;
    }

    /**
     * Creates a List of objects out of a Collection of objects.
     *
     * @param objects the Collection of objects.
     * @return a List of objects.
     */
    public static <T> List<T> getList( Collection<T> objects )
    {
        List<T> list = new ArrayList<T>();

        for ( T object : objects )
        {
            list.add( object );
        }

        return list;
    }

    /**
     * Creates a List out of an array of objects.
     *
     * @param objects the array of objects.
     * @return a List of objects.
     */
    public static <T> List<T> getList( T... objects )
    {
        List<T> list = new ArrayList<T>();

        for ( T object : objects )
        {
            list.add( object );
        }

        return list;
    }

    /**
     * Returns a Map based on an argument Collection of objects where the key is
     * the object identifier and the value if the object itself.
     *
     * @param collection the Collection of objects.
     * @return a Map with identifier object pairs.
     */
    public static <T> Map<Integer, T> getIdentifierMap( Collection<T> collection )
    {
        Map<Integer, T> map = new HashMap<Integer, T>();

        for ( T object : collection )
        {
            map.put( getIdentifier( object ), object );
        }

        return map;
    }

    /**
     * Converts a List<Double> into a double[].
     *
     * @param list the List.
     * @return a double array.
     */
    public static double[] getArray( List<Double> list )
    {
        double[] array = new double[list.size()];

        int index = 0;

        for ( Double d : list )
        {
            array[index++] = d;
        }

        return array;
    }

    /**
     * Strips the number of decimals to a maximum of 4. The string must be on the
     * form <number>.<decimals>,<number>.<decimals>
     *
     * @param coordinates the coordinates string.
     * @return the stripped coordinates string.
     */
    public static String stripCoordinates( String coordinates )
    {
        final int decimals = 4;

        if ( coordinates != null )
        {
            Matcher matcher = Pattern.compile( "\\d+\\.\\d{0," + decimals + "}" ).matcher( coordinates );
            matcher.find();
            return matcher.group() + "," + matcher.group();
        }

        return null;
    }

    /**
     * Wraps an object in a set.
     *
     * @param object the object to wrap.
     * @return a set with the given object as element.
     */
    public static <T> Set<T> wrap( T object )
    {
        Set<T> set = new HashSet<T>();
        set.add( object );
        return set;
    }

    /**
     * Casts the elements in the given collection to the desired return type. It
     * is the caller's responsibility that the types legally can be casted.
     *
     * @param collection the collection.
     * @return a collection.
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> cast( Collection<? super T> collection )
    {
        Collection<T> list = new ArrayList<T>();

        for ( Object o : collection )
        {
            list.add( (T) o );
        }

        return list;
    }
}
