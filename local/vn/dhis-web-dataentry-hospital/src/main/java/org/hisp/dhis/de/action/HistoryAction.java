package org.hisp.dhis.de.action;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueAudit;
import org.hisp.dhis.datavalue.DataValueAuditService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.de.history.DataElementHistory;
import org.hisp.dhis.de.history.HistoryRetriever;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import java.util.Collection;
import java.util.List;

/**
 * @author Torgeir Lorange Ostby
 */
public class HistoryAction
    implements Action
{
    private static final int HISTORY_LENGTH = 13;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HistoryRetriever historyRetriever;

    public void setHistoryRetriever( HistoryRetriever historyRetriever )
    {
        this.historyRetriever = historyRetriever;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataValueAuditService dataValueAuditService;

    public void setDataValueAuditService( DataValueAuditService dataValueAuditService )
    {
        this.dataValueAuditService = dataValueAuditService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataElementId;

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private Integer optionComboId;

    public void setOptionComboId( Integer optionComboId )
    {
        this.optionComboId = optionComboId;
    }

    private String periodId;

    public String getPeriodId()
    {
        return periodId;
    }

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private DataElementHistory dataElementHistory;

    public DataElementHistory getDataElementHistory()
    {
        return dataElementHistory;
    }

    private boolean historyInvalid;

    public boolean isHistoryInvalid()
    {
        return historyInvalid;
    }

    private DataValue dataValue;

    public DataValue getDataValue()
    {
        return dataValue;
    }

    private List<String> standardComments;

    public List<String> getStandardComments()
    {
        return standardComments;
    }

    private Collection<DataValueAudit> dataValueAudits;

    public Collection<DataValueAudit> getDataValueAudits()
    {
        return dataValueAudits;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );

        if ( optionCombo == null )
        {
            optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }

        if ( dataElement == null )
        {
            throw new Exception( "DataElement doesn't exist: " + dataElementId );
        }

        Period period = PeriodType.createPeriodExternalId( periodId );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, optionCombo );

        if ( dataValue != null )
        {
            UserCredentials credentials = userService.getUserCredentialsByUsername( dataValue.getStoredBy() );
            storedBy = credentials != null ? credentials.getName() : dataValue.getStoredBy();
        }

        dataElementHistory = historyRetriever.getHistory( dataElement, optionCombo, organisationUnit, period, HISTORY_LENGTH );

        historyInvalid = dataElementHistory == null;

        // ---------------------------------------------------------------------
        // Data Value Audit
        // ---------------------------------------------------------------------

        dataValueAudits = dataValueAuditService.getDataValueAuditByDataValue( dataValue );

        return SUCCESS;
    }
}
