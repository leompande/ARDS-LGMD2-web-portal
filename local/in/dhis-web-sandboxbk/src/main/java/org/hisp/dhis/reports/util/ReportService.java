package org.hisp.dhis.reports.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReportService 
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodStore periodStore;
    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
    
    private DBConnection dbConnection;
    
    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------

    public List<Period> getMonthlyPeriods(Date start, Date end)
    {
        List<Period> periodList = new ArrayList<Period>(periodStore.getPeriodsBetweenDates( start, end ));
        PeriodType monthlyPeriodType = getPeriodTypeObject("monthly");
        
        List<Period> monthlyPeriodList = new ArrayList<Period>();
        Iterator it = periodList.iterator();
        while(it.hasNext())
        {
            Period period = (Period) it.next();
            if(period.getPeriodType().getId() == monthlyPeriodType.getId())
            {
                monthlyPeriodList.add( period );
            }
        }
        return monthlyPeriodList;
    }
    
    
    /*
     * Returns the Period Object of the given date
     * For ex:- if the month is 3, year is 2006 and periodType Object of type Monthly then
     * it returns the corresponding Period Object
     */
    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equals( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }        
        Date lastDay = new Date( cal.getTimeInMillis() );
        System.out.println( lastDay.toString() );        
        Period newPeriod = new Period();
        newPeriod = periodStore.getPeriod( firstDay, lastDay, periodType );      
        return newPeriod;
    }
    
    
    /*
     * Returns the PeriodType Object based on the Period Type Name
     * For ex:- if we pass name as Monthly then it returns the PeriodType Object 
     * for Monthly PeriodType
     * If there is no such PeriodType returns null
     */
    public PeriodType getPeriodTypeObject(String periodTypeName)
    {        
        Collection periodTypes = periodStore.getAllPeriodTypes();
        PeriodType periodType = null;
        Iterator iter = periodTypes.iterator();
        while ( iter.hasNext() )
        {
            PeriodType tempPeriodType = (PeriodType) iter.next();
            if ( tempPeriodType.getName().toLowerCase().trim().equals( periodTypeName ) )
            {
                periodType = tempPeriodType;
                break;
            }
        }
        if ( periodType == null )
        {
            System.out.println( "No Such PeriodType" );
            return null;
        }        
        return periodType;
    }

    /*
     * Returns the child tree of the selected Orgunit
     */
    public List<OrganisationUnit> getAllChildren(OrganisationUnit selecteOU) {
        List<OrganisationUnit> ouList = new ArrayList<OrganisationUnit>();
        Iterator it = selecteOU.getChildren().iterator();
        while (it.hasNext()) {
                OrganisationUnit orgU = (OrganisationUnit) it.next();
                ouList.add(orgU);
        }
        return ouList;
    }
    
    public List<Integer> getLinelistingRecordNos(OrganisationUnit organisationUnit, Period period, String lltype )
    {
        List<Integer> recordNosList = new ArrayList<Integer>();
        
        Connection con = dbConnection.openConnection();

        Statement st = null;
        
        ResultSet rs1 = null;

        String query = "";
        
        int dataElementid = 1020;
        
        /*
        if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = LLDataSets.LLB_CHILD_NAME;
        else if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = LLDataSets.LLD_CHILD_NAME;
        else if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = LLDataSets.LLMD_MOTHER_NAME;
        */

        if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = 1020;
        else if( lltype.equalsIgnoreCase( "lldeath" ) )
            dataElementid = 1027;
        else if( lltype.equalsIgnoreCase( "llmaternaldeath" ) )
            dataElementid = 1032;

        try
        {
            st = con.createStatement();            
            
            query = "SELECT recordno FROM lldatavalue WHERE dataelementid = "+ dataElementid +" AND periodid = "+ period.getId() +" AND sourceid = "+organisationUnit.getId();
            rs1 = st.executeQuery( query );
            
            while(rs1.next())
            {
                recordNosList.add( rs1.getInt( 1 ) );
            }
            
            Collections.sort( recordNosList );
        }
        catch ( Exception e )
        {
            System.out.println("SQL Exception : "+e.getMessage());     
            return null;
        }
        finally
        {
            try
            {
                if(st != null) st.close();
                if(rs1 != null) rs1.close();
                
                if(con != null) con.close();
            }
            catch( Exception e )
            {
                System.out.println("SQL Exception : "+e.getMessage());
                return null;
            }
        }// finally block end

        return recordNosList;
    }
    
    public Map<Integer, Integer> getOUMappingForImporting()
    {
        String deMapXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
        + "importing" + File.separator + "oumapping.xml";

        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                deMapXMLFileName = newpath + File.separator + "importing" + File.separator
                    + "oumapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // DHIS2_HOME set, which will throw a NPE
        }
        
        Map<Integer, Integer> ouMap = null;
        try
        {           
            ouMap = new HashMap<Integer, Integer>();
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( deMapXMLFileName ) );

            NodeList listOfDBConnections = doc.getElementsByTagName( "ou-map" );
            int totalDEcodes = listOfDBConnections.getLength();
            
            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Node dbConnectionsNode = listOfDBConnections.item( s );
                
                if ( dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element dbConnectionElement = (Element) dbConnectionsNode;
    
                    NodeList dbOldList = dbConnectionElement.getElementsByTagName( "oldou" );
                    Element dbOldElement = (Element) dbOldList.item( 0 );
                    NodeList textDBOldList = dbOldElement.getChildNodes();
                    Integer tempOldPeriod = Integer.parseInt( ( textDBOldList.item( 0 )).getNodeValue().trim() );
    
                    NodeList dbNewList = dbConnectionElement.getElementsByTagName( "newou" );
                    Element dbNewElement = (Element) dbNewList.item( 0 );
                    NodeList textDBNewList = dbNewElement.getChildNodes();
                    Integer tempNewPeriod = Integer.parseInt( ( textDBNewList.item( 0 )).getNodeValue().trim() );
                    
                    ouMap.put( tempNewPeriod, tempOldPeriod );
                    
                    System.out.println(tempNewPeriod +" : "+ tempOldPeriod );
                                    
                }// end of if clause
            }
            
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
        return ouMap;
    }
    
    public Map<Integer, Integer> getPeriodMappingForImporting()
    {
        String deMapXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
        + "importing" + File.separator + "periodmapping.xml";

        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                deMapXMLFileName = newpath + File.separator + "importing" + File.separator
                    + "periodmapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // DHIS2_HOME set, which will throw a NPE
        }
        
        Map<Integer, Integer> periodMap = null;
        try
        {
          
            periodMap = new HashMap<Integer, Integer>();
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( deMapXMLFileName ) );

            NodeList listOfDBConnections = doc.getElementsByTagName( "period-map" );
            int totalDEcodes = listOfDBConnections.getLength();
            
            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Node dbConnectionsNode = listOfDBConnections.item( s );
                
                if ( dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element dbConnectionElement = (Element) dbConnectionsNode;
    
                    NodeList dbOldList = dbConnectionElement.getElementsByTagName( "oldperiod" );
                    Element dbOldElement = (Element) dbOldList.item( 0 );
                    NodeList textDBOldList = dbOldElement.getChildNodes();
                    Integer tempOldPeriod = Integer.parseInt( ( textDBOldList.item( 0 )).getNodeValue().trim() );
    
                    NodeList dbNewList = dbConnectionElement.getElementsByTagName( "newperiod" );
                    Element dbNewElement = (Element) dbNewList.item( 0 );
                    NodeList textDBNewList = dbNewElement.getChildNodes();
                    Integer tempNewPeriod = Integer.parseInt( ( textDBNewList.item( 0 )).getNodeValue().trim() );
                    
                    periodMap.put( tempNewPeriod, tempOldPeriod );
                    System.out.println(tempNewPeriod +" : "+ tempOldPeriod );
                                    
                }// end of if clause
            }
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
        return periodMap;

    }
    
    public Map<String, String> getDEMappingsForImporting()
    {
        String deMapXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
        + "importing" + File.separator + "demapping.xml";

        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                deMapXMLFileName = newpath + File.separator + "importing" + File.separator
                    + "demapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // DHIS2_HOME set, which will throw a NPE
        }
        
        Map<String, String> deMap = null;
        try
        {            
            deMap = new HashMap<String, String>();
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( deMapXMLFileName ) );

            NodeList listOfDBConnections = doc.getElementsByTagName( "de-map" );
            
            int totalDEcodes = listOfDBConnections.getLength();
            
            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Node dbConnectionsNode = listOfDBConnections.item( s );
                
                if ( dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element dbConnectionElement = (Element) dbConnectionsNode;
    
                    NodeList dbOldList = dbConnectionElement.getElementsByTagName( "oldde" );
                    Element dbOldElement = (Element) dbOldList.item( 0 );
                    NodeList textDBOldList = dbOldElement.getChildNodes();
                    String tempOldDE = ( textDBOldList.item( 0 )).getNodeValue().trim();
    
                    NodeList dbNewList = dbConnectionElement.getElementsByTagName( "newde" );
                    Element dbNewElement = (Element) dbNewList.item( 0 );
                    NodeList textDBNewList = dbNewElement.getChildNodes();
                    String tempNewDE = ( textDBNewList.item( 0 )).getNodeValue().trim();
                    
                    deMap.put( tempNewDE, tempOldDE );
                    System.out.println(tempNewDE +" : "+ tempOldDE );
                                    
                }// end of if clause
            }
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
        return deMap;
    }
     
    
    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @return The generated expression
     */
    public String getResultDataValue( String formula, int period, int organisationUnit, Connection con )
    {
        System.out.println("In getResultDataValue : "+formula);
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
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                
                PreparedStatement ps1 = null;
                ResultSet rs1 = null;
                String query1 = "SELECT value FROM datavalue WHERE sourceid = " + organisationUnit
                + " AND dataelementid = " + dataElementId+" AND periodid = " + period;
                                
                
                try
                {
                    ps1 = con.prepareStatement( query1 );
                    
                    rs1 = ps1.executeQuery();
                    
                    if ( rs1.next() )
                    {
                        replaceString = rs1.getString( 1 );
                    }
                    else
                    {
                        replaceString = "0";
                    }
                    
                    System.out.println(organisationUnit +" : "+ dataElementId + " : "+ period +" : "+replaceString);
                }
                catch ( Exception e )
                {
                    System.out.println( "Exception : " + e.getMessage() );
                    replaceString = "";
                }
                finally
                {
                    try
                    {
                        if ( rs1 != null )  rs1.close();
                        if ( ps1 != null )  ps1.close();
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception : " + e.getMessage() );
                        replaceString = "";
                    }
                }
                                
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );
            
            
            System.out.println( "Expression after converting: "+ buffer.toString() );

            String resultValue = "";
            if ( deFlag1 == 0 )
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                }
                if ( d == -1 )
                    d = 0.0;
                else
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = "" + (int) d;
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

}
