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

import java.util.Collection;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;

/**
 * @author Lars Helge Overland
 * @version $Id: DataElementIndicatorGroupConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class DataElementIndicatorGroupConverter
    implements XMLConverter
{
    public static final String ELEMENT_NAME = "DataElementAndIndicatorGroup";
    
    private static final String FIELD_ID = "DataElementAndIndicatorGroupID";
    private static final String FIELD_NAME = "DataElementAndIndicatorGroupName";
    private static final String FIELD_LAST_USER = "LastUserID";
    private static final String FIELD_LAST_UPDATED = "LastUpdated";

    private DataElementService dataElementService;
    
    private IndicatorService indicatorService;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataElementIndicatorGroupConverter( DataElementService dataElementService,
        IndicatorService indicatorService )
    {   
        this.dataElementService = dataElementService;
        this.indicatorService = indicatorService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementGroup> dataElementGroups = dataElementService.getDataElementGroups( params.getDataElementGroups() );
        
        if ( dataElementGroups != null && dataElementGroups.size() > 0 )
        {
            for ( DataElementGroup group : dataElementGroups )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( group.getId() ) );
                writer.writeElement( FIELD_NAME, group.getName() );
                writer.writeElement( FIELD_LAST_USER, "1" );
                writer.writeElement( FIELD_LAST_UPDATED, "" );
                
                writer.closeElement();
            }
        }
        
        Collection<IndicatorGroup> indicatorGroups = indicatorService.getIndicatorGroups( params.getIndicatorGroups() );
        
        if ( indicatorGroups != null && indicatorGroups.size() > 0 )
        {
            for ( IndicatorGroup group : indicatorGroups )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( group.getId() ) );
                writer.writeElement( FIELD_NAME, group.getName() );
                writer.writeElement( FIELD_LAST_USER, "1" );
                writer.writeElement( FIELD_LAST_UPDATED, "" );
                
                writer.closeElement();
            }
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        // Not implemented        
    }
}
