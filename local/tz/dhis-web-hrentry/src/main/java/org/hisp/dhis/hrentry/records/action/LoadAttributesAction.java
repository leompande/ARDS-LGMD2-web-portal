package org.hisp.dhis.hrentry.records.action;

import java.util.ArrayList;
import java.util.Collection;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Attribute;

import com.opensymphony.xwork2.Action;

public class LoadAttributesAction
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

    private int hrDataSetId;

    public void setHrDataSetId( int hrDataSetId )
    {
        this.hrDataSetId = hrDataSetId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<Attribute> attributes = new ArrayList<Attribute>();

    public Collection<Attribute> getAttributes()
    {
        return attributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        HrDataSet selectedHrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
        
        if ( selectedHrDataSet != null )
        {
        	// -----------------------------------------------------------------
            // Load Attribute with combo options for selected Hr Data Sets
            // -----------------------------------------------------------------
            
        	for ( Attribute tempAttribute : selectedHrDataSet.getAttribute() )
            {
        		if ( tempAttribute.getInputType().getName().equals("combo"))
        		{        		
        			attributes.add(tempAttribute);
        		}
            }
        }

        return SUCCESS;
    }
}
