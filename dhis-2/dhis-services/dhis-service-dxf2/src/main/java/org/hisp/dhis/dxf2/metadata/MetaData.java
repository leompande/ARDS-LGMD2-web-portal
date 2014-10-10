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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DxfNamespaces;
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
import org.hisp.dhis.mapping.Map;
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
import java.util.Date;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement(localName = "metaData", namespace = DxfNamespaces.DXF_2_0)
public class MetaData
{
    private Date created;

    private List<Attribute> attributeTypes = new ArrayList<Attribute>();

    private List<Document> documents = new ArrayList<Document>();

    private List<Constant> constants = new ArrayList<Constant>();

    private List<Concept> concepts = new ArrayList<Concept>();

    private List<User> users = new ArrayList<User>();

    private List<UserAuthorityGroup> userRoles = new ArrayList<UserAuthorityGroup>();

    private List<UserGroup> userGroups = new ArrayList<UserGroup>();

    private List<MessageConversation> messageConversations = new ArrayList<MessageConversation>();

    private List<Interpretation> interpretations = new ArrayList<Interpretation>();

    private List<OptionSet> optionSets = new ArrayList<OptionSet>();

    private List<DataElementCategory> categories = new ArrayList<DataElementCategory>();

    private List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

    private List<DataElementCategoryCombo> categoryCombos = new ArrayList<DataElementCategoryCombo>();

    private List<DataElementCategoryOptionCombo> categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

    private List<DataElementOperand> dataElementOperands = new ArrayList<DataElementOperand>();

    private List<Dashboard> dashboards = new ArrayList<Dashboard>();

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    private List<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>();

    private List<DataElementGroupSet> dataElementGroupSets = new ArrayList<DataElementGroupSet>();

    private List<DimensionalObject> dimensions = new ArrayList<DimensionalObject>();

    private List<Indicator> indicators = new ArrayList<Indicator>();

    private List<IndicatorGroup> indicatorGroups = new ArrayList<IndicatorGroup>();

    private List<IndicatorGroupSet> indicatorGroupSets = new ArrayList<IndicatorGroupSet>();

    private List<IndicatorType> indicatorTypes = new ArrayList<IndicatorType>();

    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    private List<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>();

    private List<OrganisationUnitGroupSet> organisationUnitGroupSets = new ArrayList<OrganisationUnitGroupSet>();

    private List<OrganisationUnitLevel> organisationUnitLevels = new ArrayList<OrganisationUnitLevel>();

    private List<ValidationRule> validationRules = new ArrayList<ValidationRule>();

    private List<ValidationRuleGroup> validationRuleGroups = new ArrayList<ValidationRuleGroup>();

    private List<SqlView> sqlViews = new ArrayList<SqlView>();

    private List<Chart> charts = new ArrayList<Chart>();

    private List<Report> reports = new ArrayList<Report>();

    private List<ReportTable> reportTables = new ArrayList<ReportTable>();

    private List<Map> maps = new ArrayList<Map>();

    private List<MapView> mapViews = new ArrayList<MapView>();

    private List<MapLegend> mapLegends = new ArrayList<MapLegend>();

    private List<MapLegendSet> mapLegendSets = new ArrayList<MapLegendSet>();

    private List<MapLayer> mapLayers = new ArrayList<MapLayer>();

    private List<DataDictionary> dataDictionaries = new ArrayList<DataDictionary>();

    private List<Section> sections = new ArrayList<Section>();

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    private List<Program> programs = new ArrayList<Program>();

    private List<ProgramStage> programStages = new ArrayList<ProgramStage>();

    private List<RelationshipType> relationshipTypes = new ArrayList<RelationshipType>();

    private List<MetaDataFilter> metaDataFilters = new ArrayList<MetaDataFilter>();

    private List<PatientIdentifierType> personIdentifierTypes = new ArrayList<PatientIdentifierType>();

    private List<PatientAttribute> personAttributeTypes = new ArrayList<PatientAttribute>();

    private List<PatientAttributeGroup> personAttributeGroups = new ArrayList<PatientAttributeGroup>();

    public MetaData()
    {
    }

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    public Date getCreated()
    {
        return created;
    }

