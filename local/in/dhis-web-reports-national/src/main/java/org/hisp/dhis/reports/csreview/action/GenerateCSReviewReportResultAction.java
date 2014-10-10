package org.hisp.dhis.reports.csreview.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class GenerateCSReviewReportResultAction
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

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

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

    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

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


    private List<OrganisationUnit> orgUnitList;

    private String raFolderName;

    private String reportModelTB;

    private String reportFileNameTB;

    private Date sDate;

    private Date eDate;

    private Integer monthCount;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat yearFormat;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {

        // Intialisation

        statementManager.initialise();
        orgUnitList = new ArrayList<OrganisationUnit>();
        String deCodesXMLFileName = "";
        raFolderName = reportService.getRAFolderName();

        String tempStr = "";

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        yearFormat = new SimpleDateFormat( "yyyy" );

        // Month count
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );

        String tempFromDate = simpleDateFormat.format( sDate );
        String tempToDate = simpleDateFormat.format( eDate );

        String startTargetYear = yearFormat.format( sDate );

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        tempStartDate.setTime( sDate );
        tempEndDate.setTime( eDate );

        int endYear = tempEndDate.get( Calendar.YEAR );
        int startYear = tempStartDate.get( Calendar.YEAR );
        int endMonth = tempEndDate.get( Calendar.MONTH );
        int startMonth = tempStartDate.get( Calendar.MONTH );

        monthCount = ((endYear - startYear) * 12) - startMonth + endMonth + 1;

        tempStr = monthCount.toString();

        // Getting Report Details
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();

        System.out.println( reportModelTB + " : " + reportFileNameTB + " : " + deCodesXMLFileName );

        System.out.println( "Report Generation Start Time is : \t" + new Date() );

        // Getting Orgunit Details
        List<OrganisationUnit> curUserRootOrgUnitList = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getOrganisationUnits() );
        if ( curUserRootOrgUnitList != null && curUserRootOrgUnitList.size() != 0 )
        {
            for ( OrganisationUnit orgUnit : curUserRootOrgUnitList )
            {
                List<OrganisationUnit> childOrgList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( childOrgList, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( childOrgList );
                orgUnitList.add( orgUnit );
            }
        }

        // Getting DeCodes
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        //List<String> deCodesList = getDECodes( deCodesXMLFileName );

        // Getting Exel Template
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
       // String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

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
        wCellformat.setWrap( true );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

        WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
        sheet0.addCell( new Label( 1, 1, selReportObj.getName(), getCellFormat2() ) );
        sheet0.addCell( new Label( 3, 4, startTargetYear, getCellFormat2() ) );
        sheet0.addCell( new Label( 5, 3, "Achievement for : " + tempFromDate + " - " + tempToDate, getCellFormat2() ) );

        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( tempStartDate.getTime(), tempEndDate.getTime() ) );
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
        String periodIdsByComma = getCommaDelimitedString( periodIds );

        int rowCount = 1;
        int rowIncr = 0;

        // Getting DataValues
        for ( OrganisationUnit curOrgUnit : orgUnitList )
        {
            Map<String, String> aggDeMap = new HashMap<String, String>();
            if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( curOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( curOrgUnit.getId() ) );
                List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+curOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }

            int count1 = 0;
            for ( Report_inDesign report_inDesign : reportDesignList )
            {
                int tempRowNo = report_inDesign.getRowno() + rowIncr;
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                String sType = report_inDesign.getStype();
                String deCode = report_inDesign.getExpression();

                if ( deCode.equalsIgnoreCase( "[0.0]" ) )
                {
                    tempStr = " ";
                }
                else if ( deCode.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = curOrgUnit.getName();
                }
                else if ( deCode.equalsIgnoreCase( "SLNo" ) )
                {
                    if ( rowCount == orgUnitList.size() )
                    {
                        tempStr = " ";
                    }
                    else
                    {
                        tempStr = "" + rowCount;
                    }
                }
                else if ( sType.equalsIgnoreCase( "formula" ) )
                {
                    tempStr = deCode.replace( "?", "" + (tempRowNo + 1) );
                    tempStr = tempStr.replace( "MONTHCOUNT", "" + monthCount );
                }
                else
                {
                    if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                    {
                        tempStr = getAggVal( deCode, aggDeMap );
                        //tempStr = reportService.getIndividualResultDataValue(deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit, reportModelTB );
                    } 
                    else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                    {
                        tempStr = getAggVal( deCode, aggDeMap );
                        //tempStr = getResultDataValue( deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit );
                        //tempStr = reportService.getResultDataValue( deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit, " " );
                    }
                    else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                    {
                        tempStr = getAggVal( deCode, aggDeMap );
                        //tempStr = reportService.getResultDataValueFromAggregateTable( deCode, periodIds, curOrgUnit, reportModelTB );
                    }
                }
                System.out.println( "DECode : " + deCode + "   TempStr : " + tempStr );
                sheet0 = outputReportWorkbook.getSheet( sheetNo );
                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "formula" ) )
                    {
                        sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, wCellformat ) );
                    }
                    else
                    {
                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                    }
                }
                count1++;
            }
            rowCount++;
            rowIncr++;
        }

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        fileName = reportFileNameTB;
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( "Report Generation End Time is : \t" + new Date() );

        outputReportFile.deleteOnExit();
        statementManager.destroy();
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive Methods
    // -------------------------------------------------------------------------

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

    public String getDataelementIds( List<Report_inDesign> reportDesignList )
    {
        String dataElmentIdsByComma = "-1";
        for( Report_inDesign report_inDesign : reportDesignList )
        {
            String formula = report_inDesign.getExpression();
            try
            {
                Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

                Matcher matcher = pattern.matcher( formula );
                StringBuffer buffer = new StringBuffer();

                while ( matcher.find() )
                {
                    String replaceString = matcher.group();

                    replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                    replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                    int dataElementId = Integer.parseInt( replaceString );
                    dataElmentIdsByComma += "," + dataElementId;
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                }
            }
            catch( Exception e )
            {
                
            }
        }
        
        System.out.println("DataElementIdsByComma : "+ dataElmentIdsByComma );
        
        return dataElmentIdsByComma;
    }
    
    // Excel sheet format function
    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_50 );
        wCellformat.setWrap( false );
        return wCellformat;
    } // end getCellFormat1() function

    // Excel sheet format function
    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_50 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );
        return wCellformat;
    } // end getCellFormat1() function

}
