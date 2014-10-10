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

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAudit;
import org.hisp.dhis.patient.PatientAuditStore;

/**
 * @author Chau Thu Tran
 * 
 * @version HibernatePatientAuditStore.java 9:12:20 AM Sep 26, 2012 $
 */
public class HibernatePatientAuditStore
    extends HibernateGenericStore<PatientAudit>
    implements PatientAuditStore
{
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<PatientAudit> get( Patient patient )
    {
        return getCriteria( Restrictions.eq( "patient", patient ) ).addOrder( Order.desc( "date" ) ).list();
    }

    @Override
    public PatientAudit get( Integer patientId, String visitor, Date date, String accessedModule )
    {
        return (PatientAudit) getCriteria( Restrictions.eq( "patient.id", patientId ),
            Restrictions.eq( "visitor", visitor ), Restrictions.eq( "date", date ),
            Restrictions.eq( "accessedModule", accessedModule ) ).uniqueResult();
    }
}
