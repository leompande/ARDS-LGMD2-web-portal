package org.hisp.dhis.hr;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultHrDataSetService
implements HrDataSetService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    private GenericIdentifiableObjectStore<HrDataSet> hrDataSetStore;

    public void setHrDataSetStore( GenericIdentifiableObjectStore<HrDataSet> hrDataSetStore )
    {
        this.hrDataSetStore = hrDataSetStore;
    }

    // -------------------------------------------------------------------------
    // HrDataSet
    // -------------------------------------------------------------------------

    public int saveHrDataSet( HrDataSet hrDataSet )
    {
        return hrDataSetStore.save( hrDataSet );
    }

    public void updateHrDataSet( HrDataSet hrDataSet )
    {
    	hrDataSetStore.update( hrDataSet );
    }

    public void deleteHrDataSet( HrDataSet hrDataSet )
    {
    	hrDataSetStore.delete( hrDataSet );
    }

    public Collection<HrDataSet> getAllHrDataSets()
    {
        return hrDataSetStore.getAll();
    }

    public HrDataSet getHrDataSet( int id )
    {
        return hrDataSetStore.get( id );
    }

    public HrDataSet getHrDataSetByName( String name )
    {
        return hrDataSetStore.getByName( name );
    }
    
    // ----------------------------------------------------------
    // Methods extended apart from the ones
    // ----------------------------------------------------------
    
    public Collection<HrDataSet> getHrDataSets( final Collection<Integer> identifiers )
    {
        Collection<HrDataSet> hrDataSets = getAllHrDataSets();

        return identifiers == null ? hrDataSets : FilterUtils.filter( hrDataSets, new Filter<HrDataSet>()
        {
			@Override
			public boolean retain(HrDataSet object) {
				return identifiers.contains( object.getId() );
			}
        } );
    }
    
    public Collection<Attribute> getDistinctAttributes( Collection<Integer> hrDataSetIdentifiers )
    {
        Collection<HrDataSet> hrDataSets = getHrDataSets( hrDataSetIdentifiers );

        Set<Attribute> attributes = new HashSet<Attribute>();

        for ( HrDataSet hrDataSet : hrDataSets )
        {
        	attributes.addAll( hrDataSet.getAttribute() );
        }

        return attributes;
    }
    
    public Collection<Attribute> getAttributes( HrDataSet hrDataSet )
    {
        return i18n( i18nService, hrDataSet.getAttribute() );
    } 


}
