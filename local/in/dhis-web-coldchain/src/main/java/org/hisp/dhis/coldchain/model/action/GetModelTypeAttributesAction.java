package org.hisp.dhis.coldchain.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeComparator;

import com.opensymphony.xwork2.Action;

public class GetModelTypeAttributesAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelTypeAttributeService modelTypeAttributeService;
    
    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------


    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }
    
    public String getKey()
    {
        return key;
    }

    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        modelTypeAttributes = new ArrayList<ModelTypeAttribute>(modelTypeAttributeService.getAllModelTypeAttributes());
        Collections.sort( modelTypeAttributes, new ModelTypeAttributeComparator() );
        
        /*
        if ( key != null )
        {
            modelTypeAttributes = IdentifiableObjectUtils.filterNameByKey( modelTypeAttributes, key, true );
        }

        Collections.sort( modelTypeAttributes, IdentifiableObjectNameComparator.INSTANCE );
        */
        
        
        /*
        if ( id != null && id != ALL )
        {
            DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( id );

            if ( dataElementGroup != null )
            {
                dataElements = new ArrayList<DataElement>( dataElementGroup.getMembers() );
            }
        }
        else if ( categoryComboId != null && categoryComboId != ALL )
        {
            DataElementCategoryCombo categoryCombo = categoryService.getDataElementCategoryCombo( categoryComboId );

            if ( categoryCombo != null )
            {
                dataElements = new ArrayList<DataElement>(
                    dataElementService.getDataElementByCategoryCombo( categoryCombo ) );
            }
        }
        else if ( dataSetId != null )
        {
            DataSet dataset = dataSetService.getDataSet( dataSetId );

            if ( dataset != null )
            {
                dataElements = new ArrayList<DataElement>( dataset.getDataElements() );
            }
        }
        else if ( periodTypeName != null )
        {
            PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );

            if ( periodType != null )
            {
                dataElements = new ArrayList<DataElement>( dataElementService.getDataElementsByPeriodType( periodType ) );
            }
        }
        else if ( domain != null )
        {
            dataElements = new ArrayList<DataElement>(
                dataElementService.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ) );
        }
        else
        {
            dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
            
            ContextUtils.clearIfNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), dataElements );
        }

        if ( key != null )
        {
            dataElements = IdentifiableObjectUtils.filterNameByKey( dataElements, key, true );
        }

        Collections.sort( dataElements, IdentifiableObjectNameComparator.INSTANCE );

        if ( aggregate )
        {
            FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        }
        */

        return SUCCESS;
    }

}
