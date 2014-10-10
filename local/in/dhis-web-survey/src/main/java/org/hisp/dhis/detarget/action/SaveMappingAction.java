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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.target.DeTargetMapping;
import org.hisp.dhis.target.DeTargetMappingService;

import com.opensymphony.xwork2.Action;

public class SaveMappingAction
    implements Action
{
    @SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog( SaveMappingAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DeTargetMappingService deTargetMappingService;

    public void setDeTargetMappingService( DeTargetMappingService deTargetMappingService )
    {
        this.deTargetMappingService = deTargetMappingService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private int dataelementId;

    public int getDataelementId()
    {
        return dataelementId;
    }

    private String deID;

    public void setDeID( String deID )
    {
        this.deID = deID;
    }

    private String detargetID;

    public void setDetargetID( String detargetID )
    {
        this.detargetID = detargetID;
    }

    public void setDataelementId( int dataelementId )
    {
        this.dataelementId = dataelementId;
    }

    private int detargetId;

    public int getDetargetId()
    {
        return detargetId;
    }

    public void setDetargetId( int detargetId )
    {
        this.detargetId = detargetId;
    }

    private int dataelementoptioncombo;

    public int getDataelementoptioncombo()
    {
        return dataelementoptioncombo;
    }

    public void setDataelementoptioncombo( int dataelementoptioncombo )
    {
        this.dataelementoptioncombo = dataelementoptioncombo;
    }

    private int targetoptioncombo;

    public int getTargetoptioncombo()
    {
        return targetoptioncombo;
    }

    public void setTargetoptioncombo( int targetoptioncombo )
    {
        this.targetoptioncombo = targetoptioncombo;
    }

    private String dename;

    public String getDename()
    {
        return dename;
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        String[] de_option = deID.split( ":" );
        int dataelementId = Integer.parseInt( de_option[0] );
        int dataelementoptioncombo = Integer.parseInt( de_option[1] );

        String[] de_option1 = detargetID.split( ":" );
        int detargetId = Integer.parseInt( de_option1[0] );
        int targetoptioncombo = Integer.parseInt( de_option1[1] );

        DataElement dataElement = dataElementService.getDataElement( dataelementId );

        DataElement target = dataElementService.getDataElement( detargetId );

        DataElementCategoryOptionCombo deoptioncombo = dataElementCategoryService
            .getDataElementCategoryOptionCombo( dataelementoptioncombo );

        DataElementCategoryOptionCombo detargetoptioncombo = dataElementCategoryService
            .getDataElementCategoryOptionCombo( targetoptioncombo );

        DeTargetMapping deTargetMapping = deTargetMappingService.getDeTargetMapping( dataElement, deoptioncombo );

        if ( deTargetMapping == null )
        {
            deTargetMapping = new DeTargetMapping( dataElement, deoptioncombo, target, detargetoptioncombo );

            deTargetMappingService.addDeTargetMapping( deTargetMapping );
        }
        else
        {
            deTargetMapping.setTargetDataelement( target );
            deTargetMapping.setTargetoptioncombo( detargetoptioncombo );
            deTargetMappingService.updateDeTargetMapping( deTargetMapping );
        }
        dename = dataElement.getName() + ":" + deoptioncombo.getName();

        targetname = deTargetMapping.getTargetDataelement().getName();
        targetid = "" + deTargetMapping.getTargetDataelement().getId() + ":"
        + deTargetMapping.getTargetoptioncombo().getId();

        // dataElementCategoryOptionComboService.getDefaultDataElementCategoryOptionCombo();
        return SUCCESS;
    }
}
