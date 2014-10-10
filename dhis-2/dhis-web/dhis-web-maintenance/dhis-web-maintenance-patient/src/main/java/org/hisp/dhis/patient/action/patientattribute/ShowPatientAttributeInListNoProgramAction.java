/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

package org.hisp.dhis.patient.action.patientattribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ShowPatientAttributeInListNoProgramAction.java Jan 8, 2014 8:33:46
 *          PM $
 */
public class ShowPatientAttributeInListNoProgramAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    @Autowired
    private PatientIdentifierTypeService patientIdentifierTypeService;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<PatientAttribute> availablePatientAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getAvailablePatientAttributes()
    {
        return availablePatientAttributes;
    }

    private List<PatientAttribute> selectedPatientAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getSelectedPatientAttributes()
    {
        return selectedPatientAttributes;
    }

    private List<PatientIdentifierType> availablePatientIdentifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getAvailablePatientIdentifierTypes()
    {
        return availablePatientIdentifierTypes;
    }

    private List<PatientIdentifierType> selectedPatientIdentifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getSelectedPatientIdentifierTypes()
    {
        return selectedPatientIdentifierTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        availablePatientAttributes = new ArrayList<PatientAttribute>(
            patientAttributeService.getPatientAttributesDisplayed( false ) );
        Collections.sort( availablePatientAttributes, IdentifiableObjectNameComparator.INSTANCE );

        selectedPatientAttributes = new ArrayList<PatientAttribute>(
            patientAttributeService.getPatientAttributesDisplayed( true ) );
        Collections.sort( availablePatientAttributes, IdentifiableObjectNameComparator.INSTANCE );
        
        availablePatientIdentifierTypes = new ArrayList<PatientIdentifierType>(
            patientIdentifierTypeService.getPatientIdentifierTypeDisplayed( false ) );
        Collections.sort( availablePatientAttributes, IdentifiableObjectNameComparator.INSTANCE );

        selectedPatientIdentifierTypes = new ArrayList<PatientIdentifierType>(
            patientIdentifierTypeService.getPatientIdentifierTypeDisplayed( true ) );
        Collections.sort( availablePatientAttributes, IdentifiableObjectNameComparator.INSTANCE );
        return SUCCESS;
    }
}