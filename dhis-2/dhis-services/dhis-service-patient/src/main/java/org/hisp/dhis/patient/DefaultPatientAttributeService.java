package org.hisp.dhis.patient;

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
import java.util.HashSet;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultPatientAttributeService
    implements PatientAttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAttributeStore patientAttributeStore;

    public void setPatientAttributeStore( PatientAttributeStore patientAttributeStore )
    {
        this.patientAttributeStore = patientAttributeStore;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public void deletePatientAttribute( PatientAttribute patientAttribute )
    {
        patientAttributeStore.delete( patientAttribute );
    }

    public Collection<PatientAttribute> getAllPatientAttributes()
    {
        return patientAttributeStore.getAll();
    }

    public PatientAttribute getPatientAttribute( int id )
    {
        return patientAttributeStore.get( id );
    }

    public int savePatientAttribute( PatientAttribute patientAttribute )
    {
        return patientAttributeStore.save( patientAttribute );
    }

    public void updatePatientAttribute( PatientAttribute patientAttribute )
    {
        patientAttributeStore.update( patientAttribute );
    }

    public Collection<PatientAttribute> getPatientAttributesByValueType( String valueType )
    {
        return patientAttributeStore.getByValueType( valueType );
    }

    public PatientAttribute getPatientAttributeByName( String name )
    {
        return patientAttributeStore.getByName( name );
    }

    public PatientAttribute getPatientAttributeByGroupBy()
    {
        return patientAttributeStore.getByGroupBy();
    }

    public Collection<PatientAttribute> getOptionalPatientAttributesWithoutGroup()
    {
        return patientAttributeStore.getOptionalPatientAttributesWithoutGroup();
    }

    public Collection<PatientAttribute> getPatientAttributesByMandatory( boolean mandatory )
    {
        return patientAttributeStore.getByMandatory( mandatory );
    }

    public Collection<PatientAttribute> getPatientAttributesWithoutGroup()
    {
        return patientAttributeStore.getWithoutGroup();
    }

    public PatientAttribute getPatientAttribute( String uid )
    {
        return patientAttributeStore.getByUid( uid );
    }

    public Collection<PatientAttribute> getPatientAttributesByDisplayOnVisitSchedule( boolean displayOnVisitSchedule )
    {
        return patientAttributeStore.getByDisplayOnVisitSchedule( displayOnVisitSchedule );
    }

    public Collection<PatientAttribute> getPatientAttributesWithoutProgram()
    {
        Collection<PatientAttribute> result = patientAttributeStore.getAll();

        Collection<Program> programs = programService.getAllPrograms();

        if ( result != null )
        {
            for ( Program program : programs )
            {
                result.removeAll( program.getAttributes() );
            }
            return result;
        }

        return new HashSet<PatientAttribute>();
    }

    public Collection<PatientAttribute> getPatientAttributesDisplayed( boolean displayInListNoProgram )
    {
        return patientAttributeStore.getPatientAttributesDisplayed( displayInListNoProgram );
    }
}
