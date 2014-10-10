package org.hisp.dhis.reports;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in element and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of element code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.Serializable;

@SuppressWarnings("serial")
public class Report_inDesign implements Serializable
{    
    /**
     * Service Type (like dataelement, indicator etc.)
     */
    private String stype;
    
    /**
     * Perid Type (like Cumulative, Previous year etc.)
     */
    private String ptype;
    
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
     * Number of Merged cells by rows
     */
    private int rowmerge;
    
    /**
     * Number of Merged cells by columns
     */
    private int colmerge;
    
    /**
     * Formula to calculate the values.
     */
    private String expression;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public Report_inDesign()
    {
        
    }
    
    public Report_inDesign( String stype, String ptype, int sheetno, int rowno, int colno, int rowmerge, int colmerge, String expression )
    {
        this.stype = stype;
        this.ptype = ptype;
        this.sheetno = sheetno;
        this.rowno = rowno;
        this.colno = colno;
        this.rowmerge = rowmerge;
        this.colmerge = colmerge;
        this.expression = expression;        
    }
    
    public Report_inDesign( String stype, String ptype, int sheetno, int rowno, int colno, String expression )
    {
        this.stype = stype;
        this.ptype = ptype;
        this.sheetno = sheetno;
        this.rowno = rowno;
        this.colno = colno;
        this.expression = expression;        
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getStype()
    {
        return stype;
    }

    public void setStype( String stype )
    {
        this.stype = stype;
    }

    public String getPtype()
    {
        return ptype;
    }

    public void setPtype( String ptype )
    {
        this.ptype = ptype;
    }

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

    public int getRowmerge()
    {
        return rowmerge;
    }

    public void setRowmerge( int rowmerge )
    {
        this.rowmerge = rowmerge;
    }

    public int getColmerge()
    {
        return colmerge;
    }

    public void setColmerge( int colmerge )
    {
        this.colmerge = colmerge;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }
    
    // -------------------------------------------------------------------------
    // Report Design Constants
    // -------------------------------------------------------------------------
    
    // Constants for stype
    public static final String ST_DATAELEMENT = "dataelement";
    public static final String ST_DATAELEMENT_NO_REPEAT = "dataelementnorepeat";
    public static final String ST_LLDATAELEMENT = "lldataelement";
    public static final String ST_INDICATOR = "indicator";
    public static final String ST_LLDEATHDATAELEMENTAGE = "lldeathdataelementage";
    public static final String ST_LLMATERNALDEATHDATAELEMENTAGE = "llmaternaldeathdataelement";
    public static final String ST_NON_NUMBER_DATAELEMENT = "nonnumberdataelement";
    
    // Constants for ptype
    public static final String PT_CMCY = "CMCY";
    public static final String PT_CMPY = "CMPY";
    public static final String PT_CCMCY = "CCMCY";
    public static final String PT_CCMPY = "CCMPY";
    public static final String PT_PMCY = "PMCY";
    
    // Constants for LineListDataElementMapping
    public static final String E_FACILITY = "FACILITY";
    public static final String E_PERIOD_MONTH = "PERIOD-MONTH";
    public static final String E_PERIOD_YEAR = "PERIOD-YEAR";
    
    
}