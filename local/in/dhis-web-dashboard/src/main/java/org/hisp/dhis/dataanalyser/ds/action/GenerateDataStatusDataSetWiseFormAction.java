package org.hisp.dhis.dataanalyser.ds.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class GenerateDataStatusDataSetWiseFormAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------    
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------    
    
    public String execute() throws Exception
    {
        /* Periods Type */
        periodTypes = new ArrayList<PeriodType>( periodService.getAllPeriodTypes() );
        
        Iterator<PeriodType> periodTypeIterator = periodTypes.iterator();
        while ( periodTypeIterator.hasNext() )
        {
            PeriodType type = periodTypeIterator.next();
            if ( type.getName().equalsIgnoreCase("Daily") || type.getName().equalsIgnoreCase("Weekly") || type.getName().equalsIgnoreCase("Monthly") || type.getName().equalsIgnoreCase("quarterly") || type.getName().equalsIgnoreCase("yearly") )
            {
            }
            else
            {
                periodTypeIterator.remove();
            }
        }
/*        
        for( PeriodType type : periodTypes )
        {
           System.out.println( "Period Type Name is : " + type.getName() + ", Period Type Id is : " + type.getId() );
        }
        
*/        return SUCCESS;
    }
}
