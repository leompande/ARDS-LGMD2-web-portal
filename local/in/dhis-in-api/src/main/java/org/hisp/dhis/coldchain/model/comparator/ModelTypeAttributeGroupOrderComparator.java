package org.hisp.dhis.coldchain.model.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelTypeAttributeGroupOrderComparator.javaOct 11, 2012 4:30:38 PM	
 */

public class ModelTypeAttributeGroupOrderComparator implements Comparator<ModelTypeAttributeGroup>
{
    public int compare( ModelTypeAttributeGroup group1, ModelTypeAttributeGroup group2 )
    {
        if ( group1.getModelType() != null && group2.getModelType() != null )
        {
            int modelType = group1.getModelType().getName().compareTo( group2.getModelType().getName() );
            
            if ( modelType != 0 )
            {
                return modelType;
            }
        }
        
        return group1.getSortOrder() - group2.getSortOrder();
    }
}
