package org.hisp.dhis.status.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.status.DataStatus;
import org.hisp.dhis.status.DataStatusService;
import org.hisp.dhis.status.utils.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork.Action;

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
/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class DefaultDataStatusAction
    implements Action
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private DataStatusService dataStatusService;

    private CurrentUserService currentUserService;

    private UserStore userStore;

    private OrganisationUnitSelectionManager selectionManager;

    private DataSetService dataSetService;

    private PeriodService periodService;

    private DataValueService dataValueService;

    private CompleteDataSetRegistrationService completeDataSetRegistrationService;

    private I18nFormat format;

    // -------------------------------------------------
    // Output
    // -------------------------------------------------

    private List<DataStatus> dataStatus;

    private Map<DataSet, List<DataStatus>> maps;

    private List<DataSet> dataSets;

    // -------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public Map<DataSet, List<DataStatus>> getMaps()
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

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
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

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    public List<DataStatus> getDataStatus()
    {
        return dataStatus;
    }

    public void setDataStatusService( DataStatusService dataStatusService )
    {
        this.dataStatusService = dataStatusService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public I18nFormat getFormat()
    {
        return format;
    }

    public String execute()
        throws Exception
    {
       
        if ( selectionManager.getSelectedOrganisationUnit() != null )
        {

            dataSets = new ArrayList<DataSet>( dataSetService.getDataSetsBySource( selectionManager
                .getSelectedOrganisationUnit() ) );

            if ( !currentUserService.currentUserIsSuper() )
            {
                UserCredentials userCredentials = userStore.getUserCredentials( currentUserService.getCurrentUser() );

                Set<DataSet> dataSetUserAuthorityGroups = new HashSet<DataSet>();

                for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
                {
                    dataSetUserAuthorityGroups.addAll( userAuthorityGroup.getDataSets() );
                }

                dataSets.retainAll( dataSetUserAuthorityGroups );
            }            
           

            dataStatus = new ArrayList<DataStatus>( dataStatusService.getDataStatusDefaultByDataSets(  dataSets ) );

            maps = new HashMap<DataSet, List<DataStatus>>();

            Calendar calendar = Calendar.getInstance();

            for ( DataStatus d : dataStatus )
            {
                d.setNumberOfDataElement( d.getDataSet().getDataElements().size() );

                PeriodType periodType = d.getPeriodType();

                List<Period> periods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType,
                    DateUtils.getFirstDayOfYear( calendar.get( Calendar.YEAR ) ), DateUtils.getLastDayOfYear( calendar
                        .get( Calendar.YEAR ) ) ) );
                List<DataStatus> ds_temp = new ArrayList<DataStatus>();
                Collections.sort( periods, new PeriodComparator() );
                for ( Period p : periods )
                {

                    DataStatus dataStatusNew = new DataStatus();
                    dataStatusNew.setPeriod( p );
                    dataStatusNew.setNumberOfDataElement( d.getNumberOfDataElement() );
                    int i = 0;
                    for ( DataElement de : d.getDataSet().getDataElements() )
                    {
                        DataValue datavalue = dataValueService.getDataValue( selectionManager
                            .getSelectedOrganisationUnit(), de, p );
                        if ( datavalue != null )
                        {
                            i++;
                        }
                    }
                    dataStatusNew.setNumberOfDataValue( i);
                    CompleteDataSetRegistration completeDataSetRegistration = completeDataSetRegistrationService
                        .getCompleteDataSetRegistration( d.getDataSet(), p, selectionManager
                            .getSelectedOrganisationUnit() );
                    dataStatusNew.setCompleted( (completeDataSetRegistration == null ? false : true) );
                    
                    
                    
                    
                    ds_temp.add( dataStatusNew );

                }
                maps.put( d.getDataSet(), ds_temp );
            }
        }
        return SUCCESS;
    }
}
