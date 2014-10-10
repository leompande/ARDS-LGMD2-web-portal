package org.hisp.dhis.ll.action.llimport;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class LineListingImportingResultAction 
implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private SimpleDateFormat simpleDateFormat;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        Calendar curDateTime = Calendar.getInstance();
        Date curDate = new Date();                
        curDateTime.setTime( curDate );
        
        Period period;

        PeriodType dailyPeriodType = new DailyPeriodType();
        period = dailyPeriodType.createPeriod( curDate );
        period = reloadPeriodForceAdd( period );
        
        simpleDateFormat = new SimpleDateFormat( "dd-MMM-yyyy" );
        String logFileName = System.getenv( "DHIS2_HOME" ) + File.separator + "lli" + File.separator + "log" + File.separator + simpleDateFormat.format( curDate )+".txt";

        String storedBy = "ImportEngine";
        
        // Create file 
        FileWriter fstream = new FileWriter( logFileName, true );        
        PrintWriter pw = new PrintWriter( fstream );
        
        List<String> fileNames = new ArrayList<String>( getLLImportFiles() );
                                
        for( String importFile : fileNames )
        {
            try
            {
                String importFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + "lli" + File.separator + "pending" + File.separator + importFile;
                System.out.println(importFilePath);
                            
                Workbook importWorkbook = Workbook.getWorkbook( new File( importFilePath ) );
                
                Sheet sheet = importWorkbook.getSheet( 0 );
                Cell cell = sheet.getCell( 25, 1 );
                int noOfRecords = Integer.parseInt( cell.getContents() );
                
                int rowStart = 4;
                int colStart = 1;
                int rowCount = rowStart;        
                                                
                int lastRow = rowStart + noOfRecords;
                for( int i = rowStart; i < lastRow; i++ )
                {                
                   
                    int colCount = colStart;
                  
                    cell = sheet.getCell( rowCount, colCount );
                    int departmentId = Integer.parseInt( cell.getContents() );
                    LineListGroup department = lineListService.getLineListGroup( departmentId );
                    List<LineListElement> lineListElements = new ArrayList<LineListElement>( department.getLineListElements() );
                    colCount++;
                    
                    cell = sheet.getCell( rowCount, colCount );
                    int organisationUnitId = Integer.parseInt( cell.getContents() );
                    OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
                    colCount++;
                    
                    LineListDataValue llDataValue = new LineListDataValue();
                    Map<String, String> llElementValuesMap = new HashMap<String, String>();
                    for ( LineListElement element : lineListElements )
                    {
                        cell = sheet.getCell( rowCount, colCount );
                        String dataValue = cell.getContents();

                        if ( dataValue != null && dataValue.trim().equals( "" ) )
                        {
                            dataValue = "";
                        }
                        if ( dataValue != null && !( dataValue.equals( "" ) ) )
                        {
                            System.out.println( "name = " + element.getShortName() + " value  = " + dataValue );
                            llElementValuesMap.put( element.getShortName(), dataValue );
                        }
                    }

                    // add map in linelist data value
                    llDataValue.setLineListValues( llElementValuesMap );

                    // add period source, stored by, timestamp in linelist data value
                    llDataValue.setPeriod( period );
                    llDataValue.setSource( orgUnit );

                    storedBy = currentUserService.getCurrentUsername();

                    if ( storedBy == null )
                    {
                        storedBy = "[unknown]";
                    }

                    llDataValue.setStoredBy( storedBy );

                    llDataValue.setTimestamp( curDate );
                    
                    List<LineListDataValue> llDataValuesList = new ArrayList<LineListDataValue>();
                    llDataValuesList.add( llDataValue );
                                                        
                    boolean valueInserted = dataBaseManagerInterface.insertLLValueIntoDb( llDataValuesList, department.getShortName() );
                    
                }// Each Linelisting Record 
                pw.write(importFile + " is Imported.");
                pw.println();
            }
            catch(Exception e)
            {
                pw.write(importFile + " has problem while Importing, please check it.");
                pw.println();
            }
        }// import file names for loop end
        
        //Close the output stream
        pw.close();
        fstream.close();        

        return SUCCESS;
    }
        
    public List<String> getLLImportFiles()
    {
        List<String> fileNames = new ArrayList<String>();

        try
        {
            String importFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "lli" + File.separator
                + "pending";

            File dir = new File( importFolderPath );

            System.out.println( dir.getAbsolutePath() );

            System.out.println( dir.listFiles() );

            String[] files = dir.list();

            System.out.println( "In getImportFiles Method: " + files.length );

            fileNames = Arrays.asList( files );

            System.out.println( "In getImportFiles Method: " + fileNames.size() );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

        return fileNames;
    }

    // -------------------------------------------------------------------------
    // Support methods for reloading periods
    // -------------------------------------------------------------------------
    private final Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

}
