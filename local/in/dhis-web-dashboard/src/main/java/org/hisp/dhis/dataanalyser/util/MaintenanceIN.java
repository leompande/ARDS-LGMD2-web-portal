package org.hisp.dhis.dataanalyser.util;

public class MaintenanceIN
{

    public static final String KEY_MYSQLPATH = "mysqlpath";
    public static final String KEY_ROOTDATAPATH = "rootdatapath";
    
    public static final String MYSQL_DEFAULT_PATH = "C:/DHIS2/mysql/bin";

    private String key;

    private String value;
    
    public MaintenanceIN( String key, String value )
    {
        this.key = key;
        this.value = value;
    }

    //---------------------------------------------------------------
    // Getters and Setters
    //---------------------------------------------------------------

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
          
}
