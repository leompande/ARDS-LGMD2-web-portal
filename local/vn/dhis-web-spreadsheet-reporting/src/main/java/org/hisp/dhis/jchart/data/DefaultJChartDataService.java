package org.hisp.dhis.jchart.data;

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
import java.util.Collections;
import java.util.List;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.hisp.dhis.jchart.JChart;
import org.hisp.dhis.jchart.JChartSeries;
import org.hisp.dhis.jchart.JChartSevice;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.reportsheet.period.db.PeriodDatabaseService;

/**
 * @author Tran Thanh Tri
 */

public class DefaultJChartDataService
    implements JChartDataService
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private JChartSevice jchartService;

    public void setJchartService( JChartSevice jchartService )
    {
        this.jchartService = jchartService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private I18nManager i18nManager;

    public void setI18nManager( I18nManager manager )
    {
        i18nManager = manager;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private PeriodDatabaseService periodDatabaseService;

    public void setPeriodDatabaseService( PeriodDatabaseService periodDatabaseService )
    {
        this.periodDatabaseService = periodDatabaseService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Implement
    // -------------------------------------------------------------------------

    private JChartData getJChartData( JChart jchart )
        throws I18nManagerException
    {
        statementManager.initialise();

        I18nFormat format = i18nManager.getI18nFormat();

        OrganisationUnit organisationUnit = selectionTreeManager.getSelectedOrganisationUnit();

        JChartData jChartData = new JChartData();

        jChartData.setTitle( jchart.getTitle() );
        jChartData.setSubtitle( organisationUnit.getName() );
        jChartData.setLegend( jchart.getLegend() );

        List<Period> periods = new ArrayList<Period>();

        if ( jchart.isLoadSelectedPeriods() )
        {
            periods.addAll( jchart.getPeriods() );
        }
        else
        {
            periodDatabaseService.setSelectedPeriodTypeName( jchart.getPeriodType().getName() );

            periods.addAll( periodDatabaseService.getPeriodList() );
        }

        Collections.sort( periods, new AscendingPeriodComparator() );

        for ( JChartSeries series : jchart.getSeries() )
        {
            JChartSeriesData jChartSeriesData = new JChartSeriesData();
            jChartSeriesData.setColor( series.getColor() );
            jChartSeriesData.setName( series.getIndicator().getName() );

            for ( Period period : periods )
            {
                Double value = aggregationService.getAggregatedIndicatorValue( series.getIndicator(), period
                    .getStartDate(), period.getEndDate(), organisationUnit );

                jChartSeriesData.addValue( value == null ? -1 : value );

                jChartData.addCategory( format.formatPeriod( period ) );
            }

            jChartSeriesData.setType( series.getType() );

            jChartData.addSeries( jChartSeriesData );
        }

        statementManager.destroy();

        return jChartData;
    }

    private JChartData getJChartData( JChart jchart, Period period )
        throws I18nManagerException
    {
        statementManager.initialise();

        I18nFormat format = i18nManager.getI18nFormat();

        JChartData jChartData = new JChartData();

        jChartData.setTitle( jchart.getTitle() );
        jChartData.setSubtitle( format.formatPeriod( period ) );
        jChartData.setLegend( jchart.getLegend() );

        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( selectionTreeManager
            .getSelectedOrganisationUnits() );

        Collections.sort( organisationUnits, IdentifiableObjectNameComparator.INSTANCE );

        for ( JChartSeries series : jchart.getSeries() )
        {
            JChartSeriesData jChartSeriesData = new JChartSeriesData();
            jChartSeriesData.setColor( series.getColor() );
            jChartSeriesData.setName( series.getIndicator().getName() );

            for ( OrganisationUnit organisationUnit : organisationUnits )
            {
                Double value = aggregationService.getAggregatedIndicatorValue( series.getIndicator(), period
                    .getStartDate(), period.getEndDate(), organisationUnit );

                jChartSeriesData.addValue( value == null ? -1 : value );

                jChartData.addCategory( organisationUnit.getName() );
            }

            jChartSeriesData.setType( series.getType() );

            jChartData.addSeries( jChartSeriesData );
        }

        statementManager.destroy();

        return jChartData;
    }

    @Override
    public JChartData getJChartData( int jchartId, int periodId )
        throws I18nManagerException
    {
        JChart jchart = jchartService.getJChart( jchartId );

        if ( jchart.isPeriodCategory() )
        {
            return this.getJChartData( jchart );
        }
        else
        {
            Period period = periodService.getPeriod( periodId );

            return this.getJChartData( jchart, period );
        }
    }

}
