package org.hisp.dhis.reports.util;

import java.io.Serializable;

public class Report_Header implements Serializable
{
    public static final String HEADER_FACILITY = "FACILITY";
    public static final String HEADER_HEALTH_WORKER = "HEALTH_WORKER";
    public static final String HEADER_FACILITY_P = "FACILITY_P";
    public static final String HEADER_PERIOD_FROM = "PERIOD_FROM";
    public static final String HEADER_FACILITY_PP = "FACILITY_PP";
    public static final String HEADER_PERIOD_TO = "PERIOD_TO";
    public static final String HEADER_SORT_ATTRIBUTE = "SORT_ATTRIBUTE";
        
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

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public Report_Header()
    {
        
    }
    
    public Report_Header( int sheetno, int rowno, int colno, String expression )
    {
        this.sheetno = sheetno;
        this.rowno = rowno;
        this.colno = colno;
        this.expression = expression;        
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

}
