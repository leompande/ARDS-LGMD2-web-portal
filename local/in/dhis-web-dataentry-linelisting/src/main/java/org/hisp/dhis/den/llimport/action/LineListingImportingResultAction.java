package org.hisp.dhis.den.llimport.action;

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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.den.api.LLImportParameters;

import com.opensymphony.xwork2.Action;

public class LineListingImportingResultAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private LLDataValueService lldataValueService;

    public void setLldataValueService( LLDataValueService lldataValueService )
    {
        this.lldataValueService = lldataValueService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer llImportFlag;
    
    public void setLlImportFlag( Integer llImportFlag )
    {
        this.llImportFlag = llImportFlag;
    }

    private Map<String, String> resMap;
    
    private int sourceId;
    
    private int periodId;
    
    private SimpleDateFormat simpleDateFormat;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        Calendar curDateTime = Calendar.getInstance();
        Date curDate = new Date();                
        curDateTime.setTime( curDate );
        
        simpleDateFormat = new SimpleDateFormat( "dd-MMM-yyyy" );
        String logFileName = System.getenv( "DHIS2_HOME" ) + File.separator + "lli" + File.separator + "log" + File.separator + simpleDateFormat.format( curDate )+".txt";
                
        // Create file 
        FileWriter fstream = new FileWriter( logFileName, true );        
        PrintWriter pw = new PrintWriter( fstream );
                        
        initializeResultMap();
        
        List<String> fileNames = new ArrayList<String>(lldataValueService.getLLImportFiles());
                
        int maxLLRecords = lldataValueService.getMaxRecordNo() + 1;
        
        for( String importFile : fileNames )
        {
            try
            {
                String importFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + "lli" + File.separator + "pending" + File.separator + importFile;
                System.out.println(importFilePath);
                            
                Workbook importWorkbook = Workbook.getWorkbook( new File( importFilePath ) );
                
                Sheet sheet = importWorkbook.getSheet( 0 );
                Cell cell = sheet.getCell( 25, 1 );
                sourceId = Integer.parseInt( cell.getContents() );
                
                cell = sheet.getCell( 25, 2 );
                periodId = Integer.parseInt( cell.getContents() );
                
                cell = sheet.getCell( 25, 3 );
                int noOfRecords = Integer.parseInt( cell.getContents() );
                
                cell = sheet.getCell( 25, 4 );
                String reportFileName = cell.getContents() + ".xml";
                
                List<LLImportParameters> llImportParamList = lldataValueService.getLLImportParameters( reportFileName );
                int startRow = 0;
                try
                {
                    startRow = llImportParamList.get( 0 ).getRowNo();
                }
                catch( Exception e )
                {
                    pw.write(importFile + " has no data.");
                    pw.println();
                    continue;
                }
                
                if(llImportFlag == 1)
                {
                    deleteExistingLLData(llImportParamList);
                }
                
                int lastRow = startRow + noOfRecords;
                for( int i = startRow; i < lastRow; i++ )
                {                
                    String query = "INSERT INTO lldatavalue (dataelementid,periodid,sourceid,categoryoptioncomboid,recordno,value,storedby) VALUES "; 
     
                    int count = 1;
                    for( LLImportParameters llImportParameters : llImportParamList )
                    {
                        int rowno = llImportParameters.getRowNo();
                        int colno = llImportParameters.getColNo();
                        String expression = llImportParameters.getExpression();
                        String[] partsOfExpression = expression.split( "\\." );
                        int dataElementId = Integer.parseInt( partsOfExpression[0].trim() );
                        int optionComboId = Integer.parseInt( partsOfExpression[1].trim() );
                        cell = sheet.getCell( colno, rowno );
                        String value = cell.getContents();
                        
                        String tstr = resMap.get( value.trim() );
                        if( tstr != null )
                            value = tstr;
                        
                        query += "(" +dataElementId+","+periodId+","+sourceId+","+optionComboId+","+maxLLRecords+",'"+value+"','admin')";
                        
                        if( count == llImportParamList.size() )
                            query += ";";
                        else
                            query += ",";
                        
                        System.out.print(value + " : ");
                        count++;
                        
                    }
                    System.out.println("");
                    
                    //System.out.println( query );
                    //lldataValueService.saveLLdataValue( query );
                    maxLLRecords++;
                    
                }// Each Linelsitng Record 
                pw.write(importFile + " is Imported.");
                pw.println();
            }
            catch(Exception e)
            {
                pw.write(importFile + " has problem while Importing, please check it.");
                pw.println();
            }
        }// import file names for loop end
        
        //Close the output stream
        pw.close();
        fstream.close();        

        return SUCCESS;
    }
        
    public void initializeResultMap()
    {
        resMap = new HashMap<String, String>();
        
        resMap.put( "---", "NONE" );
        resMap.put( "Male", "M" );
        resMap.put( "Female", "F" );
        resMap.put( "YES", "Y" );
        resMap.put( "NO", "N" );
        resMap.put( "NOT KNOWN", "NK" );
        resMap.put( "BELOW 1 DAY", "B1DAY" );
        resMap.put( "1 DAY - 1 WEEK", "B1WEEK" );
        resMap.put( "1 WEEK - 1 MONTH", "B1MONTH" );
        resMap.put( "1 MONTH - 1 YEAR", "B1YEAR" );
        resMap.put( "1 YEAR - 5 YEARS", "B5YEAR" );                
        resMap.put( "6 YEARS - 14 YEARS", "O5YEAR" );
        
        resMap.put( "15 YEARS - 55 YEARS", "O15YEAR" );
        resMap.put( "OVER 55 YEARS", "O55YEAR" );
        
        resMap.put( "ASPHYXIA", "ASPHYXIA" );
        resMap.put( "SEPSIS", "SEPSIS" );
        resMap.put( "LOWBIRTHWEIGH", "LOWBIRTHWEIGH" );
        resMap.put( "Immunization reactions", "IMMREAC" );
        resMap.put( "Pneumonia", "PNEUMONIA" );
        resMap.put( "Diarrhoeal Disease", "DIADIS" );
        resMap.put( "Measles", "MEASLES" );
        resMap.put( "Tuberculosis", "TUBER" );
        resMap.put( "Malaria", "MALARIA" );
        resMap.put( "HIV/AIDS", "HIVAIDS" );
        resMap.put( "Other Fever related", "OFR" );
        resMap.put( "Pregnancy Related Death( maternal mortality)", "PRD" );
        resMap.put( "Sterilisation related deaths", "SRD" );
        resMap.put( "Accidents or Injuries", "AI" );
        resMap.put( "Suicides", "SUICIDES" );
        resMap.put( "Animal Bites or Stings", "ABS" );
        resMap.put( "Respiratory Infections and Disease", "RID" );
        resMap.put( "Heart Disease and hypertension", "HDH" );
        resMap.put( "Stroke and Neurological Disease", "SND" );
        resMap.put( "Other Known Acute Disease", "OKAD" );
        resMap.put( "Other Known Chronic Disease", "OKCD" );
        resMap.put( "Others", "OTHERS" );
        resMap.put( "FIRST TRIMESTER PREGNANCY", "FTP" );
        resMap.put( "SECOND TRIMESTER PREGNANCY", "STP" );
        resMap.put( "THIRD TRIMESTER PREGNANCY", "TTP" );
        resMap.put( "DELIVERY", "DELIVERY" );
        resMap.put( "AFTER DELIVERY WITHIN 42 DAYS", "ADW42D" );
        resMap.put( "HOME", "HOME" );
        resMap.put( "SUBCENTER", "SC" );
        resMap.put( "PHC", "PHC" );
        resMap.put( "CHC", "CHC" );
        resMap.put( "MEDICAL COLLEGE", "MC" );
        resMap.put( "UNTRAINED", "UNTRAINED" );
        resMap.put( "TRAINED", "TRAINED" );
        resMap.put( "ANM", "ANM" );
        resMap.put( "NURSE", "NURSE" );
        resMap.put( "DOCTOR", "DOCTOR" );
        resMap.put( "ABORTION", "ABORTION" );
        resMap.put( "OBSTRUCTED/PROLONGED LABOUR", "OPL" );
        resMap.put( "FITS", "FITS" );
        resMap.put( "SEVERE HYPERTENSION", "SH" );
        resMap.put( "BLEEDING BEFORE CHILD DELIVERY", "BBCD" );
        resMap.put( "BLEEDING AFTER CHILD DELIVERY", "BACD" );
        resMap.put( "HIGH FEVER BEFORE DELIVERY", "HFBD" );
        resMap.put( "HIGH FEVER AFTER DELIVERY", "HFAD" );
        
    }

    public void deleteExistingLLData( List<LLImportParameters> llImportParamList )
    {
        for( LLImportParameters llImportParameters : llImportParamList )
        {
            String expression = llImportParameters.getExpression();
            String[] partsOfExpression = expression.split( "\\." );
            int dataElementId = Integer.parseInt( partsOfExpression[0].trim() );
            int optionComboId = Integer.parseInt( partsOfExpression[1].trim() );
            
            String query = "DELETE FROM lldatavalue WHERE sourceid = "+ sourceId +" AND periodid = "+ periodId + " AND dataelementid = "+dataElementId+" AND categoryoptioncomboid = "+optionComboId;
            
            //lldataValueService.saveLLdataValue( query );
        }
    }
}
