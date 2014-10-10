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

import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.hierarchy.HierarchyViolationException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Abyot Asalefew
 */
public interface DataElementCategoryService
{
    String ID = DataElementCategoryService.class.getName();

    // -------------------------------------------------------------------------
    // Category
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategory.
     *
     * @param dataElementCategory the DataElementCategory to add.
     * @return a generated unique id of the added Category.
     */
    int addDataElementCategory( DataElementCategory dataElementCategory );

    /**
     * Updates a DataElementCategory.
     *
     * @param dataElementCategory the DataElementCategory to update.
     */
    void updateDataElementCategory( DataElementCategory dataElementCategory );

    /**
     * Deletes a DataElementCategory. The DataElementCategory is also removed from any
     * DataElementCategoryCombos if it is a member of. It is not possible to delete a
     * DataElementCategory with options.
     *
     * @param dataElementCategory the DataElementCategory to delete.
     * @throws HierarchyViolationException if the DataElementCategory has children.
     */
    void deleteDataElementCategory( DataElementCategory dataElementCategory );

    /**
     * Returns a DataElementCategory.
     *
     * @param id the id of the DataElementCategory to return.
     * @return the DataElementCategory with the given id, or null if no match.
     */
    DataElementCategory getDataElementCategory( int id );

    /**
     * Returns a DataElementCategory.
     *
     * @param uid the uid of the DataElementCategory to return.
     * @return the DataElementCategory with the given uid, or null if no match.
     */
    DataElementCategory getDataElementCategory( String uid );

    /**
     * Retrieves the DataElementCategories with the given identifiers.
     *
     * @param identifiers the identifiers of the DataElementCategories to retrieve.
     * @return a collection of DataElementCategories.
     */
    Collection<DataElementCategory> getDataElementCategories( Collection<Integer> identifiers );

    /**
     * Retrieves the DataElementCategories with the given uids.
     *
     * @param uids the uids of the DataElementCategories to retrieve.
     * @return a collection of DataElementCategories.
     */
    Collection<DataElementCategory> getDataElementCategoriesByUid( Collection<String> uids );

    /**
     * Retrieves the DataElementCategory with the given name.
     *
     * @param name the name of the DataElementCategory to retrieve.
     * @return the DataElementCategory.
     */
    DataElementCategory getDataElementCategoryByName( String name );

    /**
     * Returns DataElementCategories which are considered data dimensions.
     *
     * @return DataElementCategories which are considered data dimensions.
     */
    Collection<DataElementCategory> getDataDimensionDataElementCategories();

    /**
     * Returns all DataElementCategories.
     *
     * @return a collection of all DataElementCategories.
     */
    Collection<DataElementCategory> getAllDataElementCategories();

