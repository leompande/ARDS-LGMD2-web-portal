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
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
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
public class CategoryComboCategoryAssociationConverter
    extends GroupMemberImporter implements XMLConverter, Importer<GroupMemberAssociation>
{
    public static final String COLLECTION_NAME = "categoryComboCategoryAssociations";
    public static final String ELEMENT_NAME = "categoryComboCategoryAssociation";
    
    private static final String FIELD_CATEGORY_COMBO = "categoryCombo";
    private static final String FIELD_CATEGORY = "category";
    private static final String FIELD_SORT_ORDER = "sortOrder";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataElementCategoryService categoryService;
    
    private Map<Object, Integer> categoryComboMapping;
    
    private Map<Object, Integer> categoryMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public CategoryComboCategoryAssociationConverter( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryComboMapping the categoryComboMapping to use.
     * @param categoryMapping the categoryMapping to use.
     */
    public CategoryComboCategoryAssociationConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> categoryComboMapping,
        Map<Object, Integer> categoryMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.categoryComboMapping = categoryComboMapping;
        this.categoryMapping = categoryMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementCategoryCombo> categoryCombos = categoryService.getDataElementCategoryCombos( params.getCategoryCombos() );
        
        Collection<DataElementCategory> categories = categoryService.getDataElementCategories( params.getCategories() );
        
        if ( categoryCombos != null && categoryCombos.size() > 0 && categories != null && categories.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataElementCategoryCombo categoryCombo : categoryCombos )
            {
                if ( categoryCombo.getCategories() != null )
                {
                    int sortOrder = 1;
                    
                    for ( DataElementCategory category : categoryCombo.getCategories() )
                    {
                    	if ( categories.contains( category ) )
                    	{
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_CATEGORY_COMBO, String.valueOf( categoryCombo.getId() ) );
                            writer.writeElement( FIELD_CATEGORY, String.valueOf( category.getId() ) );
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
            
            final GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.SET );
            
            association.setGroupId( categoryComboMapping.get( Integer.parseInt( values.get( FIELD_CATEGORY_COMBO ) ) ) );            
            association.setMemberId( categoryMapping.get( Integer.parseInt( values.get( FIELD_CATEGORY ) ) ) );
            association.setSortOrder( values.containsKey( FIELD_SORT_ORDER ) ? 
                Integer.parseInt( values.get( FIELD_SORT_ORDER ) ) : counter.count( association.getGroupId() ) );
            
            importObject( association, params );
        }
    }

    public void importObject( GroupMemberAssociation object, ImportParams params )
    {
        read( object, GroupMemberType.CATEGORYCOMBO_CATEGORY, params );        
    }
}
