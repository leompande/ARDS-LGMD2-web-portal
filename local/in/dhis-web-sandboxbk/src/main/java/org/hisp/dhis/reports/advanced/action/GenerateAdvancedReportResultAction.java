package org.hisp.dhis.reports.advanced.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetStore;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.util.ReportService;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.ActionSupport;

public class GenerateAdvancedReportResultAction extends ActionSupport
{
    private static final String NULL_REPLACEMENT = "0";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetStore dataSetStore;

    public void setDataSetStore( DataSetStore dataSetStore )
    {
        this.dataSetStore = dataSetStore;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String contentType;

    public String getContentType()
    {
        return contentType;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    //private OrganisationUnit selectedOrgUnit;

    //public OrganisationUnit getSelectedOrgUnit()
    //{
    //    return selectedOrgUnit;
    //}

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private List<String> dataValueList;

    public List<String> getDataValueList()
    {
        return dataValueList;
    }

    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }

    private List<String> slNos;

    public List<String> getSlNos()
    {
        return slNos;
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

    private String reportModelTB;

    public void setReportModelTB( String reportModelTB )
    {
        this.reportModelTB = reportModelTB;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private Hashtable<String, String> serviceList;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date sDate;
    private Date eDate;
    
    private List<Integer> ougmemberCountList;
    private int[] monthCount = {0,10,11,12,1,2,3,4,5,6,7,8,9};
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // Initialization        
        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        ougmemberCountList = new ArrayList<Integer>();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        deCodesXMLFileName = reportList + "DECodes.xml";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        String inputTemplatePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

                
        //WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
                    

        // OrgUnit Related Info
        //selectedOrgUnit = new OrganisationUnit();
        //selectedOrgUnit = organisationUnitService.getOrganisationUnitsAtLevel( 1 ).iterator().next();
        List<OrganisationUnitGroup> orgUnitGroupList = new ArrayList<OrganisationUnitGroup>();
        orgUnitGroupList = getOrgUnitGroupsFromXML();
        if(reportModelTB.equals( "DYNAMIC-GROUP" ))
        {
            int tempCount1=0;
            orgUnitList = new ArrayList<OrganisationUnit>();
            Iterator<OrganisationUnitGroup> iterator1 = orgUnitGroupList.iterator();
            while(iterator1.hasNext())
            {
                OrganisationUnitGroup oug = (OrganisationUnitGroup) iterator1.next();
                List<OrganisationUnit> tempList = new ArrayList<OrganisationUnit>(oug.getMembers());
                Collections.sort( tempList, new OrganisationUnitNameComparator() );
                orgUnitList.addAll( tempList );
                ougmemberCountList.add( oug.getMembers().size() );
                if(tempCount1 !=0 && tempCount1 != orgUnitGroupList.size())
                    outputReportWorkbook.copySheet( 0, oug.getName(), tempCount1 );
                System.out.println("ougMemberCount : "+oug.getMembers().size());
                tempCount1++;
            }
            System.out.println("OrgUnitList Size : "+orgUnitList.size());
        }
        else if ( reportModelTB.equals( "dynamicwithrootfacility" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt((String)orgUnitListCB.get( 0 )) );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            orgUnitList.add( orgUnit );
        }
        else if ( reportList.equals( "dynamicwithoutrootfacility" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt((String)orgUnitListCB.get( 0 )) );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        }
        else
        {
            //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt((String)orgUnitListCB.get( 0 )) );
            
            //This is hard coded and for temporary use, later we need to change this. Because in 'advanced' option
            // there is no orgunit tree to select orgunits thats why if the report is static model then by default 
            // we are taking root orgunit as selected orgunit
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnitsAtLevel( 1 ).iterator().next();;
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.add( orgUnit );
        }

        
        
        
        // Period Info
        sDate = format.parseDate( startDate );            
        eDate = format.parseDate( endDate );            

        //selectedPeriod = periodService.getPeriod( availablePeriods );
        //Period previousPeriod = new Period();
        //previousPeriod = getPreviousPeriod();

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        Iterator it = orgUnitList.iterator();
        int orgUnitCount = 0;
        int orgUnitGroupCount = 0;
        int slNo = 0;
        double[][] groupTotal = new double[ougmemberCountList.size()][deCodesList.size()];
        while ( it.hasNext() )
        {
            System.out.println(orgUnitCount+" :: "+orgUnitGroupCount);
            OrganisationUnit orgUnit = (OrganisationUnit) it.next();
            Iterator it1 = deCodesList.iterator();
            int count1 = 0;
            if(reportModelTB.equalsIgnoreCase( "DYNAMIC-GROUP" ))
            {
                if(orgUnitCount == ougmemberCountList.get( orgUnitGroupCount ))
                {
                    orgUnitGroupCount++;
                    slNo = 0;
                }
            }
            
            while ( it1.hasNext() )
            {
                String deCodeString = (String) it1.next();
                String deType = (String) deCodeType.get( count1 );
                String sType = (String) serviceType.get( count1 );
                int count = 0;
                double sum = 0.0;
                int flag1 = 0;
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>(getStartingEndingPeriods( deType ));
                if(calendarList == null || calendarList.isEmpty())
                {
                    //tempStartDate.setTime( selectedPeriod.getStartDate());
                    //tempEndDate.setTime( selectedPeriod.getEndDate());
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }
                //System.out.println("StartDate : "+tempStartDate.getTime()+" EndDate : "+tempEndDate.getTime());                
                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = orgUnit.getShortName();
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" ) )
                {
                    tempStr = orgUnitGroupList.get( orgUnitGroupCount ).getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ))
                {
                    //tempStr = format.formatDate( sDate ) + " - " + format.formatDate( eDate );
                    tempStr = simpleDateFormat.format( eDate );                    
                }
                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = ""+(slNo+1);
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else
                {  
                    int years = tempEndDate.get( Calendar.YEAR ) - tempStartDate.get( Calendar.YEAR )-1;
                    //if(years <=0 ) years = 0;
                    int months = (13 - tempStartDate.get( Calendar.MONTH ))+tempEndDate.get( Calendar.MONTH );                     
                    int tempMonthCount = months + (years * 12);
                    
                    System.out.println("BEFORE deCodeString : "+deCodeString+",  monthCount: "+tempMonthCount);
                    deCodeString = deCodeString.replaceAll( "MONTHCOUNT", ""+ tempMonthCount);
                    System.out.println("AFTER deCodeString : "+deCodeString);
                    if(sType.equalsIgnoreCase( "dataelement" ) || sType.equalsIgnoreCase( "dataelement-percentage"))
                        tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), orgUnit );
                    else
                        tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), orgUnit );
                }
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 )+orgUnitGroupCount;
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
               
                /*if(tempStr == null || tempStr.equals( " " ))
                {
                    
                }
                else*/
                {
                    if(reportModelTB.equalsIgnoreCase( "DYNAMIC-GROUP" ))
                    {
                        if(deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" ))
                        {
                            
                        }
                        else
                        {
                            tempRowNo += slNo;
                        }
                                                                        
                        //sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr ) );
                        WritableCell cell = sheet0.getWritableCell(tempColNo, tempRowNo);

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();                        
                        
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                        System.out.println("tempStr:"+tempStr+" ::: CellType : "+cell.getType().toString());
                        if (cell.getType() == CellType.LABEL)
                        {
                          Label l = (Label) cell;
                          l.setString(tempStr);
                          l.setCellFormat( cellFormat );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat) );
                        }
                     
                        try
                        {
                            groupTotal[orgUnitGroupCount][count1] +=   Double.parseDouble( tempStr );                    
                        }
                        catch(Exception e)
                        {
                            groupTotal[orgUnitGroupCount][count1] += 0.0;
                        }

                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                        tempRowNo += orgUnitCount;
                        if ( count1 == 0 )
                        {
                            if ( orgUnitCount == orgUnitList.size() - 1 )
                                sheet0.addCell( new Label( tempColNo, tempRowNo, " " ) );
                            else
                                sheet0.addCell( new Label( tempColNo, tempRowNo, "" + (orgUnitCount + 1) ) );
                        }
                        else if ( count1 == 1 )
                            sheet0.addCell( new Label( tempColNo, tempRowNo, orgUnit.getName() ) );
                        else
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr ) );
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithoutrootfacility" ) )
                    {
                        tempRowNo += orgUnitCount;
                        if ( count1 == 0 )
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, "" + (orgUnitCount + 1) ) );
                        }
                        else if ( count1 == 1 )
                            sheet0.addCell( new Label( tempColNo, tempRowNo, orgUnit.getName() ) );
                        else
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr ) );
                    }
                    else
                    {                                           
                        //sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr ) );
                        WritableCell cell = sheet0.getWritableCell(tempColNo, tempRowNo);

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();                        
                        
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                        if (cell.getType() == CellType.LABEL)
                        {
                          Label l = (Label) cell;
                          l.setString(tempStr);
                          l.setCellFormat( cellFormat );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat) );
                        }

                    }    
                }    
                dataValueList.add( tempStr );
                // dataValueList.add(deCodeString);
                count1++;
            }// inner while loop end
            orgUnitCount++;
            slNo++;
        }// outer while loop end

        
        if(reportModelTB.equalsIgnoreCase( "DYNAMIC-GROUP" ))
        {
            int groupCount1 = 0;
            while(groupCount1 != ougmemberCountList.size())
            {            
                int colCount1 = 0;
                while(colCount1 != groupTotal[0].length)
                {
                    int sheetNo = sheetList.get( colCount1 )+groupCount1;
                    WritableSheet sheet1 = outputReportWorkbook.getSheet( sheetNo );

                    if(deCodesList.get( colCount1 ).equalsIgnoreCase( "FACILITYGROUP-NOREPEAT" ) || deCodesList.get( colCount1 ).equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                    {
                        
                    }
                    else
                    {
                        int tempRow2 = rowList.get( colCount1 )+ougmemberCountList.get( groupCount1 );
                        int tempCol2 = colList.get( colCount1 );
                        
                        if(deCodesList.get( colCount1 ).equalsIgnoreCase( "SLNO" ))
                        {
                            WritableCell cell = sheet1.getWritableCell(tempCol2, tempRow2);

                            CellFormat cellFormat = cell.getCellFormat();
                            WritableCellFormat wCellformat = new WritableCellFormat();                            
                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                            
                            if (cell.getType() == CellType.LABEL)
                            {
                              Label l = (Label) cell;
                              l.setString(" ");
                              l.setCellFormat( cellFormat );
                            }
                            else
                            {
                                sheet1.addCell( new Label( tempCol2, tempRow2, " ", wCellformat) );
                            }
                            
                            //sheet1.addCell( new Label( tempCol2, tempRow2+1, "Grand Total" ) );
                            cell = sheet1.getWritableCell(tempCol2, tempRow2+1);
                            cellFormat = cell.getCellFormat();
                            if (cell.getType() == CellType.LABEL)
                            {
                              Label l = (Label) cell;
                              l.setString(" ");
                              l.setCellFormat( cellFormat );
                            }
                            else
                            {
                                sheet1.addCell( new Label( tempCol2, tempRow2+1, " ", wCellformat) );
                            }

                        }
                        else if(deCodesList.get( colCount1 ).equalsIgnoreCase( "FACILITY" ))
                        {
                            //sheet1.addCell( new Label( tempCol2, tempRow2, "Total" ) );
                            WritableCell cell = sheet1.getWritableCell(tempCol2, tempRow2);

                            CellFormat cellFormat = cell.getCellFormat();
                            WritableCellFormat wCellformat = new WritableCellFormat();                            
                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                            
                            if (cell.getType() == CellType.LABEL)
                            {
                              Label l = (Label) cell;
                              l.setString("Total");
                              l.setCellFormat( cellFormat );
                            }
                            else
                            {
                                sheet1.addCell( new Label( tempCol2, tempRow2, "Total", wCellformat) );
                            }
                            
                            //sheet1.addCell( new Label( tempCol2, tempRow2+1, "Grand Total" ) );
                            cell = sheet1.getWritableCell(tempCol2, tempRow2+1);
                            cellFormat = cell.getCellFormat();
                            if (cell.getType() == CellType.LABEL)
                            {
                              Label l = (Label) cell;
                              l.setString("Grand Total");
                              l.setCellFormat( cellFormat );
                            }
                            else
                            {
                                sheet1.addCell( new Label( tempCol2, tempRow2+1, "Grand Total", wCellformat) );
                            }
                            
                        }
                        else
                        {
                            
                            double tempGroupTotal = groupTotal[groupCount1][colCount1];
                            if(serviceType.get( colCount1 ).equalsIgnoreCase( "indicator" ) || serviceType.get( colCount1 ).equalsIgnoreCase( "dataelement-percentage" ))
                                tempGroupTotal /= ougmemberCountList.get( groupCount1 );
                            tempGroupTotal = Math.round( tempGroupTotal * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                            
                            //sheet1.addCell( new Label( tempCol2, tempRow2, ""+tempGroupTotal ) );
                            WritableCell cell = sheet1.getWritableCell(tempCol2, tempRow2);

                            CellFormat cellFormat = cell.getCellFormat();
                            WritableCellFormat wCellformat = new WritableCellFormat();                            
                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                            
                            if (cell.getType() == CellType.LABEL)
                            {
                              Label l = (Label) cell;
                              l.setString(""+tempGroupTotal);
                              l.setCellFormat( cellFormat );
                            }
                            else
                            {
                                sheet1.addCell( new Label( tempCol2, tempRow2, ""+tempGroupTotal, wCellformat) );
                            }

                            double tempGrandTotal1 = 0.0;
                            for(int i = 0;i<groupTotal.length;i++)
                            {
                                tempGrandTotal1 += groupTotal[i][colCount1];
                            }
                            if(serviceType.get( colCount1 ).equalsIgnoreCase( "indicator" ) || serviceType.get( colCount1 ).equalsIgnoreCase( "dataelement-percentage" ))
                                tempGrandTotal1 /= orgUnitList.size();
                            tempGrandTotal1 = Math.round( tempGrandTotal1 * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                            
                            //sheet1.addCell( new Label( tempCol2, tempRow2+1, ""+tempGrandTotal1 ) );
                            WritableCell cell1 = sheet1.getWritableCell(tempCol2, tempRow2+1);

                            CellFormat cellFormat1 = cell1.getCellFormat();
                            WritableCellFormat wCellformat1 = new WritableCellFormat();                            
                            wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                            
                            if (cell1.getType() == CellType.LABEL)
                            {
                              Label l = (Label) cell1;
                              l.setString(""+tempGrandTotal1);
                              l.setCellFormat( cellFormat1 );
                            }
                            else
                            {
                                sheet1.addCell( new Label( tempCol2, tempRow2+1, ""+tempGrandTotal1, wCellformat1) );
                            }

                        }
                    }
                    colCount1++;
                }
                groupCount1++;
            }
        }    
        /*
         * ActionContext ctx = ActionContext.getContext(); HttpServletResponse
         * res = (HttpServletResponse) ctx.get(
         * ServletActionContext.HTTP_RESPONSE );
         * 
         * res.setContentType("application/vnd.ms-excel");
         */

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + format.formatDate( sDate ) + " - " + format.formatDate( eDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        // inputStream.close();
        // UUID.randomUUID().toString();
        // outputReportFile.canWrite();
        outputReportFile.deleteOnExit();

        return SUCCESS;
    }

    private List<OrganisationUnitGroup> getOrgUnitGroupsFromXML()
    {
        List<OrganisationUnitGroup> orgList = new ArrayList<OrganisationUnitGroup>();
        
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + "orgUnitGroupList.xml";
        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + "dhis" + File.separator + "ra_national" + File.separator + "orgUnitGroupList.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
                return null;
            }

            NodeList listOfOrgUnitGroups = doc.getElementsByTagName( "orgunitgroup" );
            int totalOrgUnitGroups = listOfOrgUnitGroups.getLength();

            for ( int s = 0; s < totalOrgUnitGroups; s++ )
            {
                Element ougElement = (Element) listOfOrgUnitGroups.item( s );
                NodeList textOUGList = ougElement.getChildNodes();
                int ougId =  Integer.parseInt( ((Node) textOUGList.item( 0 )).getNodeValue().trim() );
                OrganisationUnitGroup oug = organisationUnitGroupService.getOrganisationUnitGroup(ougId);
                orgList.add( oug );
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
        return orgList;

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
            tempStartDate.setTime( sDate );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( eDate );
            // System.out.println("We are in PT if block");
        }
        else if ( deType.equalsIgnoreCase( "ccmpy" ) )
        {
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );

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
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );
        
        return calendarList;
    }
    
    /*
     * Returns Previous Month's Period object For ex:- selected period is
     * Aug-2007 it returns the period object corresponding July-2007
     */
    public Period getPreviousPeriod()
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = reportService.getPeriodTypeObject( "monthly" );
        period = reportService.getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            periodType );

        return period;
    }

    /*
     * Returns a list which contains the DataElementCodes
     */
    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + "dhis" + File.separator + "ra_national" + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
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

                // System.out.println(deCodes.get( s )+" : "+deCodeType.get( s
                // ));
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

    /*
     * Returns the PeriodType Object for selected DataElement, If no PeriodType
     * is found then by default returns Monthly Period type
     */
    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetStore.getAllDataSets() );
        Iterator it = dataSetList.iterator();
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

    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @return The generated expression
     */
    private String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
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
                String optionComboIdStr = replaceString.substring( replaceString.indexOf('.')+1, replaceString.length() );
                
                replaceString = replaceString.substring( 0, replaceString.indexOf('.') );
                
                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );                

                
                DataElement dataElement = dataElementService.getDataElement( dataElementId );                
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if(dataElement == null || optionCombo == null)
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if(dataElement.getType().equalsIgnoreCase( "int" ))
                {                
                    double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo, startDate, endDate, organisationUnit );                
                    //System.out.println(aggregatedValue+" ---- "+startDate.toString()+" ---- "+endDate.toString());                
                    if ( aggregatedValue == AggregationService.NO_VALUES_REGISTERED )
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
                    PeriodType dePeriodType = getDataElementPeriodType(dataElement);
                    List<Period> periodList = new ArrayList<Period>(periodService.getIntersectingPeriodsByPeriodType( dePeriodType, startDate, endDate ));
                    Period tempPeriod = new Period();
                    if(periodList == null || periodList.isEmpty()) 
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get(0);
                    }
                    
                    DataValue dataValue= dataValueService.getDataValue(organisationUnit, dataElement, tempPeriod, optionCombo);
       
                    if(dataValue != null) replaceString = dataValue.getValue();
                    else replaceString = "";

                }
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            if(deFlag1 == 0)
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression(buffer.toString());
                }
                catch(Exception e)
                {
                    d = 0.0;
                }
                if(d == -1) d = 0.0;
                else 
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = ""+ (int)d;  
                }
                
                if(deFlag2 == 0)
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
                                
                replaceString = replaceString.substring( 0, replaceString.indexOf('.') );
                
                int indicatorId = Integer.parseInt( replaceString );                                
                
                Indicator indicator = indicatorService.getIndicator( indicatorId );                                

                if(indicator == null)
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }

                double aggregatedValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate, organisationUnit );                
                if ( aggregatedValue == AggregationService.NO_VALUES_REGISTERED )
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
            if(deFlag1 == 0)
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression(buffer.toString());
                }
                catch(Exception e)
                {
                    d = 0.0;
                }
                if(d == -1) d = 0.0;
                else 
                {
                    d = Math.round( d * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                    resultValue = ""+ d;  
                }
                
                if(deFlag2 == 0)
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

}
