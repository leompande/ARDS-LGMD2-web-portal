package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.period.RelativePeriods;

public class RelativePeriodsBatchHandler
    extends AbstractBatchHandler<RelativePeriods>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public RelativePeriodsBatchHandler( JdbcConfiguration configuration )
    {
        super( configuration, false, false );
    }
    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "relativeperiods" );
    }
    
    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "relativeperiodsid" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "relativeperiodsid" );
    }
    
    @Override
    protected void setIdentifierValues( RelativePeriods relatives )
    {        
        statementBuilder.setIdentifierValue( relatives.getId() );
    }

    @Override
    protected void setUniqueColumns()
    {
    }

    @Override
    protected void setUniqueValues( RelativePeriods relatives )
    {        
    }

    @Override
    protected void setColumns()
    {
        statementBuilder.setColumn( "reportingmonth" );
        statementBuilder.setColumn( "reportingbimonth" );
        statementBuilder.setColumn( "reportingquarter" );
        statementBuilder.setColumn( "lastsixmonth" );
        statementBuilder.setColumn( "monthsthisyear" );
        statementBuilder.setColumn( "quartersthisyear" );
        statementBuilder.setColumn( "thisyear" );        
        statementBuilder.setColumn( "monthslastyear" );
        statementBuilder.setColumn( "quarterslastyear" );
        statementBuilder.setColumn( "lastyear" );
        statementBuilder.setColumn( "last5years" );
        statementBuilder.setColumn( "last12months" );
        statementBuilder.setColumn( "last3months" );
        statementBuilder.setColumn( "last6bimonths" );        
        statementBuilder.setColumn( "last4quarters" );
        statementBuilder.setColumn( "last2sixmonths" );
        statementBuilder.setColumn( "thisfinancialyear" );
        statementBuilder.setColumn( "lastfinancialyear" );
        statementBuilder.setColumn( "last5financialyears" );
        statementBuilder.setColumn( "lastweek" );
        statementBuilder.setColumn( "last4weeks" );
        statementBuilder.setColumn( "last12weeks" );
        statementBuilder.setColumn( "last52weeks" );
    }

    @Override
    protected void setValues( RelativePeriods relatives )
    {        
        statementBuilder.setValue( relatives.isReportingMonth() );
        statementBuilder.setValue( relatives.isReportingBimonth() );
        statementBuilder.setValue( relatives.isReportingQuarter() );
        statementBuilder.setValue( relatives.isLastSixMonth() );
        statementBuilder.setValue( relatives.isMonthsThisYear() );
        statementBuilder.setValue( relatives.isQuartersThisYear() );
        statementBuilder.setValue( relatives.isThisYear() );
        statementBuilder.setValue( relatives.isMonthsLastYear() );
        statementBuilder.setValue( relatives.isQuartersLastYear() );
        statementBuilder.setValue( relatives.isLastYear() );
        statementBuilder.setValue( relatives.isLast5Years() );
        statementBuilder.setValue( relatives.isLast12Months() );
        statementBuilder.setValue( relatives.isLast3Months() );
        statementBuilder.setValue( relatives.isLast6BiMonths() );
        statementBuilder.setValue( relatives.isLast4Quarters() );
        statementBuilder.setValue( relatives.isLast2SixMonths() );
        statementBuilder.setValue( relatives.isThisFinancialYear() );
        statementBuilder.setValue( relatives.isLastFinancialYear() );
        statementBuilder.setValue( relatives.isLast5FinancialYears() );
        statementBuilder.setValue( relatives.isLastWeek() );
        statementBuilder.setValue( relatives.isLast4Weeks() );
        statementBuilder.setValue( relatives.isLast12Weeks() );        
        statementBuilder.setValue( relatives.isLast52Weeks() );
    }
}
