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
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.GroupMemberImporter;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class IndicatorGroupSetMemberConverter
    extends GroupMemberImporter implements XMLConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "indicatorGroupSetMembers";
    public static final String ELEMENT_NAME = "indicatorGroupSetMember";
    
    private static final String FIELD_INDICATOR_GROUP = "indicatorGroup";
    private static final String FIELD_INDICATOR_GROUP_SET = "indicatorGroupSet";
    private static final String FIELD_SORT_ORDER = "sortOrder";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;
    
    private Map<Object, Integer> indicatorGroupMapping;
    
    private Map<Object, Integer> indicatorGroupSetMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public IndicatorGroupSetMemberConverter( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    /**
     * Constructor for read operations.
     */
    public IndicatorGroupSetMemberConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> indicatorGroupMapping, 
        Map<Object, Integer> indicatorGroupSetMapping )
    {   
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.indicatorGroupMapping = indicatorGroupMapping;
        this.indicatorGroupSetMapping = indicatorGroupSetMapping;
    }    

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<IndicatorGroupSet> groupSets = indicatorService.getIndicatorGroupSets( params.getIndicatorGroupSets() );
        
        Collection<IndicatorGroup> groups = indicatorService.getIndicatorGroups( params.getIndicators() );
        
        if ( groupSets != null && groupSets.size() > 0 && groups != null && groups.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( IndicatorGroupSet groupSet : groupSets )
            {
                int sortOrder = 1;
                
                for ( IndicatorGroup group : groupSet.getMembers() )
                {                    
                    if ( groups.contains( group ) )
                    {
                        writer.openElement( ELEMENT_NAME );
                    
                        writer.writeElement( FIELD_INDICATOR_GROUP_SET, String.valueOf( groupSet.getId() ) );
                        writer.writeElement( FIELD_INDICATOR_GROUP, String.valueOf( group.getId() ) );
                        writer.writeElement( FIELD_SORT_ORDER, String.valueOf( sortOrder++ ) );
                    
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
            
            association.setGroupId( indicatorGroupSetMapping.get( Integer.parseInt( values.get( FIELD_INDICATOR_GROUP_SET ) ) ) );
            association.setMemberId( indicatorGroupMapping.get( Integer.parseInt( values.get( FIELD_INDICATOR_GROUP ) ) ) );
            association.setSortOrder( Integer.parseInt( values.get( FIELD_SORT_ORDER ) ) );
            
            importObject( association, params );
        }
    }

    @Override
    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, GroupMemberType.INDICATORGROUPSET, params );
    }
}
