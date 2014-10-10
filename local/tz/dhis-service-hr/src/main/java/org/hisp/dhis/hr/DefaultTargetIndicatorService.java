package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultTargetIndicatorService
implements TargetIndicatorService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<TargetIndicator> targetIndicatorStore;

    public void setTargetIndicatorStore( GenericIdentifiableObjectStore<TargetIndicator> targetIndicatorStore )
    {
        this.targetIndicatorStore = targetIndicatorStore;
    }

    // -------------------------------------------------------------------------
    // TargetIndicator
    // -------------------------------------------------------------------------

    public int saveTargetIndicator( TargetIndicator targetIndicator )
    {
        return targetIndicatorStore.save( targetIndicator );
    }

    public void updateTargetIndicator( TargetIndicator targetIndicator )
    {
    	targetIndicatorStore.update( targetIndicator );
    }

    public void deleteTargetIndicator( TargetIndicator targetIndicator )
    {
    	targetIndicatorStore.delete( targetIndicator );
    }

    public Collection<TargetIndicator> getAllTargetIndicator()
    {
        return targetIndicatorStore.getAll();
    }

    public TargetIndicator getTargetIndicator( int id )
    {
        return targetIndicatorStore.get( id );
    }

    public TargetIndicator getTargetIndicatorByName( String name )
    {
        return targetIndicatorStore.getByName( name );
    }
    
    public Collection<TargetIndicator> getTargetIndicators( final Collection<Integer> identifiers )
    {
        Collection<TargetIndicator> targetIndicators = getAllTargetIndicator();

        return identifiers == null ? targetIndicators : FilterUtils.filter( targetIndicators, new Filter<TargetIndicator>()
        {
            public boolean retain( TargetIndicator targetIndicators )
            {
                return identifiers.contains( targetIndicators.getId() );
            }
        } );
    }

}
