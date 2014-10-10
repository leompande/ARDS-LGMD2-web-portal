package org.hisp.dhis.reports.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DBConnection
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DatabaseInfoProvider provider;

    public void setProvider( DatabaseInfoProvider provider )
    {
        this.provider = provider;
    }
    
    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------
    
    private DatabaseInfo info;
    
    public DatabaseInfo getInfo()
    {
        return info;
    }

    
    Connection con = null;

    String dbConnectionXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
        + "db" + File.separator + "DBConnections.xml";

    /*
     * To retrieve the db details from xml file
     */
    public List<String> getDBDeatilsFromXML()
    {
        List<String> li = null;
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( dbConnectionXMLFileName ) );

            NodeList listOfDBConnections = doc.getElementsByTagName( "db-connection" );

            Node dbConnectionsNode = listOfDBConnections.item( 0 );
            li = new ArrayList<String>();
            if ( dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element dbConnectionElement = (Element) dbConnectionsNode;

                NodeList dbUserNameList = dbConnectionElement.getElementsByTagName( "uname" );
                Element dbUserNameElement = (Element) dbUserNameList.item( 0 );
                NodeList textDBUNList = dbUserNameElement.getChildNodes();
                li.add( 0, ( textDBUNList.item( 0 )).getNodeValue().trim() );

                NodeList dbUserPwdList = dbConnectionElement.getElementsByTagName( "upwd" );
                Element dbUserPwdElement = (Element) dbUserPwdList.item( 0 );
                NodeList textDUPwdList = dbUserPwdElement.getChildNodes();
                li.add( 1, ( textDUPwdList.item( 0 )).getNodeValue().trim() );

                NodeList dbURLList = dbConnectionElement.getElementsByTagName( "dburl" );
                Element dbURLElement = (Element) dbURLList.item( 0 );
                NodeList textDBURLList = dbURLElement.getChildNodes();
                li.add( 2, ( textDBURLList.item( 0 )).getNodeValue().trim() );

                NodeList dbStateNameList = dbConnectionElement.getElementsByTagName( "state-name" );
                Element dbStateNameElement = (Element) dbStateNameList.item( 0 );
                NodeList textDBSNameList = dbStateNameElement.getChildNodes();
                li.add( 3, ( textDBSNameList.item( 0 )).getNodeValue().trim() );
            }// end of if clause
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
        return li;
    }

    public List<String> getDBDeatilsFromHibernate()
    {
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
            + "hibernate.properties";
        FileReader fr = null;
        BufferedReader input = null;

        List<String> li = new ArrayList<String>();
        try
        {
            fr = new FileReader( path );
            input = new BufferedReader( fr );

            String s = input.readLine();
            while ( s instanceof String )
            {
                if ( s.contains( "jdbc:mysql:" ) )
                {
                    /*
                     * String tempS2[] = s.split("/"); String dbName =
                     * "jdbc:mysql://localhost/"+tempS2[tempS2.length-1].substring(0,
                     * tempS2[tempS2.length-1].indexOf('?'));
                     * System.out.println("DBName : "+dbName); li.add(0,dbName);
                     */
                    String tempS2[] = s.split( "=" );
                    String dbName = tempS2[1].substring( 0, tempS2[1].indexOf( '?' ) ).trim();
                    //System.out.println( "DBName : " + dbName );
                    li.add( 0, dbName );
                }
                if ( s.contains( "hibernate.connection.username" ) )
                {
                    String tempS2[] = s.split( "=" );
                    //System.out.println( "UserName : " + tempS2[tempS2.length - 1].trim() );
                    li.add( 1, tempS2[tempS2.length - 1].trim() );
                }
                if ( s.contains( "hibernate.connection.password" ) )
                {
                    String tempS2[] = s.split( "=" );
                    //System.out.println( "PassWord : " + tempS2[tempS2.length - 1].trim() );
                    li.add( 2, tempS2[tempS2.length - 1].trim() );
                }
                // System.out.println(s);
                s = input.readLine();
            }// while loop end
        }
        catch ( FileNotFoundException e )
        {
            System.out.println( e.getMessage() );
        }
        catch ( IOException e )
        {
            System.out.println( e.getMessage() );
        }
        finally
        {
            try
            {
                if ( fr != null )
                    fr.close();
                if ( input != null )
                    input.close();
            }
            catch ( Exception e )
            {
                System.out.println( e.getMessage() );
            }
        }

        return li;
    }// getDBDeatilsFromHibernate end

    public List<String> getDBInfo()
    {
//        DatabaseInfoProvider provider = new org.hisp.dhis.system.database.HibernateDatabaseInfoProvider();
        info = provider.getDatabaseInfo();
        
        List<String> dbInfoList = new ArrayList<String>();
        
        dbInfoList.add( 0, info.getName() );
        dbInfoList.add( 1, info.getUser() );
        dbInfoList.add( 2, info.getPassword() );
        dbInfoList.add( 3, info.getUrl() );
        
        System.out.println("DB URL: "+info.getUrl());
        
        return dbInfoList;
    }
    
    public Connection openConnection()
    {

        try
        {

            // To get From XML File
            // List li = (ArrayList)getDBDeatilsFromXML();
            // String userName = (String) li.get(0);
            // String userPass = (String) li.get(1);
            // String urlForConnection = (String) li.get(2);

            // To get From Hibernate.Properties File
            //List<String> li = (ArrayList) getDBDeatilsFromHibernate();
            //String urlForConnection = (String) li.get( 0 );
            //String userName = (String) li.get( 1 );
            //String userPass = (String) li.get( 2 );

            List<String> li = new ArrayList<String>( getDBInfo() );
            String dbName = li.get( 0 );
            String userName = li.get( 1 );
            String userPass = li.get( 2 );
            String urlForConnection = li.get( 3 );

            // Direct DBConnection
            // String userName = "dhis";
            // String userPass = "";
            // String urlForConnection = "jdbc:mysql://localhost/jh_dhis2";

            Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
            con = DriverManager.getConnection( urlForConnection, userName, userPass );
        }
        catch ( Exception e )
        {
            System.out.println( "Exception while opening connection : " + e.getMessage() );
            return null;
        }
        return con;
    } // openConnection end

    public void closeConnection()
    {
        try
        {
        }
        finally
        {
            try
            {
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( e.getMessage() );
            }
        }
    } // closeConnection end

    public List<String> getDBInfoFromXML()
    {
        String dbConnXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
        + "importing" + File.separator + "dbmapping.xml";

        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                dbConnXMLFileName = newpath + File.separator + "importing" + File.separator
                    + "dbmapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // DHIS2_HOME set, which will throw a NPE
        }
        
        List<String> li = null;
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( dbConnXMLFileName ) );

            NodeList listOfDBConnections = doc.getElementsByTagName( "db-map" );

            Node dbConnectionsNode = listOfDBConnections.item( 0 );
            li = new ArrayList<String>();
            if ( dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element dbConnectionElement = (Element) dbConnectionsNode;

                NodeList dbOldList = dbConnectionElement.getElementsByTagName( "olddb" );
                Element dbOldElement = (Element) dbOldList.item( 0 );
                NodeList textDBOldList = dbOldElement.getChildNodes();
                li.add( 0, ( textDBOldList.item( 0 )).getNodeValue().trim() );

                NodeList dbNewList = dbConnectionElement.getElementsByTagName( "newdb" );
                Element dbNewElement = (Element) dbNewList.item( 0 );
                NodeList textDBNewList = dbNewElement.getChildNodes();
                li.add( 1, ( textDBNewList.item( 0 )).getNodeValue().trim() );

                NodeList dbUserNameList = dbConnectionElement.getElementsByTagName( "username" );
                Element dbUserNameElement = (Element) dbUserNameList.item( 0 );
                NodeList textDBUNList = dbUserNameElement.getChildNodes();
                li.add( 2, ( textDBUNList.item( 0 )).getNodeValue().trim() );

                NodeList dbUserPwdList = dbConnectionElement.getElementsByTagName( "password" );
                Element dbUserPwdElement = (Element) dbUserPwdList.item( 0 );
                NodeList textDUPwdList = dbUserPwdElement.getChildNodes();
                li.add( 3, ( textDUPwdList.item( 0 )).getNodeValue().trim() );
                                
            }// end of if clause
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
        return li;
    }

    public Connection openOLDDBConnection()
    {
        try
        {
            List<String> li = new ArrayList<String>( getDBInfoFromXML() );
            
            String urlForConnection = li.get( 0 );
            String userName = li.get( 2 );
            String userPass = li.get( 3 );
            
            Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
            con = DriverManager.getConnection( urlForConnection, userName, userPass );
            
            System.out.println("OLD DB "+ urlForConnection +" Connection is Opened");
        }
        catch ( Exception e )
        {
            System.out.println( "Exception while opening connection : " + e.getMessage() );
            return null;
        }
        return con;
    } // openConnection end

    public Connection openNEWDBConnection()
    {
        try
        {
            List<String> li = new ArrayList<String>( getDBInfoFromXML() );
            
            String urlForConnection = li.get( 1 );
            String userName = li.get( 2 );
            String userPass = li.get( 3 );
            
            Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
            con = DriverManager.getConnection( urlForConnection, userName, userPass );
        }
        catch ( Exception e )
        {
            System.out.println( "Exception while opening connection : " + e.getMessage() );
            return null;
        }
        return con;
    } // openConnection end

}
