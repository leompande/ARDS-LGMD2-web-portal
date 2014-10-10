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
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.HrDataSetService;
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
 * @version $Id: HrDataSetMemberConverter.java $
 */
public class HrDataSetMemberConverter
    extends HrGroupMemberImporter implements XMLHrConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "formMembers";
    public static final String ELEMENT_NAME = "formMember";
    
    private static final String FIELD_ATTRIBUTE = "fieldId";
    private static final String FIELD_HR_DATASET = "formId";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private HrDataSetService hrDataSetService;
    
    private AttributeService attributeService;
    
    private Map<Object, Integer> attributeMapping;
    
    private Map<Object, Integer> hrDataSetMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public HrDataSetMemberConverter( HrDataSetService hrDataSetService,
        AttributeService attributeService )
    {   
        this.hrDataSetService = hrDataSetService;
        this.attributeService = attributeService;
    }
    
    /**
     * Constructor for read operations.
     */
    public HrDataSetMemberConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportHrObjectService importObjectService,
        Map<Object, Integer> attributeMapping, 
        Map<Object, Integer> hrDataSetMapping )
    {   
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.attributeMapping = attributeMapping;
        this.hrDataSetMapping = hrDataSetMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, HrExportParams params )
    {
        Collection<HrDataSet> hrDataSets = hrDataSetService.getHrDataSets( params.getHrDataSets() );
        
        Collection<Attribute> attributes = attributeService.getAttributes( params.getAttributes() );
        
        if ( hrDataSets != null && hrDataSets.size() > 0 && attributes != null && attributes.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( HrDataSet hrDataSet : hrDataSets )
            {
                if ( hrDataSet.getAttribute() != null )
                {
                    for ( Attribute attribute : hrDataSet.getAttribute() )
                    {
                        if ( attributes.contains( attribute ) )
                        {
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_HR_DATASET, String.valueOf( hrDataSet.getId() ) );
                            writer.writeElement( FIELD_ATTRIBUTE, String.valueOf( attribute.getId() ) );
                            
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
            
            association.setGroupId( hrDataSetMapping.get( Integer.parseInt( values.get( FIELD_HR_DATASET ) ) ) );            
            association.setMemberId( attributeMapping.get( Integer.parseInt( values.get( FIELD_ATTRIBUTE ) ) ) );
            
            importObject( association, params );
            
        }
    }

    @Override
    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, HrGroupMemberType.HR_DATASET, params );
    }
}
