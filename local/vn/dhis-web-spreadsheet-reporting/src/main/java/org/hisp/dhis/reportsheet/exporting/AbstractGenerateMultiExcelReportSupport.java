package org.hisp.dhis.reportsheet.exporting;

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
import static org.hisp.dhis.reportsheet.utils.NumberUtils.PATTERN_DECIMAL_FORMAT1;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.applyPatternDecimalFormat;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.resetDecimalFormatByLocale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hisp.dhis.dataelement.LocalDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reportsheet.CategoryOptionAssociationService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public abstract class AbstractGenerateMultiExcelReportSupport
    extends GenerateExcelReportGeneric
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    @Autowired
    protected OrganisationUnitService organisationUnitService;

    @Autowired
    protected CategoryOptionAssociationService categoryOptionAssociationService;

    @Autowired
    protected LocalDataElementService localDataElementService;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        selectedPeriod = PeriodType.createPeriodExternalId( selectionManager.getSelectedPeriodIndex() );

        this.installPeriod();

        List<ExportReport> reports = new ArrayList<ExportReport>();

        for ( String id : selectionManager.getListObject() )
        {
            reports.add( exportReportService.getExportReport( Integer.parseInt( id ) ) );
        }

        resetDecimalFormatByLocale( Locale.GERMAN );
        applyPatternDecimalFormat( PATTERN_DECIMAL_FORMAT1 );
        
        executeGenerateOutputFile( reports );

        this.complete();

        statementManager.destroy();

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Overriding abstract method(s)
    // -------------------------------------------------------------------------

    /**
     * The process method which must be implemented by subclasses.
     * 
     * @param period
     * @param reports
     * @param organisationUnit
     */
    protected abstract void executeGenerateOutputFile( List<ExportReport> reports )
        throws Exception;
}
