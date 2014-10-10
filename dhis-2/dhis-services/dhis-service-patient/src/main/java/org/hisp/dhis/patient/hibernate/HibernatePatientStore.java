package org.hisp.dhis.patient.hibernate;

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

import static org.hisp.dhis.patient.Patient.PREFIX_IDENTIFIER_TYPE;
import static org.hisp.dhis.patient.Patient.PREFIX_PATIENT_ATTRIBUTE;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM_EVENT_BY_STATUS;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM_INSTANCE;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM_STAGE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.PatientStore;
import org.hisp.dhis.patient.TrackedEntityQueryParams;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.util.SqlHelper;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.validation.ValidationCriteria;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 */
@Transactional
public class HibernatePatientStore
    extends HibernateIdentifiableObjectStore<Patient>
    implements PatientStore
{
    private static final Log log = LogFactory.getLog( HibernatePatientStore.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByOrgUnit( OrganisationUnit organisationUnit, Integer min, Integer max )
    {
        String hql = "select p from Patient p where p.organisationUnit = :organisationUnit order by p.id DESC";

        Query query = getQuery( hql );
        query.setEntity( "organisationUnit", organisationUnit );

        if ( min != null && max != null )
        {
            query.setFirstResult( min ).setMaxResults( max );
        }

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByOrgUnitProgram( OrganisationUnit organisationUnit, Program program, Integer min,
        Integer max )
    {
        String hql = "select pt from Patient pt inner join pt.programInstances pi "
            + "where pt.organisationUnit = :organisationUnit " + "and pi.program = :program "
            + "and pi.status = :status";

        Query query = getQuery( hql );
        query.setEntity( "organisationUnit", organisationUnit );
        query.setEntity( "program", program );
        query.setInteger( "status", ProgramInstance.STATUS_ACTIVE );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public List<Patient> query( TrackedEntityQueryParams params )
    {
        SqlHelper hlp = new SqlHelper();

        String hql = "select pt from Patient pt left join pt.attributeValues av";

        for ( QueryItem at : params.getAttributes() )
        {
            hql += " " + hlp.whereAnd();
            hql += " (av.patientAttribute = :attr" + at.getItemId() + " and av.value = :filt" + at.getItemId() + ")";
        }

        Query query = getQuery( hql );

        for ( QueryItem at : params.getAttributes() )
        {
            query.setEntity( "attr" + at.getItemId(), at.getItem() );
            query.setString( "filt" + at.getItemId(), at.getFilter() );
        }

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByProgram( Program program, Integer min, Integer max )
    {
        String hql = "select pt from Patient pt inner join pt.programInstances pi "
            + "where pi.program = :program and pi.status = :status";

        Query query = getQuery( hql );
        query.setEntity( "program", program );
        query.setInteger( "status", ProgramInstance.STATUS_ACTIVE );

        return query.list();
    }

    @Override
    public int countGetPatientsByName( String fullName )
    {
        fullName = fullName.toLowerCase();
        String sql = "SELECT count(*) FROM patient where lower( name ) " + "like '%" + fullName + "%' ";

        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    @Override
    public int countListPatientByOrgunit( OrganisationUnit organisationUnit )
    {
        Query query = getQuery( "select count(p.id) from Patient p where p.organisationUnit.id=:orgUnitId " );

        query.setParameter( "orgUnitId", organisationUnit.getId() );

        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public int countGetPatientsByOrgUnitProgram( OrganisationUnit organisationUnit, Program program )
    {
        String sql = "select count(p.patientid) from patient p join programinstance pi on p.patientid=pi.patientid "
            + "where p.organisationunitid=" + organisationUnit.getId() + " and pi.programid=" + program.getId()
            + " and pi.status=" + ProgramInstance.STATUS_ACTIVE;

        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getRepresentatives( Patient patient )
    {
        String hql = "select distinct p from Patient p where p.representative = :representative order by p.id DESC";

        return getQuery( hql ).setEntity( "representative", patient ).list();
    }

    @Override
    // TODO this method must be changed - cannot retrieve one by one
    public Collection<Patient> search( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        String sql = searchPatientSql( false, searchKeys, orgunits, followup, patientAttributes, identifierTypes,
            statusEnrollment, min, max );
        Collection<Patient> patients = new HashSet<Patient>();
        try
        {
            patients = jdbcTemplate.query( sql, new RowMapper<Patient>()
            {
                public Patient mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    return get( rs.getInt( 1 ) );
                }
            } );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return patients;
    }

    @Override
    public List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        String sql = searchPatientSql( false, searchKeys, orgunits, followup, patientAttributes, identifierTypes,
            statusEnrollment, min, max );

        List<Integer> programStageInstanceIds = new ArrayList<Integer>();
        try
        {
            programStageInstanceIds = jdbcTemplate.query( sql, new RowMapper<Integer>()
            {
                public Integer mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    return rs.getInt( "programstageinstanceid" );
                }
            } );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return programStageInstanceIds;
    }

    public int countSearch( List<String> searchKeys, Collection<OrganisationUnit> orgunits, Boolean followup,
        Integer statusEnrollment )
    {
        String sql = searchPatientSql( true, searchKeys, orgunits, followup, null, null, statusEnrollment, null, null );
        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    @Override
    public Grid getPatientEventReport( Grid grid, List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        String sql = searchPatientSql( false, searchKeys, orgunits, followup, patientAttributes, identifierTypes,
            statusEnrollment, min, max );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        GridUtils.addRows( grid, rowSet );

        return grid;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByPhoneNumber( String phoneNumber, Integer min, Integer max )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "attributeValues", "attributeValue" );
        criteria.createAlias( "attributeValue.patientAttribute", "patientAttribute" );
        criteria.add( Restrictions.eq( "patientAttribute.valueType", PatientAttribute.TYPE_PHONE_NUMBER ) );
        criteria.add( Restrictions.like( "attributeValue.value", phoneNumber ) );

        if ( min != null && max != null )
        {
            criteria.setFirstResult( min );
            criteria.setMaxResults( max );
        }

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getRegistrationOrgunitIds( Date startDate, Date endDate )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.between( "registrationDate", startDate, endDate ) );
        criteria.createAlias( "organisationUnit", "orgunit" );
        criteria.setProjection( Projections.distinct( Projections.projectionList().add(
            Projections.property( "orgunit.id" ), "orgunitid" ) ) );

        return criteria.list();
    }

    public int validate( Patient patient, Program program, I18nFormat format )
    {
        if ( patient.getIdentifiers() != null && patient.getIdentifiers().size() > 0 )
        {
            Criteria criteria = getCriteria();
            criteria.createAlias( "identifiers", "patientIdentifier" );
            criteria.createAlias( "organisationUnit", "orgunit" );
            criteria.createAlias( "programInstances", "programInstance" );

            Disjunction disjunction = Restrictions.disjunction();

            for ( PatientIdentifier identifier : patient.getIdentifiers() )
            {
                PatientIdentifierType patientIdentifierType = identifier.getIdentifierType();

                Conjunction conjunction = Restrictions.conjunction();
                conjunction.add( Restrictions.eq( "patientIdentifier.identifier", identifier.getIdentifier() ) );
                conjunction.add( Restrictions.eq( "patientIdentifier.identifierType", patientIdentifierType ) );

                if ( patient.getId() != 0 )
                {
                    conjunction.add( Restrictions.ne( "id", patient.getId() ) );
                }

                if ( patientIdentifierType.getType().equals( PatientIdentifierType.VALUE_TYPE_LOCAL_ID )
                    && patientIdentifierType.getOrgunitScope() )
                {
                    conjunction.add( Restrictions.eq( "orgunit.id", patient.getOrganisationUnit().getId() ) );
                }

                if ( program != null
                    && patientIdentifierType.getType().equals( PatientIdentifierType.VALUE_TYPE_LOCAL_ID )
                    && patientIdentifierType.getProgramScope() )
                {
                    conjunction.add( Restrictions.eq( "programInstance.program", program ) );
                }

                if ( patientIdentifierType.getType().equals( PatientIdentifierType.VALUE_TYPE_LOCAL_ID )
                    && patientIdentifierType.getPeriodType() != null )
                {
                    Date currentDate = new Date();
                    Period period = patientIdentifierType.getPeriodType().createPeriod( currentDate );
                    conjunction.add( Restrictions.between( "programInstance.enrollmentDate", period.getStartDate(),
                        period.getEndDate() ) );
                }

                disjunction.add( conjunction );
            }

            criteria.add( disjunction );

            Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

            if ( rs != null && rs.intValue() > 0 )
            {
                return PatientService.ERROR_DUPLICATE_IDENTIFIER;
            }
        }

        if ( program != null )
        {
            ValidationCriteria validationCriteria = validateEnrollment( patient, program, format );

            if ( validationCriteria != null )
            {
                return PatientService.ERROR_ENROLLMENT;
            }
        }

        return PatientService.ERROR_NONE;
    }

    public ValidationCriteria validateEnrollment( Patient patient, Program program, I18nFormat format )
    {
        try
        {
            for ( ValidationCriteria criteria : program.getPatientValidationCriteria() )
            {
                String value = "";
                for ( PatientAttributeValue attributeValue : patient.getAttributeValues() )
                {
                    if ( attributeValue.getPatientAttribute().getUid().equals( criteria.getProperty() ) )
                    {
                        value = attributeValue.getValue();
                        if ( attributeValue.getPatientAttribute().getValueType().equals( PatientAttribute.TYPE_AGE ) )
                        {
                            value = PatientAttribute.getAgeFromDate( format.parseDate( value ) ) + "";
                        }

                        if ( !value.isEmpty() )
                        {
                            String type = attributeValue.getPatientAttribute().getValueType();
                            // For integer type
                            if ( type.equals( PatientAttribute.TYPE_AGE ) || type.equals( PatientAttribute.TYPE_INT ) )
                            {
                                int value1 = Integer.parseInt( value );
                                int value2 = Integer.parseInt( criteria.getValue() );

                                if ( (criteria.getOperator() == ValidationCriteria.OPERATOR_LESS_THAN && value1 >= value2)
                                    || (criteria.getOperator() == ValidationCriteria.OPERATOR_EQUAL_TO && value1 != value2)
                                    || (criteria.getOperator() == ValidationCriteria.OPERATOR_GREATER_THAN && value1 <= value2) )
                                {
                                    return criteria;
                                }
                            }
                            // For Date type
                            else if ( type.equals( PatientAttribute.TYPE_DATE ) )
                            {
                                Date value1 = format.parseDate( value );
                                Date value2 = format.parseDate( criteria.getValue() );
                                int i = value1.compareTo( value2 );
                                if ( i != criteria.getOperator() )
                                {
                                    return criteria;
                                }
                            }
                            // For other types
                            else
                            {
                                if ( criteria.getOperator() == ValidationCriteria.OPERATOR_EQUAL_TO
                                    && !value.equals( criteria.getValue() ) )
                                {
                                    return criteria;
                                }

                            }
                        }

                    }
                }

            }

            // Return null if all criteria are met

            return null;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods TODO Remplement all this!
    // -------------------------------------------------------------------------

    private String searchPatientSql( boolean count, List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        String selector = count ? "count(*) " : "* ";
        String sql = "select " + selector + " from ( select distinct p.patientid,";

        if ( identifierTypes != null )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                sql += "(select identifier from patientidentifier where patientid=p.patientid and patientidentifiertypeid="
                    + identifierType.getId() + " ) as " + PREFIX_IDENTIFIER_TYPE + "_" + identifierType.getId() + " ,";
            }
        }

        if ( patientAttributes != null )
        {
            for ( PatientAttribute patientAttribute : patientAttributes )
            {
                sql += "(select value from patientattributevalue where patientid=p.patientid and patientattributeid="
                    + patientAttribute.getId() + " ) as " + PREFIX_PATIENT_ATTRIBUTE + "_" + patientAttribute.getId()
                    + " ,";
            }
        }

        String patientWhere = "";
        String patientOperator = " where ";
        String patientGroupBy = " GROUP BY  p.patientid ";
        String otherWhere = "";
        String operator = " where ";
        String orderBy = "";
        boolean hasIdentifier = false;
        boolean isSearchEvent = false;
        boolean isPriorityEvent = false;
        Collection<Integer> orgunitChilrenIds = null;

        if ( orgunits != null )
        {
            orgunitChilrenIds = getOrgunitChildren( orgunits );
        }

        for ( String searchKey : searchKeys )
        {
            String[] keys = searchKey.split( "_" );

            if ( keys.length <= 1 || keys[1] == null || keys[1].trim().isEmpty() || keys[1].equals( "null" ) )
            {
                continue;
            }

            String id = keys[1];
            String value = "";

            if ( keys.length >= 3 )
            {
                value = keys[2];
            }

            if ( keys[0].equals( PREFIX_IDENTIFIER_TYPE ) )
            {

                String[] keyValues = id.split( " " );
                patientWhere += patientOperator + " (";
                String opt = "";
                for ( String v : keyValues )
                {
                    patientWhere += opt + " ( lower(pi.identifier) like '%" + v
                        + "%' and pi.patientidentifiertypeid is not null ) ";
                    opt = "or";
                }

                patientWhere += ")";
                patientOperator = " and ";
                hasIdentifier = true;
            }
            else if ( keys[0].equals( PREFIX_PATIENT_ATTRIBUTE ) )
            {
                sql += "(select value from patientattributevalue where patientid=p.patientid and patientattributeid="
                    + id + " ) as " + PREFIX_PATIENT_ATTRIBUTE + "_" + id + ",";

                String[] keyValues = value.split( " " );
                otherWhere += operator + "(";
                String opt = "";

                for ( String v : keyValues )
                {
                    otherWhere += opt + " lower(" + PREFIX_PATIENT_ATTRIBUTE + "_" + id + ") like '%" + v + "%'";
                    opt = "or";
                }

                otherWhere += ")";
                operator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM ) )
            {
                sql += "(select programid from programinstance pgi where patientid=p.patientid and programid=" + id;

                if ( statusEnrollment != null )
                {
                    sql += " and pgi.status=" + statusEnrollment;
                }

                sql += " limit 1 ) as " + PREFIX_PROGRAM + "_" + id + ",";
                otherWhere += operator + PREFIX_PROGRAM + "_" + id + "=" + id;
                operator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_INSTANCE ) )
            {
                sql += "(select pi." + id + " from programinstance pi where patientid=p.patientid and pi.status=0 ";

                if ( keys.length == 5 )
                {
                    sql += " and pi.programid=" + keys[4];
                }
                else
                {
                    sql += " limit 1 ";
                }

                sql += ") as " + PREFIX_PROGRAM_INSTANCE + "_" + id + ",";
                otherWhere += operator + PREFIX_PROGRAM_INSTANCE + "_" + id + "='" + keys[2] + "'";
                operator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_EVENT_BY_STATUS ) )
            {
                isSearchEvent = true;
                isPriorityEvent = Boolean.parseBoolean( keys[5] );
                patientWhere += patientOperator + "pgi.patientid=p.patientid and ";
                patientWhere += "pgi.programid=" + id + " and ";
                patientWhere += "pgi.status=" + ProgramInstance.STATUS_ACTIVE;

                String operatorStatus = "";
                String condition = " and ( ";

                for ( int index = 6; index < keys.length; index++ )
                {
                    int statusEvent = Integer.parseInt( keys[index] );
                    switch ( statusEvent )
                    {
                    case ProgramStageInstance.COMPLETED_STATUS:
                        patientWhere += condition + operatorStatus
                            + "( psi.executiondate is not null and  psi.executiondate>='" + keys[2]
                            + "' and psi.executiondate<='" + keys[3] + "' and psi.completed=true ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            patientWhere += " and psi.organisationunitid in( "
                                + TextUtils.getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            patientWhere += " and psi.organisationunitid=" + getOrgUnitId( keys );
                        }

                        patientWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.VISITED_STATUS:
                        patientWhere += condition + operatorStatus
                            + "( psi.executiondate is not null and psi.executiondate>='" + keys[2]
                            + "' and psi.executiondate<='" + keys[3] + "' and psi.completed=false ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            patientWhere += " and psi.organisationunitid in( "
                                + TextUtils.getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            patientWhere += " and psi.organisationunitid=" + getOrgUnitId( keys );
                        }

                        patientWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.FUTURE_VISIT_STATUS:
                        patientWhere += condition + operatorStatus + "( psi.executiondate is null and psi.duedate>='"
                            + keys[2] + "' and psi.duedate<='" + keys[3]
                            + "' and psi.status is not null and (DATE(now()) - DATE(psi.duedate) <= 0) ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            patientWhere += " and p.organisationunitid in( "
                                + TextUtils.getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            patientWhere += " and p.organisationunitid=" + getOrgUnitId( keys );
                        }

                        patientWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.LATE_VISIT_STATUS:
                        patientWhere += condition + operatorStatus + "( psi.executiondate is null and  psi.duedate>='"
                            + keys[2] + "' and psi.duedate<='" + keys[3]
                            + "' and psi.status is not null and (DATE(now()) - DATE(psi.duedate) > 0) ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            patientWhere += " and p.organisationunitid in( "
                                + TextUtils.getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            patientWhere += " and p.organisationunitid=" + getOrgUnitId( keys );
                        }

                        patientWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.SKIPPED_STATUS:
                        patientWhere += condition + operatorStatus + "( psi.status=5 and  psi.duedate>='" + keys[2]
                            + "' and psi.duedate<='" + keys[3] + "' ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            patientWhere += " and psi.organisationunitid in( "
                                + TextUtils.getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            patientWhere += " and p.organisationunitid=" + getOrgUnitId( keys );
                        }
                        patientWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    default:
                        continue;
                    }
                }
                if ( condition.isEmpty() )
                {
                    patientWhere += ")";
                }

                patientWhere += " and pgi.status=" + ProgramInstance.STATUS_ACTIVE + " ";
                patientOperator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_STAGE ) )
            {
                isSearchEvent = true;
                patientWhere += patientOperator + "pgi.patientid=p.patientid and psi.programstageid=" + id + " and ";
                patientWhere += "psi.duedate>='" + keys[3] + "' and psi.duedate<='" + keys[4] + "' and ";
                patientWhere += "psi.organisationunitid = " + keys[5] + " and ";

                int statusEvent = Integer.parseInt( keys[2] );
                switch ( statusEvent )
                {
                case ProgramStageInstance.COMPLETED_STATUS:
                    patientWhere += "psi.completed=true";
                    break;
                case ProgramStageInstance.VISITED_STATUS:
                    patientWhere += "psi.executiondate is not null and psi.completed=false";
                    break;
                case ProgramStageInstance.FUTURE_VISIT_STATUS:
                    patientWhere += "psi.executiondate is null and psi.duedate >= now()";
                    break;
                case ProgramStageInstance.LATE_VISIT_STATUS:
                    patientWhere += "psi.executiondate is null and psi.duedate < now()";
                    break;
                default:
                    break;
                }

                patientWhere += " and pgi.status=" + ProgramInstance.STATUS_ACTIVE + " ";
                patientOperator = " and ";
            }
        }

        if ( orgunits != null && !isSearchEvent )
        {
            sql += "(select organisationunitid from patient where patientid=p.patientid and organisationunitid in ( "
                + TextUtils.getCommaDelimitedString( getOrganisationUnitIds( orgunits ) ) + " ) ) as orgunitid,";
            otherWhere += operator + "orgunitid in ( "
                + TextUtils.getCommaDelimitedString( getOrganisationUnitIds( orgunits ) ) + " ) ";
        }

        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Removing last comma

        String from = " from patient p ";

        if ( isSearchEvent )
        {
            String subSQL = " , psi.programstageinstanceid as programstageinstanceid, pgs.name as programstagename, psi.duedate as duedate ";

            if ( isPriorityEvent )
            {
                subSQL += ",pgi.followup ";
                orderBy = " ORDER BY pgi.followup desc, p.patientid, duedate asc ";
                patientGroupBy += ",pgi.followup ";
            }
            else
            {
                orderBy = " ORDER BY p.patientid, duedate asc ";
            }

            sql = sql + subSQL + from + " inner join programinstance pgi on " + " (pgi.patientid=p.patientid) "
                + " inner join programstageinstance psi on (psi.programinstanceid=pgi.programinstanceid) "
                + " inner join programstage pgs on (pgs.programstageid=psi.programstageid) ";

            patientGroupBy += ",psi.programstageinstanceid, pgs.name, psi.duedate ";

            from = " ";
        }

        if ( hasIdentifier )
        {
            sql += from + " left join patientidentifier pi on p.patientid=pi.patientid ";
            from = " ";
        }

        sql += from + patientWhere;
        if ( followup != null )
        {
            sql += " AND pgi.followup=" + followup;
        }
        if ( isSearchEvent )
        {
            sql += patientGroupBy;
        }
        sql += orderBy;
        sql += " ) as searchresult";
        sql += otherWhere;

        if ( min != null && max != null )
        {
            sql += " limit " + max + " offset " + min;
        }

        log.info( "Search patient SQL: " + sql );

        return sql;
    }

    private Integer getOrgUnitId( String[] keys )
    {
        Integer orgUnitId;
        try
        {
            orgUnitId = Integer.parseInt( keys[4] );
        }
        catch ( NumberFormatException e )
        {
            // handle as uid
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( keys[4] );
            orgUnitId = ou.getId();
        }
        return orgUnitId;
    }

    private Collection<Integer> getOrgunitChildren( Collection<OrganisationUnit> orgunits )
    {
        Collection<Integer> orgUnitIds = new HashSet<Integer>();

        if ( orgunits != null )
        {
            for ( OrganisationUnit orgunit : orgunits )
            {
                orgUnitIds
                    .addAll( organisationUnitService.getOrganisationUnitHierarchy().getChildren( orgunit.getId() ) );
                orgUnitIds.remove( orgunit.getId() );
            }
        }

        if ( orgUnitIds.size() == 0 )
        {
            orgUnitIds.add( 0 );
        }

        return orgUnitIds;
    }

    private Collection<Integer> getOrganisationUnitIds( Collection<OrganisationUnit> orgunits )
    {
        Collection<Integer> orgUnitIds = new HashSet<Integer>();

        for ( OrganisationUnit orgUnit : orgunits )
        {
            orgUnitIds.add( orgUnit.getId() );
        }

        if ( orgUnitIds.size() == 0 )
        {
            orgUnitIds.add( 0 );
        }

        return orgUnitIds;
    }

    @SuppressWarnings( { "unchecked" } )
    @Override
    public Collection<Patient> getByPatientAttributeValue( String searchText, int patientAttributeId, Integer min,
        Integer max )
    {

        String hql = "FROM PatientAttributeValue pav WHERE lower (pav.value) LIKE lower ('%" + searchText
            + "%') AND pav.patientAttribute.id =:patientAttributeId order by pav.patient";

        Query query = getQuery( hql );

        query.setInteger( "patientAttributeId", patientAttributeId );

        if ( min != null && max != null )
        {
            query.setFirstResult( min ).setMaxResults( max );
        }

        List<Patient> patients = new ArrayList<Patient>();
        Collection<PatientAttributeValue> patientAttributeValue = query.list();
        for ( PatientAttributeValue pv : patientAttributeValue )
        {
            patients.add( pv.getPatient() );
        }

        return patients;

    }
}