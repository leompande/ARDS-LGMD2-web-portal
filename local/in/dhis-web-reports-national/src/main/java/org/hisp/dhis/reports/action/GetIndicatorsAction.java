package org.hisp.dhis.reports.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;

import com.opensymphony.xwork2.Action;

public class GetIndicatorsAction implements Action
{

    private final static int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private List<Indicator> indicators;

    public List<Indicator> getIndicators()
    {
        return indicators;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()throws Exception
    {
    
        if ( id == null || id == 0 )
        {
            indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        }
        else
        {
            IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( id );
    
            if ( indicatorGroup != null )
            {
                indicators = new ArrayList<Indicator>( indicatorGroup.getMembers() );
            }
            else
            {
                indicators = new ArrayList<Indicator>();
            }
        }
        Collections.sort( indicators, new IdentifiableObjectNameComparator() );
        return SUCCESS;
    
    }
    
}
