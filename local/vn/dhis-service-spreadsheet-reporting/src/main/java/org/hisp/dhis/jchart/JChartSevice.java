package org.hisp.dhis.jchart;

import java.util.Collection;


public interface JChartSevice
{
    final String ID = JChartSevice.class.getName();
    
    int addJChart( JChart jchart );
    
    void updateJChart( JChart jchart );
    
    void deleteJChart( int id );
    
    JChart getJChart( int id );

    JChart getJChartByTitle( String title );   
    
    Collection<JChart> getALLJChart();   
    
    
}
