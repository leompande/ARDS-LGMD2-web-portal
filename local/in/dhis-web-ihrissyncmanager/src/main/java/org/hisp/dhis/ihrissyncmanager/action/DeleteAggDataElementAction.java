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
 * Created with IntelliJ IDEA.
 * User: Mohit
 * Date: 15/9/12
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteAggDataElementAction implements Action {



    //------------------------------------------------------------------------------------------------------
    //                                       Dependencies
    //------------------------------------------------------------------------------------------------------

    private AggDataService deleteAggDataService;

    public void setDeleteAggDataService(AggDataService deleteAggDataService) {
        this.deleteAggDataService = deleteAggDataService;
    }


    //------------------------------------------------------------------------------------------------------
    //                                      Web-Params
    //------------------------------------------------------------------------------------------------------

    private String dataElementName;

    public void setDataElementName(String dataElementName) {
        this.dataElementName = dataElementName;
    }

    //------------------------------------------------------------------------------------------------------
    //                                       Implementation
    //------------------------------------------------------------------------------------------------------

    @Override
    public String execute() throws Exception {

        System.out.println("=======================================================================================================================================================================");

        System.out.println("* TEST Delete-DATAELEMENT ACTION [DataElement Name:"+dataElementName+"]");

        System.out.println("=======================================================================================================================================================================");

        deleteAggDataService.deleteAggDataElement(dataElementName);

        return null;
    }
}
