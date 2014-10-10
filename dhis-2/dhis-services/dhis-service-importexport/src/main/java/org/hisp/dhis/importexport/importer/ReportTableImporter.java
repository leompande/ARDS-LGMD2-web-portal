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

import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;

import java.util.List;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableImporter
    extends AbstractImporter<ReportTable> implements Importer<ReportTable>
{
    protected ReportTableService reportTableService;

    protected DataElementService dataElementService;

    protected DataElementCategoryService categoryService;

    protected IndicatorService indicatorService;

    protected DataSetService dataSetService;

    protected PeriodService periodService;

    protected OrganisationUnitService organisationUnitService;

    public ReportTableImporter()
    {
    }

    public ReportTableImporter( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }

    @Override
    public void importObject( ReportTable object, ImportParams params )
    {
        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( ReportTable object )
    {
        reportTableService.saveReportTable( object );
    }

    @Override
    protected void importMatching( ReportTable object, ReportTable match )
    {
        match.setName( object.getName() );
        match.setRegression( object.isRegression() );

        match.setDoIndicators( match.isDoIndicators() );
        match.setDoPeriods( match.isDoPeriods() );
        match.setDoUnits( match.isDoUnits() );

        match.getRelatives().setReportingMonth( object.getRelatives().isReportingMonth() );
        match.getRelatives().setMonthsThisYear( object.getRelatives().isMonthsThisYear() );
        match.getRelatives().setQuartersThisYear( object.getRelatives().isQuartersThisYear() );
        match.getRelatives().setThisYear( object.getRelatives().isThisYear() );
        match.getRelatives().setMonthsLastYear( object.getRelatives().isMonthsLastYear() );
        match.getRelatives().setQuartersLastYear( object.getRelatives().isQuartersLastYear() );
        match.getRelatives().setLastYear( object.getRelatives().isLastYear() );

        match.getReportParams().setParamReportingMonth( object.getReportParams().isParamReportingMonth() );
        match.getReportParams().setParamParentOrganisationUnit( object.getReportParams().isParamParentOrganisationUnit() );
        match.getReportParams().setParamOrganisationUnit( object.getReportParams().isParamOrganisationUnit() );

        reportTableService.saveReportTable( match );
    }

    @Override
    protected ReportTable getMatching( ReportTable object )
    {
        List<ReportTable> reportTableByName = reportTableService.getReportTableByName( object.getName() );
        return reportTableByName.isEmpty() ? null : reportTableByName.get( 0 );
    }

    @Override
    protected boolean isIdentical( ReportTable object, ReportTable existing )
    {
        if ( !object.getName().equals( existing.getName() ) )
        {
            return false;
        }
        if ( object.isRegression() != existing.isRegression() )
        {
            return false;
        }

        if ( object.isDoIndicators() != existing.isDoIndicators() )
        {
            return false;
        }
        if ( object.isDoPeriods() != existing.isDoPeriods() )
        {
            return false;
        }
        if ( object.isDoUnits() != existing.isDoUnits() )
        {
            return false;
        }

        return true;
    }
}
