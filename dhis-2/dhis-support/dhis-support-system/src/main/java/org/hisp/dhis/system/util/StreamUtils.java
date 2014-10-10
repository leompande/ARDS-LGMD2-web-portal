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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.hisp.dhis.system.comparator.FileLastModifiedComparator;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class StreamUtils
{
    public static final String LINE_BREAK = "\n";
    public static final String ENCODING_UTF8 = "UTF-8";

    /**
     * Loads a resorce from the classpath defined by the name parameter.
     *
     * @param name the name of the resource.
     * @return an InputStream.
     */
    public static InputStream loadResource( String name )
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        return classLoader.getResourceAsStream( name );
    }

    public static void streamcopy( BufferedInputStream in, BufferedOutputStream out )
    {
        int b = 0;

        try
        {
            while ( (b = in.read()) != -1 )
            {
                out.write( b );
            }
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( ex );
        }
        finally
        {
            closeInputStream( in );
            closeOutputStream( out );
        }
    }

    /**
     * Writes the content of the first File to the second File.
     *
     * @param inFile  the input File.
     * @param outFile the output File.
     */
    public static void write( File inFile, File outFile )
    {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try
        {
            in = new BufferedInputStream( new FileInputStream( inFile ) );
            out = new BufferedOutputStream( new FileOutputStream( outFile ) );

            streamcopy( in, out );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( ex );
        }
        finally
        {
            closeInputStream( in );
            closeOutputStream( out );
        }
    }

    /**
     * Returns all Files in the given directory.
     *
     * @param directory a File representing the relevant directory.
     * @param sort      indicates whether to sort chronologically on the lastModified property.
     * @return a List of Files.
     */
    public static List<File> getFileList( File directory, boolean sort )
    {
        List<File> files = new ArrayList<File>();

        if ( directory != null )
        {
            files = Arrays.asList( directory.listFiles() );
        }

        if ( sort )
        {
            Collections.sort( files, new FileLastModifiedComparator() );
        }

        return files;
    }

    /**
     * Writes the content of the StringBuffer to the file.
     *
     * @param file    the file to write to.
     * @param content the content to write.
     * @throws IOException
     */
    public static void writeContent( File file, StringBuffer content )
        throws IOException
    {
        BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter( new FileOutputStream( file ), ENCODING_UTF8 ) );

        try
        {
            writer.write( content.toString() );
        }
        finally
        {
            try
            {
                writer.flush();
            }
            catch ( Exception ex )
            {
            }

            try
            {
                writer.close();
            }
            catch ( Exception ex )
            {
            }
        }
    }

    /**
     * Get an InputStream for the String.
     *
     * @param string the String.
     * @return the InputStream.
     */
    public static InputStream getInputStream( String string )
    {
        try
        {
            return new BufferedInputStream( new ByteArrayInputStream( string.getBytes( ENCODING_UTF8 ) ) );
        }
        catch ( UnsupportedEncodingException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    /**
     * Creates a FileWriter.
     *
     * @param file the name of the file.
     * @return a FileWriter.
     */
    public static Writer getFileWriter( String file )
    {
        try
        {
            return new BufferedWriter( new FileWriter( file ) );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    /**
     * Returns the content of the File as a String.
     *
     * @param file the File.
     * @return the String.
     */
    public static String getContent( File file )
    {
        BufferedInputStream in = null;
        
        try
        {
            in = new BufferedInputStream( new FileInputStream( file ) );

            byte[] bytes = new byte[(int) file.length()];

            in.read( bytes );

            return new String( bytes, ENCODING_UTF8 );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( ex );
        }
        finally
        {
            closeInputStream( in );
        }
    }

    /**
     * Reads the content of the file to a StringBuffer. Each line is compared to
     * the keys of the argument map. If a line is matched, the line is replaced
     * with the keys corresponding value. Passing null as replace map argument skips
     * value replacement. The reading will stop at the first match for a single
     * line.
     *
     * @param file       the file to read from.
     * @param replaceMap a map containing keys to be matched and values with replacements.
     * @return a StringBuffer with the content of the file replaced according to the Map.
     * @throws IOException
     */
    public static StringBuffer readContent( File file, Map<String[], String> replaceMap )
        throws IOException
    {
        StringBuffer content = new StringBuffer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ), ENCODING_UTF8 ) );

        String line = null;

        String currentEndString = null;

        try
        {
            while ( (line = reader.readLine()) != null )
            {
                if ( currentEndString != null )
                {
                    if ( line.contains( currentEndString ) )
                    {
                        currentEndString = null;
                    }

                    continue;
                }

                if ( replaceMap != null )
                {
                    for ( Entry<String[], String> entry : replaceMap.entrySet() )
                    {
                        if ( line.contains( entry.getKey()[0] ) )
                        {
                            currentEndString = (entry.getKey()[1] != null && !line.contains( entry.getKey()[1] )) ? entry.getKey()[1] : null;

                            line = entry.getValue();

                            break;
                        }
                    }
                }

                content.append( line + LINE_BREAK );
            }
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch ( Exception ex )
            {
            }
        }

        return content;
    }

    /**
     * Closes the given Reader.
     *
     * @param reader the Reader to close.
     */
    public static void closeReader( Reader reader )
    {
        if ( reader != null )
        {
            try
            {
                reader.close();
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Closes the given Writer.
     *
     * @param writer the Writer to close.
     */
    public static void closeWriter( Writer writer )
    {
        if ( writer != null )
        {
            try
            {
                writer.flush();
            }
            catch ( IOException ex )
            {
                ex.printStackTrace();
            }

            try
            {
                writer.close();
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Closes the given InputStream.
     *
     * @param in the InputStream to close.
     */
    public static void closeInputStream( InputStream in )
    {
        if ( in != null )
        {
            try
            {
                in.close();
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Closes and flushes the given OutputStream.
     *
     * @param out the OutputStream to close.
     */
    public static void closeOutputStream( OutputStream out )
    {
        if ( out != null )
        {
            try
            {
                out.flush();
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }

            try
            {
                out.close();
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Test for ZIP/GZIP stream signature. Wraps the input stream in a
     * BufferedInputStream. If ZIP/GZIP test is true wraps again in ZipInputStream/GZIPInputStream.
     *
     * @param in the InputStream.
     * @return the wrapped InputStream.
     */
    public static InputStream wrapAndCheckCompressionFormat( InputStream in )
        throws IOException
    {
        BufferedInputStream bufferedIn = new BufferedInputStream( in );

        if ( isZip( bufferedIn ) )
        {
            ZipInputStream zipIn = new ZipInputStream( bufferedIn );
            zipIn.getNextEntry();
            return zipIn;
        }
        else if ( isGZip( bufferedIn ) )
        {
            GZIPInputStream gzipIn = new GZIPInputStream( bufferedIn );
            return gzipIn;
        }

        return bufferedIn;
    }

    /**
     * Test for ZIP stream signature.
     * <p/>
     * Signature of ZIP stream from
     * http://www.pkware.com/documents/casestudies/APPNOTE.TXT Local file
     * header: local file header signature 4 bytes (0x04034b50)
     *
     * @param instream the BufferedInputStream to test.
     */
    public static boolean isZip( BufferedInputStream instream )
    {
        instream.mark( 4 );
        byte[] b = new byte[4];
        byte[] zipSig = new byte[4];
        zipSig[0] = 0x50;
        zipSig[1] = 0x4b;
        zipSig[2] = 0x03;
        zipSig[3] = 0x04;

        try
        {
            instream.read( b, 0, 4 );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Couldn't read header from stream ", ex );
        }
        try
        {
            instream.reset();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Couldn't reset stream ", ex );
        }

        return Arrays.equals( b, zipSig );
    }

    /**
     * Test for GZIP stream signature.
     * <p/>
     * Signature of GZIP stream from RFC 1952: ID1 (IDentification 1) ID2
     * (IDentification 2) These have the fixed values ID1 = 31 (0x1f, \037),
     * ID2 = 139 (0x8b, \213), to identify the file as being in GZIP format.
     *
     * @param instream the BufferedInputStream to test.
     */
    public static boolean isGZip( BufferedInputStream instream )
    {
        instream.mark( 2 );
        byte[] b = new byte[2];

        try
        {
            instream.read( b, 0, 2 );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Couldn't read header from stream ", ex );
        }
        try
        {
            instream.reset();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Couldn't reset stream ", ex );
        }

        return (b[0] == 31 && b[1] == -117);
    }

    /**
     * Reads the next ZIP file entry from the ZipInputStream and positions the
     * stream at the beginning of the entry data.
     *
     * @param in the ZipInputStream to read from.
     * @return a ZipEntry.
     */
    public static ZipEntry getNextZipEntry( ZipInputStream in )
    {
        try
        {
            return in.getNextEntry();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to get next entry in ZIP-file", ex );
        }
    }

    /**
     * Closes the current ZipEntry and positions the stream for writing the next entry.
     *
     * @param out the ZipOutputStream.
     */
    public static void closeZipEntry( ZipOutputStream out )
    {
        try
        {
            out.closeEntry();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to close the current ZipEntry", ex );
        }
    }

    /**
     * Finishes writing the contents of the ZIP output stream without closing the underlying stream.
     *
     * @param out the ZipOutputStream.
     */
    public static void finishZipEntry( ZipOutputStream out )
    {
        try
        {
            out.finish();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to finish the content of the ZipOutputStream", ex );
        }
    }

    /**
     * Attempts to delete the File with the given path.
     *
     * @param path the File path.
     * @return true if the operation succeeded, false otherwise.
     */
    public static boolean delete( String path )
    {
        return new File( path ).delete();
    }

    /**
     * Tests whether the File with the given path exists.
     *
     * @param path the File path.
     * @return true if the File exists, false otherwise.
     */
    public static boolean exists( String path )
    {
        return new File( path ).exists();
    }
}
