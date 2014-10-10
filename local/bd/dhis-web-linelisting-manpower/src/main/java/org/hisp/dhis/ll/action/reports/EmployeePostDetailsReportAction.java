package org.hisp.dhis.ll.action.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.ll.action.lldataentry.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

public class EmployeePostDetailsReportAction implements Action
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

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<LineListDataValue> llDataValuesList;
    
    public List<LineListDataValue> getLlDataValuesList()
    {
        return llDataValuesList;
    }

    private List<LineListElement> lineListElements;
    
    public List<LineListElement> getLineListElements()
    {
        return lineListElements;
    }

    private String deptAndPostIds;
    
    public void setDeptAndPostIds( String deptAndPostIds )
    {
        this.deptAndPostIds = deptAndPostIds;
    }
   
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit selOrgUnit = selectedStateManager.getSelectedOrganisationUnit();

        System.out.println("deptAndPostNames: "+ deptAndPostIds ); 
        String[] partsOfDeptAndPostNames = deptAndPostIds.split( ":" );
        
        LineListGroup department = lineListService.getLineListGroup( Integer.parseInt( partsOfDeptAndPostNames[0] ) );
        LineListOption lineListOption = lineListService.getLineListOption( Integer.parseInt( partsOfDeptAndPostNames[1] ) );

        lineListElements = new ArrayList<LineListElement>( department.getLineListElements() );
        
        llDataValuesList = new ArrayList<LineListDataValue>();
        
        //HardCoding to get lastworkingdate linelist element
        String postLineListElementName = lineListElements.iterator().next().getShortName();
        String lastWorkingDateLLElementName  = "lastworkingdate";
        
        //preparing map to filter records from linelist table
        Map<String, String> llElementValueMap = new HashMap<String, String>();
        llElementValueMap.put( postLineListElementName, lineListOption.getName() );
        llElementValueMap.put( lastWorkingDateLLElementName, "null" );

        llDataValuesList = dataBaseManagerInterface.getLLValuesFilterByLLElements(  department.getShortName(), llElementValueMap, selOrgUnit );
        
        return SUCCESS;
    }
}
