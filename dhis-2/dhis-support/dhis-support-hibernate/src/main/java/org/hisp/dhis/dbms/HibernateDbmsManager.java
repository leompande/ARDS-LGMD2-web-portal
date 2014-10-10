package org.hisp.dhis.dbms;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class HibernateDbmsManager
    implements DbmsManager
{
    private static final Log log = LogFactory.getLog( HibernateDbmsManager.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private HibernateCacheManager cacheManager;

    public void setCacheManager( HibernateCacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    // -------------------------------------------------------------------------
    // DbmsManager implementation
    // -------------------------------------------------------------------------

    public void emptyDatabase()
    {
        emptyTable( "translation" );
        emptyTable( "importobject" );
        emptyTable( "importdatavalue" );
        emptyTable( "constant" );
        emptyTable( "sqlview" );

        emptyTable( "datavalue_audit" );
        emptyTable( "datavalue" );
        emptyTable( "completedatasetregistration" );

        emptyTable( "reporttable_dataelements" );
        emptyTable( "reporttable_datasets" );
        emptyTable( "reporttable_indicators" );
        emptyTable( "reporttable_periods" );
        emptyTable( "reporttable_organisationunits" );
        emptyTable( "reporttable_dataelementgroups" );
        emptyTable( "reporttable_orgunitgroups" );
        emptyTable( "reporttable_columns" );
        emptyTable( "reporttable_rows" );
        emptyTable( "reporttable_filters" );
        emptyTable( "reporttable" );

        emptyTable( "userrolemembers" );
        emptyTable( "userroledataset" );
        emptyTable( "userroleauthorities" );
        emptyTable( "usergroupmembers" );
        emptyTable( "usergroup" );
        emptyTable( "users" );
        emptyTable( "userinfo" );
        emptyTable( "userrole" );

        emptyTable( "orgunitgroupsetmembers" );
        emptyTable( "orgunitgroupset" );
        emptyTable( "orgunitgroupmembers" );
        emptyTable( "orgunitgroup" );

        emptyTable( "datadictionarydataelements" );
        emptyTable( "datadictionaryindicators" );
        emptyTable( "datadictionary" );

        emptyTable( "validationrulegroupuserrolestoalert" );
        emptyTable( "validationrulegroupmembers" );
        emptyTable( "validationrulegroup" );
        emptyTable( "validationrule" );

        emptyTable( "datasetsource" );
        emptyTable( "datasetmembers" );
        emptyTable( "datasetindicators" );
        emptyTable( "datasetoperands" );
        emptyTable( "dataset" );

        emptyTable( "patientdatavalue" );
        emptyTable( "programstageinstance" );
        emptyTable( "programinstance" );
        emptyTable( "programstage_dataelements" );
        emptyTable( "programstage" );
        emptyTable( "program_organisationunits" );
        emptyTable( "program" );
        emptyTable( "patientidentifier" );
        emptyTable( "patientidentifier_patient" );
        emptyTable( "patient" );

        emptyTable( "minmaxdataelement" );
        emptyTable( "expressiondataelement" );
        emptyTable( "expressionoptioncombo" );
        emptyTable( "calculateddataelement" );
        emptyTable( "dataelementgroupsetmembers" );
        emptyTable( "dataelementgroupset" );
        emptyTable( "dataelementgroupmembers" );
        emptyTable( "dataelementgroup" );
        emptyTable( "dataelementaggregationlevels" );
        emptyTable( "dataelementoperand" );
        emptyTable( "dataelement" );
        emptyTable( "categoryoptioncombos_categoryoptions" );
        emptyTable( "categorycombos_optioncombos" );
        emptyTable( "categorycombos_categories" );
        emptyTable( "categories_categoryoptions" );

        emptyTable( "organisationunit" );
        emptyTable( "version" );
        emptyTable( "mocksource" );
        emptyTable( "period" );

        emptyTable( "indicatorgroupsetmembers" );
        emptyTable( "indicatorgroupset" );
        emptyTable( "indicatorgroupmembers" );
        emptyTable( "indicatorgroup" );
        emptyTable( "indicator" );
        emptyTable( "indicatortype" );

        emptyTable( "expression" );
        emptyTable( "categoryoptioncombo" );
        emptyTable( "categorycombo" );
        emptyTable( "dataelementcategory" );
        emptyTable( "dataelementcategoryoption" );

        emptyTable( "optionsetmembers" );
        emptyTable( "optionset" );

        dropTable( "aggregateddatavalue" );
        dropTable( "aggregatedindicatorvalue" );
        dropTable( "aggregateddatasetcompleteness" );

        dropTable( "aggregatedorgunitdatavalue" );
        dropTable( "aggregatedorgunitindicatorvalue" );
        dropTable( "aggregatedorgunitdatasetcompleteness" );

        log.debug( "Cleared database contents" );

        cacheManager.clearCache();

        log.debug( "Cleared Hibernate cache" );
    }

    public void clearSession()
    {
        sessionFactory.getCurrentSession().clear();
    }

    public void emptyTable( String table )
    {
        try
        {
            jdbcTemplate.update( "DELETE FROM " + table );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.debug( "Table " + table + " does not exist" );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void dropTable( String table )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE " + table );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.debug( "Table " + table + " does not exist" );
        }
    }
}
