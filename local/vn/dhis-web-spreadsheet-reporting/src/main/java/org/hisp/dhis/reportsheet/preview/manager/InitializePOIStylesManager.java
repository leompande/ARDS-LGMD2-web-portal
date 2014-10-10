package org.hisp.dhis.reportsheet.preview.manager;

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

import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2009-09-18 16:45:00Z
 */
public interface InitializePOIStylesManager
{
    // -------------------------------------------------------------------------
    // Default methods
    // -------------------------------------------------------------------------

    // Header initDefaultHeader();
    void initDefaultHeader( HSSFHeader header );

    // Font initDefaultFont();
    void initDefaultFont( Font font );

    // CellStyle initDefaultCellStyle();
    void initDefaultCellStyle( CellStyle cs, Font font );

    // -------------------------------------------------------------------------
    // Use for XLS and XLSX Extension
    // -------------------------------------------------------------------------

    void initHeader( HSSFHeader header, String sCenter, String sLeft, String sRight );

    void initFont( Font test_font, String sFontName, short fontHeightInPoints, short boldWeight, short fontColor );

    void initCellStyle( CellStyle test_cs, Font font, short borderBottom, short bottomBorderColor, short borderTop,
        short topBorderColor, short borderLeft, short leftBorderColor, short borderRight, short rightBorderColor,
        short alignment, boolean bAutoWrap );

    void initCellStyle( CellStyle test_cs, Font font, short fillBgColor, short fillFgColor, short fillPattern,
        short borderBottom, short bottomBorderColor, short borderTop, short topBorderColor, short borderLeft,
        short leftBorderColor, short borderRight, short rightBorderColor, short dataFormat, short alignment,
        boolean bAutoWrap );

}
