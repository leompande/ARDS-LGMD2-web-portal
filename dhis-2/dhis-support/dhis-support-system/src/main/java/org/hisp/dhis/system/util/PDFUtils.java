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

import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_LEFT;
import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.validation.ValidationRule;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Lars Helge Overland
 * @author Dang Duy Hieu
 */
public class PDFUtils
{
    private static final String EMPTY = "";

    /**
     * Creates a document.
     * 
     * @param outputStream The output stream to write the document content.
     * @return A Document.
     */
    public static Document openDocument( OutputStream outputStream )
    {
        return openDocument( outputStream, PageSize.A4 );
    }

    /**
     * Creates a document.
     * 
     * @param outputStream The output stream to write the document content.
     * @param pageSize the page size.
     * @return A Document.
     */
    public static Document openDocument( OutputStream outputStream, Rectangle pageSize )
    {
        try
        {
            Document document = new Document( pageSize );

            PdfWriter.getInstance( document, outputStream );

            document.open();

            return document;
        }
        catch ( DocumentException ex )
        {
            throw new RuntimeException( "Failed to open PDF document", ex );
        }
    }

    /**
     * Starts a new page in the document.
     * 
     * @param document The document to start a new page in.
     */
    public static void startNewPage( Document document )
    {
        document.newPage();
    }

    /**
     * <p>
     * Creates a table. Specify the columns and widths by providing one<br>
     * float per column with a percentage value. For instance
     * </p>
     * 
     * <p>
     * getPdfPTable( 0.35f, 0.65f )
     * </p>
     * 
     * <p>
     * will give you a table with two columns where the first covers 35 %<br>
     * of the page while the second covers 65 %.
     * </p>
     * 
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     * @return
     */
    public static PdfPTable getPdfPTable( boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = new PdfPTable( columnWidths );

        table.setWidthPercentage( 100f );
        table.setKeepTogether( keepTogether );

        return table;
    }

    /**
     * Adds a table to a document.
     * 
     * @param document The document to add the table to.
     * @param table The table to add to the document.
     */
    public static void addTableToDocument( Document document, PdfPTable table )
    {
        try
        {
            document.add( table );
        }
        catch ( DocumentException ex )
        {
            throw new RuntimeException( "Failed to add table to document", ex );
        }
    }

    /**
     * Moves the cursor to the next page in the document.
     * 
     * @param document The document.
     */
    public static void moveToNewPage( Document document )
    {
        document.newPage();
    }

    /**
     * Closes the document if it is open.
     * 
     * @param document The document to close.
     */
    public static void closeDocument( Document document )
    {
        if ( document.isOpen() )
        {
            document.close();
        }
    }

    /**
     * Creates a cell.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The font of the cell text.
     * @param horizontalAlign The vertical alignment of the text in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getCell( String text, int colspan, Font font, int horizontalAlign )
    {
        Paragraph paragraph = new Paragraph( text, font );

        PdfPCell cell = new PdfPCell( paragraph );

        cell.setColspan( colspan );
        cell.setBorder( 0 );
        cell.setMinimumHeight( 15 );
        cell.setHorizontalAlignment( horizontalAlign );

        return cell;
    }

    public static PdfPCell getTitleCell( String text, int colspan )
    {
        return getCell( text, colspan, getBoldFont( 16 ), ALIGN_CENTER );
    }

    public static PdfPCell getSubtitleCell( String text, int colspan )
    {
        return getCell( text, colspan, getItalicFont( 12 ), ALIGN_CENTER );
    }

    public static PdfPCell getHeaderCell( String text, int colspan )
    {
        return getCell( text, colspan, getFont( 12 ), ALIGN_LEFT );
    }

    public static PdfPCell getTextCell( String text )
    {
        return getCell( text, 1, getFont( 9 ), ALIGN_LEFT );
    }

    public static PdfPCell getTextCell( Object object )
    {
        String text = object != null ? String.valueOf( object ) : EMPTY;

        return getCell( text, 1, getFont( 9 ), ALIGN_LEFT );
    }

    public static PdfPCell getItalicCell( String text )
    {
        return getCell( text, 1, getItalicFont( 9 ), ALIGN_LEFT );
    }

    public static PdfPCell resetPaddings( PdfPCell cell, float top, float bottom, float left, float right )
    {
        cell.setPaddingTop( top );
        cell.setPaddingBottom( bottom );
        cell.setPaddingLeft( left );
        cell.setPaddingRight( right );

        return cell;
    }

    /**
     * Creates an empty cell.
     * 
     * @param colspan The column span of the cell.
     * @param height The height of the column.
     * @return A PdfCell.
     */
    public static PdfPCell getEmptyCell( int colSpan, int height )
    {
        PdfPCell cell = new PdfPCell();

        cell.setColspan( colSpan );
        cell.setBorder( 0 );
        cell.setMinimumHeight( height );

        return cell;
    }

