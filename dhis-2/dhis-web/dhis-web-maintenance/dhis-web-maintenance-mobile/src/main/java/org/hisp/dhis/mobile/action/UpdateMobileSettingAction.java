package org.hisp.dhis.mobile.action;

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
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.api.mobile.PatientMobileSettingService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientMobileSetting;

import com.opensymphony.xwork2.Action;

public class UpdateMobileSettingAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAttributeService patientAttributeService;

    public PatientAttributeService getPatientAttributeService()
    {
        return patientAttributeService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientMobileSettingService patientMobileSettingService;

    public PatientMobileSettingService getPatientMobileSettingService()
    {
        return patientMobileSettingService;
    }

    public void setPatientMobileSettingService( PatientMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    private Integer groupingAttributeId;

    public Integer getGroupingAttributeId()
    {
        return groupingAttributeId;
    }

    public void setGroupingAttributeId( Integer groupingAttributeId )
    {
        this.groupingAttributeId = groupingAttributeId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        if ( selectedList.size() > 0 )
        {
            PatientMobileSetting setting;
            if ( patientMobileSettingService.getCurrentSetting().size() > 0 )
            {
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.updatePatientMobileSetting( setting );
            }
            else
            {
                setting = new PatientMobileSetting();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.savePatientMobileSetting( setting );
            }
        }
        else
        {
            PatientMobileSetting setting;
            if ( patientMobileSettingService.getCurrentSetting().size() > 0 )
            {
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.updatePatientMobileSetting( setting );
            }
            else
            {
                setting = new PatientMobileSetting();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.savePatientMobileSetting( setting );
            }
        }

        Collection<PatientAttribute> allPatientAttributes = patientAttributeService.getAllPatientAttributes();

        for ( PatientAttribute patientAttribute : allPatientAttributes )
        {
            patientAttribute.setGroupBy( false );
            if ( patientAttribute.getId() == groupingAttributeId )
            {
                patientAttribute.setGroupBy( true );
            }
            patientAttributeService.updatePatientAttribute( patientAttribute );
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void fillValues( List<PatientAttribute> attributes )
    {
        for ( String id : selectedList )
        {
            attributes.add( patientAttributeService.getPatientAttribute( Integer.parseInt( id ) ) );
        }
    }
}
