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
import java.util.Map;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.OrganisationUnitRelationshipImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.importexport.dhis14.util.Dhis14DateUtil;
import org.hisp.dhis.system.util.MimicingHashMap;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitHierarchyConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class OrganisationUnitHierarchyConverter
    extends OrganisationUnitRelationshipImporter implements XMLConverter
{
    public static final String ELEMENT_NAME = "OrgHierarchy";
    
    private static final String FIELD_ID = "OrgHierarchyID";
    private static final String FIELD_LEVEL = "OrgUnitLevel";
    private static final String FIELD_STRUCTURE = "OrgUnitStructureID";
    private static final String FIELD_CHILD = "OrgUnitChildID";
    private static final String FIELD_PARENT = "OrgUnitParentID";
    private static final String FIELD_LAST_USER = "LastUserID";
    private static final String FIELD_LAST_UPDATED = "LastUpdated";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private static final int STRUCTURE_ID = 1;

    private Map<Object, Integer> organisationUnitMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public OrganisationUnitHierarchyConverter( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param importObjectService the importObjectService to use.
     * @param organisationUnitService the organisationUnitService to use.
     * @param organisationUnitMapping the organisationUnitMapping to use.
     */
    public OrganisationUnitHierarchyConverter( ImportObjectService importObjectService,
        OrganisationUnitService organisationUnitService )
    {
        this.importObjectService = importObjectService;
        this.organisationUnitService = organisationUnitService;
        this.organisationUnitMapping = new MimicingHashMap<Object, Integer>();
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( params.getOrganisationUnits() );
        
        if ( units != null && units.size() > 0 )
        {
            int id = 0;
            
            for ( OrganisationUnit unit : units )
            {
                if ( unit.getParent() != null )
                {
                    int level = unit.getOrganisationUnitLevel();
                    
                    writer.openElement( ELEMENT_NAME );
                    
                    writer.writeElement( FIELD_ID, String.valueOf( ++id ) );                    
                    writer.writeElement( FIELD_LEVEL, String.valueOf( level ) );
                    writer.writeElement( FIELD_STRUCTURE, String.valueOf( STRUCTURE_ID ) );
                    writer.writeElement( FIELD_CHILD, String.valueOf( unit.getId() ) );
                    writer.writeElement( FIELD_PARENT, String.valueOf( unit.getParent().getId() ) );
                    writer.writeElement( FIELD_LAST_USER, String.valueOf( 1 ) );
                    writer.writeElement( FIELD_LAST_UPDATED, Dhis14DateUtil.getDateString( unit.getLastUpdated() ) );
                    
                    writer.closeElement();
                }
            }
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.SET );

        Map<String, String> values = reader.readElements( ELEMENT_NAME );
        
        Integer parentId = organisationUnitMapping.get( Integer.parseInt( values.get( FIELD_PARENT ) ) );
        Integer childId = organisationUnitMapping.get( Integer.parseInt( values.get( FIELD_CHILD ) ) );
        
        if ( parentId != null && childId != null && !parentId.equals( childId ) ) // Parent id equals child id for root
        {
            association.setGroupId( parentId );
            association.setMemberId( childId );
            
            read( association, GroupMemberType.ORGANISATIONUNITRELATIONSHIP, params );
        }        
    }      
}