    public void setCreated( Date created )
    {
        this.created = created;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "attributeTypes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "attributeType", namespace = DxfNamespaces.DXF_2_0)
    public List<Attribute> getAttributeTypes()
    {
        return attributeTypes;
    }

    public void setAttributeTypes( List<Attribute> attributeTypes )
    {
        this.attributeTypes = attributeTypes;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "users", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "user", namespace = DxfNamespaces.DXF_2_0)
    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers( List<User> users )
    {
        this.users = users;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "userRoles", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userRole", namespace = DxfNamespaces.DXF_2_0)
    public List<UserAuthorityGroup> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles( List<UserAuthorityGroup> userRoles )
    {
        this.userRoles = userRoles;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "userGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "userGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<UserGroup> getUserGroups()
    {
        return userGroups;
    }

    public void setUserGroups( List<UserGroup> userGroups )
    {
        this.userGroups = userGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "messageConversations", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "messageConversation", namespace = DxfNamespaces.DXF_2_0)
    public List<MessageConversation> getMessageConversations()
    {
        return messageConversations;
    }

    public void setMessageConversations( List<MessageConversation> messageConversations )
    {
        this.messageConversations = messageConversations;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "interpretations", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "interpretation", namespace = DxfNamespaces.DXF_2_0)
    public List<Interpretation> getInterpretations()
    {
        return interpretations;
    }

    public void setInterpretations( List<Interpretation> interpretations )
    {
        this.interpretations = interpretations;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dataElements", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataElement", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "optionSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "optionSet", namespace = DxfNamespaces.DXF_2_0)
    public List<OptionSet> getOptionSets()
    {
        return optionSets;
    }

    public void setOptionSets( List<OptionSet> optionSets )
    {
        this.optionSets = optionSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dataElementGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataElementGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementGroups( List<DataElementGroup> dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dataElementGroupSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataElementGroupSet", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementGroupSet> getDataElementGroupSets()
    {
        return dataElementGroupSets;
    }

    public void setDataElementGroupSets( List<DataElementGroupSet> dataElementGroupSets )
    {
        this.dataElementGroupSets = dataElementGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "concepts", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "concept", namespace = DxfNamespaces.DXF_2_0)
    public List<Concept> getConcepts()
    {
        return concepts;
    }

    public void setConcepts( List<Concept> concepts )
    {
        this.concepts = concepts;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "categories", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "category", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementCategory> getCategories()
    {
        return categories;
    }

    public void setCategories( List<DataElementCategory> categories )
    {
        this.categories = categories;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "categoryOptions", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "categoryOption", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( List<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "categoryCombos", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "categoryCombo", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementCategoryCombo> getCategoryCombos()
    {
        return categoryCombos;
    }

    public void setCategoryCombos( List<DataElementCategoryCombo> categoryCombos )
    {
        this.categoryCombos = categoryCombos;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "categoryOptionCombos", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "categoryOptionCombo", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementCategoryOptionCombo> getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( List<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dataElementOperands", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataElementOperand", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementOperand> getDataElementOperands()
    {
        return dataElementOperands;
    }

    public void setDataElementOperands( List<DataElementOperand> dataElementOperands )
    {
        this.dataElementOperands = dataElementOperands;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "indicators", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "indicator", namespace = DxfNamespaces.DXF_2_0)
    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "indicatorGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "indicatorGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    public void setIndicatorGroups( List<IndicatorGroup> indicatorGroups )
    {
        this.indicatorGroups = indicatorGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "indicatorGroupSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "indicatorGroupSet", namespace = DxfNamespaces.DXF_2_0)
    public List<IndicatorGroupSet> getIndicatorGroupSets()
    {
        return indicatorGroupSets;
    }

    public void setIndicatorGroupSets( List<IndicatorGroupSet> indicatorGroupSets )
    {
        this.indicatorGroupSets = indicatorGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "indicatorTypes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "indicatorType", namespace = DxfNamespaces.DXF_2_0)
    public List<IndicatorType> getIndicatorTypes()
    {
        return indicatorTypes;
    }

    public void setIndicatorTypes( List<IndicatorType> indicatorTypes )
    {
        this.indicatorTypes = indicatorTypes;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "organisationUnitGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnitGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( List<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "organisationUnitGroupSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnitGroupSet", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    public void setOrganisationUnitGroupSets( List<OrganisationUnitGroupSet> organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "organisationUnitLevels", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "organisationUnitLevel", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    public void setOrganisationUnitLevels( List<OrganisationUnitLevel> organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "sections", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "section", namespace = DxfNamespaces.DXF_2_0)
    public List<Section> getSections()
    {
        return sections;
    }

    public void setSections( List<Section> sections )
    {
        this.sections = sections;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dataSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataSet", namespace = DxfNamespaces.DXF_2_0)
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "validationRules", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "validationRule", namespace = DxfNamespaces.DXF_2_0)
    public List<ValidationRule> getValidationRules()
    {
        return validationRules;
    }

    public void setValidationRules( List<ValidationRule> validationRules )
    {
        this.validationRules = validationRules;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "validationRuleGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "validationRuleGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<ValidationRuleGroup> getValidationRuleGroups()
    {
        return validationRuleGroups;
    }

    public void setValidationRuleGroups( List<ValidationRuleGroup> validationRuleGroups )
    {
        this.validationRuleGroups = validationRuleGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "sqlViews", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "sqlView", namespace = DxfNamespaces.DXF_2_0)
    public List<SqlView> getSqlViews()
    {
        return sqlViews;
    }

    public void setSqlViews( List<SqlView> sqlViews )
    {
        this.sqlViews = sqlViews;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "charts", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "chart", namespace = DxfNamespaces.DXF_2_0)
    public List<Chart> getCharts()
    {
        return charts;
    }

    public void setCharts( List<Chart> charts )
    {
        this.charts = charts;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "reports", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "report", namespace = DxfNamespaces.DXF_2_0)
    public List<Report> getReports()
    {
        return reports;
    }

    public void setReports( List<Report> reports )
    {
        this.reports = reports;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "reportTables", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "reportTable", namespace = DxfNamespaces.DXF_2_0)
    public List<ReportTable> getReportTables()
    {
        return reportTables;
    }

    public void setReportTables( List<ReportTable> reportTables )
    {
        this.reportTables = reportTables;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "documents", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "document", namespace = DxfNamespaces.DXF_2_0)
    public List<Document> getDocuments()
    {
        return documents;
    }

    public void setDocuments( List<Document> documents )
    {
        this.documents = documents;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "constants", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "constant", namespace = DxfNamespaces.DXF_2_0)
    public List<Constant> getConstants()
    {
        return constants;
    }

    public void setConstants( List<Constant> constants )
    {
        this.constants = constants;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dashboards", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dashboard", namespace = DxfNamespaces.DXF_2_0)
    public List<Dashboard> getDashboards()
    {
        return dashboards;
    }

    public void setDashboards( List<Dashboard> dashboards )
    {
        this.dashboards = dashboards;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "maps", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "map", namespace = DxfNamespaces.DXF_2_0)
    public List<Map> getMaps()
    {
        return maps;
    }

    public void setMaps( List<Map> maps )
    {
        this.maps = maps;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "mapViews", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "mapView", namespace = DxfNamespaces.DXF_2_0)
    public List<MapView> getMapViews()
    {
        return mapViews;
    }

    public void setMapViews( List<MapView> mapViews )
    {
        this.mapViews = mapViews;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "mapLegends", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "mapLegend", namespace = DxfNamespaces.DXF_2_0)
    public List<MapLegend> getMapLegends()
    {
        return mapLegends;
    }

    public void setMapLegends( List<MapLegend> mapLegends )
    {
        this.mapLegends = mapLegends;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "mapLegendSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "mapLegendSet", namespace = DxfNamespaces.DXF_2_0)
    public List<MapLegendSet> getMapLegendSets()
    {
        return mapLegendSets;
    }

    public void setMapLegendSets( List<MapLegendSet> mapLegendSets )
    {
        this.mapLegendSets = mapLegendSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "mapLayers", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "mapLayer", namespace = DxfNamespaces.DXF_2_0)
    public List<MapLayer> getMapLayers()
    {
        return mapLayers;
    }

    public void setMapLayers( List<MapLayer> mapLayers )
    {
        this.mapLayers = mapLayers;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dataDictionaries", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dataDictionary", namespace = DxfNamespaces.DXF_2_0)
    public List<DataDictionary> getDataDictionaries()
    {
        return dataDictionaries;
    }

    public void setDataDictionaries( List<DataDictionary> dataDictionaries )
    {
        this.dataDictionaries = dataDictionaries;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "programs", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "program", namespace = DxfNamespaces.DXF_2_0)
    public List<Program> getPrograms()
    {
        return programs;
    }

    public void setPrograms( List<Program> programs )
    {
        this.programs = programs;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "programStages", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "programStage", namespace = DxfNamespaces.DXF_2_0)
    public List<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    public void setProgramStages( List<ProgramStage> programStages )
    {
        this.programStages = programStages;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "relationshipTypes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "relationshipType", namespace = DxfNamespaces.DXF_2_0)
    public List<RelationshipType> getRelationshipTypes()
    {
        return relationshipTypes;
    }

    public void setRelationshipTypes( List<RelationshipType> relationshipTypes )
    {
        this.relationshipTypes = relationshipTypes;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "personIdentifierTypes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "personIdentifierType", namespace = DxfNamespaces.DXF_2_0)
    public List<PatientIdentifierType> getPersonIdentifierTypes()
    {
        return personIdentifierTypes;
    }

    public void setPersonIdentifierTypes( List<PatientIdentifierType> personIdentifierTypes )
    {
        this.personIdentifierTypes = personIdentifierTypes;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "personAttributeTypes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "personAttributeType", namespace = DxfNamespaces.DXF_2_0)
    public List<PatientAttribute> getPersonAttributeTypes()
    {
        return personAttributeTypes;
    }

    public void setPersonAttributeTypes( List<PatientAttribute> personAttributeTypes )
    {
        this.personAttributeTypes = personAttributeTypes;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "personAttributeGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "personAttributeGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<PatientAttributeGroup> getPersonAttributeGroups()
    {
        return personAttributeGroups;
    }

    public void setPersonAttributeGroups( List<PatientAttributeGroup> personAttributeGroups )
    {
        this.personAttributeGroups = personAttributeGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "dimensions", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "dimension", namespace = DxfNamespaces.DXF_2_0)
    public List<DimensionalObject> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions( List<DimensionalObject> dimensions )
    {
        this.dimensions = dimensions;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "metaDataFilters", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "filter", namespace = DxfNamespaces.DXF_2_0)
    public List<MetaDataFilter> getMetaDataFilters()
    {
        return metaDataFilters;
    }

    public void setMetaDataFilters( List<MetaDataFilter> metaDataFilters )
    {
        this.metaDataFilters = metaDataFilters;
    }

    @Override
    public String toString()
    {
        return "MetaData{" +
            "created=" + created +
            ", attributeTypes=" + attributeTypes +
            ", documents=" + documents +
            ", constants=" + constants +
            ", concepts=" + concepts +
            ", users=" + users +
            ", userRoles=" + userRoles +
            ", userGroups=" + userGroups +
            ", messageConversations=" + messageConversations +
            ", interpretations=" + interpretations +
            ", optionSets=" + optionSets +
            ", categories=" + categories +
            ", categoryOptions=" + categoryOptions +
            ", categoryCombos=" + categoryCombos +
            ", categoryOptionCombos=" + categoryOptionCombos +
            ", dashboards=" + dashboards +
            ", dataElements=" + dataElements +
            ", dataElementGroups=" + dataElementGroups +
            ", dataElementGroupSets=" + dataElementGroupSets +
            ", dimensions=" + dimensions +
            ", indicators=" + indicators +
            ", indicatorGroups=" + indicatorGroups +
            ", indicatorGroupSets=" + indicatorGroupSets +
            ", indicatorTypes=" + indicatorTypes +
            ", organisationUnits=" + organisationUnits +
            ", organisationUnitGroups=" + organisationUnitGroups +
            ", organisationUnitGroupSets=" + organisationUnitGroupSets +
            ", organisationUnitLevels=" + organisationUnitLevels +
            ", validationRules=" + validationRules +
            ", validationRuleGroups=" + validationRuleGroups +
            ", sqlViews=" + sqlViews +
            ", charts=" + charts +
            ", reports=" + reports +
            ", reportTables=" + reportTables +
            ", maps=" + maps +
            ", mapViews=" + mapViews +
            ", mapLegends=" + mapLegends +
            ", mapLegendSets=" + mapLegendSets +
            ", mapLayers=" + mapLayers +
            ", dataDictionaries=" + dataDictionaries +
            ", sections=" + sections +
            ", dataSets=" + dataSets +
            ", programs=" + programs +
            ", programStages=" + programStages +
            ", relationshipTypes=" + relationshipTypes +
            ", personIdentifierTypes=" + personIdentifierTypes +
            ", personAttributeTypes=" + personAttributeTypes +
            ", personAttributeGroups=" + personAttributeGroups +
            '}';
    }
}
