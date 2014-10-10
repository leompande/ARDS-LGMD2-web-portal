package org.hisp.dhis.hr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultTrainingService
implements TrainingService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<Training> trainingStore;

    public void setTrainingStore( GenericIdentifiableObjectStore<Training> trainingStore )
    {
        this.trainingStore = trainingStore;
    }

    // -------------------------------------------------------------------------
    // Training
    // -------------------------------------------------------------------------

    public int saveTraining( Training training )
    {
        return trainingStore.save( training );
    }

    public void updateTraining( Training training )
    {
    	trainingStore.update( training );
    }

    public void deleteTraining( Training training )
    {
    	trainingStore.delete( training );
    }

    public Collection<Training> getAllTraining()
    {
        return trainingStore.getAll();
    }

    public Training getTraining( int id )
    {
        return trainingStore.get( id );
    }

    public Training getTrainingByName( String name )
    {
        return trainingStore.getByName( name );
    }
    
    public Collection<Training> getTrainings( final Collection<Integer> identifiers )
    {
        Collection<Training> trainings = getAllTraining();

        return identifiers == null ? trainings : FilterUtils.filter( trainings, new Filter<Training>()
        {
            public boolean retain( Training trainings )
            {
                return identifiers.contains( trainings.getId() );
            }
        } );
    }

}