    // -------------------------------------------------------------------------
    // Font methods
    // -------------------------------------------------------------------------

    public static Font getFont( float size )
    {
        return getFont( "ubuntu.ttf", size );
    }

    public static Font getBoldFont( float size )
    {
        return getFont( "ubuntu-bold.ttf", size );
    }

    public static Font getItalicFont( float size )
    {
        return getFont( "ubuntu-italic.ttf", size );
    }

    private static Font getFont( String fontPath, float size )
    {
        try
        {
            BaseFont baseFont = BaseFont.createFont( fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED );
            return new Font( baseFont, size );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Error while creating base font", ex );
        }
    }

    // -------------------------------------------------------------------------
    // Domain object methods
    // -------------------------------------------------------------------------

    /**
     * Writes a "Data Elements" title in front of page
     * 
     * @param dataElementIds the identifier list of Data
     * @param i18n The i18n object
     * @param format The i18nFormat object
     * 
     */
    public static void printObjectFrontPage( Document document, Collection<?> objects, I18n i18n, I18nFormat format,
        String frontPageLabel )
    {
        if ( objects == null || objects.size() > 0 )
        {
            String title = i18n.getString( frontPageLabel );

            printFrontPage( document, title, i18n, format );
        }
    }

    /**
     * Writes a "Data dictionary" title in front of page
     * 
     * @param document The document
     * @param i18n The i18n object
     * @param format The i18nFormat object
     * 
     */
    public static void printDocumentFrontPage( Document document, I18n i18n, I18nFormat format )
    {
        String title = i18n.getString( "data_dictionary" );

        printFrontPage( document, title, i18n, format );
    }

    /**
     * Writes a DHIS 2 title in front of page
     * 
     * @param document The document
     * @param exportParams the exporting params
     * 
     */
    private static void printFrontPage( Document document, String title, I18n i18n, I18nFormat format )
    {
        PdfPTable table = getPdfPTable( true, 1.00f );

        table.addCell( getTitleCell( i18n.getString( "district_health_information_software" ), 1 ) );

        table.addCell( getEmptyCell( 1, 40 ) );

        table.addCell( getSubtitleCell( title, 1 ) );

        table.addCell( getEmptyCell( 1, 40 ) );

        String date = format.formatDate( Calendar.getInstance().getTime() );

        table.addCell( getSubtitleCell( date, 1 ) );

        addTableToDocument( document, table );

        moveToNewPage( document );
    }

