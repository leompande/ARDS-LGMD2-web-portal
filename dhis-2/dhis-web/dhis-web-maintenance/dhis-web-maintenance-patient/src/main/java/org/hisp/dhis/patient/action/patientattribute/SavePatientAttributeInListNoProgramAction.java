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

import java.util.Collection;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class SavePatientAttributeInListNoProgramAction
    implements Action
{
    private final String PREFIX_IDENTYFITER_TYPE = "iden";

    private final String PREFIX_ATTRIBUTE = "attr";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    @Autowired
    private PatientIdentifierTypeService patientIdentifierTypeService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String[] selectedAttributeIds;

    public void setSelectedAttributeIds( String[] selectedAttributeIds )
    {
        this.selectedAttributeIds = selectedAttributeIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Collection<PatientIdentifierType> patientIdentifierTypes = patientIdentifierTypeService
            .getAllPatientIdentifierTypes();
        Collection<PatientAttribute> patientAttributes = patientAttributeService.getAllPatientAttributes();

        int indexIden = 1;
        int indexAttr = 1;
        for ( String objectId : selectedAttributeIds )
        {
            // Identifier type
            String[] id = objectId.split( "_" );
            if ( id[0].equals( PREFIX_IDENTYFITER_TYPE ) )
            {

                PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( Integer
                    .parseInt( id[1] ) );
                identifierType.setDisplayInListNoProgram( true );
                identifierType.setSortOrderInListNoProgram( indexIden );
                patientIdentifierTypeService.updatePatientIdentifierType( identifierType );
                indexIden++;
                patientIdentifierTypes.remove( identifierType );
            }

            // Attribute
            else if ( id[0].equals( PREFIX_ATTRIBUTE ) )
            {
                PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( Integer
                    .parseInt( id[1] ) );
                patientAttribute.setDisplayInListNoProgram( true );
                patientAttribute.setSortOrderInListNoProgram( indexAttr );
                patientAttributeService.updatePatientAttribute( patientAttribute );
                indexAttr++;
                patientAttributes.remove( patientAttribute );
            }
        }

        // Set DisplayInListNoProgram=false for other ID type
        for ( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
        {
            patientIdentifierType.setDisplayInListNoProgram( false );
            patientIdentifierType.setSortOrderInListNoProgram( 0 );
            patientIdentifierTypeService.updatePatientIdentifierType( patientIdentifierType );
        }

        // Set DisplayInListNoProgram=false for other attribute type
        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttribute.setDisplayInListNoProgram( false );
            patientAttribute.setSortOrderInListNoProgram( 0 );
            patientAttributeService.updatePatientAttribute( patientAttribute );
        }

        return SUCCESS;
    }
}
