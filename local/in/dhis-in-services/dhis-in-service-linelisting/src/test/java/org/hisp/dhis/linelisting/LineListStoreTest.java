package org.hisp.dhis.linelisting;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

public class LineListStoreTest
    extends DhisSpringTest
{
    private LineListStore lineListStore;
    
    private LineListElement elementA;
    private LineListElement elementB;
    
    @Override
    public void setUpTest()
    {
        lineListStore = (LineListStore) getBean( LineListStore.ID );
        
        elementA = new LineListElement( "ElementA", "ElementA", "DataType", "PresentationType" );
        elementB = new LineListElement( "ElementB", "ElementB", "DataType", "PresentationType" );
    }
    
    @Test
    public void add()
    {
        lineListStore.addLineListElement( elementA );
        lineListStore.addLineListElement( elementB );        
    }
}
