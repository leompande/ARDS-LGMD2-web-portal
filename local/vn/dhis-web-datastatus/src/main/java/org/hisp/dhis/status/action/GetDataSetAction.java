package org.hisp.dhis.status.action;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import java.util.*;

import com.opensymphony.xwork.Action;

public class GetDataSetAction
    implements Action
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private DataSetService dataSetService;

    private OrganisationUnitSelectionManager selectionManager;

    private UserStore userStore;

    private CurrentUserService currentUserService;

    // -------------------------------------------------
    // Ouput
    // -------------------------------------------------

    private List<DataSet> dataSets;

    // -------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public String execute()
        throws Exception
    {
        if ( selectionManager.getSelectedOrganisationUnit() != null )
        {

            dataSets = new ArrayList<DataSet>( dataSetService.getDataSetsBySource( selectionManager
                .getSelectedOrganisationUnit() ) );

            if ( !currentUserService.currentUserIsSuper() )
            {
                UserCredentials userCredentials = userStore.getUserCredentials( currentUserService.getCurrentUser() );

                Set<DataSet> dataSetUserAuthorityGroups = new HashSet<DataSet>();

                for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
                {
                    dataSetUserAuthorityGroups.addAll( userAuthorityGroup.getDataSets() );
                }

                dataSets.retainAll( dataSetUserAuthorityGroups );
            }
        }
        
        Collections.sort( dataSets, new DataSetNameComparator() );
        
        return SUCCESS;
    }

}
