package org.hisp.dhis.patient.action.patientattributegroup;

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
import java.util.List;

import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ SavePatientAttributeGroupSortOrderAction.java Jul 5, 2011 11:07:38 AM $
 * 
 */
public class SavePatientAttributeGroupSortOrderAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private List<Integer> patientAttributeGroupIds = new ArrayList<Integer>();

    public void setPatientAttributeGroupIds( List<Integer> patientAttributeGroupIds )
    {
        this.patientAttributeGroupIds = patientAttributeGroupIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        int sortOrder = 1;

        List<PatientAttributeGroup> groups = new ArrayList<PatientAttributeGroup>( patientAttributeGroupIds.size() );

        for ( Integer patientAttributeGroupId : patientAttributeGroupIds )
        {
            PatientAttributeGroup patientAttributeGroup = patientAttributeGroupService.getPatientAttributeGroup( patientAttributeGroupId );

            groups.add( patientAttributeGroup );

            patientAttributeGroup.setSortOrder( sortOrder++ );

            patientAttributeGroupService.updatePatientAttributeGroup( patientAttributeGroup );
        }
        
        return SUCCESS;
    }

}
