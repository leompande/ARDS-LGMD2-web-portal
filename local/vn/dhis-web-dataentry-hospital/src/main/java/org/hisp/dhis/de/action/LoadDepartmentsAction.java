/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

package org.hisp.dhis.de.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.LocalDataSetService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.reportsheet.OptionComboAssociation;
import org.hisp.dhis.reportsheet.OptionComboAssociationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $LoadDepartmentsAction.java May 10, 2012 3:05:03 PM$
 */
public class LoadDepartmentsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private OptionComboAssociationService associationService;

    public void setAssociationService( OptionComboAssociationService associationService )
    {
        this.associationService = associationService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private LocalDataSetService localDataSetService;

    public void setLocalDataSetService( LocalDataSetService localDataSetService )
    {
        this.localDataSetService = localDataSetService;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Collection<OptionComboAssociation> associations;

    public Collection<OptionComboAssociation> getAssociations()
    {
        return associations;
    }

    private Map<Integer, String> mapDataSets = new HashMap<Integer, String>();

    public Map<Integer, String> getMapDataSets()
    {
        return mapDataSets;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Collection<DataElementCategoryOptionCombo> departmentInOrgunit = associationService
            .getOptionCombos( selectionManager.getSelectedOrganisationUnit() );

        DataSet dataSet = dataSetService.getDataSet( dataSetId );

        String description = dataSet.getDescription();

        if ( description != null && !description.trim().isEmpty() )
        {
            Collection<DataSet> dataSets = localDataSetService.getDataSetsByDescription( description );
            dataSets.remove( dataSet );

            for ( DataSet relativedataSet : dataSets )
            {
                if ( relativedataSet.getDataEntryForm() != null )
                {
                    DataElementCategoryOptionCombo optionCombo = localDataSetService
                        .getDepartmentByDataSet( relativedataSet );

                    if ( departmentInOrgunit.contains( optionCombo ) )
                    {
                        mapDataSets.put( relativedataSet.getId(), optionCombo.getName() );
                    }
                }
            }
        }

        return SUCCESS;
    }
}
