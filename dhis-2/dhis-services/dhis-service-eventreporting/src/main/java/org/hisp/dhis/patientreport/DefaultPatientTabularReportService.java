package org.hisp.dhis.patientreport;

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

import java.util.Collection;

import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version $DefaultPatientTabularReportService.java May 7, 2012 1:12:31 PM$
 */
@Transactional
public class DefaultPatientTabularReportService
    implements PatientTabularReportService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientTabularReportStore tabularReportStore;

    public void setTabularReportStore( PatientTabularReportStore tabularReportStore )
    {
        this.tabularReportStore = tabularReportStore;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public void deletePatientTabularReport( PatientTabularReport patientTabularReport )
    {
        tabularReportStore.delete( patientTabularReport );
    }

    @Override
    public Collection<PatientTabularReport> getAllTabularReports()
    {
        return tabularReportStore.getAll();
    }

    @Override
    public PatientTabularReport getPatientTabularReport( int id )
    {
        return tabularReportStore.get( id );
    }

    @Override
    public PatientTabularReport getPatientTabularReport( String name )
    {
        return tabularReportStore.getByName( name );
    }

    @Override
    public PatientTabularReport getPatientTabularReportByUid( String uid )
    {
        return tabularReportStore.getByUid( uid );
    }

    @Override
    public Collection<PatientTabularReport> getPatientTabularReports( User user, String query, Integer min, Integer max )
    {
        return tabularReportStore.get( user, query, min, max );
    }

    @Override
    public int saveOrUpdate( PatientTabularReport patientTabularReport )
    {
        return tabularReportStore.save( patientTabularReport );
    }

    @Override
    public int countPatientTabularReportList( User user, String query )
    {
        return tabularReportStore.countList( user, query );
    }

}
