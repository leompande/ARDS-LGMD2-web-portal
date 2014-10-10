/*
 * Copyright (c) 2004-2012, University of Oslo
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
package org.hisp.dhis.dataanalyser.sa.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.apache.velocity.tools.generic.ListTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataanalyser.util.SurveyChartResult;
import org.hisp.dhis.expression.ExpressionService;
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

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateChartSurveyAction.java Dec 10, 2010 11:33:19 AM
 */
public class GenerateChartSurveyAction implements Action
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
   
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------
   
    private HttpSession session;

    public HttpSession getSession()
    {
        return session;
    }
    
    private SurveyChartResult surveyChartResult;
    
    public SurveyChartResult getSurveyChartResult()
    {
        return surveyChartResult;
    }

    private OrganisationUnit selectedOrgUnit;

    private Indicator selectedIndicator;

    public Indicator getSelectedIndicator()
    {
        return selectedIndicator;
    }

    private String[] series1;

    public String[] getSeries1()
    {
        return series1;
    }
    
    private String[] series2;

    public String[] getSeries2()
    {
        return series2;
    }

    private String[] categories1;

    public String[] getCategories1()
    {
        return categories1;
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
    
    private List<Period> monthlyPeriods;
    
    ListTool listTool;

    public ListTool getListTool()
    {
        return listTool;
    }
    
    private List<SurveyDataValue> surveyDataValueList;

    public List<SurveyDataValue> getSurveyDataValueList()
    {
        return surveyDataValueList;
    }
    
    private Integer selectedIndicatorId;
    
    public Integer getSelectedIndicatorId()
    {
        return selectedIndicatorId;
    }

    private Integer selectedOrgId;
    
    public Integer getSelectedOrgId()
    {
        return selectedOrgId;
    }
    
    private String numDataElement;
    
    public String getNumDataElement()
    {
        return numDataElement;
    }
    
    private String denumDataElement;
    
    public String getDenumDataElement()
    {
        return denumDataElement;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------    
    
    public String execute()throws Exception
    {
        statementManager.initialise();
        
        listTool = new ListTool();

      //  dataList = new ArrayList<List<String>>();
      //  xseriesList = new ArrayList<String>();
        yseriesList = new ArrayList<String>();
        
        System.out.println( "inside GenerateChartSurveyAction" );
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        chartTitle = "Facility : " + selectedOrgUnit.getShortName();

        // Service Related Info
        selectedIndicator = new Indicator();
        selectedIndicator = indicatorService.getIndicator( availableIndicators );
        chartTitle += "\n Indicator : " + selectedIndicator.getName();
        
        // for numeratorDataElement,denominatorDataElement
        numDataElement = new String();
        denumDataElement = new String();
        numDataElement = expressionService.getExpressionDescription( selectedIndicator.getNumerator());
        denumDataElement = expressionService.getExpressionDescription( selectedIndicator.getDenominator());
        
        
        
        selectedIndicatorId = selectedIndicator.getId();
        selectedOrgId = selectedOrgUnit.getId();
        
        surveyList = new ArrayList<Survey>( surveyService.getSurveysByIndicator( selectedIndicator ) );
        
        System.out.println( "availableIndicators= " + availableIndicators + ",,,ouIDTB= " + ouIDTB + "----sDateLB= " + sDateLB );
        System.out.println( "eDateLB= " + eDateLB  );
        
        
       // Map<OrganisationUnitGroup, List<OrganisationUnit>> orgUnitGroupMap = new HashMap<OrganisationUnitGroup, List<OrganisationUnit>>();
       // Map<Survey, Double> surveyValues = new HashMap<Survey, Double>(); 
        //surveyDataValueList = new ArrayList<SurveyDataValue>();

        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( new MonthlyPeriodType(),startPeriod.getStartDate(), endPeriod.getEndDate() ) );
       
        System.out.println( "Chart Generation Start Time is : \t" + new Date() );
        surveyChartResult = generateChartSurveyData( monthlyPeriods, selectedIndicator,selectedOrgUnit );
        
       // data1 = getServiceValuesByPeriod();
       // xAxis_Title = "Period";
       // yAxis_Title = "Indicator";
        
        
        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        session = req.getSession();

        session.setAttribute( "data1", surveyChartResult.getData() );
        session.setAttribute( "data2", surveyChartResult.getData2() );
        session.setAttribute( "numDataArray", surveyChartResult.getNumDataArray() );
        session.setAttribute( "denumDataArray", surveyChartResult.getDenumDataArray() );
        session.setAttribute( "series1", surveyChartResult.getSeries() );
        session.setAttribute( "series2", surveyChartResult.getSeries2() );
        session.setAttribute( "categories1", surveyChartResult.getCategories() );
        session.setAttribute( "chartTitle", surveyChartResult.getChartTitle() );
        session.setAttribute( "xAxisTitle", surveyChartResult.getXAxis_Title() );
        session.setAttribute( "yAxisTitle", surveyChartResult.getYAxis_Title() );
        session.setAttribute( "categories2", surveyChartResult.getCategories2() );
        
        statementManager.destroy();
        System.out.println( "Chart Generation End Time is : \t" + new Date() );

        return SUCCESS;
    }
    
    public SurveyChartResult generateChartSurveyData( List<Period> monthlyPeriods,Indicator selectedIndicator, OrganisationUnit selectedOrgUnit )
        throws Exception
    {

        SurveyChartResult surveyChartResult;
        
        //Double[][] serviceValues = new Double[1][monthlyPeriods.size()];

       // Double[][] numDataArray = new Double[1][monthlyPeriods.size()];
       // Double[][] denumDataArray = new Double[1][monthlyPeriods.size()];
        
        String[] series = new String[1];
        String[] categories = new String[monthlyPeriods.size()];
        Double[][] data = new Double[1][monthlyPeriods.size()];
        
        Double[][] data2 = new Double[surveyList.size()][monthlyPeriods.size()];
        
       // Double[][] data2 = new Double[surveyList.size()][surveyList.size()];
        Double[][] numDataArray = new Double[1][monthlyPeriods.size()];
        Double[][] denumDataArray = new Double[1][monthlyPeriods.size()];
        
        categories2 = new String[monthlyPeriods.size()];
/*        
        Map<String, String> surveyValues = new HashMap<String, String>(); 
        
        surveyList = new ArrayList<Survey>( surveyService.getSurveysByIndicator( selectedIndicator ) );
       
        String surveyDataValue;
        for ( Survey survey : surveyList )
        {
         //   Survey survey = surveyService.getSurveyByName( surveyId );
            
            SurveyDataValue tempSurveyDataValue = surveyDataValueService.getSurveyDataValue( selectedOrgUnit, survey, selectedIndicator );
            if ( tempSurveyDataValue.getValue() == null )
            {
                surveyDataValue = "";
            }
            else 
            {
                surveyDataValue = tempSurveyDataValue.getValue();
            }
            surveyValues.put( survey.getName(), surveyDataValue );
            // selOUGroupMemberList.addAll( selectedOUGroupMemberList );
        }
*/
        // Map<Integer, List<Double>> numData = new HashMap<Integer,
        // List<Double>>();
        // Map<Integer, List<Double>> denumData = new HashMap<Integer,
        // List<Double>>();

        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
        chartTitle += "\n Indicator : " + selectedIndicator.getName();
        String xAxis_Title = "Period";
        String yAxis_Title = "Indicator";
        
        surveyList = new ArrayList<Survey>( surveyService.getSurveysByIndicator( selectedIndicator ) );
        surveyDataValueList = new ArrayList<SurveyDataValue>();
        
        data2 = new Double[surveyList.size()][monthlyPeriods.size()];
        
       // data2 = new Double[surveyList.size()][surveyList.size()];
        series2 = new String[surveyList.size()];
        
        for ( int i = 0; i < data2.length; i++ )
        {
            Survey survey = surveyList.get( i );
            SurveyDataValue surveyDataValue = surveyDataValueService.getSurveyDataValue( selectedOrgUnit, survey, selectedIndicator );

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
                //System.out.println( data2[i][j]);
            }
            
        }
        
        int countForServiceList = 0;
        int countForPeriodList = 0;
        
        Iterator<Period> periodListIterator = monthlyPeriods.iterator();
       
        series[countForServiceList] = selectedIndicator.getName();
        yseriesList.add( selectedIndicator.getName() );
        
        Double aggSurveyIndicatorDataValue = 0.0;
        Double aggIndicatorNumValue = 0.0;
        Double aggIndicatorDenumValue = 0.0;
        
        while ( periodListIterator.hasNext() )
        {
            Period p = (Period) periodListIterator.next();
            aggSurveyIndicatorDataValue = aggregationService.getAggregatedIndicatorValue(selectedIndicator, p.getStartDate(), p.getEndDate(), selectedOrgUnit );
            
            if( aggSurveyIndicatorDataValue == null )
            {
                aggSurveyIndicatorDataValue = 0.0;
            }
            
            data[countForServiceList][countForPeriodList] = aggSurveyIndicatorDataValue;
            
            //for indicator value
            data[countForServiceList][countForPeriodList] = Math.round( data[countForServiceList][countForPeriodList] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
            
            aggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( selectedIndicator, p.getStartDate(),p.getEndDate(), selectedOrgUnit );
            aggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( selectedIndicator, p.getStartDate(),p.getEndDate(), selectedOrgUnit );
            
            // for numenetor value
            numDataArray[countForServiceList][countForPeriodList] = aggIndicatorNumValue;
            numDataArray[countForServiceList][countForPeriodList] = Math.round( numDataArray[countForServiceList][countForPeriodList] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
            
            // for denum value
            denumDataArray[countForServiceList][countForPeriodList] = aggIndicatorDenumValue;
            denumDataArray[countForServiceList][countForPeriodList] = Math.round( denumDataArray[countForServiceList][countForPeriodList] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
            
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
            categories[countForPeriodList] = simpleDateFormat.format( p.getStartDate() );
            categories2[countForPeriodList] = simpleDateFormat.format( p.getStartDate() );
            
            countForPeriodList++;
        }  
        countForServiceList++;


        surveyChartResult = new SurveyChartResult( series, series2,categories, categories2, data, data2, numDataArray, denumDataArray, chartTitle, xAxis_Title, yAxis_Title );
        return surveyChartResult;

    }

    
    
}
