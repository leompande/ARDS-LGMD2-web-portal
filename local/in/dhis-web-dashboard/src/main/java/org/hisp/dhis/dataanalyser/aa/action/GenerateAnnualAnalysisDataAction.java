package org.hisp.dhis.dataanalyser.aa.action;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.velocity.tools.generic.ListTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class GenerateAnnualAnalysisDataAction
        implements Action
{

    /* Dependencies */
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService)
    {
        this.organisationUnitService = organisationUnitService;
    }
    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService)
    {
        this.periodService = periodService;
    }
    private IndicatorService indicatorService;

    public void setIndicatorService(IndicatorService indicatorService)
    {
        this.indicatorService = indicatorService;
    }
    private DataElementService dataElementService;

    public void setDataElementService(DataElementService dataElementService)
    {
        this.dataElementService = dataElementService;
    }
    private DashBoardService dashBoardService;

    public void setDashBoardService(DashBoardService dashBoardService)
    {
        this.dashBoardService = dashBoardService;
    }
    private AggregationService aggregationService;

    public void setAggregationService(AggregationService aggregationService)
    {
        this.aggregationService = aggregationService;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    
    /* Parameters */
    private OrganisationUnit selectedOrgUnit;
    private DataElement selectedDataElement;
    
    private Indicator selectedIndicator;
    
    public Indicator getSelectedIndicator()
    {
        return selectedIndicator;
    }
    private PeriodType monthlyPeriodType;
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

    public String getChartTitle() {
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
    
 /* 
    List<List<Double>> numDataList;
    
    public List<List<Double>> getNumDataList()
    {
        return numDataList;
    }
   
    List<List<Double>> denumDataList;
    
    public List<List<Double>> getDenumDataList()
    {
        return denumDataList;
    }
*/    
    Double numServiceValues[][];
    
    public Double[][] getNumServiceValues()
    {
        return numServiceValues;
    }

    Double denumServiceValues[][];
    
    public Double[][] getDenumServiceValues()
    {
        return denumServiceValues;
    }
    
    List<List<Double>> dataList;

    public List<List<Double>> getDataList() {
        return dataList;
    }
   
    List<String> xseriesList;

    public List<String> getXseriesList() {
        return xseriesList;
    }
    List<String> yseriesList;

    public List<String> getYseriesList() {
        return yseriesList;
    }
    private DecimalFormat decimalFormat;

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    /* Input Parameters */
    private int availableDataElements;
    private int availableIndicators;
    private List<String> annualPeriodsListCB;
    private List<String> monthlyPeriodsListCB;
    private int ouIDTB;
   
    private String riRadio;

    public String getRiRadio()
    {
        return riRadio;
    }

    public void setRiRadio(String riRadio)
    {
        this.riRadio = riRadio;
    }

    public void setAnnualPeriodsListCB(List<String> annualPeriodsListCB) {
        this.annualPeriodsListCB = annualPeriodsListCB;
    }

    public void setAvailableDataElements(int availableDataElements) {
        this.availableDataElements = availableDataElements;
    }

    public void setAvailableIndicators(int availableIndicators) {
        this.availableIndicators = availableIndicators;
    }

    public void setMonthlyPeriodsListCB(List<String> monthlyPeriodsListCB) {
        this.monthlyPeriodsListCB = monthlyPeriodsListCB;
    }

    public void setOuIDTB(int ouIDTB) {
        this.ouIDTB = ouIDTB;
    }

    ListTool listTool;

    public ListTool getListTool()
    {
        return listTool;
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
    
    private Integer selectedIndicatorId;
    
    public Integer getSelectedIndicatorId()
    {
        return selectedIndicatorId;
    }
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception 
    {
        dataList = new ArrayList<List<Double>>();
        //numDataList = new ArrayList<List<Double>>();
        //denumDataList = new ArrayList<List<Double>>();
        
        xseriesList = new ArrayList<String>();
        yseriesList = new ArrayList<String>();

        decimalFormat = new DecimalFormat("0.0");
        
        listTool = new ListTool();
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit(ouIDTB);
        chartTitle = "Facility : " + selectedOrgUnit.getShortName();
        
        numDataElement = new String();
        denumDataElement = new String();
        
        System.out.println( "availableIndicators= " + availableIndicators + ",,,availableDataElements= " + availableDataElements + "ouIDTB= " + ouIDTB );
        System.out.println( "annualPeriodsListCB= " + annualPeriodsListCB + ",,,,---monthlyPeriodsListCB= " + monthlyPeriodsListCB );
        System.out.println( "riRadio= " + riRadio );
        
        
        
        //selectedIndicator = new Indicator();
        //selectedIndicator = indicatorService.getIndicator( availableIndicators );
        //selectedIndicatorId = selectedIndicator.getId();
        
        // Service Related Info
        if ( riRadio.equals("indicatorsRadio")) 
        {
            selectedIndicator = new Indicator();
            selectedIndicator = indicatorService.getIndicator( availableIndicators );
            chartTitle += "\n Indicator : " + selectedIndicator.getName();
            selectedIndicatorId = selectedIndicator.getId();
            
            // for numeratorDataElement,denominatorDataElement
            numDataElement = expressionService.getExpressionDescription( selectedIndicator.getNumerator());
            denumDataElement = expressionService.getExpressionDescription( selectedIndicator.getDenominator());
            
        } 
        else 
        {
            selectedDataElement = new DataElement();
            selectedDataElement = dataElementService.getDataElement(availableDataElements);
            if (selectedDataElement.getAlternativeName() != null) 
            {
                chartTitle += "\n DataElement : " + selectedDataElement.getAlternativeName();
            } 
            else {
                chartTitle += "\n DataElement : " + selectedDataElement.getName();
            }
        }

        // Period Related Info
        monthlyPeriodType = PeriodType.getByNameIgnoreCase( "monthly" );

        data1 = getServiceValuesByPeriod();
        xAxis_Title = "Month";
        yAxis_Title = "Facilty";

        int count1 = 0;
        while (count1 != categories1.length) 
        {
            xseriesList.add(categories1[count1]);
            count1++;
        }

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);

        HttpSession session = req.getSession();
        session.setAttribute("data1", data1);
        session.setAttribute("numServiceValues", numServiceValues);
        session.setAttribute("denumServiceValues", denumServiceValues);
        
        session.setAttribute("data2", data2);
        session.setAttribute("series1", series1);
        session.setAttribute("categories1", categories1);
        session.setAttribute("series2", series2);
        session.setAttribute("categories2", categories2);
        session.setAttribute("chartTitle", chartTitle);
        session.setAttribute("xAxisTitle", xAxis_Title);
        session.setAttribute("yAxisTitle", yAxis_Title);

        return SUCCESS;
    }// execute end

    /**
     * Returns the values for selected years by month wise for ex:- the months
     * are jan,feb,mar and the years are 2006 and 2007 then it returns the
     * values for 2006 - jan, feb, mar and 2007 - jan, feb, mar for the selected
     * orgunit and selected service
     */
    public Double[][] getServiceValuesByPeriod() 
    {
       // DecimalFormat decFormat = new DecimalFormat("0.0");

        Double[][] serviceValues = new Double[annualPeriodsListCB.size()][monthlyPeriodsListCB.size()];
        
        numServiceValues = new Double[annualPeriodsListCB.size()][monthlyPeriodsListCB.size()];
        denumServiceValues = new Double[annualPeriodsListCB.size()][monthlyPeriodsListCB.size()];
        
        data2 = new Double[annualPeriodsListCB.size()][monthlyPeriodsListCB.size()];
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        int count1 = 0;
        int count2 = 0;

        Period p = new Period();

        series1 = new String[annualPeriodsListCB.size()];
        series2 = new String[annualPeriodsListCB.size()];
        categories1 = new String[monthlyPeriodsListCB.size()];
        categories2 = new String[monthlyPeriodsListCB.size()];

        for ( String tmp : annualPeriodsListCB )
        {
            int tempYear = Integer.parseInt(tmp);

            List<Double> dataValues = new ArrayList<Double>();
            //List<Double> numDataValue = new ArrayList<Double>();
            //List<Double> denumDataValue = new ArrayList<Double>();
            
            //series1[count1] = "" + tempYear + "-" + (tempYear + 1);
            series1[count1] = "" + tempYear;
            series2[count1] = " ";
            //yseriesList.add("" + tempYear + "-" + (tempYear + 1));
            yseriesList.add("" + tempYear );

            count2 = 0;
            for ( String s : monthlyPeriodsListCB )
            {
                int tempMonth = Integer.parseInt(s);

                p = dashBoardService.getPeriodByMonth(tempMonth, tempYear, monthlyPeriodType);
                if (p == null) 
                {
                    serviceValues[count1][count2] = 0.0;
                    numServiceValues[count1][count2] = 0.0;
                    denumServiceValues[count1][count2] = 0.0;
                    
                    System.out.println("PERIOD IS NULL for " + tempMonth + " : " + tempYear);
                } 
                else 
                {
                    if (riRadio.equals("indicatorsRadio"))
                    {
                        serviceValues[count1][count2] = aggregationService.getAggregatedIndicatorValue( selectedIndicator, p.getStartDate(), p.getEndDate(), selectedOrgUnit);
                        //System.out.println("indicators Radio is Selected " + serviceValues[count1][count2]);
                       
                        numServiceValues[count1][count2] = aggregationService.getAggregatedNumeratorValue( selectedIndicator, p.getStartDate(),p.getEndDate(), selectedOrgUnit );
                        denumServiceValues[count1][count2] = aggregationService.getAggregatedDenominatorValue( selectedIndicator, p.getStartDate(),p.getEndDate(), selectedOrgUnit );
                        
                        // for numenetor value
                        numServiceValues[count1][count2] = numServiceValues[count1][count2];
                        numServiceValues[count1][count2] = Math.round( numServiceValues[count1][count2] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                        
                        // for denum value
                        denumServiceValues[count1][count2] = denumServiceValues[count1][count2];
                        denumServiceValues[count1][count2] = Math.round( denumServiceValues[count1][count2] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                        
                    } 
                    else 
                    {
                        Double aggDataValue = 0.0;
                        serviceValues[count1][count2] = 0.0;
                        DataElementCategoryCombo dataElementCategoryCombo = selectedDataElement.getCategoryCombo();

                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                dataElementCategoryCombo.getOptionCombos());

                        Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                        while (optionComboIterator.hasNext()) 
                        {
                            DataElementCategoryOptionCombo decoc = (DataElementCategoryOptionCombo) optionComboIterator.next();

                            aggDataValue = aggregationService.getAggregatedDataValue(selectedDataElement, decoc, p.getStartDate(), p.getEndDate(), selectedOrgUnit);
                            //System.out.println("selectedDataElement = "+selectedDataElement + " "+ p.getStartDate()+ " "+ p.getEndDate());
                            if (aggDataValue == null) 
                            {
                                aggDataValue = 0.0;
                            }
                            serviceValues[count1][count2] += aggDataValue;
                            //System.out.println("aggDataValue = "+aggDataValue);
                        }
                        //System.out.println("VALUE : "+serviceValues[count1][count2]);
                    }
                    //if ( serviceValues[count1][count2] != null )
                    //{
                    if ( serviceValues[count1][count2] == null )
                    {
                        serviceValues[count1][count2] = 0.0;
                    }
                    serviceValues[count1][count2] = Math.round(serviceValues[count1][count2] * Math.pow(10, 1)) / Math.pow(10, 1);
                    //}

                }
                if ( serviceValues[count1][count2] == null )
                {
                    serviceValues[count1][count2] = 0.0;
                }
                
                if ( numServiceValues[count1][count2] == null )
                {
                    numServiceValues[count1][count2] = 0.0;
                }
                
                if ( denumServiceValues[count1][count2] == null )
                {
                    denumServiceValues[count1][count2] = 0.0;
                }
                categories1[count2] = monthNames[tempMonth];
                categories2[count2] = monthNames[tempMonth];
                data2[count1][count2] = 0.0;
                
                dataValues.add(serviceValues[count1][count2]);
               // numDataValue.add( numServiceValues[count1][count2] );
                //denumDataValue.add( denumServiceValues[count1][count2] ); 
                
                count2++;
            }// Monthly PeriodList loop end
            dataList.add(dataValues);
           // numDataList.add( numDataValue );
           // denumDataList.add( denumDataValue );
            
            count1++;
        } // Annual PeriodList loop end
        return serviceValues;
    }// getServiceValues method end

    public Period getPeriodByMonth(int month, int year, PeriodType periodType) 
    {
        int monthDays[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if (periodType.getName().equalsIgnoreCase("Monthly")) 
        {
            cal.set(year, month, 1, 0, 0, 0);
            if (year % 4 == 0 && month == 1) 
            {
                cal.set(Calendar.DAY_OF_MONTH, monthDays[month] + 1);
            } 
            else 
            {
                cal.set(Calendar.DAY_OF_MONTH, monthDays[month]);
            }
        } 
        else if (periodType.getName().equalsIgnoreCase("Yearly")) 
        {
            cal.set(year, Calendar.DECEMBER, 31);
        }

        Date lastDay = new Date(cal.getTimeInMillis());
        //System.out.println( lastDay.toString() );

        Period newPeriod = periodService.getPeriod(firstDay, lastDay, periodType);

        return newPeriod;
    }

    /*
     * Returns the PeriodType Object based on the Period Type Name For ex:- if
     * we pass name as Monthly then it returns the PeriodType Object for Monthly
     * PeriodType If there is no such PeriodType returns null
     */
    public PeriodType getPeriodTypeObject( String periodTypeName ) 
    {
        Collection<PeriodType> periodTypes = periodService.getAllPeriodTypes();
        PeriodType periodType = null;
        Iterator<PeriodType> iter = periodTypes.iterator();
        while ( iter.hasNext() ) 
        {
            PeriodType tempPeriodType = ( PeriodType ) iter.next();
            if ( tempPeriodType.getName().trim().equalsIgnoreCase( periodTypeName ) ) 
            {
                periodType = tempPeriodType;
                break;
            }
        }
        if ( periodType == null ) 
        {
            return null;
        }

        return periodType;
    }
}// class end

