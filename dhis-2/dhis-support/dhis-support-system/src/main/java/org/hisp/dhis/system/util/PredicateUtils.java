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

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.system.util.functional.Predicate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class PredicateUtils
{
    public static final Predicate<Field> alwaysTrue = new StaticReply( true );

    public static final Predicate<Field> alwaysFalse = new StaticReply( false );

    public static final Predicate<Field> idObjects = new ObjectWithTypePredicate( IdentifiableObject.class );

    public static final Predicate<Field> collections = new CollectionPredicate();

    public static final Predicate<Field> idObjectCollections = new CollectionWithTypePredicate( IdentifiableObject.class );

    public static final Predicate<Field> objectCollectionsWithScanned = new CollectionWithAnnotationPredicate( Scanned.class );

    public static final Predicate<Field> idObjectCollectionsWithScanned = new CollectionWithTypeAndAnnotationPredicate( IdentifiableObject.class, Scanned.class );

    public static class StaticReply
        implements Predicate<Field>
    {
        private boolean value = false;

        public StaticReply( boolean value )
        {
            this.value = value;
        }

        @Override
        public boolean evaluate( Field object )
        {
            return value;
        }
    }

    public static class CollectionPredicate
        implements Predicate<Field>
    {
        @Override
        public boolean evaluate( Field field )
        {
            return Collection.class.isAssignableFrom( field.getType() );
        }
    }

    public static class CollectionWithTypePredicate
        implements Predicate<Field>
    {
        private CollectionPredicate collectionPredicate = new CollectionPredicate();

        private Class<?> type;

        public CollectionWithTypePredicate( Class<?> type )
        {
            this.type = type;
        }

        @Override
        public boolean evaluate( Field field )
        {
            if ( collectionPredicate.evaluate( field ) )
            {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                if ( actualTypeArguments.length > 0 )
                {
                    if ( type.isAssignableFrom( (Class<?>) actualTypeArguments[0] ) )
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static class CollectionWithAnnotationPredicate
        implements Predicate<Field>
    {
        private CollectionPredicate collectionPredicate = new CollectionPredicate();

        private Class<? extends Annotation> annotation;

        public CollectionWithAnnotationPredicate( Class<? extends Annotation> annotation )
        {
            this.annotation = annotation;
        }

        @Override
        public boolean evaluate( Field field )
        {
            if ( field.isAnnotationPresent( annotation ) )
            {
                if ( collectionPredicate.evaluate( field ) )
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static class CollectionWithTypeAndAnnotationPredicate
        implements Predicate<Field>
    {
        private final CollectionWithTypePredicate collectionWithTypePredicate;

        private Class<? extends Annotation> annotation;

        public CollectionWithTypeAndAnnotationPredicate( Class<?> type, Class<? extends Annotation> annotation )
        {
            this.annotation = annotation;
            this.collectionWithTypePredicate = new CollectionWithTypePredicate( type );
        }

        @Override
        public boolean evaluate( Field field )
        {
            if ( field.isAnnotationPresent( annotation ) )
            {
                if ( collectionWithTypePredicate.evaluate( field ) )
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static class ObjectWithTypePredicate
        implements Predicate<Field>
    {
        private Class<?> type;

        public ObjectWithTypePredicate( Class<?> type )
        {
            this.type = type;
        }

        @Override
        public boolean evaluate( Field field )
        {
            return type.isAssignableFrom( field.getType() );
        }
    }
}
