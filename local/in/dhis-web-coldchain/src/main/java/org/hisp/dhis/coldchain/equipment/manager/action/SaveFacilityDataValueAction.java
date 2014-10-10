package org.hisp.dhis.coldchain.equipment.manager.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version SaveFacilityDataValueAction.javaOct 20, 2012 5:35:33 PM	
 */

public class SaveFacilityDataValueAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveFacilityDataValueAction.class );
    
    public static final String PREFIX_DATAELEMENT = "dataelement";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    

    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private int organisationUnitId;
    
    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private String selPeriodId;
    
    public void setSelPeriodId( String selPeriodId )
    {
        this.selPeriodId = selPeriodId;
    }
/*
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
*/
    
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
        //System.out.println( "inside save data value" );
        
        //Period period = PeriodType.createPeriodExternalId( selPeriodId );
        
        Period period = PeriodType.getPeriodFromIsoString( selPeriodId );
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + selPeriodId );
        }
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
/*       
        if ( dataSetService.isLocked( dataSet, period, organisationUnit, null ) )
        {
            return logError( "Entry locked for combination: " + dataSet + ", " + period + ", " + organisationUnit, 2 );
        }
*/
        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        String storedBy = currentUserService.getCurrentUsername();
        
        //Date timestamp = new Date();
        
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        Date now = new Date();
       
        // ---------------------------------------------------------------------
        // Add / Update data
        // ---------------------------------------------------------------------
        HttpServletRequest request = ServletActionContext.getRequest();
        
        //String value = null;
        
        if ( dataElements != null && dataElements.size() > 0 )
        {
            for ( DataElement dataElement : dataElements )
            {
                DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                
                
                //dataElement.getOptionSet().getOptions();
                
                String value = request.getParameter( PREFIX_DATAELEMENT + dataElement.getId() );
                
                if ( value != null && value.trim().length() == 0 )
                {
                    value = null;
                }

                if ( value != null )
                {
                    value = value.trim();
                }
                
                DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, decoc );

                if ( dataValue == null )
                {
                    if ( value != null )
                    {
                        dataValue = new DataValue( dataElement, period, organisationUnit, decoc, null, value, storedBy, now, null  ); 
                        dataValueService.addDataValue( dataValue );
                    }
                }
                else
                {
                    dataValue.setValue( value );
                    dataValue.setTimestamp( now );
                    dataValue.setStoredBy( storedBy );

                    dataValueService.updateDataValue( dataValue );
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
