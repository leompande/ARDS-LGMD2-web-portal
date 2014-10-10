package org.hisp.dhis.ll.action.lldataentry;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class GetValidatePostVacantAction
    implements Action
{
    //--------------------------------------------------------------------------
    // Dependency
    //--------------------------------------------------------------------------

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

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService optionComboService;

    public void setOptionComboService( DataElementCategoryService optionComboService )
    {
        this.optionComboService = optionComboService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    //--------------------------------------------------------------------------
    // Input/Output
    //--------------------------------------------------------------------------
    private String dataValue;

    public void setDataValue( String dataValue )
    {
        this.dataValue = dataValue;
    }

    private String dataValueMapKey;

    public void setDataValueMapKey( String dataValueMapKey )
    {
        this.dataValueMapKey = dataValueMapKey;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    public String reportingDate;

    public void setReportingDate( String reportingDate )
    {
        this.reportingDate = reportingDate;
    }

    private String storedBy;

    private LineListGroup lineListGroup;

    //--------------------------------------------------------------------------
    // Action Implementation
    //--------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit organisationunit = selectedStateManager.getSelectedOrganisationUnit();

        lineListGroup = selectedStateManager.getSelectedLineListGroup();

        LineListOption lineListOption = selectedStateManager.getSelectedLineListOption();

        String postLineListElementName = lineListGroup.getLineListElements().iterator().next().getShortName();
        String lastWorkingDateLLElementName = "lastworkingdate";
        String departmentLineListName = lineListGroup.getName();

        // preparing map to filter records from linelist table
        Map<String, String> llElementValueMap = new HashMap<String, String>();
        llElementValueMap.put( postLineListElementName, lineListOption.getName() );
        llElementValueMap.put( lastWorkingDateLLElementName, "null" );

        int recordNo = dataBaseManagerInterface.getLLValueCountByLLElements( departmentLineListName, llElementValueMap,
            organisationunit );
        System.out.println( "The Entered Value is: " + dataValue + "Column name is: " + postLineListElementName );

        int input = Integer.parseInt( dataValue );

        if ( input > recordNo )
        {
            message = "Number of Sanctioned Position is " + input + " And Number of Filled Position is " + recordNo
                + "\nDo you want to Add ?";
            saveDataValue();
            return SUCCESS;
        }
        else
        {
            message = "Number of Filled Position is equal to Number Sanctioned Post";

            return INPUT;
        }
    }

    private void saveDataValue()
    {
        OrganisationUnit organisationunit = selectedStateManager.getSelectedOrganisationUnit();

        Period period = periodService.getPeriod( 0 );

        storedBy = currentUserService.getCurrentUsername();

        String[] partsOfDatavalueMap = dataValueMapKey.split( ":" );

        int dataElementId = Integer.parseInt( partsOfDatavalueMap[1] );

        int optionComboId = Integer.parseInt( partsOfDatavalueMap[2] );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        DataElementCategoryOptionCombo optionCombo = optionComboService
            .getDataElementCategoryOptionCombo( optionComboId );

        if ( dataValue != null && dataValue.trim().length() == 0 )
        {
            dataValue = null;
        }
        if ( dataValue != null )
        {
            dataValue = dataValue.trim();
        }

        DataValue dataValueObj = dataValueService.getDataValue( organisationunit, dataElement, period, optionCombo );

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if ( dataValueObj == null )
        {
            if ( dataValue != null )
            {
                dataValueObj = new DataValue( dataElement, period, organisationunit, dataValue, storedBy, new Date(),
                    null, optionCombo );
                dataValueService.addDataValue( dataValueObj );
            }
        }
        else
        {
            dataValueObj.setValue( dataValue );
            dataValueObj.setTimestamp( new Date() );
            dataValueObj.setStoredBy( storedBy );

            dataValueService.updateDataValue( dataValueObj );
        }
    }

}
