package org.hisp.dhis.detarget.action;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detargetdatavalue.DeTargetDataValue;
import org.hisp.dhis.detargetdatavalue.DeTargetDataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.survey.action.SaveValueAction;
import org.hisp.dhis.survey.state.SelectedStateManager;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class SavetargetValueAction
implements Action
{
    private static final Log LOG = LogFactory.getLog( SaveValueAction.class );
    
    //--------------------------------------------------------------------------
    //Dependencies
    //--------------------------------------------------------------------------
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
        
    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }    
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private DeTargetDataValueService deTargetdataValueService;
    
    public void setDeTargetdataValueService( DeTargetDataValueService deTargetdataValueService )
    {
        this.deTargetdataValueService = deTargetdataValueService;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    //--------------------------------------------------------------------------
    //Input/Output
    //--------------------------------------------------------------------------
    
    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    private int dataElementId;

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public int getDataElementId()
    {
        return dataElementId;
    }

    private int optionComboId;

    public void setOptionComboId( int optionComboId )
    {
        this.optionComboId = optionComboId;
    }

    public int getOptionComboId()
    {
        return optionComboId;
    }

    private int statusCode=0;

    public int getStatusCode()
    {
        return statusCode;
    }

    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }
    
    private int detargetid;
    
    public int getDetargetid()
    {
        return detargetid;
    }

    public void setDetargetid( int detargetid )
    {
        this.detargetid = detargetid;
    }
    
    //--------------------------------------------------------------------------
    //Action Implementation
    //--------------------------------------------------------------------------
    
    public String execute()
    {
        OrganisationUnit orgUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        Period period = selectedStateManager.getSelectedPeriod();
        
        period = periodService.reloadPeriod( period );
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        storedBy = currentUserService.getCurrentUsername();
        
        DeTarget deTarget = selectedStateManager.getSelectedDeTarget();
        
        DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );
        
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        if ( value != null )
        {
            value = value.trim();
        }
        
        DeTargetDataValue dataValue = deTargetdataValueService.getDeTargetDataValue( orgUnit, deTarget, period, dataElement, optionCombo );
        if ( dataValue == null )
        {
            if ( value != null )
            {
                LOG.debug( "Adding DataValue, value added" );

                dataValue = new DeTargetDataValue( deTarget, dataElement, optionCombo, orgUnit, period, value, storedBy, new Date() );

                deTargetdataValueService.addDeTargetDataValue( dataValue );
            }
        }
        else
        {
            if( value != null )
            {
                LOG.debug( "Updating DataValue, value added/changed" );
        
                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );
                dataValue.setStoredBy( storedBy );
        
                deTargetdataValueService.updateDeTargetDataValue( dataValue );
            }
            else
            {
                LOG.debug( "Deleting DataValue, null value deleted" );
                
                deTargetdataValueService.deleteDeTargetDataValue( dataValue );                
            }
        }
            
        return SUCCESS;
    }

}