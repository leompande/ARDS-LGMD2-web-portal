package org.hisp.dhis.pbf.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class ValidateTariffDataAction
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

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    public String getPbfType() {
		return pbfType;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getOrgUnitUid() {
		return orgUnitUid;
	}

	private String message;
    
    public String getMessage() {
		return message;
	}

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
        throws Exception
    {	
		System.out.println(startDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );

        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( pbfType ) );        
        
        List<TariffDataValue> tariffDataValues = new ArrayList<TariffDataValue>( tariffDataValueService.getTariffDataValues(organisationUnit, dataElement));
        //boolean status = false;
        for(TariffDataValue tdv : tariffDataValues)
        {
        	System.out.println(tdv.getDataSet().getId());
        	System.out.println(dataSet.getId());
        	System.out.println(tdv.getStartDate().before(sDate));
        	System.out.println(tdv.getEndDate().after(eDate));
        	if(tdv.getDataSet().getId() == dataSet.getId() && tdv.getStartDate().before(sDate) && tdv.getEndDate().after(eDate) )
        	{
        		message = "true";
        		break;
        	}
        	else
        	{
        		message = "false";
        	}
        }
        
        return SUCCESS;
    }
}