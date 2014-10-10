package org.amplecode.staxwax.writer;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.amplecode.staxwax.factory.XMLFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class XMLWriterTest
{
    private static final String VERSION = "1.0";
    private static final String ENCODING = ""; //TODO    
    private static final String COLLECTION_NAME = "dataElements";
    private static final String ELEMENT_NAME = "dataElement";
    private static final String FIELD_ID = "id";
    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_ALTERNATIVE_NAME = "alternativeName";
    private static final String FIELD_SHORT_NAME = "shortName";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_AGGREGATION_OPERATOR = "aggregationOperator";
    
    private InputStream inputStreamA;
    private InputStream inputStreamB;

    private ByteArrayOutputStream outputStream;

    private static final int END = -1;
    private static final int EMPTY = 32;
    private static final int LINE_FEED = 10;
    private static final int CARRIAGE_RETURN = 13;
    
    private String[] specialChars = { "&", "<", ">", "\"", "'" };
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Before
    public void setUp()
        throws Exception
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        inputStreamA = classLoader.getResourceAsStream( "dataA.xml" );
        inputStreamB = classLoader.getResourceAsStream( "dataB.xml" );
        
        outputStream = new ByteArrayOutputStream();
    }
    
    @After
    public void tearDown()
        throws Exception
    {
        inputStreamA.close();
        inputStreamB.close();
        
        outputStream.close();
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testWriteXML()
        throws Exception
    {   
        XMLWriter writer = XMLFactory.getXMLWriter( outputStream );
        
        writer.openDocument( ENCODING, VERSION );
        
        writer.openElement( COLLECTION_NAME );
        
        for ( int i = 0; i < 5; i++ )
        {
            int number = i + 1;
            
            writer.openElement( ELEMENT_NAME );
            
            writer.writeElement( FIELD_ID, FIELD_ID + specialChars[ i ] + number );
            writer.writeElement( FIELD_UUID, FIELD_UUID + specialChars[ i ] + number );
            writer.writeElement( FIELD_NAME, FIELD_NAME + specialChars[ i ] + number );
            writer.writeElement( FIELD_ALTERNATIVE_NAME, FIELD_ALTERNATIVE_NAME + specialChars[ i ] + number );
            writer.writeElement( FIELD_SHORT_NAME, FIELD_SHORT_NAME + specialChars[ i ] + number );
            writer.writeElement( FIELD_CODE, FIELD_CODE + specialChars[ i ] + number );
            writer.writeElement( FIELD_DESCRIPTION, FIELD_DESCRIPTION + specialChars[ i ] + number );
            writer.writeElement( FIELD_ACTIVE, FIELD_ACTIVE + specialChars[ i ] + number );
            writer.writeElement( FIELD_TYPE, FIELD_TYPE + specialChars[ i ] + number );
            writer.writeElement( FIELD_AGGREGATION_OPERATOR, "" );
            
            writer.closeElement();
        }
        
        writer.closeElement();

        writer.closeDocument();

        byte[] bytes = outputStream.toByteArray();
        
        assertTrue( inputStreamEquals( inputStreamA, new ByteArrayInputStream( bytes ) ) );
    }

    @Test
    public void testWriteXMLWithAttributes()
        throws Exception
    {
        XMLWriter writer = XMLFactory.getXMLWriter( outputStream );
        
        writer.openDocument( ENCODING, VERSION );
        
        writer.openElement( COLLECTION_NAME );
        
        for ( int i = 0; i < 5; i++ )
        {
            int number = i + 1;
            
            writer.openElement( ELEMENT_NAME, FIELD_CODE, FIELD_CODE + number, FIELD_UUID, FIELD_UUID + number );
            
            writer.writeElement( FIELD_NAME, FIELD_NAME + number, 
                FIELD_SHORT_NAME, FIELD_SHORT_NAME + number, 
                FIELD_ALTERNATIVE_NAME, FIELD_ALTERNATIVE_NAME + number );
            
            writer.writeElement( FIELD_DESCRIPTION, FIELD_DESCRIPTION + number,
                FIELD_TYPE, FIELD_TYPE + number );
            
            writer.writeElement( FIELD_ACTIVE, "" );
            
            writer.closeElement();
        }
        
        writer.closeElement();

        writer.closeDocument();

        byte[] bytes = outputStream.toByteArray();
        
        assertTrue( inputStreamEquals( inputStreamB, new ByteArrayInputStream( bytes ) ) );
    }

    @Test
    public void testNullBehaviour()
        throws Exception
    {
        XMLWriter writer = XMLFactory.getXMLWriter( outputStream );

        writer.openDocument( ENCODING, VERSION );

        try
        {
            writer.openElement( null );
            
            fail( "Element with null argument not allowed" );
        }
        catch ( Exception expected )
        {
            assertEquals( expected.getClass(), RuntimeException.class );
        }

        try
        {
            writer.openElement( null, FIELD_NAME, FIELD_NAME );
            
            fail( "Element with null argument not allowed" );
        }
        catch ( Exception expected )
        {
            assertEquals( expected.getClass(), RuntimeException.class );
        }
        
        try
        {
            writer.openElement( COLLECTION_NAME, null, FIELD_NAME );
            
            fail( "Attribute with null argument not allowed" );
        }
        catch ( Exception expected )
        {
            assertEquals( expected.getClass(), RuntimeException.class );
        }
        
        try
        {
            writer.writeElement( COLLECTION_NAME, FIELD_NAME, (String) null );
        }
        catch ( Exception ex )
        {
            fail( "Value with null argument allowed" );
        }
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Tests whether two InputStreams are equal on a byte-to-byte basis.
     * 
     * @param in1 The first InputStream.
     * @param in2 The second InputStream.
     * @return True if streams are equal, false if not.
     */
    private boolean inputStreamEquals( InputStream in1, InputStream in2 )
    {
        if ( in1 == null && in2 == null )
        {
            return true;
        }
        
        if ( ( in1 == null && in2 != null ) || ( in1 != null && in2 == null ) )
        {
            return false;
        }
        
        try
        {
            while ( true )
            {
                int c1 = read( in1 );
                int c2 = read( in2 );

                if ( c1 == -1 && c2 == -1 )
                {
                    return true;
                }
                
                if ( c1 != c2 )
                {
                    return false;
                }
            }
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to compare InputStreams", ex );            
        }
    }
    

    /**
     * Reads the next byte from the InputStream. Empty characters and line
     * breaks are ignored.
     * 
     * @param in The ZipInputStream to read.  
     * @throws IOException
     */
    private static int read( InputStream in )
        throws IOException
    {
        int b8 = -2;
        
        while ( b8 != END )
        {
            b8 = in.read();
            
            if ( b8 != EMPTY && b8 != LINE_FEED && b8 != CARRIAGE_RETURN )
            {
                return b8;
            }
        }
        
        return b8;
    }
}

