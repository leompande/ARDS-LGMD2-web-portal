package org.hisp.dhis.importexport.action.util;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.scheduling.TaskId;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ImportMetaDataTask
    implements Runnable
{
    private static final Log log = LogFactory.getLog( ImportMetaDataTask.class );

    private ImportService importService;

    private ImportOptions importOptions;

    private InputStream inputStream;

    private TaskId taskId;

    private String userUid;

    public ImportMetaDataTask( String userUid, ImportService importService, ImportOptions importOptions, InputStream inputStream,
        TaskId taskId )
    {
        this.importService = importService;
        this.importOptions = importOptions;
        this.inputStream = inputStream;
        this.taskId = taskId;
        this.userUid = userUid;
    }

    @Override
    public void run()
    {
        MetaData metaData;

        try
        {
            // TODO should probably sniff if its xml or json, but this works for now
            metaData = JacksonUtils.fromXml( inputStream, MetaData.class );
        }
        catch ( IOException ignored )
        {
            try
            {
                metaData = JacksonUtils.fromJson( inputStream, MetaData.class );
            }
            catch ( IOException ex )
            {
                log.error( "(IOException) Unable to parse meta-data while reading input stream", ex );
                return;
            }
        }

        importService.importMetaData( userUid, metaData, importOptions, taskId );
    }
}
