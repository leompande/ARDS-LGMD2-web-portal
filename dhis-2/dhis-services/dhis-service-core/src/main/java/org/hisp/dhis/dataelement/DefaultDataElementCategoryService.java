package org.hisp.dhis.dataelement;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericDimensionalObjectStore;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 */
@Transactional
public class DefaultDataElementCategoryService
    implements DataElementCategoryService
{
    private static final Log log = LogFactory.getLog( DefaultDataElementCategoryService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CategoryStore categoryStore;

    public void setCategoryStore( CategoryStore categoryStore )
    {
        this.categoryStore = categoryStore;
    }

    private GenericDimensionalObjectStore<DataElementCategoryOption> categoryOptionStore;

    public void setCategoryOptionStore( GenericDimensionalObjectStore<DataElementCategoryOption> categoryOptionStore )
    {
        this.categoryOptionStore = categoryOptionStore;
    }

    private CategoryComboStore categoryComboStore;

    public void setCategoryComboStore( CategoryComboStore categoryComboStore )
    {
        this.categoryComboStore = categoryComboStore;
    }

    private CategoryOptionComboStore categoryOptionComboStore;

    public void setCategoryOptionComboStore( CategoryOptionComboStore categoryOptionComboStore )
    {
        this.categoryOptionComboStore = categoryOptionComboStore;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Category
    // -------------------------------------------------------------------------

    public int addDataElementCategory( DataElementCategory dataElementCategory )
    {
        return categoryStore.save( dataElementCategory );
    }

    public void updateDataElementCategory( DataElementCategory dataElementCategory )
    {
        categoryStore.update( dataElementCategory );
    }

    public void deleteDataElementCategory( DataElementCategory dataElementCategory )
    {
        categoryStore.delete( dataElementCategory );
    }

    public Collection<DataElementCategory> getDataDimensionDataElementCategories()
    {
        Collection<DataElementCategory> categories = getAllDataElementCategories();
        
        FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            public boolean retain( DataElementCategory category )
            {
                return category != null && category.isDataDimension();
            }            
        } );
        
        return categories;
    }
    
    public Collection<DataElementCategory> getAllDataElementCategories()
    {
        return i18n( i18nService, categoryStore.getAll() );
    }

    public DataElementCategory getDataElementCategory( int id )
    {
        return i18n( i18nService, categoryStore.get( id ) );
    }

    public DataElementCategory getDataElementCategory( String uid )
    {
        return i18n( i18nService, categoryStore.getByUid( uid ) );
    }

    public Collection<DataElementCategory> getDataElementCategories( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategory> categories = getAllDataElementCategories();

        return identifiers == null ? categories : FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            public boolean retain( DataElementCategory object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }
    
    public Collection<DataElementCategory> getDataElementCategoriesByUid( Collection<String> uids )
    {
        return categoryStore.getByUid( uids );
    }

    public DataElementCategory getDataElementCategoryByName( String name )
    {
        List<DataElementCategory> dataElementCategories = new ArrayList<DataElementCategory>(
            categoryStore.getAllEqName( name ) );

        if ( dataElementCategories.isEmpty() )
        {
            return null;
        }

        return i18n( i18nService, dataElementCategories.get( 0 ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoriesByConcept( Concept concept )
    {
        return i18n( i18nService, categoryStore.getByConcept( concept ) );
    }
    
    public Collection<DataElementCategory> getDisaggregationCategories()
    {
        return i18n( i18nService, categoryStore.getCategoriesByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_DISAGGREGATION ) );
    }

    public Collection<DataElementCategory> getDisaggregationDataDimensionCategories()
    {
        Collection<DataElementCategory> categories = getDisaggregationCategories();
        
        FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            public boolean retain( DataElementCategory category )
            {
                return category != null && category.isDataDimension();
            }            
        } );
        
        return categories;
    }

    public Collection<DataElementCategory> getAttributeCategories()
    {
        return i18n( i18nService, categoryStore.getCategoriesByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_ATTTRIBUTE ) );        
    }

    public Collection<DataElementCategory> getAttributeDataDimensionCategories()
    {
        Collection<DataElementCategory> categories = getAttributeCategories();

        FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            public boolean retain( DataElementCategory category )
            {
                return category != null && category.isDataDimension();
            }            
        } );
        
        return categories;        
    }
    
    @Override
    public Collection<DataElementCategory> getDataElementCategoryBetween( int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoryBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    public Collection<DataElementCategory> getDataElementCategoriesBetween( int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllOrderedName( first, max ) );
    }

    public Collection<DataElementCategory> getDataElementCategoriesBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    public int getDataElementCategoryCount()
    {
        return categoryStore.getCount();
    }

    public int getDataElementCategoryCountByName( String name )
    {
        return categoryStore.getCountLikeName( name );
    }

    // -------------------------------------------------------------------------
    // CategoryOption
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        return categoryOptionStore.save( dataElementCategoryOption );
    }

    public void updateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptionStore.update( dataElementCategoryOption );
    }

    public void deleteDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptionStore.delete( dataElementCategoryOption );
    }

    public DataElementCategoryOption getDataElementCategoryOption( int id )
    {
        return i18n( i18nService, categoryOptionStore.get( id ) );
    }

    public DataElementCategoryOption getDataElementCategoryOption( String uid )
    {
        return i18n( i18nService, categoryOptionStore.getByUid( uid ) );
    }

    public DataElementCategoryOption getDataElementCategoryOptionByName( String name )
    {
        return i18n( i18nService, categoryOptionStore.getByName( name ) );
    }

    public DataElementCategoryOption getDataElementCategoryOptionByCode( String code )
    {
        return i18n( i18nService, categoryOptionStore.getByCode( code ) );
    }

    public Collection<DataElementCategoryOption> getDataElementCategoryOptions( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOption> categoryOptions = getAllDataElementCategoryOptions();

        return identifiers == null ? categoryOptions : FilterUtils.filter( categoryOptions,
            new Filter<DataElementCategoryOption>()
            {
                public boolean retain( DataElementCategoryOption object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsByUid( Collection<String> uids )
    {
        return categoryOptionStore.getByUid( uids );
    }

    public Collection<DataElementCategoryOption> getAllDataElementCategoryOptions()
    {
        return i18n( i18nService, categoryOptionStore.getAll() );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetween( int first, int max )
    {
        return i18n( i18nService, categoryOptionStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryOptionStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsByConcept( Concept concept )
    {
        return categoryOptionStore.getByConcept( concept );
    }

    @Override
    public int getDataElementCategoryOptionCount()
    {
        return categoryOptionStore.getCount();
    }

    @Override
    public int getDataElementCategoryOptionCountByName( String name )
    {
        return categoryOptionStore.getCountLikeName( name );
    }

    // -------------------------------------------------------------------------
    // CategoryCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        return categoryComboStore.save( dataElementCategoryCombo );
    }

    public void updateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        categoryComboStore.save( dataElementCategoryCombo );
    }

    public void deleteDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        categoryComboStore.delete( dataElementCategoryCombo );
    }

    public Collection<DataElementCategoryCombo> getAllDataElementCategoryCombos()
    {
        return i18n( i18nService, categoryComboStore.getAll() );
    }

    public DataElementCategoryCombo getDataElementCategoryCombo( int id )
    {
        return i18n( i18nService, categoryComboStore.get( id ) );
    }

    public DataElementCategoryCombo getDataElementCategoryCombo( String uid )
    {
        return i18n( i18nService, categoryComboStore.getByUid( uid ) );
    }

    public Collection<DataElementCategoryCombo> getDataElementCategoryCombos( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryCombo> categoryCombos = getAllDataElementCategoryCombos();

        return identifiers == null ? categoryCombos : FilterUtils.filter( categoryCombos,
            new Filter<DataElementCategoryCombo>()
            {
                public boolean retain( DataElementCategoryCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public DataElementCategoryCombo getDataElementCategoryComboByName( String name )
    {
        return i18n( i18nService, categoryComboStore.getByName( name ) );
    }
    
    public DataElementCategoryCombo getDefaultDataElementCategoryCombo()
    {
        return getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
    }

    public int getDataElementCategoryComboCount()
    {
        return categoryComboStore.getCount();
    }

    public int getDataElementCategoryComboCountByName( String name )
    {
        return categoryComboStore.getCountLikeName( name );
    }

    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetween( int first, int max )
    {
        return i18n( i18nService, categoryComboStore.getAllOrderedName( first, max ) );
    }

    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryComboStore.getAllLikeNameOrderedName( name, first, max ) );
    }
    
    public Collection<DataElementCategoryCombo> getDisaggregationCategoryCombos()
    {
        return i18n( i18nService, categoryComboStore.getCategoryCombosByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_DISAGGREGATION ) );
    }

    public Collection<DataElementCategoryCombo> getAttributeCategoryCombos()
    {
        return i18n( i18nService, categoryComboStore.getCategoryCombosByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_ATTTRIBUTE ) );
    }
    
    // -------------------------------------------------------------------------
    // CategoryOptionCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        return categoryOptionComboStore.save( dataElementCategoryOptionCombo );
    }

    public void updateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        categoryOptionComboStore.update( dataElementCategoryOptionCombo );
    }

    public void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        categoryOptionComboStore.delete( dataElementCategoryOptionCombo );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( int id )
    {
        return categoryOptionComboStore.get( id );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( String uid )
    {
        return categoryOptionComboStore.getByUid( uid );
    }

    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos(
        final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOptionCombo> categoryOptionCombos = getAllDataElementCategoryOptionCombos();

        return identifiers == null ? categoryOptionCombos : FilterUtils.filter( categoryOptionCombos,
            new Filter<DataElementCategoryOptionCombo>()
            {
                public boolean retain( DataElementCategoryOptionCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombosByUid( Collection<String> uids )
    {
        return categoryOptionComboStore.getByUid( uids );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo(
        Collection<DataElementCategoryOption> categoryOptions )
    {
        for ( DataElementCategoryOptionCombo categoryOptionCombo : getAllDataElementCategoryOptionCombos() )
        {
            if ( CollectionUtils.isEqualCollection( categoryOptions, categoryOptionCombo.getCategoryOptions() ) )
            {
                return categoryOptionCombo;
            }
        }

        return null;
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo(
        DataElementCategoryOptionCombo categoryOptionCombo )
    {
        for ( DataElementCategoryOptionCombo dcoc : getAllDataElementCategoryOptionCombos() )
        {
            // -----------------------------------------------------------------
            // Hibernate puts proxies on associations and makes the native
            // equals methods unusable
            // -----------------------------------------------------------------

            if ( dcoc.equalsOnName( categoryOptionCombo ) )
            {
                return dcoc;
            }
        }

        return null;
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( DataElementCategoryCombo categoryCombo, Set<DataElementCategoryOption> categoryOptions )
    {
        return categoryOptionComboStore.getCategoryOptionCombo( categoryCombo, categoryOptions );
    }
    
    public Collection<DataElementCategoryOptionCombo> getAllDataElementCategoryOptionCombos()
    {
        return categoryOptionComboStore.getAll();
    }

    public void generateDefaultDimension()
    {
        // ---------------------------------------------------------------------
        // DataElementCategoryOption
        // ---------------------------------------------------------------------

        DataElementCategoryOption categoryOption = new DataElementCategoryOption(
            DataElementCategoryOption.DEFAULT_NAME );

        addDataElementCategoryOption( categoryOption );

        // ---------------------------------------------------------------------
        // DataElementCategory
        // ---------------------------------------------------------------------

        DataElementCategory category = new DataElementCategory( DataElementCategory.DEFAULT_NAME );
        category.addDataElementCategoryOption( categoryOption );
        addDataElementCategory( category );

        // ---------------------------------------------------------------------
        // DataElementCategoryCombo
        // ---------------------------------------------------------------------

        DataElementCategoryCombo categoryCombo = new DataElementCategoryCombo( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        categoryCombo.addDataElementCategory( category );
        addDataElementCategoryCombo( categoryCombo );

        // ---------------------------------------------------------------------
        // DataElementCategoryOptionCombo
        // ---------------------------------------------------------------------

        DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();

        categoryOptionCombo.setCategoryCombo( categoryCombo );
        categoryOptionCombo.addDataElementCategoryOption( categoryOption );

        addDataElementCategoryOptionCombo( categoryOptionCombo );

        Set<DataElementCategoryOptionCombo> categoryOptionCombos = new HashSet<DataElementCategoryOptionCombo>();
        categoryOptionCombos.add( categoryOptionCombo );
        categoryCombo.setOptionCombos( categoryOptionCombos );

        updateDataElementCategoryCombo( categoryCombo );

        categoryOption.setCategoryOptionCombos( categoryOptionCombos );
        updateDataElementCategoryOption( categoryOption );
    }

    public DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo()
    {
        DataElementCategoryCombo categoryCombo = getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        return categoryCombo.getOptionCombos().iterator().next();
    }

    public Collection<DataElementOperand> populateOperands( Collection<DataElementOperand> operands )
    {
        for ( DataElementOperand operand : operands )
        {
            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            DataElementCategoryOptionCombo categoryOptionCombo = getDataElementCategoryOptionCombo( operand
                .getOptionComboId() );

            operand.updateProperties( dataElement, categoryOptionCombo );
        }

        return operands;
    }

    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements )
    {
        return getOperands( dataElements, false );
    }

    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements, boolean includeTotals )
    {
        List<DataElementOperand> operands = new ArrayList<DataElementOperand>();

        for ( DataElement dataElement : dataElements )
        {
            if ( !dataElement.getCategoryCombo().isDefault() && includeTotals )
            {
                DataElementOperand operand = new DataElementOperand();
                operand.updateProperties( dataElement );

                operands.add( operand );
            }

            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo()
                .getSortedOptionCombos() )
            {
                DataElementOperand operand = new DataElementOperand();
                operand.updateProperties( dataElement, categoryOptionCombo );

                operands.add( operand );
            }
        }

        return operands;
    }

    public Collection<DataElementOperand> getOperandsLikeName( String name )
    {
        Collection<DataElement> dataElements = dataElementService.getDataElementsLikeName( name );

        return getOperands( dataElements );
    }

    public Collection<DataElementOperand> getFullOperands( Collection<DataElement> dataElements )
    {
        Collection<DataElementOperand> operands = new ArrayList<DataElementOperand>();

        for ( DataElement dataElement : dataElements )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getOptionCombos() )
            {
                DataElementOperand operand = new DataElementOperand( dataElement, categoryOptionCombo );
                operand.updateProperties( dataElement, categoryOptionCombo );

                operands.add( operand );
            }
        }

        return operands;
    }

    public void generateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        categoryCombo.generateOptionCombos();

        for ( DataElementCategoryOptionCombo optionCombo : categoryCombo.getOptionCombos() )
        {
            categoryCombo.getOptionCombos().add( optionCombo );
            addDataElementCategoryOptionCombo( optionCombo );
        }

        updateDataElementCategoryCombo( categoryCombo );
    }

    public void updateOptionCombos( DataElementCategory category )
    {
        for ( DataElementCategoryCombo categoryCombo : getAllDataElementCategoryCombos() )
        {
            if ( categoryCombo.getCategories().contains( category ) )
            {
                updateOptionCombos( categoryCombo );
            }
        }
    }

    public void updateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        List<DataElementCategoryOptionCombo> generatedOptionCombos = categoryCombo.generateOptionCombosList();
        Set<DataElementCategoryOptionCombo> persistedOptionCombos = categoryCombo.getOptionCombos();

        boolean modified = false;
        
        for ( DataElementCategoryOptionCombo optionCombo : generatedOptionCombos )
        {
            if ( !persistedOptionCombos.contains( optionCombo ) )
            {                
                categoryCombo.getOptionCombos().add( optionCombo );
                addDataElementCategoryOptionCombo( optionCombo );

                log.info( "Added missing category option combo: " + optionCombo + " for category combo: " + categoryCombo.getName() );
                modified = true;
            }
        }

        if ( modified )
        {        
            updateDataElementCategoryCombo( categoryCombo );
        }
    }
    
    public void updateAllOptionCombos()
    {
        Collection<DataElementCategoryCombo> categoryCombos = getAllDataElementCategoryCombos();
        
        for ( DataElementCategoryCombo categoryCombo : categoryCombos )
        {
            updateOptionCombos( categoryCombo );
        }
    }

    public Map<String, Integer> getDataElementCategoryOptionComboUidIdMap()
    {
        Map<String, Integer> map = new HashMap<String, Integer>();

        for ( DataElementCategoryOptionCombo coc : getAllDataElementCategoryOptionCombos() )
        {
            map.put( coc.getUid(), coc.getId() );
        }

        return map;
    }

    @Override
    public int getDataElementCategoryOptionComboCount()
    {
        return categoryOptionComboStore.getCount();
    }

    @Override
    public int getDataElementCategoryOptionComboCountByName( String name )
    {
        return categoryOptionComboStore.getCountLikeName( name );
    }
}
