package org.hisp.dhis.hrentry.records.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Wilfred Senyoni
 */

public class GetReportOptionsAction 
implements Action
{
	
	private HrDataSetService hrDataSetService;

    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
        this.hrDataSetService = hrDataSetService;
    }
    
    private List<HrDataSet> hrDataSets;

    public List<HrDataSet> getHrDataSets()
    {
        return hrDataSets;
    }
        
    public String execute()
    {
    	hrDataSets = new ArrayList<HrDataSet>( hrDataSetService.getAllHrDataSets() );    	
        
        return SUCCESS;
    }

}
