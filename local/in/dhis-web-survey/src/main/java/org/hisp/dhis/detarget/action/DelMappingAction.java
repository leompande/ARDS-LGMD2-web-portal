package org.hisp.dhis.detarget.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.target.DeTargetMapping;
import org.hisp.dhis.target.DeTargetMappingService;

import com.opensymphony.xwork2.Action;

public class DelMappingAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DeTargetMappingService deTargetMappingService;

    public void setDeTargetMappingService( DeTargetMappingService deTargetMappingService )
    {
        this.deTargetMappingService = deTargetMappingService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String deID;

    public void setDeID( String deID )
    {
        this.deID = deID;
    }

    @SuppressWarnings( "unused" )
    private String detargetID;

    public void setDetargetID( String detargetID )
    {
        this.detargetID = detargetID;
    }

    private String targetname;

    public String getTargetname()
    {
        return targetname;
    }

    private String targetid;

    public String getTargetid()
    {
        return targetid;
    }

    private String dename;

    public String getDename()
    {
        return dename;
    }

    public String execute()
        throws Exception
    {
        String[] de_option = deID.split( ":" );
        int dataelementId = Integer.parseInt( de_option[0] );
        int dataelementoptioncombo = Integer.parseInt( de_option[1] );

        DataElement dataElement = dataElementService.getDataElement( dataelementId );

        DataElementCategoryOptionCombo deoptioncombo = dataElementCategoryService
            .getDataElementCategoryOptionCombo( dataelementoptioncombo );

        DeTargetMapping deTargetMapping = deTargetMappingService.getDeTargetMapping( dataElement, deoptioncombo );

        if( deTargetMapping != null )
        {
            deTargetMappingService.deleteDeTargetMapping( deTargetMapping );
        }
        
        dename = dataElement.getName() + ":" + deoptioncombo.getName();

        targetname = "None";
        targetid = "-1";

        return SUCCESS;
    }
}
