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
import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.FOREGROUND_COLOR;
import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.TEXT_COLOR;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertAlignmentString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.reportsheet.importitem.ImportItem;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */

public abstract class AbstractXMLStructure
{
    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    /**
     * The workbook we are reading from a given file
     */
    protected Workbook WORKBOOK;

    protected FormulaEvaluator evaluatorFormula;

    protected HtmlHelper htmlHelper;

    /**
     * The encoding to write
     */
    protected StringBuffer xml = new StringBuffer();

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

    protected AbstractXMLStructure( String pathFileName, Set<Integer> collectSheets, List<ImportItem> importItems )
        throws Exception
    {
        cleanUp();

        if ( getExtension( pathFileName ).equals( "xls" ) )
        {
            WORKBOOK = new HSSFWorkbook( new FileInputStream( pathFileName ) );
            htmlHelper = new HSSFHtmlHelper( (HSSFWorkbook) WORKBOOK );
        }
        else
        {
            WORKBOOK = new XSSFWorkbook( new FileInputStream( pathFileName ) );
            htmlHelper = new XSSFHtmlHelper();
        }
        
        evaluatorFormula = WORKBOOK.getCreationHelper().createFormulaEvaluator();
        
        setUpDecimalFormat();
        
        printXML( collectSheets, importItems );
    }

    /**
     * Writes out the WORKBOOK data as XML, with formatting information
     * 
     * @param bDetailed
     * 
     * @throws Exception
     */

    protected void printXML( Collection<Integer> collectSheets, List<ImportItem> importItems )
        throws Exception
    {
        printMergedInfo( collectSheets );

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            printData( sheet, importItems );
        }

        xml.append( WORKBOOK_CLOSETAG );
    }

    protected void printFormatInfo( Sheet sheet, Cell objCell )
    {
        // The format information
        CellStyle format = objCell.getCellStyle();

        if ( format != null )
        {
            xml.append( "<format a='" + convertAlignmentString( format.getAlignment() ) + "'" );
            xml.append( " w='" + sheet.getColumnWidth( objCell.getColumnIndex() ) + "'" );
            xml.append( " b='"
                + (format.getBorderBottom() + format.getBorderLeft() + format.getBorderRight() + format.getBorderTop())
                + "'" );

            Font font = WORKBOOK.getFontAt( format.getFontIndex() );

            if ( font != null )
            {
                xml.append( "><font s='" + font.getFontHeightInPoints() + "'" );
                xml.append( " b='" + (font.getBoldweight() == Font.BOLDWEIGHT_BOLD ? "1" : "0") + "'" );
                xml.append( " i='" + font.getItalic() + "'" );
                xml.append( " c='" + htmlHelper.colorStyle( TEXT_COLOR, format ) + "'/>" );

                // The cell background information
                xml.append( "<bg c='" + htmlHelper.colorStyle( FOREGROUND_COLOR, format ) + "'/>" );
                xml.append( "</format>" );
            }
            else
            {
                xml.append( "/>" );
            }
        }
    }

    public String getXml()
    {
        return xml.toString();
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract void setUpDecimalFormat();

    protected abstract void printData( int sheetNo, List<ImportItem> importItems );

    // -------------------------------------------------------------------------
    // Get the merged cell's information
    // -------------------------------------------------------------------------

    protected void cleanUp()
    {
        System.gc();
    }

    private void printMergedInfo( Collection<Integer> collectSheets )
        throws IOException
    {
        // Open the main Tag //
        xml.append( MERGEDCELL_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            printMergedInfoBySheetNo( sheet );
        }

        // Close the main Tag //
        xml.append( MERGEDCELL_CLOSETAG );
    }

    private void printMergedInfoBySheetNo( int sheetNo )
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
}