package org.hisp.dhis.patientdatavalue.hibernate;

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
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueStore;
import org.hisp.dhis.program.ProgramStageInstance;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernatePatientDataValueStore
    extends HibernateGenericStore<PatientDataValue>
    implements PatientDataValueStore
{
    public void saveVoid( PatientDataValue patientDataValue )
    {
        sessionFactory.getCurrentSession().save( patientDataValue );
    }

    public int delete( ProgramStageInstance programStageInstance )
    {
        Query query = getQuery( "delete from PatientDataValue where programStageInstance = :programStageInstance" );
        query.setEntity( "programStageInstance", programStageInstance );
        return query.executeUpdate();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramStageInstance programStageInstance )
    {
        return getCriteria( Restrictions.eq( "programStageInstance", programStageInstance ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramStageInstance programStageInstance,
        Collection<DataElement> dataElements )
    {
        String hql = "from PatientDataValue pdv where pdv.dataElement in ( :dataElements ) "
            + "and pdv.programStageInstance = :programStageInstance";

        return getQuery( hql ).setParameterList( "dataElements", dataElements )
            .setEntity( "programStageInstance", programStageInstance ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( Collection<ProgramStageInstance> programStageInstances )
    {
        if ( programStageInstances == null || programStageInstances.isEmpty() )
        {
            return new ArrayList<PatientDataValue>();
        }

        return getCriteria( Restrictions.in( "programStageInstance", programStageInstances ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "dataElement", dataElement ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( Patient patient, Collection<DataElement> dataElements, Date startDate,
        Date endDate )
    {
        String hql = "from PatientDataValue pdv where pdv.dataElement in ( :dataElements ) "
            + "and pdv.programStageInstance.programInstance.patient = :patient "
            + "and pdv.programStageInstance.executionDate >= :startDate and pdv.programStageInstance.executionDate <= :endDate ";

        return getQuery( hql ).setParameterList( "dataElements", dataElements ).setEntity( "patient", patient )
            .setDate( "startDate", startDate ).setDate( "endDate", endDate ).list();
    }

    public PatientDataValue get( ProgramStageInstance programStageInstance, DataElement dataElement )
    {
        return (PatientDataValue) getCriteria( Restrictions.eq( "programStageInstance", programStageInstance ),
            Restrictions.eq( "dataElement", dataElement ) ).uniqueResult();
    }
}
