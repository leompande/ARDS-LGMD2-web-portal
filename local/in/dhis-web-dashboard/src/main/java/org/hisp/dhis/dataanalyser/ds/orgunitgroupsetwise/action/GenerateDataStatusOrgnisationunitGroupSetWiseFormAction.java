/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.dataanalyser.ds.orgunitgroupsetwise.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */

public class GenerateDataStatusOrgnisationunitGroupSetWiseFormAction
    implements Action
{

    /* Dependencies */

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

  //  private List<OrganisationUnitGroup> orgUnitGroupNameOwnershipMembers;

    /* Output Parameters */
    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Period> monthlyPeriods;

    public List<Period> getMonthlyPeriods()
    {
        return monthlyPeriods;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private Collection<OrganisationUnit> organisationUnits;

    public Collection<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    private Collection<OrganisationUnitGroupSet> organisationUnitGroupSets;

    public Collection<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    private Collection<OrganisationUnit> selectedOrganisationUnits;

    public void setSelectedOrganisationUnits( Collection<OrganisationUnit> selectedOrganisationUnits )
    {
        this.selectedOrganisationUnits = selectedOrganisationUnits;
    }
    
    
    public Collection<OrganisationUnit> getSelectedOrganisationUnits()
    {
        return selectedOrganisationUnits;
    }


    public String execute()
        throws Exception
    {

        /* OrganisationUnit */
        organisationUnitGroupSets = organisationUnitGroupService.getAllOrganisationUnitGroupSets();

        //OrganisationUnitGroupSet organisationUnitGroupSet1 = organisationUnitGroupService
        //    .getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_TYPE );

        //List<OrganisationUnitGroup> orgUnitGroupMembers = new ArrayList<OrganisationUnitGroup>(
        //    organisationUnitGroupSet1.getOrganisationUnitGroups() );

        //OrganisationUnitGroupSet organisationUnitGroupSet2 = organisationUnitGroupService
        //    .getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_OWNERSHIP );

        //orgUnitGroupNameOwnershipMembers = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet2
        //    .getOrganisationUnitGroups() );

        //orgUnitGroupMembers.addAll( orgUnitGroupNameOwnershipMembers );

        /* DataSet List */
        dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

        Iterator<DataSet> dataSetListIterator = dataSetList.iterator();

        while ( dataSetListIterator.hasNext() )
        {
            DataSet d = (DataSet) dataSetListIterator.next();

            if ( d.getSources().size() <= 0 )
			{
                dataSetListIterator.remove();
			}
			else
			{			
				// -------------------------------------------------------------------------
				// Added to remove Indian Linelisting datasets
				// -------------------------------------------------------------------------
				
				if ( d.getId() == 8 || d.getId() == 9 || d.getId() == 10 || d.getId() == 14
					|| d.getId() == 15 || d.getId() == 35 || d.getId() == 36 || d.getId() == 37
					|| d.getId() == 38 )
				{
					dataSetListIterator.remove();
				}	
			}
        }
        Collections.sort( dataSetList, new IdentifiableObjectNameComparator() );

       // Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits();

       // Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();

        selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();

        //orgUnitGroupMembers.retainAll( selectedOrganisationUnits );

        return SUCCESS;
    }

}// class end

