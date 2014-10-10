package org.hisp.dhis.dxf2.utils;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.hisp.dhis.common.view.BasicView;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.common.view.SharingBasicView;
import org.hisp.dhis.common.view.SharingDetailedView;
import org.hisp.dhis.common.view.SharingExportView;
import org.hisp.dhis.common.view.ShortNameView;
import org.hisp.dhis.common.view.UuidView;
import org.hisp.dhis.common.view.WithoutOrganisationUnitsView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JacksonUtils
{
    private final static ObjectMapper jsonMapper = new ObjectMapper();

    private final static XmlMapper xmlMapper = new XmlMapper();

    private final static Map<String, Class<?>> viewClasses = new HashMap<String, Class<?>>();

    static
    {
        ObjectMapper[] objectMappers = new ObjectMapper[]{ jsonMapper, xmlMapper };
        // DateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );

        for ( ObjectMapper objectMapper : objectMappers )
        {
            // objectMapper.setDateFormat( format );
            objectMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
            objectMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
            objectMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false );
            objectMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
            objectMapper.configure( SerializationFeature.WRAP_EXCEPTIONS, true );

            objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
            objectMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
            objectMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );

            objectMapper.disable( MapperFeature.AUTO_DETECT_FIELDS );
            objectMapper.disable( MapperFeature.AUTO_DETECT_CREATORS );
            objectMapper.disable( MapperFeature.AUTO_DETECT_GETTERS );
            objectMapper.disable( MapperFeature.AUTO_DETECT_SETTERS );
            objectMapper.disable( MapperFeature.AUTO_DETECT_IS_GETTERS );
        }

        jsonMapper.getJsonFactory().enable( JsonGenerator.Feature.QUOTE_FIELD_NAMES );
        xmlMapper.configure( ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true );

        // Register view classes

        viewClasses.put( "default", BasicView.class );
        viewClasses.put( "basic", BasicView.class );
        viewClasses.put( "sharing", SharingBasicView.class );
        viewClasses.put( "sharingBasic", SharingBasicView.class );
        viewClasses.put( "shortName", ShortNameView.class );
        viewClasses.put( "detailed", DetailedView.class );
        viewClasses.put( "sharingDetailed", SharingDetailedView.class );
        viewClasses.put( "uuid", UuidView.class );
        viewClasses.put( "export", ExportView.class );
        viewClasses.put( "sharingExport", SharingExportView.class );
        viewClasses.put( "dimensional", DimensionalView.class );
        viewClasses.put( "withoutOrganisationUnits", WithoutOrganisationUnitsView.class );
    }

    public static boolean isSharingView( String view )
    {
        return view.equals( "sharing" ) || view.equals( "sharingBasic" ) || view.equals( "sharingDetailed" )
            || view.equals( "sharingExport" );
    }

    public static Class<?> getViewClass( Object viewName )
    {
        if ( viewName == null || !(viewName instanceof String && ((String) viewName).length() != 0) )
        {
            return viewClasses.get( "default" );
        }

        return viewClasses.get( viewName );
    }

    //--------------------------------------------------------------------------
    // Global pre-configured instances of ObjectMapper and XmlMapper
    //--------------------------------------------------------------------------

    public static ObjectMapper getJsonMapper()
    {
        return jsonMapper;
    }

    public static XmlMapper getXmlMapper()
    {
        return xmlMapper;
    }

    //--------------------------------------------------------------------------
    // JSON
    //--------------------------------------------------------------------------

    public static void toJson( OutputStream output, Object value ) throws IOException
    {
        jsonMapper.writeValue( output, value );
    }

    public static String toJsonAsString( Object value ) throws IOException
    {
        return jsonMapper.writeValueAsString( value );
    }

    public static void toJsonWithView( OutputStream output, Object value, Class<?> viewClass ) throws IOException
    {
        jsonMapper.writerWithView( viewClass ).writeValue( output, value );
    }

    public static String toJsonWithViewAsString( Object value, Class<?> viewClass ) throws IOException
    {
        return jsonMapper.writerWithView( viewClass ).writeValueAsString( value );
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson( InputStream input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson( String input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }

    //--------------------------------------------------------------------------
    // XML
    //--------------------------------------------------------------------------

    public static void toXml( OutputStream output, Object value ) throws IOException
    {
        xmlMapper.writeValue( output, value );
    }

    public static String toXmlAsString( Object value ) throws IOException
    {
        return xmlMapper.writeValueAsString( value );
    }

    public static void toXmlWithView( OutputStream output, Object value, Class<?> viewClass ) throws IOException
    {
        xmlMapper.writerWithView( viewClass ).writeValue( output, value );
    }

    public static String toXmlWithViewAsString( Object value, Class<?> viewClass ) throws IOException
    {
        return xmlMapper.writerWithView( viewClass ).writeValueAsString( value );
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromXml( InputStream input, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( input, clazz );
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromXml( String input, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( input, clazz );
    }
}
