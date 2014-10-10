package org.hisp.dhis.coldchain.equipmenttype.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;

import com.opensymphony.xwork2.Action;

public class GetDataSetListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<DataSet> dataSets;
    
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }



    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Collections.sort( dataSets, new IdentifiableObjectNameComparator() );
        
        /*
        for( DataSet dataSet : dataSets )
        {
            System.out.println( "ID---" + dataSet.getId() );
            System.out.println( "Name---" + dataSet.getName());
            System.out.println( "Discription---" + dataSet.getDescription() );
            System.out.println( "code---" + dataSet.getCode() );
        }
        */
        return SUCCESS;
    }

}

