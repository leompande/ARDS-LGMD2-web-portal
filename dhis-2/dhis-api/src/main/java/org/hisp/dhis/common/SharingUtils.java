package org.hisp.dhis.common;

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

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.patientreport.PatientAggregateReport;
import org.hisp.dhis.patientreport.PatientTabularReport;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupAccess;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public final class SharingUtils
{
    public static Map<Class<? extends IdentifiableObject>, String> EXTERNAL_AUTHORITIES = new HashMap<Class<? extends IdentifiableObject>, String>();

    public static Map<Class<? extends IdentifiableObject>, String> PUBLIC_AUTHORITIES = new HashMap<Class<? extends IdentifiableObject>, String>();

    public static Map<Class<? extends IdentifiableObject>, String> PRIVATE_AUTHORITIES = new HashMap<Class<? extends IdentifiableObject>, String>();

    public static final Map<String, Class<? extends IdentifiableObject>> SUPPORTED_TYPES = new HashMap<String, Class<? extends IdentifiableObject>>();

    public static final List<String> SHARING_OVERRIDE_AUTHORITIES = Arrays.asList( "ALL", "F_METADATA_IMPORT" );

    private static void addType( Class<? extends IdentifiableObject> clazz, String name, String externalAuth, String publicAuth, String privateAuth )
    {
        Assert.notNull( clazz );
        Assert.hasLength( name );

        SUPPORTED_TYPES.put( name, clazz );

        if ( externalAuth != null )
        {
            EXTERNAL_AUTHORITIES.put( clazz, externalAuth );
        }

        if ( publicAuth != null )
        {
            PUBLIC_AUTHORITIES.put( clazz, publicAuth );
        }

        if ( privateAuth != null )
        {
            PRIVATE_AUTHORITIES.put( clazz, privateAuth );
        }
    }

    static
    {
        addType( Document.class, "document", null, "F_DOCUMENT_PUBLIC_ADD", "F_DOCUMENT_PRIVATE_ADD" );
        addType( Report.class, "report", null, "F_REPORT_PUBLIC_ADD", "F_REPORT_PRIVATE_ADD" );
        addType( DataSet.class, "dataSet", null, "F_DATASET_PUBLIC_ADD", "F_DATASET_PRIVATE_ADD" );
        addType( DataDictionary.class, "dataDictionary", null, "F_DATADICTIONARY_PUBLIC_ADD", "F_DATADICTIONARY_PRIVATE_ADD" );
        addType( DataElement.class, "dataElement", null, "F_DATAELEMENT_PUBLIC_ADD", "F_DATAELEMENT_PRIVATE_ADD" );
        addType( OrganisationUnitGroup.class, "organisationUnitGroup", null, "F_ORGUNITGROUP_PUBLIC_ADD", "F_ORGUNITGROUP_PRIVATE_ADD" );
        addType( Indicator.class, "indicator", null, "F_INDICATOR_PUBLIC_ADD", "F_INDICATOR_PRIVATE_ADD" );
        addType( IndicatorGroup.class, "indicatorGroup", null, "F_INDICATORGROUP_PUBLIC_ADD", "F_INDICATORGROUP_PRIVATE_ADD" );
        addType( IndicatorGroupSet.class, "indicatorGroupSet", null, "F_INDICATORGROUPSET_PUBLIC_ADD", "F_INDICATORGROUPSET_PRIVATE_ADD" );
        addType( Program.class, "program", null, "F_PROGRAM_PUBLIC_ADD", "F_PROGRAM_PRIVATE_ADD" );
        addType( UserGroup.class, "userGroup", null, "F_USERGROUP_PUBLIC_ADD", null );
        addType( PatientTabularReport.class, "patientTabularReport", null, "F_PATIENT_TABULAR_REPORT_PUBLIC_ADD", null );
        addType( PatientAggregateReport.class, "patientAggregateReport", null, "F_PATIENT_TABULAR_REPORT_PUBLIC_ADD", null );

        addType( org.hisp.dhis.mapping.Map.class, "map", "F_MAP_EXTERNAL", "F_MAP_PUBLIC_ADD", null );
        addType( Chart.class, "chart", "F_CHART_EXTERNAL", "F_CHART_PUBLIC_ADD", null );
        addType( ReportTable.class, "reportTable", "F_REPORTTABLE_EXTERNAL", "F_REPORTTABLE_PUBLIC_ADD", null );
        addType( Report.class, "report", "F_REPORT_EXTERNAL", "F_REPORT_PUBLIC_ADD", "F_REPORT_PRIVATE_ADD" );
        addType( Document.class, "document", "F_DOCUMENT_EXTERNAL", "F_DOCUMENT_PUBLIC_ADD", "F_DOCUMENT_PRIVATE_ADD" );

        addType( Dashboard.class, "dashboard", null, "F_DASHBOARD_PUBLIC_ADD", null );
        addType( Interpretation.class, "interpretation", null, null, null );
    }

    public static boolean isSupported( String type )
    {
        return SUPPORTED_TYPES.containsKey( type );
    }

    public static boolean isSupported( IdentifiableObject object )
    {
        return isSupported( object.getClass() );
    }

    public static boolean isSupported( Class<?> clazz )
    {
        return SUPPORTED_TYPES.containsValue( clazz );
    }

    public static Class<? extends IdentifiableObject> classForType( String type )
    {
        return SUPPORTED_TYPES.get( type );
    }

    /**
     * Checks if a user can create a public instance of a certain object.
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Does user have the authority to create public instances of that object
     *
     * @param user  User to check against
     * @param clazz Class to check
     * @return Result of test
     */
    public static <T extends IdentifiableObject> boolean canCreatePublic( User user, Class<T> clazz )
    {
        Set<String> authorities = user != null ? user.getUserCredentials().getAllAuthorities() : new HashSet<String>();
        return CollectionUtils.containsAny( authorities, SHARING_OVERRIDE_AUTHORITIES ) || authorities.contains( PUBLIC_AUTHORITIES.get( clazz ) );
    }

    public static <T> boolean defaultPublic( Class<T> clazz )
    {
        return !Dashboard.class.isAssignableFrom( clazz );
    }

    public static boolean canCreatePublic( User user, IdentifiableObject identifiableObject )
    {
        return canCreatePublic( user, identifiableObject.getClass() );
    }

    public static boolean canCreatePublic( User user, String type )
    {
        return canCreatePublic( user, SUPPORTED_TYPES.get( type ) );
    }

    /**
     * Checks if a user can create a private instance of a certain object.
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Does user have the authority to create private instances of that object
     *
     * @param user  User to check against
     * @param clazz Class to check
     * @return Result of test
     */
    public static <T extends IdentifiableObject> boolean canCreatePrivate( User user, Class<T> clazz )
    {
        Set<String> authorities = user != null ? user.getUserCredentials().getAllAuthorities() : new HashSet<String>();
        return CollectionUtils.containsAny( authorities, SHARING_OVERRIDE_AUTHORITIES )
            || PRIVATE_AUTHORITIES.get( clazz ) == null
            || authorities.contains( PRIVATE_AUTHORITIES.get( clazz ) );
    }

    public static boolean canCreatePrivate( User user, IdentifiableObject identifiableObject )
    {
        return canCreatePrivate( user, identifiableObject.getClass() );
    }

    public static boolean canCreatePrivate( User user, String type )
    {
        return canCreatePrivate( user, SUPPORTED_TYPES.get( type ) );
    }

    /**
     * Can user write to this object (create)
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Is the user for the object null?
     * 3. Is the user of the object equal to current user?
     * 4. Is the object public write?
     * 5. Does any of the userGroupAccesses contain public write and the current user is in that group
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canWrite( User user, IdentifiableObject object )
    {
        //TODO ( (object instanceof User) && canCreatePrivate( user, object ) ): review possible security breaches and best way to give update access upon user import
        if ( sharingOverrideAuthority( user )
            || (object.getUser() == null && canCreatePublic( user, object ) && PRIVATE_AUTHORITIES.get( object.getClass() ) != null)
            || (user != null && user.equals( object.getUser() ))
            //|| authorities.contains( PRIVATE_AUTHORITIES.get( object.getClass() ) )
            || ((object instanceof User) && canCreatePrivate( user, object ))
            || AccessStringHelper.canWrite( object.getPublicAccess() ) )
        {
            return true;
        }

        for ( UserGroupAccess userGroupAccess : object.getUserGroupAccesses() )
        {
            if ( AccessStringHelper.canWrite( userGroupAccess.getAccess() )
                && userGroupAccess.getUserGroup().getMembers().contains( user ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Can user read this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Is the user for the object null?
     * 3. Is the user of the object equal to current user?
     * 4. Is the object public read?
     * 5. Does any of the userGroupAccesses contain public read and the current user is in that group
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canRead( User user, IdentifiableObject object )
    {
        if ( sharingOverrideAuthority( user )
            || UserGroup.class.isAssignableFrom( object.getClass() )
            || object.getUser() == null
            || user.equals( object.getUser() )
            || AccessStringHelper.canRead( object.getPublicAccess() ) )
        {
            return true;
        }

        for ( UserGroupAccess userGroupAccess : object.getUserGroupAccesses() )
        {
            if ( AccessStringHelper.canRead( userGroupAccess.getAccess() )
                && userGroupAccess.getUserGroup().getMembers().contains( user ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Can user update this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Can user write to this object?
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canUpdate( User user, IdentifiableObject object )
    {
        return canWrite( user, object );
    }

    /**
     * Can user delete this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Can user write to this object?
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canDelete( User user, IdentifiableObject object )
    {
        return canWrite( user, object );
    }

    /**
     * Can user manage (make public) this object
     * <p/>
     * 1. Does user have SHARING_OVERRIDE_AUTHORITY authority?
     * 2. Can user write to this object?
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static boolean canManage( User user, IdentifiableObject object )
    {
        if ( sharingOverrideAuthority( user )
            || (object.getUser() == null && canCreatePublic( user, object ) && PRIVATE_AUTHORITIES.get( object.getClass() ) != null)
            || user.equals( object.getUser() )
            || AccessStringHelper.canWrite( object.getPublicAccess() ) )
        {
            return true;
        }

        for ( UserGroupAccess userGroupAccess : object.getUserGroupAccesses() )
        {
            if ( AccessStringHelper.canWrite( userGroupAccess.getAccess() )
                && userGroupAccess.getUserGroup().getMembers().contains( user ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Can user make this object external? (read with no login)
     *
     * @param user   User to check against
     * @param object Object to check
     * @return Result of test
     */
    public static <T extends IdentifiableObject> boolean canExternalize( User user, T object )
    {
        if ( user == null )
        {
            return false;
        }

        Set<String> authorities = user.getUserCredentials().getAllAuthorities();

        return EXTERNAL_AUTHORITIES.get( object.getClass() ) != null &&
            (sharingOverrideAuthority( user ) || authorities.contains( EXTERNAL_AUTHORITIES.get( object.getClass() ) ));
    }

    private static boolean sharingOverrideAuthority( User user )
    {
        return user == null || CollectionUtils.containsAny( user.getUserCredentials().getAllAuthorities(), SHARING_OVERRIDE_AUTHORITIES );
    }

    private SharingUtils()
    {
    }
}
