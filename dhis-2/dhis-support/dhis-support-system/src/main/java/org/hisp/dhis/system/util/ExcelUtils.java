package org.hisp.dhis.system.util;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.databrowser.MetaValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExcelUtils
{
    public static final String EXTENSION_XLS = ".xls";

    public static final WritableCellFormat FORMAT_LABEL = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, 13,
        WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK ) );
    
    public static final WritableCellFormat FORMAT_TEXT = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, 11,
        WritableFont.NO_BOLD, false ) );
    
    public static void printDataElementHeaders( WritableSheet sheet, I18n i18n, int row,
        int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, i18n.getString( "name" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "short_name" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "code" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "description" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "active" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "type" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "aggregation_operator" ), FORMAT_LABEL ) );
    }

    public static void addDataElementCellToSheet( WritableSheet sheet, DataElement element,
        I18n i18n, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, element.getName(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, element.getShortName(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, element.getCode(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, element.getDescription(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, getBoolean().get( element.isActive() ), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, getType().get( element.getType() ), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, getAggregationOperator().get( element.getAggregationOperator() ),
            FORMAT_TEXT ) );
    }

    public static void printIndicatorHeaders( WritableSheet sheet, I18n i18n, int row,
        int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, i18n.getString( "name" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "short_name" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "code" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "description" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "annualized" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "indicator_type" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "numerator_description" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "numerator_formula" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "denominator_description" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "denominator_formula" ), FORMAT_LABEL ) );

    }

    public static void addIndicatorCellToSheet( WritableSheet sheet, Indicator indicator,
        I18n i18n, ExpressionService expressionService, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, indicator.getName(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, indicator.getShortName(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, indicator.getCode(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, indicator.getDescription(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, getBoolean().get( indicator.isAnnualized() ), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, getType().get( indicator.getIndicatorType().getName() ), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, indicator.getNumeratorDescription(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, expressionService.getExpressionDescription( indicator.getNumerator() ), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, indicator.getDenominatorDescription(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, expressionService.getExpressionDescription( indicator.getDenominator() ), FORMAT_TEXT ) );
    }

    public static void printOrganisationUnitHeaders( WritableSheet sheet, I18n i18n,
        int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, i18n.getString( "short_name" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "code" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "opening_date" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "closed_date" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "active" ), FORMAT_LABEL ) );
        sheet.addCell( new Label( column++, row, i18n.getString( "comment" ), FORMAT_LABEL ) );

    }

    public static void addOrganisationUnitCellToSheet( WritableSheet sheet, 
        OrganisationUnit unit, I18n i18n, I18nFormat i18nFormat, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column++, row, unit.getShortName(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, unit.getCode(), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, unit.getOpeningDate() != null ? i18nFormat.formatDate( unit.getOpeningDate() ) : "", FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, unit.getClosedDate() != null ? i18nFormat.formatDate( unit.getClosedDate() ) : "", FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, i18n.getString( getBoolean().get( unit.isActive() ) ), FORMAT_TEXT ) );
        sheet.addCell( new Label( column++, row, unit.getComment(), FORMAT_TEXT ) );
    }

    public static void printOrganisationUnitHierarchyHeaders( WritableSheet sheet,
        I18n i18n, int row, int column, int level )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column, row, i18n.getString( "organisation_unit_level" ), FORMAT_LABEL ) );
        sheet.mergeCells( column, row, level - 1, row );

        row++;

        for ( int i = 1; i <= level; i++ )
        {
            sheet.addCell( new Label( column++, row, (i + ""), FORMAT_TEXT ) );
        }
    }

    public static void addOrganisationUnitHierarchyCellToSheet( WritableSheet sheet,
        OrganisationUnit unit, I18n i18n, int row, int column )
        throws RowsExceededException, WriteException
    {
        sheet.addCell( new Label( column, row, unit.getName(), FORMAT_TEXT ) );
    }

    // -------------------------------------------------------------------------
    // DataBrowser
    // -------------------------------------------------------------------------

    public static void writeDataBrowserTitle( WritableSheet sheet, WritableCellFormat formatTitle,
        WritableCellFormat formatSubTitle, String dataBrowserTitleName, String dataBrowserFromDate,
        String dataBrowserToDate, String dataBrowserPeriodType, I18n i18n )
    {
        try
        {
            sheet.addCell( new Label( 0, 0, i18n.getString( "export_results_for" ) + " " + dataBrowserTitleName,
                formatTitle ) );
            sheet.mergeCells( 0, 0, 5, 0 );

            if ( dataBrowserFromDate.length() == 0 )
            {
                dataBrowserFromDate = i18n.getString( "earliest" );
            }

            if ( dataBrowserToDate.length() == 0 )
            {
                dataBrowserToDate = i18n.getString( "latest" );
            }

            sheet.addCell( new Label( 0, 1, i18n.getString( "from_date" ) + ": " + dataBrowserFromDate + " "
                + i18n.getString( "to_date" ) + ": " + dataBrowserToDate + ", " + i18n.getString( "period_type" )
                + ": " + i18n.getString( dataBrowserPeriodType ), formatSubTitle ) );
            sheet.mergeCells( 0, 1, 5, 1 );
        }
        catch ( RowsExceededException e )
        {
            e.printStackTrace();
        }
        catch ( WriteException e )
        {
            e.printStackTrace();
        }
    }

    public static void writeDataBrowserHeaders( WritableSheet sheet, WritableCellFormat cellFormat, Grid grid, I18n i18n )
    {
        int column = 0;

        try
        {
            for ( GridHeader col : grid.getVisibleHeaders() )
            {
                //TODO use i18nFormat.formatDate for label
                
                sheet.addCell( new Label( column++, 3, col.getName(), cellFormat ) );
            }
        }
        catch ( RowsExceededException e )
        {
            e.printStackTrace();
        }
        catch ( WriteException e )
        {
            e.printStackTrace();
        }
    }

    public static void writeDataBrowserResults( WritableSheet sheet, WritableCellFormat parFormat,
        WritableCellFormat oddFormat, int fontSize, Grid grid )
    {
        // Data rows
        int i = 0;
        int row = 4;
        int column = 0;

        WritableCellFormat cellFormat = null;
        WritableFont zeroFont = new WritableFont( WritableFont.ARIAL, fontSize, WritableFont.BOLD, false,
            UnderlineStyle.NO_UNDERLINE, Colour.RED );

        Iterator<Object> rowIt = grid.getColumn( 0 ).iterator();

        for ( List<Object> rows : grid.getRows() )
        {
            i++;
            MetaValue rowMeta = (MetaValue) rowIt.next();

            cellFormat = (i % 2 == 1) ? parFormat : oddFormat;

            try
            {
                sheet.addCell( new Label( column++, row, rowMeta.getName(), cellFormat ) );

                for ( Object rowItem : rows )
                {
                    String temp = (String)rowItem;
                    
                    if ( temp == null )
                    {
                        temp = "";
                    }
                    else if ( temp.trim().matches( "0" ) )
                    {
                        cellFormat.setFont( zeroFont );
                    }

                    // Color zero values as bold red
                    sheet.addCell( new Label( column++, row, temp, cellFormat ) );
                }
            }
            catch ( RowsExceededException e )
            {
                e.printStackTrace();
            }
            catch ( WriteException e )
            {
                e.printStackTrace();
            }

            row++;
            column = 0;
        }
    }

    /**
     * Creates a writable workbook.
     * 
     * @param outputStream The output stream to write the document content.
     * @param pageSize the page size.
     * @return A Document.
     */
    public static WritableWorkbook openWorkbook( OutputStream outputStream )
    {
        try
        {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding( "UTF-8" );
            
            return Workbook.createWorkbook( outputStream, ws );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to open a writable workbook", e );
        }
    }

    /**
     * @throws IOException
     * @throws WriteException
     */
    public static void writeAndCloseWorkbook( WritableWorkbook workbook )
    {
        if ( workbook != null )
        {
            try
            {
                workbook.write();
                workbook.close();
            }
            catch ( IOException ioe )
            {
                throw new RuntimeException( "Failed to write data to workbook", ioe );
            }
            catch ( WriteException we )
            {
                throw new RuntimeException( "Failed to close the workbook", we );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static Map<Boolean, String> getBoolean()
    {
        Map<Boolean, String> map = new HashMap<Boolean, String>();
        map.put( true, "Yes" );
        map.put( false, "No" );
        return map;
    }

    private static Map<String, String> getType()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.VALUE_TYPE_STRING, "Text" );
        map.put( DataElement.VALUE_TYPE_INT, "Number" );
        map.put( DataElement.VALUE_TYPE_BOOL, "Yes/No" );
        return map;
    }

    private static Map<String, String> getAggregationOperator()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.AGGREGATION_OPERATOR_SUM, "Sum" );
        map.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, "Average" );
        map.put( DataElement.AGGREGATION_OPERATOR_COUNT, "Count" );
        return map;
    }
}
