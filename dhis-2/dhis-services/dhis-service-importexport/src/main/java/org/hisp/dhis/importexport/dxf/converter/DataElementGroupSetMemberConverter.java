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
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.GroupMemberImporter;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementGroupSetMemberConverter
    extends GroupMemberImporter implements XMLConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "dataElementGroupSetMembers";
    public static final String ELEMENT_NAME = "dataElementGroupSetMember";
    
    private static final String FIELD_DATAELEMENT_GROUP = "dataElementGroup";
    private static final String FIELD_DATAELEMENT_GROUP_SET = "dataElementGroupSet";
    private static final String FIELD_SORT_ORDER = "sortOrder";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;
    
    private Map<Object, Integer> dataElementGroupMapping;

    private Map<Object, Integer> dataElementGroupSetMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataElementGroupSetMemberConverter( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    /**
     * Constructor for read operations.
     */
    public DataElementGroupSetMemberConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> dataElementGroupMapping, 
        Map<Object, Integer> dataElementGroupSetMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataElementGroupMapping = dataElementGroupMapping;
        this.dataElementGroupSetMapping = dataElementGroupSetMapping;
    }    

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementGroupSet> groupSets = dataElementService.getDataElementGroupSets( params.getDataElementGroups() );
        
        Collection<DataElementGroup> groups = dataElementService.getDataElementGroups( params.getDataElements() );
        
        if ( groupSets != null && groupSets.size() > 0 && groups != null && groups.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataElementGroupSet groupSet : groupSets )
            {           
                int sortOrder = 1;
                
                for ( DataElementGroup group : groupSet.getMembers() )
                {                    
                    if ( groups.contains( group ) )
                    {
                        writer.openElement( ELEMENT_NAME );
                        
                        writer.writeElement( FIELD_DATAELEMENT_GROUP_SET, String.valueOf( groupSet.getId() ) );
                        writer.writeElement( FIELD_DATAELEMENT_GROUP, String.valueOf( group.getId() ) );
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
            
            association.setGroupId( dataElementGroupSetMapping.get( Integer.parseInt( values.get( FIELD_DATAELEMENT_GROUP_SET )) ) );
            association.setMemberId( dataElementGroupMapping.get( Integer.parseInt( values.get( FIELD_DATAELEMENT_GROUP ) ) ) );
            association.setSortOrder( Integer.parseInt( values.get( FIELD_SORT_ORDER ) ) );
            
            importObject( association, params );
        }
    }

    @Override
    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, GroupMemberType.DATAELEMENTGROUPSET, params );
    }
}
