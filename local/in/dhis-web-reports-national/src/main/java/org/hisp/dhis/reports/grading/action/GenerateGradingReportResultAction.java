package org.hisp.dhis.reports.grading.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Orientation;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
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

public class GenerateGradingReportResultAction extends ActionSupport
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

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
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

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + "Grading.xls";

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
                                    
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        orgUnitGroupList = new ArrayList<OrganisationUnitGroup>();
        
        if ( ougSetCB == null )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        }
        else
        {
            Iterator<String> it7 = orgUnitListCB.iterator();
            while(it7.hasNext())
            {
                OrganisationUnitGroup oug = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( (String) it7.next() ) );
                List<OrganisationUnit> tempOUList = new ArrayList<OrganisationUnit>(oug.getMembers());
                Collections.sort( tempOUList, new IdentifiableObjectNameComparator());
                orgUnitList.addAll( tempOUList );
            }                    
        }
        
        // Period Info
        sDate = format.parseDate( startDate );            
        eDate = format.parseDate( endDate );            
        
        //Period previousPeriod = new Period();
        //previousPeriod = getPreviousPeriod();
        
        // Getting DataValues
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );                                
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        WritableCell cell;
        CellFormat cellFormat;
                
        wCellformat.setAlignment( Alignment.RIGHT );
        sheet0.addCell( new Label( 0, 1, simpleDateFormat.format( eDate ), wCellformat) );
        sheet0.addCell( new Label( 0, 2, selectedOrgUnit.getShortName(), wCellformat) );
                
        String heading1 = "";
        String heading2 = "";
        
        int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit.getId() );
        //int selOULevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit );
        int maxOULevels = organisationUnitService.getNumberOfOrganisationalLevels();
        
        if( selOULevel < maxOULevels-1 )
        {
        	heading1 = organisationUnitService.getOrganisationUnitLevel( selOULevel + 1 ).getName();
        	heading2 = organisationUnitService.getOrganisationUnitLevel( selOULevel + 2 ).getName();
        }
        	
        /*
        if(organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit) == 2)
        {
            heading1 = "Taluk / Block";
            heading2 = "Name of PHC/ CHC/ PPU/FRU";
        }
        else if(organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit) == 1)
        {
            heading1 = "District";
            heading2 = "Taluk / Block";
        }
        else if(organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit) == 3)
        {
            heading1 = "PHC";
            heading2 = "Subcentre";
        }
        */
        
        cell = sheet0.getWritableCell(1, 3);
        cellFormat = cell.getCellFormat();
        if (cell.getType() == CellType.LABEL)
        {
            Label l = (Label) cell;
            l.setString(heading1);
            l.setCellFormat( cellFormat );
        }
        else
        {
            wCellformat = getCellFormat1();            
            sheet0.addCell( new Label( 1, 3, heading1, wCellformat) );
        }    
            
        cell = sheet0.getWritableCell(2, 3);
        cellFormat = cell.getCellFormat();
        if (cell.getType() == CellType.LABEL)
        {
            Label l = (Label) cell;
            l.setString(heading2);
            l.setCellFormat( cellFormat );
        }
        else
        {
            wCellformat = getCellFormat1();            
            sheet0.addCell( new Label( 2, 3, heading2, wCellformat) );
        }
        
                
        int orgUnitCount = 0;
        int childRowCount = 6;
        int childchildRowCount = 6;
        int slNo = 1;
                        
            Iterator<OrganisationUnit> it1 = orgUnitList.iterator();
            while ( it1.hasNext() )
            {
                OrganisationUnit orgUnit = (OrganisationUnit) it1.next();
                if(orgUnit.getChildren().isEmpty())
                    continue;
                wCellformat =  getCellFormat1();
                sheet0.addCell( new Label( 1, childRowCount, orgUnit.getShortName(), wCellformat) );
                childRowCount += orgUnit.getChildren().size();            
                            
                List<OrganisationUnit> cOUList = new ArrayList<OrganisationUnit>(orgUnit.getChildren());
                Collections.sort( cOUList, new IdentifiableObjectNameComparator() );
                
                Iterator<OrganisationUnit> it2 = cOUList.iterator();
                while( it2.hasNext() )
                {
                 
                    OrganisationUnit ccOU = (OrganisationUnit) it2.next();
                    wCellformat =  getCellFormat2();
                    sheet0.addCell( new Label( 0, childchildRowCount, ""+slNo, wCellformat) );
                    sheet0.addCell( new Label( 2, childchildRowCount, ccOU.getShortName(), wCellformat) );
                    childchildRowCount++;                
                    
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
                            
                            System.out.println("BEFORE deCodeString : "+deCodeString+",  monthCount: "+tempMonthCount);
                            deCodeString = deCodeString.replaceAll( "MONTHCOUNT", ""+ tempMonthCount);
                            System.out.println("AFTER deCodeString : "+deCodeString);
                            if(count1 == 4) // For Inst. Deliveries
                            {
                                //double tempd1 = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU, sType, con);
                            	String tempStr1 = getResultDataValue(  deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU );
                            	double tempd1 = 0.0;
                            	try
                            	{
                            		tempd1 = Double.parseDouble( tempStr1 );
                            	}
                            	catch( Exception e )
                            	{
                            		tempd1 = 0.0;
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
                                    tempStr1 = getResultDataValue(  tempdeCodeString, tempStartDate.getTime(), tempEndDate.getTime(), tempOU );
                                    tempd1 = Double.parseDouble( tempStr1 );
                                    
                                    try
                                    {
                                    	tempd1 = Double.parseDouble( tempStr1 );
                                        resultVal /= tempd1;
                                    }
                                    catch(Exception e)
                                    {
                                    
                                    }
                                }
                            }
                            else
                            {
                                double tempd1 = 0.0;
                                String tempStr1;
                                
                                if(sType.equalsIgnoreCase( "dataelement" ) || sType.equalsIgnoreCase( "dataelement-percentage"))
                                    //tempd1 = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU, sType, con);
                                	tempStr1 = getResultDataValue(  deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU );                                	
                                else
                                    // tempd1 = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU, con );                                	
                                	tempStr1 = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), ccOU );
                            
                                
                                
                                try
                                {
                                	tempd1 = Double.parseDouble( tempStr1 );
                                	
                                    resultVal = tempd1;
                                }
                                catch(Exception e)
                                {
                                    resultVal = 0.0;
                                }                            
                            }
                                                    
                            
                            if(resultVal > maxMarks[count1]) 
                                resultVal = maxMarks[count1];
                            
                            resultVal = Math.round( resultVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        }
                        int tempRowNo = rowList.get( count1 )+slNo-1;
                        int tempColNo = colList.get( count1 );
                        int sheetNo = 0;
                        
                        sheet0 = outputReportWorkbook.getSheet( sheetNo );
                       
                        wCellformat = new WritableCellFormat();                                                    
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        sheet0.addCell( new Number( tempColNo, tempRowNo, resultVal, wCellformat) );
                        
                        if(count1==deCodesList.size()-1)
                        {
                            tempStr = "SUM(D"+(tempRowNo+1)+":P"+(tempRowNo+1)+")";
                            wCellformat = new WritableCellFormat();                                                    
                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                            wCellformat.setAlignment( Alignment.CENTRE );
                            sheet0.addCell( new Formula( tempColNo+1, tempRowNo, tempStr, wCellformat) );
    
                            tempStr = "IF(Q"+(tempRowNo+1)+">90,\"A\",IF(AND(Q"+(tempRowNo+1)+"<=90,Q"+(tempRowNo+1)+">80),\"B\",IF(AND(Q"+(tempRowNo+1)+"<=80,Q"+(tempRowNo+1)+">70),\"C\",\"D\")))";
                            
                            wCellformat = new WritableCellFormat();                                                    
                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                            wCellformat.setAlignment( Alignment.CENTRE );
                            sheet0.addCell( new Formula( tempColNo+2, tempRowNo, tempStr, wCellformat) );
                            
                            String tempStr1 = "Q"+(tempRowNo+1);
                            WritableCell cell1 = sheet0.getWritableCell(tempStr1);
                            
                            tempStr = cell1.getContents();
                            System.out.println("CellContent : "+ tempStr);
                            if(tempStr.equalsIgnoreCase( "a" ))
                            {
                                sheet0.addCell( new Formula( tempColNo+2, tempRowNo, tempStr, getCellFormatA()) );
                            }
                            else if(tempStr.equalsIgnoreCase( "b" ))
                            {
                                sheet0.addCell( new Formula( tempColNo+2, tempRowNo, tempStr, getCellFormatB()) );
                            }
                            else if(tempStr.equalsIgnoreCase( "c" ))
                            {
                                sheet0.addCell( new Formula( tempColNo+2, tempRowNo, tempStr, getCellFormatC()) );
                            }
                            else if(tempStr.equalsIgnoreCase( "d" ))
                            {
                                sheet0.addCell( new Formula( tempColNo+2, tempRowNo, tempStr, getCellFormatD()) );
                            }

                        }
    
                        count1++;
                    }// Decode while loop end
                    slNo++;
                }// Children Children While loop                                    
                orgUnitCount++;            
            }// children while loop end
        
        slNo += 7;
        wCellformat = new WritableCellFormat();                                                    
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        sheet0.addCell( new Label( 5, slNo, "#", wCellformat) );
        sheet0.mergeCells( 6, slNo, 10, slNo );
        sheet0.addCell( new Label( 6, slNo, heading1, wCellformat) );
        sheet0.addCell( new Label( 11, slNo, "A", wCellformat) );
        sheet0.addCell( new Label( 12, slNo, "B", wCellformat) );
        sheet0.addCell( new Label( 13, slNo, "C", wCellformat) );
        sheet0.addCell( new Label( 14, slNo, "D", wCellformat) );
        sheet0.addCell( new Label( 15, slNo, "Total", wCellformat) );
        
        slNo++;
        int count1 = 1;
        
            Iterator<OrganisationUnit> it4 = orgUnitList.iterator();
            while(it4.hasNext())
            {
                OrganisationUnit orgUnit = (OrganisationUnit) it4.next();
                
                if(orgUnit.getChildren().size() == 0) continue;
                
                int endingRowNumber = startingRowNumber+orgUnit.getChildren().size()-1;
    
                sheet0.addCell( new Label( 5, slNo, ""+count1, wCellformat) );
                sheet0.addCell( new Label( 6, slNo, orgUnit.getShortName(), wCellformat) );
                sheet0.mergeCells( 6, slNo, 10, slNo );
                            
                String formula1 = "COUNTIF(R"+startingRowNumber+":R"+endingRowNumber+",\"A\")";
                sheet0.addCell( new Formula( 11, slNo, formula1, wCellformat) );
                
                formula1 = "COUNTIF(R"+startingRowNumber+":R"+endingRowNumber+",\"B\")";
                sheet0.addCell( new Formula( 12, slNo, formula1, wCellformat) );
                
                formula1 = "COUNTIF(R"+startingRowNumber+":R"+endingRowNumber+",\"C\")";
                sheet0.addCell( new Formula( 13, slNo, formula1, wCellformat) );
                
                formula1 = "COUNTIF(R"+startingRowNumber+":R"+endingRowNumber+",\"D\")";
                sheet0.addCell( new Formula( 14, slNo, formula1, wCellformat) );
                
                formula1 = "SUM(L"+(slNo+1)+":O"+(slNo+1)+")";
                sheet0.addCell( new Formula( 15, slNo, formula1, wCellformat) );
                
                startingRowNumber = endingRowNumber+1;
                slNo++;
                count1++;
            }
        
        if ( ougSetCB == null )
        {
            sheet0.addCell( new Label( 5, slNo, " ", wCellformat) );
            sheet0.addCell( new Label( 6, slNo, selectedOrgUnit.getShortName(), wCellformat) );
            sheet0.mergeCells( 6, slNo, 10, slNo );
            String formula1 = "SUM(L"+(slNo-count1+1)+":L"+slNo+")";
            sheet0.addCell( new Formula( 11, slNo, formula1, wCellformat) );
            formula1 = "SUM(M"+(slNo-count1+1)+":M"+slNo+")";
            sheet0.addCell( new Formula( 12, slNo, formula1, wCellformat) );
            formula1 = "SUM(N"+(slNo-count1+1)+":N"+slNo+")";
            sheet0.addCell( new Formula( 13, slNo, formula1, wCellformat) );
            formula1 = "SUM(O"+(slNo-count1+1)+":O"+slNo+")";
            sheet0.addCell( new Formula( 14, slNo, formula1, wCellformat) );
            formula1 = "SUM(P"+(slNo-count1+1)+":P"+slNo+")";
            sheet0.addCell( new Formula( 15, slNo, formula1, wCellformat) );
        }
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

    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        
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
    
    public PeriodType getPeriodTypeObject(String periodTypeName)
    {        
        Collection<PeriodType> periodTypes = null;
        periodTypes = periodService.getAllPeriodTypes(); 
        PeriodType periodType = null;
        Iterator<PeriodType> iter = periodTypes.iterator();
        while ( iter.hasNext() )
        {
            PeriodType tempPeriodType = iter.next();
            if ( tempPeriodType.getName().toLowerCase().trim().equals( periodTypeName ) )
            {
                periodType = tempPeriodType;
                break;
            }
        }
        if ( periodType == null )
        {
            System.out.println( "No Such PeriodType" );
            return null;
        }        
        return periodType;
    }
    
    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equals( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }        
        Date lastDay = new Date( cal.getTimeInMillis() );
        System.out.println( lastDay.toString() );        
        Period newPeriod = new Period();
        newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );      
        return newPeriod;
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
        PeriodType periodType = getPeriodTypeObject( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
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
