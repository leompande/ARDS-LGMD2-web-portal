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

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalService
    implements DataApprovalService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    public void setDataApprovalStore( DataApprovalStore dataApprovalStore )
    {
        this.dataApprovalStore = dataApprovalStore;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // DataApproval
    // -------------------------------------------------------------------------

    public void addDataApproval( DataApproval dataApproval )
    {
        dataApprovalStore.addDataApproval( dataApproval );
    }

    public void deleteDataApproval( DataApproval dataApproval )
    {
        dataApprovalStore.deleteDataApproval( dataApproval );

        for ( OrganisationUnit ancestor : dataApproval.getOrganisationUnit().getAncestors() )
        {
            DataApproval ancestorApproval = dataApprovalStore.getDataApproval(
                    dataApproval.getDataSet(), dataApproval.getPeriod(), ancestor, dataApproval.getAttributeOptionCombo() );

            if ( ancestorApproval != null ) {
                dataApprovalStore.deleteDataApproval ( ancestorApproval );
            }
        }
    }

    public DataApproval getDataApproval( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        if ( attributeOptionCombo == null )
        {
            attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }
        
        return dataApprovalStore.getDataApproval( dataSet, period, organisationUnit, attributeOptionCombo );
    }

    public DataApprovalState getDataApprovalState( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        if ( !dataSet.isApproveData() || !period.getPeriodType().equals( dataSet.getPeriodType() ) )
        {
            return DataApprovalState.APPROVAL_NOT_NEEDED;
        }

        if ( attributeOptionCombo == null )
        {
            attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }
        
        if ( null != dataApprovalStore.getDataApproval( dataSet, period, organisationUnit, attributeOptionCombo ) )
        {
            return DataApprovalState.APPROVED;
        }

        boolean approvedAtLowerLevels = false; // Until proven otherwise

        for ( OrganisationUnit child : organisationUnit.getChildren() )
        {
            switch ( getDataApprovalState( dataSet, period, child, attributeOptionCombo ) )
            {
                // -------------------------------------------------------------
                // If ready or waiting at a lower level, return
                // WAITING_FOR_LOWER_LEVEL_APPROVAL at this level.
                // -------------------------------------------------------------
            
                case READY_FOR_APPROVAL:
                case WAITING_FOR_LOWER_LEVEL_APPROVAL:
                    return DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL;

                case APPROVED:
                    approvedAtLowerLevels = true;
                    break;

                case APPROVAL_NOT_NEEDED:
                    break; // Do nothing.
                    
                default:
                    break; // Do nothing.
            }
        }

        // ---------------------------------------------------------------------
        // If approved at lower levels (and not ready or waiting at any),
        // and/or if data is configured for entry at this level (whether or
        // not it has been entered), return READY_FOR_APPROVAL.
        // ---------------------------------------------------------------------
        
        if ( approvedAtLowerLevels || organisationUnit.getAllDataSets().contains ( dataSet ) )
        {
            return DataApprovalState.READY_FOR_APPROVAL;
        }

        // ---------------------------------------------------------------------
        // Finally, if we haven't seen any approval action at lower levels,
        // and this level is not configured for data entry from this data set,
        // then return APPROVAL_NOT_NEEDED.
        // ---------------------------------------------------------------------
        
        return DataApprovalState.APPROVAL_NOT_NEEDED;
    }

    public boolean mayApprove( OrganisationUnit organisationUnit )
    {
        User user = currentUserService.getCurrentUser();
        
        boolean mayApprove = ( user != null && user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE ) );
        
        if ( mayApprove && user.getOrganisationUnits().contains( organisationUnit ) )
        {
            return true;
        }

        boolean mayApproveAtLowerLevels = ( user != null && user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS ) );
        
        if ( mayApproveAtLowerLevels && CollectionUtils.containsAny( user.getOrganisationUnits(), organisationUnit.getAncestors() ) )
        {
            return true;
        }

        return false;
    }

    public boolean mayUnapprove( DataApproval dataApproval )
    {
        if ( isAuthorizedToUnapprove( dataApproval.getOrganisationUnit() ) )
        {
            // Check approvals at higher levels that may block this unapproval:

            for ( OrganisationUnit ancestor : dataApproval.getOrganisationUnit().getAncestors() )
            {
                DataApproval ancestorDataApproval = dataApprovalStore.getDataApproval(
                        dataApproval.getDataSet(), dataApproval.getPeriod(), ancestor, dataApproval.getAttributeOptionCombo() );
                
                if ( ancestorDataApproval != null && !isAuthorizedToUnapprove( ancestor ) )
                {
                    return false; // Could unapprove at that level, but higher-level approval is blocking.
                }
            }

            return true; // May unapprove at that level, and no higher-level approval is blocking.
        }

        return false; // May not unapprove at that level.
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Tests whether the user is authorized to unapprove for this organisation
     * unit.
     * <p>
     * Whether the user actually may unapprove an existing approval depends
     * also on whether there are higher-level approvals that the user is
     * authorized to unapprove.
     *
     * @param organisationUnit OrganisationUnit to check for approval.
     * @return true if the user may approve, otherwise false
     */
    private boolean isAuthorizedToUnapprove( OrganisationUnit organisationUnit )
    {
        if ( mayApprove( organisationUnit ) )
        {
            return true;
        }

        for ( OrganisationUnit ancestor : organisationUnit.getAncestors() )
        {
            if ( mayApprove( ancestor ) )
            {
                return true;
            }
        }

        return false;
    }
}
