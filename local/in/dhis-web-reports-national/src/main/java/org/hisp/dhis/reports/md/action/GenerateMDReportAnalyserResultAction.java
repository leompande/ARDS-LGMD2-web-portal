package org.hisp.dhis.reports.md.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

public class GenerateMDReportAnalyserResultAction implements Action
{
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
/*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format 
    }
*/    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
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

    private Integer selectedStartPeriodId;

    public void setSelectedStartPeriodId( Integer selectedStartPeriodId )
    {
        this.selectedStartPeriodId = selectedStartPeriodId;
    }

    private Integer selectedEndPeriodId;

    public void setSelectedEndPeriodId( Integer selectedEndPeriodId )
    {
        this.selectedEndPeriodId = selectedEndPeriodId;
    }
    
    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }
    
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }
    
    
    private List<OrganisationUnit> orgUnitList;
    
    private String raFolderName;
    
    private SimpleDateFormat simpleDateFormat;

    //private SimpleDateFormat monthFormat;
    
    //private SimpleDateFormat yearFormat;

    //private SimpleDateFormat simpleMonthFormat;
    
    private String reportFileNameTB;

    //private String reportModelTB;
    
    private Date sDate;

    private Date eDate;
    
   // private OrganisationUnit selectedFacilityName;
    
    private List<OrganisationUnit> orgUnitGroupMemberList;
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        // Initialization
        statementManager.initialise();
             
        raFolderName = reportService.getRAFolderName();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        //monthFormat = new SimpleDateFormat( "MMMM" );
        //yearFormat = new SimpleDateFormat( "yyyy" );
        //simpleMonthFormat = new SimpleDateFormat( "MMM" );
        //String parentUnit = "";
        
        Report_in selReportObj =  reportService.getReport( Integer.parseInt( reportList ) );
        
        deCodesXMLFileName = selReportObj.getXmlTemplateName();

         
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );
        
        // Getting selected orgunit and its immediate children
        OrganisationUnit selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        orgUnitList = new ArrayList<OrganisationUnit>( selOrgUnit.getChildren() );
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
        // Org unit Group Information
        OrganisationUnitGroup orgUnitGroup = selReportObj.getOrgunitGroup();        
        orgUnitGroupMemberList = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
        
        // Period Info
        Period selectedStartPeriod = periodService.getPeriod( selectedStartPeriodId );
        Period selectedEndPeriod = periodService.getPeriod( selectedEndPeriodId );
        sDate = selectedStartPeriod.getStartDate();
        eDate = selectedEndPeriod.getEndDate();

        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate() ) );
        Collection<Integer> tempPeriodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
        String periodIdsByComma = getCommaDelimitedString( tempPeriodIds );
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        
        // collect dataElementIDs by commaSepareted
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        
        /*
        Map<String, String> tempAggDeMap = new HashMap<String, String>();
        
        if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
        {
            Iterator<OrganisationUnit> tempOrgUnit = orgUnitList.iterator();
            while ( tempOrgUnit.hasNext() )
            {
                OrganisationUnit orgUnit = (OrganisationUnit) tempOrgUnit.next();
                
                List<OrganisationUnit> tempOrgUnitWithChildTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
                tempOrgUnitWithChildTree.retainAll( orgUnitGroupMemberList );
                
                Iterator<OrganisationUnit> tempCurrIt = tempOrgUnitWithChildTree.iterator();                        
                while ( tempCurrIt.hasNext() )
                {
                    OrganisationUnit tempCurrentOrgUnit = (OrganisationUnit) tempCurrIt.next();
                    
                    List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( tempCurrentOrgUnit.getId() ) );
                    List<Integer> tempchildOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                    String tempchildOrgUnitsByComma = getCommaDelimitedString( tempchildOrgUnitTreeIds );
                    
                    tempAggDeMap.putAll( reportService.getAggDataFromDataValueTable( tempchildOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
                }
            }
        }
        */
        System.out.println( selOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
        
        int orgUnitCount = 0;
        int rowIncr = 0;
        int tempOrgUnitCount = 0;

        int rowStart = 5;
        int colStart = 0;
        
        int rowInc = 0;
        int slno = 1;
        
        Double[] grandTotal = new Double[ reportDesignList.size() ];
        
        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit tempOrgUnit = (OrganisationUnit) it.next();
        
            Map<Integer, Double> subTotalMap = new HashMap<Integer, Double>();
            
            List<OrganisationUnit> orgUnitWithChildTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( tempOrgUnit.getId() ) );
            orgUnitWithChildTree.retainAll( orgUnitGroupMemberList );
            
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitWithChildTree ) );
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
            
            WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
            
            Map<String, String> aggDeMap = new HashMap<String, String>();            
            if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTableForOrgUnitWise( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            }            
            //else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            //{
            //    List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( tempOrgUnit.getId() ) );
            //    List<Integer> tempChildOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
            //    String tempChildOrgUnitsByComma = getCommaDelimitedString( tempChildOrgUnitTreeIds );

            //    aggDeMap.putAll( reportService.getAggDataFromDataValueTable( tempChildOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            //}
            else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromAggDataValueTableForOrgUnitWise( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            }
            
            Double tempSubTotal = 0.0;
            
            //List<String> tempTotal = new ArrayList<String>();
            int tempOuMemeberCount = 0;
            Iterator<OrganisationUnit> currIt = orgUnitWithChildTree.iterator();                        
            while ( currIt.hasNext() )
            {
                OrganisationUnit currentOrgUnit = (OrganisationUnit) currIt.next();
                
                //12/12/2011
                Map<String, String> tempAggDeMap = new HashMap<String, String>();
                if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                {
                    List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                    List<Integer> tempchildOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                    String tempchildOrgUnitsByComma = getCommaDelimitedString( tempchildOrgUnitTreeIds );
                    
                    tempAggDeMap.putAll( reportService.getAggDataFromDataValueTable( tempchildOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
                }
                //12/12/2011
                sheet0.addCell( new Number( colStart, rowStart+rowInc, slno, getCellFormat1() ) );
                sheet0.addCell( new Label( colStart+1, rowStart+rowInc, tempOrgUnit.getName(), getCellFormat1() ) );
                sheet0.addCell( new Label( colStart+2, rowStart+rowInc, currentOrgUnit.getName(), getCellFormat1() ) );
                
                int count1 = 0;
                Integer deCount = 0;
                Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();                
                while ( reportDesignIterator.hasNext() )
                {
                    Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                    //String deType = report_inDesign.getPtype();
                    String sType = report_inDesign.getStype();
                    String deCodeString = report_inDesign.getExpression();
                    String tempStr = "";
                   // String tempSubTotal = "";
                   
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                    {
                        tempStr = selOrgUnit.getName();
                    }
                    else if( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                    {
                        tempStr = simpleDateFormat.format( sDate ) + " To " + simpleDateFormat.format( eDate );
                    }                     
                    else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                    {
                        tempStr = " ";
                    }
                    else
                    {
                        deCount++;
                        if( sType.equalsIgnoreCase( "dataelement" ) )
                        {
                            if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                            {
                                Double tempAggValue = 0.0;
                                tempStr = getAggVal( deCodeString, currentOrgUnit.getId(), aggDeMap );
                                
                                try
                                {
                                    tempAggValue = Double.parseDouble( tempStr );
                                }
                                catch ( Exception e )
                                {
                                    tempAggValue = 0.0;
                                }
                                tempSubTotal += tempAggValue;
                                
                                if( grandTotal[deCount] != null )
                                {
                                    grandTotal[deCount] += tempAggValue;
                                }
                                else
                                {
                                    grandTotal[deCount] = tempAggValue;
                                }

                                Double tempST = subTotalMap.get( deCount );
                                if( tempST != null )
                                {
                                    tempST += tempAggValue;
                                }
                                else
                                {
                                    tempST = tempAggValue;
                                }
                                //System.out.println( "tempST : "+tempST + " Count : "+ deCount + " OU : "+ currentOrgUnit.getName());
                                subTotalMap.put( deCount, tempST );
                                //tempSubTotal += aggValue;
                            }
                            
                            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                            {
                                Double tempAggValue = 0.0;
                                //tempStr = reportService.getResultDataValue( deCodeString, sDate, eDate, currentOrgUnit, selReportObj.getModel() );
                                tempStr = getAggVal( deCodeString, tempAggDeMap );
                                try
                                {
                                    tempAggValue = Double.parseDouble( tempStr );
                                }
                                catch ( Exception e )
                                {
                                    tempAggValue = 0.0;
                                }
                                tempSubTotal += tempAggValue;
                                if( grandTotal[deCount] != null )
                                {
                                    grandTotal[deCount] += tempAggValue;
                                }
                                else
                                {
                                    grandTotal[deCount] = tempAggValue;
                                }

                                Double tempST = subTotalMap.get( deCount );
                                if( tempST != null )
                                {
                                    tempST += tempAggValue;
                                }
                                else
                                {
                                    tempST = tempAggValue;
                                }
                                //System.out.println( "tempST : "+tempST + " Count : "+ deCount + " OU : "+ currentOrgUnit.getName());
                                subTotalMap.put( deCount, tempST );
                                
                            }
                            else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                            {
                                Double tempAggValue = 0.0;
                                tempStr = getAggVal( deCodeString, aggDeMap );
                                try
                                {
                                    tempAggValue = Double.parseDouble( tempStr );
                                }
                                catch ( Exception e )
                                {
                                    tempAggValue = 0.0;
                                }
                                tempSubTotal += tempAggValue;
                                if( grandTotal[deCount] != null )
                                {
                                    grandTotal[deCount] += tempAggValue;
                                }
                                else
                                {
                                    grandTotal[deCount] = tempAggValue;
                                }

                                Double tempST = subTotalMap.get( deCount );
                                if( tempST != null )
                                {
                                    tempST += tempAggValue;
                                }
                                else
                                {
                                    tempST = tempAggValue;
                                }
                                subTotalMap.put( deCount, tempST );

                            }
                            //tempTotal.add( tempSubTotal );
                        }
                    }
                    int tempRowNo = report_inDesign.getRowno();
                    rowIncr = tempRowNo;
                    int tempColNo = report_inDesign.getColno();
                    int sheetNo = report_inDesign.getSheetno();
                    sheet0 = outputReportWorkbook.getSheet( sheetNo );
                    
                    if ( tempStr == null || tempStr.equals( " " ) )
                    {
                      sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                    }
                    else
                    {                                                   
                        if ( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                        {

                        }
                        else
                        {
                            tempRowNo += tempOrgUnitCount;
                            rowIncr += tempOrgUnitCount;
                        }
                           
                        if ( sType.equalsIgnoreCase( "dataelement" ) )
                        {
                            if( tempOuMemeberCount == orgUnitWithChildTree.size()-1  )
                            {
                                if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                                {
                                    
                                }
                                else
                                {
                                       try
                                       {
                                           sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                       }
                                       catch ( Exception e )
                                       {
                                           sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                                       }
                                       
                                       tempRowNo++;                                       
                                       
                                       try
                                       {                                           
                                           sheet0.addCell( new Number( tempColNo, tempRowNo, subTotalMap.get( deCount ) , getCellFormat2() ) );
                                       }
                                       catch ( Exception e )
                                       {
                                           sheet0.addCell( new Label( tempColNo, tempRowNo, " ", getCellFormat2() ) );
                                       }
                                       
                                       if( orgUnitCount == orgUnitList.size()-1 )
                                       {
                                           tempRowNo++;                                       
                                           sheet0.addCell( new Label( colStart, tempRowNo, " ", getCellFormat2() ) );
                                           sheet0.addCell( new Label( colStart+1, tempRowNo, " ", getCellFormat2() ) );
                                           sheet0.addCell( new Label( colStart+2, tempRowNo, "GRAND TOTAL", getCellFormat2() ) );

                                           try
                                           {                                           
                                               sheet0.addCell( new Number( tempColNo, tempRowNo, grandTotal[deCount] , getCellFormat2() ) );
                                           }
                                           catch ( Exception e )
                                           {
                                               sheet0.addCell( new Label( tempColNo, tempRowNo, " ", getCellFormat2() ) );
                                           }
                                       }
                               }
                            }                                
                            else
                            {
                                    try
                                    {
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                    }
                                    catch ( Exception e )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                                    }
                            }
                        }                        
                    }
                    count1++;
                }
                
                rowIncr++;
                rowInc++;
                tempOuMemeberCount++;
                tempOrgUnitCount++;
                slno++;
            }

            if( orgUnitWithChildTree.size() != 0 )
            {
                sheet0.addCell( new Label( colStart, rowStart+rowInc, " ", getCellFormat2() ) );
                sheet0.addCell( new Label( colStart+1, rowStart+rowInc, " ", getCellFormat2() ) );
                sheet0.addCell( new Label( colStart+2, rowStart+rowInc, "SUB TOTAL", getCellFormat2() ) );
                rowInc++;
                tempOrgUnitCount++;
            }
            
            rowIncr++;
            orgUnitCount++;
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        
        System.out.println( selOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation End Time is : " + new Date() );

        statementManager.destroy();
        return SUCCESS;
    }
    
    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end
  
    
    // getting data value using Map
    private String getAggVal( String expression, Integer orgUnitID, Map<String, String> aggDeMap )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                replaceString = aggDeMap.get( replaceString +":"+ orgUnitID );
                
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
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
            
            resultValue = "" + (double) d;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
    
    public WritableCellFormat getCellFormat1()throws Exception
    {
    WritableCellFormat wCellformat = new WritableCellFormat();

    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
    wCellformat.setAlignment( Alignment.CENTRE );
    wCellformat.setWrap( true );

    return wCellformat;
    }
    
    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.ICE_BLUE );
        wCellformat.setWrap( true );

        return wCellformat;
    }   

    private String getAggVal( String expression, Map<String, String> aggDeMap )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                replaceString = aggDeMap.get( replaceString );
                
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
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
            
            resultValue = "" + (double) d;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    
}
