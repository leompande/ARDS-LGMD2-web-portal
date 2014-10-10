package org.hisp.dhis.ccem.aggregation.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.coldchain.aggregation.CCEIAggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;

import com.opensymphony.xwork2.Action;

public class AddAggregationQueryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private CCEIAggregationService cceiAggregationService;

    public void setCceiAggregationService( CCEIAggregationService cceiAggregationService )
    {
        this.cceiAggregationService = cceiAggregationService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------

    private String name;

    private String aggType;

    private Integer dataElementId;

    public void setName( String name )
    {
        this.name = name;
    }

    public void setAggType( String aggType )
    {
        this.aggType = aggType;
    }

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        /**
         * TODO support for category option combo
         */
        if ( aggType.equals( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY ) )
        {
            CaseAggregationCondition condition = new CaseAggregationCondition( name, aggType, "NONE", dataElement, dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );

            aggregationConditionService.addCaseAggregationCondition( condition );    
        }
        else if ( aggType.equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL ) )
        {
            HttpServletRequest request = ServletActionContext.getRequest();
            
            Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
            
            String[] workingStatusValues = request.getParameterValues( lookup.getName() );
            String workingStatus = "";
            for( String workingStatusValue : workingStatusValues )
            {
                workingStatus += "'" + workingStatusValue + "',";
            }
            
            lookup = lookupService.getLookupByName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
            
            String modelName = request.getParameter( lookup.getName() );
            modelName = "'" + modelName + "'";
            Integer modelTypeAttributeId = Integer.parseInt( lookup.getValue() );
            
            workingStatus = workingStatus.substring( 0, workingStatus.length()-1 );
            
            lookup = lookupService.getLookupByName( Lookup.CCEI_REF_EQUIPMENTTYPE_ID );
            
            Integer equipmentTypeId = Integer.parseInt( lookup.getValue() );
            
            String query = cceiAggregationService.getQueryForRefrigeratorWorkingStatus( equipmentTypeId, modelTypeAttributeId, modelName, workingStatus );
            
            CaseAggregationCondition condition = new CaseAggregationCondition( name, aggType, query, dataElement, dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
            
            aggregationConditionService.addCaseAggregationCondition( condition );
        }
        else if ( aggType.equals( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION ) )
        {
            HttpServletRequest request = ServletActionContext.getRequest();
            
            Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_UTILIZATION_OPTIONSET );
            String utilization = request.getParameter( lookup.getName() );
            utilization = "'" + utilization  + "'";
            
            lookup = lookupService.getLookupByName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
            String modelName = request.getParameter( lookup.getName() );
            modelName = "'" + modelName + "'";
            Integer modelTypeAttributeId = Integer.parseInt( lookup.getValue() );

            lookup = lookupService.getLookupByName( Lookup.CCEI_REF_EQUIPMENTTYPE_ID );            
            Integer equipmentTypeId = Integer.parseInt( lookup.getValue() );

            String query = cceiAggregationService.getQueryForRefrigeratorUtilization( equipmentTypeId, modelTypeAttributeId, modelName,  utilization );
            
            CaseAggregationCondition condition = new CaseAggregationCondition( name, aggType, query, dataElement, dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
            
            aggregationConditionService.addCaseAggregationCondition( condition );
        }
        else if ( aggType.equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE ) )
        {
            HttpServletRequest request = ServletActionContext.getRequest();
            
            Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
            
            String[] workingStatusValues = request.getParameterValues( lookup.getName() );
            String workingStatus = "";
            for( String workingStatusValue : workingStatusValues )
            {
                workingStatus += "'" + workingStatusValue + "',";
            }
            
            lookup = lookupService.getLookupByName( Lookup.CCEI_EQUIPMENTTYPE_MODELTYPEATTRIBUTE );
            
            String modelName = request.getParameter( lookup.getName() );
            modelName = "'" + modelName + "'";
            Integer modelTypeAttributeId = Integer.parseInt( lookup.getValue() );
            
            workingStatus = workingStatus.substring( 0, workingStatus.length()-1 );
            
            lookup = lookupService.getLookupByName( Lookup.CCEI_REF_EQUIPMENTTYPE_ID );
            
            Integer equipmentTypeId = Integer.parseInt( lookup.getValue() );
            
            String query = cceiAggregationService.getQueryForRefrigeratorWorkingStatus( equipmentTypeId, modelTypeAttributeId, modelName, workingStatus );
            
            CaseAggregationCondition condition = new CaseAggregationCondition( name, aggType, query, dataElement, dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
            
            aggregationConditionService.addCaseAggregationCondition( condition );
        }


        return SUCCESS;
    }

}
