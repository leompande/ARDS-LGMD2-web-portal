package org.hisp.dhis.ws.client;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection
{
    
    public Connection openDBConnection()
    {
        Connection conn = null;
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "hp_mobile";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root"; 
        String password = "root";
        
        try 
        {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        return conn;
    }

}
