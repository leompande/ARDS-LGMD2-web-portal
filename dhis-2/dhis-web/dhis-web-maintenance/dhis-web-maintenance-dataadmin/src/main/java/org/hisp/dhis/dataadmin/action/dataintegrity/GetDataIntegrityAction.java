package org.hisp.dhis.dataadmin.action.dataintegrity;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataintegrity.DataIntegrityService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.validation.ValidationRule;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetDataIntegrityAction
    implements Action
{
    private static final Log log = LogFactory.getLog( GetDataIntegrityAction.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataIntegrityService dataIntegrityService;

    public void setDataIntegrityService( DataIntegrityService dataIntegrityService )
    {
        this.dataIntegrityService = dataIntegrityService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataElement> dataElementsWithoutDataSet;

    public List<DataElement> getDataElementsWithoutDataSet()
    {
        return dataElementsWithoutDataSet;
    }

    private List<DataElement> dataElementsWithoutGroups;

    public Collection<DataElement> getDataElementsWithoutGroups()
    {
        return dataElementsWithoutGroups;
    }

    private Map<DataElement, Collection<DataSet>> dataElementsAssignedToDataSetsWithDifferentPeriodTypes;

    public Map<DataElement, Collection<DataSet>> getDataElementsAssignedToDataSetsWithDifferentPeriodTypes()
    {
        return dataElementsAssignedToDataSetsWithDifferentPeriodTypes;
    }

    private SortedMap<DataElement, Collection<DataElementGroup>> dataElementsViolatingExclusiveGroupSets;

    public SortedMap<DataElement, Collection<DataElementGroup>> getDataElementsViolatingExclusiveGroupSets()
    {
        return dataElementsViolatingExclusiveGroupSets;
    }
    
    private SortedMap<DataSet, Collection<DataElement>> dataElementsInDataSetNotInForm;

    public SortedMap<DataSet, Collection<DataElement>> getDataElementsInDataSetNotInForm()
    {
        return dataElementsInDataSetNotInForm;
    }

    private List<DataSet> dataSetsNotAssignedToOrganisationUnits;

    public List<DataSet> getDataSetsNotAssignedToOrganisationUnits()
    {
        return dataSetsNotAssignedToOrganisationUnits;
    }

    private List<Section> sectionsWithInvalidCategoryCombinations;
    
    public List<Section> getSectionsWithInvalidCategoryCombinations()
    {
        return sectionsWithInvalidCategoryCombinations;
    }

    private Collection<Collection<Indicator>> indicatorsWithIdenticalFormulas;

    public Collection<Collection<Indicator>> getIndicatorsWithIdenticalFormulas()
    {
        return indicatorsWithIdenticalFormulas;
    }

    private List<Indicator> indicatorsWithoutGroups;

    public List<Indicator> getIndicatorsWithoutGroups()
    {
        return indicatorsWithoutGroups;
    }

    private Map<Indicator, String> invalidIndicatorNumerators;

    public Map<Indicator, String> getInvalidIndicatorNumerators()
    {
        return invalidIndicatorNumerators;
    }

    private Map<Indicator, String> invalidIndicatorDenominators;

    public Map<Indicator, String> getInvalidIndicatorDenominators()
    {
        return invalidIndicatorDenominators;
    }

    private SortedMap<Indicator, Collection<IndicatorGroup>> indicatorsViolatingExclusiveGroupSets;

    public SortedMap<Indicator, Collection<IndicatorGroup>> getIndicatorsViolatingExclusiveGroupSets()
    {
        return indicatorsViolatingExclusiveGroupSets;
    }

    private List<OrganisationUnit> organisationUnitsWithCyclicReferences;

    public List<OrganisationUnit> getOrganisationUnitsWithCyclicReferences()
    {
        return organisationUnitsWithCyclicReferences;
    }

    private List<OrganisationUnit> orphanedOrganisationUnits;

    public List<OrganisationUnit> getOrphanedOrganisationUnits()
    {
        return orphanedOrganisationUnits;
    }

    private List<OrganisationUnit> organisationUnitsWithoutGroups;

    public List<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        return organisationUnitsWithoutGroups;
    }

    private SortedMap<OrganisationUnit, Collection<OrganisationUnitGroup>> organisationUnitsViolatingExclusiveGroupSets;

    public SortedMap<OrganisationUnit, Collection<OrganisationUnitGroup>> getOrganisationUnitsViolatingExclusiveGroupSets()
    {
        return organisationUnitsViolatingExclusiveGroupSets;
    }

    private List<OrganisationUnitGroup> organisationUnitGroupsWithoutGroupSets;

    public List<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets()
    {
        return organisationUnitGroupsWithoutGroupSets;
    }

    private List<ValidationRule> validationRulesWithoutGroups;

    public List<ValidationRule> getValidationRulesWithoutGroups()
    {
        return validationRulesWithoutGroups;
    }

    private Map<ValidationRule, String> invalidValidationRuleLeftSideExpressions;

    public Map<ValidationRule, String> getInvalidValidationRuleLeftSideExpressions()
    {
        return invalidValidationRuleLeftSideExpressions;
    }

    private Map<ValidationRule, String> invalidValidationRuleRightSideExpressions;

    public Map<ValidationRule, String> getInvalidValidationRuleRightSideExpressions()
    {
        return invalidValidationRuleRightSideExpressions;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        dataElementsWithoutDataSet = new ArrayList<DataElement>( dataIntegrityService.getDataElementsWithoutDataSet() );
        dataElementsWithoutGroups = new ArrayList<DataElement>( dataIntegrityService.getDataElementsWithoutGroups() );
        dataElementsAssignedToDataSetsWithDifferentPeriodTypes = dataIntegrityService.getDataElementsAssignedToDataSetsWithDifferentPeriodTypes();
        dataElementsViolatingExclusiveGroupSets = dataIntegrityService.getDataElementsViolatingExclusiveGroupSets();
        dataElementsInDataSetNotInForm = dataIntegrityService.getDataElementsInDataSetNotInForm();

        log.info( "Checked data elements" );
        
        dataSetsNotAssignedToOrganisationUnits = new ArrayList<DataSet>( dataIntegrityService.getDataSetsNotAssignedToOrganisationUnits() );
        sectionsWithInvalidCategoryCombinations = new ArrayList<Section>( dataIntegrityService.getSectionsWithInvalidCategoryCombinations() );
        
        log.info( "Checked data sets" );
        
        indicatorsWithIdenticalFormulas = dataIntegrityService.getIndicatorsWithIdenticalFormulas();
        indicatorsWithoutGroups = new ArrayList<Indicator>( dataIntegrityService.getIndicatorsWithoutGroups() );
        invalidIndicatorNumerators = dataIntegrityService.getInvalidIndicatorNumerators();
        invalidIndicatorDenominators = dataIntegrityService.getInvalidIndicatorDenominators();
        indicatorsViolatingExclusiveGroupSets = dataIntegrityService.getIndicatorsViolatingExclusiveGroupSets();

        log.info( "Checked indicators" );
        
        organisationUnitsWithCyclicReferences = new ArrayList<OrganisationUnit>( dataIntegrityService
            .getOrganisationUnitsWithCyclicReferences() );
        orphanedOrganisationUnits = new ArrayList<OrganisationUnit>( dataIntegrityService
            .getOrphanedOrganisationUnits() );
        organisationUnitsWithoutGroups = new ArrayList<OrganisationUnit>( dataIntegrityService
            .getOrganisationUnitsWithoutGroups() );
        organisationUnitsViolatingExclusiveGroupSets = dataIntegrityService.getOrganisationUnitsViolatingExclusiveGroupSets();
        organisationUnitGroupsWithoutGroupSets = new ArrayList<OrganisationUnitGroup>( dataIntegrityService
            .getOrganisationUnitGroupsWithoutGroupSets() );
        validationRulesWithoutGroups = new ArrayList<ValidationRule>( dataIntegrityService
            .getValidationRulesWithoutGroups() );
        
        log.info( "Checked organisation units" );
        
        invalidValidationRuleLeftSideExpressions = dataIntegrityService.getInvalidValidationRuleLeftSideExpressions();
        invalidValidationRuleRightSideExpressions = dataIntegrityService.getInvalidValidationRuleRightSideExpressions();

        log.info( "Checked validation rules" );
        
        Collections.sort( dataElementsWithoutDataSet, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataElementsWithoutGroups, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataSetsNotAssignedToOrganisationUnits, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( sectionsWithInvalidCategoryCombinations, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( indicatorsWithoutGroups, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( organisationUnitsWithCyclicReferences, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( orphanedOrganisationUnits, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( organisationUnitsWithoutGroups, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( organisationUnitGroupsWithoutGroupSets, IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( validationRulesWithoutGroups, IdentifiableObjectNameComparator.INSTANCE );

        return SUCCESS;
    }
}
