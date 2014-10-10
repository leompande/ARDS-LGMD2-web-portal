package org.hisp.dhis.reportsheet.dataentrystatus.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.status.DataEntryStatus;
import org.hisp.dhis.reportsheet.utils.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class DefaultDataEntryStatusAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    private CurrentUserService currentUserService;

    private UserService userService;

    private OrganisationUnitSelectionManager selectionManager;

    private PeriodService periodService;

    private CompleteDataSetRegistrationService completeDataSetRegistrationService;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataEntryStatus> dataStatus;

    private Map<DataSet, List<DataEntryStatus>> maps;

    private List<DataSet> dataSets;

    private OrganisationUnit organisationUnit;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public Map<DataSet, List<DataEntryStatus>> getMaps()
    {
        return maps;
    }

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setCompleteDataSetRegistrationService(
        CompleteDataSetRegistrationService completeDataSetRegistrationService )
    {
        this.completeDataSetRegistrationService = completeDataSetRegistrationService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public List<DataEntryStatus> getDataStatus()
    {
        return dataStatus;
    }

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public I18nFormat getFormat()
    {
        return format;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        this.organisationUnit = selectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit != null )
        {
            dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );

            if ( !currentUserService.currentUserIsSuper() )
            {
                UserCredentials userCredentials = userService.getUserCredentials( currentUserService.getCurrentUser() );

                Set<DataSet> dataSetUserAuthorityGroups = new HashSet<DataSet>();

                for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
                {
                    dataSetUserAuthorityGroups.addAll( userAuthorityGroup.getDataSets() );
                }

                dataSets.retainAll( dataSetUserAuthorityGroups );
            }

            dataStatus = new ArrayList<DataEntryStatus>( exportReportService
                .getDataEntryStatusDefaultByDataSets( dataSets ) );

            maps = new HashMap<DataSet, List<DataEntryStatus>>();

            Calendar calendar = Calendar.getInstance();
            List<Period> periods = null;

            for ( DataEntryStatus d : dataStatus )
            {
                d.setNumberOfDataElement( d.getDataSet().getDataElements().size() );

                periods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( d.getPeriodType(), DateUtils
                    .getFirstDayOfYear( calendar.get( Calendar.YEAR ) ), DateUtils.getLastDayOfYear( calendar
                    .get( Calendar.YEAR ) ) ) );

                Collections.sort( periods, new PeriodComparator() );

                List<DataEntryStatus> ds_temp = new ArrayList<DataEntryStatus>();

                for ( Period p : periods )
                {
                    DataEntryStatus dataStatusNew = new DataEntryStatus();
                    dataStatusNew.setPeriod( p );
                    dataStatusNew.setNumberOfDataElement( d.getNumberOfDataElement() );
                    dataStatusNew.setNumberOfDataValue( exportReportService.countDataValueOfDataSet( d.getDataSet(),
                        organisationUnit, p ) );

                    CompleteDataSetRegistration completeDataSetRegistration = completeDataSetRegistrationService
                        .getCompleteDataSetRegistration( d.getDataSet(), p, organisationUnit );

                    dataStatusNew.setCompleted( (completeDataSetRegistration == null ? false : true) );

                    ds_temp.add( dataStatusNew );

                }
                maps.put( d.getDataSet(), ds_temp );
            }
        }
        return SUCCESS;
    }
}
