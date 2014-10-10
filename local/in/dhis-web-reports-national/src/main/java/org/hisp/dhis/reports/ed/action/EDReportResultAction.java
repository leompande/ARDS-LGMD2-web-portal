package org.hisp.dhis.reports.ed.action;

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
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class EDReportResultAction
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

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
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

    // -------------------------------------------------------------------------
    // Getter & Setter
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

    private Integer indicatorGroupId;
    
    public void setIndicatorGroupId( Integer indicatorGroupId )
    {
        this.indicatorGroupId = indicatorGroupId;
    }

    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }
    
    private List<OrganisationUnit> orgUnitList;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        statementManager.initialise();
        
        orgUnitList = new ArrayList<OrganisationUnit>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yy" );

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "EDReport", 0 );
        
        // Period Info
        Period selectedStartPeriod = periodService.getPeriod( selectedStartPeriodId );
        Period selectedEndPeriod = periodService.getPeriod( selectedEndPeriodId );
        
        if ( selectedStartPeriod == null || selectedEndPeriod == null )
        {
            System.out.println( "There is no period with that id" );
            sheet0.addCell( new Label( 2, 2, "There is no period with that id", getCellFormat2() ) );
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            fileName = "IndicatorReport.xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.deleteOnExit();
            statementManager.destroy();
            return SUCCESS;
        }

        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate() ) );
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
        String periodIdsByComma = getCommaDelimitedString( periodIds );

        
        List<OrganisationUnit> curUserRootOrgUnitList = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getOrganisationUnits() );
        String orgUnitName = "";
        
        if ( curUserRootOrgUnitList != null && curUserRootOrgUnitList.size() > 0 )
        {
            for ( OrganisationUnit orgUnit : curUserRootOrgUnitList )
            {
                orgUnitName += orgUnit.getName() + ", ";
                List<OrganisationUnit> childList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( childList, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( childList );
                orgUnitList.add( orgUnit );
            }
        }
      
        if ( curUserRootOrgUnitList == null || curUserRootOrgUnitList.size() == 0 )
        {
            System.out.println( "There is no orgunit with that User" );
            sheet0.addCell( new Label( 2, 2, "There is no orgunit with that User", getCellFormat2() ) );
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            fileName = "IndicatorReport.xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.deleteOnExit();
            statementManager.destroy();

            return SUCCESS;
        }

        IndicatorGroup selectedIndicatorGroup = indicatorService.getIndicatorGroup( indicatorGroupId );
        
        if ( selectedIndicatorGroup == null )
        {
            System.out.println( "There is no IndicatorGroup with that id" );
            sheet0.addCell( new Label( 2, 2, "There is no IndicatorGroup with that id", getCellFormat2() ) );
            outputReportWorkbook.write();
            outputReportWorkbook.close();

            fileName = "IndicatorReport.xls";
            File outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.deleteOnExit();

            statementManager.destroy();
            return SUCCESS;
        }

        List<Indicator> indicators = new ArrayList<Indicator>( selectedIndicatorGroup.getMembers() );
        String dataElmentIdsByComma = getDataelementIds( indicators );
        
        int rowCount = 4;
        int colCount = 0;

        // Printing Header Info
        sheet0.mergeCells( colCount, rowCount, colCount, rowCount + 1 );
        sheet0.addCell( new Label( colCount++, rowCount, "Sl. No.", getCellFormat1() ) );
        sheet0.mergeCells( colCount, rowCount, colCount, rowCount + 1 );
        sheet0.addCell( new Label( colCount++, rowCount, "Facility", getCellFormat1() ) );

        for ( Indicator indicator : indicators )
        {
            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getName(), getCellFormat1() ) );
            sheet0.addCell( new Label( colCount++, rowCount + 1, "Numerator", getCellFormat1() ) );
            sheet0.addCell( new Label( colCount++, rowCount + 1, "Denominator", getCellFormat1() ) );
            sheet0.addCell( new Label( colCount++, rowCount + 1, "Indicator", getCellFormat1() ) );
        }

        // Printing Main Header Info
        String mainHeaderInfo = "Indicator Group Name - " + selectedIndicatorGroup.getName() +  " ,OrgUnit Name is "+ orgUnitName + " From : "
            + simpleDateFormat.format( selectedStartPeriod.getStartDate() ) + " To : "
            + simpleDateFormat.format( selectedEndPeriod.getStartDate() );
        sheet0.mergeCells( 0, 1, colCount - 1, 1 );
        sheet0.addCell( new Label( 0, 1, mainHeaderInfo, getCellFormat1() ) );

        rowCount += 2;
        int slno = 1;
        for ( OrganisationUnit ou : orgUnitList )
        {
            colCount = 0;
            Map<String, String> aggDeMap = new HashMap<String, String>();
            if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( ou.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
                List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+ou.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }

            if ( slno != orgUnitList.size() )
            {
                sheet0.addCell( new Number( colCount++, rowCount, slno, getCellFormat2() ) );
            }
            else
            {
                sheet0.addCell( new Label( colCount++, rowCount, "", getCellFormat2() ) );
            }
            sheet0.addCell( new Label( colCount++, rowCount, ou.getName(), getCellFormat2() ) );

            for ( Indicator indicator : indicators )
            {
                Double numValue = 0.0;
                Double denValue = 0.0;
                Double indValue = 0.0;
                
                /*
                if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                {
                     numValue = aggregationService.getAggregatedNumeratorValue( indicator, selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate(), ou );
                     denValue = aggregationService.getAggregatedDenominatorValue( indicator, selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate(), ou );
                     indValue = aggregationService.getAggregatedIndicatorValue( indicator, selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate(), ou );
                }
                
                if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                {
                    indValue = reportService.getIndividualIndicatorValue( indicator, ou, selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate() );
                    
                    String tempStr = reportService.getIndividualResultDataValue( indicator.getNumerator(), selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate(), ou, "" );
                   
                     try
                     {
                         numValue = Double.parseDouble( tempStr );
                     }
                     catch ( Exception e )
                     {
                         numValue = 0.0;
                     }

                     tempStr = reportService.getIndividualResultDataValue( indicator.getDenominator(), selectedStartPeriod.getStartDate(), selectedEndPeriod.getEndDate(), ou, "" );

                     try
                     {
                         denValue = Double.parseDouble( tempStr );
                     }
                     catch ( Exception e )
                     {
                         denValue = 0.0;
                     }
                }
                */
                try
                {
                    numValue = Double.parseDouble( reportService.getAggVal( indicator.getNumerator(), aggDeMap ) );
                }
                catch( Exception e )
                {
                    numValue = 0.0;
                }
                
                try
                {
                    denValue = Double.parseDouble( reportService.getAggVal( indicator.getDenominator(), aggDeMap ) );    
                }
                catch( Exception e )
                {
                    denValue = 0.0;
                }

                try
                {
                    if( denValue != 0.0 )
                    {
                        indValue = ( numValue / denValue ) * indicator.getIndicatorType().getFactor();
                    }
                    else
                    {
                        indValue = 0.0;
                    }
                }
                catch( Exception e )
                {
                    indValue = 0.0;
                }

                if ( indValue == null )
                    indValue = 0.0;
                if ( numValue == null )
                    numValue = 0.0;
                if ( denValue == null )
                    denValue = 0.0;
                
                numValue = Math.round( numValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                denValue = Math.round( denValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                sheet0.addCell( new Number( colCount++, rowCount, numValue, getCellFormat2() ) );
                sheet0.addCell( new Number( colCount++, rowCount, denValue, getCellFormat2() ) );
                sheet0.addCell( new Number( colCount++, rowCount, indValue, getCellFormat1() ) );
            }

            slno++;
            rowCount++;
        }

        // Printing Indicator Formula Info
        rowCount++;
        colCount = 2;

        sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
        sheet0.addCell( new Label( colCount, rowCount, "Indicator Name", getCellFormat1() ) );
        colCount += 3;
        sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
        sheet0.addCell( new Label( colCount, rowCount, "Numerator Desciption", getCellFormat1() ) );
        colCount += 3;
        sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
        sheet0.addCell( new Label( colCount, rowCount, "Denominator Description", getCellFormat1() ) );

        rowCount++;

        for ( Indicator indicator : indicators )
        {
            colCount = 2;

            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getName(), getCellFormat2() ) );
            colCount += 3;
            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getNumeratorDescription(), getCellFormat2() ) );
            colCount += 3;
            sheet0.mergeCells( colCount, rowCount, colCount + 2, rowCount );
            sheet0.addCell( new Label( colCount, rowCount, indicator.getDenominatorDescription(), getCellFormat2() ) );

            rowCount++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        
        fileName = "IndicatorReport_" + orgUnitName + "_" + simpleDateFormat.format( selectedStartPeriod.getStartDate() ) + "_" + simpleDateFormat.format( selectedEndPeriod.getStartDate() ) + ".xls";
        fileName = fileName.replaceAll( " ", "" );
        fileName = fileName.replaceAll( ",", "_" );
        
        File outputReportFile = new File( outputReportPath );
        
        System.out.println( fileName );
        
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }


    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
    public String getDataelementIds( List<Indicator> indicatorList )
    {
        String dataElmentIdsByComma = "-1";
        for( Indicator indicator : indicatorList )
        {
            String formula = indicator.getNumerator() + " + " + indicator.getDenominator();
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
        
        return dataElmentIdsByComma;
    }

}
