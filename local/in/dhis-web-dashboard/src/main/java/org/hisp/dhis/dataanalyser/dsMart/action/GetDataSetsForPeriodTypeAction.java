package org.hisp.dhis.dataanalyser.dsMart.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

public class GetDataSetsForPeriodTypeAction
implements Action
{
 // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String periodType;

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    private Collection<DataSet> dataSets = new ArrayList<DataSet>();

    public Collection<DataSet> getDataSets()
    {
        return dataSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( periodType != null && !periodType.isEmpty() )
        {
            dataSets = dataSetService.getAssignedDataSetsByPeriodType( periodService.getPeriodTypeByName( periodType ) );
        }
        return SUCCESS;
    }

}
