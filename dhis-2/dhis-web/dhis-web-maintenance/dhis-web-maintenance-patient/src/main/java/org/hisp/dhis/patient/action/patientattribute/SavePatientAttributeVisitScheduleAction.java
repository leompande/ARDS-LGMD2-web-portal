package org.hisp.dhis.patient.action.patientattribute;

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

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ SavePatientAttributeVisitScheduleAction.java May 24, 2013 12:31:55
 *          PM $
 */
public class SavePatientAttributeVisitScheduleAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer[] selectedAttributeIds;

    public void setSelectedAttributeIds( Integer[] selectedAttributeIds )
    {
        this.selectedAttributeIds = selectedAttributeIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Collection<PatientAttribute> patientAttributes = patientAttributeService.getAllPatientAttributes();
        
        int index = 1;
        for ( Integer attributeId : selectedAttributeIds )
        {
            PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( attributeId );
            patientAttribute.setDisplayOnVisitSchedule( true );
            patientAttribute.setSortOrderInVisitSchedule( index );
            patientAttributeService.updatePatientAttribute( patientAttribute );
            index ++;
            patientAttributes.remove( patientAttribute );
        }
        
        // Set visitSchedule=false for other patientAttributes 
        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttribute.setDisplayOnVisitSchedule( false );
            patientAttribute.setSortOrderInVisitSchedule( 0 );
            patientAttributeService.updatePatientAttribute( patientAttribute ); 
        }
        
        return SUCCESS;
    }
}
