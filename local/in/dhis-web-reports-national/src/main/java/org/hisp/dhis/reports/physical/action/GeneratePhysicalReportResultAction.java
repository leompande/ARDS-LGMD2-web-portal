/**
 * 
 */
package org.hisp.dhis.reports.physical.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class GeneratePhysicalReportResultAction
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

    private String periodNameList;

    public void setPeriodNameList( String periodNameList )
    {
        this.periodNameList = periodNameList;
    }

    Period selPeriod;

    private List<OrganisationUnit> orgUnitList;

    private String reportModelTB;

    private String reportFileNameTB;

    private String raFolderName;

    String years[];
    
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }
    
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

        // Getting Report Details
        raFolderName = reportService.getRAFolderName();

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        System.out.println( selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );

        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        deCodesXMLFileName = selReportObj.getXmlTemplateName();

        // Getting Orgunit Details
        List<OrganisationUnit> curUserRootOrgUnitList = new ArrayList<OrganisationUnit>( currentUserService
            .getCurrentUser().getOrganisationUnits() );

        if ( curUserRootOrgUnitList != null && curUserRootOrgUnitList.size() > 0 )
        {
            for ( OrganisationUnit orgUnit : curUserRootOrgUnitList )
            {
                List<OrganisationUnit> childList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( childList, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( childList );
                orgUnitList.add( orgUnit );
            }
        }

        // Getting Period Info

        years = periodNameList.split( "-" );

        Date sDate = format.parseDate( years[0] + "-01-01" );

        Date eDate = format.parseDate( years[0] + "-12-31" );

        selPeriod = periodService.getPeriod( sDate, eDate, new YearlyPeriodType() );

        // Getting DeCodes
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
       // String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

        int rowCount = 1;
        // Getting DataValues
        for ( OrganisationUnit curOrgUnit : orgUnitList )
        {
            int count1 = 0;
            for ( Report_inDesign reportDesign : reportDesignList )
            {
                String tempStr = "";
                String deCode = reportDesign.getExpression();
                String deType = reportDesign.getPtype();

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( getStartingEndingPeriods( deType ) );
                if( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selPeriod.getStartDate() );
                    tempEndDate.setTime( selPeriod.getEndDate() );
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCode.equalsIgnoreCase( "[NA]" ) )
                {
                    tempStr = " ";
                }
                else if ( deCode.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = curOrgUnit.getName();
                }
                else if ( deCode.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + rowCount;
                }
                else
                {
                    if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                    {
                        tempStr = reportService.getIndividualResultDataValue(deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit, reportModelTB );
                    } 
                    else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                    {
                        tempStr = reportService.getResultDataValue( deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit, reportModelTB );
                    }
                    else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                    {
                        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( tempStartDate.getTime(), tempEndDate.getTime() ) );
                        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                        tempStr = reportService.getResultDataValueFromAggregateTable( deCode, periodIds, curOrgUnit, reportModelTB );
                    }
                    //tempStr = reportService.getResultDataValue( deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit, reportModelTB );
                }

                int tempRowNo = reportDesign.getRowno() + rowCount;
                int tempColNo = reportDesign.getColno();
                int sheetNo = reportDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                WritableCellFormat wCellformat = new WritableCellFormat();
                wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                wCellformat.setWrap( true );
                wCellformat.setAlignment( Alignment.CENTRE );

                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }
                else
                {
                    sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                }

                count1++;
            }

            rowCount++;
        }

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_"+periodNameList + ".xls";

        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selReportObj.getName()+" : Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();
        statementManager.destroy();

        return SUCCESS;
    }

    public List<Calendar> getStartingEndingPeriods( String deType )
    {
        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        if ( deType.equalsIgnoreCase( "q1" ) )
        {
            Date startDate = format.parseDate( years[0] + "-04-01" );
            Date endDate = format.parseDate( years[0] + "-06-30" );
            tempStartDate.setTime( startDate );
            tempEndDate.setTime( endDate );
        }
        else if ( deType.equalsIgnoreCase( "q2" ) )
        {
            Date startDate = format.parseDate( years[0] + "-07-01" );
            Date endDate = format.parseDate( years[0] + "-09-30" );
            tempStartDate.setTime( startDate );
            tempEndDate.setTime( endDate );
        }
        else if ( deType.equalsIgnoreCase( "q3" ) )
        {
            Date startDate = format.parseDate( years[0] + "-10-01" );
            Date endDate = format.parseDate( years[0] + "-12-31" );
            tempStartDate.setTime( startDate );
            tempEndDate.setTime( endDate );
        }
        else if ( deType.equalsIgnoreCase( "q4" ) )
        {
            Date startDate = format.parseDate( years[1] + "-01-01" );
            Date endDate = format.parseDate( years[1] + "-03-31" );
            tempStartDate.setTime( startDate );
            tempEndDate.setTime( endDate );
        }
        else
        {
            tempStartDate.setTime( selPeriod.getStartDate() );
            tempEndDate.setTime( selPeriod.getEndDate() );
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }
}
