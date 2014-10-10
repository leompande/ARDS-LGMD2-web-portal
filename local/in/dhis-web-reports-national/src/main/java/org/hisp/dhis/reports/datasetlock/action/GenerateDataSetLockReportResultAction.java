package org.hisp.dhis.reports.datasetlock.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.util.comparator.PeriodStartDateComparator;

import com.opensymphony.xwork2.Action;

public class GenerateDataSetLockReportResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
/*
    private DataSetLockService dataSetLockService;

    public void setDataSetLockService( DataSetLockService dataSetLockService )
    {
        this.dataSetLockService = dataSetLockService;
    }
*/
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

    // -------------------------------------------------------------------------
    // Input & Output
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

    private Collection<Integer> periodIds;

    public void setPeriodIds( Collection<Integer> periodId )
    {
        this.periodIds = periodId;
    }

    private Integer dataSets;

    public void setDataSets( Integer dataSets )
    {
        this.dataSets = dataSets;
    }

    private Integer ouIDTB;

    public void setOuIDTB( Integer ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private String raFolderName;

    private DataSet dataSet;

    private Set<OrganisationUnit> dSetSource;

    private List<Period> periods;

    private WritableSheet sheet0;

    private int rowStart;

    private int colStart;

    private int minLevel;

    private int maxLevel;

    private int x;

    public String execute()
        throws Exception
    {
        x = 0;
        minLevel = organisationUnitService.getNumberOfOrganisationalLevels();
        periods = new ArrayList<Period>();

        if ( periodIds != null && periodIds.size() != 0 )
        {
            for ( Integer periodId : periodIds )
            {
                periods.add( periodService.getPeriod( periodId.intValue() ) );
            }
            Collections.sort( periods, new PeriodStartDateComparator() ); // sorting
            // the
            // periods
        }
        else
        {
            return SUCCESS;
        }

        raFolderName = reportService.getRAFolderName();
        dataSet = dataSetService.getDataSet( dataSets );
        dSetSource = dataSet.getSources();

        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        sheet0 = outputReportWorkbook.createSheet( "DataSetLockedReport", 0 );
        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.CENTRE );
        rowStart = 0;
        colStart = 0;
        int i;

        for ( i = 1; i <= organisationUnitService.getNumberOfOrganisationalLevels(); i++ )
        {
            sheet0.addCell( new Label( colStart + i, rowStart, "Level-" + i, getCellFormat1() ) );
        }

        for ( Period periodElement : periods )
        {
            sheet0
                .addCell( new Label( colStart + i, rowStart, format.formatPeriod( periodElement ), getCellFormat1() ) );
            i++;
        }
        rowStart++;

        if ( ouIDTB != null )
        {
            x++;
            generateDataSetLockReport( organisationUnitService.getOrganisationUnit( ouIDTB.intValue() ), rowStart,
                colStart + organisationUnitService.getLevelOfOrganisationUnit( ouIDTB.intValue() ) );
            int noOfOrganisationalLevels = organisationUnitService.getNumberOfOrganisationalLevels();
            for ( int j = 1; j <= noOfOrganisationalLevels; j++ )
            {
                if ( !(j >= minLevel && j <= maxLevel) )
                {
                    sheet0.removeColumn( colStart + j );
                    if ( j < minLevel )
                    {
                        minLevel--;
                        maxLevel--;
                    }
                    noOfOrganisationalLevels--;
                    j--;
                }
            }

            for ( int counter = 0; counter < 6; counter++ )
            {
                sheet0.insertRow( 0 );
            }
            rowStart = 0;
            sheet0.addCell( new Number( colStart + 1, rowStart + 1, 1, getCellFormatGreen() ) );
            sheet0.addCell( new Number( colStart + 1, rowStart + 2, 0, getCellFormatRed() ) );
            sheet0.addCell( new Label( colStart + 1, rowStart + 3, "", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 1, rowStart + 4, "DataSet Name", getCellFormat2() ) );

            sheet0.addCell( new Label( colStart + 2, rowStart + 1, "LOCKED", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 2, rowStart + 2, "UNLOCKED", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 2, rowStart + 3, "NOT ASSIGN", getCellFormat1() ) );

            sheet0.addCell( new Label( colStart + 2, rowStart + 4, dataSet.getName(), getCellFormat2() ) );
        }
        else
            return SUCCESS;

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "DataSetLock_" + organisationUnitService.getOrganisationUnit( ouIDTB.intValue() ).getShortName()
            + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
        // System.out.println( minLevel + " : " + maxLevel );
        return SUCCESS;
    }

    public ArrayList<Integer> generateDataSetLockReport( OrganisationUnit organisationUnit, int rowNo, int columnNo )
        throws Exception
    {
        ArrayList<Integer> unlockedDatasetPeriodsCounter = new ArrayList<Integer>();
        int currentLevel = organisationUnitService.getLevelOfOrganisationUnit( organisationUnit.getId() );
        List<OrganisationUnit> organisationUnitChildren;

        if ( organisationUnit.hasChild() )
        {
            organisationUnitChildren = new ArrayList<OrganisationUnit>( organisationUnit.getChildren() );

            Collections.sort( organisationUnitChildren, new IdentifiableObjectNameComparator() );

            int organisationUnitChildrenCounter = 0;
            for ( OrganisationUnit orgElement : organisationUnitChildren )
            {
                organisationUnitChildrenCounter++;
                ArrayList<Integer> unlockedOrganisationUnitDatasetPeriods = new ArrayList<Integer>();
                x++;
                unlockedOrganisationUnitDatasetPeriods = generateDataSetLockReport( orgElement, x,
                    organisationUnitService.getLevelOfOrganisationUnit( orgElement.getId() ) );

                if ( unlockedOrganisationUnitDatasetPeriods != null )
                {
                    if ( unlockedOrganisationUnitDatasetPeriods.size() == periodIds.size() )
                    {
                        for ( int i = 0; i < unlockedOrganisationUnitDatasetPeriods.size(); i++ )
                        {
                            if ( unlockedDatasetPeriodsCounter != null
                                && unlockedDatasetPeriodsCounter.size() == periodIds.size() )
                                unlockedDatasetPeriodsCounter.set( i, unlockedDatasetPeriodsCounter.get( i )
                                    + unlockedOrganisationUnitDatasetPeriods.get( i ) );
                            else
                                unlockedDatasetPeriodsCounter.add( unlockedOrganisationUnitDatasetPeriods.get( i ) );
                        }
                    }
                }
            }

            if ( dSetSource.contains( organisationUnit ) )
            {

                if ( maxLevel < currentLevel )
                    maxLevel = currentLevel;
                if ( minLevel > currentLevel )
                    minLevel = currentLevel;

                if ( unlockedDatasetPeriodsCounter == null || unlockedDatasetPeriodsCounter.size() != periodIds.size() )
                {
                    unlockedDatasetPeriodsCounter = new ArrayList<Integer>();
                }

                int periodsCounter = 0;
                sheet0.addCell( new Label( colStart + columnNo, rowStart + rowNo, organisationUnit.getName(),
                    getCellFormat2() ) );
                sheet0.mergeCells( colStart + columnNo, rowStart + rowNo + 1, colStart + columnNo, x + 1 );
                for ( Period periodElement : periods )
                {
                    
                    boolean lockStatus = dataSetService.isLocked( dataSet, periodElement, organisationUnit, null );
                    //DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSet, periodElement, organisationUnit );
                    //if ( dataSetLock != null )
                    if ( lockStatus )
                    {
                        sheet0.addCell( new Number( colStart
                            + organisationUnitService.getNumberOfOrganisationalLevels() + 1 + periodsCounter, rowStart
                            + rowNo, 1, getCellFormatGreen() ) );

                        if ( unlockedDatasetPeriodsCounter != null
                            && unlockedDatasetPeriodsCounter.size() == periodIds.size() )
                        {
                            unlockedDatasetPeriodsCounter.set( periodsCounter, unlockedDatasetPeriodsCounter
                                .get( periodsCounter ) + 1 );
                        }
                        else
                            unlockedDatasetPeriodsCounter.add( 1 );
                    }

                    else
                    {
                        if ( !(unlockedDatasetPeriodsCounter != null && unlockedDatasetPeriodsCounter.size() == periodIds
                            .size()) )
                        {
                            unlockedDatasetPeriodsCounter.add( 0 );
                        }

                        sheet0.addCell( new Number( colStart
                            + organisationUnitService.getNumberOfOrganisationalLevels() + 1 + periodsCounter, rowStart
                            + rowNo, 0, getCellFormatRed() ) );
                    }
                    periodsCounter++;
                }
            }
            else if ( unlockedDatasetPeriodsCounter != null && unlockedDatasetPeriodsCounter.size() == periodIds.size() )
            {
                if ( maxLevel < currentLevel )
                    maxLevel = currentLevel;
                if ( minLevel > currentLevel )
                    minLevel = currentLevel;

                if ( !dSetSource.contains( organisationUnit ) )
                {
                    sheet0.addCell( new Label( colStart + columnNo, rowStart + rowNo, organisationUnit.getName(),
                        getCellFormat2() ) );
                    sheet0.mergeCells( colStart + columnNo, rowStart + rowNo + 1, colStart + columnNo, x + 1 );
                    for ( int i = 0; i < unlockedDatasetPeriodsCounter.size(); i++ )
                    {
                        sheet0.addCell( new Number( colStart
                            + organisationUnitService.getNumberOfOrganisationalLevels() + 1 + i, rowStart + rowNo,
                            unlockedDatasetPeriodsCounter.get( i ), getCellFormat2() ) );
                    }
                }
            }
            else
            {
                x--;
            }
        }
        else
        {
            if ( dSetSource.contains( organisationUnit ) )
            {
                if ( maxLevel < currentLevel )
                    maxLevel = currentLevel;
                if ( minLevel > currentLevel )
                    minLevel = currentLevel;

                sheet0.addCell( new Label( colStart + columnNo, rowStart + rowNo, organisationUnit.getName(),
                    getCellFormat2() ) );

                int periodsCounter = 0;
                for ( Period periodElement : periods )
                {
                    //DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSet, periodElement, organisationUnit );
                    boolean lockStatus = dataSetService.isLocked( dataSet, periodElement, organisationUnit, null );
                    //if ( dataSetLock != null )
                    if ( lockStatus )
                    {
                        sheet0.addCell( new Number( colStart
                            + organisationUnitService.getNumberOfOrganisationalLevels() + 1 + periodsCounter, rowStart
                            + rowNo, 1, getCellFormatGreen() ) );
                        unlockedDatasetPeriodsCounter.add( 1 );
                    }

                    else
                    {
                        unlockedDatasetPeriodsCounter.add( 0 );
                        sheet0.addCell( new Number( colStart
                            + organisationUnitService.getNumberOfOrganisationalLevels() + 1 + periodsCounter, rowStart
                            + rowNo, 0, getCellFormatRed() ) );
                    }
                    periodsCounter++;
                }
            }
            else
                x--;
        }

        /*
         * System.out.println( organisationUnit.getName() ); for ( int i = 0; i
         * < unlockedDatasetPeriodsCounter.size(); i++ ) { System.out.println(
         * unlockedDatasetPeriodsCounter.get( i ) ); } System.out.println(
         * "row : " + rowNo + " --- colno : " + columnNo ); System.out.println(
         * "level -" + currentLevel );
         */

        return unlockedDatasetPeriodsCounter;
    }

    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        return wCellformat;
    }

    // end getCellFormat1() function

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.BLACK );
        wCellformat.setWrap( false );
        return wCellformat;
    }

    public WritableCellFormat getCellFormatGreen()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GREEN );
        wCellformat.setWrap( false );
        return wCellformat;
    } // end getCellFormat1() function

    public WritableCellFormat getCellFormatRed()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.RED );
        wCellformat.setWrap( false );
        return wCellformat;
    }
}