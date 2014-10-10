package org.hisp.dhis.de.action;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Torgeir Lorange Ostby
 */
public class LoadFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String multiOrganisationUnit;

    public void setMultiOrganisationUnit( String multiOrganisationUnit )
    {
        this.multiOrganisationUnit = multiOrganisationUnit;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    private Map<DataElementCategoryCombo, List<DataElement>> orderedDataElements = new HashMap<DataElementCategoryCombo, List<DataElement>>();

    public Map<DataElementCategoryCombo, List<DataElement>> getOrderedDataElements()
    {
        return orderedDataElements;
    }

    private String customDataEntryFormCode;

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }

    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    private List<Section> sections = new ArrayList<Section>();

    public List<Section> getSections()
    {
        return sections;
    }

    private Map<Integer, Map<Integer, Collection<DataElementCategoryOption>>> orderedOptionsMap = new HashMap<Integer, Map<Integer, Collection<DataElementCategoryOption>>>();

    public Map<Integer, Map<Integer, Collection<DataElementCategoryOption>>> getOrderedOptionsMap()
    {
        return orderedOptionsMap;
    }

    private Map<Integer, Collection<DataElementCategory>> orderedCategories = new HashMap<Integer, Collection<DataElementCategory>>();

    public Map<Integer, Collection<DataElementCategory>> getOrderedCategories()
    {
        return orderedCategories;
    }

    private Map<Integer, Integer> numberOfTotalColumns = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getNumberOfTotalColumns()
    {
        return numberOfTotalColumns;
    }

    private Map<Integer, Map<Integer, Collection<Integer>>> catColRepeat = new HashMap<Integer, Map<Integer, Collection<Integer>>>();

    public Map<Integer, Map<Integer, Collection<Integer>>> getCatColRepeat()
    {
        return catColRepeat;
    }

    private Map<Integer, Collection<DataElementCategoryOptionCombo>> orderedCategoryOptionCombos = new HashMap<Integer, Collection<DataElementCategoryOptionCombo>>();

    public Map<Integer, Collection<DataElementCategoryOptionCombo>> getOrderedCategoryOptionCombos()
    {
        return orderedCategoryOptionCombos;
    }

    private List<DataElementCategoryCombo> orderedCategoryCombos = new ArrayList<DataElementCategoryCombo>();

    public List<DataElementCategoryCombo> getOrderedCategoryCombos()
    {
        return orderedCategoryCombos;
    }

    private Map<Integer, Integer> sectionCombos = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getSectionCombos()
    {
        return sectionCombos;
    }

    private Map<String, Boolean> greyedFields = new HashMap<String, Boolean>();

    public Map<String, Boolean> getGreyedFields()
    {
        return greyedFields;
    }

    private List<DataElement> dataElementsNotInForm = new ArrayList<DataElement>();

    public List<DataElement> getDataElementsNotInForm()
    {
        return dataElementsNotInForm;
    }

    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        dataSet = dataSetService.getDataSet( dataSetId, true, false, false, true );

        List<DataElement> dataElements = new ArrayList<DataElement>( dataElementService.getDataElements( dataSet, null,
            null ) );

        if ( dataElements.isEmpty() )
        {
            return INPUT;
        }

        Collections.sort( dataElements, IdentifiableObjectNameComparator.INSTANCE );

        orderedDataElements = dataElementService.getGroupedDataElementsByCategoryCombo( dataElements );

        orderedCategoryCombos = dataElementService.getDataElementCategoryCombos( dataElements );

        for ( DataElementCategoryCombo categoryCombo : orderedCategoryCombos )
        {
            List<DataElementCategoryOptionCombo> optionCombos = categoryCombo.getSortedOptionCombos();

            orderedCategoryOptionCombos.put( categoryCombo.getId(), optionCombos );

            // -----------------------------------------------------------------
            // Perform ordering of categories and their options so that they
            // could be displayed as in the paper form. Note that the total
            // number of entry cells to be generated are the multiple of options
            // from each category.
            // -----------------------------------------------------------------

            numberOfTotalColumns.put( categoryCombo.getId(), optionCombos.size() );

            orderedCategories.put( categoryCombo.getId(), categoryCombo.getCategories() );

            Map<Integer, Collection<DataElementCategoryOption>> optionsMap = new HashMap<Integer, Collection<DataElementCategoryOption>>();

            for ( DataElementCategory dec : categoryCombo.getCategories() )
            {
                optionsMap.put( dec.getId(), dec.getCategoryOptions() );
            }

            orderedOptionsMap.put( categoryCombo.getId(), optionsMap );

            // -----------------------------------------------------------------
            // Calculating the number of times each category should be repeated
            // -----------------------------------------------------------------

            Map<Integer, Integer> catRepeat = new HashMap<Integer, Integer>();

            Map<Integer, Collection<Integer>> colRepeat = new HashMap<Integer, Collection<Integer>>();

            int catColSpan = optionCombos.size();

            for ( DataElementCategory cat : categoryCombo.getCategories() )
            {
                int categoryOptionSize = cat.getCategoryOptions().size();

                if ( categoryOptionSize > 0 && catColSpan >= categoryOptionSize )
                {
                    catColSpan = catColSpan / categoryOptionSize;
                    int total = optionCombos.size() / (catColSpan * categoryOptionSize);
                    Collection<Integer> cols = new ArrayList<Integer>( total );

                    for ( int i = 0; i < total; i++ )
                    {
                        cols.add( i );
                    }

                    colRepeat.put( cat.getId(), cols );

                    catRepeat.put( cat.getId(), catColSpan );
                }
            }

            catColRepeat.put( categoryCombo.getId(), colRepeat );
        }

        // ---------------------------------------------------------------------
        // Get data entry form
        // ---------------------------------------------------------------------

        String displayMode = dataSet.getDataSetType();

        if ( displayMode.equals( DataSet.TYPE_DEFAULT ) )
        {
            DataSet dataSetCopy = new DataSet();
            dataSetCopy.setName( dataSet.getName() );
            dataSetCopy.setShortName( dataSet.getShortName() );
            dataSetCopy.setRenderAsTabs( dataSet.isRenderAsTabs() );
            dataSetCopy.setRenderHorizontally( dataSet.isRenderHorizontally() );
            dataSet = dataSetCopy;

            for ( int i = 0; i < orderedCategoryCombos.size(); i++ )
            {
                Section section = new Section();
                section.setUid( CodeGenerator.generateCode() );
                section.setId( i );
                section.setName( orderedCategoryCombos.get( i ).getName() );
                section.setSortOrder( i );
                section.setDataSet( dataSet );
                dataSet.getSections().add( section );

                section.getDataElements().addAll( orderedDataElements.get( orderedCategoryCombos.get( i ) ) );
            }

            displayMode = DataSet.TYPE_SECTION;
        }

        // ---------------------------------------------------------------------
        // For multi-org unit we only support section forms
        // ---------------------------------------------------------------------

        if ( CodeGenerator.isValidCode( multiOrganisationUnit ) )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( multiOrganisationUnit );
            List<OrganisationUnit> organisationUnitChildren = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit child : organisationUnit.getChildren() )
            {
                if ( child.getDataSets().contains( dataSet ) )
                {
                    organisationUnitChildren.add( child );
                }
            }

            Collections.sort( organisationUnitChildren, IdentifiableObjectNameComparator.INSTANCE );

            if ( organisationUnit.getDataSets().contains( dataSet ) )
            {
                organisationUnits.add( organisationUnit );
            }

            organisationUnits.addAll( organisationUnitChildren );

            getSectionForm( dataElements, dataSet );

            displayMode = DataSet.TYPE_SECTION_MULTIORG;
        }

        if ( displayMode.equals( DataSet.TYPE_SECTION ) )
        {
            getSectionForm( dataElements, dataSet );
        }
        else
        {
            getOtherDataEntryForm( dataElements, dataSet );
        }

        return displayMode;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void getSectionForm( Collection<DataElement> dataElements, DataSet dataSet )
    {
        sections = new ArrayList<Section>( dataSet.getSections() );

        Collections.sort( sections, new SectionOrderComparator() );

        for ( Section section : sections )
        {
            DataElementCategoryCombo sectionCategoryCombo = section.getCategoryCombo();

            if ( sectionCategoryCombo != null )
            {
                orderedCategoryCombos.add( sectionCategoryCombo );

                sectionCombos.put( section.getId(), sectionCategoryCombo.getId() );
            }

            for ( DataElementOperand operand : section.getGreyedFields() )
            {
                if ( operand != null && operand.getDataElement() != null && operand.getCategoryOptionCombo() != null )
                {
                    greyedFields.put( operand.getDataElement().getUid() + ":" + operand.getCategoryOptionCombo().getUid(), true );
                }
            }
        }
    }

    private void getOtherDataEntryForm( List<DataElement> dataElements, DataSet dataSet )
    {
        dataEntryForm = dataSet.getDataEntryForm();

        if ( dataEntryForm != null )
        {
            customDataEntryFormCode = dataEntryFormService.prepareDataEntryFormForEntry( dataEntryForm.getHtmlCode(),
                i18n, dataSet );

            dataElementsNotInForm = new ArrayList<DataElement>( dataSet.getDataElements() );
            dataElementsNotInForm.removeAll( dataEntryFormService.getDataElementsInDataEntryForm( dataSet ) );
            Collections.sort( dataElementsNotInForm, IdentifiableObjectNameComparator.INSTANCE );
        }

        List<DataElement> des;

        for ( DataElementCategoryCombo categoryCombo : orderedCategoryCombos )
        {
            des = orderedDataElements.get( categoryCombo );

            Collections.sort( des, IdentifiableObjectNameComparator.INSTANCE );

            orderedDataElements.put( categoryCombo, des );
        }
    }
}
