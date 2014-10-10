package org.hisp.dhis.patientreport.hibernate;

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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.SharingUtils;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.patientreport.PatientTabularReport;
import org.hisp.dhis.patientreport.PatientTabularReportStore;
import org.hisp.dhis.user.User;

/**
 * @author Chau Thu Tran
 * 
 * @version $HibernatePatientTabularReportStore.java May 7, 2012 1:10:37 PM$
 */
public class HibernatePatientTabularReportStore
    extends HibernateIdentifiableObjectStore<PatientTabularReport>
    implements PatientTabularReportStore
{

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<PatientTabularReport> get( User user, String query, Integer min, Integer max )
    {
        Criteria criteria = search( query );

        List<PatientTabularReport> result = new ArrayList<PatientTabularReport>();
        Collection<PatientTabularReport> reports = criteria.list();

        for ( PatientTabularReport report : reports )
        {
            if ( SharingUtils.canRead( user, report ) )
            {
                result.add( report );
            }
        }
        if ( min != null && max != null )
        {

            if ( min > result.size() )
            {
                min = result.size();
            }

            if ( max > result.size() )
            {
                max = result.size();
            }
            return result.subList( min, max );
        }
        return result;
    }

    @Override
    public int countList( User user, String query )
    {
        Criteria criteria = search( query );

        int count = 0;
        @SuppressWarnings( "unchecked" )
        Collection<PatientTabularReport> reports = criteria.list();

        for ( PatientTabularReport report : reports )
        {
            if ( SharingUtils.canRead( user, report ) )
            {
                count++;
            }
        }

        return count;
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private Criteria search( String query )
    {
        Criteria criteria = getCriteria();

        if ( query != null )
        {
            criteria.add( Restrictions.ilike( "name", "%" + query + "%" ) );
        }

        return criteria;
    }

}
