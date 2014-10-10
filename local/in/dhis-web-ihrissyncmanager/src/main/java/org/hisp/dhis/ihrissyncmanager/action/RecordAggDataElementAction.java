package org.hisp.dhis.ihrissyncmanager.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.ihrissyncmanager.AggDataService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gaurav<gaurav08021@gmail.com>, 8/27/12 [5:07 PM]
 */
public class RecordAggDataElementAction implements Action {


    //------------------------------------------------------------------------------------------------------
    //                                       Dependencies
    //------------------------------------------------------------------------------------------------------


    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    private AggDataService recordAggDataService;

    public void setRecordAggDataService(AggDataService recordAggDataService)
    {
        this.recordAggDataService = recordAggDataService;
    }

    //------------------------------------------------------------------------------------------------------
    //                                      Web-Params
    //------------------------------------------------------------------------------------------------------


    private String DataElementName;

    private String AggValue;

    private String OrganisationUnitName;

    private String PeriodStart;

    private String PeriodTypeString;

    public void setDataElementName(String dataElementName) {
        DataElementName = dataElementName;
    }

    public void setAggValue(String aggValue) {
        AggValue = aggValue;
    }

    public void setOrganisationUnitName(String organisationUnitName) {
        OrganisationUnitName = organisationUnitName;
    }

    public void setPeriodStart(String periodStart) {
        PeriodStart = periodStart;
    }

    public void setPeriodTypeString(String periodTypeString) {
        PeriodTypeString = periodTypeString;
    }

    //------------------------------------------------------------------------------------------------------
    //                                       Implementation
    //------------------------------------------------------------------------------------------------------


    public Period getSelectedPeriod(Date startDate, PeriodType periodType) throws Exception {


        List<Period> periods = new ArrayList<Period>(periodService.getPeriodsByPeriodType(periodType));
        for (Period period : periods) {
            Date tempDate = period.getStartDate();
            if (tempDate.equals(startDate)) {
                return period;
            }
        }

        Period period = periodType.createPeriod(startDate);
        period = reloadPeriodForceAdd(period);


        return period;
    }

    private final Period reloadPeriod(Period period) {
        return periodService.getPeriod(period.getStartDate(), period.getEndDate(), period.getPeriodType());
    }

    private final Period reloadPeriodForceAdd(Period period) {
        Period storedPeriod = reloadPeriod(period);

        if (storedPeriod == null) {

            periodService.addPeriod(period);
            return period;
        }

        return storedPeriod;
    }

    //------------------------------------------------------------------------------------------------------
    //                                       Action Implementation
    //------------------------------------------------------------------------------------------------------

    public String execute() throws Exception
    {

        PeriodType periodType =  PeriodType.getPeriodTypeByName(PeriodTypeString);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd,yyyy");

        Date convertedStartDate = dateFormat.parse(PeriodStart);

        Period period = getSelectedPeriod(convertedStartDate,periodType);

        System.out.println("=======================================================================================================================================================================");

        System.out.println("* TEST AGG-DATAELEMENT ACTION [Org. Unit:"+OrganisationUnitName+" ,DataElement Name:"+DataElementName+" ,Aggregate Value:"+AggValue+" ,Start Date:"+convertedStartDate+" ,PeriodType:"+PeriodTypeString+"]");

        System.out.println("=======================================================================================================================================================================");

        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnitByName(OrganisationUnitName);

        if(orgUnit==null)
        {
            orgUnit = organisationUnitService.getOrganisationUnit(1);
        }

        recordAggDataService.addNewEntries(DataElementName,orgUnit,period,Double.parseDouble(AggValue));

        return null;
    }
}
