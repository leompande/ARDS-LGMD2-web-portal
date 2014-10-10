package org.hisp.dhis.caseentry.action.patient;

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
import java.util.Iterator;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ SearchRelationshipAction.java May 13, 2011 2:38:12 PM $
 * 
 */
public class SearchRelationshipPatientAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private RelationshipService relationshipService;

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private String searchText;

    public void setSearchText( String searchText )
    {
        this.searchText = searchText;
    }

    public String getSearchText()
    {
        return searchText;
    }

    private Integer searchingAttributeId;

    public Integer getSearchingAttributeId()
    {
        return searchingAttributeId;
    }

    public void setSearchingAttributeId( Integer searchingAttributeId )
    {
        this.searchingAttributeId = searchingAttributeId;
    }

    private Collection<Patient> patients = new ArrayList<Patient>();

    public Collection<Patient> getPatients()
    {
        return patients;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( searchText != null && searchText.length() > 0 )
            searchText = searchText.trim();

        int index = searchText.indexOf( ' ' );

        if ( index != -1 && index == searchText.lastIndexOf( ' ' ) )
        {
            String[] keys = searchText.split( " " );
            searchText = keys[0] + "  " + keys[1];
        }

        if ( searchText != null && !searchText.isEmpty() )
        {
            if ( searchingAttributeId != null )
            {
                PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( searchingAttributeId );

                Collection<PatientAttributeValue> matching = patientAttributeValueService.searchPatientAttributeValue(
                    patientAttribute, searchText );

                for ( PatientAttributeValue patientAttributeValue : matching )
                {
                    patients.add( patientAttributeValue.getPatient() );
                }

            }
        }
        if ( patients != null && !patients.isEmpty() )
        {
            Patient patient = patientService.getPatient( patientId );

            patients.remove( patient );

            Collection<Relationship> relationships = relationshipService.getRelationshipsForPatient( patient );

            if ( relationships != null )
            {
                Iterator<Relationship> iter = relationships.iterator();

                while ( iter.hasNext() )
                {
                    Relationship relationship = iter.next();
                    patients.remove( relationship.getPatientA() );
                    patients.remove( relationship.getPatientB() );
                }
            }
        }

        return SUCCESS;
    }

}
