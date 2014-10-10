package org.hisp.dhis.hr;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public interface CompletenessService {
	
	String ID = CompletenessService.class.getName();

    // -------------------------------------------------------------------------
    // Completeness
    // -------------------------------------------------------------------------
	    
    int saveCompleteness( Completeness completeness );

    void updateCompleteness( Completeness completeness );

    void deleteCompleteness( Completeness completeness );

    Completeness getCompletenessByPerson( Person person );

}
