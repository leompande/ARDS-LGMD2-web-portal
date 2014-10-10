package org.hisp.dhis.coldchain.model.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.model.Model;

public class ModelComparator implements Comparator<Model>
{
    public int compare( Model model0, Model model1 )
    {
        return model0.getName().compareToIgnoreCase( model1.getName() );
    }
}
