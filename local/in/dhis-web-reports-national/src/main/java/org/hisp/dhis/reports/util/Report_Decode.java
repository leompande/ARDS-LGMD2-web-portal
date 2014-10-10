package org.hisp.dhis.reports.util;

import java.io.Serializable;

public class Report_Decode implements Serializable
{
    public static final String STYPE_SLNO = "SLNO";
    public static final String STYPE_BATTRIBUTE = "B-ATTRIBUTE";
    public static final String STYPE_BIDENTIFIERTYPE = "B-IDENTIFIERTYPE";
    public static final String STYPE_BPROPERTY = "B-PROPERTY";
    public static final String STYPE_PROGRAMSTAGEDUEDATE = "PROGRAMSTAGE-DUEDATE";
    public static final String STYPE_PROGRAMSTAGEDUE = "PROGRAMSTAGE-DUE";
    public static final String STYPE_SERVICEDUE = "SERVICE-DUE";
    public static final String STYPE_NA = "NA";

    /**
     * Sheet number
     */
    private int sheetno;
    
    /**
     * Row number
     */
    private int rowno;
    
    /**
     * Column number
     */
    private int colno;
    
    /**
     * Formula to calculate the values.
     */
    private String expression;

    private String stype;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public Report_Decode()
    {
        
    }
    
    public Report_Decode( int sheetno, int rowno, int colno, String expression, String stype )
    {
        this.sheetno = sheetno;
        this.rowno = rowno;
        this.colno = colno;
        this.expression = expression;
        this.stype = stype;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getSheetno()
    {
        return sheetno;
    }

    public void setSheetno( int sheetno )
    {
        this.sheetno = sheetno;
    }

    public int getRowno()
    {
        return rowno;
    }

    public void setRowno( int rowno )
    {
        this.rowno = rowno;
    }

    public int getColno()
    {
        return colno;
    }

    public void setColno( int colno )
    {
        this.colno = colno;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public String getStype()
    {
        return stype;
    }

    public void setStype( String stype )
    {
        this.stype = stype;
    }

}
