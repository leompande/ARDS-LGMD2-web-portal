package org.hisp.dhis.reportsheet.exporting.action;

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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementFormNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrder;
import org.hisp.dhis.reportsheet.CategoryOptionGroupOrder;
import org.hisp.dhis.reportsheet.DataElementGroupOrder;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportAttribute;
import org.hisp.dhis.reportsheet.ExportReportCategory;
import org.hisp.dhis.reportsheet.ExportReportOrganizationGroupListing;
import org.hisp.dhis.reportsheet.ExportReportPeriodColumnListing;
import org.hisp.dhis.reportsheet.ExportReportVerticalCategory;
import org.hisp.dhis.reportsheet.PeriodColumn;
import org.hisp.dhis.reportsheet.exporting.AbstractGenerateMultiExcelReportSupport;
import org.hisp.dhis.reportsheet.utils.ExcelUtils;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class GenerateMultiReportAction
    extends AbstractGenerateMultiExcelReportSupport
{
    private static final String PREFIX_FORMULA_SUM = "SUM(";

    @Override
    protected void executeGenerateOutputFile( List<ExportReport> reports )
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        DataElementCategoryOptionCombo defaultOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        Collection<ExportItem> exportItems = null;

        this.installReadTemplateFile( reports.get( 0 ), organisationUnit );

        for ( ExportReport report : reports )
        {
            for ( Integer sheetNo : exportReportService.getSheets( report.getId() ) )
            {
                Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

                exportItems = report.getExportItemBySheet( sheetNo );

                if ( report.getReportType().equals( ExportReport.TYPE.NORMAL ) )
                {
                    this.generateNormal( exportItems, organisationUnit, sheet );
                }
                else if ( report.getReportType().equals( ExportReport.TYPE.ATTRIBUTE ) )
                {
                    this.generateAttribute( defaultOptionCombo, (ExportReportAttribute) report, exportItems,
                        organisationUnit, sheet );
                }
                else if ( report.getReportType().equals( ExportReport.TYPE.CATEGORY ) )
                {
                    if ( isVerticalCategory( exportItems ) )
                    {
                        this.generateVerticalOutPutFile( (ExportReportCategory) report, exportItems, organisationUnit,
                            sheet );
                    }
                    else
                    {
                        this.generateHorizontalOutPutFile( (ExportReportCategory) report, exportItems,
                            organisationUnit, sheet );
                    }
                }
                else if ( report.getReportType().equals( ExportReport.TYPE.CATEGORY_VERTICAL ) )
                {
                    this.generateCategoryVertical( (ExportReportVerticalCategory) report, exportItems,
                        organisationUnit, sheet );
                }
                else if ( report.getReportType().equals( ExportReport.TYPE.ORGANIZATION_GROUP_LISTING ) )
                {
                    ExportReportOrganizationGroupListing reportInstance = (ExportReportOrganizationGroupListing) report;

                    Map<OrganisationUnitGroup, OrganisationUnitLevel> orgUniGroupAtLevels = new HashMap<OrganisationUnitGroup, OrganisationUnitLevel>(
                        reportInstance.getOrganisationUnitLevels() );

                    this.generateOrgUnitListing( reportInstance, orgUniGroupAtLevels, exportItems, organisationUnit,
                        sheet );
                }
                else
                {
                    ExportReportPeriodColumnListing reportInstance = (ExportReportPeriodColumnListing) report;

                    this
                        .generatePeriodListing( reportInstance.getPeriodColumns(), exportItems, organisationUnit, sheet );
                }

                this.recalculatingFormula( sheet );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    /**
     * NORMAL
     */
    private void generateNormal( Collection<ExportItem> exportItems, OrganisationUnit organisationUnit, Sheet sheet )
    {
        for ( ExportItem reportItem : exportItems )
        {
            if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
            {
                double value = getDataValue( reportItem, organisationUnit );

                ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                    ExcelUtils.NUMBER, sheet, this.csNumber );
            }
            else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_VALUETYPE_TEXT ) )
            {
                String value = getTextValue( reportItem, organisationUnit );

                ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn(), value, ExcelUtils.TEXT, sheet,
                    this.csText );
            }
            else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.INDICATOR ) )
            {
                double value = getIndicatorValue( reportItem, organisationUnit );

                ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                    ExcelUtils.NUMBER, sheet, this.csNumber );
            }
            else
            // EXCEL FORMULA
            {
                ExcelUtils.writeFormulaByPOI( reportItem.getRow(), reportItem.getColumn(), reportItem.getExpression(),
                    sheet, this.csFormulaBold, evaluatorFormula );
            }
        }
    }

    /**
     * ATTRIBUTE
     */
    private void generateAttribute( DataElementCategoryOptionCombo optionCombo, ExportReportAttribute exportReport,
        Collection<ExportItem> exportItems, OrganisationUnit organisationUnit, Sheet sheet )
    {
        boolean flag = false;
        int rowBegin = 0;

        for ( AttributeValueGroupOrder avgOrder : exportReport.getAttributeValueOrders() )
        {
            int serial = 1;
            List<DataElement> dataElements = null;

            flag = true;

            for ( String avalue : avgOrder.getAttributeValues() )
            {
                dataElements = new ArrayList<DataElement>( localDataElementService.getDataElementsByAttribute( avgOrder
                    .getAttribute(), avalue ) );

                Collections.sort( dataElements, new DataElementFormNameComparator() );

                for ( ExportItem exportItem : exportItems )
                {
                    rowBegin = (rowBegin == 0 ? exportItem.getRow() : exportItem.getRow() + rowBegin - 1);

                    if ( flag )
                    {
                        if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                        {
                            ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), avgOrder.getName(),
                                ExcelUtils.TEXT, sheet, this.csText12NormalCenter );
                        }

                        rowBegin++;
                    }

                    if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), avalue, ExcelUtils.TEXT, sheet,
                            this.csText10Normal );
                    }
                    else if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), serial + "", ExcelUtils.NUMBER,
                            sheet, this.csTextSerial );
                    }
                    else
                    {
                        int id = Integer.parseInt( exportItem.getExpression().split( "@" )[0] );
                        String value = exportItem.getExpression().split( "@" )[1];

                        for ( DataElement de : dataElements )
                        {
                            if ( localDataElementService.getDataElementCount( de.getId(), id, value ) > 0 )
                            {
                                ExportItem newExportItem = new ExportItem();

                                newExportItem.setPeriodType( exportItem.getPeriodType() );
                                newExportItem.setExpression( de.getId() + SEPARATOR + optionCombo.getId() );

                                double result = this.getDataValue( newExportItem, organisationUnit );

                                ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), result + "",
                                    ExcelUtils.NUMBER, sheet, this.csNumber );

                                break;
                            }
                        }
                    }
                }

                flag = false;
                rowBegin++;
                serial++;
            }
        }
    }

    /**
     * CATEGORY
     */
    private void generateVerticalOutPutFile( ExportReportCategory exportReport, Collection<ExportItem> exportItems,
        OrganisationUnit organisationUnit, Sheet sheet )
    {
        for ( ExportItem reportItem : exportItems )
        {
            int run = 0;
            int rowBegin = reportItem.getRow();

            for ( DataElementGroupOrder dataElementGroup : exportReport.getDataElementOrders() )
            {
                int beginChapter = rowBegin;

                if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), dataElementGroup.getName(),
                        ExcelUtils.TEXT, sheet, this.csText12NormalCenter );
                }
                else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_CODE ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), dataElementGroup.getCode(),
                        ExcelUtils.TEXT, sheet, this.csText12NormalCenter );
                }

                run++;
                rowBegin++;
                int serial = 1;

                for ( DataElement dataElement : dataElementGroup.getDataElements() )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), dataElement.getName(),
                            ExcelUtils.TEXT, sheet, this.csText10Normal );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_CODE ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), dataElement.getCode(),
                            ExcelUtils.TEXT, sheet, this.csTextICDJustify );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( serial ),
                            ExcelUtils.NUMBER, sheet, this.csTextSerial );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.FORMULA_EXCEL ) )
                    {
                        ExcelUtils.writeFormulaByPOI( rowBegin, reportItem.getColumn(), ExcelUtils
                            .generateExcelFormula( reportItem.getExpression(), run, run ), sheet, csFormulaNormal,
                            evaluatorFormula );
                    }
                    else
                    {
                        ExportItem newReportItem = new ExportItem();

                        String expression = reportItem.getExpression();
                        expression = expression.replace( "*", String.valueOf( dataElement.getId() ) );

                        newReportItem.setPeriodType( reportItem.getPeriodType() );
                        newReportItem.setExpression( expression );

                        double value = this.getDataValue( newReportItem, organisationUnit );

                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.csNumber );

                    }

                    rowBegin++;
                    serial++;
                    run++;
                }

                if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
                {
                    String columnName = ExcelUtils.convertColumnNumberToName( reportItem.getColumn() );
                    String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";

                    ExcelUtils.writeFormulaByPOI( beginChapter, reportItem.getColumn(), formula, sheet,
                        this.csFormulaBold, evaluatorFormula );
                }
            }
        }
    }

    private void generateHorizontalOutPutFile( ExportReportCategory exportReport, Collection<ExportItem> exportItems,
        OrganisationUnit organisationUnit, Sheet sheet )
    {
        for ( ExportItem reportItem : exportItems )
        {
            int colBegin = reportItem.getColumn();

            for ( DataElementGroupOrder dataElementGroup : exportReport.getDataElementOrders() )
            {
                for ( DataElement dataElement : dataElementGroup.getDataElements() )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
                    {
                        ExportItem newReportItem = new ExportItem();
                        newReportItem.setColumn( reportItem.getColumn() );
                        newReportItem.setRow( reportItem.getRow() );
                        newReportItem.setPeriodType( reportItem.getPeriodType() );
                        newReportItem.setName( reportItem.getName() );
                        newReportItem.setSheetNo( reportItem.getSheetNo() );
                        newReportItem.setItemType( reportItem.getItemType() );

                        String expression = reportItem.getExpression();
                        expression = expression.replace( "*", String.valueOf( dataElement.getId() ) );
                        newReportItem.setExpression( expression );

                        double value = this.getDataValue( newReportItem, organisationUnit );

                        ExcelUtils.writeValueByPOI( reportItem.getRow(), colBegin++, String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.csNumber );
                    }
                }
            }
        }
    }

    private boolean isVerticalCategory( Collection<ExportItem> items )
    {
        Integer previousRow = null;

        for ( ExportItem item : items )
        {
            if ( previousRow != null && previousRow != item.getRow() )
            {
                return false;
            }

            previousRow = item.getRow();
        }

        return true;
    }

    /**
     * CATEGORY-VERTICAL
     */
    private void generateCategoryVertical( ExportReportVerticalCategory exportReport,
        Collection<ExportItem> exportReportItems, OrganisationUnit organisationUnit, Sheet sheet )
    {
        DataElement de = null;
        Set<DataElementCategoryOptionCombo> optionCombos = new HashSet<DataElementCategoryOptionCombo>();
        Set<DataElementCategoryOption> associatedCategoryOptions = new HashSet<DataElementCategoryOption>(
            categoryOptionAssociationService.getCategoryOptions( organisationUnit ) );

        for ( ExportItem reportItem : exportReportItems )
        {
            int run = 0;
            int rowBegin = reportItem.getRow();

            if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
            {
                de = dataElementService.getDataElement( Integer.parseInt( reportItem.getExpression().split(
                    "\\" + SEPARATOR )[0].replace( "[", "" ) ) );

                optionCombos = de.getCategoryCombo().getOptionCombos();
            }

            for ( CategoryOptionGroupOrder group : exportReport.getCategoryOptionGroupOrders() )
            {
                int beginChapter = rowBegin;

                if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), group.getName(), ExcelUtils.TEXT,
                        sheet, this.csText12NormalCenter );
                }

                run++;
                rowBegin++;
                int serial = 1;

                for ( DataElementCategoryOption categoryOption : group.getCategoryOptions() )
                {
                    if ( associatedCategoryOptions.contains( categoryOption ) )
                    {
                        if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                        {
                            ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), categoryOption.getName(),
                                ExcelUtils.TEXT, sheet, this.csText10Normal );
                        }
                        else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL ) )
                        {
                            ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( serial ),
                                ExcelUtils.NUMBER, sheet, this.csTextSerial );
                        }
                        else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.FORMULA_EXCEL ) )
                        {
                            ExcelUtils.writeFormulaByPOI( rowBegin, reportItem.getColumn(), ExcelUtils
                                .generateExcelFormula( reportItem.getExpression(), run, run ), sheet, csFormulaNormal,
                                evaluatorFormula );
                        }
                        else
                        {
                            for ( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                if ( optionCombo.getCategoryOptions().contains( categoryOption ) )
                                {
                                    ExportItem newReportItem = new ExportItem();

                                    String expression = reportItem.getExpression();
                                    expression = expression.replace( "*", String.valueOf( optionCombo.getId() ) );

                                    newReportItem.setPeriodType( reportItem.getPeriodType() );
                                    newReportItem.setExpression( expression );

                                    double value = this.getDataValue( newReportItem, organisationUnit );

                                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String
                                        .valueOf( value ), ExcelUtils.NUMBER, sheet, this.csNumber );

                                    break;
                                }
                            }
                        }

                        rowBegin++;
                        serial++;
                        run++;

                        if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
                        {
                            String columnName = ExcelUtils.convertColumnNumberToName( reportItem.getColumn() );
                            String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName
                                + (rowBegin - 1) + ")";

                            ExcelUtils.writeFormulaByPOI( beginChapter, reportItem.getColumn(), formula, sheet,
                                this.csFormulaBold, evaluatorFormula );
                        }
                    }
                }
            }
        }
    }

    /**
     * ORGUNIT-LISTING
     */
    private void generateOrgUnitListing( ExportReportOrganizationGroupListing exportReport,
        Map<OrganisationUnitGroup, OrganisationUnitLevel> orgUniGroupAtLevels, Collection<ExportItem> exportItems,
        OrganisationUnit organisationUnit, Sheet sheet )
    {
        List<OrganisationUnit> childrens = new ArrayList<OrganisationUnit>( organisationUnit.getChildren() );

        for ( ExportItem reportItem : exportItems )
        {
            int run = 0;
            int next = 0;
            int chapperNo = 0;
            int firstRow = reportItem.getRow();
            int rowBegin = firstRow + 1;

            String totalFormula = PREFIX_FORMULA_SUM;

            for ( OrganisationUnitGroup organisationUnitGroup : exportReport.getOrganisationUnitGroups() )
            {
                List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitGroup
                    .getMembers() );

                OrganisationUnitLevel organisationUnitLevel = orgUniGroupAtLevels.get( organisationUnitGroup );

                if ( organisationUnitLevel != null )
                {
                    List<OrganisationUnit> organisationUnitsAtLevel = new ArrayList<OrganisationUnit>(
                        organisationUnitService.getOrganisationUnitsAtLevel( organisationUnitLevel.getLevel(),
                            organisationUnit ) );

                    organisationUnits.retainAll( organisationUnitsAtLevel );
                }
                else
                {
                    organisationUnits.retainAll( childrens );
                }

                Collections.sort( organisationUnits, new IdentifiableObjectNameComparator() );

                int beginChapter = rowBegin;

                if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.ORGANISATION )
                    && (!organisationUnits.isEmpty()) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), organisationUnitGroup.getName(),
                        ExcelUtils.TEXT, sheet, this.csText12NormalCenter );
                }
                else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL )
                    && (!organisationUnits.isEmpty()) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), chappter[chapperNo++],
                        ExcelUtils.TEXT, sheet, this.csText12NormalCenter );
                }

                run++;
                rowBegin++;
                int serial = 1;

                for ( OrganisationUnit o : organisationUnits )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.ORGANISATION ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), o.getName(), ExcelUtils.TEXT,
                            sheet, this.csText10Normal );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), serial + "", ExcelUtils.NUMBER,
                            sheet, this.csTextSerial );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
                    {
                        double value = this.getDataValue( reportItem, o );

                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), value + "", ExcelUtils.NUMBER,
                            sheet, this.csNumber );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.INDICATOR ) )
                    {
                        double value = this.getIndicatorValue( reportItem, o );

                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), value + "", ExcelUtils.NUMBER,
                            sheet, this.csNumber );
                    }
                    else
                    // FORMULA_EXCEL
                    {
                        ExcelUtils.writeFormulaByPOI( rowBegin, reportItem.getColumn(), ExcelUtils
                            .generateExcelFormula( reportItem.getExpression(), run, run ), sheet, this.csFormulaNormal,
                            evaluatorFormula );
                    }

                    run++;
                    rowBegin++;
                    serial++;
                }

                if ( !organisationUnits.isEmpty() )
                {
                    String formula = "";

                    if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
                    {
                        String columnName = ExcelUtils.convertColumnNumberToName( reportItem.getColumn() );
                        formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";

                        ExcelUtils.writeFormulaByPOI( beginChapter, reportItem.getColumn(), formula, sheet,
                            this.csFormulaBold, evaluatorFormula );

                        totalFormula += columnName + beginChapter + ",";
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.INDICATOR ) )
                    {
                        formula = ExcelUtils.generateExcelFormula( reportItem.getExtraExpression(), next + 1, 0 );

                        ExcelUtils.writeFormulaByPOI( beginChapter, reportItem.getColumn(), formula, sheet,
                            this.csFormulaBold, evaluatorFormula );
                    }
                }

                next = run;
            }

            if ( (reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ))
                && !totalFormula.equals( PREFIX_FORMULA_SUM ) )
            {
                totalFormula = totalFormula.substring( 0, totalFormula.length() - 1 ) + ")";

                ExcelUtils.writeFormulaByPOI( firstRow, reportItem.getColumn(), totalFormula, sheet,
                    this.csFormulaBold, evaluatorFormula );
            }
            else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.INDICATOR ) )
            {
                totalFormula = ExcelUtils.generateExcelFormula( reportItem.getExtraExpression(), 0, 0 );

                ExcelUtils.writeFormulaByPOI( firstRow, reportItem.getColumn(), totalFormula, sheet,
                    this.csFormulaBold, evaluatorFormula );
            }
        }
    }

    /**
     * PERIOD-LISTING
     */
    private void generatePeriodListing( Set<PeriodColumn> periodColumns, Collection<ExportItem> exportItems,
        OrganisationUnit organisationUnit, Sheet sheet )
    {
        for ( ExportItem reportItem : exportItems )
        {
            for ( PeriodColumn p : periodColumns )
            {
                if ( p.getPeriodType().equals( reportItem.getPeriodType() ) )
                {
                    double value = 0.0;

                    if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
                    {
                        value = this.getDataValue( reportItem, organisationUnit );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_VALUETYPE_TEXT ) )
                    {
                        String result = this.getTextValue( reportItem, organisationUnit );

                        ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn(), result,
                            ExcelUtils.TEXT, sheet, this.csText );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.INDICATOR ) )
                    {
                        value = this.getIndicatorValue( reportItem, organisationUnit );
                    }

                    ExcelUtils.writeValueByPOI( reportItem.getRow(), p.getColumn(), String.valueOf( value ),
                        ExcelUtils.NUMBER, sheet, this.csNumber );
                }
            }
        }
    }
}
