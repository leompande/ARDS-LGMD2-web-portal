package org.hisp.dhis.caseaggregation.hibernate;

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

import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PATIENT_ATTRIBUTE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PATIENT_PROGRAM_STAGE_PROPERTY;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_PROPERTY;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_PROPERTY;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_ID;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_OBJECT;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_12_MONTH;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_3_MONTH;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_6_MONTH;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_MONTH;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.caseaggregation.CaseAggregateSchedule;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Chau Thu Tran
 * 
 * @version HibernateCaseAggregationConditionStore.java Nov 18, 2010 9:36:20 AM
 */
public class HibernateCaseAggregationConditionStore
    extends HibernateIdentifiableObjectStore<CaseAggregationCondition>
    implements CaseAggregationConditionStore
{
    private final String IS_NULL = "is null";

    private final String IN_CONDITION_GET_ALL = "*";

    private final String IN_CONDITION_START_SIGN = "@";

    private final String IN_CONDITION_END_SIGN = "#";

    private final String IN_CONDITION_COUNT_X_TIMES = "COUNT";

    public static final String STORED_BY_DHIS_SYSTEM = "aggregated_from_tracker";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Implementation Methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<CaseAggregationCondition> get( DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "aggregationDataElement", dataElement ) ).list();
    }

    @Override
    public CaseAggregationCondition get( DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        return (CaseAggregationCondition) getCriteria( Restrictions.eq( "aggregationDataElement", dataElement ),
            Restrictions.eq( "optionCombo", optionCombo ) ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<CaseAggregationCondition> get( Collection<DataElement> dataElements )
    {
        return getCriteria( Restrictions.in( "aggregationDataElement", dataElements ) ).list();
    }

    public Grid getAggregateValue( CaseAggregationCondition caseAggregationCondition, Collection<Integer> orgunitIds,
        Period period, I18nFormat format, I18n i18n )
    {
        Collection<Integer> _orgunitIds = getServiceOrgunit();
        _orgunitIds.retainAll( orgunitIds );

        if ( _orgunitIds.size() > 0 )
        {
            Grid grid = new ListGrid();
            grid.setTitle( caseAggregationCondition.getDisplayName() );
            grid.setSubtitle( format.formatPeriod( period ) );

            grid.addHeader( new GridHeader( i18n.getString( "dataelementid" ), true, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "categoryoptioncomboid" ), true, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "periodid" ), true, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "organisationunitid" ), true, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "comment" ), true, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "dataelementname" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "categoryoptioncomboname" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "organisationunitname" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "value" ), false, true ) );

            Integer deSumId = (caseAggregationCondition.getDeSum() == null) ? null : caseAggregationCondition
                .getDeSum().getId();
            String sql = parseExpressionToSql( false, caseAggregationCondition.getAggregationExpression(),
                caseAggregationCondition.getOperator(), caseAggregationCondition.getAggregationDataElement().getId(),
                caseAggregationCondition.getAggregationDataElement().getDisplayName(), caseAggregationCondition
                    .getOptionCombo().getId(), caseAggregationCondition.getOptionCombo().getDisplayName(), deSumId,
                _orgunitIds, period );

            SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );
            grid.addRows( rs );

            return grid;
        }

        return null;
    }

    @Override
    public Grid getAggregateValueDetails( CaseAggregationCondition aggregationCondition, OrganisationUnit orgunit,
        Period period, I18nFormat format, I18n i18n )
    {
        Grid grid = new ListGrid();
        grid.setTitle( orgunit.getName() + " - " + aggregationCondition.getDisplayName() );
        grid.setSubtitle( format.formatPeriod( period ) );

        String sql = parseExpressionDetailsToSql( aggregationCondition.getAggregationExpression(),
            aggregationCondition.getOperator(), orgunit.getId(), period );

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        for ( String colName : rs.getMetaData().getColumnNames() )
        {
            grid.addHeader( new GridHeader( i18n.getString( colName ), false, true ) );
        }

        grid.addRows( rs );

        return grid;
    }

    public void insertAggregateValue( String expression, String operator, Integer dataElementId, Integer optionComboId,
        Integer deSumId, Collection<Integer> orgunitIds, Period period )
    {
        // Delete all data value from this period which created from DHIS-system
        // after to run Aggregate Query Builder
        String periodtypeSql = "select periodtypeid from periodtype where name='" + period.getPeriodType().getName()
            + "'";
        int periodTypeId = jdbcTemplate.queryForObject( periodtypeSql, Integer.class );
        String periodSql = "select periodid from period where periodtypeid=" + periodTypeId + " and startdate='"
            + DateUtils.getMediumDateString( period.getStartDate() ) + "' and enddate='"
            + DateUtils.getMediumDateString( period.getEndDate() ) + "'";
        SqlRowSet rs = jdbcTemplate.queryForRowSet( periodSql );
        int periodid = 0;

        if ( rs.next() )
        {
            periodid = rs.getInt( "periodid" );
        }

        if ( periodid == 0 )
        {
            String insertSql = "insert into period (periodtypeid,startdate,enddate) " + " VALUES " + "("
                + period.getPeriodType().getId() + ",'" + DateUtils.getMediumDateString( period.getStartDate() )
                + "','" + DateUtils.getMediumDateString( period.getEndDate() ) + "' )";
            jdbcTemplate.execute( insertSql );

            period.setId( jdbcTemplate.queryForObject( insertSql, Integer.class ) );
        }
        else
        {
            period.setId( periodid );

            String deleteDataValueSql = "delete from datavalue where dataelementid=" + dataElementId
                + " and categoryoptioncomboid=" + optionComboId + " and sourceid in ("
                + TextUtils.getCommaDelimitedString( orgunitIds ) + ") and periodid=" + periodid + "";

            jdbcTemplate.execute( deleteDataValueSql );
        }

        // insert data elements into database directly

        String sql = parseExpressionToSql( true, expression, operator, dataElementId, "dataelementname", optionComboId,
            "optionComboname", deSumId, orgunitIds, period );
        jdbcTemplate.execute( sql );
    }

    @Override
    public String parseExpressionToSql( boolean isInsert, String caseExpression, String operator,
        Integer aggregateDeId, String aggregateDeName, Integer optionComboId, String optionComboName, Integer deSumId,
        Collection<Integer> orgunitIds, Period period )
    {
        String sql = "SELECT '" + aggregateDeId + "' as dataelementid, '" + optionComboId
            + "' as categoryoptioncomboid, '" + optionComboId
            + "' as attributeoptioncomboid, ou.organisationunitid as sourceid, '" + period.getId() + "' as periodid,'"
            + CaseAggregationCondition.AUTO_STORED_BY + "' as comment, ";

        if ( isInsert )
        {
            sql = "INSERT INTO datavalue (dataelementid, categoryoptioncomboid, attributeoptioncomboid, sourceid, periodid, comment, value) "
                + sql;
        }
        else
        {
            sql += "'" + period.getIsoDate() + "' as periodIsoDate,'" + aggregateDeName + "' as dataelementname, '"
                + optionComboName + "' as categoryoptioncomboname, ou.name as organisationunitname, ";
        }

        if ( operator.equals( CaseAggregationCondition.AGGRERATION_COUNT )
            || operator.equals( CaseAggregationCondition.AGGRERATION_SUM ) )
        {
            if ( hasOrgunitProgramStageCompleted( caseExpression ) )
            {
                sql += createSQL( caseExpression, operator, orgunitIds,
                    DateUtils.getMediumDateString( period.getStartDate() ),
                    DateUtils.getMediumDateString( period.getEndDate() ) );
            }
            else
            {
                if ( operator.equals( CaseAggregationCondition.AGGRERATION_COUNT ) )
                {
                    sql += " count(distinct(pi.patientid)) as value ";
                }
                else
                {
                    sql += " count(psi.programinstanceid) as value ";
                }

                sql += "FROM ";
                boolean hasDataelement = hasDataelementCriteria( caseExpression );
                boolean hasPatient = hasPatientCriteria( caseExpression );

                if ( hasPatient && hasDataelement )
                {
                    sql += " programinstance as pi ";
                    sql += " INNER JOIN patient p on p.patientid=pi.patientid ";
                    sql += " INNER JOIN programstageinstance psi ON pi.programinstanceid=psi.programinstanceid ";
                    sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=psi.organisationunitid ";
                }
                else if ( hasPatient )
                {
                    sql += " programinstance as pi INNER JOIN patient p on p.patientid=pi.patientid ";
                    sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=p.organisationunitid ";
                }
                else
                {
                    sql += " programinstance as pi ";
                    sql += " INNER JOIN programstageinstance psi ON pi.programinstanceid=psi.programinstanceid ";
                    sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=psi.organisationunitid ";
                }

                sql += " WHERE "
                    + createSQL( caseExpression, operator, orgunitIds,
                        DateUtils.getMediumDateString( period.getStartDate() ),
                        DateUtils.getMediumDateString( period.getEndDate() ) );

                sql += "GROUP BY ou.organisationunitid, ou.name";
            }
        }
        else
        {
            sql += " " + operator + "( cast( pdv.value as DOUBLE PRECISION ) ) ";
            sql += "FROM patientdatavalue pdv ";
            sql += "    INNER JOIN programstageinstance psi  ";
            sql += "            ON psi.programstageinstanceid = pdv.programstageinstanceid ";
            sql += "    INNER JOIN organisationunit ou ";
            sql += "            ON ou.organisationunitid=psi.organisationunitid ";
            sql += "WHERE executiondate >='" + DateUtils.getMediumDateString( period.getStartDate() ) + "'  ";
            sql += "    AND executiondate <='" + DateUtils.getMediumDateString( period.getEndDate() )
                + "' AND pdv.dataelementid=" + deSumId;

            if ( caseExpression != null && !caseExpression.isEmpty() )
            {
                sql += " AND "
                    + createSQL( caseExpression, operator, orgunitIds,
                        DateUtils.getMediumDateString( period.getStartDate() ),
                        DateUtils.getMediumDateString( period.getEndDate() ) );
            }

            sql += "GROUP BY ou.organisationunitid, ou.name";

        }

        sql = sql.replaceAll( "COMBINE", "" );

        return sql;
    }

    @Override
    public void runAggregate( Collection<Integer> orgunitIds, CaseAggregateSchedule dataSet, Collection<Period> periods )
    {
        String sql = "select caseaggregationconditionid, aggregationdataelementid, optioncomboid, "
            + " cagg.aggregationexpression as caseexpression, cagg.operator as caseoperator, cagg.desum as desumid "
            + "     from caseaggregationcondition cagg inner join datasetmembers dm "
            + "             on cagg.aggregationdataelementid=dm.dataelementid inner join dataset ds "
            + "             on ds.datasetid = dm.datasetid inner join periodtype pt "
            + "             on pt.periodtypeid=ds.periodtypeid inner join dataelement de "
            + "             on de.dataelementid=dm.dataelementid where ds.datasetid = " + dataSet.getDataSetId();

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        while ( rs.next() )
        {
            for ( Period period : periods )
            {
                // -------------------------------------------------------------
                // Get formula, agg-dataelement and option-combo
                // -------------------------------------------------------------

                int dataelementId = rs.getInt( "aggregationdataelementid" );
                int optionComboId = rs.getInt( "optioncomboid" );
                String caseExpression = rs.getString( "caseexpression" );
                String caseOperator = rs.getString( "caseoperator" );
                int deSumId = rs.getInt( "desumid" );

                Collection<Integer> _orgunitIds = getServiceOrgunit();

                if ( orgunitIds == null )
                {
                    orgunitIds = new HashSet<Integer>();
                    orgunitIds.addAll( _orgunitIds );
                }
                else
                {
                    orgunitIds.retainAll( _orgunitIds );
                }

                // ---------------------------------------------------------------------
                // Aggregation
                // ---------------------------------------------------------------------

                if ( !orgunitIds.isEmpty() )
                {
                    insertAggregateValue( caseExpression, caseOperator, dataelementId, optionComboId, deSumId,
                        orgunitIds, period );
                }
            }

        }
    }

    /**
     * Return standard SQL from query builder formula
     * 
     * @param caseExpression The query builder expression
     * @param operator There are six operators, includes Number of persons,
     *        Number of visits, Sum, Average, Minimum and Maximum of data
     *        element values.
     * @param deType Aggregate Data element type
     * @param orgunitIds The ids of organisation units where to aggregate data
     *        value
     * @param startDate Start date
     * @param endDate End date
     */
    private String createSQL( String caseExpression, String operator, Collection<Integer> orgunitIds, String startDate,
        String endDate )
    {
        boolean orgunitCompletedProgramStage = false;

        StringBuffer sqlResult = new StringBuffer();

        String sqlOrgunitCompleted = "";

        // Get minus(date dataelement, date dataelement) out from the expression
        // and run them later

        Map<Integer, String> minus2SQLMap = new HashMap<Integer, String>();
        int idx2 = 0;
        Pattern patternMinus2 = Pattern.compile( CaseAggregationCondition.minusDataelementRegExp );
        Matcher matcherMinus2 = patternMinus2.matcher( caseExpression );
        while ( matcherMinus2.find() )
        {
            String[] ids1 = matcherMinus2.group( 2 ).split( SEPARATOR_ID );
            String[] ids2 = matcherMinus2.group( 5 ).split( SEPARATOR_ID );

            minus2SQLMap.put(
                idx2,
                getConditionForMisus2DataElement( orgunitIds, ids1[1], ids1[2], ids2[1], ids2[2],
                    matcherMinus2.group( 6 ) + matcherMinus2.group( 7 ), startDate, endDate ) );

            caseExpression = caseExpression.replace( matcherMinus2.group( 0 ),
                CaseAggregationCondition.MINUS_DATAELEMENT_OPERATOR + "_" + idx2 );

            idx2++;
        }

        // Get minus(date dataelement, date) out from the expression and run
        // them later

        Map<Integer, String> minusSQLMap = new HashMap<Integer, String>();
        int idx = 0;
        Pattern patternMinus = Pattern.compile( CaseAggregationCondition.dataelementRegExp );
        Matcher matcherMinus = patternMinus.matcher( caseExpression );
        while ( matcherMinus.find() )
        {
            String[] ids = matcherMinus.group( 2 ).split( SEPARATOR_ID );

            minusSQLMap.put(
                idx,
                getConditionForMinusDataElement( orgunitIds, Integer.parseInt( ids[1] ), Integer.parseInt( ids[2] ),
                    matcherMinus.group( 4 ), startDate, endDate ) );

            caseExpression = caseExpression.replace( matcherMinus.group( 0 ), CaseAggregationCondition.MINUS_OPERATOR
                + "_" + idx );

            idx++;
        }

        // Run nornal expression
        String[] expression = caseExpression.split( "(AND|OR)" );
        caseExpression = caseExpression.replaceAll( "AND", " ) AND " );
        caseExpression = caseExpression.replaceAll( "OR", " ) OR " );

        // ---------------------------------------------------------------------
        // parse expressions
        // ---------------------------------------------------------------------

        Pattern patternCondition = Pattern.compile( CaseAggregationCondition.regExp );

        Matcher matcherCondition = patternCondition.matcher( caseExpression );

        String condition = "";

        int index = 0;
        while ( matcherCondition.find() )
        {
            String match = matcherCondition.group();

            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            if ( info[0].equalsIgnoreCase( OBJECT_PATIENT_ATTRIBUTE ) )
            {
                String attributeId = info[1];

                String compareValue = expression[index].replace( "[" + match + "]", "" ).trim();

                boolean isExist = compareValue.equals( IS_NULL ) ? false : true;
                condition = getConditionForPatientAttribute( attributeId, orgunitIds, isExist );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE_DATAELEMENT ) )
            {
                String[] ids = info[1].split( SEPARATOR_ID );

                int programId = Integer.parseInt( ids[0] );
                String programStageId = ids[1];
                int dataElementId = Integer.parseInt( ids[2] );

                String compareValue = expression[index].replace( "[" + match + "]", "" ).trim();

                boolean isExist = compareValue.equals( IS_NULL ) ? false : true;
                condition = getConditionForDataElement( isExist, programId, programStageId, dataElementId, orgunitIds,
                    startDate, endDate );
            }

            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_PROPERTY ) )
            {
                condition = getConditionForProgramProperty( operator, startDate, endDate, info[1] );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM ) )
            {
                String[] ids = info[1].split( SEPARATOR_ID );
                condition = getConditionForProgram( ids[0], operator, orgunitIds, startDate, endDate );
                if ( ids.length > 1 )
                {
                    condition += ids[1];
                }
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE ) )
            {
                String[] ids = info[1].split( SEPARATOR_ID );
                if ( ids.length == 2 && ids[1].equals( IN_CONDITION_COUNT_X_TIMES ) )
                {
                    condition = getConditionForCountProgramStage( ids[0], operator, orgunitIds, startDate, endDate );
                }
                else
                {
                    condition = getConditionForProgramStage( ids[0], orgunitIds, startDate, endDate );
                }
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE_PROPERTY ) )
            {
                condition = getConditionForProgramStageProperty( info[1], operator, orgunitIds, startDate, endDate );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PATIENT_PROGRAM_STAGE_PROPERTY ) )
            {
                condition = getConditionForPatientProgramStageProperty( info[1], operator, startDate, endDate );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE ) )
            {
                sqlOrgunitCompleted += getConditionForOrgunitProgramStageCompleted( info[1], operator, orgunitIds,
                    startDate, endDate, orgunitCompletedProgramStage );
                orgunitCompletedProgramStage = true;
            }

            matcherCondition.appendReplacement( sqlResult, condition );

            index++;
        }

        matcherCondition.appendTail( sqlResult );

        if ( !sqlOrgunitCompleted.isEmpty() )
        {
            sqlOrgunitCompleted = sqlOrgunitCompleted.substring( 0, sqlOrgunitCompleted.length() - 2 );
        }

        sqlResult.append( sqlOrgunitCompleted );

        String sql = sqlResult.toString();

        sql = sql.replaceAll( IN_CONDITION_START_SIGN, "(" );
        sql = sql.replaceAll( IN_CONDITION_END_SIGN, ")" );
        sql = sql.replaceAll( IS_NULL, " " );

        for ( int key = 0; key < idx; key++ )
        {
            sql = sql.replace( CaseAggregationCondition.MINUS_OPERATOR + "_" + key, minusSQLMap.get( key ) );
        }

        for ( int key = 0; key < idx2; key++ )
        {
            sql = sql
                .replace( CaseAggregationCondition.MINUS_DATAELEMENT_OPERATOR + "_" + key, minus2SQLMap.get( key ) );
        }

        return sql + " ) ";
    }

    /**
     * Return standard SQL of the expression to compare data value as null
     * 
     */
    private String getConditionForDataElement( boolean isExist, int programId, String programStageId,
        int dataElementId, Collection<Integer> orgunitIds, String startDate, String endDate )
    {
        String keyExist = (isExist == true) ? "EXISTS" : "NOT EXISTS";

        String sql = " " + keyExist + " ( SELECT * "
            + "FROM patientdatavalue _pdv inner join programstageinstance _psi "
            + "ON _pdv.programstageinstanceid=_psi.programstageinstanceid JOIN programinstance _pi "
            + "ON _pi.programinstanceid=_psi.programinstanceid "
            + "WHERE psi.programstageinstanceid=_pdv.programstageinstanceid AND _pdv.dataelementid=" + dataElementId
            + "  AND _psi.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds ) + ")  "
            + "  AND _pi.programid = " + programId + " AND _psi.executionDate>='" + startDate
            + "' AND _psi.executionDate <= '" + endDate + "' ";

        if ( !programStageId.equals( IN_CONDITION_GET_ALL ) )
        {
            sql += " AND _psi.programstageid = " + programStageId;
        }

        if ( isExist )
        {
            DataElement dataElement = dataElementService.getDataElement( dataElementId );
            if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
            {
                sql += " AND ( cast( _pdv.value as " + statementBuilder.getDoubleColumnType() + " )  ) ";
            }
            else
            {
                sql += " AND _pdv.value ";
            }
        }
        else
        {
            sql = "(" + sql + " ) AND " + getConditionForProgramStage( programStageId, orgunitIds, startDate, endDate ) + ")";
        }

        return sql;
    }

    /**
     * Return standard SQL of a dynamic patient-attribute expression. E.g [CA:1]
     * OR [CA:1.age]
     * 
     */
    private String getConditionForPatientAttribute( String attributeId, Collection<Integer> orgunitIds, boolean isExist )
    {
        String sql = "  SELECT * FROM patientattributevalue _pav " + " WHERE _pav.patientid=pi.patientid ";

        if ( attributeId.split( SEPARATOR_ID ).length == 2 )
        {
            attributeId = attributeId.split( SEPARATOR_ID )[0];
            sql += " AND _pav.patientattributeid=" + attributeId + " AND DATE(now()) - DATE( _pav.value ) ";
        }
        else
        {
            sql += " AND _pav.patientattributeid=" + attributeId + " AND _pav.value ";
        }

        if ( isExist )
        {
            sql = " EXISTS ( " + sql;
        }
        else
        {
            sql = " NOT ( " + sql;
        }

        return sql;
    }

    /**
     * Return standard SQL of the program-property expression. E.g
     * [PC:executionDate]
     * 
     */
    private String getConditionForPatientProgramStageProperty( String propertyName, String operator, String startDate,
        String endDate )
    {
        String sql = " EXISTS ( SELECT _psi.programstageinstanceid from programstageinstance _psi "
            + "WHERE _psi.programstageinstanceid=psi.programstageinstanceid AND ( _psi.executionDate BETWEEN '"
            + startDate + "' AND '" + endDate + "') AND " + propertyName;

        return sql;
    }

    /**
     * Return standard SQL of the program expression. E.g
     * [PP:DATE@enrollmentdate#-DATE@dateofincident#] for geting the number of
     * days between date of enrollment and date of incident.
     * 
     */
    private String getConditionForProgramProperty( String operator, String startDate, String endDate, String property )
    {
        String sql = " EXISTS ( SELECT _pi.programinstanceid FROM programinstance as _pi WHERE _pi.programinstanceid=pi.programinstanceid AND "
            + "pi.enrollmentdate >= '"
            + startDate
            + "' AND pi.enrollmentdate <= '"
            + endDate
            + "' AND "
            + property
            + " ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the number of persons enrolled into the
     * program. E.g [PG:1]
     * 
     */
    private String getConditionForProgram( String programId, String operator, Collection<Integer> orgunitIds,
        String startDate, String endDate )
    {
        String sql = " EXISTS ( SELECT * FROM programinstance as _pi inner join patient _p on _p.patientid=_pi.patientid "
            + "WHERE _pi.patientid=pi.patientid AND _pi.programid="
            + programId
            + " AND _p.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") AND _pi.enrollmentdate >= '"
            + startDate
            + "' AND _pi.enrollmentdate <= '" + endDate + "' ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the number of visits a program-stage. E.g
     * [PS:1]
     * 
     */
    private String getConditionForProgramStage( String programStageId, Collection<Integer> orgunitIds,
        String startDate, String endDate )
    {
        String sql = " EXISTS ( SELECT _psi.programstageinstanceid FROM programstageinstance _psi "
            + "WHERE _psi.programstageinstanceid=psi.programstageinstanceid " + "AND _psi.programstageid="
            + programStageId + "  AND _psi.executiondate >= '" + startDate + "' AND _psi.executiondate <= '" + endDate
            + "' AND _psi.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds ) + ")  ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the x-time of a person visited one
     * program-stage. E.g a mother came to a hospital 3th time for third
     * trimester.
     * 
     */
    private String getConditionForCountProgramStage( String programStageId, String operator,
        Collection<Integer> orgunitIds, String startDate, String endDate )
    {
        String sql = " EXISTS ( SELECT _psi.programstageinstanceid FROM programstageinstance as _psi "
            + "WHERE psi.programstageinstanceid=_psi.programstageinstanceid AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds ) + ") and _psi.programstageid = " + programStageId + " "
            + "AND _psi.executionDate >= '" + startDate + "' AND _psi.executionDate <= '" + endDate + "' "
            + "GROUP BY _psi.programinstanceid,_psi.programstageinstanceid "
            + "HAVING count(_psi.programstageinstanceid) ";

        return sql;

    }

    /**
     * Return standard SQL to retrieve the number of days between report-date
     * and due-date. E.g [PSP:DATE@executionDate#-DATE@dueDate#]
     * 
     */
    private String getConditionForProgramStageProperty( String property, String operator,
        Collection<Integer> orgunitIds, String startDate, String endDate )
    {
        String sql = " EXISTS ( SELECT * FROM programstageinstance _psi "
            + "WHERE psi.programstageinstanceid=_psi.programstageinstanceid AND _psi.executiondate >= '" + startDate
            + "' AND _psi.executiondate <= '" + endDate + "' AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds ) + ") AND " + property + " ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the number of children orgunits has all
     * program-stage-instance completed and due-date. E.g [PSIC:1]
     * 
     * @flag True if there are many stages in the expression
     * 
     */
    private String getConditionForOrgunitProgramStageCompleted( String programStageId, String operator,
        Collection<Integer> orgunitIds, String startDate, String endDate, boolean flag )
    {
        String sql = "";
        if ( !flag )
        {
            sql = " '1' FROM organisationunit ou WHERE ou.organisationunitid in ("
                + TextUtils.getCommaDelimitedString( orgunitIds ) + ")  ";
        }

        sql += " AND EXISTS ( SELECT programstageinstanceid FROM programstageinstance _psi "
            + " WHERE _psi.organisationunitid=ou.organisationunitid AND _psi.programstageid = " + programStageId
            + " AND _psi.completed=true AND _psi.executiondate >= '" + startDate + "' AND _psi.executiondate <= '"
            + endDate + "' ) ";

        return sql;
    }

    private String getConditionForMinusDataElement( Collection<Integer> orgunitIds, Integer programStageId,
        Integer dataElementId, String compareSide, String startDate, String endDate )
    {
        return " EXISTS ( SELECT _pdv.value FROM patientdatavalue _pdv inner join programstageinstance _psi "
            + "                         ON _pdv.programstageinstanceid=_psi.programstageinstanceid "
            + "                 JOIN programinstance _pi ON _pi.programinstanceid=_psi.programinstanceid "
            + "           WHERE psi.programstageinstanceid=_pdv.programstageinstanceid "
            + "                  AND _pdv.dataelementid=" + dataElementId
            + "                 AND _psi.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") " + "                 AND _psi.programstageid = " + programStageId
            + " AND ( _psi.executionDate BETWEEN '" + startDate + "' AND '" + endDate + "') "
            + "                 AND ( DATE(_pdv.value) - DATE(" + compareSide + ") ) ";
    }

    private String getConditionForMisus2DataElement( Collection<Integer> orgunitIds, String programStageId1,
        String dataElementId1, String programStageId2, String dataElementId2, String compareSide, String startDate,
        String endDate )
    {
        return " EXISTS ( SELECT * FROM ( SELECT _pdv.value FROM patientdatavalue _pdv "
            + "                 INNER JOIN programstageinstance _psi ON _pdv.programstageinstanceid=_psi.programstageinstanceid "
            + "                 JOIN programinstance _pi ON _pi.programinstanceid=_psi.programinstanceid "
            + "           WHERE psi.programstageinstanceid=_pdv.programstageinstanceid AND _pdv.dataelementid= "
            + dataElementId1
            + "                 AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") "
            + "                 AND _psi.programstageid = "
            + programStageId1
            + "                 AND _psi.executionDate>='"
            + startDate
            + "'  "
            + "                 AND _psi.executionDate <= '"
            + endDate
            + "' ) AS d1 cross join "
            + "         (  SELECT _pdv.value FROM patientdatavalue _pdv INNER JOIN programstageinstance _psi "
            + "                        ON _pdv.programstageinstanceid=_psi.programstageinstanceid "
            + "                  JOIN programinstance _pi ON _pi.programinstanceid=_psi.programinstanceid "
            + "           WHERE psi.programstageinstanceid=_pdv.programstageinstanceid and _pdv.dataelementid= "
            + dataElementId2
            + "                 AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") "
            + "                 AND _psi.programstageid =  "
            + programStageId2
            + "                 AND _psi.executionDate>='"
            + startDate
            + "'  "
            + "                 AND _psi.executionDate <= '"
            + endDate
            + "' ) AS d2 WHERE DATE(d1.value ) - DATE(d2.value) " + compareSide;
    }

    /**
     * Return the Ids of organisation units which patients registered or events
     * happened.
     * 
     */
    private Collection<Integer> getServiceOrgunit()
    {
        String sql = "(select distinct organisationunitid from patient)";
        sql += " UNION ";
        sql += "(select distinct organisationunitid from programstageinstance where organisationunitid is not null)";

        Collection<Integer> orgunitIds = new HashSet<Integer>();
        orgunitIds = jdbcTemplate.query( sql, new RowMapper<Integer>()
        {
            public Integer mapRow( ResultSet rs, int rowNum )
                throws SQLException
            {
                return rs.getInt( 1 );
            }
        } );

        return orgunitIds;
    }

    @Override
    public String parseExpressionDetailsToSql( String caseExpression, String operator, Integer orgunitId, Period period )
    {
        String sql = "SELECT ";

        boolean hasPatients = hasPatientCriteria( caseExpression );
        boolean hasDataelement = hasDataelementCriteria( caseExpression );

        Collection<Integer> orgunitIds = new HashSet<Integer>();
        orgunitIds.add( orgunitId );

        if ( hasOrgunitProgramStageCompleted( caseExpression ) )
        {
            sql += "ou.name "
                + createSQL( caseExpression, operator, orgunitIds,
                    DateUtils.getMediumDateString( period.getStartDate() ),
                    DateUtils.getMediumDateString( period.getEndDate() ) );
        }
        else
        {
            if ( hasPatients || operator.equals( CaseAggregationCondition.AGGRERATION_COUNT ) )
            {
                sql += "p.name,";
            }

            if ( hasDataelement )
            {
                sql += "pdv.value,pgs.name as program_stage, psi.executiondate as report_date,";
            }
        }

        sql = sql.substring( 0, sql.length() - 1 );
        sql += " FROM ";

        if ( hasDataelement )
        {
            sql += " programinstance as pi INNER JOIN patient p on p.patientid=pi.patientid";
            sql += " INNER JOIN programstageinstance psi ON pi.programinstanceid=psi.programinstanceid ";
            sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=psi.organisationunitid ";
            sql += " INNER JOIN patientdatavalue pdv ON pdv.programstageinstanceid=psi.programstageinstanceid ";
            sql += " INNER JOIN program pg ON pg.programid=pi.programid ";
            sql += " INNER JOIN programstage pgs ON pgs.programid=pg.programid ";
        }
        else
        {
            sql += " programinstance as pi INNER JOIN patient p on p.patientid=pi.patientid";
            sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=p.organisationunitid ";
        }

        sql += " WHERE "
            + createSQL( caseExpression, operator, orgunitIds, DateUtils.getMediumDateString( period.getStartDate() ),
                DateUtils.getMediumDateString( period.getEndDate() ) );

        sql = sql.replaceAll( "COMBINE", "" );

        return sql;
    }

    @Override
    public List<Integer> executeSQL( String sql )
    {
        try
        {
            List<Integer> patientIds = jdbcTemplate.query( sql, new RowMapper<Integer>()
            {
                public Integer mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    return rs.getInt( 1 );
                }
            } );

            return patientIds;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Period> getPeriods( String periodTypeName, String taskStrategy )
    {
        Calendar calStartDate = Calendar.getInstance();

        if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -1 );
        }
        else if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_3_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -3 );
        }
        else if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_6_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -6 );
        }
        else if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_12_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -12 );
        }

        Date startDate = calStartDate.getTime();

        Calendar calEndDate = Calendar.getInstance();

        Date endDate = calEndDate.getTime();

        CalendarPeriodType periodType = (CalendarPeriodType) PeriodType.getPeriodTypeByName( periodTypeName );
        String sql = "select periodtypeid from periodtype where name='" + periodTypeName + "'";
        int periodTypeId = jdbcTemplate.queryForObject( sql, Integer.class );

        Collection<Period> periods = periodType.generatePeriods( startDate, endDate );

        for ( Period period : periods )
        {
            String start = DateUtils.getMediumDateString( period.getStartDate() );
            String end = DateUtils.getMediumDateString( period.getEndDate() );

            sql = "select periodid from period where periodtypeid=" + periodTypeId + " and startdate='" + start
                + "' and enddate='" + end + "'";
            Integer periodid = null;
            SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );
            if ( rs.next() )
            {
                periodid = rs.getInt( "periodid" );
            }

            if ( periodid == null )
            {
                String insertSql = "insert into period (periodtypeid,startdate,enddate) " + " VALUES " + "("
                    + periodTypeId + ",'" + start + "','" + end + "' )";
                jdbcTemplate.execute( insertSql );

                period.setId( jdbcTemplate.queryForObject( sql, Integer.class ) );
            }
            else
            {
                period.setId( periodid );
            }
        }

        return periods;
    }

    private boolean hasOrgunitProgramStageCompleted( String expresstion )
    {
        Pattern pattern = Pattern.compile( CaseAggregationCondition.regExp );
        Matcher matcher = pattern.matcher( expresstion );
        while ( matcher.find() )
        {
            String match = matcher.group();

            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );

            if ( info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean hasPatientCriteria( String expresstion )
    {
        Pattern pattern = Pattern.compile( CaseAggregationCondition.regExp );
        Matcher matcher = pattern.matcher( expresstion );
        while ( matcher.find() )
        {
            String match = matcher.group();

            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );

            if ( info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PATIENT_ATTRIBUTE ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean hasDataelementCriteria( String expresstion )
    {
        Pattern pattern = Pattern.compile( CaseAggregationCondition.regExp );
        Matcher matcher = pattern.matcher( expresstion );
        while ( matcher.find() )
        {
            String match = matcher.group();

            match = match.replaceAll( "[\\[\\]]", "" );
            String[] info = match.split( SEPARATOR_OBJECT );

            if ( info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PROGRAM_STAGE )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PROGRAM_STAGE_PROPERTY )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PATIENT_PROGRAM_STAGE_PROPERTY )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE ) )
            {
                return true;
            }
        }

        return false;
    }

}
