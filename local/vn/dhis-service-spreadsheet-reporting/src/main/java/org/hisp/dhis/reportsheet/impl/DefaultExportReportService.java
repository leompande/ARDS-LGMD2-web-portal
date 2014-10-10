package org.hisp.dhis.reportsheet.impl;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrder;
import org.hisp.dhis.reportsheet.CategoryOptionGroupOrder;
import org.hisp.dhis.reportsheet.DataElementGroupOrder;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportAttribute;
import org.hisp.dhis.reportsheet.ExportReportCategory;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportReportStore;
import org.hisp.dhis.reportsheet.ExportReportVerticalCategory;
import org.hisp.dhis.reportsheet.PeriodColumn;
import org.hisp.dhis.reportsheet.status.DataEntryStatus;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tran Thanh Tri
 * @version $Id: DefaultExportReportService.java 2010-03-11 11:52:20Z Chau Thu
 *          Tran $ $
 */
@Transactional
public class DefaultExportReportService
    implements ExportReportService
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private ExportReportStore exportReportStore;

    public void setExportReportStore( ExportReportStore exportReportStore )
    {
        this.exportReportStore = exportReportStore;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

    // -------------------------------------------------------------------------
    // Service of Report
    // -------------------------------------------------------------------------

    public int addExportReport( ExportReport report )
    {
        int id = exportReportStore.addExportReport( report );

        return id;
    }

    public void updateExportReport( ExportReport report )
    {
        exportReportStore.updateExportReport( report );
    }

    public void deleteExportReport( int id )
    {
        i18nService.removeObject( exportReportStore.getExportReport( id ) );

        exportReportStore.deleteExportReport( id );
    }

    public ExportReport getExportReport( int id )
    {
        return i18n( i18nService, exportReportStore.getExportReport( id ) );
    }

    public ExportReport getExportReport( String name )
    {
        return i18n( i18nService, exportReportStore.getExportReport( name ) );
    }

    public Collection<ExportReport> getExportReportsByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return i18n( i18nService, exportReportStore.getExportReportsByOrganisationUnit( organisationUnit ) );
    }

    public Collection<ExportReport> getAllExportReport()
    {
        return i18n( i18nService, exportReportStore.getAllExportReport() );
    }

    public Collection<ExportReport> getExportReports( User user, boolean superUser, String group )
    {
        if ( user == null || superUser )
        {
            return i18n( i18nService, this.getExportReportsByGroup( group ) );
        }

        else
        {
            Set<UserAuthorityGroup> userRoles = userService.getUserCredentials( user ).getUserAuthorityGroups();

            Collection<ExportReport> reports = new ArrayList<ExportReport>();

            for ( ExportReport report : this.getExportReportsByGroup( group ) )
            {
                if ( CollectionUtils.intersection( report.getUserRoles(), userRoles ).size() > 0 )
                {
                    reports.add( report );
                }
            }

            return i18n( i18nService, reports );
        }
    }

    public Collection<String> getExportReportGroups()
    {
        return exportReportStore.getExportReportGroups();
    }

    public Collection<ExportReport> getExportReportsByGroup( String group )
    {
        return i18n( i18nService, exportReportStore.getExportReportsByGroup( group ) );
    }

    public Collection<ExportReport> getExportReportsByClazz( Class<?> clazz )
    {
        return i18n( i18nService, exportReportStore.getExportReportsByClazz( clazz ) );
    }

    public Collection<ExportReport> getExportReportsByReportType( String reportType )
    {
        return i18n( i18nService, exportReportStore.getExportReportsByReportType( reportType ) );
    }

    public Collection<String> getAllExportReportTemplates()
    {
        return exportReportStore.getAllExportReportTemplates();
    }

    public void deleteMultiExportItem( Collection<Integer> ids )
    {
        if ( !ids.isEmpty() )
        {
            exportReportStore.deleteMultiExportItem( ids );
        }
    }

    public void updateExportReportSystemByTemplate( String curName, String newName )
    {
        exportReportStore.updateReportWithExcelTemplate( curName, newName );
    }

    // -------------------------------------------------------------------------
    // Service of Report Item
    // -------------------------------------------------------------------------

    public void addExportItem( ExportItem reportItem )
    {
        exportReportStore.addExportItem( reportItem );
    }

    public void updateExportItem( ExportItem reportItem )
    {
        exportReportStore.updateExportItem( reportItem );
    }

    public void deleteExportItem( int id )
    {
        exportReportStore.deleteExportItem( id );
    }

    public ExportItem getExportItem( int id )
    {
        return exportReportStore.getExportItem( id );
    }

    public Collection<ExportItem> getAllExportItem()
    {
        return exportReportStore.getAllExportItem();
    }

    public Collection<ExportItem> getExportItem( int sheetNo, Integer reportId )
    {
        return exportReportStore.getExportItem( sheetNo, reportId );
    }

    public Collection<Integer> getSheets( Integer reportId )
    {
        return exportReportStore.getSheets( reportId );
    }

    // -------------------------------------------------------------------------
    // Data Entry Status
    // -------------------------------------------------------------------------

    public int countDataValueOfDataSet( DataSet arg0, OrganisationUnit arg1, Period arg2 )
    {
        return exportReportStore.countDataValueOfDataSet( arg0, arg1, arg2 );
    }

    public void deleteDataEntryStatus( int arg0 )
    {
        exportReportStore.deleteDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getALLDataEntryStatus()
    {
        return exportReportStore.getALLDataEntryStatus();
    }

    public DataEntryStatus getDataEntryStatus( int arg0 )
    {
        return exportReportStore.getDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getDataEntryStatusDefault()
    {
        return exportReportStore.getDataEntryStatusDefault();
    }

    public int saveDataEntryStatus( DataEntryStatus arg0 )
    {
        return exportReportStore.saveDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getDataEntryStatusByDataSets( Collection<DataSet> arg0 )
    {
        return exportReportStore.getDataEntryStatusByDataSets( arg0 );
    }

    public Collection<DataEntryStatus> getDataEntryStatusDefaultByDataSets( Collection<DataSet> arg0 )
    {
        return exportReportStore.getDataEntryStatusDefaultByDataSets( arg0 );
    }

    public void updateDataEntryStatus( DataEntryStatus arg0 )
    {
        exportReportStore.updateDataEntryStatus( arg0 );
    }

    public PeriodColumn getPeriodColumn( Integer id )
    {
        return exportReportStore.getPeriodColumn( id );
    }

    public void updatePeriodColumn( PeriodColumn periodColumn )
    {
        exportReportStore.updatePeriodColumn( periodColumn );
    }

    public String validateEmportItems( ExportReport exportReport, I18n i18n )
    {
        Set<ExportItem> items = new HashSet<ExportItem>( exportReport.getExportItemsByItemType(
            ExportItem.TYPE.DATAELEMENT, ExportItem.TYPE.INDICATOR ) );

        if ( exportReport.getReportType().equalsIgnoreCase( ExportReport.TYPE.ATTRIBUTE ) )
        {
            for ( AttributeValueGroupOrder groupOrder : ((ExportReportAttribute) exportReport)
                .getAttributeValueOrders() )
            {
                if ( groupOrder.getAttribute() == null )
                {
                    return i18n.getString( "no_attribute_selected" );
                }

                if ( attributeService.getAttribute( groupOrder.getAttribute().getId() ) == null )
                {
                    return i18n.getString( "attribute_with_id" ) + ": " + groupOrder.getAttribute().getId()
                        + i18n.getString( "does_not_exist" );
                }

                if ( groupOrder.getAttributeValues() == null || groupOrder.getAttributeValues().isEmpty() )
                {
                    return i18n.getString( "group_order" ) + ": " + groupOrder.getName() + " "
                        + i18n.getString( "has_no_element" );
                }
            }
        }
        else if ( exportReport.getReportType().equalsIgnoreCase( ExportReport.TYPE.CATEGORY ) )
        {
            for ( DataElementGroupOrder groupOrder : ((ExportReportCategory) exportReport).getDataElementOrders() )
            {
                if ( groupOrder.getDataElements() == null || groupOrder.getDataElements().isEmpty() )
                {
                    return i18n.getString( "group_order" ) + ": " + groupOrder.getName() + " "
                        + i18n.getString( "has_no_element" );
                }
            }

            String optionComboId = null;
            List<String> optionComboIds = new ArrayList<String>();

            for ( ExportItem item : items )
            {
                optionComboId = item.getExpression().split( "\\" + DataElementOperand.SEPARATOR )[1].replace( "]", "" );

                if ( !optionComboIds.contains( optionComboId ) )
                {
                    optionComboIds.add( optionComboId );

                    DataElementCategoryOptionCombo optionCombo = categoryService
                        .getDataElementCategoryOptionCombo( Integer.parseInt( optionComboId ) );

                    if ( optionCombo == null )
                    {
                        return i18n.getString( "cate_option_combo_with_id" ) + ": " + optionComboId + " "
                            + i18n.getString( "does_not_exist" );
                    }
                }
            }
        }
        else if ( exportReport.getReportType().equalsIgnoreCase( ExportReport.TYPE.CATEGORY_VERTICAL ) )
        {
            for ( CategoryOptionGroupOrder groupOrder : ((ExportReportVerticalCategory) exportReport)
                .getCategoryOptionGroupOrders() )
            {
                if ( groupOrder.getCategoryOptions() == null || groupOrder.getCategoryOptions().isEmpty() )
                {
                    return i18n.getString( "group_order" ) + ": " + groupOrder.getName() + " "
                        + i18n.getString( "has_no_element" );
                }
            }

            String deId = null;
            List<String> deIds = new ArrayList<String>();

            for ( ExportItem item : items )
            {
                deId = item.getExpression().split( "\\" + DataElementOperand.SEPARATOR )[0].replace( "[", "" );

                if ( !deIds.contains( deId ) )
                {
                    deIds.add( deId );

                    DataElement de = dataElementService.getDataElement( Integer.parseInt( deId ) );

                    if ( de == null )
                    {
                        return i18n.getString( "dataelement_with_id" ) + ": " + deId + " "
                            + i18n.getString( "does_not_exist" );
                    }
                }
            }
        }
        else
        {
            Set<DataElementOperand> operands = new HashSet<DataElementOperand>();

            for ( ExportItem item : items )
            {
                operands = expressionService.getOperandsInExpression( item.getExpression() );

                for ( DataElementOperand operand : operands )
                {
                    if ( operand.getOptionComboId() == null || operand.getOptionComboId().isEmpty() )
                    {
                        Indicator indicator = indicatorService.getIndicator( operand.getDataElementId() );

                        if ( indicator == null )
                        {
                            return i18n.getString( "indicator_with_id" ) + ": " + operand.getDataElementId() + " "
                                + i18n.getString( "does_not_exist" );
                        }
                    }
                    else
                    {
                        DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );

                        if ( dataElement == null )
                        {
                            return i18n.getString( "dataelement_with_id" ) + ": " + operand.getDataElementId() + " "
                                + i18n.getString( "does_not_exist" );
                        }

                        DataElementCategoryOptionCombo optionCombo = categoryService
                            .getDataElementCategoryOptionCombo( operand.getOptionComboId() );

                        if ( optionCombo == null )
                        {
                            return i18n.getString( "cate_option_combo_with_id" ) + ": " + operand.getOptionComboId()
                                + " " + i18n.getString( "does_not_exist" );
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Map<Integer, String> getPeriodTypeIdentifierMap( Collection<ExportReport> reports )
    {
        String periodTypeName = null;
        Map<Integer, String> idMap = new HashMap<Integer, String>();

        for ( ExportReport exportReport : reports )
        {
            for ( ExportItem exportItem : exportReport.getExportItems() )
            {
                periodTypeName = exportItem.getPeriodType();

                if ( periodTypeName.equalsIgnoreCase( ExportItem.PERIODTYPE.DAILY )
                    || periodTypeName.equalsIgnoreCase( ExportItem.PERIODTYPE.SO_FAR_THIS_MONTH )
                    || periodTypeName.equalsIgnoreCase( ExportItem.PERIODTYPE.SO_FAR_THIS_QUARTER ) )
                {
                    idMap.put( exportReport.getId(), DailyPeriodType.NAME );
                    break;
                }
                // else if ( periodTypeName.equalsIgnoreCase(
                // ExportItem.PERIODTYPE.SELECTED_MONTH )
                // || periodTypeName.equalsIgnoreCase(
                // ExportItem.PERIODTYPE.LAST_3_MONTH )
                // || periodTypeName.equalsIgnoreCase(
                // ExportItem.PERIODTYPE.LAST_6_MONTH )
                // || periodTypeName.equalsIgnoreCase(
                // ExportItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
                // {
                // idMap.put( exportReport.getId(), MonthlyPeriodType.NAME );
                // break;
                // }
                // else if ( periodTypeName.equalsIgnoreCase(
                // ExportItem.PERIODTYPE.QUARTERLY ) )
                // {
                // idMap.put( exportReport.getId(), QuarterlyPeriodType.NAME );
                // break;
                // }
                // else if ( periodTypeName.equalsIgnoreCase(
                // ExportItem.PERIODTYPE.SIX_MONTH ) )
                // {
                // idMap.put( exportReport.getId(), SixMonthlyPeriodType.NAME );
                // break;
                // }
                // else if ( periodTypeName.equalsIgnoreCase(
                // ExportItem.PERIODTYPE.YEARLY ) )
                // {
                // idMap.put( exportReport.getId(), YearlyPeriodType.NAME );
                // break;
                // }

            }
        }

        periodTypeName = null;

        return idMap;
    }

    public ExportReport getExportReportByDataSet( DataSet dataSet )
    {
        return i18n( i18nService, exportReportStore.getExportReportByDataSet( dataSet ) );
    }
}
