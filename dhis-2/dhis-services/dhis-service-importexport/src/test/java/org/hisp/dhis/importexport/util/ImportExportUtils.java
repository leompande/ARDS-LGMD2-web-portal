package org.hisp.dhis.importexport.util;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.ImportType;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ImportExportUtils
    extends DhisConvenienceTest
{
    private static final Log log = LogFactory.getLog( ImportExportUtils.class );
    
    private static final int END = -1;
    private static final int EMPTY = 32;
    private static final int LINE_FEED = 10;
    private static final int CARRIAGE_RETURN = 13;

    private static final int LAST_CHAR_SIZE = 100;
    
    public static final int dataASize = 3;    
    public static final int dataBSize = 5;
    public static final int dataCSize = 3;    
    public static final int dataDSize = 2;
    
    public ImportExportUtils()
    {   
    }
    
    /**
     * Generates an xml export file. Primary usage is for updating
     * the reference export test files.
     * 
     * @param inputStream The ZipInputStream with export file data.
     * @param fileName The name of the export file.
     */
    public static void generateFile( InputStream in )
    {
        ZipInputStream inputStream = new ZipInputStream( in );
        
        StreamUtils.getNextZipEntry( inputStream );
        
        ZipOutputStream outputStream = null;
        
        int b8 = 0;
        int count = 0;
        
        try
        {
            outputStream = new ZipOutputStream( new FileOutputStream( "Export.zip" ) );
            
            outputStream.putNextEntry( new ZipEntry( "Export.xml" ) );
            
            while ( ( b8 = inputStream.read() ) != END )
            {
                outputStream.write( b8 );
                                
                count++;
            }
            
            log.info( "Number of bytes in file: " + count );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to generate export file", ex );
        }
        finally
        {
            StreamUtils.finishZipEntry( outputStream );            
            StreamUtils.closeOutputStream( outputStream );  
        }
    }
    
    /**
     * Compares two streams for equality.
     * 
     * @param referenceStream The referenceStream inputstream.
     * @param compareStream The compareStream inputstream.
     * @param ignoredBytePos The byte positions to be ignored.
     */
    public static boolean streamsAreEqual( InputStream referenceStream, InputStream compareStream, int[] ignoredBytePos )
    {
        ZipInputStream reference = new ZipInputStream( referenceStream );
        ZipInputStream compare = new ZipInputStream( compareStream );
        
        StreamUtils.getNextZipEntry( reference );
        StreamUtils.getNextZipEntry( compare );
        
        int b1 = 0;
        int b2 = 0;

        int pos = 0;
        
        if ( ignoredBytePos != null )
        {
            Arrays.sort( ignoredBytePos );
        }
        
        List<Character> lastChars = new ArrayList<Character>( LAST_CHAR_SIZE + 1 );
        
        try
        {
            while ( true )
            {
                pos++;
                
                b1 = read( reference, pos );
                b2 = read( compare, 0 );
                
                lastChars.add( (char)b1 );
                
                if ( lastChars.size() > LAST_CHAR_SIZE )
                {
                    lastChars.remove( 0 );
                }
                
                if ( b1 != b2 && !isIgnored( ignoredBytePos, pos ) )
                {
                    log.warn( "Not equal at reference char: " + (char)b1 + " compare char: " + (char)b2 + " at reference pos: " + pos );
                    
                    log.warn( "Trace: " + toString( lastChars ) );
                    
                    return false;
                }
                
                if ( b1 == END && b2 == END )
                {
                    return true;
                }
            }
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to compare files", ex );
        }
    }
    
    private static String toString( List<Character> list )
    {
        StringBuffer buffer = new StringBuffer();
        
        for ( Character character : list )
        {
            buffer.append( character );
        }
        
        return buffer.toString();
    }
    
    /**
     * Reads the next byte from the ZipInputStream. Empty characters and line
     * breaks are ignored.
     * 
     * @param in The ZipInputStream to read.
     * @param pos A position variable which is incremented by this method.
     * @throws IOException
     */
    private static int read( ZipInputStream in, int pos )
        throws IOException
    {
        int b8 = -2;
        
        while ( b8 != END )
        {
            b8 = in.read();
            
            pos++;
            
            if ( b8 != EMPTY && b8 != LINE_FEED && b8 != CARRIAGE_RETURN )
            {
                return b8;
            }
        }
        
        return b8;
    }
    
    /**
     * Checks whether the given byte position shoud be ignored.
     * 
     * @param ignoredBytes The ignored bytes.
     * @param pos The byte position.
     */
    private static boolean isIgnored( int[] ignoredBytes, int pos )
    {
        return ignoredBytes != null && Arrays.binarySearch( ignoredBytes, pos ) >= 0;
    }

    /**
     * Creates an ImportParams.
     * 
     * @param strategy The ImportStrategy.
     * @param preview Preview of import objects.
     * @param dataValues Include data values.
     * @param skipCheckMatching Skip the check for matching data values.
     * 
     * @return An ImportParams with the given state. 
     */
    public static ImportParams getImportParams( ImportStrategy strategy, boolean preview, boolean dataValues, boolean skipCheckMatching )
    {
        ImportParams importParams = new ImportParams();

        importParams.setType( preview ? ImportType.PREVIEW : ImportType.IMPORT );
        importParams.setImportStrategy( strategy );
        importParams.setDataValues( dataValues );
        importParams.setSkipCheckMatching( skipCheckMatching );
        
        return importParams;
    }
}
