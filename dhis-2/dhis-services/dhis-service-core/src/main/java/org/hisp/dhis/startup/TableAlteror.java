package org.hisp.dhis.startup;

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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.jdbc.batchhandler.RelativePeriodsBatchHandler;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class TableAlteror
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( TableAlteror.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private StatementManager statementManager;

    @Autowired
    private StatementBuilder statementBuilder;

    @Autowired
    private BatchHandlerFactory batchHandlerFactory;

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
    {
        int defaultCategoryComboId = getDefaultCategoryCombo();

        // ---------------------------------------------------------------------
        // Drop outdated tables
        // ---------------------------------------------------------------------

        executeSql( "DROP TABLE categoryoptioncomboname" );
        executeSql( "DROP TABLE orgunitgroupsetstructure" );
        executeSql( "DROP TABLE orgunitstructure" );
        executeSql( "DROP TABLE orgunithierarchystructure" );
        executeSql( "DROP TABLE orgunithierarchy" );
        executeSql( "DROP TABLE datavalueaudit" );
        executeSql( "DROP TABLE columnorder" );
        executeSql( "DROP TABLE roworder" );
        executeSql( "DROP TABLE sectionmembers" );
        executeSql( "DROP TABLE reporttable_categoryoptioncombos" );
        executeSql( "DROP TABLE reporttable_dataelementgroupsets" );
        executeSql( "DROP TABLE dashboardcontent_datamartexports" );
        executeSql( "DROP TABLE dashboardcontent_mapviews" );
        executeSql( "DROP TABLE customvalue" );
        executeSql( "DROP TABLE reporttable_displaycolumns" );
        executeSql( "DROP TABLE reportreporttables" );
        executeSql( "DROP TABLE frequencyoverrideassociation" );
        executeSql( "DROP TABLE dataelement_dataelementgroupsetmembers" );
        executeSql( "DROP TABLE dashboardcontent_olapurls" );
        executeSql( "DROP TABLE olapurl" );
        executeSql( "DROP TABLE target" );
        executeSql( "DROP TABLE calculateddataelement" );
        executeSql( "DROP TABLE systemsequence" );
        executeSql( "DROP TABLE reporttablecolumn" );
        executeSql( "DROP TABLE datamartexport" );
        executeSql( "DROP TABLE datamartexportdataelements" );
        executeSql( "DROP TABLE datamartexportindicators" );
        executeSql( "DROP TABLE datamartexportorgunits" );
        executeSql( "DROP TABLE datamartexportperiods" );
        executeSql( "DROP TABLE datasetlockedperiods" );
        executeSql( "DROP TABLE datasetlocksource" );
        executeSql( "DROP TABLE datasetlock" );
        executeSql( "DROP TABLE datasetlockexceptions" );
        executeSql( "DROP TABLE indicator_indicatorgroupsetmembers" );
        executeSql( "DROP TABLE maplegendsetindicator" );
        executeSql( "DROP TABLE maplegendsetdataelement" );
        executeSql( "DROP TABLE loginfailure" );
        executeSql( "ALTER TABLE dataelementcategoryoption drop column categoryid" );
        executeSql( "ALTER TABLE reporttable DROP column paramleafparentorganisationunit" );
        executeSql( "ALTER TABLE reporttable DROP column dimension_type" );
        executeSql( "ALTER TABLE reporttable DROP column dimensiontype" );
        executeSql( "ALTER TABLE reporttable DROP column tablename" );
        executeSql( "ALTER TABLE reporttable DROP column existingtablename" );
        executeSql( "ALTER TABLE reporttable DROP column docategoryoptioncombos" );
        executeSql( "ALTER TABLE reporttable DROP column mode" );
        executeSql( "ALTER TABLE categoryoptioncombo DROP COLUMN displayorder" );
        executeSql( "ALTER TABLE dataelementcategoryoption DROP COLUMN shortname" );
        executeSql( "ALTER TABLE section DROP COLUMN label" );
        executeSql( "ALTER TABLE section DROP COLUMN title" );
        executeSql( "ALTER TABLE organisationunit DROP COLUMN polygoncoordinates" );
        executeSql( "ALTER TABLE indicator DROP COLUMN extendeddataelementid" );
        executeSql( "ALTER TABLE indicator DROP COLUMN numeratoraggregationtype" );
        executeSql( "ALTER TABLE indicator DROP COLUMN denominatoraggregationtype" );
        executeSql( "ALTER TABLE dataset DROP COLUMN locked" );
        executeSql( "ALTER TABLE configuration DROP COLUMN completenessrecipientsid" );
        executeSql( "ALTER TABLE dataelement DROP COLUMN alternativename" );
        executeSql( "ALTER TABLE indicator DROP COLUMN alternativename" );
        executeSql( "ALTER TABLE orgunitgroup DROP COLUMN image" );
        executeSql( "ALTER TABLE report DROP COLUMN usingorgunitgroupsets" );

        executeSql( "DROP INDEX datamart_crosstab" );

        // remove relative period type
        executeSql( "DELETE FROM period WHERE periodtypeid=(select periodtypeid from periodtype where name in ( 'Survey', 'OnChange', 'Relative' ))" );
        executeSql( "DELETE FROM periodtype WHERE name in ( 'Survey', 'OnChange', 'Relative' )" );

        // mapping
        executeSql( "DROP TABLE maporganisationunitrelation" );
        executeSql( "ALTER TABLE mapview DROP COLUMN mapid" );
        executeSql( "ALTER TABLE mapview DROP COLUMN startdate" );
        executeSql( "ALTER TABLE mapview DROP COLUMN enddate" );
        executeSql( "ALTER TABLE mapview DROP COLUMN mapsource" );
        executeSql( "ALTER TABLE mapview DROP COLUMN mapsourcetype" );
        executeSql( "ALTER TABLE mapview DROP COLUMN mapdatetype" );
        executeSql( "ALTER TABLE mapview DROP COLUMN featuretype" );
        executeSql( "ALTER TABLE mapview DROP COLUMN bounds" );
        executeSql( "ALTER TABLE mapview DROP COLUMN valuetype" );
        executeSql( "ALTER TABLE mapview DROP COLUMN legendtype" );
        executeSql( "ALTER TABLE mapview RENAME COLUMN mapvaluetype TO valuetype" );
        executeSql( "ALTER TABLE mapview RENAME COLUMN maplegendtype TO legendtype" );
        executeSql( "ALTER TABLE mapview RENAME COLUMN maplegendsetid TO legendsetid" );
        executeSql( "ALTER TABLE mapview ALTER COLUMN opacity TYPE double precision" );

        executeSql( "ALTER TABLE maplegend DROP CONSTRAINT maplegend_name_key" );

        executeSql( "UPDATE mapview SET layer = 'thematic1' WHERE layer IS NULL" );

        executeSql( "DELETE FROM systemsetting WHERE name = 'longitude'" );
        executeSql( "DELETE FROM systemsetting WHERE name = 'latitude'" );

        executeSql( "ALTER TABLE maplayer DROP CONSTRAINT maplayer_mapsource_key" );
        executeSql( "ALTER TABLE maplayer DROP COLUMN mapsource" );
        executeSql( "ALTER TABLE maplayer DROP COLUMN mapsourcetype" );
        executeSql( "ALTER TABLE maplayer DROP COLUMN layer" );

        // extended data element
        executeSql( "ALTER TABLE dataelement DROP CONSTRAINT fk_dataelement_extendeddataelementid" );
        executeSql( "ALTER TABLE dataelement DROP COLUMN extendeddataelementid" );
        executeSql( "ALTER TABLE indicator DROP CONSTRAINT fk_indicator_extendeddataelementid" );
        executeSql( "ALTER TABLE indicator DROP COLUMN extendeddataelementid" );
        executeSql( "DROP TABLE extendeddataelement" );

        executeSql( "ALTER TABLE organisationunit DROP COLUMN hasPatients" );

        executeSql( "update dataelement set texttype='text' where valuetype='string' and texttype is null" );

        // categories_categoryoptions
        // set to 0 temporarily
        int c1 = executeSql( "UPDATE categories_categoryoptions SET sort_order=0 WHERE sort_order is NULL OR sort_order=0" );
        if ( c1 > 0 )
        {
            updateSortOrder( "categories_categoryoptions", "categoryid", "categoryoptionid" );
        }
        executeSql( "ALTER TABLE categories_categoryoptions DROP CONSTRAINT categories_categoryoptions_pkey" );
        executeSql( "ALTER TABLE categories_categoryoptions ADD CONSTRAINT categories_categoryoptions_pkey PRIMARY KEY (categoryid, sort_order)" );

        // categorycombos_categories
        // set to 0 temporarily
        int c2 = executeSql( "update categorycombos_categories SET sort_order=0 where sort_order is NULL OR sort_order=0" );
        if ( c2 > 0 )
        {
            updateSortOrder( "categorycombos_categories", "categorycomboid", "categoryid" );
        }
        executeSql( "ALTER TABLE categorycombos_categories DROP CONSTRAINT categorycombos_categories_pkey" );
        executeSql( "ALTER TABLE categorycombos_categories ADD CONSTRAINT categorycombos_categories_pkey PRIMARY KEY (categorycomboid, sort_order)" );

        // categorycombos_optioncombos
        executeSql( "ALTER TABLE categorycombos_optioncombos DROP CONSTRAINT categorycombos_optioncombos_pkey" );
        executeSql( "ALTER TABLE categorycombos_optioncombos ADD CONSTRAINT categorycombos_optioncombos_pkey PRIMARY KEY (categoryoptioncomboid)" );
        executeSql( "ALTER TABLE categorycombos_optioncombos DROP CONSTRAINT fk4bae70f697e49675" );

        // categoryoptioncombos_categoryoptions
        executeSql( "alter table categoryoptioncombos_categoryoptions drop column sort_order" );
        executeSql( "alter table categoryoptioncombos_categoryoptions add constraint categoryoptioncombos_categoryoptions_pkey primary key(categoryoptioncomboid, categoryoptionid)" );

        // dataelementcategoryoption
        executeSql( "ALTER TABLE dataelementcategoryoption DROP CONSTRAINT fk_dataelement_categoryid" );
        executeSql( "ALTER TABLE dataelementcategoryoption DROP CONSTRAINT dataelementcategoryoption_shortname_key" );

        // minmaxdataelement query index
        executeSql( "CREATE INDEX index_minmaxdataelement ON minmaxdataelement( sourceid, dataelementid, categoryoptioncomboid )" );

        // add mandatory boolean field to patientattribute
        executeSql( "ALTER TABLE patientattribute ADD mandatory bool" );

        if ( executeSql( "ALTER TABLE patientattribute ADD groupby bool" ) >= 0 )
        {
            executeSql( "UPDATE patientattribute SET groupby=false" );
        }

        // update periodType field to ValidationRule
        executeSql( "UPDATE validationrule SET periodtypeid = (SELECT periodtypeid FROM periodtype WHERE name='Monthly') WHERE periodtypeid is null" );

        // update dataelement.domainTypes of which values is null
        executeSql( "UPDATE dataelement SET domaintype='aggregate' WHERE domaintype is null" );

        // set varchar to text
        executeSql( "ALTER TABLE dataelement ALTER description TYPE text" );
        executeSql( "ALTER TABLE indicator ALTER description TYPE text" );
        executeSql( "ALTER TABLE datadictionary ALTER description TYPE text" );
        executeSql( "ALTER TABLE validationrule ALTER description TYPE text" );
        executeSql( "ALTER TABLE expression ALTER expression TYPE text" );
        executeSql( "ALTER TABLE translation ALTER value TYPE text" );
        executeSql( "ALTER TABLE organisationunit ALTER comment TYPE text" );

        executeSql( "ALTER TABLE minmaxdataelement RENAME minvalue TO minimumvalue" );
        executeSql( "ALTER TABLE minmaxdataelement RENAME maxvalue TO maximumvalue" );

        // orgunit shortname uniqueness
        executeSql( "ALTER TABLE organisationunit DROP CONSTRAINT organisationunit_shortname_key" );

        // update dataset-dataentryform association and programstage-cde association
        if ( updateDataSetAssociation() && updateProgramStageAssociation() )
        {
            // delete table dataentryformassociation
            executeSql( "DROP TABLE dataentryformassociation" );
        }

        executeSql( "ALTER TABLE section DROP CONSTRAINT section_name_key" );
        executeSql( "UPDATE patientattribute set inheritable=false where inheritable is null" );
        executeSql( "UPDATE dataelement SET numbertype='number' where numbertype is null and valuetype='int'" );

        // revert prepare aggregate*Value tables for offline diffs

        executeSql( "ALTER TABLE aggregateddatavalue DROP COLUMN modified" );
        executeSql( "ALTER TABLE aggregatedindicatorvalue DROP COLUMN modified " );
        executeSql( "UPDATE indicatortype SET indicatornumber=false WHERE indicatornumber is null" );

        // program

        executeSql( "ALTER TABLE programinstance ALTER COLUMN patientid DROP NOT NULL" );

        // migrate charts from dimension to category, series, filter

        executeSql( "UPDATE chart SET series='period', category='data', filter='organisationunit' WHERE dimension='indicator'" );
        executeSql( "UPDATE chart SET series='data', category='organisationunit', filter='period' WHERE dimension='organisationUnit'" );
        executeSql( "UPDATE chart SET series='period', category='data', filter='organisationunit' WHERE dimension='dataElement_period'" );
        executeSql( "UPDATE chart SET series='data', category='organisationunit', filter='period' WHERE dimension='organisationUnit_dataElement'" );
        executeSql( "UPDATE chart SET series='data', category='period', filter='organisationunit' WHERE dimension='period'" );
        executeSql( "UPDATE chart SET series='data', category='period', filter='organisationunit' WHERE dimension='period_dataElement'" );

        executeSql( "UPDATE chart SET type='bar' where type='bar3d'" );
        executeSql( "UPDATE chart SET type='stackedbar' where type='stackedBar'" );
        executeSql( "UPDATE chart SET type='stackedbar' where type='stackedBar3d'" );
        executeSql( "UPDATE chart SET type='line' where type='line3d'" );
        executeSql( "UPDATE chart SET type='pie' where type='pie'" );
        executeSql( "UPDATE chart SET type='pie' where type='pie3d'" );
        executeSql( "UPDATE chart SET rewindRelativePeriods = false WHERE rewindRelativePeriods is null" );

        executeSql( "UPDATE chart SET type=lower(type), series=lower(series), category=lower(category), filter=lower(filter)" );

        executeSql( "ALTER TABLE chart ALTER COLUMN dimension DROP NOT NULL" );
        executeSql( "ALTER TABLE chart DROP COLUMN size" );
        executeSql( "ALTER TABLE chart DROP COLUMN verticallabels" );
        executeSql( "ALTER TABLE chart DROP COLUMN targetline" );
        executeSql( "ALTER TABLE chart DROP COLUMN horizontalplotorientation" );
        executeSql( "ALTER TABLE chart ADD COLUMN hidesubtitle boolean NOT NULL DEFAULT false" );

        executeSql( "ALTER TABLE chart DROP COLUMN monthsLastYear" );
        executeSql( "ALTER TABLE chart DROP COLUMN quartersLastYear" );
        executeSql( "ALTER TABLE chart DROP COLUMN last6BiMonths" );

        executeSql( "ALTER TABLE chart DROP CONSTRAINT chart_title_key" );
        executeSql( "ALTER TABLE chart DROP CONSTRAINT chart_name_key" );

        executeSql( "ALTER TABLE chart DROP COLUMN domainaxixlabel" );

        executeSql( "ALTER TABLE chart ALTER hideLegend DROP NOT NULL" );
        executeSql( "ALTER TABLE chart ALTER regression DROP NOT NULL" );
        executeSql( "ALTER TABLE chart ALTER hideSubtitle DROP NOT NULL" );
        executeSql( "ALTER TABLE chart ALTER userOrganisationUnit DROP NOT NULL" );

        // remove outdated relative periods

        executeSql( "ALTER TABLE reporttable DROP COLUMN last6months" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN last9months" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN sofarthisyear" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN sofarthisfinancialyear" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN last3to6months" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN last6to9months" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN last9to12months" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN last12individualmonths" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN individualmonthsthisyear" );
        executeSql( "ALTER TABLE reporttable DROP COLUMN individualquartersthisyear" );

        executeSql( "ALTER TABLE chart DROP COLUMN last6months" );
        executeSql( "ALTER TABLE chart DROP COLUMN last9months" );
        executeSql( "ALTER TABLE chart DROP COLUMN sofarthisyear" );
        executeSql( "ALTER TABLE chart DROP COLUMN sofarthisfinancialyear" );
        executeSql( "ALTER TABLE chart DROP COLUMN last3to6months" );
        executeSql( "ALTER TABLE chart DROP COLUMN last6to9months" );
        executeSql( "ALTER TABLE chart DROP COLUMN last9to12months" );
        executeSql( "ALTER TABLE chart DROP COLUMN last12individualmonths" );
        executeSql( "ALTER TABLE chart DROP COLUMN individualmonthsthisyear" );
        executeSql( "ALTER TABLE chart DROP COLUMN individualquartersthisyear" );
        executeSql( "ALTER TABLE chart DROP COLUMN organisationunitgroupsetid" );

        // remove source

        executeSql( "ALTER TABLE datasetsource DROP CONSTRAINT fk766ae2938fd8026a" );
        executeSql( "ALTER TABLE datasetlocksource DROP CONSTRAINT fk582fdf7e8fd8026a" );
        executeSql( "ALTER TABLE completedatasetregistration DROP CONSTRAINT fk_datasetcompleteregistration_sourceid" );
        executeSql( "ALTER TABLE minmaxdataelement DROP CONSTRAINT fk_minmaxdataelement_sourceid" );
        executeSql( "ALTER TABLE datavalue DROP CONSTRAINT fk_datavalue_sourceid" );
        executeSql( "ALTER TABLE organisationunit DROP CONSTRAINT fke509dd5ef1c932ed" );
        executeSql( "DROP TABLE source CASCADE" );
        executeSql( "DROP TABLE datavaluearchive" );

        // message

        executeSql( "ALTER TABLE messageconversation DROP COLUMN messageconversationkey" );
        executeSql( "UPDATE messageconversation SET lastmessage=lastupdated WHERE lastmessage is null" );
        executeSql( "ALTER TABLE message DROP COLUMN messagesubject" );
        executeSql( "ALTER TABLE message DROP COLUMN messagekey" );
        executeSql( "ALTER TABLE message DROP COLUMN sentdate" );
        executeSql( "ALTER TABLE usermessage DROP COLUMN messagedate" );
        executeSql( "UPDATE usermessage SET isfollowup=false WHERE isfollowup is null" );
        executeSql( "DROP TABLE message_usermessages" );

        // create code unique constraints

        executeSql( "ALTER TABLE dataelement ADD CONSTRAINT dataelement_code_key UNIQUE(code)" );
        executeSql( "ALTER TABLE indicator ADD CONSTRAINT indicator_code_key UNIQUE(code)" );
        executeSql( "ALTER TABLE organisationunit ADD CONSTRAINT organisationunit_code_key UNIQUE(code)" );

        // remove uuid

        executeSql( "ALTER TABLE attribute DROP COLUMN uuid" );
        executeSql( "ALTER TABLE categorycombo DROP COLUMN uuid" );
        executeSql( "ALTER TABLE categoryoptioncombo DROP COLUMN uuid" );
        executeSql( "ALTER TABLE chart DROP COLUMN uuid" );
        executeSql( "ALTER TABLE concept DROP COLUMN uuid" );
        executeSql( "ALTER TABLE constant DROP COLUMN uuid" );
        executeSql( "ALTER TABLE datadictionary DROP COLUMN uuid" );
        executeSql( "ALTER TABLE dataelement DROP COLUMN uuid" );
        executeSql( "ALTER TABLE dataelementcategory DROP COLUMN uuid" );
        executeSql( "ALTER TABLE dataelementcategoryoption DROP COLUMN uuid" );
        executeSql( "ALTER TABLE dataelementgroup DROP COLUMN uuid" );
        executeSql( "ALTER TABLE dataelementgroupset DROP COLUMN uuid" );
        executeSql( "ALTER TABLE dataset DROP COLUMN uuid" );
        executeSql( "ALTER TABLE indicator DROP COLUMN uuid" );
        executeSql( "ALTER TABLE indicatorgroup DROP COLUMN uuid" );
        executeSql( "ALTER TABLE indicatorgroupset DROP COLUMN uuid" );
        executeSql( "ALTER TABLE indicatortype DROP COLUMN uuid" );
        // executeSql( "ALTER TABLE organisationunit DROP COLUMN uuid" );
        executeSql( "ALTER TABLE orgunitgroup DROP COLUMN uuid" );
        executeSql( "ALTER TABLE orgunitgroupset DROP COLUMN uuid" );
        executeSql( "ALTER TABLE orgunitlevel DROP COLUMN uuid" );
        executeSql( "ALTER TABLE report DROP COLUMN uuid" );
        executeSql( "ALTER TABLE validationrule DROP COLUMN uuid" );
        executeSql( "ALTER TABLE validationrulegroup DROP COLUMN uuid" );

        // replace null with false for boolean fields

        executeSql( "update dataset set fieldcombinationrequired = false where fieldcombinationrequired is null" );
        executeSql( "update chart set hidelegend = false where hidelegend is null" );
        executeSql( "update chart set regression = false where regression is null" );
        executeSql( "update chart set hidesubtitle = false where hidesubtitle is null" );
        executeSql( "update chart set userorganisationunit = false where userorganisationunit is null" );
        executeSql( "update indicator set annualized = false where annualized is null" );
        executeSql( "update indicatortype set indicatornumber = false where indicatornumber is null" );
        executeSql( "update dataset set mobile = false where mobile is null" );
        executeSql( "update dataset set allowfutureperiods = false where allowfutureperiods is null" );
        executeSql( "update dataset set validcompleteonly = false where validcompleteonly is null" );
        executeSql( "update dataset set notifycompletinguser = false where notifycompletinguser is null" );
        executeSql( "update dataset set approvedata = false where approvedata is null" );
        executeSql( "update dataelement set zeroissignificant = false where zeroissignificant is null" );
        executeSql( "update organisationunit set haspatients = false where haspatients is null" );
        executeSql( "update dataset set expirydays = 0 where expirydays is null" );
        executeSql( "update expression set nullifblank = true where nullifblank is null" );

        // move timelydays from system setting => dataset property
        executeSql( "update dataset set timelydays = 15 where timelydays is null" );
        executeSql( "delete from systemsetting where name='completenessOffset'" );

        executeSql( "update reporttable set reportingmonth = false where reportingmonth is null" );
        executeSql( "update reporttable set reportingbimonth = false where reportingbimonth is null" );
        executeSql( "update reporttable set reportingquarter = false where reportingquarter is null" );
        executeSql( "update reporttable set monthsthisyear = false where monthsthisyear is null" );
        executeSql( "update reporttable set quartersthisyear = false where quartersthisyear is null" );
        executeSql( "update reporttable set thisyear = false where thisyear is null" );
        executeSql( "update reporttable set monthslastyear = false where monthslastyear is null" );
        executeSql( "update reporttable set quarterslastyear = false where quarterslastyear is null" );
        executeSql( "update reporttable set lastyear = false where lastyear is null" );
        executeSql( "update reporttable set last5years = false where last5years is null" );
        executeSql( "update reporttable set lastsixmonth = false where lastsixmonth is null" );
        executeSql( "update reporttable set last4quarters = false where last4quarters is null" );
        executeSql( "update reporttable set last12months = false where last12months is null" );
        executeSql( "update reporttable set last3months = false where last3months is null" );
        executeSql( "update reporttable set last6bimonths = false where last6bimonths is null" );
        executeSql( "update reporttable set last4quarters = false where last4quarters is null" );
        executeSql( "update reporttable set last2sixmonths = false where last2sixmonths is null" );
        executeSql( "update reporttable set thisfinancialyear = false where thisfinancialyear is null" );
        executeSql( "update reporttable set lastfinancialyear = false where lastfinancialyear is null" );
        executeSql( "update reporttable set last5financialyears = false where last5financialyears is null" );
        executeSql( "update reporttable set cumulative = false where cumulative is null" );
        executeSql( "update reporttable set userorganisationunit = false where userorganisationunit is null" );
        executeSql( "update reporttable set userorganisationunitchildren = false where userorganisationunitchildren is null" );
        executeSql( "update reporttable set userorganisationunitgrandchildren = false where userorganisationunitgrandchildren is null" );
        executeSql( "update reporttable set totals = true where totals is null" );
        executeSql( "update reporttable set subtotals = true where subtotals is null" );
        executeSql( "update reporttable set hideemptyrows = false where hideemptyrows is null" );
        executeSql( "update reporttable set displaydensity = 'normal' where displaydensity is null" );
        executeSql( "update reporttable set fontsize = 'normal' where fontsize is null" );
        executeSql( "update reporttable set digitgroupseparator = 'space' where digitgroupseparator is null" );
        executeSql( "update reporttable set sortorder = 0 where sortorder is null" );
        executeSql( "update reporttable set toplimit = 0 where toplimit is null" );
        executeSql( "update reporttable set showhierarchy = false where showhierarchy is null" );
        executeSql( "update reporttable set aggregationtype = 'default' where aggregationtype is null" );

        executeSql( "update chart set reportingmonth = false where reportingmonth is null" );
        executeSql( "update chart set reportingbimonth = false where reportingbimonth is null" );
        executeSql( "update chart set reportingquarter = false where reportingquarter is null" );
        executeSql( "update chart set monthsthisyear = false where monthsthisyear is null" );
        executeSql( "update chart set quartersthisyear = false where quartersthisyear is null" );
        executeSql( "update chart set thisyear = false where thisyear is null" );
        executeSql( "update chart set monthslastyear = false where monthslastyear is null" );
        executeSql( "update chart set quarterslastyear = false where quarterslastyear is null" );
        executeSql( "update chart set lastyear = false where lastyear is null" );
        executeSql( "update chart set lastsixmonth = false where lastsixmonth is null" );
        executeSql( "update chart set last12months = false where last12months is null" );
        executeSql( "update chart set last3months = false where last3months is null" );
        executeSql( "update chart set last5years = false where last5years is null" );
        executeSql( "update chart set last4quarters = false where last4quarters is null" );
        executeSql( "update chart set last6bimonths = false where last6bimonths is null" );
        executeSql( "update chart set last4quarters = false where last4quarters is null" );
        executeSql( "update chart set last2sixmonths = false where last2sixmonths is null" );
        executeSql( "update chart set showdata = false where showdata is null" );
        executeSql( "update chart set userorganisationunit = false where userorganisationunit is null" );
        executeSql( "update chart set userorganisationunitchildren = false where userorganisationunitchildren is null" );
        executeSql( "update chart set userorganisationunitgrandchildren = false where userorganisationunitgrandchildren is null" );
        executeSql( "update chart set hidetitle = false where hidetitle is null" );

        // Move chart filters to chart_filters table

        executeSql( "insert into chart_filters (chartid, sort_order, filter) select chartid, 0, filter from chart" );
        executeSql( "alter table chart drop column filter" );

        // Upgrade chart dimension identifiers

        executeSql( "update chart set series = 'dx' where series = 'data'" );
        executeSql( "update chart set series = 'pe' where series = 'period'" );
        executeSql( "update chart set series = 'ou' where series = 'organisationunit'" );
        executeSql( "update chart set category = 'dx' where category = 'data'" );
        executeSql( "update chart set category = 'pe' where category = 'period'" );
        executeSql( "update chart set category = 'ou' where category = 'organisationunit'" );
        executeSql( "update chart_filters set filter = 'dx' where filter = 'data'" );
        executeSql( "update chart_filters set filter = 'pe' where filter = 'period'" );
        executeSql( "update chart_filters set filter = 'ou' where filter = 'organisationunit'" );

        executeSql( "update users set selfregistered = false where selfregistered is null" );
        executeSql( "update users set disabled = false where disabled is null" );
        executeSql( "update dataentryform set format = 1 where format is null" );

        executeSql( "update dataelementgroup set shortname=name where shortname is null and length(name)<=50" );
        executeSql( "update orgunitgroup set shortname=name where shortname is null and length(name)<=50" );

        // report, reporttable, chart groups

        executeSql( "DROP TABLE reportgroupmembers" );
        executeSql( "DROP TABLE reportgroup" );
        executeSql( "DROP TABLE reporttablegroupmembers" );
        executeSql( "DROP TABLE reporttablegroup" );
        executeSql( "DROP TABLE chartgroupmembers" );
        executeSql( "DROP TABLE chartgroup" );

        executeSql( "delete from usersetting where name='currentStyle' and value like '%blue/blue.css'" );
        executeSql( "delete from systemsetting where name='currentStyle' and value like '%blue/blue.css'" );

        executeSql( "update dataentryform set style='regular' where style is null" );

        executeSql( "UPDATE dataset SET skipaggregation = false WHERE skipaggregation IS NULL" );
        executeSql( "UPDATE dataset SET skipoffline = false WHERE skipoffline IS NULL" );
        executeSql( "UPDATE dataset SET renderastabs = false WHERE renderastabs IS NULL" );
        executeSql( "UPDATE dataset SET renderhorizontally = false WHERE renderhorizontally IS NULL" );

        executeSql( "UPDATE categorycombo SET skiptotal = false WHERE skiptotal IS NULL" );

        // short names
        executeSql( "ALTER TABLE dataelement ALTER COLUMN shortname TYPE character varying(50)" );
        executeSql( "ALTER TABLE indicator ALTER COLUMN shortname TYPE character varying(50)" );
        executeSql( "ALTER TABLE dataset ALTER COLUMN shortname TYPE character varying(50)" );
        executeSql( "ALTER TABLE organisationunit ALTER COLUMN shortname TYPE character varying(50)" );

        executeSql( "update report set type='jasperReportTable' where type is null and reporttableid is not null" );
        executeSql( "update report set type='jasperJdbc' where type is null and reporttableid is null" );

        // upgrade authorities
        executeSql( "UPDATE userroleauthorities SET authority='F_DOCUMENT_PUBLIC_ADD' WHERE authority='F_DOCUMENT_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_REPORT_PUBLIC_ADD' WHERE authority='F_REPORT_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_REPORTTABLE_PUBLIC_ADD' WHERE authority='F_REPORTTABLE_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_DATASET_PUBLIC_ADD' WHERE authority='F_DATASET_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_DATADICTIONARY_PUBLIC_ADD' WHERE authority='F_DATADICTIONARY_ADD'" );

        executeSql( "UPDATE userroleauthorities SET authority='F_DATAELEMENT_PUBLIC_ADD' WHERE authority='F_DATAELEMENT_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_DATAELEMENTGROUP_PUBLIC_ADD' WHERE authority='F_DATAELEMENTGROUP_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_DATAELEMENTGROUPSET_PUBLIC_ADD' WHERE authority='F_DATAELEMENTGROUPSET_ADD'" );

        executeSql( "UPDATE userroleauthorities SET authority='F_ORGUNITGROUP_PUBLIC_ADD' WHERE authority='F_ORGUNITGROUP_ADD'" );

        executeSql( "UPDATE userroleauthorities SET authority='F_INDICATOR_PUBLIC_ADD' WHERE authority='F_INDICATOR_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_INDICATORGROUP_PUBLIC_ADD' WHERE authority='F_INDICATORGROUP_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_INDICATORGROUPSET_PUBLIC_ADD' WHERE authority='F_INDICATORGROUPSET_ADD'" );

        executeSql( "UPDATE userroleauthorities SET authority='F_USERGROUP_PUBLIC_ADD' WHERE authority='F_USER_GRUP_ADD'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_USERGROUP_UPDATE' WHERE authority='F_USER_GRUP_UPDATE'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_USERGROUP_DELETE' WHERE authority='F_USER_GRUP_DELETE'" );
        executeSql( "UPDATE userroleauthorities SET authority='F_USERGROUP_LIST' WHERE authority='F_USER_GRUP_LIST'" );

        // remove unused authorities
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_CONCEPT_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_CONSTANT_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_DATAELEMENT_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_DATAELEMENTGROUP_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_DATAELEMENTGROUPSET_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_DATADICTIONARY_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_DATAELEMENT_MINMAX_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_DATASET_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_SECTION_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_DATAVALUE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_INDICATOR_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_INDICATORTYPE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_INDICATORGROUP_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_INDICATORGROUPSET_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_ORGANISATIONUNIT_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_ORGUNITGROUP_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_ORGUNITGROUPSET_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_USERROLE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_USERGROUP_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_VALIDATIONRULE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_VALIDATIONRULEGROUP_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_REPORT_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_SQLVIEW_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_VALIDATIONCRITERIA_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_OPTIONSET_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_ATTRIBUTE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PATIENTATTRIBUTE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PATIENT_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_UPDATE_PROGRAM_INDICATOR'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PROGRAM_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PROGRAMSTAGE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PROGRAMSTAGE_SECTION_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PATIENTIDENTIFIERTYPE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PROGRAM_ATTRIBUTE_UPDATE'" );
        executeSql( "DELETE FROM userroleauthorities WHERE authority='F_PATIENT_DATAVALUE_UPDATE'" );

        // update denominator of indicator which has indicatortype as 'number'
        executeSql( "UPDATE indicator SET denominator = 1, denominatordescription = '' WHERE indicatortypeid IN (SELECT DISTINCT indicatortypeid FROM indicatortype WHERE indicatornumber = true) AND denominator IS NULL" );

        // remove name/shortName uniqueness
        executeSql( "ALTER TABLE organisationunit DROP CONSTRAINT organisationunit_name_key" );
        executeSql( "ALTER TABLE orgunitgroup ADD CONSTRAINT orgunitgroup_name_key UNIQUE (name)" );
        executeSql( "ALTER TABLE orgunitgroupset ADD CONSTRAINT orgunitgroupset_name_key UNIQUE (name)" );
        executeSql( "ALTER TABLE indicator DROP CONSTRAINT indicator_name_key" );
        executeSql( "ALTER TABLE indicator DROP CONSTRAINT indicator_shortname_key" );
        executeSql( "ALTER TABLE indicatorgroup DROP CONSTRAINT indicatorgroup_name_key" );
        executeSql( "ALTER TABLE indicatorgroupset DROP CONSTRAINT indicatorgroupset_name_key" );
        executeSql( "ALTER TABLE dataset DROP CONSTRAINT dataset_name_key" );
        executeSql( "ALTER TABLE dataset DROP CONSTRAINT dataset_shortname_key" );
        executeSql( "ALTER TABLE document DROP CONSTRAINT document_name_key" );
        executeSql( "ALTER TABLE reporttable DROP CONSTRAINT reporttable_name_key" );
        executeSql( "ALTER TABLE report DROP CONSTRAINT report_name_key" );
        executeSql( "ALTER TABLE usergroup DROP CONSTRAINT usergroup_name_key" );
        executeSql( "ALTER TABLE datadictionary DROP CONSTRAINT datadictionary_name_key" );

        executeSql( "update relativeperiods set lastweek = false where lastweek is null" );
        executeSql( "update relativeperiods set last4weeks = false where last4weeks is null" );
        executeSql( "update relativeperiods set last12weeks = false where last12weeks is null" );

        upgradeChartRelativePeriods();
        upgradeReportTableRelativePeriods();
        upgradeReportTables();

        // clear out sharing of de-group/de-group-set for now
        executeSql( "UPDATE dataelementgroup SET userid=NULL WHERE userid IS NOT NULL" );
        executeSql( "UPDATE dataelementgroup SET publicaccess=NULL WHERE userid IS NOT NULL" );
        executeSql( "UPDATE dataelementgroupset SET userid=NULL WHERE userid IS NOT NULL" );
        executeSql( "UPDATE dataelementgroupset SET publicaccess=NULL WHERE userid IS NOT NULL" );

        // upgrade system charts/maps to public read-only sharing
        executeSql( "UPDATE chart SET publicaccess='r-------' WHERE user IS NULL AND publicaccess IS NULL;" );
        executeSql( "UPDATE map SET publicaccess='r-------' WHERE user IS NULL AND publicaccess IS NULL;" );

        executeSql( "UPDATE chart SET publicaccess='--------' WHERE user IS NULL AND publicaccess IS NULL;" );
        executeSql( "UPDATE map SET publicaccess='-------' WHERE user IS NULL AND publicaccess IS NULL;" );

        executeSql( "ALTER TABLE dataelement ALTER COLUMN domaintype SET NOT NULL" );
        executeSql( "update dataelementcategory set datadimension = false where datadimension is null" );

        executeSql( "UPDATE dataset SET dataelementdecoration=false WHERE dataelementdecoration is null" );

        executeSql( "alter table validationrulegroup rename column validationgroupid to validationrulegroupid" );
        executeSql( "alter table sqlview rename column viewid to sqlviewid" );

        executeSql( "UPDATE dashboard SET publicaccess='--------' WHERE publicaccess is null" );

        executeSql( "UPDATE optionset SET version=1 WHERE version IS NULL" );

        executeSql( "ALTER TABLE datavalue ALTER COLUMN lastupdated TYPE timestamp" );
        executeSql( "ALTER TABLE completedatasetregistration ALTER COLUMN date TYPE timestamp" );
        executeSql( "ALTER TABLE message ALTER COLUMN userid DROP NOT NULL" );
        executeSql( "ALTER TABLE message ALTER COLUMN messagetext TYPE text" );
        executeSql( "drop index crosstab" );

        executeSql( "delete from usersetting where name = 'dashboardConfig' or name = 'dashboardConfiguration'" );
        executeSql( "update usersetting set name = 'keyUiLocale' where name = 'currentLocale'" );
        executeSql( "update usersetting set name = 'keyDbLocale' where name = 'keyLocaleUserSetting'" );
        executeSql( "ALTER TABLE interpretation ALTER COLUMN userid DROP NOT NULL" );
        executeSql( "UPDATE interpretation SET publicaccess='r-------' WHERE publicaccess IS NULL;" );

        executeSql( "ALTER TABLE dataset DROP COLUMN symbol" );
        executeSql( "ALTER TABLE users ALTER COLUMN password DROP NOT NULL" );

        executeSql( "update categorycombo set dimensiontype = '" + DataElementCategoryCombo.DIMENSION_TYPE_DISAGGREGATION + "' where dimensiontype is null" );
        executeSql( "update dataelementcategory set dimensiontype = '" + DataElementCategoryCombo.DIMENSION_TYPE_DISAGGREGATION + "' where dimensiontype is null" );
        executeSql( "update dataset set categorycomboid = " + defaultCategoryComboId + " where categorycomboid is null" );

        // set default dataDimension on orgUnitGroupSet and deGroupSet
        executeSql( "UPDATE dataelementgroupset SET datadimension=true WHERE datadimension IS NULL" );
        executeSql( "ALTER TABLE dataelementgroupset ALTER COLUMN datadimension SET NOT NULL" );
        executeSql( "UPDATE orgunitgroupset SET datadimension=true WHERE datadimension IS NULL" );
        executeSql( "ALTER TABLE orgunitgroupset ALTER COLUMN datadimension SET NOT NULL" );

        upgradeDataValuesWithAttributeOptionCombo();
        upgradeMapViewsToAnalyticalObject();

        log.info( "Tables updated" );
    }

    private void upgradeDataValuesWithAttributeOptionCombo()
    {
        final String sql = statementBuilder.getNumberOfColumnsInPrimaryKey( "datavalue" );
        
        Integer no = statementManager.getHolder().queryForInteger( sql );
        
        if ( no >= 5 )
        {
            return; // attributeoptioncomboid already part of datavalue primary key
        }

        int optionComboId = getDefaultOptionCombo();

        executeSql( "alter table datavalue_audit drop constraint fk_datavalueaudit_datavalue;" );

        executeSql( "alter table datavalue drop constraint datavalue_pkey;" );

        executeSql( "alter table datavalue add column attributeoptioncomboid integer;" );
        executeSql( "update datavalue set attributeoptioncomboid = " + optionComboId + " where attributeoptioncomboid is null;" );
        executeSql( "alter table datavalue alter column attributeoptioncomboid set not null;" );
        executeSql( "alter table datavalue add constraint fk_datavalue_attributeoptioncomboid foreign key (attributeoptioncomboid) references categoryoptioncombo (categoryoptioncomboid) match simple;" );
        executeSql( "alter table datavalue add constraint datavalue_pkey primary key(dataelementid, periodid, sourceid, categoryoptioncomboid, attributeoptioncomboid);" );

        executeSql( "alter table datavalue_audit add constraint fk_datavalueaudit_datavalue foreign key (dataelementid, periodid, sourceid, categoryoptioncomboid, attributeoptioncomboid) " +
            "references datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, attributeoptioncomboid) match simple;" );

        log.info( "Data value table upgraded with attributeoptioncomboid column" );
    }

    private void upgradeMapViewsToAnalyticalObject()
    {
        executeSql( "insert into mapview_dataelements ( mapviewid, sort_order, dataelementid ) select mapviewid, 0, dataelementid from mapview where dataelementid is not null" );
        executeSql( "alter table mapview drop column dataelementid" );

        executeSql( "insert into mapview_dataelementoperands ( mapviewid, sort_order, dataelementoperandid ) select mapviewid, 0, dataelementoperandid from mapview where dataelementoperandid is not null" );
        executeSql( "alter table mapview drop column dataelementoperandid" );

        executeSql( "insert into mapview_indicators ( mapviewid, sort_order, indicatorid ) select mapviewid, 0, indicatorid from mapview where indicatorid is not null" );
        executeSql( "alter table mapview drop column indicatorid" );

        executeSql( "insert into mapview_organisationunits ( mapviewid, sort_order, organisationunitid ) select mapviewid, 0, parentorganisationunitid from mapview where parentorganisationunitid is not null" );
        executeSql( "alter table mapview drop column parentorganisationunitid" );

        executeSql( "insert into mapview_periods ( mapviewid, sort_order, periodid ) select mapviewid, 0, periodid from mapview where periodid is not null" );
        executeSql( "alter table mapview drop column periodid" );

        executeSql( "insert into mapview_orgunitlevels ( mapviewid, sort_order, orgunitlevel ) select m.mapviewid, 0, o.level " +
            "from mapview m join orgunitlevel o on (m.organisationunitlevelid=o.orgunitlevelid) where m.organisationunitlevelid is not null" );
        executeSql( "alter table mapview drop column organisationunitlevelid" );

        executeSql( "alter table mapview drop column dataelementgroupid" );
        executeSql( "alter table mapview drop column indicatorgroupid" );

        executeSql( "update mapview set userorganisationunit = false where userorganisationunit is null" );
        executeSql( "update mapview set userorganisationunitchildren = false where userorganisationunitchildren is null" );
        executeSql( "update mapview set userorganisationunitgrandchildren = false where userorganisationunitgrandchildren is null" );
    }

    private void upgradeChartRelativePeriods()
    {
        BatchHandler<RelativePeriods> batchHandler = batchHandlerFactory.createBatchHandler( RelativePeriodsBatchHandler.class ).init();

        try
        {
            String sql = "select reportingmonth, * from chart";

            ResultSet rs = statementManager.getHolder().getStatement().executeQuery( sql );

            while ( rs.next() )
            {
                RelativePeriods r = new RelativePeriods(
                    rs.getBoolean( "reportingmonth" ),
                    false,
                    rs.getBoolean( "reportingquarter" ),
                    rs.getBoolean( "lastsixmonth" ),
                    rs.getBoolean( "monthsthisyear" ),
                    rs.getBoolean( "quartersthisyear" ),
                    rs.getBoolean( "thisyear" ),
                    false, false,
                    rs.getBoolean( "lastyear" ),
                    rs.getBoolean( "last5years" ),
                    rs.getBoolean( "last12months" ),
                    rs.getBoolean( "last3months" ),
                    false,
                    rs.getBoolean( "last4quarters" ),
                    rs.getBoolean( "last2sixmonths" ),
                    false, false, false,
                    false, false, false, false );

                int chartId = rs.getInt( "chartid" );

                if ( !r.isEmpty() )
                {
                    int relativePeriodsId = batchHandler.insertObject( r, true );

                    String update = "update chart set relativeperiodsid=" + relativePeriodsId + " where chartid=" + chartId;

                    executeSql( update );

                    log.info( "Updated relative periods for chart with id: " + chartId );
                }
            }

            executeSql( "alter table chart drop column reportingmonth" );
            executeSql( "alter table chart drop column reportingquarter" );
            executeSql( "alter table chart drop column lastsixmonth" );
            executeSql( "alter table chart drop column monthsthisyear" );
            executeSql( "alter table chart drop column quartersthisyear" );
            executeSql( "alter table chart drop column thisyear" );
            executeSql( "alter table chart drop column lastyear" );
            executeSql( "alter table chart drop column last5years" );
            executeSql( "alter table chart drop column last12months" );
            executeSql( "alter table chart drop column last3months" );
            executeSql( "alter table chart drop column last4quarters" );
            executeSql( "alter table chart drop column last2sixmonths" );
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            batchHandler.flush();
        }
    }

    private void upgradeReportTableRelativePeriods()
    {
        BatchHandler<RelativePeriods> batchHandler = batchHandlerFactory.createBatchHandler( RelativePeriodsBatchHandler.class ).init();

        try
        {
            String sql = "select reportingmonth, * from reporttable";

            ResultSet rs = statementManager.getHolder().getStatement().executeQuery( sql );

            while ( rs.next() )
            {
                RelativePeriods r = new RelativePeriods(
                    rs.getBoolean( "reportingmonth" ),
                    rs.getBoolean( "reportingbimonth" ),
                    rs.getBoolean( "reportingquarter" ),
                    rs.getBoolean( "lastsixmonth" ),
                    rs.getBoolean( "monthsthisyear" ),
                    rs.getBoolean( "quartersthisyear" ),
                    rs.getBoolean( "thisyear" ),
                    rs.getBoolean( "monthslastyear" ),
                    rs.getBoolean( "quarterslastyear" ),
                    rs.getBoolean( "lastyear" ),
                    rs.getBoolean( "last5years" ),
                    rs.getBoolean( "last12months" ),
                    rs.getBoolean( "last3months" ),
                    false,
                    rs.getBoolean( "last4quarters" ),
                    rs.getBoolean( "last2sixmonths" ),
                    rs.getBoolean( "thisfinancialyear" ),
                    rs.getBoolean( "lastfinancialyear" ),
                    rs.getBoolean( "last5financialyears" ),
                    false, false, false, false );

                int reportTableId = rs.getInt( "reporttableid" );

                if ( !r.isEmpty() )
                {
                    int relativePeriodsId = batchHandler.insertObject( r, true );

                    String update = "update reporttable set relativeperiodsid=" + relativePeriodsId + " where reporttableid=" + reportTableId;

                    executeSql( update );

                    log.info( "Updated relative periods for report table with id: " + reportTableId );
                }
            }

            executeSql( "alter table reporttable drop column reportingmonth" );
            executeSql( "alter table reporttable drop column reportingbimonth" );
            executeSql( "alter table reporttable drop column reportingquarter" );
            executeSql( "alter table reporttable drop column lastsixmonth" );
            executeSql( "alter table reporttable drop column monthsthisyear" );
            executeSql( "alter table reporttable drop column quartersthisyear" );
            executeSql( "alter table reporttable drop column thisyear" );
            executeSql( "alter table reporttable drop column monthslastyear" );
            executeSql( "alter table reporttable drop column quarterslastyear" );
            executeSql( "alter table reporttable drop column lastyear" );
            executeSql( "alter table reporttable drop column last5years" );
            executeSql( "alter table reporttable drop column last12months" );
            executeSql( "alter table reporttable drop column last3months" );
            executeSql( "alter table reporttable drop column last4quarters" );
            executeSql( "alter table reporttable drop column last2sixmonths" );
            executeSql( "alter table reporttable drop column thisfinancialyear" );
            executeSql( "alter table reporttable drop column lastfinancialyear" );
            executeSql( "alter table reporttable drop column last5financialyears" );
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            batchHandler.flush();
        }
    }

    private void upgradeReportTables()
    {
        try
        {
            String sql = "select reporttableid, doindicators, doperiods, dounits, categorycomboid from reporttable";

            ResultSet rs = statementManager.getHolder().getStatement().executeQuery( sql );

            while ( rs.next() )
            {
                int id = rs.getInt( "reporttableid" );
                boolean doIndicators = rs.getBoolean( "doindicators" );
                boolean doPeriods = rs.getBoolean( "doperiods" );
                boolean doUnits = rs.getBoolean( "dounits" );
                int categoryComboId = rs.getInt( "categorycomboid" );

                int columnSortOrder = 0;
                int rowSortOrder = 0;

                if ( doIndicators )
                {
                    executeSql( "insert into reporttable_columns (reporttableid, dimension, sort_order) values (" + id + ",'dx'," + columnSortOrder + ");" );
                    columnSortOrder++;
                }
                else
                {
                    executeSql( "insert into reporttable_rows (reporttableid, dimension, sort_order) values (" + id + ",'dx'," + rowSortOrder + ");" );
                    rowSortOrder++;
                }

                if ( doPeriods )
                {
                    executeSql( "insert into reporttable_columns (reporttableid, dimension, sort_order) values (" + id + ",'pe'," + columnSortOrder + ");" );
                    columnSortOrder++;
                }
                else
                {
                    executeSql( "insert into reporttable_rows (reporttableid, dimension, sort_order) values (" + id + ",'pe'," + rowSortOrder + ");" );
                    rowSortOrder++;
                }

                if ( doUnits )
                {
                    executeSql( "insert into reporttable_columns (reporttableid, dimension, sort_order) values (" + id + ",'ou'," + columnSortOrder + ");" );
                    columnSortOrder++;
                }
                else
                {
                    executeSql( "insert into reporttable_rows (reporttableid, dimension, sort_order) values (" + id + ",'ou'," + rowSortOrder + ");" );
                    rowSortOrder++;
                }

                if ( categoryComboId > 0 )
                {
                    executeSql( "insert into reporttable_columns (reporttableid, dimension, sort_order) values (" + id + ",'co'," + columnSortOrder + ");" );
                }
            }

            executeSql( "alter table reporttable drop column doindicators" );
            executeSql( "alter table reporttable drop column doperiods" );
            executeSql( "alter table reporttable drop column dounits" );
            executeSql( "alter table reporttable drop column categorycomboid" );

            executeSql( "delete from configuration where configurationid not in (select configurationid from configuration limit 1)" );
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
    }

    private List<Integer> getDistinctIdList( String table, String col1 )
    {
        StatementHolder holder = statementManager.getHolder();

        List<Integer> distinctIds = new ArrayList<Integer>();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( "SELECT DISTINCT " + col1 + " FROM " + table );

            while ( resultSet.next() )
            {
                distinctIds.add( resultSet.getInt( 1 ) );
            }
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }

        return distinctIds;
    }

    private Map<Integer, List<Integer>> getIdMap( String table, String col1, String col2, List<Integer> distinctIds )
    {
        StatementHolder holder = statementManager.getHolder();

        Map<Integer, List<Integer>> idMap = new HashMap<Integer, List<Integer>>();

        try
        {
            Statement statement = holder.getStatement();

            for ( Integer distinctId : distinctIds )
            {
                List<Integer> foreignIds = new ArrayList<Integer>();

                ResultSet resultSet = statement.executeQuery( "SELECT " + col2 + " FROM " + table + " WHERE " + col1
                    + "=" + distinctId );

                while ( resultSet.next() )
                {
                    foreignIds.add( resultSet.getInt( 1 ) );
                }

                idMap.put( distinctId, foreignIds );
            }
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }

        return idMap;
    }

    private void updateSortOrder( String table, String col1, String col2 )
    {
        List<Integer> distinctIds = getDistinctIdList( table, col1 );

        log.info( "Got distinct ids: " + distinctIds.size() );

        Map<Integer, List<Integer>> idMap = getIdMap( table, col1, col2, distinctIds );

        log.info( "Got id map: " + idMap.size() );

        for ( Integer distinctId : idMap.keySet() )
        {
            int sortOrder = 1;

            for ( Integer foreignId : idMap.get( distinctId ) )
            {
                String sql = "UPDATE " + table + " SET sort_order=" + sortOrder++ + " WHERE " + col1 + "=" + distinctId
                    + " AND " + col2 + "=" + foreignId;

                int count = executeSql( sql );

                log.info( "Executed: " + count + " - " + sql );
            }
        }
    }

    private int executeSql( String sql )
    {
        try
        {
            //TODO use jdbcTemplate

            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( ex );

            return -1;
        }
    }

    private Integer getDefaultOptionCombo()
    {
        String sql =
            "select coc.categoryoptioncomboid from categoryoptioncombo coc " +
                "inner join categorycombos_optioncombos cco on coc.categoryoptioncomboid=cco.categoryoptioncomboid " +
                "inner join categorycombo cc on cco.categorycomboid=cc.categorycomboid " +
                "where cc.name='default';";

        return statementManager.getHolder().queryForInteger( sql );
    }

    private Integer getDefaultCategoryCombo()
    {
        String sql = "select categorycomboid from categorycombo where name = 'default'";

        return statementManager.getHolder().queryForInteger( sql );
    }

    private boolean updateDataSetAssociation()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet isUpdated = statement
                .executeQuery( "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'dataentryformassociation'" );

            if ( isUpdated.next() )
            {

                ResultSet resultSet = statement
                    .executeQuery( "SELECT associationid, dataentryformid FROM dataentryformassociation WHERE associationtablename = 'dataset'" );

                while ( resultSet.next() )
                {
                    executeSql( "UPDATE dataset SET dataentryform=" + resultSet.getInt( 2 ) + " WHERE datasetid="
                        + resultSet.getInt( 1 ) );
                }
                return true;
            }

            return false;

        }
        catch ( Exception ex )
        {
            log.debug( ex );
            return false;
        }
        finally
        {
            holder.close();
        }
    }

    private boolean updateProgramStageAssociation()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet isUpdated = statement
                .executeQuery( "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'dataentryformassociation'" );

            if ( isUpdated.next() )
            {
                ResultSet resultSet = statement
                    .executeQuery( "SELECT associationid, dataentryformid FROM dataentryformassociation WHERE associationtablename = 'programstage'" );

                while ( resultSet.next() )
                {
                    executeSql( "UPDATE programstage SET dataentryform=" + resultSet.getInt( 2 )
                        + " WHERE programstageid=" + resultSet.getInt( 1 ) );
                }
            }
            return true;
        }
        catch ( Exception ex )
        {
            log.debug( ex );
            return false;
        }
        finally
        {
            holder.close();
        }
    }
}
