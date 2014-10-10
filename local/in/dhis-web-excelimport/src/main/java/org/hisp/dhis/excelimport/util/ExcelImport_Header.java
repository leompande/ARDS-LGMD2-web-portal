package org.hisp.dhis.excelimport.util;

import java.io.Serializable;

public class ExcelImport_Header implements Serializable
{
    public static final String HEADER_PERIOD = "PERIOD";
    public static final String HEADER_FINANCIALYEAR = "FINANCIAL_YEAR";
    public static final String HEADER_FORMAT = "FORMAT";
    public static final String HEADER_FACILITY_PARENT = "FACILITY_PARENT";
    public static final String HEADER_PERIODICITY = "PERIODICITY";

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
    public ExcelImport_Header()
    {
        
    }
    
    public ExcelImport_Header( int sheetno, int rowno, int colno, String expression )
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
