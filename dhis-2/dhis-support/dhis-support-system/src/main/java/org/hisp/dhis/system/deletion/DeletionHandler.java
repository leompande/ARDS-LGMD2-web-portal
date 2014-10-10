package org.hisp.dhis.system.deletion;

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
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.dashboard.DashboardItem;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.LockException;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.i18n.locale.I18nLocale;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAudit;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientRegistrationForm;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramValidation;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserSetting;
import org.hisp.dhis.validation.ValidationCriteria;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;

/**
 * A DeletionHandler should override methods for objects that, when deleted,
 * will affect the current object in any way. Eg. a DeletionHandler for
 * DataElementGroup should override the deleteDataElement(..) method which
 * should remove the DataElement from all DataElementGroups. Also, it should
 * override the allowDeleteDataElement() method and return a non-null String value
 * if there exists objects that are dependent on the DataElement and are
 * considered not be deleted. The return value could be a hint for which object
 * is denying the delete, like the name.
 *
 * @author Lars Helge Overland
 */
public abstract class DeletionHandler
{
    protected final String ERROR = "";

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract String getClassName();

    // -------------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------------

    public void deleteAttribute( Attribute attribute )
    {
    }

    public String allowDeleteAttribute( Attribute attribute )
    {
        return null;
    }

    public void deleteAttributeValue( AttributeValue attributeValue )
    {
    }

    public String allowDeleteAttributeValue( AttributeValue attributeValue )
    {
        return null;
    }

    public void deleteChart( Chart chart )
    {
    }

    public String allowDeleteChart( Chart chart )
    {
        return null;
    }

    public void deleteDataDictionary( DataDictionary dataDictionary )
    {
    }

    public String allowDeleteDataDictionary( DataDictionary dataDictionary )
    {
        return null;
    }

    public void deleteDataElement( DataElement dataElement )
    {
    }

    public String allowDeleteDataElement( DataElement dataElement )
    {
        return null;
    }

    public void deleteDataElementGroup( DataElementGroup dataElementGroup )
    {
    }

    public String allowDeleteDataElementGroup( DataElementGroup dataElementGroup )
    {
        return null;
    }

    public void deleteDataElementGroupSet( DataElementGroupSet dataElementGroupSet )
    {
    }

    public String allowDeleteDataElementGroupSet( DataElementGroupSet dataElementGroupSet )
    {
        return null;
    }

    public void deleteDataElementCategory( DataElementCategory category )
    {
    }

    public String allowDeleteDataElementCategory( DataElementCategory category )
    {
        return null;
    }

    public void deleteDataElementCategoryOption( DataElementCategoryOption categoryOption )
    {
    }

    public String allowDeleteDataElementCategoryOption( DataElementCategoryOption categoryOption )
    {
        return null;
    }

