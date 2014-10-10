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
package org.hisp.dhis.dataanalyser.ds.mobile.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateDataStatusMobileFormAction.java Nov 24, 2010 2:34:42 PM
 */
public class GenerateDataStatusMobileFormAction
implements Action
{

    /* Dependencies */
/*
    @SuppressWarnings("unused")
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
*/
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    /* Output Parameters */
    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Period> monthlyPeriods;

    public List<Period> getMonthlyPeriods()
    {
        return monthlyPeriods;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    public String execute()
        throws Exception
    {
        /* DataSet List */
        
        dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        
        //dataSetList = new ArrayList<DataSet>( dataSetService.getDataSetsForMobile( ));
        
        Iterator<DataSet> dataSetListIterator = dataSetList.iterator();
        
        while(dataSetListIterator.hasNext())
        {
            DataSet d = (DataSet) dataSetListIterator.next();

            if ( d.getSources().size() <= 0 )
            {
                dataSetListIterator.remove();
            }
            else
            {                       
                // -------------------------------------------------------------------------
                // Added to remove Indian Linelisting datasets
                // -------------------------------------------------------------------------
                
                if ( d.getId() == 8 || d.getId() == 9 || d.getId() == 10 || d.getId() == 14
                        || d.getId() == 15 || d.getId() == 35 || d.getId() == 36 || d.getId() == 37
                        || d.getId() == 38 )
                {
                        dataSetListIterator.remove();
                } 
                
                // Remove datasets which are nor mobile datasets
                //else if ( d.getMobile() == null || !d.getMobile())
                else if ( !d.isMobile() )    
                {
                   dataSetListIterator.remove();
                }
            }
        }
       
        //Collections.sort( dataSetList, new DataSetNameComparator() );
        Collections.sort( dataSetList, new IdentifiableObjectNameComparator() );
        System.out.println("Size of DataSet List is : " + dataSetList.size());

        /* Monthly Periods */
        //monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new MonthlyPeriodType() ) );
        //Collections.sort( monthlyPeriods, new PeriodStartDateComparator() );
        //simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        return SUCCESS;
    }
 

}// class end

