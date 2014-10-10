package org.hisp.dhis.ihrissyncmanager;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * Gaurav<gaurav08021@gmail.com>, 8/27/12 [2:07 PM]
 */
public interface AggDataService {

    String ID = AggDataService.class.getName();

    public DataElement createNewAggDataElement(String aggDataElementName);

    public void addNewEntries(String aggDataElementName,OrganisationUnit orgUnit,Period period, Double aggValue);

    public void deleteAggDataElement(String nameOfDataElement);

}