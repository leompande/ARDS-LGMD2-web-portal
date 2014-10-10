/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.reports.orgunitgroupsetreport.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.util.OrganisationUnitCommentComparator;
import org.hisp.dhis.reports.util.ReportService;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class GenerateOrgunitGroupsetReportsResultAction
    implements Action
    {
        
        // ------------------------------------------------------------------------------------------------------------------------------------------
        // Dependencies
        // ------------------------------------------------------------------------------------------------------------------------------------------

        private static final String NULL_REPLACEMENT = "0";
            
        private StatementManager statementManager;

        public void setStatementManager( StatementManager statementManager )
        {
            this.statementManager = statementManager;
        }

        private ReportService reportService;

        public void setReportService( ReportService reportService )
        {
            this.reportService = reportService;
        }
        
        private OrganisationUnitGroupService organisationUnitGroupService;

        public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
        {
            this.organisationUnitGroupService = organisationUnitGroupService;
        }
        
        private OrganisationUnitService organisationUnitService;

        public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
        {
            this.organisationUnitService = organisationUnitService;
        }

        public OrganisationUnitService getOrganisationUnitService()
        {
            return organisationUnitService;
        }
        
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
        
        private DataElementCategoryService dataElementCategoryService;

        public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
        {
            this.dataElementCategoryService = dataElementCategoryService;
        }

        private AggregationService aggregationService;

        public void setAggregationService( AggregationService aggregationService )
        {
            this.aggregationService = aggregationService;
        }
        
        private DataValueService dataValueService;

        public void setDataValueService( DataValueService dataValueService )
        {
            this.dataValueService = dataValueService;
        }
        
        private IndicatorService indicatorService;

        public void setIndicatorService( IndicatorService indicatorService )
        {
            this.indicatorService = indicatorService;
        }
        
        private DataSetService dataSetService;

        public void setDataSetService( DataSetService dataSetService )
        {
            this.dataSetService = dataSetService;
        }
  
        // -------------------------------------------------------------------------------------------------------------------------------------
        // Properties
        // ---------------------------------------------------------------------------------------------------------------------------------------
        
        private InputStream inputStream;

        public InputStream getInputStream()
        {
            return inputStream;
        }

        /*
        private String contentType;

        public String getContentType()
        {
            return contentType;
        }
        */

        private String fileName;

        public String getFileName()
        {
            return fileName;
        }

        /*
        private int bufferSize;

        public int getBufferSize()
        {
            return bufferSize;
        }
        */

        /*
        private MathTool mathTool;

        public MathTool getMathTool()
        {
            return mathTool;
        }
        */
        
        private OrganisationUnitGroupSet sellectedOrgUnitGroupSet;

        public OrganisationUnitGroupSet getSellectedOrgUnitGroupSet()
        {
            return sellectedOrgUnitGroupSet;
        }
        
        private List<OrganisationUnitGroup> sellectedOrgUnitGroupList;

        public List<OrganisationUnitGroup> getSellectedOrgUnitGroupList()
        {
            return sellectedOrgUnitGroupList;
        }
               
        private List<OrganisationUnit> orgUnitList;

        public List<OrganisationUnit> getOrgUnitList()
        {
            return orgUnitList;
        }
  
        private SimpleDateFormat simpleDateFormat;

        public SimpleDateFormat getSimpleDateFormat()
        {
            return simpleDateFormat;
        }

        private List<String> deCodeType;

        private List<String> serviceType;

        private String reportFileNameTB;

        public void setReportFileNameTB( String reportFileNameTB )
        {
            this.reportFileNameTB = reportFileNameTB;
        }

        public String getReportFileNameTB( )
        {
            return reportFileNameTB;
        }
       
        private String reportModelTB;

        public void setReportModelTB( String reportModelTB )
        {
            this.reportModelTB = reportModelTB;
        }
        
        public String getReportModelTB( )
        {
            return reportModelTB;
        }
        
        private int orgUnitGroupSetList;
        
        public void setOrgUnitGroupSetList( int orgUnitGroupSetList )
        {
            this.orgUnitGroupSetList = orgUnitGroupSetList;
        }
        

        private String reportList;

        public void setReportList( String reportList )
        {
            this.reportList = reportList;
        }
        
        private Period selectedPeriod;

        public Period getSelectedPeriod()
        {
            return selectedPeriod;
        }
              
        private int availablePeriods;

        public void setAvailablePeriods( int availablePeriods )
        {
            this.availablePeriods = availablePeriods;
        }
     
        private List<Integer> sheetList;

        private List<Integer> rowList;

        private List<Integer> colList;

        private Date pDate;
        
        private int tempMonthCount;

        private String raFolderName;
        
        private SimpleDateFormat monthDateFormat;
        
        private SimpleDateFormat yearDateFormat;
        
        private Connection con = null;
              
        // ------------------------------------------------------------------------------------------------------------------------------------
        // Action implementation
        // ----------------------------------------------------------------------------------------------------------------------------------

        @SuppressWarnings("unchecked")
        public String execute()
            throws Exception
        {
            statementManager.initialise();
            raFolderName = reportService.getRAFolderName();
                    
         //--------------------------------------------- Initialization----------------------------------------------------------------------
            
            //mathTool = new MathTool();
            deCodeType = new ArrayList<String>();
            serviceType = new ArrayList<String>();
            String deCodesXMLFileName = "";
            deCodesXMLFileName = reportList + "DECodes.xml";

            sheetList = new ArrayList<Integer>();
            rowList = new ArrayList<Integer>();
            colList = new ArrayList<Integer>();
        
            String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
            //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";           
            
            String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
            File newdir = new File( outputReportPath );
            if( !newdir.exists() )
            {
                newdir.mkdirs();
            }
            outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
            
            Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
            WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
            
         //-------------------------------------------- Period Info---------------------------------------------------------------------------

            selectedPeriod = periodService.getPeriod( availablePeriods );        
            getPreviousPeriod(selectedPeriod);
            simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
            monthDateFormat = new SimpleDateFormat("MMM");
            yearDateFormat = new SimpleDateFormat("yyyy");
                           
            //pDate = format.parseDate( prevDate );
            Calendar tempPrevEndDate = Calendar.getInstance();
            tempPrevEndDate.setTime( selectedPeriod.getEndDate() );
            tempPrevEndDate.roll( Calendar.YEAR, -1 );
            pDate = tempPrevEndDate.getTime();
                               
         //-----------------------------------OrgUnit Group Info---------------------------------------------------------------------------------- 
            
            sellectedOrgUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( orgUnitGroupSetList );                 
            sellectedOrgUnitGroupList = new ArrayList<OrganisationUnitGroup>( sellectedOrgUnitGroupSet.getOrganisationUnitGroups()); 
            Collections.sort( sellectedOrgUnitGroupList, new IdentifiableObjectNameComparator() );     
                      
            Iterator<OrganisationUnitGroup> ougtr1 = sellectedOrgUnitGroupList.iterator();            
            OrganisationUnitGroup selectedOrgUnitGroup1 = new OrganisationUnitGroup();
        
        //----------------------------------------------some use full counters----------------------------------------------------------------
            
            Integer startRow = 11;
            Integer ouGroupColNo = 0;
            int slNo = 0;
            
            Integer rowCounter = 0;
            int gcount = 0;
            rowCounter = rowCounter + startRow;
            Integer currentRow = rowCounter; 
          
         // ----------------First outermost while loop starts here for each organization unit group------------------------------------------
            
            while( ougtr1.hasNext())
            {            
                selectedOrgUnitGroup1 = organisationUnitGroupService.getOrganisationUnitGroup(( ougtr1.next()).getId());
                
                
            
          //----------------------- OrgUnit Related Info of this particular organization unit group---------------------------------------
               
                orgUnitList = new ArrayList<OrganisationUnit>();
                List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup1.getMembers() );                                        
                
                Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
                Collections.sort( orgUnitList, new OrganisationUnitCommentComparator() );
                List<String> deCodesList = getDECodes( deCodesXMLFileName );
                Iterator<OrganisationUnit> it = orgUnitList.iterator();       
                
                currentRow = rowCounter;
                
                WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
                sheet0.mergeCells( ouGroupColNo, currentRow, ouGroupColNo, currentRow+ orgUnitList.size()-1 );
                sheet0.addCell( new Label( ouGroupColNo, currentRow, selectedOrgUnitGroup1.getName(), getCellFormat1() ) );
                System.out.println(currentRow+ " : "+ selectedOrgUnitGroup1.getName() + " : "+orgUnitList.size());
                
           //--------------------------------Second while loop starts here for each organisation unit in orgunitList--------------------------------------------------------------------------------            
                
                while ( it.hasNext() )            
                {
                    OrganisationUnit orgUnit = (OrganisationUnit) it.next();   
                    Iterator<String> it1 = deCodesList.iterator();
                    int count1 = 0;   
                    currentRow = rowCounter;                   
                    
          //---------------------------------third while loop which will collect data for each column or for each organization unit------------          
                    
                    while ( it1.hasNext() )
                    {
                        String deCodeString = (String) it1.next();
                        String deType = (String) deCodeType.get( count1 );
                        String sType = (String) serviceType.get( count1 );
                        String tempStr = "";
                        Double tempDouble = 0.0;    
                        String Formula = " ";  
                        Calendar tempStartDate = Calendar.getInstance();
                        Calendar tempEndDate = Calendar.getInstance();
                        List<Calendar> calendarList = new ArrayList<Calendar>(getStartingEndingPeriods( deType ));
                        
                        if ( calendarList == null || calendarList.isEmpty() )
                        {
                            return SUCCESS;
                        }
                        else
                        {
                            tempStartDate = calendarList.get( 0 );
                            tempEndDate = calendarList.get( 1 );
                        }              
                        if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                        {
                            tempStr = orgUnit.getShortName();
                        }
                        else if ( deCodeString.equalsIgnoreCase( "FACILITYGROUP" ) )
                        {
                          tempStr = selectedOrgUnitGroup1.getName().toString();
                        }
                        else if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                        {
                            tempStr = orgUnitList.get( 0 ).getShortName();
                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ))
                        {
                            tempStr = simpleDateFormat.format( selectedPeriod.getStartDate() ).toString();
                        }                
                        else if ( deCodeString.equalsIgnoreCase( "PREV-PER" ) )
                        {
                           tempStr = simpleDateFormat.format( pDate ); 
                        }
                        else if ( deCodeString.equalsIgnoreCase( "MPERIOD" ))
                        {
                            tempStr = monthDateFormat.format( selectedPeriod.getStartDate() ).toString();
                        }
                        else if ( deCodeString.equalsIgnoreCase( "YPERIOD" ))
                        {
                            tempStr = yearDateFormat.format( selectedPeriod.getStartDate() ).toString();
                        }                   
                        else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                        {
                            tempStr = "" + (slNo + 1);
                        }
                        else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                        {
                            tempStr = "";
                        }                                 
                        else if ( deCodeString.equalsIgnoreCase( "TT-PERCENTAGE" ) )
                        {                                              
                                Formula = "ROUND((((E" + (currentRow+1) + ")/D" + (currentRow+1) + ")*100),2)";  
                            //Formula = "IFERROR(ROUND((((E"+ (currentRow+1) +")/D" + (currentRow+1) + ")*100),2),\" \")";
                            //=IFERROR(ROUND((((E12)/D12)*100),2),"")
                        }
                        else if ( deCodeString.equalsIgnoreCase( "BCG-PERCENTAGE" ) )
                        {                                
                                Formula = "ROUND((((H" + (currentRow+1) + ")/G" + (currentRow+1) + ")*100),2)";                                       
                        }
                        else if ( deCodeString.equalsIgnoreCase( "DPT3-PERCENTAGE" ) )
                        {                           
                                Formula = "ROUND((((J" + (currentRow+1) + ")/G" + (currentRow+1) + ")*100),2)";                           
                        }
                        else if ( deCodeString.equalsIgnoreCase( "POLIO3-PERCENTAGE" ) )
                        {
                                Formula = "ROUND((((L" + (currentRow+1) + ")/G" + (currentRow+1) + ")*100),2)";                          
                        }
                        else if ( deCodeString.equalsIgnoreCase( "MEASLES-PERCENTAGE" ) )
                        {
                                Formula = "ROUND((((N" + (currentRow+1) + ")/G" + (currentRow+1) + ")*100),2)";                           
                        }
                        else if ( deCodeString.equalsIgnoreCase( "FULL-IMMUN-PERCENTAGE" ) )
                        {
                                Formula = "ROUND((((P" + (currentRow+1) + ")/G" + (currentRow+1) + ")*100),2)";
                        }
                        else if ( deCodeString.equalsIgnoreCase( "INST-DELIVERY-PERCENTAGE" ) )
                        {    
                                Formula = "ROUND((((E" + (currentRow+1) + ")/D" + (currentRow+1) + ")*100),2)";                         
                        }                       
                        else
                        {   
                            int years = tempEndDate.get( Calendar.YEAR ) - tempStartDate.get( Calendar.YEAR ) - 1;
                            int months = (13 - 3) + tempEndDate.get( Calendar.MONTH );
                            if(tempEndDate.get( Calendar.MONTH )>=3 && tempEndDate.get( Calendar.MONTH )<=11)
                            {
                                tempMonthCount = months + (years * 12);
                            }
                            else if (tempEndDate.get( Calendar.MONTH )==0)
                            { 
                                tempMonthCount = 10;
                            }
                            else if (tempEndDate.get( Calendar.MONTH )==1)
                            { 
                                tempMonthCount = 11;
                            }
                            else 
                            { 
                                tempMonthCount = 12;
                            }
                            
                            deCodeString = deCodeString.replaceAll( "MONTHCOUNT", "" + tempMonthCount );
                            if ( sType.equalsIgnoreCase( "dataelement" ) || sType.equalsIgnoreCase( "dataelement-percentage" ) )
                            {                        
                                tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), orgUnit );
                                try
                                {
                                        tempDouble = Double.parseDouble(tempStr);
                                }
                                catch( Exception e )
                                {
                                        tempDouble = 0.0;
                                }
                            }
                            else if ( sType.equalsIgnoreCase( "formula" ) )
                            {
                                tempStr = deCodeString;
                            }
                            else
                            {
                                tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), orgUnit.getParent() );
                                
                                try
                                {
                                        tempDouble = Double.parseDouble(tempStr);
                                }
                                catch( Exception e )
                                {
                                        tempDouble = 0.0;
                                }                               
                            }
                        }
                        int tempRowNo = rowList.get( count1 );
                        int tempColNo = colList.get( count1 );
                        int sheetNo = sheetList.get( count1 );
                        //WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                            
                        if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                            {                             
                                if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                    || deCodeString.equalsIgnoreCase( "PREV-PER" )
                                    || deCodeString.equalsIgnoreCase("FACILITY-NOREPEAT") )
                                {
                                }
                                else
                                {
                                    tempRowNo += slNo;
                                }
    
                                //tempRowNo += orgUnitCount;
                                WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );    
                                CellFormat cellFormat = cell.getCellFormat();
                                WritableCellFormat wCellformat = new WritableCellFormat();
                                WritableCellFormat numberCellFormat = new WritableCellFormat();    
                                wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                                numberCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
                                numberCellFormat.setAlignment( Alignment.CENTRE );
    
                                if ( cell.getType() == CellType.LABEL )
                                {
                                    Label l = (Label) cell;
                                    l.setString( tempStr );
                                    l.setCellFormat( cellFormat );
                                }
                                else
                                {
                                    if ( sType.equalsIgnoreCase( "dataelement" )
                                        || sType.equalsIgnoreCase( "dataelement-percentage" ) )
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "FACILITY" )
                                            || deCodeString.equalsIgnoreCase( "FACILITYGROUP" )
                                            || deCodeString.equalsIgnoreCase( "PERIOD" )
                                            || deCodeString.equalsIgnoreCase( "PERIOD-NO-REPEAT" )
                                            || deCodeString.equalsIgnoreCase( "PREV-PER" )
                                            || deCodeString.equalsIgnoreCase( "NA" ) 
                                            
                                            || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                                        {
                                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                        }
                                        else if ( deCodeString.equalsIgnoreCase( "TT-PERCENTAGE" )
                                            || deCodeString.equalsIgnoreCase( "BCG-PERCENTAGE" )
                                            || deCodeString.equalsIgnoreCase( "DPT3-PERCENTAGE" )
                                            || deCodeString.equalsIgnoreCase( "POLIO3-PERCENTAGE" )
                                            || deCodeString.equalsIgnoreCase( "MEASLES-PERCENTAGE" )
                                            || deCodeString.equalsIgnoreCase( "FULL-IMMUN-PERCENTAGE" )                                            
                                            || deCodeString.equalsIgnoreCase( "INST-DELIVERY-PERCENTAGE" ))
                                        {
                                            //sheet0.addCell( new Formula( tempColNo, tempRowNo, Formula, numberCellFormat ) );
                                            sheet0.addCell( new Formula( tempColNo, tempRowNo, Formula, numberCellFormat ) );
                                        }
                                        else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                                        {
                                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, numberCellFormat ) );
                                        }
                                        else
                                        {
                                            sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, numberCellFormat ) );
                                        }
                                    }
                                    else if ( sType.equalsIgnoreCase( "indicator" ) )
                                    {
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, numberCellFormat ) );
                                    }
                                    else
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                    }
                                }
                            }
                            else
                            {
                                WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );  
                                CellFormat cellFormat = cell.getCellFormat();
                                WritableCellFormat wCellformat = new WritableCellFormat();
                                WritableCellFormat numberCellFormat = new WritableCellFormat();
                                wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                                wCellformat.setAlignment( Alignment.CENTRE );
                                numberCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
                                numberCellFormat.setAlignment( Alignment.CENTRE );
    
                                if ( cell.getType() == CellType.LABEL && !sType.equalsIgnoreCase( "formula" ) )
                                {
                                    Label l = (Label) cell;
                                    l.setString( tempStr );          
                                    l.setCellFormat( cellFormat );
                                }
                                else
                                {
                                    if ( sType.equalsIgnoreCase( "dataelement" )
                                        || sType.equalsIgnoreCase( "dataelement-percentage" ) )
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "FACILITY" )
                                            || deCodeString.equalsIgnoreCase( "FACILITYGROUP" )
                                            || deCodeString.equalsIgnoreCase( "PERIOD" )
                                            || deCodeString.equalsIgnoreCase( "PERIOD-NO-REPEAT" )
                                            || deCodeString.equalsIgnoreCase( "PREV-PER" )
                                            || deCodeString.equalsIgnoreCase( "NA" ) 
                                            || deCodeString.equalsIgnoreCase( "SLNO" ) )
                                        {
                                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                        }
                                        else
                                        {
                                            sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, numberCellFormat ) );
                                        }
                                    }
                                    else if ( sType.equalsIgnoreCase( "indicator" ) )
                                    {
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, tempDouble, wCellformat ) );
                                    }
                                    else if ( sType.equalsIgnoreCase( "formula" ) )
                                    {
                                        sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, cellFormat) );
                                    }
                                    else
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                    }                           
                              }
                        }               
                        count1++;
                    }
                    
                    //-----------------------------------third inner most while loop end -----------------------------------------------------------
                    
                    rowCounter++;
                    slNo++;
                }
                
                //---------------------------------------middle outer while loop end-----------------------------------------------------------
                
                gcount++;
            }
            
            //----------------------------------------------first or outer most while loop end-------------------------------------------------------------------  
            
            outputReportWorkbook.write();
            outputReportWorkbook.close();
    
            fileName = reportFileNameTB.replace( ".xls", "" );
            fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );      
            outputReportFile.deleteOnExit();
                
            try
                {
                    
                }
            finally
                {
                    try
                    {
                        if ( con != null )   con.close();
                    }
                    catch ( Exception e )
                    {
                        
                    }
                }// finally block end    
                statementManager.destroy();
                return SUCCESS;
        }

        //------------------------------------------------execute method ends here---------------------------------------------------------------
        
        public WritableCellFormat getCellFormat1() throws Exception
        {
            WritableCellFormat wCellformat = new WritableCellFormat();                        
            
            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
            wCellformat.setAlignment( Alignment.CENTRE );
            wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
            wCellformat.setWrap( true );

            return wCellformat;
        }
        
        public List<Calendar> getStartingEndingPeriods(String deType)
        {
            
            List<Calendar> calendarList = new ArrayList<Calendar>();           
            Calendar tempStartDate = Calendar.getInstance();
            Calendar tempEndDate = Calendar.getInstance();
            Period previousPeriod = new Period();
            previousPeriod = getPreviousPeriod();

            if ( deType.equalsIgnoreCase( "cpmcy" ) )
            {
                tempStartDate.setTime( previousPeriod.getStartDate() );
                if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                {
                    tempStartDate.roll( Calendar.YEAR, -1 );
                }
                tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
                tempEndDate.setTime( previousPeriod.getEndDate() );
            }
            else if ( deType.equalsIgnoreCase( "ccmcy" ) )
            {
                tempStartDate.setTime( selectedPeriod.getStartDate() );
                if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                {
                    tempStartDate.roll( Calendar.YEAR, -1 );
                }
                tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
                tempEndDate.setTime( selectedPeriod.getEndDate() );
            }
            else if ( deType.equalsIgnoreCase( "ccmpy" ) )
            {
                tempStartDate.setTime( selectedPeriod.getStartDate() );
                tempEndDate.setTime( selectedPeriod.getEndDate() );
                tempStartDate.roll( Calendar.YEAR, -1 );
                tempEndDate.roll( Calendar.YEAR, -1 );
                if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                {
                    tempStartDate.roll( Calendar.YEAR, -1 );
                }
                tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            }
            else if ( deType.equalsIgnoreCase( "cmpy" ) )
            {
                tempStartDate.setTime( selectedPeriod.getStartDate() );
                tempEndDate.setTime( selectedPeriod.getEndDate() );
                tempStartDate.roll( Calendar.YEAR, -1 );
                tempEndDate.roll( Calendar.YEAR, -1 );
            }
            else
            {
                tempStartDate.setTime( selectedPeriod.getStartDate() );
                tempEndDate.setTime( selectedPeriod.getEndDate() );
            }
            calendarList.add( tempStartDate );
            calendarList.add( tempEndDate );          
            return calendarList;
        }

        public Period getPreviousPeriod()
        {
            Period period = new Period();
            Calendar tempDate = Calendar.getInstance();
            tempDate.setTime( selectedPeriod.getStartDate() );
            if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
            {
                tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
                tempDate.roll( Calendar.YEAR, -1 );
            }
            else
            {
                tempDate.roll( Calendar.MONTH, -1 );
            }
            PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
            period = reportService.getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
                periodType );

            return period;
        }
        
        public Period getPreviousPeriod(Period selectedPeriod)
        {
            Period period = new Period();
            Calendar tempDate = Calendar.getInstance();
            tempDate.setTime( selectedPeriod.getStartDate() );
            if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
            {
                tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
                tempDate.roll( Calendar.YEAR, -1 );
            }
            else
            {
                tempDate.roll( Calendar.MONTH, -1 );
            }
            PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
            period = reportService.getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
                periodType );

            return period;
        }
        
        public List<String> getDECodes( String fileName )
        {
            List<String> deCodes = new ArrayList<String>();
            String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
                + File.separator + fileName;
            try
            {
                String newpath = System.getenv( "DHIS2_HOME" );
                if ( newpath != null )
                {
                    path = newpath + File.separator + File.separator + raFolderName + File.separator + fileName;
                }
            }
            catch ( NullPointerException npe )
            {
            }

            try
            {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse( new File( path ) );
                if ( doc == null )
                {
                    return null;
                }
                NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
                int totalDEcodes = listOfDECodes.getLength();

                for ( int s = 0; s < totalDEcodes; s++ )
                {
                    Element deCodeElement = (Element) listOfDECodes.item( s );
                    NodeList textDECodeList = deCodeElement.getChildNodes();
                    deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
                    serviceType.add( deCodeElement.getAttribute( "stype" ) );
                    deCodeType.add( deCodeElement.getAttribute( "type" ) );
                    sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                    rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                    colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );
                }// end of for loop with s var
            }// try block end
            catch ( SAXParseException err )
            {
                System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
                System.out.println( " " + err.getMessage() );
            }
            catch ( SAXException e )
            {
                Exception x = e.getException();
                ((x == null) ? e : x).printStackTrace();
            }
            catch ( Throwable t )
            {
                t.printStackTrace();
            }
            return deCodes;
        }// getDECodes end
        
        private String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
        {
            //System.out.println("In Local Reports getResultDataValue : ");
            try
            {               
                int deFlag1 = 0;
                int deFlag2 = 0;
                Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );
                Matcher matcher = pattern.matcher( formula );
                StringBuffer buffer = new StringBuffer();
                String resultValue = "";

                while ( matcher.find() )
                {
                    String replaceString = matcher.group();

                    replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                    String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                        .length() );
                    replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );
          
                    int dataElementId = 0;                                
                    int optionComboId = 0;
                    
                    try
                    {
                            dataElementId = Integer.parseInt( replaceString );
                            optionComboId = Integer.parseInt( optionComboIdStr );
                    }
                    catch(Exception e)
                    {
                            dataElementId = 0;
                            optionComboId = 0;
                    }
                    
                    DataElement dataElement = dataElementService.getDataElement( dataElementId );
                    DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                        .getDataElementCategoryOptionCombo( optionComboId );

                    if ( dataElement == null || optionCombo == null )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                    {
                        Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                            startDate, endDate, organisationUnit );
  
                        if ( aggregatedValue == null )
                        {
                            replaceString = NULL_REPLACEMENT;
                        }
                        else
                        {
                            replaceString = String.valueOf( aggregatedValue );
                            deFlag2 = 1;
                        }
                    }
                    else
                    {
                        deFlag1 = 1;
                        PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                            dePeriodType, startDate, endDate ) );
                        Period tempPeriod = new Period();
                        if ( periodList == null || periodList.isEmpty() )
                        {
                            replaceString = "";
                            matcher.appendReplacement( buffer, replaceString );
                            continue;
                        }
                        else
                        {
                            tempPeriod = (Period) periodList.get( 0 );
                        }
                        DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,optionCombo );
                        if ( dataValue != null )
                        {
                            replaceString = dataValue.getValue();
                        }
                        else
                            replaceString = "";

                        if ( replaceString == null )
                            replaceString = "";
                    }
                    matcher.appendReplacement( buffer, replaceString );
                    resultValue = replaceString;
                }
                matcher.appendTail( buffer );

                if ( deFlag1 == 0 )
                {
                    double d = 0.0;
                    try
                    {
                        d = MathUtils.calculateExpression( buffer.toString() );
                    }
                    catch ( Exception e )
                    {
                        d = 0.0;
                        resultValue = "";
                    }
                    if ( d == -1 )
                    {
                        d = 0.0;
                        resultValue = "";
                    }
                    else
                    {
                        resultValue = "" + d;
                        d = d * 10;
                        if ( d % 10 == 0 )
                        {
                            resultValue = "" + (int) d / 10;
                        }
                        d = d / 10;                     
                        if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                            resultValue = "" + (int) d;
                    }
                }
                else
                {
                    resultValue = buffer.toString();
                }
                if ( resultValue.equalsIgnoreCase( "" ) )
                    resultValue = " ";

                return resultValue;
            }
            catch ( NumberFormatException ex )
            {
                throw new RuntimeException( "Illegal DataElement id", ex );
            }
        }
        
        private String getResultIndicatorValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
            {
                try
                {
                    int deFlag1 = 0;
                    int deFlag2 = 0;
                    Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );
                    Matcher matcher = pattern.matcher( formula );
                    StringBuffer buffer = new StringBuffer();

                    while ( matcher.find() )
                    {
                        String replaceString = matcher.group();
                        replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                        replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );
                        int indicatorId = Integer.parseInt( replaceString );
                        Indicator indicator = indicatorService.getIndicator( indicatorId );

                        if ( indicator == null )
                        {
                            replaceString = "";
                            matcher.appendReplacement( buffer, replaceString );
                            continue;
                        }

                       Double aggregatedValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,organisationUnit );

                        if ( aggregatedValue == null )
                        {
                            replaceString = NULL_REPLACEMENT;
                        }
                        else
                        {
                            replaceString = String.valueOf( aggregatedValue );
                            deFlag2 = 1;
                        }
                        matcher.appendReplacement( buffer, replaceString );
                    }
                    
                    matcher.appendTail( buffer );
                    String resultValue = "";
                    
                    if ( deFlag1 == 0 )
                    {
                        double d = 0.0;
                        try
                        {
                            d = MathUtils.calculateExpression( buffer.toString() );
                        }
                        catch ( Exception e )
                        {
                            d = 0.0;
                        }
                        if ( d == -1 )
                            d = 0.0;
                        else
                        {
                            d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                            resultValue = "" + d;
                        }

                        if ( deFlag2 == 0 )
                        {
                            resultValue = " ";
                        }
                    }
                    else
                    {
                        resultValue = buffer.toString();
                    }
                    return resultValue;
                }
                catch ( NumberFormatException ex )
                {
                    throw new RuntimeException( "Illegal DataElement id", ex );
                }
         }
        
           
        public PeriodType getDataElementPeriodType( DataElement de )
        {
            List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
            Iterator<DataSet> it = dataSetList.iterator();
            while ( it.hasNext() )
            {
                DataSet ds = (DataSet) it.next();
                List<DataElement> dataElementList = new ArrayList<DataElement>( ds.getDataElements() );
                if ( dataElementList.contains( de ) )
                {
                    return ds.getPeriodType();
                }
            }
            return null;
        } // getDataElementPeriodType end
   }

