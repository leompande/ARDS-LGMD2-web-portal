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
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 * @version $Id: PeriodBatchHandler.java 5062 2008-05-01 18:10:35Z larshelg $
 */
public class PeriodBatchHandler
    extends AbstractBatchHandler<Period>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public PeriodBatchHandler( JdbcConfiguration config )
    {
        super( config, false, true );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "period" );
    }
    
    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "periodid" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "periodid" );
    }
    
    @Override
    protected void setIdentifierValues( Period period )
    {
        statementBuilder.setIdentifierValue( period.getId() );
    }
    
    @Override
    protected void setMatchColumns()
    {
        statementBuilder.setMatchColumn( "periodtypeid" );
        statementBuilder.setMatchColumn( "startdate" );
        statementBuilder.setMatchColumn( "enddate" );
    }
    
    @Override
    protected void setMatchValues( Object object )
    {
        Period period = (Period) object;

        statementBuilder.setMatchValue( period.getPeriodType().getId() );
        statementBuilder.setMatchValue( period.getStartDate() );
        statementBuilder.setMatchValue( period.getEndDate() );        
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "periodtypeid" );
        statementBuilder.setUniqueColumn( "startdate" );
        statementBuilder.setUniqueColumn( "enddate" );
    }
    
    protected void setUniqueValues( Period period )
    {        
        statementBuilder.setUniqueValue( period.getPeriodType().getId() );
        statementBuilder.setUniqueValue( period.getStartDate() );
        statementBuilder.setUniqueValue( period.getEndDate() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "periodtypeid" );
        statementBuilder.setColumn( "startdate" );
        statementBuilder.setColumn( "enddate" );
    }
    
    protected void setValues( Period period )
    {        
        statementBuilder.setValue( period.getPeriodType().getId() );
        statementBuilder.setValue( period.getStartDate() );
        statementBuilder.setValue( period.getEndDate() );
    }
}
