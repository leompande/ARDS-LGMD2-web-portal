package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValue;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class SaveEquipmentDataValueAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveEquipmentDataValueAction.class );
    
    public static final String PREFIX_DATAELEMENT = "dataelement";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private EquipmentDataValueService equipmentDataValueService;
    
    public void setEquipmentDataValueService( EquipmentDataValueService equipmentDataValueService )
    {
        this.equipmentDataValueService = equipmentDataValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    

    private int selectedDataSetId;
    
    public void setSelectedDataSetId( int selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }
    
    private int equipmentId;
    
    public void setEquipmentId( int equipmentId )
    {
        this.equipmentId = equipmentId;
    }
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private int statusCode = 0;

    public int getStatusCode()
    {
        return statusCode;
    }  
   
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        Period period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + selectedPeriodId );
        }
        
        Equipment equipment = equipmentService.getEquipment( equipmentId );
        OrganisationUnit organisationUnit = equipment.getOrganisationUnit();
        
        DataSet dataSet = dataSetService.getDataSet( selectedDataSetId );
        
       /*
        if ( dataSetService.isLocked( dataSet, period, organisationUnit, null ) )
        {
            return logError( "Entry locked for combination: " + dataSet + ", " + period + ", " + organisationUnit, 2 );
        }
*/
        
        
        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        String storedBy = currentUserService.getCurrentUsername();
        
        Date timestamp = new Date();
        
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        
        // ---------------------------------------------------------------------
        // Add / Update data
        // ---------------------------------------------------------------------
        HttpServletRequest request = ServletActionContext.getRequest();
        
        //String value = null;
        
        if ( dataElements != null && dataElements.size() > 0 )
        {
            for ( DataElement dataElement : dataElements )
            {
                EquipmentDataValue equipmentDataValue = equipmentDataValueService.getEquipmentDataValue( equipment, period, dataElement );
                
                String value = request.getParameter( PREFIX_DATAELEMENT + dataElement.getId() );
                
                if ( value != null && value.trim().length() == 0 )
                {
                    value = null;
                }

                if ( value != null )
                {
                    value = value.trim();
                }
                
                if ( equipmentDataValue == null )
                {
                    if ( value != null )
                    {
                        equipmentDataValue = new EquipmentDataValue();
                        
                        equipmentDataValue.setValue( value );
                        equipmentDataValue.setDataElement( dataElement );
                        equipmentDataValue.setEquipment( equipment );
                        equipmentDataValue.setPeriod( period );
                        equipmentDataValue.setStoredBy( storedBy );
                        equipmentDataValue.setTimestamp( timestamp );
                        equipmentDataValueService.addEquipmentDataValue( equipmentDataValue );
                    }
                }     
                else
                {
                    //System.out.println( " dataElement :" + dataElement + "-- value : " + value );
                    equipmentDataValue.setDataElement( dataElement );
                    equipmentDataValue.setValue( value );
                    equipmentDataValue.setEquipment( equipment );
                    equipmentDataValue.setPeriod( period );
                    equipmentDataValue.setStoredBy( storedBy );
                    equipmentDataValue.setTimestamp( timestamp );
                    equipmentDataValueService.updateEquipmentDataValue( equipmentDataValue );
                }
            }
                 
        }
        
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String logError( String message )
    {
        return logError( message, 1 );
    }

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    }
    
    
}
