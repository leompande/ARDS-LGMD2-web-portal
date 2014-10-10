package org.hisp.dhis.dataanalyser.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

public class GetOrgUnitsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private String type;
    
    public void setType( String type )
    {
        this.type = type;
    }

    private OrganisationUnit orgUnit;

    public OrganisationUnit getOrgUnit()
    {
        return orgUnit;
    }

    private Integer orgUnitLevel;

    public Integer getOrgUnitLevel()
    {
        return orgUnitLevel;
    }

    private Integer maxOrgUnitLevel;
    
    public Integer getMaxOrgUnitLevel()
    {
        return maxOrgUnitLevel;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( orgUnitId != null )
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        }
        
        System.out.println(" orgUnit Id is : " + orgUnit.getId() + " , orgUnit Name is : " + orgUnit.getName() );
        //orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
        orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() );
        maxOrgUnitLevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        // Hardcoded : if it is Tabular Analysis, Null Reporter
        if( type != null && type.equalsIgnoreCase( "ta" ) )
        {
            
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
            Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
        
            maxOrgUnitLevel = 1;
            Iterator<OrganisationUnit> ouIterator = orgUnitList.iterator();
            while ( ouIterator.hasNext() )
            {
                OrganisationUnit orgU = ouIterator.next();
                
                Integer level = orgunitLevelMap.get( orgU.getId() );
                if( level == null )
                    level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
                if ( level > maxOrgUnitLevel )
                {
                    maxOrgUnitLevel = level;
                }
            }
            
            /*
            for( int i = orgUnitLevel+1; i <= maxOrgUnitLevel; i++ )
            {
                Collection<OrganisationUnit> tempOrgUnitList = organisationUnitService.getOrganisationUnitsAtLevel( i, orgUnit );
                if( tempOrgUnitList == null || tempOrgUnitList.size() == 0 )
                {
                    maxOrgUnitLevel = i-1;
                    break;
                }
            }
            */
        }
        
        return SUCCESS;
    }

}
