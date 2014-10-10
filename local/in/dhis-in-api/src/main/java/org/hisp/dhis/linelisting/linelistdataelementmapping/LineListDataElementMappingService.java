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
package org.hisp.dhis.linelisting.linelistdataelementmapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version LineListDataElementMappingService.java Oct 12, 2010 12:32:39 PM
 */
public interface LineListDataElementMappingService
{
    String ID = LineListDataElementMappingService.class.getName();

    final int VALID = 1;

    final int LINELISTGROUP_ID_NOT_NUMERIC = -1;

    final int LINELISTELEMENT_ID_NOT_NUMERIC = -2;

    final int LINELISTOPTION_ID_NOT_NUMERIC = -3;

    final int LINELISTGROUP_DOES_NOT_EXIST = -4;

    final int LINELISTELEMENT_DOES_NOT_EXIST = -5;

    final int LINELISTOPTION_DOES_NOT_EXIST = -6;

    final int LINELIST_EXPRESSION_NOT_WELL_FORMED = -7;

    final int DATAELEMENT_ID_NOT_NUMERIC = -8;

    final int CATEGORYOPTIONCOMBO_ID_NOT_NUMERIC = -9;

    final int DATAELEMENT_DOES_NOT_EXIST = -10;

    final int CATEGORYOPTIONCOMBO_DOES_NOT_EXIST = -11;

    final int DATAELEMENT_EXPRESSION_NOT_WELL_FORMED = -12;

    /**
     * Adds a new LineListDataElementMapping to the database.
     * 
     * @param lineListDataElementMapping The LineListDataElementMapping to add.
     * @return The generated identifier for this LineListDataElementMapping.
     */
    int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Updates an LineListDataElementMapping.
     * 
     * @param lineListDataElementMapping The LineListDataElementMapping to
     *        update.
     */
    void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Deletes an LineListDataElementMapping from the database.
     * 
     * @param id Identifier of the LineListDataElementMapping to delete.
     */
    void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Get the LineListDataElementMapping with the given identifier.
     * 
     * @param id The identifier.
     * @return an LineListDataElementMapping with the given identifier.
     */
    LineListDataElementMapping getLineListDataElementMapping( int id );

    /**
     * Gets all LineListDataElementMappings.
     * 
     * @return A collection with all LineListDataElementMappings.
     */
    Collection<LineListDataElementMapping> getAllLineListDataElementMappings();

    /**
     * Calculates the value of the given LineListDataElementMapping.
     * 
     * @param expression The LineListDataElementMapping.
     * @param source The Source.
     * @param period The Period.
     * @return The value of the given LineListDataElementMapping, or null if no
     *         values are registered for a given combination of LineListGroup,
     *         LineListElement,LineListOption, Source, and Period.
     */
    // Double getLineListDataElementMappingValue( LineListDataElementMapping
    // expression, Period period, Source source );
    /**
     * Returns all LineListGroups,Elements and Options included in the given
     * expression string.
     * 
     * @param expression The expression string.
     * @return A Set of LineListGroups,Elements and Options included in the
     *         expression string.
     */
    Set<DataElement> getDataElementsInExpression( String expression );

    Set<DataElementCategoryOptionCombo> getCategoryOptionCombosInExpression( String expression );

    Set<LineListGroup> getLineListGroupsInLineListDataElementMapping( String expression );

    Set<LineListElement> getLineListElementsInLineListDataElementMapping( String expression );

    Set<LineListOption> getLineListOptionsInLineListDataElementMapping( String expression );

    /**
     * Returns all operands included in an expression string. The operand is on
     * the form <data element id>.<category option combo id>.
     * 
     * @param expression The expression string.
     * @return A Set of Operands.
     */
    Set<DataElementOperand> getOperandsInDataElementExpression( String dataElementExpression );

