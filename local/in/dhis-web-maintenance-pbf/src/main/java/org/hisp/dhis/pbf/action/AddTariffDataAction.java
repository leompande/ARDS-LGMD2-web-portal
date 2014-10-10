package org.hisp.dhis.pbf.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class AddTariffDataAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TariffDataValueService tariffDataValueService;

    public void setTariffDataValueService( TariffDataValueService tariffDataValueService )
    {
        this.tariffDataValueService = tariffDataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String pbfType;

    private String tariff;

    private String startDate;

    private String endDate;

    private String dataElementId;

    private String orgUnitUid;

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public void setOrgUnitUid( String orgUnitUid )
    {
        this.orgUnitUid = orgUnitUid;
    }

    public void setPbfType( String pbfType )
    {
        this.pbfType = pbfType;
    }

    public void setTariff( String tariff )
    {
        this.tariff = tariff;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );

        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( pbfType ) );
        
        TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue( organisationUnit, dataElement, dataSet, sDate, eDate );

        if ( tariffDataValue == null )
        {
            tariffDataValue = new TariffDataValue();
            
            tariffDataValue.setValue( Double.parseDouble( tariff ) );
            tariffDataValue.setStartDate( sDate );
            tariffDataValue.setEndDate( eDate );
            tariffDataValue.setTimestamp( new Date() );
            tariffDataValue.setStoredBy( currentUserService.getCurrentUsername() );
            tariffDataValue.setDataElement( dataElement );
            tariffDataValue.setDataSet( dataSet );
            tariffDataValue.setOrganisationUnit( organisationUnit );
            
            tariffDataValueService.addTariffDataValue( tariffDataValue );
        }
        else
        {
            tariffDataValue.setValue( Double.parseDouble( tariff ) );
            tariffDataValue.setTimestamp( new Date() );
            tariffDataValue.setStoredBy( currentUserService.getCurrentUsername() );
            
            tariffDataValueService.updateTariffDataValue( tariffDataValue );
        }

        return SUCCESS;
    }
}