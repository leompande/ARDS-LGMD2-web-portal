package org.hisp.dhis.ll.action.lldataentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;

import com.opensymphony.xwork2.Action;

public class ShowEmployeePostFormAction
implements Action
{
  
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
 
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    List<LineListElement> lineListElements;
   
    public Collection<LineListElement> getLineListElements()
    {
        return lineListElements;
    }
    
    private String linelistGroupName;
    
    public String getLinelistGroupName()
    {
        return linelistGroupName;
    }

    private String linelistOptionName;
    
    public String getLinelistOptionName()
    {
        return linelistOptionName;
    }
    
    private Integer linelistGroupId;
    
    public Integer getLinelistGroupId()
    {
        return linelistGroupId;
    }
    
    private String dataValueMapKey;
    
    public String getDataValueMapKey()
    {
        return dataValueMapKey;
    }

    public void setDataValueMapKey( String dataValueMapKey )
    {
        this.dataValueMapKey = dataValueMapKey;
    }
    
    private String dataValue;

    public String getDataValue()
    {
        return dataValue;
    }

    public void setDataValue( String dataValue )
    {
        this.dataValue = dataValue;
    }
    
    public String reportingDate;

    public String getReportingDate()
    {
        return reportingDate;
    }
    
    public void setReportingDate( String reportingDate )
    {
        this.reportingDate = reportingDate;
    }

    private Map<String, Collection<LineListOption>> llElementOptionsMap;

    public Map<String, Collection<LineListOption>> getLlElementOptionsMap()
    {
        return llElementOptionsMap;
    }
    
    private Collection<LineListOption> lineListOptions;
    
    //--------------------------------------------------------------------------
    //Action Implementation
    //--------------------------------------------------------------------------

    public String execute()
    {
        LineListGroup lineListGroup = selectedStateManager.getSelectedLineListGroup();
        
        llElementOptionsMap = new HashMap<String, Collection<LineListOption>>();
        
        lineListElements = new ArrayList<LineListElement>( lineListGroup.getLineListElements() );
        
        if ( lineListElements.size() == 0 )
        {
            return SUCCESS;
        } 
        else
        {
            Iterator<LineListElement> it2 = lineListElements.iterator();
            while ( it2.hasNext() )
            {
                LineListElement element = it2.next();

                lineListOptions = element.getLineListElementOptions();
                llElementOptionsMap.put( element.getShortName(), lineListOptions );
            }
        }
        lineListElements.remove( 0 );
        
        // Hardcoding to remove lastWorkingDate and reasonWhyLeft
        lineListElements.remove( lineListElements.size()-1 );
        lineListElements.remove( lineListElements.size()-1 );
        
        linelistGroupName = selectedStateManager.getSelectedLineListGroup().getName();
        linelistOptionName = selectedStateManager.getSelectedLineListOption().getName();
        linelistGroupId = selectedStateManager.getSelectedLineListGroup().getId();
        
        return SUCCESS;
    }

}
