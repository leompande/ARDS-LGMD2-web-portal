package org.hisp.dhis.reports.routine.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSetPopulator;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GenerateColdChainRoutineReportAnalyserFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
 
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    
    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }
    
    private List<OrganisationUnitGroup> orgUnitGroupMembers;
    
    public List<OrganisationUnitGroup> getOrgUnitGroupMembers()
    {
        return orgUnitGroupMembers;
    }
    
    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    
    public String execute() throws Exception
    {
        //reportTypeName = ReportType.RT_ROUTINE;
        reportTypeName = ReportType.RT_COLDCHAIN_REPORT;
        periodTypes = periodService.getAllPeriodTypes();

        Iterator<PeriodType> periodTypeIterator = periodTypes.iterator();
        while ( periodTypeIterator.hasNext() )
        {
            PeriodType type = periodTypeIterator.next();
            
            if( type.getName().equalsIgnoreCase("Monthly") || type.getName().equalsIgnoreCase("quarterly") || type.getName().equalsIgnoreCase("yearly") || type.getName().equalsIgnoreCase("weekly") || type.getName().equalsIgnoreCase("Daily") )
            {
            }
            else
            {
                periodTypeIterator.remove();
            }
        }
        
        OrganisationUnitGroupSet organisationUnitGroupSet1 = organisationUnitGroupService.getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_TYPE );
        
        orgUnitGroupMembers = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet1.getOrganisationUnitGroups() );
        
        OrganisationUnitGroupSet organisationUnitGroupSet2 = organisationUnitGroupService.getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_OWNERSHIP );
        
        orgUnitGroupMembers.addAll( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet2.getOrganisationUnitGroups() ) );
        
        return SUCCESS;
    }
}
