package org.hisp.dhis.reports.meta.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateMetaDataReportResultAction
    implements Action
{
    private final String ORGUNIT = "ORGUNIT";

    private final String ORGUNITGRP = "ORGUNITGRP";

    private final String DATAELEMENT = "DATAELEMENTS";

    private final String DATAELEMENTGRP = "DATAELEMENTSGRP";

    private final String INDIACTOR = "INDICATORS";

    private final String INDICATORGRP = "INDICATORGRP";

    private final String DATASET = "DATASETS";

    private final String VALIDATIONRULE = "VALIDATIONRULE";

    private final String VALIDATIONRULEGRP = "VALIDATIONRULEGRP";

    private final String USER = "USER";

    private final String ORGUNIT_USER = "ORGUNIT_USER";

    private final String SOURCE = "SOURCE";

    private final String PRINT = "PRINT";

    private final String SUMMARY = "SUMMARY";

    private final String DATASETMEMBER = "DATASETMEMBER";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitgroupService;

    public void setOrganisationUnitgroupService( OrganisationUnitGroupService organisationUnitgroupService )
    {
        this.organisationUnitgroupService = organisationUnitgroupService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }
    /*
    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    */
    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
    

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String metaDataId;

    public void setMetaDataId( String metaDataId )
    {
        this.metaDataId = metaDataId;
    }

    private String incID;

    public void setIncID( String incID )
    {
        this.incID = incID;
    }

    @SuppressWarnings( "unused" )
    private String raFolderName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        raFolderName = reportService.getRAFolderName();

        System.out.println( "MetaDataReport Generation Start Time is : " + new Date() );

        if ( metaDataId.equalsIgnoreCase( ORGUNIT ) )
        {
            generateOrgUnitList();
        }
        else if ( metaDataId.equalsIgnoreCase( ORGUNITGRP ) )
        {
            generateOrgUnitGroupList();
        }
        else if ( metaDataId.equalsIgnoreCase( DATAELEMENT ) )
        {
            generateDataElementList();
        }
        else if ( metaDataId.equalsIgnoreCase( DATAELEMENTGRP ) )
        {
            generateDataElementGroupList();
        }
        else if ( metaDataId.equalsIgnoreCase( INDIACTOR ) )
        {
            generateIndicatorList();
        }
        else if ( metaDataId.equalsIgnoreCase( INDICATORGRP ) )
        {
            generateIndicatorGroupList();
        }
        else if ( metaDataId.equalsIgnoreCase( DATASET ) )
        {
            generateDataSetList();
        }
        else if ( metaDataId.equalsIgnoreCase( VALIDATIONRULE ) )
        {
            generateValidationRuleList();
        }
        else if ( metaDataId.equalsIgnoreCase( VALIDATIONRULEGRP ) )
        {
            generateValidationGroupList();
        }
        else if ( metaDataId.equalsIgnoreCase( USER ) )
        {
            generateUserList();
        }
        else if ( metaDataId.equalsIgnoreCase( ORGUNIT_USER ) )
        {
            generateOrgUnitTreeAlongWithUsers();
        }
        else if ( metaDataId.equalsIgnoreCase( SUMMARY ) )
        {
            generateSummaryReport();
        }

        else if ( metaDataId.equalsIgnoreCase( DATASETMEMBER ) )
        {
            generateDataSetMemberReport();
        }
        statementManager.destroy();

        System.out.println( "MetaDataReport Generation End Time is : " + new Date() );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive Methods
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Methods for getting Summary List in Excel Sheet
    // -------------------------------------------------------------------------
    public void generateSummaryReport()
        throws Exception
    {

        List<OrganisationUnit> organisationList = new ArrayList<OrganisationUnit>( organisationUnitService
            .getAllOrganisationUnits() );
        int countorgunit = organisationList.size();

        List<OrganisationUnitGroup> orgUnitGroupList = new ArrayList<OrganisationUnitGroup>(
            organisationUnitgroupService.getAllOrganisationUnitGroups() );
        int countOrgUnitGroup = orgUnitGroupList.size();

        List<DataElement> dataelement = new ArrayList<DataElement>( dataElementService.getAllActiveDataElements() );
        int countdatelement = dataelement.size();

        List<DataElementGroup> dataElementGroupList = new ArrayList<DataElementGroup>( dataElementService
            .getAllDataElementGroups() );
        int countdataeleGroup = dataElementGroupList.size();

        List<Indicator> indicator = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        int countindicator = indicator.size();

        List<IndicatorGroup> indicatorGroupList = new ArrayList<IndicatorGroup>( indicatorService
            .getAllIndicatorGroups() );
        int countindigroup = indicatorGroupList.size();

        List<ValidationRule> validation = new ArrayList<ValidationRule>( validationRuleService.getAllValidationRules() );
        int countvalidrules = validation.size();

        List<ValidationRuleGroup> validationRuleGroupList = new ArrayList<ValidationRuleGroup>( validationRuleService
            .getAllValidationRuleGroups() );
        int countvalidgroup = validationRuleGroupList.size();

        List<DataSet> datasetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        int countdataset = datasetList.size();
        
        //Collection<User> UserList = new ArrayList<User>( userStore.getAllUsers() );
        Collection<User> UserList = new ArrayList<User>( userService.getAllUsers() );
        int user = UserList.size();

        // ----------------------------------------------------------------------
        // Coding For Printing MetaData
        // ----------------------------------------------------------------------

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "Summary Report", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        sheet0.mergeCells( 2, 0, 3, 0 );
        sheet0.addCell( new Label( 2, 0, "Summary Report ", getCellFormat5() ) );

        sheet0.addCell( new Label( 2, 3, "Meta Data ", getCellFormat5() ) );

        sheet0.addCell( new Label( 3, 3, " Count", getCellFormat5() ) );

        int rowStart = 5;
        int colStart = 2;

        sheet0.addCell( new Label( colStart, rowStart, "OrganisationUnit ", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, rowStart, countorgunit, getCellFormat4() ) );

        int maxLevels = organisationUnitService.getNumberOfOrganisationalLevels();

        int i = 1;
        for ( i = 1; i <= maxLevels; i++ )
        {
            int size = organisationUnitService.getOrganisationUnitsAtLevel( i ).size();

            sheet0.addCell( new Label( colStart, rowStart + i, "Level - " + i, getCellFormat3() ) );
            sheet0.addCell( new Number( colStart + 1, rowStart + i, size, getCellFormat3() ) );
        }

        sheet0.addCell( new Label( colStart, rowStart + maxLevels + 2, "OrganisationUnitGroup", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, rowStart + maxLevels + 2, countOrgUnitGroup, getCellFormat4() ) );

        int k = rowStart + maxLevels + 2;

        for ( OrganisationUnitGroup orgUnitGroup : orgUnitGroupList )
        {
            sheet0.addCell( new Label( colStart, k + 1, orgUnitGroup.getName(), getCellFormat3() ) );
            sheet0.addCell( new Number( colStart + 1, k + 1, orgUnitGroup.getMembers().size(), getCellFormat3() ) );
            k++;
        }

        sheet0.addCell( new Label( colStart, k + 2, " Dataelements", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 2, countdatelement, getCellFormat4() ) );

        sheet0.addCell( new Label( colStart, k + 4, "DataElementgroup", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 4, countdataeleGroup, getCellFormat4() ) );

        for ( DataElementGroup dataElementGroup : dataElementGroupList )
        {
            sheet0.addCell( new Label( colStart, k + 5, dataElementGroup.getName(), getCellFormat3() ) );
            sheet0.addCell( new Number( colStart + 1, k + 5, dataElementGroup.getMembers().size(), getCellFormat3() ) );
            k++;
        }
        sheet0.addCell( new Label( colStart, k + 6, "Indicator", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 6, countindicator, getCellFormat4() ) );

        sheet0.addCell( new Label( colStart, k + 8, "IndicatorGroup", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 8, countindigroup, getCellFormat4() ) );

        for ( IndicatorGroup indicatorGroup : indicatorGroupList )
        {
            sheet0.addCell( new Label( colStart, k + 9, indicatorGroup.getName(), getCellFormat3() ) );
            sheet0.addCell( new Number( colStart + 1, k + 9, indicatorGroup.getMembers().size(), getCellFormat3() ) );
            k++;
        }

        sheet0.addCell( new Label( colStart, k + 10, "Validation", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 10, countvalidrules, getCellFormat4() ) );

        sheet0.addCell( new Label( colStart, k + 12, "Validation Group", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 12, countvalidgroup, getCellFormat4() ) );

        for ( ValidationRuleGroup validruleGroup : validationRuleGroupList )
        {
            sheet0.addCell( new Label( colStart, k + 13, validruleGroup.getName(), getCellFormat3() ) );
            sheet0.addCell( new Number( colStart + 1, k + 13, validruleGroup.getMembers().size(), getCellFormat3() ) );
            k++;
        }

        sheet0.addCell( new Label( colStart, k + 14, "Datasets", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 14, countdataset, getCellFormat4() ) );

        sheet0.addCell( new Label( colStart, k + 16, "User", getCellFormat4() ) );
        sheet0.addCell( new Number( colStart + 1, k + 16, user, getCellFormat4() ) );

        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = "SummaryList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting DataElementwise List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateDataElementList()
        throws Exception
    {
        List<DataElement> dataElementList = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        Collections.sort( dataElementList, new IdentifiableObjectNameComparator() );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "DataElements", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;
        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataElementID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataElementID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "DataElementName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "DataElementAlternativeName", getCellFormat1() ) );
                sheet0
                    .addCell( new Label( colStart + 3, rowStart, "DataElementAggregationOperator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "DataElementDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "DataElementCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "DataElementShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "DataElementType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "DataElementUrl", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 9, rowStart, "DomainType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart, "NumberType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 11, rowStart, "PeriodType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 12, rowStart, "LastUpdated", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "DataElementName", getCellFormat1() ) );

        rowStart++;

        for ( DataElement dataElement : dataElementList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, dataElement.getId(), wCellformat ) );
                }
                else if ( incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, dataElement.getId(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 1, rowStart, dataElement.getName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, dataElement.getAlternativeName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, dataElement.getAggregationOperator(),
                        wCellformat ) );
                    sheet0.addCell( new Label( colStart + 4, rowStart, dataElement.getDescription(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 5, rowStart, dataElement.getCode(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 6, rowStart, dataElement.getShortName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 7, rowStart, dataElement.getType(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 8, rowStart, dataElement.getUrl(), wCellformat ) );

                    String domainType = new String();
                    if ( dataElement.getDomainType() != null )
                    {
                        domainType = dataElement.getDomainType();
                    }
                    else
                    {
                        domainType = "";
                    }
                    sheet0.addCell( new Label( colStart + 9, rowStart, domainType, wCellformat ) );

                    String numberType = new String();
                    if ( dataElement.getNumberType() != null )
                    {
                        numberType = dataElement.getNumberType();
                    }
                    else
                    {
                        numberType = "";
                    }
                    sheet0.addCell( new Label( colStart + 10, rowStart, numberType, wCellformat ) );

                    String periodType = new String();
                    if ( dataElement.getPeriodType() != null )
                    {
                        periodType = dataElement.getPeriodType().getName();
                    }
                    else
                    {
                        periodType = "";
                    }
                    sheet0.addCell( new Label( colStart + 11, rowStart, periodType, wCellformat ) );

                    String lastUpdate = new String();
                    if ( dataElement.getLastUpdated() != null )
                    {
                        lastUpdate = dataElement.getLastUpdated().toString();
                    }
                    else
                    {
                        lastUpdate = "";
                    }
                    sheet0.addCell( new Label( colStart + 12, rowStart, lastUpdate, wCellformat ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, dataElement.getName(), wCellformat ) );

            rowStart++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "DataElementList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting DataElement Groupwise List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateDataElementGroupList()
        throws Exception
    {
        List<DataElementGroup> dataElementGroupList = new ArrayList<DataElementGroup>( dataElementService
            .getAllDataElementGroups() );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "DataElementsGroupWiseList", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataElementID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataElementID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "DataElementName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "DataElementAlternativeName", getCellFormat1() ) );
                sheet0
                    .addCell( new Label( colStart + 3, rowStart, "DataElementAggregationOperator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "DataElementDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "DataElementCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "DataElementShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "DataElementType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "DataElementUrl", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 9, rowStart, "DomainType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart, "NumberType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 11, rowStart, "PeriodType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 12, rowStart, "LastUpdated", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "DataElementName", getCellFormat1() ) );

        rowStart++;

        for ( DataElementGroup dataElementGroup : dataElementGroupList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) || incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, dataElementGroup.getId(), getCellFormat1() ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, dataElementGroup.getName(), getCellFormat1() ) );

            if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.mergeCells( colStart + 1, rowStart, colStart + 12, rowStart );
            }

            rowStart++;

            List<DataElement> dataElementList = new ArrayList<DataElement>( dataElementGroup.getMembers() );

            Collections.sort( dataElementList, new IdentifiableObjectNameComparator() );

            for ( DataElement dataElement : dataElementList )
            {
                if ( incID != null )
                {
                    if ( incID.equalsIgnoreCase( SOURCE ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, dataElement.getId(), wCellformat ) );
                    }
                    else if ( incID.equalsIgnoreCase( PRINT ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, dataElement.getId(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 1, rowStart, dataElement.getName(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 2, rowStart, dataElement.getAlternativeName(),
                            wCellformat ) );
                        sheet0.addCell( new Label( colStart + 3, rowStart, dataElement.getAggregationOperator(),
                            wCellformat ) );
                        sheet0.addCell( new Label( colStart + 4, rowStart, dataElement.getDescription(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 5, rowStart, dataElement.getCode(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 6, rowStart, dataElement.getShortName(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 7, rowStart, dataElement.getType(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 8, rowStart, dataElement.getUrl(), wCellformat ) );

                        String domainType = new String();
                        if ( dataElement.getDomainType() != null )
                        {
                            domainType = dataElement.getDomainType();
                        }
                        else
                        {
                            domainType = "";
                        }
                        sheet0.addCell( new Label( colStart + 9, rowStart, domainType, wCellformat ) );

                        String numberType = new String();
                        if ( dataElement.getNumberType() != null )
                        {
                            numberType = dataElement.getNumberType();
                        }
                        else
                        {
                            numberType = "";
                        }
                        sheet0.addCell( new Label( colStart + 10, rowStart, numberType, wCellformat ) );

                        String periodType = new String();
                        if ( dataElement.getPeriodType() != null )
                        {
                            periodType = dataElement.getPeriodType().getName();
                        }
                        else
                        {
                            periodType = "";
                        }
                        sheet0.addCell( new Label( colStart + 11, rowStart, periodType, wCellformat ) );

                        String lastUpdate = new String();
                        if ( dataElement.getLastUpdated() != null )
                        {
                            lastUpdate = dataElement.getLastUpdated().toString();
                        }
                        else
                        {
                            lastUpdate = "";
                        }
                        sheet0.addCell( new Label( colStart + 12, rowStart, lastUpdate, wCellformat ) );
                    }
                }

                sheet0.addCell( new Label( colStart + 1, rowStart, dataElement.getName(), wCellformat ) );

                rowStart++;
            }
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "DataElementGroupWiseList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Methods for getting Organisation Unit Groupwise List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateOrgUnitGroupList()
        throws Exception
    {
        List<OrganisationUnitGroup> orgUnitGroupList = new ArrayList<OrganisationUnitGroup>(
            organisationUnitgroupService.getAllOrganisationUnitGroups() );
        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "OrganisationUnitGroupWiseList", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.CENTRE );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "OrganisationUnitID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "OrganisationUnitID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "OrganisationUnitName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "organisationUnitShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "organisationUnitOpeningDate", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "organisationUnitClosedDate", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "organisationUnitParentName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "organisationUnitCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "organisationUnitUrl", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "Last Updated", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 9, rowStart, "Contact Person", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart, "Phone Number", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 11, rowStart, "Email", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 12, rowStart, "Comment", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 13, rowStart, "Coordinates", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "OrganisationUnitName", getCellFormat1() ) );

        rowStart++;

        for ( OrganisationUnitGroup organisationUnitGroup : orgUnitGroupList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) || incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, organisationUnitGroup.getId(), getCellFormat1() ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, organisationUnitGroup.getName(), getCellFormat1() ) );

            if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.mergeCells( colStart + 1, rowStart, colStart + 13, rowStart );
            }

            rowStart++;

            List<OrganisationUnit> organisationUnitList = new ArrayList<OrganisationUnit>( organisationUnitGroup
                .getMembers() );

            Collections.sort( organisationUnitList, new IdentifiableObjectNameComparator() );

            for ( OrganisationUnit organisationUnit : organisationUnitList )
            {
                if ( incID != null )
                {
                    if ( incID.equalsIgnoreCase( SOURCE ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, organisationUnit.getId(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 1, rowStart, organisationUnit.getName(), wCellformat ) );
                    }
                    else if ( incID.equalsIgnoreCase( PRINT ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, organisationUnit.getId(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 1, rowStart, organisationUnit.getName(), wCellformat ) );
                        sheet0
                            .addCell( new Label( colStart + 2, rowStart, organisationUnit.getShortName(), wCellformat ) );

                        String opendate = new String();
                        if ( organisationUnit.getOpeningDate() != null )
                        {
                            opendate = organisationUnit.getOpeningDate().toString();
                        }
                        else
                        {
                            opendate = "";
                        }
                        sheet0.addCell( new Label( colStart + 3, rowStart, opendate, wCellformat ) );

                        String closedate = new String();
                        if ( organisationUnit.getClosedDate() != null )
                        {
                            closedate = organisationUnit.getClosedDate().toString();
                        }
                        else
                        {
                            closedate = "";
                        }
                        sheet0.addCell( new Label( colStart + 4, rowStart, closedate, wCellformat ) );

                        String PARENT = new String();
                        if ( organisationUnit.getParent() != null )
                        {
                            PARENT = organisationUnit.getParent().getName();
                        }
                        else
                        {
                            PARENT = "ROOT";
                        }

                        sheet0.addCell( new Label( colStart + 5, rowStart, PARENT, wCellformat ) );
                        sheet0.addCell( new Label( colStart + 6, rowStart, organisationUnit.getCode(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 7, rowStart, organisationUnit.getUrl(), wCellformat ) );

                        String lastUpdate = new String();
                        if ( organisationUnit.getLastUpdated() != null )
                        {
                            lastUpdate = organisationUnit.getLastUpdated().toString();
                        }
                        else
                        {
                            lastUpdate = "";
                        }
                        sheet0.addCell( new Label( colStart + 8, rowStart, lastUpdate, wCellformat ) );

                        String contactPerson = new String();
                        if ( organisationUnit.getContactPerson() != null )
                        {
                            contactPerson = organisationUnit.getContactPerson();
                        }
                        else
                        {
                            contactPerson = "";
                        }
                        sheet0.addCell( new Label( colStart + 9, rowStart, contactPerson, wCellformat ) );

                        String phoneNumber = new String();
                        if ( organisationUnit.getPhoneNumber() != null )
                        {
                            phoneNumber = organisationUnit.getPhoneNumber();
                        }
                        else
                        {
                            phoneNumber = "";
                        }
                        sheet0.addCell( new Label( colStart + 10, rowStart, phoneNumber, wCellformat ) );

                        String email = new String();
                        if ( organisationUnit.getEmail() != null )
                        {
                            email = organisationUnit.getEmail();
                        }
                        else
                        {
                            email = "";
                        }
                        sheet0.addCell( new Label( colStart + 11, rowStart, email, wCellformat ) );

                        String comment = new String();
                        if ( organisationUnit.getComment() != null )
                        {
                            comment = organisationUnit.getComment();
                        }
                        else
                        {
                            comment = "";
                        }
                        sheet0.addCell( new Label( colStart + 12, rowStart, comment, wCellformat ) );

                        String coordinates = new String();
                        if ( organisationUnit.getCoordinates() != null )
                        {
                            coordinates = organisationUnit.getCoordinates();
                        }
                        else
                        {
                            coordinates = "";
                        }
                        sheet0.addCell( new Label( colStart + 13, rowStart, coordinates, wCellformat ) );

                    }
                    else
                    {
                        sheet0.addCell( new Label( colStart + 1, rowStart, organisationUnit.getName(), wCellformat ) );
                    }
                }

                rowStart++;
            }
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "OrganisationUnitGroupWiseList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // OrganisationUnit Tree along with Users
    // -------------------------------------------------------------------------

    public void generateOrgUnitTreeAlongWithUsers()
        throws Exception
    {
        OrganisationUnit rootOrgUnit = organisationUnitService.getRootOrganisationUnits().iterator().next();

        List<OrganisationUnit> OrganisitionUnitList = new ArrayList<OrganisationUnit>( organisationUnitService
            .getOrganisationUnitWithChildren( rootOrgUnit.getId() ) );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator + "output" +
        // File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "OrganisationUnit", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.CENTRE );

        int rowStart = 0;
        int colStart = 0;

        sheet0.addCell( new Label( colStart, rowStart, "OrgUnitID", getCellFormat1() ) );

        int maxLevels = organisationUnitService.getNumberOfOrganisationalLevels();
        int i = 1;
        for ( i = 1; i <= maxLevels; i++ )
        {
            sheet0.addCell( new Label( colStart + i, rowStart, "Level - " + i, getCellFormat1() ) );
        }

        sheet0.addCell( new Label( colStart + i, rowStart, "UserID", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + i + 1, rowStart, "UserName", getCellFormat1() ) );

        rowStart++;

        Iterator<OrganisationUnit> orgUnitIterator = OrganisitionUnitList.iterator();
        while ( orgUnitIterator.hasNext() )
        {
            OrganisationUnit ou = orgUnitIterator.next();

            sheet0.addCell( new Number( colStart, rowStart, ou.getId(), getCellFormat2() ) );

            int ouLevel = ou.getLevel();

            sheet0.addCell( new Label( colStart + ouLevel, rowStart, ou.getName(), getCellFormat2() ) );

            // String query =
            // "SELECT userid,username FROM users WHERE userid IN ( SELECT userinfoid FROM usermembership WHERE organisationunitid = "+
            // ou.getId() +")";
            String query = "SELECT users.userid,users.username FROM users INNER JOIN usermembership ON users.userid = usermembership.userinfoid WHERE usermembership.organisationunitid = "
                + ou.getId();
            String userName = "";
            String userId = "";

            try
            {
                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

                if ( sqlResultSet != null )
                {
                    sqlResultSet.beforeFirst();

                    while ( sqlResultSet.next() )
                    {
                        userId += sqlResultSet.getInt( 1 ) + ", ";
                        userName += sqlResultSet.getString( 2 ) + ", ";
                    }
                }
            }
            catch ( Exception e )
            {
                System.out.println( "Exception with jdbcTemplate: " + e.getMessage() );
            }

            sheet0.addCell( new Label( colStart + maxLevels + 1, rowStart, userId, getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + maxLevels + 2, rowStart, userName, getCellFormat2() ) );

            rowStart++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "OrgUnit_UserList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Methods for getting Organisation List in Tree format in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateOrgUnitList()
        throws Exception
    {
        OrganisationUnit rootOrgUnit = organisationUnitService.getRootOrganisationUnits().iterator().next();

        List<OrganisationUnit> OrganisitionUnitList = new ArrayList<OrganisationUnit>( organisationUnitService
            .getOrganisationUnitWithChildren( rootOrgUnit.getId() ) );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator + "output" +
        // File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "OrganisationUnit", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.CENTRE );

        int rowStart = 0;
        int colStart = 0;

        // Heading
        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "OrgUnitID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "OrganisationUnitName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "OrganisationUnitShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "OrganisationUnitOpeningDate", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "OrganisationUnitClosedDate", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "OrganisationUnitParentName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "OrganisationUnitCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "OrganisationUnitUrl", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "Last Updated", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 9, rowStart, "Contact Person", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart, "Phone Number", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 11, rowStart, "Email", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 12, rowStart, "Comment", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 13, rowStart, "Coordinates", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "OrgUnitID", getCellFormat1() ) );
                int maxLevels = organisationUnitService.getNumberOfOrganisationalLevels();
                for ( int i = 1; i <= maxLevels; i++ )
                {
                    sheet0.addCell( new Label( colStart + i, rowStart, "Level-" + i, getCellFormat1() ) );
                }
            }
            else
            {
                int maxLevels = organisationUnitService.getNumberOfOrganisationalLevels();
                for ( int i = 1; i <= maxLevels; i++ )
                {
                    sheet0.addCell( new Label( colStart + i, rowStart, "Level-" + i, getCellFormat1() ) );
                }
            }
        }

        rowStart++;
        Iterator<OrganisationUnit> orgUnitIterator = OrganisitionUnitList.iterator();
        while ( orgUnitIterator.hasNext() )
        {
            OrganisationUnit ou = orgUnitIterator.next();
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, ou.getId(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 1, rowStart, ou.getName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, ou.getShortName(), wCellformat ) );

                    String opendate = new String();
                    if ( ou.getOpeningDate() != null )
                    {
                        opendate = ou.getOpeningDate().toString();
                    }
                    else
                    {
                        opendate = "";
                    }
                    sheet0.addCell( new Label( colStart + 3, rowStart, opendate, wCellformat ) );

                    String closedate = new String();
                    if ( ou.getClosedDate() != null )
                    {
                        closedate = ou.getClosedDate().toString();
                    }
                    else
                    {
                        closedate = "";
                    }
                    sheet0.addCell( new Label( colStart + 4, rowStart, closedate, wCellformat ) );

                    String PARENT = new String();
                    if ( ou.getParent() != null )
                    {
                        PARENT = ou.getParent().getShortName();
                    }
                    else
                    {
                        PARENT = "ROOT";
                    }
                    sheet0.addCell( new Label( colStart + 5, rowStart, PARENT, wCellformat ) );

                    sheet0.addCell( new Label( colStart + 6, rowStart, ou.getCode(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 7, rowStart, ou.getUrl(), wCellformat ) );

                    String lastUpdate = new String();
                    if ( ou.getLastUpdated() != null )
                    {
                        lastUpdate = ou.getLastUpdated().toString();
                    }
                    else
                    {
                        lastUpdate = "";
                    }
                    sheet0.addCell( new Label( colStart + 8, rowStart, lastUpdate, wCellformat ) );

                    String contactPerson = new String();
                    if ( ou.getContactPerson() != null )
                    {
                        contactPerson = ou.getContactPerson();
                    }
                    else
                    {
                        contactPerson = "";
                    }
                    sheet0.addCell( new Label( colStart + 9, rowStart, contactPerson, wCellformat ) );

                    String phoneNumber = new String();
                    if ( ou.getPhoneNumber() != null )
                    {
                        phoneNumber = ou.getPhoneNumber();
                    }
                    else
                    {
                        phoneNumber = "";
                    }
                    sheet0.addCell( new Label( colStart + 10, rowStart, phoneNumber, wCellformat ) );

                    String email = new String();
                    if ( ou.getEmail() != null )
                    {
                        email = ou.getEmail();
                    }
                    else
                    {
                        email = "";
                    }
                    sheet0.addCell( new Label( colStart + 11, rowStart, email, wCellformat ) );

                    String comment = new String();
                    if ( ou.getComment() != null )
                    {
                        comment = ou.getComment();
                    }
                    else
                    {
                        comment = "";
                    }
                    sheet0.addCell( new Label( colStart + 12, rowStart, comment, wCellformat ) );

                    String coordinates = new String();
                    if ( ou.getCoordinates() != null )
                    {
                        coordinates = ou.getCoordinates();
                    }
                    else
                    {
                        coordinates = "";
                    }
                    sheet0.addCell( new Label( colStart + 13, rowStart, coordinates, wCellformat ) );
                }
                else if ( incID.equalsIgnoreCase( SOURCE ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, ou.getId(), getCellFormat2() ) );
                    int ouLevel = ou.getLevel();
                    sheet0.addCell( new Label( colStart + ouLevel, rowStart, ou.getShortName(), getCellFormat2() ) );
                }
                else
                {
                    int ouLevel = ou.getLevel();
                    sheet0.addCell( new Label( colStart + ouLevel, rowStart, ou.getName(), getCellFormat2() ) );
                }
            }

            rowStart++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "OrgUnitList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting Indicator List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateIndicatorList()
        throws Exception
    {
        List<Indicator> indicatorList = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        Collections.sort( indicatorList, new IdentifiableObjectNameComparator() );

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "Indicators", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "IndicatorID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "IndicatorName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "IndicatorNumerator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "IndicatorDenominator", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "IndicatorID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "IndicatorName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "IndicatorAlternativeName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "IndicatorCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "IndicatorNumerator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "IndicatorDenominator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "IndicatorNumeratorDescription", getCellFormat1() ) );
                sheet0
                    .addCell( new Label( colStart + 7, rowStart, "IndicatorDenominatorDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "IndicatorNumeratorAggregationOperator",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 9, rowStart, "IndicatorDenominatorAggregationOperator",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart, "IndicatorDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 11, rowStart, "IndicatorShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 12, rowStart, "IndicatorUrl", getCellFormat1() ) );
            }
            else
            {
                sheet0.addCell( new Label( colStart + 1, rowStart, "IndicatorName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "IndicatorNumerator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "IndicatorDenominator", getCellFormat1() ) );
            }
        }

        rowStart++;

        for ( Indicator indicator : indicatorList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, indicator.getId(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 1, rowStart, indicator.getName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, expressionService
                        .getExpressionDescription( indicator.getNumerator() ), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, expressionService
                        .getExpressionDescription( indicator.getDenominator() ), wCellformat ) );
                }
                else if ( incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, indicator.getId(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 1, rowStart, indicator.getName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, indicator.getAlternativeName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, indicator.getCode(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 4, rowStart, expressionService
                        .getExpressionDescription( indicator.getNumerator() ), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 5, rowStart, expressionService
                        .getExpressionDescription( indicator.getDenominator() ), wCellformat ) );
                    sheet0
                        .addCell( new Label( colStart + 6, rowStart, indicator.getNumeratorDescription(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 7, rowStart, indicator.getDenominatorDescription(),
                        wCellformat ) );
                    // sheet0.addCell( new Label( colStart + 8, rowStart,
                    // indicator.getNumeratorAggregationOperator(),wCellformat )
                    // );
                    sheet0
                        .addCell( new Label( colStart + 8, rowStart, indicator.getNumeratorDescription(), wCellformat ) );
                    // sheet0.addCell( new Label( colStart + 9, rowStart,
                    // indicator.getDenominatorAggregationOperator(),wCellformat
                    // ) );
                    sheet0.addCell( new Label( colStart + 9, rowStart, indicator.getDenominatorDescription(),
                        wCellformat ) );
                    sheet0.addCell( new Label( colStart + 10, rowStart, indicator.getDescription(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 11, rowStart, indicator.getShortName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 12, rowStart, indicator.getUrl(), wCellformat ) );
                }
                else
                {
                    sheet0.addCell( new Label( colStart + 1, rowStart, indicator.getName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, expressionService
                        .getExpressionDescription( indicator.getNumerator() ), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, expressionService
                        .getExpressionDescription( indicator.getDenominator() ), wCellformat ) );
                }
            }

            rowStart++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "IndicatorList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting Indicator Group wise List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateIndicatorGroupList()
        throws Exception
    {
        List<IndicatorGroup> indicatorGroupList = new ArrayList<IndicatorGroup>( indicatorService
            .getAllIndicatorGroups() );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "IndicatorGroupWiseList", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "IndicatorID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "IndicatorID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "IndicatorName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "IndicatorAlternativeName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "IndicatorCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "IndicatorNumerator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "IndicatorDenominator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "IndicatorNumeratorDescription", getCellFormat1() ) );
                sheet0
                    .addCell( new Label( colStart + 7, rowStart, "IndicatorDenominatorDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "IndicatorNumeratorAggregationOperator",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 9, rowStart, "IndicatorDenominatorAggregationOperator",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart, "IndicatorDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 11, rowStart, "IndicatorShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 12, rowStart, "IndicatorUrl", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "IndicatorName", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 2, rowStart, "IndicatorNumerator", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 3, rowStart, "IndicatorDenominator", getCellFormat1() ) );

        rowStart++;
        for ( IndicatorGroup indicatorGroup : indicatorGroupList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) || incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, indicatorGroup.getId(), getCellFormat1() ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, indicatorGroup.getName(), getCellFormat1() ) );
            sheet0.mergeCells( colStart + 1, rowStart, colStart + 3, rowStart );

            if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.mergeCells( colStart + 1, rowStart, colStart + 12, rowStart );
            }

            rowStart++;

            List<Indicator> indicatorList = new ArrayList<Indicator>( indicatorGroup.getMembers() );
            Collections.sort( indicatorList, new IdentifiableObjectNameComparator() );
            for ( Indicator indicator : indicatorList )
            {
                if ( incID != null )
                {
                    if ( incID.equalsIgnoreCase( SOURCE ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, indicator.getId(), wCellformat ) );
                    }
                    else if ( incID.equalsIgnoreCase( PRINT ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, indicator.getId(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 1, rowStart, indicator.getName(), wCellformat ) );
                        sheet0
                            .addCell( new Label( colStart + 2, rowStart, indicator.getAlternativeName(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 3, rowStart, indicator.getCode(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 4, rowStart, expressionService
                            .getExpressionDescription( indicator.getNumerator() ), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 5, rowStart, expressionService
                            .getExpressionDescription( indicator.getDenominator() ), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 6, rowStart, indicator.getNumeratorDescription(),
                            wCellformat ) );
                        sheet0.addCell( new Label( colStart + 7, rowStart, indicator.getDenominatorDescription(),
                            wCellformat ) );
                        // sheet0.addCell( new Label( colStart + 8, rowStart,
                        // indicator.getNumeratorAggregationOperator(),wCellformat
                        // ) );
                        sheet0.addCell( new Label( colStart + 8, rowStart, indicator.getNumeratorDescription(),
                            wCellformat ) );
                        // sheet0.addCell( new Label( colStart + 9, rowStart,
                        // indicator.getDenominatorAggregationOperator(),
                        // wCellformat ) );
                        sheet0.addCell( new Label( colStart + 9, rowStart, indicator.getDenominatorDescription(),
                            wCellformat ) );
                        sheet0.addCell( new Label( colStart + 10, rowStart, indicator.getDescription(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 11, rowStart, indicator.getShortName(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 12, rowStart, indicator.getUrl(), wCellformat ) );
                    }
                }

                sheet0.addCell( new Label( colStart + 1, rowStart, indicator.getName(), wCellformat ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, expressionService
                    .getExpressionDescription( indicator.getNumerator() ), wCellformat ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, expressionService
                    .getExpressionDescription( indicator.getDenominator() ), wCellformat ) );

                rowStart++;
            }
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "IndicatorGroupWiseList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Methods for getting DataSet List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateDataSetList()
        throws Exception
    {
        List<DataSet> datasetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Collections.sort( datasetList, new IdentifiableObjectNameComparator() );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "DataSets", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataSetID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataSetID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "DataSetName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "DataSetShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "DataSetCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "DataSetAlternativeName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "DataSetPeriodType", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "DataSetName", getCellFormat1() ) );

        rowStart++;

        for ( DataSet dataSet : datasetList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, dataSet.getId(), wCellformat ) );
                }
                else if ( incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, dataSet.getId(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 1, rowStart, dataSet.getName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, dataSet.getShortName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, dataSet.getCode(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 4, rowStart, dataSet.getAlternativeName(), wCellformat ) );
                    sheet0
                        .addCell( new Label( colStart + 5, rowStart, dataSet.getPeriodType().getName(), wCellformat ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, dataSet.getName(), wCellformat ) );

            rowStart++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "DataSetList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Methods for getting Validation Rule List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateValidationRuleList()
        throws Exception
    {
        List<ValidationRule> validationRuleList = new ArrayList<ValidationRule>( validationRuleService
            .getAllValidationRules() );
        Collections.sort( validationRuleList, new IdentifiableObjectNameComparator() );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "ValidationRule", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "ValidationRuleID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "ValidationRuleID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "ValidationRuleName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "ValidationRuleDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "ValidationRuleMathematicalOperator",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "ValidationRuleOperator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "ValidationRuleType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "ValidationRuleLeftSideDescription",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "ValidationRuleRightSideDescription",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "ValidationRulePeriodType", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "ValidationRuleName", getCellFormat1() ) );

        rowStart++;

        for ( ValidationRule validationRule : validationRuleList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, validationRule.getId(), wCellformat ) );
                }
                else if ( incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, validationRule.getId(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 1, rowStart, validationRule.getName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, validationRule.getDescription(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, validationRule.getOperator()
                        .getMathematicalOperator(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 4, rowStart, validationRule.getOperator().toString(),
                        wCellformat ) );
                    sheet0.addCell( new Label( colStart + 5, rowStart, validationRule.getType(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 6, rowStart, validationRule.getLeftSide().getDescription(),
                        wCellformat ) );
                    sheet0.addCell( new Label( colStart + 7, rowStart, validationRule.getRightSide().getDescription(),
                        wCellformat ) );

                    String periodType = new String();
                    if ( validationRule.getPeriodType() != null )
                    {
                        periodType = validationRule.getPeriodType().getName();
                    }
                    else
                    {
                        periodType = "";
                    }
                    sheet0.addCell( new Label( colStart + 8, rowStart, periodType, wCellformat ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, validationRule.getName(), wCellformat ) );

            rowStart++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "ValidationRuleList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Methods for getting DataElement Group wise List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateValidationGroupList()
        throws Exception
    {
        List<ValidationRuleGroup> validationRuleGroupList = new ArrayList<ValidationRuleGroup>( validationRuleService
            .getAllValidationRuleGroups() );

        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "ValidationRuleGroupWiseList", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "ValidationRuleID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "ValidationRuleID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "ValidationRuleName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "ValidationRuleDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "ValidationRuleMathematicalOperator",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "ValidationRuleOperator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "ValidationRuleType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "ValidationRuleLeftSideDescription",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "ValidationRuleRightSideDescription",
                    getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "ValidationRulePeriodType", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "ValidationRuleName", getCellFormat1() ) );

        rowStart++;

        for ( ValidationRuleGroup validationRuleGroup : validationRuleGroupList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) || incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, validationRuleGroup.getId(), getCellFormat1() ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, validationRuleGroup.getName(), getCellFormat1() ) );

            if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.mergeCells( colStart + 1, rowStart, colStart + 8, rowStart );
            }

            rowStart++;

            List<ValidationRule> validationRuleList = new ArrayList<ValidationRule>( validationRuleGroup.getMembers() );
            Collections.sort( validationRuleList, new IdentifiableObjectNameComparator() );
            for ( ValidationRule validationRule : validationRuleList )
            {
                if ( incID != null )
                {
                    if ( incID.equalsIgnoreCase( SOURCE ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, validationRule.getId(), wCellformat ) );
                    }
                    else if ( incID.equalsIgnoreCase( PRINT ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, validationRule.getId(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 1, rowStart, validationRule.getName(), wCellformat ) );
                        sheet0
                            .addCell( new Label( colStart + 2, rowStart, validationRule.getDescription(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 3, rowStart, validationRule.getOperator()
                            .getMathematicalOperator(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 4, rowStart, validationRule.getOperator().toString(),
                            wCellformat ) );
                        sheet0.addCell( new Label( colStart + 5, rowStart, validationRule.getType(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 6, rowStart, validationRule.getLeftSide()
                            .getDescription(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 7, rowStart, validationRule.getRightSide()
                            .getDescription(), wCellformat ) );
                        String periodType = new String();
                        if ( validationRule.getPeriodType() != null )
                        {
                            periodType = validationRule.getPeriodType().getName();
                        }
                        else
                        {
                            periodType = "";
                        }
                        sheet0.addCell( new Label( colStart + 8, rowStart, periodType, wCellformat ) );
                    }
                }

                sheet0.addCell( new Label( colStart + 1, rowStart, validationRule.getName(), wCellformat ) );

                rowStart++;
            }
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "ValidationRuleGroupWiseList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

    }// end generateValidationGroupList method

    // -------------------------------------------------------------------------
    // Method for getting User List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateUserList()
        throws Exception
    {
        //List<User> userList = new ArrayList<User>( userStore.getAllUsers() );
        List<User> userList = new ArrayList<User>( userService.getAllUsers() );
        // String outputReportPath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "UserInfo", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "UserID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "userID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "UserName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "UserFirstName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, "UserSurName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "UserEmail", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "UserPhoneNumber", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "UserOrganisationUnit", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "UserRole", getCellFormat1() ) );
            }
        }

        sheet0.addCell( new Label( colStart + 1, rowStart, "UserName", getCellFormat1() ) );

        rowStart++;

        for ( User user : userList )
        {
            String query = "SELECT username FROM users WHERE userid = " + user.getId();
            String userName = "";

            try
            {
                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

                if ( sqlResultSet != null )
                {
                    sqlResultSet.beforeFirst();
                    sqlResultSet.next();
                    userName = sqlResultSet.getString( 1 );
                }
            }
            catch ( Exception e )
            {
                System.out.println( "Exception with jdbcTemplate: " + e.getMessage() );
            }

            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, user.getId(), wCellformat ) );
                }
                else if ( incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, user.getId(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 1, rowStart, userName, wCellformat ) );
                    sheet0.addCell( new Label( colStart + 2, rowStart, user.getFirstName(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, user.getSurname(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 4, rowStart, user.getEmail(), wCellformat ) );
                    sheet0.addCell( new Label( colStart + 5, rowStart, user.getPhoneNumber(), wCellformat ) );

                    List<OrganisationUnit> userOrganisationUnitlist = new ArrayList<OrganisationUnit>( user
                        .getOrganisationUnits() );

                    String ouNames = "";
                    for ( OrganisationUnit organisationUnit : userOrganisationUnitlist )
                    {
                        ouNames += organisationUnit.getName() + " , ";
                    }

                    sheet0.addCell( new Label( colStart + 6, rowStart, ouNames, wCellformat ) );

                    String userRoleName = "";

                    String query1 = "select userrole.name from userrole inner join userrolemembers on userrole.userroleid=userrolemembers.userroleid where userrolemembers.userid = "
                        + user.getId();

                    try
                    {
                        SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query1 );

                        if ( sqlResultSet != null )
                        {
                            sqlResultSet.beforeFirst();

                            while ( sqlResultSet.next() )
                            {
                                userRoleName += sqlResultSet.getString( 1 ) + ", ";
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception with jdbcTemplate: " + e.getMessage() );
                    }

                    sheet0.addCell( new Label( colStart + 7, rowStart, userRoleName, wCellformat ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, userName, wCellformat ) );

            rowStart++;
        }// for loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "UserList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

    }// end of generateUserList method

    // -------------------------------------------------------------------------
    // Method for getting DataSetMembers List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateDataSetMemberReport()
        throws Exception
    {
        List<DataSet> datasetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Collections.sort( datasetList, new IdentifiableObjectNameComparator() );

        // List<DataElement> tttt = new ArrayList<DataElement>(
        // dataSet.getDataElements() );
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "DataSetsMember", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        wCellformat.setAlignment( Alignment.LEFT );

        int rowStart = 0;
        int colStart = 0;

        if ( incID != null )
        {
            if ( incID.equalsIgnoreCase( SOURCE ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataSetID", getCellFormat1() ) );
            }
            else if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.addCell( new Label( colStart, rowStart, "DataElementID", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, "DataElementName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, "DataElementAlternativeName", getCellFormat1() ) );
                sheet0
                    .addCell( new Label( colStart + 3, rowStart, "DataElementAggregationOperator", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, "DataElementDescription", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 5, rowStart, "DataElementCode", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 6, rowStart, "DataElementShortName", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 7, rowStart, "DataElementType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 8, rowStart, "DataElementUrl", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 9, rowStart, "DomainType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart, "NumberType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 11, rowStart, "PeriodType", getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 12, rowStart, "LastUpdated", getCellFormat1() ) );
            }
        }
        sheet0.addCell( new Label( colStart + 1, rowStart, "DataSetName", getCellFormat1() ) );

        rowStart++;

        for ( DataSet dataSet : datasetList )
        {
            if ( incID != null )
            {
                if ( incID.equalsIgnoreCase( SOURCE ) || incID.equalsIgnoreCase( PRINT ) )
                {
                    sheet0.addCell( new Number( colStart, rowStart, dataSet.getId(), getCellFormat1() ) );
                }
            }

            sheet0.addCell( new Label( colStart + 1, rowStart, dataSet.getName(), getCellFormat1() ) );

            if ( incID.equalsIgnoreCase( PRINT ) )
            {
                sheet0.mergeCells( colStart + 1, rowStart, colStart + 12, rowStart );
            }

            rowStart++;

            List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );

            Collections.sort( dataElementList, new IdentifiableObjectNameComparator() );

            for ( DataElement dataElement : dataElementList )
            {
                if ( incID != null )
                {
                    if ( incID.equalsIgnoreCase( SOURCE ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, dataElement.getId(), wCellformat ) );
                    }
                    else if ( incID.equalsIgnoreCase( PRINT ) )
                    {
                        sheet0.addCell( new Number( colStart, rowStart, dataElement.getId(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 1, rowStart, dataElement.getName(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 2, rowStart, dataElement.getAlternativeName(),
                            wCellformat ) );
                        sheet0.addCell( new Label( colStart + 3, rowStart, dataElement.getAggregationOperator(),
                            wCellformat ) );
                        sheet0.addCell( new Label( colStart + 4, rowStart, dataElement.getDescription(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 5, rowStart, dataElement.getCode(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 6, rowStart, dataElement.getShortName(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 7, rowStart, dataElement.getType(), wCellformat ) );
                        sheet0.addCell( new Label( colStart + 8, rowStart, dataElement.getUrl(), wCellformat ) );

                        String domainType = new String();
                        if ( dataElement.getDomainType() != null )
                        {
                            domainType = dataElement.getDomainType();
                        }
                        else
                        {
                            domainType = "";
                        }
                        sheet0.addCell( new Label( colStart + 9, rowStart, domainType, wCellformat ) );

                        String numberType = new String();
                        if ( dataElement.getNumberType() != null )
                        {
                            numberType = dataElement.getNumberType();
                        }
                        else
                        {
                            numberType = "";
                        }
                        sheet0.addCell( new Label( colStart + 10, rowStart, numberType, wCellformat ) );

                        String periodType = new String();
                        if ( dataElement.getPeriodType() != null )
                        {
                            periodType = dataElement.getPeriodType().getName();
                        }
                        else
                        {
                            periodType = "";
                        }
                        sheet0.addCell( new Label( colStart + 11, rowStart, periodType, wCellformat ) );

                        String lastUpdate = new String();
                        if ( dataElement.getLastUpdated() != null )
                        {
                            lastUpdate = dataElement.getLastUpdated().toString();
                        }
                        else
                        {
                            lastUpdate = "";
                        }
                        sheet0.addCell( new Label( colStart + 12, rowStart, lastUpdate, wCellformat ) );
                    }
                }

                sheet0.addCell( new Label( colStart + 1, rowStart, dataElement.getName(), wCellformat ) );

                rowStart++;
            }
        }
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "DataSetMemberList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

    }

    // end of DataSetMembers Report method

    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );

        Collections.sort( children, new IdentifiableObjectNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

    // Excel sheet format function
    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        return wCellformat;
    } // end getCellFormat1() function

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.BLACK );
        wCellformat.setWrap( false );
        return wCellformat;
    }

    public WritableCellFormat getCellFormat3()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.WHITE );
        wCellformat.setWrap( false );
        return wCellformat;
    }

    public WritableCellFormat getCellFormat4()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        return wCellformat;
    }

    public WritableCellFormat getCellFormat5()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THICK );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        return wCellformat;
    }

}
