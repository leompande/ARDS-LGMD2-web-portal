package org.hisp.dhis.ll.action.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListDataElementMap;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.ll.action.lldataentry.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

public class SummaryReportAction
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

    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }

    private LineListService lineListService;
    
    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private DataValueService dataValueService;
    
    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<String, String> resultMap;
    
    public Map<String, String> getResultMap()
    {
        return resultMap;
    }

    private Map<String, String> resultIdMap;
    
    public Map<String, String> getResultIdMap()
    {
        return resultIdMap;
    }

    List<String> resultKeys;
    
    public List<String> getResultKeys()
    {
        return resultKeys;
    }
    
    private String selOrgUnitName = "NONE";
    
    public String getSelOrgUnitName()
    {
        return selOrgUnitName;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        resultMap = new HashMap<String, String>();
        resultIdMap = new HashMap<String, String>();
        resultKeys = new ArrayList<String>();
        Period dataValuePeriod = periodService.getPeriod( 0 );
        OrganisationUnit selOrgUnit = selectedStateManager.getSelectedOrganisationUnit();
        String lastWorkingDateLLElementName  = "lastworkingdate";
        //int sanctionedPostsCount = 0;
        
        if( selOrgUnit == null )
        {
            return SUCCESS;
        }
        
        selOrgUnitName = selOrgUnit.getName();
        
        List<LineListGroup> lineListGroups = new ArrayList<LineListGroup>( lineListService.getLineListGroupsBySource( selOrgUnit ) );
        
        for( LineListGroup lineListGroup : lineListGroups )
        {
            LineListElement postElement = lineListGroup.getLineListElements().iterator().next();
            List<LineListOption> postOptions = new ArrayList<LineListOption>( postElement.getLineListElementOptions() );
            
            for( LineListOption postOption : postOptions )
            {
                int sanctionedPostsCount = 0;
                Map<String, String> llElementValueMap = new HashMap<String, String>();
                llElementValueMap.put( postElement.getShortName(), postOption.getName() );
                llElementValueMap.put( lastWorkingDateLLElementName, "null" );

                List<LineListDataElementMap> lineListDataElementMaps = lineListService.getLinelistDataelementMappings( postElement, postOption );
                
                if( lineListDataElementMaps != null && !lineListDataElementMaps.isEmpty() )
                {
                    LineListDataElementMap lineListDataElementMap = lineListDataElementMaps.iterator().next();
                    DataValue dataValue = dataValueService.getDataValue( selOrgUnit, lineListDataElementMap.getDataElement(), dataValuePeriod, lineListDataElementMap.getDataElementOptionCombo() );
                    if( dataValue != null && dataValue.getValue() != null )
                    {
                        sanctionedPostsCount = Integer.parseInt( dataValue.getValue() );
                    }
                }
                
                int filledPostsCount = dataBaseManagerInterface.getLLValueCountByLLElements( lineListGroup.getName(), llElementValueMap, selOrgUnit );
                int vacantPostsCount = sanctionedPostsCount - filledPostsCount;
                
                resultMap.put( lineListGroup.getName()+" - "+postOption.getName(), sanctionedPostsCount + " - " + filledPostsCount + " - " + vacantPostsCount );
                resultIdMap.put( lineListGroup.getName()+" - "+postOption.getName(), lineListGroup.getId()+":"+postOption.getId() );
            }
        }
        
        resultKeys.addAll( resultMap.keySet() );
        Collections.sort( resultKeys );
        
        return SUCCESS;
    }

}
