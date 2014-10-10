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

import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author Chau Thu Tran
 * @version DefaultPatientAggregateReportService.java 12:22:45 PM Jan 10, 2013 $
 */
@Transactional
public class DefaultPatientAggregateReportService
    implements PatientAggregateReportService
{

    private PatientAggregateReportStore patientAggregateReportStore;

    public void setPatientAggregateReportStore( PatientAggregateReportStore patientAggregateReportStore )
    {
        this.patientAggregateReportStore = patientAggregateReportStore;
    }

    @Override
    public int addPatientAggregateReport( PatientAggregateReport patientAggregateReport )
    {
        return patientAggregateReportStore.save( patientAggregateReport );
    }

    @Override
    public void updatePatientAggregateReport( PatientAggregateReport patientAggregateReport )
    {
        patientAggregateReportStore.update( patientAggregateReport );
    }

    @Override
    public PatientAggregateReport getPatientAggregateReport( int id )
    {
        return patientAggregateReportStore.get( id );
    }
    
    @Override
    public PatientAggregateReport getPatientAggregateReportByUid( String uid )
    {
        return patientAggregateReportStore.getByUid( uid );
    }

    @Override
    public void deletePatientAggregateReport( PatientAggregateReport patientAggregateReport )
    {
        patientAggregateReportStore.delete( patientAggregateReport );
    }

    @Override
    public Collection<PatientAggregateReport> getAllPatientAggregateReports()
    {
        return patientAggregateReportStore.getAll();
    }

    @Override
    public Collection<PatientAggregateReport> getPatientAggregateReports( User user, String query, Integer min,
        Integer max )
    {
        return patientAggregateReportStore.get( user, query, min, max );
    }

    @Override
    public int countPatientAggregateReportList( User user, String query )
    {
        return patientAggregateReportStore.countList( user, query );
    }

    @Override
    public PatientAggregateReport getPatientAggregateReport( String name )
    {
        return patientAggregateReportStore.getByName( name );
    }
}
