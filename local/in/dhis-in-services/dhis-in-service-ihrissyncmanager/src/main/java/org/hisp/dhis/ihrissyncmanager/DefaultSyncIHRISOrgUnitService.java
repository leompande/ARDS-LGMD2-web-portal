package org.hisp.dhis.ihrissyncmanager;

import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

/**
 * Gaurav<gaurav08021@gmail.com>, 8/29/12 [5:06 PM]
**/

public class DefaultSyncIHRISOrgUnitService implements SyncIHRISOrgUnitService {

    DataElementService dataElementService;

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    public OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnit createNewOrgUnit(String orgUnitName, String parentOrgUnitName)
    {
        OrganisationUnit checkIfOrgUnitExists = organisationUnitService.getOrganisationUnitByName(orgUnitName);

        OrganisationUnit checkIfParentOrgUnitExists = organisationUnitService.getOrganisationUnitByName(parentOrgUnitName);

        if(checkIfParentOrgUnitExists == null)
        {
            return null;
        }
        else if(checkIfOrgUnitExists == null && checkIfParentOrgUnitExists!= null)
        {
            OrganisationUnit newIHRISOrgUnit = new OrganisationUnit();

            newIHRISOrgUnit.setName(orgUnitName);

            newIHRISOrgUnit.setShortName(orgUnitName);

            newIHRISOrgUnit.setActive(true);

            newIHRISOrgUnit.setParent(checkIfParentOrgUnitExists);

            organisationUnitService.addOrganisationUnit(newIHRISOrgUnit);

            return newIHRISOrgUnit;
        }

        return checkIfOrgUnitExists;
    }

}
