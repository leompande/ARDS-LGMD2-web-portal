package org.hisp.dhis.importexport.zip;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author bobj
 */
public class ZipAnalyzer
{
    /**
     * This is a list of common data streams found in zip packages
     */
    public static final Collection<String> knownDataStreams = new ArrayList<String>();

    static
    {
        knownDataStreams.add( "Data_CROSS.xml" ); // sdmx cross sectional data file
        knownDataStreams.add( "GMS_INDICATOR.xml" ); // sdmx indicator metadata
        knownDataStreams.add( "xl/workbook.xml" ); // xslsx file
        knownDataStreams.add( "content.xml" ); // xslsx file
    }

    public static InputStream getTransformableStream( ZipFile zipFile )
        throws IOException
    {
        InputStream resultStream = null;
        Enumeration<?> entries = zipFile.entries();

        // this is the simplest case - eg dxf
        if ( zipFile.size() == 1 )
        {
            ZipEntry entry = zipFile.entries().nextElement();
            resultStream = zipFile.getInputStream( entry );
        }
        else
        {
            while ( entries.hasMoreElements() )
            {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if ( knownDataStreams.contains( entry.getName() ) )
                {
                    resultStream = zipFile.getInputStream( entry );
                }
            }
        }
        
        return resultStream;
    }
}
