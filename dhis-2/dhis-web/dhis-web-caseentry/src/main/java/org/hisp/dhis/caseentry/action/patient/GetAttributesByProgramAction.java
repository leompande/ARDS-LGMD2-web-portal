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

package org.hisp.dhis.caseentry.action.patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $ GetAttributesByProgramAction.java Jan 8, 2014 4:16:30 PM $
 */
public class GetAttributesByProgramAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    @Autowired
    private ProgramService programService;

    @Autowired
    private PatientAttributeService patientAttributeService;

    @Autowired
    private PatientAttributeValueService attributeValueService;

    @Autowired
    private PatientService patientService;

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private List<PatientIdentifierType> patientIdentifierTypes;

    public List<PatientIdentifierType> getPatientIdentifierTypes()
    {
        return patientIdentifierTypes;
    }

    private List<PatientAttribute> patientAttributes;

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    private Map<Integer, String> attributeValueMaps = new HashMap<Integer, String>();

    public Map<Integer, String> getAttributeValueMaps()
    {
        return attributeValueMaps;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( id != null )
        {
            Program program = programService.getProgram( id );
            patientAttributes = program.getAttributes();
        }
        else
        {
            patientAttributes = new ArrayList<PatientAttribute>(
                patientAttributeService.getPatientAttributesDisplayed( true ) );
        }

        if ( patientId != null )
        {
            Patient patient = patientService.getPatient( patientId );

            for ( PatientAttributeValue attributeValue : attributeValueService.getPatientAttributeValues( patient ) )
            {
                attributeValueMaps.put( attributeValue.getPatientAttribute().getId(), attributeValue.getValue() );
            }
        }

        return SUCCESS;
    }
}
