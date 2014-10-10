package org.hisp.dhis.hr;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

@Transactional
public class DefaultCompletenessService 
implements CompletenessService{

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CompletenessStore completenessStore;

    public void setCompletenessStore( CompletenessStore completenessStore )
    {
        this.completenessStore = completenessStore;
    }
    
    // -------------------------------------------------------------------------
    // Completeness
    // -------------------------------------------------------------------------

    public int saveCompleteness( Completeness completeness )
    {
        return completenessStore.save( completeness );
    }

    public void updateCompleteness( Completeness completeness )
    {
    	completenessStore.update( completeness );
    }

    public void deleteCompleteness( Completeness completeness )
    {
    	completenessStore.delete( completeness );
    }

    public Completeness getCompletenessByPerson( Person person )
    {
        return completenessStore.getCompletenessByPerson( person );
    }
}
