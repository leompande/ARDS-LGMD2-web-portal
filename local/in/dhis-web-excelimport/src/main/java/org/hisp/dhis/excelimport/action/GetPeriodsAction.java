package org.hisp.dhis.excelimport.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Lars Helge Overland
 * @version $Id: GetPeriodsAction.java 3272 2007-04-26 22:22:50Z larshelg $
 */

public class GetPeriodsAction    extends ActionSupport
{
    private final String ALL = "null";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    private List<String> periodNameList;
    
        public List<String> getPeriodNameList() 
        {
                return periodNameList;
        }
    
    private SimpleDateFormat simpleDateFormat1;

    private SimpleDateFormat simpleDateFormat2;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        periodNameList = new ArrayList<String>();
        
        if ( id.equals( ALL ) )
        {
            Collection<PeriodType> periodTypes = periodService.getAllPeriodTypes();

            periods = new ArrayList<Period>();

            for ( PeriodType type : periodTypes )
            {
                periods.addAll( periodService.getPeriodsByPeriodType( type ) );
            }
            Collections.sort(periods, new PeriodComparator() );
        }
        else
        {
            PeriodType periodType = periodService.getPeriodTypeByName( id );

            periods = new ArrayList<Period>(periodService.getPeriodsByPeriodType( periodType ));
            
            Iterator<Period> periodIterator = periods.iterator();
            while( periodIterator.hasNext() )
            {
                Period p1 = periodIterator.next();
                
                if ( p1.getStartDate().compareTo( new Date() ) > 0 )
                {
                    periodIterator.remove( );
                }
                
            }
            Collections.sort( periods, new PeriodComparator() );
                       
            if(periodType.getName().equalsIgnoreCase("monthly"))
            {
                simpleDateFormat1 = new SimpleDateFormat( "MMM-yyyy" );
                for(Period p1 : periods)
                {
                        periodNameList.add( simpleDateFormat1.format(p1.getStartDate() ) ); 
                }
            }
            else if(periodType.getName().equalsIgnoreCase("quarterly"))
            {
                simpleDateFormat1 = new SimpleDateFormat( "MMM" );
                simpleDateFormat2 = new SimpleDateFormat( "MMM yyyy" );
                
                for(Period p1 : periods)
                {
                        String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - " + simpleDateFormat2.format( p1.getEndDate() ); 
                        periodNameList.add( tempPeriodName ); 
                }
            }
            else if(periodType.getName().equalsIgnoreCase("yearly"))
            {
                simpleDateFormat1 = new SimpleDateFormat( "yyyy" );
                for(Period p1 : periods)
                {
                        periodNameList.add( simpleDateFormat1.format(p1.getStartDate() ) ); 
                }
            }
            else
            {
                simpleDateFormat1 = new SimpleDateFormat( "yyyy-mm-dd" );
                for(Period p1 : periods)
                {
                        String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - " + simpleDateFormat1.format( p1.getEndDate() ); 
                        periodNameList.add( tempPeriodName ); 
                }
            }
        }

        
        
        return SUCCESS;
    }

}