    /**
     * Returns all operands included in an expression string. The operand is on
     * the form <line list group id>:<line list element id>.<line list option
     * id>.
     * 
     * @param expression The expression string.
     * @return A Set of Operands.
     */
    Set<LineListOperand> getOperandsInLineListDataElementMapping( String lineListDataElementMapping );

    /**
     * Converts the given expression based on the maps of corresponding data
     * element identifiers and category option combo identifiers.
     * 
     * @param expression the expression formula.
     * @param dataElementMapping the data element mapping.
     * @param categoryOptionComboMapping the category option combo mapping.
     * 
     * @return an expression which has converted its data element and category
     *         option combo identifiers with the corresponding entries in the
     *         mappings.
     */
    String convertDataElementExpression( String dataElementExpression, Map<Object, Integer> dataElementMapping,
        Map<Object, Integer> categoryOptionComboMapping );

    /**
     * Converts the given expression based on the maps of corresponding data
     * element identifiers and category option combo identifiers.
     * 
     * @param expression the expression formula.
     * @param lineListGroupMapping the line list group mapping.
     * @param lineListElementMapping the line list element mapping.
     * @param lineListOptionMapping the line list option mapping.
     * 
     * @return an expression which has converted its line list group, line list
     *         element and line list option identifiers with the corresponding
     *         entries in the mappings.
     */
    String convertLineListDataElementMapping( String lineListDataElementMapping,
        Map<Object, Integer> lineListGroupMapping, Map<Object, Integer> lineListElementMapping,
        Map<Object, Integer> lineListOptionMapping );

    /**
     * Tests whether the expression is valid. Returns a positive value if the
     * expression is valid, or a negative value if not.
     * 
     * @param formula the expression formula.
     * @return VALID if the expression is valid. LINELISTGROUP_ID_NOT_NUMERIC if
     *         the line list group is not a number.
     *         LINELISTELMENT_ID_NOT_NUMERICif the line list element id is not a
     *         number. LINELISTOPTION_ID_NOT_NUMERICif the line list option id
     *         is not a number. LINELISTGROUP_DOES_NOT_EXIST if the line list
     *         group does not exist. LINELISTELEMENT_DOES_NOT_EXIST if the line
     *         list element does not exist. LINELISTOPTION_DOES_NOT_EXIST if the
     *         line list option does not exist. EXPRESSION_NOT_WELL_FORMED if
     *         formula is not well formed.
     */
    int expressionIsValid( String dataElementFormula, String lineListFormula );

    /**
     * Creates an expression string containing Data Element and
     * LineListOptionCombo from a string consisting of identifiers.
     * 
     * @param expression The expression string.
     * @return An expression string containing Line List Group, Element and
     *         Option names and the names of the CategoryOptions in the
     *         CategoryOptionCombo.
     * @throws IllegalArgumentException if Data Element or LineListOptionCombo
     *         are not numeric or if Data Element or LineListOptionCombo.
     */
    String getDataElementExpressionDescription( String dataElementExpression );

    /**
     * Creates an expression string containing Line List Group, Element and
     * Option names from a string consisting of identifiers.
     * 
     * @param expression The expression string.
     * @return An expression string containing Line List Group, Element and
     *         Option names and the names of the CategoryOptions in the
     *         CategoryOptionCombo.
     * @throws IllegalArgumentException if line list group, element or option id
     *         are not numeric or line list group, element or option do not
     *         exist.
     */
    String getLineListDataElementMappingDescription( String lineListDataElementMapping );

    /**
     * Converts an expression on the form [34] + [23], where the numbers are IDs
     * of Line List Groups, Elements and Options, to the form 200 + 450, where
     * the numbers are the values of the values registered for the Period and
     * Source. "0" is included if there is no value registered for the given
     * parameters.
     * 
     * @param expression The expression string.
     * @param period The Period.
     * @param source The Source.
     * @return A numerical expression.
     */
    // String generateLineListDataElementMapping( String
    // dataElementLineListDataElementMapping, String
    // lineListLineListDataElementMapping, Period period, Source source );
}
