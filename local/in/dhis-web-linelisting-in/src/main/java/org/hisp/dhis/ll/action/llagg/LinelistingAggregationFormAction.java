package org.hisp.dhis.ll.action.llagg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;

import com.opensymphony.xwork2.Action;

public class LinelistingAggregationFormAction
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

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private List<DataSet> datasets;

    public List<DataSet> getDatasets()
    {
        return datasets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        datasets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

        Iterator<DataSet> dataSetListIterator = datasets.iterator();

        while ( dataSetListIterator.hasNext() )
        {
            DataSet d = (DataSet) dataSetListIterator.next();

            if ( d.getSources().size() <= 0 )
            {
                dataSetListIterator.remove();
            }
        }

        Collections.sort( datasets, new  IdentifiableObjectNameComparator() );

        return SUCCESS;
    }

}
