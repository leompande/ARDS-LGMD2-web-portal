package org.hisp.dhis.patientattributevalue;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultPatientAttributeValueService
    implements PatientAttributeValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAttributeValueStore patientAttributeValueStore;

    public void setPatientAttributeValueStore( PatientAttributeValueStore patientAttributeValueStore )
    {
        this.patientAttributeValueStore = patientAttributeValueStore;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public void deletePatientAttributeValue( PatientAttributeValue patientAttributeValue )
    {
        patientAttributeValueStore.delete( patientAttributeValue );
    }

    public int deletePatientAttributeValue( Patient patient )
    {
        return patientAttributeValueStore.deleteByPatient( patient );
    }

    public int deletePatientAttributeValue( PatientAttribute patientAttribute )
    {
        return patientAttributeValueStore.deleteByAttribute( patientAttribute );
    }

    public Collection<PatientAttributeValue> getAllPatientAttributeValues()
    {
        return patientAttributeValueStore.getAll();
    }

    public PatientAttributeValue getPatientAttributeValue( Patient patient, PatientAttribute patientAttribute )
    {
        return patientAttributeValueStore.get( patient, patientAttribute );
    }

    public Collection<PatientAttributeValue> getPatientAttributeValues( Patient patient )
    {
        return patientAttributeValueStore.get( patient );
    }

    public Collection<PatientAttributeValue> getPatientAttributeValues( PatientAttribute patientAttribute )
    {
        return patientAttributeValueStore.get( patientAttribute );
    }

    public Collection<PatientAttributeValue> getPatientAttributeValues( Collection<Patient> patients )
    {
        if ( patients != null && patients.size() > 0 )
            return patientAttributeValueStore.get( patients );
        return null;
    }

    public void savePatientAttributeValue( PatientAttributeValue patientAttributeValue )
    {
        if ( patientAttributeValue.getValue() != null )
        {
            patientAttributeValueStore.saveVoid( patientAttributeValue );
        }
    }

    public void updatePatientAttributeValue( PatientAttributeValue patientAttributeValue )
    {
        if ( patientAttributeValue.getValue() == null )
        {
            patientAttributeValueStore.delete( patientAttributeValue );
        }
        else
        {
            patientAttributeValueStore.update( patientAttributeValue );
        }
    }

    public Map<Integer, Collection<PatientAttributeValue>> getPatientAttributeValueMapForPatients(
        Collection<Patient> patients )
    {
        Map<Integer, Set<PatientAttributeValue>> attributeValueMap = new HashMap<Integer, Set<PatientAttributeValue>>();

        Collection<PatientAttributeValue> patientAttributeValues = getPatientAttributeValues( patients );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( attributeValueMap.containsKey( patientAttributeValue.getPatient().getId() ) )
            {
                attributeValueMap.get( patientAttributeValue.getPatient().getId() ).add( patientAttributeValue );
            }
            else
            {
                Set<PatientAttributeValue> attributeValues = new HashSet<PatientAttributeValue>();
                attributeValues.add( patientAttributeValue );
                attributeValueMap.put( patientAttributeValue.getPatient().getId(), attributeValues );
            }
        }

        Map<Integer, Collection<PatientAttributeValue>> patentAttributeValueMap = new HashMap<Integer, Collection<PatientAttributeValue>>();

        for ( Entry<Integer, Set<PatientAttributeValue>> entry : attributeValueMap.entrySet() )
        {
            SortedMap<String, PatientAttributeValue> sortedByAttribute = new TreeMap<String, PatientAttributeValue>();

            for ( PatientAttributeValue patientAttributeValue : entry.getValue() )
            {
                sortedByAttribute.put( patientAttributeValue.getPatientAttribute().getName(), patientAttributeValue );
            }

            patentAttributeValueMap.put( entry.getKey(), sortedByAttribute.values() );

        }

        return patentAttributeValueMap;
    }

    public Map<Integer, PatientAttributeValue> getPatientAttributeValueMapForPatients( Collection<Patient> patients,
        PatientAttribute patientAttribute )
    {
        Map<Integer, PatientAttributeValue> attributeValueMap = new HashMap<Integer, PatientAttributeValue>();

        Collection<PatientAttributeValue> patientAttributeValues = getPatientAttributeValues( patients );

        if ( patientAttributeValues != null )
        {
            for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
            {
                if ( patientAttributeValue.getPatientAttribute() == patientAttribute )
                {
                    attributeValueMap.put( patientAttributeValue.getPatient().getId(), patientAttributeValue );
                }
            }
        }

        return attributeValueMap;
    }

    public Collection<PatientAttributeValue> searchPatientAttributeValue( PatientAttribute patientAttribute,
        String searchText )
    {
        return patientAttributeValueStore.searchByValue( patientAttribute, searchText );
    }

    public void copyPatientAttributeValues( Patient source, Patient destination )
    {
        deletePatientAttributeValue( destination );

        for ( PatientAttributeValue patientAttributeValue : getPatientAttributeValues( source ) )
        {
            PatientAttributeValue attributeValue = new PatientAttributeValue(
                patientAttributeValue.getPatientAttribute(), destination, patientAttributeValue.getValue() );

            savePatientAttributeValue( attributeValue );
        }
    }

    public int countByPatientAttributeOption( PatientAttributeOption attributeOption )
    {
        return patientAttributeValueStore.countByPatientAttributeOption( attributeOption );
    }

    public Collection<Patient> getPatient( PatientAttribute attribute, String value )
    {
        return patientAttributeValueStore.getPatient( attribute, value );
    }

    public void updatePatientAttributeValues( PatientAttributeOption patientAttributeOption )
    {
        patientAttributeValueStore.updatePatientAttributeValues( patientAttributeOption );
    }

}
