package org.hisp.dhis.hrentry.action.dataentry;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;

import com.opensymphony.xwork2.Action;


/**
 * @author Wilfred Felix Senyoni
 *
 * @version $Id$
 */

public class SearchRecordAction 
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HrDataSetService hrDataSetService;

    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
        this.hrDataSetService = hrDataSetService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------      
    
    private Integer hrDataSetId;
    
    public void setHrDataSetId( Integer hrDataSetId )
    {
        this.hrDataSetId = hrDataSetId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------      
      
    private HrDataSet hrDataSet;
    
    public HrDataSet getHrDataSet()
    {
    	return hrDataSet;    	
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
    	hrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
        
        return SUCCESS;
    }

}