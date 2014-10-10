package org.hisp.dhis.patientdatavalue;

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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.program.ProgramStageInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientDataValueService
    implements PatientDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientDataValueStore patientDataValueStore;

    public void setPatientDataValueStore( PatientDataValueStore patientDataValueStore )
    {
        this.patientDataValueStore = patientDataValueStore;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public void savePatientDataValue( PatientDataValue patientDataValue )
    {
        if ( patientDataValue.getValue() != null )
        {
            patientDataValueStore.saveVoid( patientDataValue );
        }
    }

    public void deletePatientDataValue( PatientDataValue patientDataValue )
    {
        patientDataValueStore.delete( patientDataValue );
    }

    public int deletePatientDataValue( ProgramStageInstance programStageInstance )
    {
        return patientDataValueStore.delete( programStageInstance );
    }
    
    public void updatePatientDataValue( PatientDataValue patientDataValue )
    {
        if ( patientDataValue.getValue() == null )
        {
            patientDataValueStore.delete( patientDataValue );
        }
        else
        {
            patientDataValueStore.update( patientDataValue );
        }
    }

    public Collection<PatientDataValue> getAllPatientDataValues()
    {
        return patientDataValueStore.getAll();
    }

    public Collection<PatientDataValue> getPatientDataValues( ProgramStageInstance programStageInstance )
    {
        return patientDataValueStore.get( programStageInstance );
    }

    public Collection<PatientDataValue> getPatientDataValues( ProgramStageInstance programStageInstance,
        Collection<DataElement> dataElements )
    {
        return patientDataValueStore.get( programStageInstance, dataElements );
    }

    public Collection<PatientDataValue> getPatientDataValues( Collection<ProgramStageInstance> programStageInstances )
    {
        return patientDataValueStore.get( programStageInstances );
    }

    public Collection<PatientDataValue> getPatientDataValues( DataElement dataElement )
    {
        return patientDataValueStore.get( dataElement );
    }

    public Collection<PatientDataValue> getPatientDataValues( Patient patient, Collection<DataElement> dataElements,
        Date startDate, Date endDate )
    {
        return patientDataValueStore.get( patient, dataElements, startDate, endDate );
    }

    public PatientDataValue getPatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement )
    {
        return patientDataValueStore.get( programStageInstance, dataElement );
    }
}
