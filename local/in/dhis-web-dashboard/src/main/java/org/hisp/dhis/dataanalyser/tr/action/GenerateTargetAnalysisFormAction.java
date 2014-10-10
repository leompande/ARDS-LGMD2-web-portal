package org.hisp.dhis.dataanalyser.tr.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

public class GenerateTargetAnalysisFormAction
implements Action
{
    //--------------------------------------------------------------------------
    //Dependencies
    //--------------------------------------------------------------------------
 /*   
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
   */ 
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitGroupService orgUnitGroupService;
    
    public void setOrgUnitGroupService( OrganisationUnitGroupService orgUnitGroupService )
    {
        this.orgUnitGroupService = orgUnitGroupService;
    }
    
    private SectionService sectionService;
    
    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }
    //--------------------------------------------------------------------------
    //Input/Output
    //--------------------------------------------------------------------------
    
   private List<DataElementGroup> dataElementGroups;

   public List<DataElementGroup> getDataElementGroups()
   {
      return dataElementGroups;
   }
   
   private List<OrganisationUnitGroup> orgUnitGroups;

   public List<OrganisationUnitGroup> getOrgUnitGroups()
   {
       return orgUnitGroups;
   }
   
   private List<Period> monthlyPeriods;
   
   public List<Period> getMonthlyPeriods()
   {
       return monthlyPeriods;
   }
   
   private SimpleDateFormat simpleDateFormat;

   public SimpleDateFormat getSimpleDateFormat()
   {
       return simpleDateFormat;
   }
   
   private List<Section> sections;
   
   public Collection<Section> getSections()
   {
       return sections;
   }
    //--------------------------------------------------------------------------
    //Action Implementation
    //--------------------------------------------------------------------------

    public String execute()
    {
        //dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        //Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );
        
        // for dataSet Sections
        sections = new ArrayList<Section>();
        sections = new ArrayList<Section>( sectionService.getAllSections() );
        Collections.sort( sections, new SectionOrderComparator() );
        
        
        orgUnitGroups = new ArrayList<OrganisationUnitGroup> ( orgUnitGroupService.getAllOrganisationUnitGroups() );
        
        monthlyPeriods = new ArrayList<Period> ( periodService.getPeriodsByPeriodType( new MonthlyPeriodType() ));
        Iterator<Period> periodIterator = monthlyPeriods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        Collections.sort( monthlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        return SUCCESS;
    }
}
