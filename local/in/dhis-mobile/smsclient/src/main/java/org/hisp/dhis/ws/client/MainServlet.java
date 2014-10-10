package org.hisp.dhis.ws.client;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MainServlet
    extends HttpServlet
{

    private static final long serialVersionUID = 1031422249396784970L;

    private static final String MONTHLY_PERIODTYPE = "Monthly";

    private static final String WEEKLY_PERIODTYPE = "Weekly";

    private static final String DAILY_PERIODTYPE = "Daily";

    private static final String YEARLY_PERIODTYPE = "Yearly";

    private static final String QUARTERLY_PERIODTYPE = "Quarterly";

    private String phoneNumber = null; // the mobile number sent message.

    private String timeStamp = null; // the time that message has been sent.

    private String message = null; // the message content.

    // database infomation
    private static String driverClassName = "com.mysql.jdbc.Driver";

    private static String databaseURL = "jdbc:mysql://localhost:3306/chuong";

    private static String username = "root";

    private static String password = "root";

    public void doGet( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        process( req, resp );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        process( req, resp );
    }

    private void process( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {

        // resp.setContentType("text/html");

        // Old documentation from CDAC (third party)
        // this.phoneNumber = req.getParameter( "msisdn" );
        // this.timeStamp = req.getParameter( "timestamp" );
        // this.message = req.getParameter( "message" );

        // New documentation from CDAC (third party)
        // CDAC Just send the number with format: 91..., does not append "+"
        // into
        this.phoneNumber = "+" + req.getParameter( "mobileNumber" );
        this.timeStamp = req.getParameter( "timeStamp" );
        this.message = req.getParameter( "message" );

        // Send data to main
        String[] information = new String[3];
        information[0] = this.phoneNumber;
        information[1] = this.timeStamp;
        information[2] = this.message;

        // main(information);
        String responseMessage = importSmsDataIntoDHIS( this.phoneNumber, this.timeStamp, this.message );

        System.out.println( "Response Message: " + responseMessage );
        System.out.println( "Information from CDAC: " + this.phoneNumber + " " + this.timeStamp + " " + this.message );

        PrintWriter out = resp.getWriter();
        out.print( responseMessage );

        out.flush();
        out.close();
    }

    // Importing received sms into DHIS
    public String importSmsDataIntoDHIS( String phoneNumber, String timeStamp, String smsData )
    {
        try
        {
            String smsContent = "" + smsData;
            String statusMessage = "Thank you for submitting report";
            String[] seperateSMS = mysplit( smsData, "NRHM" );
            for ( int indexSeperateSMS = 1; indexSeperateSMS < seperateSMS.length; indexSeperateSMS++ )
            {

                smsData = seperateSMS[indexSeperateSMS];

                DBConnection dbConnection = new DBConnection();
                Connection con = dbConnection.openDBConnection();

                updateRawSMSTable( con, phoneNumber + "_" + timeStamp, smsContent );

                String storedBy = "";
                String formid = null, date, periodType, updateInsertBuildQueryResponse, datavalue, periodTypeName, orgunitCode;

                int periodId = 0, sourceId = 0, dataelementid = 0, comboid = 0, periodTypeId;

                String dhis2Home = System.getenv( "DHIS2_HOME" );

                // get de, coid from csv file
                Properties props = new Properties();
                try
                {
                    String csvFilePath = dhis2Home + File.separator + "mi" + File.separator + "formIDLayout.csv";
                    props.load( new FileReader( csvFilePath ) );
                }
                catch ( IOException ex )
                {
                    ex.printStackTrace();
                }

                //
                String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";

                /*
                 * If the message have seperated messages Thai Chuong support to
                 * process multi-sms 27/03/2012
                 */
                // the order of textfiled (the index of textfield)
                int order = 0;
                if ( smsData.indexOf( '?' ) != -1 ) // It's mean: the message
                                                    // too long, and have to
                                                    // seperate into many
                                                    // messages
                {
                    // Remove "!" if exists
                    if ( smsData.indexOf( "!" ) != -1 )
                    {
                        smsData = smsData.substring( 0, smsData.indexOf( "!" ) );
                    }

                    // The order at $order? of smsData
                    order = Integer.parseInt( smsData.substring( smsData.indexOf( '$' ) + 1, smsData.indexOf( '?' ) ) );
                    smsData = smsData.substring( 0, smsData.indexOf( '$' ) + 1 )
                        + smsData.substring( smsData.indexOf( '?' ) + 1, smsData.length() );
                }

                // Thai Chuong fixed issue make server error
                // Message format "HP NRHM formId*yymm$data:data"
                // Eg: HP NRHM 1*1202$data:data...
                // Meaning: formID = 1 ; Year is 2012 and month is 02
                String[] splitDummyId = mysplit( smsData, " " );

                // print for test
                System.out.println( smsData );

                // get the formID
                String[] splitFormId = mysplit( splitDummyId[1], "*" );
                orgunitCode = splitFormId[0].substring( 0, 4 );
                formid = splitFormId[0].substring( 4, splitFormId[0].length() );

                // get periodType
                periodType = "3::Monthly"; // Monthly By Default
                periodType = getPeriodType( formid, con );
                String[] partsPeriodType = mysplit( periodType, "::" );
                periodTypeId = Integer.parseInt( partsPeriodType[0] );
                periodTypeName = partsPeriodType[1];

                // get the date for report
                String[] splitDate = mysplit( splitFormId[1], "$" );
                date = splitDate[0];

                // re-structure date value and format
                String year = "20" + date.substring( 0, 2 );
                String month = date.substring( 2, 4 );
                String day = "01"; // Monthly programs default with day = 01

                if ( periodTypeName.equalsIgnoreCase( WEEKLY_PERIODTYPE ) )
                {
                    // Calendar gregorianCalendar = new GregorianCalendar();
                    // gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                    // gregorianCalendar.setMinimalDaysInFirstWeek(4);

                    // Calendar calendar = Calendar.getInstance();
                    // calendar.set( Calendar.YEAR, Integer.parseInt( year ) );
                    // calendar.setFirstDayOfWeek( Calendar.MONDAY );
                    // calendar.set(Calendar.DAY_OF_YEAR,0);
                    // calendar.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(
                    // month ) );
                    // System.out.println( calendar.get( Calendar.YEAR ) + " : "
                    // + calendar.get( Calendar.MONTH ) + " : " + calendar.get(
                    // Calendar.DATE ) );
                    /*
                     * if( (calendar.get( Calendar.MONTH )+1) > 9 ) { month = ""
                     * + (calendar.get( Calendar.MONTH )+1); } else { month =
                     * "0" + (calendar.get( Calendar.MONTH )+1); }
                     * 
                     * if( calendar.get( Calendar.DATE ) > 9 ) { day = "" +
                     * calendar.get( Calendar.DATE ); } else { day = "0" +
                     * calendar.get( Calendar.DATE ); }
                     */
                    date = getStartdayOfWeek( Integer.parseInt( year ), Integer.parseInt( month ) );
                }
                else
                {
                    date = year + "-" + month + "-" + day;
                }

                System.out.println( "Date : " + date );

                // get date and organisationunit
                try
                {
                    // SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    // "yyyy-MM-dd" );
                    Calendar cal = new GregorianCalendar();
                    Date currDate = new Date();
                    cal.set( Integer.parseInt( date.split( "-" )[0] ), Integer.parseInt( date.split( "-" )[1] ) - 1,
                        Integer.parseInt( date.split( "-" )[2] ) );

                    System.out.println( cal.getTime().getTime() + "_" + currDate.getTime() );
                    if ( cal.getTime().getTime() > currDate.getTime() )
                    {
                        statusMessage = "future period reporting not allowed.";
                        return statusMessage;
                    }

                    String sqlQuery = "select periodid from period where periodtypeid = " + periodTypeId
                        + " and startdate = '" + date + "'";
                    System.out.println( "PeriodQuery: " + sqlQuery );
                    Statement st1 = null;
                    ResultSet rs1 = null;

                    st1 = con.createStatement();
                    rs1 = st1.executeQuery( sqlQuery );
                    if ( rs1.next() )
                    {
                        periodId = rs1.getInt( 1 );
                    }
                    else if ( (periodId = createPeriod( periodTypeName, periodTypeId, date, con )) == -1 )
                    {
                        rs1.close();
                        st1.close();
                        con.close();
                        return statusMessage = "Make sure your date is valid.";
                    }

                    rs1.close();
                    st1.close();

                    if( orgunitCode.equalsIgnoreCase( "CODE" ) )
                    {
                        sqlQuery = "select organisationunitid from organisationunit where phoneNumber = '" + phoneNumber + "'";
                    }
                    else
                    {
                        sqlQuery = "select organisationunitid from organisationunit where phoneNumber = '" + phoneNumber
                        + "' and code = '" + orgunitCode + "'";
                    }
                    Statement st2 = null;
                    ResultSet rs2 = null;
                    st2 = con.createStatement();
                    rs2 = st2.executeQuery( sqlQuery );
                    if ( rs2.next() )
                    {
                        sourceId = rs2.getInt( 1 );
                    }
                    else
                    {
                        statusMessage = "No facility is registered with this phone number OR facility code is not mapped";

                        rs2.close();
                        st2.close();
                        con.close();

                        return statusMessage;
                    }
                    rs2.close();
                    st2.close();

                    sqlQuery = "select username from users where userid in (select userinfoid from usermembership where organisationunitid ="
                        + sourceId + ")";
                    Statement st3 = null;
                    ResultSet rs3 = null;
                    st3 = con.createStatement();
                    rs3 = st3.executeQuery( sqlQuery );
                    if ( rs3.next() )
                    {
                        storedBy = rs3.getString( 1 );
                    }
                    else
                    {
                        storedBy = "[unknown]-" + phoneNumber;
                    }
                    rs3.close();
                    st3.close();
                }
                catch ( Exception ex )
                {
                    ex.printStackTrace();
                    statusMessage = "Wrong Format";
                }

                smsData = splitDate[1];

                System.out.println( smsData );

                String IdString = props.getProperty( formid );

                // System.out.println("IdString: " + IdString);
                String[] elementIds = mysplit( IdString, "," );

                String[] smsDataParsed = mysplit( smsData, ":" );

                // check if value is null

                /*
                 * for each datavalue in the sms extract the deid coid from csv
                 * file
                 */

                int flag = 1;
                for ( int i = order; i < order + smsDataParsed.length; i++ )
                {
                    // System.out.println( elementIds[i] + "=" + smsDataParsed[i
                    // - order] );
                    datavalue = smsDataParsed[i - order];
                    String[] dataelementidComboId = elementIds[i].split( "\\." );
                    dataelementid = Integer.parseInt( dataelementidComboId[0] );
                    comboid = Integer.parseInt( dataelementidComboId[1] );

                    updateInsertBuildQueryResponse = updateInsertBuildQueryForExternalClient( con, dataelementid,
                        comboid, periodId, sourceId, datavalue, phoneNumber, timeStamp, storedBy );

                    if ( updateInsertBuildQueryResponse.equalsIgnoreCase( "update" ) )
                    {
                        System.out.print( "update" );
                    }
                    else
                    {
                        if ( updateInsertBuildQueryResponse.equalsIgnoreCase( "noChange" ) )
                        {// nothing to change because datavalue from mobile is
                         // null
                         // and the database also has datvalue =null

                            System.out.print( "nothing to change: continue" );
                            continue;

                        }
                        else
                        {
                            if ( updateInsertBuildQueryResponse.startsWith( "(" ) )
                            {
                                System.out.print( "insert query plus" );
                                insertQuery += updateInsertBuildQueryResponse;
                                flag = 2;
                            }
                            else
                            {
                                // any unexpected error
                                continue;
                            }
                        }
                    }

                    System.out.println( "" );
                }

                if ( flag == 2 )
                {
                    insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );

                    // System.out.println( "Insert Query :" + insertQuery );

                    try
                    {
                        PreparedStatement ps1 = con.prepareStatement( insertQuery );
                        ps1.executeUpdate();
                        ps1.close();

                        statusMessage = "Thank you for submitting report";
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace();
                        statusMessage = e.getMessage();
                    }
                    finally
                    {
                        try
                        {
                            con.close();
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }// end for seperated message
            return statusMessage;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            return /* ex.getMessage(); */"Wrong Format";
        }
    }

    private static boolean isEvenYear( int Year )
    {
        return Year % 4 == 0 ? true : false;
    }

    private static String getStartdayOfWeek( int Year, int myOrder )
    {
        Calendar cal = Calendar.getInstance();

        // Order of the week
        int order = 0;
        int daysOfMonth[] = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if ( isEvenYear( Year ) )
        {
            // have 29 days on Feb.
        }
        else
        {
            // have 28 days on Feb.
            daysOfMonth[1] = 28;
        }

        // Calculate the order of the week
        int days = 0;
        cal.set( Calendar.YEAR, Year );
        cal.set( Calendar.MONTH, 0 );
        cal.set( Calendar.DATE, 1 );
        String returnString = "";

        for ( int i = 0; i < 12; i++ )
        {
            // Set the month
            cal.set( Calendar.MONTH, i );
            for ( int j = 1; j < daysOfMonth[i] + 1; j++ )
            {
                cal.set( Calendar.DATE, j ); // set the date
                days = cal.get( Calendar.DAY_OF_WEEK ); // set the day of week
                if ( days == 3 ) // I still don't understand why. In mobile,
                                 // it's 2
                    order++;
                if ( order == myOrder - 1 ) // because we always order ++ at the
                                            // end;
                {
                    returnString = Year + "-";
                    if ( (cal.get( Calendar.MONTH ) + 1) > 9 )
                    {
                        returnString += (cal.get( Calendar.MONTH ) + 1) + "-";
                    }
                    else
                    {
                        returnString += "0" + (cal.get( Calendar.MONTH ) + 1) + "-";
                    }
                    if ( cal.get( Calendar.DATE ) > 9 )
                    {
                        returnString += cal.get( Calendar.DATE );
                    }
                    else
                    {
                        returnString += "0" + cal.get( Calendar.DATE );
                    }
                }
            }
        }
        return returnString;
    }

    private String[] mysplit( String original, String separator )
    {
        Vector nodes = new Vector();

        // Parse nodes into vector
        int index = original.indexOf( separator );
        while ( index >= 0 )
        {
            nodes.addElement( original.substring( 0, index ) );
            original = original.substring( index + separator.length() );
            index = original.indexOf( separator );
        }

        // Get the last node
        nodes.addElement( original );

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if ( nodes.size() > 0 )
        {
            for ( int loop = 0; loop < nodes.size(); loop++ )
            {
                result[loop] = (String) nodes.elementAt( loop );
            }
        }
        return result;
    }

    public String updateInsertBuildQueryForExternalClient( Connection con, int dataelementid, int comboid,
        int periodId, int sourceId, String dataValue, String phoneNumber, String timestamp, String storedBy )
    {
        String query;
        String insertQuery;

        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        Date timeStamp = null;
        try
        {
            timeStamp = dateFormat.parse( timestamp );
        }
        catch ( ParseException ex )
        {
            ex.printStackTrace();
        }

        long t;
        if ( timeStamp == null )
        {
            Date d = new Date();
            t = d.getTime();
        }
        else
        {
            t = timeStamp.getTime();
        }

        java.sql.Date lastUpdatedDate = new java.sql.Date( t );

        query = "SELECT value FROM datavalue WHERE dataelementid = " + dataelementid + " AND categoryoptioncomboid = "
            + comboid + " AND periodid = " + periodId + " AND sourceid = " + sourceId;

        try
        {
            Statement st1 = null;
            ResultSet rs1 = null;
            st1 = con.createStatement();
            rs1 = st1.executeQuery( query );
            if ( rs1.next() )
            {
                String updateQuery = "UPDATE datavalue SET value = '" + dataValue + "', storedby = '" + storedBy
                    + "',lastupdated='" + lastUpdatedDate + "' WHERE dataelementid = " + dataelementid
                    + " AND periodid = " + periodId + " AND sourceid = " + sourceId + " AND categoryoptioncomboid = "
                    + comboid;

                PreparedStatement ps1 = con.prepareStatement( updateQuery );
                ps1.executeUpdate();
                ps1.close();

                return "update";
            }
            else
            {

                // System.out.print( "Inside else block," + dataValue );
                if ( dataValue != null && !dataValue.trim().equalsIgnoreCase( "" ) )
                {
                    insertQuery = "( " + dataelementid + ", " + periodId + ", " + sourceId + ", " + comboid + ", '"
                        + dataValue + "', '" + storedBy + "', '" + lastUpdatedDate + "' ), ";

                    rs1.close();
                    st1.close();
                    return insertQuery;
                }
                else
                {
                    rs1.close();
                    st1.close();
                    return "noChange";
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return "error";
    }

    private void updateRawSMSTable( Connection con, String senderinfo, String messagecontent )
        throws Exception
    {

        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS rawsms " + "(senderinfo VARCHAR(254) not NULL, "
            + " messagecontent VARCHAR(254))";

        PreparedStatement ps1 = con.prepareStatement( sqlCreateTable );

        String query = "INSERT INTO rawsms (senderinfo, messagecontent) VALUES (?, ?)";

        PreparedStatement ps2 = con.prepareStatement( query );

        try
        {
            ps1.executeUpdate();

            ps2.setString( 1, senderinfo );
            ps2.setString( 2, messagecontent );

            ps2.executeUpdate();
        }
        finally
        {
            ps1.close();
            ps2.close();
        }
    }

    // Main function
    // Use for running as a desktop application
    public static void main( String[] args )
    {

        // create connection
        Connection conn = null;

        // create data source
        DataSource dataSource = new DriverManagerDataSource( driverClassName, databaseURL, username, password );

        // create table if not exists
        String sqlCreatetable = "CREATE TABLE IF NOT EXISTS rawsms " + "(senderinfo VARCHAR(254) not NULL, "
            + " messagecontent VARCHAR(254))";

        // Add message to data base
        String sql = "INSERT INTO rawsms " + "(senderinfo, messagecontent) VALUES (?, ?)";

        try
        {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement( sql );

            // Create table if not exists
            ps.executeUpdate( sqlCreatetable );

            // ps.setString(1, "+9958985377_2012-01-01 12:30:25");
            // ps.setString(2, "14|555*|&$#@^()!-_=+~`");

            ps.setString( 1, args[0] + "_" + args[1] );
            ps.setString( 2, args[2] );

            // Insert data into table
            ps.executeUpdate();
            ps.close();

        }
        catch ( SQLException e )
        {
            throw new RuntimeException( e );

        }
        finally
        {
            if ( conn != null )
            {
                try
                {
                    conn.close();
                }
                catch ( SQLException e )
                {
                }
            }
        }
    }

    private String getPeriodType( String formid, Connection con )
        throws SQLException
    {
        String selectquery = "select periodtype.periodtypeid, periodtype.name from dataset INNER JOIN periodtype on dataset.periodtypeid = periodtype.periodtypeid where datasetid = "
            + formid;

        String periodType = "3::Monthly";

        Statement st1 = null;
        ResultSet rs1 = null;

        st1 = con.createStatement();
        rs1 = st1.executeQuery( selectquery );

        if ( rs1.next() )
        {
            periodType = "" + rs1.getInt( 1 );
            periodType += "::" + rs1.getString( 2 );
        }
        else
        {
            System.out.print( "error while getting periodtypeid using formid" );
        }

        rs1.close();
        st1.close();

        return periodType;
    }

    private int createPeriod( String periodTypeName, int periodTypeId, String date, Connection con )
        throws SQLException
    {
        System.out.println( "creating period......." );
        int periodId = -1;
        String selectquery = "select periodid from period order by periodid desc limit 1;";
        String startdate = null, enddate = null;
        String[] dateDMY = date.split( "-" );
        String year = dateDMY[0];
        String month = dateDMY[1];
        String day = dateDMY[2];
        // get date and organisationunit
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
        // "yyyy-MM-dd" );
        Calendar cal = new GregorianCalendar();

        Date currDate = new Date();
        cal.set( Integer.parseInt( year ), Integer.parseInt( month ) - 1, Integer.parseInt( day ) );

        if ( periodTypeName.equalsIgnoreCase( MONTHLY_PERIODTYPE ) )
        {
            startdate = year + "-" + month + "-" + cal.getActualMinimum( Calendar.DAY_OF_MONTH );
            enddate = year + "-" + month + "-" + cal.getActualMaximum( Calendar.DAY_OF_MONTH );
            System.out.println( startdate + enddate );
        }
        else if ( periodTypeName.equalsIgnoreCase( WEEKLY_PERIODTYPE ) )
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set( Calendar.YEAR, Integer.parseInt( year ) );
            calendar.set( Calendar.MONTH, Integer.parseInt( month ) - 1 );
            calendar.set( Calendar.DATE, Integer.parseInt( day ) );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            calendar.add( Calendar.DATE, 6 );

            startdate = year + "-" + month + "-" + day;
            enddate = simpleDateFormat.format( calendar.getTime() );

            System.out.println( startdate + " **AND** " + enddate );
        }

        Statement st1 = null;
        ResultSet rs1 = null;

        st1 = con.createStatement();
        rs1 = st1.executeQuery( selectquery );
        if ( rs1.next() )
        {
            periodId = rs1.getInt( 1 );
            periodId = periodId + 1; // increment periodid
            String insertquery = "insert into period (periodid,periodtypeid,startdate,enddate)Values(" + periodId + ","
                + periodTypeId + ",'" + startdate + "','" + enddate + "')";
            PreparedStatement ps = con.prepareStatement( insertquery );

            ps.executeUpdate();
        }
        else
        {
            System.out.print( "error while creating period" );
        }

        try
        {

        }
        finally
        {
            rs1.close();
            st1.close();
        }

        return periodId;
    }
}