    /**
     * Creates a table with the given data element
     * 
     * @param element The data element
     * @param i18n i18n object
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printDataElement( DataElement element, I18n i18n, boolean keepTogether,
        float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeaderCell( element.getName(), 2 ) );

        table.addCell( getEmptyCell( 2, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "short_name" ) ) );
        table.addCell( getTextCell( element.getShortName() ) );

        if ( nullIfEmpty( element.getCode() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "code" ) ) );
            table.addCell( getTextCell( element.getCode() ) );
        }
        if ( nullIfEmpty( element.getDescription() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "description" ) ) );
            table.addCell( getTextCell( element.getDescription() ) );
        }

        table.addCell( getItalicCell( i18n.getString( "active" ) ) );
        table.addCell( getTextCell( i18n.getString( getBoolean().get( element.isActive() ) ) ) );

        if ( nullIfEmpty( element.getType() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "value_type" ) ) );
            table.addCell( getTextCell( i18n.getString( getType().get( element.getType() ) ) ) );
        }
        if ( nullIfEmpty( element.getAggregationOperator() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "aggregation_operator" ) ) );
            table.addCell( getTextCell( i18n.getString( getAggregationOperator().get( element.getAggregationOperator() ) ) ) );
        }

        for ( AttributeValue value : element.getAttributeValues() )
        {
            table.addCell( getItalicCell( value.getAttribute().getName() ) );
            table.addCell( getTextCell( value.getValue() ) );
        }

        table.addCell( getEmptyCell( 2, 30 ) );

        return table;
    }

    /**
     * Creates a table with the given indicator
     * 
     * @param indicator The indicator
     * @param i18n i18n object
     * @param expressionService The expression service
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printIndicator( Indicator indicator, I18n i18n, ExpressionService expressionService,
        boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeaderCell( indicator.getName(), 2 ) );

        table.addCell( getEmptyCell( 2, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "short_name" ) ) );
        table.addCell( getTextCell( indicator.getShortName() ) );

        if ( nullIfEmpty( indicator.getCode() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "code" ) ) );
            table.addCell( getTextCell( indicator.getCode() ) );
        }
        if ( nullIfEmpty( indicator.getDescription() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "description" ) ) );
            table.addCell( getTextCell( indicator.getDescription() ) );
        }

        table.addCell( getItalicCell( i18n.getString( "annualized" ) ) );
        table.addCell( getTextCell( i18n.getString( getBoolean().get( indicator.isAnnualized() ) ) ) );

        table.addCell( getItalicCell( i18n.getString( "indicator_type" ) ) );
        table.addCell( getTextCell( indicator.getIndicatorType().getName() ) );

        table.addCell( getItalicCell( i18n.getString( "numerator_description" ) ) );
        table.addCell( getTextCell( indicator.getNumeratorDescription() ) );

        table.addCell( getItalicCell( i18n.getString( "numerator_formula" ) ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( indicator.getNumerator() ) ) );

        table.addCell( getItalicCell( i18n.getString( "denominator_description" ) ) );
        table.addCell( getTextCell( indicator.getDenominatorDescription() ) );

        table.addCell( getItalicCell( i18n.getString( "denominator_formula" ) ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( indicator.getDenominator() ) ) );

        for ( AttributeValue value : indicator.getAttributeValues() )
        {
            table.addCell( getItalicCell( value.getAttribute().getName() ) );
            table.addCell( getTextCell( value.getValue() ) );
        }

        table.addCell( getEmptyCell( 2, 30 ) );

        return table;
    }

    /**
     * Creates a table with the given unit
     * 
     * @param unit The organization unit
     * @param i18n i18n object
     * @param format
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printOrganisationUnit( OrganisationUnit unit, I18n i18n, I18nFormat format,
        boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeaderCell( unit.getName(), 2 ) );

        table.addCell( getEmptyCell( 2, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "short_name" ) ) );
        table.addCell( getTextCell( unit.getShortName() ) );

        if ( nullIfEmpty( unit.getCode() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "code" ) ) );
            table.addCell( getTextCell( unit.getCode() ) );
        }

        table.addCell( getItalicCell( i18n.getString( "opening_date" ) ) );
        table.addCell( getTextCell( unit.getOpeningDate() != null ? format.formatDate( unit.getOpeningDate() ) : EMPTY ) );

        if ( unit.getClosedDate() != null )
        {
            table.addCell( getItalicCell( i18n.getString( "closed_date" ) ) );
            table.addCell( getTextCell( format.formatDate( unit.getClosedDate() ) ) );
        }

        table.addCell( getItalicCell( i18n.getString( "active" ) ) );
        table.addCell( getTextCell( i18n.getString( getBoolean().get( unit.isActive() ) ) ) );

        if ( nullIfEmpty( unit.getComment() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "comment" ) ) );
            table.addCell( getTextCell( unit.getComment() ) );
        }

        for ( AttributeValue value : unit.getAttributeValues() )
        {
            table.addCell( getItalicCell( value.getAttribute().getName() ) );
            table.addCell( getTextCell( value.getValue() ) );
        }

        table.addCell( getEmptyCell( 2, 30 ) );

        return table;
    }

    /**
     * Creates a table with the given validation rule
     * 
     * @param user The User
     * @param i18n i18n object
     * @param format I18nFormat object
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printUser( UserCredentials userCredentials, I18n i18n, I18nFormat format,
        boolean keepTogether, float... columnWidths )
    {
        User user = userCredentials.getUser();

        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeaderCell( user.getFirstName() + ", " + user.getSurname(), 2 ) );

        table.addCell( getEmptyCell( 2, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "username" ) ) );
        table.addCell( getTextCell( userCredentials.getUsername() ) );

        if ( nullIfEmpty( user.getEmail() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "email" ) ) );
            table.addCell( getTextCell( user.getEmail() ) );
        }

        if ( nullIfEmpty( user.getPhoneNumber() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "phone_number" ) ) );
            table.addCell( getTextCell( user.getPhoneNumber() ) );
        }

        table.addCell( getItalicCell( i18n.getString( "last_login" ) ) );
        table.addCell( getTextCell( userCredentials.getLastLogin() != null ? format.formatDate( userCredentials.getLastLogin() ) : EMPTY ) );

        String temp = "";

        for ( OrganisationUnit unit : user.getOrganisationUnits() )
        {
            temp += unit.getName().concat( ", " );
        }

        temp = temp.trim();
        temp = temp.substring( 0, temp.isEmpty() ? 0 : temp.length() - 1 );

        table.addCell( getItalicCell( i18n.getString( "organisation_units" ) ) );
        table.addCell( getTextCell( temp ) );

        temp = "";

        for ( UserAuthorityGroup role : userCredentials.getUserAuthorityGroups() )
        {
            temp += role.getName().concat( ", " );
        }

        temp = temp.trim();
        temp = temp.substring( 0, temp.isEmpty() ? 0 : temp.length() - 1 );

        table.addCell( getItalicCell( i18n.getString( "roles" ) ) );
        table.addCell( getTextCell( temp ) );

        for ( AttributeValue value : user.getAttributeValues() )
        {
            table.addCell( getItalicCell( value.getAttribute().getName() ) );
            table.addCell( getTextCell( value.getValue() ) );
        }
        
        table.addCell( getEmptyCell( 2, 30 ) );

        return table;
    }

    /**
     * Creates a table with the given validation rule
     * 
     * @param validationRule The validation rule
     * @param i18n i18n object
     * @param expressionService The expression service
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printValidationRule( ValidationRule validationRule, I18n i18n,
        ExpressionService expressionService, boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeaderCell( validationRule.getName(), 2 ) );

        table.addCell( getEmptyCell( 2, 15 ) );

        if ( nullIfEmpty( validationRule.getDescription() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "description" ) ) );
            table.addCell( getTextCell( validationRule.getDescription() ) );
        }

        table.addCell( getItalicCell( i18n.getString( "type" ) ) );
        table.addCell( getTextCell( i18n.getString( validationRule.getType() ) ) );

        table.addCell( getItalicCell( i18n.getString( "operator" ) ) );
        table.addCell( getTextCell( i18n.getString( validationRule.getOperator().toString() ) ) );

        table.addCell( getItalicCell( i18n.getString( "left_side_of_expression" ) ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( validationRule.getLeftSide()
            .getExpression() ) ) );

        table.addCell( getItalicCell( i18n.getString( "left_side_description" ) ) );
        table.addCell( getTextCell( validationRule.getLeftSide().getDescription() ) );

        table.addCell( getItalicCell( i18n.getString( "right_side_of_expression" ) ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( validationRule.getRightSide()
            .getExpression() ) ) );

        table.addCell( getItalicCell( i18n.getString( "right_side_description" ) ) );
        table.addCell( getTextCell( validationRule.getRightSide().getDescription() ) );

        table.addCell( getItalicCell( i18n.getString( "period_type" ) ) );
        table.addCell( getTextCell( i18n.getString( validationRule.getPeriodType().getName() ) ) );

        table.addCell( getEmptyCell( 2, 30 ) );

        return table;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static Map<Boolean, String> getBoolean()
    {
        Map<Boolean, String> map = new HashMap<Boolean, String>();
        map.put( true, "yes" );
        map.put( false, "no" );
        return map;
    }

    private static Map<String, String> getType()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.VALUE_TYPE_STRING, "text" );
        map.put( DataElement.VALUE_TYPE_INT, "number" );
        map.put( DataElement.VALUE_TYPE_BOOL, "yes_no" );
        map.put( DataElement.VALUE_TYPE_DATE, "date" );
        return map;
    }

    private static Map<String, String> getAggregationOperator()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.AGGREGATION_OPERATOR_SUM, "sum" );
        map.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, "average" );
        map.put( DataElement.AGGREGATION_OPERATOR_COUNT, "count" );
        return map;
    }
}
