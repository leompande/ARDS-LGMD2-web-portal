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

import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.FOREGROUND_COLOR;
import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.TEXT_COLOR;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertAlignmentString;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.readValueByPOI;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.PATTERN_DECIMAL_FORMAT1;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.applyPatternDecimalFormat;
import static org.hisp.dhis.reportsheet.utils.NumberUtils.resetDecimalFormatByLocale;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.reportsheet.importitem.ImportItem;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class XMLStructureResponse
    extends AbstractXMLStructure
{
    // -------------------------------------------------------------------------
    // Sub-methods
    // -------------------------------------------------------------------------

    protected XMLStructureResponse( String pathFileName, Set<Integer> collectSheets, List<ImportItem> importItems )
        throws Exception
    {
        super( pathFileName, collectSheets, importItems );
    }

    @Override
    protected void setUpDecimalFormat()
    {
        resetDecimalFormatByLocale( Locale.GERMAN );
        applyPatternDecimalFormat( PATTERN_DECIMAL_FORMAT1 );
    }

    @Override
    protected void printData( int sheetNo, List<ImportItem> importItems )
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

                    readingDetailsFormattedCell( s, cell );

                    xml.append( "</col>" );
                }
            }
            xml.append( "</row>" );
        }
        xml.append( "</sheet>" );
    }

    private void readingDetailsFormattedCell( Sheet sheet, Cell objCell )
    {
        // The format information
        CellStyle format = objCell.getCellStyle();

        if ( format != null )
        {
            xml.append( "<format a='" + convertAlignmentString( format.getAlignment() ) + "'" );
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
}