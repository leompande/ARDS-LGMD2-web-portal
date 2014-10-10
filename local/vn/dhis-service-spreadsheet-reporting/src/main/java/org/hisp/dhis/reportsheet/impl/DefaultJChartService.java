package org.hisp.dhis.reportsheet.impl;

import java.util.Collection;

import org.hisp.dhis.jchart.JChart;
import org.hisp.dhis.jchart.JChartSevice;
import org.hisp.dhis.jchart.JChartStore;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultJChartService
    implements JChartSevice
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    private JChartStore jchartStore;

    public void setJchartStore( JChartStore jchartStore )
    {
        this.jchartStore = jchartStore;
    }
    
    

    // -------------------------------------------------------------------------
    // Implement
    // -------------------------------------------------------------------------

    @Override
    public int addJChart( JChart jchart )
    {
        PeriodType periodType = periodStore.getPeriodType( jchart.getPeriodType().getClass() );

        jchart.setPeriodType( periodType );

        return jchartStore.save( jchart );
    }

    @Override
    public void deleteJChart( int id )
    {
        jchartStore.delete( this.getJChart( id ) );
    }

    @Override
    public Collection<JChart> getALLJChart()
    {
        return jchartStore.getAll();
    }

    @Override
    public JChart getJChart( int id )
    {
        return jchartStore.get( id );
    }

    @Override
    public void updateJChart( JChart jchart )
    {
        PeriodType periodType = periodStore.getPeriodType( jchart.getPeriodType().getClass() );

        jchart.setPeriodType( periodType );

        jchartStore.update( jchart );
    }

    @Override
    public JChart getJChartByTitle( String title )
    {
        return jchartStore.getJChartByTitle( title );
    }
    

}
