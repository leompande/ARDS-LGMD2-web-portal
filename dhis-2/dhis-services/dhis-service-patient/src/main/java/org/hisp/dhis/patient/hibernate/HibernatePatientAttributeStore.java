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

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeStore;

import java.util.Collection;

/**
 * @author Abyot Asalefew Gizaw
 */
public class HibernatePatientAttributeStore
    extends HibernateIdentifiableObjectStore<PatientAttribute>
    implements PatientAttributeStore
{
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getByValueType( String valueType )
    {
        return getCriteria( Restrictions.eq( "valueType", valueType ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getByMandatory( boolean mandatory )
    {
        return getCriteria( Restrictions.eq( "mandatory", mandatory ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getOptionalPatientAttributesWithoutGroup()
    {
        return getCriteria( Restrictions.isNull( "patientAttributeGroup" ) )
            .add( Restrictions.eq( "mandatory", false ) ).list();
    }

    public PatientAttribute getByGroupBy()
    {
        return (PatientAttribute) getCriteria( Restrictions.eq( "groupBy", true ) ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getWithoutGroup()
    {
        return getCriteria( Restrictions.isNull( "patientAttributeGroup" ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getByDisplayOnVisitSchedule( boolean displayOnVisitSchedule )
    {
        return getCriteria( Restrictions.eq( "displayOnVisitSchedule", displayOnVisitSchedule ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getPatientAttributesDisplayed( boolean displayInListNoProgram )
    {
        return getCriteria( Restrictions.eq( "displayInListNoProgram", displayInListNoProgram ) ).list();
    }

}
