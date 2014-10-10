package org.hisp.dhis.reports.routine.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportModel;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import com.opensymphony.xwork2.Action;

public class RoutineReportResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
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

    private ConfigurationService configurationService;
    
    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }


    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Report_in selReport;
    
    private String raFolderName;
    
    private List<OrganisationUnit> orgUnitList;
    
    private Period selPeriod;
    
    private OrganisationUnit selOrgUnit;
    
    
    private Integer reportList;

    public void setReportList( Integer reportList )
    {
        this.reportList = reportList;
    }

    private Integer ouIDTB;

    public void setOuIDTB( Integer ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private Integer availablePeriods;
    
    public void setAvailablePeriods( Integer availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }
    
    private String aggCB;

    public void setAggCB( String aggCB )
    {
        this.aggCB = aggCB;
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
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {        
        /* Intialisation */
        Date startTime = new Date();
        raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        SimpleDateFormat monthFormat = new SimpleDateFormat( "MMMM" );
        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat( "MMM" );
        SimpleDateFormat yearFormat = new SimpleDateFormat( "yyyy" );
        SimpleDateFormat simpleYearFormat = new SimpleDateFormat( "yy" );
                        
        /* Report Info */
        selReport = reportService.getReport( reportList );
        
        /* Period Info */
        selPeriod = periodService.getPeriod( availablePeriods );
        
        /* OrgUnit Info */
        selOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        if ( selReport.getModel().equalsIgnoreCase( ReportModel.RM_STATIC ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();            
            orgUnitList.add( selOrgUnit );
        }
        else if ( selReport.getModel().equalsIgnoreCase( ReportModel.RM_DYNAMIC_WITH_ROOT ) )
        {            
            orgUnitList = new ArrayList<OrganisationUnit>( selOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
            orgUnitList.add( selOrgUnit );
        }
        
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + selReport.getExcelTemplateName();
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
    
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        
        List<Report_inDesign> reportDesign = new ArrayList<Report_inDesign>(reportService.getReportDesign( selReport )); 
        
        int orgUnitCount = 0;
        for( OrganisationUnit ou : orgUnitList )
        {
            for(Report_inDesign rd : reportDesign )
            {
                String expression = rd.getExpression();
                String ptype = rd.getPtype();
                String stype = rd.getStype();
                
                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( ptype,  selPeriod.getStartDate(), selPeriod.getEndDate() ));
                if(calendarList == null || calendarList.isEmpty())
                {
                    tempStartDate.setTime( selPeriod.getStartDate());
                    tempEndDate.setTime( selPeriod.getEndDate()); 
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }
                
                String resultData="";
                
                if ( expression.equalsIgnoreCase( "FACILITY" ) )
                {
                    resultData = ou.getName();
                }
                else if ( expression.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    resultData = selOrgUnit.getName();
                }
                else if ( expression.equalsIgnoreCase( "FACILITYP" ) )
                {
                    resultData = ou.getParent().getName();
                }
                else if ( expression.equalsIgnoreCase( "FACILITYPP" ) )
                {                    
                    resultData = ou.getParent().getParent().getName();
                }
                else if ( expression.equalsIgnoreCase( "FACILITYPPP" ) )
                {
                    resultData = ou.getParent().getParent().getParent().getName();
                }
                else if ( expression.equalsIgnoreCase( "FACILITYPPPP" ) )
                {
                    resultData = ou.getParent().getParent().getParent().getParent().getName();
                }
                else if ( expression.equalsIgnoreCase( "PERIOD" )
                    || expression.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    resultData = simpleDateFormat.format( selPeriod.getStartDate() );
                }
                else if ( expression.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    resultData = monthFormat.format( selPeriod.getStartDate() );
                }
                else if ( expression.equalsIgnoreCase( "PERIOD-YEAR" ) )
                {
                    Date sDateTemp = selPeriod.getStartDate();

                    Calendar tempQuarterYear = Calendar.getInstance();

                    tempQuarterYear.setTime( sDateTemp );

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    PeriodType periodType = selPeriod.getPeriodType();

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = selPeriod.getStartDate();
                    }
                    else
                    {
                        if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" ))
                            && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, -1 );

                            sDateTemp = tempQuarterYear.getTime();
                        }
                    }

                    resultData = yearFormat.format( sDateTemp );
                }
                else if ( expression.equalsIgnoreCase( "SLNO" ) )
                {
                    resultData = "" + (orgUnitCount + 1);
                }
                else if ( expression.equalsIgnoreCase( "NA" ) )
                {
                    resultData = " ";
                }
                else
                {
                    if ( stype.equalsIgnoreCase( "dataelement" ) )
                    {
                        if ( aggCB == null )
                        {
                            //resultData = getIndividualResultDataValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou );
                        }
                        else
                        {                            
                            resultData = reportService.getResultDataValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou, aggCB );
                        }
                    }
                    else if ( stype.equalsIgnoreCase( "indicator" ) )
                    {
                        if ( aggCB == null )
                        {
                            //resultData = getIndividualResultIndicatorValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou.getParent() );
                        }
                        else
                        {
                            //resultData = getResultIndicatorValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou.getParent() );
                        }
                    }
                    else if ( stype.equalsIgnoreCase( "dataelement-boolean" ) )
                    {
                        if ( aggCB == null )
                        {
                            //resultData = getBooleanDataValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou );
                        }
                        else
                        {
                            //resultData = getBooleanDataValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou );
                        }
                    }
                    else
                    {
                        if ( aggCB == null )
                        {
                            //resultData = getIndividualResultIndicatorValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou );
                        }
                        else
                        {
                            //resultData = getResultIndicatorValue( expression, tempStartDate.getTime(), tempEndDate.getTime(), ou );
                        }
                    }
                }
                
                
                int tempRowNo = rd.getRowno();
                int tempColNo = rd.getColno();
                int sheetNo = rd.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                if ( resultData == null || resultData.equals( " " ) )
                {
                    tempColNo += orgUnitCount;

                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }
                else
                {
                    if ( selReport.getModel().equalsIgnoreCase( ReportModel.RM_STATIC ) )
                    {

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( resultData );
                            l.setCellFormat( cellFormat );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, resultData, wCellformat ) );
                        }
                    }
                }
                
            }// Report Design Loop end
            
            orgUnitCount++;
        }// Orgunit info

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = selReport.getExcelTemplateName().replace( ".xls", "" );
        fileName += "_" + selOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println("***********************************************");
        System.out.println(fileName+" Report Generation Start Time is : " + startTime );
        System.out.println(fileName+" Report Generation End Time is : " + new Date());
        System.out.println("***********************************************");
        
        outputReportFile.deleteOnExit();
        
        return SUCCESS;
    }
 
}
