package org.hisp.dhis.coldchain.model.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;

public class ModelTypeAttributeComparator
    implements Comparator<ModelTypeAttribute>
{
    public int compare( ModelTypeAttribute modelTypeAttribute0, ModelTypeAttribute modelTypeAttribute1 )
    {
        return modelTypeAttribute0.getName().compareToIgnoreCase( modelTypeAttribute1.getName() );
    }
}
