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
package org.hisp.dhis.dataanalyser.action;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;
import org.hisp.dhis.surveydatavalue.SurveyDataValue;
import org.hisp.dhis.surveydatavalue.SurveyDataValueService;

import com.keypoint.PngEncoder;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ExportSurveyDataToExcelAction.java Dec 9, 2010 5:19:10 PM
 */
public class ExportSurveyDataToExcelAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    double[][] data1;

    double[][] numDataArray;
    
    double[][] denumDataArray;
    
    double[][] data2;

    String[] series1;

    String[] series2;

    String[] categories1;

    String[] categories2;
    
    List<Survey> surveyList;

    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String viewSummary;
    
    public void setViewSummary( String viewSummary )
    {
        this.viewSummary = viewSummary;
    }
    
    private String chartDisplayOption;

    public void setChartDisplayOption( String chartDisplayOption )
    {
        this.chartDisplayOption = chartDisplayOption;
    }
    
    private Integer selectedOrgUnitId;
    
    public void setSelectedOrgUnitId( Integer selectedOrgUnitId )
    {
        this.selectedOrgUnitId = selectedOrgUnitId;
    }

    private Integer selctedIndicatorId;

    public void setSelctedIndicatorId( Integer selctedIndicatorId )
    {
        this.selctedIndicatorId = selctedIndicatorId;
    }

    
    private OrganisationUnit selectedOrgUnit;

    private Indicator selectedIndicator;
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {   
        int tempCol1 = 0;
        int tempRow1 = 1;
        
        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );                        
        HttpSession session = req.getSession();
        BufferedImage chartImage = (BufferedImage) session.getAttribute("chartImage");
        PngEncoder encoder = new PngEncoder(chartImage, false, 0, 9);
        
        byte[] encoderBytes = encoder.pngEncode();
        Double[][] objData1 = (Double[][]) session.getAttribute( "data1" );
        Double[][] objData2 = (Double[][]) session.getAttribute( "data2" );
        Double[][] objnumData1 = (Double[][]) session.getAttribute( "numDataArray" );
        Double[][] objdenumData1 = (Double[][]) session.getAttribute( "denumDataArray" );
        
        String[] series1S = (String[]) session.getAttribute( "series1" );
        String[] series2S = (String[]) session.getAttribute( "series2" );
        String[] categories1S = (String[]) session.getAttribute( "categories1" );
        String[] categories2S = (String[]) session.getAttribute( "categories2" );
        
        initialzeAllLists( series1S, series2S, categories1S, categories2S );
        
        data1 = convertDoubleTodouble( objData1 );//Indicator value
        numDataArray = convertDoubleTodouble( objnumData1 );//Num data
        denumDataArray = convertDoubleTodouble( objdenumData1 );//Denum data
        
        data2 = convertDoubleTodouble( objData2 );//survey value
        
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( selectedOrgUnitId );
        selectedIndicator = indicatorService.getIndicator( selctedIndicatorId );

        String outputReportFile = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportFile );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportFile += File.separator + UUID.randomUUID().toString() + ".xls";
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File(outputReportFile) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "SurveyChartOutput", 0 );
        
        if(viewSummary.equals( "no" ))
        {
            WritableImage writableImage = new WritableImage(0,1,10,23,encoderBytes);
            sheet0.addImage( writableImage );
            tempRow1 = 24;
        }    
        else
        {
            tempRow1 = 0;
        }
        
        tempCol1 = 0;
        tempRow1++;
        WritableCellFormat wCellformat1 = new WritableCellFormat();                            
        wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat1.setAlignment( Alignment.CENTRE );
        wCellformat1.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat1.setWrap( true );
    
        WritableCellFormat wCellformat2 = new WritableCellFormat();                            
        wCellformat2.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat2.setAlignment( Alignment.CENTRE );
        wCellformat2.setVerticalAlignment( VerticalAlignment.TOP);
        wCellformat2.setBackground( Colour.GRAY_25 );                
        wCellformat2.setWrap( true );
        
        WritableFont wfobj2 = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wcf = new WritableCellFormat( wfobj2 );
        wcf.setBorder( Border.ALL, BorderLineStyle.THIN );
        wcf.setAlignment( Alignment.CENTRE );
        wcf.setVerticalAlignment( VerticalAlignment.CENTRE );
        wcf.setWrap( true );
        
        sheet0.addCell( new Label( tempCol1, tempRow1, "Indicator", wCellformat2) );
 
         //for survey value Display
         for(int i=0; i< series2.length; i++)
         {   
             sheet0.addCell( new Label( tempCol1+1, tempRow1, series2[i], wCellformat2) );
             tempCol1++;
         }
        
         sheet0.addCell( new Label( tempCol1+1, tempRow1, "", wCellformat2) );
         tempCol1++;

         //for time display
         for(int i=0; i< categories1.length; i++)
         {   
             sheet0.addCell( new Label( tempCol1+1, tempRow1, categories1[i], wCellformat2) );
             tempCol1++;
         }
         
         tempRow1 = tempRow1+1;
        
         int tempRowValue = 0;
         
         for(int j=0; j< series1.length; j++)
         {
             tempCol1 = 0;
             sheet0.mergeCells( tempCol1 , tempRow1, tempCol1, tempRow1+2 ); 
             sheet0.addCell( new Label( tempCol1, tempRow1, series1[j], wCellformat2) );
             
             int tempSurveyValueCol = 1;
             int temColValue = 0;
             
             for( int k=0; k< series2.length; k++ )
             {
                 tempCol1 = 0;
                 surveyList = new ArrayList<Survey>( surveyService.getSurveysByIndicator( selectedIndicator ) );
                 Survey survey = surveyList.get( k );
                 
                 Double tempSurveyDataValue = 0.0;
                 SurveyDataValue surveyDataValue = surveyDataValueService.getSurveyDataValue( selectedOrgUnit, survey, selectedIndicator );
                 
                 if ( surveyDataValue != null )
                 {
                     tempSurveyDataValue = Double.parseDouble( surveyDataValue.getValue() );
                 }
                 else
                 {
                     tempSurveyDataValue = 0.0;
                 }
                 sheet0.addCell( new Number( tempSurveyValueCol, tempRow1, tempSurveyDataValue, wCellformat1 ) );
                 
                 tempSurveyValueCol++;
                 temColValue = tempSurveyValueCol;
             }

             int tempNumCol = temColValue;
             
             sheet0.addCell( new Label( tempNumCol, tempRow1, "Num", wCellformat2) );
             tempNumCol =  tempNumCol+1;
             
             for( int k=0; k< categories1.length; k++ )
             {
                 sheet0.addCell( new Number( tempNumCol, tempRow1, numDataArray[j][k], wCellformat1 ) );
                 tempNumCol++;
             }

             int tempDenumCol = temColValue;
             
             sheet0.addCell( new Label( tempDenumCol, tempRow1+1, "Den", wCellformat2) );
             
             tempDenumCol = tempDenumCol+1;
             for( int k=0; k<categories1.length; k++ )
             { 
                sheet0.addCell( new Number( tempDenumCol, tempRow1+1, denumDataArray[j][k], wCellformat1 ) );
                tempDenumCol++;
            }
           
             int tempValueCol = temColValue;
           
             sheet0.addCell( new Label( tempValueCol, tempRow1+2, "Val", wCellformat2) );
             tempValueCol = tempValueCol+1;
             
             for( int k=0; k< categories1.length; k++ )
             { 
                 sheet0.addCell( new Number( tempValueCol, tempRow1+2, data1[j][k], wcf ) );
                 tempValueCol++;
             }
            
            tempRow1 = tempRow1+3;
            tempRow1++;
            tempRowValue = tempRow1++;
         }
         tempRow1 = tempRowValue;
         
         tempRow1 = tempRow1+2;
         sheet0.addCell( new Label( tempCol1, tempRow1, "Indicators Names", wCellformat2) );
         
         sheet0.mergeCells( tempCol1+1 , tempRow1, tempCol1 + 2, tempRow1 );
         sheet0.addCell( new Label( tempCol1+1, tempRow1, "Formula", wCellformat2) );
         
         sheet0.addCell( new Label( tempCol1+3, tempRow1, "Numerator DataElements", wCellformat2) );
         sheet0.addCell( new Label( tempCol1+4, tempRow1, "Denominator DataElements", wCellformat2) );
         
         tempRow1 = tempRow1+1;
         
         for(int j=0; j< series1.length; j++)
         {
             Indicator indicator =  indicatorService.getIndicatorByName( series1[j] );
             
             sheet0.addCell( new Label( tempCol1, tempRow1, indicator.getName(), wCellformat1 ) );
             String formula = indicator.getNumeratorDescription() + "/" +  indicator.getDenominatorDescription();
             
             sheet0.addCell( new Label( tempCol1+1, tempRow1,formula , wCellformat1 ) );
             String factor = "X" + indicator.getIndicatorType().getFactor();
             
             sheet0.addCell( new Label( tempCol1+2, tempRow1, factor, wCellformat1) );
             sheet0.addCell( new Label( tempCol1+3, tempRow1, expressionService.getExpressionDescription( indicator.getNumerator()), wCellformat1 ) );
             sheet0.addCell( new Label( tempCol1+4, tempRow1, expressionService.getExpressionDescription( indicator.getDenominator() ), wCellformat1 ) );
             
             tempRow1++;
         }
   
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "Survey Chart Output.xls";
                
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );


        return SUCCESS;
    }
    
    public void initialzeAllLists(String[]series1S, String[] series2S, String[] categories1S, String[] categories2S)
    {
        int i;
        series1 = new String[series1S.length];
        series2 = new String[series2S.length];
        categories1 = new String[categories1S.length];
        categories2 = new String[categories2S.length];
        
        for(i = 0; i < series1S.length; i++)
        {
                series1[i] = series1S[i];
        }

        for(i = 0; i < series2S.length; i++)
        {
                series2[i] = series2S[i];
        }
        
        for(i = 0; i < categories1S.length; i++)
        {
                categories1[i] = categories1S[i];
        }
        
        for(i = 0; i < categories2S.length; i++)
        {
                categories2[i] = categories2S[i];
        }
        
    }
    
    public double[][] convertDoubleTodouble( Double[][] objData )
    {
        double[][] data = new double[objData.length][objData[0].length];
        
        for ( int i = 0; i < objData.length; i++ )
        {
            for ( int j = 0; j < objData[0].length; j++ )
            {
                data[i][j] = objData[i][j].doubleValue();
            }
        }

        return data;
    }// convertDoubleTodouble end

}

