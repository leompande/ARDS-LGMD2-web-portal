/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.dataanalyser.sms.action;


import com.opensymphony.xwork2.Action;
import java.util.Collection;
import java.util.Iterator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

/**
 *
 * @author harsh
 */
public class SendDueDatesFormAction implements Action
{

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    private String result="";

    public String getResult()
    {
        return result;
    }

   private String selectDates;

    public void setSelectDates( String selectDates )
    {
        this.selectDates = selectDates;
    }
    
    @Override
    public String execute() throws Exception
    {
        Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
        Iterator<OrganisationUnit> iterator = selectedOrganisationUnits.iterator();
       while(iterator.hasNext())
        System.out.print("sel->"+iterator.next());
        System.out.println("sele"+selectDates);
       // dashBoardService.getDueDates( 1, organisationUnitId, null );
        
       result="module in progress....";    
        return SUCCESS;
    }
    
}
