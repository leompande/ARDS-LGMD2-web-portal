package org.hisp.dhis.validationrule.minmax.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;

import com.opensymphony.xwork2.Action;

public class MinMaxViolationFormAction implements Action
{

    //-------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    //-------------------------------------------------------------------------
    // Input & Output
    //-------------------------------------------------------------------------

    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    //-------------------------------------------------------------------------
    // Action Implementation
    //-------------------------------------------------------------------------
  
    public String execute()
        throws Exception
    {
        /* DataSet List */
        
        dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        
        Collections.sort( dataSetList, new DataSetNameComparator() );

        return SUCCESS;
    }
 

}
