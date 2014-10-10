package org.hisp.dhis.importexport.synchronous;

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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.StoreIterator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.MathUtils;

/**
 * Exports pivot view synchronously (using calling thread)
 *
 * TODO: use exportparams and abstract service
 *       factor out commonality between processIndicatorValues and processDataValues
 *
 * @author bobj
 */
public class ExportPivotViewService
{
    private static final Log log = LogFactory.getLog( ExportPivotViewService.class );

    public enum RequestType
    {
        DATAVALUE, INDICATORVALUE
    }

    public static int PRECISION = 5;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void execute( OutputStream out, RequestType requestType, PeriodType periodType,
        Date startDate, Date endDate, int level, int root )
        throws IOException
    {
        Writer writer = new BufferedWriter( new OutputStreamWriter( out ) );

        Collection<Period> periods = getPeriods( periodType, startDate, endDate );

        if ( periods.isEmpty() )
        {
            log.info( "no periods to export" );
            return;
        }

        OrganisationUnit rootOrgUnit = organisationUnitService.getOrganisationUnit( root );

        if ( rootOrgUnit == null )
        {
            log.info( "no orgunit root to export with id = " + rootOrgUnit );
            return;
        }

        rootOrgUnit.setLevel( rootOrgUnit.getOrganisationUnitLevel() );

        OrganisationUnitLevel orgUnitLevel = organisationUnitService.getOrganisationUnitLevelByLevel( level );

        if ( orgUnitLevel == null )
        {
            log.info( "no level with level id = " + orgUnitLevel );
            return;
        }

        log.info( "Exporting for " + rootOrgUnit.getName() + " at level: " + orgUnitLevel.getName() );

        if ( requestType == RequestType.DATAVALUE )
        {
            processDataValues( writer, rootOrgUnit, orgUnitLevel, periods );
        } 
        else
        {
            processIndicatorValues( writer, rootOrgUnit, orgUnitLevel, periods );
        }
    }

    public int count( RequestType requestType, PeriodType periodType,
        Date startDate, Date endDate, int level, int root )
    {
        Collection<Period> periods = getPeriods( periodType, startDate, endDate );

        if ( periods.isEmpty() )
        {
            return 0;
        }

        OrganisationUnit rootOrgUnit = organisationUnitService.getOrganisationUnit( root );

        if ( rootOrgUnit == null )
        {
            return 0;
        }

        rootOrgUnit.setLevel( rootOrgUnit.getOrganisationUnitLevel() );

        OrganisationUnitLevel orgUnitLevel = organisationUnitService.getOrganisationUnitLevelByLevel( level );

        if ( orgUnitLevel == null )
        {
            return 0;
        }

        log.debug( "Counting for " + rootOrgUnit.getName() + " at level: " + orgUnitLevel.getName() );

        if ( requestType == RequestType.DATAVALUE )
        {
            return aggregatedDataValueService.countDataValuesAtLevel( rootOrgUnit, orgUnitLevel, periods );
        } 
        else
        {
            return aggregatedDataValueService.countIndicatorValuesAtLevel( rootOrgUnit, orgUnitLevel, periods );
        }
    }

    private void processDataValues( Writer writer, OrganisationUnit rootOrgUnit, OrganisationUnitLevel orgUnitLevel, Collection<Period> periods )
        throws IOException
    {
        Map<Integer, String> periodIdIsoMap = getPeriodIdIsoMap( periods );
        
        StoreIterator<AggregatedDataValue> iterator = aggregatedDataValueService.getAggregateDataValuesAtLevel( rootOrgUnit, orgUnitLevel, periods );

        try
        {
            AggregatedDataValue adv = iterator.next();

            writer.write( "# period, orgunit, dataelement, catoptcombo, value\n" );

            while ( adv != null )
            {
                writer.write( "'" + periodIdIsoMap.get( adv.getPeriodId() ) + "'," );
                writer.write( adv.getOrganisationUnitId() + "," );
                writer.write( adv.getDataElementId() + "," );
                writer.write( adv.getCategoryOptionComboId() + "," );
                writer.write( adv.getValue() + "\n" );

                adv = iterator.next();
            }
        } 
        catch ( IOException ex )
        {
            iterator.close();
            throw ( ex );
        }

        writer.flush();
    }

    private void processIndicatorValues( Writer writer, OrganisationUnit rootOrgUnit, OrganisationUnitLevel orgUnitLevel, Collection<Period> periods )
        throws IOException
    {
        Map<Integer, String> periodIdIsoMap = getPeriodIdIsoMap( periods );
        
        StoreIterator<AggregatedIndicatorValue> iterator = aggregatedDataValueService.getAggregateIndicatorValuesAtLevel( rootOrgUnit, orgUnitLevel, periods );

        try 
        {
            AggregatedIndicatorValue aiv = iterator.next();

            writer.write( "# period, orgunit, indicator, factor, numerator, denominator\n" );

            while ( aiv != null )
            {
                writer.write( "'" + periodIdIsoMap.get( aiv.getPeriodId() ) + "'," );
                writer.write( aiv.getOrganisationUnitId() + "," );
                writer.write( aiv.getIndicatorId() + "," );
                writer.write( MathUtils.roundToString( aiv.getFactor(), PRECISION ) + "," );
                writer.write( MathUtils.roundToString( aiv.getNumeratorValue(), PRECISION ) + "," );
                writer.write( MathUtils.roundToString( aiv.getDenominatorValue(), PRECISION ) + "\n" );

                aiv = iterator.next();
            }
        } 
        catch ( IOException ex )
        {
            iterator.close();
            throw ( ex );
        }

        writer.flush();
    }

    private Collection<Period> getPeriods( PeriodType periodType, Date startDate, Date endDate )
    {
        periodType = periodType != null ? periodType : new MonthlyPeriodType(); // Fall back to monthly
        return periodService.getIntersectingPeriodsByPeriodType( periodType, startDate, endDate );
    }
    
    private Map<Integer, String> getPeriodIdIsoMap( Collection<Period> periods )
    {
        Map<Integer, String> map = new HashMap<Integer, String>();
        
        for ( Period period : periods )
        {
            map.put( period.getId(), period.getIsoDate() );
        }
        
        return map;
    }
}
