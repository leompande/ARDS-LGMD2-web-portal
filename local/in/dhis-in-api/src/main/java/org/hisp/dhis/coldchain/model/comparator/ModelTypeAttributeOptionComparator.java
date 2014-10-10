package org.hisp.dhis.coldchain.model.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelTypeAttributeOptionComparator.java Aug 1, 2012 12:57:58 PM	
 */
public class ModelTypeAttributeOptionComparator implements Comparator<ModelTypeAttributeOption>
{
    public int compare( ModelTypeAttributeOption modelTypeAttributeOption0, ModelTypeAttributeOption modelTypeAttributeOption1 )
    {
        return modelTypeAttributeOption0.getName().compareToIgnoreCase( modelTypeAttributeOption1.getName() );
    }
}
