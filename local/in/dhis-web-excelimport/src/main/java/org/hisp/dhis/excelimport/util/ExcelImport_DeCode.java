package org.hisp.dhis.excelimport.util;

import java.io.Serializable;

public class ExcelImport_DeCode implements Serializable
{
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
    public ExcelImport_DeCode()
    {
        
    }
    
    public ExcelImport_DeCode( int sheetno, int rowno, int colno, String expression )
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
