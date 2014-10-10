package org.hisp.dhis.dxf2.metadata;

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

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.filter.MetaDataFilter;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.mapping.MapLayer;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
final public class ExchangeClasses
{
    // all available classes for export, used for controllers etc
    private final static Map<Class<? extends IdentifiableObject>, String> allExportClasses;

    // these are the ones that are available for dxf2 export
    private final static Map<Class<? extends IdentifiableObject>, String> exportClasses;

    // these are the ones that are available for dxf2 import
    private final static Map<Class<? extends IdentifiableObject>, String> importClasses;

    // these are the ones that are available for dxf2 delete
    private final static Map<Class<? extends IdentifiableObject>, String> deletableClasses;

    static
    {
        allExportClasses = new LinkedHashMap<Class<? extends IdentifiableObject>, String>();

        allExportClasses.put( SqlView.class, "sqlViews" );
        allExportClasses.put( Concept.class, "concepts" );
        allExportClasses.put( Constant.class, "constants" );
        allExportClasses.put( Document.class, "documents" );
        allExportClasses.put( OptionSet.class, "optionSets" );
        allExportClasses.put( Attribute.class, "attributeTypes" );

        allExportClasses.put( MapLegend.class, "mapLegends" );
        allExportClasses.put( MapLegendSet.class, "mapLegendSets" );
        allExportClasses.put( MapLayer.class, "mapLayers" );

        allExportClasses.put( OrganisationUnit.class, "organisationUnits" );
        allExportClasses.put( OrganisationUnitLevel.class, "organisationUnitLevels" );
        allExportClasses.put( OrganisationUnitGroup.class, "organisationUnitGroups" );
        allExportClasses.put( OrganisationUnitGroupSet.class, "organisationUnitGroupSets" );

        allExportClasses.put( DataElementCategoryOption.class, "categoryOptions" );
        allExportClasses.put( DataElementCategory.class, "categories" );
        allExportClasses.put( DataElementCategoryCombo.class, "categoryCombos" );
        allExportClasses.put( DataElementCategoryOptionCombo.class, "categoryOptionCombos" );

        allExportClasses.put( DataElement.class, "dataElements" );
        allExportClasses.put( DataElementGroup.class, "dataElementGroups" );
        allExportClasses.put( DataElementGroupSet.class, "dataElementGroupSets" );

        allExportClasses.put( DataElementOperand.class, "dataElementOperands" );

        allExportClasses.put( IndicatorType.class, "indicatorTypes" );
        allExportClasses.put( Indicator.class, "indicators" );
        allExportClasses.put( IndicatorGroup.class, "indicatorGroups" );
        allExportClasses.put( IndicatorGroupSet.class, "indicatorGroupSets" );

        allExportClasses.put( DataDictionary.class, "dataDictionaries" );

        allExportClasses.put( User.class, "users" );
        allExportClasses.put( UserGroup.class, "userGroups" );

        allExportClasses.put( DataSet.class, "dataSets" );
        allExportClasses.put( Section.class, "sections" );

        allExportClasses.put( UserAuthorityGroup.class, "userRoles" );

        allExportClasses.put( ReportTable.class, "reportTables" );
        allExportClasses.put( Report.class, "reports" );
        allExportClasses.put( Chart.class, "charts" );

        allExportClasses.put( Dashboard.class, "dashboards" );

        allExportClasses.put( ValidationRule.class, "validationRules" );
        allExportClasses.put( ValidationRuleGroup.class, "validationRuleGroups" );

        allExportClasses.put( MapView.class, "mapViews" );
        allExportClasses.put( org.hisp.dhis.mapping.Map.class, "maps" );

        allExportClasses.put( MessageConversation.class, "messageConversations" );
        allExportClasses.put( Interpretation.class, "interpretations" );

        allExportClasses.put( Program.class, "programs" );
        allExportClasses.put( ProgramStage.class, "programStages" );
        allExportClasses.put( RelationshipType.class, "relationshipTypes" );
        allExportClasses.put( PatientIdentifierType.class, "personIdentifierTypes" );
        allExportClasses.put( PatientAttribute.class, "personAttributeTypes" );
        allExportClasses.put( PatientAttributeGroup.class, "personAttributeGroups" );

        allExportClasses.put( BaseDimensionalObject.class, "dimensions" );

        exportClasses = new LinkedHashMap<Class<? extends IdentifiableObject>, String>( allExportClasses );
        importClasses = new LinkedHashMap<Class<? extends IdentifiableObject>, String>( allExportClasses );

        // this is considered data, and is not available for meta-data export/import
        exportClasses.remove( MessageConversation.class );
        exportClasses.remove( Interpretation.class );
        exportClasses.remove( Dashboard.class );
        exportClasses.remove( BaseDimensionalObject.class );
        importClasses.remove( MessageConversation.class );
        importClasses.remove( Interpretation.class );
        importClasses.remove( Dashboard.class );
        importClasses.remove( BaseDimensionalObject.class );

        // tracker types are not enabled for meta-data import-export yet
        exportClasses.remove( Program.class );
        exportClasses.remove( ProgramStage.class );
        exportClasses.remove( RelationshipType.class );
        exportClasses.remove( PatientIdentifierType.class );
        exportClasses.remove( PatientAttribute.class );
        exportClasses.remove( PatientAttributeGroup.class );
        importClasses.remove( Program.class );
        importClasses.remove( ProgramStage.class );
        importClasses.remove( RelationshipType.class );
        importClasses.remove( PatientIdentifierType.class );
        importClasses.remove( PatientAttribute.class );
        importClasses.remove( PatientAttributeGroup.class );

        // special class which is created on demand in association with other objects
        exportClasses.remove( DataElementOperand.class );
        importClasses.remove( DataElementOperand.class );

        allExportClasses.put( MetaDataFilter.class, "metaDataFilters" );
        exportClasses.remove( MetaDataFilter.class );
        importClasses.remove( MetaDataFilter.class );

        deletableClasses = new LinkedHashMap<Class<? extends IdentifiableObject>, String>( importClasses );
        deletableClasses.remove( User.class );
        deletableClasses.remove( UserGroup.class );
        deletableClasses.remove( UserAuthorityGroup.class );
    }

    public static Map<Class<? extends IdentifiableObject>, String> getAllExportMap()
    {
        return Collections.unmodifiableMap( allExportClasses );
    }

    public static Map<Class<? extends IdentifiableObject>, String> getExportMap()
    {
        return Collections.unmodifiableMap( exportClasses );
    }

    public static Map<Class<? extends IdentifiableObject>, String> getImportMap()
    {
        return Collections.unmodifiableMap( importClasses );
    }

    public static List<Class<? extends IdentifiableObject>> getImportClasses()
    {
        return new ArrayList<Class<? extends IdentifiableObject>>( importClasses.keySet() );
    }

    public static Map<Class<? extends IdentifiableObject>, String> getDeletableMap()
    {
        return Collections.unmodifiableMap( deletableClasses );
    }

    public static List<Class<? extends IdentifiableObject>> getDeletableClasses()
    {
        return new ArrayList<Class<? extends IdentifiableObject>>( deletableClasses.keySet() );
    }
}
