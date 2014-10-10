package org.hisp.dhis.dataanalyser.action;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
import org.hisp.dhis.survey.Survey;

import com.keypoint.PngEncoder;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class ExportTargetDataToExcelAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
        
        String[] series1S = (String[]) session.getAttribute( "series1" );
        String[] series2S = (String[]) session.getAttribute( "series2" );
        String[] categories1S = (String[]) session.getAttribute( "categories1" );
        String[] categories2S = (String[]) session.getAttribute( "categories2" );
        
        initialzeAllLists( series1S, series2S, categories1S, categories2S );
        
        data1 = convertDoubleTodouble( objData1 );
        
        data2 = convertDoubleTodouble( objData2 );
        
        String outputReportFile = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportFile );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportFile += File.separator + UUID.randomUUID().toString() + ".xls";
               
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File(outputReportFile) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TargetChartOutput", 0 );
        
        WritableImage writableImage = new WritableImage(0,1,10,23,encoderBytes);
        sheet0.addImage( writableImage );
        tempRow1 = 24;
        
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
        
        sheet0.addCell( new Label( tempCol1, tempRow1, "DataElement", wCellformat2) );
 
         for(int i=0; i< categories1.length; i++)
         {   
             sheet0.addCell( new Label( tempCol1+1, tempRow1, categories1[i], wCellformat2) );
             tempCol1++;
         }
         
         tempRow1 = tempRow1+1;
        
         for(int j=0; j< series1.length; j++)
         {
             tempCol1 = 0;
             sheet0.addCell( new Label( tempCol1, tempRow1, series1[j], wCellformat2) );
             
             int temColValue = 0;
           
             int tempValueCol = temColValue;
           
             tempValueCol = tempValueCol+1;
             
             for( int k=0; k< categories1.length; k++ )
             { 
                 sheet0.addCell( new Number( tempValueCol, tempRow1, data1[j][k], wcf ) );
                 tempValueCol++;
             }
            tempRow1++;
         }
         
         tempRow1++;
         
         for(int j=0; j< series2.length; j++)
         {
             tempCol1 = 0;
             sheet0.addCell( new Label( tempCol1, tempRow1, series2[j], wCellformat2) );
             
             int tempValueCol = 1;
           
             for( int k=0; k< categories1.length; k++ )
             { 
                 sheet0.addCell( new Number( tempValueCol, tempRow1, data2[j][k], wcf ) );
                 tempValueCol++;
             }
             
             tempRow1++;
         }
         
         
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "Target_ChartOutput.xls";
                
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
