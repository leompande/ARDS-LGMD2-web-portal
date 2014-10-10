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

import static org.hisp.dhis.reportsheet.utils.ExcelUtils.readValueByPOI;

import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.reportsheet.importitem.ImportItem;

/**
 * 
 * @author Dang Duy Hieu
 * @version $Id XMLStructureResponseImport.java 2011-06-28 16:08:00$
 */

public class XMLStructureResponseImport
    extends AbstractXMLStructure
{
    public XMLStructureResponseImport( String pathFileName, Set<Integer> collectSheets, List<ImportItem> importItems )
        throws Exception
    {
        super( pathFileName, collectSheets, importItems );
    }

    @Override
    protected void setUpDecimalFormat()
    {
        // TODO: Nothing
    }

    @Override
    protected void printData( int sheetNo, List<ImportItem> importItems )
    {
        Sheet s = WORKBOOK.getSheetAt( sheetNo - 1 );

        xml.append( "<sheet id='" + (sheetNo) + "'>" );
        xml.append( "<name><![CDATA[" + s.getSheetName() + "]]></name>" );

        int run = 0;
        int i = 0;// Presented as row index
        int j = 0;// Presented as column index

        for ( Row row : s )
        {
            j = 0;

            xml.append( "<row index='" + i + "'>" );

            for ( Cell cell : row )
            {
                run = 0;

                // Remember that empty cells can contain format information
                if ( (cell.getCellStyle() != null) || cell.getCellType() != Cell.CELL_TYPE_BLANK )
                {
                    xml.append( "<col no='" + j + "'" );

                    for ( ImportItem importItem : importItems )
                    {
                        if ( (importItem.getSheetNo() == sheetNo) && (importItem.getRow() == (i + 1))
                            && (importItem.getColumn() == (j + 1)) )
                        {
                            xml.append( " id='" + importItem.getExpression() + "'>" );
                            // If there is any importItem matched the condition
                            // then break out the for loop
                            break;
                        }

                        run++;
                    }

                    if ( run == importItems.size() )
                    {
                        xml.append( ">" );
                    } // end checking

                    xml.append( "<data><![CDATA[" + readValueByPOI( i + 1, j + 1, s, evaluatorFormula ) + "]]></data>" );

                    this.printFormatInfo( s, cell );

                    xml.append( "</col>" );
                }

                j++;
            }

            i++;

            xml.append( "</row>" );
        }
        xml.append( "</sheet>" );
    }
}