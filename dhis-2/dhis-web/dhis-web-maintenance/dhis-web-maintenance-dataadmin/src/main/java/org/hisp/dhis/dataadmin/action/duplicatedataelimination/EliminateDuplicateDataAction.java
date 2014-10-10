package org.hisp.dhis.dataadmin.action.duplicatedataelimination;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamerge.DataMergeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class EliminateDuplicateDataAction
    implements Action
{
    private static final Log log = LogFactory.getLog( EliminateDuplicateDataAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMergeService dataMergeService;

    public void setDataMergeService( DataMergeService dataMergeService )
    {
        this.dataMergeService = dataMergeService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataElementToEliminate;

    public void setDataElementToEliminate( Integer dataElementToEliminate )
    {
        this.dataElementToEliminate = dataElementToEliminate;
    }

    private Integer categoryOptionComboToEliminate;

    public void setCategoryOptionComboToEliminate( Integer categoryOptionComboToEliminate )
    {
        this.categoryOptionComboToEliminate = categoryOptionComboToEliminate;
    }

    private Integer dataElementToKeep;

    public void setDataElementToKeep( Integer dataElementToKeep )
    {
        this.dataElementToKeep = dataElementToKeep;
    }

    private Integer categoryOptionComboToKeep;
    
    public void setCategoryOptionComboToKeep( Integer categoryOptionComboToKeep )
    {
        this.categoryOptionComboToKeep = categoryOptionComboToKeep;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        DataElement dataElementEliminate = dataElementService.getDataElement( dataElementToEliminate );
        
        DataElementCategoryOptionCombo categoryOptionComboEliminate = categoryService.getDataElementCategoryOptionCombo( categoryOptionComboToEliminate );
        
        DataElement dataElementKeep = dataElementService.getDataElement( dataElementToKeep );
        
        DataElementCategoryOptionCombo categoryOptionComboKeep = categoryService.getDataElementCategoryOptionCombo( categoryOptionComboToKeep );
        
        log.info( "Eliminating: " + dataElementEliminate + " " + categoryOptionComboEliminate  );
        log.info( "Keeping: " + dataElementKeep + " " + categoryOptionComboKeep );
        
        if ( !( dataElementToEliminate == dataElementToKeep && categoryOptionComboEliminate == categoryOptionComboKeep ) )
        {
            dataMergeService.eliminateDuplicateDataElement( dataElementKeep, categoryOptionComboKeep, dataElementEliminate, categoryOptionComboEliminate );
        }
        
        log.info( "Elimination done" );
        
        return SUCCESS;
    }
}
