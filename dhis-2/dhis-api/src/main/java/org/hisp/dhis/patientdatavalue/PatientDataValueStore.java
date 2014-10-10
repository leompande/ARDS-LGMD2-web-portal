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

import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.program.ProgramStageInstance;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public interface PatientDataValueStore
    extends GenericStore<PatientDataValue>
{
    String ID = PatientDataValueStore.class.getName();

    /**
     * Adds an {@link PatientDataValue}
     * 
     * @param patientAttribute The to PatientDataValue add.
     * 
     * @return A generated unique id of the added {@link PatientDataValue}.
     */
    void saveVoid( PatientDataValue patientDataValue );

    /**
     * Deletes a {@link PatientDataValue}.
     * 
     * @param programStageInstance ProgramStageInstance.
     */
    int delete( ProgramStageInstance programStageInstance );

    /**
     * Retrieve patient data values of a event
     * 
     * @param programStageInstance ProgramStageInstance
     * 
     * @return PatientDataValue list
     */
    Collection<PatientDataValue> get( ProgramStageInstance programStageInstance );

    /**
     * Retrieve patient data values of a event with data elements specified
     * 
     * @param programStageInstance ProgramStageInstance
     * @param dataElement DataElement List
     * 
     * @return PatientDataValue list
     */
    Collection<PatientDataValue> get( ProgramStageInstance programStageInstance, Collection<DataElement> dataElements );

    /**
     * Retrieve patient data values of many events
     * 
     * @param programStageInstance ProgramStageInstance
     * 
     * @return PatientDataValue list
     */
    Collection<PatientDataValue> get( Collection<ProgramStageInstance> programStageInstances );

    /**
     * Retrieve patient data values on a data element
     * 
     * @param dataElement {@link DataElement}
     * 
     * @return PatientDataValue list
     */
    Collection<PatientDataValue> get( DataElement dataElement );

    /**
     * Retrieve patient data values of a {@link Patient} on a
     * {@link DataElement} list.
     * 
     * @param patient Patient
     * @param dataElements The data element list
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * 
     * @return PatientDataValue list
     */
    Collection<PatientDataValue> get( Patient patient, Collection<DataElement> dataElements, Date after, Date before );

    /**
     * Retrieve a patient data value on an event and a data element
     * 
     * @param programStageInstance ProgramStageInstance
     * @param dataElement DataElement
     * 
     * @return PatientDataValue
     */
    PatientDataValue get( ProgramStageInstance programStageInstance, DataElement dataElement );
}