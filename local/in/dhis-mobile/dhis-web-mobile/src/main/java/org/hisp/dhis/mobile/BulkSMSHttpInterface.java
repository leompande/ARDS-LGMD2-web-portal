/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.mobile;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.collections.iterators.ArrayListIterator;

/**
 *
 * @author harsh
 */
public class BulkSMSHttpInterface
{

    private String username, password, message, phoneNo, senderName;

    private URL url;

    private String url_string, data, response = "";

    Properties properties;

    public BulkSMSHttpInterface() throws FileNotFoundException, IOException
    {

//        FileReader bulkSMSconfig= new FileReader( "BulkSMS.conf");
        properties = new Properties();

        properties.load( new FileReader( System.getenv( "DHIS2_HOME" ) + File.separator+"SMSServer.conf" ) );
System.out.println(System.getenv( "DHIS2_HOME" ) + File.separator+"SMSServer.conf");
        username = getUsername();
        password = getPassword();
        senderName = getSenderName();

//System.out.println("user="+username+"p"+password);
    }

    public String getUsername()
    {

        return properties.getProperty( "username" );
    }

    public String getPassword()
    {
        return properties.getProperty( "password" );
    }

    public String getSenderName()
    {
        return properties.getProperty( "sendername" );

    }

    public BulkSMSHttpInterface( String username, String password, String senderName )
    {
        this.username = username;
        this.password = password;
        this.senderName = senderName;
    }

    public String sendMessage( String message, String phoneNo ) throws MalformedURLException, IOException
    {

        data = "username=" + username + "&password=" + password + "&sendername=" + senderName + "&mobileno=" + phoneNo + "&message=" + message;

        //for sending sms
        url_string = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp?";

        url = new URL( url_string );
        URLConnection conn = url.openConnection();
        conn.setDoOutput( true );

        OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
        out.write( data );
        out.flush();

        InputStreamReader in = new InputStreamReader( conn.getInputStream() );
        BufferedReader buff_in = new BufferedReader( in );

        while ( buff_in.ready() )
        {
            response += buff_in.readLine() + "   ";
            System.out.println( response + " " + data );

        }

        buff_in.close();
        out.close();

        return response;
    }

    public String sendMessages( String message, List<String> phonenos ) throws MalformedURLException, IOException
    {

        Iterator it = phonenos.iterator();

        while ( it.hasNext() )
        {
            if ( phoneNo == null )
            {
                phoneNo = (String) it.next();
            } else
            {
                phoneNo += "," + it.next();
            }
        }
        //System.out.println("-------------------->"+phoneNo);



        data = "username=" + username + "&password=" + password + "&sendername=" + senderName + "&mobileno=" + phoneNo + "&message=" + message;

        //for sending multiple sms (same as single sms)
        url_string = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp?";

        url = new URL( url_string );
        URLConnection conn = url.openConnection();
        conn.setDoOutput( true );

        OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
        out.write( data );
        out.flush();

        InputStreamReader in = new InputStreamReader( conn.getInputStream() );
        BufferedReader buff_in = new BufferedReader( in );

        while ( buff_in.ready() )
        {
            response += buff_in.readLine() + "   ";
            System.out.println( response + " " + data );

        }

        buff_in.close();
        out.close();

        return response;


    }

    public String sendMessages( String message, String filename ) throws FileNotFoundException, IOException
    {
        properties = new Properties();
        List<String> phoneno = new ArrayList<String>();
        FileReader fr = new FileReader( System.getenv( "DHIS2_HOME" ) + "test.prop" );
        BufferedReader bfr = new BufferedReader( fr );

        while ( bfr.ready() )
        {

            if ( phoneNo == null )
            {
                phoneNo = bfr.readLine();
            } else
            {
                phoneNo += "," + bfr.readLine();
            }
        }
        System.out.println( phoneNo );

        data = "username=" + username + "&password=" + password + "&sendername=" + senderName + "&mobileno=" + phoneNo + "&message=" + message;

        //for sending multiple sms (same as single sms)
        url_string = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp?";

        url = new URL( url_string );
        URLConnection conn = url.openConnection();
        conn.setDoOutput( true );

        OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
        out.write( data );
        out.flush();

        InputStreamReader in = new InputStreamReader( conn.getInputStream() );
        BufferedReader buff_in = new BufferedReader( in );

        while ( buff_in.ready() )
        {
            response += buff_in.readLine() + "   ";
            System.out.println( response + " " + data );
        }

        buff_in.close();
        out.close();

        return response;
    }

    public String sendMessages( String message, String filename, int repeat ) throws FileNotFoundException, IOException
    {
        properties = new Properties();
        List<String> phonenoList = new ArrayList<String>();
        FileReader fr = new FileReader( System.getenv( "DHIS2_HOME" ) + "test.prop" );
        BufferedReader bfr = new BufferedReader( fr );
        //get nos from file
        String str;
        while ( bfr.ready() )
        {
            //str=bfr.readLine();
            //if (!str.equals( "") )
            phonenoList.add( bfr.readLine() );
        }
        //put them in a list
        int phoneListSizeOriginal = phonenoList.size();
        for ( int i = 0; i < repeat; i++ )
        {
            for ( int j = 0; j < phoneListSizeOriginal; j++ )
            {
                phonenoList.add( phonenoList.get( j ) );
            }
        }
        for ( int i = 0; i < phonenoList.size(); i++ )
        {
            System.out.print( phonenoList.get( i ) + " " );
        }

        //append them in a string
        for ( int i = 0; i < phonenoList.size(); i++ )
        {
            if ( phoneNo == null )
            {
                phoneNo = phonenoList.get( i );
                // System.out.println("->"+phoneNo);

            } else
            {
                phoneNo += "," + phonenoList.get( i );
                //   System.out.println("-->"+phoneNo+"   ----"+phonenoList.get( i ) );
            }
        }


        System.out.println( phoneNo );

        data = "username=" + username + "&password=" + password + "&sendername=" + senderName + "&mobileno=" + phoneNo + "&message=" + message;

        //for sending multiple sms (same as single sms)
        url_string = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp?";

        url = new URL( url_string );
        URLConnection conn = url.openConnection();
        conn.setDoOutput( true );

        OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
        out.write( data );
        out.flush();

        InputStreamReader in = new InputStreamReader( conn.getInputStream() );
        BufferedReader buff_in = new BufferedReader( in );

        while ( buff_in.ready() )
        {
            response += buff_in.readLine() + "   ";
            System.out.println( response + " " + data );

        }

        buff_in.close();
        out.close();

        return response;
    }

    public String checkBalance() throws MalformedURLException, IOException
    {


        data = "username=" + username + "&password=" + password;
        //for checking balance
        url_string = "http://bulksms.mysmsmantra.com:8080/WebSMS/balance.jsp?";
        url = new URL( url_string );
        URLConnection conn = url.openConnection();
        conn.setDoOutput( true );

        OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
        out.write( data );
        out.flush();

        InputStreamReader in = new InputStreamReader( conn.getInputStream() );
        BufferedReader buff_in = new BufferedReader( in );

        while ( buff_in.ready() )
        {
            response += buff_in.readLine() + "   ";
            System.out.println( response + " " + data );

        }

        buff_in.close();
        out.close();

        return response;
    }
}
