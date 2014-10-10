package org.hisp.dhis.ihrissyncmanager;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

/**
 * Gaurav<gaurav08021@gmail.com>, 8/27/12 [2:24 PM]
**/

public class DefaultAggDataService implements AggDataService {

    DataElementService dataElementService;

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService(DataElementCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService(DataValueService dataValueService) {
        this.dataValueService = dataValueService;
    }

    public OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    public PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }


    public DataElement createNewAggDataElement(String aggDataElementName)
    {
        DataElement checkIfDataElementExists = dataElementService.getDataElementByName(aggDataElementName);

        if(checkIfDataElementExists == null)
        {
            DataElement newAggDataElement = new DataElement();

            newAggDataElement.setName(aggDataElementName);

            newAggDataElement.setShortName(aggDataElementName);

            newAggDataElement.setActive(true);

            newAggDataElement.setType("string");

            newAggDataElement.setAggregationOperator("sum");

            newAggDataElement.setZeroIsSignificant(false);

            dataElementService.addDataElement(newAggDataElement);

            return newAggDataElement;
        }

        return checkIfDataElementExists;
    }

    public void addNewEntries(String aggDataElementName,OrganisationUnit orgUnit,Period period, Double aggValue)
    {
        DataElement aggDataElement = createNewAggDataElement(aggDataElementName);

        DataElementCategoryOptionCombo OptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        DataValue dataValue = dataValueService.getDataValue(orgUnit,aggDataElement,period,OptionCombo);

        if ((dataValue == null) && period != null )
        {

            DataValue newDataValue = new DataValue(aggDataElement,period,orgUnit,aggValue.toString(),OptionCombo);
            dataValueService.addDataValue(newDataValue);

        }
        else if( dataValue.getPeriod().getStartDate().before(period.getStartDate()) && period != null)
        {

            dataValue.setValue(aggValue.toString());
            dataValue.setPeriod(period);
            dataValue.setSource(orgUnit);

            dataValueService.updateDataValue(dataValue);
        }
    }

    @Override
    public void deleteAggDataElement(String nameOfDataElement) {


        DataElement dataElementToDelete = dataElementService.getDataElementByName(nameOfDataElement);

        dataValueService.deleteDataValuesByDataElement(dataElementToDelete);
        dataElementService.deleteDataElement(dataElementToDelete);
    }
}

