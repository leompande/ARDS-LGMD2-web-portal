package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Jim Grace
 * @version $Id$
 */
public interface DataApprovalService
{
    String ID = DataApprovalService.class.getName();

    /**
     * Adds a DataApproval in order to approve data.
     *
     * @param dataApproval the DataApproval to add.
     */
    void addDataApproval( DataApproval dataApproval );

    /**
     * Deletes a DataApproval in order to un-approve data.
     * Any higher-level DataApprovals above this organisation unit
     * are also deleted for the same period and data set.
     *
     * @param dataApproval the DataApproval to delete.
     */
    void deleteDataApproval( DataApproval dataApproval );

    /**
     * Returns the DataApproval object (if any) for a given
     * dataset, period and organisation unit. If attributeOptionCombo is null,
     * the default option combo will be used.
     *
     * @param dataSet DataSet for approval
     * @param period Period for approval
     * @param organisationUnit OrganisationUnit for approval
     * @param attributeOptionCombo DataElementCategoryOptionCombo for approval.
     * @return matching DataApproval object, if any
     */
    DataApproval getDataApproval( DataSet dataSet, Period period, 
        OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo );
    
    /**
     * Returns the DataApprovalState for a given data set, period and
     * OrganisationUnit. If attributeOptionCombo is null, the default option 
     * combo will be used.
     *
     * @param dataSet DataSet to check for approval.
     * @param period Period to check for approval.
     * @param organisationUnit OrganisationUnit to check for approval.
     * @return the data approval state.
     */
    DataApprovalState getDataApprovalState( DataSet dataSet, Period period, 
        OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo );

    /**
     * Checks to see whether a user may approve data for a given
     * organisation unit.
     *
     * @param organisationUnit OrganisationUnit to check for approval.
     * @return true if the user may approve, otherwise false
     */
    boolean mayApprove( OrganisationUnit organisationUnit );

    /**
     * Checks to see whether a user may unapprove a given data approval.
     * <p>
     * A user may unapprove data for organisation unit A if they have the
     * authority to approve data for organisation unit B, and B is an
     * ancestor of A.
     * <p>
     * A user may also unapprove data for organisation unit A if they have
     * the authority to approve data for organisation unit A, and A has no
     * ancestors.
     * <p>
     * But a user may not unapprove data for an organisation unit if the data
     * has been approved already at a higher level for the same period and
     * data set, and the user is not authorized to remove that approval as well.
     *
     * @param dataApproval The data approval to check for access.
     */
    boolean mayUnapprove( DataApproval dataApproval );
}
