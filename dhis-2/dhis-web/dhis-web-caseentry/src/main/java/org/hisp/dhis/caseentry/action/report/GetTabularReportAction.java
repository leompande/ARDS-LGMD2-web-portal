package org.hisp.dhis.caseentry.action.report;

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

import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patientreport.PatientTabularReport;
import org.hisp.dhis.patientreport.PatientTabularReportService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $DeleteTabularReportAction.java May 7, 2012 4:11:43 PM$
 */
public class GetTabularReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientTabularReportService tabularReportService;

    public void setTabularReportService( PatientTabularReportService tabularReportService )
    {
        this.tabularReportService = tabularReportService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    private PatientTabularReport tabularReport;

    public PatientTabularReport getTabularReport()
    {
        return tabularReport;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    private List<PatientIdentifierType> dimensionIdentifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getDimensionIdentifierTypes()
    {
        return dimensionIdentifierTypes;
    }

    private List<PatientIdentifierType> filterIdentifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getFilterIdentifierTypes()
    {
        return filterIdentifierTypes;
    }

    private List<PatientAttribute> dimensionAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getDimensionAttributes()
    {
        return dimensionAttributes;
    }

    private List<PatientAttribute> filterAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getFilterAttributes()
    {
        return filterAttributes;
    }

    private List<DataElement> dimensionDataElements = new ArrayList<DataElement>();

    public List<DataElement> getDimensionDataElements()
    {
        return dimensionDataElements;
    }

    private List<DataElement> filterDataElements = new ArrayList<DataElement>();

    public List<DataElement> getFilterDataElements()
    {
        return filterDataElements;
    }

    private Map<String, String> mapFilters = new HashMap<String, String>();

    public Map<String, String> getMapFilters()
    {
        return mapFilters;
    }

    private Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();

    public Collection<OrganisationUnit> getOrgunits()
    {
        return orgunits;
    }

    private Boolean userOrgunits;

    public Boolean getUserOrgunits()
    {
        return userOrgunits;
    }

    private Boolean userOrgunitChildren;

    public Boolean getUserOrgunitChildren()
    {
        return userOrgunitChildren;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        tabularReport = tabularReportService.getPatientTabularReportByUid( id );

        Program program = tabularReport.getProgram();

        programStage = tabularReport.getProgramStage();

        for ( String dimension : tabularReport.getDimension() )
        {
            String dimensionId = DataQueryParams.getDimensionFromParam( dimension );

            String[] filters = dimension.split( DataQueryParams.DIMENSION_NAME_SEP );
            if ( filters.length > 1 )
            {
                mapFilters.put( dimensionId, dimension.substring( dimensionId.length() + 1, dimension.length() ) );
            }

            if ( ORGUNIT_DIM_ID.equals( dimensionId ) )
            {
                List<String> items = DataQueryParams.getDimensionItemsFromParam( dimension );
                for ( String item : items )
                {
                    if ( item.equals( "USER_ORGUNIT" ) )
                    {
                        userOrgunits = true;
                    }
                    else if ( item.equals( "USER_ORGUNIT_CHILDREN" ) )
                    {
                        userOrgunitChildren = true;
                    }
                    orgunits.add( organisationUnitService.getOrganisationUnit( item ) );
                }
            }
            else
            {
                PatientIdentifierType it = patientIdentifierTypeService.getPatientIdentifierType( dimensionId );

                if ( it != null && program.getIdentifierTypes().contains( it ) )
                {
                    dimensionIdentifierTypes.add( it );
                }

                PatientAttribute at = patientAttributeService.getPatientAttribute( dimensionId );

                if ( at != null && program.getAttributes().contains( at ) )
                {
                    dimensionAttributes.add( at );
                }

                DataElement de = dataElementService.getDataElement( dimensionId );

                if ( de != null && program.getAllDataElements().contains( de ) )
                {
                    dimensionDataElements.add( de );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Get filters
        // ---------------------------------------------------------------------

        for ( String filter : tabularReport.getFilter() )
        {
            String filterId = DataQueryParams.getDimensionFromParam( filter );

            String[] filters = filter.split( DataQueryParams.DIMENSION_NAME_SEP );
            if ( filters.length > 1 )
            {
                mapFilters.put( filterId, filter.substring( filterId.length() + 1, filter.length() ) );
            }

            PatientIdentifierType it = patientIdentifierTypeService.getPatientIdentifierType( filterId );

            if ( it != null && program.getIdentifierTypes().contains( it ) )
            {
                filterIdentifierTypes.add( it );
            }

            PatientAttribute at = patientAttributeService.getPatientAttribute( filterId );

            if ( at != null && program.getAttributes().contains( at ) )
            {
                filterAttributes.add( at );
            }

            DataElement de = dataElementService.getDataElement( filterId );

            if ( de != null && program.getAllDataElements().contains( de ) )
            {
                filterDataElements.add( de );
            }
        }

        return SUCCESS;
    }
}
