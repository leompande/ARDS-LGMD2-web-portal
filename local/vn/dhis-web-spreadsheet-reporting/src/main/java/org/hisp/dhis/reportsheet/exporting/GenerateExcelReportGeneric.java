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
package org.hisp.dhis.reportsheet.exporting;

import static org.hisp.dhis.reportsheet.utils.DateUtils.getEndQuaterly;
import static org.hisp.dhis.reportsheet.utils.DateUtils.getEndSixMonthly;
import static org.hisp.dhis.reportsheet.utils.DateUtils.getFirstDayOfMonth;
import static org.hisp.dhis.reportsheet.utils.DateUtils.getFirstDayOfYear;
import static org.hisp.dhis.reportsheet.utils.DateUtils.getLastDayOfYear;
import static org.hisp.dhis.reportsheet.utils.DateUtils.getStartQuaterly;
import static org.hisp.dhis.reportsheet.utils.DateUtils.getStartSixMonthly;
import static org.hisp.dhis.reportsheet.utils.DateUtils.getTimeRoll;
import static org.hisp.dhis.reportsheet.utils.ExpressionUtils.generateExpression;
import static org.hisp.dhis.reportsheet.utils.ExpressionUtils.generateIndicatorExpression;
import static org.hisp.dhis.reportsheet.utils.FileUtils.checkingExtensionExcelFile;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.amplecode.quick.StatementManager;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.reportsheet.preview.manager.InitializePOIStylesManager;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.hisp.dhis.reportsheet.utils.ExcelUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class GenerateExcelReportGeneric
{
    static final short CELLSTYLE_ALIGN_LEFT = CellStyle.ALIGN_LEFT;

    static final short CELLSTYLE_ALIGN_CENTER = CellStyle.ALIGN_CENTER;

    static final short CELLSTYLE_ALIGN_RIGHT = CellStyle.ALIGN_RIGHT;

    static final short CELLSTYLE_ALIGN_JUSTIFY = CellStyle.ALIGN_JUSTIFY;

    static final short CELLSTYLE_BORDER = CellStyle.BORDER_THIN;

    static final short CELLSTYLE_BORDER_COLOR = IndexedColors.DARK_BLUE.getIndex();

    protected static final String[] chappter = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI",
        "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI",
        "XXVII", "XXVIII", "XXIX", "XXX" };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    InitializePOIStylesManager initPOIStylesManager;

    @Autowired
    protected IndicatorService indicatorService;

    @Autowired
    protected AggregationService aggregationService;

    @Autowired
    protected AggregatedDataValueService aggregatedDataValueService;

    @Autowired
    protected DataElementCategoryService categoryService;

    @Autowired
    protected DataElementService dataElementService;

    @Autowired
    protected PeriodService periodService;

    @Autowired
    protected ExportReportService exportReportService;

    @Autowired
    protected ReportLocationManager reportLocationManager;

    @Autowired
    protected StatementManager statementManager;

    @Autowired
    protected SelectionManager selectionManager;

    @Autowired
    protected OrganisationUnitSelectionManager organisationUnitSelectionManager;

    @Autowired
    protected DataValueService dataValueService;

    protected I18n i18n;

    protected I18nFormat format;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    protected InputStream inputStream;

    private boolean generateByDataSet;

    // -------------------------------------------------------------------------
    // Local variables
    // -------------------------------------------------------------------------

    protected File outputReportFile;

    protected FileInputStream inputStreamExcelTemplate;

    protected FileOutputStream outputStreamExcelTemplate;

    protected Workbook templateWorkbook;

    protected Sheet sheetPOI;

    protected Date startDate;

    protected Date endDate;

    protected Date firstDayOfMonth;

    protected Date firstDayOfYear;

    protected Date last3MonthStartDate;

    protected Date last3MonthEndDate;

    protected Date last6MonthStartDate;

    protected Date last6MonthEndDate;

    protected Date endDateOfYear;

    protected Date startQuaterly;

    protected Date endQuaterly;

    protected Date startSixMonthly;

    protected Date endSixMonthly;

    protected Period selectedPeriod;

    // -------------------------------------------------------------------------
    // Excel format
    // -------------------------------------------------------------------------

    protected Font csFont;

    protected Font csFont8Normal;

    protected Font csFont10Normal;

    protected Font csFont11Bold;

    protected Font csFont11Normal;

    protected Font csFont12NormalCenter;

    protected CellStyle csNumber;

    protected CellStyle csFormulaBold;

    protected CellStyle csFormulaNormal;

    protected CellStyle csText;

    protected CellStyle csTextWithoutBorder;

    protected CellStyle csText10Normal;

    protected CellStyle csText9Bold;

    protected CellStyle csText8Normal;

    protected CellStyle csTextSerial;

    protected CellStyle csTextICDJustify;

    protected CellStyle csText12NormalCenter;

    protected FormulaEvaluator evaluatorFormula;

    SimpleDateFormat dateformatter = new SimpleDateFormat( "dd.MM.yyyy.h.mm.ss.a" );

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    /**
     * @param i18n the i18n to set
     */
    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public boolean isGenerateByDataSet()
    {
        return generateByDataSet;
    }

    public void setGenerateByDataSet( boolean generateByDataSet )
    {
        this.generateByDataSet = generateByDataSet;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void createWorkbookInstance( String excelTemplateFileName )
        throws FileNotFoundException, IOException
    {
        this.inputStreamExcelTemplate = new FileInputStream( reportLocationManager.getExportReportTemplateDirectory()
            + File.separator + excelTemplateFileName );

        if ( checkingExtensionExcelFile( excelTemplateFileName ) )
        {
            this.templateWorkbook = new HSSFWorkbook( this.inputStreamExcelTemplate );
        }
        else
        {
            this.templateWorkbook = new XSSFWorkbook( this.inputStreamExcelTemplate );
        }
    }

    private void initExcelFormat()
        throws Exception
    {
        sheetPOI = templateWorkbook.getSheetAt( 0 );
        csFont = templateWorkbook.createFont();
        csFont8Normal = templateWorkbook.createFont();
        csFont10Normal = templateWorkbook.createFont();
        csFont11Bold = templateWorkbook.createFont();
        csFont11Normal = templateWorkbook.createFont();
        csFont12NormalCenter = templateWorkbook.createFont();
        csNumber = templateWorkbook.createCellStyle();
        csFormulaBold = templateWorkbook.createCellStyle();
        csFormulaNormal = templateWorkbook.createCellStyle();
        csText = templateWorkbook.createCellStyle();
        csTextWithoutBorder = templateWorkbook.createCellStyle();
        csText8Normal = templateWorkbook.createCellStyle();
        csText10Normal = templateWorkbook.createCellStyle();
        csTextSerial = templateWorkbook.createCellStyle();
        csTextICDJustify = templateWorkbook.createCellStyle();
        csText12NormalCenter = templateWorkbook.createCellStyle();
    }

    @SuppressWarnings( "static-access" )
    private void installDefaultExcelFormat()
        throws Exception
    {
        initPOIStylesManager.initDefaultFont( csFont );
        initPOIStylesManager.initDefaultCellStyle( csText, csFont );

        csTextWithoutBorder.setFont( csFont );

        initPOIStylesManager.initFont( csFont8Normal, "Tahoma", (short) 8, Font.BOLDWEIGHT_NORMAL, IndexedColors.BLACK
            .getIndex() );
        initPOIStylesManager.initFont( csFont10Normal, "Tahoma", (short) 10, Font.BOLDWEIGHT_NORMAL,
            IndexedColors.BLACK.getIndex() );
        initPOIStylesManager.initFont( csFont11Bold, "Tahoma", (short) 11, Font.BOLDWEIGHT_BOLD,
            IndexedColors.DARK_BLUE.getIndex() );
        initPOIStylesManager.initFont( csFont11Normal, "Tahoma", (short) 11, Font.BOLDWEIGHT_NORMAL,
            IndexedColors.DARK_BLUE.getIndex() );
        initPOIStylesManager.initFont( csFont12NormalCenter, "Tahoma", (short) 12, Font.BOLDWEIGHT_NORMAL,
            IndexedColors.BLUE.getIndex() );

        initPOIStylesManager.initCellStyle( csNumber, csFont, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_RIGHT, false );
        initPOIStylesManager.initCellStyle( csFormulaBold, csFont11Bold, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_RIGHT, true );
        initPOIStylesManager.initCellStyle( csFormulaNormal, csFont11Normal, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_RIGHT, true );
        initPOIStylesManager.initCellStyle( csText8Normal, csFont8Normal, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_LEFT,
            true );
        initPOIStylesManager.initCellStyle( csText10Normal, csFont10Normal, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_LEFT,
            true );
        initPOIStylesManager.initCellStyle( csTextSerial, csFont, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_CENTER, false );
        initPOIStylesManager.initCellStyle( csTextICDJustify, csFont, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_JUSTIFY, true );
        initPOIStylesManager.initCellStyle( csText12NormalCenter, csFont12NormalCenter, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_CENTER, true );

    }

    protected void installPeriod()
    {
        Calendar calendar = Calendar.getInstance();

        // Daily (or Monthly) period
        startDate = selectedPeriod.getStartDate();
        endDate = selectedPeriod.getEndDate();

        // So-far-this-month
        firstDayOfMonth = getFirstDayOfMonth( startDate );
        firstDayOfMonth = getTimeRoll( firstDayOfMonth, Calendar.DATE, -1 );

        // Last 3 month period
        // Last 2 months + this month = last 3 month
        last3MonthStartDate = getTimeRoll( startDate, Calendar.MONTH, -2 );
        last3MonthStartDate = getTimeRoll( last3MonthStartDate, Calendar.DATE, -1 );
        last3MonthEndDate = selectedPeriod.getEndDate();

        // Last 6 month period
        // Last 5 months + this month = last 6 month
        last6MonthStartDate = getTimeRoll( startDate, Calendar.MONTH, -5 );
        last6MonthStartDate = getTimeRoll( last6MonthStartDate, Calendar.DATE, -1 );
        last6MonthEndDate = selectedPeriod.getEndDate();

        // Quarterly
        startQuaterly = getStartQuaterly( startDate );
        startQuaterly = getTimeRoll( startQuaterly, Calendar.DATE, -1 );
        endQuaterly = getEndQuaterly( startDate );

        // Six monthly
        startSixMonthly = getStartSixMonthly( startDate );
        startSixMonthly = getTimeRoll( startSixMonthly, Calendar.DATE, -1 );
        endSixMonthly = getEndSixMonthly( startDate );

        // So far this year period
        calendar.setTime( endDate );

        firstDayOfYear = getFirstDayOfYear( calendar.get( Calendar.YEAR ) );
        firstDayOfYear = getTimeRoll( firstDayOfYear, Calendar.DATE, -1 );
        endDateOfYear = getLastDayOfYear( calendar.get( Calendar.YEAR ) );
    }

    protected void installReadTemplateFile( ExportReport exportReport, Object object )
        throws Exception
    {
        this.outputReportFile = new File( reportLocationManager.getExportReportTemporaryDirectory(), currentUserService
            .getCurrentUsername()
            + this.dateformatter.format( Calendar.getInstance().getTime() ) + exportReport.getExcelTemplateFile() );

        this.outputStreamExcelTemplate = new FileOutputStream( outputReportFile );

        this.createWorkbookInstance( exportReport.getExcelTemplateFile() );

        this.initExcelFormat();

        this.installDefaultExcelFormat();

        this.initFormulaEvaluating();

        if ( exportReport.getOrganisationRow() != null && exportReport.getOrganisationColumn() != null )
        {
            String value = "";

            if ( object instanceof OrganisationUnit )
            {
                value = ((OrganisationUnit) object).getName();
            }
            else
            {
                value = ((OrganisationUnitGroup) object).getName();
            }

            ExcelUtils.writeValueByPOI( exportReport.getOrganisationRow(), exportReport.getOrganisationColumn(), value,
                ExcelUtils.TEXT, sheetPOI, csTextWithoutBorder );
        }

        if ( exportReport.getPeriodRow() != null && exportReport.getPeriodColumn() != null )
        {
            ExcelUtils.writeValueByPOI( exportReport.getPeriodRow(), exportReport.getPeriodColumn(), format
                .formatPeriod( selectedPeriod ), ExcelUtils.TEXT, sheetPOI, csTextWithoutBorder );
        }
    }

    // -------------------------------------------------------------------------
    // DataElement Value as Text
    // -------------------------------------------------------------------------

    protected String getTextValue( ExportItem exportItem, OrganisationUnit organisationUnit )
    {
        String result = "";
        Collection<Period> periods = new ArrayList<Period>();

        if ( generateByDataSet )
        {
            periods = periodService.getPeriodsBetweenDates( selectedPeriod.getPeriodType(), startDate, endDate );
        }
        else
        {
            if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.DAILY ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( DailyPeriodType.NAME ), startDate, startDate );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SELECTED_MONTH ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( MonthlyPeriodType.NAME ), startDate, endDate );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.QUARTERLY ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( QuarterlyPeriodType.NAME ), startQuaterly, endQuaterly );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.YEARLY ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( YearlyPeriodType.NAME ), firstDayOfYear, endDateOfYear );
            }
        }

        for ( Period p : periods )
        {
            result += generateExpression( exportItem, p, organisationUnit, dataElementService, categoryService,
                dataValueService );
            result += "\n";
        }

        return result;
    }

    protected String getTextValue( ExportItem exportItem, OrganisationUnit organisationUnit, Date startDate,
        Date endDate )
    {
        String result = "";
        Collection<Period> periods = new ArrayList<Period>();

        if ( generateByDataSet )
        {
            periods = periodService.getPeriodsBetweenDates( selectedPeriod.getPeriodType(), startDate, endDate );
        }
        else
        {
            if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.DAILY ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( DailyPeriodType.NAME ), startDate, startDate );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SELECTED_MONTH ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( MonthlyPeriodType.NAME ), startDate, endDate );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.QUARTERLY ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( QuarterlyPeriodType.NAME ), startDate, endDate );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.YEARLY ) )
            {
                periods = periodService.getPeriodsBetweenDates( periodService
                    .getPeriodTypeByName( YearlyPeriodType.NAME ), startDate, endDate );
            }
        }

        for ( Period p : periods )
        {
            result += generateExpression( exportItem, p, organisationUnit, dataElementService, categoryService,
                dataValueService );
            result += "\n";
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // DataElement Value
    // -------------------------------------------------------------------------

    protected double getDataValue( ExportItem exportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        if ( generateByDataSet )
        {
            value = calculateExpression( generateExpression( exportItem, startDate, endDate, organisationUnit,
                dataElementService, categoryService, aggregationService ) );
        }
        else
        {
            if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.DAILY ) )
            {
                value = calculateExpression( generateExpression( exportItem, startDate, startDate, organisationUnit,
                    dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SO_FAR_THIS_MONTH ) )
            {
                value = calculateExpression( generateExpression( exportItem, firstDayOfMonth, endDate,
                    organisationUnit, dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SO_FAR_THIS_QUARTER ) )
            {
                value = calculateExpression( generateExpression( exportItem, startQuaterly, endDate, organisationUnit,
                    dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
            {
                value = calculateExpression( generateExpression( exportItem, firstDayOfYear, endDate, organisationUnit,
                    dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SELECTED_MONTH ) )
            {
                value = calculateExpression( generateExpression( exportItem, startDate, endDate, organisationUnit,
                    dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.LAST_3_MONTH ) )
            {
                value = calculateExpression( generateExpression( exportItem, last3MonthStartDate, last3MonthEndDate,
                    organisationUnit, dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.LAST_6_MONTH ) )
            {
                value = calculateExpression( generateExpression( exportItem, last6MonthStartDate, last6MonthEndDate,
                    organisationUnit, dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.QUARTERLY ) )
            {
                value = calculateExpression( generateExpression( exportItem, startQuaterly, endQuaterly,
                    organisationUnit, dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SIX_MONTH ) )
            {
                value = calculateExpression( generateExpression( exportItem, startSixMonthly, endSixMonthly,
                    organisationUnit, dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.THREE_SIX_NINE_TWELVE_MONTH ) )
            {
                value = calculateExpression( generateExpression( exportItem, firstDayOfYear, endQuaterly,
                    organisationUnit, dataElementService, categoryService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.YEARLY ) )
            {
                value = calculateExpression( generateExpression( exportItem, firstDayOfYear, endDateOfYear,
                    organisationUnit, dataElementService, categoryService, aggregationService ) );
            }
        }
        return value;
    }

    // -------------------------------------------------------------------------
    // Indicator Value
    // -------------------------------------------------------------------------

    protected double getIndicatorValue( ExportItem exportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        if ( generateByDataSet )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, startDate, endDate, organisationUnit,
                indicatorService, aggregationService ) );
        }
        else
        {
            if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.DAILY ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, startDate, startDate,
                    organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SELECTED_MONTH ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, startDate, endDate,
                    organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SO_FAR_THIS_QUARTER ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, startQuaterly, endDate,
                    organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, firstDayOfYear, endDate,
                    organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.LAST_3_MONTH ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, last3MonthStartDate,
                    last3MonthEndDate, organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.LAST_6_MONTH ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, last6MonthStartDate,
                    last6MonthEndDate, organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.QUARTERLY ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, startQuaterly, endQuaterly,
                    organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.SIX_MONTH ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, startSixMonthly, endSixMonthly,
                    organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.THREE_SIX_NINE_TWELVE_MONTH ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, firstDayOfYear, endQuaterly,
                    organisationUnit, indicatorService, aggregationService ) );
            }
            else if ( exportItem.getPeriodType().equalsIgnoreCase( ExportItem.PERIODTYPE.YEARLY ) )
            {
                value = calculateExpression( generateIndicatorExpression( exportItem, firstDayOfYear, endDateOfYear,
                    organisationUnit, indicatorService, aggregationService ) );
            }
        }
        return value;
    }

    // -------------------------------------------------------------------------
    // Formulae methods
    // -------------------------------------------------------------------------

    protected void initFormulaEvaluating()
    {
        this.evaluatorFormula = this.templateWorkbook.getCreationHelper().createFormulaEvaluator();
    }

    protected void recalculatingFormula( Sheet sheet )
    {
        for ( Row row : sheet )
        {
            for ( Cell cell : row )
            {
                if ( (cell != null) && (cell.getCellType() == Cell.CELL_TYPE_FORMULA) )
                {
                    this.evaluatorFormula.evaluateFormulaCell( cell );
                }
            }
        }
    }

    protected void complete()
        throws IOException
    {
        this.templateWorkbook.write( outputStreamExcelTemplate );

        this.outputStreamExcelTemplate.close();

        selectionManager.setDownloadFilePath( outputReportFile.getPath() );
    }
}
