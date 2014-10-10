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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetStore;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.util.ReportService;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.ActionSupport;

public class GenerateRoutineReportAnalyserResultAction
    extends ActionSupport
{
    private static final String NULL_REPLACEMENT = "0";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataSetStore dataSetStore;

    public void setDataSetStore( DataSetStore dataSetStore )
    {
        this.dataSetStore = dataSetStore;
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

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String contentType;

    public String getContentType()
    {
        return contentType;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    //private OrganisationUnit selectedOrgUnit;

    //public OrganisationUnit getSelectedOrgUnit()
    //{
    //    return selectedOrgUnit;
    //}

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private List<String> dataValueList;

    public List<String> getDataValueList()
    {
        return dataValueList;
    }

    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }

    private List<String> slNos;

    public List<String> getSlNos()
    {
        return slNos;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private List<String> deCodeType;

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }

    private String reportModelTB;

    public void setReportModelTB( String reportModelTB )
    {
        this.reportModelTB = reportModelTB;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }


    private Hashtable<String, String> serviceList;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        statementManager.initialise();
        
        // Initialization
        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        deCodesXMLFileName = reportList + "DECodes.xml";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        String inputTemplatePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

        //WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );

        // OrgUnit Related Info
        //selectedOrgUnit = new OrganisationUnit();
        //selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
                        
        if ( reportModelTB.equalsIgnoreCase("dynamic-group-colwise" ) )
        {
            OrganisationUnitGroup oug = organisationUnitGroupService.getOrganisationUnitGroupByName(reportList);
        	orgUnitList = new ArrayList<OrganisationUnit>( oug.getMembers() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
        }
        else
        {
        	OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnitsAtLevel( 1 ).iterator().next();
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.add( orgUnit );
        }
                
        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod();

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        Iterator it = orgUnitList.iterator();
        int orgUnitCount = 0;
        int slNo = 0;
        while ( it.hasNext() )
        {
            OrganisationUnit orgUnit = (OrganisationUnit) it.next();
            Iterator it1 = deCodesList.iterator();
            int count1 = 0;
            while ( it1.hasNext() )
            {
                String deCodeString = (String) it1.next();
                String deType = (String) deCodeType.get( count1 );
                int count = 0;
                double sum = 0.0;
                int flag1 = 0;
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>(getStartingEndingPeriods( deType ));
                if(calendarList == null || calendarList.isEmpty())
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate());
                    tempEndDate.setTime( selectedPeriod.getEndDate()); 
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }
                //System.out.println("StartDate : "+tempStartDate.getTime()+" EndDate : "+tempEndDate.getTime());                
                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = orgUnit.getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ))
                {
                    tempStr = simpleDateFormat.format( selectedPeriod.getStartDate() ).toString();
                }
                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = ""+(slNo+1);
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else
                {
                    tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), orgUnit );                    
                }
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 );
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                if(tempStr == null || tempStr.equals( " " ))
                {
                    
                }
                else
                {
                	if(reportModelTB.equalsIgnoreCase( "dynamic-group-colwise" ))
                	{
                		tempColNo += slNo;
                		
                		WritableCell cell = sheet0.getWritableCell(tempColNo, tempRowNo);

                		CellFormat cellFormat = cell.getCellFormat();
                		WritableCellFormat wCellformat = new WritableCellFormat();                        
                    
                		wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                		System.out.println("tempStr:"+tempStr+" ::: CellType : "+cell.getType().toString());
                		if (cell.getType() == CellType.LABEL)
                		{
                			Label l = (Label) cell;
                			l.setString(tempStr);
                			l.setCellFormat( cellFormat );
                		}
                		else
                		{
                			sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat) );
                		}                		
                	}
                	else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                    	if(deCodeString.equalsIgnoreCase( "SLNO" ) && slNo == orgUnitList.size()-1)
                    	{
                    		
                    	}
                    	else
                    	{	
                    		if(deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ))
                    		{
                            
                    		}
                    		else
                    		{
                    			tempRowNo += slNo;
                    		}
                                                
                    		WritableCell cell = sheet0.getWritableCell(tempColNo, tempRowNo);

                    		CellFormat cellFormat = cell.getCellFormat();
                    		WritableCellFormat wCellformat = new WritableCellFormat();                        
                        
                    		wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );                        
                    		System.out.println("tempStr:"+tempStr+" ::: CellType : "+cell.getType().toString());
                    		if (cell.getType() == CellType.LABEL)
                    		{
                    			Label l = (Label) cell;
                    			l.setString(tempStr);
                    			l.setCellFormat( cellFormat );
                    		}
                    		else
                    		{
                    			sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat) );
                    		}
                    	}	
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithoutrootfacility" ) )
                    {
                        if(deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ))
                        {
                            
                        }
                        else
                        {
                        	tempRowNo += orgUnitCount;
                        }	
                        if ( count1 == 0 )
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, "" + (orgUnitCount + 1) ) );
                        }
                        else if ( count1 == 1 )
                            sheet0.addCell( new Label( tempColNo, tempRowNo, orgUnit.getName() ) );
                        else
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr ) );
                    }
                    else
                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr ) );
                }    
                dataValueList.add( tempStr );
                // dataValueList.add(deCodeString);
                count1++;
            }// inner while loop end
            orgUnitCount++;
            slNo++;
        }// outer while loop end

        /*
         * ActionContext ctx = ActionContext.getContext(); HttpServletResponse
         * res = (HttpServletResponse) ctx.get(
         * ServletActionContext.HTTP_RESPONSE );
         * 
         * res.setContentType("application/vnd.ms-excel");
         */

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        // inputStream.close();
        // UUID.randomUUID().toString();
        // outputReportFile.canWrite();
        outputReportFile.deleteOnExit();

        statementManager.destroy();
        
        return SUCCESS;
    }

    public List<Calendar> getStartingEndingPeriods(String deType)
    {
        
        List<Calendar> calendarList = new ArrayList<Calendar>();
        
        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod();

        if ( deType.equalsIgnoreCase( "cpmcy" ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        }
        else if ( deType.equalsIgnoreCase( "ccmcy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
            // System.out.println("We are in PT if block");
        }
        else if ( deType.equalsIgnoreCase( "ccmpy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
        }
        else if ( deType.equalsIgnoreCase( "cmpy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );
        
        return calendarList;
    }
    
    /*
     * Returns Previous Month's Period object For ex:- selected period is
     * Aug-2007 it returns the period object corresponding July-2007
     */
    public Period getPreviousPeriod()
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( selectedPeriod.getStartDate() );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = reportService.getPeriodTypeObject( "monthly" );
        period = reportService.getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            periodType );

        return period;
    }

    /*
     * Returns a list which contains the DataElementCodes
     */
    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + "dhis" + File.separator + "ra_national" + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );

                // System.out.println(deCodes.get( s )+" : "+deCodeType.get( s
                // ));
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end

    /*
     * Returns the PeriodType Object for selected DataElement, If no PeriodType
     * is found then by default returns Monthly Period type
     */
    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetStore.getAllDataSets() );
        Iterator it = dataSetList.iterator();
        while ( it.hasNext() )
        {
            DataSet ds = (DataSet) it.next();
            List<DataElement> dataElementList = new ArrayList<DataElement>( ds.getDataElements() );
            if ( dataElementList.contains( de ) )
            {
                return ds.getPeriodType();
            }
        }

        return null;

    } // getDataElementPeriodType end

    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @return The generated expression
     */
    private String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {           
        try
        {               
            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );
            
            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();            
            
            while ( matcher.find() )
            {
                String replaceString = matcher.group();
                
                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf('.')+1, replaceString.length() );
                
                replaceString = replaceString.substring( 0, replaceString.indexOf('.') );
                
                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );                

                
                DataElement dataElement = dataElementService.getDataElement( dataElementId );                
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if(dataElement == null || optionCombo == null)
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if(dataElement.getType().equalsIgnoreCase( "int" ))
                {                
                    double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo, startDate, endDate, organisationUnit );                
                    System.out.println(aggregatedValue+" ---- "+startDate.toString()+" ---- "+endDate.toString());                
                    if ( aggregatedValue == AggregationService.NO_VALUES_REGISTERED )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );
                        deFlag2 = 1;
                    }
                }
                else
                {                    
                    deFlag1 = 1;
                    PeriodType dePeriodType = getDataElementPeriodType(dataElement);
                    List<Period> periodList = new ArrayList<Period>(periodService.getIntersectingPeriodsByPeriodType( dePeriodType, startDate, endDate ));
                    Period tempPeriod = new Period();
                    if(periodList == null || periodList.isEmpty()) 
                    {
                        tempPeriod = selectedPeriod; 
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get(0);
                    }
                    
                    DataValue dataValue= dataValueService.getDataValue(organisationUnit, dataElement, tempPeriod, optionCombo);
       
                    if(dataValue != null) replaceString = dataValue.getValue();
                    else replaceString = "";

                }
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            if(deFlag1 == 0)
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression(buffer.toString());
                }
                catch(Exception e)
                {
                    d = 0.0;
                }
                if(d == -1) d = 0.0;
                else 
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = ""+ (int)d;  
                }
                
                if(deFlag2 == 0)
                {
                    resultValue = " ";
                }
            }
            else
            {
               resultValue = buffer.toString(); 
            }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

}// class end
