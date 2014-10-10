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
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2009-09-18 16:45:00Z
 */
public class DefaultInitializePOIStylesManager
    implements InitializePOIStylesManager
{
    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    private static final String STYLE_DEFAULT_TITLE_CENTER = "Center Header";

    private static final String STYLE_DEFAULT_TITLE_LEFT = "Left Header";

    private static final String STYLE_DEFAULT_TITLE_RIGHT = HSSFHeader.font( "Stencil-Normal", "Italic" )
        + HSSFHeader.fontSize( (short) 16 ) + "Right w/ Stencil-Normal Italic font and size 12";

    private static final String STYLE_DEFAULT_FONT_NAME = "Tahoma";

    private static final short STYLE_DEFAULT_FONT_HEIGHT = 11;

    private static final short STYLE_DEFAULT_FONT_WEIGHT = Font.BOLDWEIGHT_NORMAL;

    private static final short STYLE_DEFAULT_PATTERN = CellStyle.SOLID_FOREGROUND;

    private static final short STYLE_DEFAULT_BORDER = CellStyle.BORDER_THIN;

    private static final short STYLE_DEFAULT_FONT_COLOR = IndexedColors.BROWN.getIndex();

    private static final short STYLE_DEFAULT_BACK_FORE_GROUND_COLOR = IndexedColors.WHITE.getIndex();

    private static final short STYLE_DEFAULT_BORDER_COLOR = IndexedColors.LIGHT_ORANGE.getIndex();

    // -------------------------------------------------------------------------
    // Default Methods - Using for XLS Extension
    // -------------------------------------------------------------------------

    @SuppressWarnings( "static-access" )
    public void initDefaultHeader( HSSFHeader poi_header )
    {
        poi_header.setCenter( this.STYLE_DEFAULT_TITLE_CENTER );
        poi_header.setLeft( this.STYLE_DEFAULT_TITLE_LEFT );
        poi_header.setRight( this.STYLE_DEFAULT_TITLE_RIGHT );

    }

    @SuppressWarnings( "static-access" )
    public void initDefaultFont( Font poi_font )
    {
        poi_font.setFontName( this.STYLE_DEFAULT_FONT_NAME );
        poi_font.setFontHeightInPoints( this.STYLE_DEFAULT_FONT_HEIGHT );
        poi_font.setBoldweight( this.STYLE_DEFAULT_FONT_WEIGHT );
        poi_font.setColor( this.STYLE_DEFAULT_FONT_COLOR );

    }

    @SuppressWarnings( "static-access" )
    public void initDefaultCellStyle( CellStyle poi_cs, Font poi_font )
    {
        poi_cs.setFont( poi_font );
        poi_cs.setFillBackgroundColor( this.STYLE_DEFAULT_BACK_FORE_GROUND_COLOR );
        poi_cs.setFillForegroundColor( this.STYLE_DEFAULT_BACK_FORE_GROUND_COLOR );
        poi_cs.setFillPattern( this.STYLE_DEFAULT_PATTERN );
        poi_cs.setBorderBottom( this.STYLE_DEFAULT_BORDER );
        poi_cs.setBottomBorderColor( this.STYLE_DEFAULT_BORDER_COLOR );
        poi_cs.setBorderTop( this.STYLE_DEFAULT_BORDER );
        poi_cs.setTopBorderColor( this.STYLE_DEFAULT_BORDER_COLOR );
        poi_cs.setBorderLeft( this.STYLE_DEFAULT_BORDER );
        poi_cs.setLeftBorderColor( this.STYLE_DEFAULT_BORDER_COLOR );
        poi_cs.setBorderRight( this.STYLE_DEFAULT_BORDER );
        poi_cs.setRightBorderColor( this.STYLE_DEFAULT_BORDER_COLOR );

    }

    // -------------------------------------------------------------------------
    // Customized Methods - Using for XLS Extension
    // -------------------------------------------------------------------------

    public void initHeader( HSSFHeader poi_header, String center, String left, String right )
    {

        poi_header.setCenter( center );
        poi_header.setLeft( left );
        poi_header.setRight( right );

    }

    public void initFont( Font poi_font, String fontName, short fontHeightInPoints, short boldWeight, short fontColor )
    {

        poi_font.setFontName( fontName );
        poi_font.setFontHeightInPoints( fontHeightInPoints );
        poi_font.setBoldweight( boldWeight );
        poi_font.setColor( fontColor );

    }

    public void initCellStyle( CellStyle poi_cs, Font font, short borderBottom, short bottomBorderColor,
        short borderTop, short topBorderColor, short borderLeft, short leftBorderColor, short borderRight,
        short rightBorderColor, short alignment, boolean bAutoWrap )
    {

        poi_cs.setFont( font );
        poi_cs.setBorderBottom( borderBottom );
        poi_cs.setBottomBorderColor( bottomBorderColor );
        poi_cs.setBorderTop( borderTop );
        poi_cs.setTopBorderColor( topBorderColor );
        poi_cs.setBorderLeft( borderLeft );
        poi_cs.setLeftBorderColor( leftBorderColor );
        poi_cs.setBorderRight( borderRight );
        poi_cs.setRightBorderColor( rightBorderColor );
        poi_cs.setAlignment( alignment );
        poi_cs.setWrapText( bAutoWrap );

    }

    public void initCellStyle( CellStyle poi_cs, Font font, short fillBgColor, short fillFgColor, short fillPattern,
        short borderBottom, short bottomBorderColor, short borderTop, short topBorderColor, short borderLeft,
        short leftBorderColor, short borderRight, short rightBorderColor, short dataFormat, short alignment,
        boolean bAutoWrap )
    {

        poi_cs.setFont( font );
        poi_cs.setFillBackgroundColor( fillBgColor );
        poi_cs.setFillForegroundColor( fillFgColor );
        poi_cs.setFillPattern( fillPattern );
        poi_cs.setBorderBottom( borderBottom );
        poi_cs.setBottomBorderColor( bottomBorderColor );
        poi_cs.setBorderTop( borderTop );
        poi_cs.setTopBorderColor( topBorderColor );
        poi_cs.setBorderLeft( borderLeft );
        poi_cs.setLeftBorderColor( leftBorderColor );
        poi_cs.setBorderRight( borderRight );
        poi_cs.setRightBorderColor( rightBorderColor );
        poi_cs.setDataFormat( dataFormat );
        poi_cs.setAlignment( alignment );
        poi_cs.setWrapText( bAutoWrap );
    }

}
