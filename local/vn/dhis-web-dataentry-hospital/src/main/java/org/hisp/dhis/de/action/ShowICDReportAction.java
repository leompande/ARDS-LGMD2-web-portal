/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
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

package org.hisp.dhis.de.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ShowICDReportAction
    implements Action
{
    private static final String EMPTY = "";

    private static final String KEY_ICDREPORT = "icdreportresult";

    /**
     * attribute_column_index is the name of an concrete attribute in database
     * This key will be declared in one properties file only
     */
    private static final String ATTRIBUTE_COLUMN_INDEX = "attribute_column_index";

    /**
     * This would be the title showed in ICD REPORTING FORM
     */
    private static final String[] titles = { "icd_element_1", "icd_element_2", "icd_element_3", "icd_element_4",
        "icd_element_5", "icd_element_6", "icd_element_7", "icd_element_8", "icd_element_9", "icd_element_10",
        "icd_element_11", "icd_element_12" };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private StatementManager statementManager;

    @Autowired
    private PeriodStore periodStore;

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer sourceId;

    public void setSourceId( Integer sourceId )
    {
        this.sourceId = sourceId;
    }

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private Integer chapterId;

    public void setChapterId( Integer chapterId )
    {
        this.chapterId = chapterId;
    }

    private Grid grid = new ListGrid();

    public Grid getGrid()
    {
        return grid;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Period period = periodStore.reloadPeriod( PeriodType.createPeriodExternalId( periodId ) );
        periodId = String.valueOf( period.getId() );

        this.setFixedHeaderStructure( grid );
        this.getEnteredData();

        SessionUtils.setSessionVar( KEY_ICDREPORT, grid );

        return SUCCESS;
    }

    public String getOrderedDataElement()
    {
        String query = null;

        query = "SELECT dataelementid, av.value AS column_index ";
        query += "FROM dataelementattributevalues AS dav ";
        query += "JOIN attributevalue AS av ON (dav.attributevalueid = av.attributevalueid) ";
        query += "JOIN attribute AS a ON (av.attributeid = a.attributeid) ";
        query += "WHERE a.name = '" + ATTRIBUTE_COLUMN_INDEX + "'";

        return query;

    }

    public void getEnteredData()
    {
        StringBuffer sqlsb = new StringBuffer();

        sqlsb
            .append( "SELECT av.attributevalueid, av.value AS Disease, sorted_de.dataelementid, dv.value, sorted_de.column_index " );
        sqlsb.append( "FROM datavalue AS dv " );
        sqlsb.append( "JOIN (" + getOrderedDataElement() );
        sqlsb.append( ") AS sorted_de ON (dv.dataelementid = sorted_de.dataelementid) " );
        sqlsb.append( "JOIN datasetmembers AS dsm ON (sorted_de.dataelementid = dsm.dataelementid) " );
        sqlsb.append( "JOIN dataelementattributevalues dav ON (dsm.dataelementid = dav.dataelementid) " );
        sqlsb.append( "JOIN attributevalue av ON (dav.attributevalueid = av.attributevalueid) " );
        sqlsb.append( "WHERE datasetid = " + this.dataSetId + " " );
        sqlsb.append( "AND periodid = " + this.periodId + " " );
        sqlsb.append( "AND sourceid = " + this.sourceId + " " );
        sqlsb.append( "AND av.value IN (SELECT attributevalue FROM attributevaluegrouporder_attributevalues " );
        sqlsb.append( (this.chapterId != null && this.chapterId != -1 ) ? "WHERE attributevaluegrouporderid = " + this.chapterId + ") " : ") " );
        sqlsb.append( "ORDER BY (av.value, sorted_de.column_index)" );

        fillUpData( grid, sqlsb, statementManager );
    }

    public void setFixedHeaderStructure( Grid grid )
    {
        int column = 0;

        grid.addHeader( new GridHeader( "", "-1", String.class.getName(), false, true ) );

        for ( String title : titles )
        {
            grid.addHeader( new GridHeader( i18n.getString( title ), String.valueOf( column ), String.class.getName(),
                false, false ) );
            column++;
        }
    }

    public int fillUpData( Grid grid, StringBuffer sqlsb, StatementManager statementManager )
    {
        final StatementHolder holder = statementManager.getHolder();

        int curRowIndex = -1;
        int columnIndex = -1;
        String rowKey = null;

        List<String> metaList = new ArrayList<String>();

        try
        {
            ResultSet rs = getScrollableResult( sqlsb.toString(), holder );

            while ( rs.next() )
            {
                rowKey = rs.getString( 2 );

                if ( rowKey != null && !metaList.contains( rowKey ) )
                {
                    this.initRow( rowKey );
                    metaList.add( rowKey );
                    curRowIndex++;
                }

                columnIndex = rs.getInt( 5 ); // Start at 1 -> n

                grid.getRow( curRowIndex ).set( columnIndex, rs.getString( 4 ) );
            }
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get data value\n", e );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Oops. Something else went wrong\n", e );
        }
        finally
        {
            holder.close();
        }

        return curRowIndex;
    }

    private void initRow( String rowKey )
    {
        grid.addRow().addValue( rowKey );

        for ( int i = 0; i < titles.length; i++ )
        {
            grid.addValue( EMPTY );
        }
    }

    private static ResultSet getScrollableResult( String sql, StatementHolder holder )
        throws SQLException
    {
        Connection con = holder.getConnection();
        Statement stm = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
        stm.execute( sql );

        return stm.getResultSet();
    }
}
