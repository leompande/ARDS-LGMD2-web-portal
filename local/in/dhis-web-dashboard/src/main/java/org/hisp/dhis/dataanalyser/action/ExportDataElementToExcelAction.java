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
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;

import com.keypoint.PngEncoder;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ExportDataElementToExcelAction.java Oct 29, 2010 1:59:14 PM
 */
public class ExportDataElementToExcelAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    double[][] data1;

    String[] series1;

    String[] categories1;

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
        
        String[] series1S = (String[]) session.getAttribute( "series1" );
        String[] categories1S = (String[]) session.getAttribute( "categories1" );
                        
        initialzeAllLists(series1S, categories1S );
        
        data1 = convertDoubleTodouble( objData1 );
        
        if(chartDisplayOption == null || chartDisplayOption.equalsIgnoreCase("none")) { }
        else if(chartDisplayOption.equalsIgnoreCase("ascend")) { sortByAscending(); }
        else if(chartDisplayOption.equalsIgnoreCase("desend")) { sortByDesscending(); }
        else if(chartDisplayOption.equalsIgnoreCase("alphabet")) { sortByAlphabet(); }          
                
        String outputReportFile = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportFile );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportFile += File.separator + UUID.randomUUID().toString() + ".xls";
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File(outputReportFile) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "DataElementChartOutput", 0 );
        
        if(viewSummary.equals( "no" ))
        {
            WritableImage writableImage = new WritableImage(0,1,10,23,encoderBytes);
            sheet0.addImage( writableImage );
            tempRow1 = 24;
        }    
        else
        {
            tempRow1 -= objData1.length;
        }

        int count1 = 0;
        int count2 = 0;
        int flag1 = 0;
        while(count1 <= categories1.length)
        {
            for(int j=0;j<data1.length;j++)
            {            
                tempCol1 = 1;
                tempRow1++;
                WritableCellFormat wCellformat1 = new WritableCellFormat();                            
                wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
                wCellformat1.setWrap( true );

                WritableCellFormat wCellformat2 = new WritableCellFormat();                            
                wCellformat2.setBorder( Border.ALL, BorderLineStyle.THIN );
                wCellformat2.setAlignment( Alignment.CENTRE );
                wCellformat2.setBackground( Colour.GRAY_25 );                
                wCellformat2.setWrap( true );

                
                WritableCell cell1;
                CellFormat cellFormat1;
                            
                for(int k=count2;k<count1;k++)
                {
                    if(k==count2 && j==0)
                    {                                       
                        tempCol1 = 0;
                        tempRow1++;
                        cell1 = sheet0.getWritableCell(tempCol1, tempRow1);
                        cellFormat1 = cell1.getCellFormat();


                        if (cell1.getType() == CellType.LABEL)
                        {
                            Label l = (Label) cell1;
                            l.setString("Service");
                            l.setCellFormat( cellFormat1 );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempCol1, tempRow1, "DataElements", wCellformat2) );
                        }
                        tempCol1++;
                    
                        for(int i=count2; i< count1; i++)
                        {                        
                            cell1 = sheet0.getWritableCell(tempCol1, tempRow1);
                            cellFormat1 = cell1.getCellFormat();
                            if (cell1.getType() == CellType.LABEL)
                            {
                                Label l = (Label) cell1;
                                l.setString(categories1[i]);
                                l.setCellFormat( cellFormat1 );
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempCol1, tempRow1, categories1[i], wCellformat2) );
                            }
                            tempCol1++;
                        }
                        tempRow1++;
                        tempCol1 = 1;
                    }
                
                
                    if(k==count2)
                    {
                        tempCol1 = 0;
                        cell1 = sheet0.getWritableCell(tempCol1, tempRow1);
                        cellFormat1 = cell1.getCellFormat();

                        if (cell1.getType() == CellType.LABEL)
                        {
                            Label l = (Label) cell1;
                            l.setString(series1[j]);
                            l.setCellFormat( cellFormat1 );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempCol1, tempRow1, series1[j], wCellformat2) );
                        }
                        tempCol1++;
                    }
                    cell1 = sheet0.getWritableCell(tempCol1, tempRow1);
                    cellFormat1 = cell1.getCellFormat();

                    if (cell1.getType() == CellType.LABEL)
                    {
                        Label l = (Label) cell1;
                        l.setString(""+data1[j][k]);
                        l.setCellFormat( cellFormat1 );
                    }
                    else
                    {
                        sheet0.addCell( new Number( tempCol1, tempRow1, data1[j][k], wCellformat1 ) );
                    }
                    tempCol1++;                
                }
            }
            if(flag1 == 1) break;
            count2 = count1;
            if( (count1+10 > categories1.length) && (categories1.length - count1 <= 10))
            {
                count1 += categories1.length - count1;
                flag1 = 1;
            }
            else
            {
                count1 += 10;
            }
        } 
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "DataElement Chart Output.xls";
                
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );


        return SUCCESS;
    }
    
    
    public void initialzeAllLists(String[]series1S, String[] categories1S)
    {
        int i;
        series1 = new String[series1S.length];
        categories1 = new String[categories1S.length];
        
        for(i = 0; i < series1S.length; i++)
        {
                series1[i] = series1S[i];
        }
        for(i = 0; i < categories1S.length; i++)
        {
                categories1[i] = categories1S[i];
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

    public void sortByAscending()
    {
        for(int i=0; i < categories1.length-1 ; i++)
        {
            for(int j=0; j < categories1.length-1-i; j++)
            {
                if(data1[0][j] > data1[0][j+1])
                {
                    for(int k=0; k<series1.length; k++)
                    {
                        double temp1 = data1[k][j];
                        data1[k][j] = data1[k][j+1];
                        data1[k][j+1] = temp1;                                          
                    }
                        
                    String temp2 = categories1[j];
                    categories1[j] = categories1[j+1];
                    categories1[j+1] = temp2;
                }
            }
        }                
    }

    public void sortByDesscending()
    {
        for(int i=0; i < categories1.length-1 ; i++)
        {
            for(int j=0; j < categories1.length-1-i; j++)
            {
                if(data1[0][j] < data1[0][j+1])
                {
                    for(int k=0; k<series1.length; k++)
                    {
                        double temp1 = data1[k][j];
                        data1[k][j] = data1[k][j+1];
                        data1[k][j+1] = temp1;                                          
                    }
                        
                    String temp2 = categories1[j];
                    categories1[j] = categories1[j+1];
                    categories1[j+1] = temp2;
                }
            }
        }
    }   
    
    public void sortByAlphabet()
    {
        for(int i=0; i < categories1.length-1 ; i++)
        {
            for(int j=0; j < categories1.length-1-i; j++)
            {
                if(categories1[j].compareToIgnoreCase(categories1[j+1]) > 0)
                {
                    for(int k=0; k<series1.length; k++)
                    {
                        double temp1 = data1[k][j];
                        data1[k][j] = data1[k][j+1];
                        data1[k][j+1] = temp1;                                          
                    }
                        
                    String temp2 = categories1[j];
                    categories1[j] = categories1[j+1];
                    categories1[j+1] = temp2;
                }
            }
        }
    }

}
