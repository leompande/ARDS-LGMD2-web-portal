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
package org.hisp.dhis.reportsheet.exporting.advance.action;

import java.util.Collection;
import java.util.Set;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.reportsheet.DataElementGroupOrder;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportCategory;
import org.hisp.dhis.reportsheet.exporting.AbstractGenerateExcelReportSupport;
import org.hisp.dhis.reportsheet.utils.ExcelUtils;

/**
 * @author Chau Thu Tran
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class GenerateAdvancedReportCategoryAction
    extends AbstractGenerateExcelReportSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer organisationGroupId;

    public void setOrganisationGroupId( Integer organisationGroupId )
    {
        this.organisationGroupId = organisationGroupId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    protected void executeGenerateOutputFile( ExportReport exportReport )
        throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService
            .getOrganisationUnitGroup( organisationGroupId );

        ExportReportCategory exportReportInstance = (ExportReportCategory) exportReport;

        this.installReadTemplateFile( exportReportInstance, organisationUnitGroup );

        for ( Integer sheetNo : exportReportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            Collection<ExportItem> exportReportItems = exportReportInstance.getExportItemBySheet( sheetNo );

            this
                .generateOutPutFile( organisationUnitGroup.getMembers(), exportReportItems, exportReportInstance, sheet );
        }

        for ( Integer sheetNo : exportReportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            this.recalculatingFormula( sheet );
        }
    }

    private void generateOutPutFile( Set<OrganisationUnit> organisationUnits, Collection<ExportItem> exportItems,
        ExportReportCategory exportReport, Sheet sheet )
        throws RowsExceededException, WriteException
    {
        for ( ExportItem exportItem : exportItems )
        {
            int iRow = 0;
            int iCol = 0;
            int rowBegin = exportItem.getRow();

            for ( DataElementGroupOrder dataElementGroup : exportReport.getDataElementOrders() )
            {
                int beginChapter = rowBegin;

                if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( dataElementGroup
                        .getName() ), ExcelUtils.TEXT, sheet, this.csText12NormalCenter );
                }
                else if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_CODE ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( dataElementGroup
                        .getCode() ), ExcelUtils.TEXT, sheet, this.csText12NormalCenter );
                }

                rowBegin++;
                int serial = 1;

                for ( DataElement dataElement : dataElementGroup.getDataElements() )
                {
                    if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_NAME ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( dataElement
                            .getName() ), ExcelUtils.TEXT, sheet, this.csText10Normal );
                    }
                    else if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT_CODE ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( dataElement
                            .getCode() ), ExcelUtils.TEXT, sheet, this.csTextICDJustify );
                    }
                    else if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( serial ),
                            ExcelUtils.NUMBER, sheet, this.csTextSerial );
                    }
                    else if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.FORMULA_EXCEL ) )
                    {
                        ExcelUtils.writeFormulaByPOI( rowBegin, exportItem.getColumn(), ExcelUtils
                            .generateExcelFormula( exportItem.getExpression(), iRow, iCol ), sheet, this.csFormulaNormal,
                            evaluatorFormula );
                    }
                    else
                    {
                        ExportItem newReportItem = new ExportItem();
                        newReportItem.setColumn( exportItem.getColumn() );
                        newReportItem.setRow( exportItem.getRow() );
                        newReportItem.setPeriodType( exportItem.getPeriodType() );
                        newReportItem.setName( exportItem.getName() );
                        newReportItem.setSheetNo( exportItem.getSheetNo() );
                        newReportItem.setItemType( exportItem.getItemType() );

                        String expression = exportItem.getExpression();
                        expression = expression.replace( "*", String.valueOf( dataElement.getId() ) );
                        newReportItem.setExpression( expression );

                        double value = 0.0;
                        for ( OrganisationUnit organisationUnit : organisationUnits )
                        {
                            value += this.getDataValue( newReportItem, organisationUnit );
                        }
                        ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.csNumber );
                    }
                    rowBegin++;
                    serial++;
                    iRow++;
                }

                if ( exportItem.getItemType().equalsIgnoreCase( ExportItem.TYPE.DATAELEMENT ) )
                {
                    String columnName = ExcelUtils.convertColumnNumberToName( exportItem.getColumn() );
                    String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";
                    ExcelUtils.writeFormulaByPOI( beginChapter, exportItem.getColumn(), formula, sheet, this.csFormulaBold,
                        evaluatorFormula );
                }
            }
        }
    }

}
