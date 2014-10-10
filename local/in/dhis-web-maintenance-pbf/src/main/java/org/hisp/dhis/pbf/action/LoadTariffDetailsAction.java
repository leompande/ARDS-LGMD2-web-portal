package org.hisp.dhis.pbf.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;

import com.opensymphony.xwork2.Action;

public class LoadTariffDetailsAction
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

    private LookupService lookupService;
    
    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
    private String dataElementName;

    public void setDataElementName( String dataElementName )
    {
        this.dataElementName = dataElementName;
    }

    private String orgUnitUid;

    public void setOrgUnitUid( String orgUnitUid )
    {
        this.orgUnitUid = orgUnitUid;
    }

    private List<TariffDataValue> tariffList = new ArrayList<TariffDataValue>();

    public List<TariffDataValue> getTariffList()
    {
        return tariffList;
    }

    private String updateAuthority;

    public String getUpdateAuthority()
    {
        return updateAuthority;
    }

    private DataElement selecteddataElement;

    public DataElement getSelecteddataElement()
    {
        return selecteddataElement;
    }

    private List<DataSet> dataSets = new ArrayList<DataSet>();
    
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );;
    
    public SimpleDateFormat getSimpleDateFormat() {
		return simpleDateFormat;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
    {
        User curUser = currentUserService.getCurrentUser();
        
        List<UserAuthorityGroup> userAuthorityGroups = new ArrayList<UserAuthorityGroup>( curUser.getUserCredentials().getUserAuthorityGroups() );
        
        for ( UserAuthorityGroup userAuthorityGroup : userAuthorityGroups )
        {
            userAuthorityGroup.getUserGroupAccesses();
            if ( userAuthorityGroup.getAuthorities().contains( "F_TARIFFDATAVALUE_UPDATE" ) )
            {
                updateAuthority = "Yes";
            }
            else
            {
                updateAuthority = "No";
            }
        }

        selecteddataElement = dataElementService.getDataElementByName( dataElementName );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        tariffList = new ArrayList<TariffDataValue>( tariffDataValueService.getTariffDataValues( organisationUnit, selecteddataElement ) );

        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
        
        for( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );
            
            DataSet dataSet = dataSetService.getDataSet( dataSetId );
            
            dataSets.add( dataSet );
        }
        
        return SUCCESS;
    }
}