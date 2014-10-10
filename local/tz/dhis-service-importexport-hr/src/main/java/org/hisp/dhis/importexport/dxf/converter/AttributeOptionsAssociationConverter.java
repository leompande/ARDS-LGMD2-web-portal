package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.XMLHrConverter;
import org.hisp.dhis.importexport.importer.HrGroupMemberImporter;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class AttributeOptionsAssociationConverter
    extends HrGroupMemberImporter implements XMLHrConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "fieldOptionGroupMembers";
    public static final String ELEMENT_NAME = "fieldOptionGroupMember";
    
    private static final String FIELD_ATTRIBUTE_OPTION_GROUP = "combinationCategoryGroupId";
    private static final String FIELD_ATTRIBUTE_OPTION = "combinationCategoryId";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private AttributeOptionGroupService attributeOptionGroupService;
    
    private Map<Object, Integer> attributeOptionGroupMapping;
    
    private Map<Object, Integer> attributeOptionsMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public AttributeOptionsAssociationConverter( AttributeOptionGroupService attributeOptionGroupService )
    {
        this.attributeOptionGroupService = attributeOptionGroupService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param attributeOptionGroupMapping the attributeOptionGroupMapping to use.
     * @param attributeOptionsMapping the attributeOptionsMapping to use.
     */
    public AttributeOptionsAssociationConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportHrObjectService importObjectService,
        Map<Object, Integer> attributeOptionGroupMapping, 
        Map<Object, Integer> attributeOptionsMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.attributeOptionGroupMapping = attributeOptionGroupMapping;
        this.attributeOptionsMapping = attributeOptionsMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, HrExportParams params )
    {
        Collection<AttributeOptionGroup> attributeOptionGroups = attributeOptionGroupService.getAttributeOptionGroups( null );
        Collection<AttributeOptions> attributeOptions = attributeOptionGroupService.getGroupAttributeOptions( null );
        
        if ( attributeOptionGroups != null && attributeOptionGroups.size() > 0 && attributeOptions != null && attributeOptions.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( AttributeOptionGroup attributeOptionGroup : attributeOptionGroups )
            {
                if ( attributeOptionGroup.getMembers() != null )
                {
                	
                    for ( AttributeOptions attributeOption : attributeOptionGroup.getMembers() )
                    {
                        if ( attributeOptions.contains( attributeOption ) )
                        {
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_ATTRIBUTE_OPTION, String.valueOf( attributeOption.getId() ) );
                            writer.writeElement( FIELD_ATTRIBUTE_OPTION_GROUP, String.valueOf( attributeOptionGroup.getId() ) );
                            
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
            
            final GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.LIST );
            
            association.setMemberId( attributeOptionsMapping.get( Integer.parseInt( values.get( FIELD_ATTRIBUTE_OPTION ) ) ) );
            association.setGroupId( attributeOptionGroupMapping.get( Integer.parseInt( values.get( FIELD_ATTRIBUTE_OPTION_GROUP ) ) ) );
            
            importObject( association, params );
        }
    }

    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, HrGroupMemberType.ATTRIBUTEOPTION_GROUP_MEMBER, params );        
    }
}