    /**
     * Retrieves all DataElementCategories of dimension type disaggregation.
     *
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategory> getDisaggregationCategories();

    /**
     * Retrieves all DataElementCategories of dimension type disaggregation and
     * data dimensional.
     *
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategory> getDisaggregationDataDimensionCategories();

    /**
     * Retrieves all DataElementCategories of dimension type attribute.
     *
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategory> getAttributeCategories();

    /**
     * Retrieves all DataElementCategories of dimension type attribute and
     * data dimensional.
     *
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategory> getAttributeDataDimensionCategories();

    // -------------------------------------------------------------------------
    // CategoryOption
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategoryOption.
     *
     * @param dataElementCategoryOption the DataElementCategoryOption to add.
     * @return a generated unique id of the added DataElementCategoryOption.
     */
    int addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption );

    /**
     * Updates a DataElementCategoryOption.
     *
     * @param dataElementCategoryOption the DataElementCategoryOption to update.
     */
    void updateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption );

    /**
     * Deletes a DataElementCategoryOption.
     *
     * @param dataElementCategoryOption
     */
    void deleteDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption );

    /**
     * Returns a DataElementCategoryOption.
     *
     * @param id the id of the DataElementCategoryOption to return.
     * @return the DataElementCategoryOption with the given id, or null if no match.
     */
    DataElementCategoryOption getDataElementCategoryOption( int id );

    /**
     * Returns a DataElementCategoryOption.
     *
     * @param uid the id of the DataElementCategoryOption to return.
     * @return the DataElementCategoryOption with the given uid, or null if no match.
     */
    DataElementCategoryOption getDataElementCategoryOption( String uid );

    /**
     * Retrieves the DataElementCategoryOptions with the given identifiers.
     *
     * @param identifiers the identifiers of the DataElementCategoryOption to retrieve.
     * @return a Collection of DataElementCategoryOptions.
     */
    Collection<DataElementCategoryOption> getDataElementCategoryOptions( Collection<Integer> identifiers );

    /**
     * Retrieves the DataElementCategoryOptions with the given uids.
     *
     * @param uids the uids of the DataElementCategoryOption to retrieve.
     * @return a Collection of DataElementCategoryOptions.
     */
    Collection<DataElementCategoryOption> getDataElementCategoryOptionsByUid( Collection<String> uids );

    /**
     * Retrieves the DataElementCategoryOption with the given name.
     *
     * @param name the name.
     * @return the DataElementCategoryOption with the given name.
     */
    DataElementCategoryOption getDataElementCategoryOptionByName( String name );

    /**
     * Retrieves the DataElementCategoryOption with the given code.
     *
     * @param code the code.
     * @return the DataElementCategoryOption with the given code.
     */
    DataElementCategoryOption getDataElementCategoryOptionByCode( String code );

    /**
     * Returns all DataElementCategoryOptions.
     *
     * @return a collection of all DataElementCategoryOptions, or an empty collection if there
     * are no DataElementCategoryOptions.
     */
    Collection<DataElementCategoryOption> getAllDataElementCategoryOptions();

    /**
     * Returns all DataElementCategoryOptions for a given concept
     *
     * @param concept the Concept
     * @return a collection of all DataElementCategoryOptions, or an empty collection if there
     * are no DataElementCategoryOptions.
     */
    Collection<DataElementCategoryOption> getDataElementCategoryOptionsByConcept( Concept concept );

    // -------------------------------------------------------------------------
    // CategoryCombo
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategoryCombo.
     *
     * @param dataElementCategoryCombo the DataElementCategoryCombo to add.
     * @return the generated identifier.
     */
    int addDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo );

    /**
     * Updates a DataElementCategoryCombo.
     *
     * @param dataElementCategoryCombo the DataElementCategoryCombo to update.
     */
    void updateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo );

    /**
     * Deletes a DataElementCategoryCombo.
     *
     * @param dataElementCategoryCombo the DataElementCategoryCombo to delete.
     */
    void deleteDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo );

    /**
     * Retrieves a DataElementCategoryCombo with the given identifier.
     *
     * @param id the identifier of the DataElementCategoryCombo to retrieve.
     * @return the DataElementCategoryCombo.
     */
    DataElementCategoryCombo getDataElementCategoryCombo( int id );

    /**
     * Retrieves a DataElementCategoryCombo with the given uid.
     *
     * @param uid the identifier of the DataElementCategoryCombo to retrieve.
     * @return the DataElementCategoryCombo.
     */
    DataElementCategoryCombo getDataElementCategoryCombo( String uid );

    /**
     * Retrieves the DataElementCategoryCombo with the given identifiers.
     *
     * @param identifiers the identifiers.
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategoryCombo> getDataElementCategoryCombos( Collection<Integer> identifiers );

    /**
     * Retrieves the DataElementCategoryCombo with the given name.
     *
     * @param name the name of the DataElementCategoryCombo to retrieve.
     * @return the DataElementCategoryCombo.
     */
    DataElementCategoryCombo getDataElementCategoryComboByName( String name );
    
    /**
     * Returns the default category combo.
     */
    DataElementCategoryCombo getDefaultDataElementCategoryCombo();

    /**
     * Retrieves all DataElementCategoryCombos.
     *
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategoryCombo> getAllDataElementCategoryCombos();

    /**
     * Retrieves all DataElementCategoryCombos of dimension type disaggregation.
     *
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategoryCombo> getDisaggregationCategoryCombos();

    /**
     * Retrieves all DataElementCategoryCombos of dimension type attribute.
     *
     * @return a collection of DataElementCategoryCombos.
     */
    Collection<DataElementCategoryCombo> getAttributeCategoryCombos();

    // -------------------------------------------------------------------------
    // CategoryOptionCombo
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategoryOptionCombo.
     *
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *                                       to add.
     * @return the generated identifier.
     */
    int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Updates a DataElementCategoryOptionCombo.
     *
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *                                       to update.
     */
    void updateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Deletes a DataElementCategoryOptionCombo.
     *
     * @param dataElementCategoryOptionCombo the DataElementCategoryOptionCombo
     *                                       to delete.
     */
    void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo );

    /**
     * Retrieves the DataElementCategoryOptionCombo with the given identifier.
     *
     * @param id the identifier of the DataElementCategoryOptionCombo.
     * @return the DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( int id );

    /**
     * Retrieves the DataElementCategoryOptionCombo with the given uid.
     *
     * @param uid the uid of the DataElementCategoryOptionCombo.
     * @return the DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( String uid );

    /**
     * Retrieves the DataElementCategoryOptionCombos with the given identifiers.
     *
     * @param identifiers the identifiers of the DataElementCategoryOptionCombos.
     * @return a Collection of DataElementCategoryOptionCombos.
     */
    Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos( Collection<Integer> identifiers );

    Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombosByUid( Collection<String> uids );

    /**
     * Retrieves the DataElementCategoryOptionCombo with the given Collection
     * of DataElementCategoryOptions.
     *
     * @param categoryOptions
     * @return
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( Collection<DataElementCategoryOption> categoryOptions );

    /**
     * Retrieves a DataElementCategoryOptionCombo.
     *
     * @param categoryOptionCombo the DataElementCategoryOptionCombo to
     *                            retrieve.
     * @return a DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo );

    DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( DataElementCategoryCombo categoryCombo, Set<DataElementCategoryOption> categoryOptions );

    /**
     * Retrieves all DataElementCategoryOptionCombos.
     *
     * @return a Collection of DataElementCategoryOptionCombos.
     */
    Collection<DataElementCategoryOptionCombo> getAllDataElementCategoryOptionCombos();

    /**
     * Generates and persists a default DataElementCategory,
     * DataElementCategoryOption, DataElementCategoryCombo and
     * DataElementCategoryOptionCombo.
     */
    void generateDefaultDimension();

    /**
     * Retrieves the default DataElementCategoryOptionCombo.
     *
     * @return the DataElementCategoryOptionCombo.
     */
    DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo();

    /**
     * Generates and persists DataElementCategoryOptionCombos for the given
     * DataElementCategoryCombo.
     *
     * @param categoryCombo the DataElementCategoryCombo.
     */
    void generateOptionCombos( DataElementCategoryCombo categoryCombo );

    /**
     * Invokes updateOptionCombos( DataElementCategoryCombo ) for all category
     * combos which the given category is a part of.
     *
     * @param category the DataElementCategory.
     */
    void updateOptionCombos( DataElementCategory category );

    /**
     * Generates the complete set of category option combos for the given
     * category combo and compares it to the set of persisted category option
     * combos. Those which are not matched are persisted.
     *
     * @param categoryCombo the DataElementCategoryCombo.
     */
    void updateOptionCombos( DataElementCategoryCombo categoryCombo );

    /**
     * Generates the complete set of category option combos for all category
     * combos.
     */
    void updateAllOptionCombos();

    /**
     * Populates all transient properties on each Operand in the given collection.
     *
     * @param operands the collection of Operands.
     * @return a collection of Operands.
     */
    public Collection<DataElementOperand> populateOperands( Collection<DataElementOperand> operands );

    /**
     * Gets the Operands for the given Collection of DataElements.
     *
     * @param dataElements the Collection of DataElements.
     * @return the Operands for the given Collection of DataElements.
     */
    Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements );

    /**
     * Gets the Operands for the given Collection of DataElements.
     *
     * @param dataElements  the Collection of DataElements.
     * @param includeTotals whether to include DataElement totals in the Collection of Operands.
     * @return the Operands for the given Collection of DataElements.
     */
    Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements, boolean includeTotals );

    /**
     * Gets the Operands for the DataElements with names like the given name.
     *
     * @param name the name.
     * @return the Operands for the DataElements with names like the given name.
     */
    Collection<DataElementOperand> getOperandsLikeName( String name );

    /**
     * Gets the Operands for the given Collection of DataElements. Operands will contain DataElement and CategoryOptionCombo object
     *
     * @param dataElements the Collection of DataElements.
     * @return the Operands for the given Collection of DataElements.
     */
    Collection<DataElementOperand> getFullOperands( Collection<DataElement> dataElements );

    Collection<DataElementCategory> getDataElementCategoriesBetween( int first, int max );

    Collection<DataElementCategory> getDataElementCategoriesBetweenByName( String name, int first, int max );

    /**
     * Returns all DataElementCategories for a given concept
     *
     * @param concept the Concept
     * @return a collection of all DataElementCategories, or an empty collection.
     */
    Collection<DataElementCategory> getDataElementCategoriesByConcept( Concept concept );

    Map<String, Integer> getDataElementCategoryOptionComboUidIdMap();

    int getDataElementCategoryCount();

    int getDataElementCategoryCountByName( String name );

    Collection<DataElementCategory> getDataElementCategoryBetween( int first, int max );

    Collection<DataElementCategory> getDataElementCategoryBetweenByName( String name, int first, int max );

    int getDataElementCategoryOptionCount();

    int getDataElementCategoryOptionCountByName( String name );

    Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetween( int first, int max );

    Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetweenByName( String name, int first, int max );

    int getDataElementCategoryOptionComboCount();

    int getDataElementCategoryOptionComboCountByName( String name );

    int getDataElementCategoryComboCount();

    int getDataElementCategoryComboCountByName( String name );

    Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetween( int first, int max );

    Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetweenByName( String name, int first, int max );
}
