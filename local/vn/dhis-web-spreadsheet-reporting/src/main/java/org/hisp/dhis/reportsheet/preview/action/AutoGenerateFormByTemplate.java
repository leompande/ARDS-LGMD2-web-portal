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

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.getName;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.DOMAIN_TYPE_AGGREGATE;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;
import static org.hisp.dhis.reportsheet.ExportItem.PERIODTYPE.SELECTED_MONTH;
import static org.hisp.dhis.reportsheet.ExportItem.TYPE.DATAELEMENT;
import static org.hisp.dhis.reportsheet.ExportItem.TYPE.INDICATOR;
import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.FOREGROUND_COLOR;
import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.TEXT_COLOR;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.PATTERN_EXCELFORMULA;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertAlignmentString;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertColumnNameToNumber;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertColumnNumberToName;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.readValueByPOI;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportNormal;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.importitem.ImportItem;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @author Chau Thu Tran
 * @version $Id$
 */

public class AutoGenerateFormByTemplate
    extends ActionSupport
{
    private static final String REPORT_EXCEL_GROUP = "BÁO CÁO KIỂM TRA BỆNH VIỆN";

    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    private static final String DESCRIPTION = "Description";

    private static final String DATAELEMENT_KEY = "de";

    private static final String INDICATOR_KEY = "id";

    private static final String VALIDATIONRULE_KEY = "vr";

    private static final String INDICATOR_NAME = " - CS ";

    private static final String INDICATOR_TYPE_NAME = "Loại số";

    private static final String VALIDATION_RULE_NAME = " - VR ";

    private static final Pattern pattern = Pattern.compile( PATTERN_EXCELFORMULA );

    private static final PeriodType periodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );

    private static DataElementCategoryOptionCombo optionCombo = null;

    private static IndicatorType indicatorType = null;

    private String excelFileName = "";

    private String commonName = "";

    private Set<Integer> vrList = new HashSet<Integer>();

    private Map<String, Integer> deMap1 = new HashMap<String, Integer>();

    private Map<String, String> deMap2 = new HashMap<String, String>();

    private Map<String, Integer> idMap = new HashMap<String, Integer>();

    private static final Map<String, String> operatorMap = new HashMap<String, String>()
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            put( "==", "equal_to" );
            put( "=", "equal_to" );
            put( "!=", "not_equal_to" );
            put( "<>", "not_equal_to" );
            put( ">", "greater_than" );
            put( ">=", "greater_than_or_equal_to" );
            put( "=>", "greater_than_or_equal_to" );
            put( "<", "less_than" );
            put( "<=", "less_than_or_equal_to" );
            put( "=<", "less_than_or_equal_to" );
            put( "cp", "compulsory_pair" );
        }
    };

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

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataEntryFormService dataEntryFormService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ExportReportService exportReportService;

    @Autowired
    protected SelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private String xmlStructureResponse;

    public String getXmlStructureResponse()
    {
        return xmlStructureResponse;
    }

    private int exportReportId;

    public int getExportReportId()
    {
        return exportReportId;
    }

    private int dataSetId;

    public int getDataSetId()
    {
        return dataSetId;
    }

    public Collection<Integer> getDataElementIds()
    {
        return deMap1.values();
    }

    public Collection<Integer> getIndicatorIds()
    {
        return idMap.values();
    }

    public Set<Integer> getValidationRuleIds()
    {
        return new HashSet<Integer>( vrList );
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Set<Integer> collectSheets = new HashSet<Integer>();
        collectSheets.add( 1 );

        try
        {
            String pathFileName = selectionManager.getUploadFilePath();

            excelFileName = getName( pathFileName );
            commonName = getBaseName( pathFileName );

            if ( dataSetService.getDataSetByName( commonName ) != null )
            {
                message = i18n.getString( "form_name_already_exists" );

                return INPUT;
            }

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

            for ( IndicatorType type : indicatorService.getAllIndicatorTypes() )
            {
                if ( type.getFactor() == 1 )
                {
                    indicatorType = type;
                    break;
                }
            }

            if ( indicatorType == null )
            {
                indicatorType = new IndicatorType( INDICATOR_TYPE_NAME, 1, true );

                indicatorService.addIndicatorType( indicatorType );
            }

            optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
            evaluatorFormula = WORKBOOK.getCreationHelper().createFormulaEvaluator();

            printXML( collectSheets, null );

            xmlStructureResponse = xml.toString();

            xml = null;
        }
        catch ( Exception e )
        {
            xml = null;

            e.printStackTrace();

            return ERROR;
        }

        return SUCCESS;
    }

    private void printXML( Collection<Integer> collectSheets, List<ImportItem> importItems )
    {
        printMergedInfo( collectSheets );

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            printData( sheet, importItems );
        }

        xml.append( WORKBOOK_CLOSETAG );
    }

    private void printData( int sheetNo, List<ImportItem> importItems )
    {
        // Create new DataSet
        DataSet dataSet = new DataSet( commonName, commonName, periodType );

        // Create new ExportReport
        ExportReport exportReport = new ExportReportNormal( commonName, REPORT_EXCEL_GROUP, excelFileName, null );
        exportReportId = exportReportService.addExportReport( exportReport );

        Sheet s = WORKBOOK.getSheetAt( sheetNo - 1 );

        xml.append( "<sheet id='" + (sheetNo) + "'>" );
        xml.append( "<name><![CDATA[" + s.getSheetName() + "]]></name>" );

        for ( Row row : s )
        {
            xml.append( "<row index='" + row.getRowNum() + "'>" );

            for ( Cell cell : row )
            {
                Comment cmt = cell.getCellComment();
                int rowIndex = cell.getRowIndex();
                int colIndex = cell.getColumnIndex();

                if ( cmt != null )
                {
                    String values[] = cmt.getString().toString().split( ":" );

                    if ( values[0].equalsIgnoreCase( DATAELEMENT_KEY ) )
                    {
                        String deName = commonName + " - " + cell.getStringCellValue();
                        String[] colNames = values[1].split( "," );

                        for ( String colName : colNames )
                        {
                            int colIdx = convertColumnNameToNumber( colName );
                            String name = deName + " (" + colIdx + ")";
                            message = DATAELEMENT_KEY + "@" + name;

                            // Generate DataElement
                            DataElement dataElement = new DataElement( name );
                            /** TAKE CARE OF SHORT_NAME IS TOO LONG */
                            dataElement.setShortName( name );
                            dataElement.setActive( true );
                            dataElement.setZeroIsSignificant( false );
                            dataElement.setDomainType( DOMAIN_TYPE_AGGREGATE );
                            dataElement.setType( VALUE_TYPE_INT );
                            dataElement.setNumberType( VALUE_TYPE_INT );
                            dataElement.setAggregationOperator( AGGREGATION_OPERATOR_SUM );
                            dataElement.setCategoryCombo( optionCombo.getCategoryCombo() );

                            int deId = dataElementService.addDataElement( dataElement );

                            deMap1.put( (colIdx - 1) + "#" + rowIndex, deId );
                            deMap2.put( colName + (rowIndex + 1), "[" + deId + "." + optionCombo.getId() + "]" );

                            // Add the dataElement into the dataSet
                            dataSet.addDataElement( dataElement );

                            // Generate Report Item
                            ExportItem exportItem = new ExportItem();
                            exportItem.setName( name );
                            exportItem.setItemType( DATAELEMENT );
                            exportItem.setRow( rowIndex + 1 );
                            exportItem.setColumn( colIdx );
                            exportItem.setExpression( "[" + deId + "." + optionCombo.getId() + "]" );
                            exportItem.setPeriodType( SELECTED_MONTH );
                            exportItem.setSheetNo( sheetNo );
                            exportItem.setExportReport( exportReport );

                            exportReportService.addExportItem( exportItem );
                        }
                    }
                    else if ( values[0].equalsIgnoreCase( INDICATOR_KEY ) )
                    {
                        Integer colIdx = colIndex + 1;
                        String idName = commonName + INDICATOR_NAME;

                        switch ( values.length )
                        {
                        case 3:
                            idName += "(" + values[2];
                            colIdx = convertColumnNameToNumber( values[2] );
                            break;
                        case 4:
                            idName += "(" + values[2];
                            colIdx = convertColumnNameToNumber( values[2] );
                            rowIndex = Integer.parseInt( values[3] ) - 1;
                            break;
                        default:
                            idName += "(" + convertColumnNumberToName( colIdx + 1 );
                            break;
                        }

                        idName += rowIndex + ")";
                        message = INDICATOR_KEY + "@" + idName;

                        // Create Indicator
                        Indicator indicator = new Indicator();
                        indicator.setName( idName );
                        indicator.setShortName( idName );
                        indicator.setAnnualized( false );
                        indicator.setIndicatorType( indicatorType );
                        indicator.setNumerator( prepareExcelFormulaForAutoForm( values[1] ) );
                        indicator.setNumeratorDescription( DESCRIPTION );
                        indicator.setDenominator( 1 + "" );
                        indicator.setDenominatorDescription( DESCRIPTION );

                        int indicatorId = indicatorService.addIndicator( indicator );

                        idMap.put( (colIdx - 1) + "#" + rowIndex, indicatorId );

                        // Add the dataElement into the dataSet
                        dataSet.addIndicator( indicator );

                        // Generate Report Item
                        ExportItem exportItem = new ExportItem();
                        exportItem.setName( idName );
                        exportItem.setItemType( INDICATOR );
                        exportItem.setRow( rowIndex + 1 );
                        exportItem.setColumn( colIdx );
                        exportItem.setExpression( "[" + indicatorId + "]" );
                        exportItem.setPeriodType( SELECTED_MONTH );
                        exportItem.setSheetNo( sheetNo );
                        exportItem.setExportReport( exportReport );

                        exportReportService.addExportItem( exportItem );
                    }
                    else
                    {
                        String name = commonName + VALIDATION_RULE_NAME + "("
                            + convertColumnNumberToName( colIndex + 1 ) + (rowIndex + 1) + ")";
                        message = VALIDATIONRULE_KEY + "@" + name;

                        // Validation rules
                        Expression leftSide = new Expression();

                        leftSide.setExpression( prepareExcelFormulaForAutoForm( values[1] ) );
                        leftSide.setDescription( DESCRIPTION );
                        leftSide.setNullIfBlank( true );

                        Expression rightSide = new Expression();

                        rightSide.setExpression( prepareExcelFormulaForAutoForm( values[3] ) );
                        rightSide.setDescription( DESCRIPTION );
                        rightSide.setNullIfBlank( true );

                        ValidationRule validationRule = new ValidationRule();

                        validationRule.setName( name );
                        validationRule.setDescription( DESCRIPTION );
                        validationRule.setType( ValidationRule.TYPE_ABSOLUTE );
                        validationRule.setOperator( Operator.valueOf( operatorMap.get( values[2] ) ) );
                        validationRule.setLeftSide( leftSide );
                        validationRule.setRightSide( rightSide );

                        validationRule.setPeriodType( periodType );

                        vrList.add( validationRuleService.saveValidationRule( validationRule ) );
                    }
                }

                String key = colIndex + "#" + rowIndex;

                if ( deMap1.containsKey( key ) )
                {
                    xml.append( "<col no='" + colIndex + "'>" );

                    xml.append( "<data><![CDATA[<input id=\"" + deMap1.get( key ) + "-" + optionCombo.getId()
                        + "-val\" style=\"width:7em;text-align:center\" value=\"\" title=\"\" />]]></data>" );

                    xml.append( printFormatInfo( s, cell ) );

                    xml.append( "</col>" );
                }
                else if ( idMap.containsKey( key ) )
                {
                    int indicatorId = idMap.get( key );

                    xml.append( "<col no='" + colIndex + "'>" );

                    xml.append( "<data><![CDATA[<input id=\"indicator" + indicatorId + "\"" );
                    xml.append( " indicatorid=\"" + indicatorId + "\" name=\"indicator\" readonly=\"readonly\"" );
                    xml.append( " style=\"width:7em;text-align:center;\" title=\"\" value=\"\" />]]></data>" );

                    xml.append( printFormatInfo( s, cell ) );

                    xml.append( "</col>" );
                }
                else if ( (cell.getCellStyle() != null || cell.getCellType() != Cell.CELL_TYPE_BLANK)
                    && !s.isColumnHidden( colIndex ) )
                {
                    xml.append( "<col no='" + colIndex + "'>" );

                    xml.append( "<data><![CDATA["
                        + readValueByPOI( row.getRowNum() + 1, colIndex + 1, s, evaluatorFormula ) + "]]></data>" );

                    xml.append( printFormatInfo( s, cell ) );

                    xml.append( "</col>" );
                }
            }
            xml.append( "</row>" );
        }
        xml.append( "</sheet>" );

        // Update DataSet
        DataEntryForm dataEntryForm = new DataEntryForm( commonName, "<p></p>" );
        dataEntryFormService.addDataEntryForm( dataEntryForm );

        dataSet.setDataEntryForm( dataEntryForm );
        dataSetId = dataSetService.addDataSet( dataSet );

        // Update ExportReport
        Set<DataSet> dataSets = new HashSet<DataSet>();
        dataSets.add( dataSet );

        exportReport.setDataSets( dataSets );
        exportReportService.updateExportReport( exportReport );

        updateIndicator();
        updateValidationRule();

        xml.append( "<ds id='" + dataSetId + "' n='" + commonName + "'/>" );
    }

    private String printFormatInfo( Sheet sheet, Cell objCell )
    {
        // The format information
        CellStyle format = objCell.getCellStyle();
        StringBuffer sb = new StringBuffer();

        if ( format != null )
        {
            sb.append( "<format a='" + convertAlignmentString( format.getAlignment() ) + "'" );
            sb.append( " w='" + sheet.getColumnWidth( objCell.getColumnIndex() ) + "'" );
            sb.append( " b='"
                + (format.getBorderBottom() + format.getBorderLeft() + format.getBorderRight() + format.getBorderTop())
                + "'" );

            Font font = WORKBOOK.getFontAt( format.getFontIndex() );

            if ( font != null )
            {
                sb.append( "><font s='" + font.getFontHeightInPoints() + "'" );
                sb.append( " b='" + (font.getBoldweight() == Font.BOLDWEIGHT_BOLD ? "1" : "0") + "'" );
                sb.append( " i='" + font.getItalic() + "'" );
                sb.append( " c='" + htmlHelper.colorStyle( TEXT_COLOR, format ) + "'/>" );

                // The cell background information
                sb.append( "<bg c='" + htmlHelper.colorStyle( FOREGROUND_COLOR, format ) + "'/>" );
                sb.append( "</format>" );
            }
            else
            {
                sb.append( "/>" );
            }
        }
        else
        {
            return "";
        }

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Get the merged cell's information
    // -------------------------------------------------------------------------

    private void printMergedInfo( Collection<Integer> collectSheets )
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

    private String prepareExcelFormulaForAutoForm( String formula )
    {
        Matcher matcher = pattern.matcher( formula );

        StringBuffer buffer = new StringBuffer();

        while ( matcher.find() )
        {
            String s = matcher.group().replaceAll( "\\s", "" );

            if ( !s.endsWith( "!" ) )
            {
                s = "[" + s + "]";
            }

            matcher.appendReplacement( buffer, s );
        }

        matcher.appendTail( buffer );

        return buffer.toString();
    }

    private void updateIndicator()
    {
        for ( String key1 : idMap.keySet() )
        {
            Indicator indicator = indicatorService.getIndicator( idMap.get( key1 ) );
            String expression = indicator.getNumerator();

            for ( String key2 : deMap2.keySet() )
            {
                expression = expression.replaceAll( "\\[" + key2 + "\\]", deMap2.get( key2 ) );
            }

            indicator.setNumerator( expression );
            indicatorService.updateIndicator( indicator );
        }
    }

    private void updateValidationRule()
    {
        for ( Integer id : vrList )
        {
            ValidationRule vr = validationRuleService.getValidationRule( id );

            String leftExpression = vr.getLeftSide().getExpression();
            String rightExpression = vr.getRightSide().getExpression();

            for ( String key2 : deMap2.keySet() )
            {
                String operandId = deMap2.get( key2 );

                leftExpression = leftExpression.replaceAll( "\\[" + key2 + "\\]", operandId );
                rightExpression = rightExpression.replaceAll( "\\[" + key2 + "\\]", operandId );
            }

            vr.getLeftSide().setExpression( leftExpression );
            vr.getLeftSide().setDataElementsInExpression(
                expressionService.getDataElementsInExpression( leftExpression ) );
            vr.getLeftSide().setOptionCombosInExpression(
                expressionService.getOptionCombosInExpression( leftExpression ) );

            vr.getRightSide().setExpression( rightExpression );
            vr.getRightSide().setDataElementsInExpression(
                expressionService.getDataElementsInExpression( rightExpression ) );
            vr.getRightSide().setOptionCombosInExpression(
                expressionService.getOptionCombosInExpression( rightExpression ) );

            validationRuleService.updateValidationRule( vr );
        }
    }
}