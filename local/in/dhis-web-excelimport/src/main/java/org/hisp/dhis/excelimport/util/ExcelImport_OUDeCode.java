package org.hisp.dhis.excelimport.util;

public class ExcelImport_OUDeCode
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

    /**
     * Organisation Unit Code.
     */
    private int ouCode;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ExcelImport_OUDeCode()
    {

    }

    public ExcelImport_OUDeCode(int sheetno, int rowno, int colno, String expression, int ouCode)
    {
        this.sheetno = sheetno;
        this.rowno = rowno;
        this.colno = colno;
        this.expression = expression;
        this.ouCode = ouCode;

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

    public int getOuCode() {
        return ouCode;
    }

    public void setOuCode(int ouCode) {
        this.ouCode = ouCode;
    }

}
