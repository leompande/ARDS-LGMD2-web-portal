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
package org.hisp.dhis.dataanalyser.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetWeeklyPeriodAction.java Nov 30, 2010 12:14:31 PM
 */
public class GetWeeklyPeriodAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    } 
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }


    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    

    private String yearList;
    
    public void setYearList( String yearList )
    {
        this.yearList = yearList;
    }

/*        
    private List<String> yearList;
    
    public void setYearList( List<String> yearList )
    {
        this.yearList = yearList;
    }
*/    
    private String weeklyPeriodTypeName;
    
    public void setWeeklyPeriodTypeName( String weeklyPeriodTypeName )
    {
        this.weeklyPeriodTypeName = weeklyPeriodTypeName;
    }
    
    private List<String> weeklyPeriodList;
    
    public List<String> getWeeklyPeriodList()
    {
        return weeklyPeriodList;
    }
    
    private List<Period> periods;
    
    public List<Period> getPeriods()
    {
        return periods;
    }
   
    String[] yearListArray;
   
    private SimpleDateFormat simpleDateFormat1;
    private SimpleDateFormat simpleDateFormat2;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
        throws Exception
    {
       
        simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
        
        simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        weeklyPeriodList = new ArrayList<String>();
        
       // System.out.println( "selected Year  size : " + yearList.length());
        
        //tempYearList = new ArrayList<String>();
        
        weeklyPeriodTypeName = WeeklyPeriodType.NAME;
        PeriodType periodType = periodService.getPeriodTypeByName( weeklyPeriodTypeName );
        
        yearListArray = yearList.split( ";" ) ;
       // int j = yearListArray.length;
        for ( int i = 0 ; i < yearListArray.length ; i++ )
        {
           // int selYear = Integer.parseInt( year.split( "-" )[0] );
            
           
            String selYear = yearListArray[i].split( "-" )[0];
            
            String tempStartDate = selYear+"-01-01";
            String tempEndDate = selYear+"-12-31";
            
            Date startDate = format.parseDate( tempStartDate );
            Date endDate   = format.parseDate( tempEndDate );
            
            periods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, startDate, endDate ) );
            
          //  System.out.println( "Period  size : " + periods.size());
            for ( Period period : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( period.getStartDate() ) + "To" + simpleDateFormat2.format( period.getEndDate() );
               // System.out.println( "tempPeriodName : " + tempPeriodName );
                weeklyPeriodList.add( tempPeriodName );
               // System.out.println( "weekly period is  : " + weeklyPeriodList );
            }
            
        }
        
        //Collection<PeriodType> periodTypes = periodService.getAllPeriodTypes();
        //PeriodType periodType = periodService.;
        
        return SUCCESS;   
    }
}
