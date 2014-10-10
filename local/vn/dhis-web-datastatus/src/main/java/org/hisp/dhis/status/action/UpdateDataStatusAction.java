package org.hisp.dhis.status.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.status.DataStatus;
import org.hisp.dhis.status.DataStatusService;

import com.opensymphony.xwork.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class UpdateDataStatusAction
    implements Action
{

    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private DataStatusService dataStatusService;

    private DataSetService dataSetService;

    // -------------------------------------------------
    // Input
    // -------------------------------------------------

    private Integer dataSetId;

    private boolean makeDefault;

    private Integer dataStatusId;

    // -------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------

    public void setDataStatusService( DataStatusService dataStatusService )
    {
        this.dataStatusService = dataStatusService;
    }

    public void setDataStatusId( Integer dataStatusId )
    {
        this.dataStatusId = dataStatusId;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public void setMakeDefault( boolean makeDefault )
    {
        this.makeDefault = makeDefault;
    }

    public String execute()
        throws Exception
    {
        DataSet dataSet = dataSetService.getDataSet( dataSetId );

        DataStatus dataStatus = dataStatusService.get( dataStatusId );
        dataStatus.setDataSet( dataSet );
        dataStatus.setFrontPage( makeDefault );
        dataStatus.setPeriodType( dataSet.getPeriodType() );

        dataStatusService.update( dataStatus );

        return SUCCESS;
    }

}