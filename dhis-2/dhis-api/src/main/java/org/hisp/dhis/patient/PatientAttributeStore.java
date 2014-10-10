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

import org.hisp.dhis.common.GenericNameableObjectStore;

import java.util.Collection;

/**
 * @author Abyot Asalefew Gizaw
 */
public interface PatientAttributeStore
    extends GenericNameableObjectStore<PatientAttribute>
{
    String ID = PatientAttributeStore.class.getName();

    /**
     * Get patient attributes by value type
     * 
     * @param valueType Value type
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getByValueType( String valueType );

    /**
     * Get mandatory patient attributes without groups
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getOptionalPatientAttributesWithoutGroup();

    /**
     * Get patient attributes by mandatory option
     * 
     * @param mandatory True/False value
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getByMandatory( boolean mandatory );

    /**
     * Get {@link PatientAttribute} without any group
     * 
     * @return PatientAttribute without group.
     */
    Collection<PatientAttribute> getWithoutGroup();

    /**
     * Get patient attributes by groupBy option
     * 
     * @return PatientAttribute with groupby as true
     */
    PatientAttribute getByGroupBy();

    /**
     * Get patient attributes which are displayed in visit schedule
     * @param displayOnVisitSchedule True/False value
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getByDisplayOnVisitSchedule( boolean displayOnVisitSchedule );

    /**
     * Get patient attributes which are displayed in visit schedule
     * @param displayInListNoProgram True/False value
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getPatientAttributesDisplayed( boolean displayInListNoProgram );
}
