package org.hisp.dhis.reports.periodwiseprogress.action;

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
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.util.comparator.PeriodStartDateComparator;

import com.opensymphony.xwork2.Action;

public class GeneratePeriodWiseProgressAnalyserResultAction
    implements Action
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
/*
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
*/
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

    private String fileName;

    public String getFileName()
    {
        return fileName;
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

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }
    
    private int availablePeriodsto;
    
    public void setAvailablePeriodsto( int availablePeriodsto )
    {
        this.availablePeriodsto = availablePeriodsto;
    }

    private String periodTypeId;
    
    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }

    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    private Period selectedPeriod;
    
    private Period selectedEndPeriod;

    private SimpleDateFormat simpleDateFormat;

    private Date sDate;

    private Date eDate;

    private String raFolderName;

    private int sheetNo = 0;

    private int tempColNo;

    private int tempRowNo;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        statementManager.initialise();
        
        // Initialization
        raFolderName = reportService.getRAFolderName();

        String colArray[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
            "AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ",
            "BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ" };
        
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        // OrgUnit Info
        OrganisationUnit currentOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        System.out.println( currentOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );

        List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ouIDTB ) );
        List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
        String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

        // Report Info
        String deCodesXMLFileName = selReportObj.getXmlTemplateName();
        String reportModelTB = selReportObj.getModel();
        String reportFileNameTB = selReportObj.getExcelTemplateName();

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
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        selectedEndPeriod = periodService.getPeriod( availablePeriodsto );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedEndPeriod.getEndDate() ) );

        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );
        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sDate, eDate ) );
        //List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
        Collections.sort( periodList, new PeriodStartDateComparator() );
        
        if( periodTypeId.equalsIgnoreCase( "monthly" ) )
        {
            simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" ); 
        }
        else if( periodTypeId.equalsIgnoreCase( "yearly" ) )
        {
            simpleDateFormat = new SimpleDateFormat( "yyyy" );
        }
        else
        {
            simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        }
        
        // To get Aggregation Data
        
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );

        //String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        String dataElmentIdsByComma = reportService.getDataelementIdsByStype( reportDesignList, Report_inDesign.ST_DATAELEMENT );
        String nonNumberDataElementIdsByComma = reportService.getDataelementIdsByStype( reportDesignList, Report_inDesign.ST_NON_NUMBER_DATAELEMENT );
        
        //Collection<Integer> periodIds1 = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
        String periodsByComma = "";
        //getCommaDelimitedString( periodIds1 );
                
        int colCount = 0;
        for( Period period : periodList )
        {               
            if( periodTypeId.equalsIgnoreCase( "daily" ) )
            {
                periodsByComma = ""+period.getId();
            }
            else
            {
                Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodService.getIntersectingPeriods( period.getStartDate(), period.getEndDate() ) ) );
                periodsByComma = getCommaDelimitedString( periodIds );
            }

            Map<String, String> aggDeMap = new HashMap<String, String>();
            if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( currentOrgUnit.getId(), dataElmentIdsByComma, periodsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodsByComma ) );
                aggDeMap.putAll( reportService.getAggNonNumberDataFromDataValueTable(childOrgUnitsByComma, nonNumberDataElementIdsByComma, periodsByComma ) );
                System.out.println(childOrgUnitsByComma +" \n " + dataElmentIdsByComma + " \n " + periodsByComma );
            }
            else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+currentOrgUnit.getId(), dataElmentIdsByComma, periodsByComma ) );
                aggDeMap.putAll( reportService.getAggNonNumberDataFromDataValueTable(""+currentOrgUnit.getId(), nonNumberDataElementIdsByComma, periodsByComma ) );
            }
            System.out.println( "aggDeMap size : " + aggDeMap.size() );

            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while (  reportDesignIterator.hasNext() )
            {
                Report_inDesign reportDesign =  reportDesignIterator.next();
                String deCodeString = reportDesign.getExpression();

                String sType = reportDesign.getStype();
                String tempStr = "";

                tempRowNo = reportDesign.getRowno();
                tempColNo = reportDesign.getColno();
                sheetNo = reportDesign.getSheetno();
                              
                if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                } 
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-RANGE" ) )
                {
                    tempStr = simpleDateFormat.format( selectedPeriod.getStartDate() ) + " To " + simpleDateFormat.format( selectedEndPeriod.getEndDate() );
                }
                else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                {
                    tempStr = simpleDateFormat.format( period.getStartDate() );
                } 
                else if( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                } 
                else
                {
                    if( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) ) 
                        {
                            //tempStr = reportService.getIndividualResultDataValue( deCodeString, period.getStartDate(), period.getEndDate(), currentOrgUnit, reportModelTB );
                            tempStr = getAggVal( deCodeString, aggDeMap );
                        } 
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            //tempStr = reportService.getResultDataValue( deCodeString, period.getStartDate(), period.getEndDate(), currentOrgUnit, reportModelTB );
                            tempStr = getAggVal( deCodeString, aggDeMap );
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                        }
                    }
                    else if( sType.equalsIgnoreCase( Report_inDesign.ST_DATAELEMENT_NO_REPEAT ) )
                    {
                        deCodeString = deCodeString.replaceAll( ":", "\\." );
                        deCodeString = deCodeString.replaceAll( "[", "" );
                        deCodeString = deCodeString.replaceAll( "]", "" );
                        System.out.println( "deCodeString : "+ deCodeString );
                        tempStr = aggDeMap.get( deCodeString );
                    }
                }
                
                if( tempStr == null || tempStr.equals( " " ) )
                {
                    tempColNo += colCount;

                    WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                } 
                else
                {
                    if( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                    {
                        if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-RANGE" ) )
                        {                            
                        } 
                        else
                        {
                            tempColNo += colCount;
                        }

                        WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                        try
                        {
                            try
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                            }
                            catch( Exception e )
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        } 
                        catch( Exception e )
                        {
                            System.out.println( "Cannot write to Excel" );
                        }
                    }
                }
            }// inner while loop end

            colCount++;
        }// outer while loop end

        // ---------------------------------------------------------------------
        // Writing Total Values
        // ---------------------------------------------------------------------
        
        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
        while (  reportDesignIterator.hasNext() )
        {
            Report_inDesign reportDesign =  reportDesignIterator.next();
            
            String deCodeString = reportDesign.getExpression();

            if( deCodeString.equalsIgnoreCase( "FACILITY" ) || 
                deCodeString.equalsIgnoreCase( "PERIOD-RANGE" ) )
            {
                continue;
            } 
            
            tempRowNo = reportDesign.getRowno();
            tempColNo = reportDesign.getColno();
            sheetNo = reportDesign.getSheetno();
            
            String colStart = ""+ colArray[tempColNo];
            String colEnd = ""+ colArray[tempColNo+colCount-1];
            
            String tempFormula = "SUM("+colStart+(tempRowNo+1)+":"+colEnd+(tempRowNo+1)+")";
            
            WritableSheet totalSheet = outputReportWorkbook.getSheet( sheetNo );
            WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
            WritableCellFormat totalCellformat = new WritableCellFormat( arialBold );
            totalCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
            totalCellformat.setAlignment( Alignment.CENTRE );
            totalCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
            totalCellformat.setWrap( true );

            if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
            {
                totalSheet.addCell( new Label( tempColNo+colCount, tempRowNo, "Total", totalCellformat ) );
            }
            else if( deCodeString.equalsIgnoreCase( "NA" ) )
            {
                totalSheet.addCell( new Label( tempColNo+colCount, tempRowNo, " ", totalCellformat ) );
            }
            else
            {
                totalSheet.addCell( new Formula( tempColNo+colCount, tempRowNo, tempFormula, totalCellformat ) );    
            }
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + currentOrgUnit.getShortName();
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( currentOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
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

                                System.out.println( replaceString + " : " + aggDeMap.get( replaceString ) );
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
