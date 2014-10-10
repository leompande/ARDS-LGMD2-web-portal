package org.hisp.dhis.reportsheet.period.db;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reportsheet.utils.DateUtils;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultSelectedStateManager.java 5282 2008-05-28 10:41:06Z
 *          larshelg $
 * @modifier Dang Duy Hieu
 * @since 2009-10-14
 */
public class DefaultPeriodDatabaseManager
    implements PeriodDatabaseService
{
    private static final Log LOG = LogFactory.getLog( DefaultPeriodDatabaseManager.class );

    public static final String SESSION_DATABASE_KEY_SELECTED_PERIOD_TYPE_NAME = "SESSION_DATABASE_KEY_SELECTED_PERIOD_TYPE_NAME";

    public static final String SESSION_DATABASE_KEY_SELECTED_PERIOD = "SESSION_DATABASE_KEY_SELECTED_PERIOD";

    public static final String SESSION_DATABASE_KEY_BASE_PERIOD = "SESSION_DATABASE_KEY_BASE_PERIOD";

    public static final String SESSION_DATABASE_KEY_SELECTED_YEAR = "SESSION_DATABASE_KEY_SELECTED_YEAR";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Cache
    // -------------------------------------------------------------------------

    private ThreadLocal<List<Period>> generatedPeriodsCache = new ThreadLocal<List<Period>>();

    // -------------------------------------------------------------------------
    // SelectedStateManager implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public void setSelectedPeriodTypeName( String periodTypeName )
    {
        getSession().put( SESSION_DATABASE_KEY_SELECTED_PERIOD_TYPE_NAME, periodTypeName );
    }

    public String getSelectedPeriodTypeName()
    {
        return (String) getSession().get( SESSION_DATABASE_KEY_SELECTED_PERIOD_TYPE_NAME );
    }

    public Period getSelectedPeriod()
    {
        return (Period) getSession().get( SESSION_DATABASE_KEY_SELECTED_PERIOD );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setSelectedperiod( Integer periodId )
    {
        Period period = periodService.getPeriod( periodId );

        getSession().put( SESSION_DATABASE_KEY_SELECTED_PERIOD, period );

    }

    public void clearSelectedPeriod()
    {
        getSession().remove( SESSION_DATABASE_KEY_SELECTED_PERIOD );
    }

    public List<Period> getPeriodList()
    {
        List<Period> periods = generatedPeriodsCache.get();

        Period basePeriod = getBasePeriod();

        if ( periods == null || periods.size() == 0
            || !periods.get( 0 ).getPeriodType().equals( basePeriod.getPeriodType() ) || !periods.contains( basePeriod ) )
        {
            CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

            LOG.debug( "Generated periods cache invalid, generating new periods based on " + basePeriod );

            periods = periodType.generatePeriods( basePeriod );

            generatedPeriodsCache.set( periods );
        }

        Collection<Period> persistedPeriods = periodService.getPeriodsByPeriodType( getPeriodType() );

        // get the period elements which exist in Collection
        persistedPeriods.retainAll( periods );
        Collections.sort( (ArrayList<Period>) persistedPeriods, new PeriodComparator() );

        // return periods;
        return (ArrayList<Period>) persistedPeriods;
    }

    @SuppressWarnings( "unchecked" )
    public void nextPeriodSpan()
    {
        setSelectedYear( getSelectedYear() + 1 );

        List<Period> periods = getPeriodList();
        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

        Period basePeriod = periods.get( periods.size() - 1 );
        Period newBasePeriod = periodType.getNextPeriod( basePeriod );

        // Future periods not allowed
        if ( newBasePeriod.getStartDate().before( new Date() ) )
        {
            getSession().put( SESSION_DATABASE_KEY_BASE_PERIOD, newBasePeriod );
        }
        generatedPeriodsCache.remove();

    }

    @SuppressWarnings( "unchecked" )
    public void previousPeriodSpan()
    {

        setSelectedYear( getSelectedYear() - 1 );

        List<Period> periods = getPeriodList();
        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

        Period basePeriod = periods.get( 0 );
        Period newBasePeriod = periodType.getPreviousPeriod( basePeriod );

        getSession().put( SESSION_DATABASE_KEY_BASE_PERIOD, newBasePeriod );

        generatedPeriodsCache.remove();
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------
    private PeriodType getPeriodType()
    {
        return periodService.getPeriodTypeByName( getSelectedPeriodTypeName() );
    }

    @SuppressWarnings( "unchecked" )
    private Period getBasePeriod()
    {
        Period basePeriod = (Period) getSession().get( SESSION_DATABASE_KEY_BASE_PERIOD );
        PeriodType periodType = getPeriodType();

        if ( basePeriod == null )
        {
            LOG.debug( "getBasePeriod(): Base period is null, creating new." );

            basePeriod = periodType.createPeriod();
            getSession().put( SESSION_DATABASE_KEY_BASE_PERIOD, basePeriod );

            setSelectedYear( DateUtils.getCurrentYear() );

        }
        else if ( getSelectedYear() > 0 )
        {
            Date firstDayOfYear = DateUtils.getFirstDayOfYear( getSelectedYear() );

            basePeriod = periodType.createPeriod( firstDayOfYear );
        }

        return basePeriod;
    }

    @SuppressWarnings( "unchecked" )
    private static final Map getSession()
    {
        return ActionContext.getContext().getSession();
    }

    @SuppressWarnings( "unchecked" )
    private void setSelectedYear( Integer year )
    {
        getSession().put( SESSION_DATABASE_KEY_SELECTED_YEAR, year );

    }

    private Integer getSelectedYear()
    {
        return (Integer) getSession().get( SESSION_DATABASE_KEY_SELECTED_YEAR );
    }

}