    public void deleteDataElementCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
    }

    public String allowDeleteDataElementCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        return null;
    }

    public void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
    }

    public String allowDeleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        return null;
    }

    public void deleteDataSet( DataSet dataSet )
    {
    }

    public String allowDeleteDataSet( DataSet dataSet )
    {
        return null;
    }

    public void deleteSection( Section section )
    {
    }

    public String allowDeleteSection( Section section )
    {
        return null;
    }

    public void deleteCompleteDataSetRegistration( CompleteDataSetRegistration registration )
    {
    }

    public String allowDeleteCompleteDataSetRegistration( CompleteDataSetRegistration registration )
    {
        return null;
    }

    public void deleteDataValue( DataValue dataValue )
    {
    }

    public String allowDeleteDataValue( DataValue dataValue )
    {
        return null;
    }

    public void deleteExpression( Expression expression )
    {
    }

    public String allowDeleteExpression( Expression expression )
    {
        return null;
    }

    public void deleteMinMaxDataElement( MinMaxDataElement minMaxDataElement )
    {
    }

    public String allowDeleteMinMaxDataElement( MinMaxDataElement minMaxDataElement )
    {
        return null;
    }

    public void deleteIndicator( Indicator indicator )
    {
    }

    public String allowDeleteIndicator( Indicator indicator )
    {
        return null;
    }

    public void deleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
    }

    public String allowDeleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        return null;
    }

    public void deleteIndicatorType( IndicatorType indicatorType )
    {
    }

    public String allowDeleteIndicatorType( IndicatorType indicatorType )
    {
        return null;
    }

    public void deleteIndicatorGroupSet( IndicatorGroupSet indicatorGroupSet )
    {
    }

    public String allowDeleteIndicatorGroupSet( IndicatorGroupSet indicatorGroupSet )
    {
        return null;
    }

    public void deletePeriod( Period period )
    {
    }

    public String allowDeletePeriod( Period period )
    {
        return null;
    }

    public void deleteRelativePeriods( RelativePeriods relativePeriods )
    {
    }

    public String allowDeleteRelativePeriods( RelativePeriods relativePeriods )
    {
        return null;
    }

    public void deleteValidationRule( ValidationRule validationRule )
    {
    }

    public String allowDeleteValidationRule( ValidationRule validationRule )
    {
        return null;
    }
    
    public void deleteValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
    }

    public String allowDeleteValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        return null;
    }

    public void deleteDataEntryForm( DataEntryForm form )
    {
    }

    public String allowDeleteDataEntryForm( DataEntryForm form )
    {
        return null;
    }

    public void deleteOrganisationUnit( OrganisationUnit unit )
    {
    }

    public String allowDeleteOrganisationUnit( OrganisationUnit unit )
    {
        return null;
    }

    public void deleteOrganisationUnitGroup( OrganisationUnitGroup group )
    {
    }

    public String allowDeleteOrganisationUnitGroup( OrganisationUnitGroup group )
    {
        return null;
    }

    public void deleteOrganisationUnitGroupSet( OrganisationUnitGroupSet groupSet )
    {
    }

    public String allowDeleteOrganisationUnitGroupSet( OrganisationUnitGroupSet groupSet )
    {
        return null;
    }

    public void deleteOrganisationUnitLevel( OrganisationUnitLevel level )
    {
    }

    public String allowDeleteOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        return null;
    }

    public void deleteReport( Report report )
    {
    }

    public String allowDeleteReport( Report report )
    {
        return null;
    }

    public void deleteReportTable( ReportTable reportTable )
    {
    }

    public String allowDeleteReportTable( ReportTable reportTable )
    {
        return null;
    }

    public void deleteUser( User user )
    {
    }

    public String allowDeleteUser( User user )
    {
        return null;
    }

    public void deleteUserAuthorityGroup( UserAuthorityGroup authorityGroup )
    {
    }

    public String allowDeleteUserAuthorityGroup( UserAuthorityGroup authorityGroup )
    {
        return null;
    }

    public String allowDeleteUserGroup( UserGroup userGroup )
    {
        return null;
    }

    public void deleteUserGroup( UserGroup userGroup )
    {
    }

    public void deleteUserSetting( UserSetting userSetting )
    {
    }

    public String allowDeleteUserSetting( UserSetting userSetting )
    {
        return null;
    }

    public void deleteDocument( Document document )
    {
    }

    public String allowDeleteDocument( Document document )
    {
        return null;
    }

    public void deleteMapLegend( MapLegend mapLegend )
    {
    }

    public String allowDeleteMapLegend( MapLegend mapLegend )
    {
        return null;
    }

    public void deleteMapLegendSet( MapLegendSet mapLegendSet )
    {
    }

    public String allowDeleteMapLegendSet( MapLegendSet mapLegendSet )
    {
        return null;
    }

    public void deleteMap( Map map )
    {
    }

    public String allowDeleteMap( Map map )
    {
        return null;
    }

    public void deleteMapView( MapView mapView )
    {
    }

    public String allowDeleteMapView( MapView mapView )
    {
        return null;
    }

    public void deleteConcept( Concept concept )
    {
    }

    public String allowDeleteConcept( Concept concept )
    {
        return null;
    }

    public void deletePatient( Patient patient )
    {
    }

    public String allowDeletePatient( Patient patient )
    {
        return null;
    }

    public String allowDeletePatientAttribute( PatientAttribute patientAttribute )
    {
        return null;
    }

    public void deletePatientAttribute( PatientAttribute patientAttribute )
    {
    }

    public String allowDeletePatientAttributeValue( PatientAttributeValue patientAttributeValue )
    {
        return null;
    }

    public void deletePatientAttributeValue( PatientAttributeValue patientAttributeValue )
    {
    }

    public String allowDeletePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
        return null;
    }

    public void deletePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
    }

    public String allowDeletePatientIdentifier( PatientIdentifier patientIdentifier )
    {
        return null;
    }

    public void deletePatientIdentifier( PatientIdentifier patientIdentifier )
    {
    }

    public String allowDeletePatientIdentifierType( PatientIdentifierType patientIdentifierType )
    {
        return null;
    }

    public void deletePatientIdentifierType( PatientIdentifierType patientIdentifierType )
    {
    }

    public String allowDeleteRelationship( Relationship relationship )
    {
        return null;
    }

    public void deleteRelationship( Relationship relationship )
    {
    }

    public String allowDeleteRelationshipType( RelationshipType relationshipType )
    {
        return null;
    }

    public void deleteRelationshipType( RelationshipType relationshipType )
    {
    }

    public String allowDeleteProgram( Program program )
    {
        return null;
    }

    public void deleteProgram( Program program )
    {
    }

    public String allowDeleteProgramInstance( ProgramInstance programInstance )
    {
        return null;
    }

    public void deleteProgramInstance( ProgramInstance programInstance )
    {
    }

    public String allowDeleteProgramStage( ProgramStage programStage )
    {
        return null;
    }

    public void deleteProgramStage( ProgramStage programStage )
    {
    }

    public void deleteProgramStageSection( ProgramStageSection programStageSection )
    {
    }

    public String allowDeleteProgramStageSection( ProgramStageSection programStageSection )
    {
        return null;
    }

    public String allowDeleteProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        return null;
    }

    public void deleteProgramStageInstance( ProgramStageInstance programStageInstance )
    {
    }

    public String allowDeleteProgramStageDataElement( ProgramStageDataElement programStageDataElement )
    {
        return null;
    }

    public void deleteProgramStageDataElement( ProgramStageDataElement programStageDataElement )
    {
    }

    public String allowDeletePatientDataValue( PatientDataValue patientDataValue )
    {
        return null;
    }

    public void deletePatientDataValue( PatientDataValue patientDataValue )
    {
    }

    public String allowDeleteProgramValidation( ProgramValidation programValidation )
    {
        return null;
    }

    public void deleteProgramValidation( ProgramValidation programValidation )
    {
    }

    public String allowDeleteValidationCriteria( ValidationCriteria validationCriteria )
    {
        return null;
    }

    public void deleteValidationCriteria( ValidationCriteria validationCriteria )
    {
    }

    public String allowDeletePatientRegistrationForm( PatientRegistrationForm patientRegistrationForm )
    {
        return null;
    }

    public void deletePatientRegistrationForm( PatientRegistrationForm patientRegistrationForm )
    {
    }

    public String allowDeleteConstant( Constant constant )
    {
        return null;
    }

    public void deleteConstant( Constant constant )
    {
    }

    public String allowDeleteCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
        return null;
    }

    public void deleteCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
    }

    public String allowDeleteOptionSet( OptionSet optionSet )
    {
        return null;
    }

    public void deleteOptionSet( OptionSet optionSet )
    {
    }

    public String allowDeleteLockException( LockException lockException )
    {
        return null;
    }

    public void deleteLockException( LockException lockException )
    {
    }

    public void deletePatientAudit( PatientAudit patientAudit )
    {
    }

    public String allowDeletePatientAudit( PatientAudit patientAudit )
    {
        return null;
    }

    public void deleteIntepretation( Interpretation interpretation )
    {
    }

    public String allowDeleteInterpretation( Interpretation interpretation )
    {
        return null;
    }

    public void deleteI18nLocale( I18nLocale i18nLocale )
    {
    }

    public String allowDeleteI18nLocale( I18nLocale i18nLocale )
    {
        return null;
    }

    public void deleteSqlView( SqlView sqlView )
    {
    }

    public String allowDeleteSqlView( SqlView sqlView )
    {
        return null;
    }

    public void deleteDashboardItem( DashboardItem dashboardItem )
    {
    }

    public String allowDeleteDashboardItem( DashboardItem dashboardItem )
    {
        return null;
    }
}
