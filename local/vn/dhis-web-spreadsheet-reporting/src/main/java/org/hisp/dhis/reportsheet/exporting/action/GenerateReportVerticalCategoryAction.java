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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportsheet.CategoryOptionGroupOrder;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportVerticalCategory;
import org.hisp.dhis.reportsheet.OptionComboAssociationService;
import org.hisp.dhis.reportsheet.exporting.AbstractGenerateExcelReportSupport;
import org.hisp.dhis.reportsheet.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class GenerateReportVerticalCategoryAction
    extends AbstractGenerateExcelReportSupport
{
    @Autowired
    private OptionComboAssociationService optionComboAssociationService;

    @Override
    protected void executeGenerateOutputFile( ExportReport exportReport )
        throws Exception
    {
        OrganisationUnit unit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        ExportReportVerticalCategory exportReportInstance = (ExportReportVerticalCategory) exportReport;

        this.installReadTemplateFile( exportReportInstance, unit );

        Collection<ExportItem> exportReportItems = null;

        for ( Integer sheetNo : exportReportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            exportReportItems = exportReportInstance.getExportItemBySheet( sheetNo );

            this.generateVerticalOutPutFile( exportReportInstance, exportReportItems, unit, sheet );

            this.recalculatingFormula( sheet );
        }

        /**
         * Garbage
         */
        exportReportItems = null;
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private void generateVerticalOutPutFile( ExportReportVerticalCategory exportReport,
        Collection<ExportItem> exportReportItems, OrganisationUnit organisationUnit, Sheet sheet )
    {
        Set<DataElementCategoryOptionCombo> associatedOptionCombos = new HashSet<DataElementCategoryOptionCombo>(
            optionComboAssociationService.getOptionCombos( organisationUnit ) );

        for ( ExportItem reportItem : exportReportItems )
        {
            int run = 0;
            int chapperNo = 0;
            int rowBegin = reportItem.getRow();

            for ( CategoryOptionGroupOrder group : exportReport.getCategoryOptionGroupOrders() )
            {
                int beginChapter = rowBegin;

                if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), group.getName(), ExcelUtils.TEXT,
                        sheet, this.csText10Normal );
                }
                else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), chappter[chapperNo++],
                        ExcelUtils.TEXT, sheet, this.csText10Normal );
                }

                run++;
                rowBegin++;
                int serial = 1;

                for ( DataElementCategoryOption categoryOption : group.getCategoryOptions() )
                {
                    for ( DataElementCategoryOptionCombo optionCombo : associatedOptionCombos )
                    {
                        if ( categoryOption.getCategoryOptionCombos().contains( optionCombo ) )
                        {
                            if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                            {
                                ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), categoryOption.getName(),
                                    ExcelUtils.TEXT, sheet, this.csText8Normal );
                            }
                            else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL ) )
                            {
                                ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( serial ),
                                    ExcelUtils.NUMBER, sheet, this.csTextSerial );
                            }
                            else if ( reportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.FORMULA_EXCEL ) )
                            {
                                ExcelUtils.writeFormulaByPOI( rowBegin, reportItem.getColumn(), ExcelUtils
                                    .generateExcelFormula( reportItem.getExpression(), run, run ), sheet,
                                    csFormulaNormal, evaluatorFormula );
                            }
                            else
                            {
                                ExportItem newReportItem = new ExportItem();

                                String expression = reportItem.getExpression();
                                expression = expression.replace( "*", String.valueOf( optionCombo.getId() ) );

                                newReportItem.setPeriodType( reportItem.getPeriodType() );
                                newReportItem.setExpression( expression );

                                double value = this.getDataValue( newReportItem, organisationUnit );

                                ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( value ),
                                    ExcelUtils.NUMBER, sheet, this.csNumber );
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

                            break;
                        }
                    }
                }
            }
        }
    }
}
