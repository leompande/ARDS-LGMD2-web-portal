package org.hisp.dhis.importexport.dxf.converter;

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
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.GroupMemberImporter;
import org.hisp.dhis.indicator.Indicator;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataDictionaryIndicatorConverter
    extends GroupMemberImporter implements XMLConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "dataDictionaryIndicators";
    public static final String ELEMENT_NAME = "dataDictionaryIndicator";
    
    private static final String FIELD_DATADICTIONARY = "dataDictionary";
    private static final String FIELD_INDICATOR = "indicator";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataDictionaryService dataDictionaryService;
    
    private Map<Object, Integer> dataDictionaryMapping;

    private Map<Object, Integer> indicatorMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataDictionaryIndicatorConverter( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;   
    }

    /**
     * Constructor for read operations.
     */
    public DataDictionaryIndicatorConverter( BatchHandler<GroupMemberAssociation> batchHandler,
        ImportObjectService importObjectService,
        Map<Object, Integer> dataDictionaryMapping,
        Map<Object, Integer> indicatorMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataDictionaryMapping = dataDictionaryMapping;
        this.indicatorMapping = indicatorMapping;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataDictionary> dataDictionaries = dataDictionaryService.getDataDictionaries( params.getDataDictionaries() );
        
        if ( dataDictionaries != null && dataDictionaries.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataDictionary dictionary : dataDictionaries )
            {
                if ( dictionary.getDataElements() != null )
                {
                    for ( Indicator indicator : dictionary.getIndicators() )
                    {
                        writer.openElement( ELEMENT_NAME );
                    
                        writer.writeElement( FIELD_DATADICTIONARY, String.valueOf( dictionary.getId() ) );
                        writer.writeElement( FIELD_INDICATOR, String.valueOf( indicator.getId() ) );
                        
                        writer.closeElement();
                    }
                }
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.SET );
            
            association.setGroupId( dataDictionaryMapping.get( Integer.parseInt( values.get( FIELD_DATADICTIONARY ) ) ) );
            association.setMemberId( indicatorMapping.get( Integer.parseInt( values.get( FIELD_INDICATOR ) ) ) );
            
            importObject( association, params );
        }
    }

    @Override
    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, GroupMemberType.DATADICTIONARY_INDICATOR, params );
    }       
}
