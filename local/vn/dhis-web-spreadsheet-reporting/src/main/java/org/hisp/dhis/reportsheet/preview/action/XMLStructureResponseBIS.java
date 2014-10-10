package org.hisp.dhis.reportsheet.preview.action;

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

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertAlignmentString;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertVerticalString;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.readValueByPOI;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.PATTERN_DECIMAL_FORMAT1;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.applyPatternDecimalFormat;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.resetDecimalFormatByLocale;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class XMLStructureResponseBIS
{
    /**
     * The encoding to write
     */
    private StringBuffer xml = new StringBuffer( 200000 );

    /**
     * The workbook we are reading from a given file
     */
    private Workbook WORKBOOK;

    private FormulaEvaluator evaluatorFormula;

    private boolean bWRITE_VERSION;

    private boolean bWRITE_DTD;

    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private static final String DOCTYPE_NORMAL = "<!DOCTYPE WORKBOOK SYSTEM \"WORKBOOK.dtd\">";

    private static final String DOCTYPE_FORMAT = "<!DOCTYPE WORKBOOK SYSTEM \"formatWORKBOOK.dtd\">";

    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    // -------------------------------------------------------------------------
    // Get & Set methods
    // -------------------------------------------------------------------------

    protected String getXml()
    {
        return xml.toString();
    }

    private void cleanUpForResponse()
    {
        System.gc();
    }

    /**
     * Constructor
     * 
     * @param w The workbook to interrogate
     * @param enc The encoding used by the output stream. Null or unrecognized
     *        values cause the encoding to default to UTF8
     * @param f Indicates whether the generated XML document should contain the
     *        cell format information
     * @exception java.io.IOException
     */

    public XMLStructureResponseBIS( String pathFileName, Set<Integer> collectSheets, boolean bWriteDTD,
        boolean bWriteVersion, boolean bFormat, boolean bDetailed, boolean bWriteDescription )
        throws Exception
    {
        this.cleanUpForResponse();
        this.bWRITE_DTD = bWriteDTD;
        this.bWRITE_VERSION = bWriteVersion;

        if ( getExtension( pathFileName ).equals( "xls" ) )
        {
            this.WORKBOOK = new HSSFWorkbook( new FileInputStream( pathFileName ) );
        }
        else
        {
            this.WORKBOOK = new XSSFWorkbook( new FileInputStream( pathFileName ) );
        }

        resetDecimalFormatByLocale( Locale.GERMAN );
        applyPatternDecimalFormat( PATTERN_DECIMAL_FORMAT1 );

        this.evaluatorFormula = WORKBOOK.getCreationHelper().createFormulaEvaluator();

        if ( bFormat )
        {
            writeFormattedXML( collectSheets, bDetailed, bWriteDescription );
        }
        else
        {
            writeXML( collectSheets );
        }
    }

    /**
     * Writes out the WORKBOOK data as XML, without formatting information
     */
    private void writeXML( Collection<Integer> collectSheets )
        throws IOException
    {
        if ( this.bWRITE_VERSION )
        {
            xml.append( XML_VERSION );
        }

        if ( this.bWRITE_DTD )
        {
            xml.append( DOCTYPE_NORMAL );
        }

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheetNo : collectSheets )
        {
            Sheet sheet = WORKBOOK.getSheetAt( sheetNo - 1 );

            xml.append( "<sheet id='" + sheet + "'>" );
            xml.append( "<name><![CDATA[" + sheet.getSheetName() + "]]></name>" );

            for ( Row row : sheet )
            {
                xml.append( "<row number='" + row.getRowNum() + "'>" );

                for ( Cell cell : row )
                {
                    if ( cell.getCellType() != Cell.CELL_TYPE_BLANK )
                    {
                        xml.append( "<col number='" + cell.getColumnIndex() + "'>" );
                        xml.append( "<![CDATA["
                            + readValueByPOI( row.getRowNum() + 1, cell.getColumnIndex() + 1, sheet, evaluatorFormula )
                            + "]]>" );
                        xml.append( "</col>" );
                    }
                }
                xml.append( "</row>" );
            }
            xml.append( "</sheet>" );
        }
        xml.append( WORKBOOK_CLOSETAG );
    }

    /**
     * Writes out the WORKBOOK data as XML, with formatting information
     * 
     * @param bDetailed
     * 
     * @throws Exception
     */

    private void writeFormattedXML( Collection<Integer> collectSheets, boolean bDetailed, boolean bWriteDescription )
        throws Exception
    {
        if ( bWriteDescription )
        {
            this.writeXMLMergedDescription( collectSheets );
        }

        if ( this.bWRITE_VERSION )
        {
            xml.append( XML_VERSION );
        }

        if ( this.bWRITE_DTD )
        {
            xml.append( DOCTYPE_FORMAT );
        }

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            writeBySheetNo( sheet, bDetailed );
        }

        xml.append( WORKBOOK_CLOSETAG );
    }

    // -------------------------------------------------------------------------
    // Sub-methods
    // -------------------------------------------------------------------------

    private void writeBySheetNo( int sheetNo, boolean bDetailed )
    {
        Sheet s = WORKBOOK.getSheetAt( sheetNo - 1 );

        xml.append( "<sheet id='" + (sheetNo) + "'>" );
        xml.append( "<name><![CDATA[" + s.getSheetName() + "]]></name>" );

        for ( Row row : s )
        {
            xml.append( "<row index='" + row.getRowNum() + "'>" );

            for ( Cell cell : row )
            {
                // Remember that empty cells can contain format information
                if ( (cell.getCellStyle() != null || cell.getCellType() != Cell.CELL_TYPE_BLANK)
                    && !s.isColumnHidden( cell.getColumnIndex() ) )
                {
                    xml.append( "<col no='" + cell.getColumnIndex() + "'>" );
                    xml.append( "<data><![CDATA["
                        + readValueByPOI( row.getRowNum() + 1, cell.getColumnIndex() + 1, s, evaluatorFormula )
                        + "]]></data>" );

                    this.readingDetailsFormattedCell( s, cell, bDetailed );

                    xml.append( "</col>" );
                }
            }
            xml.append( "</row>" );
        }
        xml.append( "</sheet>" );
    }

    private void readingDetailsFormattedCell( Sheet sheet, Cell objCell, boolean bDetailed )
    {
        // The format information
        CellStyle format = objCell.getCellStyle();

        if ( format != null )
        {
            xml.append( "<format a='" + convertAlignmentString( format.getAlignment() ) + "'" );
            xml.append( " w='" + sheet.getColumnWidth( objCell.getColumnIndex() ) + "'" );
            xml.append( " b='" + (format.getBorderBottom() + format.getBorderLeft() + format.getBorderRight() + format.getBorderTop()) + "'" );

            Font font = WORKBOOK.getFontAt( format.getFontIndex() );

            if ( bDetailed && font != null )
            {
                xml.append( " valign='" + convertVerticalString( format.getVerticalAlignment() ) + "'>" );

                xml.append( "<font s='" + font.getFontHeightInPoints() + "'" );
                xml.append( " b='" + (font.getBoldweight() == Font.BOLDWEIGHT_BOLD ? "1" : "0") + "'" );
                xml.append( " i='" + font.getItalic() + "'" );
                xml.append( " u='" + FontUnderline.valueOf( font.getUnderline() ).name() + "'" );
                xml.append( " c='" + getSimilarColor( font.getColor() ) + "'" );
                xml.append( "/>" );

                // The cell background information
                if ( format.getFillBackgroundColor() != IndexedColors.WHITE.getIndex()
                    || format.getFillPattern() != HSSFPatternFormatting.NO_FILL )
                {
                    xml.append( "<bg c='" +  format.getFillBackgroundColor() + "'/>" );
                }

                // The cell number/date format
                if ( !format.getDataFormatString().equals( "" ) )
                {
                    xml.append( "<dfs s='" + format.getDataFormatString() + "'/>" );
                }
                xml.append( "</format>" );
            }
            else
            {
                xml.append( "/>" );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Get the merged cell's information
    // -------------------------------------------------------------------------
    private void writeXMLMergedDescription( Collection<Integer> collectSheets )
        throws IOException
    {
        // Get the Range of the Merged Cells //
        if ( this.bWRITE_VERSION )
        {
            xml.append( XML_VERSION );
        }

        // Open the main Tag //
        xml.append( MERGEDCELL_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            writeBySheetNo( sheet );
        }

        // Close the main Tag //
        xml.append( MERGEDCELL_CLOSETAG );
    }

    private void writeBySheetNo( int sheetNo )
    {
        Sheet sheet = WORKBOOK.getSheetAt( sheetNo - 1 );
        CellRangeAddress cellRangeAddress = null;

        for ( int i = 0; i < sheet.getNumMergedRegions(); i++ )
        {
            cellRangeAddress = sheet.getMergedRegion( i );

            if ( cellRangeAddress.getFirstColumn() != cellRangeAddress.getLastColumn() )
            {
                xml.append( "<cell " + "iKey='" + (sheetNo) + "#" + cellRangeAddress.getFirstRow() + "#"
                    + cellRangeAddress.getFirstColumn() + "'>"
                    + (cellRangeAddress.getLastColumn() - cellRangeAddress.getFirstColumn() + 1) + "</cell>" );
            }
        }
    }

    private String getSimilarColor( short index )
    {
        if ( IndexedColors.DARK_BLUE.getIndex() == index || IndexedColors.BLUE.getIndex() == index )
        {
            return "blue";
        }
        
        if ( IndexedColors.BROWN.getIndex() == index )
        {
            return "brown";
        }

        return "";
    }
}