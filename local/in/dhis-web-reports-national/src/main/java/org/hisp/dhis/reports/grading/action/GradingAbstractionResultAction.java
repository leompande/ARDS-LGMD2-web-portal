package org.hisp.dhis.reports.grading.action;

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
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Orientation;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
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
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reports.util.DBConnection;
import org.hisp.dhis.reports.util.ReportService;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.ActionSupport;

public class GradingAbstractionResultAction  extends ActionSupport
{
    private static final String NULL_REPLACEMENT = "0";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
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

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    private OrganisationUnit selectedOrgUnit;

    public OrganisationUnit getSelectedOrgUnit()
    {
        return selectedOrgUnit;
    }

    private List<OrganisationUnitGroup> orgUnitGroupList;
    
    public List<OrganisationUnitGroup> getOrgUnitGroupList()
    {
        return orgUnitGroupList;
    }

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

    private String summaryByCB;
    
    public void setSummaryByCB( String summaryByCB )
    {
        this.summaryByCB = summaryByCB;
    }

    private String ougSetCB;

    public void setOugSetCB( String ougSetCB )
    {
        this.ougSetCB = ougSetCB;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date sDate;
    private Date eDate;
    
    private Map<OrganisationUnit,Integer> gradeATotal;
    private Map<OrganisationUnit,Integer> gradeBTotal;
    private Map<OrganisationUnit,Integer> gradeCTotal;
    private Map<OrganisationUnit,Integer> gradeDTotal;
    
    private int selLevel;
    
    private Connection con = null;
    
    private String raFolderName;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        con = (new DBConnection()).openConnection();
        
        statementManager.initialise();

        // Initialization
        
        raFolderName = reportService.getRAFolderName();
        
        int maxMarks[] = {10,8,7,8,10,7,5,5,5,5,5,13,12};
        int startingRowNumber = 7;
        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();        
        String deCodesXMLFileName = "gjgradingDECodes.xml";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ));
                                    
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( (String) orgUnitListCB.get( 0 ) ) );
        orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );

        // Period Info
        sDate = format.parseDate( startDate );            
        eDate = format.parseDate( endDate );            

        // Getting DataValues
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "GradingAbstratction", 0 );                                
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        WritableCell cell;
        CellFormat cellFormat;

        wCellformat.setAlignment( Alignment.RIGHT );
        sheet0.addCell( new Label( 0, 1, simpleDateFormat.format( eDate ), wCellformat) );
        sheet0.addCell( new Label( 0, 2, selectedOrgUnit.getShortName(), wCellformat) );
                
        String heading1 = "";
        String heading2 = "";
        String reportHeading = "";
        
        
        int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit.getId() );
        //int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit );
        int maxOULevels = organisationUnitService.getNumberOfOrganisationalLevels();
        
        if( selOULevel < maxOULevels-1 )
        {
        	heading1 = organisationUnitService.getOrganisationUnitLevel( selOULevel + 1 ).getName();        	
        }

        if(summaryByCB.equalsIgnoreCase( "level-0" ))
        {
            reportHeading = "Summary of Statewise Grading by ";
            selLevel = 1;            
        }
        else if(summaryByCB.equalsIgnoreCase( "level-1" ))
        {
            reportHeading = "Summary of Districtwise Grading by ";
            selLevel = 2;
        }
        else if(summaryByCB.equalsIgnoreCase( "level-2" ))
        {
            reportHeading = "Summary of Blockwise Grading by ";
            selLevel = 3;
        }
        else if(summaryByCB.equalsIgnoreCase( "level-3" ))
        {
            reportHeading = "Summary of PHCwise Grading by ";
            selLevel = 4;
        }
        else
        {
            reportHeading = "Summary of Subcentrewise Grading by ";
            selLevel = 5;
        }
            
        if( organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit.getId() ) == 2)
        {
            reportHeading += "Block, "+simpleDateFormat.format( eDate );
            heading1 = "Taluk / Block";            
        }
        else if(organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit.getId() ) == 1)
        {
            reportHeading += "District, "+simpleDateFormat.format( eDate );
            heading1 = "District";            
        }
        else if(organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit.getId() ) == 3)
        {
            reportHeading += "PHC, "+simpleDateFormat.format( eDate );
            heading1 = "PHC";            
        }

        
        CellView cv1 = new CellView();
        cv1.setSize( 25*256 );
        sheet0.setColumnView(1, cv1);

        CellView cv2 = new CellView();
        cv2.setSize( 3*256 );
        sheet0.setColumnView(0, cv2);

        sheet0.addCell( new Label( 0, 0, reportHeading, getReportHeadingFormat()) );
        sheet0.mergeCells( 0, 0, 11, 0 );
        
        sheet0.addCell( new Label( 0, 1, "NO", getCellHeadingFormat()) );
        sheet0.mergeCells( 0, 1, 0, 2 );
        sheet0.addCell( new Label( 1, 1, heading1, getCellHeadingFormat()) );
        sheet0.mergeCells( 1, 1, 1, 2 );
        
        
        sheet0.addCell( new Label( 2, 1, "No.s", getCellHeadingFormat()) );
        sheet0.mergeCells( 2, 1, 6, 1 );
        sheet0.addCell( new Label( 7, 1, "Percentage to Total No.s", getCellHeadingFormat()) );
        sheet0.mergeCells( 7, 1, 11, 1 );
        
        sheet0.addCell( new Label( 2, 2, "A", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 3, 2, "B", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 4, 2, "C", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 5, 2, "D", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 6, 2, "Total", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 7, 2, "A", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 8, 2, "B", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 9, 2, "C", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 10, 2, "D", getCellHeadingFormat()) );
        sheet0.addCell( new Label( 11, 2, "Total", getCellHeadingFormat()) );        
        
        
        int tempCol1 = 2;
        int tempRow1 = 3;                        
        int orgUnitCount = 1;
        int childRowCount = 3;
        int childchildRowCount = 6;
        int slNo = 1;
                        
            Iterator<OrganisationUnit> it1 = orgUnitList.iterator();
            while ( it1.hasNext() )
            {
                OrganisationUnit orgUnit = (OrganisationUnit) it1.next();
                if(orgUnit.getChildren().isEmpty())
                    continue;
                wCellformat =  getCellFormat1();
                sheet0.addCell( new Label( 0, childRowCount, ""+orgUnitCount, wCellformat) );
                sheet0.addCell( new Label( 1, childRowCount, orgUnit.getShortName(), getCellFormat2()) );
                childRowCount++;            
                            
                List<OrganisationUnit> cOUList = new ArrayList<OrganisationUnit>();
                cOUList = getChildList(orgUnit);
                Collections.sort( cOUList, new IdentifiableObjectNameComparator() );
                
                int gradeATotal = 0;
                int gradeBTotal = 0;
                int gradeCTotal = 0;
                int gradeDTotal = 0;
                int gradeTotal = 0;
                //int totalMarks = 0;
                                
                Iterator<OrganisationUnit> it2 = cOUList.iterator();
                while( it2.hasNext() )
                {                 
                    double totalMarks = 0;
                    OrganisationUnit ccOU = (OrganisationUnit) it2.next();
                                        
                    int count1 = 0;
                    Iterator<String> it3 = deCodesList.iterator();                                
                    while ( it3.hasNext() )
                    {                    
                        String deCodeString = (String) it3.next();
                        String deType = (String) deCodeType.get( count1 );
                        String sType = (String) serviceType.get( count1 );
                        String tempStr = "";
                        double resultVal = 0.0;
                        
                        Calendar tempStartDate = Calendar.getInstance();
                        Calendar tempEndDate = Calendar.getInstance();
                        List<Calendar> calendarList = new ArrayList<Calendar>(getStartingEndingPeriods( deType ));
                        if(calendarList == null || calendarList.isEmpty())
                        {
                            return SUCCESS;
                        }
                        else
                        {
                            tempStartDate = calendarList.get( 0 );
                            tempEndDate = calendarList.get( 1 );
                        }                
                        
                        if ( deCodeString.equalsIgnoreCase( "NA" ) )
                        {
                            tempStr = " ";
                        }
                        else
                        {  
                            int years = tempEndDate.get( Calendar.YEAR ) - tempStartDate.get( Calendar.YEAR )-1;
                            //if(years <=0 ) years = 0;
                            int months = (13 - tempStartDate.get( Calendar.MONTH ))+tempEndDate.get( Calendar.MONTH );                     
                            int tempMonthCount = months + (years * 12);
                            
                            deCodeString = deCodeString.replaceAll( "MONTHCOUNT", ""+ tempMonthCount);

                            if(count1 == 4) // For Inst. Deliveries
                            {
                                //double tempd1 = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU, sType, con);
                                String tempStr1 = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU );
                                
                                double tempd1 = 0.0;
                                
                                try
                                {
                                	tempd1 = Double.parseDouble( tempStr1 );
                                }
                                catch( Exception e )
                                {
                                	
                                }
                                
                                resultVal = tempd1;
                                
                                OrganisationUnit tempOU = new OrganisationUnit();
                                
                                if(organisationUnitService.getLevelOfOrganisationUnit( ccOU.getId() ) == 2) 
                                    tempOU = ccOU.getParent();
                                else if(organisationUnitService.getLevelOfOrganisationUnit( ccOU.getId() ) == 3)
                                    tempOU = ccOU.getParent();
                                else if(organisationUnitService.getLevelOfOrganisationUnit( ccOU.getId() ) == 4)
                                    tempOU = ccOU.getParent().getParent();
                                else if(organisationUnitService.getLevelOfOrganisationUnit( ccOU.getId() ) == 5)
                                    tempOU = ccOU.getParent().getParent();
                                
                                if(organisationUnitService.getLevelOfOrganisationUnit( ccOU.getId() ) != 1)
                                {
                                    String tempdeCodeString = "([1251.1]+[1252.1]+[1253.1]+[1254.1]+[1255.1]+[26.1])*100/([14.1]+[15.1]+[1251.1]+[1252.1]+[1253.1]+[1254.1]+[1255.1]+[26.1])*10";

                                    //tempd1 = reportService.getResultDataValue( tempdeCodeString, tempStartDate.getTime(), tempEndDate.getTime(), tempOU, sType, con);
                                    tempStr1 = getResultDataValue( tempdeCodeString, tempStartDate.getTime(), tempEndDate.getTime(), tempOU );
                                    try 
                                    {
                                    	tempd1 = Double.parseDouble( tempStr1 );
                                    	
                                    	 resultVal /= tempd1;
                                    }
                                    catch( Exception e )
                                    {
                                    	
                                    }
                                }
                            }
                            else
                            {
                                double tempd1=0.0;
                                String tempStr1;
                                
                                if(sType.equalsIgnoreCase( "dataelement" ) || sType.equalsIgnoreCase( "dataelement-percentage"))
                                    //tempd1 = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU, sType, con);
                                	tempStr1 = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU );
                                else
                                    //tempd1 = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU, con );
                                	tempStr1 = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU );
                                
                                
                                try
                                {
                                	tempd1 = Double.parseDouble( tempStr1 );
                                }
                                catch( Exception e )
                                {
                                	
                                }
                                resultVal = tempd1;
                            }
                            
                            if(resultVal > maxMarks[count1]) 
                                resultVal = maxMarks[count1];
                            
                            resultVal = Math.round( resultVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                            
                            //totalMarks += resultVal;
                        }
                            
                        count1++;
                        totalMarks += resultVal;
                        System.out.println(ccOU.getShortName() + " Result : "+resultVal + " Total Marks : "+totalMarks);
                    }// Decode while loop end

                    System.out.println("**********************************************");
                    System.out.println(ccOU.getShortName() + " Total Marks : "+totalMarks);
                    if(totalMarks > 90 )
                    {
                        gradeATotal++;
                    }
                    else if(totalMarks <=90 && totalMarks > 80)
                    {
                        gradeBTotal++;
                    }
                    else if(totalMarks <=80 && totalMarks > 70)
                    {
                        gradeCTotal++;
                    }
                    else
                    {
                        gradeDTotal++;
                    }
                                                           
                    slNo++;
                }// Children Children While loop
                
                tempCol1 = 2;
                sheet0.addCell( new Number( tempCol1, tempRow1, gradeATotal, wCellformat) );
                tempCol1++;
                sheet0.addCell( new Number( tempCol1, tempRow1, gradeBTotal, wCellformat) );
                tempCol1++;
                sheet0.addCell( new Number( tempCol1, tempRow1, gradeCTotal, wCellformat) );
                tempCol1++;
                sheet0.addCell( new Number( tempCol1, tempRow1, gradeDTotal, wCellformat) );
                tempCol1++;
                gradeTotal = gradeATotal + gradeBTotal + gradeCTotal + gradeDTotal;
                sheet0.addCell( new Number( tempCol1, tempRow1, gradeTotal, wCellformat) );
                tempCol1++;
                
                double tempVal = 0.0;
                try
                {
                    tempVal = ((double) gradeATotal / gradeTotal) * 100;
                    tempVal = Math.round( tempVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                catch(Exception e) { tempVal = 0.0; }
                System.out.println("Percentage : "+gradeATotal+"/"+gradeTotal+ " = "+tempVal);
                sheet0.addCell( new Number( tempCol1, tempRow1, tempVal, wCellformat) );
                tempCol1++;
                
                try
                {
                    tempVal = ((double) gradeBTotal / gradeTotal) * 100;
                    tempVal = Math.round( tempVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                catch(Exception e) { tempVal = 0.0; }
                System.out.println("Percentage : "+gradeBTotal+"/"+gradeTotal+ " = "+tempVal);
                sheet0.addCell( new Number( tempCol1, tempRow1, tempVal, wCellformat) );
                tempCol1++;
                
                try
                {
                    tempVal = ((double) gradeCTotal / gradeTotal) * 100;
                    tempVal = Math.round( tempVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                catch(Exception e) { tempVal = 0.0; }
                System.out.println("Percentage : "+gradeCTotal+"/"+gradeTotal+ " = "+tempVal);
                sheet0.addCell( new Number( tempCol1, tempRow1, tempVal, wCellformat) );
                tempCol1++;
                
                try
                {
                    tempVal = ( (double) gradeDTotal / gradeTotal) * 100;
                    tempVal = Math.round( tempVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                catch(Exception e) { tempVal = 0.0; }                
                System.out.println("Percentage : "+gradeDTotal+"/"+gradeTotal+ " = "+tempVal);
                sheet0.addCell( new Number( tempCol1, tempRow1, tempVal, wCellformat) );
                tempCol1++;
                
                try
                {
                    tempVal = ( (double) gradeTotal / gradeTotal) * 100;
                    tempVal = Math.round( tempVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                catch(Exception e) { tempVal = 0.0; }
                System.out.println("Percentage : "+gradeBTotal+"/"+gradeTotal+ " = "+tempVal);
                sheet0.addCell( new Number( tempCol1, tempRow1, tempVal, wCellformat) );
                tempCol1++;

                orgUnitCount++;
                tempRow1++;
            }// children while loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "Grading_";
        fileName += format.formatDate( sDate ) + " - " + format.formatDate( eDate ) + ".xls";
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

    
    public List<OrganisationUnit> getChildList(OrganisationUnit orgUnit)
    {
        List<OrganisationUnit> ouList = new ArrayList<OrganisationUnit>();
        List<OrganisationUnit> tempOuList = new ArrayList<OrganisationUnit>(organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ));
        
        Iterator<OrganisationUnit> it = tempOuList.iterator();
        while(it.hasNext())
        {
            OrganisationUnit ou = (OrganisationUnit) it.next();
            if(organisationUnitService.getLevelOfOrganisationUnit( ou.getId() ) == selLevel)
            {
                ouList.add( ou );
            }
        }
        
        return ouList;
    }
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellHeadingFormat() throws Exception
    {
        WritableFont wFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat wCellformat = new WritableCellFormat(wFont);                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );        
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getReportHeadingFormat() throws Exception
    {
        WritableFont wFont = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD);
        WritableCellFormat wCellformat = new WritableCellFormat(wFont);                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );        
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
    public WritableCellFormat getCellFormat3() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setOrientation( Orientation.PLUS_90 );

        return wCellformat;
    }

    public WritableCellFormat getCellFormatA() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.BLACK, jxl.format.Pattern.PATTERN1 );

        return wCellformat;
    }

    public WritableCellFormat getCellFormatB() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.BLACK, jxl.format.Pattern.PATTERN2 );

        return wCellformat;
    }

    public WritableCellFormat getCellFormatC() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.BLACK, jxl.format.Pattern.PATTERN3 );

        return wCellformat;
    }

    public WritableCellFormat getCellFormatD() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.BLACK, jxl.format.Pattern.PATTERN4 );

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
            
            /*
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            */
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
        PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
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
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName + File.separator + fileName;
        
        Calendar tempEndDate = Calendar.getInstance();
        tempEndDate.setTime( eDate );
        int selMonth = tempEndDate.get(Calendar.MONTH)+1;
        
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + fileName;
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
                
                int mon = Integer.parseInt( deCodeElement.getAttribute( "month" ) );
                if(mon == selMonth)
                {
                    deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
                    serviceType.add( deCodeElement.getAttribute( "stype" ) );
                    deCodeType.add( deCodeElement.getAttribute( "type" ) );
                    sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                    rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                    colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );
                }    
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
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
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
            // System.out.println( "expression : " + formula + " ***** " +
            // String.valueOf( startDate ) + " **** "
            // + String.valueOf( endDate ) );

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

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

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
                	PeriodType dePeriodType = getDataElementPeriodType( dataElement );                	
                	
                	if( dePeriodType.getName().equalsIgnoreCase(YearlyPeriodType.NAME) )
                	{
                		Calendar tempDate = Calendar.getInstance();
                    	tempDate.setTime( startDate );
                    	
                		tempDate.set(  Calendar.MONTH, Calendar.DECEMBER );
                		
                		endDate = tempDate.getTime();                		
                	}
                	
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );
                    if ( aggregatedValue == null)
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

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

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

                    // This is to display financial data as it is like 2.1476838
                    resultValue = "" + d;

                    // These lines are to display financial data that do not
                    // have decimals
                    d = d * 10;

                    if ( d % 10 == 0 )
                    {
                        resultValue = "" + (int) d / 10;
                    }

                    d = d / 10;

                    // These line are to display non financial data that do not
                    // require decimals
                    //if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                        resultValue = "" + (int) d;

                    // if ( resultValue.equalsIgnoreCase( "0" ) )
                    // {
                    // resultValue = "";
                    // }
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

                    Double aggregatedValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,
                        organisationUnit );

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

}
