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
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
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
import org.hisp.dhis.organisationunit.OrganisationUnitService;

/**
 * @author Lars Helge Overland
 * @version $Id: GroupSetConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class DataSetSourceAssociationConverter
    extends GroupMemberImporter implements XMLConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "dataSetSourceAssociations";
    public static final String ELEMENT_NAME = "dataSetSourceAssociation";
    
    private static final String FIELD_DATASET = "dataSet";
    private static final String FIELD_SOURCE = "source";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;
    
    private OrganisationUnitService organisationUnitService;
    
    private Map<Object, Integer> dataSetMapping;
    
    private Map<Object, Integer> sourceMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataSetSourceAssociationConverter( DataSetService dataSetService,
        OrganisationUnitService organisationUnitService )
    {   
        this.dataSetService = dataSetService;
        this.organisationUnitService = organisationUnitService;
    }
    
    /**
     * Constructor for read operations.
     */
    public DataSetSourceAssociationConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> dataSetMapping, 
        Map<Object, Integer> sourceMapping )
    {   
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataSetMapping = dataSetMapping;
        this.sourceMapping = sourceMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataSet> dataSets = dataSetService.getDataSets( params.getDataSets() );
        
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( params.getOrganisationUnits() );
        
        if ( dataSets != null && dataSets.size() > 0 && units != null && units.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataSet dataSet : dataSets )
            {
                if ( dataSet.getSources() != null )
                {
                    for ( OrganisationUnit source : dataSet.getSources() )
                    {
                        if ( units.contains( source ) )
                        {
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_DATASET, String.valueOf( dataSet.getId() ) );
                            writer.writeElement( FIELD_SOURCE, String.valueOf( source.getId() ) );
                            
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
            
            association.setGroupId( dataSetMapping.get( Integer.parseInt( values.get( FIELD_DATASET ) ) ) );            
            association.setMemberId( sourceMapping.get( Integer.parseInt( values.get( FIELD_SOURCE ) ) ) );
            
            importObject( association, params );
        }
    }

    @Override
    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, GroupMemberType.DATASET_SOURCE, params );
    }
}
