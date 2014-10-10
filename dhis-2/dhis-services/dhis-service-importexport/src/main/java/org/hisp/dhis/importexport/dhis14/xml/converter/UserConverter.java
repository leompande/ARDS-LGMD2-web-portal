package org.hisp.dhis.importexport.dhis14.xml.converter;

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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;

/**
 * @author Lars Helge Overland
 * @version $Id: UserConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class UserConverter
    implements XMLConverter
{
    public static final String ELEMENT_NAME = "UserName";
    
    private static final String FIELD_ID = "UserID";
    private static final String FIELD_NAME = "UserName";
    private static final String FIELD_SURNAME = "Surname";
    private static final String FIELD_FIRSTNAME = "Firstname";  
    private static final String FIELD_INFO_ROLE = "InfoRoleID";  
    private static final String FIELD_TELEPHONE_NUMBER = "TelephoneNumber";
    private static final String FIELD_FAX_NUMBER = "FaxNumber";
    private static final String FIELD_CELL_NUMBER = "CellNumber";
    private static final String FIELD_EMAIL_ADDRESS = "EmailAddress";
    private static final String FIELD_USER_URL = "UserURL";
    private static final String FIELD_SELECTED = "Selected";
    private static final String FIELD_LAST_UPDATED = "LastUpdated";

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */    
    public UserConverter()
    {  
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( XMLWriter writer, ExportParams params )
    {
        writer.openElement( ELEMENT_NAME );
        
        writer.writeElement( FIELD_ID, String.valueOf( 1 ) );
        writer.writeElement( FIELD_NAME, "NDOH_DD" );
        writer.writeElement( FIELD_SURNAME, "NDOH_DD" );
        writer.writeElement( FIELD_FIRSTNAME, "NDOH_DD" );
        writer.writeElement( FIELD_INFO_ROLE, String.valueOf( 1 ) );
        writer.writeElement( FIELD_TELEPHONE_NUMBER, "" );
        writer.writeElement( FIELD_FAX_NUMBER, "" );
        writer.writeElement( FIELD_CELL_NUMBER, "" );
        writer.writeElement( FIELD_EMAIL_ADDRESS, "" );
        writer.writeElement( FIELD_USER_URL, "#http://www.hisp.org#" );
        writer.writeElement( FIELD_SELECTED, String.valueOf( 1 ) );        
        writer.writeElement( FIELD_LAST_UPDATED, "" );
        
        writer.closeElement();
    }    

    public void read( XMLReader reader, ImportParams params )
    {
        // Not implemented        
    }
}
