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
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.GroupMemberImporter;
import org.hisp.dhis.system.util.Counter;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CategoryCategoryOptionAssociationConverter
    extends GroupMemberImporter implements XMLConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "categoryCategoryOptionAssociations";
    public static final String ELEMENT_NAME = "categoryCategoryOptionAssociation";
    
    private static final String FIELD_CATEGORY = "category";
    private static final String FIELD_CATEGORY_OPTION = "categoryOption";
    private static final String FIELD_SORT_ORDER = "sortOrder";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataElementCategoryService categoryService;
    
    private Map<Object, Integer> categoryMapping;
    
    private Map<Object, Integer> categoryOptionMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public CategoryCategoryOptionAssociationConverter( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryMapping the categoryMapping to use.
     * @param categoryOptionMapping the categoryOptionMapping to use.
     */
    public CategoryCategoryOptionAssociationConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> categoryMapping, 
        Map<Object, Integer> categoryOptionMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.categoryMapping = categoryMapping;
        this.categoryOptionMapping = categoryOptionMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementCategory> categories = categoryService.getDataElementCategories( params.getCategories() );
        Collection<DataElementCategoryOption> categoryOptions = categoryService.getDataElementCategoryOptions( params.getCategoryOptions() );
        
        if ( categories != null && categories.size() > 0 && categoryOptions != null && categoryOptions.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataElementCategory category : categories )
            {
                if ( category.getCategoryOptions() != null )
                {
                    int sortOrder = 1;
                	
                    for ( DataElementCategoryOption categoryOption : category.getCategoryOptions() )
                    {
                        if ( categoryOptions.contains( categoryOption ) )
                        {
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_CATEGORY, String.valueOf( category.getId() ) );
                            writer.writeElement( FIELD_CATEGORY_OPTION, String.valueOf( categoryOption.getId() ) );
                            writer.writeElement( FIELD_SORT_ORDER, String.valueOf( sortOrder++ ) );
                            
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
        Counter<Integer> counter = new Counter<Integer>(); // Used for backwards compatibility
        
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.LIST );
            
            association.setGroupId( categoryMapping.get( Integer.parseInt( values.get( FIELD_CATEGORY ) ) ) );
            association.setMemberId( categoryOptionMapping.get( Integer.parseInt( values.get( FIELD_CATEGORY_OPTION ) ) ) );            
            association.setSortOrder( values.containsKey( FIELD_SORT_ORDER ) ? 
                Integer.parseInt( values.get( FIELD_SORT_ORDER ) ) : counter.count( association.getGroupId() ) );
            
            importObject( association, params );
        }
    }

    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, GroupMemberType.CATEGORY_CATEGORYOPTION, params );        
    }
}
