package org.hisp.dhis.ccem.transferfacilitydata.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version TraferFacilityDataFormAction.javaJan 21, 2013 1:46:35 PM	
 */

public class TraferFacilityDataFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private int currentYear;
    
    public int getCurrentYear()
    {
        return currentYear;
    }

    private int previousYear;
    
    public int getPreviousYear()
    {
        return previousYear;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
		List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getDataSetByShortName( "FMD" ) );
        dataSet = dataSets.get( 0 );
		
        //Period Information        
        PeriodType periodType = dataSet.getPeriodType();
        
        if ( periodType.getName().equals( "Yearly" ) )
        {
            Calendar cal = Calendar.getInstance();
            
            currentYear = cal.get( Calendar.YEAR );
            
            previousYear = currentYear - 1;
        }
        
		return SUCCESS;
    }
}
