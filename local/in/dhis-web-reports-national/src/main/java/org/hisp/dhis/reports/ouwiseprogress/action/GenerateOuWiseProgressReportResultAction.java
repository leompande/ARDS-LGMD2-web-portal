
package org.hisp.dhis.reports.ouwiseprogress.action;

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
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

public class GenerateOuWiseProgressReportResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    
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
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
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

    private Integer orgUnitGroup;
    
    public void setOrgUnitGroup( Integer orgUnitGroup )
    {
        this.orgUnitGroup = orgUnitGroup;
    }

    private OrganisationUnit selectedOrgUnit;

    private List<OrganisationUnit> orgUnitList;

    private SimpleDateFormat simpleDateFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private String raFolderName;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    @Override
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        // Initialization
        raFolderName = reportService.getRAFolderName();
        simpleDateFormat = new SimpleDateFormat( "MMM-yy" );
        SimpleDateFormat dayFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        String colArray[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                                "AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ",
                                "BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ" };

        // Getting Report Details       
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        int selectedOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( ouIDTB );

                System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
                
        if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
        {            
            if( orgUnitGroup != 0 )
            {
                orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
                OrganisationUnitGroup ouGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroup );
            
                if( ouGroup != null )
                {
                    orgUnitList.retainAll( ouGroup.getMembers() );
                }
            }
            else
            {
                orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            }
            
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            
            //Hardcoded to level 2 to make report fast for state level
            if( selectedOrgUnitLevel != 2 )
            {
                orgUnitList.add( selectedOrgUnit );
            }
            
            /*
            if( orgUnitList == null || orgUnitList.size() == 0 )
            {
                orgUnitList.add( selectedOrgUnit );
            }
            */
        }

        

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
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );
        
        PeriodType selPeriodType = selReportObj.getPeriodType();
        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( selPeriodType, sDate, eDate ) );
        //List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );        
        String periodIdsByComma = getCommaDelimitedString( periodIds );
        
        //System.out.println( "periodIdsByComma :"+ periodIdsByComma );
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );

        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        
        Map<String, String> aggDeMapForselectedFacility = new HashMap<String, String>();
        aggDeMapForselectedFacility.putAll( reportService.getAggDataFromDataValueTable( ""+selectedOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
        
        int orgUnitCount = 0;
        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            Map<String, String> aggDeMap = new HashMap<String, String>();
            if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( currentOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+currentOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }

            int count1 = 0;
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";
                String tempStrForSelectedFacility = "";
                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )                    
                {
                    tempStr = selectedOrgUnit.getName();
                    tempStrForSelectedFacility = selectedOrgUnit.getName();
                }
                else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    tempStr = currentOrgUnit.getName();
                    tempStrForSelectedFacility = selectedOrgUnit.getName()+"-ONLY";
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = selectedOrgUnit.getParent().getName();
                    tempStrForSelectedFacility = selectedOrgUnit.getParent().getName();
                    
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = selectedOrgUnit.getParent().getParent().getName();
                    tempStrForSelectedFacility = selectedOrgUnit.getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "DATE-FROM" ) )
                {
                    tempStr = dayFormat.format( sDate );
                    tempStrForSelectedFacility = dayFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                {
                    tempStr = dayFormat.format( eDate );
                    tempStrForSelectedFacility = dayFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-FROM" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                    tempStrForSelectedFacility = simpleDateFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-TO" ) )
                {
                    tempStr = simpleDateFormat.format( eDate );
                    tempStrForSelectedFacility = simpleDateFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                    tempStrForSelectedFacility = " ";
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) ) 
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            //tempStr = reportService.getIndividualResultDataValue( deCodeString, sDate, eDate, currentOrgUnit, reportModelTB );
                            tempStrForSelectedFacility = getAggVal( deCodeString, aggDeMapForselectedFacility );
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            //tempStr = reportService.getResultDataValue( deCodeString, sDate, eDate, currentOrgUnit, reportModelTB );
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            tempStrForSelectedFacility = getAggVal( deCodeString, aggDeMapForselectedFacility );
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            tempStrForSelectedFacility = getAggVal( deCodeString, aggDeMapForselectedFacility );
                        }
                    }
                    else if ( sType.equalsIgnoreCase( "formula" ) )
                    {
                        tempStr = deCodeString;
                    }
                }

                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) 
                        || deCodeString.equalsIgnoreCase( "MONTH-FROM" ) || deCodeString.equalsIgnoreCase( "MONTH-TO" ) 
                        || deCodeString.equalsIgnoreCase( "DATE-FROM" ) || deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                    {
                    }
                    else
                    {
                        tempColNo += orgUnitCount;
                    }

                    try
                    {
                       
                        if( sType.equalsIgnoreCase( "formula" ) )
                        {
                            tempStr = tempStr.replace( "?", colArray[tempColNo] );
                            if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 2 )
                            {
                                sheet0.addCell( new Formula( tempColNo+1, tempRowNo, tempStr, getCellFormat1() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                        else
                       {
                            if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 2 )
                            {
                                //sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStrForSelectedFacility ), getCellFormat2() ) );
                                if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) 
                                    || deCodeString.equalsIgnoreCase( "MONTH-FROM" ) || deCodeString.equalsIgnoreCase( "MONTH-TO" ) 
                                    || deCodeString.equalsIgnoreCase( "DATE-FROM" ) || deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                                {
                                    continue;
                                }
                                else
                                {
                                    sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStrForSelectedFacility ), getCellFormat2() ) );
                                    sheet0.addCell( new Number( tempColNo+1, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                }
                                
                                
                            }
                            else
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                            }
                       }
                    }
                    catch( Exception e )
                    {
                        if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 2 )
                        {
                            //sheet0.addCell( new Label( tempColNo, tempRowNo, tempStrForSelectedFacility, getCellFormat2() ) );
                            if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) 
                                || deCodeString.equalsIgnoreCase( "MONTH-FROM" ) || deCodeString.equalsIgnoreCase( "MONTH-TO" ) 
                                || deCodeString.equalsIgnoreCase( "DATE-FROM" ) || deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                            {
                                continue;
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStrForSelectedFacility, getCellFormat2() ) );
                                sheet0.addCell( new Label( tempColNo+1, tempRowNo, tempStr, getCellFormat1() ) );
                            }
                            
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }
                }
                
                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end        
        
        // ---------------------------------------------------------------------
        // Writing Total Values
        // ---------------------------------------------------------------------
        
        if( selectedOrgUnitLevel == 2 )
        {
            WritableCellFormat totalCellformat = new WritableCellFormat( getCellFormat1() );
            totalCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
            totalCellformat.setAlignment( Alignment.CENTRE );
            totalCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
            totalCellformat.setWrap( true );
            
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while (  reportDesignIterator.hasNext() )
            {
                Report_inDesign reportDesign =  reportDesignIterator.next();
                
                String deCodeString = reportDesign.getExpression();
    
                if( deCodeString.equalsIgnoreCase( "FACILITY" ) || 
                    deCodeString.equalsIgnoreCase( "FACILITYP" ) || 
                    deCodeString.equalsIgnoreCase( "FACILITYPP" ) ||                
                    deCodeString.equalsIgnoreCase( "MONTH-FROM" ) || 
                    deCodeString.equalsIgnoreCase( "MONTH-TO" ) ||
                    deCodeString.equalsIgnoreCase( "DATE-FROM" ) ||
                    deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                {
                    continue;
                } 
                
                int tempRowNo = reportDesign.getRowno();
                int tempColNo = reportDesign.getColno();
                int sheetNo = reportDesign.getSheetno();
                
                String colStart = ""+ colArray[tempColNo];
                String colEnd = ""+ colArray[tempColNo+orgUnitCount-1];
                
                String tempFormula = "SUM("+colStart+(tempRowNo+1)+":"+colEnd+(tempRowNo+1)+")";
                
                WritableSheet totalSheet = outputReportWorkbook.getSheet( sheetNo );
    
                if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    totalSheet.addCell( new Label( tempColNo+orgUnitCount, tempRowNo, selectedOrgUnit.getName(), totalCellformat ) );
                }
                else if( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    totalSheet.addCell( new Label( tempColNo+orgUnitCount, tempRowNo, " ", totalCellformat ) );
                }
                else
                {
                    totalSheet.addCell( new Formula( tempColNo+orgUnitCount, tempRowNo, tempFormula, totalCellformat ) );    
                }
            }
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" Report Generation End Time is : " + new Date() );

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
    
    
    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );
        //Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }
    // getChildOrgUnitTree end
    
}
