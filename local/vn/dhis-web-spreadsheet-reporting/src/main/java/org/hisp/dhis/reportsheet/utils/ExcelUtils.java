package org.hisp.dhis.reportsheet.utils;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import static org.hisp.dhis.reportsheet.utils.NumberUtils.getFormattedNumber;
import static org.apache.poi.ss.usermodel.ErrorConstants.getText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.Sheet;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 * @author Tran Thanh Tri
 * @author Chau Thu Tran
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExcelUtils
{
    private static Pattern pattern = null;

    private static Matcher matcher = null;

    private static DataFormatter dataFormatter = new DataFormatter();

    private static FormulaParsingWorkbook evaluationWorkbook = HSSFEvaluationWorkbook.create( new HSSFWorkbook() );

    private static final Integer NUMBER_OF_LETTER = new Integer( 26 );

    private static final Integer POI_CELLSTYLE_BLANK = new Integer( org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK );

    private static final Byte POI_CELLERROR_NAN = (byte) org.apache.poi.ss.usermodel.ErrorConstants.ERROR_NA;

    private static final Byte POI_CELLERROR_INFINITE = (byte) org.apache.poi.ss.usermodel.ErrorConstants.ERROR_NUM;

    public static final String ZERO = "0.0";

    public static final String TEXT = "TEXT";

    public static final String NUMBER = "NUMBER";

    public static final String EXTENSION_XLS = ".xls";

    public static final String PATTERN_FOR_ROW = "(\\d{1,})";

    public static final String PATTERN_FOR_COLUMN = "([a-zA-Z]{1,})";

    public static final String PATTERN_EXCELFORMULA = "(\\$?([a-zA-Z]{1,})\\$?(\\d{1,}!?))";

    // -------------------------------------------------------------------------
    // JXL methods
    // -------------------------------------------------------------------------

    /* JXL - Get the specified cell */
    public static Cell getCell( int row, int column, Sheet sheet )
    {
        return sheet.getCell( column - 1, row - 1 );
    }

    /* JXL - Read the value of specified cell */
    public static String readValue( int row, int column, Sheet sheet )
    {
        return sheet.getCell( column - 1, row - 1 ).getContents();
    }

    /* JXL - Write the value with customize format */
    public static void writeValue( int row, int column, String value, String type, WritableSheet sheet,
        WritableCellFormat format )
        throws RowsExceededException, WriteException
    {
        if ( row > 0 && column > 0 )
        {
            if ( type.equalsIgnoreCase( TEXT ) )
            {
                sheet.addCell( new Label( column - 1, row - 1, (value == null ? "" : value), format ) );
            }
            else if ( type.equalsIgnoreCase( NUMBER ) )
            {
                double v = Double.parseDouble( value );
                if ( v != 0 )
                {
                    sheet.addCell( new Number( column - 1, row - 1, v, format ) );
                }
                else
                {
                    sheet.addCell( new Blank( column - 1, row - 1, format ) );
                }
            }
        }
    }

    /* JXL - Write formula with customize format */
    public static void writeFormula( int row, int column, String formula, WritableSheet sheet, WritableCellFormat format )
        throws RowsExceededException, WriteException
    {
        if ( row > 0 && column > 0 )
        {
            sheet.addCell( new Formula( column - 1, row - 1, formula, format ) );
        }
    }

    // -------------------------------------------------------------------------
    // POI methods
    // -------------------------------------------------------------------------

    /* POI - Get the specified cell */
    public static org.apache.poi.ss.usermodel.Cell getCellByPOI( int row, int column,
        org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );

        if ( rowPOI == null )
        {
            return null;
        }

        return rowPOI.getCell( column - 1 );
    }

    /* POI - Read the value of specified cell */
    public static String readValueByPOI( int row, int column, org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        org.apache.poi.ss.usermodel.Cell cellPOI = getCellByPOI( row, column, sheetPOI );

        String value = "";

        if ( cellPOI != null )
        {
            switch ( cellPOI.getCellType() )
            {
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                value = cellPOI.getRichStringCellValue().toString();
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf( cellPOI.getBooleanCellValue() );
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR:
                value = getText( cellPOI.getErrorCellValue() );
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
                value = cellPOI.getCellFormula();
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                if ( DateUtil.isCellDateFormatted( cellPOI ) )
                {
                    value = String.valueOf( cellPOI.getDateCellValue() );
                }
                else
                {
                    value = String.valueOf( cellPOI.getNumericCellValue() );
                }
                break;

            default:
                value = cellPOI.getStringCellValue();
                break;
            }
        }

        return value;

    }

    /* POI - Read the special value of given cell */
    public static String readValueByPOI( int row, int column, org.apache.poi.ss.usermodel.Sheet sheetPOI,
        FormulaEvaluator evaluator )
    {
        org.apache.poi.ss.usermodel.Cell cellPOI = getCellByPOI( row, column, sheetPOI );

        String value = "";

        if ( cellPOI != null )
        {
            switch ( cellPOI.getCellType() )
            {
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                value = cellPOI.getRichStringCellValue().toString();
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf( cellPOI.getBooleanCellValue() );
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR:
                value = getText( cellPOI.getErrorCellValue() );
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
                try
                {
                    value = getFormattedNumber( dataFormatter.formatCellValue( cellPOI, evaluator ) );
                }
                catch ( Exception ex )
                {
                    value = getText( cellPOI.getErrorCellValue() );
                }
                break;
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                if ( DateUtil.isCellDateFormatted( cellPOI ) )
                {
                    value = String.valueOf( cellPOI.getDateCellValue() );
                }
                else
                {
                    value = getFormattedNumber( dataFormatter.formatCellValue( cellPOI ) );
                }
                break;

            default:
                break;
            }
        }

        return value;

    }

    public static String readValueImportingByPOI( int row, int column, org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        org.apache.poi.ss.usermodel.Cell cellPOI = getCellByPOI( row, column, sheetPOI );

        String value = "";

        if ( cellPOI != null )
        {
            switch ( cellPOI.getCellType() )
            {
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK:
                value = cellPOI.getRichStringCellValue().toString();
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf( cellPOI.getNumericCellValue() );
                break;
            }
        }
        else
        {
            System.out.println( "Cell at [" + row + "][" + column + "] is null" );
        }

        return value;

    }

    /**
     * USING FOR XLS-XLSX EXTENSION
     */

    /* POI - Write value without CellStyle */
    public static void writeValueByPOI( int row, int column, String value, String type,
        org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        if ( row > 0 && column > 0 )
        {
            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );
            org.apache.poi.ss.usermodel.CellStyle cellStylePOI = sheetPOI.getColumnStyle( column - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.getCell( column - 1 );

            if ( cellPOI == null )
            {
                cellPOI = rowPOI.createCell( column - 1 );
            }
            else
            {
                cellStylePOI = cellPOI.getCellStyle();
            }

            cellPOI.setCellStyle( cellStylePOI );

            if ( type.equalsIgnoreCase( ExcelUtils.TEXT ) )
            {
                cellPOI.setCellValue( value );
            }
            else if ( type.equalsIgnoreCase( ExcelUtils.NUMBER ) )
            {
                if ( value == null )
                {
                    cellPOI.setCellType( POI_CELLSTYLE_BLANK );
                }
                else if ( Double.isNaN( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_NAN );
                }
                else if ( Double.isInfinite( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_INFINITE );
                }
                else
                {
                    cellPOI.setCellValue( Double.parseDouble( value ) );
                    // cellPOI.setCellValue( getFormattedNumber( value ) );
                }
            }
        }
    }

    /* POI - Write value with customized CellStyle */
    public static void writeValueByPOI( int row, int column, String value, String type,
        org.apache.poi.ss.usermodel.Sheet sheetPOI, org.apache.poi.ss.usermodel.CellStyle cellStyle )
    {
        if ( row > 0 && column > 0 )
        {
            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.createCell( column - 1 );

            cellPOI.setCellStyle( cellStyle );

            if ( type.equalsIgnoreCase( ExcelUtils.TEXT ) )
            {
                cellPOI.setCellValue( value );
            }
            else if ( type.equalsIgnoreCase( ExcelUtils.NUMBER ) )
            {
                if ( value == null || Double.valueOf( value ) == 0 )
                {
                    cellPOI.setCellType( POI_CELLSTYLE_BLANK );
                }
                else if ( Double.isNaN( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_NAN );
                }
                else if ( Double.isInfinite( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_INFINITE );
                }
                else
                {
                    cellPOI.setCellValue( Double.parseDouble( value ) );
                    // cellPOI.setCellValue( getFormattedNumber( value ) );
                }
            }
        }
    }

    /* POI - Write formula without CellStyle */
    public static void writeFormulaByPOI( int row, int column, String formula,
        org.apache.poi.ss.usermodel.Sheet sheetPOI, FormulaEvaluator evaluator )
    {
        if ( row > 0 && column > 0 )
        {
            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );
            org.apache.poi.ss.usermodel.CellStyle cellStylePOI = sheetPOI.getColumnStyle( column - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.getCell( column - 1 );

            if ( cellPOI == null )
            {
                cellPOI = rowPOI.createCell( column - 1 );
            }
            else
            {
                cellStylePOI = cellPOI.getCellStyle();
            }

            cellPOI.setCellStyle( cellStylePOI );
            cellPOI.setCellFormula( formula );

            evaluator.evaluateFormulaCell( cellPOI );
        }
    }

    /* POI - Write formula with customize CellStyle */
    public static void writeFormulaByPOI( int row, int column, String formula,
        org.apache.poi.ss.usermodel.Sheet sheetPOI, org.apache.poi.ss.usermodel.CellStyle cellStyle,
        FormulaEvaluator evaluator )
    {
        if ( row > 0 && column > 0 )
        {
            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.createCell( column - 1 );
            cellPOI.setCellStyle( cellStyle );
            cellPOI.setCellFormula( formula );

            evaluator.evaluateFormulaCell( cellPOI );
        }
    }

    public static String convertColumnNumberToName( int column )
    {
        String ConvertToLetter = "";

        int iAlpha = column / 27;
        int iRemainder = column - (iAlpha * 26);

        if ( iAlpha > 0 )
        {
            ConvertToLetter = String.valueOf( ((char) (iAlpha + 64)) );
        }
        if ( iRemainder > 0 )
        {
            ConvertToLetter += String.valueOf( ((char) (iRemainder + 64)) );
        }

        return ConvertToLetter;
    }

    public static int convertColumnNameToNumber( String columnName )
    {
        try
        {
            int iCol = 0;

            if ( columnName.length() > 0 )
            {
                char[] characters = columnName.toUpperCase().toCharArray();

                for ( int i = 0; i < characters.length; i++ )
                {
                    iCol *= NUMBER_OF_LETTER;
                    iCol += (characters[i] - 'A' + 1);
                }
            }
            return iCol;
        }
        catch ( Exception e )
        {
            return -1;
        }
    }

    public static String convertAlignmentString( String s )
    {
        if ( s.equalsIgnoreCase( "centre" ) )
        {
            return "center";
        }
        else
        {
            return s;
        }
    }

    public static String convertAlignmentString( Short s )
    {
        String align = "";

        switch ( s )
        {
        case CellStyle.ALIGN_CENTER:
        case CellStyle.ALIGN_CENTER_SELECTION:
            align = "center";
            break;

        case CellStyle.ALIGN_JUSTIFY:
            align = "justify";
            break;

        case CellStyle.ALIGN_LEFT:
            align = "left";
            break;

        case CellStyle.ALIGN_RIGHT:
            align = "right";
            break;

        default:
            align = "general";
            break;
        }

        return align;
    }

    public static String convertVerticalString( Short s )
    {
        String valign = "";

        switch ( s )
        {
        case CellStyle.VERTICAL_TOP:
            valign = "top";
            break;

        case CellStyle.VERTICAL_CENTER:
            valign = "center";
            break;

        case CellStyle.VERTICAL_BOTTOM:
            valign = "bottom";
            break;

        default:
            valign = "justify";
            break;
        }

        return valign;
    }

    public static String generateExcelFormula( String string_formula, int indexRow, int indexCol )
    {
        Pattern pattern_formula = Pattern.compile( PATTERN_EXCELFORMULA );
        Matcher matcher_formula = pattern_formula.matcher( string_formula );

        String s = null;
        String sTemp = null;
        StringBuffer buffer = null;

        while ( matcher_formula.find() )
        {
            buffer = new StringBuffer();

            s = matcher_formula.group().replaceAll( " ", "" );

            if ( !s.endsWith( "!" ) )
            {
                sTemp = s;

                if ( s.startsWith( "$" ) )
                {
                    if ( s.lastIndexOf( '$' ) > 0 )
                    {
                        buffer = new StringBuffer( s );
                    }
                    else
                    {
                        applyingPatternForRow( s, buffer, indexRow );
                    }
                }
                else if ( s.lastIndexOf( '$' ) > 0 )
                {
                    applyingPatternForColumn( s, buffer, indexCol );
                }
                else
                {
                    applyingPatternForColumn( s, buffer, indexCol );

                    s = buffer.toString();
                    buffer = new StringBuffer();

                    applyingPatternForRow( s, buffer, indexRow );
                }

                string_formula = string_formula.replace( sTemp, buffer.substring( 0 ) );
            }
        }

        return string_formula;
    }

    public static boolean isValidFormula( String formula )
    {
        try
        {
            FormulaParser.parse( formula, evaluationWorkbook, FormulaType.CELL, -1 );
        }
        catch ( Exception e )
        {
            return false;
        }

        return true;
    }

    public static boolean isValidFormula( String formula, int formulaType, int sheetIndex )
    {
        try
        {
            FormulaParser.parse( formula, evaluationWorkbook, formulaType, sheetIndex );
        }
        catch ( Exception e )
        {
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static void applyingPatternForColumn( String sCell, StringBuffer buffer, int iCol )
    {
        pattern = Pattern.compile( PATTERN_FOR_COLUMN );
        matcher = pattern.matcher( sCell );

        if ( matcher.find() )
        {
            sCell = ExcelUtils.convertColumnNumberToName( ExcelUtils.convertColumnNameToNumber( matcher.group() )
                + iCol );
            matcher.appendReplacement( buffer, sCell );
        }

        matcher.appendTail( buffer );
    }

    private static void applyingPatternForRow( String sCell, StringBuffer buffer, int iRow )
    {
        pattern = Pattern.compile( PATTERN_FOR_ROW );
        matcher = pattern.matcher( sCell );

        if ( matcher.find() )
        {
            sCell = Integer.parseInt( matcher.group() ) + iRow + "";
            matcher.appendReplacement( buffer, sCell );
        }
        matcher.appendTail( buffer );
    }

}
