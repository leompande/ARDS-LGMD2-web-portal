package org.hisp.dhis.dataanalyser.sa.action;

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
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;
import org.hisp.dhis.surveydatavalue.SurveyDataValue;
import org.hisp.dhis.surveydatavalue.SurveyDataValueService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class GenerateSurveyAnalysisDataAction
    implements Action
{

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private SurveyService surveyService;

    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }

    private SurveyDataValueService surveyDataValueService;

    public void setSurveyDataValueService( SurveyDataValueService surveyDataValueService )
    {
        this.surveyDataValueService = surveyDataValueService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------

    private OrganisationUnit selectedOrgUnit;

    private Indicator selectedIndicator;

    private String[] series1;

    public String[] getSeries1()
    {
        return series1;
    }

    private String[] categories1;

    public String[] getCategories1()
    {
        return categories1;
    }

    private String[] series2;

    public String[] getSeries2()
    {
        return series2;
    }

    private String[] categories2;

    public String[] getCategories2()
    {
        return categories2;
    }

    String chartTitle = "Service : ";

    public String getChartTitle()
    {
        return chartTitle;
    }

    String xAxis_Title;

    public String getXAxis_Title()
    {
        return xAxis_Title;
    }

    String yAxis_Title;

    public String getYAxis_Title()
    {
        return yAxis_Title;
    }

    List<String> numeratorDEList;

    public List<String> getNumeratorDEList()
    {
        return numeratorDEList;
    }

    List<String> denominatorDEList;

    public List<String> getDenominatorDEList()
    {
        return denominatorDEList;
    }

    Double data1[][];

    public Double[][] getData1()
    {
        return data1;
    }

    Double data2[][];

    public Double[][] getData2()
    {
        return data2;
    }

    List<List<String>> dataList;

    public List<List<String>> getDataList()
    {
        return dataList;
    }

    List<String> xseriesList;

    public List<String> getXseriesList()
    {
        return xseriesList;
    }

    List<String> yseriesList;

    public List<String> getYseriesList()
    {
        return yseriesList;
    }

    /* Input Parameters */
    private int availableIndicators;

    public void setAvailableIndicators( int availableIndicators )
    {
        this.availableIndicators = availableIndicators;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    private List<Survey> surveyList;

    public List<Survey> getSurveyList()
    {
        return surveyList;
    }

    private List<SurveyDataValue> surveyDataValueList;

    public List<SurveyDataValue> getSurveyDataValueList()
    {
        return surveyDataValueList;
    }

    private List<Period> monthlyPeriods;

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        dataList = new ArrayList<List<String>>();
        xseriesList = new ArrayList<String>();
        yseriesList = new ArrayList<String>();

        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        chartTitle = "Facility : " + selectedOrgUnit.getShortName();

        // Service Related Info
        selectedIndicator = new Indicator();
        selectedIndicator = indicatorService.getIndicator( availableIndicators );
        chartTitle += "\n Indicator : " + selectedIndicator.getName();

        surveyList = new ArrayList<Survey>( surveyService.getSurveysByIndicator( selectedIndicator ) );
        surveyDataValueList = new ArrayList<SurveyDataValue>();

        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( new MonthlyPeriodType(),
            startPeriod.getStartDate(), endPeriod.getEndDate() ) );

        data1 = getServiceValuesByPeriod();
        xAxis_Title = "Period";
        yAxis_Title = "Indicator";

        int count1 = 0;
        while ( count1 != categories1.length )
        {
            xseriesList.add( categories1[count1] );
            count1++;
        }

        data2 = new Double[surveyList.size()][monthlyPeriods.size()];
        series2 = new String[surveyList.size()];
        for ( int i = 0; i < data2.length; i++ )
        {
            Survey survey = surveyList.get( i );
            SurveyDataValue surveyDataValue = surveyDataValueService.getSurveyDataValue( selectedOrgUnit, survey,
                selectedIndicator );
            
            

            surveyDataValueList.add( surveyDataValue );

            series2[i] = survey.getName();
            for ( int j = 0; j < data2[i].length; j++ )
            {
                if ( surveyDataValue != null )
                {
                    data2[i][j] = Double.parseDouble( surveyDataValue.getValue() );
                }
                else
                {
                    data2[i][j] = 0.0;
                }
            }
        }

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        HttpSession session = req.getSession();
        session.setAttribute( "data1", data1 );
        session.setAttribute( "data2", data2 );
        session.setAttribute( "series1", series1 );
        session.setAttribute( "categories1", categories1 );
        session.setAttribute( "series2", series2 );
        session.setAttribute( "categories2", categories2 );
        session.setAttribute( "chartTitle", chartTitle );
        session.setAttribute( "xAxisTitle", xAxis_Title );
        session.setAttribute( "yAxisTitle", yAxis_Title );
        
        //session.setAttribute( "surveyList", surveyList );

        statementManager.destroy();

        return SUCCESS;
    }// execute end

    public Double[][] getServiceValuesByPeriod()
    {
        Double[][] serviceValues = new Double[1][monthlyPeriods.size()];

        Double[][] numDataArray = new Double[1][monthlyPeriods.size()];
        Double[][] denumDataArray = new Double[1][monthlyPeriods.size()];
        
        int countForServiceList = 0;
        int countForPeriodList = 0;

        series1 = new String[1];
        // series2 = new String[1];
        categories1 = new String[monthlyPeriods.size()];
        categories2 = new String[monthlyPeriods.size()];

        List<String> dataValues = new ArrayList<String>();

        series1[countForServiceList] = selectedIndicator.getName();
        // series2[countForServiceList] = selectedIndicator.getName();

        yseriesList.add( selectedIndicator.getName() );

        Iterator<Period> periodListIterator = monthlyPeriods.iterator();
        countForPeriodList = 0;
        
        Double aggSurveyDataValue = 0.0;
        
        Double aggIndicatorNumValue = 0.0;
        Double aggIndicatorDenumValue = 0.0;
        
        while ( periodListIterator.hasNext() )
        {
            Period p = (Period) periodListIterator.next();

           // serviceValues[countForServiceList][countForPeriodList] = aggregationService.getAggregatedIndicatorValue(selectedIndicator, p.getStartDate(), p.getEndDate(), selectedOrgUnit );
            aggSurveyDataValue = aggregationService.getAggregatedIndicatorValue(selectedIndicator, p.getStartDate(), p.getEndDate(), selectedOrgUnit );
            
            if( aggSurveyDataValue == null ) aggSurveyDataValue = 0.0;
            
            serviceValues[countForServiceList][countForPeriodList] = aggSurveyDataValue;
            
            aggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( selectedIndicator, p.getStartDate(),p.getEndDate(), selectedOrgUnit );
            aggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( selectedIndicator, p.getStartDate(),p.getEndDate(), selectedOrgUnit );
            
            
            //for indicator value
            serviceValues[countForServiceList][countForPeriodList] = Math.round( serviceValues[countForServiceList][countForPeriodList] * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
            
            // for numenetor value
            numDataArray[countForServiceList][countForPeriodList] = aggIndicatorNumValue;
            numDataArray[countForServiceList][countForPeriodList] = Math.round( numDataArray[countForServiceList][countForPeriodList] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
            
            // for denum value
            denumDataArray[countForServiceList][countForPeriodList] = aggIndicatorDenumValue;
            denumDataArray[countForServiceList][countForPeriodList] = Math.round( denumDataArray[countForServiceList][countForPeriodList] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
            /*
            if ( serviceValues[countForServiceList][countForPeriodList] == -1 )
                serviceValues[countForServiceList][countForPeriodList] = 0.0;
             */
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
            categories1[countForPeriodList] = simpleDateFormat.format( p.getStartDate() );
            categories2[countForPeriodList] = simpleDateFormat.format( p.getStartDate() );

            dataValues.add( "" + serviceValues[countForServiceList][countForPeriodList] );

            countForPeriodList++;
        }// periodList loop end
        dataList.add( dataValues );
        countForServiceList++;

        return serviceValues;
    }// getServiceValues method end

}// class end
