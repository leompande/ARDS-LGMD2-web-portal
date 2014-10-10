package org.hisp.dhis.ihrissyncmanager;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * Gaurav<gaurav08021@gmail.com>, 8/29/12 [3:10 PM]
 */
public interface SyncIHRISOrgUnitService {

    String ID = SyncIHRISOrgUnitService.class.getName();

    public OrganisationUnit createNewOrgUnit(String orgUnitName, String parentOrgUnitName);
}
