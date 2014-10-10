/**
 * 
 */
package org.hisp.dhis.coldchain.reports.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.reports.CCEMReport;
import org.hisp.dhis.coldchain.reports.CCEMReportDesign;
import org.hisp.dhis.coldchain.reports.CCEMReportManager;
import org.hisp.dhis.coldchain.reports.CCEMReportOutput;
import org.hisp.dhis.coldchain.reports.action.GenerateCCEIReportAction.PieRenderer;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.springframework.jdbc.core.JdbcTemplate;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * 
 * @version GenerateJRXMLReportAction.java Jun 26, 2012 12:12:17 PM
 */
public class GenerateCCEIReportAction implements Action
{
    public Integer NumPag = 0;

    protected JasperPrint jasperPrint;

    protected JasperReport jr;

    protected DynamicReport dr;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CCEMReportManager ccemReportManager;

    public void setCcemReportManager( CCEMReportManager ccemReportManager )
    {
        this.ccemReportManager = ccemReportManager;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private ModelService modelService;

    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }

    private ModelAttributeValueService modelAttributeValueService;

    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }
   
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private List<Integer> selOrgUnitList = new ArrayList<Integer>();

    private String[] option;

    public void setOption( String[] option )
    {
        this.option = option;
    }

    private String[] orgUnit;

    public void setOrgUnit( String[] orgUnit )
    {
        this.orgUnit = orgUnit;
    }

    private List<Integer> orgunitGroupList = new ArrayList<Integer>();

    private CCEMReport ccemReport;

    public CCEMReport getCcemReport()
    {
        return ccemReport;
    }

    private CCEMReportOutput ccemReportOutput;

    public CCEMReportOutput getCcemReportOutput()
    {
        return ccemReportOutput;
    }

    private String periodRadio;

    public void setPeriodRadio( String periodRadio )
    {
        this.periodRadio = periodRadio;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private String pe;

    public void setPe( String pe )
    {
        this.pe = pe;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }
    
    /*
    private String[] ownership;
    
    public void setOwnership( String[] ownership )
    {
        this.ownership = ownership;
    }
    
    private String[] coldstore;
    
    public void setColdstore( String[] coldstore )
    {
        this.coldstore = coldstore;
    }
    
    
    public String facilityTypeRadio = "";
    
    public void setFacilityTypeRadio( String facilityTypeRadio )
    {
        this.facilityTypeRadio = facilityTypeRadio;
    }
    */
    
    private Integer organisationUnitGroupSets;
    
    public void setOrganisationUnitGroupSets( Integer organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------


    @Override
    public String execute() throws Exception, JRException
    {
        /*
        if ( facilityTypeRadio.equalsIgnoreCase( "facilityType" ) )
        {
            if ( option[0].isEmpty() )
            {
            }
            else
            {
                for ( int i = 0; i <= option.length - 1; i++ )
                {
                    orgunitGroupList.add( Integer.parseInt( option[i].trim() ) );
                }
            }
            
        }
        
        if ( facilityTypeRadio.equalsIgnoreCase( "ownershipType" ) )
        {
            if ( ownership[0].isEmpty() )
            {
            }
            else
            {
                for ( int i = 0; i <= ownership.length - 1; i++ )
                {
                    orgunitGroupList.add( Integer.parseInt( ownership[i].trim() ) );
                }
            }        
            
        }

        if ( facilityTypeRadio.equalsIgnoreCase( "coldStoreType" ) )
        {
            if ( coldstore[0].isEmpty() )
            {
            }
            else
            {
                for ( int i = 0; i <= coldstore.length - 1; i++ )
                {
                    orgunitGroupList.add( Integer.parseInt( coldstore[i].trim() ) );
                }
            }        
            
        }        
        */
        if( organisationUnitGroupSets != null )
        {
            if ( option[0].isEmpty() )
            {
            }
            else
            {
                for ( int i = 0; i <= option.length - 1; i++ )
                {
                    orgunitGroupList.add( Integer.parseInt( option[i].trim() ) );
                }
            }
            
        }
            
        
        //System.out.println( " Size of  orgunitGroup List  " + orgunitGroupList.size() );
        
        if ( orgUnit[0].isEmpty() )
        {
        }
        else
        {
            for ( int i = 0; i <= orgUnit.length - 1; i++ )
            {
                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(  orgUnit[i].trim()  );
                selOrgUnitList.add( organisationUnit.getId() );
                //selOrgUnitList.add( Integer.parseInt( orgUnit[i].trim() ) );
            }
        }

        Connection con = jdbcTemplate.getDataSource().getConnection();
        String fileName = null;
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "ccei" + File.separator + "ireports" + File.separator;

        HashMap<String, Object> hash = new HashMap<String, Object>();

        String orgUnitIdsByComma = ccemReportManager.getOrgunitIdsByComma( selOrgUnitList, orgunitGroupList );
        
        //System.out.println( " orgUnitIdsByComma  " + orgUnitIdsByComma );
        
        ccemReport = ccemReportManager.getCCEMReportByReportId( reportList );
        Map<String, String> ccemSettingsMap = new HashMap<String, String>( ccemReportManager.getCCEMSettings() );
        List<CCEMReportDesign> reportDesignList = new ArrayList<CCEMReportDesign>( ccemReportManager.getCCEMReportDesign( ccemReport.getXmlTemplateName() ) );

        String oName = null;
        String oUnitGrpName = null;
        
        oUnitGrpName = organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( 0 ) ).getName() + "";
        
        for ( int i = 1; i <= orgunitGroupList.size() - 1; i++ )
        {
            oUnitGrpName += "," + organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( i ) ).getName();
        }
        
        oName = organisationUnitService.getOrganisationUnit( selOrgUnitList.get( 0 ) ).getName() + "";
        
        for ( int j = 1; j <= selOrgUnitList.size() - 1; j++ )
        {
            oName += "," + organisationUnitService.getOrganisationUnit( selOrgUnitList.get( j ) ).getName();
        }
        hash.put( "orgunitGroup", oUnitGrpName );
        hash.put( "selOrgUnit", oName );
        hash.put( "orgUnitIdsByComma", orgUnitIdsByComma );
        HttpServletResponse response = ServletActionContext.getResponse();

        ccemReport = ccemReportManager.getCCEMReportByReportId( reportList );
        Date date = pe != null ? DateUtils.getMediumDate( pe ) : new Date();

        hash.put( "reportName", ccemReport.getReportName() );
        hash.put( "date", date );

        Date date2 = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date2 );
        String periodStartDate = "";
        String periodEndDate = "";
        String periodIdsByComma = "";
        List<Period> periodList = null;
        Date sDate = null;
        Date eDate = null;

        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        PeriodType periodType = periodService.getPeriodTypeByName( ccemReport.getPeriodRequire() );
        if ( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
        {
            periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
            periodEndDate = calendar.get( Calendar.YEAR ) + "-12-31";
            sDate = format.parseDate( periodStartDate );
            eDate = format.parseDate( periodEndDate );
        }
        else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
        {
            periodStartDate = (calendar.get( Calendar.YEAR ) - 1) + "-01-01";
            periodEndDate = (calendar.get( Calendar.YEAR ) - 1) + "-12-31";
            sDate = format.parseDate( periodStartDate );
            eDate = format.parseDate( periodEndDate );
        }
        else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_6_MONTHS ) )
        {
            calendar.add( Calendar.MONTH, -1 );
            calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
            eDate = calendar.getTime();

            calendar.add( Calendar.MONTH, -5 );
            calendar.set( Calendar.DATE, 1 );
            sDate = calendar.getTime();
        }
        else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_3_MONTHS ) )
        {
            calendar.add( Calendar.MONTH, -1 );
            calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
            eDate = calendar.getTime();

            calendar.add( Calendar.MONTH, -2 );
            calendar.set( Calendar.DATE, 1 );
            sDate = calendar.getTime();
        }

        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sDate, eDate ) );
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
        periodIdsByComma = getCommaDelimitedString( periodIds );

        // Facility Type

        if ( ccemReport.getReportType().equals( CCEMReport.TOTAL_POPULATION ) )
        {
            hash.put( "reportName", "Total population by facility type" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            
            Integer dataElementId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer optionComboId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();
            frb.addColumn( "Admin Area", "Admin Area", String.class.getName(), 70, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 120, true );
            frb.addColumn( "No. Facilities", "No. Facilities", String.class.getName(), 70, true );
            frb.addColumn( "Minimum", "Minimum", String.class.getName(), 80, true );
            frb.addColumn( "Maximum", "Maximum", String.class.getName(), 80, true );
            frb.addColumn( "Mean", "Mean", String.class.getName(), 80, true );

            try
            {
                for ( Integer orgUnitId : selOrgUnitList )
                {
                	OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
                	List<OrganisationUnit> orgUnitChildren = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
                	
                    for ( Integer orgUnitGroupId : orgunitGroupList )
                    {
                        Map<String, String> numberOfData = new HashMap<String, String>();
                        OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                        List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                        orgUnitGroupMembers.retainAll( orgUnitChildren );

                        if( orgUnitGroupMembers != null && orgUnitGroupMembers.size() > 0 )
                        {
                            Collection<Integer> orgUnitGroupMemberIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitGroupMembers ) );
                            String orgUnitidsByComma = getCommaDelimitedString( orgUnitGroupMemberIds );

                        	String minMaxandAvgValue = ccemReportManager.getMinMaxAvgValues( orgUnitidsByComma, periodIdsByComma, dataElementId, optionComboId );

	                        numberOfData.put( "Admin Area", orgUnit.getName() );
	                        numberOfData.put( "Facility Type", orgUnitGroup.getName() );
	                        numberOfData.put( "No. Facilities", orgUnitGroupMembers.size() + "" );
	                        numberOfData.put( "Minimum", minMaxandAvgValue.split( "," )[0] );
	                        numberOfData.put( "Maximum", minMaxandAvgValue.split( "," )[1] );
	                        numberOfData.put( "Mean", minMaxandAvgValue.split( "," )[2] );
	                        tableData.add( numberOfData );
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                System.out.print( "Exception : " + e );
                e.printStackTrace();
            }
            
            frb.setHeaderHeight( 35 );
            frb.setPrintColumnNames( true );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Total Population By Facility.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );

        }
        else if ( ccemReport.getReportType().equals( CCEMReport.LIVE_BIRTHS ) )
        {
            hash.put( "reportName", "Live births by facility type" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            
            Integer dataElementId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer optionComboId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();
            frb.addColumn( "Admin Area", "Admin Area", String.class.getName(), 100, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 80, true );
            frb.addColumn( "No. Facilities", "No. Facilities", String.class.getName(), 70, true );
            frb.addColumn( "Minimum", "Minimum", String.class.getName(), 50, true );
            frb.addColumn( "Maximum", "Maximum", String.class.getName(), 50, true );
            frb.addColumn( "Mean", "Mean", String.class.getName(), 50, true );

            try
            {
                Constant constant = constantService.getConstantByName( "LiveBirthsPerThousand" );                    
                Double liveBirthPerThousand = 40.0;
                if( constant != null )
                {
                    liveBirthPerThousand = constant.getValue();
                }
                
                liveBirthPerThousand /= 1000;
                
                for ( Integer orgUnitId : selOrgUnitList )
                {
                    OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
                    List<OrganisationUnit> orgUnitChildren = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );

                    for ( Integer orgUnitGroupId : orgunitGroupList )
                    {
                        Map<String, String> numberOfData = new HashMap<String, String>();
                        OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                        List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                        orgUnitGroupMembers.retainAll( orgUnitChildren );
                        
                        

                        if( orgUnitGroupMembers != null && orgUnitGroupMembers.size() > 0 )
                        {
                            Collection<Integer> orgUnitGroupMemberIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitGroupMembers ) );
                            String orgUnitidsByComma = getCommaDelimitedString( orgUnitGroupMemberIds );

                            //String minMaxandAvgValue = ccemReportManager.getMinMaxAvgValues( orgUnitidsByComma, periodIdsByComma, dataElementId, optionComboId );
                            String minMaxandAvgValue = ccemReportManager.getMinMaxAvgValuesForLiveBirths( orgUnitidsByComma, periodIdsByComma, dataElementId, optionComboId, liveBirthPerThousand );

                            numberOfData.put( "Admin Area", orgUnit.getName() );
                            numberOfData.put( "Facility Type", orgUnitGroup.getName() );
                            numberOfData.put( "No. Facilities", orgUnitGroupMembers.size() + "" );
                            numberOfData.put( "Minimum", minMaxandAvgValue.split( "," )[0] );
                            numberOfData.put( "Maximum", minMaxandAvgValue.split( "," )[1] );
                            numberOfData.put( "Mean", minMaxandAvgValue.split( "," )[2] );
                            tableData.add( numberOfData );
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
            
            frb.setHeaderHeight( 35 );
            frb.setPrintColumnNames( true );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Total Population By Facility.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );

        }
        else if ( ccemReport.getReportType().equals( CCEMReport.MODE_OF_VACCINE ) )
        {
            hash.put( "reportName", "Mode of vaccine supply by facility type" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer dataelementId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            Map<String, Integer> dataValueMap = ccemReportManager.getDataValueAndCount( dataelementId + "", orgUnitIdsByComma, periodIdsByComma );
            DefaultPieDataset dataset = new DefaultPieDataset();
            for ( String dataValue : dataValueMap.keySet() )
            {
                dataset.setValue( dataValue, dataValueMap.get( dataValue ) );
            }
            
            hash.put( "chart2", createPieChart( dataset, "Mode of vaccine supply by facility type" ) );
            fileName = "Pie_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }

        else if ( ccemReport.getReportType().equals( CCEMReport.ELECTRICITY_AVAILABILITY_BY_FACILITY_TYPE ) )
        {
            hash.put( "reportName", "Electricity availibility by facility type" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer dataelementId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            Map<String, Integer> dataValueMap = ccemReportManager.getDataValueAndCount( dataelementId + "", orgUnitIdsByComma, periodIdsByComma );

            DefaultPieDataset dataset = new DefaultPieDataset();
            for ( String dataValue : dataValueMap.keySet() )
            {
                dataset.setValue( dataValue, dataValueMap.get( dataValue ) );
            }
            hash.put( "chart2", createPieChart( dataset, "Electricity availibility by facility type" ) );
            fileName = "Pie_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.KEROSENE_AVAILABILITY_BY_FACILITY_TYPE ) )
        {
            hash.put( "reportName", "Kerosene Availability by facility type" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer dataelementId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            Map<String, Integer> dataValueMap = ccemReportManager.getDataValueAndCount( dataelementId + "",
                orgUnitIdsByComma, periodIdsByComma );

            DefaultPieDataset dataset = new DefaultPieDataset();
            for ( String dataValue : dataValueMap.keySet() )
            {
                dataset.setValue( dataValue, dataValueMap.get( dataValue ) );
            }
            hash.put( "chart2", createPieChart( dataset, "Kerosene Availability by facility type" ) );
            fileName = "Pie_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.GAS_AVAILABILITY_BY_FACILITY_TYPE ) )
        {
            hash.put( "reportName", "Gas Availability by facility type" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer dataelementId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            Map<String, Integer> dataValueMap = ccemReportManager.getDataValueAndCount( dataelementId + "",
                orgUnitIdsByComma, periodIdsByComma );

            DefaultPieDataset dataset = new DefaultPieDataset();
            for ( String dataValue : dataValueMap.keySet() )
            {
                dataset.setValue( dataValue, dataValueMap.get( dataValue ) );
            }
            hash.put( "chart2", createPieChart( dataset, "Gas Availability by facility type" ) );
            fileName = "Pie_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.ENERGY_AVAILABILITY_AT_FACILITIES ) )
        {
            hash.put( "reportName", "Energy Availability at Facilities" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );

            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );

            String[] ccemCellContentParts = ccemCellContent.split( "--" );
            Integer dataelementId1 = Integer.parseInt( ccemCellContentParts[0].split( ":" )[0] );
            Integer dataelementId2 = Integer.parseInt( ccemCellContentParts[0].split( ":" )[1] );
            Integer dataelementId3 = Integer.parseInt( ccemCellContentParts[0].split( ":" )[2] );

            String electricityLessThan8hrsOption = ccemCellContentParts[1].split( "," )[0];
            String electricity8to16hrsOption = ccemCellContentParts[1].split( "," )[1];
            String electricityGreaterThan16hrsOption = ccemCellContentParts[1].split( "," )[2];
            String gasAvailableOption = ccemCellContentParts[1].split( "," )[3];
            String keroseneReliableOption = ccemCellContentParts[1].split( "," )[4];

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for ( Integer orgUnit : selOrgUnitList )
            {
                List<OrganisationUnit> orgUnitChildren = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit ) );
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                    orgUnitGroupMembers.retainAll( orgUnitChildren );
                    Collection<Integer> orgUnitGroupMemberIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitGroupMembers ) );
                    String orgUnitidsByComma = getCommaDelimitedString( orgUnitGroupMemberIds );

                    if ( orgUnitGroupMemberIds == null || orgUnitGroupMemberIds.size() <= 0 )
                    {
                        dataset.setValue( 0, "Electricity <8 Hrs", orgUnitGroup.getName() );
                        dataset.setValue( 0, "Electricity 8/16 Hrs", orgUnitGroup.getName() );
                        dataset.setValue( 0, "Electricity >16 Hrs", orgUnitGroup.getName() );
                        dataset.setValue( 0, "Gas Availabile", orgUnitGroup.getName() );
                        dataset.setValue( 0, "Kerosene, Reliable", orgUnitGroup.getName() );
                    }
                    else
                    {
                        dataset.setValue( ccemReportManager.getDataValue( dataelementId1 + "", electricityLessThan8hrsOption, orgUnitidsByComma, periodIdsByComma ), "Electricity <8 Hrs", orgUnitGroup.getName() );
                        dataset.setValue( ccemReportManager.getDataValue( dataelementId1 + "", electricity8to16hrsOption, orgUnitidsByComma, periodIdsByComma ), "Electricity 8/16 Hrs", orgUnitGroup.getName() );
                        dataset.setValue( ccemReportManager.getDataValue( dataelementId1 + "", electricityGreaterThan16hrsOption, orgUnitidsByComma, periodIdsByComma ), "Electricity >16 Hrs", orgUnitGroup.getName() );
                        dataset.setValue( ccemReportManager.getDataValue( dataelementId3 + "", gasAvailableOption, orgUnitidsByComma, periodIdsByComma ), "Gas Availabile", orgUnitGroup.getName() );
                        dataset.setValue( ccemReportManager.getDataValue( dataelementId2 + "", keroseneReliableOption, orgUnitidsByComma, periodIdsByComma ), "Kerosene, Reliable", orgUnitGroup.getName() );
                    }
                }
            }

            hash.put( "chart", createSimpleBarChart( dataset, "Energy Availability at Facilities", "", "Number of Facilities", PlotOrientation.HORIZONTAL ) );
            fileName = "Bar_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.ELECTRICITY_AVAILABILITY ) )
        {
            hash.put( "reportName", "Electricity Availability" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer dataelementId = Integer.parseInt( ccemCellContent.split( ":" )[0] );

            DataElement dataElemnet = dataElementService.getDataElement( dataelementId );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 8 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();
            frb.addColumn( "Admin Area", "Admin Area", String.class.getName(), 60, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 70, true );
            frb.addColumn( "Total Facilities", "Total Facilities", String.class.getName(), 50, true );

            if ( dataElemnet != null && dataElemnet.getOptionSet() != null )
            {
                List<String> options = new ArrayList<String>( dataElemnet.getOptionSet().getOptions() );
                for ( String option : options )
                {
                    frb.addColumn( option, dataElemnet.getId()+"_"+option, String.class.getName(), 50, true );
                    frb.addColumn( "%", dataElemnet.getId()+"_"+option + "_%", String.class.getName(), 30, true );
                }

                frb.setColspan( 3, options.size() * 2, "Availability of Electricity" );
            }

            for ( Integer orgUnit : selOrgUnitList )
            {
                List<OrganisationUnit> orgUnitChildren = new ArrayList<OrganisationUnit>( organisationUnitService
                    .getOrganisationUnitWithChildren( orgUnit ) );
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    // List<Integer> orgUnitId = new ArrayList<Integer>();
                    // List<Integer> orgUnitGrpId = new ArrayList<Integer>();

                    // orgUnitId.add( orgUnit );
                    // orgUnitGrpId.add( orgUnitGroupId );

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup
                        .getMembers() );
                    orgUnitGroupMembers.retainAll( orgUnitChildren );
                    Collection<Integer> orgUnitGroupMemberIds = new ArrayList<Integer>( getIdentifiers(
                        OrganisationUnit.class, orgUnitGroupMembers ) );
                    String orgUnitidsByComma = getCommaDelimitedString( orgUnitGroupMemberIds );
                    Integer totalFacilities = orgUnitGroupMembers.size();

                    if ( totalFacilities != 0 )
                    {
                        Map<String, String> numberOfData = new HashMap<String, String>();

                        Map<String, Integer> dataValueCountMap = new HashMap<String, Integer>( ccemReportManager
                            .getDataValueAndCount( dataelementId + "", orgUnitidsByComma, periodIdsByComma ) );

                        if ( dataElemnet != null && dataElemnet.getOptionSet() != null )
                        {
                            List<String> options = new ArrayList<String>( dataElemnet.getOptionSet().getOptions() );
                            for ( String option : options )
                            {
                                Integer optionValueCount = dataValueCountMap.get( option );
                                if ( optionValueCount == null )
                                {
                                    optionValueCount = 0;
                                }

                                numberOfData.put( dataElemnet.getId()+"_"+option, optionValueCount + "" );
                                double percentageOfOptionValueCount = 0.0;
                                try
                                {
                                    percentageOfOptionValueCount = (double) optionValueCount / (double) totalFacilities
                                        * 100.0;
                                    percentageOfOptionValueCount = Math.round( percentageOfOptionValueCount
                                        * Math.pow( 10, 2 ) )
                                        / Math.pow( 10, 2 );
                                }
                                catch ( Exception e )
                                {
                                    percentageOfOptionValueCount = 0.0;
                                }
                                numberOfData.put( dataElemnet.getId()+"_"+option + "_%", percentageOfOptionValueCount + "" );
                                // System.out.println( option + " : " +
                                // optionValueCount + " -- " +
                                // percentageOfOptionValueCount );
                            }

                        }

                        /*
                         * Double none = Double.valueOf(
                         * ccemReportManager.getDataValue( dataelementId1 + "",
                         * "none", orgUnitidsByComma,null ) + "" ); none =
                         * Math.round( none * Math.pow( 10, 1 ) ) / Math.pow(
                         * 10, 1 );
                         * 
                         * Double lessThan8 = Double.valueOf(
                         * ccemReportManager.getDataValue( dataelementId1 + "",
                         * "<8hrs/8 to 16hrs/24hrs4hrs", orgUnitidsByComma,null
                         * ) + "" ); lessThan8 = Math.round( lessThan8 *
                         * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                         * 
                         * Double elc8to16 = Double.valueOf(
                         * ccemReportManager.getDataValue( dataelementId1 + "",
                         * "8 hrs to 16 hrs", orgUnitidsByComma,null ) + "" );
                         * elc8to16 = Math.round( elc8to16 * Math.pow( 10, 1 ) )
                         * / Math.pow( 10, 1 );
                         * 
                         * Double greaterThan16 = Double.valueOf(
                         * ccemReportManager.getDataValue( dataelementId1 + "",
                         * "More than 16hrs/24hrs", orgUnitidsByComma,null ) +
                         * "" ); greaterThan16 = Math.round( greaterThan16 *
                         * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                         * 
                         * Double pernone = none / total * 100; pernone =
                         * Math.round( pernone * Math.pow( 10, 2 ) ) / Math.pow(
                         * 10, 2 );
                         * 
                         * Double perLessThan8 = lessThan8 / total * 100;
                         * perLessThan8 = Math.round( perLessThan8 * Math.pow(
                         * 10, 2 ) ) / Math.pow( 10, 2 );
                         * 
                         * Double per8to16 = elc8to16 / total * 100; per8to16 =
                         * Math.round( per8to16 * Math.pow( 10, 2 ) ) /
                         * Math.pow( 10, 2 );
                         * 
                         * Double perGreaterThan16 = greaterThan16 / total *
                         * 100; perGreaterThan16 = Math.round( perGreaterThan16
                         * * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                         */

                        numberOfData.put( "Admin Area", organisationUnitService.getOrganisationUnit( orgUnit )
                            .getName() );
                        numberOfData.put( "Facility Type", orgUnitGroup.getName() );
                        numberOfData.put( "Total Facilities", totalFacilities + "" );
                        /*
                         * numberOfData.put( "None", none + "" );
                         * numberOfData.put( "None_%", pernone + "" );
                         * numberOfData.put( "< 8 hours", lessThan8 + "" );
                         * numberOfData.put( "<_8_hours_%", perLessThan8 + "" );
                         * numberOfData.put( "8 to 16 hours", elc8to16 + "" );
                         * numberOfData.put( "8_to_16_hours_%", per8to16 + "" );
                         * numberOfData.put( "> 16 hours", greaterThan16 + "" );
                         * numberOfData.put( ">_16_hours_%", perGreaterThan16 +
                         * "" );
                         */
                        tableData.add( numberOfData );
                    }

                }
            }
            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Total Population By Facility.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }

        // Cold Chain

        else if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE ) )
        {
            hash.put( "reportName", "Refrigerators/freezer by Type" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            Map<String, Integer> ccemResultMap = ccemReportManager.getCatalogTypeAttributeValue( orgUnitIdsByComma,
                equipmentTypeId, modelTypeAttributeId );

            DefaultPieDataset dataset = new DefaultPieDataset();
            for ( String dataValue : ccemResultMap.keySet() )
            {
                dataset.setValue( dataValue, ccemResultMap.get( dataValue ) );
            }
            hash.put( "chart2", createPieChart( dataset, "Refrigerators/freezer by Type" ) );
            fileName = "Pie_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );

            /*
             * for ( String catalog : ccemResultMap.keySet() ) { totalValue =
             * totalValue + ccemResultMap.get( catalog ); } hash.put(
             * "totalValue", totalValue ); hash.put( "inventoryTypeId",
             * inventoryTypeId ); hash.put( "catalogTypeAttributeId",
             * catalogTypeAttributeId ); fileName =
             * "Refrigerators_freezer_by_type.jrxml"; JasperReport jasperReport
             * = JasperCompileManager.compileReport( path + fileName );
             * jasperPrint = JasperFillManager.fillReport( jasperReport, hash,
             * con );
             */
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE_BY_WORKING_STATUS ) )
        {
            hash.put( "reportName", "Refrigerators/freezers by working status, facility type and area" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId  = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 8 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();
            frb.addColumn( "Area", "Area", String.class.getName(), 60, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 50, true );
            frb.addColumn( "Equipment Type", "Equipment Type", String.class.getName(), 50, true );
            frb.addColumn( "Total Refs/Freezers", "Total Refs/Freezers", String.class.getName(), 50, true );
            frb.addColumn( "#", "Working_#", String.class.getName(), 30, true );
            frb.addColumn( "%", "Working_%", String.class.getName(), 30, true );
            frb.addColumn( "#", "Repair_#", String.class.getName(), 30, true );
            frb.addColumn( "%", "Repair_%", String.class.getName(), 30, true );
            frb.addColumn( "#", "notWorking_#", String.class.getName(), 30, true );
            frb.addColumn( "%", "notWorking_%", String.class.getName(), 30, true );
            frb.setColspan( 4, 2, "Working" );
            frb.setColspan( 6, 2, "Working Needs Service" );
            frb.setColspan( 8, 2, "Not Working" );

            for ( Integer orgUnit : selOrgUnitList )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    List<Integer> orgUnitId = new ArrayList<Integer>();
                    List<Integer> orgUnitGrpId = new ArrayList<Integer>();

                    orgUnitId.add( orgUnit );
                    orgUnitGrpId.add( orgUnitGroupId );

                    String orgUnitidsByComma = ccemReportManager.getOrgunitIdsByComma( orgUnitId, orgUnitGrpId );
                    
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

                    Map<String, Integer> totalEquipmentMap = ccemReportManager.getModelNameAndCount( modelTypeAttributeId, equipmentTypeId, null, orgUnitidsByComma );
                    Map<String, Integer> workingEquipmentMap = ccemReportManager.getModelNameAndCount( modelTypeAttributeId, equipmentTypeId, EquipmentStatus.STATUS_WORKING_WELL, orgUnitidsByComma );
                    Map<String, Integer> repairEquipmentMap = ccemReportManager.getModelNameAndCount( modelTypeAttributeId, equipmentTypeId, EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE, orgUnitidsByComma );
                    Map<String, Integer> notWorkingEquipmentMap = ccemReportManager.getModelNameAndCount( modelTypeAttributeId, equipmentTypeId, EquipmentStatus.STATUS_NOT_WORKING, orgUnitidsByComma );

                    List<String> modelsList = ccemReportManager.getModelName( equipmentTypeId, modelTypeAttributeId, orgUnitidsByComma );
                    for ( String model : modelsList )
                    {
                        Double total = 0.0;
                        Double working = 0.0;
                        Double repair = 0.0;
                        Double notWorking = 0.0;
                        if ( totalEquipmentMap.containsKey( model ) )
                        {
                            total = Double.valueOf( totalEquipmentMap.get( model ) );
                            total = Math.round( total * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        }

                        if ( workingEquipmentMap.containsKey( model ) )
                        {
                            working = Double.valueOf( workingEquipmentMap.get( model ) );
                            working = Math.round( working * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        }

                        if ( repairEquipmentMap.containsKey( model ) )
                        {
                            repair = Double.valueOf( repairEquipmentMap.get( model ) );
                            repair = Math.round( repair * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        }
                        if ( notWorkingEquipmentMap.containsKey( model ) )
                        {
                            notWorking = Double.valueOf( notWorkingEquipmentMap.get( model ) );
                            notWorking = Math.round( notWorking * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        }

                        Double perWorking = working / total * 100;
                        perWorking = Math.round( perWorking * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                        Double perRepair = repair / total * 100;
                        perRepair = Math.round( perRepair * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                        Double perNotWorking = notWorking / total * 100;
                        perNotWorking = Math.round( perNotWorking * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                        Map<String, String> numberOfData = new HashMap<String, String>();
                        numberOfData.put( "Area", organisationUnitService.getOrganisationUnit( orgUnit ).getName() );
                        numberOfData.put( "Facility Type", orgUnitGroup.getName() );
                        numberOfData.put( "Equipment Type", model );
                        numberOfData.put( "Total Refs/Freezers", total + "" );
                        numberOfData.put( "Working_#", working + "" );
                        numberOfData.put( "Working_%", perWorking + "" );
                        numberOfData.put( "Repair_#", repair + "" );
                        numberOfData.put( "Repair_%", perRepair + "" );
                        numberOfData.put( "notWorking_#", notWorking + "" );
                        numberOfData.put( "notWorking_%", perNotWorking + "" );
                        tableData.add( numberOfData );

                    }
                }
            }
            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 46 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Total Population By Facility.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.WORKING_STATUS_BY_REFRIGERATORS_FREEZER_MODEL ) )
        {
            hash.put( "reportName", "WORKING_STATUS_BY_REFRIGERATORS_FREEZER_MODEL" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            Map<String, Integer> workingEquipmentMap = ccemReportManager.getModelNameAndCount( catalogTypeAttributeId, inventoryTypeId, EquipmentStatus.STATUS_WORKING_WELL, orgUnitIdsByComma );
            Map<String, Integer> repairEquipmentMap = ccemReportManager.getModelNameAndCount( catalogTypeAttributeId, inventoryTypeId, EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE, orgUnitIdsByComma );
            Map<String, Integer> notWorkingEquipmentMap = ccemReportManager.getModelNameAndCount( catalogTypeAttributeId, inventoryTypeId, EquipmentStatus.STATUS_NOT_WORKING, orgUnitIdsByComma );

            List<String> modelsList = ccemReportManager.getModelName( inventoryTypeId, catalogTypeAttributeId, orgUnitIdsByComma );

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for ( String model : modelsList )
            {
                if ( workingEquipmentMap.containsKey( model ) )
                {
                    dataset.setValue( workingEquipmentMap.get( model ), "Working", model );
                }
                else
                {
                    dataset.setValue( 0, "Working", model );
                }
                
                if ( repairEquipmentMap.containsKey( model ) )
                {
                    dataset.setValue( repairEquipmentMap.get( model ), "Working Need Maintence", model );
                }
                else
                {
                    dataset.setValue( 0, "Working Need Maintence", model );
                }
                
                if ( notWorkingEquipmentMap.containsKey( model ) )
                {
                    dataset.setValue( notWorkingEquipmentMap.get( model ), "Not Working", model );
                }
                else
                {
                    dataset.setValue( 0, "Not Working", model );
                }
            }

            hash.put( "chart", createBarChart( dataset, "WORKING_STATUS_BY_REFRIGERATORS_FREEZER_MODEL", "", "Number of Equipment", PlotOrientation.VERTICAL ) );
            fileName = "Bar_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.REFRIGERATORS_FREEZER_BY_WORKING_STATUS ) )
        {
            hash.put( "reportName", "REFRIGERATORS_FREEZER_BY_WORKING_STATUS" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();
            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Model Name", "Model Name", String.class.getName(), 70, true );
            frb.addColumn( "Total ", "Total", String.class.getName(), 80, true );
            frb.addColumn( "#", "Working_#", String.class.getName(), 50, true );
            frb.addColumn( "%", "Working_%", String.class.getName(), 50, true );
            frb.addColumn( "#", "Repair_#", String.class.getName(), 50, true );
            frb.addColumn( "%", "Repair_%", String.class.getName(), 50, true );
            frb.addColumn( "#", "notWorking_#", String.class.getName(), 50, true );
            frb.addColumn( "%", "notWorking_%", String.class.getName(), 50, true );
            frb.setColspan( 2, 2, "Working" );
            frb.setColspan( 4, 2, "Working Needs Service" );
            frb.setColspan( 6, 2, "Not Working" );

            Map<String, Integer> totalEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId, inventoryTypeId, null, orgUnitIdsByComma ));
            Map<String, Integer> workingEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId, inventoryTypeId, EquipmentStatus.STATUS_WORKING_WELL, orgUnitIdsByComma ));
            Map<String, Integer> repairEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId, inventoryTypeId, EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE, orgUnitIdsByComma ));
            Map<String, Integer> notWorkingEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId, inventoryTypeId, EquipmentStatus.STATUS_NOT_WORKING, orgUnitIdsByComma ));

            List<String> modelsList = ccemReportManager.getModelName( inventoryTypeId, catalogTypeAttributeId, orgUnitIdsByComma );
            for ( String model : modelsList )
            {
                Double total = 0.0;
                Double working = 0.0;
                Double repair = 0.0;
                Double notWorking = 0.0;
                if ( totalEquipmentMap.containsKey( model ) )
                {
                    total = Double.valueOf( totalEquipmentMap.get( model ) );
                    total = Math.round( total * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                }

                if ( workingEquipmentMap.containsKey( model ) )
                {
                    working = Double.valueOf( workingEquipmentMap.get( model ) );
                    working = Math.round( working * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                }

                if ( repairEquipmentMap.containsKey( model ) )
                {
                    repair = Double.valueOf( repairEquipmentMap.get( model ) );
                    repair = Math.round( repair * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                }
                if ( notWorkingEquipmentMap.containsKey( model ) )
                {
                    notWorking = Double.valueOf( notWorkingEquipmentMap.get( model ) );
                    notWorking = Math.round( notWorking * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                }

                Double perWorking = working / total * 100;
                perWorking = Math.round( perWorking * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                Double perRepair = repair / total * 100;
                perRepair = Math.round( perRepair * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                Double perNotWorking = notWorking / total * 100;
                perNotWorking = Math.round( perNotWorking * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                Map<String, String> numberOfData = new HashMap<String, String>();
                numberOfData.put( "Model Name", model );
                numberOfData.put( "Total", total + "" );
                numberOfData.put( "Working_#", working + "" );
                numberOfData.put( "Working_%", perWorking + "" );
                numberOfData.put( "Repair_#", repair + "" );
                numberOfData.put( "Repair_%", perRepair + "" );
                numberOfData.put( "notWorking_#", notWorking + "" );
                numberOfData.put( "notWorking_%", perNotWorking + "" );
                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 40 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Total Population By Facility.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.REFRIGERATORS_BY_WORKING_STATUS ) )
        {
            hash.put( "reportName", "REFRIGERATORS_BY_WORKING_STATUS" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            Map<String, Integer> workingEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId,
                inventoryTypeId, EquipmentStatus.STATUS_WORKING_WELL, orgUnitIdsByComma ));
            Map<String, Integer> repairEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId,
                inventoryTypeId, EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE, orgUnitIdsByComma ));
            Map<String, Integer> notWorkingEquipmentMap = (ccemReportManager.getModelNameAndCount(
                catalogTypeAttributeId, inventoryTypeId, EquipmentStatus.STATUS_NOT_WORKING, orgUnitIdsByComma ));

            List<String> modelsList = ccemReportManager.getModelName( inventoryTypeId, catalogTypeAttributeId,
                orgUnitIdsByComma );

            DefaultPieDataset dataset = new DefaultPieDataset();

            Integer workingInt = 0;
            Integer repairInt = 0;
            Integer notworkingInt = 0;
            for ( String model : modelsList )
            {
                if ( workingEquipmentMap.containsKey( model ) )
                {
                    workingInt += workingEquipmentMap.get( model );
                }
                if ( repairEquipmentMap.containsKey( model ) )
                {
                    repairInt += repairEquipmentMap.get( model );
                }
                if ( notWorkingEquipmentMap.containsKey( model ) )
                {
                    notworkingInt += notWorkingEquipmentMap.get( model );
                }
            }

            dataset.setValue( "Working", workingInt );
            dataset.setValue( "Working Needs Service", repairInt );
            dataset.setValue( "Not Working", notworkingInt );

            hash.put( "chart2", createPieChart( dataset, "REFRIGERATORS_BY_WORKING_STATUS" ) );
            fileName = "Pie_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP_PIE ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId  = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId  = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            hash.put( "equipmentTypeId ", equipmentTypeId  );
            hash.put( "modelTypeAttributeId ", modelTypeAttributeId  );
            int i = 0;
            Integer equipmentTypeAttributeId = 3;
            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                i++;
                if ( i == 1 )
                    continue;
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                if ( ccemCellContent1.split( ":" )[3].equalsIgnoreCase( "UNKNOWN" ) )
                {

                }
                else if ( ccemCellContent1.split( ":" )[4].equalsIgnoreCase( "MORE" ) )
                {
                    equipmentTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    modelTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    equipmentTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
                else
                {
                    equipmentTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    modelTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    equipmentTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
            }
            Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 0, 2 ) );

            Map<String, Integer> catalogTypeAttributeValueMap2 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 3, 5 ) );

            Map<String, Integer> catalogTypeAttributeValueMap3 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 6, 10 ) );

            Map<String, Integer> catalogTypeAttributeValueMap4 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId, 3, 11,
                    -1 ) );

            hash.put( "Value_0_2", catalogTypeAttributeValueMap1 );
            hash.put( "Value_3_5", catalogTypeAttributeValueMap2 );
            hash.put( "Value_6_10", catalogTypeAttributeValueMap3 );
            hash.put( "Value_11_MORE", catalogTypeAttributeValueMap4 );

            DefaultPieDataset dataset = new DefaultPieDataset();
            Integer age_0_5 = 0;
            Integer age_6_10 = 0;
            Integer age_more_10 = 0;
            for ( String catalog : catalogTypeAttributeValueMap1.keySet() )
            {
                age_0_5 = age_0_5 + catalogTypeAttributeValueMap1.get( catalog );
            }
            for ( String catalog2 : catalogTypeAttributeValueMap2.keySet() )
            {
                age_0_5 = age_0_5 + catalogTypeAttributeValueMap2.get( catalog2 );
            }
            for ( String catalog3 : catalogTypeAttributeValueMap3.keySet() )
            {
                age_6_10 = age_6_10 + catalogTypeAttributeValueMap3.get( catalog3 );
            }
            for ( String catalog4 : catalogTypeAttributeValueMap4.keySet() )
            {
                age_more_10 = age_more_10 + catalogTypeAttributeValueMap4.get( catalog4 );
            }

            dataset.setValue( "0-5 Years", age_0_5 );
            dataset.setValue( "6-10 Years", age_6_10 );
            dataset.setValue( ">10 Years", age_more_10 );

            hash.put( "chart", createPieChart( dataset, "Refrigerators/freezer models by age group" ) );
            fileName = "Pie_Refrigerator_freezer_models_by_age_group.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP_BAR ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            hash.put( "equipmentTypeId", equipmentTypeId );
            hash.put( "modelTypeAttributeId", modelTypeAttributeId );
    
            int i = 0;
            Integer equipmentTypeAttributeId = 3;
            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                i++;
                if ( i == 1 )
                    continue;
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                if ( ccemCellContent1.split( ":" )[3].equalsIgnoreCase( "UNKNOWN" ) )
                {

                }
                else if ( ccemCellContent1.split( ":" )[4].equalsIgnoreCase( "MORE" ) )
                {
                    equipmentTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    modelTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    equipmentTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
                else
                {
                    equipmentTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    modelTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    equipmentTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
            }

            List<String> catalogTypeAttributeValueList = new ArrayList<String>( ccemReportManager.getModelName(
                equipmentTypeId, modelTypeAttributeId, orgUnitIdsByComma ) );

            Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 0, 2 ) );

            Map<String, Integer> catalogTypeAttributeValueMap2 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 3, 5 ) );

            Map<String, Integer> catalogTypeAttributeValueMap3 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 6, 10 ) );

            Map<String, Integer> catalogTypeAttributeValueMap4 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId, 3, 11,
                    -1 ) );

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for ( String catalog : catalogTypeAttributeValueList )
            {
                if ( catalogTypeAttributeValueMap1.containsKey( catalog ) )
                {
                    dataset.setValue( catalogTypeAttributeValueMap1.get( catalog ), "0-2 Years", catalog );
                }
                else
                {
                    dataset.setValue( 0, "0-2 Years", catalog );
                }
                if ( catalogTypeAttributeValueMap2.containsKey( catalog ) )
                {
                    dataset.setValue( catalogTypeAttributeValueMap2.get( catalog ), "3-5 Years", catalog );
                }
                else
                {
                    dataset.setValue( 0, "3-5 Years", catalog );
                }
                if ( catalogTypeAttributeValueMap3.containsKey( catalog ) )
                {
                    dataset.setValue( catalogTypeAttributeValueMap3.get( catalog ), "6-10 Years", catalog );
                }
                else
                {
                    dataset.setValue( 0, "6-10 Years", catalog );
                }
                if ( catalogTypeAttributeValueMap4.containsKey( catalog ) )
                {
                    dataset.setValue( catalogTypeAttributeValueMap4.get( catalog ), ">10 Years", catalog );
                }
                else
                {
                    dataset.setValue( 0, ">10 Years", catalog );
                }
            }

            hash.put( "chart2", createBarChart( dataset, "WORKING_STATUS_BY_REFRIGERATORS_FREEZER_MODEL", "",
                "Number of Equipment", PlotOrientation.VERTICAL ) );
            fileName = "Bar_Refrigerator_freezer_models_by_age_group.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );

        }
        else if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP.trim() ) )
        {
            hash.put( "reportName", "Refrigerators/freezer models by age group" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            hash.put( "inventoryTypeId", inventoryTypeId );
            hash.put( "catalogTypeAttributeId", catalogTypeAttributeId );
            int i = 0;
            Integer inventoryTypeAttributeId = 3;
            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                i++;
                if ( i == 1 )
                    continue;
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                if ( ccemCellContent1.split( ":" )[3].equalsIgnoreCase( "UNKNOWN" ) )
                {

                }
                else if ( ccemCellContent1.split( ":" )[4].equalsIgnoreCase( "MORE" ) )
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
                else
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
            }
            Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId,
                    inventoryTypeAttributeId, 0, 2 ) );

            Map<String, Integer> catalogTypeAttributeValueMap2 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId,
                    inventoryTypeAttributeId, 3, 5 ) );

            Map<String, Integer> catalogTypeAttributeValueMap3 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId,
                    inventoryTypeAttributeId, 6, 10 ) );

            Map<String, Integer> catalogTypeAttributeValueMap4 = new HashMap<String, Integer>( ccemReportManager
                .getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId, 3, 11,
                    -1 ) );

            hash.put( "Value_0_2", catalogTypeAttributeValueMap1 );
            hash.put( "Value_3_5", catalogTypeAttributeValueMap2 );
            hash.put( "Value_6_10", catalogTypeAttributeValueMap3 );
            hash.put( "Value_11_MORE", catalogTypeAttributeValueMap4 );

            fileName = "Refrigerator_freezer_models_by_age_group.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, con );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.REFRIGERATOR_FREEZER_UTILIZATION_PIE ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer equipmentUtilizationId = Integer.parseInt( ccemCellContent.split( ":" )[2] );
            
            EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( equipmentUtilizationId );
            
            DefaultPieDataset dataset = new DefaultPieDataset();

            Map<String, Integer> equipmentValue_CountMap = new HashMap<String, Integer>( ccemReportManager.getEquipmentValue_Count( equipmentTypeId, equipmentUtilizationId, orgUnitIdsByComma ) );

            if( equipmentTypeAttribute.getOptionSet() != null )
            {
            	List<String> equipmentTypeAttributeOptions = new ArrayList<String>( equipmentTypeAttribute.getOptionSet().getOptions() );
                for( String equipmentTypeAttributeOption : equipmentTypeAttributeOptions )
                {
                    Integer equipmentValue_Count = equipmentValue_CountMap.get( equipmentTypeAttributeOption );                	
                    if( equipmentValue_Count == null )
                    {
                	equipmentValue_Count = 0;
                    }
                	
                    dataset.setValue( equipmentTypeAttributeOption, equipmentValue_Count );
                }
            }
            
            createPieChart( dataset, "REFRIGERATOR FREEZER UTILIZATION" );
            hash.put( "chart2", createPieChart( dataset, "REFRIGERATOR FREEZER UTILIZATION" ) );
            fileName = "Pie_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.REFRIGERATOR_FREEZER_UTILIZATION_BAR ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer equipmentUtilizationId = Integer.parseInt( ccemCellContent.split( ":" )[2] );

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( equipmentUtilizationId );

            Map<String, Map<String,Integer>> modelName_EquipmentUnilization_CountMap = new HashMap<String, Map<String,Integer>>( ccemReportManager.getModelName_EquipmentUtilization_Count( equipmentTypeId, modelTypeAttributeId, equipmentUtilizationId, orgUnitIdsByComma ) );
            Map<String, Integer> modelName_CountMap = new HashMap<String, Integer>( ccemReportManager.getModelName_Count( equipmentTypeId, modelTypeAttributeId, orgUnitIdsByComma ) );
            List<String> modelList = new ArrayList<String>( modelName_CountMap.keySet() );
            Collections.sort( modelList );
            
            for ( String model : modelList )
            {
                Map<String, Integer> equipmentUtilisationMap = new HashMap<String, Integer>( modelName_EquipmentUnilization_CountMap.get( model ) );
                if( equipmentTypeAttribute.getOptionSet() != null )
                {
                    List<String> inventoryTypeAttributeOptions = new ArrayList<String>( equipmentTypeAttribute.getOptionSet().getOptions() );
                    for( String inventoryTypeAttributeOption : inventoryTypeAttributeOptions )
                    {
                    	Integer equipmentUtilisationCount = equipmentUtilisationMap.get( inventoryTypeAttributeOption );
                    	
                    	if( equipmentUtilisationCount == null )
                    	{
                    		equipmentUtilisationCount = 0;
                    	}
                    	
                    	dataset.setValue( equipmentUtilisationCount, inventoryTypeAttributeOption, model );                        
                    }
                }
            }

            hash.put( "chart", createBarChart( dataset, "REFRIGERATOR FREEZER UTILIZATION", "", "Number of Equipment", PlotOrientation.VERTICAL ) );
            fileName = "Bar_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.REFRIGERATOR_FREEZER_UTILIZATION ) )
        {
            hash.put( "reportName", "Refrigerator/Freezer Utilization" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer equipmentUtilizationId = Integer.parseInt( ccemCellContent.split( ":" )[2] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 8 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Model Name", "Model Name", String.class.getName(), 70, true );
            frb.addColumn( "Total # ", "Total", String.class.getName(), 80, true );
            
            EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( equipmentUtilizationId );
            if( equipmentTypeAttribute.getOptionSet() != null )
            {
            	List<String> inventoryTypeAttributeOptions = new  ArrayList<String>( equipmentTypeAttribute.getOptionSet().getOptions() );
                for( String inventoryTypeAttributeOption : inventoryTypeAttributeOptions )
                {
                    frb.addColumn( "#", inventoryTypeAttributeOption+"_#", String.class.getName(), 50, true );
                    frb.addColumn( "%", inventoryTypeAttributeOption+"_%", String.class.getName(), 50, true );
                }

                int colCount = 2;
                for( String inventoryTypeAttributeOption : inventoryTypeAttributeOptions )
                {
                    frb.setColspan( colCount, 2, inventoryTypeAttributeOption );
                    colCount += 2;
                }
            }
            
            /*
            frb.addColumn( "#", "In Use_#", String.class.getName(), 50, true );
            frb.addColumn( "%", "In Use_%", String.class.getName(), 50, true );
            frb.addColumn( "#", "In Store_#", String.class.getName(), 50, true );
            frb.addColumn( "%", "In Store_%", String.class.getName(), 50, true );
            frb.addColumn( "#", "Not Used_#", String.class.getName(), 50, true );
            frb.addColumn( "%", "Not Used_%", String.class.getName(), 50, true );
            frb.addColumn( "#", "Unknown_#", String.class.getName(), 50, true );
            frb.addColumn( "%", "Unknown_%", String.class.getName(), 50, true );
            frb.setColspan( 2, 2, "In Use" );
            frb.setColspan( 4, 2, "In Store" );
            frb.setColspan( 6, 2, "Not Used" );
            frb.setColspan( 8, 2, "Unknown Status" );
            
            frb.setColspan( 2, 2, "In Use" );
                frb.setColspan( 4, 2, "In Store" );
                frb.setColspan( 6, 2, "Not Used" );
                frb.setColspan( 8, 2, "Unknown Status" );
            */

            
            /*
            Map<String, Integer> inuseEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId,
                inventoryTypeId, EquipmentStatus.STATUS_IN_USE, orgUnitIdsByComma ));
            Map<String, Integer> instoreEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId,
                inventoryTypeId, EquipmentStatus.STATUS_IN_STORE, orgUnitIdsByComma ));
            Map<String, Integer> notusedEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId,
                inventoryTypeId, EquipmentStatus.STATUS_NOT_IN_USE, orgUnitIdsByComma ));
            Map<String, Integer> unknownEquipmentMap = (ccemReportManager.getModelNameAndCount( catalogTypeAttributeId,
                inventoryTypeId, EquipmentStatus.STATUS_UNKNOWN, orgUnitIdsByComma ));
			*/

            Map<String, Map<String,Integer>> modelName_EquipmentUnilization_CountMap = new HashMap<String, Map<String,Integer>>( ccemReportManager.getModelName_EquipmentUtilization_Count( inventoryTypeId, catalogTypeAttributeId, equipmentUtilizationId, orgUnitIdsByComma ) );
            Map<String, Integer> modelName_CountMap = new HashMap<String, Integer>( ccemReportManager.getModelName_Count( inventoryTypeId, catalogTypeAttributeId, orgUnitIdsByComma ) );
            List<String> modelList = new ArrayList<String>( modelName_CountMap.keySet() );
            Collections.sort( modelList );
            for ( String model : modelList )
            {
                Integer totalCount = modelName_CountMap.get( model );
                if( totalCount == null )
                {
                	totalCount = 0;
                }
                
                Map<String, Integer> equipmentUtilisationMap = new HashMap<String, Integer>( modelName_EquipmentUnilization_CountMap.get( model ) );
                Map<String, String> numberOfData = new HashMap<String, String>();
                
                if( equipmentTypeAttribute.getOptionSet() != null )
                {
                    List<String> inventoryTypeAttributeOptions = new  ArrayList<String>( equipmentTypeAttribute.getOptionSet().getOptions() );
                    for( String inventoryTypeAttributeOption : inventoryTypeAttributeOptions )
                    {
                    	Integer equipmentUtilisationCount = equipmentUtilisationMap.get( inventoryTypeAttributeOption );
                    	
                    	if( equipmentUtilisationCount == null )
                    	{
                    		equipmentUtilisationCount = 0;
                    	}
                    	
                    	Double percentageEquipmentUtilisation = 0.0;
                    	if( totalCount != 0)
                    	{
                    		percentageEquipmentUtilisation = (double) equipmentUtilisationCount / (double) totalCount * 100.0;                    	
                    		percentageEquipmentUtilisation = Math.round( percentageEquipmentUtilisation * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    	}
                        
                        //totalCount += equipmentUtilisationCount;
                        
                        numberOfData.put( inventoryTypeAttributeOption+"_#", equipmentUtilisationCount + "" );
                        numberOfData.put( inventoryTypeAttributeOption+"_%", percentageEquipmentUtilisation + "" );
                    }
                }
                
                numberOfData.put( "Model Name", model );
                numberOfData.put( "Total", totalCount + "" );
               
                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 40 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Refrigerator_freezer_utilization.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.DISTRIBUTION_REFRIGERATOR_FREEZER_MODELS ) )
        {
            hash.put( "reportName", "Distribution of Refrigerators/freezers by model and facility type" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 8 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();
            frb.addColumn( "Model Name", "Model Name", String.class.getName(), 50, true );
            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitGroupId );
                frb.addColumn( orgUnitGroup.getName(), orgUnitGroup.getName(), String.class.getName(), 39, true );
            }
            frb.addColumn( "Total", "Total", String.class.getName(), 30, true );
            frb.addColumn( "% of Total", "% of Total", String.class.getName(), 30, true );
            List<String> modelsList = ccemReportManager.getModelName( inventoryTypeId, catalogTypeAttributeId,
                orgUnitIdsByComma );
            Integer totalValue = 0;
            Map<String, Integer> ccemResultMap = ccemReportManager.getCatalogTypeAttributeValue( orgUnitIdsByComma,
                inventoryTypeId, catalogTypeAttributeId );
            for ( String catalog : ccemResultMap.keySet() )
            {
                totalValue = totalValue + ccemResultMap.get( catalog );
            }
            for ( String model : modelsList )
            {
                Integer total = 0;
                Map<String, String> numberOfData = new HashMap<String, String>();
                numberOfData.put( "Model Name", model );
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    Map<String, Integer> catalogAndCountMap = new HashMap<String, Integer>();
                    for ( Integer orgUnitId : selOrgUnitList )
                    {
                        List<Integer> orgIdList = new ArrayList<Integer>();
                        List<Integer> orgUnitGrpIdList = new ArrayList<Integer>();

                        orgIdList.add( orgUnitId );
                        orgUnitGrpIdList.add( orgUnitGroupId );

                        String orgUnitidsByComma = ccemReportManager.getOrgunitIdsByComma( orgIdList, orgUnitGrpIdList );
                        catalogAndCountMap = ccemReportManager.getCatalogTypeAttributeValue( orgUnitidsByComma,
                            inventoryTypeId, catalogTypeAttributeId );
                    }
                    if ( catalogAndCountMap.get( model ) == null )
                    {
                        numberOfData.put( orgUnitGroup.getName(), "" );
                    }
                    else
                    {
                        numberOfData.put( orgUnitGroup.getName(), catalogAndCountMap.get( model ) + "" );
                        total = total + catalogAndCountMap.get( model );
                    }
                }
                numberOfData.put( "Total", total + "" );
                Double perTotal = (Double.valueOf( total ) / Double.valueOf( totalValue )) * 100;
                perTotal = Math.round( perTotal * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                numberOfData.put( "% of Total", perTotal + "" );
                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 40 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Distribution of Refrigerators_freezers by model and facility type.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.LINELIST_EQUIPMENT_NOT_WORKING_AND_REPAIR ) )
        {
            hash
                .put( "reportName", "Linelist of equipment with working status = not working and working needs service" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Facility Code", "Facility Code", String.class.getName(), 32, true );
            frb.addColumn( "Province", "Province", String.class.getName(), 32, true );
            frb.addColumn( "District", "District", String.class.getName(), 32, true );
            frb.addColumn( "Division", "Division", String.class.getName(), 32, true );
            frb.addColumn( "Location", "Location", String.class.getName(), 32, true );
            frb.addColumn( "Facility Name", "Facility Name", String.class.getName(), 32, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 32, true );
            frb.addColumn( "Library ID", "Library ID", String.class.getName(), 32, true );
            frb.addColumn( "Model", "Model", String.class.getName(), 32, true );
            frb.addColumn( "Manufacturer", "Manufacturer", String.class.getName(), 32, true );
            frb.addColumn( "Serial #", "Serial #", String.class.getName(), 32, true );
            frb.addColumn( "+4", "4", String.class.getName(), 32, true );
            frb.addColumn( "+20", "20", String.class.getName(), 32, true );
            frb.addColumn( "Year of Supply", "Year of Supply", String.class.getName(), 32, true );
            frb.addColumn( "Supply Source", "Supply Source", String.class.getName(), 32, true );
            frb.addColumn( "Working Status", "Working Status", String.class.getName(), 32, true );
            frb.addColumn( "Equipment Utilization", "Equipment Utilization", String.class.getName(), 32, true );
            frb.setColspan( 11, 2, "Net Volume" );

            List<String> catalogIds = ccemReportManager.equipmentCatalogies( orgUnitIdsByComma, inventoryTypeId );
            Map<String, String> serialNumber = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 2 );
            Map<String, String> yearOfSupply = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 3 );
            Map<String, String> workingStatus = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 4 );
            Map<String, String> equipmentUtilization = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 5 );
            Map<String, String> model = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma, inventoryTypeId,
                16 );
            Map<String, String> NetVolume4 = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 31 );
            Map<String, String> NetVolume20 = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 36 );
            Map<String, String> sourceOfSupply = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 42 );
            Map<String, String> manufacturer = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 15 );

            Map<String, String> eqipmentOrgUnit = ccemReportManager.equipmentOrgUnit( orgUnitIdsByComma,
                inventoryTypeId );

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                for ( String catalogId : catalogIds )
                {

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );

                    OrganisationUnit org = organisationUnitService.getOrganisationUnit( Integer
                        .parseInt( eqipmentOrgUnit.get( catalogId ) ) );

                    Map<String, String> numberOfData = new HashMap<String, String>();
                    numberOfData.put( "Facility Code", org.getCode() );
                    numberOfData.put( "Province", org.getParent().getParent().getParent().getShortName() );
                    numberOfData.put( "District", org.getParent().getParent().getShortName() );
                    numberOfData.put( "Division", org.getParent().getShortName() );
                    // numberOfData.put( "Location", org.getName() );
                    numberOfData.put( "Facility Name", org.getName() );
                    numberOfData.put( "Facility Type", orgUnitGroup.getShortName() );

                    numberOfData.put( "Model", modelService.getModel( Integer.parseInt( catalogId ) ).getName() );
                    numberOfData.put( "Manufacturer", manufacturer.get( catalogId ) );
                    numberOfData.put( "Serial #", serialNumber.get( catalogId ) );
                    numberOfData.put( "4", NetVolume4.get( catalogId ) );
                    numberOfData.put( "20", NetVolume20.get( catalogId ) );
                    numberOfData.put( "Year of Supply", yearOfSupply.get( catalogId ) );
                    numberOfData.put( "Supply Source", sourceOfSupply.get( catalogId ) );
                    numberOfData.put( "Working Status", workingStatus.get( catalogId ) );
                    numberOfData.put( "Equipment Utilization", equipmentUtilization.get( catalogId ) );
                    tableData.add( numberOfData );

                }
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 35 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Linelist of equipment.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.LINELIST_EQUIPMENT_WORKING_AND_NOT_WORKING ) )
        {
            hash.put( "reportName", "Linelist of equipment with working status = working and not working" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Facility Code", "Facility Code", String.class.getName(), 32, true );
            frb.addColumn( "Province", "Province", String.class.getName(), 32, true );
            frb.addColumn( "District", "District", String.class.getName(), 32, true );
            frb.addColumn( "Division", "Division", String.class.getName(), 32, true );
            frb.addColumn( "Location", "Location", String.class.getName(), 32, true );
            frb.addColumn( "Facility Name", "Facility Name", String.class.getName(), 32, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 32, true );
            frb.addColumn( "Library ID", "Library ID", String.class.getName(), 32, true );
            frb.addColumn( "Model", "Model", String.class.getName(), 32, true );
            frb.addColumn( "Manufacturer", "Manufacturer", String.class.getName(), 32, true );
            frb.addColumn( "Serial #", "Serial #", String.class.getName(), 32, true );
            frb.addColumn( "+4", "4", String.class.getName(), 32, true );
            frb.addColumn( "+20", "20", String.class.getName(), 32, true );
            frb.addColumn( "Year of Supply", "Year of Supply", String.class.getName(), 32, true );
            frb.addColumn( "Supply Source", "Supply Source", String.class.getName(), 32, true );
            frb.addColumn( "Working Status", "Working Status", String.class.getName(), 32, true );
            frb.addColumn( "Equipment Utilization", "Equipment Utilization", String.class.getName(), 32, true );
            frb.setColspan( 11, 2, "Net Volume" );

            List<String> catalogIds = ccemReportManager.equipmentCatalogies( orgUnitIdsByComma, inventoryTypeId );
            Map<String, String> serialNumber = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 2 );
            Map<String, String> yearOfSupply = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 3 );
            Map<String, String> workingStatus = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 4 );
            Map<String, String> equipmentUtilization = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 5 );
            Map<String, String> model = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma, inventoryTypeId,
                16 );
            Map<String, String> NetVolume4 = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 31 );
            Map<String, String> NetVolume20 = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 36 );
            Map<String, String> sourceOfSupply = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 42 );
            Map<String, String> manufacturer = ccemReportManager.equipmentCatalogyValues( orgUnitIdsByComma,
                inventoryTypeId, 15 );

            Map<String, String> eqipmentOrgUnit = ccemReportManager.equipmentOrgUnit( orgUnitIdsByComma,
                inventoryTypeId );

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                for ( String catalogId : catalogIds )
                {

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );

                    OrganisationUnit org = organisationUnitService.getOrganisationUnit( Integer
                        .parseInt( eqipmentOrgUnit.get( catalogId ) ) );

                    Map<String, String> numberOfData = new HashMap<String, String>();
                    numberOfData.put( "Facility Code", org.getCode() );
                    numberOfData.put( "Province", org.getParent().getParent().getParent().getShortName() );
                    numberOfData.put( "District", org.getParent().getParent().getShortName() );
                    numberOfData.put( "Division", org.getParent().getShortName() );
                    // numberOfData.put( "Location", org.getName() );
                    numberOfData.put( "Facility Name", org.getName() );
                    numberOfData.put( "Facility Type", orgUnitGroup.getShortName() );

                    numberOfData.put( "Model", modelService.getModel( Integer.parseInt( catalogId ) ).getName() );
                    numberOfData.put( "Manufacturer", manufacturer.get( catalogId ) );
                    numberOfData.put( "Serial #", serialNumber.get( catalogId ) );
                    numberOfData.put( "4", NetVolume4.get( catalogId ) );
                    numberOfData.put( "20", NetVolume20.get( catalogId ) );
                    numberOfData.put( "Year of Supply", yearOfSupply.get( catalogId ) );
                    numberOfData.put( "Supply Source", sourceOfSupply.get( catalogId ) );
                    numberOfData.put( "Working Status", workingStatus.get( catalogId ) );
                    numberOfData.put( "Equipment Utilization", equipmentUtilization.get( catalogId ) );
                    tableData.add( numberOfData );

                }
            }

            // }
            // }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 35 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Linelist of equipment.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }

        // Storage Capacity
        else if ( ccemReport.getReportType().equals( CCEMReport.VACCINE_STORAGE_CAPACITY ) )
        {
            hash.put( "reportName", "Vaccine storage capacity at +2 to +8C against requirements (in Litres)" );
            List tableData = new ArrayList();
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();
            String orgUnitGroupIdsByComma = "-1";
            Integer periodId = 0;

            date2 = new Date();
            calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            //periodStartDate = "";

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            //periodStartDate = calendar.get( Calendar.YEAR ) - 1 + "-01-01";

            periodId = ccemReportManager.getPeriodId( periodStartDate, "Yearly" );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Facility Code", "Facility Code", String.class.getName(), 50, true );
            frb.addColumn( "District", "District", String.class.getName(), 50, true );
            frb.addColumn( "Facility Name", "Facility Name", String.class.getName(), 60, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 60, true );
            frb.addColumn( "Actual", "Actual", String.class.getName(), 40, true );
            frb.addColumn( "Required", "Required", String.class.getName(), 40, true );
            frb.addColumn( "Difference", "Difference", String.class.getName(), 40, true );
            frb.addColumn( ">30%", ">30%-1", String.class.getName(), 40, true );
            frb.addColumn( "10-30%", "10-30%-1", String.class.getName(), 40, true );
            frb.addColumn( "+/- 10%", "+/- 10%-1", String.class.getName(), 40, true );
            frb.addColumn( "10-30%", "10-30%-2", String.class.getName(), 40, true );
            frb.addColumn( ">30%", ">30%-2", String.class.getName(), 40, true );
            frb.setColspan( 4, 3, "Net Storage" );
            frb.setColspan( 7, 2, "Surplus" );
            frb.setColspan( 9, 1, "Match" );
            frb.setColspan( 10, 2, "Shortage" );

            CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
            String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            if ( ccemCellContent1.equals( "ALL" ) )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                    orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                }
            }
            else
            {
                String orgUnitGroupIds[] = ccemReportDesign1.getContent().split( "," );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    int flag = 0;
                    for ( String ouGroupId : orgUnitGroupIds )
                    {
                        if ( Integer.parseInt( ouGroupId ) == orgUnitGroupId )
                        {
                            orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                            flag = 1;
                            break;
                        }
                    }

                    if ( flag == 0 )
                        continue;

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                }
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }
            // Collections.sort(orgUnitGroupMembers);
            orgUnitList.retainAll( orgUnitGroupMembers );

            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            // Collections.sort(orgUnitList,new OrganisationUnitComparator());
            
            // Calculations for Actual Column
            ccemReportDesign1 = reportDesignList.get( 1 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

            String[] partsOfCellContent = ccemCellContent1.split( "-" );
            Integer vscrActualInventoryTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
            Integer vscrActualInventoryTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
            Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );
            
            Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>( ccemReportManager.getSumOfEquipmentDatabyInventoryType( orgUnitIdsByComma, vscrActualInventoryTypeId,
                    vscrActualInventoryTypeAttributeId, factor ) );

            String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
            Integer vsrActualEquipmentTypeidId = Integer.parseInt( partsOfVSRActualCellContent[0] );
            Integer vsrActualModelTypeAttributeidId = Integer.parseInt( partsOfVSRActualCellContent[1] );
            Integer vsrActualEquipmentTypeAttributeidId = Integer.parseInt( partsOfVSRActualCellContent[2] );
            String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

            Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager.getCatalogDataSumByEquipmentData( orgUnitIdsByComma, vsrActualEquipmentTypeidId, vsrActualModelTypeAttributeidId, vsrActualEquipmentTypeAttributeidId, vsrActualEquipmentValue ) );

            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            // PART 1 - 4:47:+4C:48:Yes
            String[] catalogDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqCatalogTypeId = Integer.parseInt( catalogDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( catalogDataParts[1] );
            String vsReqStorageTemp = catalogDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( catalogDataParts[3] );
            String vsReqNationalSupply = catalogDataParts[4];

            List<Integer> catalogIdsForRequirement = new ArrayList<Integer>( ccemReportManager.getCatalogIdsForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp, vsReqNationalSupplyId, vsReqNationalSupply ) );

            // PART 2 - 5
            String dataelementIds = "" + partsOfCellContent[1];
            Integer vsReqLiveBirthDeId = Integer.parseInt( dataelementIds.split( "," )[0] );
            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager.getDataElementDataForCatalogOptionsForRequirement( orgUnitIdsByComma, dataelementIds, periodId ) );

            // PART 3 - VaccineVolumePerChild
            String vvpcConstantName = "" + partsOfCellContent[2];
            Constant constant = constantService.getConstantByName( vvpcConstantName );
            Double vsReqVaccineVolumePerChildData = constant.getValue();

            // PART 4 - 3,4
            String orgUnitGroupAttribIds = partsOfCellContent[3];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager.getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager.getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();

                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }

                numberOfData.put( "District", orgUnitBranch );
                // numberOfData.put( "District",
                // orgUnit.getParent().getParent().getParent().getShortName() );
                numberOfData.put( "Facility Name", orgUnit.getName() );
                numberOfData.put( "Facility Code", orgUnit.getCode() );
                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    numberOfData.put( "Facility Type", " " );
                }
                else
                {
                    numberOfData.put( "Facility Type", orgUnitGroupName );
                }

                Double vsrActualValue = catalogSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByInventoryTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Actual", vaccineActualValue + "" );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                Double vsReqLiveBirthData = 0.0;
                tempStr = dataElementDataForRequirement.get( vsReqLiveBirthDeId + ":" + periodId + ":" + orgUnit.getId() );
                if ( tempStr != null )
                {
                    try
                    {
                        vsReqLiveBirthData = Double.parseDouble( tempStr );
                        constant = constantService.getConstantByName( "LiveBirthsPerThousand" );                    
                        Double liveBirthPerThousand = 40.0;
                        if( constant != null )
                        {
                            liveBirthPerThousand = constant.getValue();
                        }
                        vsReqLiveBirthData = ( vsReqLiveBirthData * liveBirthPerThousand ) / 1000;
                    }
                    catch ( Exception e )
                    {
                        vsReqLiveBirthData = 0.0;
                    }
                }

                Double vsReqSupplyIntervalData = 0.0;
                tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
                if ( tempStr != null )
                {
                    try
                    {
                        vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                    }
                    catch ( Exception e )
                    {
                        vsReqSupplyIntervalData = 0.0;
                    }
                }

                Double vsReqReserveStockData = 0.0;
                tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
                if ( tempStr != null )
                {
                    try
                    {
                        vsReqReserveStockData = Double.parseDouble( tempStr );
                    }
                    catch ( Exception e )
                    {
                        vsReqReserveStockData = 0.0;
                    }
                }

                System.out.println( vsReqLiveBirthData + " : " + vsReqVaccineVolumePerChildData + " : " + vsReqSupplyIntervalData + " : " + vsReqReserveStockData );
                // Formula for calculating Requirement 
                try
                {
                    vaccineRequirement = vsReqLiveBirthData * vsReqVaccineVolumePerChildData * ( (vsReqSupplyIntervalData + vsReqReserveStockData) / 52);
                }
                catch ( Exception e )
                {
                    System.out.println( "Exception while calculating individualVaccineRequirement" );
                    vaccineRequirement = 0.0;
                }

                vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Required", vaccineRequirement + "" );

                Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
                diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Difference", "" + diffVaccineReq );

                Double diffPercentage = 0.0;
                if( vaccineActualValue == 0 && diffVaccineReq == 0 )
                {
                	diffPercentage = 0.0;
                }
                else
                {
                	diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;
                }
                
                if ( diffPercentage < -30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "1" );
                }
                else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "1" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "1" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "1" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else
                {
                    numberOfData.put( ">30%-1", "1" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }

                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 34 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "VACCINE_STORAGE_CAPACITY.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );

        }

        else if ( ccemReport.getReportType().equals( CCEMReport.VACCINE_STORAGE_CAPACITY_BAR ) )
        {
            List<Map<String, String>> tableData = new ArrayList<Map<String, String>>();
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();
            String orgUnitGroupIdsByComma = "-1";
            Integer periodId = 0;
            date2 = new Date();
            calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            //periodStartDate = "";

            //periodStartDate = calendar.get( Calendar.YEAR ) - 1 + "-01-01";

            periodId = ccemReportManager.getPeriodId( periodStartDate, "Yearly" );

            CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
            String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            if ( ccemCellContent1.equals( "ALL" ) )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                    orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                }
            }
            else
            {
                String orgUnitGroupIds[] = ccemReportDesign1.getContent().split( "," );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    int flag = 0;
                    for ( String ouGroupId : orgUnitGroupIds )
                    {
                        if ( Integer.parseInt( ouGroupId ) == orgUnitGroupId )
                        {
                            orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                            flag = 1;
                            break;
                        }
                    }

                    if ( flag == 0 )
                        continue;

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                }
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }

            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            Collections.sort( orgUnitList );
            // Calculations for Actual Column
            ccemReportDesign1 = reportDesignList.get( 1 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

            String[] partsOfCellContent = ccemCellContent1.split( "-" );
            Integer vscrActualInventoryTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
            Integer vscrActualInventoryTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
            Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );
            ;

            Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>( ccemReportManager
                .getSumOfEquipmentDatabyInventoryType( orgUnitIdsByComma, vscrActualInventoryTypeId,
                    vscrActualInventoryTypeAttributeId, factor ) );

            String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
            Integer vsrActualInventoryTypeId = Integer.parseInt( partsOfVSRActualCellContent[0] );
            Integer vsrActualCatalogTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[1] );
            Integer vsrActualInventoryTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[2] );
            String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

            Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager
                .getCatalogDataSumByEquipmentData( orgUnitIdsByComma, vsrActualInventoryTypeId,
                    vsrActualCatalogTypeAttributeId, vsrActualInventoryTypeAttributeId, vsrActualEquipmentValue ) );

            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            // PART 1 - 4:47:+4C:48:Yes
            String[] catalogDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqCatalogTypeId = Integer.parseInt( catalogDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( catalogDataParts[1] );
            String vsReqStorageTemp = catalogDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( catalogDataParts[3] );
            String vsReqNationalSupply = catalogDataParts[4];

            List<Integer> catalogIdsForRequirement = new ArrayList<Integer>( ccemReportManager
                .getCatalogIdsForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply ) );

            // PART 2 - 5
            String dataelementIds = "" + partsOfCellContent[1];
            Integer vsReqLiveBirthDeId = Integer.parseInt( dataelementIds.split( "," )[0] );
            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getDataElementDataForCatalogOptionsForRequirement( orgUnitIdsByComma, dataelementIds, periodId ) );

            // PART 3 - VaccineVolumePerChild
            String vvpcConstantName = "" + partsOfCellContent[2];
            Constant constant = constantService.getConstantByName( vvpcConstantName );
            Double vsReqVaccineVolumePerChildData = constant.getValue();

            // PART 4 - 3,4
            String orgUnitGroupAttribIds = partsOfCellContent[3];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager.getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager.getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();

                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }

                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    numberOfData.put( "Facility Type", " " );
                }
                else
                {
                    numberOfData.put( "Facility Type", orgUnitGroupName );
                }

                Double vsrActualValue = catalogSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByInventoryTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                //for ( Integer catalogId : catalogIdsForRequirement )
                {
                    Double vsReqLiveBirthData = 0.0;
                    tempStr = dataElementDataForRequirement.get( vsReqLiveBirthDeId + ":" + periodId + ":"
                        + orgUnit.getId() );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqLiveBirthData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqLiveBirthData = 0.0;
                        }
                    }

                    Double vsReqSupplyIntervalData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqSupplyIntervalData = 0.0;
                        }
                    }

                    Double vsReqReserveStockData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqReserveStockData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqReserveStockData = 0.0;
                        }
                    }

                    // Formula for calculating Requirement for individual
                    // vaccine
                    Double individualVaccineRequirement = 0.0;
                    try
                    {
                        individualVaccineRequirement = vsReqLiveBirthData * vsReqVaccineVolumePerChildData
                            * ((vsReqSupplyIntervalData + vsReqReserveStockData) / 52);
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating individualVaccineRequirement" );
                        individualVaccineRequirement = 0.0;
                    }

                    vaccineRequirement += individualVaccineRequirement;
                }

                vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
                diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                Double diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;

                if ( diffPercentage < -30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "1" );
                }
                else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "1" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "1" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "1" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else
                {
                    numberOfData.put( ">30%-1", "1" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }

                tableData.add( numberOfData );
            }

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitGroupId );

                Integer value1 = 0, value2 = 0, value3 = 0, value4 = 0, value5 = 0;
                for ( Map<String, String> map : tableData )
                {
                    if ( map.get( "Facility Type" ).contains( orgUnitGroup.getName() ) )
                    {
                        value1 = value1 + Integer.parseInt( map.get( ">30%-1" ) );
                        value2 = value2 + Integer.parseInt( map.get( "10-30%-1" ) );
                        value3 = value3 + Integer.parseInt( map.get( "+/- 10%-1" ) );
                        value4 = value4 + Integer.parseInt( map.get( "10-30%-2" ) );
                        value5 = value5 + Integer.parseInt( map.get( ">30%-2" ) );
                    }
                }
                dataset.setValue( value1, "Surplus >30%", orgUnitGroup.getName() );
                dataset.setValue( value2, "Surplus 10-30%", orgUnitGroup.getName() );
                dataset.setValue( value3, "Match +/- 10%", orgUnitGroup.getName() );
                dataset.setValue( value4, "Shrotage 10-30%", orgUnitGroup.getName() );
                dataset.setValue( value5, "Shrotage >30%", orgUnitGroup.getName() );
            }

            hash.put( "chart", createBarChart2( dataset, "Vaccine Storage Capacity at +2 to +8C Against Requirements (in Litres)",
                "", "Number of Health Facility", PlotOrientation.HORIZONTAL ) );
            fileName = "Bar_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }

        else if ( ccemReport.getReportType().equals( CCEMReport.VACCINE_STORAGE_CAPACITY_FOR_LESS_THAN_20 ) )
        {
            List tableData = new ArrayList();
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();
            String orgUnitGroupIdsByComma = "-1";
            Integer periodId = 0;
            date2 = new Date();
            calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            periodStartDate = "";

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            periodStartDate = calendar.get( Calendar.YEAR ) - 1 + "-01-01";

            periodId = ccemReportManager.getPeriodId( periodStartDate, "Yearly" );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Facility Code", "Facility Code", String.class.getName(), 50, true );
            frb.addColumn( "District", "District", String.class.getName(), 50, true );
            frb.addColumn( "Facility Name", "Facility Name", String.class.getName(), 60, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 60, true );
            frb.addColumn( "Actual", "Actual", String.class.getName(), 40, true );
            frb.addColumn( "Required", "Required", String.class.getName(), 40, true );
            frb.addColumn( "Difference", "Difference", String.class.getName(), 40, true );
            frb.addColumn( ">30%", ">30%-1", String.class.getName(), 40, true );
            frb.addColumn( "10-30%", "10-30%-1", String.class.getName(), 40, true );
            frb.addColumn( "+/- 10%", "+/- 10%-1", String.class.getName(), 40, true );
            frb.addColumn( "10-30%", "10-30%-2", String.class.getName(), 40, true );
            frb.addColumn( ">30%", ">30%-2", String.class.getName(), 40, true );
            frb.setColspan( 4, 3, "Net Storage" );
            frb.setColspan( 7, 2, "Surplus" );
            frb.setColspan( 9, 1, "Match" );
            frb.setColspan( 10, 2, "Shortage" );

            CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
            String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            if ( ccemCellContent1.equals( "ALL" ) )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                    orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                }
            }
            else
            {
                String orgUnitGroupIds[] = ccemReportDesign1.getContent().split( "," );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    int flag = 0;
                    for ( String ouGroupId : orgUnitGroupIds )
                    {
                        if ( Integer.parseInt( ouGroupId ) == orgUnitGroupId )
                        {
                            orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                            flag = 1;
                            break;
                        }
                    }

                    if ( flag == 0 )
                        continue;

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                }
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }
            // Collections.sort(orgUnitGroupMembers);
            orgUnitList.retainAll( orgUnitGroupMembers );

            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            // Collections.sort(orgUnitList,new OrganisationUnitComparator());
            // Calculations for Actual Column
            ccemReportDesign1 = reportDesignList.get( 1 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

            String[] partsOfCellContent = ccemCellContent1.split( "-" );
            Integer vscrActualInventoryTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
            Integer vscrActualInventoryTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
            Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );
            ;

            Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>( ccemReportManager
                .getSumOfEquipmentDatabyInventoryType( orgUnitIdsByComma, vscrActualInventoryTypeId,
                    vscrActualInventoryTypeAttributeId, factor ) );

            String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
            Integer vsrActualInventoryTypeId = Integer.parseInt( partsOfVSRActualCellContent[0] );
            Integer vsrActualCatalogTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[1] );
            Integer vsrActualInventoryTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[2] );
            String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

            Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager
                .getCatalogDataSumByEquipmentData( orgUnitIdsByComma, vsrActualInventoryTypeId,
                    vsrActualCatalogTypeAttributeId, vsrActualInventoryTypeAttributeId, vsrActualEquipmentValue ) );

            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            String[] catalogDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqCatalogTypeId = Integer.parseInt( catalogDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( catalogDataParts[1] );
            String vsReqStorageTemp = catalogDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( catalogDataParts[3] );
            String vsReqNationalSupply = catalogDataParts[4];
            String vsReqCatalogAttribIds = catalogDataParts[5];

            Integer vsReqPackedVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[0] );
            Integer vsReqDiluentVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[1] );
            Integer vsReqDoses = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[2] );
            Integer vsReqTargetPopCat = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[3] );
            Integer vsReqUsage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[4] );
            Integer vsReqWastage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[5] );

            List<Integer> catalogIdsForRequirement = new ArrayList<Integer>( ccemReportManager
                .getCatalogIdsForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply ) );

            Map<String, String> catalogDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getCatalogDataForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply, vsReqCatalogAttribIds ) );

            Integer vsReqStaticDel = Integer.parseInt( partsOfCellContent[3].split( "," )[0] );
            Integer vsReqOutReachDel = Integer.parseInt( partsOfCellContent[3].split( "," )[1] );

            String catalogOption_DataelementIds = vsReqStaticDel + "," + vsReqOutReachDel;

            String[] dataelementDataParts = partsOfCellContent[1].split( "," );
            Map<String, Integer> catalogOption_DataelementMap = new HashMap<String, Integer>();

            for ( String de_catalogOption : dataelementDataParts )
            {
                catalogOption_DataelementMap.put( de_catalogOption.split( ":" )[1], Integer.parseInt( de_catalogOption
                    .split( ":" )[0] ) );
                catalogOption_DataelementIds += "," + Integer.parseInt( de_catalogOption.split( ":" )[0] );
            }

            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getDataElementDataForCatalogOptionsForRequirement( orgUnitIdsByComma, catalogOption_DataelementIds,
                    periodId ) );

            String orgUnitGroupAttribIds = partsOfCellContent[2];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager
                .getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();
                numberOfData.put( "District", orgUnit.getParent().getParent().getParent().getShortName() );
                numberOfData.put( "Facility Name", orgUnit.getName() );
                numberOfData.put( "Facility Code", orgUnit.getCode() );
                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    numberOfData.put( "Facility Type", " " );
                }
                else
                {
                    numberOfData.put( "Facility Type", orgUnitGroupName );
                }

                Double vsrActualValue = catalogSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByInventoryTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Actual", vaccineActualValue + "" );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                for ( Integer catalogId : catalogIdsForRequirement )
                {
                    Double vsReqUsageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqUsage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqUsageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqUsageData = 0.0;
                        }
                    }

                    Double vsReqTargetPopData = 0.0;
                    String vsReqTargetPopCatData = catalogDataForRequirement.get( catalogId + ":" + vsReqTargetPopCat );
                    if ( vsReqTargetPopCatData != null )
                    {
                        Integer deId = catalogOption_DataelementMap.get( vsReqTargetPopCatData );
                        tempStr = dataElementDataForRequirement.get( deId + ":" + periodId + ":" + orgUnit.getId() );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqTargetPopData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqTargetPopData = 0.0;
                            }
                        }
                    }

                    Double vsReqDosesData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDoses );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqDosesData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqDosesData = 0.0;
                        }
                    }

                    Double vsReqPackedVolData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqPackedVol );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqPackedVolData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqPackedVolData = 0.0;
                        }
                    }

                    String tempStr1 = dataElementDataForRequirement.get( vsReqStaticDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    String tempStr2 = dataElementDataForRequirement.get( vsReqOutReachDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    if ( (tempStr1 != null && tempStr1.equalsIgnoreCase( "true" ))
                        || (tempStr2 != null && tempStr2.equalsIgnoreCase( "true" )) )
                    {
                        Double vsReqDiluentVolData = 0.0;
                        tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDiluentVol );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqDiluentVolData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqDiluentVolData = 0.0;
                            }
                        }

                        vsReqPackedVolData += vsReqDiluentVolData;
                    }

                    Double vsReqWastageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqWastage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqWastageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqWastageData = 0.0;
                        }
                    }

                    Double vsReqSupplyIntervalData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqSupplyIntervalData = 0.0;
                        }
                    }

                    Double vsReqReserveStockData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqReserveStockData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqReserveStockData = 0.0;
                        }
                    }

                    // Formula for calculating Requirement for individual
                    // vaccine
                    Double individualVaccineRequirement = 0.0;
                    try
                    {
                        individualVaccineRequirement = ((vsReqUsageData * vsReqTargetPopData) / 100) * vsReqDosesData
                            * vsReqPackedVolData * (1 / (1 - (vsReqWastageData / 100)))
                            * (((vsReqSupplyIntervalData + vsReqReserveStockData) / 52) / 1000);
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating individualVaccineRequirement" );
                        individualVaccineRequirement = 0.0;
                    }

                    vaccineRequirement += individualVaccineRequirement;

                }

                vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Required", vaccineRequirement + "" );

                Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
                diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Difference", "" + diffVaccineReq );

                Double diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;
                if ( diffPercentage < -30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "1" );
                }
                else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "1" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "1" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "1" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else
                {
                    numberOfData.put( ">30%-1", "1" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }

                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 34 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "VACCINE_STORAGE_CAPACITY.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );

        }

        else if ( ccemReport.getReportType().equals( CCEMReport.VACCINE_STORAGE_CAPACITY_FOR_LESS_THAN_20_BAR ) )
        {
            List<Map<String, String>> tableData = new ArrayList<Map<String, String>>();
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();
            String orgUnitGroupIdsByComma = "-1";
            Integer periodId = 0;
            date2 = new Date();
            calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            periodStartDate = "";

            periodStartDate = calendar.get( Calendar.YEAR ) - 1 + "-01-01";

            periodId = ccemReportManager.getPeriodId( periodStartDate, "Yearly" );

            CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
            String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            if ( ccemCellContent1.equals( "ALL" ) )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                    orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                }
            }
            else
            {
                String orgUnitGroupIds[] = ccemReportDesign1.getContent().split( "," );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    int flag = 0;
                    for ( String ouGroupId : orgUnitGroupIds )
                    {
                        if ( Integer.parseInt( ouGroupId ) == orgUnitGroupId )
                        {
                            orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                            flag = 1;
                            break;
                        }
                    }

                    if ( flag == 0 )
                        continue;

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                }
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }

            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            Collections.sort( orgUnitList );
            // Calculations for Actual Column
            ccemReportDesign1 = reportDesignList.get( 1 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

            String[] partsOfCellContent = ccemCellContent1.split( "-" );
            Integer vscrActualInventoryTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
            Integer vscrActualInventoryTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
            Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );

            Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>( ccemReportManager
                .getSumOfEquipmentDatabyInventoryType( orgUnitIdsByComma, vscrActualInventoryTypeId,
                    vscrActualInventoryTypeAttributeId, factor ) );

            String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
            Integer vsrActualInventoryTypeId = Integer.parseInt( partsOfVSRActualCellContent[0] );
            Integer vsrActualCatalogTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[1] );
            Integer vsrActualInventoryTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[2] );
            String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

            Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager
                .getCatalogDataSumByEquipmentData( orgUnitIdsByComma, vsrActualInventoryTypeId,
                    vsrActualCatalogTypeAttributeId, vsrActualInventoryTypeAttributeId, vsrActualEquipmentValue ) );

            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            String[] catalogDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqCatalogTypeId = Integer.parseInt( catalogDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( catalogDataParts[1] );
            String vsReqStorageTemp = catalogDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( catalogDataParts[3] );
            String vsReqNationalSupply = catalogDataParts[4];
            String vsReqCatalogAttribIds = catalogDataParts[5];

            Integer vsReqPackedVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[0] );
            Integer vsReqDiluentVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[1] );
            Integer vsReqDoses = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[2] );
            Integer vsReqTargetPopCat = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[3] );
            Integer vsReqUsage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[4] );
            Integer vsReqWastage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[5] );

            List<Integer> catalogIdsForRequirement = new ArrayList<Integer>( ccemReportManager
                .getCatalogIdsForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply ) );

            Map<String, String> catalogDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getCatalogDataForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply, vsReqCatalogAttribIds ) );

            Integer vsReqStaticDel = Integer.parseInt( partsOfCellContent[3].split( "," )[0] );
            Integer vsReqOutReachDel = Integer.parseInt( partsOfCellContent[3].split( "," )[1] );

            String catalogOption_DataelementIds = vsReqStaticDel + "," + vsReqOutReachDel;

            String[] dataelementDataParts = partsOfCellContent[1].split( "," );
            Map<String, Integer> catalogOption_DataelementMap = new HashMap<String, Integer>();

            for ( String de_catalogOption : dataelementDataParts )
            {
                catalogOption_DataelementMap.put( de_catalogOption.split( ":" )[1], Integer.parseInt( de_catalogOption
                    .split( ":" )[0] ) );
                catalogOption_DataelementIds += "," + Integer.parseInt( de_catalogOption.split( ":" )[0] );
            }

            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getDataElementDataForCatalogOptionsForRequirement( orgUnitIdsByComma, catalogOption_DataelementIds,
                    periodId ) );

            String orgUnitGroupAttribIds = partsOfCellContent[2];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager
                .getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for ( OrganisationUnit orgUnit : orgUnitList )
            {

                Map<String, String> numberOfData = new HashMap<String, String>();

                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    numberOfData.put( "Facility Type", " " );
                }
                else
                {
                    numberOfData.put( "Facility Type", orgUnitGroupName );
                }

                Double vsrActualValue = catalogSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByInventoryTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                for ( Integer catalogId : catalogIdsForRequirement )
                {
                    Double vsReqUsageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqUsage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqUsageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqUsageData = 0.0;
                        }
                    }

                    Double vsReqTargetPopData = 0.0;
                    String vsReqTargetPopCatData = catalogDataForRequirement.get( catalogId + ":" + vsReqTargetPopCat );
                    if ( vsReqTargetPopCatData != null )
                    {
                        Integer deId = catalogOption_DataelementMap.get( vsReqTargetPopCatData );
                        tempStr = dataElementDataForRequirement.get( deId + ":" + periodId + ":" + orgUnit.getId() );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqTargetPopData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqTargetPopData = 0.0;
                            }
                        }
                    }

                    Double vsReqDosesData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDoses );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqDosesData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqDosesData = 0.0;
                        }
                    }

                    Double vsReqPackedVolData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqPackedVol );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqPackedVolData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqPackedVolData = 0.0;
                        }
                    }

                    String tempStr1 = dataElementDataForRequirement.get( vsReqStaticDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    String tempStr2 = dataElementDataForRequirement.get( vsReqOutReachDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    if ( (tempStr1 != null && tempStr1.equalsIgnoreCase( "true" ))
                        || (tempStr2 != null && tempStr2.equalsIgnoreCase( "true" )) )
                    {
                        Double vsReqDiluentVolData = 0.0;
                        tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDiluentVol );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqDiluentVolData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqDiluentVolData = 0.0;
                            }
                        }

                        vsReqPackedVolData += vsReqDiluentVolData;
                    }

                    Double vsReqWastageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqWastage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqWastageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqWastageData = 0.0;
                        }
                    }

                    Double vsReqSupplyIntervalData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqSupplyIntervalData = 0.0;
                        }
                    }

                    Double vsReqReserveStockData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqReserveStockData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqReserveStockData = 0.0;
                        }
                    }

                    // Formula for calculating Requirement for individual
                    // vaccine
                    Double individualVaccineRequirement = 0.0;
                    try
                    {
                        individualVaccineRequirement = ((vsReqUsageData * vsReqTargetPopData) / 100) * vsReqDosesData
                            * vsReqPackedVolData * (1 / (1 - (vsReqWastageData / 100)))
                            * (((vsReqSupplyIntervalData + vsReqReserveStockData) / 52) / 1000);
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating individualVaccineRequirement" );
                        individualVaccineRequirement = 0.0;
                    }
                    vaccineRequirement += individualVaccineRequirement;
                }

                vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
                diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                Double diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;
                if ( diffPercentage < -30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "1" );
                }
                else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "1" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "1" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "1" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else
                {
                    numberOfData.put( ">30%-1", "1" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }

                tableData.add( numberOfData );
            }

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitGroupId );

                Integer value1 = 0, value2 = 0, value3 = 0, value4 = 0, value5 = 0;
                for ( Map<String, String> map : tableData )
                {
                    if ( map.get( "Facility Type" ).contains( orgUnitGroup.getName() ) )
                    {
                        value1 = value1 + Integer.parseInt( map.get( ">30%-1" ) );
                        value2 = value2 + Integer.parseInt( map.get( "10-30%-1" ) );
                        value3 = value3 + Integer.parseInt( map.get( "+/- 10%-1" ) );
                        value4 = value4 + Integer.parseInt( map.get( "10-30%-2" ) );
                        value5 = value5 + Integer.parseInt( map.get( ">30%-2" ) );
                    }
                }
                dataset.setValue( value1, "Surplus >30%", orgUnitGroup.getName() );
                dataset.setValue( value2, "Surplus 10-30%", orgUnitGroup.getName() );
                dataset.setValue( value3, "Match +/- 10%", orgUnitGroup.getName() );
                dataset.setValue( value4, "Shrotage 10-30%", orgUnitGroup.getName() );
                dataset.setValue( value5, "Shrotage >30%", orgUnitGroup.getName() );
            }

            hash.put( "chart", createBarChart2( dataset, "WORKING_STATUS_BY_REFRIGERATORS_FREEZER_MODEL", "",
                "Number of Health Facility", PlotOrientation.HORIZONTAL ) );
            fileName = "Bar_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.STORAGE_CAPACITY_SHORTAGES_4C_BY_AREA ) )
        {
            List tableData = new ArrayList();
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();
            String orgUnitGroupIdsByComma = "-1";
            Integer periodId = 0;
            date2 = new Date();
            calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            periodStartDate = "";

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            periodStartDate = calendar.get( Calendar.YEAR ) - 1 + "-01-01";

            periodId = ccemReportManager.getPeriodId( periodStartDate, "Yearly" );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Area", "Area", String.class.getName(), 100, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 100, true );
            frb.addColumn( "Total # of Facilities", "Total # of Facilities", String.class.getName(), 100, true );
            frb.addColumn( "#", "#", String.class.getName(), 50, true );
            frb.addColumn( "%", "%", String.class.getName(), 50, true );
            frb.setColspan( 3, 2, "Facilities with >30% Shortage" );

            CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
            String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            if ( ccemCellContent1.equals( "ALL" ) )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                    orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                }
            }
            else
            {
                String orgUnitGroupIds[] = ccemReportDesign1.getContent().split( "," );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    int flag = 0;
                    for ( String ouGroupId : orgUnitGroupIds )
                    {
                        if ( Integer.parseInt( ouGroupId ) == orgUnitGroupId )
                        {
                            orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                            flag = 1;
                            break;
                        }
                    }

                    if ( flag == 0 )
                        continue;

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                }
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }
            orgUnitList.retainAll( orgUnitGroupMembers );

            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            // Calculations for Actual Column
            ccemReportDesign1 = reportDesignList.get( 1 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

            String[] partsOfCellContent = ccemCellContent1.split( "-" );
            Integer vscrActualInventoryTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
            Integer vscrActualInventoryTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
            Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );

            Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>( ccemReportManager
                .getSumOfEquipmentDatabyInventoryType( orgUnitIdsByComma, vscrActualInventoryTypeId,
                    vscrActualInventoryTypeAttributeId, factor ) );

            String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
            Integer vsrActualInventoryTypeId = Integer.parseInt( partsOfVSRActualCellContent[0] );
            Integer vsrActualCatalogTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[1] );
            Integer vsrActualInventoryTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[2] );
            String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

            Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager
                .getCatalogDataSumByEquipmentData( orgUnitIdsByComma, vsrActualInventoryTypeId,
                    vsrActualCatalogTypeAttributeId, vsrActualInventoryTypeAttributeId, vsrActualEquipmentValue ) );

            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            String[] catalogDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqCatalogTypeId = Integer.parseInt( catalogDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( catalogDataParts[1] );
            String vsReqStorageTemp = catalogDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( catalogDataParts[3] );
            String vsReqNationalSupply = catalogDataParts[4];
            String vsReqCatalogAttribIds = catalogDataParts[5];

            Integer vsReqPackedVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[0] );
            Integer vsReqDiluentVol = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[1] );
            Integer vsReqDoses = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[2] );
            Integer vsReqTargetPopCat = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[3] );
            Integer vsReqUsage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[4] );
            Integer vsReqWastage = Integer.parseInt( vsReqCatalogAttribIds.split( "," )[5] );

            List<Integer> catalogIdsForRequirement = new ArrayList<Integer>( ccemReportManager
                .getCatalogIdsForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply ) );

            Map<String, String> catalogDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getCatalogDataForRequirement( vsReqCatalogTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply, vsReqCatalogAttribIds ) );

            Integer vsReqStaticDel = Integer.parseInt( partsOfCellContent[3].split( "," )[0] );
            Integer vsReqOutReachDel = Integer.parseInt( partsOfCellContent[3].split( "," )[1] );

            String catalogOption_DataelementIds = vsReqStaticDel + "," + vsReqOutReachDel;

            String[] dataelementDataParts = partsOfCellContent[1].split( "," );
            Map<String, Integer> catalogOption_DataelementMap = new HashMap<String, Integer>();

            for ( String de_catalogOption : dataelementDataParts )
            {
                catalogOption_DataelementMap.put( de_catalogOption.split( ":" )[1], Integer.parseInt( de_catalogOption
                    .split( ":" )[0] ) );
                catalogOption_DataelementIds += "," + Integer.parseInt( de_catalogOption.split( ":" )[0] );
            }

            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getDataElementDataForCatalogOptionsForRequirement( orgUnitIdsByComma, catalogOption_DataelementIds,
                    periodId ) );

            String orgUnitGroupAttribIds = partsOfCellContent[2];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager
                .getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();
                numberOfData.put( "Area", orgUnit.getParent().getParent().getParent().getShortName() );
                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    numberOfData.put( "Facility Type", " " );
                }
                else
                {
                    numberOfData.put( "Facility Type", orgUnitGroupName );
                }

                Double vsrActualValue = catalogSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByInventoryTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                for ( Integer catalogId : catalogIdsForRequirement )
                {
                    Double vsReqUsageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqUsage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqUsageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqUsageData = 0.0;
                        }
                    }

                    Double vsReqTargetPopData = 0.0;
                    String vsReqTargetPopCatData = catalogDataForRequirement.get( catalogId + ":" + vsReqTargetPopCat );
                    if ( vsReqTargetPopCatData != null )
                    {
                        Integer deId = catalogOption_DataelementMap.get( vsReqTargetPopCatData );
                        tempStr = dataElementDataForRequirement.get( deId + ":" + periodId + ":" + orgUnit.getId() );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqTargetPopData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqTargetPopData = 0.0;
                            }
                        }
                    }

                    Double vsReqDosesData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDoses );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqDosesData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqDosesData = 0.0;
                        }
                    }

                    Double vsReqPackedVolData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqPackedVol );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqPackedVolData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqPackedVolData = 0.0;
                        }
                    }

                    String tempStr1 = dataElementDataForRequirement.get( vsReqStaticDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    String tempStr2 = dataElementDataForRequirement.get( vsReqOutReachDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    if ( (tempStr1 != null && tempStr1.equalsIgnoreCase( "true" ))
                        || (tempStr2 != null && tempStr2.equalsIgnoreCase( "true" )) )
                    {
                        Double vsReqDiluentVolData = 0.0;
                        tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqDiluentVol );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqDiluentVolData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqDiluentVolData = 0.0;
                            }
                        }

                        vsReqPackedVolData += vsReqDiluentVolData;
                    }

                    Double vsReqWastageData = 0.0;
                    tempStr = catalogDataForRequirement.get( catalogId + ":" + vsReqWastage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqWastageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqWastageData = 0.0;
                        }
                    }

                    Double vsReqSupplyIntervalData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqSupplyIntervalData = 0.0;
                        }
                    }

                    Double vsReqReserveStockData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqReserveStockData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqReserveStockData = 0.0;
                        }
                    }

                    // Formula for calculating Requirement for individual
                    // vaccine
                    Double individualVaccineRequirement = 0.0;
                    try
                    {
                        individualVaccineRequirement = ((vsReqUsageData * vsReqTargetPopData) / 100) * vsReqDosesData
                            * vsReqPackedVolData * (1 / (1 - (vsReqWastageData / 100)))
                            * (((vsReqSupplyIntervalData + vsReqReserveStockData) / 52) / 1000);
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating individualVaccineRequirement" );
                        individualVaccineRequirement = 0.0;
                    }

                    vaccineRequirement += individualVaccineRequirement;

                }

                vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Required", vaccineRequirement + "" );

                Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
                diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Difference", "" + diffVaccineReq );

                Double diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;
                if ( diffPercentage < -30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "1" );
                }
                else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "1" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "1" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "1" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else
                {
                    numberOfData.put( ">30%-1", "1" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }

                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 34 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "VACCINE_STORAGE_CAPACITY.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );

        }

        // Energy for Cooling
        else if ( ccemReport.getReportType().equals( CCEMReport.EQUIPMENT_BY_AVAILABILITY_OF_ELECTRICITY ) )
        {
            hash.put( "reportName", "Equipment by availability of electricity" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer electricityId = Integer.parseInt( ccemCellContent.split( ":" )[2] );
            Integer bottledGasId = Integer.parseInt( ccemCellContent.split( ":" )[3] );
            Integer keroseneId = Integer.parseInt( ccemCellContent.split( ":" )[4] );
            Integer solarId = Integer.parseInt( ccemCellContent.split( ":" )[5] );

            DataElement dataElemnet = dataElementService.getDataElement( electricityId );
            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 8 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Equipment Type", "Equipment Type", String.class.getName(), 120, true );
            
            if ( dataElemnet.getOptionSet() != null )
            {
                List<String> options = new ArrayList<String>( dataElemnet.getOptionSet().getOptions() );
                for ( String option : options )
                {
                    frb.addColumn( option, dataElemnet.getId()+"_"+option, String.class.getName(), 50, true );                    
                }
                frb.setColspan( 1, options.size(), "Availability of Electricity" );
            }
            
            //Map<String, String> equipmentMap = ccemReportManager.getEquipmentNameWithOrgUnit( inventoryTypeId,
            //    catalogTypeAttributeId, orgUnitIdsByComma );
            //TreeMap<String, String> sortedEquipmentMap = new TreeMap<String, String>( equipmentMap );
            Map<String, Map<String,Integer>> equiplmentType_ElectricityAvailability_CountMap = new HashMap<String, Map<String,Integer>>( 
            		ccemReportManager.getEquipmentType_ElectricityAvailability_Count( equipmentTypeId, modelTypeAttributeId, electricityId, periodIdsByComma, orgUnitIdsByComma ) );
            List<String> equiplmentTypeList = new ArrayList<String>( equiplmentType_ElectricityAvailability_CountMap.keySet() );
            Collections.sort( equiplmentTypeList );
            for ( String equipment : equiplmentTypeList )
            {
            	Map<String,Integer> electricityAvailabilityMap = new HashMap<String, Integer>( equiplmentType_ElectricityAvailability_CountMap.get( equipment ) );
                Map<String, String> numberOfData = new HashMap<String, String>();
                List<String> options = new ArrayList<String>( dataElemnet.getOptionSet().getOptions() );
                numberOfData.put( "Equipment Type", equipment );
                for ( String option : options )
                {
                	Integer count = 0;
                	if( electricityAvailabilityMap.get( option ) != null )
                	{
                		count = electricityAvailabilityMap.get( option );
                	}
                    numberOfData.put( dataElemnet.getId()+"_"+option, count + "" );
                }
                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Energy_for_cooling.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.SUMMARY_OF_ABSORPTION_REFRIGERATORS_EXISTING_IN_fACILITIES ) )
        {
            hash.put( "reportName", "Summary of absorption refrigerators existing in facilities with >8/24 electricity per day" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );

            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            String[] ccemCellContentParts = ccemCellContent.split( "--" );
            Integer dataelementId = Integer.parseInt( ccemCellContentParts[0].split( ":" )[0] );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContentParts[0].split( ":" )[1] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContentParts[0].split( ":" )[2] );

            String electricity8to16hrsOption = ccemCellContentParts[1].split( "," )[0];
            String electricityMorethan16hrsOption = ccemCellContentParts[1].split( "," )[1];
            String electricityGas = ccemCellContentParts[1].split( "," )[2];
            String electricityKerosene = ccemCellContentParts[1].split( "," )[3];
            
            electricity8to16hrsOption = electricity8to16hrsOption.toLowerCase();
            electricityMorethan16hrsOption = electricityMorethan16hrsOption.toLowerCase();
            electricityGas = electricityGas.toLowerCase();
            electricityKerosene = electricityKerosene.toLowerCase();

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Area", "Area", String.class.getName(), 100, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 100, true );
            frb.addColumn( "Total Number of Refrigerators", "Total Number of Refrigerators", String.class.getName(), 100, true );
            frb.addColumn( "#", "#", String.class.getName(), 50, true );
            frb.addColumn( "%", "%", String.class.getName(), 50, true );
            frb.setColspan( 3, 2, "Absorption Refrigerators" );

            Collections.sort( orgunitGroupList );
            for ( Integer orgUnitId : selOrgUnitList )
            {
                List<OrganisationUnit> orgUnitChildren = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
                
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    Map<String, String> numberOfData = new HashMap<String, String>();
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                    orgUnitGroupMembers.retainAll( orgUnitChildren );
                    Collection<Integer> orgUnitGroupMemberIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitGroupMembers ) );
                    String orgUnitidsByComma = getCommaDelimitedString( orgUnitGroupMemberIds );

                    if( orgUnitGroupMemberIds != null && orgUnitGroupMemberIds.size() > 0 )
                    {
	                    List<String> facilityList = ccemReportManager.getDataValueFacility( dataelementId, "\'" + electricity8to16hrsOption
	                        + "\',\'" + electricityMorethan16hrsOption + "\'", orgUnitidsByComma, periodIdsByComma );
	                    String facilityByComma = getCommaDelimitedString( facilityList );
	
	                    if( facilityList != null && facilityList.size() > 0 )
	                    {
		                    //Integer totalValue = ccemReportManager.getCatalogDataValueCount( inventoryTypeId, catalogTypeAttributeId, null, facilityByComma );
	                    	Integer totalValue = ccemReportManager.getEquipmentInstanceCount( equipmentTypeId , facilityByComma );
	                    	
		                    Integer absorptionValue = ccemReportManager.getCatalogDataValueCount( equipmentTypeId, modelTypeAttributeId, "\'"
		                        + electricityGas + "\',\'" + electricityKerosene + "\'", facilityByComma );
		
		                    if ( totalValue != 0 )
		                    {
		                        Double percentValue = (Double.valueOf( absorptionValue ) / Double.valueOf( totalValue )) * 100;
		                        percentValue = Math.round( percentValue * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
		
		                        numberOfData.put( "Area", orgUnit.getName() );
		                        numberOfData.put( "Facility Type", orgUnitGroup.getName() );
		                        numberOfData.put( "Total Number of Refrigerators", totalValue + "" );
		                        if ( absorptionValue != 0 )
		                        {
		                            numberOfData.put( "#", absorptionValue + "" );
		                        }
		                        else
		                        {
		                            numberOfData.put( "#", "0" );
		                        }
		                        numberOfData.put( "%", percentValue + "" );
		                        tableData.add( numberOfData );
		                    }
	                    }
                    }
                }
            }
            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 44 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Energy_for_cooling.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.ALL_ENERGY_AVAILABILITY_AT_FACILITIES ) )
        {
            hash.put( "reportName", "Energy availability at facilities" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );

            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );

            Integer dataelementId1 = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer dataelementId2 = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer dataelementId3 = Integer.parseInt( ccemCellContent.split( ":" )[2] );

            DataElement dataElement1 = dataElementService.getDataElement( dataelementId1 );
            DataElement dataElement2 = dataElementService.getDataElement( dataelementId2 );
            DataElement dataElement3 = dataElementService.getDataElement( dataelementId3 );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Admin Area", "Admin Area", String.class.getName(), 50, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 50, true );
            frb.addColumn( "Total Facilities", "Total Facilities", String.class.getName(), 35, true );

            /*
             * frb.addColumn( "None", "None", String.class.getName(), 28, true
             * ); frb.addColumn( "%", "None %", String.class.getName(), 28, true
             * );
             * 
             * frb.addColumn( "< 8 hours", "< 8 hours", String.class.getName(),
             * 28, true ); frb.addColumn( "%", "< 8 hours%",
             * String.class.getName(), 28, true );
             * 
             * frb.addColumn( "8 to 16 hours", "8 to 16 hours",
             * String.class.getName(), 28, true ); frb.addColumn( "%",
             * "8 to 16 hours%", String.class.getName(), 28, true );
             * 
             * frb.addColumn( "> 16 hours", "> 16 hours",
             * String.class.getName(), 28, true ); frb.addColumn( "%",
             * "> 16 hours%", String.class.getName(), 28, true );
             * 
             * frb.addColumn( "Available : Clean", "Available : Clean",
             * String.class.getName(), 28, true ); frb.addColumn( "%",
             * "Available : Clean%", String.class.getName(), 28, true );
             * 
             * frb.addColumn( "Available: Dirty", "Available: Dirty",
             * String.class.getName(), 28, true ); frb.addColumn( "%",
             * "Available: Dirty%", String.class.getName(), 28, true );
             * 
             * frb.addColumn( "Not Available", "Not Available kero",
             * String.class.getName(), 28, true ); frb.addColumn( "%",
             * "Not Available kero%", String.class.getName(), 28, true );
             * 
             * frb.addColumn( "Unknown", "Unknown", String.class.getName(), 28,
             * true ); frb.addColumn( "%", "Unknown%", String.class.getName(),
             * 28, true );
             * 
             * frb.addColumn( "Available : Reliable", "Available : Reliable",
             * String.class.getName(), 28, true ); frb.addColumn( "%",
             * "Available : Reliable%", String.class.getName(), 28, true );
             * 
             * frb.addColumn( "Available : Unreliable",
             * "Available : Unreliable", String.class.getName(), 28, true );
             * frb.addColumn( "%", "Available : Unreliable%",
             * String.class.getName(), 28, true );
             * 
             * frb.addColumn( "Not Available", "Not Available gas",
             * String.class.getName(), 28, true ); frb.addColumn( "%",
             * "Not Available gas%", String.class.getName(), 28, true );
             * 
             * frb.addColumn( "Unknown", "Unknown gas", String.class.getName(),
             * 28, true ); frb.addColumn( "%", "Unknown gas%",
             * String.class.getName(), 28, true );
             */

            List<String> dataElement1Options = new ArrayList<String>();
            List<String> dataElement2Options = new ArrayList<String>();
            List<String> dataElement3Options = new ArrayList<String>();

            if ( dataElement1.getOptionSet() != null )
            {
                dataElement1Options = new ArrayList<String>( dataElement1.getOptionSet().getOptions() );
                for ( String option : dataElement1Options )
                {
                    frb.addColumn( option, dataElement1.getId()+"_"+option, String.class.getName(), 28, true );
                    frb.addColumn( "%", dataElement1.getId()+"_"+option + "_%", String.class.getName(), 28, true );
                }
            }

            if ( dataElement2.getOptionSet() != null )
            {
                dataElement2Options = new ArrayList<String>( dataElement2.getOptionSet().getOptions() );
                for ( String option : dataElement2Options )
                {
                    frb.addColumn( option, dataElement2.getId()+"_"+option, String.class.getName(), 28, true );
                    frb.addColumn( "%", dataElement2.getId()+"_"+option + "_%", String.class.getName(), 28, true );
                }
            }

            if ( dataElement3.getOptionSet() != null )
            {
                dataElement3Options = new ArrayList<String>( dataElement3.getOptionSet().getOptions() );
                for ( String option : dataElement3Options )
                {
                    frb.addColumn( option, dataElement3.getId()+"_"+option, String.class.getName(), 28, true );
                    frb.addColumn( "%", dataElement3.getId()+"_"+option + "_%", String.class.getName(), 28, true );
                }
            }

            int colCount = 3;
            frb.setColspan( colCount, dataElement1Options.size() * 2, "Availability of Electricity" );
            colCount += dataElement1Options.size() * 2;
            frb.setColspan( colCount, dataElement2Options.size() * 2, "Kerosene Availability" );
            colCount += dataElement2Options.size() * 2;
            frb.setColspan( colCount, dataElement3Options.size() * 2, "Gas Availability" );

            // frb.setColspan( 3, 8, "Availability of Electricity" );
            // frb.setColspan( 11, 8, "Kerosene Availability" );
            // frb.setColspan( 19, 8, "Gas Availability" );

            for ( Integer orgUnitId : selOrgUnitList )
            {
                List<OrganisationUnit> orgUnitChildren = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    // OrganisationUnit organisationUnit =
                    // organisationUnitService.getOrganisationUnit( orgUnit );

                    // List<Integer> orgUnitId = new ArrayList<Integer>();
                    // List<Integer> orgUnitGrpId = new ArrayList<Integer>();

                    // orgUnitId.add( orgUnit );
                    // orgUnitGrpId.add( orgUnitGroupId );

                    // String orgUnitidsByComma =
                    // ccemReportManager.getOrgunitIdsByComma( orgUnitId,
                    // orgUnitGrpId );

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                    orgUnitGroupMembers.retainAll( orgUnitChildren );
                    Collection<Integer> orgUnitGroupMemberIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitGroupMembers ) );
                    String orgUnitidsByComma = getCommaDelimitedString( orgUnitGroupMemberIds );
                    Integer totalFacilities = orgUnitGroupMembers.size();

                    /*
                     * Integer totalFacilities = 0;
                     * 
                     * Integer facilityNone = 0; Double percentNone = 0.0;
                     * Integer facilityLessThan8 = 0; Double percentLessThan8 =
                     * 0.0; Integer facility8to16 = 0; Double percent8to16 =
                     * 0.0; Integer facilityGreaterThan16 = 0; Double
                     * percentGreaterThan16 = 0.0;
                     * 
                     * Integer keroseneClean = 0; Double percentClean = 0.0;
                     * Integer keroseneDirty = 0; Double percentDirty = 0.0;
                     * Integer keroseneNot = 0; Double percentNot = 0.0; Integer
                     * keroseneUnknown = 0; Double percentUnknown = 0.0;
                     * 
                     * Integer gasReliable = 0; Double percentReliable = 0.0;
                     * Integer gasUnreliable = 0; Double percentUnreliable =
                     * 0.0; Integer gasNotAvailable = 0; Double
                     * percentNotAvailable = 0.0; Integer gasUnknown = 0; Double
                     * percentgasUnknown = 0.0;
                     */

                    // totalFacilities = getTotalFacilitiesWithOrgUnit(
                    // orgUnitidsByComma );
                    // totalFacilities =
                    // ccemReportManager.getCountByOrgUnitGroup( orgUnit,
                    // orgUnitGroupId );
                    if ( totalFacilities != 0 )
                    {
                        Map<String, String> numberOfData = new HashMap<String, String>();

                        /*
                         * facilityNone = ccemReportManager.getDataValue(
                         * dataelementId1 + "", "none", orgUnitidsByComma, null
                         * ); percentNone = (Double.valueOf( facilityNone ) /
                         * Double.valueOf( totalFacilities )) * 100; percentNone
                         * = Math.round( percentNone * Math.pow( 10, 2 ) ) /
                         * Math.pow( 10, 2 );
                         * 
                         * facilityLessThan8 = ccemReportManager.getDataValue(
                         * dataelementId1 + "", "<8hrs/8 to 16hrs/24hrs4hrs",
                         * orgUnitidsByComma, null ); percentLessThan8 =
                         * (Double.valueOf( facilityLessThan8 ) /
                         * Double.valueOf( totalFacilities )) * 100;
                         * percentLessThan8 = Math.round( percentLessThan8 *
                         * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                         * 
                         * facility8to16 = ccemReportManager.getDataValue(
                         * dataelementId1 + "", "8 to 16hrs/24hrs",
                         * orgUnitidsByComma, null ); percent8to16 =
                         * (Double.valueOf( facility8to16 ) / Double.valueOf(
                         * totalFacilities )) * 100; percent8to16 = Math.round(
                         * percent8to16 * Math.pow( 10, 2 ) ) / Math.pow( 10, 2
                         * );
                         * 
                         * facilityGreaterThan16 =
                         * ccemReportManager.getDataValue( dataelementId1 + "",
                         * "More than 16hrs/24hrs", orgUnitidsByComma, null );
                         * percentGreaterThan16 = (Double.valueOf(
                         * facilityGreaterThan16 ) / Double .valueOf(
                         * totalFacilities )) * 100; percentGreaterThan16 =
                         * Math.round( percentGreaterThan16 * Math.pow( 10, 2 )
                         * ) / Math.pow( 10, 2 );
                         * 
                         * keroseneClean = ccemReportManager.getDataValue(
                         * dataelementId2 + "", "Available and clean",
                         * orgUnitidsByComma, null ); percentClean =
                         * (Double.valueOf( keroseneClean ) / Double.valueOf(
                         * totalFacilities )) * 100; percentClean = Math.round(
                         * percentClean * Math.pow( 10, 2 ) ) / Math.pow( 10, 2
                         * );
                         * 
                         * keroseneDirty = ccemReportManager.getDataValue(
                         * dataelementId2 + "", "Available but dirty",
                         * orgUnitidsByComma, null ); percentDirty =
                         * (Double.valueOf( keroseneDirty ) / Double.valueOf(
                         * totalFacilities )) * 100; percentDirty = Math.round(
                         * percentDirty * Math.pow( 10, 2 ) ) / Math.pow( 10, 2
                         * );
                         * 
                         * keroseneNot = ccemReportManager.getDataValue(
                         * dataelementId2 + "", "Not available",
                         * orgUnitidsByComma, null ); percentNot =
                         * (Double.valueOf( keroseneNot ) / Double.valueOf(
                         * totalFacilities )) * 100; percentNot = Math.round(
                         * percentNot * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                         * 
                         * keroseneUnknown = ccemReportManager.getDataValue(
                         * dataelementId2 + "", "Unknown", orgUnitidsByComma,
                         * null ); percentUnknown = (Double.valueOf(
                         * keroseneUnknown ) / Double.valueOf( totalFacilities
                         * )) * 100; percentUnknown = Math.round( percentUnknown
                         * * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                         * 
                         * gasReliable = ccemReportManager.getDataValue(
                         * dataelementId3 + "", "Always available",
                         * orgUnitidsByComma, null ); percentReliable =
                         * (Double.valueOf( gasReliable ) / Double.valueOf(
                         * totalFacilities )) * 100; percentReliable =
                         * Math.round( percentReliable * Math.pow( 10, 2 ) ) /
                         * Math.pow( 10, 2 );
                         * 
                         * gasUnreliable = ccemReportManager.getDataValue(
                         * dataelementId3 + "", "Sometimes available",
                         * orgUnitidsByComma, null ); percentUnreliable =
                         * (Double.valueOf( gasUnreliable ) / Double.valueOf(
                         * totalFacilities )) * 100; percentUnreliable =
                         * Math.round( percentUnreliable * Math.pow( 10, 2 ) ) /
                         * Math.pow( 10, 2 );
                         * 
                         * gasNotAvailable = ccemReportManager.getDataValue(
                         * dataelementId3 + "", "Not available",
                         * orgUnitidsByComma, null ); percentNotAvailable =
                         * (Double.valueOf( gasNotAvailable ) / Double.valueOf(
                         * totalFacilities )) * 100; percentNotAvailable =
                         * Math.round( percentNotAvailable * Math.pow( 10, 2 ) )
                         * / Math.pow( 10, 2 );
                         * 
                         * gasUnknown = ccemReportManager.getDataValue(
                         * dataelementId3 + "", "Unknown", orgUnitidsByComma,
                         * null ); percentgasUnknown = (Double.valueOf(
                         * gasUnknown ) / Double.valueOf( totalFacilities )) *
                         * 100; percentgasUnknown = Math.round(
                         * percentgasUnknown * Math.pow( 10, 2 ) ) / Math.pow(
                         * 10, 2 );
                         */

                        Map<String, Integer> dataValueCountMap = new HashMap<String, Integer>( ccemReportManager.getDataValueAndCount( dataelementId1 + "", orgUnitidsByComma, periodIdsByComma ) );

                        if ( dataElement1.getOptionSet() != null )
                        {
                            List<String> options = new ArrayList<String>( dataElement1.getOptionSet().getOptions() );
                            for ( String option : options )
                            {
                                Integer optionValueCount = dataValueCountMap.get( option );
                                if ( optionValueCount == null )
                                {
                                    optionValueCount = 0;
                                }

                                numberOfData.put( dataElement1.getId()+"_"+option, optionValueCount + "" );
                                double percentageOfOptionValueCount = 0.0;
                                try
                                {
                                    percentageOfOptionValueCount = (double) optionValueCount / (double) totalFacilities * 100.0;
                                    percentageOfOptionValueCount = Math.round( percentageOfOptionValueCount * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                                }
                                catch ( Exception e )
                                {
                                    percentageOfOptionValueCount = 0.0;
                                }
                                numberOfData.put( dataElement1.getId()+"_"+option + "_%", percentageOfOptionValueCount + "" );
                            }
                        }

                        dataValueCountMap = new HashMap<String, Integer>( ccemReportManager.getDataValueAndCount(
                            dataelementId2 + "", orgUnitidsByComma, periodIdsByComma ) );

                        if ( dataElement2.getOptionSet() != null )
                        {
                            List<String> options = new ArrayList<String>( dataElement2.getOptionSet().getOptions() );
                            for ( String option : options )
                            {
                                Integer optionValueCount = dataValueCountMap.get( option );
                                if ( optionValueCount == null )
                                {
                                    optionValueCount = 0;
                                }

                                numberOfData.put( dataElement2.getId()+"_"+option, optionValueCount + "" );
                                double percentageOfOptionValueCount = 0.0;
                                try
                                {
                                    percentageOfOptionValueCount = (double) optionValueCount / (double) totalFacilities
                                        * 100.0;
                                    percentageOfOptionValueCount = Math.round( percentageOfOptionValueCount
                                        * Math.pow( 10, 2 ) )
                                        / Math.pow( 10, 2 );
                                }
                                catch ( Exception e )
                                {
                                    percentageOfOptionValueCount = 0.0;
                                }
                                numberOfData.put( dataElement2.getId()+"_"+option + "_%", percentageOfOptionValueCount + "" );
                            }
                        }

                        dataValueCountMap = new HashMap<String, Integer>( ccemReportManager.getDataValueAndCount(
                            dataelementId3 + "", orgUnitidsByComma, periodIdsByComma ) );

                        if ( dataElement3.getOptionSet() != null )
                        {
                            List<String> options = new ArrayList<String>( dataElement3.getOptionSet().getOptions() );
                            for ( String option : options )
                            {
                                Integer optionValueCount = dataValueCountMap.get( option );
                                if ( optionValueCount == null )
                                {
                                    optionValueCount = 0;
                                }

                                numberOfData.put( dataElement3.getId()+"_"+option, optionValueCount + "" );
                                double percentageOfOptionValueCount = 0.0;
                                try
                                {
                                    percentageOfOptionValueCount = (double) optionValueCount / (double) totalFacilities
                                        * 100.0;
                                    percentageOfOptionValueCount = Math.round( percentageOfOptionValueCount
                                        * Math.pow( 10, 2 ) )
                                        / Math.pow( 10, 2 );
                                }
                                catch ( Exception e )
                                {
                                    percentageOfOptionValueCount = 0.0;
                                }
                                numberOfData.put( dataElement3.getId()+"_"+option + "_%", percentageOfOptionValueCount + "" );
                            }
                        }

                        numberOfData.put( "Admin Area", orgUnit.getName() );
                        numberOfData.put( "Facility Type", orgUnitGroup.getName() );
                        numberOfData.put( "Total Facilities", totalFacilities + "" );

                        /*
                         * numberOfData.put( "None", facilityNone + "" );
                         * numberOfData.put( "None %", percentNone + "" );
                         * numberOfData.put( "< 8 hours", facilityLessThan8 + ""
                         * ); numberOfData.put( "< 8 hours%", percentLessThan8 +
                         * "" ); numberOfData.put( "8 to 16 hours",
                         * facility8to16 + "" ); numberOfData.put(
                         * "8 to 16 hours%", percent8to16 + "" );
                         * numberOfData.put( "> 16 hours", facilityGreaterThan16
                         * + "" ); numberOfData.put( "> 16 hours%",
                         * percentGreaterThan16 + "" );
                         * 
                         * numberOfData.put( "Available : Clean", keroseneClean
                         * + "" ); numberOfData.put( "Available : Clean%",
                         * percentClean + "" ); numberOfData.put(
                         * "Available: Dirty", keroseneDirty + "" );
                         * numberOfData.put( "Available: Dirty%", percentDirty +
                         * "" ); numberOfData.put( "Not Available kero",
                         * keroseneNot + "" ); numberOfData.put(
                         * "Not Available kero%", percentNot + "" );
                         * numberOfData.put( "Unknown", keroseneUnknown + "" );
                         * numberOfData.put( "Unknown%", percentUnknown + "" );
                         * 
                         * numberOfData.put( "Available : Reliable", gasReliable
                         * + "" ); numberOfData.put( "Available : Reliable%",
                         * percentReliable + "" ); numberOfData.put(
                         * "Available : Unreliable", gasUnreliable + "" );
                         * numberOfData.put( "Available : Unreliable%",
                         * percentUnreliable + "" ); numberOfData.put(
                         * "Not Available gas", gasNotAvailable + "" );
                         * numberOfData.put( "Not Available gas%",
                         * percentNotAvailable + "" ); numberOfData.put(
                         * "Unknown gas", gasUnknown + "" ); numberOfData.put(
                         * "Unknown gas%", percentgasUnknown + "" );
                         */
                        tableData.add( numberOfData );
                    }

                }
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 80 );
            frb.setColumnsPerPage( 1, 50 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "All_Energy_type.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.COLD_ROOM_BY_MODEL_AND_WORKING_STATUS ) )
        {
            hash.put( "reportName", "Cold Rooms by model and working status" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer workingStatusInventoryAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[2] );
            Integer modelInventoryAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[3] );
            Integer manufacturerInventoryAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[4] );
            Integer equipmentId1 = Integer.parseInt( ccemCellContent.split( ":" )[5] );
            Integer equipmentId2 = Integer.parseInt( ccemCellContent.split( ":" )[6] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 8 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Area", "Area", String.class.getName(), 60, true );
            frb.addColumn( "Facility Type", "Facility Type", String.class.getName(), 70, true );
            frb.addColumn( "Model Name", "Model Name", String.class.getName(), 50, true );
            frb.addColumn( "Manufacturer", "Manufacturer", String.class.getName(), 50, true );
            frb.addColumn( "Equipment Type", "Equipment Type", String.class.getName(), 60, true );
            frb.addColumn( "Total", "Total", String.class.getName(), 50, true );
            frb.addColumn( "#", "Working#", String.class.getName(), 35, true );
            frb.addColumn( "%", "Working%", String.class.getName(), 35, true );
            frb.addColumn( "#", "Working Needs Service#", String.class.getName(), 35, true );
            frb.addColumn( "%", "Working Needs Service%", String.class.getName(), 35, true );
            frb.addColumn( "#", "Not Working#", String.class.getName(), 35, true );
            frb.addColumn( "%", "Not Working%", String.class.getName(), 35, true );
            frb.setColspan( 6, 2, "Working" );
            frb.setColspan( 8, 2, "Working Needs Service" );
            frb.setColspan( 10, 2, "Not Working" );

            for ( Integer orgUnit : selOrgUnitList )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    Map<String, String> numberOfData = new HashMap<String, String>();
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnit );

                    List<Integer> orgUnitId = new ArrayList<Integer>();
                    List<Integer> orgUnitGrpId = new ArrayList<Integer>();

                    orgUnitId.add( orgUnit );
                    orgUnitGrpId.add( orgUnitGroupId );

                    String orgUnitidsByComma = ccemReportManager.getOrgunitIdsByComma( orgUnitId, orgUnitGrpId );

                    Map<String, String> inventoryMap = ccemReportManager
                        .getTotalColdRoomValue( inventoryTypeId, orgUnitidsByComma, (workingStatusInventoryAttributeId
                            + "," + modelInventoryAttributeId + "," + manufacturerInventoryAttributeId + ","
                            + equipmentId1 + "," + equipmentId2 + ",27,28,28,30,32,33,34,35"), null );

                    /*
                     * if ( inventoryMap.size() > 0 ) { Integer count = 1;
                     * 
                     * for ( String inventoryTypeAttribute :
                     * inventoryMap.keySet() ) { numberOfData.put( "Area",
                     * organisationUnit.getName() ); numberOfData.put(
                     * "Facility Type", orgUnitGroup.getName() ); if (
                     * inventoryTypeAttribute.equals( modelInventoryAttributeId
                     * +"-"+count ) ) { numberOfData.put( "Model Name",
                     * inventoryMap.get( inventoryTypeAttribute ) + "" ); } else
                     * if ( inventoryTypeAttribute.equals(
                     * manufacturerInventoryAttributeId +"-"+count ) ) {
                     * numberOfData.put( "Manufacturer", inventoryMap.get(
                     * inventoryTypeAttribute ) + "" );
                     * 
                     * } else if ( inventoryTypeAttribute.equals( equipmentId1
                     * +"-"+count ) || inventoryTypeAttribute.equals(
                     * equipmentId2 +"-"+count ) ||
                     * inventoryTypeAttribute.equals( 27 +"-"+count ) ||
                     * inventoryTypeAttribute.equals( 28 +"-"+count ) ||
                     * inventoryTypeAttribute .equals( 29 +"-"+count ) ||
                     * inventoryTypeAttribute .equals( 30 +"-"+count ) ||
                     * inventoryTypeAttribute .equals( 32 +"-"+count) ||
                     * inventoryTypeAttribute .equals( 33 +"-"+count ) ||
                     * inventoryTypeAttribute .equals( 34 +"-"+count ) ||
                     * inventoryTypeAttribute .equals( 35 +"-"+count) ) {
                     * numberOfData.put( "Equipment Type", inventoryMap.get(
                     * inventoryTypeAttribute ) + "" );
                     * 
                     * } else if ( inventoryTypeAttribute.equals(
                     * workingStatusInventoryAttributeId +"-"+count ) ) {
                     * numberOfData.put( "Total", inventoryMap.get(
                     * inventoryTypeAttribute ) + "" ); } tableData.add(
                     * numberOfData ); count++; }
                     * 
                     * }
                     */
                }
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 50 );
            frb.setColumnsPerPage( 1, 50 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Cold_Room.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.LISTING_OF_COLD_ROOM_FACILITIES_AND_WORKING_STATUS ) )
        {
            hash.put( "reportName", "Listing of cold room facilities and working status" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "area", "area", String.class.getName(), 60, true );
            frb.addColumn( "ft_name", "ft_name", String.class.getName(), 80, true );
            frb.addColumn( "ft_model_name", "ft_model_name", String.class.getName(), 60, true );
            frb.addColumn( "type", "type", String.class.getName(), 60, true );
            frb.addColumn( "Total", "Total", String.class.getName(), 60, true );
            frb.addColumn( "#", "Working#", String.class.getName(), 40, true );
            frb.addColumn( "%", "Working%", String.class.getName(), 40, true );
            frb.addColumn( "#", "Working Needs Service#", String.class.getName(), 40, true );
            frb.addColumn( "%", "Working Needs Service%", String.class.getName(), 40, true );
            frb.addColumn( "#", "Not Working#", String.class.getName(), 40, true );
            frb.addColumn( "%", "Not Working%", String.class.getName(), 40, true );
            frb.addColumn( "Serial Number", "Serial Number", String.class.getName(), 40, true );
            frb.addColumn( "Has generator", "Has generator", String.class.getName(), 40, true );
            frb.setColspan( 5, 2, "Working" );
            frb.setColspan( 7, 2, "Working Needs Service" );
            frb.setColspan( 9, 2, "Not Working" );
            for ( Integer orgUnit : selOrgUnitList )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    Map<String, String> numberOfData = new HashMap<String, String>();
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnit );

                    List<Integer> orgUnitId = new ArrayList<Integer>();
                    List<Integer> orgUnitGrpId = new ArrayList<Integer>();

                    orgUnitId.add( orgUnit );
                    orgUnitGrpId.add( orgUnitGroupId );

                    String orgUnitidsByComma = ccemReportManager.getOrgunitIdsByComma( orgUnitId, orgUnitGrpId );
                    numberOfData.put( "area", organisationUnit.getName() );
                    numberOfData.put( "ft_name", orgUnitGroup.getName() );
                    tableData.add( numberOfData );
                }
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 40 );
            frb.setColumnsPerPage( 1, 50 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Cold_Room.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.COLD_ROOM_QUALITY_ATTRIBUTES ) )
        {
            hash.put( "reportName", "Cold room quality attributes" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 7 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 6 );

            FastReportBuilder frb = new FastReportBuilder();

            frb.addColumn( "Facility Name", "Facility Name", String.class.getName(), 50, true );
            frb.addColumn( "Facility Code", "Facility Code", String.class.getName(), 50, true );
            frb.addColumn( "Serial Number", "Facility Name", String.class.getName(), 50, true );
            frb.addColumn( "Model Name", "Facility Code", String.class.getName(), 50, true );
            frb.addColumn( "Temperature Recording System", "Temperature Recording System", String.class.getName(), 50,
                true );
            frb.addColumn( "Standby Generator", "Facility Code", String.class.getName(), 50, true );
            frb.addColumn( "Standby Regulator", "Standby Regulator", String.class.getName(), 50, true );

            for ( Integer orgUnit : selOrgUnitList )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    Map<String, String> numberOfData = new HashMap<String, String>();
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnit );

                    List<Integer> orgUnitId = new ArrayList<Integer>();
                    List<Integer> orgUnitGrpId = new ArrayList<Integer>();

                    orgUnitId.add( orgUnit );
                    orgUnitGrpId.add( orgUnitGroupId );

                    String orgUnitidsByComma = ccemReportManager.getOrgunitIdsByComma( orgUnitId, orgUnitGrpId );
                    numberOfData.put( "Facility Name", organisationUnit.getName() );
                    numberOfData.put( "Facility Code", organisationUnit.getCode() );
                    tableData.add( numberOfData );
                }
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 40 );
            frb.setColumnsPerPage( 1, 50 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Cold_Room.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }

        // Cold boxes and vaccine carries

        else if ( ccemReport.getReportType().equals( CCEMReport.COLD_BOX_AND_VACCINE_CARRIERS_BY_MODEL_AND_WORKING_STATUS ) )
        {
            hash.put( "reportName", "Cold box and vaccine carriers by model and working status" );

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer qtyPresentEquipmentTypeAttributeId =  Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer qtyNotWorkingEquipmentTypeAttributeId =  Integer.parseInt( ccemCellContent.split( ":" )[2] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[3] );

            List tableData = new ArrayList();

            Font f1 = new Font();
            f1.setFontSize( 8 );
            f1.isBold();

            Font f2 = new Font();
            f2.setFontSize( 7 );

            FastReportBuilder frb = new FastReportBuilder();
            frb.addColumn( "Model Name", "Model Name", String.class.getName(), 60, true );
            int colNumber = 1;
            int colQuantity = 2;

            List<OrganisationUnitGroup> selOrgUnitGroupList = new ArrayList<OrganisationUnitGroup>();
            for ( int i = 0; i <= orgunitGroupList.size() - 1; i++ )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( i ) );
                frb.addColumn( "W", "W" + orgUnitGroup.getName(), String.class.getName(), 20, true );
                frb.addColumn( "NW", "NW" + orgUnitGroup.getName(), String.class.getName(), 20, true );
                selOrgUnitGroupList.add( orgUnitGroup );
            }
            for ( int i = 0; i <= orgunitGroupList.size() - 1; i++ )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( i ) );
                frb.setColspan( colNumber, colQuantity, orgUnitGroup.getShortName() );
                colNumber = colNumber + colQuantity;
            }

            Map<String, Integer> orgUnitGroupwiseQtyPresentMap = new HashMap<String, Integer>();
            Map<String, Integer> orgUnitGroupwiseQtyNowWorkingMap = new HashMap<String, Integer>();
            for ( OrganisationUnitGroup orgUnitGroup : selOrgUnitGroupList )
            {
                List<Integer> orgUnitGrpIdList = new ArrayList<Integer>();
                orgUnitGrpIdList.add( orgUnitGroup.getId() );

                String orgUnitidsByComma = ccemReportManager.getOrgunitIdsByComma( selOrgUnitList, orgUnitGrpIdList );
                    		
                Map<String, Integer> qtyPresentMap = ccemReportManager.getSumOfColdBoxByOrgUnitGroup( equipmentTypeId, qtyPresentEquipmentTypeAttributeId, modelTypeAttributeId, orgUnitidsByComma, orgUnitGroup.getId() );
                Map<String, Integer> qtyNotWorkingMap = ccemReportManager.getSumOfColdBoxByOrgUnitGroup( equipmentTypeId, qtyNotWorkingEquipmentTypeAttributeId, modelTypeAttributeId, orgUnitidsByComma, orgUnitGroup.getId() );
                
                if( qtyPresentMap != null )
                {
                	orgUnitGroupwiseQtyPresentMap.putAll( qtyPresentMap );
                }
                
                if( qtyNotWorkingMap != null )
                {
                	orgUnitGroupwiseQtyNowWorkingMap.putAll( qtyNotWorkingMap );
                }
            }
            
            List<String> modelsList = ccemReportManager.getModelName( equipmentTypeId, modelTypeAttributeId, orgUnitIdsByComma );
            for( String model : modelsList )
            {
                Integer total = 0;
                Map<String, String> numberOfData = new HashMap<String, String>();
                numberOfData.put( "Model Name", model );

                for ( OrganisationUnitGroup orgUnitGroup : selOrgUnitGroupList )
                {
                	Integer qtyPresent = orgUnitGroupwiseQtyPresentMap.get( orgUnitGroup.getId()+":"+ model );
                	Integer qtyNotWorking = orgUnitGroupwiseQtyNowWorkingMap.get( orgUnitGroup.getId() +":"+ model );
                	
                	if( qtyPresent == null ) qtyPresent = 0;
                	if( qtyNotWorking == null ) qtyNotWorking = 0;
                	
                	Integer qtyWorking = qtyPresent - qtyNotWorking;
                	
                	numberOfData.put( "W" + orgUnitGroup.getName(), qtyWorking+"" );
                    numberOfData.put( "NW" + orgUnitGroup.getName(), qtyNotWorking+"" );
                    System.out.println( model + " - " + orgUnitGroup.getName() + " - " + qtyPresent + " - " + qtyWorking + " - " + qtyNotWorking);
                }

                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 90 );
            frb.setColumnsPerPage( 1, 40 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows( true );
            frb.setTemplateFile( path + "Distribution of Refrigerators_freezers by model and facility type.jrxml" );

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultHeaderStyle().setFont( f1 );
            dynamicReport.getOptions().getDefaultHeaderStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setFont( f2 );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.COLD_BOX_AND_VACCINE_CARRIERS_BY_MODEL_AND_WORKING_STATUS_BAR ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer qtyPresentEquipmentTypeAttributeId =  Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer qtyNotWorkingEquipmentTypeAttributeId =  Integer.parseInt( ccemCellContent.split( ":" )[2] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[3] );

            
            Map<String, Double> qtyPresentMap = ccemReportManager.getSumOfEquipmentAndCatalogValue( equipmentTypeId, qtyPresentEquipmentTypeAttributeId, modelTypeAttributeId, orgUnitIdsByComma );
            Map<String, Double> qtyNotWorkingMap = ccemReportManager.getSumOfEquipmentAndCatalogValue( equipmentTypeId, qtyNotWorkingEquipmentTypeAttributeId, modelTypeAttributeId, orgUnitIdsByComma );

            //Map<String, Integer> workingEquipmentMap = (ccemReportManager.getModelNameAndCountForColdBox( catalogTypeAttributeId, inventoryTypeId, 1 + "", orgUnitIdsByComma ));
            //Map<String, Integer> notWorkingEquipmentMap = (ccemReportManager.getModelNameAndCountForColdBox( catalogTypeAttributeId, inventoryTypeId, 0 + "", orgUnitIdsByComma ));

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            List<String> modelsList = ccemReportManager.getModelName( equipmentTypeId, modelTypeAttributeId, orgUnitIdsByComma );
            for ( String model : modelsList )
            {
                if ( qtyPresentMap.containsKey( model ) )
                {
                    dataset.setValue( qtyPresentMap.get( model ), "Working", model );
                }
                else
                {
                    dataset.setValue( 0, "Working", model );
                }
                
                if ( qtyNotWorkingMap.containsKey( model ) )
                {
                    dataset.setValue( qtyNotWorkingMap.get( model ), "Not Working", model );
                }
                else
                {
                    dataset.setValue( 0, "Not Working", model );
                }
            }
            hash.put( "chart", createBarChart2( dataset, "Cold box and vaccine carriers by model and working status", "", "", PlotOrientation.HORIZONTAL ) );
            fileName = "Bar_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.QUANTITY_OF_COLD_BOXES_OR_CARRIERS ) )
        {
            hash.put( "reportName", "Quantity of Cold Boxes/carriers" );
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer equipmentTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[2] );

            Map<String, Double> quantityMap = ccemReportManager.getSumOfEquipmentAndCatalogValue( equipmentTypeId, equipmentTypeAttributeId, modelTypeAttributeId, orgUnitIdsByComma );
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for ( String catalogValue : quantityMap.keySet() )
            {
                dataset.setValue( quantityMap.get( catalogValue ), "Quantity Of Cold Box and carriers", catalogValue );
            }
            hash.put( "chart", createSimpleBarChart( dataset, "Quantity of Cold Boxes/carriers", "", "", PlotOrientation.HORIZONTAL ) );
            fileName = "Bar_Refrigerator_freezer_utilization.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.ORGUNITGROUP_DATAVALUE ) )
        {
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List tableData = new ArrayList();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List<String> content = new ArrayList<String>();

            FastReportBuilder frb = new FastReportBuilder();
            Integer periodId = 0;
            Date date1 = new Date();
            calendar = Calendar.getInstance();
            calendar.setTime( date1 );
            periodStartDate = "";
            Map<String, Integer> subHeadingNumber = new HashMap<String, Integer>();
            if ( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
                periodStartDate = (calendar.get( Calendar.YEAR ) - 1) + "-01-01";
            }

            periodId = ccemReportManager.getPeriodId( periodStartDate, ccemReport.getPeriodRequire() );
            tableHeadings.add( "Facility Type" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "Total Facilities" );
            oneSubHeadingRow.add( " " );
            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";
            List<String> dataElementOptions = new ArrayList<String>();

            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );

                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;
                tableHeadings.add( ccemReportDesign1.getDisplayheading() );
                List<String> distinctDataElementValues = new ArrayList<String>( ccemReportManager
                    .getDistinctDataElementValue( dataElementId, optComboId, periodId ) );
                int number = 0;
                for ( int i = 0; i < distinctDataElementValues.size(); i++ )
                {
                    if ( i != 0 )
                    {
                        tableHeadings.add( " " );
                    }
                    oneSubHeadingRow.add( distinctDataElementValues.get( i ).split( ":" )[2] );
                    dataElementOptions.add( distinctDataElementValues.get( i ) );
                    number++;
                }
                subHeadingNumber.put( ccemReportDesign1.getDisplayheading(), number );
            }

            tableSubHeadings.add( oneSubHeadingRow );
            int count = 0;

            for ( int i = 0; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {
                }
                else
                {
                    if ( i == 0 || i == 1 )
                    {
                        frb.addColumn( tableHeadings.get( i ), tableHeadings.get( i ), String.class.getName(), 100,
                            true );
                        count++;

                    }
                    else
                    {
                    }
                }
            }
            for ( int j = 0; j <= tableSubHeadings.size() - 1; j++ )
            {
                for ( int k = 0; k <= tableSubHeadings.get( j ).size() - 1; k++ )
                {
                    if ( tableSubHeadings.get( j ).get( k ) == " " )
                    {
                    }
                    else
                    {
                        frb.addColumn( tableSubHeadings.get( j ).get( k ), tableSubHeadings.get( j ).get( k ),
                            String.class.getName(), 50, true );
                        content.add( tableSubHeadings.get( j ).get( k ) );
                        count++;
                    }
                }
            }
            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage( 1, count ).setUseFullPageWidth( true );

            int start = 2;
            for ( int i = 2; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {

                }
                else
                {
                    frb.setColspan( start, subHeadingNumber.get( tableHeadings.get( i ) ), tableHeadings.get( i ) );
                    start = start + subHeadingNumber.get( tableHeadings.get( i ) );
                }
            }
            frb.setTemplateFile( path + "ORGUNITGROUP_DATAVALUE.jrxml" );
            frb.setPrintBackgroundOnOddRows( true );
            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                Map numberOfData = new HashMap();
                List<Integer> orgUnitIds = ccemReportManager.getOrgunitIds( selOrgUnitList, orgUnitGroupId );
                if ( orgUnitIds == null || orgUnitIds.size() <= 0 )
                {

                }
                else
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );

                    String orgUnitIdsBycomma = getCommaDelimitedString( orgUnitIds );
                    numberOfData.put( "Facility Type", orgUnitGroup.getName() );
                    numberOfData.put( "Total Facilities", "" + orgUnitIds.size() );
                    Map<String, Integer> dataValueCountMap2 = new HashMap<String, Integer>( ccemReportManager
                        .getDataValueCountforDataElements( dataElementIdsByComma, optComboIdsByComma, periodId,
                            orgUnitIdsBycomma ) );
                    for ( int i = 0; i <= dataElementOptions.size() - 1; i++ )
                    {
                        Integer temp = dataValueCountMap2.get( dataElementOptions.get( i ) );
                        if ( temp == null )
                        {
                            numberOfData.put( content.get( i ), "0" );
                        }
                        else
                        {
                            numberOfData.put( content.get( i ), temp + "" );
                        }
                    }
                    tableData.add( numberOfData );
                }
            }

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }

        else if ( ccemReport.getReportType().equals( CCEMReport.ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE ) )
        {
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List tableData = new ArrayList();
            List<String> content = new ArrayList<String>();
            Map<String, Integer> subHeadingNumber = new HashMap<String, Integer>();

            tableHeadings.add( "OrgUnit Hierarchy" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "OrgUnit" );
            oneSubHeadingRow.add( " " );

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yy" );

            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";

            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );

                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;

                tableHeadings.add( ccemReportDesign1.getDisplayheading() );
                int i = 0;
                for ( Period period : periodList )
                {
                    oneSubHeadingRow.add( simpleDateFormat.format( period.getStartDate() ) );
                    if ( i != 0 )
                        tableHeadings.add( " " );
                    i++;
                }
                subHeadingNumber.put( ccemReportDesign1.getDisplayheading(), i );
            }

            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitGroupId );
                orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }

            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            Map<String, Integer> equipmentDataValueMap = new HashMap<String, Integer>( ccemReportManager
                .getFacilityWiseEquipmentRoutineData( orgUnitIdsByComma, periodIdsByComma, dataElementIdsByComma,
                    optComboIdsByComma ) );

            FastReportBuilder frb = new FastReportBuilder();
            tableSubHeadings.add( oneSubHeadingRow );

            int count = 0;

            for ( int i = 0; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {
                }
                else
                {
                    if ( i == 0 || i == 1 )
                    {
                        content.add( tableHeadings.get( i ) );
                        frb
                            .addColumn( tableHeadings.get( i ), tableHeadings.get( i ), String.class.getName(), 50,
                                true );
                        count++;

                    }
                    else
                    {
                    }
                }
            }
            for ( int j = 0; j <= tableSubHeadings.size() - 1; j++ )
            {
                int increment = 0;

                for ( int k = 0; k <= tableSubHeadings.get( j ).size() - 1; k++ )
                {

                    if ( tableSubHeadings.get( j ).get( k ) == " " )
                    {
                    }
                    else
                    {
                        if ( tableSubHeadings.get( j ).get( k ).contains( tableSubHeadings.get( j ).get( k ) ) )
                        {
                            increment++;
                        }

                        frb.addColumn( tableSubHeadings.get( j ).get( k ), tableSubHeadings.get( j ).get( k ) + " "
                            + increment, String.class.getName(), 50, true );
                        content.add( tableSubHeadings.get( j ).get( k ) + " " + increment );
                        count++;
                    }
                }
            }
            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage( 1, count ).setUseFullPageWidth( true );

            int start = 2;
            for ( int i = 2; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {

                }
                else
                {
                    frb.setColspan( start, subHeadingNumber.get( tableHeadings.get( i ) ), tableHeadings.get( i ) );
                    start = start + subHeadingNumber.get( tableHeadings.get( i ) );
                }
            }

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();
                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }

                numberOfData.put( content.get( 0 ), orgUnitBranch );
                numberOfData.put( content.get( 1 ), orgUnit.getName() );
                int i = 2;
                for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
                {
                    String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                    Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );

                    for ( Period period : periodList )
                    {
                        Integer temp = equipmentDataValueMap.get( orgUnit.getId() + ":" + dataElementId + ":"
                            + period.getId() );
                        if ( temp == null )
                        {
                            numberOfData.put( content.get( i ), " " );
                        }
                        else
                        {
                            numberOfData.put( content.get( i ), temp + "" );
                        }
                        i++;
                    }

                }
                tableData.add( numberOfData );
            }

            frb.setTemplateFile( path + "ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE.jrxml" );
            frb.setPrintBackgroundOnOddRows( true );
            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }

        ServletOutputStream ouputStream = response.getOutputStream();
        JRExporter exporter = null;
        if ( "pdf".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/pdf" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.pdf\"" );
            exporter = new JRPdfExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "rtf".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/rtf" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.rtf\"" );

            exporter = new JRRtfExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "html".equalsIgnoreCase( type ) )
        {
            response.setContentType( "text/html" );
            exporter = new JRHtmlExporter();
            exporter.setParameter( JRHtmlExporterParameter.OUTPUT_STREAM, false );
            exporter.setParameter( JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean( false ) );
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "xls".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/vnd.ms-excel" );
            // response.addHeader("Content-disposition","attachment; filename=fileName=\"file.xls\"");
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.xls\"" );

            exporter = new JExcelApiExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE );
            exporter.setParameter( JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE );
        }
        else if ( "csv".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/csv" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.csv\"" );

            exporter = new JRCsvExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        con.close();
        try
        {
            exporter.exportReport();
        }
        catch ( JRException e )
        {
            throw new ServletException( e );
        }

        return SUCCESS;
    }

    private String getOrgunitBranch( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " -> " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }

    public BufferedImage createPieChart( DefaultPieDataset dataset, String title )
    {

        JFreeChart chart = ChartFactory.createPieChart( title, dataset, true, true, false );

        chart.setBorderVisible( false );
        chart.getTitle().setFont( new java.awt.Font( "SansSerif", java.awt.Font.PLAIN, 24 ) );

        chart.setBackgroundPaint( Color.WHITE );
        chart.setBorderPaint( Color.WHITE );
        // chart.setBorderStroke( new BasicStroke( 10.0f ) );

        LegendTitle legend = chart.getLegend();
        legend.setPosition( RectangleEdge.BOTTOM );
        legend.setItemFont( new java.awt.Font( "SansSerif", java.awt.Font.PLAIN, 20 ) );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setCircular( true );

        plot.setLabelLinkStyle( PieLabelLinkStyle.QUAD_CURVE );
        plot.setShadowXOffset( 0 );
        plot.setShadowYOffset( 0 );

        RectangleInsets ri = chart.getPadding();

        ri = new RectangleInsets( ri.getTop(), ri.getBottom(), ri.getLeft(), ri.getRight() );
        chart.setPadding( ri );

        plot.setLabelBackgroundPaint( Color.WHITE );
        plot.setBackgroundPaint( Color.WHITE );
        plot.setLabelFont( new java.awt.Font( "SansSerif", 0, 18 ) );

        Color[] colors = { Color.decode( "#EE2A2A" ), Color.decode( "#2A78EE" ), Color.decode( "#2AEE3D" ),
            Color.decode( "#EEEE2A" ), Color.decode( "#EE822A" ), Color.decode( "#2AEEEE" ), Color.decode( "#EE2ACD" ),
            Color.decode( "#CFCBCE" ), Color.decode( "#4F314A" ), Color.decode( "#6F9FB5" ), Color.decode( "#10501E" ),
            Color.decode( "#888525" ), Color.decode( "#704097" ), Color.decode( "#67400A" ), Color.decode( "#EBC794" ),
            Color.decode( "#E7B9E9" ), Color.decode( "#2E3B84" ), Color.decode( "#A1CF9F" ), Color.decode( "#C06872" ) };
        PieRenderer renderer = new PieRenderer( colors );
        renderer.setColor( plot, dataset );

        chart.getPlot().setOutlineVisible( false );

        plot.setCircular( true );

        StandardPieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator( "{1} ({2})" );
        plot.setLabelGenerator( labelGenerator );

        BufferedImage image = chart.createBufferedImage( 800, 800 );

        return image;
    }

    public BufferedImage createBarChart( DefaultCategoryDataset dataset, String title, String sideLabel,
        String downLabel, PlotOrientation plotOrientation )
    {

        JFreeChart chart = ChartFactory.createStackedBarChart( title, sideLabel, downLabel, dataset, plotOrientation,
            true, true, true );

        chart.setBackgroundPaint( Color.white );

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.gray );
        plot.setRangeGridlinePaint( Color.gray );

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions( CategoryLabelPositions.UP_90 );
        domainAxis.setVisible( true );

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline( true );
        renderer.setBaseItemLabelsVisible( true );
        renderer.setShadowVisible( false );
        renderer.setMaximumBarWidth( .05 );

        // set up gradient paints for series...
        plot.getRenderer().setSeriesPaint( 0, new Color( 13, 178, 2 ) );
        plot.getRenderer().setSeriesPaint( 1, new Color( 255, 0, 0 ) );
        plot.getRenderer().setSeriesPaint( 2, new Color( 0, 2, 202 ) );
        plot.getRenderer().setSeriesPaint( 3, new Color( 223, 19, 202 ) );
        plot.getRenderer().setSeriesPaint( 4, new Color( 204, 204, 0 ) );
        plot.getRenderer().setSeriesPaint( 5, new Color( 170, 0, 6 ) );

        chart.getCategoryPlot().setRenderer( renderer );
        chart.getCategoryPlot().setDomainAxis( domainAxis );
        chart.getCategoryPlot().setRangeAxis( rangeAxis );
        BufferedImage image = chart.createBufferedImage( 800, 600 );
        return image;
    }

    public BufferedImage createSimpleBarChart( DefaultCategoryDataset dataset, String title, String sideLabel,
        String downLabel, PlotOrientation plotOrientation )
    {

        JFreeChart chart = ChartFactory.createBarChart( title, sideLabel, downLabel, dataset, plotOrientation, true,
            true, true );

        chart.setBackgroundPaint( Color.white );

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.gray );
        plot.setRangeGridlinePaint( Color.gray );
        plot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );

        // left align the category labels...
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible( true );

        CategoryLabelPositions p = domainAxis.getCategoryLabelPositions();

        CategoryLabelPosition left = new CategoryLabelPosition( RectangleAnchor.RIGHT, TextBlockAnchor.CENTER_RIGHT,
            TextAnchor.CENTER_RIGHT, 0.0, CategoryLabelWidthType.RANGE, 0.60f );

        domainAxis.setCategoryLabelPositions( CategoryLabelPositions.replaceLeftPosition( p, left ) );

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline( true );
        renderer.setShadowVisible( false );
        renderer.setMaximumBarWidth( 0.05 );
        renderer.setBaseItemLabelFont( new java.awt.Font( "SansSerif", java.awt.Font.PLAIN, 12 ) );

        // set up gradient paints for series...
        plot.getRenderer().setSeriesPaint( 0, new Color( 13, 178, 2 ) );
        plot.getRenderer().setSeriesPaint( 1, new Color( 255, 140, 0 ) );
        plot.getRenderer().setSeriesPaint( 2, new Color( 255, 20, 147 ) );
        plot.getRenderer().setSeriesPaint( 3, new Color( 30, 144, 255 ) );
        plot.getRenderer().setSeriesPaint( 4, new Color( 255, 215, 0 ) );
        plot.getRenderer().setSeriesPaint( 5, new Color( 148, 0, 211 ) );

        LegendTitle legend = chart.getLegend();
        legend.setPosition( RectangleEdge.TOP );
        legend.setHorizontalAlignment( HorizontalAlignment.CENTER );

        BufferedImage image = chart.createBufferedImage( 800, 600 );
        return image;
    }

    public BufferedImage createBarChart2( DefaultCategoryDataset dataset, String title, String sideLabel,
        String downLabel, PlotOrientation plotOrientation )
    {

        JFreeChart chart = ChartFactory.createStackedBarChart( title, sideLabel, downLabel, dataset, plotOrientation,
            true, true, true );

        chart.setBackgroundPaint( Color.white );

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.gray );
        plot.setRangeGridlinePaint( Color.gray );
        plot.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );

        // left align the category labels...
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible( true );

        CategoryLabelPositions p = domainAxis.getCategoryLabelPositions();

        CategoryLabelPosition left = new CategoryLabelPosition( RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT,
            TextAnchor.CENTER_LEFT, 0.0, CategoryLabelWidthType.RANGE, 0.30f );

        domainAxis.setCategoryLabelPositions( CategoryLabelPositions.replaceLeftPosition( p, left ) );

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline( true );
        renderer.setShadowVisible( false );
        renderer.setMaximumBarWidth( .05 );
        // set up gradient paints for series...

        plot.getRenderer().setSeriesPaint( 0, new Color( 20, 27, 239 ) );
        plot.getRenderer().setSeriesPaint( 1, new Color( 239, 232, 20 ) );
        plot.getRenderer().setSeriesPaint( 2, new Color( 35, 239, 20 ) );
        plot.getRenderer().setSeriesPaint( 3, new Color( 239, 71, 20 ) );
        plot.getRenderer().setSeriesPaint( 4, new Color( 117, 74, 61 ) );
        plot.getRenderer().setSeriesPaint( 5, new Color( 61, 87, 117 ) );

        LegendTitle legend = chart.getLegend();
        legend.setPosition( RectangleEdge.TOP );
        legend.setHorizontalAlignment( HorizontalAlignment.CENTER );

        BufferedImage image = chart.createBufferedImage( 800, 600 );
        return image;
    }

    static class PieRenderer
    {
        private Color[] color;

        public PieRenderer( Color[] color )
        {
            this.color = color;
        }

        public void setColor( PiePlot plot, DefaultPieDataset dataset )
        {
            List<Comparable> keys = dataset.getKeys();
            int aInt;

            for ( int i = 0; i < keys.size(); i++ )
            {
                aInt = i % this.color.length;
                plot.setSectionPaint( keys.get( i ), this.color[aInt] );
            }
        }
    }

   

}
