package org.hisp.dhis.coldchain.model.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.model.ModelType;

public class ModelTypeComparator
implements Comparator<ModelType>
{
    public int compare( ModelType modelType0, ModelType modelType1 )
    {
        return modelType0.getName().compareToIgnoreCase( modelType1.getName() );
    }
}
