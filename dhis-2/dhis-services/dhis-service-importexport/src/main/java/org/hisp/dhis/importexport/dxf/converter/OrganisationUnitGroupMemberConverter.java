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
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitGroupMemberConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class OrganisationUnitGroupMemberConverter
    extends GroupMemberImporter implements XMLConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "organisationUnitGroupMembers";
    public static final String ELEMENT_NAME = "organisationUnitGroupMember";
    
    private static final String FIELD_ORGANISATION_UNIT = "organisationUnit";
    private static final String FIELD_ORGANISATION_UNIT_GROUP = "organisationUnitGroup";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;
    
    private OrganisationUnitService organisationUnitService;
    
    private Map<Object, Integer> organisationUnitMapping;
    
    private Map<Object, Integer> organisationUnitGroupMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public OrganisationUnitGroupMemberConverter( OrganisationUnitGroupService organisationUnitGroupService,
        OrganisationUnitService organisationUnitService )
    {   
        this.organisationUnitGroupService = organisationUnitGroupService;
        this.organisationUnitService = organisationUnitService;
    }
    
    /**
     * Constructor for read operations.
     */
    public OrganisationUnitGroupMemberConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> organisationUnitMapping, 
        Map<Object, Integer> organisationUnitGroupMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.organisationUnitMapping = organisationUnitMapping;
        this.organisationUnitGroupMapping = organisationUnitGroupMapping;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<OrganisationUnitGroup> groups = organisationUnitGroupService.getOrganisationUnitGroups( params.getOrganisationUnitGroups() );
        
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( params.getOrganisationUnits() );
        
        if ( groups != null && groups.size() > 0 && units != null && units.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( OrganisationUnitGroup group : groups )
            {
                if ( group.getMembers() != null )
                {
                    for ( OrganisationUnit unit : group.getMembers() )
                    {
                        if ( units.contains( unit ) )
                        {
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_ORGANISATION_UNIT_GROUP, String.valueOf( group.getId() ) );
                            writer.writeElement( FIELD_ORGANISATION_UNIT, String.valueOf( unit.getId() ) );
                            
                            writer.closeElement();
                        }
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
            
            association.setGroupId( organisationUnitGroupMapping.get( Integer.parseInt( values.get( FIELD_ORGANISATION_UNIT_GROUP ) ) ) );            
            association.setMemberId( organisationUnitMapping.get( Integer.parseInt( values.get( FIELD_ORGANISATION_UNIT ) ) ) );
            
            importObject( association, params );
        }
    }

    @Override
    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, GroupMemberType.ORGANISATIONUNITGROUP, params );
    }
}
