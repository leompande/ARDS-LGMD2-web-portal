package org.hisp.dhis.reportsheet;

/*
 * Copyright (c) 2004-2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class ExcelContentTypeMap
{
    private static final String APPLICATION = "application/";

    public static final String XLS = "xls";

    public static final String XLSX = "xlsx";

    private static Map<String, List<String>> contentTypes;

    static
    {
        contentTypes = new HashMap<String, List<String>>();

        List<String> xlsContentTypes = new ArrayList<String>();

        xlsContentTypes.add( APPLICATION + XLS );
        xlsContentTypes.add( APPLICATION + "ms-excel" );
        xlsContentTypes.add( APPLICATION + "vnd.ms-excel" );
        xlsContentTypes.add( APPLICATION + "octet-stream" );

        contentTypes.put( XLS, xlsContentTypes );

        List<String> xlsxContentTypes = new ArrayList<String>();

        xlsxContentTypes.add( APPLICATION + XLSX );
        xlsxContentTypes.add( APPLICATION + "octet-stream" );
        xlsxContentTypes.add( APPLICATION + "vnd.openxmlformats-officedocument.spreadsheetml.sheet" );

        contentTypes.put( XLSX, xlsxContentTypes );

        /*
         * List<String> odsContentTypes = new ArrayList<String>();
         * 
         * odsContentTypes.add( APPLICATION + "octet-stream" );
         * odsContentTypes.add( APPLICATION +
         * "vnd.oasis.opendocument.spreadsheet" );
         * 
         * contentTypes.put( "ods", odsContentTypes );
         */
    }

    public static List<String> getContentTypeByKey( String key )
    {
        return contentTypes.get( key );
    }

    public static Map<String, List<String>> getContentTypes()
    {
        return contentTypes;
    }
}
