package org.hisp.dhis.reports.mobile.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateMobileReportAnalyserResultAction
implements Action
{

    //--------------------------------------------------------------------------
    //Dependencies
    //--------------------------------------------------------------------------
    
    private OrganisationUnitService organisationunitService;
    
    public OrganisationUnitService getOrganisationunitService()
    {
        return organisationunitService;
    }

    public void setOrganisationunitService( OrganisationUnitService organisationunitService )
    {
        this.organisationunitService = organisationunitService;
    }
    
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
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
    
    //--------------------------------------------------------------------------
    //Input/Output
    //--------------------------------------------------------------------------
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
    
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    private String raFolderName;
    
    private Date sDate;

    private Date eDate;
    

    //--------------------------------------------------------------------------
    //Action Implementation
    //--------------------------------------------------------------------------
    
    public String execute()
    throws Exception
    {
        statementManager.initialise();

        raFolderName = reportService.getRAFolderName();
        
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        generateMobileReport();
        statementManager.destroy();
        System.out.println( "Report Generation End Time is : \t" + new Date() );
        
        return SUCCESS;
    }
    
    public void generateMobileReport()
    throws Exception
    {
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "MobileReport", 0 );
        
        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( true );
        wCellformat.setAlignment( Alignment.CENTRE );
     
        int rowStart = 0;
        int colStart = 0;
        
        sheet0.addCell( new Label( colStart, rowStart, "Date And Time", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 1, rowStart, "Phone Number", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 2, rowStart, "ANM Name", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 3, rowStart, "Query", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 4, rowStart, "SubCentre", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 5, rowStart, "Block", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 6, rowStart, "District", getCellFormat1() ) );
        
        rowStart++;
        
        
        //----------------------------------------------------------------------
        //Coding for Printing Data
        //----------------------------------------------------------------------
        
        sDate = format.parseDate( startDate );

        eDate = format.parseDate( endDate );
        
        List<Date> dateList = new ArrayList<Date> ( getDatesBetweenDates( sDate, eDate ) );
        
        for ( Date date : dateList )
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            String query = "select receiverInfo,smscontent from receivesms where receiverInfo like '%_" + simpleDateFormat.format( date ) + "_%'";
            
            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
            
            if ( rs1 != null )
            {
                while ( rs1.next() )
                {
                    String senderInfo = rs1.getString( 1 );
                    String receivedSMS = rs1.getString( 2 );
                    String mobileNo = senderInfo.split( "_" )[0];
                    String sentDate = senderInfo.split( "_" )[1];
                    String sentTime = senderInfo.split( "_" )[2];
                    
                    sentTime = sentTime.replaceAll( ".xml", "" );
                    sentTime = sentTime.replaceAll( "-", ":" );
                    
                    if ( mobileNo != null)
                    {
                        List<User> userList = new ArrayList<User> ( userService.getUsersByPhoneNumber( mobileNo ) );
                        if ( userList != null && userList.size() != 0)
                        {
                            User user = userList.get( 0 );
                            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit> ( user.getOrganisationUnits() );
                            if ( orgUnitList != null && orgUnitList.size() != 0 )
                            {
                                OrganisationUnit orgUnit = orgUnitList.get( 0 );
                                
                                String anmName = orgUnit.getComment();
                                
                                sheet0.addCell( new Label( colStart, rowStart, sentDate + "_" + sentTime, wCellformat ) );
                                sheet0.addCell( new Label( colStart + 1, rowStart, mobileNo, wCellformat ) );
                                sheet0.addCell( new Label( colStart + 2, rowStart, anmName, wCellformat ) );
                                sheet0.addCell( new Label( colStart + 3, rowStart, receivedSMS, wCellformat ) );
                                if ( orgUnit.getParent() != null )
                                {
                                    sheet0.addCell( new Label( colStart + 4, rowStart, orgUnit.getParent().getName(), wCellformat ) );
                                    if ( orgUnit.getParent().getParent() != null )
                                    {
                                        sheet0.addCell( new Label( colStart + 5, rowStart, orgUnit.getParent().getParent().getName(), wCellformat ) );
                                        
                                        if ( orgUnit.getParent().getParent().getParent() != null )
                                        {
                                            sheet0.addCell( new Label( colStart + 6, rowStart, orgUnit.getParent().getParent().getParent().getName(), wCellformat ) );
                                        }
                                        else
                                        {
                                            sheet0.addCell( new Label( colStart + 6, rowStart, " ", wCellformat ) );
                                        }
                                    }
                                    else
                                    {
                                        sheet0.addCell( new Label( colStart + 5, rowStart, " ", wCellformat ) );
                                        sheet0.addCell( new Label( colStart + 6, rowStart, " ", wCellformat ) );
                                    }
                                }
                                else
                                {
                                    sheet0.addCell( new Label( colStart + 4, rowStart, " ", wCellformat ) );
                                    sheet0.addCell( new Label( colStart + 5, rowStart, " ", wCellformat ) );
                                    sheet0.addCell( new Label( colStart + 6, rowStart, " ", wCellformat ) );
                                }
                                
                                rowStart++;
                            }
                        }
                    }
                    
                }
            }
        }
        
        outputReportWorkbook.write();

        outputReportWorkbook.close();
        
        fileName = "MobileReport.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
        
    }
    
    private List<Date> getDatesBetweenDates( Date startDate, Date endDate )
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startDateCal = Calendar.getInstance();
        startDateCal.setTime( startDate );
        List<Date> dateList = new ArrayList<Date>(); 
        dateList.add( startDateCal.getTime() );
        
        while( !simpleDateFormat.format( startDateCal.getTime() ).equalsIgnoreCase( simpleDateFormat.format( endDate ) ) )
        {
            
            startDateCal.add( Calendar.DAY_OF_YEAR, 1 );
            
            dateList.add( startDateCal.getTime() );
            System.out.println(startDateCal.getTime() + "---" + endDate);
        }
        
        System.out.println("DateListSize:"+dateList.size() );
        
        return dateList;
    }
    
    public WritableCellFormat getCellFormat1()
    throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
    
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( true );
        return wCellformat;
    } // end getCellFormat1() function
    
}
