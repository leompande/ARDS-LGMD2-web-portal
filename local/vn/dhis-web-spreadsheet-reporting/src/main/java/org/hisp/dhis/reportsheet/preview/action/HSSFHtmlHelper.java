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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class HSSFHtmlHelper
    implements HtmlHelper
{
    private final HSSFWorkbook wb;

    private final HSSFPalette colors;

    private static final HSSFColor HSSF_AUTO_COLOR = new HSSFColor.AUTOMATIC();

    public HSSFHtmlHelper( HSSFWorkbook wb )
    {
        this.wb = wb;

        colors = wb.getCustomPalette();
    }

    public String colorStyle( String type, CellStyle style )
    {
        if ( type == null || type.trim().isEmpty() )
        {
            return EMPTY;
        }

        HSSFCellStyle cellStyle = (HSSFCellStyle) style;

        short index = 0;

        if ( type.equals( FOREGROUND_COLOR ) )
        {
            index = cellStyle.getFillForegroundColor();
        }
        else
        {
            index = cellStyle.getFont( wb ).getColor();
        }

        HSSFColor color = colors.getColor( index );

        if ( index == HSSF_AUTO_COLOR.getIndex() || color == null )
        {
            return EMPTY;
        }

        short[] rgb = color.getTriplet();

        return "rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")";
    }
}
