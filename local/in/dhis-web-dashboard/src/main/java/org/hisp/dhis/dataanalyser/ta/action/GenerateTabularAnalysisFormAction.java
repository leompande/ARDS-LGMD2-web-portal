package org.hisp.dhis.dataanalyser.ta.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.FinancialAprilPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.TwoYearlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

public class GenerateTabularAnalysisFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private SectionService sectionService;
    
    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
/*
    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }
*/
    private List<Indicator> indicators;

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    private List<IndicatorGroup> indicatorGroups;

    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
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

    private String dailyPeriodTypeName;
   
    public String getDailyPeriodTypeName()
    {
    return dailyPeriodTypeName;
    }
    
    private String weeklyPeriodTypeName;
    
    public String getWeeklyPeriodTypeName()
    {
        return weeklyPeriodTypeName;
    }

    private String monthlyPeriodTypeName;

    public String getMonthlyPeriodTypeName()
    {
        return monthlyPeriodTypeName;
    }

    private String quarterlyPeriodTypeName;

    public String getQuarterlyPeriodTypeName()
    {
        return quarterlyPeriodTypeName;
    }

    private String sixMonthPeriodTypeName;

    public String getSixMonthPeriodTypeName()
    {
        return sixMonthPeriodTypeName;
    }

    private String yearlyPeriodTypeName;

    public String getYearlyPeriodTypeName()
    {
        return yearlyPeriodTypeName;
    }

    private Integer maxOrgUnitLevels;

    public Integer getMaxOrgUnitLevels()
    {
        return maxOrgUnitLevels;
    }

    private List<Period> yearlyPeriods;

    public List<Period> getYearlyPeriods()
    {
        return yearlyPeriods;
    }

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    private List<OrganisationUnitGroup> orgUnitGroups;

    public List<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }
    
    private String financialAprilPeriodType;
    
    public String getFinancialAprilPeriodType()
    {
        return financialAprilPeriodType;
    }
    
    private List<Section> sections;
    
    public Collection<Section> getSections()
    {
        return sections;
    }
    
    public String execute()
        throws Exception
    {
        /* DataElements and Groups */
        dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        System.out.println(" dataElements size = "+dataElements.size());
        // take only those dataElement which are VALUE_TYPE_INT and DOMAIN_TYPE_AGGREGATE
        Iterator<DataElement> alldeIterator = dataElements.iterator();
        while ( alldeIterator.hasNext() )
        {
            DataElement dataElement = alldeIterator.next();
            if ( !dataElement.getDomainType().equalsIgnoreCase( DataElement.DOMAIN_TYPE_AGGREGATE ) )
            {
                alldeIterator.remove();
            }
        }
        System.out.println(" dataElements size = "+dataElements.size());
        
        //dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        
        // for dataSet Sections
        sections = new ArrayList<Section>();
        sections = new ArrayList<Section>( sectionService.getAllSections() );
        Collections.sort( sections, new SectionOrderComparator() );
        
        
        Collections.sort( dataElements, new IdentifiableObjectNameComparator() );
        //Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );

        /* Indicators and Groups */
        indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        indicatorGroups = new ArrayList<IndicatorGroup>( indicatorService.getAllIndicatorGroups() );
        Collections.sort( indicators, new IdentifiableObjectNameComparator() );
        Collections.sort( indicatorGroups, new IdentifiableObjectNameComparator() );

        /* Monthly Periods */
        periodTypes = new ArrayList<PeriodType>( periodService.getAllPeriodTypes() );

        Iterator<PeriodType> ptIterator = periodTypes.iterator();
/*        while ( ptIterator.hasNext() )
        {
            String pTName = ptIterator.next().getName();
            if ( pTName.equalsIgnoreCase( DailyPeriodType.NAME ) || pTName.equalsIgnoreCase( TwoYearlyPeriodType.NAME )
                || pTName.equalsIgnoreCase( OnChangePeriodType.NAME )
                || pTName.equalsIgnoreCase( WeeklyPeriodType.NAME ) )
            {
                ptIterator.remove();
            }
        }
*/        
        while ( ptIterator.hasNext() )
        {
            String pTName = ptIterator.next().getName();
           // if ( pTName.equalsIgnoreCase( FinancialAprilPeriodType.NAME ) || pTName.equalsIgnoreCase( TwoYearlyPeriodType.NAME ) || pTName.equalsIgnoreCase( OnChangePeriodType.NAME ) )
            //if ( pTName.equalsIgnoreCase( TwoYearlyPeriodType.NAME ) || pTName.equalsIgnoreCase( OnChangePeriodType.NAME ) )
            if ( pTName.equalsIgnoreCase( TwoYearlyPeriodType.NAME ) )
            {
                ptIterator.remove();
            }
        }
        
        monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new MonthlyPeriodType() ) );
        periodNameList = new ArrayList<String>();
        Collections.sort( monthlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        dailyPeriodTypeName = DailyPeriodType.NAME;
        weeklyPeriodTypeName = WeeklyPeriodType.NAME;
        monthlyPeriodTypeName = MonthlyPeriodType.NAME;
        quarterlyPeriodTypeName = QuarterlyPeriodType.NAME;
        sixMonthPeriodTypeName = SixMonthlyPeriodType.NAME;
        yearlyPeriodTypeName = YearlyPeriodType.NAME;
        financialAprilPeriodType =  FinancialAprilPeriodType.NAME;
        

        yearlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new YearlyPeriodType() ) );
        Iterator<Period> periodIterator = yearlyPeriods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        Collections.sort( yearlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "yyyy" );
        //System.out.println( monthlyPeriodTypeName );
       // System.out.println( dailyPeriodTypeName );
       // int year;
        for ( Period p1 : yearlyPeriods )
        {
            //year = Integer.parseInt( simpleDateFormat.format( p1.getStartDate() ) ) + 1;
           // periodNameList.add( simpleDateFormat.format( p1.getStartDate() ) + "-" + year );
            periodNameList.add( simpleDateFormat.format( p1.getStartDate() ) );
        }

        /* Organisationunit Levels */
        maxOrgUnitLevels = organisationUnitService.getNumberOfOrganisationalLevels();

        orgUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );

        return SUCCESS;
    }

}
