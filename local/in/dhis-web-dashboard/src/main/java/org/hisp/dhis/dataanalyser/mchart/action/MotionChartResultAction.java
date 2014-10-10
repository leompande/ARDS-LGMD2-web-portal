package org.hisp.dhis.dataanalyser.mchart.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.MotionChart;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class MotionChartResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

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

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String xaxisIndicator;

    public void setXaxisIndicator( String xaxisIndicator )
    {
        this.xaxisIndicator = xaxisIndicator;
    }

    private String yaxisIndicator;

    public void setYaxisIndicator( String yaxisIndicator )
    {
        this.yaxisIndicator = yaxisIndicator;
    }

    private String zaxisDataelements;

    public void setZaxisDataelements( String zaxisDataelements )
    {
        this.zaxisDataelements = zaxisDataelements;
    }

    private String ougSetCB;

    public void setOugSetCB( String ougSetCB )
    {
        this.ougSetCB = ougSetCB;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private String periodTypeLB;

    public void setPeriodTypeLB( String periodTypeLB )
    {
        this.periodTypeLB = periodTypeLB;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private Indicator xAxisInd;

    public Indicator getXAxisInd()
    {
        return xAxisInd;
    }

    private Indicator yAxisInd;

    public Indicator getYAxisInd()
    {
        return yAxisInd;
    }

    private DataElement zaxisDE;

    public DataElement getZaxisDE()
    {
        return zaxisDE;
    }

    private List<MotionChart> mcList;

    public List<MotionChart> getMcList()
    {
        return mcList;
    }

    private Date sDate;

    private Date eDate;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        mcList = new ArrayList<MotionChart>();

        // Orgunit Info

        List<Object> orgUnitList = new ArrayList<Object>();
        if ( facilityLB.equals( "children" ) )
        {
            if ( ougSetCB == null )
            {
                OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB
                    .get( 0 ) ) );
                orgUnitList = new ArrayList<Object>( ou.getChildren() );
            }
            else
            {
                orgUnitList = new ArrayList<Object>();
                for ( String tmp : orgUnitListCB )
                {
                    OrganisationUnitGroup oug = organisationUnitGroupService.getOrganisationUnitGroup( Integer
                        .parseInt( tmp ) );
                    List<OrganisationUnit> tempOUList = new ArrayList<OrganisationUnit>( oug.getMembers() );
                    Collections.sort( tempOUList, new IdentifiableObjectNameComparator() );
                    orgUnitList.addAll( tempOUList );
                }
            }
        }
        else
        {
            for ( String tmp : orgUnitListCB )
            {
                int id = Integer.parseInt( tmp );

                if ( ougSetCB == null )
                {
                    orgUnitList.add( organisationUnitService.getOrganisationUnit( id ) );
                }
                else
                {
                    orgUnitList.add( organisationUnitGroupService.getOrganisationUnitGroup( id ) );
                }
            }
        }

        // Period Info
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );
        PeriodType selPeriodType = periodService.getPeriodTypeByName( periodTypeLB );
        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( selPeriodType, sDate,
            eDate ) );

        // Axis Info
        xAxisInd = indicatorService.getIndicator( Integer.parseInt( xaxisIndicator ) );
        yAxisInd = indicatorService.getIndicator( Integer.parseInt( yaxisIndicator ) );
        zaxisDE = dataElementService.getDataElement( Integer.parseInt( zaxisDataelements ) );

        DataElementCategoryOptionCombo zaxisDECOC = zaxisDE.getCategoryCombo().getOptionCombos().iterator().next();

        Iterator orgUnitListIterator = orgUnitList.iterator();
        while ( orgUnitListIterator.hasNext() )
        {
            OrganisationUnit childOrgUnit = new OrganisationUnit();
            OrganisationUnitGroup childOrgUnitGroup = new OrganisationUnitGroup();

            if ( ougSetCB == null || facilityLB.equals( "children" ) )
            {
                childOrgUnit = (OrganisationUnit) orgUnitListIterator.next();
            }
            else
            {
                childOrgUnitGroup = (OrganisationUnitGroup) orgUnitListIterator.next();
            }

            Iterator<Period> periodListIterator = periodList.iterator();
            while ( periodListIterator.hasNext() )
            {
                Period period = periodListIterator.next();

                Calendar cal1 = Calendar.getInstance();
                cal1.setTime( period.getStartDate() );

                String dateStr = cal1.get( Calendar.YEAR ) + "," + cal1.get( Calendar.MONTH ) + ","
                    + cal1.get( Calendar.DATE );
                Calendar zaxisStartCal = Calendar.getInstance();
                Calendar zaxisEndCal = Calendar.getInstance();

                if ( cal1.get( Calendar.MONTH ) < Calendar.APRIL )
                {
                    zaxisStartCal.set( cal1.get( Calendar.YEAR ) - 1, Calendar.JANUARY, 1, 0, 0, 0 );
                    zaxisEndCal.set( cal1.get( Calendar.YEAR ) - 1, Calendar.DECEMBER, 31, 0, 0, 0 );
                }
                else
                {
                    zaxisStartCal.set( cal1.get( Calendar.YEAR ), Calendar.JANUARY, 1, 0, 0, 0 );
                    zaxisEndCal.set( cal1.get( Calendar.YEAR ), Calendar.DECEMBER, 31, 0, 0, 0 );
                }

                Date zaxisStateDate;
                Date zaxisEndDate;

                PeriodType pt = getDataElementPeriodType( zaxisDE );
                if ( pt != null && pt.getName().equalsIgnoreCase( "yearly" ) )
                {
                    zaxisStateDate = zaxisStartCal.getTime();
                    zaxisEndDate = zaxisEndCal.getTime();
                }
                else
                {
                    zaxisStateDate = period.getStartDate();
                    zaxisEndDate = period.getEndDate();
                }

                Double xaxisValue = 0.0;
                Double yaxisValue = 0.0;
                Double zaxisValue = 0.0;

                if ( ougSetCB == null || facilityLB.equals( "children" ) )
                {
                    xaxisValue = aggregationService.getAggregatedIndicatorValue( xAxisInd, period.getStartDate(),
                        period.getEndDate(), childOrgUnit );
                    yaxisValue = aggregationService.getAggregatedIndicatorValue( yAxisInd, period.getStartDate(),
                        period.getEndDate(), childOrgUnit );
                    zaxisValue = aggregationService.getAggregatedDataValue( zaxisDE, zaxisDECOC, zaxisStateDate,
                        zaxisEndDate, childOrgUnit );
                }
                else
                {
                    List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>( childOrgUnitGroup.getMembers() );
                    Iterator<OrganisationUnit> orgUnitsIterator = orgUnits.iterator();
                    while ( orgUnitsIterator.hasNext() )
                    {
                        OrganisationUnit ou = (OrganisationUnit) orgUnitsIterator.next();

                        double tempX = aggregationService.getAggregatedIndicatorValue( xAxisInd, period.getStartDate(),
                            period.getEndDate(), ou );
                        double tempY = aggregationService.getAggregatedIndicatorValue( yAxisInd, period.getStartDate(),
                            period.getEndDate(), ou );
                        double tempZ = aggregationService.getAggregatedDataValue( zaxisDE, zaxisDECOC, zaxisStateDate,
                            zaxisEndDate, ou );

                        if ( tempX == -1 )
                            tempX = 0.0;
                        if ( tempY == -1 )
                            tempY = 0.0;
                        if ( tempZ == -1 )
                            tempZ = 0.0;

                        xaxisValue += tempX;
                        yaxisValue += tempY;
                        zaxisValue += tempZ;
                    }
                }

                xaxisValue = Math.round( xaxisValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                yaxisValue = Math.round( yaxisValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                zaxisValue = Math.round( zaxisValue * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );

                if ( ougSetCB == null || facilityLB.equals( "children" ) )
                {
                    MotionChart mc = new MotionChart( childOrgUnit.getName(), dateStr, xaxisValue, yaxisValue,
                        zaxisValue );
                    mcList.add( mc );
                }
                else
                {
                    MotionChart mc = new MotionChart( childOrgUnitGroup.getName(), dateStr, xaxisValue, yaxisValue,
                        zaxisValue );
                    mcList.add( mc );
                }

            }// Period Loop end
        }// OrgUnit Loop end

        statementManager.destroy();
        return SUCCESS;
    }

    @SuppressWarnings( "unchecked" )
    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Iterator it = dataSetList.iterator();
        while ( it.hasNext() )
        {
            DataSet ds = (DataSet) it.next();
            List<DataElement> dataElementList = new ArrayList<DataElement>( ds.getDataElements() );
            if ( dataElementList.contains( de ) )
            {
                return ds.getPeriodType();
            }
        }

        return null;

    } // getDataElementPeriodType end

}
