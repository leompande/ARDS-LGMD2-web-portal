package org.hisp.dhis.reports.ranking2.action;


import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

public class GenerateRanking2ReportAction implements Action
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
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

    private String aggData;

    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    private Integer orgUnitGroup;

    public void setOrgUnitGroup( Integer orgUnitGroup )
    {
        this.orgUnitGroup = orgUnitGroup;
    }

    /*
    private String aggCB;
    
    public void setAggCB( String aggCB )
    {
    this.aggCB = aggCB;
    }
     */
    private String reportFileNameTB;

   

    private List<OrganisationUnit> orgUnitList;

    private Period selectedPeriod;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat defaultDateFromat;

    private OrganisationUnit selectedOrgUnit;

    private SimpleDateFormat dateTimeFormat;

    private Date sDate;

    private Date eDate;

    private String raFolderName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        System.out.println( ouIDTB + "-" + availablePeriods + "" + aggData );


        statementManager.initialise();
        // Initialization
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        defaultDateFromat = new SimpleDateFormat( "yyyy-MM-dd" );
        dateTimeFormat = new SimpleDateFormat( "EEEE, dd MMMM yyyy HH:mm:ss zzzz" );
        
        raFolderName = reportService.getRAFolderName();

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        // OrgUnit Info
        OrganisationUnit current1OrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        System.out.println( current1OrgUnit.getName() + " : " + selReportObj.getName() + " : Report Generation Start Time is : " + new Date() );

        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ouIDTB ) );

        if ( orgUnitGroup != 0 )
        {
            OrganisationUnitGroup ouGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroup );

            if ( ouGroup != null )
            {
                orgUnitList.retainAll( ouGroup.getMembers() );
            }
        }
        List<Integer> orgUnitListIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
        String orgUnitListIdsByComma = getCommaDelimitedString( orgUnitListIds );

        String deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportFileNameTB = selReportObj.getExcelTemplateName();

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        //System.out.println( inputTemplatePath + " " + outputReportPath );

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        // Period Info
//      not required!  

        // List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
//        
//        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
//        
        String periodIdsByComma;
//        periodIdsByComma= getCommaDelimitedString( periodIds );
//       
//         only one period can be selected=>
        periodIdsByComma = "" + availablePeriods;

        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );

        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        //System.out.println("orgunitlistidbycomma="+ orgUnitListIdsByComma+"dataelementidbycomma"+dataElmentIdsByComma);

        Map<String, String> aggDeMapForselectedFacility = new HashMap<String, String>();


        if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
        {
            List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
            periodIdsByComma= getCommaDelimitedString( periodIds );
            
            aggDeMapForselectedFacility.putAll( reportService.getAggDataFromDataValueTable( orgUnitListIdsByComma, dataElmentIdsByComma, periodIdsByComma ) );
        } 
        else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
        {
            aggDeMapForselectedFacility.putAll( reportService.getAggDataFromAggDataValueTable( orgUnitListIdsByComma, dataElmentIdsByComma, periodIdsByComma ) );
        }
        else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
        {
            String childOrgUnitsByComma = "-1";
            for( OrganisationUnit orgUnit : orgUnitList )
            {    
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
                List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                childOrgUnitsByComma += "," + getCommaDelimitedString( childOrgUnitTreeIds );
            }    
            List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
            periodIdsByComma= getCommaDelimitedString( periodIds );
            
            aggDeMapForselectedFacility.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );            
        }

        //System.out.println( "aggdemapforselectedfacility=" + aggDeMapForselectedFacility.size() );
                
        int count1 = 0;
        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
        while ( reportDesignIterator.hasNext() )
        {
            Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

            String sType = report_inDesign.getStype();
            String deCodeString = report_inDesign.getExpression();
            System.out.print( "deCode=" + deCodeString + "---" );
            String tempStr = "";
            
            if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
            {
                tempStr = selectedOrgUnit.getName();
            } 
            else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
            {
                tempStr = simpleDateFormat.format( sDate );
            }
            else if ( deCodeString.equalsIgnoreCase( "CURRENTDATETIME" ) )
            {
                tempStr = dateTimeFormat.format( new Date() );
            }
            else if( sType.equalsIgnoreCase( "orgunitcountbygroup" ) )
            {
                tempStr = ""+reportService.getOrgunitCountByOrgunitGroup( deCodeString, selectedOrgUnit.getId() );
            }
            else if( sType.equalsIgnoreCase( "reportingunitcountbyperiod" ) )
            {
                tempStr = ""+reportService.getReportingOrgunitCountByDataset( Integer.parseInt( deCodeString ), selectedOrgUnit.getId(), selectedPeriod.getId() );
            }
            else if( sType.equalsIgnoreCase( "reportingunitcount" ) )
            {
                tempStr = ""+reportService.getReportingOrgunitCountByDataset( Integer.parseInt( deCodeString ), selectedOrgUnit.getId() );
            }
            else if ( sType.equalsIgnoreCase( "dataelementxmonthdays" ) )
            {
                String tempDate = defaultDateFromat.format( sDate );
                Integer month = Integer.parseInt( tempDate.split( "-" )[1] );
                Integer year = Integer.parseInt( tempDate.split( "-" )[2] );
                Integer monthDays[] = {0,31,28,31,30,31,30,31,31,30,31,30,31};
                tempStr = getAggVal( deCodeString, aggDeMapForselectedFacility );
                if( year % 4 == 0 && month == 2 )
                {
                    tempStr = "" + Double.parseDouble( tempStr ) * (monthDays[ month ]+1);
                }
                else
                {
                    tempStr = "" + Double.parseDouble( tempStr ) * monthDays[ month ];
                }
                System.out.println( tempStr + " : " + month + " : " + year );
            }            
            else if ( sType.equalsIgnoreCase( "dataelement" ) )
            {
                tempStr = getAggVal( deCodeString, aggDeMapForselectedFacility );
            }
            
            int tempRowNo = report_inDesign.getRowno();
            int tempColNo = report_inDesign.getColno();
            int sheetNo = report_inDesign.getSheetno();
            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
            try
            {
                try
                {
                    sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                } 
                catch ( Exception e )
                {
                    sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                }
            } 
            catch ( Exception e )
            {
                System.out.println( "Cannot write to Excel" );
            }

            count1++;
        }// while loop end

        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selectedOrgUnit.getName() + " : " + selReportObj.getName() + " Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();


        return SUCCESS;
    }

    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.NO_BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.ICE_BLUE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
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

                if ( replaceString == null )
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
        } catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
}
