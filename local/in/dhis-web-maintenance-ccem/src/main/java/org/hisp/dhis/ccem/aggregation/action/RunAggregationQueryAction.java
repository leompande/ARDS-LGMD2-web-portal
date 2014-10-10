package org.hisp.dhis.ccem.aggregation.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.coldchain.aggregation.CCEIAggregationService;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class RunAggregationQueryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    private CCEIAggregationService cceiAggregationService;

    public void setCceiAggregationService( CCEIAggregationService cceiAggregationService )
    {
        this.cceiAggregationService = cceiAggregationService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private String importStatus = "";

    public String getImportStatus()
    {
        return importStatus;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();

        Set<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );

        Set<OrganisationUnitGroup> orgUnitGroups = new HashSet<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );

        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) );

        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );

        if ( ouGroup != null )
        {
            orgUnitList.retainAll( ouGroup.getMembers() );
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMM" );
        String curMonth = simpleDateFormat.format( new Date() );
        Period period = PeriodType.getPeriodFromIsoString( curMonth );
        period = periodService.reloadPeriod( period );

        Set<CaseAggregationCondition> conditions = new HashSet<CaseAggregationCondition>( aggregationConditionService.getAllCaseAggregationCondition() );
        for ( CaseAggregationCondition condition : conditions )
        {
            DataElement dataElement = condition.getAggregationDataElement();
            
            if ( condition.getOperator().equals( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY ) )
            {
                aggregationResultMap.putAll( cceiAggregationService.calculateStorageCapacityData( period, dataElement, orgUnitList, orgUnitGroups ) );
            }
            else if ( condition.getOperator().equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL ) || condition.getOperator().equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE ) )
            {
                aggregationResultMap.putAll( cceiAggregationService.calculateRefrigeratorWorkingStatus( period, dataElement, orgUnitList, condition.getAggregationExpression() ) );
            }
            else if ( condition.getOperator().equals( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION ) )
            {
                aggregationResultMap.putAll( cceiAggregationService.calculateRefrigeratorUtilization( period, dataElement, orgUnitList, condition.getAggregationExpression() ) );
            }

            dataElements.add( dataElement );
        }

        for( String key : aggregationResultMap.keySet() )
        {
            System.out.println( key + " -- " + aggregationResultMap.get(  key ) );
        }
        
        //importStatus = cceiAggregationService.importData( aggregationResultMap, period );

        return SUCCESS;
    }

}
