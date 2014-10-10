package org.hisp.dhis.importexport.importer;

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

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;

/**
 * @author Lars Helge Overland
 */
public class ChartImporter
    extends AbstractImporter<Chart> implements Importer<Chart>
{
    protected ChartService chartService;

    public ChartImporter()
    {
    }
    
    public ChartImporter( ChartService chartService )
    {
        this.chartService = chartService;
    }
    
    @Override
    public void importObject( Chart object, ImportParams params )
    {
        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( Chart object )
    {
        chartService.addChart( object );
    }

    @Override
    protected void importMatching( Chart object, Chart match )
    {
        match.setName( object.getName() );
        match.setType( object.getType() );
        match.setHideLegend( object.isHideLegend() );
        match.setRegression( object.isRegression() );        

        match.getRelatives().setReportingMonth( object.getRelatives().isReportingMonth() );
        match.getRelatives().setMonthsThisYear( object.getRelatives().isMonthsThisYear() );
        match.getRelatives().setQuartersThisYear( object.getRelatives().isQuartersThisYear() );
        match.getRelatives().setThisYear( object.getRelatives().isThisYear() );
        match.getRelatives().setMonthsLastYear( object.getRelatives().isMonthsLastYear() );
        match.getRelatives().setQuartersLastYear( object.getRelatives().isQuartersLastYear() );
        match.getRelatives().setLastYear( object.getRelatives().isLastYear() );
        
        chartService.addChart( match );
    }

    @Override
    protected Chart getMatching( Chart object )
    {
        return chartService.getChartByName( object.getName() );
    }

    @Override
    protected boolean isIdentical( Chart object, Chart existing )
    {
        if ( !object.getName().equals( existing.getName() ) )
        {
            return false;
        }
        if ( !isSimiliar( object.getType(), existing.getType() ) || ( isNotNull( object.getType(), existing.getType() ) && !object.getType().equals( existing.getType() ) ) )
        {
            return false;
        }
        if ( object.isHideLegend() != existing.isHideLegend() )
        {
            return false;
        }
        if ( object.isRegression() != existing.isRegression() )
        {
            return false;
        }
                
        return true;
    }
}
